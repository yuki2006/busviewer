package jp.co.yuki2006.busmap.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;

import jp.co.yuki2006.busmap.R;
import jp.co.yuki2006.busmap.etc.TransitionManager;
import jp.co.yuki2006.busmap.route.AdvancedSearchResultActivity;
import jp.co.yuki2006.busmap.route.BusSearchList;
import jp.co.yuki2006.busmap.route.SearchLoader;
import jp.co.yuki2006.busmap.route.store.AdvancedSearchResult;
import jp.co.yuki2006.busmap.route.store.BusSearch;
import jp.co.yuki2006.busmap.route.store.RouteData;
import jp.co.yuki2006.busmap.values.IntentValues;
import jp.co.yuki2006.busmap.web.IWebPostRunnableAndErrors;

public class AppWidget extends AppWidgetProvider {

    static class BusSearchListForCache implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 2L;
        private final ArrayList<BusSearch> mList = new ArrayList<BusSearch>();

        public BusSearchListForCache(BusSearchList list) {
            for (int i = 0; i < list.getCount(); i++) {
                mList.add(list.getItem(i));
            }
        }

        /**
         * @return list
         */
        public BusSearchList getList(Context context) {
            BusSearchList busSearchList = new BusSearchList(context, mList);
            return busSearchList;
        }

    }

    /**
     *
     */
    private static final int AUTO_SYNCRONIZED_INTERVAL = 1000 * 60 * 60;

    // private static final SparseArray<RouteData> dataMap = new
    // SparseArray<RouteData>();
    // private static final HashMap<RouteData, BusSearchList> dataToResultMap =
    // new HashMap<RouteData, BusSearchList>();
    // private static final SparseArray<Long> getLastGetMap = new
    // SparseArray<Long>();
    // 1時間以上Webからの読み出しがなかったら強制読み込み
    // private static final SparseArray<Long> getLastWebAccessMap = new
    // SparseArray<Long>();

    public static final String ALARM = AppWidget.class.getCanonicalName() + ".ALARM";

    public static final String LIST_ELEMENT = "LIST_ELEMENT";

    private static int getRetainCount(Context context, AppWidgetManager appWidgetManager) {
        int tmp = 0;
        tmp += appWidgetManager.getAppWidgetIds(new
                ComponentName(context, AppWidget.class)).length;
        tmp += appWidgetManager.getAppWidgetIds(new
                ComponentName(context, AppWidget4x2.class)).length;
        return tmp;

    }

    private void invalidateForRoot(Context context, AppWidgetManager appWidgetManager, RemoteViews views,
                                   int widgetID, RouteData data, BusSearchList busSearchList, boolean isListRefresh) {
        if (AppWidgetManager.getInstance(context).getAppWidgetInfo(widgetID) == null) {
            return;
        }
        TransitionManager intent = new TransitionManager(context, AdvancedSearchResultActivity.class, data);
        intent.putExtra(IntentValues.FROM_MY_ROOT, true);
        intent.putExtra(IntentValues.FROM_WIDGET, true);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, widgetID, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        // widgetのボタンクリックイベントに呼び出したいIntentを設定する。
        views.setOnClickPendingIntent(R.id.widget_frame, pendingIntent);

        AppWidgetCompat.setOnClickPendingIntent(context, views);

        // 共通のデータ
        views.setTextViewText(R.id.widget_d_busstop, data.getBusStop(true).toString());
        views.setTextViewText(R.id.widget_a_busstop, data.getBusStop(false).toString());
        if (busSearchList == null) {
            return;
        }
        if (this instanceof AppWidget2x1) {
            views.setTextViewText(R.id.my_root_next_time_text, busSearchList.toShortNextTimeString());
        } else if (this instanceof AppWidget1x1) {
            views.setTextViewText(R.id.my_root_next_time_text, busSearchList.toOnlyTimeString());
            int colors = busSearchList.isNextHasRemarks() ? R.color.remark_emphasis :
                    R.color.bustimeline_next_bus_time;
            views.setInt(R.id.my_root_next_time_text, "setTextColor",
                    context.getResources().getColor(colors));
        } else {
            views.setTextViewText(R.id.my_root_next_time_text, busSearchList.toNextTimeString());

        }
        if (isListRefresh) {
            AppWidgetCompat.listViewDataChanged(context, views, appWidgetManager, widgetID, R.id.widget_root_list,
                    busSearchList);
        }
        appWidgetManager.updateAppWidget(widgetID, views);
    }

    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager,
                         final int widgetID) {

        final RouteData data = (RouteData) TransitionManager.getWidgetDataByPreference(context, widgetID);

        if (data == null || data.getBusStop(true).getBusStopID() == 0) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_error);
            appWidgetManager.updateAppWidget(widgetID, views);
            return;
        }

        final BusSearchList busSearchList = readNextDataCache(context, widgetID);
        // views.setTextViewText(R.id.busstop_name,
        // selectData.busStop.getTitle());
        boolean getWebFlag = false;
        if (busSearchList != null) {
            // data = dataMap.get(widgetID);
            // // 前回のWebへのアクセスの差を見て1時間以上なら読み込む
            // // 日付が変わった時対策と安全のための同期
            // if (getLastGetMap.indexOfKey(widgetID) >= 0 &&
            // (Calendar.getInstance().getTimeInMillis() -
            // getLastWebAccessMap.get(widgetID))
            // > AUTO_SYNCRONIZED_INTERVAL) {
            // getWebFlag = true;
            // } else {
            busSearchList.refleshNextTime();
            getWebFlag = busSearchList.isShouldLoadNextData();
            // }
        } else {
            // データがなかった場合
            // dataMap.put(widgetID, data);
            getWebFlag = true;
        }
        // getLastGetMap.put(widgetID,
        // Calendar.getInstance().getTimeInMillis());

        final RemoteViews views = new RemoteViews(context.getPackageName(),
                getLayout());

        IWebPostRunnableAndErrors<AdvancedSearchResult[]> postRunnable = new IWebPostRunnableAndErrors<AdvancedSearchResult[]>() {
            @Override
            public void onErrorRunnable() {
                if (busSearchList == null) {
                    invalidateForRoot(context, appWidgetManager, views, widgetID, data, new BusSearchList(context, new ArrayList<BusSearch>()),
                            true);
                } else {
                    busSearchList.setLoadingError();
                    invalidateForRoot(context, appWidgetManager, views, widgetID, data, busSearchList, true);
                }
            }

            public void onPostRunnable(final AdvancedSearchResult[] results) {
                AdvancedSearchResult result = results[0];
                // dataToResultMap.put(data, result.busRouteElements);
                // result.busRouteElements.setNextTime();
                // result.busRouteElements.setRefleshMode();
                saveNextDataCache(context, widgetID, result.busRouteElements);
                // ウィジェットの置いてみたら領域的に無効だった。

                invalidateForRoot(context, appWidgetManager, views, widgetID, data, result.busRouteElements, true);

            }
        };
        AppWidgetCompat.setRemoteAdapter(this, context, views, widgetID, R.id.widget_root_list);
        data.setNow();
        if (getWebFlag) {
            Log.d("Bus", "Widget WebLoad" + " ID=" + widgetID);
            SearchLoader loader = new SearchLoader(context, postRunnable);
            loader.setByWidget(true);
            loader.execute(new RouteData[]{data});
            // getLastWebAccessMap.put(widgetID,
            // Calendar.getInstance().getTimeInMillis());
        } else {
            Log.d("Bus", "Widget Load" + " ID=" + widgetID);
            invalidateForRoot(context, appWidgetManager, views, widgetID, data, busSearchList, false);
        }

    }

    protected static BusSearchList readNextDataCache(Context context, int widgetID) {
        File file = new File(context.getCacheDir(), "widget-" + widgetID);
        // キャッシュファイルがなかったとき
        if (file.isFile() == false) {
            return null;
        }
        // 最後の読み込みが一時間以上前ならCacheを使わない。
        long lastModified = file.lastModified();
        long l = Calendar.getInstance().getTimeInMillis() - lastModified;
        Log.d("Bus", "Widget Cache Diff Time " + (l / 1000) + "s");
        if (l >= AUTO_SYNCRONIZED_INTERVAL) {
            file.delete();
            return null;
        }
        BusSearchList data = null;

        try {
            FileInputStream inputStream = new FileInputStream(file);
            ObjectInputStream objectOutputStream = new ObjectInputStream(inputStream);
            BusSearchListForCache tmp = (BusSearchListForCache) objectOutputStream.readObject();
            data = tmp.getList(context);
            data.refleshMode();
            objectOutputStream.close();
            Log.d("Bus", "Widget READ Cache " + " ID=" + widgetID);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }

    private void refleshForClass(Context context, AppWidgetManager appWidgetManager, Class<?> cls) {
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new
                ComponentName(context, cls));
        for (int appWidgetId : appWidgetIds) {
            onUpdate(context, appWidgetManager, appWidgetId);
        }
    }

    protected static void saveNextDataCache(Context context, int widgetID, BusSearchList object) {
        File file = new File(context.getCacheDir(), "widget-" + widgetID);
        FileOutputStream outputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            outputStream = new FileOutputStream(file);

            objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(new BusSearchListForCache(object));
            Log.d("Bus", "Widget WRITE Cache" + " ID=" + widgetID);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (objectOutputStream != null) {
                    objectOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setAlarm(Context context, long time, AppWidgetManager appWidgetManager) {
        // Intent alarmIntent = new Intent(context, WidgetTimerService.class);
        Intent alarmIntent = new Intent(context, getClass());
        alarmIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context,
                getClass()));
        Log.d("Bus", this + " AppWidgetIDs" + Arrays.toString(appWidgetIds));
        alarmIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        PendingIntent operation = PendingIntent.getBroadcast(context, 0,
                alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);

        long now = Calendar.getInstance(TimeZone.getTimeZone("Japan")).getTimeInMillis();
        // 0秒だと微妙にずれるので５秒後に設定
        // long oneHourAfter = now + 60000 - now % (60000);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + (time - (now % time)) + 5000,
                time,
                operation);
        // context.stopService(alarmIntent);
        // サービススタート！
        // context.startService(alarmIntent);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        for (int widgetID : appWidgetIds) {
            SharedPreferences sp = context.getSharedPreferences("Widget" + widgetID, 0);
            sp.edit().clear().commit();
        }
        Log.d("Bus", "AppWidget Delete " + Arrays.toString(appWidgetIds));
    }

    // @Override
    // public void onReceive(Context context, Intent intent) {
    // if (ACTION_START_MY_ALARM.equals(intent.getAction())) {
    // isAlarm = false;
    //
    // AppWidgetManager appWidgetManager =
    // AppWidgetManager.getInstance(context);
    // // 画面更新
    // int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new
    // ComponentName(context, AppWidget4x2.class));
    // for (int appWidgetId : appWidgetIds) {
    // onUpdate(context, appWidgetManager, appWidgetId);
    // }
    // // ウィジェットのリストがある場合。
    // if (appWidgetIds.length > 0) {
    // setAlarm(context, INTERNAL);
    // }
    // }
    // super.onReceive(context, intent);
    // }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);

        // // 残っているウィジェットが０ならアラームサービス終了
        // AppWidgetManager appWidgetManager =
        // AppWidgetManager.getInstance(context);
        // int remainCount = getRetainCount(context, appWidgetManager);
        // if (remainCount == 0) {
        Intent alarmIntent = new Intent(context, this.getClass());
        alarmIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        AlarmManager am = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent operation = PendingIntent.getBroadcast(context, 0,
                alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.cancel(operation);
        // context.stopService(alarmIntent);

        // }
        Log.d("Bus", "AppWidget Disabled " + this);

    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        // setAlarm(context, 60000, AppWidgetManager.getInstance(context));

    }

    // /*
    // * (非 Javadoc)
    // *
    // * @see
    // * android.appwidget.AppWidgetProvider#onReceive(android.content.Context,
    // * android.content.Intent)
    // */
    // @Override
    public void onReceive(Context context, Intent intent) {
        // if (ALARM.equals(intent.getAction())) {
        // AppWidgetManager appWidgetManager =
        // AppWidgetManager.getInstance(context);
        // 画面更新
        // refleshForClass(context, appWidgetManager, AppWidget.class);
        // refleshForClass(context, appWidgetManager, AppWidget4x2.class);

        // }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        setAlarm(context, 60000, appWidgetManager);
        for (int appWidgetId : appWidgetIds) {
            onUpdate(context, appWidgetManager, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    /**
     * @return
     */
    public int getLayout() {
        if (this instanceof AppWidget2x1) {
            return R.layout.widget_2x1;
        }
        if (this instanceof AppWidget1x1) {
            return R.layout.widget_1x1;
        }
        if (this instanceof AppWidget4x2
                || this instanceof AppWidget) {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                    R.layout.widget_ics_over : R.layout.widget_4x;
        }
        return 0;
    }
}
