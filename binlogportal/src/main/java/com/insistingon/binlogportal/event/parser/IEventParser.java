package com.insistingon.binlogportal.event.parser;

import com.github.shyiko.mysql.binlog.event.Event;
import com.insistingon.binlogportal.BinlogPortalException;
import com.insistingon.binlogportal.event.EventEntity;

import java.util.List;

/**
 * 事件解析器接口
 */
public interface IEventParser {
    List<EventEntity> parse(Event event) throws BinlogPortalException;
}
