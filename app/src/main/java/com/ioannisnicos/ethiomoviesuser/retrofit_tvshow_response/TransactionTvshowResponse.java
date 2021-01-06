package com.ioannisnicos.ethiomoviesuser.retrofit_tvshow_response;

import com.google.gson.annotations.SerializedName;
import com.ioannisnicos.ethiomoviesuser.models.Stores;
import com.ioannisnicos.ethiomoviesuser.models.Tvshow;
import com.ioannisnicos.ethiomoviesuser.models.Users;

public class TransactionTvshowResponse {

    @SerializedName("users_result")
    private Users users;

    @SerializedName("tvshows_result")
    private Tvshow tvshow;

    @SerializedName("stores_result")
    private Stores stores;

    private TvshowsRequestStatus status;

    public TransactionTvshowResponse(Users users, Tvshow tvshow, Stores stores, TvshowsRequestStatus status) {
        this.users = users;
        this.tvshow = tvshow;
        this.stores = stores;
        this.status = status;
    }


    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public Tvshow getTvshow() {
        return tvshow;
    }

    public void setTvshow(Tvshow tvshow) {
        this.tvshow = tvshow;
    }

    public Stores getStores() {
        return stores;
    }

    public void setStores(Stores stores) {
        this.stores = stores;
    }

    public TvshowsRequestStatus getStatus() {
        return status;
    }

    public void setStatus(TvshowsRequestStatus status) {
        this.status = status;
    }
}
