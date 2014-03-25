/**
 *
 */
package jp.co.yuki2006.busmap.direction;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import jp.co.yuki2006.busmap.R;
import jp.co.yuki2006.busmap.store.DirectionData;
import jp.co.yuki2006.busmap.store.TimeTableElement;
import jp.co.yuki2006.busmap.values.PreferenceValues;

/**
 * @author yuki
 */
public class DirectionExpandAdapater extends SimpleExpandableListAdapter {

    /**
     * @author yuki
     */
    protected static class DirectionHolder {
        public TextView nextTimeTextView;
        public TextView prevTimeTextView;
        public TextView loadingTextView;
        public TextView linenumberTextView;
        public TextView directionTextView;
        public TextView busStopNameTextView;

    }

    protected static class TimeElementHolder {

        public TextView timeDetail;
        public TextView timeLabel;

    }

    protected final List<DirectionData> myDirectionDataList;
    protected List<DirectionData> filteredDataList;
    private final LayoutInflater myInflater;
    protected String filterLoadingZone = "";
    private boolean isShowLineNumber;
    private boolean isShowPrevTime;
    private boolean isShowNextTime;

    private final SharedPreferences sp;

    private boolean isShowLoadingZone;

    public DirectionExpandAdapater(Context context, List<DirectionData> myDirectionDataList) {
        super(context, null, 0, null, null, null, 0, null, null);
        this.myDirectionDataList = myDirectionDataList;
        filteredDataList = myDirectionDataList;
        myInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /*
     * (非 Javadoc)
     *
     * @see android.widget.SimpleExpandableListAdapter#getChildrenCount(int)
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        // TODO 自動生成されたメソッド・スタブ
        return filteredDataList.get(groupPosition).getTimeTableElementList().size();
    }

    /*
     * (非 Javadoc)
     *
     * @see android.widget.SimpleExpandableListAdapter#getChildView(int, int,
     * boolean, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                             ViewGroup parent) {
        TimeElementHolder timeElementHolder;
        if (convertView == null) {
            timeElementHolder = new TimeElementHolder();
            convertView = myInflater.inflate(R.layout.timetable_direction_children_view, null);
            timeElementHolder.timeLabel = (TextView) convertView.findViewById(R.id.expandable_time_label);
            timeElementHolder.timeDetail = (TextView) convertView.findViewById(R.id.expandable_time_detail);
            convertView.setTag(timeElementHolder);
        } else {
            timeElementHolder = (TimeElementHolder) convertView.getTag();
        }

        DirectionData directionData = filteredDataList.get(groupPosition);

        TimeTableElement timeTableElement = directionData.getTimeTableElementList()
                .get(childPosition);
        timeElementHolder.timeLabel.setTextColor(Color.WHITE);
        timeElementHolder.timeDetail.setTextColor(Color.WHITE);

        convertView.setBackgroundColor(Color.BLACK);

        if (timeTableElement.hour < Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            // 今の時間なら　背景変更
            timeElementHolder.timeLabel.setTextColor(Color.GRAY);
            timeElementHolder.timeDetail.setTextColor(Color.GRAY);
        } else if (timeTableElement.hour == Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            convertView.setBackgroundColor(Color.DKGRAY);
        } else {
            convertView.setBackgroundColor(Color.BLACK);
        }
        // ここを何とかしないと
        timeElementHolder.timeLabel.setText(timeTableElement.hour + " : ");

        timeElementHolder.timeDetail.setText(timeTableElement.data);
        return convertView;

    }

    /*
     * (非 Javadoc)
     *
     * @see android.widget.SimpleExpandableListAdapter#getGroup(int)
     */
    @Override
    public Object getGroup(int groupPosition) {
        // TODO 自動生成されたメソッド・スタブ
        return filteredDataList.get(groupPosition);
    }

    /*
     * (非 Javadoc)
     *
     * @see android.widget.SimpleExpandableListAdapter#getGroupCount()
     */
    @Override
    public int getGroupCount() {
        return filteredDataList.size();

    }

    /*
     * (非 Javadoc)
     *
     * @see android.widget.SimpleExpandableListAdapter#getGroupView(int,
     * boolean, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (groupPosition == 0) {
            isShowLoadingZone = sp.getBoolean(PreferenceValues.PF_KEY_MAIN_TIMELINE_SHOW_LOADING_ZONE, true);
            isShowLineNumber = sp.getBoolean(PreferenceValues.PF_KEY_MAIN_TIMELINE_SHOW_LINENUMBER, false);
            isShowPrevTime = sp.getBoolean(PreferenceValues.PF_KEY_MAIN_TIMELINE_SHOW_PREV_TIME, false);
            isShowNextTime = sp.getBoolean(PreferenceValues.PF_KEY_MAIN_TIMELINE_SHOW_NEXT_TIME, false);

        }

        DirectionHolder holder = null;
        if (convertView == null) {
            holder = new DirectionHolder();
            convertView = myInflater.inflate(R.layout.mydirection_expand_parent, null);

            holder.busStopNameTextView = (TextView) convertView.findViewById(R.id.busstop_name);
            holder.directionTextView = (TextView) convertView.findViewById(R.id.expandable_parent_text);
            holder.linenumberTextView = (TextView) convertView.findViewById(R.id.linenumber);
            holder.loadingTextView = (TextView) convertView.findViewById(R.id.expandable_parent_loading);
            holder.prevTimeTextView = (TextView) convertView.findViewById(R.id.expandable_parent_prev_time);
            holder.nextTimeTextView = (TextView) convertView.findViewById(R.id.expandable_parent_next_time);

            convertView.setTag(holder);
        } else {
            holder = (DirectionHolder) convertView.getTag();
        }

        DirectionData directionData = filteredDataList.get(groupPosition);

//		holder.busStopNameTextView.setText(directionData.busStop.getBusStopName().toString());
        holder.directionTextView.setText(directionData.toDestinationString());
        holder.linenumberTextView.setText(directionData.toLineNumberString());
        holder.loadingTextView.setText(directionData.toLoadingZoneString());

        holder.nextTimeTextView.setText(directionData.getBusNextTime().toString());
        holder.prevTimeTextView.setText(directionData.getBusPrevTime().toString());

        holder.loadingTextView.setVisibility(isExpanded || isShowLoadingZone ? View.VISIBLE : View.GONE);
        holder.linenumberTextView.setVisibility(isExpanded || isShowLineNumber ? View.VISIBLE : View.GONE);
        holder.nextTimeTextView.setVisibility(isExpanded || isShowNextTime ? View.VISIBLE : View.GONE);
        holder.prevTimeTextView.setVisibility(isExpanded || isShowPrevTime ? View.VISIBLE : View.GONE);

        return convertView;
    }

    /**
     * 内部の生データを取得します。
     *
     * @param groupPosition
     * @return
     */
    public DirectionData getRawGroup(int groupPosition) {
        return myDirectionDataList.get(groupPosition);
    }

    public void removeElement(DirectionData directionData) {
        myDirectionDataList.remove(directionData);
        filteredDataList.remove(directionData);
    }

    /**
     * 乗り場でのフィルタリングの更新をします。
     *
     * @param loadingZone
     */
    public void setLoadingFilter(String loadingZone) {
        filteredDataList = new ArrayList<DirectionData>(myDirectionDataList);
        // すべて縮小表示する

        if (loadingZone.equals("")) {

            return;
        }
        for (Iterator<DirectionData> iterator = filteredDataList.iterator(); iterator.hasNext(); ) {
            DirectionData data = (DirectionData) iterator.next();
            // 乗り場の指定ではないものはリストから削除する
            if (data.getLoadingZone().getLoadingID().equals(loadingZone) == false) {
                iterator.remove();
            }
        }
        this.filterLoadingZone = loadingZone;

    }

    public void setRawGroup(int groupPosition, DirectionData data) {
        myDirectionDataList.set(groupPosition, data);
    }
}