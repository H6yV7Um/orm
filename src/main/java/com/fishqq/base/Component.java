package com.fishqq.base;

/**
 * @author 白路 bailu.zjj@alibaba-inc.com
 * @date 2018/6/28
 */
public interface Component<T> {
    void start(T config);
    void stop();
}
