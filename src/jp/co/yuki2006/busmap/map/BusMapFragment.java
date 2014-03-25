package jp.co.yuki2006.busmap.map;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import jp.co.yuki2006.busmap.R;
import jp.co.yuki2006.busmap.bustimeline.TimeLineActivity;
import jp.co.yuki2006.busmap.etc.Etc;
import jp.co.yuki2006.busmap.etc.TransitionManager;
import jp.co.yuki2006.busmap.store.BusStop;
import jp.co.yuki2006.busmap.store.LoadingZone;
import jp.co.yuki2006.busmap.store.MarkerItems;
import jp.co.yuki2006.busmap.web.IWebPostRunnable;
import jp.co.yuki2006.busmap.web.WebPortalBase;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class BusMapFragment extends com.google.android.gms.maps.SupportMapFragment
		implements OnInfoWindowClickListener, OnMarkerClickListener, IWebPostRunnable<ArrayList<BusStop>>,
		OnMapLongClickListener {
	/**
	 *
	 */
	private static final LatLng DEFAULT_INITIAL = new LatLng(36.561066, 136.656489);

	/**
	 * http://dev.classmethod.jp/smartphone/android/android-google-play-services
	 * -2/
	 */
	public static class ErrorDialogFragment extends DialogFragment {
		private Dialog mDialog;

		public ErrorDialogFragment() {
			super();
			mDialog = null;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}

		public void setDialog(Dialog dialog) {
			mDialog = dialog;
		}
	}

	private static class DetailGeoData extends WebPortalBase<LatLng, Integer, String> {
		/**
		 * @param context
		 * @param postRunnable
		 */
		public DetailGeoData(Context context, IWebPostRunnable<String> postRunnable) {
			super(context, postRunnable);
		}

		/*
		 * (非 Javadoc)
		 * 
		 * @see jp.co.yuki2006.busmap.web.WebPortalBase#doInBackground(Params[])
		 */
		@Override
		protected String doInBackground(LatLng... params) {
			JSONObject locationInfo = getLocationInfo(params[0]);
			try {
				JSONArray jsonArray = (JSONArray) locationInfo.get("results");
				JSONObject object = (JSONObject) jsonArray.get(0);
				JSONArray addressComponents = (JSONArray) object.get("address_components");
				HashMap<String, String> map = new HashMap<String, String>();
				for (int i = 0; i < addressComponents.length(); i++) {
					JSONObject jsonObject = (JSONObject) addressComponents.get(i);
					JSONArray object2 = (JSONArray) jsonObject.get("types");
					if (!object2.get(0).toString().equals("country")) {
						map.put(object2.get(0).toString(), jsonObject.get("long_name").toString());
					}
				}
				String[] SORT_ADDRESS_LINE = { "administrative_area_level_1", "locality", "sublocality_level_1",
						"sublocality_level_2", "sublocality_level_3", "sublocality_level_4" };
				StringBuilder stringBuilder = new StringBuilder();
				for (String key : SORT_ADDRESS_LINE) {
					if (map.containsKey(key)) {
						stringBuilder.append(map.get(key));
					}
				}
				return stringBuilder.toString();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return "";
		}

		public static JSONObject getLocationInfo(LatLng latLng) {

			HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?address=" + latLng.latitude
					+ ","
					+ latLng.longitude + "&region=JP&language=ja&sensor=false");
			HttpClient client = new DefaultHttpClient();
			HttpResponse response;
			StringBuilder stringBuilder = new StringBuilder();

			try {
				response = client.execute(httpGet);
				HttpEntity entity = response.getEntity();
				InputStream stream = entity.getContent();
				InputStreamReader objReader = new InputStreamReader(stream);
				BufferedReader objBuf = new BufferedReader(objReader);
				String sLine;
				while ((sLine = objBuf.readLine()) != null) {
					stringBuilder.append(sLine);
				}
			} catch (ClientProtocolException e) {
			} catch (IOException e) {
			}
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject = new JSONObject(stringBuilder.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return jsonObject;
		}

	}

	// class MapMomentumTimer extends TimerTask {
	// private GeoPoint oldGeo;
	// private final Runnable runnable;
	//
	// public MapMomentumTimer(GeoPoint oldGeo, Runnable runnable) {
	// this.oldGeo = oldGeo;
	// this.runnable = runnable;
	// }
	//
	// @Override
	// public void run() {
	// GeoPoint mapCenter = getMapCenter();
	// // 不動点かどうか調べる。
	// if (oldGeo.getLatitudeE6() == mapCenter.getLatitudeE6()
	// && oldGeo.getLongitudeE6() == mapCenter.getLongitudeE6()) {
	// // 不動点だったら
	// activity.runOnUiThread(runnable);
	// cancel();
	// } else {
	// oldGeo = mapCenter;
	// }
	//
	// }
	// }

	/**
	 * @author yuki
	 */
	public class MapMomentumTimer extends TimerTask {

		private CameraPosition cameraPosition;
		private Runnable runnable;

		/**
		 * @param cameraPosition
		 * @param runnable
		 */
		public MapMomentumTimer(CameraPosition cameraPosition, Runnable runnable) {
			this.cameraPosition = cameraPosition;
			this.runnable = runnable;
		}

		/*
		 * (非 Javadoc)
		 * 
		 * @see java.util.TimerTask#run()
		 */
		@Override
		public void run() {
			if (cameraPosition.target.equals(cameraPosition.target)) {
				runnable.run();
			}
		}

	}

	public LatLng initailLocation = DEFAULT_INITIAL;
	/**
	 * 4月1日になったらアイコンが変わるやつです.
	 */
	private static final int APRIL_CODE = 3;

	/**
	 * 4月1日になったらアイコンが変わるやつです.
	 */
	private static final int FIRSTDAY = 1;

	/**
	 * バス停のマーカーです.
	 * 
	 * @see MarkerItems
	 */
	private MarkerItems markerItems;
	private BusStop selectedItem;
	protected boolean isSelectableMode = false;
	private boolean detailMode;

	public BusMapFragment() {
		super();
		setRetainInstance(true);
		// super(activity, getApiKey(activity));

		// getMap().addMarker(new MarkerOptions().position(START_POINT));

		// myLocationOverlay = new CustomMyLocation(activity, this);
		//
		// myLocationOverlay.enableMyLocation();
		// myLocationOverlay.enableCompass();

		// detailFocusPosition = new DetailFocusPosition(getResources()
		// .getDrawable(R.drawable.april), this);

		// // マーカーのレイヤーをまず入れる。
		// getOverlays().add(marker);
		// // ロケーションレイヤーを入れる。
		// getOverlays().add(myLocationOverlay);
		// // ロケーションレイヤーを入れる。
		// getOverlays().add(myLocationOverlay.customMyLocationOverlay);
		// // ロングタップ地点のレイヤーを入れる。
		//
		// getOverlays().add(detailFocusPosition);
		//
		// // アプリ起動直後の場合デフォルトの設定
		// if (getZoomLevel() <= 5) {
		// getController().setCenter(START_POINT);
		// getController().setZoom(START_ZOOM_LEVEL);
		// }

	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see com.google.android.gms.maps.SupportMapFragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		registerForContextMenu(getView());
		if (map == null) {
			map = getMap();
			if (map == null) {
				return;
			}
			map.getUiSettings().setAllGesturesEnabled(true);
			map.setMyLocationEnabled(true);
			map.setIndoorEnabled(true);

			map.setOnMarkerClickListener(this);
			map.setOnInfoWindowClickListener(this);

			map.setOnMapLongClickListener(this);

			// Drawable defaultMarker = getResources().getDrawable(
			// R.drawable.busstop);
			// エイプリルフールてきなあれ
			Calendar date = Calendar.getInstance();
			if (APRIL_CODE == date.get(Calendar.MONTH)
					&& FIRSTDAY == date.get(Calendar.DATE)) {
			}
			markerItems = new MarkerItems(R.drawable.busstop, map);
			map.moveCamera(
					CameraUpdateFactory.newLatLngZoom(initailLocation, initailLocation == DEFAULT_INITIAL ? 10 : 18));
			OnCameraChangeListener arg0 = new OnCameraChangeListener() {
				@Override
				public void onCameraChange(CameraPosition cameraPosition) {
					setOnMapMove();
				}
			};

			map.setOnCameraChangeListener(arg0);
			OnMyLocationChangeListener listener = new OnMyLocationChangeListener() {
				@Override
				public void onMyLocationChange(Location location) {
					map.setOnMyLocationChangeListener(null);

					LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

					map.animateCamera(
							CameraUpdateFactory.newLatLngZoom(latLng, 18));
					showCurrentPosition(latLng);
				}
			};
			if (initailLocation == DEFAULT_INITIAL) {
				map.setOnMyLocationChangeListener(listener);
			}
			registerForContextMenu(getView());

		}
	}

	/**
	 * @param result
	 * @param forceAdd
	 *            おなじバス停IDでも強制的に追加します。
	 */
	public void addMarker(ArrayList<BusStop> result, boolean forceAdd) {
		markerItems.addNewMarkerAllData(result, forceAdd, detailMode);

	}

	/**
	 * @return selectedItem
	 */
	public BusStop getSelectedItem() {
		return selectedItem;
	}

	/**
	 * @return
	 */
	public boolean isDetailMode() {
		return detailMode;
	}

	public static LatLng getGeoPoint(JSONObject jsonObject) {

		Double lon = Double.valueOf(0);
		Double lat = Double.valueOf(0);
		try {

			lon = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
					.getJSONObject("geometry").getJSONObject("location")
					.getDouble("lng");

			lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
					.getJSONObject("geometry").getJSONObject("location")
					.getDouble("lat");

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return new LatLng(lat, lon);

	}

	@Override
	public void onMapLongClick(final LatLng e) {
		showCurrentPosition(e);
	}

	private void showCurrentPosition(final LatLng e) {
		IWebPostRunnable<String> postRunnable = new IWebPostRunnable<String>() {
			@Override
			public void onPostRunnable(String addressLine) {
				MarkerOptions markerOption =
						new MarkerOptions().position(e).title(addressLine).icon(
								BitmapDescriptorFactory.fromResource(R.drawable.map_detail_dummy));
				// .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
				detailMarker = map.addMarker(markerOption);
				detailMarker.showInfoWindow();
			}
		};
		DetailGeoData detailGeoData = new DetailGeoData(getActivity(), postRunnable);
		detailGeoData.execute(e);
	}

	void showNearBusStopList(final LatLng latLng) {
		if (map.getCameraPosition().zoom < Etc.getIncrementalLevel(getActivity()) + 1) {
			CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, Etc.getIncrementalLevel(getActivity()) + 1);
			map.moveCamera(update);
		}

		IWebPostRunnable<BusStop[]> postRunnable = new
				IWebPostRunnable<BusStop[]>() {

					@Override
					public void onPostRunnable(final BusStop[] result) {
						AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
						CharSequence[] items = new String[result.length];
						for (int i = 0; i < result.length; i++) {
							items[i] = result[i].toString();

						}
						DialogInterface.OnClickListener listener = new
								DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										setSelectedItem(result[which]);
										// BusMapView.this.setSelectedItem(result[which]);
										setMapCenter(result[which].getPoint(), true);
										setOnMapMove();
										// BusMapActivity.this.openContextMenu(map);
									}
								};
						builder.setTitle("この地点での近くのバス停です。");
						builder.setItems(items, listener);
						builder.create().show();

					}
				};
		NearBusStopLoader busStopLoader = new NearBusStopLoader(
				getActivity(), postRunnable);
		busStopLoader.execute(latLng);
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see
	 * jp.co.yuki2006.busmap.web.IWebPostRunnable#postRunnable(java.lang.Object)
	 */
	@Override
	public void onPostRunnable(ArrayList<BusStop> result) {
		addMarker(result, false);
		// invalidate();
	}

	// public void onResume() {
	// myLocationOverlay.enableMyLocation();
	//
	// }

	// @Override
	// public boolean onTouchEvent(MotionEvent event) {
	// switch (event.getAction()) {
	// case MotionEvent.ACTION_MOVE:
	// moveStatus = true;
	// break;
	// case MotionEvent.ACTION_UP:
	// if (moveStatus) {
	// setOnMapMove();
	// }
	// moveStatus = false;
	// break;
	// default:
	//
	// break;
	// }
	//
	// return super.onTouchEvent(event);
	// }

	public void setFocus(int index) {
		Marker marker = markerItems.getMarker(index);
		marker.showInfoWindow();
		selectedItem = markerItems.get(marker);

		map.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 20));
	}

	// public void setLastFix() {
	// Location location = myLocationOverlay.getLastFix();
	// if (location == null) {
	// if (LocationGetter.getLocationSettingDialog(activity) != null) {
	// Toast.makeText(activity, R.string.please_wait_location_get,
	// Toast.LENGTH_LONG).show();
	// }
	// return;
	// }
	//
	// GeoPoint point = new GeoPoint(
	// (int) (location.getLatitude() * 1000000.0),
	// (int) (location.getLongitude() * 1000000.0));
	// activity.setMapCenter(point, false);
	// myLocationOverlay.customMyLocationOverlay.invalidate();
	//
	// Toast.makeText(
	// activity,
	// getResources().getString(R.string.accuracy_fix_meter,
	// (int) location.getAccuracy()), Toast.LENGTH_LONG)
	// .show();
	//
	// }

	/**
	 * @param i
	 */
	public void setNextItem(int diff) {
		int i = markerItems.indexOf(selectedItem) + diff;
		Marker marker = markerItems.getMarker(i);
		marker.showInfoWindow();
		selectedItem = markerItems.get(marker);
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), map.getCameraPosition().zoom));
	}

	/**
	 * 地図が動かされたときに色々するメソッドです。 マップを動かしてから呼んでください。
	 */
	public void setOnMapMove() {
		final BusStopOnMapLoader busStopOnMapLoader = new BusStopOnMapLoader(
				getActivity(), Etc.getMAXStopMarker(getActivity()), this);
		TextView modeText = (TextView) getActivity().findViewById(R.id.loadingmode);
		// このTextViewがない画面の場合
		// 詳細検索の場合
		if (modeText == null) {
			return;
		}
		final Timer timer = new Timer();
		int incrementalLevel = Etc.getIncrementalLevel(getActivity());
		if (getMap().getCameraPosition().zoom > incrementalLevel + 1) {
			modeText.setText("詳細モード");
			detailMode = true;
		} else {
			modeText.setText("広域モード");
			detailMode = false;

		}
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				// タイマー停止
				timer.cancel();
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						MapMarkerParam params = new MapMarkerParam(getMap(),
								!detailMode);
						busStopOnMapLoader.execute(params);
					}
				});
			}
		};
		if (getMap().getCameraPosition().zoom > incrementalLevel) {
			// runnable.run();
			timer.schedule(new
					MapMomentumTimer(getMap().getCameraPosition(), runnable),
					300,
					300);
			// MapMarkerParam params = new MapMarkerParam(getMap(),
			// !detailMode);
			// busStopOnMapLoader.execute(params);
		} else {
			modeText.setText(R.string.if_display_marker_on_map_zoom);
		}

	}

	/**
	 * @param selectedItem
	 *            セットする selectedItem
	 */
	public void setSelectedItem(BusStop selectedItem) {
		// 広域モードの時にはすべてのバス停
		if (isDetailMode() == false) {
			selectedItem.setLoading(new LoadingZone("", ""));
		}
		this.selectedItem = selectedItem;
	}

	/**
	 * アクティビティから帰ってきたときのマーカーを表示するズームレベルです.
	 */
	private static final int RETURM_ZOOM_LEVEL = 19;
	public static final int REQUEST_INSTALL_SUPPORT_LIBRARY = 1;
	private GoogleMap map;
	private Marker detailMarker;

	/**
	 * @param geoPoint
	 * @param notAnimation
	 */
	public void setMapCenter(LatLng geoPoint, boolean notAnimation) {
		if (notAnimation) {
			getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(geoPoint, RETURM_ZOOM_LEVEL));
		} else {
			getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(geoPoint, RETURM_ZOOM_LEVEL));

		}

	}

	/**
     *
     */
	public void setLastFix() {

	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener#
	 * onInfoWindowClick(com.google.android.gms.maps.model.Marker)
	 */
	@Override
	public void onInfoWindowClick(Marker paramMarker) {
		if (isCurrentPosition(paramMarker)) {
			showNearBusStopList(paramMarker.getPosition());
		} else {
			// マップから選択モードの時
			if (isSelectableMode) {
				TransitionManager intent = new TransitionManager(getActivity(),
						TimeLineActivity.class, getSelectedItem());
				getActivity().setResult(Activity.RESULT_OK, intent);
				getActivity().finish();
				return;
			}
			getActivity().openContextMenu(getView());
		}
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see
	 * com.google.android.gms.maps.SupportMapFragment#onCreate(android.os.Bundle
	 * )
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		checkServiceAvailable();
	}

	private void checkServiceAvailable() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
		if (ConnectionResult.SUCCESS == resultCode) {
			// Google Play Services 利用可能
		} else {
			// Google Play Services 利用不可

			// ErrorDialog の表示
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
					REQUEST_INSTALL_SUPPORT_LIBRARY);
			if (dialog != null) {
				ErrorDialogFragment frag = new ErrorDialogFragment();
				frag.setDialog(dialog);
				frag.show(getFragmentManager(), "error_dialog_fragment");
			}
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		menu.setHeaderTitle(selectedItem.toString());
		android.view.MenuInflater infect = getActivity().getMenuInflater();
		infect.inflate(R.menu.buslist_contextmenu, menu);

		super.onCreateContextMenu(menu, v, menuInfo);
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see
	 * com.google.android.gms.maps.GoogleMap.OnMarkerClickListener#onMarkerClick
	 * (com.google.android.gms.maps.model.Marker)
	 */
	@Override
	public boolean onMarkerClick(Marker paramMarker) {
		if (isCurrentPosition(paramMarker)) {
		} else {
			selectedItem = markerItems.get(paramMarker);
		}
		return false;
	}

	private boolean isCurrentPosition(Marker paramMarker) {
		if (detailMarker != null && paramMarker.getId().equals(detailMarker.getId())) {
			return true;
		}
		return false;
	}
}
