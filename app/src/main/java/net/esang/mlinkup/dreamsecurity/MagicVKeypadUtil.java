package net.esang.mlinkup.dreamsecurity;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.dreamsecurity.magicvkeypad.MagicClassCastException;
import com.dreamsecurity.magicvkeypad.MagicException;
import com.dreamsecurity.magicvkeypad.MagicNullPointerException;
import com.dreamsecurity.magicvkeypad.MagicVKeypad;
import com.dreamsecurity.magicvkeypad.MagicVKeypadType;
import com.dreamsecurity.magicvkeypad.MagicVKeypadUnInitializedException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class MagicVKeypadUtil {
    public static final String strLicense = "N9aSQvdukfyu7K3dRxNh5ETONR7xBYjEN95M5HfOtlHQTXLcZ/hUgGhEpFzHtC/D53NoU+tDNl+1y3klzgYfLj32dPiNfxbBtcAVdy0AQZ71e7z8ngCpcnrJkrszIz08rOGoe5gIe3ylinQqN7XSGzFSUv7ebg3PHY2bWwJx9aUHiYD9oloPNLDZH7m6P0GqhKyWIs1oCe/BapwzSAdqU9eGN8R3VxFE9oCLTSJn/Pfz42+gGYZtFiPUoI0KzY4kJH7Eqqzyyp/9RDqX7ZHZlW61ZLwySh0iK8aPwvSx6BgSrYJQByRh+MvkS+4P263qUn6Nt62yRnrIBUon+TIceQ==";
    public static final String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgXC3C/uZ6naR3xGB5wIu8eW0UHz1embCT4RLZc+/xwdAqy18vdjr7YhQA7eVMiSU4X/Q505Z0TC596mGsQgrjtVPrUvQVei3Zq/N/Lvqzvz+580TfNuhQr4TNZBIUs1GfYCrY1JqtRGsy+O3tixTju7Y50CjMUIJ/WhqtvkSUv5V7tmU+3B01nqMDoqoOqj9EBVKgLQojj1Y9mWt4oytl2o0xKGGO7jAQvHcGf6JIIZl1HwUtYJ32L55WB2DLU91BWeenhJGpOi0xUDTRwb/AHmZBiZtdYZFYgN68t0/D3jF/0Vvdpov4OKKDGii/gyY+2TF07o3R+6sloHr9bMOIwIDAQAB";
    public static final String e2eTestUrl = "http://10.10.30.130:8080/MagicKeypadServer/decryptKeypadRecord.jsp";

    // 라이센스 검증 및 E2E 공개키 설정
    public void settingKeypad(MagicVKeypad magicVKeypad, Context context) {
        // 키패드 라이센스 값
        try {
            int verifyLicenseResult = magicVKeypad.initializeMagicVKeypad(context, strLicense);

            if (verifyLicenseResult == MagicVKeypadType.MAGICVKEYPAD_TYPE_VERIFY_LICENSE_SUCCESS) {
                //Toast.makeText(context, "라이선스 검증 성공 (성공코드: " + verifyLicenseResult + ")", Toast.LENGTH_SHORT).show();
                // 검증 성공시 E2E 공개키 설정 (SettingsActivity의 useE2E 변수 접근)
                magicVKeypad.setE2EPublicKey(publicKey, false);
            } else {
                //Toast.makeText(context, "라이선스 검증 실패로 인한 사용 불가 (오류코드: " + verifyLicenseResult + ")", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            if (e instanceof MagicNullPointerException) {
                Log.d("TAG", "context is null\n" + Log.getStackTraceString(e));
            } else if (e instanceof MagicClassCastException) {
                Log.d("TAG", "wrong context\n" + Log.getStackTraceString(e));
            } else if (e instanceof MagicVKeypadUnInitializedException) {
                Log.d("TAG", "unInitialized " + Log.getStackTraceString(e));
            } else if (e instanceof MagicException) {
                Log.d("TAG", Log.getStackTraceString(e));
            }
        }
    }

    // E2E 데이터 POST 호출
    public void postE2EData(String data) {
        try {
            URL url = new URL(e2eTestUrl);
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("encKeypad", data);

            StringBuilder stringBuilder = new StringBuilder();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (stringBuilder.length() > 0) stringBuilder.append('&');
                stringBuilder.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                stringBuilder.append('=');
                stringBuilder.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
            }

            byte[] postData = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                postData = stringBuilder.toString().getBytes(StandardCharsets.UTF_8);
            }

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postData.length));
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(postData);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    String inputLine;
                    while ((inputLine = br.readLine()) != null) {
                        System.out.println(inputLine);
                    }
                }
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ByteArray 변환
    public String byteArray2Hex(byte[] input) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : input) {
            stringBuilder.append(String.format("%02x", (b & 0xff)));
        }
        return stringBuilder.toString();
    }
}