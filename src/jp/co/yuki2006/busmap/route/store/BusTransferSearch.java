package jp.co.yuki2006.busmap.route.store;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

import jp.co.yuki2006.busmap.R;
import jp.co.yuki2006.busmap.store.BusStop;

public class BusTransferSearch implements Serializable {
    ArrayList<NewBusSearch> list = new ArrayList<NewBusSearch>();
    /**
     *
     */
    private static final long serialVersionUID = 1120026548762701636L;

    public void inflateLayout(Context context, View convertView) {
        int[] viewSelector = {R.id.advanced_search_result_departure, R.id.advanced_search_result_arrive};

        for (int viewContainer : viewSelector) {
            boolean isDeparture = viewContainer == R.id.advanced_search_result_departure;
            View departureView = convertView.findViewById(viewContainer);

            TextView timeTextView = (TextView) departureView.findViewById(R.id.advanced_search_result_time);
            timeTextView.setText(getTime(isDeparture));
            TextView loadingZoneTextView = (TextView) departureView
                    .findViewById(R.id.advanced_search_result_loading_zone);
            loadingZoneTextView.setText(getBusStop(isDeparture).getLoading().toString());
        }
        StringBuilder builder = new StringBuilder(list.get(0).getBusStop(true).getBusStopName());
        for (NewBusSearch newBusSearch : list) {
            builder.append("->");
            builder.append(newBusSearch.getBusStop(false).getBusStopName());
        }
        ((TextView) convertView.findViewById(R.id.advanced_new_busstop_history)).
                setText(builder.toString());
    }

    /**
     * @param isDeparture
     * @return
     */
    private BusStop getBusStop(boolean isDeparture) {
        NewBusSearch item;
        if (list.size() == 1) {
            item = list.get(0);
        } else if (isDeparture) {
            item = list.get(0);
        } else {
            item = list.get(1);
        }
        return item.getBusStop(isDeparture);
    }

    /**
     * @param isDeparture
     * @return
     */
    private CharSequence getTime(boolean isDeparture) {
        NewBusSearch item;
        if (list.size() == 1) {
            item = list.get(0);
        } else if (isDeparture) {
            item = list.get(0);
        } else {
            item = list.get(1);
        }
        return item.getTime(isDeparture);
    }

    /**
     * @param i
     * @return
     */
    public NewBusSearch get(int i) {
        return list.get(i);
    }

    /**
     * @param searchElement
     * @return
     */
    public boolean add(NewBusSearch searchElement) {
        return list.add(searchElement);
    }

    /**
     * @return
     */
    public int size() {
        return list.size();
    }

}