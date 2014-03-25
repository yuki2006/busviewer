package jp.co.yuki2006.busmap.map;

import android.app.Activity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.InputStream;
import java.util.ArrayList;

import jp.co.yuki2006.busmap.parser.BusStopMarkerXMLParser;
import jp.co.yuki2006.busmap.store.BusStop;
import jp.co.yuki2006.busmap.web.IWebPostRunnable;
import jp.co.yuki2006.busmap.web.WebPortal;

public class BusStopOnMapLoader extends WebPortal<MapMarkerParam, Integer, ArrayList<BusStop>> {

    private Boolean dataAll = null;
    private final int busStopListMarkerLimit;

    public BusStopOnMapLoader(Activity activity, int busStopListMarkerLimit,
                              IWebPostRunnable<ArrayList<BusStop>> postRunnable) {
        super(activity, postRunnable);
        this.busStopListMarkerLimit = busStopListMarkerLimit;
    }

    /*
     * (非 Javadoc)
     *
     * @see
     * jp.co.yuki2006.busmap.web.WebPortal#onBackGroundCore(java.io.InputStream)
     */
    @Override
    protected ArrayList<BusStop> onBackGroundCore(InputStream is) {
        ArrayList<BusStop> busStopDataListNew = new ArrayList<BusStop>();
        dataAll = BusStopMarkerXMLParser.parse(is, busStopDataListNew, busStopListMarkerLimit);
        if (dataAll == null) {
            return null;
        }
        // if (!dataAll) {
        // Toast.makeText(mapview.getContext(),
        // "マーカーの制限値より10個多いデータを読み込もうとしましたので打ち切りました", Toast.LENGTH_SHORT)
        // .show();
        // }
        return busStopDataListNew;
    }

    /*
     * (非 Javadoc)
     *
     * @see jp.co.yuki2006.busmap.web.WebPortal#onParamParser(java.lang.Object)
     */
    @Override
    protected String onParamParser(MapMarkerParam mapMarkerParam) {

        StringBuilder param = new StringBuilder("get_busstop_from_location.php?");

        param.append("Lat=")
                .append((mapMarkerParam.northeast.latitude + mapMarkerParam.southwest.latitude) / 2)
                .append("&LatSpan=")
                .append((mapMarkerParam.northeast.latitude - mapMarkerParam.southwest.latitude))
                .append("&Lit=")
                .append((mapMarkerParam.northeast.longitude + mapMarkerParam.southwest.longitude) / 2)
                .append("&LitSpan=")
                .append(mapMarkerParam.northeast.longitude - mapMarkerParam.southwest.longitude);
        if (mapMarkerParam.roughmode) {
            param.append("&roughmode=1");
        }
        return param.toString();
    }

}

class MapMarkerParam {
    boolean roughmode;
    public LatLng northeast;
    public LatLng southwest;

    public MapMarkerParam(GoogleMap googleMap, boolean roughmode) {
        this.roughmode = roughmode;
        LatLngBounds latLngBounds = googleMap.getProjection().getVisibleRegion().latLngBounds;
        this.northeast = latLngBounds.northeast;
        this.southwest = latLngBounds.southwest;
    }
}
