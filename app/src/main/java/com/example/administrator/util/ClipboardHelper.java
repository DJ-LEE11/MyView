package com.example.administrator.util;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;

/**
 * @author 李栋杰
 * @time 2019/8/28  17:42
 * @desc 剪贴板帮助类
 */
public class ClipboardHelper {
    public static final String TAG = ClipboardHelper.class.getSimpleName();

    private volatile static ClipboardHelper mInstance;
    private ClipboardManager mClipboardManager;

    private ClipboardHelper(Context context) {
        mClipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    /**
     * 获取ClipboardUtil实例，记得初始化
     *
     * @return 单例
     */
    public static ClipboardHelper getInstance(Context context) {
        if (mInstance == null) {
            synchronized (ClipboardHelper.class) {
                if (mInstance == null) {
                    mInstance = new ClipboardHelper(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 判断剪贴板内是否有数据
     *
     * @return
     */
    public boolean hasPrimaryClip() {
        return mClipboardManager.hasPrimaryClip();
    }

    /**
     * 获取剪贴板中第一条String
     *
     * @return
     */
    public String getClipText() {
        if (!hasPrimaryClip()) {
            return null;
        }
        ClipData data = mClipboardManager.getPrimaryClip();
        if (data != null
                && mClipboardManager.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
            ClipData.Item firstItem = data.getItemAt(0);
            if (null != firstItem) {
                CharSequence text = firstItem.getText();
                if (null != text) {
                    return text.toString();
                }
            }
        }
        return null;
    }

    /**
     * 获取标志
     */
    public String getLable(){
        if (!hasPrimaryClip()) {
            return null;
        }
        ClipData data = mClipboardManager.getPrimaryClip();
        if (data!=null && data.getDescription()!=null && data.getDescription().getLabel()!=null){
            return data.getDescription().getLabel().toString();
        }
        return null;
    }

    /**
     * 将文本拷贝至剪贴板(自定义标志)
     *
     * @param text
     */
    public void copyText(String label, String text) {
        ClipData clip = ClipData.newPlainText(label, text);
        mClipboardManager.setPrimaryClip(clip);
    }

    /**
     * 将文本拷贝至剪贴板
     */
    public void copyText(String text) {
        copyText("HuanLiao",text);
    }

    /**
     * 清空剪贴板
     */
    public void clearClip() {
        mClipboardManager.setPrimaryClip(ClipData.newPlainText(null, ""));
    }
}
