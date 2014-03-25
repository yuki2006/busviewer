/**
 *
 */
package jp.co.yuki2006.busmap.map;

import android.app.Activity;

import com.google.android.gms.maps.model.LatLng;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import jp.co.yuki2006.busmap.web.IWebPostRunnable;
import jp.co.yuki2006.busmap.web.WebPortal;

/**
 * @author yuki
 */
class LocationSearchResult extends WebPortal<CharSequence, Integer, Void> {

    private final int hitCount;
    private final LatLng geoPoint;

    /**
     * @param activity
     * @param showDialog
     * @param postRunnable
     * @param hitCount
     * @param searchQuery
     * @param geoPoint
     */
    public LocationSearchResult(Activity activity, IWebPostRunnable<Void> postRunnable,
                                int hitCount, LatLng geoPoint) {
        super(activity, postRunnable);
        this.geoPoint = geoPoint;
        this.hitCount = hitCount;
    }

    /*
     * (非 Javadoc)
     *
     * @see
     * jp.co.yuki2006.busmap.web.WebPortal#onBackGroundCore(java.io.InputStream)
     */
    @Override
    protected Void onBackGroundCore(InputStream is) {
        return null;
    }

    /*
     * (非 Javadoc)
     *
     * @see jp.co.yuki2006.busmap.web.WebPortal#onParamParser(java.lang.Object)
     */
    @Override
    protected String onParamParser(CharSequence param) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("etc/location_search_log.php?query=");
        try {
            stringBuilder.append(URLEncoder.encode(param.toString(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        stringBuilder.append("&count=");
        stringBuilder.append(hitCount);
        stringBuilder.append("&Lat=");
        stringBuilder.append(geoPoint.latitude);
        stringBuilder.append("&Lit=");
        stringBuilder.append(geoPoint.longitude);
        return stringBuilder.toString();
    }

}
