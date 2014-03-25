/**
 *
 */
package jp.co.yuki2006.busmap.map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import jp.co.yuki2006.busmap.R;
import jp.co.yuki2006.busmap.etc.TransitionManager;
import jp.co.yuki2006.busmap.store.BusStop;
import jp.co.yuki2006.busmap.values.IntentValues;

/**
 * @author yuki
 */
public class BusMapActivity extends MapViewBasicActivity {

	public BusMapActivity() {
		super(R.layout.map_view);

	}

	/*
	 * (非 Javadoc)
	 *
	 * @see
	 * jp.co.yuki2006.busmap.map.MapViewBasicActivity#onCreate(android.os.Bundle
	 * )
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		boolean selectableMode = false;
		Intent intent = getIntent();
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null && intent.getExtras() != null) {
			if (intent.hasExtra(IntentValues.TRANSITION_BUS_STOP)) {
				// インテントで飛ばす。
				BusStop busStop = TransitionManager.getBusStopByIndent(this);
				map.initailLocation = busStop.getPoint();
			} else if (intent.hasExtra(IntentValues.EQUIRE_SELECT_MENU)) {
				selectableMode = true;
			}
		}
		map.isSelectableMode = selectableMode;
		if (selectableMode) {
			findViewById(R.id.select_busstop_for_map).setVisibility(
					View.VISIBLE);
		} else {
			findViewById(R.id.select_busstop_for_map).setVisibility(View.GONE);
		}

	}
}
