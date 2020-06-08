package com.application.dsi;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.dsi.adapters.RecyclerAdapter;
import com.application.dsi.dataClass.RequestCall;
import com.application.dsi.databinding.ActivityCustomerListBinding;
import com.application.dsi.viewModels.dataViewModel;

import java.util.Objects;

public class customerListActivity extends AppCompatActivity {

    ActivityCustomerListBinding binding;
    RecyclerAdapter mAdapter;
    dataViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_customer_list);

        viewModel = new ViewModelProvider(this).get(dataViewModel.class);
        viewModel.getAllCustomers().observe(this, new Observer<RequestCall>() {
            @Override
            public void onChanged(RequestCall requestCall) {
                if (requestCall.getStatus() == 0) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                } else if (requestCall.getStatus() == 1 && requestCall.getMessage().equals("Finished")) {
                    binding.progressBar.setVisibility(View.GONE);
                    mAdapter.notifyDataSetChanged();
                } else if (requestCall.getStatus() == 1 && requestCall.getMessage().equals("No data Found")) {
                    Toast.makeText(customerListActivity.this, "No Data Found", Toast.LENGTH_SHORT).show();
                } else if (requestCall.getStatus() == -1) {
                    Toast.makeText(customerListActivity.this, "Failed while processing data", Toast.LENGTH_SHORT).show();
                }
            }
        });

        initRecyclerView();
    }

    private void initRecyclerView() {
        mAdapter = new RecyclerAdapter(this, Objects.requireNonNull(viewModel.getAllCustomers().getValue()).getCustomers());
        RecyclerView.LayoutManager linearLayout = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(linearLayout);
        binding.recyclerView.setAdapter(mAdapter);
    }
}
