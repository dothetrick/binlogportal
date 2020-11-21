package com.insistingon.binlogportal.position;

import com.insistingon.binlogportal.BinlogPortalException;
import com.insistingon.binlogportal.config.SyncConfig;

/**
 * 处理binlog位点信息接口，实现该接口创建自定义位点处理类
 */
public interface IPositionHandler {
    BinlogPositionEntity getPosition(SyncConfig syncConfig) throws BinlogPortalException;

    void savePosition(SyncConfig syncConfig, BinlogPositionEntity binlogPositionEntity) throws BinlogPortalException;
}
