package com.vonallin.lib.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

import com.vonallin.lib.base.util.StringUtil;
import com.vonallin.lib.jump.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
public class FragmentUtility {
    public static void replaceFragment(FragmentManager fragmentManager, Fragment targetFragment, String tag) {
        replaceFragment(fragmentManager, targetFragment, android.R.id.content, tag);
    }

    public static void replaceFragment(FragmentManager fragmentManager, Fragment targetFragment, int resId, String tag) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(resId, targetFragment, tag);
        transaction.commitAllowingStateLoss();
    }


    public static void initFragment(FragmentManager fragmentManager, Fragment targetFragment, String tag) {
        initFragment(fragmentManager, targetFragment, tag, android.R.id.content);
    }

    public static void initFragment(FragmentManager fragmentManager, Fragment targetFragment, String tag, int postion) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(postion, targetFragment, tag);
        transaction.commitAllowingStateLoss();
    }

    public static void initFragment(FragmentManager fragmentManager, Fragment targetFragment, String tag, int postion, int animIn, int animOut, int animCloseIn, int animCloseOut) {
        if (animIn < 0) {
            animIn = 0;
        }
        if (animOut < 0) {
            animOut = 0;
        }
        if (animCloseIn < 0) {
            animCloseIn = 0;
        }
        if (animCloseOut < 0) {
            animCloseOut = 0;
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(animIn, animOut, animCloseIn, animCloseOut);
        transaction.add(postion, targetFragment, tag);
        transaction.commitAllowingStateLoss();
    }


    public static void addFragment(FragmentManager supportFragmentManager, Fragment baseDialogFragment, String tag) {
        addFragment(supportFragmentManager, baseDialogFragment, Window.ID_ANDROID_CONTENT, tag);
    }

    public static void addFragment(FragmentManager supportFragmentManager, Fragment baseDialogFragment, int content, String tag) {
        FragmentTransaction transaction = supportFragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.allin_slide_left_in, R.anim.allin_slide_left_out, R.anim.allin_slide_right_in, R.anim.allin_slide_right_out);
        Fragment fragment = supportFragmentManager.findFragmentById(content);
        if (fragment != null) {
            if (fragment instanceof FragmentManager.OnBackStackChangedListener) {
                supportFragmentManager.addOnBackStackChangedListener((FragmentManager.OnBackStackChangedListener) fragment);
            }
            transaction.hide(fragment);
        }
        transaction.add(content, baseDialogFragment, tag);
        transaction.addToBackStack(tag);
        transaction.commitAllowingStateLoss();
    }

    public static void addFragment(FragmentManager supportFragmentManager, Fragment baseDialogFragment, int content, String tag, int animIn, int animOut, int animCloseIn, int animCloseOut) {
        FragmentTransaction transaction = supportFragmentManager.beginTransaction();
        transaction.setCustomAnimations(animIn, animOut, animCloseIn, animCloseOut);
        Fragment fragment = supportFragmentManager.findFragmentById(content);
        if (fragment != null) {
            if (fragment instanceof FragmentManager.OnBackStackChangedListener) {
                supportFragmentManager.addOnBackStackChangedListener((FragmentManager.OnBackStackChangedListener) fragment);
            }
            transaction.hide(fragment);
        }
        transaction.add(content, baseDialogFragment, tag);
        transaction.addToBackStack(tag);
        transaction.commitAllowingStateLoss();
    }

    public static void addWithoutAnimFragment(FragmentManager supportFragmentManager, Fragment baseDialogFragment, String tag) {
        addWithoutAnimFragment(supportFragmentManager, baseDialogFragment, Window.ID_ANDROID_CONTENT, tag);
    }

    public static void addWithoutAnimFragment(FragmentManager supportFragmentManager, Fragment baseDialogFragment, int content, String tag) {
        FragmentTransaction transaction = supportFragmentManager.beginTransaction();
        transaction.add(content, baseDialogFragment, tag);
        transaction.addToBackStack(tag);
        transaction.commitAllowingStateLoss();
    }

    public static void addWithoutStackFragment(FragmentManager supportFragmentManager, Fragment baseDialogFragment, int content, String tag) {
        FragmentTransaction transaction = supportFragmentManager.beginTransaction();
        transaction.add(content, baseDialogFragment, tag);
        transaction.commitAllowingStateLoss();
    }


    /**
     * 新的addFragment方法，更多的配置
     */
    public static void pushFragment(FragmentManager fragmentManager, Fragment targetFragment, int resId, String tag) {
        pushFragment(fragmentManager, targetFragment, resId, tag, true, true, false, false, false, null);
    }


    /**
     * 新的addFragment方法，更多的配置
     *
     * @param fragmentManager
     * @param targetFragment
     * @param resId
     * @param tag
     * @param isAddBackStack
     * @param animated
     * @param isCommitAtOnce
     * @param isProtectedAddedRepeated
     * @param hideTargetFragment
     * @param lastFragmentTag
     */


    public static void pushFragment(FragmentManager fragmentManager, Fragment targetFragment,
                                    int resId, String tag,
                                    boolean isAddBackStack, boolean animated, boolean isCommitAtOnce,
                                    boolean isProtectedAddedRepeated, boolean hideTargetFragment, String lastFragmentTag) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //是否执行动画
        if (animated) {
            transaction.setCustomAnimations(R.anim.allin_slide_left_in, R.anim.allin_slide_left_out, R.anim.allin_slide_right_in, R.anim.allin_slide_right_out);
        }
        //是否隐藏添加的fragment
        if (hideTargetFragment) {
            transaction.hide(targetFragment);
        }


        //隐藏当前显示的fragment
        //使用mAdded获取栈顶，如果是连续push是没有办法拿到的，一定要用findById
        //但是如果findById会把隐藏的也给拿出来，比如 p1->p2->p1 这个时候获取是p2,所以tab切换有问题
        //因此就用lastTag获取是最保险的
//        Fragment topFragment = FragmentUtility.getTopFragment(fragmentManager);
        Fragment topFragment = null;
        if (StringUtil.isEmpty(lastFragmentTag)) {
            topFragment = fragmentManager.findFragmentById(resId);
        } else {
            topFragment = fragmentManager.findFragmentByTag(lastFragmentTag);
        }

        if (topFragment != null) {
            if (topFragment instanceof FragmentManager.OnBackStackChangedListener) {
                fragmentManager.addOnBackStackChangedListener((FragmentManager.OnBackStackChangedListener) topFragment);
            }
            transaction.hide(topFragment);
        }

        Fragment __addedFragment = null;
        //是否要防止重复添加，主要是tab切换用到
        //有tag就用tag去找，如果没有tag则用对象比较
        if (isProtectedAddedRepeated) {
            if (StringUtil.isNotEmpty(tag)) {
                __addedFragment = fragmentManager.findFragmentByTag(tag);
            } else {
                __addedFragment = FragmentUtility.getFragment(fragmentManager, targetFragment);
            }
        }
        //找到则show出来，没有找到就添加进去
        if (__addedFragment == null) {
            transaction.add(resId, targetFragment, tag);
        } else {
            transaction.show(__addedFragment);

        }
        //是否添加到返回栈中
        if (isAddBackStack) {
            transaction.addToBackStack(tag);
        }
        transaction.commitAllowingStateLoss();
        //是否立即执行，主要是push多个，我觉得这个慎用啊
        if (isCommitAtOnce) {
            fragmentManager.executePendingTransactions();
        }
    }


    /**
     * 移除Fragment
     *
     * @param fragmentManager
     * @param tag
     */
    public static void removeFragment(FragmentManager fragmentManager, String tag) {
        if (fragmentManager != null) {
            try {
                if (fragmentManager != null && fragmentManager.findFragmentByTag(tag) != null) {
                    fragmentManager.popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
            } catch (Exception e) {
            }
            Fragment targetFragment = fragmentManager.findFragmentByTag(tag);
            if (targetFragment != null) {
                FragmentTransaction localFragmentTransaction = fragmentManager.beginTransaction();
                localFragmentTransaction.remove(targetFragment);
                localFragmentTransaction.commitAllowingStateLoss();
                fragmentManager.executePendingTransactions();
            }
        }
    }

    /**
     * 移除Fragment
     *
     * @param fragmentManager
     * @param targetFragment
     */
    public static void removeFragment(FragmentManager fragmentManager, Fragment targetFragment) {
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
            } catch (Exception e) {
            }
        }
    }

    //获取fragment的堆栈
    public static ArrayList<Fragment> getAllFragments(FragmentManager fragmentMgr) {
        ArrayList<Fragment> fragments = null;
        if (fragmentMgr == null) {
            return null;
        }
        try {
            Class<?> clazz = fragmentMgr.getClass();
            Field field = clazz.getDeclaredField("mAdded");
            field.setAccessible(true);
            fragments = new ArrayList<Fragment>();
            fragments.addAll((ArrayList<Fragment>) field.get(fragmentMgr));
        } catch (Exception e) {
        }

        return fragments;
    }

    public static Fragment getTopFragment(FragmentManager manager) {
        List viewController = FragmentUtility.getAllFragments(manager);
        for (int i = viewController.size() - 1; i >= 0; i--) {
            Fragment __fragment = (Fragment) viewController.get(i);
            if (__fragment != null && __fragment.isVisible()) {
                return __fragment;
            }
        }
        return null;
    }

    public static Fragment getFirstFragment(FragmentManager manager) {
        List viewController = FragmentUtility.getAllFragments(manager);
        Fragment __fragment = (Fragment) viewController.get(0);
        return __fragment;
    }

    public static Fragment getFragment(FragmentManager manager, Fragment fragment) {
        List viewController = FragmentUtility.getAllFragments(manager);
        for (int i = viewController.size() - 1; i >= 0; i--) {
            Fragment __viewController = (Fragment) viewController.get(i);
            if (__viewController != null && __viewController == fragment) {
                return __viewController;
            }
        }
        return null;
    }
}