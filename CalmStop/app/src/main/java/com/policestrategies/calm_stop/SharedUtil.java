package com.policestrategies.calm_stop;

import android.app.ProgressDialog;

/**
 * Shared helper functions.
 * @author Talal Abou Haiba
 */
public class SharedUtil {

    public static void dismissProgressDialog(ProgressDialog progressDialog) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

} // end class SharedUtil
