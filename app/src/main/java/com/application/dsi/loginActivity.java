package com.application.dsi;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.application.dsi.dataClass.RequestCall;
import com.application.dsi.databinding.ActivityLoginBinding;
import com.application.dsi.viewModels.AuthViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class loginActivity extends AppCompatActivity implements View.OnKeyListener, View.OnClickListener {

    ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    AuthViewModel viewModel;

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            loginUser();
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        binding.backgroundLayout.setOnClickListener(this);
        binding.txtWelcome.setOnClickListener(this);
        binding.txtWelcomeDesc.setOnClickListener(this);
        binding.btnLogin.setOnClickListener(this);
        binding.txtSignUp.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            startActivity(new Intent(this, userDashboardActivity.class));
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.background_layout || view.getId() == R.id.txt_welcome || view.getId() == R.id.txt_welcomeDesc) {
            InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } else if (view.getId() == R.id.txt_signUp) {
            startActivity(new Intent(loginActivity.this, SignUpActivity.class));
        } else if (view.getId() == R.id.btn_login) {
            if (binding.edtPassword.getText().toString().length() < 8) {
                binding.edtPassword.setError("Password should be 8 character long");
                binding.edtPassword.requestFocus();
            } else {
                loginUser();
            }
        }
    }

    public void loginUser() {
        String email = binding.edtEmail.getText().toString();
        String password = binding.edtPassword.getText().toString();

        if (email.equals("")) {
            Toast.makeText(loginActivity.this, "Username Can not be Empty", Toast.LENGTH_SHORT).show();
        } else if (password.equals("")) {
            Toast.makeText(loginActivity.this, "Password Can Not be Empty", Toast.LENGTH_SHORT).show();
        } else {
            viewModel.viewModelLogin(email, password).observe(this, new Observer<RequestCall>() {
                @Override
                public void onChanged(RequestCall requestCall) {
                    if (requestCall.getStatus() == 1 && requestCall.getMessage().equals("No data Found")) {
                        Toast toast = Toast.makeText(loginActivity.this, "Employee is not registered!!", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    } else if (requestCall.getStatus() == 1 && requestCall.getMessage().equals("Finished")) {
                        updateUi();
                        binding.loginProgressBar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        binding.backgroundLayout.setAlpha(1);
                    } else if (requestCall.getStatus() == 0) {
                        binding.loginProgressBar.setVisibility(View.VISIBLE);
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        binding.backgroundLayout.setAlpha((float) 0.4);
                    } else if (requestCall.getStatus() == -1) {
                        Toast.makeText(loginActivity.this, requestCall.getMessage(), Toast.LENGTH_SHORT).show();
                        binding.loginProgressBar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        binding.backgroundLayout.setAlpha(1);
                    }
                }
            });
        }
    }

    public void updateUi() {
        Intent intent = new Intent(this, userDashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}