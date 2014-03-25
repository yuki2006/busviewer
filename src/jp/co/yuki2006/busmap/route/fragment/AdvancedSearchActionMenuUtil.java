/**
 *
 */
package jp.co.yuki2006.busmap.route.fragment;

import android.app.Activity;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import jp.co.yuki2006.busmap.R;
import jp.co.yuki2006.busmap.db.MyRouteDB;
import jp.co.yuki2006.busmap.etc.Etc;
import jp.co.yuki2006.busmap.route.store.RouteData;

/**
 * 詳細検索のMenu部分のみUIがないフラグメントです。
 *
 * @author yuki
 */
public class AdvancedSearchActionMenuUtil {


    public static void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {

        // メニューインフレーターを取得
        // xmlのリソースファイルを使用してメニューにアイテムを追加

        menuInflater.inflate(R.menu.advanced_search_view_menu, menu);
    }

    public static boolean onOptionsItemSelected(SherlockFragmentActivity sherlockActivity, MenuItem item, RouteData data) {
        switch (item.getItemId()) {
            case R.id.menu_add_myroot:
                MyRouteDB db = new MyRouteDB(sherlockActivity);
                if (db.insertOrDeleteData(data)) {
                    Toast.makeText(sherlockActivity, "マイルートに登録しました。", Toast.LENGTH_LONG).show();
                }
                sherlockActivity.invalidateOptionsMenu();
                db.close();
                return true;
            case R.id.go_to_buscool: {
                Etc.goToBusCool(sherlockActivity, data);

                break;
            }
        }
        return false;
    }

    /*
     * (非 Javadoc)
     *
     * @see com.actionbarsherlock.app.SherlockFragment#onPrepareOptionsMenu(com.
     * actionbarsherlock.view.Menu)
     */
    public static void onPrepareOptionsMenu(Activity activity, Menu menu, RouteData data) {

//		RouteData data = (RouteData) getArguments().getSerializable(ROUTE_DATA);
        MyRouteDB db = new MyRouteDB(activity);
        menu.findItem(R.id.menu_add_myroot).setIcon(
                db.isDataExist(data) ? android.R.drawable.star_big_on : android.R.drawable.star_big_off
        );
        db.close();
        boolean isVisible = data.getBusStop(true) != null && data.getBusStop(false) != null;
        menu.findItem(R.id.menu_add_myroot).setEnabled(isVisible);
        menu.findItem(R.id.go_to_buscool).setEnabled(isVisible);
    }
}
