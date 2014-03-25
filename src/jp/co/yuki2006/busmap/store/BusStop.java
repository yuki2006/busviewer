package jp.co.yuki2006.busmap.store;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;

public class BusStop implements Serializable {
    private static final long serialVersionUID = 1L;

    public static class Builder {
        private LatLng point;
        private String title;
        private String region;

        private LoadingZone loadingZone;

        private int busStopID;
        private String snippet = "";

        public BusStop create() {
            return new BusStop(point, title, region, loadingZone, busStopID,
                    snippet);
        }

        public void setBusStopID(int busStopID) {
            this.busStopID = busStopID;
        }

        public void setLoadingZone(LoadingZone loadingZone) {
            this.loadingZone = loadingZone;
        }

        public void setPoint(LatLng geoPoint) {
            point = geoPoint;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public void setSnippet(String snippet) {
            this.snippet = snippet;
        }

        public void setTitle(String title) {
            this.title = title;
        }

    }

    private String title;

    private String region = "";

    private LoadingZone loadingZone;
    private int busStopID;

    private String optionalSnippet;
    private double latitude;
    private double longitude;

    private transient BitmapDescriptor icon;

    public BusStop(BusStop busStop, String optionalSnippet) {
        this(busStop.getPoint(), busStop.getBusStopName(), busStop.getRegion(),
                busStop.getLoading(), busStop.getBusStopID(), optionalSnippet);
    }

    public BusStop(LatLng point, String title, LoadingZone loading) {
        this(point, title, "", loading, 0, "");
    }

    public BusStop(LatLng point, String title, String region,
                   LoadingZone loadingZone, int busStopID, String optionalSnippet) {
        this.region = region;
        this.busStopID = busStopID;
        this.title = title;

        this.loadingZone = loadingZone;
        this.optionalSnippet = optionalSnippet;
        if (point != null) {
            this.latitude = point.latitude;
            this.longitude = point.longitude;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BusStop) {
            BusStop newName = (BusStop) obj;
            if (busStopID == newName.getBusStopID()) {
                if (loadingZone.getLoadingID().length() > 0 &&
                        newName.getLoading().getLoadingID().length() > 0) {
                    if (loadingZone.getLoadingID().equals(
                            newName.getLoading().getLoadingID())) {
                        return true;
                    }
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    public int getBusStopID() {
        return busStopID;
    }

    public String getBusStopName() {
        return title;
    }

    public String getBusStopNameAndRegion() {
        StringBuilder builder = new StringBuilder(title);
        if (getRegion() != null && getRegion().length() > 0) {
            builder.append("(");
            builder.append(getRegion());
            builder.append(")");
        }
        return builder.toString();
    }

    public LoadingZone getLoading() {
        return loadingZone;
    }

    public MarkerOptions getMarkerOptions() {
        if (getPoint() != null) {
            LatLng point = getPoint();
            MarkerOptions markerOptions = new MarkerOptions().position(point).title(getBusStopNameAndRegion());
            if (icon != null) {
                markerOptions.icon(icon);
            }
            if (loadingZone != null) {
                return markerOptions.snippet("(" + loadingZone.toString() + ")" + optionalSnippet);
            } else {
                return markerOptions;
            }
        } else {
            return null;
        }

    }

    public LatLng getPoint() {
        return new LatLng(latitude, longitude);
    }

    public String getRegion() {
        return region;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + busStopID;
        result = prime * result
                + ((loadingZone == null) ? 0 : loadingZone.hashCode());
        return result;
    }

    /**
     * データの整合性をチェックします。
     *
     * @return
     */
    public boolean isValid() {
        if (getLoading().getDbId() > 0) {
            return true;
        } else if (getLoading().getDbId() == -1) {
            if (getBusStopID() > 0) {
                return true;
            }
        }
        return false;
    }

    public void setLoading(LoadingZone loadingZone) {
        this.loadingZone = loadingZone;

    }

    public void setIcon(BitmapDescriptor icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {

        if (getBusStopName().length() == 0) {
            if (loadingZone.getAliasName().length() > 0) {
                return loadingZone.getAliasName();
            } else {
                return loadingZone.getLoadingID();
            }
        }
        StringBuilder toStringBuilder = new StringBuilder();
        toStringBuilder.append(this.getBusStopNameAndRegion());

        if (loadingZone != null && loadingZone.getLoadingID() != null
                && loadingZone.getLoadingID().length() > 0) {
            toStringBuilder.append("@");
            if (loadingZone.getAliasName() != null
                    && loadingZone.getAliasName().length() > 0) {
                toStringBuilder.append(loadingZone.getAliasName());
            } else {
                toStringBuilder.append(loadingZone.getLoadingID());
            }
        }
        return toStringBuilder.toString();
    }
}
