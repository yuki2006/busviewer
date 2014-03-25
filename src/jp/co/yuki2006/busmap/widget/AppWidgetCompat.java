/**
 *
 */
package jp.co.yuki2006.busmap.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import jp.co.yuki2006.busmap.R;
import jp.co.yuki2006.busmap.route.AdvancedSearchResultActivity;
import jp.co.yuki2006.busmap.route.BusSearchList;

/**
 * @author yuki
 */
public class AppWidgetCompat {
    private static final float GESTURE_THRESHOLD_DIP = 40.0f;

    private static int WIDGET_LAYOUT_IDS[][] = new int[][]{
            {R.id.widget_next_bus1_time, R.id.widget_next_bus1_linenumber,
                    R.id.widget_next_bus1_last_bus_stop,
                    R.id.widget_next_bus1_remark},
            {R.id.widget_next_bus2_time, R.id.widget_next_bus2_linenumber,
                    R.id.widget_next_bus2_last_bus_stop,
                    R.id.widget_next_bus2_remark},
            {R.id.widget_next_bus3_time, R.id.widget_next_bus3_linenumber,
                    R.id.widget_next_bus3_last_bus_stop,
                    R.id.widget_next_bus3_remark}
    };

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void setRemoteAdapter(AppWidgetProvider provider, Context context, RemoteViews views,
                                        int widgetID, int widgetRootList) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB
                && provider instanceof AppWidget4x2) {
            Intent listIntent = new Intent(context, WidgetListService.class);
            listIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
            listIntent.setData(Uri.parse(listIntent.toUri(Intent.URI_INTENT_SCHEME)));
            views.setRemoteAdapter(widgetID, widgetRootList, listIntent);
        }
    }

    public static boolean isLarge(AppWidgetManager appWidgetManager, int widgetID) {
        return is(appWidgetManager, widgetID, AppWidget4x2.class);
    }

    public static boolean is(AppWidgetManager appWidgetManager, int widgetID, Class<?> class2) {
        ComponentName class1 = appWidgetManager.getAppWidgetInfo(widgetID).provider;
        Log.d("Bus", "widgetID=" + widgetID + " " + class1.getClassName());
        return class1.getClassName().equals(class2.getCanonicalName());
    }

    /**
     * @param views
     * @param appWidgetManager
     * @param widgetID
     * @param widgetRootList
     * @param listIntent
     * @param context
     * @param busSearchList
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void listViewDataChanged(Context context, RemoteViews views, AppWidgetManager appWidgetManager,
                                           int widgetID,
                                           int widgetRootList, BusSearchList busSearchList) {
        // Convert the dips to pixels
        // final float scale =
        // context.getResources().getDisplayMetrics().density;
        // int mHeightThreshold = (int) (GESTURE_THRESHOLD_DIP * scale);
        // AppWidgetProviderInfo appWidgetInfo =
        // appWidgetManager.getAppWidgetInfo(widgetID);
        ComponentName class1 = appWidgetManager.getAppWidgetInfo(widgetID).provider;
        if (class1.getClassName().equals(AppWidget4x2.class.getCanonicalName())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                appWidgetManager.notifyAppWidgetViewDataChanged(widgetID, widgetRootList);
            } else {
                if (busSearchList != null) {
                    for (int i = 0; i < WIDGET_LAYOUT_IDS.length; i++) {
                        // Log.d("bus",
                        // busSearchList.getItem(i).getStringForWidget());
                        if (i < busSearchList.getCount()) {
                            views.setTextViewText(WIDGET_LAYOUT_IDS[i][0], busSearchList.getItem(i)
                                    .getStringForWidget());
                            views.setTextViewText(WIDGET_LAYOUT_IDS[i][1], busSearchList.getItem(i)
                                    .getLineNumberForWidget());
                            views.setTextViewText(WIDGET_LAYOUT_IDS[i][2], busSearchList.getItem(i)
                                    .getLastBusStopName()
                                    + " 行");

                            views.setTextViewText(WIDGET_LAYOUT_IDS[i][3], busSearchList.getItem(i)
                                    .getRemarkForString());
                        } else {
                            views.setTextViewText(WIDGET_LAYOUT_IDS[i][0], "");
                            views.setTextViewText(WIDGET_LAYOUT_IDS[i][1], "");
                            views.setTextViewText(WIDGET_LAYOUT_IDS[i][2], "");

                            views.setTextViewText(WIDGET_LAYOUT_IDS[i][3], "");
                        }
                    }
                }
            }
        }
    }

    /**
     * @param pendingIntentre
     * @param widgetFrame
     * @param context
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void setOnClickPendingIntent(Context context, RemoteViews views) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Intent intent2 = new Intent(context, AdvancedSearchResultActivity.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent2.setAction(Intent.ACTION_VIEW);
            PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0, intent2,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.widget_root_list, pendingIntent2);
        } else {

        }
    }

}
