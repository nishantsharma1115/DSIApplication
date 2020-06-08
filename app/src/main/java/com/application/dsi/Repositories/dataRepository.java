package com.application.dsi.Repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.application.dsi.common.Constants;
import com.application.dsi.dataClass.Customer;
import com.application.dsi.dataClass.Employee;
import com.application.dsi.dataClass.RequestCall;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import static com.application.dsi.common.Constants.DB;

public class dataRepository {
    private final MutableLiveData<RequestCall> downloadMutableLiveData;
    private final FirebaseUser user;

    public dataRepository() {
        this.downloadMutableLiveData = new MutableLiveData<>();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    public MutableLiveData<RequestCall> checkCoordinator(final String id) {
        final RequestCall r = new RequestCall();
        r.setStatus(Constants.OPERATION_IN_PROGRESS);
        r.setMessage("Please Wait....");
        downloadMutableLiveData.setValue(r);

        DB.child("Employee").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Employee employee = ds.getValue(Employee.class);
                        if (employee != null) {
                            Log.d("Post", employee.getPost());
                            if (employee.getPost().equals("Block Coordinator") && employee.getEmployeeId().equals(id)) {
                                r.setStatus(Constants.OPERATION_COMPLETE_SUCCESS);
                                r.setMessage("Finished");
                            }
                        } else {
                            r.setStatus(Constants.OPERATION_COMPLETE_SUCCESS);
                            r.setMessage("No data Found");
                        }
                    }
                } else {
                    r.setStatus(Constants.OPERATION_COMPLETE_SUCCESS);
                    r.setMessage("No data Found");
                }
                downloadMutableLiveData.postValue(r);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                r.setStatus(Constants.OPERATION_COMPLETE_FAILURE);
                r.setMessage(databaseError.getMessage());
                downloadMutableLiveData.postValue(r);
            }
        });
        return downloadMutableLiveData;
    }

    public MutableLiveData<RequestCall> employeeDetails() {
        final RequestCall r = new RequestCall();
        r.setStatus(Constants.OPERATION_IN_PROGRESS);
        r.setMessage("Please Wait....");
        r.setEmployee(new Employee());
        downloadMutableLiveData.setValue(r);

        DB.child("Employee").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Employee employee = dataSnapshot.getValue(Employee.class);
                    r.setStatus(Constants.OPERATION_COMPLETE_SUCCESS);
                    r.setMessage("Finished");
                    r.setEmployee(employee);
                } else {
                    r.setStatus(Constants.OPERATION_COMPLETE_SUCCESS);
                    r.setMessage("No data Found");
                }
                downloadMutableLiveData.postValue(r);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                r.setStatus(Constants.OPERATION_COMPLETE_FAILURE);
                r.setMessage(databaseError.getMessage());
                downloadMutableLiveData.postValue(r);
            }
        });
        return downloadMutableLiveData;
    }

    public MutableLiveData<RequestCall> getCustomerList() {
        final RequestCall r = new RequestCall();
        final ArrayList<Customer> customers = new ArrayList<>();
        r.setStatus(Constants.OPERATION_IN_PROGRESS);
        r.setMessage("Please Wait....");
        r.setCustomers(customers);
        downloadMutableLiveData.setValue(r);

        DB.child("Customers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (Objects.requireNonNull(ds.getValue(Customer.class)).getNavPanchayatId().equals(user.getUid())) {
                            Customer current = ds.getValue(Customer.class);
                            customers.add(current);
                        }
                        r.setStatus(Constants.OPERATION_COMPLETE_SUCCESS);
                        r.setMessage("Finished");
                        r.setCustomers(customers);
                    }
                } else {
                    r.setStatus(Constants.OPERATION_COMPLETE_SUCCESS);
                    r.setMessage("No data Found");
                }
                downloadMutableLiveData.postValue(r);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                r.setStatus(Constants.OPERATION_COMPLETE_FAILURE);
                r.setMessage(databaseError.getMessage());
                downloadMutableLiveData.postValue(r);
            }
        });

        return downloadMutableLiveData;
    }

    public MutableLiveData<RequestCall> customerDetails(String customerId) {
        final RequestCall r = new RequestCall();
        r.setStatus(Constants.OPERATION_IN_PROGRESS);
        r.setMessage("Please Wait....");
        r.setCustomer(new Customer());
        downloadMutableLiveData.setValue(r);

        DB.child("Customers").child(customerId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Customer customer = dataSnapshot.getValue(Customer.class);
                    r.setStatus(Constants.OPERATION_COMPLETE_SUCCESS);
                    r.setMessage("Finished");
                    r.setCustomer(customer);
                } else {
                    r.setStatus(Constants.OPERATION_COMPLETE_SUCCESS);
                    r.setMessage("No data Found");
                }
                downloadMutableLiveData.postValue(r);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                r.setStatus(Constants.OPERATION_COMPLETE_FAILURE);
                r.setMessage(databaseError.getMessage());
                downloadMutableLiveData.postValue(r);
            }
        });
        return downloadMutableLiveData;
    }

}
