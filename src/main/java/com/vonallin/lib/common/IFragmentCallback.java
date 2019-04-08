package com.vonallin.lib.common;

/**
 * add by zhaoshuchao
 */
public interface IFragmentCallback extends IControllerCallback {
    /**
     * fragment出现的回调，创建或者返回时都会回调
     */
    void onViewAppear();
}
