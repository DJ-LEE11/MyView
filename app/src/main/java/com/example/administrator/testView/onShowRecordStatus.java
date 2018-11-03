package com.example.administrator.testView;

/**
 * @author 李栋杰
 * @time 2018/11/2  19:27
 * @desc ${TODD}
 */
public interface onShowRecordStatus {
    void showInitial();

    void showPre();

    void showRecording();

    void showResume();

    void showRecordComplete(boolean mIsMaxSec);

    void showRecordCancel();

    void showRecordCompleteToInitial();

    void showPlay();

    void showPlayResume();
}
