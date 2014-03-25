/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.co.yuki2006.busmap.route.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TimePicker;

import java.util.Calendar;

import jp.co.yuki2006.busmap.R;

/**
 * A simple dialog containing an {@link android.widget.DatePicker}.
 * <p/>
 * <p>
 * See the <a href="{@docRoot}guide/topics/ui/controls/pickers.html">Pickers</a>
 * guide.
 * </p>
 */
public class DateDayPicker extends AlertDialog implements OnClickListener,
		OnDateChangedListener {

	private static final String YEAR = "year";
	private static final String MONTH = "month";
	private static final String DAY = "day";
	private static final String HOUR = "hour";
	private static final String MINUTE = "minute";

	private final DatePicker mDatePicker;
	private final TimePicker mTimePicker;
	private final OnDateSetListener mOnDateSetCallBack;
	private final OnTimeSetListener mOnTimeSetListener;
	private final Calendar mCalendar;

	/**
	 * @param context
	 *            The context the dialog is to run in.
	 * @param callBack
	 *            How the parent is notified that the date is set.
	 * @param year
	 *            The initial year of the dialog.
	 * @param monthOfYear
	 *            The initial month of the dialog.
	 * @param dayOfMonth
	 *            The initial day of the dialog.
	 */
	public DateDayPicker(Activity activity,
			OnDateSetListener onDateSetListener, OnTimeSetListener onTimeSetCallBack,
			int year,
			int monthOfYear,
			int dayOfMonth, int hour, int minute) {
		this(activity, 0, onDateSetListener, onTimeSetCallBack, year, monthOfYear, dayOfMonth, hour, minute);
	}

	/**
	 * @param context
	 *            The context the dialog is to run in.
	 * @param theme
	 *            the theme to apply to this dialog
	 * @param callBack
	 *            How the parent is notified that the date is set.
	 * @param year
	 *            The initial year of the dialog.
	 * @param monthOfYear
	 *            The initial month of the dialog.
	 * @param dayOfMonth
	 *            The initial day of the dialog.
	 */
	public DateDayPicker(Activity activity,
                         int theme,
                         OnDateSetListener onDateSetCallBack, OnTimeSetListener onTimeSetCallBack,
                         int year,
                         int monthOfYear,
                         int dayOfMonth, int currentHour, int currentMinute) {
        super(activity, theme);

        mOnDateSetCallBack = onDateSetCallBack;
        mOnTimeSetListener = onTimeSetCallBack;

        mCalendar = Calendar.getInstance();

        Context themeContext = getContext();
        // setButton(BUTTON_POSITIVE,
        // themeContext.getText(R.string.date_time_done), this);
        setButton(BUTTON_POSITIVE, "完了", this);
        setIcon(0);

        LayoutInflater inflater =
                (LayoutInflater) themeContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.day_time_picker_layout, null);
        setView(view);

		DisplayMetrics metrics = new DisplayMetrics();
		((Activity) activity).getWindowManager().getDefaultDisplay().getMetrics(metrics);

		boolean isCompactLayout = (metrics.widthPixels / metrics.density >= 480 && metrics.heightPixels
				/ metrics.density < 500);


        mDatePicker = (DatePicker) view.findViewById(R.id.datePicker);
        mDatePicker.init(year, monthOfYear, dayOfMonth, this);
        mTimePicker = (TimePicker) view.findViewById(R.id.timePicker);
        mTimePicker.setIs24HourView(true);
        mTimePicker.setCurrentHour(currentHour);
        mTimePicker.setCurrentMinute(currentMinute);


        ((LinearLayout)mDatePicker.getParent()).setOrientation(isCompactLayout?LinearLayout.HORIZONTAL:LinearLayout.VERTICAL);

        view.findViewById(R.id.currentTime).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                mDatePicker.init(year, month, day, DateDayPicker.this);
                mTimePicker.setCurrentHour(hour);
                mTimePicker.setCurrentMinute(minute);
                updateTitle(year, month, day, hour, minute);
            }
        });

        updateTitle(year, monthOfYear, dayOfMonth, currentHour, currentMinute);
    }

	public void onClick(DialogInterface dialog, int which) {
		tryNotifyDateSet();
	}

	public void onDateChanged(DatePicker view, int year,
			int month, int day) {
		mDatePicker.init(year, month, day, this);
		updateTitle(year, month, day, mTimePicker.getCurrentHour(), mTimePicker.getCurrentMinute());
	}

	/**
	 * Gets the {@link DatePicker} contained in this dialog.
	 *
	 * @return The calendar view.
	 */
	public DatePicker getDatePicker() {
		return mDatePicker;
	}

	/**
	 * Sets the current date.
	 *
	 * @param year
	 *            The date year.
	 * @param monthOfYear
	 *            The date month.
	 * @param dayOfMonth
	 *            The date day of month.
	 */
	public void updateDate(int year, int monthOfYear, int dayOfMonth) {
		mDatePicker.updateDate(year, monthOfYear, dayOfMonth);
	}

	public void updateTime(int currentHour, int currentMinute) {
		mTimePicker.setCurrentHour(currentHour);
		mTimePicker.setCurrentMinute(currentMinute);
	}

	private void tryNotifyDateSet() {
		if (mOnDateSetCallBack != null) {
			mDatePicker.clearFocus();
			mOnDateSetCallBack.onDateSet(mDatePicker, mDatePicker.getYear(),
					mDatePicker.getMonth(), mDatePicker.getDayOfMonth());

			mOnTimeSetListener.onTimeSet(mTimePicker, mTimePicker.getCurrentHour(), mTimePicker.getCurrentMinute());
		}
	}

	@Override
	protected void onStop() {
		tryNotifyDateSet();
		super.onStop();
	}

	private void updateTitle(int year, int month, int day, int currentHour, int currentMinute) {
		mCalendar.set(Calendar.YEAR, year);
		mCalendar.set(Calendar.MONTH, month);
		mCalendar.set(Calendar.DAY_OF_MONTH, day);
		String title = DateUtils.formatDateTime(getContext(),
				mCalendar.getTimeInMillis(),
				DateUtils.FORMAT_SHOW_DATE
						| DateUtils.FORMAT_SHOW_WEEKDAY
						| DateUtils.FORMAT_SHOW_YEAR
						| DateUtils.FORMAT_ABBREV_MONTH
						| DateUtils.FORMAT_ABBREV_WEEKDAY | DateUtils.FORMAT_ABBREV_TIME);
		setTitle(title);
	}

	@Override
	public Bundle onSaveInstanceState() {
		Bundle state = super.onSaveInstanceState();
		state.putInt(YEAR, mDatePicker.getYear());
		state.putInt(MONTH, mDatePicker.getMonth());
		state.putInt(DAY, mDatePicker.getDayOfMonth());
		state.putInt(HOUR, mTimePicker.getCurrentHour());
		state.putInt(MINUTE, mTimePicker.getCurrentMinute());

		return state;
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		int year = savedInstanceState.getInt(YEAR);
		int month = savedInstanceState.getInt(MONTH);
		int day = savedInstanceState.getInt(DAY);

		int hour = savedInstanceState.getInt(HOUR);
		int minute = savedInstanceState.getInt(MINUTE);
		mDatePicker.init(year, month, day, this);
		mTimePicker.setCurrentHour(hour);
		mTimePicker.setCurrentMinute(minute);

	}
}
