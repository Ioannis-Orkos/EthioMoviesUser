package com.ioannisnicos.ethiomoviesuser.retrofit_movie_response;

import com.ioannisnicos.ethiomoviesuser.models.Stores;
import com.ioannisnicos.ethiomoviesuser.models.StoresAdditionalStatus;

import java.util.List;

public class StoresSubscriptionResponse {

    private List<Stores> stores_list;
    private List<StoresAdditionalStatus> stores_list_status;
    private String message;

    public StoresSubscriptionResponse(List<Stores> stores_list, List<StoresAdditionalStatus> stores_list_status, String message) {
        this.stores_list = stores_list;
        this.stores_list_status = stores_list_status;
        this.message = message;
    }

    public StoresSubscriptionResponse() {
    }

    public List<Stores> getStores_list() {
        return stores_list;
    }

    public void setStores_list(List<Stores> stores_list) {
        this.stores_list = stores_list;
    }

    public List<StoresAdditionalStatus> getStores_list_status() {
        return stores_list_status;
    }

    public void setStores_list_status(List<StoresAdditionalStatus> stores_list_status) {
        this.stores_list_status = stores_list_status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
