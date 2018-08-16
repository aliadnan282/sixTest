package com.workout.sixpacksabs.view.fragment;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.workout.sixpacksabs.R;
import com.workout.sixpacksabs.databinding.ExitDailogAdBinding;
import com.workout.sixpacksabs.helper.AppUtils;
import com.workout.sixpacksabs.interfaces.CloseAppListener;
import com.workout.sixpacksabs.manager.AdsManager;

/**
 * Created by saqibmirza on 22/12/2016.
 */

public class ShowExistDialog implements View.OnClickListener {

    private Dialog dialog;
    private Context context;
    private ExitDailogAdBinding exitDialogAdBinding;

    CloseAppListener closeAppListener;

    public ShowExistDialog(Context context) {
        this.context = context;
        closeAppListener = (CloseAppListener) context;
        createDialog();
    }

    private void createDialog() {
        LayoutInflater myInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        exitDialogAdBinding = DataBindingUtil.inflate(myInflator, R.layout.exit_dailog_ad, null, false);

        if (Build.VERSION.SDK_INT >= 21) {
            dialog = new Dialog(context, android.R.style.Theme_Material_Light_Dialog_NoActionBar);
        } else {
            dialog = new Dialog(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        }

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(exitDialogAdBinding.getRoot());

        AdsManager.getInstance().showAdMobLargeBanner(exitDialogAdBinding.adView);

        exitDialogAdBinding.dialogRateUs.setOnClickListener(this);
        exitDialogAdBinding.dialogNo.setOnClickListener(this);
        exitDialogAdBinding.dialogYes.setOnClickListener(this);

    }

    public void showDialog() {

        dialog.show();
    }

    public void hideDialog() {
        dialog.hide();
    }

    public void setCancelable(boolean isCancelable) {
        dialog.setCancelable(isCancelable);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_rate_us:
                dialog.dismiss();
                context.startActivity(AppUtils.getLikeUsIntent());

                break;
            case R.id.dialog_no:
                dialog.dismiss();
                break;
            case R.id.dialog_yes:

                if (closeAppListener != null) {
                    dialog.dismiss();
                    dialog.cancel();
                    closeAppListener.closeApp();
                } else
                    dialog.dismiss();
                break;
        }
    }
}
