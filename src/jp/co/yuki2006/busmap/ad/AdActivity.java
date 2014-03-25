package jp.co.yuki2006.busmap.ad;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.ads.AdRequest;
import com.google.ads.AdView;

import java.util.Random;

import jp.co.yuki2006.busmap.R;
import jp.co.yuki2006.busmap.etc.Etc;
import jp.co.yuki2006.busmap.web.Web;

public class AdActivity extends SherlockFragmentActivity {

    @Override
    protected void onResume() {
        super.onResume();
        insertMob(this, findViewById(android.R.id.content));
    }

    public static void insertMob(final Activity activity, View view) {
    	//stub
    }
}
