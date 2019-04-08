package com.vonallin.lib.common;

import android.support.v4.app.Fragment;


import com.vonallin.lib.jump.FragmentExchanger;
//import com.vonallin.lib.jump.FragmentOption;

public interface IActivityCallback extends IControllerCallback {
    IFragmentCallback getCurrentFragment();

//    Fragment gotoFragment(FragmentOption fo);

    FragmentExchanger getFragmentExchanger();
}
