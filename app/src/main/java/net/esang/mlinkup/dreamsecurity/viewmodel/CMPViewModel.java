package net.esang.mlinkup.dreamsecurity.viewmodel;

import androidx.lifecycle.ViewModel;

import com.dreamsecurity.common.exception.MagicXSignException;
import com.dreamsecurity.magicxsign.MagicXSign;
import com.dreamsecurity.magicxsign.MagicXSignType;
import com.dreamsecurity.magicxsign.model.AsymmetricKeyInfo;
import com.dreamsecurity.magicxsign.model.CMPOption;
import com.dreamsecurity.magicxsign.model.Certificate;
import com.dreamsecurity.magicxsign.model.CertificatePair;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CMPViewModel extends ViewModel {
    MagicXSign magicXSign;
    private final ExecutorService executorService;
    public CMPViewModel(MagicXSign magicXSign) {
        this.magicXSign = magicXSign;
        this.executorService = Executors.newSingleThreadExecutor();
    }
    public void saveCertificate(String signCertificate, String signPrivateKey, String kmCertificate, String kmPriKey) throws MagicXSignException {
        magicXSign.saveCertificate(signCertificate, signPrivateKey, kmCertificate, kmPriKey);
    }

    public void deleteCertificate(Certificate certificate) throws MagicXSignException {
        magicXSign.deleteCertificate(certificate);
    }

    public void changeCertificate(Certificate certificate, byte[] oldPassword, byte[] newPassword) throws MagicXSignException {
        magicXSign.changeCertificatePassword(certificate,oldPassword, newPassword);
    }

    public String exportCertificate(MagicXSignType.IOType exportType, Certificate certificate, byte[] password) throws MagicXSignException {
        return magicXSign.exportCertificate(exportType, certificate, password);
    }

    public CertificatePair importCertificate (MagicXSignType.IOType importType, byte[] data, byte[] password) throws MagicXSignException {
        return magicXSign.importCertificate(importType, data, password);
    }

    public String createCSR(AsymmetricKeyInfo asymmetricKey, MagicXSignType.ALGHash hashAlgorithm, String dn, String idn, byte[] nonce, String severKMCertificate) throws MagicXSignException {
        return magicXSign.createCSR(asymmetricKey, hashAlgorithm, dn, idn, nonce, severKMCertificate);
    }

    public Certificate issueCertificate(String caIP, int caPort, MagicXSignType.CAType caType, String authCode, String refNumber, byte[] password, CMPOption cmpOption) throws MagicXSignException {
        return magicXSign.issueCertificate(caIP, caPort, caType, authCode, refNumber, password, cmpOption);
    }

    public Certificate updateCertificate(String caIP, int caPort, MagicXSignType.CAType caType, Certificate certificate, byte[] password, CMPOption cmpOption) throws MagicXSignException {
        return magicXSign.updateCertificate(caIP, caPort, caType, certificate, password, cmpOption);
    }

    public Certificate revocationCertificate(String caIP, int caPort, MagicXSignType.CAType caType, Certificate certificate, byte[] password, CMPOption cmpOption) throws MagicXSignException {
        return magicXSign.revokeCertificate(caIP, caPort, caType, certificate, password, cmpOption);
    }
}
