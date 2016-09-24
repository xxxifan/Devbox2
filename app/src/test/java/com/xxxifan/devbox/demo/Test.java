package com.xxxifan.devbox.demo;

import com.xxxifan.devbox.demo.data.model.GithubModel;
import com.xxxifan.devbox.library.util.FieldChecker;

import java.util.ArrayList;

/**
 * Created by xifan on 9/24/16.
 */

public class Test {
    @org.junit.Test
    public void FieldCheckerTest() {
        GithubModel model = new GithubModel();
        model.documentation_url = "url";
        model.message = "msg";
        model.errors = new ArrayList<>();
        long start = System.nanoTime();
        System.out.println(FieldChecker.checkNull(model));
        long end = System.nanoTime();
        System.out.println("cost " + (end - start));
    }
}
