package com.vonallin.lib.util;

import java.io.Serializable;

/**
 * add by zhaoshuchao
 */
public class Flag implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    public static final Flag FLAG = new Flag(0);
    private int mFlag = 0;

    public Flag(int flag) {
        mFlag = flag;
    }

    public final int getFlag() {
        return mFlag;
    }

    public final void setFlag(int flag) {
        mFlag = flag;
    }

    public static final boolean hasFlag(int value, int flag) {
        return flag == 0 ? false : flag == (value & flag);
    }

    public final boolean hasFlag(int flag) {
        return flag == 0 ? false : flag == (mFlag & flag);
    }

    public final void addFlag(int flag) {
        mFlag |= flag;
    }

    public static int addFlag(int value, int flag) {
        return value | flag;
    }

    public final void subFlag(int flag) {
        if (flag != 0) {
            mFlag &= ~flag;
        }
    }

    public static final int subFlag(int value, int flag) {
        if (flag != 0) {
            value &= ~flag;
        }
        return value;
    }

    public final void addOrSub(int flag) {
        if (flag > 0) {
            addFlag(flag);
        } else {
            subFlag(-flag);
        }
    }

    public final int addOrSub(int value, int flag) {
        if (flag > 0) {
            return value |= flag;
        } else {
            return subFlag(value, -flag);
        }
    }

    public final void reverseFlag(int flag) {
        mFlag ^= flag;
    }

    public static final int reverseFlag(int value, int flag) {
        return value ^= flag;
    }

    /**
     * return diff flag of flagSet
     *
     * @param flagSet a flag set include current flags.
     */
    public final int diffFlag(int flagSet) {
        return (mFlag & flagSet) ^ flagSet;
    }

    public static final int diffFlag(int value, int flagSet) {
        return (value & flagSet) ^ flagSet;
    }

    /**
     * return the intersection of both flags.
     *
     * @param flag
     * @return
     */
    public final int insetFlag(int flag) {
        return mFlag & flag;
    }

    public static final int insetFlag(int value, int flag) {
        return value & flag;
    }

    // 0 to 31 return 2^bits;
    public static final int flag(int bits) {
        if (bits >= 0 && bits < Integer.SIZE) {
            return 1 << bits;
        }
        return 0;
    }

    public static final int maxFlag(int flag) {
        if (flag > 0) {
            int n = Integer.SIZE - 2, temp = 1 << n;
            if ((temp & flag) != 0) {
                return temp;
            } else {
                for (int i = 0; i < n; i++) {
                    temp = temp >> 1;
                    if ((temp & flag) != 0) {
                        return temp;
                    }
                }
            }
        }
        return 0;
    }

    public static final int minFlag(int flag) {
        if (flag > 0) {
            int n = Integer.SIZE - 2, temp = 1;
            if ((temp & flag) != 0) {
                return temp;
            } else {
                for (int i = 0; i < n; i++) {
                    temp = temp << 1;
                    if ((temp & flag) != 0) {
                        return temp;
                    }
                }
            }
        }
        return 0;
    }

    /**
     * {@code Flag}只含有一个{@code int}型成员，构造函数就够用了
     *
     * @param flag
     * @return
     */
    @Deprecated
    public static final Flag create(int flag) {
        Flag f = (Flag) (FLAG == null ? null : FLAG.clone());
        if (f == null) {
            f = new Flag(flag);
        } else {
            f.setFlag(flag);
        }
        return f;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return Integer.toBinaryString(mFlag);
    }

    public static String toString(int value) {
        return Integer.toBinaryString(value);
    }

}

