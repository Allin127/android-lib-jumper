package com.vonallin.lib.jump;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;

import com.vonallin.lib.util.FragmentUtility;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.List;

//public class FragmentExchanger implements OnBackStackChangedListener  {
    public class FragmentExchanger {
//    protected String TAG = "FragmentExchanger";
//    protected FragmentManager mFragmentManager = null;
//    protected FragmentActivity mAty = null;
//    protected int mContentId = -1;
//    protected int mBackStackCount = 0;
//    protected Fragment mCurrentFragment = null;
//    protected WeakReference<Fragment> mPreVisible = null;
//
//    public FragmentExchanger(FragmentActivity aty, int contentId) {
//        this.mAty = aty;
//        this.mContentId = contentId;
//        this.mFragmentManager = aty.getSupportFragmentManager();
//        mFragmentManager.addOnBackStackChangedListener(this);
//    }
//
//    public int getBackStackCount() {
//        return mBackStackCount;
//    }
//
//    public Fragment getCurrentFrag() {
//        if (mCurrentFragment == null) {
//            mCurrentFragment = findTopVisibleFrag();
//        }
//        return mCurrentFragment;
//    }
//
//    private Fragment switchTo(FragmentTransaction fragmentTransaction, Fragment fragment, FragmentOptions fragmentOption) {
//        StringBuilder sb = new StringBuilder("require ");
//        String requireFragName = fragmentOption.getFragName();
//        int index = requireFragName.lastIndexOf('.') + 1;
//        sb.append(index > 0 ? requireFragName.substring(index) : requireFragName).append(" from ");
//        if (fragment != null) {
//            if (fragment.isDetached()) {
//                fragmentTransaction.attach(fragment);
//                fragmentTransaction.show(fragment);
//                sb.append("cache , attach and show it");
//            } else {
//                fragmentTransaction.show(fragment);
//                // fg.onResume();
//                // fg.setMenuVisibility(true);
//                sb.append("cache, show it");
//            }
//        } else {
//            fragment = fragmentOption.applyFrag(fragmentTransaction, mAty, mContentId);
//            if (fragmentOption.isAddWay()) {
//                sb.append("new , add it");
//            } else {
//                sb.append("new , replace it");
//            }
//        }
////        Logger.d(TAG, sb.toString());
//        return fragment;
//    }
//
//    public Fragment switchTo(FragmentOption fragmentOption) {
//        Fragment fragment = null;
//        if (fragmentOption.hasFlag(FragmentOption.FRAG_UNIQUEUE_INSTANCE)) {
//            fragment = mFragmentManager.findFragmentByTag(fragmentOption.getTag());
//        }
//        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
//        fragmentOption.addTransAnim(fragmentTransaction);
//        fragment = switchTo(fragmentTransaction, fragment, fragmentOption);
//        fragmentOption.addBackStack(fragmentTransaction);
//        if (!fragmentOption.hasFlag(FragmentOption.FRAG_IGNORE_PREVISIBLE) && mCurrentFragment != null) {
//            if (mCurrentFragment != fragment && mCurrentFragment.getId() == fragment.getId()) {
//                fragmentTransaction.hide(mCurrentFragment);
//            }
//        }
//        if (fragmentOption.commit(fragmentTransaction)) {
//            mFragmentManager.executePendingTransactions();
//        }
//        return onCurrentFragmentChanged(0, fragment, mCurrentFragment);
//    }
//
//    @Override
//    public void onBackStackChanged() {
//        int n = mFragmentManager.getBackStackEntryCount();
//        boolean addBack = n > mBackStackCount;
//        mBackStackCount = n;
//        Fragment frag = null;
//        if (addBack) {// add back frag.
//            String fragTag = mFragmentManager.getBackStackEntryAt(n - 1).getName();
//            frag = mFragmentManager.findFragmentByTag(fragTag);
//            onCurrentFragmentChanged(1, frag, mCurrentFragment);
//        } else {// sub back frag.
//            frag = findTopVisibleFrag();
//            onCurrentFragmentChanged(2, frag, mCurrentFragment);
//        }
//    }
//
//    /**
//     * @param fromType 0 for switch to ,1 for back stack increased,2 for back stack decreased.
//     * @param current
//     * @param pre
//     * @return
//     */
//    private Fragment onCurrentFragmentChanged(int fromType, Fragment current, Fragment pre) {
//        if (pre != null && pre != current) {
//            if (mPreVisible != null) {
//                mPreVisible.clear();
//            }
//            mPreVisible = new WeakReference<Fragment>(pre);
//        }
//        return (mCurrentFragment = current == null ? pre : current);
//    }
//
//    public Fragment findTopVisibleFrag() {
//        Fragment mPre = mPreVisible == null ? null : mPreVisible.get();
//        if (mPre != null && mPre.isVisible() && mPre.getUserVisibleHint()) {
//            return mPre;
//        }
//        return findTopVisibleFrag(mFragmentManager);
//    }
//
//    /**
//     * find the current visible Fragment.
//     *
//     * @param fragmentManager find from which FragmentManager , null for default FragmentManager.
//     * @return
//     */
//    public static Fragment findTopVisibleFrag(FragmentManager fragmentManager) {
//        if (fragmentManager != null) {
//            return FragmentUtility.getTopFragment(fragmentManager);
//        }
//        return null;
//    }
//
//
//    @SuppressWarnings("unchecked")
//    public static List<Fragment> getAllFragments(FragmentManager fragmentManager, boolean allAdded) {
//        List<Fragment> result = null;
//        if (fragmentManager != null) {
//            if (allAdded) {
//                try {
//                    result = (List<Fragment>) invokeField(fragmentManager, "mAdded");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            if (result == null || result.size() == 0) {
//                result = fragmentManager.getFragments();
//            }
//        }
//        return result;
//    }
//
//    private StringBuilder dumpFragList(StringBuilder sb, boolean fragStack, boolean fragList) {
//        return dumpFragList(mFragmentManager, sb, fragStack, fragList);
//    }
//
//    public static StringBuilder dumpFragList(FragmentManager fragMgr, StringBuilder sb, boolean fragStack,
//                                             boolean fragList) {
//        if (fragMgr == null) {
//            return sb;
//        }
//        sb = sb == null ? new StringBuilder() : sb;
//        if (fragStack) {
//            int n = fragMgr.getBackStackEntryCount();
//            sb.append("\r\n-------back stack list<" + n + ">:\r\n");
//            for (int i = 0; i < n; i++) {
//                BackStackEntry be = fragMgr.getBackStackEntryAt(i);
//                sb.append("back_frag_").append(i).append(":")
//                        .append(be.getName()).append("\r\n");
//            }
//        }
//        if (fragList) {
//            List<Fragment> list = fragMgr.getFragments();
//            int n = list == null ? 0 : list.size();
//            sb.append("+++++++frag list <" + n + ">:\r\n");
//            for (int i = 0; i < n; i++) {
//                Fragment f = list.get(i);
//                if (f != null) {
//                    sb.append("list_frag_").append(i)
//                            .append(":" + f.getClass().getName())
//                            .append(" visible=" + f.isVisible()).append("\r\n");
//                } else {
//                    sb.append("list_frag_").append(i)
//                            .append(": frag is null\r\n");
//                }
//            }
//        }
//        return sb;
//    }
//
//    /**
//     * 获取反射的属性
//     */
//    public static Object invokeField(Object owner, String fieldName) throws Exception {
//        Field mField = owner.getClass().getDeclaredField(fieldName);
//        if (!mField.isAccessible()) {
//            mField.setAccessible(true);
//        }
//        Object property = mField.get(owner);
//        return property;
//    }
}
