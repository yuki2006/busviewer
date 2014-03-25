/**
 *
 */
package jp.co.yuki2006.busmap.route.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import java.util.ArrayList;

import jp.co.yuki2006.busmap.R;
import jp.co.yuki2006.busmap.ad.AdActivity;
import jp.co.yuki2006.busmap.db.AdvancedSearchHistoryDB;
import jp.co.yuki2006.busmap.db.MyRouteDB;
import jp.co.yuki2006.busmap.route.AdvancedSearchConditionActivity;
import jp.co.yuki2006.busmap.route.AdvancedSearchResultActivity;
import jp.co.yuki2006.busmap.route.MyRouteActivity;
import jp.co.yuki2006.busmap.route.NewAdvancedSearchResultActivity;
import jp.co.yuki2006.busmap.route.store.RouteData;
import jp.co.yuki2006.busmap.values.IntentValues;

/**
 * @author yuki
 */
public class AdvancedHistoryFragment extends SherlockFragment {
	public static class AdvancedSearchHistoryAdapter extends
			ArrayAdapter<RouteData> {

		private final LayoutInflater layoutInflater;

		/**
		 * @param context
		 * @param historyRootData
		 */
		public AdvancedSearchHistoryAdapter(Context context, ArrayList<RouteData> historyRootData) {
			super(context, 0, historyRootData);
			layoutInflater = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);

		}

		/*
		 * (非 Javadoc)
		 *
		 * @see jp.co.yuki2006.busmap.list.InteractiveListAdapter#getView(int,
		 * android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.advanced_search_history_list, parent, false);
			}
			ImageButton addMyRouteButton = (ImageButton) convertView.findViewById(R.id.menu_add_myroot);
			addMyRouteButton.setFocusable(false);
			addMyRouteButton.bringToFront();
			MyRouteDB db = new MyRouteDB(getContext());
			final RouteData item = getItem(position);
			addMyRouteButton.setImageResource(
					db.isDataExist(item) ? android.R.drawable.star_big_on :
							android.R.drawable.star_big_off
					);
			addMyRouteButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					MyRouteDB myRouteDB = new MyRouteDB(getContext());
					if (myRouteDB.insertOrDeleteData(item)) {
						Toast.makeText(getContext(), "マイルートに登録しました。", Toast.LENGTH_LONG).show();
					}
					myRouteDB.close();
					notifyDataSetChanged();
				}
			});

			// リスト編集モードなら　消す
			// まだ読み込んでなければ非表示
			// それ以外は表示
			MyRouteActivity.showRootBlock(getContext(), convertView, item);

			// nextTimeTextView.setVisibility(this.isEditMode() ? View.GONE :
			// item.nextMinutes == null ? View.INVISIBLE
			// : View.VISIBLE);
			String textString =
					item.isEnableTransfer() ? "乗り換え" : "";
			((TextView) convertView.findViewById(R.id.is_transfer_text)).setText(textString);
			return convertView;
		}

	}

	private OnItemClickListener listener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int index, long j) {
			RouteData data = (RouteData) adapterView.getAdapter().getItem(index);
			Class<?> cls =
					data.isEnableTransfer() ?
							NewAdvancedSearchResultActivity.class :
							AdvancedSearchResultActivity.class;

			Intent intent = new Intent(getActivity(), cls);
			intent.putExtra(IntentValues.TRANSITION_ADVANCED_SEARCH, data);
			intent.putExtra(IntentValues.FROM_MY_ROOT, true);
			startActivityForResult(intent, AdvancedSearchConditionActivity.REQUEST_RESULT_ACTIVITY);
		}
	};

	/*
	 * (非 Javadoc)
	 *
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {
		View view = inflater.inflate(R.layout.advanced_search_history_fragment, viewGroup, false);
		AdActivity.insertMob(getActivity(), view);
		ListView historyList = (ListView) view.findViewById(R.id.advanced_search_history_list);
		historyList.setOnItemClickListener(listener);
		return view;
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		AdvancedSearchHistoryDB db = new AdvancedSearchHistoryDB(getActivity());
		ArrayList<RouteData> historyRootData = db.getHistoryRootData();
		ListView histryList = (ListView) getView().findViewById(R.id.advanced_search_history_list);
		AdvancedSearchHistoryAdapter adapter = new AdvancedSearchHistoryAdapter(getActivity(), historyRootData);
		histryList.setAdapter(adapter);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see com.actionbarsherlock.app.SherlockFragment#onCreateOptionsMenu(com.
	 * actionbarsherlock.view.Menu, com.actionbarsherlock.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.advanced_search_history_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
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
	 * @see
	 * com.actionbarsherlock.app.SherlockFragment#onOptionsItemSelected(com.
	 * actionbarsherlock.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.advanced_search_history_clear) {
			AdvancedSearchHistoryDB db = new AdvancedSearchHistoryDB(getActivity());
			db.truncate();
			ListAdapter adapter = ((ListView) getView().findViewById(R.id.advanced_search_history_list)).
					getAdapter();
			((AdvancedSearchHistoryAdapter) adapter).clear();

		}
		return super.onOptionsItemSelected(item);
	}
}
