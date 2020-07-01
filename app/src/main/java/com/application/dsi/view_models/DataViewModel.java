package com.application.dsi.view_models;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.application.dsi.repositories.DataRepository;
import com.application.dsi.dataClass.RequestCall;

public class DataViewModel extends ViewModel {
    private final DataRepository datarepository;

    public DataViewModel() {
        datarepository = new DataRepository();
    }

    public MutableLiveData<RequestCall> getEmployee() {
        return datarepository.employeeDetails();
    }

    public MutableLiveData<RequestCall> getAllCustomers() {
        return datarepository.getCustomerList();
    }

    public MutableLiveData<RequestCall> checkForCoordinator(String id) {
        return datarepository.checkCoordinator(id);
    }

    public MutableLiveData<RequestCall> getCustomerDetails(String customerId) {
        return datarepository.customerDetails(customerId);
    }
}
