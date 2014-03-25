/**
 *
 */
package jp.co.yuki2006.busmap.bustimeline.fragment;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

import jp.co.yuki2006.busmap.bustimeline.TimeLineActivity;
import jp.co.yuki2006.busmap.bustimeline.TimeLineActivity.CheckDate;

/**
 * @author yuki
 */
public class DateSelectDialogFragment extends DialogFragment {
    private OnDateSetListener callBack = new OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, monthOfYear, dayOfMonth);
            ((TimeLineActivity) getActivity()).showBusStopData(calendar);
        }
    };

    /*
     * (非 Javadoc)
     *
     * @see
     * android.support.v4.app.DialogFragment#onCreateDialog(android.os.Bundle)
     */
    @Override
    public Dialog onCreateDialog(Bundle arg0) {
        Bundle arguments = getArguments();
        CheckDate date = (CheckDate) arguments.getSerializable(TimeLineActivity.CURRENT_DATE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, date.month - 1);
        calendar.set(Calendar.DATE, date.date);

        return new DatePickerDialog(getActivity(), callBack, calendar.get(
                Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }
}
