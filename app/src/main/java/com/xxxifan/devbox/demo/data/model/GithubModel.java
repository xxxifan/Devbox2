package com.xxxifan.devbox.demo.data.model;

import java.util.List;

/**
 * Created by xifan on 6/18/16.
 */
public class GithubModel {
    public String message;
    public String documentation_url;
    public List<Errors> errors;

    public static class Errors {
        public String resource;
        public String field;
        public String code;
    }
}
