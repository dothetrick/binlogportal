package com.insistingon.binlogportal.distributed;

import com.insistingon.binlogportal.config.BinlogPortalConfig;
import com.insistingon.binlogportal.BinlogPortalException;

public interface IDistributedHandler {
    void start(BinlogPortalConfig binlogPortalConfig) throws BinlogPortalException;
}
