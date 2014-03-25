package jp.co.yuki2006.busmap;

import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.yuki2006.busmap.etc.ActionBarUPWrapper;
import jp.co.yuki2006.busmap.etc.Etc;

public class AboutMeActivity extends SherlockFragmentActivity {
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Sherlock);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aboutme);
        StringBuilder sb = new StringBuilder();
        sb.append(Etc.getApplicationName(this));
        sb.append(" Ver");
        sb.append(Etc.getVersionName(this));
        sb.append(" @");
        sb.append(Etc.getMyID(this));
        TextView tv = (TextView) findViewById(R.id.aboutme_textview);
        tv.setText(sb.toString());

        ExpandableListView exListView = (ExpandableListView) findViewById(R.id.aboutme_expandable_list_view);
        List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
        List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();

        // 初期化
        Resources res = this.getResources();
        for (int resource : new int[]{R.raw.aboutme, R.raw.history}) {
            InputStream is = res.openRawResource(resource);
            InputStreamReader inputStreamReader = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(inputStreamReader);

            try {
                String str;
                StringBuffer sb2 = null;
                while ((str = br.readLine()) != null) {

                    if (str.matches("＜.*＞")) {
                        List<Map<String, String>> childObject =
                                new ArrayList<Map<String, String>>();

                        if (sb2 != null) {
                            Map<String, String> childNode = new HashMap<String, String>();
                            childNode.put("text", sb2.toString());
                            childObject.add(childNode);
                            childData.add(childObject);
                        }
                        HashMap<String, String> groupObject = new HashMap<String, String>();
                        groupObject.put("category", str.substring(1, str.length() - 1));
                        groupData.add(groupObject);

                        // 初期化
                        sb2 = new StringBuffer();
                    } else {

                        sb2.append(str + "\n");
                    }
                }
                List<Map<String, String>> childObject =
                        new ArrayList<Map<String, String>>();
                Map<String, String> childNode = new HashMap<String, String>();
                childNode.put("text", sb2.toString());
                childObject.add(childNode);
                childData.add(childObject);

                ExpandableListAdapter adapter = new SimpleExpandableListAdapter(
                        getApplication(),
                        groupData,
                        R.layout.aboutme_expandable,
                        new String[]{"category"}, // 親のMapで表示するデータを設定
                        new int[]{R.id.aboutme_expandable},
                        childData,
                        R.layout.aboutme_expandable_child,
                        new String[]{"text"}, // 子の表示データ
                        new int[]{R.id.aboutme_expandable}
                );
                exListView.setAdapter(adapter);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    br.close();
                    inputStreamReader.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
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
		if (item.getItemId() == android.R.id.home) {
			ActionBarUPWrapper.doActionUpNavigation(this);
		}

		return super.onOptionsItemSelected(item);
	}
}
