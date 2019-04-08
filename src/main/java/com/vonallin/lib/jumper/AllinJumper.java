package com.vonallin.lib.jumper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.vonallin.lib.base.util.StringUtil;
import com.vonallin.lib.util.ActivityUtil;

public class AllinJumper {
    public static final String KEY_FRAG_OPTION = "KEY_FRAG_OPTION";
    public static final String KEY_EXIT_NOANIM = "KEY_EXIT_NOANIM";
    public static final String KEY_AUTO_NET = "KEY_AUTO_NET";
    public static final String KEY_ATY_STYLE = "KEY_ATY_STYLE";
    public static final String KEY_ATY_ANIM = "KEY_ATY_ANIM";
    public static final String KEY_FRAGMENT_INSTACE = "fragment_instace";

    /**
     * @param atyOrFrag must be a Fragment or a IActivityCallback object.
     */
    private static Fragment goNextFragment(Object atyOrFrag, FragmentOption option) {
        return null;
    }



    /**
     * 通过tag方式查找并移除Fragment
     *
     * @param currentFragment 源Fragment，操作源.
     * @param tag 要移除Fragment的tag.
     */
    public static void removeFragment(Fragment currentFragment, String tag) {
        removeFragment((Object) currentFragment, tag);
    }


    public static void removeFragment(Activity currentActivity, String tag) {
        removeFragment((Object) currentActivity, tag);
    }


    /**
     * @param atyOrFrag must be a Fragment or a IActivityCallback object.
     */
    private static void removeFragment(Object atyOrFrag, String tag) {
        FragmentActivity aty = null;
        FragmentManager fragmentManager = null;
        if (atyOrFrag instanceof FragmentActivity) {
            aty = (FragmentActivity) atyOrFrag;
            fragmentManager = aty.getSupportFragmentManager();
        } else if (atyOrFrag instanceof Fragment) {
            aty = ((Fragment) atyOrFrag).getActivity();
            fragmentManager = ((Fragment) atyOrFrag).getFragmentManager();
        }
        if (fragmentManager != null) {
            try {
                if (fragmentManager != null && fragmentManager.findFragmentByTag(tag) != null) {
                    fragmentManager.popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
            Fragment targetFragment = fragmentManager.findFragmentByTag(tag);
            if (targetFragment != null) {
                FragmentTransaction localFragmentTransaction = fragmentManager.beginTransaction();
                localFragmentTransaction.remove(targetFragment);
                localFragmentTransaction.commitAllowingStateLoss();
                fragmentManager.executePendingTransactions();
            }
//            if (aty instanceof IActivityCallback && ((IActivityCallback) aty).getFragmentExchanger() != null) {
//                ((IActivityCallback) aty).getFragmentExchanger().onBackStackChanged();
//            }
        }
    }


    /**
     * 通过tag方式查找并移除Fragment
     *
     * @param currentFragment 源Fragment，操作源.
     * @param targetFragment 要移除的Fragment.
     */
    public static void removeFragment(Fragment currentFragment, Fragment targetFragment) {
        removeFragment((Object) currentFragment, targetFragment);
    }

    public static void removeFragment(Activity currentActivity, Fragment targetFragment) {
        removeFragment((Object) currentActivity, targetFragment);
    }


    /**
     * @param atyOrFrag must be a Fragment or a IActivityCallback object.
     */
    private static void removeFragment(Object atyOrFrag, Fragment targetFragment) {
        FragmentActivity aty = null;
        FragmentManager fragmentManager = null;
        if (atyOrFrag instanceof FragmentActivity) {
            aty = (FragmentActivity) atyOrFrag;
            fragmentManager = aty.getSupportFragmentManager();
        } else if (atyOrFrag instanceof Fragment) {
            aty = ((Fragment) atyOrFrag).getActivity();
            fragmentManager = ((Fragment) atyOrFrag).getFragmentManager();
        }
        if (fragmentManager != null) {
            String tag = targetFragment.getTag();
            try {
                try {
                    if (fragmentManager != null && fragmentManager.findFragmentByTag(tag) != null) {
                        fragmentManager.popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                } catch (Exception e) {
                }
                FragmentTransaction localFragmentTransaction = fragmentManager.beginTransaction();
                localFragmentTransaction.remove(targetFragment);
                localFragmentTransaction.commitAllowingStateLoss();
                fragmentManager.executePendingTransactions();

                Fragment fragment = fragmentManager.findFragmentByTag(tag);
                if (fragment != null) {
                    localFragmentTransaction.remove(fragment);
                    localFragmentTransaction.commitAllowingStateLoss();
                    fragmentManager.executePendingTransactions();
                }
//                if (aty instanceof IActivityCallback && ((IActivityCallback) aty).getFragmentExchanger() != null) {
//                    ((IActivityCallback) aty).getFragmentExchanger().onBackStackChanged();
//                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * 确保安全启动activity.
     *
     * @param atyOrFrag atyOrFrag为Fragment或Activity或Context.
     * @param intent not null.
     * @param fragmentOption 如果有效，目标Activity自行获取处理
     * @param requestCode 大于0且currentContext为Activity会以startActivityFroResutl方式启动。
     */
    private static void goNextActivity(Object atyOrFrag, Intent intent, FragmentOption fragmentOption, int requestCode) {
        try {
//            tryRedirectActivityJump(fragmentOption, intent);
            if (fragmentOption != null) {
                intent.putExtra(KEY_FRAG_OPTION, fragmentOption);
            }
            boolean launched = false, forResult = requestCode >= 0;
            Context cx = null;
            if (atyOrFrag instanceof Activity) {
                if ((launched = forResult)) {
                    ((Activity) atyOrFrag).startActivityForResult(intent, requestCode);
                } else {
                    cx = (Context) atyOrFrag;
                }
            } else if ((launched = atyOrFrag instanceof Fragment)) {
                Fragment frag = (Fragment) atyOrFrag;
                if (forResult) {
                    frag.startActivityForResult(intent, requestCode);
                } else {
                    frag.startActivity(intent);
                }
            } else if (atyOrFrag instanceof Context) {
                cx = (Context) atyOrFrag;
            }
            if (!launched && cx != null) {
                cx.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static JumperBuilder from(Activity aty) {
        return new JumperBuilder(aty);
    }

    public static JumperBuilder from(Context context) {
        return new JumperBuilder(context);
    }

    public static JumperBuilder from(Fragment fragment) {
        return new JumperBuilder(fragment);
    }

    public static final void jumpPageByClasName(Context context, String fragmentClassName) {
        jumpPageByClsName(context, fragmentClassName, true);
    }

    public static final void jumpPageByClsName(Context context, String className, boolean isFragment) {
        if (isFragment) {
            jumpPageByClsName(context, className, null, null);
        } else {
            jumpPageByClsName(context, null, className, null);
        }
    }

    public static final void jumpPageByClsName(Context context, String fragmentClassName, String activityClassName, String params) {

//        if (context == null) {
//            context = BaseApplication.topActivity;
//        }
        if (context == null || StringUtil.isEmpty(fragmentClassName) && StringUtil.isEmpty(activityClassName)) {
            return;
        }

        Class<? extends Activity> activityClazz = null;
        Class<? extends Fragment> fragment = null;
        try {
            activityClazz = (Class<? extends Activity>) Class.forName(activityClassName);
        } catch (Exception e) {
        }
        try {
            fragment = (Class<? extends Fragment>) Class.forName(fragmentClassName);
        } catch (Exception e) {
        }
        Bundle bundle = parseBundle(params);
        JumperBuilder builder = from(context);
        if (fragment != null) {
            if (bundle != null) {
                builder = builder.setArgment(bundle);
            }
            if (activityClazz != null) {
                builder.setFragment(fragment).goActivity(activityClazz);
            } else {
                builder.goFragment(fragment);
            }
        } else if (activityClazz != null) {
            builder.goActivity(activityClazz);
        }
    }


    private static final Bundle parseBundle(String params) {
        Bundle bundle = null;
        if (StringUtil.isNotEmpty(params)) {
            bundle = new Bundle();
//            try {
            bundle.putString("parameters",params);


//                JSONObject jsonObj = (JSONObject) JSON.parse(params);
//                Set<String> keys = jsonObj.keySet();
//                Iterator<String> iterator = keys.iterator();
//                while (iterator.hasNext()) {
//                    String key = iterator.next();
//                    Object value = jsonObj.get(key);
//                    LuglobalRouterUtil.putBundleValue(bundle, key, value);
//                }
//            } catch (Exception e) {
//            }
        }
        return bundle;
    }

    public static class JumperBuilder {
        public static final int SLIDE_IN_FROM_RIGHT = 0;
        public static final int SLIDE_IN_FROM_BOTTOM = 1;
        private Object mTarget = null;
        private int mAtyTheme = 0;
        private int mAtyAnimStyle = SLIDE_IN_FROM_RIGHT; //进入动画类型 0 从右向左划出，1从下向上划出， 默认0
        private Activity mActivity = null;
        private Fragment mFragment = null;
        private Context mContext = null, mTempContext;

        private Bundle mArgment = null;
        private FragmentOption mFragmentOption = null;
        //fragment跳转默认行为
        private int mGoAction = FragmentOption.FRAG_ADD | FragmentOption.FRAG_BACKSTACK | FragmentOption.FRAG_LOSS_STATE | FragmentOption.FRAG_ANIM_DEFAULT;
        //fragment初始化默认行为
        private int mInitAction = FragmentOption.FRAG_ADD | FragmentOption.FRAG_LOSS_STATE;
        //replace fragment默认行为
        private int mReplaceAction = FragmentOption.FRAG_LOSS_STATE;

        private JumperBuilder(Activity activity) {
            mActivity = activity;
            ensureTempContext(activity);
        }

        private JumperBuilder(Fragment fragment) {
            mFragment = fragment;
            ensureTempContext(fragment);
        }

        private JumperBuilder(Context context) {
            mContext = context;
            ensureTempContext(context);
        }

        public JumperBuilder setArgment(Bundle bundle) {
            mArgment = bundle;
            if (mFragmentOption != null) {
                mFragmentOption.setArgment(mArgment);
            }
            return JumperBuilder.this;
        }

        public JumperBuilder setFragment(Class<? extends Fragment> fragCls) {
            FragmentOption opt = ensureFragmentOption();
            opt.setFragment(fragCls.getName());
            return JumperBuilder.this;
        }

        public JumperBuilder setFragment(String fragName) {
            FragmentOption opt = ensureFragmentOption();
            opt.setFragment(fragName);
            return JumperBuilder.this;
        }

        @Deprecated
        public JumperBuilder setFragmentOption(FragmentOption fragmentOption) {
            mFragmentOption = fragmentOption;
            return JumperBuilder.this;
        }

        public JumperBuilder setContentId(int contentId) {
            FragmentOption opt = ensureFragmentOption();
            opt.setContentId(contentId);
            return JumperBuilder.this;
        }

        public JumperBuilder setCustAnim(int enterIn, int enterOut, int exitIn, int exitOut) {
            FragmentOption opt = ensureFragmentOption();
            opt.setCustomAnim(enterIn, enterOut, exitIn, exitOut);
            return JumperBuilder.this;
        }

        public JumperBuilder setSystemAnim(int TRANSIT_FRAGMENT_X) {
            FragmentOption opt = ensureFragmentOption();
            opt.setAnimSystem(TRANSIT_FRAGMENT_X);
            return JumperBuilder.this;
        }

//        public JumperBuilder setBackModel(BackModel backModel) {
//            FragmentOption opt = ensureFragmentOption();
//            opt.setBackModel(backModel);
//            return JumperBuilder.this;
//        }

        public JumperBuilder setTarget(Fragment target) {
            FragmentOption opt = ensureFragmentOption();
            opt.setTargetFragment(target);
            return JumperBuilder.this;
        }

        public JumperBuilder setTheme(int theme) {
            this.mAtyTheme = theme;
            return JumperBuilder.this;
        }

        public JumperBuilder setAnimStyle(int animStyle) {
            this.mAtyAnimStyle = animStyle;
            return JumperBuilder.this;
        }

        public JumperBuilder setTag(String tag) {
            FragmentOption opt = ensureFragmentOption();
            opt.setTag(tag);
            return JumperBuilder.this;
        }

        /**
         * @param fragmentOption 只当从外部传入才可能不为空。
         * @return
         */
        public Fragment goFragmentByOption(FragmentOption fragmentOption) {
            Fragment result = null;
            FragmentOption target = mFragmentOption;
            if (fragmentOption != null) {
                target = fragmentOption;
                //如果是从外部传入的FragmentOption , 有必要替换Argment.
                if (target != null && mArgment != null) {
                    result = target.getFrag(null);//不这空说明是以Fragment为参数传入的。
                    if (result != null && (result.getArguments() == null || result.getArguments().isEmpty())) {
                        result.setArguments(mArgment);
                    }
                    result = null;
                }
            }
            if (mTarget != null && target != null && target.isValid()) {
                result = AllinJumper.goNextFragment(mTarget, target);
                clean();
            }
            return result;
        }

        /**
         * fragment跳转
         * 1，add；2，有默认动画；3，addBackStack；4，commitAllowingStateLoss
         *
         * @param fragCls
         * @return
         */
        public Fragment goFragment(Class<? extends Fragment> fragCls) {
            FragmentOption option = ensureFragmentOption();
            option.setAction(mGoAction);
            return setFragment(fragCls).goFragmentByOption(null);
        }

        /**
         * fragment初始化
         * 用于activity创建时初始化第一个fragment
         * 1，add；2，无动画；3，不会添加到backStack中；4，commitAllowingStateLoss
         *
         * @param fragmentClass
         * @return
         */
        public Fragment initFragment(Class<? extends Fragment> fragmentClass) {
            FragmentOption option = ensureFragmentOption();
            option.setAction(mInitAction);
            return setFragment(fragmentClass).goFragmentByOption(null);
        }

        /**
         * 1,replace；2，无动画；3，不会添加到backStack中；4，commitAllowingStateLoss
         *
         * @param fragmentClass
         * @return
         */
        public Fragment replaceFragment(Class<? extends Fragment> fragmentClass) {
            FragmentOption option = ensureFragmentOption();
            option.setAction(mReplaceAction);
            return setFragment(fragmentClass).goFragmentByOption(null);
        }

        public void goActivity(Class<? extends Activity> nextActivityClass) {
            goActivityForResult(nextActivityClass, 0, -1);
        }

        public void goActivity(Class<? extends Activity> nextActivityClass, int activityFlag) {
            goActivityForResult(nextActivityClass, activityFlag, -1);
        }

        public void goActivity(Intent intent, int requestCode) {
            if (mTarget != null) {
                if (mAtyTheme != 0) {
                    intent.putExtra(KEY_ATY_STYLE, mAtyTheme);
                }
                if (mAtyAnimStyle != SLIDE_IN_FROM_RIGHT) {
                    intent.putExtra(KEY_ATY_ANIM, mAtyAnimStyle);
                }
                if (mFragmentOption != null && !mFragmentOption.isValid()) {
                    //兼容老框架在extras中设置目的Fragment.
                    if (!validateFragmentFromExtras()) {
                        mFragmentOption = null;
                    }
                }
                if (mArgment != null && mFragmentOption == null) {
                    intent.putExtras(mArgment);
                }
                AllinJumper.goNextActivity(mTarget, intent, mFragmentOption, requestCode);
                clean();
            }
        }

        public void goActivityForResult(Class<? extends Activity> nextActivityClass, int requestCode) {
            goActivityForResult(nextActivityClass, 0, requestCode);
        }

        public void goActivityForResult(Class<? extends Activity> nextActivityClass, int activityFlag, int requestCode) {
            if (mTarget != null && nextActivityClass != null) {
                Intent intent = new Intent(mTempContext, nextActivityClass);
                if (activityFlag != 0) {
                    intent.addFlags(activityFlag);
                }
                goActivity(intent, requestCode);
            }
        }

        private void clean() {
            mTarget = null;
            mActivity = null;
            mFragment = null;
            mContext = mTempContext = null;
            mArgment = null;
            mFragmentOption = null;
        }

        private void ensureTempContext(Object target) {
            mTarget = target;
            if (mActivity != null) {
                mTempContext = mActivity;
            } else if (mFragment != null) {
                mTempContext = mFragment.getActivity();
            } else if (mContext != null) {
                mTempContext = ActivityUtil.findActivityContext(mContext);
            }
//            if (mTempContext == null) {
//                mTempContext = BaseApplication.getApp();
//            }
        }

        private FragmentOption ensureFragmentOption() {
            if (mFragmentOption == null) {
                mFragmentOption = new FragmentOption((String) null, mArgment);
            }
            return mFragmentOption;
        }

        private boolean validateFragmentFromExtras() {
            String fragName = (mArgment == null ? null : mArgment.getString(KEY_FRAGMENT_INSTACE));
            if (StringUtil.isNotEmpty(fragName)) {

                String tagName = mFragmentOption.getTag();

                if (StringUtil.isEmpty(tagName)) {
                    int lastPoint = fragName.lastIndexOf('.');
                    tagName = lastPoint != -1 ? fragName.substring(lastPoint + 1) : fragName;
                }

                mFragmentOption.setFragment(fragName);
                mFragmentOption.setTag(tagName);
                return true;
            }
            return false;
        }



    }
}
