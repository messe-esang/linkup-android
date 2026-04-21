package net.esang.mlinkup.dreamsecurity;

import static com.dreamsecurity.magicxsign.MagicXSignUtilKt.encodeBase64;
import static net.esang.mlinkup.MyApplication.certificateSelectViewModel;
import static net.esang.mlinkup.dreamsecurity.Common.MRS_SERVER_IP;
import static net.esang.mlinkup.dreamsecurity.Common.MRS_SERVER_PORT;
import static net.esang.mlinkup.ui.MainWebViewFragment.mWebViewEx;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamsecurity.common.exception.MagicXSignException;

import com.dreamsecurity.magicmrs.MagicMRS;
import com.dreamsecurity.magicmrs.MagicMRSConfig;
import com.dreamsecurity.magicvkeypad.MagicVKeypad;
import com.dreamsecurity.magicvkeypad.MagicVKeypadClickListener;
import com.dreamsecurity.magicvkeypad.MagicVKeypadModel;
import com.dreamsecurity.magicvkeypad.MagicVKeypadResult;
import com.dreamsecurity.magicvkeypad.MagicVKeypadType;
import com.dreamsecurity.magicxsign.MagicXSignType;
import com.dreamsecurity.magicxsign.model.Certificate;
import com.dreamsecurity.magicxsign.model.CertificateDetail;
import com.dreamsecurity.magicxsign.model.CertificateFilter;
import com.dreamsecurity.magicxsign.model.CertificatePair;
import com.dreamsecurity.magicxsign.model.SignOption;

import net.esang.mlinkup.MyApplication;
import net.esang.mlinkup.R;
import net.esang.mlinkup.dreamsecurity.adapter.CertificateListAdapter;
import net.esang.mlinkup.dreamsecurity.adapter.FunctionListAdapter;
import net.esang.mlinkup.dreamsecurity.viewmodel.CMPViewModel;
import net.esang.mlinkup.dreamsecurity.viewmodel.CertificateSelectViewModel;
import net.esang.mlinkup.dreamsecurity.viewmodel.SignViewModel;
import net.esang.mlinkup.ui.MainActivity;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static net.esang.mlinkup.MyApplication.cmpViewModel;
import static net.esang.mlinkup.MyApplication.magicXSign;
import static net.esang.mlinkup.MyApplication.Certificateselected;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MagicXsignFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MagicXsignFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "MagicXsignFragment";
    //private MagicXSign magicXSign;
    private FunctionListAdapter adapter;
    private SignViewModel viewModel;
    private Button btSelect;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "plainText";

    // TODO: Rename and change types of parameters
    private String mPlainText = "";
    private RelativeLayout noSelectedCertificate;
    private View selectedCertificate;
    private View tvNoCertificate;

    private View vr;
    private View vrVerifySign;
    private TextView tvResult;
    private TextView tvVerifyResult;

    private RelativeLayout xsign_fragment_layout;
    private EditText edt_password;
    private Button btDoFunction;
    private Button btSendResult;

    public static final MagicVKeypadModel magicVKeypadModel = new MagicVKeypadModel();
    private final MagicVKeypad magicVKeypad = new MagicVKeypad(magicVKeypadModel);
    private final MagicVKeypadUtil magicVKeypadUtil = new MagicVKeypadUtil();

    private String rsaEncryptData = "";
    private String aesEncryptData = "";
    private String aesDecryptData = "";

    String Xsign_Result_Y_N = "N";
    String signCert = "";
    String signPri = "";

    private CertificateListAdapter certificateListAdapter;
    private CertificateSelectViewModel certificateSelectViewModel;

    private RecyclerView rvCertificate;
    private Button btAddQRCertificate;
    private Button btAddNumberCertificate;
    private View dimView;
    private ImageButton btn_poppup;
    private int ERROR_CODE = -1;
    private String ERROR_MESSAGE = "";
    public static MagicMRS magicMRS;

    public MagicXsignFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment MagicXsignFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MagicXsignFragment newInstance(String param1) {
        MagicXsignFragment fragment = new MagicXsignFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPlainText = getArguments().getString(ARG_PARAM1);
        }
//        MyApplication application = (MyApplication) requireActivity().getApplication();
//        magicXSign = application.getMagicXSign();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_magic_xsign, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        xsign_fragment_layout = view.findViewById(R.id.xsign_fragment_layout);
        MagicVKeypadUtilKt.applyWindowInset(xsign_fragment_layout.getRootView(), getActivity().getWindow(), getResources().getConfiguration());
        //magicXSign = ((MyApplication) requireActivity().getApplication()).getMagicXSign();
        viewModel = new ViewModelProvider(this, new MagicViewModelProvider(magicXSign)).get(SignViewModel.class);

        certificateSelectViewModel = new ViewModelProvider(this, new MagicViewModelProvider(magicXSign)).get(CertificateSelectViewModel.class);
        cmpViewModel = new ViewModelProvider(this, new MagicViewModelProvider(magicXSign)).get(CMPViewModel.class);
        certificateListAdapter = new CertificateListAdapter(certificate -> {
            if (certificate != null) {
                Log.e("CERT", "선택됨: " + certificate.getCertificatePair());
                ((MyApplication) requireActivity().getApplication()).getSelectedCertificateStateLiveData().setValue(certificate);
                certificateListAdapter.notifyDataSetChanged();
                startCharKeypad();
            }
            Certificateselected = certificate;
        });

        // 키패드 초기 설정
        magicVKeypadUtil.settingKeypad(magicVKeypad, getContext());
        magicVKeypadModel.setFullMode(false);
        magicVKeypadModel.setNumKeypadType(MagicVKeypadType.MAGICVKEYPAD_TYPE_HALF_NUM_DEFAULT);

        TextView tvVersion = view.findViewById(R.id.tvVersion);
        btSelect = view.findViewById(R.id.btSelect);
        edt_password = view.findViewById(R.id.edt_password);
        btDoFunction = view.findViewById(R.id.btDoFunction);
        btSendResult = view.findViewById(R.id.btn_send_result);
        vr = view.findViewById(R.id.vr);
        vrVerifySign = view.findViewById(R.id.vrVerifySign);
        tvResult = vr.findViewById(R.id.tvResult);
        tvVerifyResult = vrVerifySign.findViewById(R.id.tvResult);
        btAddQRCertificate = view.findViewById(R.id.btAddQRCertificate);
        btAddNumberCertificate = view.findViewById(R.id.btAddNumberCertificate);
        dimView = view.findViewById(R.id.dimView);
        btn_poppup = view.findViewById(R.id.btn_poppup);

        TextView tvTitle = vr.findViewById(R.id.tvTitle);
        TextView tvTitleVerifySign = vrVerifySign.findViewById(R.id.tvTitle);
        tvTitle.setText("결과");
        tvTitleVerifySign.setText("인증서 검증 결과");

        noSelectedCertificate = view.findViewById(R.id.noSelectedCertificate);
        selectedCertificate = view.findViewById(R.id.selectedCertificate);
        tvNoCertificate = view.findViewById(R.id.tvNoCertificate);
        vr = view.findViewById(R.id.vr);
        vrVerifySign = view.findViewById(R.id.vrVerifySign);

        TextView tvRealName = selectedCertificate.findViewById(R.id.tvRealName);
        TextView tvSubjectDN = selectedCertificate.findViewById(R.id.tvSubjectDN);
        AppCompatImageView ivNoSelectCertificate = noSelectedCertificate.findViewById(R.id.ivNoSelectCertificate);

        //AppCompatImageView ivtvNoCertificate = tvNoCertificate.findViewById(R.id.ivNoSelectCertificate);
        tvVersion.setText("MagicXSign Version: " + magicXSign.getVersion());


        rvCertificate = view.findViewById(R.id.rvCertificate);
        btAddQRCertificate = view.findViewById(R.id.btAddQRCertificate);
        btAddNumberCertificate = view.findViewById(R.id.btAddNumberCertificate);

        rvCertificate.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCertificate.setAdapter(certificateListAdapter);


        btSelect.setOnClickListener(this);
        edt_password.setOnClickListener(this);
        btDoFunction.setOnClickListener(this);
        btSendResult.setOnClickListener(this);
        btAddQRCertificate.setOnClickListener(this);
        btAddNumberCertificate.setOnClickListener(this);
        btn_poppup.setOnClickListener(this);

        observeCertificateList();
        observeCertificateFilter();

        loadCertificateList();

        MyApplication application = (MyApplication) requireActivity().getApplication();
        application.getSelectedCertificateStateLiveData().observe(getViewLifecycleOwner(), certificate -> {
            Log.e(TAG, "getSelectedCertificateStateLiveData");
            if (certificate != null) {
                Log.e(TAG, "certificate != null");
                CertificateDetail currentSelectedCertificateSignDetail = certificate.getSignDetail();
                selectedCertificate.setVisibility(View.GONE);
                selectedCertificate.findViewById(R.id.selectedLayout).setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black));
                tvRealName.setText(currentSelectedCertificateSignDetail.getRealName());
                tvRealName.setTextColor(Color.WHITE);
                tvSubjectDN.setText(currentSelectedCertificateSignDetail.getSubjectDN());
                tvSubjectDN.setTextColor(Color.WHITE);
                selectedCertificate.findViewById(R.id.tvExpire).setVisibility(View.GONE);
                noSelectedCertificate.setVisibility(View.GONE);
            } else {
                Log.e(TAG, "certificate == null");
                noSelectedCertificate.setVisibility(View.GONE);
                //noSelectedCertificate.setVisibility(View.GONE);
                //ivtvNoCertificate.setImageResource(R.drawable.menu_btn_06_nor);
                //ivNoSelectCertificate.setImageResource(R.drawable.menu_btn_06_nor);
                selectedCertificate.setVisibility(View.GONE);
            }
        });

        requireActivity()
                .getOnBackPressedDispatcher()
                .addCallback(
                        getViewLifecycleOwner(), // ⭐ 중요
                        new OnBackPressedCallback(true) {

                            @Override
                            public void handleOnBackPressed() {
                                // 여기서 뒤로가기 처리
                                FragmentManager fm = getParentFragmentManager();
                                if (magicVKeypad.isKeypadOpen()) {
                                    edt_password.setText("");
                                    certificateListAdapter.resetSelectedPosition();
                                    dimView.setVisibility(View.GONE);
                                    magicVKeypad.closeKeypad();
                                } else if (fm.getBackStackEntryCount() > 0) {
                                    fm.popBackStack();
                                } else {
                                    magicVKeypad.finalizeMagicVKeypad();
                                    requireActivity().finish();
                                }
                            }
                        });

        Log.e("CERT", "mPlainText: " + mPlainText);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btSelect) {
            //magicMRSCallBack();
            FragmentTransaction transaction =
                    getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainerView, new CertificateSelectFragment());
            transaction.addToBackStack(null); // 뒤로가기 지원
            transaction.commit();
        } else if (v.getId() == R.id.edt_password) {
            startCharKeypad();
        } else if (v.getId() == R.id.btDoFunction) {
            signCert = "";
            signPri = "";
            Xsign_Result_Y_N = "N";
            tvResult.setText("");
            tvVerifyResult.setText("");
            ERROR_CODE = -1;
            ERROR_MESSAGE = "";

            Certificate currentCertificate = ((MyApplication) requireActivity().getApplication()).getSelectedCertificateStateLiveData().getValue();
            SignOption currentSignOption = ((MyApplication) requireActivity().getApplication()).getSignOptionStateLiveData().getValue();
            Date signDate = ((MyApplication) requireActivity().getApplication()).getSignDateLiveData().getValue();
            MagicXSignType.SignType signType = MagicXSignType.SignType.SIGNATURE;

            //byte[] password = edt_password.getText().toString().getBytes();
            byte[] password = aesDecryptData.getBytes();
            byte[] plainText = mPlainText.getBytes();

            if (currentCertificate != null) {
                CertificatePair certificatePair = currentCertificate.getCertificatePair();
                String signResult = null;
                try {
                    signResult = viewModel.sign(signType, currentSignOption, certificatePair.getSignCertificate(), certificatePair.getSignPrivateKey(), password, plainText, signDate);
                    tvResult.setText(signResult);
                    Xsign_Result_Y_N = "Y";
                    signCert = certificatePair.getSignCertificate();
                    signPri = certificatePair.getSignPrivateKey();
                } catch (MagicXSignException magicXSignException) {
                    Xsign_Result_Y_N = "N";
                    tvResult.setText(magicXSignException.getErrorMessage());
                    System.out.println(magicXSignException.getErrorCode());
                    System.out.println(magicXSignException.getErrorMessage());
                    ERROR_CODE = magicXSignException.getErrorCode();
                    ERROR_MESSAGE = magicXSignException.getErrorMessage();
                }

                if (signType == MagicXSignType.SignType.SIGNED_DATA) {
                    ((MyApplication) requireActivity().getApplication()).getLastSignedDataLiveData().setValue(signResult);
                }

                if (signResult != null) {
                    try {
                        List<String> result = viewModel.verifySign(signType, currentSignOption, signResult, certificatePair.getSignCertificate(), plainText);
                        tvVerifyResult.setText(result.toString());
                        Xsign_Result_Y_N = "Y";
                    } catch (MagicXSignException magicXSignException) {
                        Xsign_Result_Y_N = "N";
                        tvVerifyResult.setText(magicXSignException.getErrorMessage());
                        System.out.println(magicXSignException.getErrorCode());
                        System.out.println(magicXSignException.getErrorMessage());
                        ERROR_CODE = magicXSignException.getErrorCode();
                        ERROR_MESSAGE = magicXSignException.getErrorMessage();
                    }
                } else {
                    //Common.showToast(requireContext(), Common.ToastType.NO_SELECTED_CERTIFICATE);
                }
            } else {
                Toast.makeText(getContext(), "선택된 인증서가 없습니다.", Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.btn_send_result) {
            SendDataXsign();
        } else if (v.getId() == R.id.btAddQRCertificate) {
            // QRCode 스캔으로 인증서 가져오기
        } else if (v.getId() == R.id.btAddNumberCertificate) {
            // 인증번호로 인증서 가져오기
            magicMRSCallBack();
//            magicMRS.importCertificateWithAuthCode(false);
//            FragmentTransaction transaction =
//                    getParentFragmentManager().beginTransaction();
//            transaction.replace(R.id.fragmentContainerView, new CertificateSelectFragment());
//            transaction.addToBackStack(null); // 뒤로가기 지원
//            transaction.commit();
        } else if (v.getId() == R.id.btn_poppup) {
            CertInfoDialog dialog = new CertInfoDialog();
            dialog.show(getParentFragmentManager(), "CertInfoDialog");
        }

    }

    private void startCharKeypad() {
        dimView.setVisibility(View.VISIBLE);
        edt_password.setText("");
        View view = getView();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        magicVKeypadModel.setFieldName(edt_password.getText().toString());
        magicVKeypad.startCharKeypad(magicVKeypadClickListener);
    }

    private final MagicVKeypadClickListener magicVKeypadClickListener = new MagicVKeypadClickListener() {
        @Override
        public void onMagicVKeypadClick(MagicVKeypadResult magicVKeypadResult) {
            if (magicVKeypadResult.getLicenseResult() != MagicVKeypadType.MAGICVKEYPAD_TYPE_VERIFY_LICENSE_SUCCESS) {
                Log.d("Test", "라이센스 검증 실패 (오류코드: " + magicVKeypadResult.getLicenseResult() + ")");
            }

            Integer errorCode = magicVKeypadResult.getErrorCode();
            if (errorCode != null) {
                Log.d("Test", "MagicVKeypad 실행불가 (오류코드: " + errorCode + ")");
            }

            int buttonType = magicVKeypadResult.getButtonType();
            if (buttonType == MagicVKeypadType.MAGICVKEYPAD_TYPE_CHAR_NUM_BUTTON ||
                    buttonType == MagicVKeypadType.MAGICVKEYPAD_TYPE_REMOVE_BUTTON ||
                    buttonType == MagicVKeypadType.MAGICVKEYPAD_TYPE_ALL_REMOVE_BUTTON) {

                if (magicVKeypadResult.getMaskingData() != null) {
                    //if (edt_password.equals(magicVKeypadResult.getFieldName())) {
                    edt_password.setText(magicVKeypadResult.getMaskingData());
                    //}
                } else {
                    //if (edt_password.equals(magicVKeypadResult.getFieldName())) {
                    edt_password.setText("");
                    //}
                }
            } else if (buttonType == MagicVKeypadType.MAGICVKEYPAD_TYPE_OK_BUTTON) {

                if (magicVKeypadResult.getMaskingData() == null) {
                    Toast.makeText(getContext(), "패스워드를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                setCipherData(magicVKeypadResult);

                if (magicVKeypadModel.isFullMode()) {
                    if (magicVKeypadResult.getMaskingData() != null) {
                        //if (edt_password.equals(magicVKeypadResult.getFieldName())) {
                        edt_password.setText(magicVKeypadResult.getMaskingData());
                        //}
                    }
                } else {
                    magicVKeypad.closeKeypad();
                    dimView.setVisibility(View.GONE);
                }
            } else if (buttonType == MagicVKeypadType.MAGICVKEYPAD_TYPE_CANCEL_BUTTON) {
                //if (edt_password.equals(magicVKeypadResult.getFieldName())) {
                edt_password.setText("");
                //}
                certificateListAdapter.resetSelectedPosition();
                magicVKeypad.closeKeypad();
                dimView.setVisibility(View.GONE);
            }
        }
    };

    private void setCipherData(MagicVKeypadResult magicVKeypadResult) {
        byte[] encData = magicVKeypad.getEncryptData();

//        if (!SettingsActivity.Companion.getUseE2E()) {
        if (encData != null) {
            aesEncryptData = magicVKeypadUtil.byteArray2Hex(encData);
            byte[] decData = magicVKeypad.getDecryptData(encData);
            if (decData != null) {
                aesDecryptData = new String(decData);
                Arrays.fill(decData, (byte) 0x00);
            }
        }

        if (!aesEncryptData.isEmpty()) {
            //Toast.makeText(getContext(), "AES256 암호화 데이터: " + aesEncryptData, Toast.LENGTH_SHORT).show();

            //defaultBinding.tvDefaultEncData.setText("AES256 암호화 데이터: " + aesEncryptData);
            //aesEncryptData = "";


        }

        if (!aesDecryptData.isEmpty()) {
            //if (edt_password.equals(magicVKeypadResult.getFieldName())) {
            // edt_password.setText(aesDecryptData);

            //}
            //binding.vePassword.etContent.setText(aesDecryptData);
            //Toast.makeText(getContext(), "AES256 복호화 데이터: " + aesDecryptData, Toast.LENGTH_SHORT).show();
            //defaultBinding.tvDefaultDecData.setText("AES256 복호화 데이터: " + aesDecryptData);
            //aesDecryptData = "";
            Log.e(TAG, "SendDataXsign aesDecryptData: " + aesDecryptData);
            Log.e(TAG, "SendDataXsign ERROR_CODE: " + ERROR_CODE);
            Log.e(TAG, "SendDataXsign ERROR_MESSAGE: " + ERROR_MESSAGE);

            SendDataXsign();
        }

    }

    private void SendDataXsign() {
        signCert = "";
        signPri = "";
        Xsign_Result_Y_N = "N";
        tvResult.setText("");
        tvVerifyResult.setText("");
        ERROR_CODE = -1;
        ERROR_MESSAGE = "";

        Certificate currentCertificate = ((MyApplication) requireActivity().getApplication()).getSelectedCertificateStateLiveData().getValue();
        SignOption currentSignOption = ((MyApplication) requireActivity().getApplication()).getSignOptionStateLiveData().getValue();
        Date signDate = ((MyApplication) requireActivity().getApplication()).getSignDateLiveData().getValue();
        MagicXSignType.SignType signType = MagicXSignType.SignType.SIGNATURE;

        //byte[] password = edt_password.getText().toString().getBytes();
        byte[] password = aesDecryptData.getBytes();
        byte[] plainText = mPlainText.getBytes();

        if (currentCertificate != null) {
            CertificatePair certificatePair = currentCertificate.getCertificatePair();
            String signResult = null;
            try {
                signResult = viewModel.sign(signType, currentSignOption, certificatePair.getSignCertificate(), certificatePair.getSignPrivateKey(), password, plainText, signDate);
                tvResult.setText(signResult);
                Xsign_Result_Y_N = "Y";
                signCert = certificatePair.getSignCertificate();
                signPri = certificatePair.getSignPrivateKey();
            } catch (MagicXSignException magicXSignException) {
                Xsign_Result_Y_N = "N";
                tvResult.setText(magicXSignException.getErrorMessage());
                System.out.println(magicXSignException.getErrorCode());
                System.out.println(magicXSignException.getErrorMessage());
                ERROR_CODE = magicXSignException.getErrorCode();
                ERROR_MESSAGE = magicXSignException.getErrorMessage();
            }

            if (signType == MagicXSignType.SignType.SIGNED_DATA) {
                ((MyApplication) requireActivity().getApplication()).getLastSignedDataLiveData().setValue(signResult);
            }

            if (signResult != null) {
                try {
                    List<String> result = viewModel.verifySign(signType, currentSignOption, signResult, certificatePair.getSignCertificate(), plainText);
                    tvVerifyResult.setText(result.toString());
                    Xsign_Result_Y_N = "Y";
                } catch (MagicXSignException magicXSignException) {
                    Xsign_Result_Y_N = "N";
                    tvVerifyResult.setText(magicXSignException.getErrorMessage());
                    System.out.println(magicXSignException.getErrorCode());
                    System.out.println(magicXSignException.getErrorMessage());
                    ERROR_CODE = magicXSignException.getErrorCode();
                    ERROR_MESSAGE = magicXSignException.getErrorMessage();
                }
            } else {
                //Common.showToast(requireContext(), Common.ToastType.NO_SELECTED_CERTIFICATE);
            }
        } else {
            Toast.makeText(getContext(), "선택된 인증서가 없습니다.", Toast.LENGTH_SHORT).show();
        }
        Log.e(TAG, "SendDataXsign Xsign_Result_Y_N: " + Xsign_Result_Y_N);
        Log.e(TAG, "SendDataXsign tvResult: " + tvResult.getText().toString());
        Log.e(TAG, "SendDataXsign signCert: " + signCert);
        Log.e(TAG, "SendDataXsign signPri: " + signPri);
        Log.e(TAG, "SendDataXsign aesDecryptData: " + aesDecryptData);
        if (mWebViewEx != null) {
            if (ERROR_CODE == 3006 && ERROR_MESSAGE.contains("password")) {
                certificateListAdapter.resetSelectedPosition();
                Toast.makeText(getContext(), "비밀번호가 틀렸습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            } else {
                byte[] XsignPassword = aesDecryptData.getBytes(StandardCharsets.UTF_8);
                String script = String.format("javascript:fnMobileSignCallback('%s', '%s', '%s', '%s', '%s');", Xsign_Result_Y_N, tvVerifyResult.getText().toString(), signCert, signPri, Base64.encodeToString(XsignPassword, Base64.DEFAULT));
                mWebViewEx.loadUrl(script);
                Log.e(TAG, "SendDataXsign ok");
                finishFragment();
            }
        }
    }


    private void loadCertificateList() {
        CertificateFilter currentFilter = ((MyApplication) requireActivity().getApplication()).getCertificateFilterStateLiveData().getValue();
        certificateSelectViewModel.getCertificateList(currentFilter, requireContext());
        Log.e(TAG, "loadCertificateList");
    }

    private void observeCertificateFilter() {
        LiveData<CertificateFilter> certificateFilterLiveData = ((MyApplication) requireActivity().getApplication()).getCertificateFilterStateLiveData();
        certificateFilterLiveData.observe(getViewLifecycleOwner(), certificateFilter -> {
            certificateSelectViewModel.getCertificateList(certificateFilter, requireContext());
            Log.e(TAG, "observeCertificateFilter");
        });
    }

    private void observeCertificateList() {
        LiveData<List<Certificate>> certificateListLiveData = ((MyApplication) requireActivity().getApplication()).getCertificateListStateLiveData();
        certificateListLiveData.observe(getViewLifecycleOwner(), new Observer<List<Certificate>>() {
            @Override
            public void onChanged(List<Certificate> currentCertificateList) {
                Log.e(TAG, "observeCertificateList");
                if (currentCertificateList.isEmpty()) {
                    rvCertificate.setVisibility(View.GONE);
                    noSelectedCertificate.setVisibility(View.VISIBLE);
                } else {
                    rvCertificate.setVisibility(View.VISIBLE);
                    noSelectedCertificate.setVisibility(View.GONE);
                }
                certificateListAdapter.submitList(currentCertificateList);
                Certificateselected = null;
            }
        });
    }

    private void finishFragment() {
        if (!isAdded()) return;

        FragmentManager fm = requireActivity().getSupportFragmentManager();

        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            requireActivity().finish();
        }
    }

    public static class CertInfoDialog extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Dialog dialog = new Dialog(requireContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_cert_info);
            dialog.setCanceledOnTouchOutside(true);

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(
                        new ColorDrawable(Color.TRANSPARENT)
                );
            }

            return dialog;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        magicVKeypad.finalizeMagicVKeypad();
    }

    public void magicMRSCallBack() {
        // MagicMRS 초기화 및 콜백 설정
        magicMRS = new MagicMRS(requireContext(), (type, mrsResult, cert) -> {
            Log.d(TAG, TAG + "\ntype = " + type +
                    " \nresultCode = " + mrsResult.getErrorCode() +
                    " \nresultDesc = " + mrsResult.getErrorDescription());

            if (mrsResult.getErrorCode() == MagicMRSConfig.MAGICMRS_RESULT_SUCCESS) {
                if (type != MagicMRSConfig.MAGICMRS_TYPE_EXPORT) {
                    Log.d(TAG, "=====" + TAG + " Import Cert======");
                    if (cert != null) {
                        Log.d(TAG, "signCert\n" + (cert.getSignCert() != null ? Arrays.toString(cert.getSignCert()) : "null"));
                        Log.d(TAG, "signPri\n" + (cert.getSignPri() != null ? Arrays.toString(cert.getSignPri()) : "null"));
                        Log.d(TAG, "kmCert\n" + (cert.getKmCert() != null ? Arrays.toString(cert.getKmCert()) : "null"));
                        Log.d(TAG, "kmPri\n" + (cert.getKmPri() != null ? Arrays.toString(cert.getKmPri()) : "null"));
                        Log.d(TAG, "keyUsage: " + cert.getKeyUsage());

                        try {
                            String signCert = cert.getSignCert() != null ? encodeBase64(cert.getSignCert()) : null;
                            String signPri = cert.getSignPri() != null ? encodeBase64(cert.getSignPri()) : null;
                            String kmCert = cert.getKmCert() != null ? encodeBase64(cert.getKmCert()) : null;
                            String kmPri = cert.getKmPri() != null ? encodeBase64(cert.getKmPri()) : null;
                            Log.d(TAG, "signCert: " + signCert);
                            Log.d(TAG, "signPri: " + signPri);
                            Log.d(TAG, "kmCert: " + kmCert);
                            Log.d(TAG, "kmPri: " + kmPri);

                            cmpViewModel.saveCertificate(signCert, signPri, kmCert, kmPri);

                            CertificateFilter currentFilter = ((MyApplication) requireActivity().getApplication()).getCertificateFilterStateLiveData().getValue();
                            certificateSelectViewModel.getCertificateList(currentFilter, requireContext());
                            Toast.makeText(getContext(), "인증서 가져오기 : " + mrsResult.getErrorDescription(), Toast.LENGTH_SHORT).show();
                        } catch (MagicXSignException e) {
                            Toast.makeText(getContext(), "인증서 가져오기 : " + mrsResult.getErrorDescription(), Toast.LENGTH_SHORT).show();
                            throw new RuntimeException(e);
                        }
                    }
                    Log.d(TAG, "===================================");
                }
            } else {
                Toast.makeText(getContext(), "인증서 가져오기 : " + mrsResult.getErrorDescription(), Toast.LENGTH_SHORT).show();
            }
        });

        // 라이선스 검증(필수)
        magicMRS.initializeMagicMRS(Common.MRS_LICENSE_STRING);

        String version = magicMRS.getVersion();
        Log.d(TAG, TAG + " version: " + version);

        magicMRS.setURL(MRS_SERVER_IP, MRS_SERVER_PORT);

        // SSL 인증서 검사 결과 무시
        // magicMRS.setIgnoreTLSCert(true);

        magicMRS.setTextExportCertificate("title", "content");
        magicMRS.importCertificateWithAuthCode(false);
    }
}