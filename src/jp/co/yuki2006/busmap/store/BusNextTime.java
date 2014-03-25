/**
 *
 */
package jp.co.yuki2006.busmap.store;


/**
 * 次のバスが○分という情報を管理するクラスです。
 *
 * @author yuki
 */
public class BusNextTime extends BusTime {
    private final String[] STRING = {"次のバスは%d分後%sです。", "その次のバスは%d分後%sです。"};

    @Override
    public String toString() {
        return super.toString(STRING);
    }

}