/**
 *
 */
package jp.co.yuki2006.busmap.route;

import android.app.AlarmManager;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import java.io.InputStream;
import java.util.ArrayList;

import jp.co.yuki2006.busmap.R;
import jp.co.yuki2006.busmap.ad.AdActivity;
import jp.co.yuki2006.busmap.db.AdvancedSearchHistoryDB;
import jp.co.yuki2006.busmap.etc.TransitionManager;
import jp.co.yuki2006.busmap.map.AdvancedSearchOnMapActivity;
import jp.co.yuki2006.busmap.parser.BusRouteListXMLParser;
import jp.co.yuki2006.busmap.route.fragment.AdvancedSearchActionMenuUtil;
import jp.co.yuki2006.busmap.route.fragment.AdvancedSearchFormFragment;
import jp.co.yuki2006.busmap.route.fragment.TimeSelectFragment;
import jp.co.yuki2006.busmap.route.store.AdvancedSearchResult;
import jp.co.yuki2006.busmap.route.store.BusRouteElement;
import jp.co.yuki2006.busmap.route.store.BusSearch;
import jp.co.yuki2006.busmap.route.store.RouteData;
import jp.co.yuki2006.busmap.values.IntentValues;
import jp.co.yuki2006.busmap.web.IWebPostRunnable;
import jp.co.yuki2006.busmap.web.WebPortal;

/**
 * @author yuki
 */
public class AdvancedSearchResultActivity extends AdActivity implements
		IWebPostRunnable<AdvancedSearchResult[]>, OnItemClickListener, OnClickListener, IAdvancedSearch {

	private static class AdvancedSearchListResultAdapter extends ArrayAdapter<BusRouteElement> {
		LayoutInflater inflater;
		private final RouteData data;

		/**
		 * @param context
		 * @param routeResult
		 * @param textViewResourceId
		 */
		public AdvancedSearchListResultAdapter(Context context, RouteData data, ArrayList<BusRouteElement> routeResult) {
			super(context, 0, routeResult);
			this.data = data;
			inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
		}

		/*
		 * (非 Javadoc)
		 *
		 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
		 * android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.advanced_search_bus_route, parent, false);
			}
			TextView busStopTextView = (TextView) convertView.findViewById(android.R.id.text1);
			busStopTextView.setText(getItem(position).toString());
			if (getItem(position).getBusStop().equals(data.getBusStop(true))
					|| getItem(position).getBusStop().equals(data.getBusStop(false))) {

				convertView.setBackgroundResource(R.color.bus_detail_target);
			} else {
				convertView.setBackgroundResource(R.color.bus_detail_normal);
			}
			busStopTextView.setTextColor(Color.BLACK);

			// parent.setBackgroundColor(Color.WHITE);
			return convertView;
		}
	}

	static class BusDetailListLoader extends WebPortal<Integer, Integer, ArrayList<BusRouteElement>> {

		public BusDetailListLoader(SherlockFragmentActivity activity, RouteData data) {
			super(activity, true, new OnPostExecute(activity, data), false);
			this.activity = activity;
		}

		@Override
		protected ArrayList<BusRouteElement> onBackGroundCore(InputStream is) {
			return BusRouteListXMLParser.getBusDetailList(is);
		}

		@Override
		protected String onParamParser(Integer param) {
			return "route/getBusRouteList.php?busID=" + param;
		}

	}

	public static class BusRouteDialog extends DialogFragment implements
			android.content.DialogInterface.OnClickListener {
		private ArrayList<BusRouteElement> routeResult;
		private RouteData routeData;
		private android.content.DialogInterface.OnClickListener calendarListener = new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Intent.ACTION_INSERT);
				// intent.setData(Events.CONTENT_URI);
				// intent.setType("vnd.android.cursor.item/event");
				intent.putExtra("title", "【バス予定】");
				intent.putExtra("description", "乗車バス停" + routeData.getBusStop(true).toString() + "\n降車バス停"
						+ routeData.getBusStop(false).toString());
				intent.putExtra("allDay", false);

				for (BusRouteElement element : routeResult) {
					if (element.getBusStop().getBusStopID() == routeData.getBusStop(true).getBusStopID()) {
						intent.putExtra("beginTime", element.getArriveMillTime(routeData.getCurrentMiliTime())); // 開始日時
					}
					if (element.getBusStop().getBusStopID() == routeData.getBusStop(false).getBusStopID()) {
						intent.putExtra("endTime", element.getArriveMillTime(routeData.getCurrentMiliTime())); // 終了日時
					}

				}

				startActivity(intent);
			}
		};

		private Uri makeUri() {
			StringBuffer buffer = new StringBuffer();
			long nowElapsedRealtime = SystemClock.elapsedRealtime();
			buffer.append("SCHEME" + "://")
					.append("HOSTNAME" + "/")
					.append(Long.toString(nowElapsedRealtime));
			Uri uri = Uri.parse(buffer.toString());
			return uri;
		}

		private android.content.DialogInterface.OnClickListener alermListener = new android.content.DialogInterface.OnClickListener() {
			private final String mBcastAction = "jp.co.yuki2006.busmap.BCAST";

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// MyReceiver mReceiver = new MyReceiver();
				// IntentFilter filter = new IntentFilter(mBcastAction); // (4)
				// getActivity().registerReceiver(receiver, filter)
				// getActivity().registerReceiver(mReceiver, filter);
				Toast.makeText(getActivity(), "10分前に通知が表示されます。\n端末を再起動されると反応しないので気をつけてください", Toast.LENGTH_LONG).show();
				AlarmManager alarmManager = (AlarmManager) getActivity()
						.getSystemService(Context.ALARM_SERVICE);
				Intent intent = new Intent(mBcastAction);
				intent.putExtra(IntentValues.ROUTE_RESULT, routeResult);
				intent.putExtra(IntentValues.TRANSITION_ADVANCED_SEARCH, routeData);
				intent.setData(makeUri());
				for (BusRouteElement element : routeResult) {
					if (element.getBusStop().getBusStopID() == routeData.getBusStop(true).getBusStopID()) {
						intent.putExtra("bus_route", element);
						PendingIntent operation = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
						// alarmManager.set(AlarmManager.RTC_WAKEUP,
						// Calendar.getInstance().getTimeInMillis() + 1 * 60 *
						// 1000, operation);

						alarmManager.set(AlarmManager.RTC_WAKEUP,
								element.getArriveMillTime(routeData.getCurrentMiliTime())
								- 10 * 60 * 1000, operation);
						break;
					}

				}

			}
		};

		public void onClick(DialogInterface dialog, int which) {
			// バスのロケーションがあるところのみ
			if (routeResult.get(which).getBusStop().getPoint() != null) {
				TransitionManager transitionManager = new TransitionManager(getActivity(),
						AdvancedSearchOnMapActivity.class,
						routeData, routeResult, which);
				getActivity().startActivity(transitionManager);
			}
		}

		/*
		 * (非 Javadoc)
		 *
		 * @see
		 * android.support.v4.app.DialogFragment#onCreateDialog(android.os.Bundle
		 * )
		 */
		@SuppressWarnings("unchecked")
		@Override
		public Dialog onCreateDialog(Bundle arg0) {
			Builder builder = new Builder(getActivity());
			Bundle bundle = getArguments();
			routeData = (RouteData) bundle.get(IntentValues.TRANSITION_BUS_STOP_LIST);

			routeResult = (ArrayList<BusRouteElement>) bundle.getSerializable(IntentValues.ROUTE_RESULT);
			AdvancedSearchListResultAdapter adapter = new AdvancedSearchListResultAdapter(
					getActivity(), routeData, routeResult);
			builder.setTitle("このバスの通る経路です。");
			builder.setAdapter(adapter, this);
			builder.setCancelable(true);
			builder.setPositiveButton(android.R.string.ok, null);
			android.content.DialogInterface.OnClickListener listener = new android.content.DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					TransitionManager transitionManager = new TransitionManager(getActivity(),
							AdvancedSearchOnMapActivity.class, routeData, routeResult, 0);
					getActivity().startActivity(transitionManager);
				}
			};
			// builder.setNeutralButton("カレンダーに登録", calendarListener);
			builder.setNeutralButton("10分前にアラーム", alermListener);
			builder.setNegativeButton("地図上で表示", listener);
			return builder.create();
		}

	}

	static class OnPostExecute implements IWebPostRunnable<ArrayList<BusRouteElement>>

	{

		private final SherlockFragmentActivity activity;
		private final RouteData data;

		OnPostExecute(SherlockFragmentActivity activity, RouteData data) {
			this.activity = activity;
			this.data = data;

		}

		/*
		 * (非 Javadoc)
		 *
		 * @see
		 * jp.co.yuki2006.busmap.web.IWebPostRunnable#onPostRunnable(java.lang
		 * .Object)
		 */
		@Override
		public void onPostRunnable(final ArrayList<BusRouteElement> result) {
			BusRouteDialog dialog = new BusRouteDialog();
			Bundle bundle = new Bundle();
			bundle.putSerializable(IntentValues.TRANSITION_BUS_STOP_LIST, data);
			bundle.putSerializable(IntentValues.ROUTE_RESULT, result);
			dialog.setArguments(bundle);
			dialog.show(activity.getSupportFragmentManager(), "bus_list");

		}
	}

	private static class RetainData {
		SearchLoader loader;
		RouteData data;
		AdvancedSearchResult result;
		int selectedIndex = -1;
	}

	private boolean isDirectBack;

	private RetainData retainData;

	/*
	 * (非 Javadoc)
	 *
	 * @see jp.co.yuki2006.busmap.route.IAdvancedSearch#getData()
	 */
	// @Override
	// public RouteData getData() {
	// return retainData.data;
	// }

	private void navigateUpActivity() {
		Intent intent = new Intent(this, AdvancedSearchConditionActivity.class);
		intent.putExtra(IntentValues.TRANSITION_ADVANCED_SEARCH, retainData.data);
		if (isDirectBack) {
			// 普通の画面からだと画面削除
			setResult(RESULT_OK, intent);
			finish();
		} else {
			// マイルートからの呼び出しからだと新しい画面を作る

			startActivity(intent);

		}
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.change_search_condition) {
			navigateUpActivity();
		} else if (id == R.id.advanced_search_switch_button) {
			retainData.data.swap();
			searchBus();
		}

	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.advanced_search_result_view);

		ComponentName callingActivity = getCallingActivity();
		if (callingActivity == null) {
			isDirectBack = false;
		} else {
			isDirectBack = callingActivity.getClassName().equals(
					AdvancedSearchConditionActivity.class.getCanonicalName());
		}
		retainData = (RetainData) getLastCustomNonConfigurationInstance();
		if (retainData == null) {
			// 新しいインテント処理と同様な処理
			onNewIntent(getIntent());
			// retainData = new RetainData();
			// retainData.data = (RouteData) getIntent().getSerializableExtra(
			// IntentValues.TRANSITION_ADVANCED_SEARCH);
			//
			// // ウィジェットの場合は時間を現在の時間にする（何故かそういうAndroidの仕様）
			// boolean fromWidget =
			// getIntent().getBooleanExtra(IntentValues.FROM_WIDGET, false);
			// if (fromWidget) {
			// retainData.data.setNow();
			// }
			// retainData.loader = new SearchLoader(this, true, this);
			// retainData.loader.execute(new RouteData[] { retainData.data });
		} else {
			retainData.selectedIndex = -1;
			if (retainData.result != null) {
				onPostRunnable(new AdvancedSearchResult[] { retainData.result });
			}
			retainData.loader.resetActivity(this);
			retainData.loader.resetPostRunnable(this);
			// onNewIntentないでレイアウトさせるので
			MyRouteActivity.showRootBlock(this, getWindow().getDecorView(), retainData.data);
		}

		ListView listView = (ListView) findViewById(R.id.resultlist);
		// registerForContextMenu(listView);
		listView.setOnItemClickListener(this);

		findViewById(R.id.change_search_condition).setOnClickListener(this);
		findViewById(R.id.advanced_search_switch_button).setOnClickListener(this);
		ActionBar actionBar = this.getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		FragmentManager fragmentManager = getSupportFragmentManager();
		if (fragmentManager.findFragmentByTag(TimeSelectFragment.TIME_SELECT_FRAGMENT_TAG) == null) {
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			TimeSelectFragment fragment = new TimeSelectFragment();
			transaction.replace(R.id.time_select_fragment_stub, fragment, TimeSelectFragment.TIME_SELECT_FRAGMENT_TAG);
			Bundle bundle = new Bundle();
			bundle.putSerializable(IntentValues.TRANSITION_ADVANCED_SEARCH, retainData.data);
			fragment.setArguments(bundle);
			transaction.commit();
		}
		AdvancedSearchHistoryDB db = new AdvancedSearchHistoryDB(this);
		db.insertData(retainData.data);
		db.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// メニューインフレーターを取得
		// xmlのリソースファイルを使用してメニューにアイテムを追加
		AdvancedSearchActionMenuUtil.onCreateOptionsMenu(menu, getSupportMenuInflater());
		// menuInflater.inflate(R.menu.advanced_search_view_menu, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		AdvancedSearchActionMenuUtil.onPrepareOptionsMenu(this, menu, getRouteData());
		return true;
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		if (retainData.loader != null) {
			retainData.loader.onDestroy();
		}
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see
	 * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
	 * .AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		BusSearchList list = (BusSearchList) parent.getAdapter();
		BusSearch item = list.getItem(position);

		BusDetailListLoader loader = new BusDetailListLoader(this, retainData.data);
		loader.execute(item.busID);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see
	 * com.google.android.maps.MapActivity#onNewIntent(android.content.Intent)
	 */
	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent.hasExtra(IntentValues.TRANSITION_ADVANCED_SEARCH)) {
			retainData = new RetainData();
			retainData.data = (RouteData) intent.getSerializableExtra(IntentValues.TRANSITION_ADVANCED_SEARCH);
			retainData.selectedIndex = intent.getIntExtra(IntentValues.TRANSITION_ADVANCED_SEARCH_SELECT_INDEX, -1);
			// 画面表示の更新
			MyRouteActivity.showRootBlock(this, getWindow().getDecorView(),
					retainData.data);
			// ウィジェットの場合は時間を現在の時間にする（何故かそういうAndroidの仕様）
			boolean fromWidget = getIntent().getBooleanExtra(IntentValues.FROM_WIDGET, false);
			if (fromWidget) {
				retainData.data.setNow();
			}
			retainData.loader = new SearchLoader(this, true, this);
			retainData.loader.execute(new RouteData[] { retainData.data });
			//リストクリア
			setResultAdapter(new AdvancedSearchResult(this, new ArrayList<BusSearch>(), -1));

		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home: {
			// ActionBarUPWrapper.doActionUpNavigation(this);
			navigateUpActivity();
			break;
		}
		default:
			AdvancedSearchActionMenuUtil.onOptionsItemSelected(this, item, getRouteData());
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.app.Activity#onRetainNonConfigurationInstance()
	 */
	@Override
	public void onPostRunnable(final AdvancedSearchResult[] results) {
		AdvancedSearchResult result = results[0];
		if (result.busRouteElements.getCount() == 0) {
			Toast.makeText(getApplicationContext(), "条件にあったバスがありませんでした。", Toast.LENGTH_LONG).show();
		}

		if (result.fare >= 0) {
			TextView fareTextView = (TextView) findViewById(R.id.fare_textview);
			fareTextView.setText("運賃(目安) :" + result.fare + "円");
		}

		TextView busCountTextView = (TextView) findViewById(R.id.bus_count_tv);
		busCountTextView.setText(result.busRouteElements.getCount()
				+ (SearchLoader.LIMIT == result.busRouteElements.getCount() ?
						"便のみ表示しています"
						: "便ありました"
				));
		setResultAdapter(result);

	}

	/*
	 * (非 Javadoc)
	 *
	 * @see jp.co.yuki2006.busmap.view.MobActivity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		invalidateOptionsMenu();
		retainData.data.setEnableTransfer(false);
	}

	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		return retainData;
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.support.v4.app.FragmentActivity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		//
		// FragmentManager fragmentManager = getSupportFragmentManager();
		// if (fragmentManager.findFragmentByTag("menu") == null) {
		// AdvancedSearchActionMenuFragment menuFragment = new
		// AdvancedSearchActionMenuFragment();
		// Bundle bundle = new Bundle();
		// bundle.putSerializable(AdvancedSearchActionMenuFragment.ROUTE_DATA,
		// retainData.data);
		// menuFragment.setArguments(bundle);
		// FragmentTransaction transaction = fragmentManager.beginTransaction();
		// transaction.add(menuFragment, "menu");
		// transaction.commit();
		// }
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see jp.co.yuki2006.busmap.route.IAdvancedSearch#searchBus()
	 */
	@Override
	public void searchBus() {

		AdvancedSearchFormFragment.transitionAdvancedSearchWrapper(this, retainData.data);
	}

	private void setResultAdapter(AdvancedSearchResult result) {
		ListView resultlist = (ListView) findViewById(R.id.resultlist);
		resultlist.setAdapter(result.busRouteElements);
		retainData.result = result;
		if (retainData.selectedIndex >= 0 && retainData.selectedIndex < result.busRouteElements.getCount()) {
			onItemClick(resultlist, null, retainData.selectedIndex, 0);
			retainData.selectedIndex = 0;
		}

	}

	/*
	 * (非 Javadoc)
	 *
	 * @see jp.co.yuki2006.busmap.route.IAdvancedSearch#getRouteData()
	 */
	@Override
	public RouteData getRouteData() {
		return retainData.data;
	}
}
