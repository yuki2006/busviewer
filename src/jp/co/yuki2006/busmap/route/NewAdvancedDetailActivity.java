/**
 *
 */
package jp.co.yuki2006.busmap.route;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

import jp.co.yuki2006.busmap.R;
import jp.co.yuki2006.busmap.etc.ActionBarUPWrapper;
import jp.co.yuki2006.busmap.route.store.BusTransferSearch;
import jp.co.yuki2006.busmap.route.store.NewBusSearch;
import jp.co.yuki2006.busmap.store.BusStop;
import jp.co.yuki2006.busmap.values.IntentValues;

/**
 * @author yuki
 */
public class NewAdvancedDetailActivity extends SherlockFragmentActivity {
	/*
	 * (非 Javadoc)
	 *
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock);
		super.onCreate(savedInstanceState);

		ActionBar actionBar = this.getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		LinearLayout linearLayout = new LinearLayout(this);
		linearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		setContentView(linearLayout);

		Intent intent = getIntent();
		BusTransferSearch data = (BusTransferSearch) intent
				.getSerializableExtra(IntentValues.TRANSITION_ADVANCED_NEW_SEARCH);
		for (int i = 0; i < data.size(); i++) {
			NewBusSearch newBusSearch = data.get(i);
			View view = layoutInflater.inflate(R.layout.advanced_new_detail_element,
					(ViewGroup) findViewById(android.R.id.content), false);
			linearLayout.addView(view);
			for (int layout : new int[] { R.id.advanced_new_detail_departure, R.id.advanced_new_detail_arrival }) {
				boolean isDeparture = layout == R.id.advanced_new_detail_departure;
				View detailElement = view.findViewById(layout);
				((TextView) detailElement.findViewById(R.id.advanced_search_label)).
						setText(isDeparture ? R.string.departure : R.string.arrive);

				((TextView) detailElement.findViewById(R.id.advanced_new_time)).
						setText(newBusSearch.getTime(isDeparture));

				BusStop busStop = newBusSearch.getBusStop(isDeparture);
				((TextView) detailElement.findViewById(R.id.advanced_busstop_label)).
						setText(busStop.toString());
			}
			((TextView) view.findViewById(R.id.advanced_new_detail_bus_data)).
					setText(
					newBusSearch.getLineNumber() + " " +
							newBusSearch.getLastBusStopName() + "行き");
			((TextView) view.findViewById(R.id.advanced_new_detail_bus_remark)).setText(newBusSearch
					.getRemarkForString());

		}

	}
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                ActionBarUPWrapper.doActionUpNavigation(this);
                break;
            }
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
