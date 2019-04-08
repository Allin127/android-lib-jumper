package com.vonallin.lib.jumper;

import java.util.List;

import static com.vonallin.lib.jumper.FragJumperEnum.*;

public class EnumWrapper {
    private int value;

    public static EnumWrapper newInstance() {
        return new EnumWrapper();
    }

    public EnumWrapper subEnum(IEnumValue aEnum) {
        this.value &= ~aEnum.getValue();
        return EnumWrapper.this;
    }

    public EnumWrapper addEnum(IEnumValue aEnum) {
        this.value |= aEnum.getValue();
        return EnumWrapper.this;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    /*某个枚举合成值是否包含某个枚举*/
    public boolean contains(FragJumperEnum targetEnum){
        List s = targetEnum.getEnums();
        boolean isValid = s.contains(this.value);
        return isValid;
    }


    public static void main(String args[]) {
        System.out.println(EnumWrapper.newInstance().addEnum(FRAG_BACKSTACK)
                .addEnum(FRAG_ADD).addEnum(FRAG_UNIQUEUE_INSTANCE)
                .getValue());//35
        System.out.println(EnumWrapper.newInstance().addEnum(FRAG_BACKSTACK)
                .addEnum(FRAG_ADD).subEnum(FRAG_UNIQUEUE_INSTANCE)
                .getValue());//3
        System.out.println(EnumWrapper.newInstance().addEnum(FRAG_BACKSTACK)
                .addEnum(FRAG_ADD).subEnum(FRAG_BACKSTACK).subEnum(FRAG_BACKSTACK)
                .getValue());//2
    }

}
