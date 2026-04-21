package net.esang.mlinkup;

import static net.esang.mlinkup.dreamsecurity.Common.MAGICXSIGN_LICENSE;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.webkit.WebView;

import androidx.lifecycle.MutableLiveData;

import com.dreamsecurity.common.exception.MagicXSignException;
import com.dreamsecurity.magicmrs.MagicMRS;
import com.dreamsecurity.magicxsign.MagicXSign;
import com.dreamsecurity.magicxsign.model.CMPOption;
import com.dreamsecurity.magicxsign.model.CMPOptionBuilder;
import com.dreamsecurity.magicxsign.model.Certificate;
import com.dreamsecurity.magicxsign.model.CertificateFilter;
import com.dreamsecurity.magicxsign.model.CertificateFilterBuilder;
import com.dreamsecurity.magicxsign.model.EnvelopeOption;
import com.dreamsecurity.magicxsign.model.EnvelopeOptionBuilder;
import com.dreamsecurity.magicxsign.model.SignOption;
import com.dreamsecurity.magicxsign.model.SignOptionBuilder;
import com.google.firebase.analytics.FirebaseAnalytics;

import net.esang.mlinkup.dreamsecurity.viewmodel.CMPViewModel;
import net.esang.mlinkup.dreamsecurity.viewmodel.CertificateSelectViewModel;
import net.esang.mlinkup.kit.Kit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyApplication extends Application {
    public static Context context;
    public static boolean AlertDialog_Check = false;

    public static FirebaseAnalytics mFirebaseAnalytics;
    public static String URL_Value = "";

    public static boolean SIDE_MENU_OPEN_CHECK = false;

    public static MagicXSign magicXSign;
    public static Certificate Certificateselected;
    public static CertificateSelectViewModel certificateSelectViewModel;
    public static CMPViewModel cmpViewModel;
    // MutableLiveData로 상태 관리
    private MutableLiveData<List<Certificate>> certificateListStateLiveData = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<Certificate> selectedCertificateStateLiveData = new MutableLiveData<>(null);
    private MutableLiveData<CertificateFilter> certificateFilterStateLiveData = new MutableLiveData<>(new CertificateFilterBuilder().build());
    private MutableLiveData<CMPOption> cmpOptionStateLiveData = new MutableLiveData<>(new CMPOptionBuilder().build());
    private MutableLiveData<EnvelopeOption> envelopedDataOptionStateLiveData = new MutableLiveData<>(new EnvelopeOptionBuilder().build());
    private MutableLiveData<SignOption> signOptionStateLiveData = new MutableLiveData<>(new SignOptionBuilder().build());
    private MutableLiveData<Date> signDateLiveData = new MutableLiveData<>(null);
    private MutableLiveData<String> lastSignedDataLiveData = new MutableLiveData<>(null);

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Kit.getOrCreateUUID(context);

        // MagicXSign 초기화
        magicXSign = new MagicXSign(this);
        try{
            magicXSign.setLicense(MAGICXSIGN_LICENSE);
        } catch (MagicXSignException e) {
            System.out.println("errorCode" + e.getErrorCode());
            System.out.println("errorMessage" + e.getErrorMessage());
        }

        // Android 9 (Pie) 이상에서 Multi-Process WebView 지원
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                WebView.setDataDirectorySuffix(Application.getProcessName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Context getAppContext() {
        return context;
    }

    /**
     * 애플리케이션 종료시 singleton 어플리케이션 객체 초기화한다.
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    // MagicXSign 객체에 대한 getter
    public MagicXSign getMagicXSign() {
        return magicXSign;
    }

    // LiveData의 getter들
    public MutableLiveData<List<Certificate>> getCertificateListStateLiveData() {
        return certificateListStateLiveData;
    }

    public MutableLiveData<Certificate> getSelectedCertificateStateLiveData() {
        return selectedCertificateStateLiveData;
    }

    public MutableLiveData<CertificateFilter> getCertificateFilterStateLiveData() {
        return certificateFilterStateLiveData;
    }

    public MutableLiveData<CMPOption> getCmpOptionStateLiveData() {
        return cmpOptionStateLiveData;
    }

    public MutableLiveData<EnvelopeOption> getEnvelopedDataOptionStateLiveData() {
        return envelopedDataOptionStateLiveData;
    }

    public MutableLiveData<SignOption> getSignOptionStateLiveData() {
        return signOptionStateLiveData;
    }

    public MutableLiveData<Date> getSignDateLiveData() {
        return signDateLiveData;
    }

    public MutableLiveData<String> getLastSignedDataLiveData() {
        return lastSignedDataLiveData;
    }
}
