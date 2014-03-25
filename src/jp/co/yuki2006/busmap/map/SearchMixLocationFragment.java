/**
 *
 */
package jp.co.yuki2006.busmap.map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.location.Address;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;
import java.util.List;

import jp.co.yuki2006.busmap.R;
import jp.co.yuki2006.busmap.values.IntentValues;
import jp.co.yuki2006.busmap.values.PreferenceValues;

/**
 * @author yuki
 */
public class SearchMixLocationFragment extends SherlockDialogFragment {
    public static class LocationResultAdapter extends ArrayAdapter<Address> {

        private final LayoutInflater inflater;

        /**
         * @param context
         * @param objects
         */
        public LocationResultAdapter(Context context, List<Address> objects) {
            super(context, 0, objects);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.location_result_adapter_layout, parent, false);
            }
            Address item = getItem(position);
            TextView titleTextView = (TextView) convertView.findViewById(R.id.location_result_adapter_title);
            titleTextView.setText(item.getFeatureName());
            TextView addressTextView = (TextView) convertView.findViewById(R.id.location_result_adapter_address);
            StringBuilder builder = new StringBuilder();
            if (null != item.getAddressLine(1)) {
                builder.append(item.getAddressLine(1));
            }

            addressTextView.setText(builder.toString());

            return convertView;
        }
    }

    /**
     *
     */
    static final String ADDRESS_LIST = "address_list";

    /*
     * (非 Javadoc)
     *
     * @see android.support.v4.app.DialogFragment#onCancel(android.content.
     * DialogInterface)
     */
    @Override
    public void onCancel(DialogInterface arg0) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Fragment loaderFragment = fragmentManager.findFragmentByTag("loader");
        if (loaderFragment != null) {
            FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
            beginTransaction.remove(loaderFragment);
            beginTransaction.commit();
        }
        super.onCancel(arg0);
    }

    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View view = inflater.inflate(R.layout.search_mix_fragment, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setNegativeButton(android.R.string.cancel, null);
        String searchQuery = getArguments().getString(IntentValues.SEARCH_NAME);
        Address[] addresses = (Address[]) getArguments().getParcelableArray(ADDRESS_LIST);
        List<Address> list = Arrays.asList(addresses);

        if (getActivity() instanceof MapViewBasicActivity) {
            builder.setNeutralButton("再検索", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getActivity().onSearchRequested();
                }
            });
        }
        if (bundle == null && list.size() == 0) {
            builder.setTitle("住所が検索できませんでした。")
                    .setMessage(
                            "このアプリは石川県の住所しかサポートしておりません。\n\nまた、なるべく正確な施設名や、「○○市"
                                    + searchQuery
                                    + "」なども試してみてください。\n\n 例）「もりの里イオン」など");
        } else {
            builder.setTitle("検索結果");
            builder.setView(view);
            final LocationResultAdapter adapter = new LocationResultAdapter(getActivity().getApplicationContext(), list);
            ListView listView = new ListView(getActivity());
            listView.setAdapter(adapter);
            OnClickListener listener = new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Address item = (Address) adapter.getItem(which);
                    LatLng geoPoint = new LatLng(item.getLatitude(),
                            item.getLongitude());
                    MapViewBasicActivity mapViewBasicActivity = (MapViewBasicActivity) getActivity();

                    mapViewBasicActivity.setMapCenter(geoPoint, false);
                    mapViewBasicActivity.setOnMapMove();
                    dismiss();

                }
            };
            OnItemClickListener listener2 = new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int which, long arg3) {
                    Address item = (Address) adapter.getItem(which);
                    LatLng geoPoint = new LatLng(item.getLatitude(),
                             item.getLongitude());
                    MapViewBasicActivity mapViewBasicActivity = (MapViewBasicActivity) getActivity();

                    mapViewBasicActivity.setMapCenter(geoPoint, true);
                    mapViewBasicActivity.map.setOnMapMove();
                    dismiss();
                }
            };
            listView.setOnItemClickListener(listener2);
            builder.setView(listView);
        }
        searchGeo(searchQuery, list.size());
        return builder.create();
    }

    /*
     * (非 Javadoc)
     *
     * @see android.support.v4.app.DialogFragment#onDismiss(android.content.
     * DialogInterface)
     */
    @Override
    public void onDismiss(DialogInterface arg0) {
        if (getActivity() != null) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Fragment loaderFragment = fragmentManager.findFragmentByTag("loader");
            if (loaderFragment != null) {
                FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
                beginTransaction.remove(loaderFragment);
                beginTransaction.commit();
            }
        }
        super.onDismiss(arg0);
    }

    private void searchGeo(final CharSequence searchQuery, int hitCount) {

        final SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        if (sp.contains(PreferenceValues.PF_SEND_LOCATION_NAME)) {
            // これがtrueなら送信
            boolean isSend = sp.getBoolean(
                    PreferenceValues.PF_SEND_LOCATION_NAME, false);
            if (isSend) {
                LocationSearchResult locationSearchResult = new LocationSearchResult(
                        getActivity(), null, hitCount,
                        ((MapViewBasicActivity) getActivity()).map.getMap().getCameraPosition().target);
                locationSearchResult.execute(searchQuery);
            }
        }
    }
}
