package com.vonallin.lib.jumper;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.vonallin.lib.base.util.StringUtil;
import com.vonallin.lib.util.Flag;
import com.vonallin.lib.util.JsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import com.vonallin.lib.jumper.*;

public final class FragmentOption implements Parcelable, JsonUtils.Jsonable {
    //start:以下是用户可操作的flag标志。
    public static final int FRAG_BACKSTACK = 1;
    public static final int FRAG_ADD = 2;
    public static final int FRAG_LOSS_STATE = 4;
    public static final int FRAG_EXECUTE_NOW = 8;
    public static final int FRAG_ANIM_DEFAULT = 16;
    public static final int FRAG_UNIQUEUE_INSTANCE = 32;
    public static final int FRAG_IGNORE_PREVISIBLE = 64;
    //end.

    //start:以下是在用户设置动画时自动添加的标志。
    private static final int FRAG_ANIM_CUSTOM = 128;//
    private static final int FRAG_ANIM_SYSTEM = 256;//

    private static final int FLAG_USER_ACCESS = FRAG_BACKSTACK | FRAG_ADD | FRAG_LOSS_STATE | FRAG_EXECUTE_NOW | FRAG_ANIM_DEFAULT | FRAG_UNIQUEUE_INSTANCE;
    ////////////////////////////////////////////////////////////////////////////
    /**
     * mFlag用来存储设置的标志。标志见FRAG_开头的常量。
     */
    private Flag mFlag = Flag.create(0);

    private EnumWrapper fragEnums = new EnumWrapper();


    //以下为用户设置的动画resid.
    private static int DEF_ANIM_IN, DEF_ANIM_OUT, DEF_ANIM_IN1, DEF_ANIM_OUT1;
    private int mAnimCusIn = 0, mAnimCusOut = 0, mAnimCusIn1 = 0, mAnimCusOut1 = 0;
    private int mAnimSysType = FragmentTransaction.TRANSIT_FRAGMENT_FADE;
    //以下为Fragment初始化相关的。
    private String mFragName = null;
    private String mFragTag = null;
    private Fragment mFrag = null, mTempTarget = null;
    private Bundle mFragArg = null;
    private int mContentId = -1;
//    private BackModel mTempModel = null;


    public FragmentOption(Fragment fragment, String tag) {
        if (fragment != null) {
            mFrag = fragment;
            mFragTag = tag;
            mFragName = fragment.getClass().getName();
            if (mFragTag == null) {
                mFragTag = mFrag.getTag();
            }
            mFragArg = mFrag.getArguments();
            this.initOtherSetting();
        }
    }

    public FragmentOption(String fragName, Bundle fragArg) {
        this(fragName, null, fragArg);
    }

    public FragmentOption(Class<? extends Fragment> fragCls, Bundle fragArg) {
        this(fragCls.getName(), null, fragArg);
    }

    public FragmentOption(Class<? extends Fragment> fragCls, String fragTag, Bundle fragArg) {
        this(fragCls.getName(), fragTag, fragArg);
    }

    public FragmentOption(String fragName, String fragTag, Bundle fragArg) {
        this.mFragName = fragName;
        this.mFragTag = fragTag;
        this.mFragArg = fragArg;
        this.initOtherSetting();
    }

    private void initOtherSetting(){
        fragEnums.addEnum(FragJumperEnum.FRAG_BACKSTACK)
                .addEnum(FragJumperEnum.FRAG_ADD).addEnum(FragJumperEnum.FRAG_LOSS_STATE)
                .addEnum(FragJumperEnum.FRAG_EXECUTE_NOW).addEnum(FragJumperEnum.FRAG_ANIM_DEFAULT)
                .addEnum(FragJumperEnum.FRAG_UNIQUEUE_INSTANCE);
    }



    public void setFragment(String fragName) {
        mFragName = fragName;
    }

    public void setTag(String fragTag) {
        mFragTag = fragTag;
    }

    public void setArgment(Bundle arg) {
        mFragArg = arg;
    }

    public boolean isValid() {
        return mFrag != null || StringUtil.isNotEmpty(mFragName);
    }


    public static Fragment creatFrag(Context cx, String fragName, Bundle arg) {
        if (cx != null && StringUtil.isNotEmpty(fragName)) {
            if (arg == null) {
                arg = new Bundle();
            }
            return Fragment.instantiate(cx, fragName, arg);
        }
        return null;
    }

    public static void setDefaultAnim(int animIn, int animOut) {
        DEF_ANIM_IN = animIn;
        DEF_ANIM_OUT = animOut;
    }

    public static void setDefaultAnim(int enterIn, int enterOut, int exitIn, int exitOut) {
        DEF_ANIM_IN1 = exitIn;
        DEF_ANIM_OUT1 = exitOut;
        setDefaultAnim(enterIn, enterOut);
    }

    /**
     * 设置fragment即将加入的容器id.默认为android.R.id.content.
     *
     * @param id
     */
    public void setContentId(int id) {
        mContentId = id;
    }

//    public void setBackModel(BackModel model) {
//        mTempModel = model;
//    }

    public void setTargetFragment(Fragment target) {
        mTempTarget = target;
    }

    public Fragment getFrag(Context cx) {
        if (mFrag == null && cx != null) {
            mFrag = creatFrag(cx, mFragName, mFragArg);
        }
        if (mFrag != null) {
//            if (mTempModel != null && mFrag instanceof IBackablePage) {
//                ((IBackablePage) mFrag).setBackModel(mTempModel);
//                mTempModel = null;
//            }
            if (mTempTarget != null) {
                mFrag.setTargetFragment(mTempTarget, -1);
                mTempTarget = null;
            }
        }
        return mFrag;
    }

    /**
     * 设置用户自定义的转场动画anim id.
     */
    public void setCustomAnim(int animIn, int animOut) {
        mAnimCusIn = animIn;
        mAnimCusOut = animOut;
        mFlag.subFlag(FRAG_ANIM_SYSTEM);
        mFlag.addFlag(FRAG_ANIM_CUSTOM);
    }

    /**
     * 设置用户自定义的转场动画anim id.
     */
    public void setCustomAnim(int enterIn, int enterOut, int exitIn, int exitOut) {
        mAnimCusIn1 = exitIn;
        mAnimCusOut1 = exitOut;
        setCustomAnim(enterIn, enterOut);
    }

    /**
     * 设置fragment转场动画FragmentTransaction预置常量。
     *
     * @param TRANSIT_FRAGMENT_X FragmentTransaction .TRANSIT_FRAGMENT_常量
     */
    public void setAnimSystem(int TRANSIT_FRAGMENT_X) {
        mAnimSysType = TRANSIT_FRAGMENT_X;
        if (mAnimSysType != 0) {
            mFlag.subFlag(FRAG_ANIM_CUSTOM);
            mFlag.addFlag(FRAG_ANIM_SYSTEM);
        }
    }

    /**
     * 增加Frag标志。
     *
     * @param flag
     */
    public void addAction(int flag) {
        flag &= FLAG_USER_ACCESS;
        mFlag.addFlag(flag);
    }

    /**
     * 移除Frag标志。
     *
     * @param flag
     */
    public void subAction(int flag) {
        flag &= FLAG_USER_ACCESS;
        mFlag.subFlag(flag);
    }

    /**
     * 设置Frag标志，会忽略原来的所有标志。
     *
     * @param flag
     */
    public void setAction(int flag) {
        flag &= FLAG_USER_ACCESS;
        mFlag.setFlag(flag | (mFlag.insetFlag(FRAG_ANIM_CUSTOM | FRAG_ANIM_SYSTEM)));
    }

    public int getFlag() {
        return mFlag.getFlag();
    }

    public boolean hasFlag(int flag) {
        return mFlag.hasFlag(flag);
    }

    protected boolean hasBackStack() {
        return mFlag.hasFlag(FRAG_BACKSTACK);
    }

    protected boolean isAddWay() {
        return mFlag.hasFlag(FRAG_ADD);
    }

    public String getTag() {
        return StringUtil.isEmpty(mFragTag) ? mFragName : mFragTag;
    }

    public String getFragName() {
        if (StringUtil.isNotEmpty(mFragName)) {
            return mFragName;
        } else if (mFrag != null) {
            return mFrag.getClass().getName();
        } else {
            return StringUtil.isEmpty(mFragTag) ? "" : mFragTag;
        }
    }

    public Bundle getFragArg() {
        return mFragArg;
    }

    protected boolean addTransAnim(FragmentTransaction ft) {
        int in = 0, out = 0, in1 = 0, out1 = 0;
        if (mFlag.hasFlag(FRAG_ANIM_CUSTOM)) {
            in = mAnimCusIn;
            out = mAnimCusOut;
            in1 = mAnimCusIn1;
            out1 = mAnimCusOut1;
        } else if (mFlag.hasFlag(FRAG_ANIM_DEFAULT)) {
            in = DEF_ANIM_IN;
            out = DEF_ANIM_OUT;
            in1 = DEF_ANIM_IN1;
            out1 = DEF_ANIM_OUT1;
        }
        if (mFlag.hasFlag(FRAG_ANIM_SYSTEM)) {
            ft.setTransition(mAnimSysType);
            return true;
        } else {
            boolean anim1 = (in != 0 && out != 0);
            boolean anim2 = (in1 != 0 && out1 != 0);
            if (anim1 || anim2) {
                if (anim1 && anim2) {
                    ft.setCustomAnimations(in, out, in1, out1);
                } else {
                    ft.setCustomAnimations(in, out);
                }
                return true;
            }
        }
        return false;
    }

    protected Fragment applyFrag(FragmentTransaction ft, Context cx,
                                 int contentId) {
        int id = mContentId < 0 ? contentId : mContentId;
        Fragment frag = getFrag(cx);
        if (mFlag.hasFlag(FRAG_ADD)) {
            if (id == 0) {
                ft.add(frag, getTag());
            } else {
                ft.add(id, frag, getTag());
            }
        } else {
            ft.replace(id == 0 ? contentId : id, frag, getTag());
        }
        return frag;
    }

    protected boolean addBackStack(FragmentTransaction ft) {
        if (hasBackStack()) {
            ft.addToBackStack(getTag());
            return true;
        }
        return false;
    }

    protected boolean commit(FragmentTransaction ft) {
        if (mFlag.hasFlag(FRAG_LOSS_STATE)) {
            ft.commitAllowingStateLoss();
        } else {
            ft.commit();
        }
        return mFlag.hasFlag(FRAG_EXECUTE_NOW);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (mFrag != null) {
            if (StringUtil.isEmpty(mFragTag)) {
                mFragTag = mFrag.getTag();
            }
            if (StringUtil.isEmpty(mFragName)) {
                mFragName = mFrag.getClass().getName();
            }
//            if (mFrag instanceof IBackablePage) {
//                mTempModel = ((IBackablePage) mFrag).getBackModel();
//            }
        }
        dest.writeSerializable(this.mFlag);
        dest.writeInt(this.mAnimCusIn);
        dest.writeInt(this.mAnimCusOut);
        dest.writeInt(this.mAnimCusIn1);
        dest.writeInt(this.mAnimCusOut1);
        dest.writeInt(this.mAnimSysType);
        dest.writeString(this.mFragName);
        dest.writeString(this.mFragTag);
        dest.writeBundle(mFragArg);
        dest.writeInt(this.mContentId);
//        dest.writeParcelable(this.mTempModel, 0);
    }

    private FragmentOption(Parcel in) {
        this.mFlag = (Flag) in.readSerializable();
        this.mAnimCusIn = in.readInt();
        this.mAnimCusOut = in.readInt();
        this.mAnimCusIn1 = in.readInt();
        this.mAnimCusOut1 = in.readInt();
        this.mAnimSysType = in.readInt();
        this.mFragName = in.readString();
        this.mFragTag = in.readString();
        mFragArg = in.readBundle();
        this.mContentId = in.readInt();
//        this.mTempModel = in.readParcelable(BackModel.class.getClassLoader());
    }

    public static final Creator<FragmentOption> CREATOR = new Creator<FragmentOption>() {
        @Override
        public FragmentOption createFromParcel(Parcel source) {
            return new FragmentOption(source);
        }

        @Override
        public FragmentOption[] newArray(int size) {
            return new FragmentOption[size];
        }
    };

    @Override
    public String toString() {
        JSONObject json = toJSON();
        try {
            return json.toString(2);
        } catch (JSONException ignored) {
            return json.toString();
        }
    }

    @Override
    public JSONObject toJSON() {
        JsonUtils.JSONBuilder jb = JsonUtils.build();
        if (StringUtil.isNotEmpty(mFragName)) {
            jb.put("fragName", mFragName);
        } else if (mFrag != null) {
            jb.put("fragName", mFrag.getClass().getSimpleName());
        }
        if (StringUtil.isNotEmpty(mFragTag)) {
            jb.put("fragTag", mFragTag);
        }
        if (mFlag.getFlag() != 0) {
            jb.put("fragFlag", mFlag.toString());
        }

//        jb.put("bundle", Logger.dumpBundle(mFragArg));
        jb.put("bundle", dumpBundle(mFragArg));

        if (mTempTarget != null) {
            jb.put("targetFrag", mTempTarget.getClass().getSimpleName());
        }

//        if (mTempModel != null) {
//            jb.put("backModel", mTempModel);
//        }
        return jb.toJSON();

    }

    public static JSONObject dumpBundle(Bundle bundle) {
        JsonUtils.JSONBuilder jb = JsonUtils.build();
        if (bundle != null) {
            try {
                if (bundle.getClassLoader() == null) {
                    bundle.setClassLoader(FragmentOption.class.getClassLoader());
                }
                if (!bundle.isEmpty()) {
                    for (String key : bundle.keySet()) {
                        Object value = bundle.get(key);
                        if (value instanceof JsonUtils.Jsonable) {
                            jb.put(key, ((JsonUtils.Jsonable) value).toJSON());
                        } else if (value instanceof Map) {
                            jb.put(key, new JSONObject(((Map) value)));
                        } else {
                            jb.put(key, value);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jb.toJSON();
    }
}