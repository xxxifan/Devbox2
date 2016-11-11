/*
 * Copyright(c) 2016 xxxifan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xxxifan.devbox.demo.ui.view.main;

import com.xxxifan.devbox.core.base.BasePresenter;
import com.xxxifan.devbox.core.base.BaseView;

/**
 * Generated for Devbox(https://github.com/xxxifan/Devbox2)
 * Date: 8/24/16 5:15 PM
 */
public interface MvpContract {
    interface View extends BaseView<Presenter> {
        void onShowInfo(String info);
    }

    interface Presenter extends BasePresenter<View> {
        void getInfo();

        void setInfo(String info);
    }
}
