/*
 * Copyright(c) 2016 Shanghai Lujiazui International Financial
 * Asset Exchange Co.,Ltd. All Rights Reserved.
 *
 * This software is the proprietary information of Shanghai Lujiazui
 * International Financial Asset Exchange Co.,Ltd.
 */
package com.vonallin.lib.common;

import android.os.Bundle;
import android.support.annotation.NonNull;

public interface ChannelCallback {


    boolean execute(Bundle bundle);

    /**
     * callback标识
     * 不可为空
     *
     * @return
     */
    @NonNull
    String getTag();

}
