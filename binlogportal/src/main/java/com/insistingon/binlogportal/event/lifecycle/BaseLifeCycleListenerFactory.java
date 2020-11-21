package com.insistingon.binlogportal.event.lifecycle;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.insistingon.binlogportal.config.SyncConfig;

public class BaseLifeCycleListenerFactory implements ILifeCycleFactory {
    @Override
    public BinaryLogClient.LifecycleListener getLifeCycleListener(SyncConfig syncConfig) {
        return new BaseLifeCycleEventListener(syncConfig);
    }
}
