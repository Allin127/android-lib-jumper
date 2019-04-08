package com.vonallin.lib.jumper;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.vonallin.lib.jumper.enums.JumpAnimEnum;


import lombok.Builder;
import lombok.Data;
@Builder
@Data
public class FragmentOptions {
    private boolean addBackStack;
    private boolean transactionAdd;// 加入事务or替换 add or replace
    private boolean commitLossState; //提交方式
    private boolean isExecuteNow;//是否立即提交事务

    private boolean isSingltone;
    private boolean preFragVisible; //是否要让推到后台的隐藏显示
    private String tag;//fragment的tag add or replace时候用的
    private Class<? extends Fragment> fragClazz;//

//    返回事务用tag替换
//    private String transactionName;//事务压入返回栈时候的名字

    private boolean isPreVisible;//覆盖或者添加的时候是否要隐藏之前的


    private Bundle arguments;
    private int contentId;
    private JumpAnimEnum animType;//动画类型
    private int PUSH_ANIM_IN, PUSH_ANIM_OUT, POP_ANIM_IN, POP_ANIM_OUT;//自定义动画设置，默认的放在jumper


}
