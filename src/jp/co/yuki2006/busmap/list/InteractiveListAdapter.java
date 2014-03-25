/**
 *
 */
package jp.co.yuki2006.busmap.list;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import jp.co.yuki2006.busmap.R;

/**
 * @author yuki
 */
public abstract class InteractiveListAdapter<T> extends ArrayAdapter<T>
        implements OnTouchListener, OnGestureListener {
    /**
     * Android 2.2以上のものだけ実行するように
     * 　それ未満はとりあえず保留。。
     *
     * @author yuki
     */
    public static class ListScroll {
        @TargetApi(8)
        public ListScroll(ListView listView, boolean isForward) {

            int distance = 15;
            if (isForward == false) {
                distance = -distance;
            }
            listView.smoothScrollBy(distance, 60);
        }
    }

    private final ViewGroup swipeLayout;
    private GestureDetector gestureDetector;
    private float startDownX;
    private ListView parent;
    private T swipingItem;
    private View swipingView;
    private LayoutInflater inflater;
    private boolean mEditMode = false;
    protected final int swipeViewLayout;
    /**
     * リストの挿入後のアイテムの位置です。
     */
    private int currentPosition = -1;

    /**
     * スワイプ中のアイテムの位置です。
     */
    private int selectedPosition = -1;

    /**
     * @param context
     * @param busStopNameTextView
     * @param swipeViewLayout
     * @param textViewResourceId
     */
    public InteractiveListAdapter(Context context, int swipeViewLayout, ViewGroup swipeLayout) {
        super(context, 0);
        this.swipeViewLayout = swipeViewLayout;
        this.swipeLayout = swipeLayout;
        gestureDetector = new GestureDetector(this);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (swipeLayout != null) {
            swipeLayout.setOnTouchListener(this);
        }
    }

    public InteractiveListAdapter(Context context, int swipeViewLayout, ViewGroup swipeLayout, List<T> objects) {
        super(context, 0, objects);
        this.swipeViewLayout = swipeViewLayout;
        this.swipeLayout = swipeLayout;
        gestureDetector = new GestureDetector(this);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (swipeLayout != null) {
            swipeLayout.setOnTouchListener(this);
        }
    }

    /**
     * 仮のリストを更新します。
     * DBの操作はしないという想定です。
     *
     * @param oldPosition
     * @param newPosition
     */
    protected void assumedChangeList(int oldPosition, int newPosition) {
        remove(getItem(oldPosition), true);
        insert(swipingItem, newPosition);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        this.parent = (ListView) parent;

        if (convertView == null) {
            convertView = inflater.inflate(swipeViewLayout, parent, false);
        }

        convertView.findViewById(R.id.list_handle).setVisibility(mEditMode == false ? View.GONE : View.VISIBLE);

        if (mEditMode == false) {
            // 編集モードでなければここで処理しない。
            return convertView;
        }
        parent.setOnTouchListener(this);
        if (position != currentPosition) {

            convertView.setVisibility(View.VISIBLE);

        } else {
            convertView.setVisibility(View.INVISIBLE);
        }

        // convertView.setTag(item);
        return convertView;
    }

    ;

    public boolean isEditMode() {
        return this.mEditMode;
    }

    /* Android 2.2未満のための対応 */
    private void listSmoothScroll(ListView listView, boolean isForward) {
        // Android 2.2以上のみ実行

        if (Build.VERSION.SDK_INT >= 8) {
            new ListScroll(listView, isForward);
        }
    }

    /**
     * 編集用のリストと、実際のリストが違う場合は、要素入れ替えの実装をしてください。
     *
     * @param oldPosition
     * @param newPosition
     */
    abstract protected void movePositionList(int oldPosition, int newPosition);

    @Override
    public boolean onDown(MotionEvent e) {
        // TODO 自動生成されたメソッド・スタブ

        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // TODO 自動生成されたメソッド・スタブ
        Log.d("bus", "press");
    }

    @TargetApi(11)
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        FrameLayout sv = (FrameLayout) swipeLayout.findViewById(R.id.swipe_view);
        int paddingSize = (int) (swipeLayout.getPaddingLeft() - distanceX);
        swipeLayout.setPadding(paddingSize,
                parent.getTop() + (int) e2.getY() - sv.getHeight() / 2, -paddingSize, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            float alpha = 1 - (float) Math.abs(swipeLayout.getPaddingLeft() / (parent.getWidth() / 2f));
            Log.d("a", swipeLayout.getPaddingLeft() + ":" + parent.getWidth() + ":" + alpha + "");
            if (alpha < 0)
                alpha = 0;
            swipeLayout.setAlpha(alpha);
        }
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    // リストのスワイプの実装をしてみたい！！！！
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        View itemView = null;
        if (mEditMode == false) {
            return false;
        }
        if (v.getId() == R.id.list_swipe_layout) {
            // 空選択だったら
            if (swipingView == null) {
                return true;
            }
            gestureDetector.onTouchEvent(event);
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                startDownX = event.getX();
                return false;
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                int childCount = parent.getAdapter().getCount();
                int selectedIndex = 0;
                for (int i = 0; i < childCount - parent.getFirstVisiblePosition() && i < parent.getChildCount(); i++) {
                    selectedIndex = parent.getFirstVisiblePosition() + i;
                    itemView = parent.getChildAt(i);
                    if (event.getY() < itemView.getBottom()) {
                        break;
                    }
                }

                if (currentPosition != selectedIndex) {
                    assumedChangeList(currentPosition, selectedIndex);

                    currentPosition = selectedIndex;
                }
                notifyDataSetChanged();
                // 表示されてないリストへのスクロール
                if (3 * parent.getHeight() <= 4 * event.getY()) {
                    // 下へスクロール
                    // 最後の要素の位置がリストの高さ以上の時
                    listSmoothScroll(parent, true);

                } else if (4 * (event.getY()) <= 1 * parent.getHeight()) {
                    // 上へスクロール
                    // 最初の要素の位置がマイナスの時
                    if (parent.getFirstVisiblePosition() > 0 || parent.getChildAt(0).getTop() < 0) {
                        listSmoothScroll(parent, false);
                    }
                }
                return false;

                //
            } else if (event.getAction() == MotionEvent.ACTION_UP) {

                // 半分以上動かしたら削除
                if (Math.abs(event.getX() - startDownX) > parent.getWidth() / 2L) {
                    removeListPosition(getPosition(swipingItem));
                    swipeLayout.setVisibility(View.INVISIBLE);
                    setNotifyOnChange(true);
                    swipingView = null;
                    currentPosition = -1;
                    return true;

                }
                movePositionList(selectedPosition, currentPosition);

                currentPosition = -1;
                swipeLayout.setVisibility(View.INVISIBLE);
                swipingView = null;
                notifyDataSetInvalidated();
            }
        } else {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 画面の1/2より右はスクロール領域
                // if (2 * event.getX() > parent.getWidth()) {
                // return false;
                // }
                int childCount = parent.getAdapter().getCount();
                int selectedIndex = -1;
                swipingItem = null;
                for (int i = 0; i < childCount - parent.getFirstVisiblePosition() && i < parent.getChildCount(); i++) {
                    selectedIndex = parent.getFirstVisiblePosition() + i;
                    itemView = parent.getChildAt(i);
                    if (event.getY() < itemView.getBottom()) {
                        swipingItem = getItem(selectedIndex);
                        break;
                    }
                }
                if (swipingItem == null) {
                    return true;
                }
                swipingView = getView(selectedIndex, null, parent);
                currentPosition = selectedIndex;
                selectedPosition = selectedIndex;
                swipeLayout.setVisibility(View.VISIBLE);
                // ((TextView)
                // itemView.findViewById(R.id.bus_stop_name)).setText("");
                // ((TextView)
                // swipeLayout.findViewById(R.id.list_handle)).setText("");

                FrameLayout sv = (FrameLayout) swipeLayout.findViewById(R.id.swipe_view);
                // Viewの消去
                sv.removeAllViews();
                sv.addView(swipingView);
                // 何故かパディングで対応

                swipeLayout.setPadding(0, parent.getTop() + (int) (event.getY()) - sv.getHeight() / 2, 0, 0);
                swipeLayout.invalidate();
                // swipeLayout.layout(0, 100, parent.getRight(),
                // parent.getBottom());
                // ((TextView)
                // v.findViewById(R.id.bus_stop_name)).setText(swipingItem.getTitle());
                notifyDataSetInvalidated();
                swipeLayout.dispatchTouchEvent(event);
                swipeLayout.requestFocus();
            } else {
                // 普通のイベント処理
                if (swipingItem == null) {
                    return false;
                }
                swipeLayout.dispatchTouchEvent(event);
                swipeLayout.requestFocus();
            }
        }
        return true;
    }

    /**
     * データベースからは削除しないremove
     *
     * @param object
     * @param isOnlyAdapter
     */
    public void remove(T object, boolean isOnlyAdapter) {
        super.remove(object);
    }

    abstract protected void removeListPosition(int oldPosition);

    public void setEditMode(boolean editMode) {
        this.mEditMode = editMode;
        if (getCount() == 0) {
            this.mEditMode = false;
            return;
        }

        if (this.mEditMode) {
            // 編集が有効になった時
            Toast.makeText(getContext(), "左右にスワイプさせると削除できます。", Toast.LENGTH_SHORT).show();
            // リストの編集設定
        }
        int y = 0;
        int position = 0;
        // 最初の呼び出しでは必ずnullでしかも処理の必要がない。
        if (parent != null) {
            position = parent.getFirstVisiblePosition();
            y = parent.getChildAt(0).getTop();
            notifyDataSetInvalidated();
            parent.setSelectionFromTop(position, y);
        }

    }
}
