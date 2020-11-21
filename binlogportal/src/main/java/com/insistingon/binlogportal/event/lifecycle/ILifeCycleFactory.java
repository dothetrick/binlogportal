package com.insistingon.binlogportal.event.lifecycle;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.insistingon.binlogportal.config.SyncConfig;

public interface ILifeCycleFactory {
    BinaryLogClient.LifecycleListener getLifeCycleListener(SyncConfig syncConfig);
}
