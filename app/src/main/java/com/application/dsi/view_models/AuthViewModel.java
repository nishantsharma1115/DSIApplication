package com.application.dsi.view_models;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.application.dsi.repositories.AuthRepository;
import com.application.dsi.dataClass.Customer;
import com.application.dsi.dataClass.Employee;
import com.application.dsi.dataClass.RequestCall;

public class AuthViewModel extends ViewModel {
    private final AuthRepository repository;

    public AuthViewModel() {
        repository = new AuthRepository();
    }

    public MutableLiveData<RequestCall> viewModelSignUp(String email, String password, Employee employee) {
        return repository.signUpUser(email, password, employee);
    }

    public MutableLiveData<RequestCall> viewModelSignUpCustomer(String email, String password, Customer customer) {
        return repository.viewModelSignUpCustomer(email, password, customer);
    }

    public MutableLiveData<RequestCall> viewModelLogin(String email, String password) {
        return repository.loginUser(email, password);
    }
}
