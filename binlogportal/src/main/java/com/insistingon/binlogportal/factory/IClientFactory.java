package com.insistingon.binlogportal.factory;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.insistingon.binlogportal.BinlogPortalException;
import com.insistingon.binlogportal.config.SyncConfig;
import com.insistingon.binlogportal.event.lifecycle.ILifeCycleFactory;
import com.insistingon.binlogportal.position.IPositionHandler;

public interface IClientFactory {
    BinaryLogClient getClient(SyncConfig syncConfig) throws BinlogPortalException;

    BinaryLogClient getCachedClient(SyncConfig syncConfig);

    void setPositionHandler(IPositionHandler positionHandler);

    IPositionHandler getPositionHandler();

    void setLifeCycleFactory(ILifeCycleFactory lifeCycleFactory);

    ILifeCycleFactory getLifeCycleFactory();
}
