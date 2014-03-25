/**
 *
 */
package jp.co.yuki2006.busmap.store;

import com.google.android.gms.maps.model.LatLng;

/**
 * @author yuki
 */
public class MyLatLngWrapper {
    public LatLng getLatlag(int latitudeE6, int longtitudeE6) {
        return new LatLng(latitudeE6 / 1E6, longtitudeE6 / 1E6);
    }
}
