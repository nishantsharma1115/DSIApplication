package com.application.dsi;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.application.dsi.dataClass.RequestCall;
import com.application.dsi.databinding.ActivityEmployeeProfileBinding;
import com.application.dsi.viewModels.dataViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import static com.application.dsi.common.Constants.SR;

public class employeeProfileActivity extends AppCompatActivity {

    ActivityEmployeeProfileBinding binding;
    dataViewModel viewModel;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_employee_profile);
        viewModel = new ViewModelProvider(this).get(dataViewModel.class);

        user = FirebaseAuth.getInstance().getCurrentUser();
        SR.child("Profile Picture").child("Employees").child(Objects.requireNonNull(user).getUid()).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get()
                                .load(uri)
                                .fit()
                                .into(binding.profilePicture);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        binding.profilePicture.setImageResource(R.drawable.profile_picture);
                    }
                });

        viewModel.getEmployee().observe(this, new Observer<RequestCall>() {
            @Override
            public void onChanged(RequestCall requestCall) {
                if (requestCall.getStatus() == 0) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.background.setAlpha((float) 0.4);
                } else if (requestCall.getStatus() == 1 && requestCall.getMessage().equals("Finished")) {
                    binding.setEmployee(requestCall.getEmployee());
                    binding.progressBar.setVisibility(View.GONE);
                    binding.background.setAlpha(1);
                } else if (requestCall.getStatus() == 1 && requestCall.getMessage().equals("No data Found")) {
                    Toast.makeText(employeeProfileActivity.this, "Error while fetching data", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (requestCall.getStatus() == -1) {
                    finish();
                }
            }
        });

    }
}
