/**
 *
 */
package jp.co.yuki2006.busmap.store;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * 前のバスは○分というデータ
 *
 * @author yuki
 */
public class BusPrevTime extends BusTime {
    private final String[] STRING = {"前のバスは%d分前%sです。", "その前のバスは%d分前%sです。"};

    /* (非 Javadoc)
     * @see jp.co.yuki2006.busmap.model.BusTime#addTime(java.lang.String, java.lang.String)
     */
    @Override
    public void addTime(String newTimeString, String newRemark) {

        super.addTime("-" + newTimeString, newRemark);
    }

    @Override
    public String toString() {
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
            output.append(String.format(STRING[i], (calendar.getTimeInMillis() - time[i]) / (1000 * 60), remarkString));
        }
        return output.toString();
    }
}
