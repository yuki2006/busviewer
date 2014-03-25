package jp.co.yuki2006.busmap.pf;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import jp.co.yuki2006.busmap.R;

public class PreferenceSlider extends DialogPreference implements OnSeekBarChangeListener {
    private String dialogsummary;
    private int defaultvalue;
    private int maxvalue;
    private SharedPreferences sp;
    private SeekBar bar;
    private TextView textview;

    public PreferenceSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        dialogsummary = attrs.getAttributeValue(null, "dialogsummary");
        defaultvalue = attrs.getAttributeIntValue("http://schemas.android.com/apk/res/android", "defaultValue", 0);
        maxvalue = attrs.getAttributeIntValue("http://schemas.android.com/apk/res/android", "max", 0);
        // ダイアログのレイアウトリソース指定
        setDialogLayoutResource(R.layout.preference_slider);
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);

        int initvalue = sp.getInt(getKey(), defaultvalue);
        bar = (SeekBar) v.findViewById(R.id.seekBar1);
        bar.setMax(maxvalue);
        bar.setProgress(initvalue);

        bar.setOnSeekBarChangeListener(this);


        textview = (TextView) v.findViewById(R.id.seekvalue);
        textview.setText(String.valueOf(initvalue));

        TextView summaryview = (TextView) v.findViewById(R.id.summary);

        summaryview.setText(dialogsummary);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            persistInt(bar.getProgress());
        }
        super.onDialogClosed(positiveResult);
    }

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        textview.setText(String.valueOf(progress));

    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO 自動生成されたメソッド・スタブ

    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO 自動生成されたメソッド・スタブ

    }

}
