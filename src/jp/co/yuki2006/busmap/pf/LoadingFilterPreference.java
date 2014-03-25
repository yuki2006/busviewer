/**
 *
 */
package jp.co.yuki2006.busmap.pf;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Comparator;

import jp.co.yuki2006.busmap.R;

/**
 * @author yuki
 */
public class LoadingFilterPreference extends DialogPreference implements OnClickListener {

    static class LoadingFilterListAdapter extends ArrayAdapter<LoadingFilterListModel> implements
            Comparator<LoadingFilterListModel> {

        private LayoutInflater myInflater;

        public LoadingFilterListAdapter(Context context) {
            super(context, 0);

            myInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int compare(LoadingFilterListModel object1, LoadingFilterListModel object2) {
            return (object1.loadingNumberStr.compareTo(object2.loadingNumberStr));
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {

                convertView = myInflater.inflate(R.layout.preference_loading_filter_list, parent, false);

            }
            final LoadingFilterListModel item = getItem(position);

            ((TextView) convertView.findViewById(R.id.pref_loading_text)).
                    setText(item.loadingNumberStr);

            convertView.setTag(item);
            convertView.findViewById(R.id.pref_loading_delete).setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    remove(item);
                }
            });
            return convertView;
        }

    }

    static public class LoadingFilterListModel {
        public String loadingNumberStr;

        public LoadingFilterListModel() {
        }

        public LoadingFilterListModel(String format) {
            loadingNumberStr = format;

        }

    }

    private LoadingFilterListAdapter entries;

    public LoadingFilterPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.loading_filter_pref);
        entries = new LoadingFilterListAdapter(context);

    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindView(view);

        String preferenceValue = getSharedPreferences().getString(getKey(), "");
        entries.clear();
        if (preferenceValue.length() > 0) {
            for (String element : preferenceValue.split(" ")) {
                LoadingFilterListModel data = new LoadingFilterListModel();
                data.loadingNumberStr = element;
                entries.add(data);
            }
        }

        ListView loadingFilterList = (ListView) view.findViewById(R.id.loading_filter_list);
        loadingFilterList.setAdapter(entries);
        view.findViewById(R.id.pref_add_loading).setOnClickListener(this);

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            // データの保存
            StringBuilder value = new StringBuilder();
            if (entries.getCount() > 0) {
                for (int i = 0; i < entries.getCount() - 1; i++) {

                    value.append(entries.getItem(i).loadingNumberStr);
                    value.append(" ");
                }
                value.append(entries.getItem(entries.getCount() - 1).loadingNumberStr);
            }
            getSharedPreferences().edit().putString(getKey(), value.toString()).commit();
        }
        super.onClick(dialog, which);
    }

    public void onClick(final View v) {
        if (v.getId() == R.id.pref_add_loading) {
            TextView textView = (TextView) v.getRootView().findViewById(R.id.pref_add_loading_text);
            int loadingNumber = Integer.valueOf((String) (textView.getText().toString()));

            entries.add(new LoadingFilterListModel(String.format("%02d", loadingNumber)));
            entries.sort(entries);
            textView.setText("");

        }

    }

}
