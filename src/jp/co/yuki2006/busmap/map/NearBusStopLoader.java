/**
 *
 */
package jp.co.yuki2006.busmap.map;

import android.app.Activity;

import com.google.android.gms.maps.model.LatLng;

import java.io.InputStream;
import java.util.ArrayList;

import jp.co.yuki2006.busmap.parser.BusStopMarkerXMLParser;
import jp.co.yuki2006.busmap.store.BusStop;
import jp.co.yuki2006.busmap.web.IWebPostRunnable;
import jp.co.yuki2006.busmap.web.WebPortal;

/**
 * @author yuki
 */
public class NearBusStopLoader extends WebPortal<LatLng, Integer, BusStop[]> {

    /**
     * @param activity
     * @param showDialog
     * @param postRunnable
     */
    public NearBusStopLoader(Activity activity, IWebPostRunnable<BusStop[]> postRunnable) {
        super(activity, true, postRunnable, false);
    }

    /* (非 Javadoc)
     * @see jp.co.yuki2006.busmap.web.WebPortal#onBackGroundCore(java.io.InputStream)
     */
    @Override
    protected BusStop[] onBackGroundCore(InputStream is) {
        ArrayList<BusStop> ret = new ArrayList<BusStop>();
        BusStopMarkerXMLParser.parse(is, ret, 10);
        publishProgress(100);
        return ret.toArray(new BusStop[0]);
    }

    /* (非 Javadoc)
     * @see jp.co.yuki2006.busmap.web.WebPortal#onParamParser(java.lang.Object)
     */
    @Override
    protected String onParamParser(LatLng geo) {
        StringBuilder param = new StringBuilder("get_near_busstop_data.php?");
        param.append("Lat=")
                .append(geo.latitude * 1E6)
                .append("&Lit=")
                .append(geo.longitude * 1E6)
                .append("&count=10");
        return param.toString();
    }


}
