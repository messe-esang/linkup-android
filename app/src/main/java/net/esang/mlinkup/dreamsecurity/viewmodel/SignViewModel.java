package net.esang.mlinkup.dreamsecurity.viewmodel;

import androidx.lifecycle.ViewModel;

import com.dreamsecurity.common.exception.MagicXSignException;
import com.dreamsecurity.magicxsign.MagicXSign;
import com.dreamsecurity.magicxsign.MagicXSignType;
import com.dreamsecurity.magicxsign.model.SignOption;

import java.util.Date;
import java.util.List;

public class SignViewModel extends ViewModel {
    private final MagicXSign magicXSign;

    public SignViewModel(MagicXSign magicXSign) {
        this.magicXSign = magicXSign;
    }

    public String sign(MagicXSignType.SignType signType, SignOption signOption, String signCertificate, String signPrivateKey, byte[] password, byte[] plainText, Date signDate) throws MagicXSignException {
        return magicXSign.sign(signType, signOption, signCertificate, signPrivateKey, password, plainText, signDate);
    }

    public String sign(MagicXSignType.SignType signType, SignOption signOption, byte[] plainText, Date signDate) throws MagicXSignException {
        return magicXSign.sign(signType, signOption, plainText, signDate);
    }

    public List<String> verifySign(MagicXSignType.SignType signType, SignOption signOption, String base64EncodedSignedData, String signCertificate, byte[] plainText) throws MagicXSignException {
        return magicXSign.verifySign(signType, signOption, base64EncodedSignedData, signCertificate, plainText);
    }

    public String addSigner(String signCertificate, String signPrivateKey, String signedData, byte[] password) throws MagicXSignException {
        return magicXSign.addSigner(signCertificate, signPrivateKey, password, signedData);
    }

}
