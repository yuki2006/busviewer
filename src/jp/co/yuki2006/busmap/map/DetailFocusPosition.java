/**
 *
 */
package jp.co.yuki2006.busmap.map;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * @author yuki
 */
public class DetailFocusPosition {
    interface OnBaloonTapListener {
        public void onTap();
    }

    private Marker overlayItem = null;

    private OnBaloonTapListener tapListner;

    /**
     * @param defaultMarker
     * @param mapView
     * @param defaultMarker
     */
    public DetailFocusPosition(GoogleMap mapView) {
//		super(new DrawableContainer(), mapView);
//		// なぜか入れないといけない・・・要調査
//		overlayItem = new OverlayItem(new GeoPoint(1000, 10000), "test",
//				"ここをタップでこの近くのバス停を読み込む");
//
//		populate();
    }


//	public void setCurrentPosition(GeoPoint geoPoint, String location) {
//		overlayItem = new OverlayItem(geoPoint, location, "ここをタップでこの近くのバス停を読み込む");
//	}

    public void setOnTapListener(OnBaloonTapListener tapListner) {
        this.tapListner = tapListner;
    }
}

//	/**
//	 * ポップアップを表示します。
//	 */
//	public void show() {
//		setFocus(overlayItem);
//	}

//	/*
//	 * (非 Javadoc)
//	 *
//	 * @see com.google.android.maps.ItemizedOverlay#size()
//	 */
//	@Override
//	public int size() {
//		return overlayItem != null ? 1 : 0;
//	}
//}
