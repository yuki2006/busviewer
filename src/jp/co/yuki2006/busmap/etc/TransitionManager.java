package jp.co.yuki2006.busmap.etc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;

import java.util.ArrayList;

import jp.co.yuki2006.busmap.route.store.BusRouteElement;
import jp.co.yuki2006.busmap.route.store.RouteData;
import jp.co.yuki2006.busmap.store.BusStop;
import jp.co.yuki2006.busmap.store.LoadingZone;
import jp.co.yuki2006.busmap.values.IntentValues;
import jp.co.yuki2006.busmap.values.PreferenceValues;

public class TransitionManager extends Intent {
    /**
     *
     */
    private static final String NEXT_TIME = "next_time";

    /**
     * ヘルパーメソッド
     *
     * @param activity
     * @return
     */
    public static BusStop getBusStopByIndent(Activity activity) {
        return getBusStopByIndent(activity, activity.getIntent().getExtras());
    }

    /**
     * インテントからバス停の情報を読み出します。
     *
     * @param context getStringを取得するためのコンテキスト（このコンテキストからインテントを取るわけではない）
     * @param intent  取得元のインテント
     * @return
     */
    public static BusStop getBusStopByIndent(Context context, Bundle bundle) {
        return (BusStop) bundle
                .getSerializable(IntentValues.TRANSITION_BUS_STOP);

    }

    @SuppressWarnings("unchecked")
    public static ArrayList<BusRouteElement> getBusStopRouteByIntent(
            Context context, Intent intent) {
        return (ArrayList<BusRouteElement>) intent
                .getSerializableExtra(IntentValues.TRANSITION_BUS_STOP_LIST);
    }

    public static RouteData getBusStopAdvancedResult(
            Context context, Intent intent) {
        return (RouteData) intent
                .getSerializableExtra(IntentValues.TRANSITION_ADVANCED_SEARCH);
    }

    public static String getFilterString(Activity act) {
        return act.getIntent().getStringExtra(
                IntentValues.TRANSITION_BUS_STOP_FILTER);
    }

    public static RouteData getWidgetDataByPreference(Context context,
                                                      int widgetID) {

        // ウィジェットごとのプレファレンス
        SharedPreferences pref = context.getSharedPreferences("Widget"
                + widgetID, 0);
        int widgetType = pref.getInt(PreferenceValues.WIDGET_TYPE, 0);
        int widgetVersion = pref.getInt(PreferenceValues.WIDGET_VERSION, 0);
        if (widgetType == 0 || widgetVersion == 0) {
            return null;
        }
        RouteData selectData = new RouteData();
        boolean bool = false;
        do {
            bool = !bool;
            String prefix = bool ? "departure" : "arrival";
            LoadingZone loading = new LoadingZone(pref.getString(prefix
                    + "_busstop_loading_zone", ""), pref.getString(prefix
                    + "_busstop_loading_zone_alias", ""), pref.getInt(
                    prefix + "_busstop_dbid", -1));

            BusStop busStop = new BusStop(null, pref.getString(prefix
                    + "_busstop_name", ""), "", loading, pref.getInt(prefix
                    + "_busstop_id", 0), "");

            selectData.setBusStop(bool, busStop);

        } while (bool);
        return selectData;

    }

    public static Boolean isDepatureByIntent(Bundle bundle) {
        if (bundle.containsKey(IntentValues.IS_DEPARTURE)) {
            return bundle.getBoolean(IntentValues.IS_DEPARTURE, false);
        } else {
            return null;
        }

    }

    /**
     * ウィジット用のインテント
     *
     * @param selectData
     * @param widgetID
     * @return
     */
    // public static void setWidgetPreference(Context currentContext,
    // DirectionData selectData, int widgetID) {
    //
    // // ウィジェットごとのプレファレンス
    // SharedPreferences pref = currentContext.getSharedPreferences("Widget"
    // + widgetID, 0);
    //
    // Editor edit = pref.edit();
    // edit.putInt(PreferenceValues.WIDGET_TYPE, 0);
    // edit.putInt(PreferenceValues.WIDGET_DIRECTION_BUSSTOP_ID,
    // selectData.busStop.getBusStopID());
    // edit.putString(PreferenceValues.WIDGET_DIRECTION_DST_BUSSTOP_NAME,
    // selectData.destination);
    // edit.putString(PreferenceValues.WIDGET_DIRECTION_VIA_BUSSTOP_NAME,
    // selectData.via);
    // edit.putString(PreferenceValues.WIDGET_DIRECTION_BUSSTOP_LOADING,
    // selectData.busStop.getLoading().getLoadingID());
    //
    // edit.putString(PreferenceValues.WIDGET_DIRECTION_BUSSTOP_NAME,
    // selectData.busStop.getBusStopName());
    // edit.commit();
    // }
    public static void setWidgetPreference(Context currentContext,
                                           RouteData selectData, int widgetID) {

        // ウィジェットごとのプレファレンス
        SharedPreferences pref = currentContext.getSharedPreferences("Widget"
                + widgetID, 0);

        Editor edit = pref.edit();
        edit.putInt(PreferenceValues.WIDGET_TYPE, 1);
        edit.putInt(PreferenceValues.WIDGET_VERSION, 1);
        boolean bool = false;
        do {
            bool = !bool;
            String prefix = bool ? "departure" : "arrival";
            edit.putInt(prefix + "_busstop_id", selectData.getBusStop(bool)
                    .getBusStopID());
            edit.putInt(prefix + "_busstop_dbid", selectData.getBusStop(bool)
                    .getLoading().getDbId());
            edit.putString(prefix + "_busstop_name", selectData
                    .getBusStop(bool).getBusStopName());
            edit.putString(prefix + "_busstop_loading_zone", selectData
                    .getBusStop(bool).getLoading().getLoadingID());
            edit.putString(prefix + "_busstop_loading_zone_alias", selectData
                    .getBusStop(bool).getLoading().getAliasName());
        } while (bool);
        edit.commit();
    }

    public TransitionManager(Context currentContext, Class<?> cls,
                             RouteData routeData, ArrayList<BusRouteElement> routeResult, int index) {
        super(currentContext, cls);
        putExtra(IntentValues.TRANSITION_ADVANCED_SEARCH, routeData);
        putExtra(IntentValues.TRANSITION_BUS_STOP_LIST, routeResult);
        putExtra(PreferenceValues.SELECT_INDEX, index);
    }

    /**
     *
     */

    public TransitionManager(Context currentContext, Class<?> cls,
                             BusStop busStop) {
        super(currentContext, cls);
        putExtra(IntentValues.TRANSITION_BUS_STOP, busStop);

    }

    public TransitionManager(Context currentContext, Class<?> cls,
                             BusStop busStop, Boolean isDeparture) {
        this(currentContext, cls, busStop);
        putExtra(IntentValues.IS_DEPARTURE, isDeparture);

    }

    public TransitionManager(Context currentContext, Class<?> cls,
                             RouteData data) {
        super(currentContext, cls);

        putExtra(IntentValues.TRANSITION_ADVANCED_SEARCH, data);

    }

    public void setFilterString(String filterText) {
        putExtra(IntentValues.TRANSITION_BUS_STOP_FILTER, filterText);
    }

}
