package jp.co.yuki2006.busmap.route.store;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import jp.co.yuki2006.busmap.store.BusStop;
import jp.co.yuki2006.busmap.store.LoadingZone;

/**
 * 詳細検索で必要などのデータクラスです。
 *
 * @author yuki
 */
public class RouteData implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private BusStop departureBusStop;
    private BusStop arriveBusStop;
    private long currentTime;
    private transient Calendar currentTimeCallender = null;
    private boolean fromDeparture = true;
    private String departureBusStopFilter = "";
    private String arriveBusStopFilter = "";

    private LoadingZone[] departureBusStopLoading;

    private LoadingZone[] arrivalBusStopLoading;
    private boolean isFirstOrLast = false;

    private boolean enableTransfer;
    private int transferTime=5;

    public RouteData() {

        setNow();
    }

    private void createIfCallenderNull() {
        if (currentTimeCallender == null) {
            currentTimeCallender = Calendar.getInstance(new Locale("JAPAN"));
            currentTimeCallender.setTimeInMillis(currentTime);
        }
    }

    /*
     * (非 Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RouteData other = (RouteData) obj;
        if (this.arriveBusStop == null) {
            if (other.arriveBusStop != null)
                return false;
        } else if (!this.arriveBusStop.equals(other.arriveBusStop))
            return false;
        if (this.departureBusStop == null) {
            if (other.departureBusStop != null)
                return false;
        } else if (!this.departureBusStop.equals(other.departureBusStop))
            return false;
        return true;
    }

    /**
     * @param field
     * @return
     * @see java.util.Calendar#get(int)
     */
    public int get(int field) {
        createIfCallenderNull();
        return this.currentTimeCallender.get(field);
    }

    public BusStop getBusStop(boolean isDeparture) {
        if (isDeparture) {
            return departureBusStop;
        } else {
            return arriveBusStop;
        }
    }

    // public Calendar getCurrentTime() {
    // if (currentTimeCallender != null) {
    // return currentTimeCallender;
    // }
    // currentTimeCallender = Calendar.getInstance(new Locale("JAPAN"));
    // currentTimeCallender.setTimeInMillis(currentTime);
    // return currentTimeCallender;
    // }

    public long getCurrentMiliTime() {
        return currentTime;
    }

    public String getFilterText(boolean isDeparture) {
        if (isDeparture) {
            return departureBusStopFilter;
        } else {
            return arriveBusStopFilter;
        }
    }

    public LoadingZone[] getLoadingArray(boolean isDeparture) {
        if (isDeparture) {
            return departureBusStopLoading;
        } else {
            return arrivalBusStopLoading;
        }
    }

    /*
     * (非 Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return 0;
    }

    public boolean isFirstOrLast() {
        return isFirstOrLast;
    }

    public boolean isFromDeparture() {
        return fromDeparture;
    }

    /**
     * データの整合性をチェックします。
     */
    public boolean isValid() {
        return getBusStop(true).isValid() && getBusStop(false).isValid();
    }

    /**
     * @param field
     * @param value
     * @see java.util.Calendar#set(int, int)
     */
    public void set(int field, int value) {
        createIfCallenderNull();
        this.currentTimeCallender.set(field, value);
        currentTime = currentTimeCallender.getTimeInMillis();
    }

    /**
     * @param year
     * @param monthOfYear
     * @param dayOfMonth
     */
    public void set(int year, int monthOfYear, int dayOfMonth) {
        currentTimeCallender.set(year, monthOfYear, dayOfMonth);
        currentTime = currentTimeCallender.getTimeInMillis();
    }

    public void setBusStop(boolean isDeparture, BusStop busStop) {
        if (isDeparture) {
            this.departureBusStop = busStop;
        } else {
            this.arriveBusStop = busStop;
        }
    }

    /**
     * @param isDeparture
     * @param string
     */
    public void setFilterText(boolean isDeparture, String string) {
        if (isDeparture) {
            departureBusStopFilter = string;
        } else {
            arriveBusStopFilter = string;
        }
    }

    public void setFromDeparture(boolean fromDeparture) {
        this.fromDeparture = fromDeparture;
    }

    public void setIsFirstOrLast(boolean bool) {
        this.isFirstOrLast = bool;
    }

    public void setLoadingList(boolean isDeparture, LoadingZone[] loadingZones) {
        if (isDeparture) {
            this.departureBusStopLoading = loadingZones;
        } else {
            this.arrivalBusStopLoading = loadingZones;
        }

    }

    public void setNow() {
        currentTimeCallender = Calendar.getInstance(TimeZone.getTimeZone("Japan"));
        currentTime = currentTimeCallender.getTimeInMillis();
    }

    /**
     *
     */
    public void swap() {
        BusStop tmpBusStop = getBusStop(true);
        LoadingZone[] loadingZone = getLoadingArray(true);
        String tmpfilterDeparture = departureBusStopFilter;

        BusStop tmpBusStop2 = getBusStop(false);
        LoadingZone[] loadingZone2 = getLoadingArray(false);
        String tmpfilterArrive = arriveBusStopFilter;

        setBusStop(true, tmpBusStop2);
        setBusStop(false, tmpBusStop);

        departureBusStopLoading = loadingZone2;
        arrivalBusStopLoading = loadingZone;

        departureBusStopFilter = tmpfilterArrive;
        arriveBusStopFilter = tmpfilterDeparture;

    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (departureBusStop != null) {
            stringBuilder.append("出発バス停:  ");
            stringBuilder.append(departureBusStop.toString());
        }
        if (arriveBusStop != null) {
            stringBuilder.append("\n到着バス停:  ");
            stringBuilder.append(arriveBusStop.toString());
        }
        return stringBuilder.toString();
    }

    /**
     * @param isChecked
     */
    public void setEnableTransfer(boolean enableTransfer) {
        this.enableTransfer = enableTransfer;
    }

    public boolean isEnableTransfer() {
        return enableTransfer;
    }

	/**
	 * @return transferTime
	 */
	public int getTransferTime() {
		return transferTime;
	}

	/**
	 * @param transferTime セットする transferTime
	 */
	public void setTransferTime(int transferTime) {
		this.transferTime = transferTime;
	}
}