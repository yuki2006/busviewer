package jp.co.yuki2006.busmap.custom;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.co.yuki2006.busmap.R;
import jp.co.yuki2006.busmap.bustimeline.fragment.CustomAliasThanksDialogFragment;
import jp.co.yuki2006.busmap.etc.ProgressDialogFragment;
import jp.co.yuki2006.busmap.map.IFragmentToBusStop;
import jp.co.yuki2006.busmap.map.SearchMixBusStopFragment;
import jp.co.yuki2006.busmap.parser.BusLoadingXMLParser;
import jp.co.yuki2006.busmap.store.BusStop;
import jp.co.yuki2006.busmap.store.LoadingZone;
import jp.co.yuki2006.busmap.values.IntentValues;
import jp.co.yuki2006.busmap.web.IWebPostRunnable;
import jp.co.yuki2006.busmap.web.Web;

public class CustomLoadingEditFragment extends SherlockFragment implements IFragmentToBusStop {
    private final int MENU_SEND_BUTTON = 10000;

    public static class LoadingZoneLoader extends AsyncTaskLoader<List<LoadingZone>> {
        private final int busStopID;
        private final Context context;

        /**
         * @param arg0
         */
        public LoadingZoneLoader(Context context, int busStopID) {
            super(context);
            this.context = context;
            this.busStopID = busStopID;
        }

        /*
         * (非 Javadoc)
         *
         * @see android.support.v4.content.AsyncTaskLoader#loadInBackground()
         */
        @Override
        public List<LoadingZone> loadInBackground() {
            try {
                URL url = new URL(Web.getHostURL(getContext())
                        + "rooting/getStationLoading.php?BusStopID=" + busStopID);
                InputStream content = (InputStream) url.getContent();
                if (content != null) {
                    LoadingZone[] loadingList = BusLoadingXMLParser.getLoading(content);
                    return Arrays.asList(loadingList);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                if (context instanceof Activity) {
                    Runnable action = new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), R.string.connection_terminated, Toast.LENGTH_LONG).show();
                        }
                    };
                    ((Activity) context).runOnUiThread(action);

                }

                e.printStackTrace();
            }

            return null;
        }

    }

    private BusStop busStop;
    private List<LoadingZone> loadingZones = new ArrayList<LoadingZone>();
    private OnClickListener l = new OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText editText = (EditText) getView().findViewById(R.id.bus_stop_edit_text);
            String busStopName = editText.getText().toString();
            Bundle bundle = new Bundle();
            bundle.putString(IntentValues.SEARCH_NAME, busStopName);
            getLoaderManager().restartLoader(0, bundle, loaderSearchBusStopCallbacks).forceLoad();
        }
    };

    private static final Handler handler = new Handler();

    public final LoaderCallbacks<ArrayList<BusStop>> loaderSearchBusStopCallbacks = new LoaderCallbacks<ArrayList<BusStop>>() {
        @Override
        public Loader<ArrayList<BusStop>> onCreateLoader(int arg0, Bundle bundle) {
            ProgressDialogFragment.showHelperDialog(CustomLoadingEditFragment.this.getFragmentManager());
            String busStopName = bundle.getString(IntentValues.SEARCH_NAME);

            MixBusStopSearchLoader mixBusStopSearchLoader = new MixBusStopSearchLoader(getActivity(),
                    busStopName);
            // mixBusStopSearchLoader.forceLoad();
            return mixBusStopSearchLoader;
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<BusStop>> arg0) {
            // TODO 自動生成されたメソッド・スタブ

        }

        @Override
        public void onLoadFinished(final Loader<ArrayList<BusStop>> arg0, final ArrayList<BusStop> busStops) {
            ProgressDialogFragment.dismissHelperDialog(CustomLoadingEditFragment.this.getFragmentManager());
            if (busStops == null) {
                return;
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    FragmentManager supportFragmentManager = CustomLoadingEditFragment.this.getFragmentManager();
                    if (supportFragmentManager.findFragmentByTag("list") == null) {
                        SearchMixBusStopFragment dialogBusStopFragment = new SearchMixBusStopFragment();
                        Bundle argument = new Bundle();
                        argument.putSerializable(SearchMixBusStopFragment.BUS_STOP_LIST, (Serializable) busStops);
                        dialogBusStopFragment.setArguments(argument);
                        dialogBusStopFragment.show(supportFragmentManager, "list");
                        getLoaderManager().destroyLoader(arg0.getId());
                    }
                }
            });

        }
    };

    private LoaderCallbacks<List<LoadingZone>> loaderLoadingCallbacks = new LoaderCallbacks<List<LoadingZone>>() {
        @Override
        public Loader<List<LoadingZone>> onCreateLoader(int i, Bundle bundle) {
            ProgressDialogFragment.showHelperDialog(CustomLoadingEditFragment.this.getFragmentManager());

            int busStopID = bundle.getInt(IntentValues.TRANSITION_BUS_STOP_ID);
            return new LoadingZoneLoader(getActivity(), busStopID);
        }

        @Override
        public void onLoaderReset(Loader<List<LoadingZone>> arg0) {

        }

        @Override
        public void onLoadFinished(Loader<List<LoadingZone>> arg0, List<LoadingZone> list) {
            ProgressDialogFragment.dismissHelperDialog(CustomLoadingEditFragment.this.getFragmentManager());
            loadingZones.clear();
            for (LoadingZone loadingZone : list) {
                if (loadingZone.isCanLoading()) {
                    loadingZones.add(loadingZone);
                }
            }

            Spinner spinner = (Spinner) getView().findViewById(R.id.bus_stop_loading_spinner);
            spinner.setAdapter(spinner.getAdapter());
            spinner.setSelection(0);
            spinner.invalidate();
            getLoaderManager().destroyLoader(arg0.getId());
        }
    };

    public CustomLoadingEditFragment() {
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    private void clearBusStop() {
        loadingZones.clear();
        Spinner spinner = (Spinner) getView().findViewById(R.id.bus_stop_loading_spinner);
        spinner.invalidate();
        setBusStopData(null);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle bundle) {

        return inflater.inflate(R.layout.custom_loading_edit_fragment, group, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getLoaderManager().getLoader(0) != null) {
            getLoaderManager().initLoader(0, null, loaderSearchBusStopCallbacks);
        }
        if (getLoaderManager().getLoader(1) != null) {
            getLoaderManager().initLoader(1, null, loaderLoadingCallbacks);
        }
        ViewSwitcher viewSwitcher = (ViewSwitcher) getView().findViewById(R.id.rooting_busstop_text_view_swicher);
        viewSwitcher.setDisplayedChild(busStop == null ? 0 : 1);
        viewSwitcher = (ViewSwitcher) getView().findViewById(R.id.rooting_busstop_search_swicher);
        viewSwitcher.setDisplayedChild(busStop == null ? 0 : 1);

        EditText editText = (EditText) getView().findViewById(R.id.bus_stop_edit_text);
        editText.setText(busStop != null ? busStop.getBusStopName() : "");
        TextView textView = (TextView) getView().findViewById(R.id.bus_stop_text_view);
        textView.setText(busStop != null ? busStop.getBusStopName() : "");

        ArrayAdapter<LoadingZone> adapter = new ArrayAdapter<LoadingZone>(getActivity(),
                android.R.layout.simple_spinner_item, loadingZones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) getView().findViewById(R.id.bus_stop_loading_spinner);
        spinner.setAdapter(adapter);
    }

    public void onViewCreated(final View view, Bundle bundle) {
        view.findViewById(R.id.search_button).setOnClickListener(l);

        final Spinner spinner = (Spinner) view.findViewById(R.id.bus_stop_loading_spinner);
        final EditText editText = (EditText) view.findViewById(R.id.custom_loading_new_alias);
        view.findViewById(R.id.edit_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // reset

                clearBusStop();
            }

        });
        view.findViewById(R.id.bus_stop_edit_text).setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    if (KeyEvent.KEYCODE_SEARCH == keyCode || KeyEvent.KEYCODE_ENTER == keyCode) {
                        view.findViewById(R.id.search_button).performClick();
                        return true;
                    }
                }
                return false;
            }
        });

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editText.setText(((LoadingZone) parent.getAdapter().getItem(position)).toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO 自動生成されたメソッド・スタブ

            }
        });
        view.findViewById(R.id.custom_loading_send_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final Spinner spinner = (Spinner) getView().findViewById(R.id.bus_stop_loading_spinner);
                final EditText editText = (EditText) getView().findViewById(R.id.custom_loading_new_alias);

                String newAliasName = editText.getText().toString();

                LoadingZone selectedLoading = (LoadingZone) spinner.getSelectedItem();
                showThanksDialog(busStop, selectedLoading, newAliasName);

            }
        });
        Bundle bundle2 = getArguments();
        if (bundle2 != null) {
            setBusStopData((BusStop) bundle2.getSerializable(IntentValues.TRANSITION_BUS_STOP));
            loadingZones.add(busStop.getLoading());
        }
    }

    /*
     * (非 Javadoc)
     *
     * @see com.actionbarsherlock.app.SherlockFragment#onCreateOptionsMenu(com.
     * actionbarsherlock.view.Menu, com.actionbarsherlock.view.MenuInflater)
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem item = menu.add(Menu.NONE, MENU_SEND_BUTTON, Menu.NONE, "送信する");
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        item.setEnabled(busStop != null);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /*
     * (非 Javadoc)
     *
     * @see
     * com.actionbarsherlock.app.SherlockFragment#onOptionsItemSelected(com.
     * actionbarsherlock.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == MENU_SEND_BUTTON) {
            getView().findViewById(R.id.custom_loading_send_button).performClick();
        }
        return super.onOptionsItemSelected(item);
    }

    /*
     * (非 Javadoc)
     *
     * @see
     * jp.co.yuki2006.busmap.map.IFragmentToBusStop#selectBusStop(jp.co.yuki2006
     * .busmap.model.BusStop)
     */
    @Override
    public void selectBusStop(BusStop busStop) {
        this.busStop = busStop;

        Bundle bundle = new Bundle();
        bundle.putInt(IntentValues.TRANSITION_BUS_STOP_ID, busStop.getBusStopID());
        getLoaderManager().restartLoader(1, bundle, loaderLoadingCallbacks).forceLoad();
        setBusStopData(busStop);
        getLoaderManager().destroyLoader(0);
    }

    private void setBusStopData(BusStop busStop) {
        if (busStop == null) {
            ((EditText) getView().findViewById(R.id.bus_stop_edit_text)).setText(this.busStop.getBusStopName());

        } else {
            ((EditText) getView().findViewById(R.id.bus_stop_edit_text)).setText(busStop.getBusStopName());
        }
        getView().findViewById(R.id.custom_loading_send_button).setEnabled(busStop != null);

        ViewSwitcher viewSwitcher = (ViewSwitcher) getView().findViewById(R.id.rooting_busstop_text_view_swicher);
        viewSwitcher.setDisplayedChild(busStop == null ? 0 : 1);
        viewSwitcher = (ViewSwitcher) getView().findViewById(R.id.rooting_busstop_search_swicher);
        viewSwitcher.setDisplayedChild(busStop == null ? 0 : 1);

        TextView textView = (TextView) getView().findViewById(R.id.bus_stop_text_view);
        textView.setText(busStop != null ? busStop.getBusStopName() : "");
        this.busStop = busStop;
        getSherlockActivity().invalidateOptionsMenu();
    }

    public void showThanksDialog(BusStop busStop, LoadingZone loadingZone, String newAliasName) {
        IWebPostRunnable<Boolean> postRunnable = new IWebPostRunnable<Boolean>() {
            @Override
            public void onPostRunnable(Boolean result) {
                CustomAliasThanksDialogFragment dialogFragment = new CustomAliasThanksDialogFragment();
                dialogFragment.show(getFragmentManager(), "thanks");
            }
        };
        CustomAliasNameSetter customAliasNameSetter =
                new CustomAliasNameSetter(
                        getActivity(), busStop.getBusStopID(),
                        loadingZone.getLoadingID(),
                        postRunnable);
        customAliasNameSetter.execute(newAliasName);

    }
}
