package com.insistingon.binlogportal.event.lifecycle;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.insistingon.binlogportal.config.SyncConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseLifeCycleEventListener implements BinaryLogClient.LifecycleListener {

    private final Logger log = LoggerFactory.getLogger(BaseLifeCycleEventListener.class);

    SyncConfig syncConfig;

    public BaseLifeCycleEventListener(SyncConfig syncConfig) {
        this.syncConfig = syncConfig;
    }

    @Override
    public void onConnect(BinaryLogClient client) {

    }

    @Override
    public void onCommunicationFailure(BinaryLogClient client, Exception ex) {
        log.error(syncConfig.getHost() + ":" + syncConfig.getPort() + "," + ex.getMessage() + "." + client.getBinlogFilename() + "/" + client.getBinlogPosition(), ex);
    }

    @Override
    public void onEventDeserializationFailure(BinaryLogClient client, Exception ex) {
        log.error(syncConfig.getHost() + ":" + syncConfig.getPort() + "," + ex.getMessage(), ex);
    }

    @Override
    public void onDisconnect(BinaryLogClient client) {

    }
}
