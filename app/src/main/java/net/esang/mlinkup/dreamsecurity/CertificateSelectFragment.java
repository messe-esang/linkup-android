package net.esang.mlinkup.dreamsecurity;

import static androidx.lifecycle.AndroidViewModel_androidKt.getApplication;
import static com.dreamsecurity.magicxsign.MagicXSignUtilKt.encodeBase64;

import static net.esang.mlinkup.dreamsecurity.Common.MRS_SERVER_IP;
import static net.esang.mlinkup.dreamsecurity.Common.MRS_SERVER_PORT;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamsecurity.common.exception.MagicXSignException;
import com.dreamsecurity.magicmrs.MRSCertificate;
import com.dreamsecurity.magicmrs.MagicMRS;
import com.dreamsecurity.magicmrs.MagicMRSConfig;
import com.dreamsecurity.magicxsign.MagicXSign;
import com.dreamsecurity.magicxsign.model.Certificate;
import com.dreamsecurity.magicxsign.model.CertificateFilter;

import net.esang.mlinkup.MyApplication;
import net.esang.mlinkup.R;
import net.esang.mlinkup.dreamsecurity.adapter.CertificateListAdapter;
import net.esang.mlinkup.dreamsecurity.viewmodel.CMPViewModel;
import net.esang.mlinkup.dreamsecurity.viewmodel.CertificateSelectViewModel;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class CertificateSelectFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "CertificateSelectFragment";
    private CertificateListAdapter adapter;
    private MagicXSign magicXSign;
    private Certificate selectedCertificate;
    private CertificateSelectViewModel viewModel;
    private CMPViewModel cmpViewModel;

    private RecyclerView rvCertificate;
    private Button btAddTestCertificate;
    private Button btSelectCertificate;
    private Button btDeleteCertificate;
    private Button btAddQRCertificate;
    private Button btAddNumberCertificate;

    private TextView tvNoCertificate;
    private View vgSelectAndDelete;


    private MagicMRS magicMRS;
    // IP, PORT 설정 (환경에 맞게 반드시 변경해야 함)

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_certificate_select, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        magicXSign = ((MyApplication) requireActivity().getApplication()).getMagicXSign();
        viewModel = new ViewModelProvider(this, new MagicViewModelProvider(magicXSign)).get(CertificateSelectViewModel.class);
        cmpViewModel = new ViewModelProvider(this, new MagicViewModelProvider(magicXSign)).get(CMPViewModel.class);
        adapter = new CertificateListAdapter(certificate -> {
            this.selectedCertificate = certificate;
        });

        rvCertificate = view.findViewById(R.id.rvCertificate);
        btAddTestCertificate = view.findViewById(R.id.btAddTestCertificate);
        btSelectCertificate = view.findViewById(R.id.btSelectCertificate);
        btDeleteCertificate = view.findViewById(R.id.btDeleteCertificate);
        tvNoCertificate = view.findViewById(R.id.tvNoCertificate);
        vgSelectAndDelete = view.findViewById(R.id.vgSelectAndDelete);
        btAddQRCertificate = view.findViewById(R.id.btAddQRCertificate);
        btAddNumberCertificate = view.findViewById(R.id.btAddNumberCertificate);

        rvCertificate.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCertificate.setAdapter(adapter);
        btAddTestCertificate.setOnClickListener(this);
        btSelectCertificate.setOnClickListener(this);
        btDeleteCertificate.setOnClickListener(this);
        btAddQRCertificate.setOnClickListener(this);
        btAddNumberCertificate.setOnClickListener(this);

        magicMRSCallBack();
        observeCertificateList();
        observeCertificateFilter();

        loadCertificateList();
    }

    @Override
    public void onClick(View v) {
        try {
            if (v.getId() == R.id.btAddTestCertificate) {
                CertificateFilter currentFilter = ((MyApplication) requireActivity().getApplication()).getCertificateFilterStateLiveData().getValue();
                viewModel.getCertificateList(currentFilter, this.requireContext());
            } else if (v.getId() == R.id.btSelectCertificate) {
                if (selectedCertificate != null) {
                    ((MyApplication) requireActivity().getApplication()).getSelectedCertificateStateLiveData().setValue(selectedCertificate);
                    getParentFragmentManager().popBackStack();
                }
            } else if (v.getId() == R.id.btDeleteCertificate) {
                if (selectedCertificate != null) {
                    cmpViewModel.deleteCertificate(selectedCertificate);
                    CertificateFilter currentFilter = ((MyApplication) requireActivity().getApplication()).getCertificateFilterStateLiveData().getValue();
                    viewModel.getCertificateList(currentFilter, this.requireContext());
                    ((MyApplication) requireActivity().getApplication()).getSelectedCertificateStateLiveData().setValue(null);
                }
            } else if (v.getId() == R.id.btAddQRCertificate) {
                // QRCode 스캔으로 인증서 가져오기
                magicMRS.importCertificateWithQRCode(false, 60);
            } else if (v.getId() == R.id.btAddNumberCertificate) {
                // 인증번호로 인증서 가져오기
                magicMRS.importCertificateWithAuthCode(false);
            }
        } catch (MagicXSignException e) {
            System.out.println(e.getErrorCode());
            System.out.println(e.getErrorMessage());
        }
    }

    private void loadCertificateList() {
        CertificateFilter currentFilter = ((MyApplication) requireActivity().getApplication()).getCertificateFilterStateLiveData().getValue();
        viewModel.getCertificateList(currentFilter, requireContext());
    }

    private void observeCertificateFilter() {
        LiveData<CertificateFilter> certificateFilterLiveData = ((MyApplication) requireActivity().getApplication()).getCertificateFilterStateLiveData();
        certificateFilterLiveData.observe(getViewLifecycleOwner(), certificateFilter -> {
            viewModel.getCertificateList(certificateFilter, requireContext());
        });
    }

    private void observeCertificateList() {
        LiveData<List<Certificate>> certificateListLiveData = ((MyApplication) requireActivity().getApplication()).getCertificateListStateLiveData();
        certificateListLiveData.observe(getViewLifecycleOwner(), new Observer<List<Certificate>>() {
            @Override
            public void onChanged(List<Certificate> currentCertificateList) {
                if (currentCertificateList.isEmpty()) {
                    tvNoCertificate.setVisibility(View.VISIBLE);
                    btAddTestCertificate.setVisibility(View.VISIBLE);
                    rvCertificate.setVisibility(View.GONE);
                    vgSelectAndDelete.setVisibility(View.GONE);
                } else {
                    tvNoCertificate.setVisibility(View.GONE);
                    btAddTestCertificate.setVisibility(View.GONE);
                    rvCertificate.setVisibility(View.VISIBLE);
                    vgSelectAndDelete.setVisibility(View.VISIBLE);
                }
                adapter.submitList(currentCertificateList);
                selectedCertificate = null;
            }
        });
    }


    private void magicMRSCallBack() {
        // MagicMRS 초기화 및 콜백 설정
        magicMRS = new MagicMRS(requireContext(), (type, mrsResult, cert) -> {
            Log.d("UIActivity", TAG + "\ntype = " + type +
                    " \nresultCode = " + mrsResult.getErrorCode() +
                    " \nresultDesc = " + mrsResult.getErrorDescription());

            showToast(type + " | " + mrsResult.getErrorCode() + " | " + mrsResult.getErrorDescription());

            if (mrsResult.getErrorCode() == MagicMRSConfig.MAGICMRS_RESULT_SUCCESS) {
                if (type != MagicMRSConfig.MAGICMRS_TYPE_EXPORT) {
                    Log.d("UIActivity", "=====" + TAG + " Import Cert======");
                    if (cert != null) {
                        Log.d("UIActivity", "signCert\n" + (cert.getSignCert() != null ? Arrays.toString(cert.getSignCert()) : "null"));
                        Log.d("UIActivity", "signPri\n" + (cert.getSignPri() != null ? Arrays.toString(cert.getSignPri()) : "null"));
                        Log.d("UIActivity", "kmCert\n" + (cert.getKmCert() != null ? Arrays.toString(cert.getKmCert()) : "null"));
                        Log.d("UIActivity", "kmPri\n" + (cert.getKmPri() != null ? Arrays.toString(cert.getKmPri()) : "null"));
                        Log.d("UIActivity", "keyUsage: " + cert.getKeyUsage());

                        try {
                            String signCert = cert.getSignCert() != null ? encodeBase64(cert.getSignCert()) : null;
                            String signPri = cert.getSignPri() != null ? encodeBase64(cert.getSignPri()) : null;
                            String kmCert = cert.getKmCert() != null ? encodeBase64(cert.getKmCert()) : null;
                            String kmPri = cert.getKmPri() != null ? encodeBase64(cert.getKmPri()) : null;
                            Log.d("UIActivity", "signCert: " + signCert);
                            Log.d("UIActivity", "signPri: " + signPri);
                            Log.d("UIActivity", "kmCert: " + kmCert);
                            Log.d("UIActivity", "kmPri: " + kmPri);

                            cmpViewModel.saveCertificate(signCert, signPri, kmCert, kmPri);

                            CertificateFilter currentFilter = ((MyApplication) requireActivity().getApplication()).getCertificateFilterStateLiveData().getValue();
                            viewModel.getCertificateList(currentFilter, requireContext());
                        } catch (MagicXSignException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    Log.d("UIActivity", "===================================");
                }
            }
        });

        // 라이선스 검증(필수)
        magicMRS.initializeMagicMRS(Common.MRS_LICENSE_STRING);

        String version = magicMRS.getVersion();
        Log.d("UIActivity", TAG + " version: " + version);

        magicMRS.setURL(MRS_SERVER_IP, MRS_SERVER_PORT);

        // SSL 인증서 검사 결과 무시
        // magicMRS.setIgnoreTLSCert(true);

    }

    private void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}
