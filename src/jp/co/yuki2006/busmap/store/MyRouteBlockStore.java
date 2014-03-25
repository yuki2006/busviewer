/**
 *
 */
package jp.co.yuki2006.busmap.store;

import jp.co.yuki2006.busmap.route.BusSearchList;
import jp.co.yuki2006.busmap.route.store.RouteData;

/**
 * @author yuki
 */
public class MyRouteBlockStore {
    public RouteData routeData;
    private BusSearchList busSearchList;

    /**
     * @return busSearchList
     */
    public BusSearchList getBusSearchList() {
        return busSearchList;
    }

    /**
     */
    public void setBusSearchList(BusSearchList busSearchList) {
        this.busSearchList = busSearchList;
//		busSearchList.setNextTime();
    }

    /**
     * @return
     */
    public CharSequence toNextTimeString() {
        return getBusSearchList() != null ? getBusSearchList().toNextTimeString() : "";
    }
}
