package com.wind.applock;

public class GlobalVars {
    private static GlobalVars instance;
    private static final byte[] lock = new byte[0];

    private GlobalVars() {

    }

    public static GlobalVars getInstance() {
        if (instance == null) {

            synchronized (lock) {
                instance = new GlobalVars();
            }
        }
        return instance;
    }

    /*
     * 设置与取得应用锁package名字
     * */
    private String LockAppName = "";

    public void updateLockAppName(String name) {
        LockAppName = name;
    }

    public String getLockAppName() {
        return LockAppName;
    }

}
