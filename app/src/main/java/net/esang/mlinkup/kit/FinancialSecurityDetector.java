package net.esang.mlinkup.kit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Debug;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FinancialSecurityDetector {
    private static final String TAG = "FinSecDetect";

    private Context context;

    public FinancialSecurityDetector(Context ctx) {
        this.context = ctx.getApplicationContext();
    }

    // ------------------------- Developer / Debug checks -------------------------

    public boolean isDeveloperOptionsEnabled() {
        try {
            int v = Settings.Global.getInt(context.getContentResolver(), "development_settings_enabled", 0);
            return v == 1;
        } catch (Exception e) {
            return false;
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    public boolean isUsbDebuggingEnabled() {
        try {
            int v = Settings.Secure.getInt(context.getContentResolver(), "adb_enabled", 0);
            return v == 1;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isAppDebuggable() {
        return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

    public boolean isDebuggerConnected() {
        // Java-level check
        if (Debug.isDebuggerConnected() || Debug.waitingForDebugger()) return true;
        // Native-level check (JNI) - disabled as library is missing
        return false;
    }

    // private native boolean native_is_debugger_present();

    // ------------------------- USB physical connection (optional) -------------------------
    public boolean isUsbConnected() {
        // Try reading common sysfs path
        String[] paths = {
                "/sys/class/android_usb/android0/state",
                "/sys/class/android_usb/f_adb/state",
                "/sys/class/android_usb/state"
        };
        for (String p : paths) {
            try {
                File f = new File(p);
                if (!f.exists()) continue;
                BufferedReader br = new BufferedReader(new FileReader(f));
                String s = br.readLine();
                br.close();
                if (s == null) continue;
                s = s.trim().toUpperCase();
                if (s.contains("CONFIGURED") || s.contains("CONNECTED") || s.contains("ADB")) {
                    return true;
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    // ------------------------- Root detection -------------------------
    public boolean isDeviceRooted() {
        return checkRootMethod1() || checkRootMethod2() || checkRootMethod3();
    }

    private boolean checkRootMethod1() {
        String buildTags = Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private boolean checkRootMethod2() {
        String[] paths = {
                "/system/app/Superuser.apk",
                "/sbin/su",
                "/system/bin/su",
                "/system/xbin/su",
                "/data/local/xbin/su",
                "/data/local/bin/su",
                "/system/sd/xbin/su",
                "/system/bin/failsafe/su",
                "/data/local/su"
        };
        for (String path : paths) {
            try {
                if (new File(path).exists()) return true;
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    private boolean checkRootMethod3() {
        // try executing su
        Process p = null;
        try {
            p = Runtime.getRuntime().exec(new String[]{"/system/xbin/which", "su"});
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            if (in.readLine() != null) return true;
            return false;
        } catch (Exception e) {
            return false;
        } finally {
            if (p != null) p.destroy();
        }
    }

    // ------------------------- Emulator detection -------------------------
    public boolean isProbablyAnEmulator() {
        // Heuristics - combine many checks
        if (Build.FINGERPRINT != null && (Build.FINGERPRINT.startsWith("generic") || Build.FINGERPRINT.startsWith("unknown")))
            return true;
        String model = Build.MODEL;
        if (model != null && (model.contains("google_sdk") || model.contains("Emulator") || model.contains("Android SDK built for x86")))
            return true;
        String product = Build.PRODUCT;
        if (product != null && (product.contains("sdk_google") || product.contains("sdk") || product.contains("vbox")))
            return true;
        String manufacturer = Build.MANUFACTURER;
        if (manufacturer != null && (manufacturer.contains("Genymotion") || manufacturer.contains("unknown")))
            return true;
        // QEmu drivers
        try {
            BufferedReader br = new BufferedReader(new FileReader("/proc/tty/drivers"));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("goldfish")) {
                    br.close();
                    return true;
                }
            }
            br.close();
        } catch (Exception ignored) {
        }
        return false;
    }

    // ------------------------- Proxy detection -------------------------
    public boolean isProxySet() {
        try {
            String proxyAddress = System.getProperty("http.proxyHost");
            String proxyPort = System.getProperty("http.proxyPort");
            if (!TextUtils.isEmpty(proxyAddress)) return true;
            // Android API
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String globalProxy = Settings.Global.getString(context.getContentResolver(), "http_proxy");
                if (!TextUtils.isEmpty(globalProxy)) return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    // ------------------------- Xposed / Frida / Hook detection (best-effort) -------------------------
    public boolean isXposedPresent() {
        // Check common Xposed classes
        try {
            Class.forName("de.robv.android.xposed.XposedBridge");
            return true;
        } catch (ClassNotFoundException ignored) {
        }
        // Check for su or suspicious jar
        try {
            File f = new File("/system/framework/XposedBridge.jar");
            if (f.exists()) return true;
        } catch (Exception ignored) {
        }
        return false;
    }

    public boolean isFridaServerRunning() {
        // Look for frida-server process in /proc
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"/system/bin/ps", "-A"});
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                if (line.toLowerCase().contains("frida") || line.toLowerCase().contains("gadget")) {
                    return true;
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    // ------------------------- Full composite check -------------------------
    public SecurityReport runFullChecks() {
        SecurityReport r = new SecurityReport();
        r.developerOptions = isDeveloperOptionsEnabled();
        r.usbDebugging = isUsbDebuggingEnabled();
        r.appDebuggable = isAppDebuggable();
        r.debuggerConnected = isDebuggerConnected();
        r.usbConnected = isUsbConnected();
        r.rooted = isDeviceRooted();
        r.emulator = isProbablyAnEmulator();
        r.proxy = isProxySet();
        r.xposed = isXposedPresent();
        r.frida = isFridaServerRunning();
        return r;
    }

    public static class SecurityReport {
        public boolean developerOptions = false;
        public boolean usbDebugging = false;
        public boolean appDebuggable = false;
        public boolean debuggerConnected = false;
        public boolean usbConnected = false;
        public boolean rooted = false;
        public boolean emulator = false;
        public boolean proxy = false;
        public boolean xposed = false;
        public boolean frida = false;

        public boolean isAnySuspicious() {
            return developerOptions || usbDebugging || appDebuggable || debuggerConnected || usbConnected || rooted || emulator || proxy || xposed || frida;
        }

        @Override
        public String toString() {
            return "SecurityReport{" +
                    "developerOptions=" + developerOptions +
                    ", usbDebugging=" + usbDebugging +
                    ", appDebuggable=" + appDebuggable +
                    ", debuggerConnected=" + debuggerConnected +
                    ", usbConnected=" + usbConnected +
                    ", rooted=" + rooted +
                    ", emulator=" + emulator +
                    ", proxy=" + proxy +
                    ", xposed=" + xposed +
                    ", frida=" + frida +
                    '}';
        }
    }

    // ------------------------- Helper: show security dialog and open settings -------------------------
    public static void showBlockingSecurityDialog(final Activity activity, SecurityReport report) {
        AlertDialog.Builder b = new AlertDialog.Builder(activity);
        b.setTitle("보안 경고");
        String message = buildMessageForReport(report);
        b.setMessage(message);
        b.setCancelable(false);
        if (report.developerOptions) {
            b.setPositiveButton("개발자 설정 열기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    try {
                        // Open developer settings directly
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (intent.resolveActivity(activity.getPackageManager()) != null) {
                            activity.startActivity(intent);
                        } else {
                            // fallback: open general settings
                            activity.startActivity(new Intent(Settings.ACTION_SETTINGS));
                        }
                    } catch (Exception e) {
                        activity.startActivity(new Intent(Settings.ACTION_SETTINGS));
                    }
                    activity.finishAffinity();
                }
            });
        }
        b.setNegativeButton("앱 종료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                activity.finishAffinity();
            }
        });
        b.setNeutralButton("다시 검사", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Re-run checks after user claims to have disabled.
                FinancialSecurityDetector det = new FinancialSecurityDetector(activity);
                SecurityReport rr = det.runFullChecks();
                if (!rr.isAnySuspicious()) {
                    // allow app
                    dialogInterface.dismiss();
                } else {
                    // show again
                    showBlockingSecurityDialog(activity, rr);
                }
            }
        });
        b.show();
    }

    private static String buildMessageForReport(SecurityReport report) {
        List<String> items = new ArrayList<>();
        if (report.developerOptions) items.add("개발자 옵션이 켜져 있습니다.");
        if (report.usbDebugging) items.add("USB 디버깅이 활성화되어 있습니다.");
        if (report.appDebuggable) items.add("앱이 디버그 가능 모드로 빌드되어 있습니다.");
        if (report.debuggerConnected) items.add("디버거가 연결된 것으로 보입니다.");
        if (report.usbConnected) items.add("USB가 PC에 연결되어 있습니다.");
        if (report.rooted) items.add("기기가 루팅되어 있거나 루트 흔적이 있습니다.");
        if (report.emulator) items.add("에뮬레이터에서 실행되는 것으로 보입니다.");
        if (report.proxy) items.add("시스템 프록시가 설정되어 있습니다.");
        if (report.xposed) items.add("Xposed 또는 모듈이 감지되었습니다.");
        if (report.frida) items.add("Frida 관련 프로세스 또는 가젯이 감지되었습니다.");
        if (items.isEmpty()) return "보안 이상 없음.";
        StringBuilder sb = new StringBuilder();
        for (String s : items) {
            sb.append("• ").append(s).append('\n');
        }
        sb.append('\n').append("앱을 계속 사용하려면 위 항목들을 해제해 주세요.");
        return sb.toString();
    }
}