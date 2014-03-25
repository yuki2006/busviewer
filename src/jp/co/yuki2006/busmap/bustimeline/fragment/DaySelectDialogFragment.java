/**
 *
 */
package jp.co.yuki2006.busmap.bustimeline.fragment;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import jp.co.yuki2006.busmap.R;
import jp.co.yuki2006.busmap.bustimeline.BusTimeTableTimerBasisActivity.DateType;
import jp.co.yuki2006.busmap.bustimeline.TimeLineActivity;
import jp.co.yuki2006.busmap.etc.Etc;

/**
 * @author yuki
 */
public class DaySelectDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        CharSequence[] items = {
                getString(R.string.day_today_timetable),
                getString(R.string.day_weekday_timetable),
                getString(R.string.day_saturday_timetable),
                getString(R.string.day_holyday_timetable),
                "日付の指定"
        };
        final TimeLineActivity timelineActivity = (TimeLineActivity) getActivity();
        Builder builder = new Builder(timelineActivity);
        builder.setTitle("表示する曜日を選択");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        timelineActivity.showBusStopData(DateType.TODAY);
                        break;
                    case 1:
                        timelineActivity.showBusStopData(DateType.WEEKDAY);
                        break;
                    case 2:
                        timelineActivity.showBusStopData(DateType.SATURDAY);
                        break;
                    case 3:
                        timelineActivity.showBusStopData(DateType.HOLIDAY);
                        break;
                    case 4: {
                        timelineActivity.showDateSelectDialog();

                    }
                    default:
                        break;
                }

            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        AlertDialog dialog = builder.create();
        Etc.setNoSearchKeyCancel(dialog);
        return dialog;
    }
}
