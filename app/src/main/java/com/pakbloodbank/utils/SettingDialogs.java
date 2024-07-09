package com.pakbloodbank.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;

import androidx.appcompat.app.AppCompatDelegate;

import com.pakbloodbank.R;


public class SettingDialogs {
    Context context;
    LayoutInflater layoutInflater;
    OnDialogSubmit submit;

    public interface OnDialogSubmit {
        void OnThemeSubmit(int i);
    }

    public SettingDialogs(Context context2, LayoutInflater layoutInflater2) {
        this.context = context2;
        this.layoutInflater = layoutInflater2;
        this.submit = (OnDialogSubmit) context2;
    }

    public void selectTheme(int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        builder.setTitle(this.context.getString(R.string.dialog_theme_title));
        int i2 = 0;
        String[] strArr = {this.context.getString(R.string.dialog_theme_light), this.context.getString(R.string.dialog_theme_dark), this.context.getString(R.string.dialog_theme_system), this.context.getString(R.string.dialog_theme_battery)};
        if (i != 1) {
            i2 = i != 2 ? i != 3 ? 2 : 3 : 1;
        }
        builder.setSingleChoiceItems(strArr, i2, new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                if (i == 0) {
                    submit.OnThemeSubmit(AppCompatDelegate.MODE_NIGHT_NO);
                } else if (i == 1) {
                    submit.OnThemeSubmit(AppCompatDelegate.MODE_NIGHT_YES);
                } else if (i != 3) {
                    submit.OnThemeSubmit(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                } else {
                    submit.OnThemeSubmit(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                }
            }
        });
        builder.create().show();
    }

//    public void whatsNew() {
//        AlertDialog dialog = new AlertDialog.Builder(this.context).create();
//        View inflate = this.layoutInflater.inflate(R.layout.dialog_info, null);
//        dialog.setView(inflate);
//        inflate.findViewById(R.id.button_no).setOnClickListener(new View.OnClickListener() {
//           @Override
//            public final void onClick(View view) {
//                dialog.dismiss();
//            }
//        });
//        inflate.findViewById(R.id.button_Yes).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public final void onClick(View view) {
//                context.startActivity(new Intent(context, BillingActivity.class));
//                dialog.dismiss();
//            }
//        });
//        dialog.setCanceledOnTouchOutside(true);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
//        dialog.show();
//    }

}
