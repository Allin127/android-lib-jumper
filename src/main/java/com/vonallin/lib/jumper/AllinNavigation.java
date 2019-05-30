package com.vonallin.lib.jumper;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.google.gson.Gson;
import com.vonallin.lib.base.util.StringUtil;
import com.vonallin.lib.jump.R;
import com.vonallin.lib.jumper.enums.JumpAnimEnum;
import com.vonallin.lib.jumper.enums.JumpTypeEnum;
import com.vonallin.lib.jumper.exception.ContextNullException;
import com.vonallin.lib.jumper.exception.FragmentCreateException;
import com.vonallin.lib.jumper.exception.OptionsSingletoneTagEmptyException;
import com.vonallin.lib.util.ActivityUtil;
import com.vonallin.lib.util.FragmentUtility;

import java.lang.ref.WeakReference;

import static com.vonallin.lib.jumper.enums.JumpAnimEnum.*;


/*Activity才会持有的跳转控件*/
public class AllinNavigation implements FragmentManager.OnBackStackChangedListener {
    private Object mTarget;
    //三种构造函数对应不同的设置
    private Context mContext;
    private FragmentActivity mActivity;
    private Fragment mFragment;

    // 具体的replace或者add的id
    private int mContentId;

    //对应的fragmentManager,目前只考虑activity的manager
    private FragmentManager mFragmentManager;
    //临时的context设置
    private Context mTempContext;

    //Jumper的基础options
    private FragmentOptions mBasicJumpOptions;

    //上一个fragment
    public WeakReference<Fragment> mPreFragment = null;
    //当前的fragment
    public Fragment mCurrentFragment = null;
    //返回栈的数量
    public int mBackStackCount = 0;

    private AllinNavigation(FragmentActivity activity) {
        mActivity = activity;
        mFragmentManager = mActivity.getSupportFragmentManager();
        mFragmentManager.addOnBackStackChangedListener(this);
        ensureBasicOptions(activity);
        ensureTempContext(activity);
    }

    //基本的跳转参数设置，Activity可以影响基础的
    private void ensureBasicOptions(FragmentActivity activity) {
        if (activity instanceof IActivityJumpOptionsSetting) {
            this.mBasicJumpOptions = ((IActivityJumpOptionsSetting) activity).basicOptionsSetting();
        } else {
            this.mBasicJumpOptions = AllinNavigation.pushFragmentOptions().build();
        }
    }



    //补偿逻辑
    private void ensureTempContext(Object target) {
        mTarget = target;
        if (mActivity != null) {
            mTempContext = mActivity;

        } else if (mFragment != null) {
            mTempContext = mFragment.getActivity();
        } else if (mContext != null) {
            mTempContext = ActivityUtil.findActivityContext(mContext);
        }
    }
    /**
     * 确保安全启动另外的Activity.
     */

    public static String ALLIN_JUMP_FRAGMENT_OPTIONS_KEY = "ALLIN_JUMP_FRAGMENT_OPTIONS_KEY";

    public static void toActivity(Activity activity, Intent intent) {
        AllinNavigation.toActivity(activity,intent,null);
    }

    public static void toActivity(Activity activity, Intent intent, FragmentOptions fragmentOptions) {
        AllinNavigation.toActivity(activity,intent,fragmentOptions,-1);
    }

    public static void toActivity(Activity activity, Intent intent, FragmentOptions fragmentOptions, int requestCode) {
        if (activity == null) {
            throw new ContextNullException("toActivity params activity is null");
        }
        if (intent == null) {
            throw new ContextNullException("toActivity params intent is null");
        }
        if (fragmentOptions != null) {
            intent.putExtra(ALLIN_JUMP_FRAGMENT_OPTIONS_KEY, fragmentOptions.toString());
        }
        if (requestCode >= 0) {
            activity.startActivityForResult(intent, requestCode);
        } else {
            activity.startActivity(intent);
        }
    }


    //    对外启动
    public static AllinNavigation from(FragmentActivity aty) {
        return new AllinNavigation(aty);
    }

    public AllinNavigation contentId(int contentId) {
        this.mContentId = contentId;
        return this;
    }


    //创建一个fragment实例
    public Fragment createFragment(FragmentOptions options) {
        if (this.mTempContext != null && StringUtil.isNotEmpty(options.getFragClazz().getName())) {
            return Fragment.instantiate(mTempContext, options.getFragClazz().getName(), options.getArguments());
        }
        return null;
    }

    //Activity中的第一个fragment
    public AllinNavigation root(Class<? extends Fragment> fragClazz, String tag){
        return to(fragClazz,tag,(builder)-> AllinNavigation.switchFragmentOptions());
    }


    public AllinNavigation to(Class<? extends Fragment> fragClazz) {
        return to(fragClazz, null,null);
    }
    public AllinNavigation to(Class<? extends Fragment> fragClazz, String tag) {
        return to(fragClazz, tag, null);
    }
    public AllinNavigation to(Class<? extends Fragment> fragClazz, IFragmentOptionsSetting settingOptions) {
        return to(fragClazz, null, settingOptions);
    }
    public AllinNavigation to(Class<? extends Fragment> fragClazz, String tag, IFragmentOptionsSetting settingOptions) {
        Fragment fragment = null;
        FragmentOptions options;
        boolean isAttachExist = false;
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        //options顺序决定   IFragmentOptionsSetting > IActivityJumpOptionsSetting > AllinNavigation.pushFragmentOptions
        options = settingOptions != null ? settingOptions.fragmentOptionsBuilding(this.pushFragmentOptions()).build()
                : this.mBasicJumpOptions;
        //设置跳转的目标class
        options.setFragClazz(fragClazz);
        //tag用于backStack的tag和fragment的tag,架构设计上保持一致
        options.setTag(tag);
        //①如果是singleTone的，则通过tag寻找对应的fragment
        if (options.isSingltone()) {
            if (StringUtil.isEmpty(options.getTag())) throw new OptionsSingletoneTagEmptyException("单例共享tag不能为空");
            fragment = mFragmentManager.findFragmentByTag(options.getTag());
            if(fragment!=null) isAttachExist=true;
        }
        //②如果没有找到，则创建一个新的
        if (fragment == null) fragment = createFragment(options);
        //③再没有创建出则抛错
        if (fragment == null) throw new FragmentCreateException("没有找到并且创建的fragment");

        //④事务设置过渡动画
        doAnimAction(fragmentTransaction, options);
        //⑤设置过渡方式
        if (isAttachExist) {
            attachExistFragment(fragmentTransaction, fragment);
        } else {
            addOrReplaceTransition(fragmentTransaction, fragment, options);
        }
//        fragment = navigation(fragmentTransaction, fragment, options);

        //⑥后续操作
        doBackStackAction(fragmentTransaction, options) //是否压入返回栈
                .doCommitAction(fragmentTransaction, options) //提交方式
                .doHidePreviousFragment(fragmentTransaction, options, fragment) //隐藏即将被覆盖或者替换的fragment
                .doExecuteNow(options);//是否立刻执行

        //⑦记录当前装填，主要是更新当前的fragment以及上一个fragment
        onCurrentFragmentChanged(fragment, mCurrentFragment, JumpTypeEnum.SWITCH_TO);
        return this;
    }

    public AllinNavigation pop() {
        return pop(null);
    }

    public AllinNavigation pop(String tag) {
        if (!StringUtil.isEmpty(tag)) {
            if (mFragmentManager != null) {
                if(mFragmentManager.findFragmentByTag(tag) != null) {
                    //比如add的backstack为  back1->back2->back3->back4->back5,
                    //mFragmentManager.popBackStack("back3", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    //则会跳转到back1->back2,flag如果设置为0，则为back1->back2->back3
                    //如果tag相同，则一直会找到最深的stack,以此为pop的点，可以利用这个来pop到某个为止
                    //mFragmentManager.popBackStack(null, 0);= __manager.popBackStack();
                    mFragmentManager.popBackStack(tag, 0);
                }else {
                    mFragmentManager.popBackStack();
                }
            }
        }else {
            mFragmentManager.popBackStack();
        }
        return this;
    }




    //Transaction压入返回栈中
    public AllinNavigation doBackStackAction(FragmentTransaction ft, FragmentOptions fragmentOptions) {
        //此处的tag不是真正意义上fragment的tag,应该是transaction的事务名字，后续可以pop对应的事务出来
        if (fragmentOptions.isAddBackStack())
            ft.addToBackStack(fragmentOptions.getTag());
        return this;
    }


    public AllinNavigation doCommitAction(FragmentTransaction ft, FragmentOptions fragmentOptions) {
        if (fragmentOptions.isCommitLossState()) {
            ft.commitAllowingStateLoss();
        } else {
            ft.commit();
        }
        return this;
    }

    public AllinNavigation doHidePreviousFragment(FragmentTransaction fragmentTransaction, FragmentOptions options, Fragment fragment) {
        if (!options.isPreFragVisible()) {
            if (mCurrentFragment != null && mCurrentFragment != fragment) {
                fragmentTransaction.hide(mCurrentFragment);
            }
        }
        return this;
    }

    public AllinNavigation doExecuteNow(FragmentOptions options) {
        if (options.isExecuteNow()) mFragmentManager.executePendingTransactions();
        return this;
    }

    //singletone下，只要attach&show就行了
    private void attachExistFragment(FragmentTransaction fragmentTransaction, Fragment fragment) {
        if (fragment != null) {
            if (fragment.isDetached()) {
                fragmentTransaction.attach(fragment);
                fragmentTransaction.show(fragment);
            } else {
                fragmentTransaction.show(fragment);
            }
        }
    }

    //新创建的fragment或者tag无法找到的，走入这个流程，执行replace或者add操作
    private void addOrReplaceTransition(FragmentTransaction fragmentTransaction,
                                        Fragment fragment, FragmentOptions fragmentOptions) {
        int id = fragmentOptions.getContentId() == 0 ? mContentId : fragmentOptions.getContentId();
        if (fragmentOptions.isTransactionAdd()) {
            // id 是页面添加的R.id frag就是对应加进去的fragment实例，tag是一个标识，可以通过FragmentManager.findFragmentByTag来获得fragment
            if (id == 0) {
                //id=0添加一个不显示的frag,但是是存在的，可以做一些后台工作
                fragmentTransaction.add(fragment, fragmentOptions.getTag());
            } else {
                fragmentTransaction.add(id, fragment, fragmentOptions.getTag());
            }
        } else {
            fragmentTransaction.replace(id, fragment, fragmentOptions.getTag());
        }
    }




    //设置FragmentTransaction的过渡动画
    private AllinNavigation doAnimAction(FragmentTransaction fragmentTransaction, FragmentOptions fragmentOptions) {
        int in, out, in1, out1;
        if (fragmentOptions.getPUSH_ANIM_IN() > 0 && fragmentOptions.getPUSH_ANIM_OUT() > 0) {
            in = fragmentOptions.getPUSH_ANIM_IN();
            out = fragmentOptions.getPUSH_ANIM_OUT();
            in1 = fragmentOptions.getPOP_ANIM_IN();
            out1 = fragmentOptions.getPOP_ANIM_OUT();
            fragmentTransaction.setCustomAnimations(in, out, in1, out1);
        } else if (fragmentOptions.getAnimType() == ANIM_DEFAULT) {
            in = R.anim.allin_slide_left_in;
            out = R.anim.allin_slide_left_out;
            in1 = R.anim.allin_slide_right_in;
            out1 = R.anim.allin_slide_right_out;
            fragmentTransaction.setCustomAnimations(in, out, in1, out1);
        } else {
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_UNSET);
        }
        return this;
    }


    private Fragment onCurrentFragmentChanged(Fragment current, Fragment pre, JumpTypeEnum jumpType) {
        if (pre != null && pre != current) {
            if (mPreFragment != null) {
                mPreFragment.clear();
            }
            mPreFragment = new WeakReference<Fragment>(pre);
        }
        return (mCurrentFragment = current == null ? pre : current);
    }

    public Fragment findTopVisibleFrag() {
        Fragment mPre = mPreFragment == null ? null : mPreFragment.get();
        if (mPre != null && mPre.isVisible() && mPre.getUserVisibleHint()) {
            return mPre;
        } else {
            return FragmentUtility.getTopFragment(this.mFragmentManager);
        }
    }


    @Override
    //判断当前栈的数量知道当前是推栈还是进栈
    //TODO:这里有bug???
    public void onBackStackChanged() {

        int n = mFragmentManager.getBackStackEntryCount();
        System.out.println(String.format("进入方法时manager栈总数为：%d，对象目前总数为:%d",n,mBackStackCount));
        boolean addBack = n > mBackStackCount;
        mBackStackCount = n;
        System.out.println(addBack?"加入返回栈":"退出返回栈");
        Fragment frag = null;
        if (addBack) {// add back frag.
            String fragTag = mFragmentManager.getBackStackEntryAt(n - 1).getName();
            frag = mFragmentManager.findFragmentByTag(fragTag);
            onCurrentFragmentChanged(frag, mCurrentFragment, JumpTypeEnum.BACK_STATCK_INCREASE);
        } else {// sub back frag.
            frag = findTopVisibleFrag();
            onCurrentFragmentChanged(frag, mCurrentFragment, JumpTypeEnum.BACK_STATCK_DECREASE);
        }
    }


    public static FragmentOptions.FragmentOptionsBuilder pushFragmentOptions() {
        return FragmentOptions.builder()
                .animType(JumpAnimEnum.ANIM_DEFAULT)
                .transactionAdd(true)
                .addBackStack(true)
                .commitLossState(true)
                .preFragVisible(false)
                .isExecuteNow(true)
                ;
    }


    //add&commitNormal/
    public static FragmentOptions.FragmentOptionsBuilder switchFragmentOptions() {
        return FragmentOptions.builder()
                .animType(JumpAnimEnum.ANIM_NONE)
                .transactionAdd(true)
                ;
    }

}



