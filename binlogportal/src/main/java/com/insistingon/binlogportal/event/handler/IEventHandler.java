package com.insistingon.binlogportal.event.handler;

import com.insistingon.binlogportal.event.EventEntity;
import com.insistingon.binlogportal.BinlogPortalException;

public interface IEventHandler {
    public void process(EventEntity eventEntity) throws BinlogPortalException;
}
