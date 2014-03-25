/**
 *
 */
package jp.co.yuki2006.busmap.map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockDialogFragment;

import java.util.ArrayList;

import jp.co.yuki2006.busmap.R;
import jp.co.yuki2006.busmap.etc.BusStopAdapter;
import jp.co.yuki2006.busmap.store.BusStop;

/**
 * @author yuki
 */
public class SearchMixBusStopFragment extends SherlockDialogFragment {
    /**
     *
     */
    public static final String BUS_STOP_LIST = "bus_stop_list";

    /*
     * (非 Javadoc)
     *
     * @see android.support.v4.app.DialogFragment#onDismiss(android.content.
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

    /*
     * (非 Javadoc)
     *
     * @see
     * android.support.v4.app.DialogFragment#onCreateDialog(android.os.Bundle)
     */
    @Override
    public Dialog onCreateDialog(Bundle arg0) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("検索結果");
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.search_mix_fragment, (ViewGroup) getView());
        ArrayList<BusStop> busStops = (ArrayList<BusStop>) getArguments().getSerializable(BUS_STOP_LIST);
        if (arg0 == null && busStops.size() == 0) {
            Toast.makeText(getActivity(), "一致するものがありませんでした。", Toast.LENGTH_LONG).show();
        }

        final BusStopAdapter adapter = new BusStopAdapter(getActivity(), null, busStops);
        ListView busStopListView = (ListView) view.findViewById(R.id.search_mix_activity_bus_stop_list);
        busStopListView.setAdapter(adapter);
        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BusStop item = (BusStop) adapter.getItem(which);
                if (getActivity() instanceof IFragmentToBusStop) {
                    ((IFragmentToBusStop) getActivity()).selectBusStop(item);
                }
                dismiss();
            }
        };
        CharSequence[] items = new String[busStops.size()];
        for (int i = 0; i < busStops.size(); i++) {
            items[i] = busStops.get(i).toString();
        }
        builder.setItems(items, listener);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setNeutralButton("再検索", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().onSearchRequested();
            }
        });
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
}
