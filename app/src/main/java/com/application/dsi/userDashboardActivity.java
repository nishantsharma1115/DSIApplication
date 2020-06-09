package com.application.dsi;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.application.dsi.dataClass.Employee;
import com.application.dsi.dataClass.RequestCall;
import com.application.dsi.databinding.ActivityUserDashboardBinding;
import com.application.dsi.viewModels.dataViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.application.dsi.common.Constants.DB;
import static com.application.dsi.common.Constants.SR;

public class userDashboardActivity extends AppCompatActivity {

    ActivityUserDashboardBinding binding;
    dataViewModel viewModel;
    FirebaseUser user;
    LocationManager locationManager;
    LocationListener locationListener;
    Employee employee = new Employee();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_dashboard);
        viewModel = new ViewModelProvider(this).get(dataViewModel.class);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                saveLocationToDataBase(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                //When User Disable or Enable the Location Permission
            }

            @Override
            public void onProviderEnabled(String s) {
                //When Enabled
            }

            @Override
            public void onProviderDisabled(String s) {
                //When Disabled
            }
        };

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
                        binding.profilePicture.setImageResource(R.drawable.profile_picture);
                    }
                });

        binding.userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(userDashboardActivity.this, employeeProfileActivity.class));
            }
        });

        viewModel.getEmployee().observe(this, new Observer<RequestCall>() {
            @Override
            public void onChanged(RequestCall requestCall) {
                if (requestCall.getStatus() == 0) {
                    binding.dashboardProgressBar.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    binding.dashboardBackground.setAlpha((float) 0.4);
                } else if (requestCall.getStatus() == 1 && requestCall.getMessage().equals("Finished")) {
                    binding.dashboardProgressBar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    binding.dashboardBackground.setAlpha(1);
                    employee = requestCall.getEmployee();
                    updateUi(employee);
                }
            }
        });

        binding.btnNewReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(userDashboardActivity.this, customerRegistrationActivity.class));
            }
        });
        binding.btnViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(userDashboardActivity.this, customerListActivity.class));
            }
        });
        binding.imgEditProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                } else {
                    getPhoto();
                }
            }
        });
    }

    public void updateUi(Employee employee) {
        binding.setEmployee(employee);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location != null) {
                saveLocationToDataBase(location);
            }
        }
    }

    private void saveLocationToDataBase(Location location) {
        Map<String, Object> employeeLocation = new HashMap<>();

        employeeLocation.put("latitude", String.valueOf(location.getLatitude()));
        employeeLocation.put("longitude", String.valueOf(location.getLongitude()));
        employeeLocation.put("userId", employee.getUserId());
        employeeLocation.put("post", employee.getPost());
        employeeLocation.put("name", employee.getName());

        DB.child("Location").child(employee.getUserId()).updateChildren(employeeLocation);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getPhoto();
        }
        if (requestCode == 2 && grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
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
                binding.profilePicture.setImageBitmap(bitmap);

                //Upload to Firebase Storage
                binding.profilePicture.setDrawingCacheEnabled(true);
                binding.profilePicture.buildDrawingCache();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] photoData = stream.toByteArray();
                user = FirebaseAuth.getInstance().getCurrentUser();
                SR.child("Profile Picture").child("Employees").child(Objects.requireNonNull(user).getUid()).putBytes(photoData);
            } catch (Exception e) {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        } else {
            binding.profilePicture.setImageResource(R.drawable.profile_picture);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logoutUser) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, loginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        return true;
    }
}
