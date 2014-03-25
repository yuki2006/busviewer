/**
 *
 */
package jp.co.yuki2006.busmap.store;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * @author yuki
 */
public abstract class BusTime {
    public Long[] time = {null, null};
    public String[] remark = {"", ""};

    /**
     * 時間を追加します。 -1がある部分を置き換えていきます。
     *
     * @param newTime
     */
    public void addTime(String newTimeString, String newRemark) {
        for (int i = 0; i < time.length; i++) {
            if (time[i] == null || time[i] < 0) {
                Calendar calendar = (Calendar.getInstance(TimeZone.getTimeZone("Japan")));
                calendar.set(Calendar.SECOND, 59);
                time[i] = calendar.getTimeInMillis()
                        + 1000L * 60 * Integer.valueOf(newTimeString);
                remark[i] = newRemark;
                return;
            }
        }
    }

    /**
     * 次（前）のバスが合ったか調べます。
     *
     * @return そのバスがあればtrue　なければfalseです。
     */
    public boolean checkTime() {
        for (int i = 0; i < time.length; i++) {
            if (time[i] != null && time[i] >= 0) {
                return true;
            }
        }
        return false;
    }

    public void clearData() {
        for (int i = 0; i < time.length; i++) {
            time[i] = null;
        }
        for (int i = 0; i < remark.length; i++) {
            remark[i] = null;
        }

    }

    /**
     * downTime分の時間を減らします（または増やします）
     *
     * @param downTime 　減らす分の時間です。
     * @return マイナスになった時 true、それ以外ならfalseです。
     */
    public boolean downCount() {
        boolean result = false;
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("JAPAN"));

        for (int i = 0; i < this.time.length; i++) {
            if (time[i] != null) {
                long value = time[i] - calendar.getTimeInMillis();
                // 初めは０以上で　マイナスになった時
                if (value < 0) {
                    result = true;
                    // 時間の更新
                    // 次の時間があれば代入
                    if (time[1] != null) {
                        time[0] = time[1];
                        time[1] = null;
                    }

                }
            }
        }
        return result;
    }

    protected String toString(String[] format) {
        StringBuilder output = new StringBuilder();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("JAPAN"));
        for (int i = 0; i < time.length; i++) {
            if (time[i] == null || time[i] < 0) {
                continue;
            }
            String remarkString = "";
            if (remark[i].equals("") == false) {
                remarkString = "(" + remark[i] + ")";
            }
            output.append(String.format(format[i], (time[i] - calendar.getTimeInMillis()) / (1000 * 60), remarkString));
        }
        return output.toString();
    }
}
