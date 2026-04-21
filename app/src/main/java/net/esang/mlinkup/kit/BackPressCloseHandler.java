package net.esang.mlinkup.kit;

import android.app.Activity;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import net.esang.mlinkup.R;


public class BackPressCloseHandler {
    private long backKeyPressedTime = 0;
    private Activity activity;
    private View mainLayout;

    public BackPressCloseHandler(Activity context, View layout) {
        this.activity = context;
        this.mainLayout = layout;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            activity.setResult(Activity.RESULT_CANCELED);
            activity.finishAffinity();
            //toast.cancel();
        }
    }

    public void showGuide() {
        Snackbar snackbar = Snackbar.make(mainLayout, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Snackbar.LENGTH_SHORT);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(activity, R.color.color_primary));
        snackbar.show();
    }
}
