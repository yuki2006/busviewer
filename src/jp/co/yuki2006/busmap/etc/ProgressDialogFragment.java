/**
 *
 */
package jp.co.yuki2006.busmap.etc;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import jp.co.yuki2006.busmap.R;

/**
 * @author yuki
 */
public class ProgressDialogFragment extends DialogFragment {
    private static final String DIALOG_FRAGMENT_TAG = "dialog2";

    public static void dismissHelperDialog(FragmentManager fm) {
        final ProgressDialogFragment dialogFragment = (ProgressDialogFragment) (fm)
                .findFragmentByTag(DIALOG_FRAGMENT_TAG);
        if (dialogFragment != null) {
            dialogFragment.onDismiss(dialogFragment.getDialog());

        }
    }

    public static void showHelperDialog(FragmentManager manager) {
        ProgressDialogFragment dialogFragment = (ProgressDialogFragment) manager
                .findFragmentByTag(DIALOG_FRAGMENT_TAG);
        if (dialogFragment != null) {
            FragmentTransaction beginTransaction = manager.beginTransaction();
            beginTransaction.remove(dialogFragment);
        }
        dialogFragment = new ProgressDialogFragment();
        dialogFragment.show(manager, DIALOG_FRAGMENT_TAG);
    }

    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getText(R.string.loading_data));
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        return dialog;
    }
}