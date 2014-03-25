package jp.co.yuki2006.busmap.etc;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import jp.co.yuki2006.busmap.MyBusStopActivity;
import jp.co.yuki2006.busmap.R;
import jp.co.yuki2006.busmap.list.InteractiveListAdapter;
import jp.co.yuki2006.busmap.store.BusStop;

public class BusStopAdapter extends InteractiveListAdapter<BusStop> {

    public BusStopAdapter(Context context, ViewGroup swipeLayout) {
        super(context, R.layout.buslist_layout, swipeLayout);
    }

    public BusStopAdapter(Context context, ViewGroup swipeLayout, List<BusStop> objects) {
        super(context, R.layout.buslist_layout, swipeLayout, objects);

    }

    /*
     * (非 Javadoc)
     *
     * @see jp.co.yuki2006.busmap.list.InteractiveListAdapter#getView(int,
     * android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = super.getView(position, convertView, parent);
        BusStop item = getItem(position);
        ((TextView) convertView.findViewById(R.id.bus_stop_name)).setText(item.toString());
        return convertView;
    }

    /*
     * (非 Javadoc)
     *
     * @see
     * jp.co.yuki2006.busmap.list.InteractiveListAdapter#movePositionList(java
     * .lang.Object, int)
     */
    @Override
    protected void movePositionList(int oldPostion, int newPosition) {
        if (getContext() instanceof MyBusStopActivity) {
            MyBusStopActivity myBusStopActivity = (MyBusStopActivity) getContext();
            myBusStopActivity.moveListPosition(oldPostion, newPosition);
        }
    }

    /*
     * (非 Javadoc)
     *
     * @see
     * jp.co.yuki2006.busmap.list.InteractiveListAdapter#removeListPosition(int)
     */
    @Override
    protected void removeListPosition(int oldPosition) {
        if (getContext() instanceof MyBusStopActivity) {
            MyBusStopActivity myBusStopActivity = (MyBusStopActivity) getContext();
            myBusStopActivity.removeBusStop(getItem(oldPosition));
        }
    }

}