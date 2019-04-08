package com.vonallin.lib.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.view.View;


public class ActivityUtil {
    /**
     * 如果是Activity，返回Activity
     * 否则返回Context
     */
    public static Context findActivityContext(Context context) {
        Activity aty = findActivityContext(context, 100);
        return aty == null ? context : aty;
    }

    public static Activity findActivityContext(Context context, int remainDeep) {
        if (context instanceof Activity) {
            return (Activity) context;
        }
        if (remainDeep > 0 && context instanceof ContextWrapper) {
            return findActivityContext(((ContextWrapper) context).getBaseContext(), remainDeep - 1);
        }
        return null;
    }

    public static Activity findActivity(View view) {
        if (view == null) {
            return null;
        }
        return findActivity(view.getContext());
    }

    public static Activity findActivity(View view, Activity defActivity) {
        Activity activity = findActivity(view);
        return activity == null ? defActivity : activity;
    }

    public static Activity findActivity(Context context) {
        while (true) {
            if (context instanceof Activity) {
                return (Activity) context;
            } else if (context instanceof ContextWrapper) {
                context = ((ContextWrapper) context).getBaseContext();
            } else {
                return null;
            }
        }
    }


    public static boolean isContextAlive(Context context) {
        Activity aty = findActivityContext(context, 100);
        if (aty instanceof FragmentActivity) {
            return !((FragmentActivity) aty).getSupportFragmentManager().isDestroyed();
        } else if (aty != null) {
            if (Build.VERSION.SDK_INT < 17) {
                return !aty.isFinishing();
            } else {
                return !aty.isDestroyed();
            }
        }
        return false;
    }

    //判断一个activity是否还存活
    public static boolean isActivityAlive(Context context) {
        if (context == null || !(context instanceof Activity)) {
            return false;
        }

        Activity activity = (Activity) context;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (activity.isDestroyed() || activity.isFinishing()) {
                return false;
            }
        } else {
            if (activity.isFinishing()) {
                return false;
            }
        }

        return true;
    }
}
