package jp.co.yuki2006.busmap.store;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * マイ行き先用データクラスです。
 *
 * @author ono
 */
public class DirectionData implements Serializable {
    public static class Builder {
        private String destination;

        private String via;

        // バス時刻表の乗り場フィルターのため
        private LoadingZone loadingZone;

        private String lineNumber;
        private boolean isEmptyBus = false;

        public DirectionData create() {
            return new DirectionData(loadingZone, destination, via, lineNumber, isEmptyBus);
        }

        public void setDestination(String destination) {
            this.destination = destination;
        }

        /**
         * @param isEmptyBus セットする isEmptyBus
         */
        public void setEmptyBus(boolean isEmptyBus) {
            this.isEmptyBus = isEmptyBus;
        }

        /**
         * @param linenumber
         */
        public void setLineNumber(String lineNumber) {
            this.lineNumber = lineNumber;
        }

        public void setLoadingZone(LoadingZone loadingZone) {
            this.loadingZone = loadingZone;
        }

        public void setVia(String via) {
            this.via = via;
        }
    }

    /**
     *
     */
    private static final long serialVersionUID = 1900893369355868954L;
    // public BusStop busStop;
    public String destination;
    public String via;
    private final boolean isEmptyBus;
    // バス時刻表の乗り場フィルターのため
    private final LoadingZone loadingZone;
    private final transient BusNextTime busNextTime = new BusNextTime();
    private final transient BusPrevTime busPrevTime = new BusPrevTime();

    private final transient List<TimeTableElement> timeTableElementList = new ArrayList<TimeTableElement>();

    public transient Long nextTime = null;

    private final String lineNumber;

    public DirectionData(LoadingZone loadingZone, String destination, String via, String lineNumber, boolean isEmptyBus) {

        this.loadingZone = loadingZone;
        this.destination = destination;
        this.via = via;
        this.lineNumber = lineNumber;
        this.isEmptyBus = isEmptyBus;

    }

    /**
     * @return busNextTime
     */
    public BusNextTime getBusNextTime() {
        return this.busNextTime;
    }

    /**
     * @return busPrevTime
     */
    public BusPrevTime getBusPrevTime() {
        return this.busPrevTime;
    }

    public LoadingZone getLoadingZone() {
        return loadingZone;
    }

    /**
     * @return timeTableElementList
     */
    public List<TimeTableElement> getTimeTableElementList() {
        return timeTableElementList;
    }

    public boolean isEmptyBus() {
        return isEmptyBus;
    }

    public boolean refleshNextTime() {
        busPrevTime.downCount();
        return busNextTime.downCount();
    }

    public String toDestinationString() {
        StringBuilder builder = new StringBuilder();
        builder.append(destination);
        if (via != null && via.length() > 0) {
            builder.append("(")
                    .append(via).append(" 経由)");
        }
        return builder.toString();
    }

    public String toLineNumberString() {
        return "路線番号 : " + lineNumber;
    }

    public String toLoadingZoneString() {
        if (isEmptyBus()) {
            return "この行き先での今日のバスはありません";
        }
        return "乗り場：" + loadingZone.toString();

    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(loadingZone);
        builder.append("\n");
        builder.append(toDestinationString());
        return builder.toString();

    }
}
