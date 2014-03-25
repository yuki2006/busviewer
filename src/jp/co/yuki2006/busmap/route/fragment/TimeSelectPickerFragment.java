/**
 *
 */
package jp.co.yuki2006.busmap.route.fragment;

import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.actionbarsherlock.app.SherlockDialogFragment;

import java.util.Calendar;

import jp.co.yuki2006.busmap.route.store.RouteData;

/**
 * @author yuki
 */
public class TimeSelectPickerFragment extends SherlockDialogFragment {
	/**
     *
     */
	public static final String PICKER_FRAGMENT_TAG = "picker";

	@Override
	public Dialog onCreateDialog(Bundle arg0) {
		final TimeSelectFragment baseFragment = (TimeSelectFragment) getFragmentManager().findFragmentByTag(
				TimeSelectFragment.TIME_SELECT_FRAGMENT_TAG);
		Bundle arguments = getArguments();
		int id = arguments.getInt(TimeSelectFragment.BUNDLE_ID);
		final RouteData data = (RouteData) arguments
				.getSerializable(TimeSelectFragment.BUNDLE_ROUTE_DATA);
		OnDateSetListener datecallBack = new OnDateSetListener() {
			public void onDateSet(final DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {
				data.set(year, monthOfYear, dayOfMonth);
				if (baseFragment.getActivity() != null) {
					baseFragment.setDateTimebox(baseFragment.getView());
				}
				// baseFragment.setDatebox(baseFragment.getView());
			}
		};
		OnTimeSetListener timecallBack = new OnTimeSetListener() {
			public void onTimeSet(final TimePicker view, final int hourOfDay, final int minute) {
				data.set(Calendar.HOUR_OF_DAY, hourOfDay);
				data.set(Calendar.MINUTE, minute);
				if (baseFragment.getActivity() != null) {
					baseFragment.setDateTimebox(baseFragment.getView());
				}
				// baseFragment.setTimebox(baseFragment.getView());
			}

		};
		// if (id == TimeSelectFragment.DATE_DIALOG_ID) {
		// return new DatePickerDialog(getActivity(), datecallBack, data.get(
		// Calendar.YEAR),
		// data.get(Calendar.MONTH), data.get(Calendar.DAY_OF_MONTH));
		// } else if (id == TimeSelectFragment.TIME_DIALOG_ID) {
		//
		// return new TimePickerDialog(getActivity(), timecallBack, data.get(
		// Calendar.HOUR_OF_DAY),
		// data.get(Calendar.MINUTE), true);
		// }
		return new DateDayPicker(getActivity(), datecallBack, timecallBack, data.get(Calendar.YEAR),
				data.get(Calendar.MONTH), data.get(Calendar.DAY_OF_MONTH),
				data.get(Calendar.HOUR_OF_DAY), data.get(Calendar.MINUTE));
	}

}