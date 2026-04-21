package net.esang.mlinkup.dreamsecurity;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.dreamsecurity.magicxsign.MagicXSign;

import net.esang.mlinkup.dreamsecurity.viewmodel.CMPViewModel;
import net.esang.mlinkup.dreamsecurity.viewmodel.CertificateSelectViewModel;
import net.esang.mlinkup.dreamsecurity.viewmodel.EncDecViewModel;
import net.esang.mlinkup.dreamsecurity.viewmodel.SignViewModel;
import net.esang.mlinkup.dreamsecurity.viewmodel.UtilViewModel;

public class MagicViewModelProvider implements ViewModelProvider.Factory {
    MagicXSign magicXSign;
    public MagicViewModelProvider(MagicXSign magicXSign){
        this.magicXSign = magicXSign;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(CertificateSelectViewModel.class)) {
            return (T) new CertificateSelectViewModel(magicXSign);
        } else if(modelClass.isAssignableFrom(CMPViewModel.class)) {
            return (T) new CMPViewModel(magicXSign);
        } else if(modelClass.isAssignableFrom(SignViewModel.class)) {
            return (T) new SignViewModel(magicXSign);
        } else if(modelClass.isAssignableFrom(EncDecViewModel.class)) {
            return (T) new EncDecViewModel(magicXSign);
        } else if(modelClass.isAssignableFrom(UtilViewModel.class)) {
            return (T) new UtilViewModel(magicXSign);
        } else {
            throw new RuntimeException("Wrong viewModel.");
        }
    }

}
