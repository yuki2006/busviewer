/**
 *
 */
package jp.co.yuki2006.busmap.map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import com.actionbarsherlock.app.SherlockDialogFragment;

import java.io.IOException;
import java.util.Locale;

import jp.co.yuki2006.busmap.R;
import jp.co.yuki2006.busmap.values.PreferenceValues;

/**
 * @author yuki
 */
public class ConfirmDialogFragment extends SherlockDialogFragment {
    /*
     * (非 Javadoc)
     *
     * @see
     * android.support.v4.app.DialogFragment#onCreateDialog(android.os.Bundle)
     */
    @Override
    public Dialog onCreateDialog(Bundle arg0) {
        setCancelable(false);
        final SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        // 仮のデータを送ってみて　もしかしてつかえない場合は、データを送らない設定にする。
        Geocoder geocoder = new Geocoder(getActivity(), Locale.JAPAN);
        Builder notUsefulInformationDialog = new AlertDialog.Builder(getActivity());
        notUsefulInformationDialog.setTitle("お知らせ！!");
        notUsefulInformationDialog
                .setMessage("お使いの端末では、「施設や住所検索」機能に対応してない可能性があります。");
        notUsefulInformationDialog.setPositiveButton(android.R.string.ok, null);
        Builder confirmDialogBuilder = new AlertDialog.Builder(getActivity());
        try {
            if (geocoder.getFromLocationName("金沢市", 1).size() == 0) {
                return notUsefulInformationDialog.create();
            } else {
                final View inflate = LayoutInflater.from(getActivity()).inflate(
                        R.layout.map_view_address_search_confirm, null);
                confirmDialogBuilder.setView(inflate)
                        .setTitle("住所検索条件を送信しますか？")
                        .setPositiveButton(android.R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {
                                        CheckBox confirmCheckBox = (CheckBox) inflate
                                                .findViewById(R.id.search_confirm_check_box);
                                        boolean isChecked = confirmCheckBox
                                                .isChecked();
                                        Editor edit = sp.edit();
                                        // プリファレンスに書き込み
                                        edit.putBoolean(
                                                PreferenceValues.PF_SEND_LOCATION_NAME,
                                                isChecked);
                                        edit.commit();
                                        getActivity().onSearchRequested();
                                    }
                                });
            }
        } catch (IOException e) {
            e.printStackTrace();
            return notUsefulInformationDialog.create();
        }
        return confirmDialogBuilder.create();
    }
}
