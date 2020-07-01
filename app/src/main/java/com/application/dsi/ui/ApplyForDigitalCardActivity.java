package com.application.dsi.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.application.dsi.R;
import com.application.dsi.common.Constants;
import com.application.dsi.dataClass.Customer;
import com.application.dsi.dataClass.RequestCall;
import com.application.dsi.databinding.ActivityApplyForDigitalCardBinding;
import com.application.dsi.view_models.DataViewModel;

public class ApplyForDigitalCardActivity extends AppCompatActivity {

    ActivityApplyForDigitalCardBinding binding;
    DataViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_apply_for_digital_card);
        viewModel = new ViewModelProvider(this).get(DataViewModel.class);

        viewModel.getCustomerDetails(getIntent().getStringExtra("customerId")).observe(this, new Observer<RequestCall>() {
            @Override
            public void onChanged(RequestCall requestCall) {
                if (requestCall.getStatus() == Constants.OPERATION_IN_PROGRESS) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.backgroundLayout.setAlpha((float) 0.4);
                } else if (requestCall.getStatus() == Constants.OPERATION_COMPLETE_SUCCESS && requestCall.getMessage().equals("Finished")) {
                    Customer customer = requestCall.getCustomer();
                    String fullAddress = customer.getVillage() + " " + customer.getPostOffice() + " " + customer.getPoliceStation() + " " + customer.getCity() + " " + customer.getDistrict() + " " + customer.getPinCode();
                    binding.setFullAddress(fullAddress);
                    binding.setCustomer(customer);
                    binding.progressBar.setVisibility(View.GONE);
                    binding.backgroundLayout.setAlpha(1);
                } else if (requestCall.getStatus() == Constants.OPERATION_COMPLETE_SUCCESS && requestCall.getMessage().equals("No data Found")) {
                    finish();
                } else if (requestCall.getStatus() == Constants.OPERATION_COMPLETE_FAILURE) {
                    Toast.makeText(ApplyForDigitalCardActivity.this, requestCall.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
