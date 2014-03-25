package jp.co.yuki2006.busmap.bustimeline;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import jp.co.yuki2006.busmap.R;
import jp.co.yuki2006.busmap.bustimeline.fragment.DateSelectDialogFragment;
import jp.co.yuki2006.busmap.bustimeline.fragment.DaySelectDialogFragment;
import jp.co.yuki2006.busmap.bustimeline.fragment.RemarkDialogFragment;
import jp.co.yuki2006.busmap.custom.CustomLoadingEditActivity;
import jp.co.yuki2006.busmap.db.MyBusStopDB;
import jp.co.yuki2006.busmap.direction.DirectionExpandAdapater;
import jp.co.yuki2006.busmap.etc.ActionBarUPWrapper;
import jp.co.yuki2006.busmap.etc.Etc;
import jp.co.yuki2006.busmap.etc.TransitionManager;
import jp.co.yuki2006.busmap.map.BusMapActivity;
import jp.co.yuki2006.busmap.pf.MainTimeLinePreferenceActivity;
import jp.co.yuki2006.busmap.store.BusStop;
import jp.co.yuki2006.busmap.store.DirectionData;
import jp.co.yuki2006.busmap.store.LoadingZone;
import jp.co.yuki2006.busmap.values.IntentValues;
import jp.co.yuki2006.busmap.web.IWebPostRunnable;

/**
 * 時刻表表示画面のActivityです。
 *
 * @author yuki
 */
public class TimeLineActivity extends BusTimeTableTimerBasisActivity implements
        IWebPostRunnable<BusTimeTableDataResult> {

    public static class CheckDate implements Serializable {
        public int month;
        public int date;
        public DateType day;
    }

    static class TimeLineActivityData {
        public TimeLineLoader loader;
        public BusStop busStop;
        public List<DirectionData> directionList;
        public BusTimeTableListAdapter timeLineExpandAdapter;
        public String[] remarks;
        public SpinnerAdapter loadingAdapter;
        public CheckDate checkDate;
        public long checkTime;
    }

    /**
     *
     */
    public static final String CURRENT_DATE = "CURRENT_DATE";
    private static final String DATE_FRAGMENT_TAG = "date";

    private static final String REMARK_FRAGMENT_TAG = "remark";

    private static final String DAY_FRAGMENT_TAG = "day";

    public static void setDayText(Activity activity, TextView textView, DateType day) {
        int colorResources = 0;
        switch (day) {
            case HOLIDAY:
                colorResources = R.color.bustimeline_sunday;
                textView.setBackgroundResource(R.drawable.timeline_diagram_bg_sunday);
                textView.setText("日・祝");
                break;
            case SATURDAY:
                colorResources = R.color.bustimeline_saturday;
                textView.setBackgroundResource(R.drawable.timeline_diagram_bg_saturday);
                textView.setText("土曜");
                break;
            case WEEKDAY:
                colorResources = R.color.bustimeline_weekday;
                textView.setBackgroundResource(R.drawable.timeline_diagram_bg_weekday);
                textView.setText("平日");
            default:
                break;
        }
        // 今日の場合は0なので
        if (colorResources != 0) {
            textView.setTextColor(activity.getResources().getColor(colorResources));
        }
    }

    /**
     * このクラスで使う不揮発的なデータクラスです。
     */
    TimeLineActivityData timeLineData;

    private TimeLineLoader loader;

    /**
     * 乗り場リストから 乗り場データを取得します。
     *
     * @return その乗り場のBusStopクラスを取得します。
     */
    private BusStop getLoadingBusStop() {
        final Spinner loadingList = (Spinner) findViewById(R.id.loadinglist);
        BusStop item = (BusStop) loadingList.getSelectedItem();
        // BusStop data = new BusStop(item.getPoint(),
        // timeLineData.busStop.getBusStopName(),
        // item.getRegion(), null, timeLineData.busStop.getBusStopID(), "");
        // data.setBusStopID(timeLineData.busStop.getBusStopID());

        // for (int i = 0; i < loadingList.getCount(); i++) {
        // BusStop tmp = (BusStop) loadingList.getAdapter().getItem(i);
        // if (tmp.getLoading().equals(timeLineData.busStop.getLoading())) {
        // data.setLoading(tmp.getLoading());
        // }
        // }

        return item;

    }

    public Spinner getLoadingList() {
        return (Spinner) findViewById(R.id.loadinglist);
    }

    public ExpandableListView getTimeLineExpand() {
        return (ExpandableListView) findViewById(R.id.timeline_expandable);
    }

    @Override
    protected void invalidate() {
        super.invalidate();
        final ExpandableListView timelineExpandView = (ExpandableListView) findViewById(R.id.timeline_expandable);
        timelineExpandView.invalidateViews();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        setTheme(R.style.Theme_Sherlock);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetable_layout);
        final ExpandableListView timelineExpandView = (ExpandableListView) findViewById(R.id.timeline_expandable);

        this.registerForContextMenu(timelineExpandView);
        final Spinner loadinglist = (Spinner) findViewById(R.id.loadinglist);
        // 備考の表示のリスナーの設定
        findViewById(R.id.show_remarks).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                RemarkDialogFragment fragment = new RemarkDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putStringArray(RemarkDialogFragment.REMARK_KEY, timeLineData.remarks);
                fragment.setArguments(bundle);
                fragment.show(getSupportFragmentManager(), REMARK_FRAGMENT_TAG);
            }
        });

        loadinglist.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, long id) {
                // 選択されたアイテムを取得します
                BusStop item = (BusStop) loadinglist.getSelectedItem();
                for (int groupPos = 0; groupPos < timelineExpandView.getCount(); groupPos++) {
                    timelineExpandView.collapseGroup(groupPos);
                }
                timeLineData.timeLineExpandAdapter.setLoadingFilter(item.getLoading().getLoadingID());

                timelineExpandView.invalidateViews();
                timeLineData.busStop.setLoading(item.getLoading());
                TimeLineActivity.this.invalidateOptionsMenu();

            }

            public void onNothingSelected(final AdapterView<?> arg0) {

            }
        });
        findViewById(R.id.timeline_day_textview).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DaySelectDialogFragment dayChangeDialogFragment = new DaySelectDialogFragment();
                dayChangeDialogFragment.show(getSupportFragmentManager(), DAY_FRAGMENT_TAG);
            }
        });

        timeLineData = (TimeLineActivityData) getLastCustomNonConfigurationInstance();
        // 画面回転前のデータない場合インテントから
        if (timeLineData == null) {
            timeLineData = new TimeLineActivityData();

            timeLineData.busStop = TransitionManager.getBusStopByIndent(this);
            timeLineData.checkDate = new CheckDate();
            timeLineData.checkDate.month = Calendar.getInstance().get(Calendar.MONTH);
            timeLineData.checkDate.date = Calendar.getInstance().get(Calendar.DATE);

            showBusStopData(DateType.TODAY);

        } else {
            // 画面回転前のデータがある場合それを利用
            loader = timeLineData.loader;
            loader.resetActivity(this);
            loader.resetPostRunnable(this);
            if (timeLineData.loadingAdapter != null) {
                setTimeLineExpandAdapter(timeLineData.timeLineExpandAdapter);
                getLoadingList().setAdapter(timeLineData.loadingAdapter);
            }
        }
        refleshView();
        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenuInfo menuInfo) {
        final ExpandableListContextMenuInfo contextmenu = (ExpandableListContextMenuInfo) menuInfo;
        if (!(v instanceof ListView)) {
            return;
        }
        ExpandableListView lv = (ExpandableListView) v;
        // メニューインフレーターを取得
        android.view.MenuInflater inflater = getMenuInflater();
        // xmlのリソースファイルを使用してメニューにアイテムを追加
        inflater.inflate(R.menu.timetable_list_contextmenu, menu);
        long packedPosition = contextmenu.packedPosition;
        int type = ExpandableListView.getPackedPositionType(packedPosition);
        if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
            int selectedIndex = ExpandableListView.getPackedPositionGroup(packedPosition);

            ExpandableListAdapter adapter = (lv.getExpandableListAdapter());
            final DirectionData obj = (DirectionData) adapter.getGroup(selectedIndex);

            android.view.MenuItem.OnMenuItemClickListener l = new android.view.MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(android.view.MenuItem menuitem) {
                    Etc.goToBusCool(TimeLineActivity.this,
                            timeLineData.busStop.getBusStopName(),
                            obj.destination);
                    return true;
                }
            };
            // menu.findItem(R.id.addmyroute).setVisible(true).setOnMenuItemClickListener(new
            // OnMenuItemClickListener() {
            // public boolean onMenuItemClick(final MenuItem item) {
            // MyDirectionDB db = new MyDirectionDB(TimeLineActivity.this);
            // db.addMyDirection(timeLineData.busStop, obj.destination, obj.via,
            // obj.getLoadingZone());
            // return true;
            // }
            // });
            menu.findItem(R.id.go_to_buscool).setVisible(true)
                    .setOnMenuItemClickListener(l);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // メニューインフレーターを取得
        MenuInflater inflater = getSupportMenuInflater();

        // xmlのリソースファイルを使用してメニューにアイテムを追加
        inflater.inflate(R.menu.timetable_view_optionmenu, menu);

        // 乗り場名の変更のボタンの無効
        Spinner loadingList = (Spinner) findViewById(R.id.loadinglist);
        BusStop busStop = (BusStop) loadingList.getSelectedItem();
        MenuItem gotoMapButton = menu.findItem(R.id.current_position);
        gotoMapButton.setEnabled(false);
        if (busStop != null) {
            menu.findItem(R.id.change_loading_alias_button).setEnabled(!"".equals(busStop.getLoading().getLoadingID()));
            if ("".equals(busStop.getLoading().getLoadingID())) {
                gotoMapButton.setEnabled(false);
                for (int i = 0; i < (loadingList).getCount(); i++) {
                    if (((BusStop) (loadingList).getItemAtPosition(i)).getPoint() != null) {
                        gotoMapButton.setEnabled(true);
                    }
                }
            } else {
                gotoMapButton.setEnabled(busStop.getPoint() != null);
            }

            MyBusStopDB db = new MyBusStopDB(this);
            boolean checkAlreadyData = db.checkAlreadyData(busStop);
            menu.findItem(R.id.add_my_bus_stop).setIcon(
                    checkAlreadyData ? android.R.drawable.star_big_on : android.R.drawable.star_big_off
            );
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (loader != null) {
            loader.onDestroy();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_my_bus_stop) {
            MyBusStopDB db = new MyBusStopDB(this);
            BusStop busStop = getLoadingBusStop();
            if (db.checkAlreadyData(busStop)) {
                db.removeBusStop(busStop);
            } else {
                Etc.addMyBusStop(this, getLoadingBusStop());
            }
            invalidateOptionsMenu();
        } else if (item.getItemId() == R.id.add_shortcut) {
            Etc.addBusStopShortCut(this, getLoadingBusStop());
        } else if (item.getItemId() == R.id.change_loading_alias_button) {

            final Spinner loadingList = (Spinner) findViewById(R.id.loadinglist);
            BusStop sendBusStop = (BusStop) loadingList.getSelectedItem();
            Intent intent = new Intent(this, CustomLoadingEditActivity.class);
            intent.putExtra(IntentValues.TRANSITION_BUS_STOP, sendBusStop);
            startActivity(intent);
            // 一旦削除して再生成
            // removeDialog(CHANGE_LOADING_ALIAS);
            // showDialog(CHANGE_LOADING_ALIAS);
        } else if (item.getItemId() == R.id.timetable_view_change_day) {
            DaySelectDialogFragment dayChangeDialogFragment = new DaySelectDialogFragment();
            dayChangeDialogFragment.show(getSupportFragmentManager(), DAY_FRAGMENT_TAG);
            // showDialog(CHANGE_DAY);
        } else if (item.getItemId() == R.id.timetable_show_pf) {
            Intent intent = new Intent(this, MainTimeLinePreferenceActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.current_position) {
            final Spinner loadinglist = (Spinner) findViewById(R.id.loadinglist);
            BusStop selectedBusStop = (BusStop) loadinglist.getSelectedItem();
            BusStop jumpToBusStop = null;

            if ("".equals(selectedBusStop.getLoading().getLoadingID())) {
                for (int i = 0; i < loadinglist.getCount(); i++) {
                    if (((BusStop) loadinglist.getItemAtPosition(i)).getPoint() != null) {
                        jumpToBusStop = (BusStop) loadinglist.getItemAtPosition(i);
                    }
                }
            } else {
                jumpToBusStop = (BusStop) loadinglist.getSelectedItem();

            }
            TransitionManager transitionManager = new TransitionManager(TimeLineActivity.this,
                    BusMapActivity.class, jumpToBusStop);

            startActivity(transitionManager);
        } else if (item.getItemId() == android.R.id.home) {
            ActionBarUPWrapper.doActionUpNavigation(this);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onPostRunnable(BusTimeTableDataResult result) {

        setTimeLineExpandAdapter(result.timelineExpandAdapter);

        // 2つ以上乗り場所があれば、すべての乗り場を追加
        if (result.loadingListAdapter.getCount() > 1) {
            BusStop allLoadingZone =
                    new BusStop(null, timeLineData.busStop.getBusStopName(), timeLineData.busStop.getRegion(),
                            new LoadingZone("", "すべての乗り場"), timeLineData.busStop.getBusStopID(), "");
            result.loadingListAdapter.insert(allLoadingZone, 0);
            timeLineData.busStop = new BusStop(result.loadingListAdapter.getItem(0), "");

        } else if (result.loadingListAdapter.getCount() == 0) {
            Toast.makeText(this, "この曜日でのバスがありませんでした。", Toast.LENGTH_LONG).show();
        }
        timeLineData.checkDate = result.checkDate;
        timeLineData.remarks = result.remarks;

        setLoadingZone(result.loadingListAdapter);

        refleshView();
        invalidateOptionsMenu();
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        timeLineData.timeLineExpandAdapter = (timeLineData.timeLineExpandAdapter);

        SpinnerAdapter loadingAdapter = ((Spinner) findViewById(R.id.loadinglist)).getAdapter();
        timeLineData.loadingAdapter = loadingAdapter;

        timeLineData.loader = loader;
        return timeLineData;
    }

    /**
     * ビューを更新します。
     *
     * @param result
     */
    private void refleshView() {
        // 曜日ごとにデザイン変えるとかで。
        if (timeLineData.checkDate.day != null) {
            TextView textView = (TextView) findViewById(R.id.timeline_day_textview);
            setDayText(this, textView, timeLineData.checkDate.day);
        }

        setTitle(timeLineData.busStop.getBusStopNameAndRegion());
    }

    @Override
    protected void reshowBusStopData(final int diffMinutesTime) {

        final int currentbusstopID = timeLineData.busStop.getBusStopID();
        // 最初の読み込みをしてなかったらパス
        DirectionExpandAdapater expandableListAdapter = (DirectionExpandAdapater) getTimeLineExpand()
                .getExpandableListAdapter();
        if (expandableListAdapter == null) {
            return;
        }

        // グループのサイズを取得しそれぞれ１分減らす
        // 0であるなら取得しに行く
        if (!downCountText(expandableListAdapter, diffMinutesTime)) {
            return;
        }

        final DirectionExpandAdapater timeLineExpandAdapter = timeLineData.timeLineExpandAdapter;
        IWebPostRunnable<Boolean> postRunnable = new IWebPostRunnable<Boolean>() {
            @Override
            public void onPostRunnable(Boolean bool) {
                final ExpandableListView timelineExpandView = (ExpandableListView) findViewById(R.id.timeline_expandable);
                timelineExpandView.invalidateViews();

            }
        };
        TimeLineReLoader loader = new TimeLineReLoader(this, postRunnable, timeLineExpandAdapter);

        loader.execute(currentbusstopID);

    }

    /**
     * 乗り場のアダプターを設定して、デフォルトの選択を設定します。
     *
     * @param loadingListAdapter
     */
    private void setLoadingZone(LoadingListAdapter loadingListAdapter) {
        Spinner loadingList = (Spinner) findViewById(R.id.loadinglist);
        loadingList.setAdapter(loadingListAdapter);
        loadingList.setSelection(0);

        if (timeLineData.busStop.getLoading().getLoadingID().length() > 0) {
            int size = loadingList.getAdapter().getCount();
            for (int i = 0; i < size; i++) {
                BusStop tmp = (BusStop) loadingList.getAdapter().getItem(i);
                if (tmp.getLoading().equals(timeLineData.busStop.getLoading())) {
                    loadingList.setSelection(i, true);
                    break;
                }
            }
        }
        if (loadingList.getAdapter().getCount() == 0) {
            loadingList.setEnabled(false);
        }
    }

    public void setTimeLineExpandAdapter(BusTimeTableListAdapter timeLineExpandAdapter) {

        ((ExpandableListView) findViewById(R.id.timeline_expandable)).setAdapter(timeLineExpandAdapter);

        this.timeLineData.timeLineExpandAdapter = timeLineExpandAdapter;

    }

    public void showBusStopData(final Calendar calendar) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        DialogFragment fragment = (DialogFragment) supportFragmentManager.findFragmentByTag(DATE_FRAGMENT_TAG);
        if (fragment != null) {
            fragment.dismiss();
        }
        timeLineData.checkDate.day = null;
        timeLineData.checkTime = calendar.getTimeInMillis() / 1000;
        loader = new TimeLineLoader(this, this, timeLineData.busStop.getBusStopID());
        loader.execute(timeLineData);
    }

    public void showBusStopData(DateType day) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        DialogFragment fragment = (DialogFragment) supportFragmentManager.findFragmentByTag(DAY_FRAGMENT_TAG);
        if (fragment != null) {
            fragment.dismiss();
        }
        timeLineData.checkDate.day = day;
        loader = new TimeLineLoader(this, this, timeLineData.busStop.getBusStopID());
        loader.execute(timeLineData);
    }

    /**
     *
     */
    public void showDateSelectDialog() {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        DateSelectDialogFragment fragment = new DateSelectDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(CURRENT_DATE, timeLineData.checkDate);
        fragment.setArguments(bundle);
        FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
        beginTransaction.add(fragment, DATE_FRAGMENT_TAG);
        beginTransaction.commit();
    }
}
