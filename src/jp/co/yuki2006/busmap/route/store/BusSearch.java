package jp.co.yuki2006.busmap.route.store;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.io.Serializable;

import jp.co.yuki2006.busmap.R;
import jp.co.yuki2006.busmap.store.BusStop;

public class BusSearch implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1120026548762701636L;
    public int busID;
    private final String arrivalTime;
    private final String departureTime;
    private final String remark;
    private BusStop departureBusStop;
    private BusStop arriveBusStop;
    public boolean isNonStep;
    public Integer lineNumber;
    private final String lastBusStopName;

    public BusSearch(String departuretime, String arrivaltime, String comment, String lastBusStopName) {
        this.arrivalTime = arrivaltime;
        this.departureTime = departuretime;
        this.remark = comment;
        this.lastBusStopName = lastBusStopName;
    }

    private BusStop getBusStop(boolean isDeparture) {
        if (isDeparture) {
            return departureBusStop;
        } else {
            return arriveBusStop;
        }
    }

    /**
     * @return lastBusStopName
     */
    public String getLastBusStopName() {
        return lastBusStopName;
    }

    /**
     * @return
     */
    public CharSequence getLineNumberForWidget() {
        StringBuilder stringBuilder = new StringBuilder();
        if (lineNumber != null) {
            stringBuilder.append(lineNumber);
        }
        return stringBuilder.toString();
    }

    public String getNextTime() {
        return departureTime;
    }

    /**
     * @return
     */
    public CharSequence getRemarkForString() {
        StringBuilder stringBuilder = new StringBuilder("");
        if (remark != null) {
            stringBuilder.append(remark);
        }
        return stringBuilder.toString();
    }

    private static String abbreviate(String str, int maxWidth) {
        if (str.length() > 4) {
            str = str.substring(0, 4) + "...";
        }
        return str;
    }

    public String getStringForWidget() {
        StringBuilder stringBuilder = new StringBuilder();
        // stringBuilder.append("時間:　　");
        stringBuilder.append(departureTime);
        stringBuilder.append("(");
        stringBuilder.append(abbreviate(departureBusStop.getLoading().toString(), 4));
        stringBuilder.append(")");

        stringBuilder.append(" ～ ");
        stringBuilder.append(arrivalTime);
        stringBuilder.append("(");
        stringBuilder.append(abbreviate(arriveBusStop.getLoading().toString(), 4));
        stringBuilder.append(")");

        return stringBuilder.toString();
    }

    private String getTime(boolean isDeparture) {
        if (isDeparture) {
            return departureTime;
        } else {
            return arrivalTime;
        }
    }

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

        ((TextView) convertView.findViewById(R.id.advanced_search_result_last_bus_stop_name)).
                setText(getLastBusStopName() + "　行");

        ((TextView) convertView.findViewById(R.id.advanced_search_result_non_step)).
                setVisibility(isNonStep ? View.VISIBLE : View.INVISIBLE);

        ((TextView) convertView.findViewById(R.id.advanced_search_result_linenumber))
                .setText(lineNumber != null ? "路線番号:" + String.format("%02d", lineNumber) : "");

        ((TextView) convertView.findViewById(R.id.advanced_search_result_remark))
                .setText(remark);
        if (remark.length() > 0) {
            convertView.setBackgroundResource(R.color.bus_list_remark);
        } else {
            convertView.setBackgroundResource(R.color.bus_list_normal);
        }

    }

    public void setArriveBusStop(BusStop arriveBusStop) {
        this.arriveBusStop = arriveBusStop;
    }

    public void setDepartureBusStop(BusStop departureBusStop) {
        this.departureBusStop = departureBusStop;
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        // stringBuilder.append("時間:　　");
        stringBuilder.append(departureTime);
        stringBuilder.append(" ～ ");
        stringBuilder.append(arrivalTime);
        stringBuilder.append("       ");
        stringBuilder.append(remark);
        if (isNonStep) {
            stringBuilder.append("　ノンステップバス　");
        }
        if (lineNumber != null) {
            stringBuilder.append("路線番号:");
            stringBuilder.append(lineNumber);
        }
        stringBuilder.append("\n\n");
        // stringBuilder.append("乗り場:　　");
        stringBuilder.append(departureBusStop.toString());
        stringBuilder.append("→");
        stringBuilder.append(arriveBusStop.toString());

        return stringBuilder.toString();
    }

}