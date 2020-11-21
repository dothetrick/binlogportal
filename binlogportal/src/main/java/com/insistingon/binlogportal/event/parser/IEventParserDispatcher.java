package com.insistingon.binlogportal.event.parser;

import com.github.shyiko.mysql.binlog.event.Event;
import com.insistingon.binlogportal.BinlogPortalException;
import com.insistingon.binlogportal.event.EventEntity;

import java.util.List;

/**
 * 事件解析调度器接口
 */
public interface IEventParserDispatcher {
    public List<EventEntity> parse(Event event) throws BinlogPortalException;
}
