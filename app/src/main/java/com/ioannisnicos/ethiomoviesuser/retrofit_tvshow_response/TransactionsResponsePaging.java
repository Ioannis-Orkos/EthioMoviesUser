package com.ioannisnicos.ethiomoviesuser.retrofit_tvshow_response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TransactionsResponsePaging {
    @SerializedName("results")
    private List<TransactionTvshowResponse> result_data;
    @SerializedName("pageInfo")
    private Paging paging;
    private QuerryResponse querryMessage;


    public TransactionsResponsePaging(List<TransactionTvshowResponse> result_data, Paging paging, QuerryResponse querryMessage) {
        this.result_data = result_data;
        this.paging = paging;
        this.querryMessage = querryMessage;
    }

    public List<TransactionTvshowResponse> getResult_data() {
        return result_data;
    }

    public void setResult_data(List<TransactionTvshowResponse> result_data) {
        this.result_data = result_data;
    }

    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }

    public QuerryResponse getQuerryMessage() {
        return querryMessage;
    }

    public void setQuerryMessage(QuerryResponse querryMessage) {
        this.querryMessage = querryMessage;
    }
}
