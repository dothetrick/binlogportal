package com.insistingon.binlogportal.config;

import com.insistingon.binlogportal.distributed.IDistributedHandler;
import com.insistingon.binlogportal.event.lifecycle.BaseLifeCycleListenerFactory;
import com.insistingon.binlogportal.event.lifecycle.ILifeCycleFactory;
import com.insistingon.binlogportal.position.IPositionHandler;

import java.util.ArrayList;
import java.util.List;

public class BinlogPortalConfig {
    //配置列表
    List<SyncConfig> syncConfigList = new ArrayList<>();

    //binlog位点处理器
    IPositionHandler positionHandler;

    //分布式处理器
    IDistributedHandler distributedHandler;

    //LifeCycleEvent监听器
    ILifeCycleFactory lifeCycleFactory = new BaseLifeCycleListenerFactory();

    //增加配置项
    public void addSyncConfig(SyncConfig syncConfig) {
        syncConfigList.add(syncConfig);
    }

    public List<SyncConfig> getSyncConfigList() {
        return syncConfigList;
    }

    public IPositionHandler getPositionHandler() {
        return positionHandler;
    }

    public void setPositionHandler(IPositionHandler positionHandler) {
        this.positionHandler = positionHandler;
    }

    public IDistributedHandler getDistributedHandler() {
        return distributedHandler;
    }

    public void setDistributedHandler(IDistributedHandler distributedHandler) {
        this.distributedHandler = distributedHandler;
    }

    public ILifeCycleFactory getLifeCycleFactory() {
        return lifeCycleFactory;
    }

    public void setLifeCycleFactory(ILifeCycleFactory lifeCycleFactory) {
        this.lifeCycleFactory = lifeCycleFactory;
    }
}
