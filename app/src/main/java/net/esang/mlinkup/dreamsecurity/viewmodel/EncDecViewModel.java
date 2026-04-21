package net.esang.mlinkup.dreamsecurity.viewmodel;

import androidx.lifecycle.ViewModel;

import com.dreamsecurity.common.exception.MagicXSignException;
import com.dreamsecurity.magicxsign.MagicXSign;
import com.dreamsecurity.magicxsign.MagicXSignType;
import com.dreamsecurity.magicxsign.model.AsymmetricKeyInfo;
import com.dreamsecurity.magicxsign.model.EnvelopeOption;
import com.dreamsecurity.magicxsign.model.SymmetricKeyInfo;

import java.util.List;

public class EncDecViewModel extends ViewModel {
    MagicXSign magicXSign;
    public EncDecViewModel (MagicXSign magicXSign) {
        this.magicXSign = magicXSign;
    }

    public String hash(MagicXSignType.ALGHash hashAlgorithm, byte[] plainText) throws MagicXSignException  {
        return magicXSign.generateHash(hashAlgorithm, plainText);
    }

    public SymmetricKeyInfo genSymmetricKey(MagicXSignType.ALGSymmetricKey symmetricKeyAlgorithm) throws MagicXSignException {
        return magicXSign.generateSymmetricKey(symmetricKeyAlgorithm);
    }

    public String encDataWithSymmetricKey(SymmetricKeyInfo symmetricKey, byte[] plainText) throws MagicXSignException {
        return magicXSign.encryptWithSymmetricKey(symmetricKey, plainText);
    }

    public byte[] decDataWithSymmetricKey(SymmetricKeyInfo symmetricKey, byte[] encryptedData) throws MagicXSignException {
        return magicXSign.decryptWithSymmetricKey(symmetricKey, encryptedData);
    }
    public AsymmetricKeyInfo genASymmetricKey(MagicXSignType.ALGAsymmetricKey aSymmetricKeyAlgorithm) throws MagicXSignException {
        return magicXSign.generateASymmetricKey(aSymmetricKeyAlgorithm);
    }

    public String encDataWithASymmetricKey(AsymmetricKeyInfo asymmetricKey, byte[] plainText) throws MagicXSignException {
        return magicXSign.encryptWithAsymmetricKey(asymmetricKey, plainText);
    }

    public byte[] decDataWithASymmetricKey(AsymmetricKeyInfo asymmetricKey, byte[] encryptedData) throws MagicXSignException {
        return magicXSign.decryptWithAsymmetricKey(asymmetricKey, encryptedData);
    }

    public String envelopedData(EnvelopeOption envelopeOption, MagicXSignType.ALGSymmetricKey symmetricKeyAlgorithm, List<String> recipient, byte[] plainText) throws MagicXSignException {
        return magicXSign.envelope(envelopeOption, symmetricKeyAlgorithm, recipient, plainText);
    }

    public byte[] develop(String kmCertificate, String kmPrivateKey, byte[] password, byte[] binEnvelopedData) throws MagicXSignException {
        return magicXSign.develop(kmCertificate, kmPrivateKey, password, binEnvelopedData);
    }
}
