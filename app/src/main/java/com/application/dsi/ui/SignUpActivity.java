package com.application.dsi.ui;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.application.dsi.R;
import com.application.dsi.common.Constants;
import com.application.dsi.dataClass.Employee;
import com.application.dsi.dataClass.RequestCall;
import com.application.dsi.databinding.ActivitySignUpBinding;
import com.application.dsi.view_models.AuthViewModel;
import com.application.dsi.view_models.DataViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.Calendar;
import java.util.Random;

import static com.application.dsi.common.Constants.DB;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    ActivitySignUpBinding binding;
    AuthViewModel viewModel;
    DataViewModel dataViewModel;
    Employee employee;
    boolean isEmployeeSet = false;
    String phoneNumber;
    Random rand = new Random();
    int otp;
    PendingIntent sendPi;
    PendingIntent deliveredPi;
    BroadcastReceiver smsSendReceiver;
    BroadcastReceiver smsDeliveredReceiver;
    String smsSent = "SMS_SENT";
    String smsDelivered = "SMS_DELIVERED";
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        dataViewModel = new ViewModelProvider(this).get(DataViewModel.class);
        employee = new Employee();

        binding.txtLoginHere.setOnClickListener(this);
        binding.signUpBackgroundLayout.setOnClickListener(this);
        binding.txtLets.setOnClickListener(this);
        binding.txtLetsDesc.setOnClickListener(this);
        binding.btnSendOtp.setOnClickListener(this);
        binding.imgCalender.setOnClickListener(this);
        binding.checkCoordinator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.edtBlockCoordinator.getText().toString().trim().length() < 9) {
                    binding.edtBlockCoordinator.setError("Id should be 9 digit long");
                    binding.edtBlockCoordinator.requestFocus();
                } else {
                    dataViewModel.checkForCoordinator(binding.edtBlockCoordinator.getText().toString().trim()).observe(SignUpActivity.this, new Observer<RequestCall>() {
                        @Override
                        public void onChanged(RequestCall requestCall) {
                            if (requestCall.getStatus() == 1 && requestCall.getMessage().equals("Finished")) {
                                updateUi();
                                binding.progressBar.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                binding.verifyBlockBackgroundLayout.setAlpha(1);
                            } else if (requestCall.getStatus() == 0) {
                                binding.progressBar.setVisibility(View.VISIBLE);
                                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                binding.verifyBlockBackgroundLayout.setAlpha((float) 0.4);
                            } else if (requestCall.getStatus() == 1 && requestCall.getMessage().equals("No data Found")) {
                                binding.progressBar.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                binding.verifyBlockBackgroundLayout.setAlpha(1);
                                binding.edtBlockCoordinator.setError("No such Coordinator Found");
                                binding.edtBlockCoordinator.requestFocus();
                            }
                        }
                    });
                }
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String dayInner = String.valueOf(day);
                String monthInner = String.valueOf(month);
                if (day < 10) {
                    dayInner = "0" + day;
                }
                if ((month + 1) < 10) {
                    monthInner = "0" + month;
                }
                binding.edtDob.setText(new StringBuilder().append(dayInner).append("/").append(Integer.parseInt(monthInner) + 1).append("/").append(year));
            }
        };
    }

    //Update UI when coordinator ID confirmed
    private void updateUi() {
        binding.verifyBlockBackgroundLayout.setVisibility(View.GONE);
        binding.signUpBackgroundLayout.setVisibility(View.VISIBLE);
        binding.verifyLayout.setVisibility(View.GONE);
        employee.setBlockCoordinatorId(binding.edtBlockCoordinator.getText().toString());
    }

    @Override
    public void onClick(@NotNull View view) {
        if (view.getId() == R.id.signUp_background_layout || view.getId() == R.id.txt_lets || view.getId() == R.id.txt_letsDesc) {
            InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } else if (view.getId() == R.id.txt_loginHere) {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        } else if (view.getId() == R.id.btn_sendOtp) {
            if (checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 1);
            } else {
                sendOtp();
            }
        } else if (view.getId() == R.id.img_calender || view.getId() == R.id.edt_dob) {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            new DatePickerDialog(
                    this,
                    R.style.Theme_AppCompat_DayNight_Dialog,
                    mDateSetListener,
                    year, month + 1, day).show();
        } else if (view.getId() == R.id.btn_verifyOtp) {
            verifyOtp();
        } else if (view.getId() == R.id.btn_goBack) {
            goBack();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        smsSendReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (getResultCode() == Activity.RESULT_OK) {
                    Toast.makeText(SignUpActivity.this, "OTP Send", Toast.LENGTH_SHORT).show();
                }
            }
        };

        smsDeliveredReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (getResultCode() == Activity.RESULT_OK) {
                    Toast.makeText(SignUpActivity.this, "OTP Send", Toast.LENGTH_SHORT).show();
                }
            }
        };

        registerReceiver(smsSendReceiver, new IntentFilter(smsSent));
        registerReceiver(smsDeliveredReceiver, new IntentFilter(smsDelivered));
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(smsSendReceiver);
        unregisterReceiver(smsDeliveredReceiver);
    }

    //When Send OTP Button is clicked
    private void sendOtp() {
        setEmployee();
        if (isEmployeeSet) {
            if (binding.edtMobile.getText().toString().startsWith("+91")) {
                phoneNumber = binding.edtMobile.getText().toString();
            } else {
                phoneNumber = "+91" + binding.edtMobile.getText().toString();
            }

            sendPi = PendingIntent.getBroadcast(this, 0, new Intent(smsSent), 0);
            deliveredPi = PendingIntent.getBroadcast(this, 0, new Intent(smsDelivered), 0);

            otp = rand.nextInt(899999) + 100000;
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null, otp + " is your Verification Code", sendPi, deliveredPi);

            binding.verifyBlockBackgroundLayout.setVisibility(View.GONE);
            binding.signUpBackgroundLayout.setVisibility(View.GONE);
            binding.verifyLayout.setVisibility(View.VISIBLE);
        }

    }

    private void setEmployee() {

        final String required = "Required";

        if (TextUtils.isEmpty(binding.edtName.getText())) {
            binding.edtName.setError(required);
            binding.edtName.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtFatherName.getText())) {
            binding.edtFatherName.setError(required);
            binding.edtFatherName.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtMotherName.getText())) {
            binding.edtMobile.setError(required);
            binding.edtMotherName.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtDob.getText())) {
            binding.edtDob.setError(required);
            binding.edtDob.requestFocus();
        } else if (!binding.male.isChecked() && !binding.female.isChecked()) {
            binding.tvGender.setError(required);
            binding.tvGender.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtPanNo.getText())) {
            binding.edtPanNo.setError(required);
            binding.edtPanNo.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtAadhaar.getText())) {
            binding.edtAadhaar.setError(required);
            binding.edtAadhaar.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtVoter.getText())) {
            binding.edtVoter.setError(required);
            binding.edtVoter.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtRation.getText())) {
            binding.edtRation.setError(required);
            binding.edtRation.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtNationality.getText())) {
            binding.edtNationality.setError(required);
            binding.edtNationality.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtHouseNo.getText())) {
            binding.edtHouseNo.setError(required);
            binding.edtHouseNo.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtStreetNumber.getText())) {
            binding.edtStreetNumber.setError(required);
            binding.edtStreetNumber.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtBlock.getText())) {
            binding.edtBlock.setError(required);
            binding.edtBlock.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtLandmark.getText())) {
            binding.edtLandmark.setError(required);
            binding.edtLandmark.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtVillage.getText())) {
            binding.edtVillage.setError(required);
            binding.edtVillage.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtPostOffice.getText())) {
            binding.edtPostOffice.setError(required);
            binding.edtPostOffice.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtPoliceSt.getText())) {
            binding.edtPoliceSt.setError(required);
            binding.edtPoliceSt.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtDistrict.getText())) {
            binding.edtDistrict.setError(required);
            binding.edtDistrict.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtCity.getText())) {
            binding.edtCity.setError(required);
            binding.edtCity.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtPinCode.getText())) {
            binding.edtPinCode.setError(required);
            binding.edtPinCode.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtMobile.getText())) {
            binding.edtMobile.setError(required);
            binding.edtMobile.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtEmail.getText())) {
            binding.edtEmail.setError(required);
            binding.edtEmail.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.edtEmail.getText().toString()).matches()) {
            binding.edtEmail.setError("Email Format is incorrect");
            binding.edtEmail.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtBankName.getText())) {
            binding.edtBankName.setError(required);
            binding.edtBankName.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtBranch.getText())) {
            binding.edtBranch.setError(required);
            binding.edtBranch.requestFocus();
        } else {
            employee.setEmployeeId(String.valueOf(new Random().nextInt(899999999) + 100000000));
            employee.setPost("Nav Panchayat");
            employee.setName(binding.edtName.getText().toString());
            employee.setFatherName(binding.edtFatherName.getText().toString());
            employee.setMotherName(binding.edtMotherName.getText().toString());
            employee.setDob(binding.edtDob.getText().toString());
            employee.setPanNo(binding.edtPanNo.getText().toString());
            employee.setAadhaarNo(binding.edtAadhaar.getText().toString());
            employee.setVoterNo(binding.edtVoter.getText().toString());
            employee.setRationNo(binding.edtRation.getText().toString());
            employee.setNationality(binding.edtNationality.getText().toString());
            employee.setHouse_no(binding.edtHouseNo.getText().toString());
            employee.setStreet_no(binding.edtStreetNumber.getText().toString());
            employee.setBlock(binding.edtBlock.getText().toString());
            employee.setLandmark(binding.edtLandmark.getText().toString());
            employee.setVillage(binding.edtVillage.getText().toString());
            employee.setPostOffice(binding.edtPostOffice.getText().toString());
            employee.setPoliceStation(binding.edtPoliceSt.getText().toString());
            employee.setDistrict(binding.edtDistrict.getText().toString());
            employee.setCity(binding.edtCity.getText().toString());
            employee.setPinCode(binding.edtPinCode.getText().toString());
            employee.setMobile(binding.edtMobile.getText().toString());
            employee.setEmail(binding.edtEmail.getText().toString());
            employee.setBankName(binding.edtBankName.getText().toString());
            employee.setBankBranch(binding.edtBranch.getText().toString());

            if (binding.male.isChecked()) {
                employee.setGender(binding.male.getText().toString());
            } else if (binding.female.isChecked()) {
                employee.setGender(binding.female.getText().toString());
            }

            isEmployeeSet = true;
        }
    }

    public void verifyOtp() {
        if (binding.edtVerifyOtp.getText().toString().equals(String.valueOf(otp))) {
            viewModel.viewModelSignUp(employee.getEmail(), binding.edtPassword.getText().toString(), employee).observe(this, new Observer<RequestCall>() {
                @Override
                public void onChanged(RequestCall requestCall) {
                    if (requestCall.getStatus() == Constants.OPERATION_COMPLETE_SUCCESS && requestCall.getMessage().equals("Finished")) {
                        registerEmployee(requestCall.getEmployee());
                        binding.progressBar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        binding.signUpBackgroundLayout.setAlpha(1);
                    } else if (requestCall.getStatus() == Constants.OPERATION_IN_PROGRESS) {
                        binding.progressBar.setVisibility(View.VISIBLE);
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        binding.signUpBackgroundLayout.setAlpha((float) 0.4);
                    } else if (requestCall.getStatus() == Constants.OPERATION_COMPLETE_FAILURE) {
                        Toast.makeText(SignUpActivity.this, "E-Mail ID already Registered", Toast.LENGTH_LONG).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 3000);
                    }
                }
            });
        }
    }

    private void registerEmployee(@NonNull Employee employee) {
        DB.child("Employee").child(employee.getUserId()).setValue(employee).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(binding.edtMobile.getText().toString(), null, "Thanks for Registering for DSI.", sendPi, deliveredPi);
                Intent intent = new Intent(SignUpActivity.this, UserDashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    public void goBack() {
        binding.verifyLayout.setVisibility(View.GONE);
        binding.signUpBackgroundLayout.setVisibility(View.VISIBLE);
    }
}
