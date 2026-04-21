package net.esang.mlinkup.dreamsecurity.viewmodel;

import android.content.Context;

import androidx.lifecycle.ViewModel;
import com.dreamsecurity.magicxsign.MagicXSign;
import com.dreamsecurity.magicxsign.model.Certificate;
import com.dreamsecurity.magicxsign.model.CertificateFilter;

import net.esang.mlinkup.MyApplication;

import java.util.List;

public class CertificateSelectViewModel extends ViewModel {
    private final MagicXSign magicXSign;

    public CertificateSelectViewModel(MagicXSign magicXSign) {
        this.magicXSign = magicXSign;
    }

    public void getCertificateList(CertificateFilter certificateFilter, Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Simulate fetching the certificate list
                    List<Certificate> certificateList = magicXSign.getCertificateList(certificateFilter);

                    // Update the LiveData in MagicApplication
                    MyApplication app = (MyApplication) context.getApplicationContext();
                    app.getCertificateListStateLiveData().postValue(certificateList);  // Post to LiveData

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}