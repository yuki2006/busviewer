/**
 *
 */
package jp.co.yuki2006.busmap.route.store;

import android.content.Context;

import java.util.ArrayList;

import jp.co.yuki2006.busmap.route.NewBusSearchList;

/**
 * @author yuki
 */
public class NewAdvancedSearchResult {
    /* パフォーマンス向上のため */
    public static class Builder {
        public ArrayList<BusTransferSearch> busSearchs = new ArrayList<BusTransferSearch>();
        public int fare = -1;
        private final Context context;

        /**
         * @param context
         */
        public Builder(Context context) {
            this.context = context;
        }

        public NewAdvancedSearchResult build() {
            return new NewAdvancedSearchResult(context, busSearchs, fare);
        }
    }

    public final NewBusSearchList busRouteElements;
    public final int fare;

    /**
     * @param fare
     * @param busSearchs
     */
    public NewAdvancedSearchResult(Context context, ArrayList<BusTransferSearch> busSearchs, int fare) {
        this.fare = fare;
        busRouteElements = new NewBusSearchList(context, busSearchs);
    }

}
