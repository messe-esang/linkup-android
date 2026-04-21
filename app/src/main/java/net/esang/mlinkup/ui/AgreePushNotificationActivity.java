package net.esang.mlinkup.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import net.esang.mlinkup.R;
import net.esang.mlinkup.data.Extra;
import net.esang.mlinkup.kit.PrefKit;

public class AgreePushNotificationActivity extends BaseActivity implements View.OnClickListener {
    private boolean mIsSaveAgreement = false;       // 동의 여부만 저장할지, false면 알림받기 터치시 설정 페이지로 이동
    private Button txtCancel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int themeColor = ContextCompat.getColor(this, R.color.white);
        setStatusColor(themeColor, true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agree_push_notification);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.agree_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtCancel = findViewById(R.id.txtCancel);

        findViewById(R.id.txtAgree).setOnClickListener(this);
        txtCancel.setOnClickListener(this);

        mIsSaveAgreement = getIntent().getBooleanExtra(Extra.KEY_SAVE_AGREEMENT, false);
        if (mIsSaveAgreement) {
            txtCancel.setText("취소");
        } else {
            txtCancel.setText("일주일간 보지 않기");
//            PrefKit.setOpenPushAgreeTime(this, System.currentTimeMillis());
        }

        getOnBackPressedDispatcher().addCallback(
                this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        // 아무 것도 하지 않음 = Back 무시
                    }
                }
        );
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.txtAgree) {
            if (mIsSaveAgreement) {
                PrefKit.setAgreePushNoti(this, true);
            } else {
                Intent intent = new Intent();
                intent.putExtra(Extra.KEY_ALLOW_PUSH_NOTIFICATION, true);

                setResult(RESULT_OK, intent);
            }
            finish();
        } else if (id == R.id.txtCancel) {
            if (mIsSaveAgreement == false) {
                PrefKit.setDoNotShowWeekTime(this, System.currentTimeMillis());
            }
            finish();
        }
    }
}
