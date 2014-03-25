/**
 *
 */
package jp.co.yuki2006.busmap.store;


/**
 * @author yuki
 */
public class TimeTableElement {
    public int hour = 0;
    public String data;

    /*
     * (非 Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        return hour + " : " + data;
    }
}
