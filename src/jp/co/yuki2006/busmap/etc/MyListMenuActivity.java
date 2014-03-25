/**
 *
 */
package jp.co.yuki2006.busmap.etc;

import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

import jp.co.yuki2006.busmap.ad.AdActivity;

/**
 * @author yuki
 */
public abstract class MyListMenuActivity extends AdActivity implements OnItemClickListener {
    private int selectedListPosition = 0;
    /**
     * ロングタップでメニューを出す場合を考え　基本はfalseで
     */
    private boolean isAutoSelected = false;

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // 通常のメニュー選択
        if (isAutoSelected == false) {
            onMenuClick(item.getItemId(), selectedListPosition);
        }
        isAutoSelected = false;
        return true;
    }

    /**
     * ListViewのコンテキストを表示される時にみでさらに、Menuのインフレーションが終了後に呼んでください。 {@inheritDoc}
     * これを呼んだ後にメニューが消える可能性アリ
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, final View v, ContextMenuInfo menuInfo) {
        final AdapterContextMenuInfo contextmenu = (AdapterContextMenuInfo) menuInfo;
        selectedListPosition = contextmenu.position;
        if (isAutoSelected) {
            for (int i = 0; i < menu.size(); i++) {
                MenuItem item = menu.getItem(i);
                if (item.isVisible() && item.isEnabled()) {
                    onMenuClick(item.getItemId(), selectedListPosition);
                    isAutoSelected = false;
                    break;
                }
            }
            // 全メニューを削除
            menu.clear();

        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (Etc.getListDefaultAction(this)) {
            isAutoSelected = false;
        } else {
            isAutoSelected = true;
        }
        parent.showContextMenuForChild(view);
    }

    public abstract void onMenuClick(int id, int selectedListPosition);
}
