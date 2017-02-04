package com.xxxifan.devbox.demo;

import com.xxxifan.devbox.core.util.FieldChecker;
import com.xxxifan.devbox.demo.data.model.GithubModel;

import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by xifan on 9/24/16.
 */

public class DemoTest {

    @Test
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
