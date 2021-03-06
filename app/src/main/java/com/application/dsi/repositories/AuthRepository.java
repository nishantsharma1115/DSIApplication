package com.application.dsi.repositories;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.application.dsi.common.Constants;
import com.application.dsi.dataClass.Customer;
import com.application.dsi.dataClass.Employee;
import com.application.dsi.dataClass.RequestCall;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthRepository {
    private final MutableLiveData<RequestCall> downloadMutableLiveData;
    private static final String PLEASE_WAIT = "Please Wait....";
    private static final String FINISHED = "Finished";

    public AuthRepository() {
        this.downloadMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<RequestCall> signUpUser(final String email, String password, final Employee employee) {
        final RequestCall r = new RequestCall();

        r.setStatus(Constants.OPERATION_IN_PROGRESS);
        r.setMessage(PLEASE_WAIT);
        r.setEmployee(employee);
        downloadMutableLiveData.setValue(r);

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                employee.setUserId(user.getUid());
                                r.setStatus(Constants.OPERATION_COMPLETE_SUCCESS);
                                r.setEmployee(employee);
                                r.setMessage(FINISHED);
                            }
                            downloadMutableLiveData.setValue(r);
                        } else {
                            r.setStatus(Constants.OPERATION_COMPLETE_FAILURE);
                            r.setMessage("Error while Authentication");
                            downloadMutableLiveData.postValue(r);
                        }
                    }
                });

        return downloadMutableLiveData;
    }

    public MutableLiveData<RequestCall> loginUser(String email, String password) {

        final RequestCall r = new RequestCall();
        r.setStatus(Constants.OPERATION_IN_PROGRESS);
        r.setMessage(PLEASE_WAIT);
        downloadMutableLiveData.setValue(r);

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                r.setStatus(Constants.OPERATION_COMPLETE_SUCCESS);
                                r.setMessage(FINISHED);
                            } else {
                                r.setStatus(Constants.OPERATION_COMPLETE_SUCCESS);
                                r.setMessage("No data Found");
                            }
                            downloadMutableLiveData.setValue(r);
                        } else {
                            r.setStatus(Constants.OPERATION_COMPLETE_FAILURE);
                            r.setMessage("Error while login");
                            downloadMutableLiveData.postValue(r);
                        }
                    }
                });

        return downloadMutableLiveData;
    }

    public MutableLiveData<RequestCall> viewModelSignUpCustomer(String email, String password, final Customer customer) {
        final RequestCall r = new RequestCall();

        r.setStatus(Constants.OPERATION_IN_PROGRESS);
        r.setMessage(PLEASE_WAIT);
        r.setCustomer(customer);
        downloadMutableLiveData.setValue(r);

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                customer.setCustomerId(user.getUid());
                                r.setStatus(Constants.OPERATION_COMPLETE_SUCCESS);
                                r.setCustomer(customer);
                                r.setMessage(FINISHED);
                            }
                            downloadMutableLiveData.setValue(r);
                        } else {
                            r.setStatus(Constants.OPERATION_COMPLETE_FAILURE);
                            r.setMessage("Error while Authentication");
                            downloadMutableLiveData.postValue(r);
                        }
                    }
                });

        return downloadMutableLiveData;
    }
}
