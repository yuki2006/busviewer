package jp.co.yuki2006.busmap.etc;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import java.util.List;

import jp.co.yuki2006.busmap.MyBusStopActivity;
import jp.co.yuki2006.busmap.R;
import jp.co.yuki2006.busmap.SearchBusActivity;
import jp.co.yuki2006.busmap.bustimeline.TimeLineActivity;
import jp.co.yuki2006.busmap.etc.Etc.onSelectVoiceResult;
import jp.co.yuki2006.busmap.map.BusMapActivity;
import jp.co.yuki2006.busmap.map.MapSuggestionProvider;
import jp.co.yuki2006.busmap.route.AdvancedSearchConditionActivity;
import jp.co.yuki2006.busmap.store.BusStop;
import jp.co.yuki2006.busmap.values.IntentValues;
import jp.co.yuki2006.busmap.values.ProviderValues;

public class BusListActivity extends MyListMenuActivity implements OnItemClickListener {

	protected BusStopAdapter arrayAdapter;
	protected int REQUESTCODE = 1;

	private boolean isSelectableMenu = false;

	protected void menuProcess(int id, BusStop selectData) {
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == this.REQUESTCODE && resultCode == RESULT_OK) {
			if (data != null) {
				setResult(RESULT_OK, new Intent(data));
				finish();
			}
		} else if (requestCode == Etc.REQUEST_VOICE_SEARCH_CODE) {
			if (resultCode == RESULT_OK) {
				onSelectVoiceResult selectVoiceResult = new onSelectVoiceResult() {
					@Override
					public void onSelect(String result) {
						searchBusStop(result);
					}
				};
				Etc.resultVoice(this, data, selectVoiceResult);
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bus_layout);

		final ListView list = (ListView) this.findViewById(R.id.buslist);

		arrayAdapter = new BusStopAdapter(this,
				(ViewGroup) findViewById(R.id.list_swipe_layout));

		list.setAdapter(arrayAdapter);

		registerForContextMenu(list);

		getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		list.setOnItemClickListener(this);
		list.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				return false;
			}
		});

		((View) findViewById(R.id.suggest_text_view).getParent()).setVisibility(View.GONE);

		// findViewById(R.id.list_edit_mode_button).setVisibility(View.GONE);
		ActionBar supportActionBar = getSupportActionBar();
		supportActionBar.setDisplayHomeAsUpEnabled(true);
		if (!(this instanceof MyBusStopActivity)) {
			supportActionBar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, final View v, ContextMenuInfo menuInfo) {

		if (!(v instanceof ListView)) {
			return;
		}
		final AdapterContextMenuInfo contextmenu = (AdapterContextMenuInfo) menuInfo;

		final BusStop selectData = BusListActivity.this.arrayAdapter.getItem(contextmenu.position);

		menu.setHeaderTitle(selectData.toString());
		android.view.MenuInflater infect = getMenuInflater();
		infect.inflate(R.menu.buslist_contextmenu, menu);

		// BusDataMenuUtil.inflateMenu(menu, isSelectableMenu);
		super.onCreateContextMenu(menu, v, menuInfo);
		// 主にマイバス停だけかな。
		if (selectData.getPoint() != null && selectData.getPoint().latitude != 0) {
			if (menu.findItem(R.id.move_busstop) != null) {
				menu.findItem(R.id.move_busstop).setVisible(true);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// メニューインフレーターを取得
		MenuInflater inflater = getSupportMenuInflater();

		// xmlのリソースファイルを使用してメニューにアイテムを追加
		inflater.inflate(R.menu.bus_list_option_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see jp.co.yuki2006.busmap.etc.MyListMenuActivity#onMenuClick(int, int)
	 */
	@Override
	public void onMenuClick(int id, int selectedListPosition) {
		final BusStop selectData = arrayAdapter.getItem(selectedListPosition);
		switch (id) {
		case R.id.departure_stop_menu:
		case R.id.arrive_stop_menu: {
			TransitionManager transitionManager =
					new TransitionManager(this, AdvancedSearchConditionActivity.class, selectData,
							id == R.id.departure_stop_menu);
			BusListActivity.this.startActivityForResult(transitionManager, REQUESTCODE);
			break;
		}
		case R.id.bus_stop_show_time: {
			TransitionManager transitionManager = new TransitionManager(BusListActivity.this, TimeLineActivity.class,
					selectData);

			BusListActivity.this.startActivityForResult(transitionManager, REQUESTCODE);
			break;
		}
		// case R.id.list_up_to:
		// case R.id.list_down_to: {
		// MyBusStopActivity instance = (MyBusStopActivity)
		// BusListActivity.this;
		// instance.moveListPostion(selectedListPosition, id ==
		// R.id.list_up_to);
		// break;
		//
		// }
		case R.id.move_busstop: {

			TransitionManager intent = new TransitionManager(this, BusMapActivity.class,
					selectData);
			startActivity(intent);
			/*
			 * ArrayList<Integer> value = new ArrayList<Integer>();
			 * GeoPoint point = selectdata.getPoint();
			 * value.add(point.getLatitudeE6());
			 * value.add(point.getLongitudeE6());
			 * intent.putIntegerArrayListExtra("GEOPOINT", value);
			 * intent.putExtra("BUSSTOPNAME", selectdata.getTitle());
			 * setResult(RESULT_OK, intent);
			 * finish();
			 */
			break;

		}
		case R.id.add_my_bus_stop: {
			Etc.addMyBusStop(this, selectData);
			break;

		}
		case R.id.add_shortcut: {
			Etc.addBusStopShortCut(this, selectData);
			break;

		}

		default:
			break;
		}
	}

	@Override
	public void onNewIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			SearchRecentSuggestions suggestions =
					new SearchRecentSuggestions(this, ProviderValues.BUS_STOP_SUGGESTION_PROVIDER,
							MapSuggestionProvider.DATABASE_MODE_QUERIES);
			suggestions.saveRecentQuery(query, null);
			if (this instanceof SearchBusActivity) {
				setTitle(query);
			}
			searchBusStop(query);

		}
	}

	/*
	 * public void afterTextChanged(final Editable editable) {
	 * 
	 * final ListView list = (ListView) this.findViewById(R.id.buslist);
	 * String text = editable.toString();
	 * if (text.length() > 0) {
	 * list.setAdapter(arrayAdapter.getFilterData(text));
	 * } else {
	 * list.setAdapter(arrayAdapter);
	 * }
	 * 
	 * }
	 * 
	 * public void beforeTextChanged(CharSequence charsequence, int i, int j,
	 * int k) {
	 * 
	 * }
	 * 
	 * public void onTextChanged(CharSequence s, int start, int before, int
	 * count) {
	 * }
	 */

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.search_button:
			startSearch(null, false, null, false);
			break;
		case android.R.id.home:
			ActionBarUPWrapper.doActionUpNavigation(this);
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	private void searchBusStop(String busStopName) {
		if (busStopName.length() == 0) {
			return;
		}
		Intent intent = new Intent(this, SearchBusActivity.class);
		intent.putExtra(IntentValues.TRANSITION_BUS_STOP_NAME, busStopName);
		intent.setAction(Intent.ACTION_VIEW);
		startActivity(intent);

	}

	protected void setBusList(List<BusStop> listdata) {
		arrayAdapter.clear();
		for (BusStop data : listdata) {
			arrayAdapter.add(data);
		}
		final ListView list = (ListView) this.findViewById(R.id.buslist);
		list.setAdapter(arrayAdapter);

	}

	public void setEnableSelectForMenu(boolean enable) {
		this.isSelectableMenu = enable;

	}
}
