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

package com.xxxifan.devbox.core.base.uicomponent;

import android.view.View;

/**
 * Created by xifan on 12/21/16.
 */

public interface UIComponent {
    /**
     * @param containerView base container of user layout, usually use $(BASE_CONTAINER_ID)
     */
    void inflate(View containerView);

    String getTag();
}
