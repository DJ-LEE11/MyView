package com.example.administrator.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author 李栋杰
 * @time 2017/8/1  23:54
 * @desc 跳转Activity工具
 */
public class IntentUtils {
    Intent mIntent;
    boolean isFinish;
    int mEnterAnimation;
    int mExitAnimation;
    Activity mContext;
    int requestCode = Integer.MAX_VALUE;

    IntentUtils(Intent intent) {
        this.mIntent = intent;
    }

    public void start() {
        if (mIntent == null) {
            return;
        }
        if (requestCode == Integer.MAX_VALUE) {
            mContext.startActivity(mIntent);
        } else {
            ((Activity) mContext).startActivityForResult(mIntent, requestCode);
        }
        if (isFinish) {
            ((Activity) mContext).finish();
        }
        if (mEnterAnimation != 0 && mExitAnimation != 0) {
            ((Activity) mContext).overridePendingTransition(mEnterAnimation, mExitAnimation);
        }
    }

    public static class Builder {
        private Activity mContext;
        private Bundle mExtras;
        Intent mIntent;
        private boolean isFinish;
        private int mEnterAnimation;
        private int mExitAnimation;
        private int mFlags;

        public Builder(Activity context) {
            mContext = context;
        }


        public Builder to(Intent o) {
            mIntent = new Intent(o);
            return this;
        }

        public Builder to(String action) {
            mIntent = new Intent(action);
            return this;
        }

        public Builder to(String action, Uri uri) {
            mIntent = new Intent(action, uri);
            return this;
        }

        public Builder to(Class<?> cls) {
            mIntent = new Intent(mContext, cls);
            return this;
        }

        public Builder to(String action, Uri uri, Class<?> cls) {
            mIntent = new Intent(action, uri, mContext, cls);
            return this;
        }

        public Builder putExtra(String name, boolean value) {
            if (mExtras == null) {
                mExtras = new Bundle();
            }
            mExtras.putBoolean(name, value);
            return this;
        }


        public Builder putExtra(String name, byte value) {
            if (mExtras == null) {
                mExtras = new Bundle();
            }
            mExtras.putByte(name, value);
            return this;
        }


        public Builder putExtra(String name, char value) {
            if (mExtras == null) {
                mExtras = new Bundle();
            }
            mExtras.putChar(name, value);
            return this;
        }


        public Builder putExtra(String name, short value) {
            if (mExtras == null) {
                mExtras = new Bundle();
            }
            mExtras.putShort(name, value);
            return this;
        }


        public Builder putExtra(String name, int value) {
            if (mExtras == null) {
                mExtras = new Bundle();
            }
            mExtras.putInt(name, value);
            return this;
        }


        public Builder putExtra(String name, long value) {
            if (mExtras == null) {
                mExtras = new Bundle();
            }
            mExtras.putLong(name, value);
            return this;
        }


        public Builder putExtra(String name, float value) {
            if (mExtras == null) {
                mExtras = new Bundle();
            }
            mExtras.putFloat(name, value);
            return this;
        }

        public Builder putExtra(String name, double value) {
            if (mExtras == null) {
                mExtras = new Bundle();
            }
            mExtras.putDouble(name, value);
            return this;
        }


        public Builder putExtra(String name, String value) {
            if (mExtras == null) {
                mExtras = new Bundle();
            }
            mExtras.putString(name, value);
            return this;
        }


        public Builder putExtra(String name, CharSequence value) {
            if (mExtras == null) {
                mExtras = new Bundle();
            }
            mExtras.putCharSequence(name, value);
            return this;
        }


        public Builder putExtra(String name, Parcelable value) {
            if (mExtras == null) {
                mExtras = new Bundle();
            }
            mExtras.putParcelable(name, value);
            return this;
        }


        public Builder putExtra(String name, Parcelable[] value) {
            if (mExtras == null) {
                mExtras = new Bundle();
            }
            mExtras.putParcelableArray(name, value);
            return this;
        }


        public Builder putParcelableArrayListExtra(String name, ArrayList<? extends Parcelable> value) {
            if (mExtras == null) {
                mExtras = new Bundle();
            }
            mExtras.putParcelableArrayList(name, value);
            return this;
        }


        public Builder putIntegerArrayListExtra(String name, ArrayList<Integer> value) {
            if (mExtras == null) {
                mExtras = new Bundle();
            }
            mExtras.putIntegerArrayList(name, value);
            return this;
        }


        public Builder putStringArrayListExtra(String name, ArrayList<String> value) {
            if (mExtras == null) {
                mExtras = new Bundle();
            }
            mExtras.putStringArrayList(name, value);
            return this;
        }


        public Builder putCharSequenceArrayListExtra(String name, ArrayList<CharSequence> value) {
            if (mExtras == null) {
                mExtras = new Bundle();
            }
            mExtras.putCharSequenceArrayList(name, value);
            return this;
        }


        public Builder putExtra(String name, Serializable value) {
            if (mExtras == null) {
                mExtras = new Bundle();
            }
            mExtras.putSerializable(name, value);
            return this;
        }


        public Builder putExtra(String name, boolean[] value) {
            if (mExtras == null) {
                mExtras = new Bundle();
            }
            mExtras.putBooleanArray(name, value);
            return this;
        }


        public Builder putExtra(String name, byte[] value) {
            if (mExtras == null) {
                mExtras = new Bundle();
            }
            mExtras.putByteArray(name, value);
            return this;
        }


        public Builder putExtra(String name, short[] value) {
            if (mExtras == null) {
                mExtras = new Bundle();
            }
            mExtras.putShortArray(name, value);
            return this;
        }


        public Builder putExtra(String name, char[] value) {
            if (mExtras == null) {
                mExtras = new Bundle();
            }
            mExtras.putCharArray(name, value);
            return this;
        }

        public Builder putExtra(String name, int[] value) {
            if (mExtras == null) {
                mExtras = new Bundle();
            }
            mExtras.putIntArray(name, value);
            return this;
        }


        public Builder putExtra(String name, long[] value) {
            if (mExtras == null) {
                mExtras = new Bundle();
            }
            mExtras.putLongArray(name, value);
            return this;
        }


        public Builder putExtra(String name, float[] value) {
            if (mExtras == null) {
                mExtras = new Bundle();
            }
            mExtras.putFloatArray(name, value);
            return this;
        }


        public Builder putExtra(String name, double[] value) {
            if (mExtras == null) {
                mExtras = new Bundle();
            }
            mExtras.putDoubleArray(name, value);
            return this;
        }


        public Builder putExtra(String name, String[] value) {
            if (mExtras == null) {
                mExtras = new Bundle();
            }
            mExtras.putStringArray(name, value);
            return this;
        }


        public Builder putExtra(String name, CharSequence[] value) {
            if (mExtras == null) {
                mExtras = new Bundle();
            }
            mExtras.putCharSequenceArray(name, value);
            return this;
        }


        public Builder putExtra(String name, Bundle value) {
            if (mExtras == null) {
                mExtras = new Bundle();
            }
            mExtras.putBundle(name, value);
            return this;
        }

        public Builder putExtras(Intent src) {
            if (src.getExtras() != null) {
                if (mExtras == null) {
                    mExtras = new Bundle(src.getExtras());
                } else {
                    mExtras.putAll(src.getExtras());
                }
            }
            return this;
        }


        public Builder putExtras(Bundle extras) {
            if (mExtras == null) {
                mExtras = new Bundle();
            }
            mExtras.putAll(extras);
            return this;
        }

        public Builder finish(boolean needFinish) {
            isFinish = needFinish;
            return this;
        }

        public Builder addFlags(int flags) {
            mFlags |= flags;
            return this;
        }

        public IntentUtils build() {
            if (mIntent == null) {
                return null;
            }
            if (mExtras != null) {
                mIntent.putExtras(mExtras);
            }
            //            mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (mFlags != 0) {
                mIntent.addFlags(mFlags);
            }
            IntentUtils intentUtils = new IntentUtils(mIntent);
            intentUtils.mContext = mContext;
            intentUtils.isFinish = isFinish;
            intentUtils.mEnterAnimation = mEnterAnimation;
            intentUtils.mExitAnimation = mExitAnimation;
            return intentUtils;
        }

        public IntentUtils build(int requestCode) {
            IntentUtils intentUtils = build();
            intentUtils.requestCode = requestCode;
            return intentUtils;
        }

        public Builder anim(int enter, int exit) {
            mEnterAnimation = enter;
            mExitAnimation = exit;
            return this;
        }
    }
}
