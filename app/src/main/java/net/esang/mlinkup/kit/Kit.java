/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.esang.mlinkup.kit;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebStorage;
import android.webkit.WebView;

import androidx.appcompat.app.AlertDialog;

import net.esang.mlinkup.R;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Kit {
    public static final String TAG = "mlinkup";

    public static boolean isNetworkConnected(Context context) {
        try {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null) {
                if (connectivityManager.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTING) {
                    return true;
                } else if (connectivityManager.getActiveNetworkInfo().getState() == NetworkInfo.State.DISCONNECTED ||
                        connectivityManager.getActiveNetworkInfo().getState() == NetworkInfo.State.DISCONNECTING) {
                    return false;
                }
                return activeNetworkInfo.isConnected();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Map<String, String> getQueryMap(String query) {
        return getQueryMap(query, "&");
    }

    public static Map<String, String> getQueryMap(String query, String seperator) {
        if (query == null)
            return null;

        String[] params = query.split(seperator);
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params) {
            String name = param.split("=")[0];
            String value = "";
            if (param.endsWith("=") == false) {
                value = param.split("=")[1];
            }
            map.put(name, value);
        }
        return map;
    }

    public static String getPackageVersionName(Context context) {
        String name = "";

        // 패키지 정보
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            name = packageInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return name;
    }

    public static String getAppName(Context context) {
        return context.getResources().getString(R.string.app_name);
    }

    public static boolean checkAvailableStorage(long limitedSize) {
        try {
            long size = getInternalMemorySize();
            if (size >= 0 && size < limitedSize) {
                return false;
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

        return true;
    }

    public static long getInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        return availableBlocks * blockSize;
    }

    public static boolean isNotNullNotEmpty(String str) {
        return (str != null && !str.isEmpty());
    }

    public static void showAlertDialog(Context context, String title, String msg, String positiveButtonText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //alertBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(positiveButtonText,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    public static void showAlertDialog(Context context, String msg, String positiveButtonText) {
        showAlertDialog(context, getAppName(context), msg, positiveButtonText);
    }

    public static void execBrowser(Context context, String urlStr) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlStr));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {

        }
    }

    public static void execBrowserEx(Context context, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getOrCreateUUID(Context context) {
        String uuid = PrefKit.getUUID(context);
        Log.e(TAG, "getOrCreateUUID : " + uuid);
        if (uuid.isEmpty()) {
            uuid = UUID.randomUUID().toString().replace("-", "");
            PrefKit.setUUID(context, uuid);
            Log.e(TAG, "setUUID : " + uuid);
        }
    }

    public static String getPackageVersionCode(Context context) {
        int Code = 0;

        // 패키지 정보
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            Code = packageInfo.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return Integer.toString(Code);
    }

    public static String getPackageName(Context context) {
        String name = "";
        // 패키지 정보
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            name = packageInfo.packageName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }

    public static void openPlayStore(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(String.format("market://details?id=%s", getPackageName(context))));
        context.startActivity(intent);
    }

    public static void shareSMS(Context context, String shareContent) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.putExtra("sms_body", shareContent); // 보낼 문자
            intent.setType("vnd.android-dir/mms-sms");
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearWebViewData(WebView webView) {

        if (webView != null) {
            webView.clearCache(true);
            webView.clearHistory();
            webView.clearFormData();
            webView.destroy();
        }

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookies(null);
        cookieManager.flush();
        WebStorage.getInstance().deleteAllData();
    }
}