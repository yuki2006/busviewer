/**
 *
 */
package jp.co.yuki2006.busmap.direction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.co.yuki2006.busmap.bustimeline.TimeLineActivity.CheckDate;
import jp.co.yuki2006.busmap.store.DirectionData;

/**
 * @author yuki
 */
public class MyDirectionParseResult {

    public List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
    public List<List<Map<String, String>>> childrenData =
            new ArrayList<List<Map<String, String>>>();
    public CheckDate checkTime;
    /**
     * 次の移行への先行実装です。
     */
    public List<DirectionData> directionData;

}
