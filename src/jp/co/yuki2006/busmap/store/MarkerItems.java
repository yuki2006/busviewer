package jp.co.yuki2006.busmap.store;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

public class MarkerItems {
    private ArrayList<BusStop> busStops = new ArrayList<BusStop>();
    private ArrayList<Marker> markers = new ArrayList<Marker>();

    private GoogleMap map;

    public MarkerItems(int defaultMarker, GoogleMap map) {
        this.map = map;
    }

    public void addNewMarkerAllData(ArrayList<BusStop> busStopDataList, boolean forceAdd, boolean detailMode) {
        for (BusStop newBusStopData : busStopDataList) {
            boolean hitflag = false;
            if (forceAdd == false) {
                // すでにあるマーカーかどうか調べる
                for (BusStop oldBusStopData : busStops) {
                    if (detailMode) {
                        if (newBusStopData.equals(oldBusStopData)) {
                            hitflag = true;
                            break;
                        }

                    } else {

                        if (newBusStopData.getBusStopID() == oldBusStopData
                                .getBusStopID()) {
                            hitflag = true;
                            break;
                        }
                    }
                }
            }
            if (!hitflag) {
                // if (detailmode == false) {
                // newBusStopData.setLoading(new LoadingZone("", ""));
                // }
                busStops.add(newBusStopData);
                Marker addMarker = map.addMarker(newBusStopData.getMarkerOptions());

                markers.add(addMarker);
            }
        }
        populate();
        // notify();

    }

    public void clear() {
        busStops.clear();
        markers.clear();

        populate();
    }

    /**
     *
     */
    private void populate() {
        // map.clear();

    }

    /**
     * @param marker
     * @return
     */
    public BusStop get(Marker marker) {

        return busStops.get(markers.indexOf(marker));
    }

    /**
     * @param selectedItem
     * @return
     */
    public Marker get(BusStop selectedItem) {
        return markers.get(busStops.indexOf(selectedItem));
    }

    /**
     * @param selectedItem
     * @return
     */
    public int indexOf(BusStop selectedItem) {
        return busStops.indexOf(selectedItem);
    }

    /**
     * @param i
     * @return
     */
    public Marker getMarker(int i) {
        int index = (i + markers.size()) % markers.size();
        return markers.get(index);
    }

    // public void setNextItem(int delta) {
    // listPointer = getLastFocusedIndex();
    // listPointer=0;
    // mapView.setSelectedItem(items.get(listPointer));

    // }

}
