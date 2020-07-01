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
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.application.dsi.R;
import com.application.dsi.common.Constants;
import com.application.dsi.dataClass.Customer;
import com.application.dsi.dataClass.Employee;
import com.application.dsi.dataClass.RequestCall;
import com.application.dsi.databinding.ActivityCustomerRegistrationBinding;
import com.application.dsi.view_models.AuthViewModel;
import com.application.dsi.view_models.DataViewModel;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import static com.application.dsi.common.Constants.DB;
import static com.application.dsi.common.Constants.SR;

public class CustomerRegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityCustomerRegistrationBinding binding;
    Customer customer;
    String customerId;
    Boolean isCustomerSet = false;
    String phoneNumber;
    String noOfRooms;
    String sourceOfIncome;
    Random rand = new Random();
    int otp;
    boolean isPhotoSelected = false;
    PendingIntent sendPi;
    PendingIntent deliveredPi;
    BroadcastReceiver smsSendReceiver;
    BroadcastReceiver smsDeliveredReceiver;
    String smsSent = "SMS_SENT";
    String smsDelivered = "SMS_DELIVERED";
    DataViewModel viewModel;
    Employee employee = new Employee();
    AuthViewModel authViewModel;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_customer_registration);
        viewModel = new ViewModelProvider(this).get(DataViewModel.class);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        customer = new Customer();
        customerId = String.valueOf(new Random().nextInt(899999999) + 100000000);
        binding.imgEditProfilePicture.setOnClickListener(this);
        binding.btnSendOtp.setOnClickListener(this);
        binding.imgCalender.setOnClickListener(this);
        binding.customerRegistrationLayout.setOnClickListener(this);
        binding.verifyLayout.setOnClickListener(this);

        viewModel.getEmployee().observe(this, new Observer<RequestCall>() {
            @Override
            public void onChanged(RequestCall requestCall) {
                if (requestCall.getStatus() == Constants.OPERATION_IN_PROGRESS) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.customerRegistrationLayout.setAlpha((float) 0.4);
                    binding.verifyLayout.setAlpha((float) 0.4);
                } else if (requestCall.getStatus() == Constants.OPERATION_COMPLETE_SUCCESS && requestCall.getMessage().equals("Finished")) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.customerRegistrationLayout.setAlpha(1);
                    binding.verifyLayout.setAlpha(1);
                    employee = requestCall.getEmployee();
                    binding.edtEmployeeId.setText(employee.getEmployeeId());
                }
            }
        });

        ArrayAdapter<CharSequence> noOfRoomsAdapter = ArrayAdapter.createFromResource(this, R.array.no_of_rooms, R.layout.spinner_item);
        binding.edtNoOfRooms.setAdapter(noOfRoomsAdapter);

        ArrayAdapter<CharSequence> sourceOfIncomeAdapter = ArrayAdapter.createFromResource(this, R.array.source_of_income, R.layout.spinner_item);
        binding.edtSourceOfIncome.setAdapter(sourceOfIncomeAdapter);

        binding.edtNoOfRooms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CustomerRegistrationActivity.this.noOfRooms = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(CustomerRegistrationActivity.this, "Please Select", Toast.LENGTH_SHORT).show();
            }
        });
        binding.edtSourceOfIncome.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CustomerRegistrationActivity.this.sourceOfIncome = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(CustomerRegistrationActivity.this, "Please Select", Toast.LENGTH_SHORT).show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                binding.edtDob.setText(new StringBuilder().append(day).append("/").append(month + 1).append("/").append(year));
            }
        };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getPhoto();
        }
    }

    public void getPhoto() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                binding.customerProfilePicture.setImageBitmap(bitmap);
                isPhotoSelected = true;
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            binding.customerProfilePicture.setImageResource(R.drawable.profile_picture);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.customerRegistrationLayout && view.getId() == R.id.verify_layout) {
            InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
        if (view.getId() == R.id.btn_sendOtp) {
            if (checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 1);
            } else {
                sendOtp();
            }
        } else if (view.getId() == R.id.img_editProfilePicture) {
            getPhoto();
        } else if (view.getId() == R.id.img_calender) {
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
                    Toast.makeText(CustomerRegistrationActivity.this, "OTP Send", Toast.LENGTH_SHORT).show();
                }
            }
        };

        smsDeliveredReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (getResultCode() == Activity.RESULT_OK) {
                    Toast.makeText(CustomerRegistrationActivity.this, "OTP Send", Toast.LENGTH_SHORT).show();
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

    private void sendOtp() {
        setCustomer();

        if (isCustomerSet) {
            otp = rand.nextInt(899999) + 100000;
            if (binding.edtMobile.getText().toString().startsWith("+91")) {
                phoneNumber = binding.edtMobile.getText().toString();
            } else {
                phoneNumber = "+91" + binding.edtMobile.getText().toString();
            }

            sendPi = PendingIntent.getBroadcast(this, 0, new Intent(smsSent), 0);
            deliveredPi = PendingIntent.getBroadcast(this, 0, new Intent(smsDelivered), 0);

            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null, otp + " is your Verification Code", sendPi, deliveredPi);

            binding.customerRegistrationLayout.setVisibility(View.GONE);
            binding.verifyLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setCustomer() {

        ArrayList<String> availableWaterSupply = new ArrayList<>();
        ArrayList<String> availableHouseBuilding = new ArrayList<>();
        ArrayList<String> availableUseElectricity = new ArrayList<>();
        ArrayList<String> availableElectronicItem = new ArrayList<>();
        ArrayList<String> availableVehicle = new ArrayList<>();

        if (binding.edtEmployeeId.getText().toString().isEmpty()) {
            binding.edtEmployeeId.setError("Required");
            binding.edtEmployeeId.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtName.getText())) {
            binding.edtName.setError("Required");
            binding.edtName.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtFatherName.getText())) {
            binding.edtFatherName.setError("Required");
            binding.edtFatherName.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtMotherName.getText())) {
            binding.edtMobile.setError("Required");
            binding.edtMotherName.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtFatherOcc.getText())) {
            binding.edtFatherOcc.setError("Required");
            binding.edtFatherOcc.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtDob.getText())) {
            binding.edtDob.setError("Required");
            binding.edtDob.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtPanNo.getText())) {
            binding.edtPanNo.setError("Required");
            binding.edtPanNo.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtAadhaar.getText())) {
            binding.edtAadhaar.setError("Required");
            binding.edtAadhaar.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtVoter.getText())) {
            binding.edtVoter.setError("Required");
            binding.edtVoter.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtRation.getText())) {
            binding.edtRation.setError("Required");
            binding.edtRation.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtNationality.getText())) {
            binding.edtNationality.setError("Required");
            binding.edtNationality.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtReligion.getText())) {
            binding.edtReligion.setError("Required");
            binding.edtReligion.requestFocus();
        } else if (!binding.male.isChecked() && !binding.female.isChecked()) {
            binding.tvGender.setError("Required");
            binding.tvGender.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtVillage.getText())) {
            binding.edtVillage.setError("Required");
            binding.edtVillage.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtPostOffice.getText())) {
            binding.edtPostOffice.setError("Required");
            binding.edtPostOffice.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtPoliceSt.getText())) {
            binding.edtPoliceSt.setError("Required");
            binding.edtPoliceSt.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtDistrict.getText())) {
            binding.edtDistrict.setError("Required");
            binding.edtDistrict.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtCity.getText())) {
            binding.edtCity.setError("Required");
            binding.edtCity.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtPinCode.getText())) {
            binding.edtPinCode.setError("Required");
            binding.edtPinCode.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtMobile.getText())) {
            binding.edtMobile.setError("Required");
            binding.edtMobile.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtEmail.getText())) {
            binding.edtEmail.setError("Required");
            binding.edtEmail.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.edtEmail.getText().toString()).matches()) {
            binding.edtEmail.setError("Email Format is incorrect");
            binding.edtEmail.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtBankName.getText())) {
            binding.edtBankName.setError("Required");
            binding.edtBankName.requestFocus();
        } else if (TextUtils.isEmpty(binding.edtBranch.getText())) {
            binding.edtBranch.setError("Required");
            binding.edtBranch.requestFocus();
        } else if (noOfRooms.equals("Select Number of Rooms")) {
            Toast.makeText(this, "Select Number of Rooms", Toast.LENGTH_SHORT).show();
            ((TextView) binding.edtNoOfRooms.getSelectedView()).setError("Select No of Rooms");
            (binding.edtNoOfRooms.getSelectedView()).requestFocus();
        } else if (sourceOfIncome.equals("Select Source of Income")) {
            Toast.makeText(this, "Select Source of Income", Toast.LENGTH_SHORT).show();
            ((TextView) binding.edtSourceOfIncome.getSelectedView()).setError("Required");
            (binding.edtNoOfRooms.getSelectedView()).requestFocus();
        } else if (binding.rbFan.isChecked() && binding.edtFan.getText().toString().isEmpty()) {
            binding.edtFan.setError("Can not be empty");
            binding.edtFan.requestFocus();
        } else if (binding.rbBulb.isChecked() && binding.edtBulb.getText().toString().isEmpty()) {
            binding.edtBulb.setError("Can not be empty");
            binding.edtBulb.requestFocus();
        } else if (binding.rbAc.isChecked() && binding.edtAc.getText().toString().isEmpty()) {
            binding.edtAc.setError("Can not be empty");
            binding.edtAc.requestFocus();
        } else if (binding.rbTv.isChecked() && binding.edtTv.getText().toString().isEmpty()) {
            binding.edtTv.setError("Can not be empty");
            binding.edtTv.requestFocus();
        } else if (binding.rbRefrigerator.isChecked() && binding.edtRefrigerator.getText().toString().isEmpty()) {
            binding.edtRefrigerator.setError("Can not be empty");
            binding.edtRefrigerator.requestFocus();
        } else if (binding.rbMotorPump.isChecked() && binding.edtMotorPump.getText().toString().isEmpty()) {
            binding.edtMotorPump.setError("Can not be empty");
            binding.edtMotorPump.requestFocus();
        } else if (binding.rbElectricIron.isChecked() && binding.edtElectronicIron.getText().toString().isEmpty()) {
            binding.edtElectronicIron.setError("Can not be empty");
            binding.edtElectronicIron.requestFocus();
        } else if (binding.rbMixture.isChecked() && binding.edtMixture.getText().toString().isEmpty()) {
            binding.edtMixture.setError("Can not be empty");
            binding.edtMixture.requestFocus();
        } else if (binding.rbBicycle.isChecked() && binding.edtBicycle.getText().toString().isEmpty()) {
            binding.edtBicycle.setError("Can not be empty");
            binding.edtBicycle.requestFocus();
        } else if (binding.rbMotorCycle.isChecked() && binding.edtMotorCycle.getText().toString().isEmpty()) {
            binding.edtMotorCycle.setError("Can not be empty");
            binding.edtMotorCycle.requestFocus();
        } else if (binding.rbMotorCar.isChecked() && binding.edtMotorCar.getText().toString().isEmpty()) {
            binding.edtMotorCar.setError("Can not be empty");
            binding.edtMotorCar.requestFocus();
        } else if (binding.rbTempo.isChecked() && binding.edtTempo.getText().toString().isEmpty()) {
            binding.edtTempo.setError("Can not be empty");
            binding.edtTempo.requestFocus();
        } else if (binding.rbRikshaw.isChecked() && binding.edtRikshaw.getText().toString().isEmpty()) {
            binding.edtRikshaw.setError("Can not be empty");
            binding.edtRikshaw.requestFocus();
        } else if (binding.rbTruck.isChecked() && binding.edtTruck.getText().toString().isEmpty()) {
            binding.edtTruck.setError("Can not be empty");
            binding.edtTruck.requestFocus();
        } else if (binding.rbBus.isChecked() && binding.edtBus.getText().toString().isEmpty()) {
            binding.edtBus.setError("Can not be empty");
            binding.edtBus.requestFocus();
        } else if (binding.edtPassword.getText().toString().isEmpty() && binding.edtConfirmPass.getText().toString().isEmpty()) {
            binding.edtPassword.setError("Required");
            binding.edtPassword.requestFocus();
        } else if (!binding.edtPassword.getText().toString().equals(binding.edtConfirmPass.getText().toString())) {
            binding.edtConfirmPass.setError("Not Equal to Password");
            binding.edtConfirmPass.requestFocus();
        } else {

            customer.setPost("Customer");
            customer.setCustomerId(customerId);
            customer.setNavPanchayatId(binding.edtEmployeeId.getText().toString());
            customer.setName(binding.edtName.getText().toString());
            customer.setFatherName(binding.edtFatherName.getText().toString());
            customer.setMotherName(binding.edtMotherName.getText().toString());
            customer.setFatherOcc(binding.edtFatherOcc.getText().toString());
            customer.setDob(binding.edtDob.getText().toString());
            customer.setPanNo(binding.edtPanNo.getText().toString());
            customer.setAadhaarNo(binding.edtAadhaar.getText().toString());
            customer.setVoterNo(binding.edtVoter.getText().toString());
            customer.setRationNo(binding.edtRation.getText().toString());
            customer.setNationality(binding.edtNationality.getText().toString());
            customer.setReligion(binding.edtReligion.getText().toString());

            if (binding.male.isChecked())
                customer.setGender(binding.male.getText().toString());
            else if (binding.female.isChecked())
                customer.setGender(binding.female.getText().toString());

            customer.setVillage(binding.edtVillage.getText().toString());
            customer.setPostOffice(binding.edtPostOffice.getText().toString());
            customer.setPoliceStation(binding.edtPoliceSt.getText().toString());
            customer.setDistrict(binding.edtDistrict.getText().toString());
            customer.setCity(binding.edtCity.getText().toString());
            customer.setPinCode(binding.edtPinCode.getText().toString());
            customer.setMobile(binding.edtMobile.getText().toString());
            customer.setEmail(binding.edtEmail.getText().toString());
            customer.setNoOfRooms(noOfRooms);
            customer.setSourceOfIncome(sourceOfIncome);

            if (binding.rbHandPump.isChecked())
                availableWaterSupply.add(binding.rbHandPump.getText().toString());
            if (binding.rbBoring.isChecked())
                availableWaterSupply.add(binding.rbBoring.getText().toString());
            if (binding.rbWell.isChecked())
                availableWaterSupply.add(binding.rbWell.getText().toString());
            if (binding.rbTubewel.isChecked())
                availableWaterSupply.add(binding.rbTubewel.getText().toString());
            if (binding.rbCorpLine.isChecked())
                availableWaterSupply.add(binding.rbCorpLine.getText().toString());
            if (binding.rbRiver.isChecked())
                availableWaterSupply.add(binding.rbRiver.getText().toString());

            if (binding.rbHut.isChecked())
                availableHouseBuilding.add(binding.rbHut.getText().toString());
            if (binding.rbRoof.isChecked())
                availableHouseBuilding.add(binding.rbRoof.getText().toString());
            if (binding.rbRent.isChecked())
                availableHouseBuilding.add(binding.rbRent.getText().toString());
            if (binding.rbGeneral.isChecked())
                availableHouseBuilding.add(binding.rbGeneral.getText().toString());

            if (binding.rbSolar.isChecked())
                availableUseElectricity.add(binding.rbSolar.getText().toString());
            if (binding.rbElectricity.isChecked())
                availableUseElectricity.add(binding.rbElectricity.getText().toString());
            if (binding.rbOther.isChecked())
                availableUseElectricity.add(binding.rbOther.getText().toString());

            if (binding.rbFan.isChecked()) {
                availableElectronicItem.add(binding.rbFan.getText().toString());
                customer.setNoOfFan(binding.edtFan.getText().toString());
            }
            if (binding.rbBulb.isChecked()) {
                availableElectronicItem.add(binding.rbBulb.getText().toString());
                customer.setNoOfBulb(binding.edtBulb.getText().toString());
            }
            if (binding.rbAc.isChecked()) {
                availableElectronicItem.add(binding.rbAc.getText().toString());
                customer.setNoOfAc(binding.edtAc.getText().toString());
            }
            if (binding.rbTv.isChecked()) {
                availableElectronicItem.add(binding.rbTv.getText().toString());
                customer.setNoOfTv(binding.edtTv.getText().toString());
            }
            if (binding.rbRefrigerator.isChecked()) {
                availableElectronicItem.add(binding.rbRefrigerator.getText().toString());
                customer.setNoOfRefrigerator(binding.edtRefrigerator.getText().toString());
            }
            if (binding.rbMotorPump.isChecked()) {
                availableElectronicItem.add(binding.rbMotorPump.getText().toString());
                customer.setNoOfMotorPump(binding.edtMotorPump.getText().toString());
            }
            if (binding.rbElectricIron.isChecked()) {
                availableElectronicItem.add(binding.rbElectricIron.getText().toString());
                customer.setNoOfElectricIron(binding.edtElectronicIron.getText().toString());
            }
            if (binding.rbMixture.isChecked()) {
                availableElectronicItem.add(binding.rbMixture.getText().toString());
                customer.setNoOfMixture(binding.edtMixture.getText().toString());
            }

            if (binding.rbBicycle.isChecked()) {
                availableVehicle.add(binding.rbBicycle.getText().toString());
                customer.setNoOfBicycle(binding.edtBicycle.getText().toString());
            }
            if (binding.rbMotorCycle.isChecked()) {
                availableVehicle.add(binding.rbMotorCycle.getText().toString());
                customer.setNoOfMotorCycle(binding.edtMotorCycle.getText().toString());
            }
            if (binding.rbMotorCar.isChecked()) {
                availableVehicle.add(binding.rbMotorCar.getText().toString());
                customer.setNoOfMotorCar(binding.edtMotorCar.getText().toString());
            }
            if (binding.rbTempo.isChecked()) {
                availableVehicle.add(binding.rbTempo.getText().toString());
                customer.setNoOfTempo(binding.edtTempo.getText().toString());
            }
            if (binding.rbRikshaw.isChecked()) {
                availableVehicle.add(binding.rbRikshaw.getText().toString());
                customer.setNoOfRikshaw(binding.edtRikshaw.getText().toString());
            }
            if (binding.rbTruck.isChecked()) {
                availableVehicle.add(binding.rbTruck.getText().toString());
                customer.setNoOfTruck(binding.edtTruck.getText().toString());
            }
            if (binding.rbBus.isChecked()) {
                availableVehicle.add(binding.rbBus.getText().toString());
                customer.setNoOfBus(binding.edtBus.getText().toString());
            }

            if (availableWaterSupply.size() > 0)
                customer.setWaterSupply(availableWaterSupply);
            if (availableHouseBuilding.size() > 0)
                customer.setHouseBuilding(availableHouseBuilding);
            if (availableUseElectricity.size() > 0)
                customer.setUseElectricity(availableUseElectricity);
            if (availableElectronicItem.size() > 0)
                customer.setElectronicItems(availableElectronicItem);
            if (availableVehicle.size() > 0)
                customer.setVehicle(availableVehicle);

            customer.setBankName(binding.edtBankName.getText().toString());
            customer.setBankBranch(binding.edtBranch.getText().toString());

            if (isPhotoSelected) {
                binding.customerProfilePicture.setDrawingCacheEnabled(true);
                binding.customerProfilePicture.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) binding.customerProfilePicture.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] photoData = stream.toByteArray();
                SR.child("Profile Picture").child("Customers").child(customerId).putBytes(photoData);
            }

            isCustomerSet = true;
        }
    }

    //Side Button of Verify OTP Button
    public void goBack() {
        binding.verifyLayout.setVisibility(View.GONE);
        binding.customerRegistrationLayout.setVisibility(View.VISIBLE);
    }

    public void verifyOtp() {
        if (binding.edtVerifyOtp.getText().toString().equals(String.valueOf(otp))) {
            authViewModel.viewModelSignUpCustomer(customer.getEmail(), binding.edtPassword.getText().toString(), customer).observe(this, new Observer<RequestCall>() {
                @Override
                public void onChanged(RequestCall requestCall) {
                    if (requestCall.getStatus() == Constants.OPERATION_COMPLETE_SUCCESS && requestCall.getMessage().equals("Finished")) {
                        registerCustomer(requestCall.getCustomer());
                        binding.progressBar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        binding.customerRegistrationLayout.setAlpha(1);
                    } else if (requestCall.getStatus() == Constants.OPERATION_IN_PROGRESS) {
                        binding.progressBar.setVisibility(View.VISIBLE);
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        binding.customerRegistrationLayout.setAlpha((float) 0.4);
                    }
                }
            });
        }
    }

    private void registerCustomer(Customer customer) {
        DB.child("Employee").child(customer.getCustomerId()).setValue(customer).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(binding.edtMobile.getText().toString(), null, "Thanks for Registering for DSI.", sendPi, deliveredPi);
                Intent intent = new Intent(CustomerRegistrationActivity.this, UserDashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}
