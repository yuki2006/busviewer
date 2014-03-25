package jp.co.yuki2006.busmap.bustimeline;

import android.os.Handler;
import android.util.Log;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import jp.co.yuki2006.busmap.ad.AdActivity;
import jp.co.yuki2006.busmap.direction.DirectionExpandAdapater;
import jp.co.yuki2006.busmap.etc.Etc;
import jp.co.yuki2006.busmap.store.DirectionData;

/**
 * 時刻表の画面やマイ行き先で使う、時刻表の処理の基本クラスです。
 *
 * @author yuki
 */
public abstract class BusTimeTableTimerBasisActivity extends AdActivity {
    /**
     * 時刻表のデータ指定の取得曜日です。
     */
    public enum DateType {
        /**
         * 今日の分・平日・土曜日・日祝の分です。
         */
        TODAY, WEEKDAY, SATURDAY, HOLIDAY
    }

    /**
     * 各曜日の文字列です。
     */
    protected static final String[] DAY_STRING_LIST = new String[]{"日・祝", "月", "火", "水", "木", "金", "土"};
    protected Timer timer;
    private Calendar lastLoadingTime;
    /**
     * 自動的に読み込みを行うかどうかのフラグ
     */
    protected boolean enableAutoLoading = true;

    /**
     * @param adapter
     * @return 新しいデータを呼び出す必要がある場合true
     */
    protected boolean downCountText(DirectionExpandAdapater adapter, int diffMinutesTime) {
        int count = adapter.getGroupCount();
        boolean isWebAccess = false;
        for (int i = 0; i < count; i++) {
            DirectionData data = (DirectionData) adapter.getGroup(i);
            isWebAccess |= data.refleshNextTime();

        }

        return isWebAccess;
    }

    /**
     * 前回読み込み時間空の差を取ります。無効なら0を返す。
     *
     * @return
     */
    protected int getDiffMinutsTime() {
        if (enableAutoLoading == false) {
            return 0;
        }
        Calendar nowTime = Calendar.getInstance();
        return nowTime.get(Calendar.SECOND) - lastLoadingTime.get(Calendar.SECOND);
    }

    protected TimerTask getTimerTask() {
        final Handler handler = new Handler();
        TimerTask timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        int diffTime = getDiffMinutsTime();
                        reshowBusStopData(diffTime);
                        invalidate();
                    }
                });
            }
        };
        return timerTask;
    }

    protected void invalidate() {
        lastLoadingTime = Calendar.getInstance();
        // lastLoadingTime.setTimeInMillis(lastLoadingTime.getTimeInMillis() -
        // lastLoadingTime.getTimeInMillis()
        // % (60 * 1000));

    }

    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if (timer != null) {
            timer.cancel();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (lastLoadingTime != null) {
            int diffTime = getDiffMinutsTime();
            Log.d("Bus", "" + diffTime);
            if (diffTime > 0) {
                reshowBusStopData(diffTime);
            }
        }
        invalidate();

        setIntervalTimer();
    }

    protected abstract void reshowBusStopData(int timeDiff);

    private void setIntervalTimer() {
        timer = new Timer();
        Etc.setMinutesIntervalTimer(timer, getTimerTask());
    }
}
