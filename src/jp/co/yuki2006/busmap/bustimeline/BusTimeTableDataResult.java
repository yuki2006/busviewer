package jp.co.yuki2006.busmap.bustimeline;

import android.app.Activity;

import jp.co.yuki2006.busmap.bustimeline.TimeLineActivity.CheckDate;

public class BusTimeTableDataResult {
    /*今だけ　簡易的に*/
    public BusTimeTableListAdapter timelineExpandAdapter;
    public String[] remarks;

    public LoadingListAdapter loadingListAdapter;
    public CheckDate checkDate;

    public BusTimeTableDataResult(Activity activity) {
        loadingListAdapter = new LoadingListAdapter(activity);
    }
}
