package net.esang.mlinkup.ui;

import static androidx.core.content.ContextCompat.registerReceiver;
import static com.dreamsecurity.magicxsign.MagicXSignUtilKt.encodeBase64;
import static net.esang.mlinkup.MyApplication.certificateSelectViewModel;
import static net.esang.mlinkup.MyApplication.cmpViewModel;
import static net.esang.mlinkup.MyApplication.magicXSign;
import static net.esang.mlinkup.dreamsecurity.Common.MRS_SERVER_IP;
import static net.esang.mlinkup.dreamsecurity.Common.MRS_SERVER_PORT;
import static net.esang.mlinkup.kit.TelKit.PATH_HOME;
import static net.esang.mlinkup.kit.TelKit.URL_BASE_PRD;
import static net.esang.mlinkup.ui.MainWebViewFragment.mWebViewEx;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.net.Uri;

import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.dreamsecurity.common.exception.MagicXSignException;
import com.dreamsecurity.magicmrs.MagicMRS;
import com.dreamsecurity.magicmrs.MagicMRSConfig;
import com.dreamsecurity.magicxsign.model.CertificateFilter;
import com.google.firebase.FirebaseApp;

import net.esang.mlinkup.MyApplication;
import net.esang.mlinkup.R;
import net.esang.mlinkup.data.Extra;
import net.esang.mlinkup.dreamsecurity.Common;
import net.esang.mlinkup.dreamsecurity.MagicViewModelProvider;
import net.esang.mlinkup.dreamsecurity.viewmodel.CMPViewModel;
import net.esang.mlinkup.dreamsecurity.viewmodel.CertificateSelectViewModel;
import net.esang.mlinkup.kit.BackPressCloseHandler;
import net.esang.mlinkup.kit.Kit;
import net.esang.mlinkup.kit.PrefKit;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MainActivity extends BaseActivity {
    private static String TAG = MainActivity.class.getSimpleName();

    public static BackPressCloseHandler backPressCloseHandler;

    private MainWebViewFragment mMainWebViewFragment = null;

    public static final int REQUEST_CODE_INPUT_FILE = 3000;
    public static final int REQUEST_CODE_AGREE_PUSH_NOTIFICATION = 3001;
    public static final int REQUEST_CODE_CONTENT_NUMBER = 3002;

    public static RelativeLayout mLayoutRoot = null;


    public static MagicMRS magicMRS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int themeColor = ContextCompat.getColor(this, R.color.white);
        setStatusColor(themeColor, true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layoutRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mLayoutRoot = findViewById(R.id.layoutRoot);
        backPressCloseHandler = new BackPressCloseHandler(this, mLayoutRoot);

        FirebaseApp.initializeApp(this);

        // 푸시알림 동의 화면 띄울지 결정할 실행 횟수
        PrefKit.increaseExecCountForPushAgree(this);

        mMainWebViewFragment = new MainWebViewFragment();
        Bundle bundle = new Bundle();
        Intent intent = getIntent(); /*데이터 수신*/
        if (intent.getExtras() != null) {
            String url = intent.getExtras().getString("url");
            bundle.putString("URL", url);
        } else {
            bundle.putString("URL", URL_BASE_PRD + PATH_HOME);
        }
        mMainWebViewFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.layoutFragmentContainer, mMainWebViewFragment).commit();

        // 앱 첫 실행시 푸시알림 동의 화면.
//        if (PrefKit.getFirstExec(MainActivity.this)) {
//            PrefKit.setFirstExec(MainActivity.this, false);
//
//            if (PrefKit.getAgreePushNoti(MainActivity.this) == false) {
//                Intent it = new Intent(MainActivity.this, AgreePushNotificationActivity.class);
//                it.putExtra(Extra.KEY_SAVE_AGREEMENT, true);
//                startActivity(it);
//            }
//        }
        //getHashKey();
        Kit.clearWebViewData(mWebViewEx);
        certificateSelectViewModel = new ViewModelProvider(this, new MagicViewModelProvider(magicXSign)).get(CertificateSelectViewModel.class);
        cmpViewModel = new ViewModelProvider(this, new MagicViewModelProvider(magicXSign)).get(CMPViewModel.class);
//        if(mWebViewEx != null) {
//            mWebViewEx.setDownloadListener(new DownloadListener() {
//                @Override
//                public void onDownloadStart(String url, String userAgent,
//                                            String contentDisposition,
//                                            String mimetype,
//                                            long contentLength) {
//
//                    downloadFile(url, contentDisposition, mimetype);
//                }
//            });
//        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // 앱이 켜져있고 Pending Intent 를 통해 들어왔을 때 여기로 들어옴...
        checkAndOpenLink(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Kit.log(Kit.LogType.EVENT, "MainActivity::onActivityResult");
        Log.e(TAG, "onActivityResult::requestCode = " + requestCode);
        Log.e(TAG, "onActivityResult::resultCode = " + resultCode);
        Log.e(TAG, "onActivityResult::data = " + data);

        switch (requestCode) {
            case REQUEST_CODE_CONTENT_NUMBER:
                if (resultCode == RESULT_OK) {
                    Cursor cursor = null;
                    if (data != null) {
                        cursor = getContentResolver().query(data.getData(),
                                new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                                        ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);
                    }

                    if (cursor != null) {
                        cursor.moveToFirst();
                        String script = String.format("javascript:selectContact('%s', '%s');", cursor.getString(1), cursor.getString(0));
                        mMainWebViewFragment.getInstance().loadUrl(script);
                        cursor.close();
                    }
                }
                break;
            case REQUEST_CODE_INPUT_FILE:
                // WebViewEx webViewEx = getCurrentWebViewEx();
                WebViewEx webViewEx = mMainWebViewFragment.getInstance().mWebViewEx;
                if (webViewEx.onActivityResultForInputFile(requestCode, resultCode, data) == false) {
                    super.onActivityResult(requestCode, resultCode, data);
                    return;
                }
                break;
            case REQUEST_CODE_AGREE_PUSH_NOTIFICATION:
                if (resultCode == RESULT_OK) {
                    boolean allow = data.getBooleanExtra(Extra.KEY_ALLOW_PUSH_NOTIFICATION, false);
//                    Log.e(TAG, "onActivityResult::REQUEST_CODE_AGREE_PUSH_NOTIFICATION::allow = " + allow);
                    if (allow) {
                        mMainWebViewFragment.getInstance().loadUrl("javascript:push_setting('Y');");
                    }
                }
                break;
            default:
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void loadUrl(final String url) {
        Log.e(TAG, "MainActivity::loadUrl::url = " + url);

        if (mWebViewEx != null) {
            mWebViewEx.post(new Runnable() {
                @Override
                public void run() {
                    if (Kit.isNotNullNotEmpty(url)) {
                        mMainWebViewFragment.loadUrl(url);
                    } else {
                        mMainWebViewFragment.loadUrl(URL_BASE_PRD + PATH_HOME);
                    }
                }
            });
        }
    }

    public void checkAndOpenLink(Intent intent) {
        if (intent.getExtras() != null) {
            String link = intent.getStringExtra(Extra.KEY_LINK);
            String url = intent.getStringExtra(Extra.KEY_URL);
//        Log.e(TAG, "MainActivity::checkAndOpenLink::link = " + link);
            if (Kit.isNotNullNotEmpty(link)) {
                Uri uri = Uri.parse(link);
                String scheme = uri.getScheme();
                String host = "";
                String query = "";
                if (uri.getHost() != null) {
                    host = uri.getHost().toLowerCase();
                }
                if (uri.getQuery() != null) {
                    query = uri.getQuery();
                }
//            Log.e(TAG, "MainActivity::checkAndOpenLink::scheme = " + scheme);        // https
//            Log.e(TAG, "MainActivity::checkAndOpenLink::host = " + host);            // dev.cobemall.com
//            Log.e(TAG, "MainActivity::checkAndOpenLink::query = " + query);          // null
                if ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme)) {
                    if (WebViewEx.isAllowedHost(host)) {
                        loadUrl(link);
                    } else {
                        Kit.execBrowserEx(this, link);
                    }
                } else if (Kit.isNotNullNotEmpty(query)) {
                    Log.e("MainActivity", "link : " + link);
                    loadUrl(link);
                } else {
                    loadUrl(URL_BASE_PRD + PATH_HOME);
                }
            } else if (Kit.isNotNullNotEmpty(url)) {
                loadUrl(url);
            } else {
                loadUrl(URL_BASE_PRD + PATH_HOME);
            }
        }
    }

    private void getHashKey() {
        try {                                                        // 패키지이름을 입력해줍니다.
            PackageInfo info = getPackageManager().getPackageInfo("net.esang.mlinkup", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d(TAG, "key_hash=" + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void magicMRSCallBack() {
        // MagicMRS 초기화 및 콜백 설정
        magicMRS = new MagicMRS(this, (type, mrsResult, cert) -> {
            Log.d(TAG, TAG + "\ntype = " + type +
                    " \nresultCode = " + mrsResult.getErrorCode() +
                    " \nresultDesc = " + mrsResult.getErrorDescription());

            if (mrsResult.getErrorCode() == MagicMRSConfig.MAGICMRS_RESULT_SUCCESS) {
                if (type != MagicMRSConfig.MAGICMRS_TYPE_EXPORT) {
                    Log.d(TAG, "=====" + TAG + " Import Cert======");
                    if (cert != null) {
                        Log.d(TAG, "signCert\n" + (cert.getSignCert() != null ? Arrays.toString(cert.getSignCert()) : "null"));
                        Log.d(TAG, "signPri\n" + (cert.getSignPri() != null ? Arrays.toString(cert.getSignPri()) : "null"));
                        Log.d(TAG, "kmCert\n" + (cert.getKmCert() != null ? Arrays.toString(cert.getKmCert()) : "null"));
                        Log.d(TAG, "kmPri\n" + (cert.getKmPri() != null ? Arrays.toString(cert.getKmPri()) : "null"));
                        Log.d(TAG, "keyUsage: " + cert.getKeyUsage());

                        try {
                            String signCert = cert.getSignCert() != null ? encodeBase64(cert.getSignCert()) : null;
                            String signPri = cert.getSignPri() != null ? encodeBase64(cert.getSignPri()) : null;
                            String kmCert = cert.getKmCert() != null ? encodeBase64(cert.getKmCert()) : null;
                            String kmPri = cert.getKmPri() != null ? encodeBase64(cert.getKmPri()) : null;
                            Log.d(TAG, "signCert: " + signCert);
                            Log.d(TAG, "signPri: " + signPri);
                            Log.d(TAG, "kmCert: " + kmCert);
                            Log.d(TAG, "kmPri: " + kmPri);

                            cmpViewModel.saveCertificate(signCert, signPri, kmCert, kmPri);

                            CertificateFilter currentFilter = ((MyApplication) this.getApplication()).getCertificateFilterStateLiveData().getValue();
                            certificateSelectViewModel.getCertificateList(currentFilter, this);
                            Toast.makeText(this, "인증서 가져오기 : " + mrsResult.getErrorDescription(), Toast.LENGTH_SHORT).show();
                        } catch (MagicXSignException e) {
                            Toast.makeText(this, "인증서 가져오기 : " + mrsResult.getErrorDescription(), Toast.LENGTH_SHORT).show();
                            throw new RuntimeException(e);
                        }
                    }
                    Log.d(TAG, "===================================");
                }
            } else {
                Toast.makeText(this, "인증서 가져오기 : " + mrsResult.getErrorDescription(), Toast.LENGTH_SHORT).show();
            }
        });

        // 라이선스 검증(필수)
        magicMRS.initializeMagicMRS(Common.MRS_LICENSE_STRING);

        String version = magicMRS.getVersion();
        Log.d(TAG, TAG + " version: " + version);

        magicMRS.setURL(MRS_SERVER_IP, MRS_SERVER_PORT);

        // SSL 인증서 검사 결과 무시
        // magicMRS.setIgnoreTLSCert(true);

        magicMRS.setTextExportCertificate("title", "content");
        magicMRS.importCertificateWithAuthCode(false);
    }
}