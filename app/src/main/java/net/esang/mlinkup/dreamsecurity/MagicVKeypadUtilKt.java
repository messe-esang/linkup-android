package net.esang.mlinkup.dreamsecurity;

import android.content.res.Configuration;
import android.view.View;
import android.view.Window;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class MagicVKeypadUtilKt {

    public static void applyWindowInset(View view, Window window, Configuration configuration) {
        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(window, window.getDecorView());
        controller.setAppearanceLightStatusBars(true);
        controller.setAppearanceLightNavigationBars(true);

        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
                Insets statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars());
                Insets navBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
                v.setPadding(
                        statusBarInsets.left,
                        statusBarInsets.top,
                        statusBarInsets.right,
                        navBarInsets.bottom
                );
                return insets;
            });
        } else if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
                Insets statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars());
                Insets navBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
                Insets displayCutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout());
                v.setPadding(
                        Math.max(navBarInsets.left, displayCutout.left),
                        statusBarInsets.top,
                        Math.max(navBarInsets.right, displayCutout.right),
                        navBarInsets.bottom
                );
                return insets;
            });
        }
    }
}