/**
 *
 */
package jp.co.yuki2006.busmap.route.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.actionbarsherlock.app.SherlockFragment;

import java.util.Calendar;

import jp.co.yuki2006.busmap.R;
import jp.co.yuki2006.busmap.route.IAdvancedSearch;
import jp.co.yuki2006.busmap.route.fragment.AdvancedSearchFormFragment.TransferCofirmDialog;
import jp.co.yuki2006.busmap.route.store.RouteData;

/**
 * @author yuki
 */
public class TimeSelectFragment extends SherlockFragment implements OnClickListener {
	static final int TIME_DIALOG_ID = 0;
	static final int DATE_DIALOG_ID = 1;

	public static final String TIME_SELECT_FRAGMENT_TAG = "TIME_SELECT_FRAGMENT_TAG";
	static final String BUNDLE_ROUTE_DATA = "BUNDLE_ROUTE_DATA";

	/**
     *
     */
	static final String BUNDLE_ID = "BUNDLE_ID";

	private String getTimePaddingZero(int time) {
		String tmp = String.valueOf(time);
		if (tmp.length() == 1) {
			tmp = "0" + tmp;
		}
		return tmp;
	}

	public void invalidate(View mView) {
		// setDatebox(mView);
		// setTimebox(mView);
		setDateTimebox(mView);
		setConditionList(mView);
	}

	public void onClick(View v) {
		RouteData data = getRouteData();
		switch (v.getId()) {

		// case R.id.date_button: {
		// showFragment(getFragmentManager(), data, DATE_DIALOG_ID);
		//
		// break;
		// }
		// case R.id.time_button: {
		// showFragment(getFragmentManager(), data, TIME_DIALOG_ID);
		//
		// break;
		// }
		case R.id.date_time_button: {
			showFragment(getFragmentManager(), data, TIME_DIALOG_ID);
			break;
		}
		case R.id.advanced_search_button:
			((IAdvancedSearch) getActivity()).searchBus();
			break;

		default:
			break;
		}
	}
	/*
	 * (非 Javadoc)
	 *
	 * @see
	 * com.actionbarsherlock.app.SherlockFragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onResume() {
		super.onResume();
		CheckBox checkBox = (CheckBox) getView().findViewById(R.id.advanced_enable_transfer);
		checkBox.setChecked(getRouteData().isEnableTransfer());
		changeCheckBox(checkBox.isChecked());
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				getRouteData().setEnableTransfer(isChecked);
				if (isChecked) {
					FragmentTransaction beginTransaction = getFragmentManager().beginTransaction();
					beginTransaction.add(new TransferCofirmDialog(), "confirm");
					beginTransaction.commit();
				}
				changeCheckBox(isChecked);
			}
		});
		Spinner transferTimeSpinner = (Spinner) getView().findViewById(R.id.transfer_time_spinner);
		transferTimeSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
						getRouteData().setTransferTime(Integer.parseInt((String) parent.getItemAtPosition(position)));
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
					}
				});

		SpinnerAdapter adapter = transferTimeSpinner.getAdapter();
		for (int i = 0; i < adapter.getCount(); i++) {
			if (adapter.getItem(i).equals("" + getRouteData().getTransferTime())) {
				transferTimeSpinner.setSelection(i);
				break;
			}
		}
	}

	private void changeCheckBox(boolean isChecked) {
		((View) getView().findViewById(R.id.transfer_time_spinner).getParent()).setVisibility(isChecked ? View.VISIBLE
				: View.GONE);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle bundle) {

		final View mView = inflater.inflate(R.layout.advanced_search_time_fragment, group, false);




		// mView.findViewById(R.id.date_button).setOnClickListener(this);
		// mView.findViewById(R.id.time_button).setOnClickListener(this);
		mView.findViewById(R.id.date_time_button).setOnClickListener(this);
		mView.findViewById(R.id.advanced_search_button).setOnClickListener(this);

		OnItemSelectedListener listener = new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// if (position >= 2) {
				// mView.findViewById(R.id.time_button).setEnabled(false);
				// } else {
				// mView.findViewById(R.id.time_button).setEnabled(true);
				// }
				RouteData data = getRouteData();
				data.setFromDeparture(position % 2 == 0);
				data.setIsFirstOrLast(position / 2 == 1);
				setDateTimebox(mView);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		};
		// 別のフラグメント内での準備ができてない時
		if (getRouteData() != null) {
			invalidate(mView);
		}
		((Spinner) mView.findViewById(R.id.select_condition)).setOnItemSelectedListener(listener);
		return mView;
	}

	/**
	 * @param mView
	 */
	private void setConditionList(View mView) {
		Spinner spinner = (Spinner) mView.findViewById(R.id.select_condition);
		RouteData data = getRouteData();
		if (data.isFirstOrLast() == false) {
			if (data.isFromDeparture()) {
				spinner.setSelection(0);
			} else {
				spinner.setSelection(1);
			}
		} else {
			if (data.isFromDeparture()) {
				spinner.setSelection(2);
			} else {
				spinner.setSelection(3);
			}
		}

	}

	void setDateTimebox(View mView) {
		StringBuilder dateString = new StringBuilder();
		RouteData data = getRouteData();


		dateString.append(data.get(Calendar.DAY_OF_MONTH)).append("日 ");

		Spinner spinner = (Spinner) mView.findViewById(R.id.select_condition);
		if (spinner.getSelectedItemPosition() < 2) {
			dateString.append(getTimePaddingZero(data.get(Calendar.HOUR_OF_DAY))).append(":")
					.append(getTimePaddingZero(data.get(Calendar.MINUTE)));
		}
		// 画面回転でダイアログが消され、タイミングでも呼ばれるので
		// その時viewが消えてる。
		((Button) mView.
				findViewById(R.id.date_time_button)).setText(dateString);
	}

	// void setDatebox(View mView) {
	// StringBuilder datestring = new StringBuilder();
	// RouteData data = getRouteData();
	// datestring.append(data.get(Calendar.DAY_OF_MONTH)).append("日");
	// // 画面回転でダイアログが消され、タイミングでも呼ばれるので
	// // その時viewが消えてる。
	// ((Button) mView.
	// findViewById(R.id.date_button)).setText(datestring);
	// }
	//
	// void setTimebox(View mView) {
	// StringBuilder timestring = new StringBuilder();
	// RouteData data = getRouteData();
	// timestring.append(getTimePaddingZero(data.get(Calendar.HOUR_OF_DAY))).append(":")
	// .append(getTimePaddingZero(data.get(Calendar.MINUTE)));
	//
	// ((Button) mView.findViewById(R.id.time_button)).setText(timestring);
	// }

	void showFragment(FragmentManager fragmentManager, RouteData data, int id) {
		TimeSelectPickerFragment fragment = new TimeSelectPickerFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(BUNDLE_ID, id);
		bundle.putSerializable(BUNDLE_ROUTE_DATA, data);
		fragment.setArguments(bundle);
		fragment.show(fragmentManager, TimeSelectPickerFragment.PICKER_FRAGMENT_TAG);
	}

	private RouteData getRouteData() {
		return ((IAdvancedSearch) getActivity()).getRouteData();
	}
}
