/**
 *
 */
package jp.co.yuki2006.busmap.route;

import jp.co.yuki2006.busmap.route.store.RouteData;


/**
 * @author yuki
 */
public interface IAdvancedSearch {
    public void searchBus();

    public RouteData getRouteData();
}
