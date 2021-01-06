package com.ioannisnicos.ethiomoviesuser.retrofit_movie_response;

import com.google.gson.annotations.SerializedName;
import com.ioannisnicos.ethiomoviesuser.models.Movies;
import com.ioannisnicos.ethiomoviesuser.models.Stores;
import com.ioannisnicos.ethiomoviesuser.models.Users;

public class TransactionMovieResponse {

    @SerializedName("users_result")
    private Users users;

    @SerializedName("movies_result")
    private Movies movies;

    @SerializedName("stores_result")
    private Stores stores;

    private MoviesRequestStatus status;

    public TransactionMovieResponse(Users users, Movies movies, Stores stores, MoviesRequestStatus status) {
        this.users = users;
        this.movies = movies;
        this.stores = stores;
        this.status = status;
    }


    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public Movies getMovies() {
        return movies;
    }

    public void setMovies(Movies movies) {
        this.movies = movies;
    }

    public Stores getStores() {
        return stores;
    }

    public void setStores(Stores stores) {
        this.stores = stores;
    }

    public MoviesRequestStatus getStatus() {
        return status;
    }

    public void setStatus(MoviesRequestStatus status) {
        this.status = status;
    }
}
