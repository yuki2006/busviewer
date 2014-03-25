package jp.co.yuki2006.busmap.bustimeline;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import jp.co.yuki2006.busmap.store.BusStop;
import jp.co.yuki2006.busmap.store.LoadingZone;

public class LoadingListAdapter extends ArrayAdapter<BusStop> implements
        SpinnerAdapter {

    public LoadingListAdapter(Context context) {
        super(context, android.R.layout.simple_spinner_item);
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    }

    public void addAllLoadingZoneList(LoadingZone[] loadingZones) {
        for (int i = 0; i < loadingZones.length; i++) {
            BusStop busStop = new BusStop(null, "", "", loadingZones[i], 0, "");
            add(busStop);
        }
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        convertView = super.getDropDownView(position, convertView, parent);
        ((TextView) convertView).setText(getItem(position).getLoading()
                .toString());
        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = super.getView(position, convertView, parent);
        ((TextView) convertView).setText(getItem(position).getLoading()
                .toString());

        return convertView;
    }

}
