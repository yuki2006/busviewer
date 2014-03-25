package jp.co.yuki2006.busmap;

import android.content.Intent;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import java.util.List;

import jp.co.yuki2006.busmap.db.MyBusStopDB;
import jp.co.yuki2006.busmap.etc.BusListActivity;
import jp.co.yuki2006.busmap.store.BusStop;

/**
 * マイバス停のアクティビティです。
 *
 * @author yuki
 */
public class MyBusStopActivity extends BusListActivity {

    /**
     *
     */
    private static final String FRAGMENT_GUIDE_TAG = "guide";
    private boolean editMode = false;

    public void moveListPosition(final int oldPosition, final int newPosition) {
        MyBusStopDB db = new MyBusStopDB(this);
        db.truncateBusStop();
        for (int i = 0; i < arrayAdapter.getCount(); i++) {
            db.insertData(arrayAdapter.getItem(i));
        }
        db.close();
        arrayAdapter.notifyDataSetInvalidated();

    }

    /**
     * 現在のマイバス停の位置を変更します。
     *
     * @param position 現在の位置です。
     * @param isUp     上に上げるならtrueです。
     */
    public void moveListPostion(final int position, final boolean isUp) {
        if (isUp && position == 0 || !isUp
                && position == arrayAdapter.getCount() - 1) {
            return;
        }
        BusStop item = arrayAdapter.getItem(position);
        arrayAdapter.remove(item);
        arrayAdapter.insert(item, position + (isUp ? -1 : 1));
        moveListPosition(position, 0);

    }

    @Override
    public void onCreateContextMenu(final ContextMenu menu, final View v,
                                    final ContextMenuInfo menuInfo) {
        // 編集モードならメニューを出さない
        if (editMode) {
            return;
        }
        super.onCreateContextMenu(menu, v, menuInfo);
        // 消えている可能性があるので
        if (menu.size() > 0) {
            menu.findItem(R.id.bus_stop_delete).setVisible(true);
            menu.findItem(R.id.add_my_bus_stop).setVisible(false);
        }

    }

    /*
     * (非 Javadoc)
     *
     * @see jp.co.yuki2006.busmap.etc.BusListActivity#onMenuClick(int, int)
     */
    @Override
    public void onMenuClick(int id, int selectedListPosition) {
        final BusStop selectData = arrayAdapter.getItem(selectedListPosition);
        super.onMenuClick(id, selectedListPosition);
        if (id == R.id.bus_stop_delete) {
            removeBusStop(selectData);
            return;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.list_edit_mode_button) {
            editMode = !editMode;
            arrayAdapter.setEditMode(editMode);
        } else if (item.getItemId() == R.id.go_tutorial) {
            Intent intent = new Intent(this, MyBusStopGuideActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.findItem(R.id.list_edit_mode_button).setVisible(true);
        menu.findItem(R.id.go_tutorial).setVisible(true);
        return super.onPrepareOptionsMenu(menu);
    }

    /*
     * (非 Javadoc)
     *
     * @see jp.co.yuki2006.busmap.view.MobActivity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        MyBusStopDB db = new MyBusStopDB(this);
        List<BusStop> busstoplist = db.getBusStopData();

        arrayAdapter.clear();
        for (BusStop data : busstoplist) {
            arrayAdapter.add(data);
        }
        arrayAdapter.notifyDataSetInvalidated();
        db.close();
        if (arrayAdapter.getCount() == 0) {
            Intent intent = new Intent(this, MyBusStopGuideActivity.class);
            startActivity(intent);
            finish();
        }

    }

    public void removeBusStop(final BusStop deleteData) {
        arrayAdapter.remove(deleteData);
        MyBusStopDB db = new MyBusStopDB(this);
        db.removeBusStop(deleteData);
        db.close();
    }

}
