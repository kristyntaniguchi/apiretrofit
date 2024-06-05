package com.example.apiretrofit;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

// Interface for defining API endpoints and their HTTP methods using Retrofit
public interface ApiService {

    // HTTP POST request to create a new API response entry
    // The endpoint URL is "api/basic/"
    // The request body contains an ApiResponse object
    @POST("api/basic/")
    Call<ApiResponse> postApiResponse(@Body ApiResponse apiResponse);

    // HTTP DELETE request to delete an existing API response entry by ID
    // The endpoint URL is "api/basic/{id}/" where {id} is a path parameter
    @DELETE("api/basic/{id}/")
    Call<Void> deleteApiResponse(@Path("id") int id);

    // HTTP PUT request to update an existing API response entry by ID
    // The endpoint URL is "api/basic/{id}/" where {id} is a path parameter
    // The request body contains an updated ApiResponse object
    @PUT("api/basic/{id}/")
    Call<ApiResponse> putApiResponse(@Path("id") int id, @Body ApiResponse apiResponse);
}
