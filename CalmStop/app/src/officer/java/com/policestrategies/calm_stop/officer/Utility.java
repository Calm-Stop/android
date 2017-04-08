package com.policestrategies.calm_stop.officer;

import android.app.ProgressDialog;
import android.content.Context;

import com.policestrategies.calm_stop.R;

/**
 * Helper functions.
 * @author Talal Abou Haiba
 */
public class Utility {

    public static String getCurrentDepartmentNumber(Context ctx) {
        return ctx.getSharedPreferences(ctx.getString(R.string.shared_preferences), Context.MODE_PRIVATE)
                .getString(ctx.getString(R.string.shared_preferences_department_number), "");
    }

    public static void dismissProgressDialog(ProgressDialog progressDialog) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}
