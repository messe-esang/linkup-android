package net.esang.mlinkup.dreamsecurity;

import android.content.Context;
import android.widget.Toast;


public class Common {

    public static final String MRS_SERVER_IP = "https://mdream.e-sang.net";
    public static final String MRS_SERVER_PORT = "30711";
    public static final String MRS_LICENSE_STRING = "MIH3BAE2BAVKQ0FPUwQXKOyjvCnrk5zrprzsi5ztgZDrpqzti7AEFkpUS1MtMDAwMDAtMjQxMDI0LTAyNzUwADAABAAEgYBBNjgyRkM0RjdGRkNENTY2RUExNTE1OERDQjFBMDNFREI2MzI3MUVGNzdDNkIyNDg1MDA2MzhFMzNFNjBBM0MyOTBFNjAxNUNDMjBENzcyRjAwMkE0NTNBQzg0QzFCRDY2RUEwNEIyMzM4MkExRUMxMzJBNEVCQkQxNEVCQUM2M6ICMACjLQQrY29tLmRyZWFtc2VjdXJpdHkudGVzdG1hZ2ljbXJzc2FtcGxlX2tvdGxpbg==";
    public static final String MAGICXSIGN_LICENSE =
            "MIHNBAEzBAVKQ0FPUwQW7KO87Iud7ZqM7IKsIO2UvOuFuO2FjQQWSlRLVC0wMDAwMC0yNjAxMTYtMDAxOQQABAAECjIwMjctMDItMTAEgYA2QTM2RDA5OTAzQ0E5MDQ4QTlENjg4RkFFMTA0ODkzQTk0MTM4MTIyRjI4RkQyQzdDMDg1QTA2NjY2MEE0MDY0NzMzRTk4RDc2MzM2MThERTM2N0M0RTU0RkVFRDI1N0YzQkIxQjAzRDdCQTAxNjk0RUJCN0RFNDJDM0E5RjEwMw==";

    public enum ToastType {
        NO_SELECTED_CERTIFICATE,
        NO_KM_CERTIFICATE,
        NO_LAST_SIGNED_DATA
    }

    // showToast 메서드 정의
    public static void showToast(Context context, ToastType toastType) {
        String message;

        // Kotlin의 when 구문을 Java의 switch 문으로 변환
        switch (toastType) {
            case NO_SELECTED_CERTIFICATE:
                message = "There is no selected certificate.";
                break;
            case NO_KM_CERTIFICATE:
                message = "There is no km certificate.";
                break;
            case NO_LAST_SIGNED_DATA:
                message = "There is no last signedData.";
                break;
            default:
                message = "Unknown error.";
        }

        // Toast 메시지 표시
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}
