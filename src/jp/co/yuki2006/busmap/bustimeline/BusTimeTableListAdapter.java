/**
 *
 */
package jp.co.yuki2006.busmap.bustimeline;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import jp.co.yuki2006.busmap.direction.DirectionExpandAdapater;
import jp.co.yuki2006.busmap.store.DirectionData;

/**
 * @author yuki
 */
public class BusTimeTableListAdapter extends DirectionExpandAdapater {

    /**
     * @param context
     * @param myDirectionDataList
     */
    public BusTimeTableListAdapter(Context context, List<DirectionData> myDirectionDataList) {
        super(context, myDirectionDataList);
    }

    /*
     * (非 Javadoc)
     *
     * @see
     * jp.co.yuki2006.busmap.direction.DirectionExpandAdapater#getGroupView(int,
     * boolean, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View groupView = super.getGroupView(groupPosition, isExpanded, convertView, parent);
        DirectionHolder holder = (DirectionHolder) groupView.getTag();
        // 時刻表画面での不必要な表示の省略
        holder.busStopNameTextView.setVisibility(View.GONE);


        return groupView;

    }
}
