package jp.co.yuki2006.busmap.route.store;

import java.io.Serializable;

import jp.co.yuki2006.busmap.store.BusStop;

public class NewBusSearch implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1120026548762701636L;
    public int busID;
    private final String remark;
    private BusStop departureBusStop;
    private BusStop arriveBusStop;
    public boolean isNonStep;
    private String lineNumber = "";
    private final String lastBusStopName;
    private String departureTime;
    private String arrivalTime;

    public NewBusSearch(String comment, String lastBusStopName) {
        this.remark = comment;
        this.lastBusStopName = lastBusStopName;
    }

    public BusStop getBusStop(boolean isDeparture) {
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
        if (getLineNumber() != null) {
            stringBuilder.append(getLineNumber());
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


    public String getTime(boolean isDeparture) {
        if (isDeparture) {
            return departureTime;
        } else {
            return arrivalTime;
        }
    }


    public void setBusStop(BusStop busStop, String time, boolean isDeparture) {
        if (isDeparture) {
            this.departureBusStop = busStop;
            departureTime = time;
        } else {
            this.arriveBusStop = busStop;
            arrivalTime = time;
        }
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
        if (getLineNumber() != null) {
            stringBuilder.append("路線番号:");
            stringBuilder.append(getLineNumber());
        }
        stringBuilder.append("\n\n");
        // stringBuilder.append("乗り場:　　");
        stringBuilder.append(departureBusStop.toString());
        stringBuilder.append("→");
        stringBuilder.append(arriveBusStop.toString());

        return stringBuilder.toString();
    }

    /**
     * @return lineNumber
     */
    public String getLineNumber() {
        return lineNumber;
    }

    /**
     * @param lineNumber セットする lineNumber
     */
    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }

}