/**
 *
 */
package jp.co.yuki2006.busmap.route;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.test.UiThreadTest;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import java.util.Timer;
import java.util.TimerTask;

import jp.co.yuki2006.busmap.R;
import jp.co.yuki2006.busmap.db.MyRouteDB;
import jp.co.yuki2006.busmap.etc.ActionBarUPWrapper;
import jp.co.yuki2006.busmap.etc.Etc;
import jp.co.yuki2006.busmap.etc.MyListMenuActivity;
import jp.co.yuki2006.busmap.etc.TransitionManager;
import jp.co.yuki2006.busmap.list.InteractiveListAdapter;
import jp.co.yuki2006.busmap.route.store.AdvancedSearchResult;
import jp.co.yuki2006.busmap.route.store.RouteData;
import jp.co.yuki2006.busmap.store.MyRouteBlockStore;
import jp.co.yuki2006.busmap.values.IntentValues;
import jp.co.yuki2006.busmap.web.IWebPostRunnable;

/**
 * @author yuki
 */
public class MyRouteActivity extends MyListMenuActivity implements
        OnItemClickListener {
    private MyRouteListAdapter adapter;

    private boolean editMode;
    private boolean notDataLoading = false;
    private Timer timer = new Timer();

    public static class MyRouteListAdapter extends
            InteractiveListAdapter<MyRouteBlockStore> {

        public MyRouteListAdapter(Context context, ViewGroup swipeLayout) {
            super(context, R.layout.my_root_list, swipeLayout);
        }

        /*
         * (非 Javadoc)
         *
         * @see jp.co.yuki2006.busmap.list.InteractiveListAdapter#getView(int,
         * android.view.View, android.view.ViewGroup)
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = super.getView(position, convertView, parent);
            MyRouteBlockStore item = getItem(position);
            TextView nextTimeTextView = (TextView) convertView
                    .findViewById(R.id.my_root_next_time_text);
            // リスト編集モードなら　消す
            // まだ読み込んでなければ非表示
            // それ以外は表示
            showRootBlock(getContext(), convertView, item.routeData);

            // nextTimeTextView.setVisibility(this.isEditMode() ? View.GONE :
            // item.nextMinutes == null ? View.INVISIBLE
            // : View.VISIBLE);

            nextTimeTextView.setText(item.toNextTimeString());
            return convertView;
        }

        /*
         * (非 Javadoc)
         *
         * @see
         * jp.co.yuki2006.busmap.list.InteractiveListAdapter#movePositionList
         * (int, int)
         */
        @Override
        protected void movePositionList(int oldPosition, int newPosition) {
            MyRouteDB db = new MyRouteDB(getContext());
            db.truncateMyRoot();
            int count = getCount();
            for (int position = 0; position < count; position++) {
                db.insertData(getItem(position).routeData);
            }
            db.close();
        }

        public void refleshList(boolean isLoadingNextTime) {
            MyRouteDB db = new MyRouteDB(getContext());
            clear();
            final RouteData[] myRootData = db.getMyRootData();
            for (RouteData element : myRootData) {
                MyRouteBlockStore myRouteBlockStore = new MyRouteBlockStore();
                myRouteBlockStore.routeData = element;
                add(myRouteBlockStore);
            }
            db.close();
            if (isLoadingNextTime == false || getCount() == 0) {
                return;
            }
            IWebPostRunnable<AdvancedSearchResult[]> postRunnable = new IWebPostRunnable<AdvancedSearchResult[]>() {
                @Override
                public void onPostRunnable(AdvancedSearchResult[] result) {

                    for (int i = 0; i < result.length; i++) {
                        getItem(i).setBusSearchList(result[i].busRouteElements);
                    }
                    MyRouteListAdapter.this.notifyDataSetChanged();

                    ((MyRouteActivity) getContext()).setNotDataLoading(true);
                    ((MyRouteActivity) getContext()).invalidateOptionsMenu();
                }
            };
            SearchLoader loader = new SearchLoader((Activity) getContext(),
                    false, postRunnable);
            loader.setSingleResult(true);
            loader.execute(myRootData);

        }

        /*
         * (非 Javadoc)
         *
         * @see android.widget.ArrayAdapter#remove(java.lang.Object)
         */
        @Override
        public void remove(MyRouteBlockStore object) {
            super.remove(object);
            MyRouteDB db = new MyRouteDB(getContext());
            db.remove(object.routeData);
            db.close();
        }

        /*
         * (非 Javadoc)
         *
         * @see
         * jp.co.yuki2006.busmap.list.InteractiveListAdapter#removeListPosition
         * (int)
         */
        @Override
        protected void removeListPosition(int oldPosition) {
            remove(getItem(oldPosition));
        }

        /*
         * (非 Javadoc)
         *
         * @see
         * jp.co.yuki2006.busmap.list.InteractiveListAdapter#setEditMode(boolean
         * )
         */
        @Override
        public void setEditMode(boolean editMode) {
            super.setEditMode(editMode);

        }

        /**
         * リスト全体を読込し直します。
         *
         * @return 再読み込みの必要が有る場合はtrue 、ない場合はfalseを返します。
         */
        public boolean setNow() {
            int count = getCount();
            boolean isLoad = false;
            for (int position = 0; position < count; position++) {
                BusSearchList busSearchList = getItem(position).getBusSearchList();
                if (busSearchList != null) {
                    busSearchList.refleshNextTime();
                    isLoad |= busSearchList.isShouldLoadNextData();
                }
            }
            return isLoad;
        }
    }

    public static void showRootBlock(Context context, View convertView,
                                     RouteData item) {
        for (int block : new int[]{R.id.advanced_search_departure_block,
                R.id.advanced_search_arrival_block}) {
            boolean isDepature = (block == R.id.advanced_search_departure_block);
            View departureBlock = convertView.findViewById(block);
            TextView label = (TextView) departureBlock
                    .findViewById(R.id.advanced_search_label);
            label.setBackgroundDrawable(context.getResources().getDrawable(
                    R.drawable.advanced_search_departure));
            label.setTextColor(context.getResources().getColor(
                    R.color.advanced_search_departure));

            label.setText(isDepature ? R.string.departure : R.string.arrive);

            TextView dTextView = (TextView) departureBlock
                    .findViewById(R.id.advanced_search_busstop);
            dTextView.setText(item.getBusStop(isDepature).toString());

        }
    }

    public boolean isNotDataLoading() {
        return notDataLoading;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Sherlock);
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.my_root_activity);
        ListView listView = (ListView) findViewById(R.id.my_root_activity_list);
        adapter = new MyRouteListAdapter(this,
                (ViewGroup) findViewById(R.id.list_swipe_layout));

        listView.setAdapter(adapter);
        listView.setClickable(true);
        listView.setOnItemClickListener(this);
        registerForContextMenu(listView);

        setProgressBarIndeterminateVisibility(false);

        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Runnable action = new Runnable() {
                    @Override
                    @UiThreadTest
                    public void run() {
                        Log.d("Bus", "My Route Reflesh");
                        if (adapter.setNow()) {
                            adapter.refleshList(true);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    }
                };
                MyRouteActivity.this.runOnUiThread(action);
            }
        };

        Etc.setMinutesIntervalTimer(timer, task);
    }

    private void goTutorial() {
        Intent intent = new Intent(this, MyRouteGuideActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, final View v,
                                    ContextMenuInfo menuInfo) {
        if (!(v instanceof ListView)) {
            return;
        }

        menu.setHeaderTitle(R.string.menu);
        MenuInflater infect = getMenuInflater();
        infect.inflate(R.menu.myroute_list_contextmenu, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        com.actionbarsherlock.view.MenuInflater supportMenuInflater = getSupportMenuInflater();
        supportMenuInflater.inflate(R.menu.my_root_option, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onMenuClick(int id, int selectedPosition) {
        switch (id) {
            case R.id.go_to_advanced_search:
                TransitionManager transitionManager = new TransitionManager(this,
                        AdvancedSearchResultActivity.class,
                        adapter.getItem(selectedPosition).routeData);

                transitionManager.putExtra(IntentValues.FROM_MY_ROOT, true);

                startActivity(transitionManager);
                break;
            case R.id.go_to_buscool: {
                Etc.goToBusCool(this, adapter.getItem(selectedPosition).routeData);
                break;
            }
            case R.id.remove_my_route:
                adapter.remove(adapter.getItem(selectedPosition));
                break;
            default:
                break;
        }
    }

    /*
     * (非 Javadoc)
     *
     * @see
     * com.actionbarsherlock.app.SherlockFragmentActivity#onOptionsItemSelected
     * (com.actionbarsherlock.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(
            com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                ActionBarUPWrapper.doActionUpNavigation(this);
                break;
            }
            case R.id.list_edit_mode_button: {

                editMode = !editMode;

                adapter.setEditMode(editMode);
                break;
            }
            case R.id.go_tutorial: {
                goTutorial();
                break;
            }
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem listEditButton = menu.findItem(R.id.list_edit_mode_button)
                .setEnabled(isNotDataLoading());
        listEditButton.setVisible(true);
        return super.onPrepareOptionsMenu(menu);
    }

    /*
     * (非 Javadoc)
     *
     * @see com.actionbarsherlock.app.SherlockFragmentActivity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    /*
     * (非 Javadoc)
     *
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        // 詳細検索でデータ追加した時の対策。
        adapter.refleshList(true);

        if (adapter.getCount() == 0) {
            goTutorial();
            // Toast.makeText(this, R.string.empty_my_root, Toast.LENGTH_LONG)
            // .show();
        } else {

        }
    }

    public void setNotDataLoading(boolean notDataLoading) {
        this.notDataLoading = notDataLoading;
    }

}
