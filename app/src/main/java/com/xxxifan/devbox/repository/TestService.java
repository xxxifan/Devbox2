package com.xxxifan.devbox.repository;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by xifan on 5/18/16.
 */
public interface TestService {
    @GET("api/v1/Edition/EditionDetails")
    Call<String> getUser();
}
