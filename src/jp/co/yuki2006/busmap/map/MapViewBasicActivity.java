/**
 *
 */
package jp.co.yuki2006.busmap.map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import jp.co.yuki2006.busmap.R;
import jp.co.yuki2006.busmap.bustimeline.TimeLineActivity;
import jp.co.yuki2006.busmap.db.SearchMapSuggestDB;
import jp.co.yuki2006.busmap.etc.ActionBarUPWrapper;
import jp.co.yuki2006.busmap.etc.Etc;
import jp.co.yuki2006.busmap.etc.ProgressDialogFragment;
import jp.co.yuki2006.busmap.etc.ResultSearchBusList;
import jp.co.yuki2006.busmap.etc.TransitionManager;
import jp.co.yuki2006.busmap.parser.BusStopListXMLParser;
import jp.co.yuki2006.busmap.route.AdvancedSearchConditionActivity;
import jp.co.yuki2006.busmap.store.BusStop;
import jp.co.yuki2006.busmap.values.IntentValues;
import jp.co.yuki2006.busmap.values.PreferenceValues;
import jp.co.yuki2006.busmap.web.Web;

/**
 * @author yuki
 */
public abstract class MapViewBasicActivity extends SherlockFragmentActivity
		implements IFragmentToBusStop {
	public static class ARROWS_X_LTE_DIALOG extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle arg0) {
			AlertDialog.Builder builder = new Builder(getActivity());
			builder.setTitle("ARROWS X LTE (F-05D)の方へ");
			builder.setMessage("現在、マップ表示画面でアプリが落ちるという報告を受けております。これは、現在配布されているAndroid4.0に更新すると改善するとわかっています。ぜひ更新してみてください。\n詳しくはNTT Docomoのホームページを御覧ください。");
			builder.setPositiveButton(android.R.string.ok, null);
			return builder.create();
		}
	}

	public static class MixAddressSearchFragment extends Fragment {
		public final Handler handler = new Handler();
		private String busStopName;
		public final LoaderCallbacks<List<Address>> loaderSearchBusStopCallbacks = new LoaderCallbacks<List<Address>>() {
			@Override
			public Loader<List<Address>> onCreateLoader(int arg0, Bundle bundle) {
				ProgressDialogFragment.showHelperDialog(getFragmentManager());
				busStopName = bundle.getString(IntentValues.SEARCH_NAME);

				MixLocationSearchLoader mixBusStopSearchLoader = new MixLocationSearchLoader(getActivity(), busStopName);
				// mixBusStopSearchLoader.forceLoad();
				return mixBusStopSearchLoader;
			}

			@Override
			public void onLoaderReset(Loader<List<Address>> arg0) {
				// TODO 自動生成されたメソッド・スタブ
			}

			@Override
			public void onLoadFinished(final Loader<List<Address>> loader, final List<Address> addresses) {
				ProgressDialogFragment.dismissHelperDialog(getFragmentManager());
				if (addresses == null) {
					return;
				}
				handler.post(new Runnable() {

					@Override
					public void run() {

						FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
						Fragment fragment = supportFragmentManager.findFragmentByTag("list");
						if (fragment == null) {
							SearchMixLocationFragment dialogBusStopFragment = new SearchMixLocationFragment();
							Bundle argument = new Bundle();
							argument.putParcelableArray(SearchMixLocationFragment.ADDRESS_LIST,
									addresses.toArray(new Address[0]));
							argument.putString(IntentValues.SEARCH_NAME, busStopName);
							dialogBusStopFragment.setArguments(argument);
							dialogBusStopFragment.show(supportFragmentManager, "list");
						}
						getLoaderManager().destroyLoader(loader.getId());
					}
				});

			}
		};

		/*
		 * (非 Javadoc)
		 *
		 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
		 */
		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			getLoaderManager().initLoader(0, getArguments(), loaderSearchBusStopCallbacks).forceLoad();

		}
	}

	public static class MixBusStopSearchFragment extends Fragment {
		public final Handler handler = new Handler();
		public final LoaderCallbacks<ArrayList<BusStop>> loaderSearchBusStopCallbacks = new LoaderCallbacks<ArrayList<BusStop>>() {
			@Override
			public Loader<ArrayList<BusStop>> onCreateLoader(int arg0, Bundle bundle) {
				LoaderManager.enableDebugLogging(true);
				ProgressDialogFragment.showHelperDialog(getFragmentManager());
				String busStopName = bundle.getString(IntentValues.SEARCH_NAME);

				MixBusStopSearchLoader mixBusStopSearchLoader = new MixBusStopSearchLoader(getActivity(), busStopName);
				// mixBusStopSearchLoader.forceLoad();
				return mixBusStopSearchLoader;
			}

			@Override
			public void onLoaderReset(Loader<ArrayList<BusStop>> arg0) {
				// TODO 自動生成されたメソッド・スタブ

			}

			@Override
			public void onLoadFinished(final Loader<ArrayList<BusStop>> arg0, final ArrayList<BusStop> busStops) {
				ProgressDialogFragment.dismissHelperDialog(getFragmentManager());
				if (busStops == null) {
					return;
				}
				handler.post(new Runnable() {
					@Override
					public void run() {
						FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
						if (supportFragmentManager.findFragmentByTag("list") == null) {
							SearchMixBusStopFragment dialogBusStopFragment = new SearchMixBusStopFragment();
							Bundle argument = new Bundle();
							argument.putSerializable(SearchMixBusStopFragment.BUS_STOP_LIST, (Serializable) busStops);
							dialogBusStopFragment.setArguments(argument);
							dialogBusStopFragment.show(supportFragmentManager, "list");
						}
						getLoaderManager().destroyLoader(arg0.getId());
					}
				});

			}
		};

		/*
		 * (非 Javadoc)
		 *
		 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
		 */
		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			getLoaderManager().initLoader(0, getArguments(), loaderSearchBusStopCallbacks).forceLoad();

		}
	}

	public static class MixBusStopSearchLoader extends AsyncTaskLoader<ArrayList<BusStop>> {
		private final String searchName;
		private final Context context;

		/**
		 * @param searchName
		 * @param arg0
		 */
		public MixBusStopSearchLoader(Context context, String searchName) {
			super(context);
			this.context = context;
			this.searchName = searchName;
		}

		/*
		 * (非 Javadoc)
		 *
		 * @see android.support.v4.content.AsyncTaskLoader#loadInBackground()
		 */
		@Override
		public ArrayList<BusStop> loadInBackground() {
			try {
				URL url = new URL(Web.getHostURL(getContext())
						+ "search_busstop.php?search_name=" + URLEncoder.encode(searchName, "UTF-8"));
				InputStream content = (InputStream) url.getContent();
				if (content != null) {
					ResultSearchBusList setBusListView = BusStopListXMLParser.setBusListView(content);
					return setBusListView.busStopList;
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				if (context instanceof Activity) {
					Runnable action = new Runnable() {
						@Override
						public void run() {
							Toast.makeText(getContext(), R.string.connection_terminated, Toast.LENGTH_LONG).show();
						}
					};
					((Activity) context).runOnUiThread(action);

				}

				e.printStackTrace();
			}

			return null;
		}
	}

	public static class MixLocationSearchLoader extends AsyncTaskLoader<List<Address>> {
		private final String searchName;
		private Context context;

		/**
		 * @param searchName
		 * @param arg0
		 */
		public MixLocationSearchLoader(Context context, String searchName) {
			super(context);
			this.context = context;
			this.searchName = searchName;
		}

		/*
		 * (非 Javadoc)
		 *
		 * @see android.support.v4.content.AsyncTaskLoader#loadInBackground()
		 */
		@Override
		public List<Address> loadInBackground() {
			Geocoder geocoder = new Geocoder(getContext(), Locale.JAPAN);
			try {
				List<Address> addressList = geocoder.getFromLocationName(searchName, 10);
				Iterator<Address> iterator = addressList.iterator();
				while (iterator.hasNext()) {
					Address address = (Address) iterator.next();
					if ("石川県".equals(address.getAdminArea())
							|| (address.getPostalCode() != null && address
									.getPostalCode().startsWith("92"))) {
					} else {
						iterator.remove();
					}
				}
				return addressList;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				if (context instanceof Activity) {
					Runnable action = new Runnable() {
						@Override
						public void run() {
							Toast.makeText(getContext(), R.string.connection_terminated, Toast.LENGTH_LONG).show();
						}
					};
					((Activity) context).runOnUiThread(action);

				}

			}
			return null;
		}
	}

	public static class SearchChoiceDialogFragment extends DialogFragment

	{

		public LoaderCallbacks<List<Address>> loaderSearchLocationCallbacks = new LoaderCallbacks<List<Address>>() {
			@Override
			public Loader<List<Address>> onCreateLoader(int arg0, Bundle bundle) {
				ProgressDialogFragment.showHelperDialog(getFragmentManager());
				String searchName = bundle.getString(IntentValues.SEARCH_NAME);
				MixLocationSearchLoader mixLocationSearchLoader = new MixLocationSearchLoader(getActivity(), searchName);
				// mixLocationSearchLoader.forceLoad();
				return mixLocationSearchLoader;
			}

			@Override
			public void onLoaderReset(Loader<List<Address>> arg0) {
			}

			@Override
			public void onLoadFinished(Loader<List<Address>> arg0, List<Address> addresses) {
				ProgressDialogFragment.dismissHelperDialog(getFragmentManager());
				if (addresses == null) {
					return;
				}
				SearchMixLocationFragment dialogFragment = new SearchMixLocationFragment();

				Bundle bundle = new Bundle();
				bundle.putParcelableArray(SearchMixLocationFragment.ADDRESS_LIST, addresses.toArray(new Address[0]));
				dialogFragment.setArguments(bundle);
				dialogFragment.show(getFragmentManager(), "list");
			}
		};
		private boolean selectListFlag = false;

		@Override
		public Dialog onCreateDialog(Bundle bundle) {

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			final SearchMapSuggestDB searchMapSuggestDB = new SearchMapSuggestDB(getActivity());
			View view = LayoutInflater.from(getActivity()).inflate(R.layout.search_choice_dialog, null, false);
			List<String> allData = searchMapSuggestDB.getAllData();
			final ListView listView =
					(ListView) view.findViewById(R.id.search_choice_dialog_list);
			final SearchSuggestAdapter adapter = new SearchSuggestAdapter(getActivity(),
					R.layout.map_view_search_suggest_list_layout,
					allData);
			listView.setAdapter(adapter);
			builder.setView(view);
			final EditText autoCompleteTextView =
					(EditText) view.findViewById(R.id.search_choice_dialog_text_view);
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int i, long arg3) {
					selectListFlag = true;
					autoCompleteTextView.setText(adapter.getItem(i).toString());
					adapter.setFilter(adapter.getItem(i).toString() + " ");

				}
			});
			autoCompleteTextView.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(View view, int i, KeyEvent keyevent) {
					if (keyevent.getAction() == KeyEvent.ACTION_DOWN
							&& keyevent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
						InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(
								Context.INPUT_METHOD_SERVICE);
						inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

						return true;
					}
					return false;
				}
			});
			autoCompleteTextView.addTextChangedListener(new TextWatcher() {
				@Override
				public void afterTextChanged(Editable editable) {

				}

				@Override
				public void beforeTextChanged(CharSequence charsequence, int i, int j, int k) {

				}

				@Override
				public void onTextChanged(CharSequence charsequence, int i, int j, int k) {
					if (selectListFlag == false) {
						adapter.setFilter(charsequence.toString());
						autoCompleteTextView.invalidate();
					}
					selectListFlag = false;
				}
			});

			android.content.DialogInterface.OnClickListener l = new android.content.DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					searchMapSuggestDB.addKeyWord(autoCompleteTextView.getEditableText().toString());
					// SearchChoiceDialogFragment.this.dismiss();
					Bundle arguments = new Bundle();
					arguments.putString(IntentValues.SEARCH_NAME, autoCompleteTextView.getEditableText().toString());
					Fragment fragment = null;
					if (which == DialogInterface.BUTTON_POSITIVE) {
						fragment = new MixBusStopSearchFragment();
					} else if (which == DialogInterface.BUTTON_NEUTRAL) {
						fragment = new MixAddressSearchFragment();
					}
					FragmentTransaction beginTransaction = getActivity().getSupportFragmentManager().beginTransaction();
					fragment.setArguments(arguments);

					beginTransaction.add(fragment, "loader");
					// beginTransaction.addToBackStack(null);
					beginTransaction.commit();
				}
			};
			builder.setTitle("住所・バス停検索");
			builder.setPositiveButton("バス停を検索", l);
			builder.setNeutralButton("住所・施設を検索", l);

			return builder.create();
		}
	}

	public static class SearchSuggestAdapter extends BaseAdapter {
		private final List<String> data;
		private ArrayList<String> filterd;
		private final LayoutInflater layoutInflater;
		private int mLayout;

		public SearchSuggestAdapter(Context context, int layout, List<String> data) {
			this.mLayout = layout;
			this.layoutInflater = LayoutInflater.from(context);
			this.data = data;
			setFilter("");
		}

		/*
		 * (非 Javadoc)
		 *
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			return filterd.size();
		}

		/*
		 * (非 Javadoc)
		 *
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public Object getItem(int i) {
			return filterd.get(i);
		}

		/*
		 * (非 Javadoc)
		 *
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId(int i) {
			return 0;
		}

		/*
		 * (非 Javadoc)
		 *
		 * @see android.widget.Adapter#getView(int, android.view.View,
		 * android.view.ViewGroup)
		 */
		@Override
		public View getView(int i, View view, ViewGroup viewgroup) {
			if (view == null) {
				view = layoutInflater.inflate(mLayout, viewgroup, false);
			}
			((TextView) view).setText(filterd.get(i));
			return view;
		}

		public void setFilter(String filter) {
			filterd = new ArrayList<String>();
			for (String element : data) {
				if (element.startsWith(filter)) {
					filterd.add(element);
				}
			}
			notifyDataSetChanged();
		}
	}

	private static final String CHOICE_FRAGMENT_TAG = "choice";

	/**
	 * マップのインスタンスです.
	 *
	 * @see MapView
	 */
	// protected BusMapView map;

	protected final int layout;

	/*
	 * (非 Javadoc)
	 *
	 * @see android.app.Activity#onTouchEvent(android.view.MotionEvent)
	 */

	/**
     *
     */
	static final String BUS_STOP_NAME_LIST = "bus_stop_name_list";

	public BusMapFragment map;

	public MapViewBasicActivity(int layout) {
		this.layout = layout;
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int request, int result, Intent intent) {
		if (request == BusMapFragment.REQUEST_INSTALL_SUPPORT_LIBRARY) {
			finish();
		}

		super.onActivityResult(request, result, intent);
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		BusStop selectdata = map.getSelectedItem();

		int id = item.getItemId();
		switch (id) {
		case R.id.departure_stop_menu:
		case R.id.arrive_stop_menu: {
			TransitionManager transitionManager = new TransitionManager(this,
					AdvancedSearchConditionActivity.class, selectdata,
					id == R.id.departure_stop_menu);
			// startActivityForResult(transitionManager, REQUESTCODE);
			startActivity(transitionManager);
			break;
		}
		case R.id.bus_stop_show_time: {
			TransitionManager transitionManager = new TransitionManager(this,
					TimeLineActivity.class, selectdata);

			// startActivityForResult(transitionManager, REQUESTCODE);
			startActivity(transitionManager);
			break;
		}

		case R.id.add_my_bus_stop: {
			Etc.addMyBusStop(this, selectdata);
			break;

		}
		case R.id.add_shortcut: {
			Etc.addBusStopShortCut(this, selectdata);
			break;

		}

		default:
			break;
		}

		return super.onContextItemSelected(item);
	}


	@Override
	public void onCreate(final Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock);
		super.onCreate(savedInstanceState);
		setContentView(layout);
		map = ((BusMapFragment) getSupportFragmentManager().findFragmentByTag("map"));

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		// ARROWS X LTE用ダイアログ
		// if (Build.MODEL.equals("F-05D") && Build.VERSION.SDK_INT <
		// Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
		// SharedPreferences preferences =
		// PreferenceManager.getDefaultSharedPreferences(this);
		// String key = "F-05D@" + Etc.getVersionCode(this);
		// if (!preferences.contains(key)) {
		// ARROWS_X_LTE_DIALOG dialog = new ARROWS_X_LTE_DIALOG();
		// dialog.show(getSupportFragmentManager(), "arrows");
		// preferences.edit().putBoolean(key, true).commit();
		// }
		// }
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {

		// メニューインフレーターを取得
		MenuInflater inflater = getSupportMenuInflater();

		// xmlのリソースファイルを使用してメニューにアイテムを追加
		inflater.inflate(R.menu.map_optionmenu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.item_switch_map:
			map.getMap().setMapType(
					(map.getMap().getMapType() + 1) % 2);
			break;
		case R.id.current_position:
			map.setLastFix();
			break;
		case R.id.menu_search:

			onSearchRequested();
			break;
		case android.R.id.home: {
			ActionBarUPWrapper.doActionUpNavigation(this);
			break;
		}
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		super.onPause();
		map.onPause();

	}

	@Override
	protected void onRestoreInstanceState(final Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// map.setOnMapMove();
	}

	@Override
	protected void onResume() {
		super.onResume();
		map.onResume();
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.app.Activity#onSearchRequested()
	 */
	@Override
	public boolean onSearchRequested() {
		final SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (sp.contains(PreferenceValues.PF_SEND_LOCATION_NAME) == false) {
			ConfirmDialogFragment dialogFragment = new ConfirmDialogFragment();
			dialogFragment.show(getSupportFragmentManager(), "confirm");
		} else {
			MapViewBasicActivity.SearchChoiceDialogFragment fragment = new
					MapViewBasicActivity.SearchChoiceDialogFragment();
			FragmentManager manager = getSupportFragmentManager();
			fragment.show(manager, CHOICE_FRAGMENT_TAG);

		}

		return true;
	}

	// @Override
	// public void onZoom(final boolean zoomIn) {
	// // マップズームコントローラーで呼び出される。
	// if (zoomIn) {
	// map.get.getController().zoomIn();
	// } else {
	// map.getController().zoomOut();
	// }
	//
	// map.setOnMapMove();
	// }

	/**
	 * @param b
	 */
	public void search(boolean searchMode) {
		FragmentManager manager = getSupportFragmentManager();
		MapViewBasicActivity.SearchChoiceDialogFragment fragment = (MapViewBasicActivity.SearchChoiceDialogFragment) manager
				.findFragmentByTag(CHOICE_FRAGMENT_TAG);
		fragment.dismiss();
		// startSearch("", false, null, false);

	}

	@Override
	public void selectBusStop(BusStop busStop) {

		LatLng geoPoint = busStop.getPoint();
		setMapCenter(geoPoint, false);
		map.setOnMapMove();
	}

	/**
	 * マップの中心を設定します。
	 *
	 * @param geo
	 */
	public void setMapCenter(LatLng geoPoint, boolean notAnimation) {
		map.setMapCenter(geoPoint, notAnimation);
	}

	/**
     *
     */
	public void setOnMapMove() {
		// TODO 自動生成されたメソッド・スタブ

	}
}
