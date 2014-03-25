package jp.co.yuki2006.busmap.etc;

import android.app.Activity;
import android.content.Intent;

import java.util.HashMap;

import jp.co.yuki2006.busmap.AboutMeActivity;
import jp.co.yuki2006.busmap.AboutWidgetActivity;
import jp.co.yuki2006.busmap.Main;
import jp.co.yuki2006.busmap.MyBusStopActivity;
import jp.co.yuki2006.busmap.MyBusStopGuideActivity;
import jp.co.yuki2006.busmap.SearchBusActivity;
import jp.co.yuki2006.busmap.bustimeline.TimeLineActivity;
import jp.co.yuki2006.busmap.custom.CustomLoadingEditActivity;
import jp.co.yuki2006.busmap.map.AdvancedSearchOnMapActivity;
import jp.co.yuki2006.busmap.map.BusMapActivity;
import jp.co.yuki2006.busmap.pf.MainTimeLinePreferenceActivity;
import jp.co.yuki2006.busmap.pf.MyPreferenceActivity;
import jp.co.yuki2006.busmap.route.AdvancedSearchConditionActivity;
import jp.co.yuki2006.busmap.route.AdvancedSearchResultActivity;
import jp.co.yuki2006.busmap.route.MyRouteActivity;
import jp.co.yuki2006.busmap.route.MyRouteGuideActivity;
import jp.co.yuki2006.busmap.route.NewAdvancedDetailActivity;
import jp.co.yuki2006.busmap.route.NewAdvancedSearchResultActivity;

public final class ActionBarUPWrapper {
    private static final HashMap<Class<? extends Activity>, Class<?>> UP_NAVIGATION_MAP = new HashMap<Class<? extends Activity>, Class<?>>();

    static {
        UP_NAVIGATION_MAP.put(BusMapActivity.class, Main.class);
        UP_NAVIGATION_MAP.put(MyRouteActivity.class, Main.class);
        UP_NAVIGATION_MAP.put(MyBusStopActivity.class, Main.class);
        UP_NAVIGATION_MAP.put(AdvancedSearchConditionActivity.class, Main.class);
        UP_NAVIGATION_MAP.put(MyPreferenceActivity.class, Main.class);
        UP_NAVIGATION_MAP.put(AboutMeActivity.class, Main.class);
        UP_NAVIGATION_MAP.put(SearchBusActivity.class, Main.class);
        UP_NAVIGATION_MAP.put(TimeLineActivity.class, Main.class);
        UP_NAVIGATION_MAP.put(AboutWidgetActivity.class, MyPreferenceActivity.class);
        UP_NAVIGATION_MAP.put(MainTimeLinePreferenceActivity.class, MyPreferenceActivity.class);
        UP_NAVIGATION_MAP.put(AdvancedSearchResultActivity.class, AdvancedSearchConditionActivity.class);
        UP_NAVIGATION_MAP.put(AdvancedSearchOnMapActivity.class, AdvancedSearchResultActivity.class);
        UP_NAVIGATION_MAP.put(CustomLoadingEditActivity.class, Main.class);
        UP_NAVIGATION_MAP.put(MyRouteGuideActivity.class, Main.class);
        UP_NAVIGATION_MAP.put(MyBusStopGuideActivity.class, Main.class);
        UP_NAVIGATION_MAP.put(NewAdvancedDetailActivity.class, NewAdvancedSearchResultActivity.class);
    }

    public static boolean doActionUpNavigation(Activity activity) {
        Class<?> targetActivity = UP_NAVIGATION_MAP.get(activity.getClass());
        if (targetActivity != null) {
            Intent intent = new Intent(activity, targetActivity);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            activity.startActivity(intent);
            return true;
        }
        return false;
    }
}
