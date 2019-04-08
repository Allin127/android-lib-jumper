package com.vonallin.lib.jumper;

import java.util.ArrayList;
import java.util.List;

import com.vonallin.lib.base.util.StringUtil;

public enum FragJumperEnum implements IEnumValue {
    FRAG_BACKSTACK(1<<0),
    FRAG_ADD(1<<1),
    FRAG_LOSS_STATE(1<<2),
    FRAG_EXECUTE_NOW(1<<3),
    FRAG_ANIM_DEFAULT(1<<4),
    FRAG_UNIQUEUE_INSTANCE(1<<5),
    FRAG_IGNORE_PREVISIBLE(1<<6),
    ;

    private int value;
    FragJumperEnum(int value) {
        this.value = value;
    }
    @Override
    public int getValue() {
        return value;
    }

    /**
     * 根据枚举值，或者可能在枚举中的组合方式，用于数据库查询，
     * 比如一条记录是3个角色的权限组合，则可以查看当前角色所有组合的值是否存在于这条数据中
     * 1.获得当前枚举最大值
     * 比如4个枚举，二进制位移方式就是数字1向右位移5位减一，1111
     * 2.获取当前value值
     * 比如是第二个枚举，2进制表示就是0010
     * 3.最大值从1开始遍历到最大数，并且和当前值与操作 只要大于0，则表示这个数字包含当前值
     */
    public List<Integer> getEnums(){
        int max = (1 << values().length) - 1;
        List<Integer> result = new ArrayList<>();
        for(int i = 1; i <= max; i++){
            if((getValue() & i) > 0){
                result.add(i);
            }
        }
        return result;
    }

    public static FragJumperEnum parse(String value) {
        if (StringUtil.isEmpty(value)) {
            return null;
        }

        FragJumperEnum role;
        try {
            role = valueOf(value);
        } catch (Exception e) {
            return null;
        }
        return role;
    }

    public static List<Integer> getEnumList(String role){
        FragJumperEnum fragJumperEnum = parse(role);
        if(fragJumperEnum != null){
            return fragJumperEnum.getEnums();
        }
        return new ArrayList<>();
    }

    /*某个枚举合成值是否包含某个枚举*/
    public static boolean isContain(Integer enumValue,FragJumperEnum targetEnum){
        List s = targetEnum.getEnums();
        boolean isValid = s.contains(enumValue);
        return isValid;
    }


    public static void main(String[] args) {
        System.out.println(FRAG_BACKSTACK.getEnums());
        //权限合并，不会重复，做"或"运算
        int targetValue = FRAG_BACKSTACK.getValue()|FRAG_ADD.getValue();
        System.out.println(targetValue);
        //权限减少，做"与反"运算
        System.out.println(targetValue&(~FRAG_ADD.getValue())&(~FRAG_ADD.getValue()));
        System.out.println(FragJumperEnum.isContain(targetValue,FRAG_ADD));
        System.out.println(FragJumperEnum.isContain(targetValue,FRAG_EXECUTE_NOW));
    }
}
