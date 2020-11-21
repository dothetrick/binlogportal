package com.insistingon.binlogportal.binlogportalspringbootstartertest.eventhandler;

import com.insistingon.binlogportal.BinlogPortalException;
import com.insistingon.binlogportal.event.EventEntity;
import com.insistingon.binlogportal.event.handler.IEventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LogEventHandler implements IEventHandler {
    public void process(EventEntity eventEntity) throws BinlogPortalException {
        log.info(eventEntity.getJsonFormatData());
    }
}
