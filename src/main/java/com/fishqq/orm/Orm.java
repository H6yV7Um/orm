package com.fishqq.orm;

import com.fishqq.base.Component;
import com.fishqq.orm.Orm.OrmConfig;

/**
 * @author 白路 bailu.zjj@alibaba-inc.com
 * @date 2018/6/28
 */
public class Orm implements Component<OrmConfig> {
    public static OrmConfig global;

    @Override
    public void start(OrmConfig config) {
        global = config;
    }

    @Override
    public void stop() {

    }

    public class OrmConfig {

    }
}
