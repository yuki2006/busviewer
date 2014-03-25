/**
 *
 */
package jp.co.yuki2006.busmap.route.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jp.co.yuki2006.busmap.R;
import jp.co.yuki2006.busmap.SearchBusLoader;
import jp.co.yuki2006.busmap.ad.AdActivity;
import jp.co.yuki2006.busmap.bustimeline.LoadingListAdapter;
import jp.co.yuki2006.busmap.db.MyBusStopDB;
import jp.co.yuki2006.busmap.etc.Etc;
import jp.co.yuki2006.busmap.etc.ResultSearchBusList;
import jp.co.yuki2006.busmap.etc.TransitionManager;
import jp.co.yuki2006.busmap.map.BusMapActivity;
import jp.co.yuki2006.busmap.parser.BusLoadingXMLParser;
import jp.co.yuki2006.busmap.route.AdvancedSearchConditionActivity;
import jp.co.yuki2006.busmap.route.AdvancedSearchResultActivity;
import jp.co.yuki2006.busmap.route.NewAdvancedSearchResultActivity;
import jp.co.yuki2006.busmap.route.store.RouteData;
import jp.co.yuki2006.busmap.store.BusStop;
import jp.co.yuki2006.busmap.store.LoadingZone;
import jp.co.yuki2006.busmap.values.IntentValues;
import jp.co.yuki2006.busmap.web.IWebPostRunnable;
import jp.co.yuki2006.busmap.web.WebPortal;

/**
 * @author yuki
 */
public class AdvancedSearchFormFragment extends SherlockFragment {
	private static enum CommandEnum {
		DEPARTURE_GET_FOR_TV, ARRIVE_GET_FOR_TV,
		SEARCH
	}

	private class LoadingLoader extends WebPortal<Integer, Void, LoadingZone[]> {

		public LoadingLoader(Activity activity, boolean showDialog, LoadingLoaderPostRunnable postRunnable) {
			super(activity, showDialog, postRunnable, false);
		}

		@Override
		protected LoadingZone[] onBackGroundCore(InputStream is) {
			return BusLoadingXMLParser.getLoading(is);
		}

		@Override
		protected String onParamParser(Integer params) {
			return "route/getStationLoading.php?BusStopID=" + params;
		}
	}

	private class LoadingLoaderPostRunnable implements IWebPostRunnable<LoadingZone[]> {
		public final boolean isDeparture;
		private final BusStop busStop;

		public LoadingLoaderPostRunnable(BusStop busStop, boolean isDeparture) {
			this.busStop = busStop;
			this.isDeparture = isDeparture;
		}

		/*
		 * (非 Javadoc)
		 *
		 * @see
		 * jp.co.yuki2006.busmap.web.IWebPostRunnable#onPostRunnable(java.lang
		 * .Object)
		 */
		@Override
		public void onPostRunnable(LoadingZone[] loadingList) {
			data.setLoadingList(isDeparture, loadingList);
			busStop.setLoading(loadingList[0]);
			data.setBusStop(isDeparture, busStop);
			// if (busStop.getLoading().getDbId() == 0 &&
			// busStop.getLoading().getLoadingID().equals("")) {
			// busStop.setLoading(loadingList[0]);
			// }
			execCommand(getView());
			if (data.getBusStop(true) != null && data.getBusStop(false) != null) {
				// キーボードを隠す
				InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(
						Context.INPUT_METHOD_SERVICE);
				// ボタンを押したときにソフトキーボードを閉じる
				View currentFocus = getActivity().getCurrentFocus();
				if (currentFocus != null) {
					inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
				}
			}
		}

	}

	private LoadingLoader loader;

	private final ArrayList<CommandEnum> commandList = new ArrayList<CommandEnum>();
	private RouteData data;

	public static final int DEPARTURE_REQUEST_CODE = 3;

	public static final int ARRIVE_REQUEST_CODE = 2;
	static final int BUSSTOP_TEXT = 0;

	static final int BUSSTOP_LOADING_TEXT = 1;
	static final int BUSSTOP_EDIT_TEXT = 2;

	static final int CHANGE_BUTTON = 3;

	private final int[][] SUB_LAYOUTS = new int[][] {
			{ R.id.rooting_departure_container, R.id.rooting_arrive_container },
			{ R.string.rooting_departure_container_label, R.string.rooting_arrive_container_label, },
	};

	/**
	 * バス停の変更をする時
	 *
	 * @param textView
	 * @param isDeparture
	 */
	private void changeBusStop(TextView textView, final boolean isDeparture) {

		data.setFilterText(isDeparture, textView.getText().toString());
		IWebPostRunnable<ResultSearchBusList> postRunnable = new IWebPostRunnable<ResultSearchBusList>() {
			@Override
			public void onPostRunnable(ResultSearchBusList result) {
				showBusStopListFormList(isDeparture, result.busStopList);
			}
		};

		SearchBusLoader loader = new SearchBusLoader(getActivity(), true, postRunnable);
		loader.execute(textView.getText().toString());
	}

	private void clearBusStop(final TextView textView, final boolean isDeparture) {
		BusStop busStop = data.getBusStop(isDeparture);
		data.setFilterText(isDeparture, busStop.getBusStopName());
		data.setBusStop(isDeparture, null);
		invalidate(getView());
	}

	private void execCommand(View view) {
		if (commandList.size() > 0) {
			switch (commandList.get(0)) {
			case SEARCH:
				invalidate(view);
				if (data.getBusStop(true) != null && data.getBusStop(false) != null) {

					transitionAdvancedSearchWrapper(getActivity(), data);
				}
				break;
			case DEPARTURE_GET_FOR_TV: {
				changeBusStop((TextView) getView().findViewById(SUB_LAYOUTS[0][0])
						.findViewById(R.id.bus_stop_edit_text), true);
				break;
			}
			case ARRIVE_GET_FOR_TV: {
				changeBusStop((TextView) getView().findViewById(SUB_LAYOUTS[0][1])
						.findViewById(R.id.bus_stop_edit_text), false);
				break;
			}
			default:
				break;
			}
			commandList.remove(0);

		} else {
			invalidate(view);
		}
	}

	public static void transitionAdvancedSearchWrapper(Activity activity, RouteData data) {
		Class<?> cls =
				data.isEnableTransfer() ?
						NewAdvancedSearchResultActivity.class :
						AdvancedSearchResultActivity.class;
		Intent intent = new Intent(activity, cls);
		intent.putExtra(IntentValues.TRANSITION_ADVANCED_SEARCH, data);
		activity.startActivityForResult(intent, AdvancedSearchConditionActivity.REQUEST_RESULT_ACTIVITY);
	}

	/**
	 * @return data
	 */
	public RouteData getRouteData() {
		return this.data;
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setHasOptionsMenu(true);
		setRetainInstance(true);

	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.support.v4.app.Fragment#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {

		super.onActivityResult(requestCode, resultCode, intent);

		if ((requestCode == ARRIVE_REQUEST_CODE || requestCode == DEPARTURE_REQUEST_CODE)
				&& resultCode == Activity.RESULT_OK) {

			boolean isDeparture = requestCode == DEPARTURE_REQUEST_CODE;

			// 設定するのはブーリアンを反転する
			setBusStopFromBundle(intent.getExtras(), isDeparture);

		} else if (requestCode == AdvancedSearchConditionActivity.REQUEST_RESULT_ACTIVITY
				&& resultCode == Activity.RESULT_OK) {
			data = (RouteData) intent.getSerializableExtra(IntentValues.TRANSITION_ADVANCED_SEARCH);
			FragmentManager fragmentManager = getFragmentManager();
			TimeSelectFragment fragment = (TimeSelectFragment) fragmentManager
					.findFragmentByTag(TimeSelectFragment.TIME_SELECT_FRAGMENT_TAG);
			fragment.invalidate(fragment.getView());
			invalidate(getView());
		}
	}

	public static class TransferCofirmDialog extends DialogFragment {
		/*
		 * (非 Javadoc)
		 *
		 * @see
		 * android.support.v4.app.DialogFragment#onCreateDialog(android.os.Bundle
		 * )
		 */
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new Builder(getActivity());
			builder.setTitle("注意！");
			builder.setMessage("乗換検索機能は、まだβ版なので使いづらい点や、表示のミスがある可能性があります。\n注意してご使用ください。\n\n" +
					"乗り換えは最大1回までです。\n" +
					"また、乗換検索では乗り場の指定はできません。\n" +
					"乗り換えない場合は、チェックを入れないほうが素早く検索出来ます。");
			builder.setPositiveButton(android.R.string.ok, null);
			return builder.create();
		}
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {
		View view = inflater.inflate(R.layout.rooting_view_layout, viewGroup, false);
		DisplayMetrics metrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
		boolean isCompactLayout = (metrics.widthPixels / metrics.density >= 480 && metrics.heightPixels
				/ metrics.density < 500);
		((LinearLayout) view.findViewById(R.id.advanced_search_orientation_ll))
				.setOrientation(isCompactLayout ? LinearLayout.HORIZONTAL
						: LinearLayout.VERTICAL);
		AdActivity.insertMob(getActivity(), view);

		view.findViewById(R.id.advanced_search_switch_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				data.swap();
				invalidate(getView());
			}
		});
		view.findViewById(R.id.advanced_search_button_in_form).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchBus();
			}
		});

		for (int element : SUB_LAYOUTS[0]) {
			final LinearLayout container = (LinearLayout) view.findViewById(element);
			final boolean isDeparture = element == R.id.rooting_departure_container;
			((TextView) container.findViewById(R.id.container_label)).
					setText(SUB_LAYOUTS[1][isDeparture ? 0 : 1]);
			android.widget.LinearLayout.LayoutParams layoutParams = (android.widget.LinearLayout.LayoutParams) container
					.getLayoutParams();
			layoutParams.weight = isCompactLayout ? 1f : 0f;
			View byMyBusStop = container.findViewById(R.id.change_by_my_busstop);

			byMyBusStop.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					MyBusStopDB db = new MyBusStopDB(getActivity());
					final List<BusStop> list = db.getBusStopData();
					showBusStopListFormList(isDeparture, list);
				}

			});
			View byMapButton = container.findViewById(R.id.change_by_map_button);

			byMapButton.setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(getActivity(), BusMapActivity.class);
							intent.putExtra("equireSelectMenu", true);
							int requestCode = isDeparture ?
									DEPARTURE_REQUEST_CODE
									: ARRIVE_REQUEST_CODE;
							startActivityForResult(intent, requestCode);
						}
					});
			// 乗り場のリスト
			final Spinner loadingZoneList = (Spinner) container.findViewById(R.id.bus_stop_loading_spinner);

			((Spinner) loadingZoneList)
					.setOnItemSelectedListener(new OnItemSelectedListener() {
						@Override
						public void onItemSelected(AdapterView<?> adapterview, View view, int i, long l) {
							data.getBusStop(isDeparture).setLoading(
									((BusStop) loadingZoneList.getAdapter().getItem(i)).getLoading());
						}

						@Override
						public void onNothingSelected(AdapterView<?> adapterview) {

						}
					});
			final TextView editText = (TextView) container.findViewById(R.id.bus_stop_edit_text);
			editText.addTextChangedListener(new TextWatcher() {
				@Override
				public void afterTextChanged(Editable s) {
					data.setFilterText(isDeparture, s.toString());
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {

				}

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {

				}
			});
			// View searchButton = container.findViewById(R.id.search_button);
			// searchButton.setOnClickListener(new OnClickListener() {
			// @Override
			// public void onClick(View view) {
			// changeBusStop(editText, isDeparture);
			// }
			// });
			editText.setOnKeyListener(new Etc.OnNextKeyDownListener() {
				@Override
				public void onNextKeyDown() {
					changeBusStop(editText, isDeparture);
				}
			});
			final ImageButton editButton = (ImageButton) container.findViewById(R.id.edit_button);

			editButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					clearBusStop((TextView) container.findViewById(R.id.bus_stop_edit_text)
							, isDeparture);
				}
			});
			// スピナーの初期化
			SpinnerAdapter adapter = new LoadingListAdapter(getActivity());
			loadingZoneList.setAdapter(adapter);
		}

		getActivity().getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setArgumentForRouteData(view);

		FragmentTransaction transaction =
				getFragmentManager().beginTransaction();

		TimeSelectFragment timeSelectFragment = new TimeSelectFragment();
		Bundle arguments = new Bundle();
		arguments.putSerializable(IntentValues.TRANSITION_ADVANCED_SEARCH, data);
		timeSelectFragment.setArguments(arguments);
		transaction
				.replace(R.id.time_select_view_stub, timeSelectFragment, TimeSelectFragment.TIME_SELECT_FRAGMENT_TAG);
		transaction.commit();
		return view;
	}

	private void setArgumentForRouteData(View view) {
		Bundle arguments = getArguments();
		if (data == null) {
			if (arguments != null) {
				data = (RouteData) arguments.get(IntentValues.TRANSITION_ADVANCED_SEARCH);
				if (data != null) {
					if (data.getBusStop(true) != null) {
						if (data.getLoadingArray(true) == null) {
							selectDefaultLoading(data.getBusStop(true), true);
						}
					}

					if (data.getBusStop(false) != null) {
						if (data.getLoadingArray(false) == null) {
							selectDefaultLoading(data.getBusStop(false), false);
						}
					}
					execCommand(view);
					// commandList.add(CommandEnum.SEARCH);
					invalidate(view);
				} else {
					data = new RouteData();
					// マイルートからの呼び出しではないとき
					Boolean depatureByIntent = TransitionManager.isDepatureByIntent(arguments);
					if (depatureByIntent == null) {
					} else {
						setBusStopFromBundle(arguments, depatureByIntent);
						invalidate(view);
					}
				}
			} else {
				data = new RouteData();
			}
		} else {
			// 画面回転でデータが引継ぎがあるとき
			invalidate(view);
		}
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.support.v4.app.Fragment#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (loader != null) {
			loader.onDestroy();
		}
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();

		getSherlockActivity().invalidateOptionsMenu();
	}

	public void invalidate(View view) {
		boolean bool = false;

		do {
			BusStop busStop = data.getBusStop(bool);
			View container = view.findViewById(SUB_LAYOUTS[0][bool ? 0 : 1]);
			ViewSwitcher viewSwitcher = (ViewSwitcher) container
					.findViewById(R.id.rooting_busstop_text_view_swicher);
			LoadingZone[] loadingArray = data.getLoadingArray(bool);

			if (busStop != null && loadingArray != null) {
				((TextView) container.findViewById(R.id.bus_stop_text_view)).
						setText(busStop.getBusStopNameAndRegion());

				Spinner spinner = (Spinner) view.findViewById(SUB_LAYOUTS[0][bool ? 0 : 1]).findViewById(
						R.id.bus_stop_loading_spinner);
				LoadingListAdapter loadingSpinerAdapter = (LoadingListAdapter) spinner.getAdapter();
				loadingSpinerAdapter.clear();

				loadingSpinerAdapter.addAllLoadingZoneList(loadingArray);

				spinner.invalidate();
				for (int i = 0; i < loadingSpinerAdapter.getCount(); i++) {
					if (((BusStop) loadingSpinerAdapter.getItem(i)).getLoading().equals(busStop.getLoading())) {
						spinner.setSelection(i, true);
					}
				}

				viewSwitcher.setDisplayedChild(1);
			} else {
				viewSwitcher.setDisplayedChild(0);
				TextView busStopTextView = (TextView) container.findViewById(R.id.bus_stop_edit_text);

				busStopTextView.setText(data.getFilterText(bool));

			}
			// ViewSwitcher searchButtonSwitcher = (ViewSwitcher) container
			// .findViewById(R.id.rooting_busstop_search_swicher);

			// searchButtonSwitcher.setDisplayedChild(busStop == null ? 1 : 0);
			ImageButton imageButton = (ImageButton) container
					.findViewById(R.id.edit_button);

			imageButton.setVisibility(busStop == null ? View.GONE : View.VISIBLE);
			bool = !bool;
		} while (bool);
		getSherlockActivity().invalidateOptionsMenu();

	}

	/**
	 * 実際にバスの詳細検索を行います。
	 */
	public void searchBus() {
		// インスタンスデータの更新のためのハック
		if ((data.getBusStop(true) == null && "".equals(data.getFilterText(true)))
				|| (data.getBusStop(false) == null && ""
						.equals(data.getFilterText(false)))) {

			Builder builder = new Builder(getActivity());
			builder.setMessage("検索条件を入力してください");
			builder.setPositiveButton(android.R.string.ok, null);
			builder.show();
			return;
		}

		if (data.getBusStop(true) == null && !"".equals(data.getFilterText(true))) {
			commandList.add(CommandEnum.DEPARTURE_GET_FOR_TV);
		}
		if (data.getBusStop(false) == null && !"".equals(data.getFilterText(false))) {
			commandList.add(CommandEnum.ARRIVE_GET_FOR_TV);

		}
		commandList.add(CommandEnum.SEARCH);
		execCommand(getView());

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {

		// メニューインフレーターを取得
		// xmlのリソースファイルを使用してメニューにアイテムを追加
		AdvancedSearchActionMenuUtil.onCreateOptionsMenu(menu, menuInflater);
		// menuInflater.inflate(R.menu.advanced_search_view_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return AdvancedSearchActionMenuUtil.onOptionsItemSelected(getSherlockActivity(), item, getRouteData());
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		AdvancedSearchActionMenuUtil.onPrepareOptionsMenu(getActivity(), menu, getRouteData());
	}

	/**
	 * 検索する乗り場を指定してもらうリストを作成します。
	 *
	 * @param defaultLoadingZone
	 * @param intent
	 */
	private void selectDefaultLoading(BusStop busStop, boolean isDeparture) {
		loader = new LoadingLoader(getActivity(), true, new LoadingLoaderPostRunnable(busStop, isDeparture));
		loader.execute(busStop.getBusStopID());

	}

	private void setBusStopFromBundle(final Bundle bundle, boolean isDeparture) {
		BusStop busStop = TransitionManager.getBusStopByIndent(getActivity(), bundle);
		selectDefaultLoading(busStop, isDeparture);
	}

	private void showBusStopListFormList(final boolean isDeparture, final List<BusStop> list) {
		Builder builder = new Builder(getActivity());

		android.content.DialogInterface.OnClickListener listener = new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				selectDefaultLoading(list.get(which), isDeparture);
			}
		};
		// たまたま1つだけだった時
		if (list.size() == 1) {
			selectDefaultLoading(list.get(0), isDeparture);
			return;
		} else if (list.size() == 0) {
			Toast.makeText(getActivity(),
					getResources().getStringArray(R.array.not_found_busstop_for_advanced_search)[isDeparture ? 0 : 1]
					, Toast.LENGTH_LONG).show();

			execCommand(getView());
			return;
		}

		CharSequence[] items = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			items[i] = list.get(i).toString();

		}
		OnCancelListener onCancelListener = new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				// キャンセルされたらコマンドを全て削除
				commandList.clear();
				execCommand(getView());
			}
		};
		builder.setOnCancelListener(onCancelListener);
		builder.setTitle(
				getResources().getStringArray(R.array.select_buslist_for_advanced_search)[isDeparture ? 0
						: 1])
				.setItems(items, listener).show();
	}

	/**
	 * @param data2
	 */
	public void setRouteData(RouteData data) {
		this.data = data;

	}

}
