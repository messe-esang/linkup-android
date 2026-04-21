package net.esang.mlinkup.kit;

import static net.esang.mlinkup.MyApplication.AlertDialog_Check;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TelKit {

    public static final String URL_API_BASE_PRD = "mlinkup.e-sang.net";   //운영
    //public static final String URL_API_BASE_PRD = "mlinkupdev.e-sang.net";    //개발
    public static final String URL_BASE_PRD = "https://" + URL_API_BASE_PRD;

    public static final String PATH_HOME = "/main/home.aspx";
    public static final String PATH_LOGIN = "/auth/userlogin.aspx";
    public static final String PATH_LOGOUT = "";
    public static final String PATH_MOBILECERT = "/mobileCert.aspx";
    public static final String PATH_DEVICE_SETTING = "/auth/device_settings.aspx";

    public static final String PATH_SEARCH = "/search"; // 검색
    public static final String PATH_MY_PAGE = "/mypage"; // 마이페이지
    public static final String PATH_LIVE = "/live/list"; // 라이브
    public static final String PATH_BRAND = "/brand/list"; // 브랜드

    public static final String PATH_AUTH_LOGIN = "/auth/login";
    public static final String PATH_MEMBER_FINDID = "/member/findid";
    public static final String PATH_MEMBER_FINDPW = "/member/findpw";
    public static final String PATH_MEMBER_JOIN = "/member/join";

    public static final String PATH_REQUEST_DEVICE_INFO = "/api/common/saveMemberDevice";
    public static final String PATH_DOMAIN_LIST = "/api/common/getAppAllowDomainList";

    public static String[] DOWN_DOMAIN_LIST = null;
    public static final String[] DOMAIN_LIST = {"mlinkupdev.e-sang.net", "mlinkup.e-sang.net", "bit.ly", "mobile-ok.com", "scert.mobile-ok.com"};

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private OnResultListener mOnResultListener = null;
    private OkHttpClient mHttpClient = null;
    private ProgressBar mProgressBar = null;
    private View mProgressView = null;
    private ProgressDialog mProgressDialog = null;
    private Context mContext = null;
    private LinearLayout mLinearLayout = null;

    public static class Result {
        public boolean mIsSucc = false;
        public String mRequestUrl = "";
        public String mResponse = "";
        public int mRequestCode = 0;
    }

    public interface OnResultListener {
        void onResult(Result result);
    }

    public TelKit(Context context, OnResultListener listener) {
        mContext = context;
        mOnResultListener = listener;
        initHttpClient();
    }

    public TelKit(Context context, OnResultListener listener, ProgressBar progressBar) {
        this(context, listener);
        mProgressBar = progressBar;
    }

    public TelKit(Context context, OnResultListener listener, View progressView) {
        this(context, listener);
        mProgressView = progressView;
    }

    public TelKit(Context context, OnResultListener listener, ProgressDialog progressDialog) {
        this(context, listener);
        mProgressDialog = progressDialog;
    }

    public TelKit(Context context, OnResultListener listener, LinearLayout linearLayout) {
        this(context, listener);
        mLinearLayout = linearLayout;
    }

    private void initHttpClient() {
        if (mHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(20, TimeUnit.SECONDS);
            builder.readTimeout(30, TimeUnit.SECONDS);
            builder.writeTimeout(30, TimeUnit.SECONDS);
            mHttpClient = builder.build();
        }
    }

    public void request(String url, HashMap<String, String> body) {
        request(url, body, 0);
    }

    public void request(String url, String body) {
        Map<String, String> bodyMap = parseQuery(body);
        request(url, new HashMap<>(bodyMap), 0);
    }

    private Map<String, String> parseQuery(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.trim().isEmpty()) return map;

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] parts = pair.split("=", 2);
            if (parts.length == 2) {
                map.put(parts[0], parts[1]);
            }
        }
        return map;
    }

    public void request(String url, HashMap<String, String> body, int requestCode) {
        if (!Kit.isNetworkConnected(mContext)) {
            showNetworkAlertDialog();
            return;
        }

        showProgress();

        executorService.submit(() -> {
            Result result = new Result();
            result.mIsSucc = false;
            result.mRequestUrl = url;
            result.mRequestCode = requestCode;

            try {
                FormBody.Builder formBuilder = new FormBody.Builder();
                if (body != null) {
                    for (Map.Entry<String, String> entry : body.entrySet()) {
                        formBuilder.add(entry.getKey(), entry.getValue());
                    }
                }

                RequestBody requestBody = formBuilder.build();
                Request.Builder requestBuilder = new Request.Builder();

                String fullUrl = URL_BASE_PRD + url;

                requestBuilder.url(fullUrl)
                        .post(requestBody);

                Response response = mHttpClient.newCall(requestBuilder.build()).execute();
                result.mResponse = response.body().string();
                result.mIsSucc = true;

            } catch (Exception e) {
                e.printStackTrace();
                result.mIsSucc = false;
            }

            mainHandler.post(() -> {
                hideProgress();
                if (mOnResultListener != null) {
                    mOnResultListener.onResult(result);
                }
            });
        });
    }

    private void showProgress() {
        mainHandler.post(() -> {
            if (mProgressBar != null) mProgressBar.setVisibility(View.VISIBLE);
            if (mProgressView != null) mProgressView.setVisibility(View.VISIBLE);
            if (mProgressDialog != null) mProgressDialog.show();
            if (mLinearLayout != null) mLinearLayout.setVisibility(View.VISIBLE);
        });
    }

    private void hideProgress() {
        mainHandler.post(() -> {
            if (mProgressBar != null) mProgressBar.setVisibility(View.INVISIBLE);
            if (mProgressView != null) mProgressView.setVisibility(View.INVISIBLE);
            if (mProgressDialog != null) mProgressDialog.dismiss();
            if (mLinearLayout != null) mLinearLayout.setVisibility(View.GONE);
        });
    }

    private void showNetworkAlertDialog() {
        mainHandler.post(() -> {
            if (!AlertDialog_Check) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("네트워크 연결상태를 확인해 주세요.")
                        .setCancelable(false)
                        .setPositiveButton("확인", (dialog, which) -> {
                            AlertDialog_Check = false;
                            dialog.dismiss();
                        });
                AlertDialog alertDialog = builder.create();
                if (!alertDialog.isShowing()) {
                    AlertDialog_Check = true;
                    alertDialog.show();
                }
            }
        });
    }

    public static void tokenRegistrationToServer(Context context, String mem_idx, String token) {

        if (!Kit.isNotNullNotEmpty(token))
            return;

        String uuid_id = "";
        try {
            uuid_id = PrefKit.getUUID(context);
            if (TextUtils.isEmpty(uuid_id)) {
                uuid_id = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String bodyStr = String.format("encrypted_member_id=%s&device=android&device_id=%s&token_id=%s",
                mem_idx,
                uuid_id,
                token);

        Log.e("TelKit", "tokenRegistrationToServer bodyStr : " + bodyStr);
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        if (!bodyStr.isEmpty()) {
            Map<String, String> map = Kit.getQueryMap(bodyStr);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                formBuilder.add(entry.getKey(), entry.getValue());
            }
        }
        RequestBody body = formBuilder.build();

        //request
        Request request = new Request.Builder()
                .url(URL_BASE_PRD + PATH_REQUEST_DEVICE_INFO)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
            }
        });
    }
}