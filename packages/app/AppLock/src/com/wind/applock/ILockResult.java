package com.wind.applock;

public interface ILockResult {
    void lock();
    void unlock();
    boolean callKeycode(int keyCode);
}
