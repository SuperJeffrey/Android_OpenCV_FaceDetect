package com.facedetect.permission;

public class IManager {
    /**
     * 权限申请
     */
    public interface IPermission {
        void accede();//同意

        void reject();//拒绝
    }
}