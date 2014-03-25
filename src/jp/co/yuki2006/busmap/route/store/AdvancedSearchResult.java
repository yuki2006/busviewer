/**
 *
 */
package jp.co.yuki2006.busmap.route.store;

import android.content.Context;

import java.util.ArrayList;

import jp.co.yuki2006.busmap.route.BusSearchList;

/**
 * @author yuki
 */
public class AdvancedSearchResult {
    /* パフォーマンス向上のため */
    public static class Builder {
        public ArrayList<BusSearch> busSearchs = new ArrayList<BusSearch>();
        public int fare = -1;
        private final Context context;

        /**
         * @param context
         */
        public Builder(Context context) {
            this.context = context;
        }

        public AdvancedSearchResult build() {
            return new AdvancedSearchResult(context, busSearchs, fare);

        }
    }

    public final BusSearchList busRouteElements;
    public final int fare;

    /**
     * @param fare
     * @param busSearchs
     */
    public AdvancedSearchResult(Context context, ArrayList<BusSearch> busSearchs, int fare) {
        this.fare = fare;
        busRouteElements = new BusSearchList(context, busSearchs);
    }

}
