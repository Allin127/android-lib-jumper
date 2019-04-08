package com.vonallin.lib.jumper.enums;

import com.vonallin.lib.base.util.StringUtil;

public enum JumpAnimEnum  {
    ANIM_DEFAULT(0),
    ANIM_NONE(99),
    ;

    private int value;
    JumpAnimEnum(int value) {
        this.value = value;
    }

    public int getValue(){
     return value;
    }

    public static JumpAnimEnum parse(String value) {
        if (StringUtil.isEmpty(value)) {
            return null;
        }

        JumpAnimEnum animEnum;
        try {
            animEnum = valueOf(value);
        } catch (Exception e) {
            return null;
        }
        return animEnum;
    }
}
