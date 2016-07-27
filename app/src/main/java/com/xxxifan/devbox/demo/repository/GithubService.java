package com.xxxifan.devbox.demo.repository;

import com.xxxifan.devbox.demo.data.model.Repo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by xifan on 6/18/16.
 */
public interface GithubService {

    String REPO_TYPE_OWNER = "owner";
    String REPO_TYPE_ALL = "all";
    String REPO_SORT_CREATED = "created";
    String REPO_SORT_UPDATED = "updated";
    String REPO_SORT_NAME = "full_name";
    String DIRECTION_ASC = "asc";
    String DIRECTION_DESC = "desc";

    @GET("users/xxxifan/repos")
    Call<List<Repo>> getUserRepos(@Query("type") String type, @Query("sort") String sort, @Query("direction") String direction);

    @GET("users/xxxifan/repos")
    Observable<List<Repo>> getRxUserRepos(@Query("type") String type, @Query("sort") String sort, @Query("direction") String direction);
}
