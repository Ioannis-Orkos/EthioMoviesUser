package com.ioannisnicos.ethiomoviesuser.retrofit_movie_response;

public class QuerryResponse {


    private String message;

    public QuerryResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
