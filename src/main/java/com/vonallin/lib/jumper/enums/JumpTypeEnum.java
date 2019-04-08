package com.vonallin.lib.jumper.enums;

public enum JumpTypeEnum {
    SWITCH_TO(0), //add replace attach
    BACK_STATCK_INCREASE(1),
    BACK_STATCK_DECREASE(2),
    ;
    private int value;
    JumpTypeEnum(int value) {
        this.value = value;
    }
}

