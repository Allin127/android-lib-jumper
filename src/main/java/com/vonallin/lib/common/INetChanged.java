package com.vonallin.lib.common;

/**
 * TODO:功能说明
 *
 * @author: zhaoshuchao950
 * @date: 2018-10-12 17:33
 */
public interface INetChanged {
    int NET_TYPE_NONE = 0;
    int NET_TYPE_UNKNOW = 1;
    int NET_TYPE_2G = 2;
    int NET_TYPE_3G = 3;
    int NET_TYPE_4G = 4;
    int NET_TYPE_WIFI = 8;

    boolean onNetChanged(int netType, int preType);
}
