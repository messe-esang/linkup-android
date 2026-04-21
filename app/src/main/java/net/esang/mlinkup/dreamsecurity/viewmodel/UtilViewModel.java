package net.esang.mlinkup.dreamsecurity.viewmodel;

import static com.dreamsecurity.magicxsign.MagicXSignUtilKt.decodeBase64;
import static com.dreamsecurity.magicxsign.MagicXSignUtilKt.encodeBase64;

import androidx.lifecycle.ViewModel;

import com.dreamsecurity.common.exception.MagicXSignException;
import com.dreamsecurity.magicxsign.MagicXSign;
import com.dreamsecurity.magicxsign.MagicXSignType;
import com.dreamsecurity.magicxsign.model.PathVerifyOption;

import java.util.List;

public class UtilViewModel extends ViewModel {
    private final MagicXSign magicXSign;
    public UtilViewModel(MagicXSign magicXSign){
        this.magicXSign = magicXSign;
    }

    public boolean verifyCertificatePath(PathVerifyOption certificateVerifyOption, List<String> caCertificateList, String targetCertificate) throws MagicXSignException  {
        return magicXSign.verifyCertificatePath(certificateVerifyOption, caCertificateList, targetCertificate);
    }

    public boolean verifyVID(String certificate, String privateKey, byte[] password, String idn) throws MagicXSignException {
        return magicXSign.verifyVID(certificate, privateKey, password, idn);
    }

    public byte[] getVIDRandom(String privateKey, byte[] password) throws MagicXSignException {
        return magicXSign.getVIDRandom(privateKey, password);
    }

    public String getVIDHashRandom(String certificate, String idn, byte[] vidRandom)throws MagicXSignException {
        return magicXSign.getVIDHashRandom(certificate, idn, vidRandom);
    }

    public String genUCPIDRequestInfo(String agreementStr, boolean name, boolean gender, boolean nationality, boolean birth, boolean cIInformation, byte[] receivedNonce, String serviceUrl) throws MagicXSignException {
        return magicXSign.createUCPIDRequestInfo(agreementStr, name, gender, nationality, birth, cIInformation, receivedNonce, serviceUrl);
    }

    public byte[] genSecureRandom(int dataLength) throws MagicXSignException {
        return magicXSign.generateSecureRandom(dataLength);
    }

    public String pemEncode(MagicXSignType.PEMEncodeType pemEncodeType, byte[] data) throws MagicXSignException {
        return magicXSign.encodePEM(pemEncodeType, data);
    }

    public byte[] pemDecode(byte[] data) throws MagicXSignException {
        return magicXSign.decodePEM(data);
    }

    public String base64Encode(byte[] data) {
        return encodeBase64(data);
    }

    public byte[] base64Decode(String data) {
        return decodeBase64(data);
    }}
