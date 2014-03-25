/**
 *
 */
package jp.co.yuki2006.busmap.map;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.actionbarsherlock.app.ActionBar;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import jp.co.yuki2006.busmap.R;
import jp.co.yuki2006.busmap.etc.TransitionManager;
import jp.co.yuki2006.busmap.route.store.BusRouteElement;
import jp.co.yuki2006.busmap.route.store.RouteData;
import jp.co.yuki2006.busmap.store.BusStop;
import jp.co.yuki2006.busmap.values.PreferenceValues;

/**
 * @author yuki
 */
public class AdvancedSearchOnMapActivity extends MapViewBasicActivity implements OnClickListener {
	private ArrayList<BusRouteElement> busStopRoute;
	private RouteData routeData;
	private Object isFirst;

	public AdvancedSearchOnMapActivity() {
		super(R.layout.map_advanced_search);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.arrow_up_button: {
			map.setNextItem(-1);

			break;
		}
		case R.id.arrow_down_button: {
			map.setNextItem(1);
			break;
		}
		default:
			break;
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// 現在地情報を追跡しない。
		super.onCreate(savedInstanceState);
		busStopRoute = TransitionManager.getBusStopRouteByIntent(this, getIntent());
		routeData = TransitionManager.getBusStopAdvancedResult(this, getIntent());
		findViewById(R.id.arrow_up_button).setOnClickListener(this);
		findViewById(R.id.arrow_down_button).setOnClickListener(this);

		// LAYOUTS_IN_MAP = new View[] {
		// map.getZoomButtonsController().getZoomControls(),
		// // findViewById(R.id.my_busstop_button),
		// findViewById(R.id.arrow_up_button),
		// findViewById(R.id.arrow_down_button)
		// };
		isFirst = getLastCustomNonConfigurationInstance();
		ActionBar actionBar = this.getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		// 初期画面を設定しない。
		map.initailLocation = new LatLng(map.initailLocation.latitude, map.initailLocation.longitude);
	
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isFirst == null) {
			int selectIndex = getIntent().getIntExtra(PreferenceValues.SELECT_INDEX, 0);
			ArrayList<BusStop> arrayList = new ArrayList<BusStop>();
			BusRouteElement selectedRoute = busStopRoute.get(selectIndex);
			int i = 0;
			for (BusRouteElement busRouteElement : busStopRoute) {
				if (busRouteElement.getBusStop().getPoint() != null) {
					BusStop busStop = new BusStop(busRouteElement.getBusStop()
							, busRouteElement.arriveTime + "");
					if (busStop.equals(routeData.getBusStop(true)) || busStop.equals(routeData.getBusStop(false))) {
						busStop.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
					}
					// 上書き
					busRouteElement.setBusStop(busStop);
					arrayList.add(busStop);

					// バス停の情報がないとずれる可能性があるので
					// ヒットしたデータでインデックス更新
					if (selectedRoute == busRouteElement) {
						selectIndex = i;
					}
					i++;
				}

			}

			map.addMarker(arrayList, true);
			// ルート上のバス停が全て無い時の対策
			if (busStopRoute.get(selectIndex).getBusStop().getPoint() != null) {
				map.setFocus(selectIndex);
			}
		}
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.support.v4.app.FragmentActivity#
	 * onRetainCustomNonConfigurationInstance()
	 */
	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		return new Object();
	}
}
