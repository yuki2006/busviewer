package jp.co.yuki2006.busmap.route;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import jp.co.yuki2006.busmap.R;
import jp.co.yuki2006.busmap.route.store.BusSearch;

public class BusSearchList extends ArrayAdapter<BusSearch> {
    private static enum STATE {
        LOADED, ERROR, NO_BUS
    }

    private Integer diffTime;

    private final LayoutInflater inflater;
    private Long nextBusTime;
    private STATE state = STATE.ERROR;

    public BusSearchList(Context context, ArrayList<BusSearch> busSearchs) {
        super(context, 0, busSearchs);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setNextTime();
    }

    /**
     * @return nextBusTime
     */
    public Long getNextBusTime() {
        return nextBusTime;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.advanced_search_result_list, parent, false);
        }
        BusSearch item = getItem(position);

        item.inflateLayout(getContext(), convertView);

        return convertView;
    }

    public boolean isShouldLoadNextData() {
        // 本日のバスがもう無いときは読み込まない
        if (state == STATE.NO_BUS) {
            return false;
        }
        if (diffTime >= 0) {
            return false;
        } else {
            // mode = STATE.NOW_LOADING;
            // nextBusTime = null;
        }
        return true;
    }

    public void refleshMode() {
        if (getCount() == 0) {
            state = STATE.NO_BUS;
            // nextBusTime = -1L;
            return;
        }
        state = STATE.LOADED;
    }

    /*
     * (非 Javadoc)
     *
     * @see jp.co.yuki2006.busmap.model.CountDownable#downCount(int)
     */
    public void refleshNextTime() {

        if (getNextBusTime() != null) {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Japan"));
            long time = getNextBusTime() - calendar.getTimeInMillis();
            diffTime = (int) (time / (1000 * 60));
            Log.d("Bus", "diffTime=" + diffTime);
        }

    }

    public boolean isNextHasRemarks() {
        switch (state) {
            case NO_BUS:
                return false;
            case LOADED:
                return getItem(0).getRemarkForString().length() > 0;
            case ERROR:
                return false;
        }
        return false;
    }

    public void setLoadingError() {
        state = STATE.ERROR;
    }

    /**
     */
    private void setNextTime() {
        if (getCount() > 0) {
            String[] timeSplit = getItem(0).getNextTime().split(":");
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Japan"));
            calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(timeSplit[0]));
            calendar.set(Calendar.MINUTE, Integer.valueOf(timeSplit[1]));
            calendar.set(Calendar.SECOND, 59);
            nextBusTime = calendar.getTimeInMillis();
            refleshNextTime();
        }
    }

    public String toNextTimeString() {
        switch (state) {
            case NO_BUS:
                return ("今からのバスがありません。");
            case LOADED:
                return "次のバスは" + String.valueOf(diffTime) + "分後です。"
                        + (getItem(0).getRemarkForString().length() > 0 ? "(※)" : "");
            case ERROR:
                return "読み込みに失敗しました";
        }
        return "";

    }

    /**
     * @return
     */
    public CharSequence toShortNextTimeString() {
        switch (state) {
            case NO_BUS:
                return ("バスがありません。");
            case LOADED:
                return String.valueOf(diffTime) + "分後です。"
                        + (getItem(0).getRemarkForString().length() > 0 ? "(※)" : "");
            case ERROR:
                return "読み込みエラー";
        }
        return "";
    }

    /**
     * @return
     */
    public CharSequence toOnlyTimeString() {
        switch (state) {
            case NO_BUS:
                return "";
            case LOADED:
                return String.valueOf(diffTime);
            case ERROR:
                return "err";
        }
        return "";
    }
}