package com.application.dsi.viewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.application.dsi.Repositories.dataRepository;
import com.application.dsi.dataClass.RequestCall;

public class dataViewModel extends ViewModel {
    private final dataRepository datarepository;

    public dataViewModel() {
        datarepository = new dataRepository();
    }

    public MutableLiveData<RequestCall> getEmployee() {
        return datarepository.employeeDetails();
    }

    public MutableLiveData<RequestCall> getAllCustomers() {
        return datarepository.getCustomerList();
    }

    public MutableLiveData<RequestCall> checkForCoordinator (String id) {
        return datarepository.checkCoordinator(id);
    }

    public MutableLiveData<RequestCall> getCustomerDetails (String customerId) {
        return datarepository.customerDetails(customerId);
    }
}
