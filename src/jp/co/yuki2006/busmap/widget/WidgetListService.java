/**
 *
 */
package jp.co.yuki2006.busmap.widget;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import jp.co.yuki2006.busmap.R;
import jp.co.yuki2006.busmap.etc.TransitionManager;
import jp.co.yuki2006.busmap.route.AdvancedSearchResultActivity;
import jp.co.yuki2006.busmap.route.BusSearchList;
import jp.co.yuki2006.busmap.route.store.RouteData;
import jp.co.yuki2006.busmap.values.IntentValues;

/**
 * @author yuki
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class WidgetListService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        return new WidgetListRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
class WidgetListRemoteViewsFactory implements RemoteViewsFactory {
    private Context mContext;
    private int mAppWidgetId;
    private BusSearchList busSearchList;

    public WidgetListRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

    }

    /*
     * (非 Javadoc)
     *
     * @see android.widget.RemoteViewsService.RemoteViewsFactory#getCount()
     */
    @Override
    public int getCount() {
        if (busSearchList == null) {
            return 0;
        }
        return busSearchList.getCount();
    }

    /*
     * (非 Javadoc)
     *
     * @see android.widget.RemoteViewsService.RemoteViewsFactory#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        // TODO 自動生成されたメソッド・スタブ
        return 0;
    }

    /*
     * (非 Javadoc)
     *
     * @see
     * android.widget.RemoteViewsService.RemoteViewsFactory#getLoadingView()
     */
    @Override
    public RemoteViews getLoadingView() {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    /*
     * (非 Javadoc)
     *
     * @see android.widget.RemoteViewsService.RemoteViewsFactory#getViewAt(int)
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public RemoteViews getViewAt(int position) {

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_root_list_element);
        views.setTextViewText(R.id.widget_next_bus1_time, busSearchList.getItem(position)
                .getStringForWidget());
        views.setTextViewText(R.id.widget_next_bus1_linenumber, busSearchList.getItem(position)
                .getLineNumberForWidget());
        views.setTextViewText(R.id.widget_next_bus1_last_bus_stop, busSearchList.getItem(position)
                .getLastBusStopName()
                + " 行");
        views.setTextViewText(R.id.widget_next_bus1_remark, busSearchList.getItem(position)
                .getRemarkForString());
        RouteData data = (RouteData) TransitionManager.getWidgetDataByPreference(mContext, mAppWidgetId);
        TransitionManager intent = new TransitionManager(mContext, AdvancedSearchResultActivity.class, data);
        intent.putExtra(IntentValues.FROM_MY_ROOT, true);
        intent.putExtra(IntentValues.FROM_WIDGET, true);
        intent.putExtra(IntentValues.TRANSITION_ADVANCED_SEARCH_SELECT_INDEX, position);
        views.setOnClickFillInIntent(R.id.widget_list_element, intent);
        return views;
    }

    /*
     * (非 Javadoc)
     *
     * android.widget.RemoteViewsService.RemoteViewsFactory#getViewTypeCount()
     */
    @Override
    public int getViewTypeCount() {
        // TODO 自動生成されたメソッド・スタブ
        return 1;
    }

    /*
     * (非 Javadoc)
     *
     * @see android.widget.RemoteViewsService.RemoteViewsFactory#hasStableIds()
     */
    @Override
    public boolean hasStableIds() {
        // TODO 自動生成されたメソッド・スタブ
        return false;
    }

    /*
     * (非 Javadoc)
     *
     * @see android.widget.RemoteViewsService.RemoteViewsFactory#onCreate()
     */
    @Override
    public void onCreate() {

    }

    /*
     * (非 Javadoc)
     *
     * @see
     * android.widget.RemoteViewsService.RemoteViewsFactory#onDataSetChanged()
     */
    @Override
    public void onDataSetChanged() {
        busSearchList = AppWidget.readNextDataCache(mContext, mAppWidgetId);
    }

    /*
     * (非 Javadoc)
     *
     * @see android.widget.RemoteViewsService.RemoteViewsFactory#onDestroy()
     */
    @Override
    public void onDestroy() {

    }

}
