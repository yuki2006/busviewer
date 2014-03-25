package jp.co.yuki2006.busmap.route.store;

import java.io.Serializable;
import java.util.Calendar;

import jp.co.yuki2006.busmap.store.BusStop;

public class BusRouteElement implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1367088511074564822L;
    public String arriveTime;
    private BusStop busStop;

    public long getArriveMillTime(long l) {
        String[] split = arriveTime.split(":");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(l);
        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(split[0]));
        calendar.set(Calendar.MINUTE, Integer.valueOf(split[1]));
        return calendar.getTimeInMillis();
    }

    public BusStop getBusStop() {
        return busStop;
    }

    public void setBusStop(BusStop busStop) {
        this.busStop = busStop;
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getBusStop().toString());
        if (getBusStop().getPoint() == null) {
            stringBuilder.append("\t\t ※マーカーなし");
        }
        stringBuilder.append("\n");
        stringBuilder.append(arriveTime);

        return stringBuilder.toString();
    }
}