package com.example.floatwindow;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Toast;

import com.example.floatwindow.floatwindow.FloatActivity;
import com.example.floatwindow.floatwindow.FloatWindow;
import com.example.floatwindow.listener.PermissionListener;
import com.example.floatwindow.listener.ViewStateListener;
import com.example.floatwindow.type.MoveType;
import com.example.floatwindow.type.Screen;
import com.example.floatwindow.util.DisplayUtil;
import com.example.floatwindow.util.Miui;
import com.example.floatwindow.util.PermissionUtil;
import com.example.floatwindow.view.FloatWindowLayout;


/**
 * @author 李栋杰
 * @time 2019/3/13  19:47
 * @desc 悬浮弹窗管理
 */
public class FloatWindowManager {

    private Context mContext;
    private ViewStateListener mViewStateListener;
    private static volatile FloatWindowManager instance = null;

    private FloatWindowManager(Context context) {
        this.mContext = context;
        initListener();
    }

    public static FloatWindowManager getInstance(Context context) {
        if (instance == null) {
            synchronized (FloatWindowManager.class) {
                if (instance == null) {
                    instance = new FloatWindowManager(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    private void initListener() {
        mViewStateListener = new ViewStateListener() {
            @Override
            public void onPositionUpdate(int x, int y) {
            }

            @Override
            public void onShow() {
            }

            @Override
            public void onHide() {
            }

            @Override
            public void onDismiss() {
            }

            @Override
            public void onMoveAnimStart() {
            }

            @Override
            public void onMoveAnimEnd() {
            }

            @Override
            public void onBackToDesktop() {
            }
        };
    }

    public void open() {
        if (hasPermission()) {
            if (FloatWindow.get() == null) {
                FloatWindowLayout floatWindowLayout = new FloatWindowLayout(mContext);
                floatWindowLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(mContext, "onClick", Toast.LENGTH_SHORT).show();
                    }
                });

                FloatWindow
                        .with(mContext)
                        .setView(floatWindowLayout)
                        .setWidth(DisplayUtil.dp2px(mContext,60)) //设置悬浮控件宽高
                        .setHeight(DisplayUtil.dp2px(mContext,120))
                        .setX(Screen.width, 0.83f)
                        .setY(Screen.height, 0.45f)
                        .setMoveType(MoveType.slide, 0, 0)
                        .setMoveStyle(200, new AccelerateInterpolator())
                        .setDesktopShow(false)
                        .setViewStateListener(mViewStateListener)
                        .build();
            }
            FloatWindow.get().show();
        } else {
            requestPermission();
        }
    }

    public void hide() {
        FloatWindow.hide();
    }

    public void close() {
        FloatWindow.destroy();
    }


    private boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            return PermissionUtil.hasPermission(mContext);
        } else if (Miui.rom()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return PermissionUtil.hasPermission(mContext);
            } else {
                return PermissionUtil.hasPermission(mContext);
            }
        }
        return true;
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            req();
        } else if (Miui.rom()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                req();
            } else {
                Miui.req(mContext, new PermissionListener() {
                    @Override
                    public void onSuccess() {
                        open();
                    }

                    @Override
                    public void onFail() {
                        Toast.makeText(mContext, "获取权限失败", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    private void req() {
        FloatActivity.request(mContext, new PermissionListener() {
            @Override
            public void onSuccess() {
                open();

            }

            @Override
            public void onFail() {
                Toast.makeText(mContext, "获取权限失败", Toast.LENGTH_LONG).show();
            }
        });
    }

}
