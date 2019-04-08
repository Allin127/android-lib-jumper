package com.vonallin.lib.common;



/**
 * add by zhaoshuchao
 */
public interface IControllerCallback extends INetChanged {
    /**
     * @param fromBar 是否由左上角返回按钮发起。true: 由左上角按钮发起 false:由物理返回键发起
     * @return
     */
    boolean onKeyBack(boolean fromBar);

    void registerChannel(String channelName, ChannelCallback toRegisterCallback);
}
