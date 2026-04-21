package net.esang.mlinkup.dreamsecurity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import net.esang.mlinkup.R;
import net.esang.mlinkup.ui.BaseActivity;

public class MagicXSignMainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int themeColor = ContextCompat.getColor(this, R.color.white);
        setStatusColor(themeColor, true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magic_xsign_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false); // 기본 타이틀 제거
            actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼
            TextView title = toolbar.findViewById(R.id.tvTitle);
            title.setText("인증서 서명");
            toolbar.setNavigationIcon(R.drawable.ic_back);
        }

        String plainText = getIntent().getStringExtra("plainText");

        MagicXsignFragment fragment = MagicXsignFragment.newInstance(plainText);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // 또는 onBackPressed()
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}