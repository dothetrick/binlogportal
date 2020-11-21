package com.insistingon.binlogportal.event.parser;

import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventType;
import com.github.shyiko.mysql.binlog.event.TableMapEventData;
import com.insistingon.binlogportal.event.EventEntity;
import com.insistingon.binlogportal.tablemeta.TableMetaFactory;
import com.insistingon.binlogportal.BinlogPortalException;

import java.util.List;

/**
 * 通用事件解析调度器
 * 根据不同事件类型调用事件解析器
 */
public class CommonEventParserDispatcher implements IEventParserDispatcher {

    //数据表元数据工厂
    TableMetaFactory tableMetaFactory;

    public TableMetaFactory getTableMetaFactory() {
        return tableMetaFactory;
    }

    public void setTableMetaFactory(TableMetaFactory tableMetaFactory) {
        this.tableMetaFactory = tableMetaFactory;
    }

    public CommonEventParserDispatcher(TableMetaFactory tableMetaFactory) {
        this.tableMetaFactory = tableMetaFactory;
        this.updateEventParser = new UpdateEventParser(tableMetaFactory);
        this.insertEventParser = new InsertEventParser(tableMetaFactory);
        this.deleteEventParser = new DeleteEventParser(tableMetaFactory);
    }

    //更新事件解析器
    IEventParser updateEventParser;

    //插入事件解析器
    IEventParser insertEventParser;

    //删除事件解析器
    IEventParser deleteEventParser;

    @Override
    public List<EventEntity> parse(Event event) throws BinlogPortalException {

        /*
         * table_id不固定对应一个表，它是表载入table cache时临时分配的，一个不断增长的变量
         * 连续往同一个table中进行多次DML操作，table_id不变。 一般来说，出现DDL操作时，table_id才会变化
         * 所有更新和插入操作，都会产生一个TABLE_MAP事件
         * 通过该事件缓存table_id对应的表信息，然后再处理对应的事件
         */
        if (EventType.TABLE_MAP.equals(event.getHeader().getEventType())) {
            TableMapEventData tableMapEventData = event.getData();
            //table_map事件，要更新下tableMetaFactory中的tableId对应的信息缓存
            tableMetaFactory.getTableMetaEntity(
                    tableMapEventData.getTableId(),
                    tableMapEventData.getDatabase(),
                    tableMapEventData.getTable()
            );
        }

        //处理更新事件
        if (EventType.isUpdate(event.getHeader().getEventType())) {
            return updateEventParser.parse(event);
        }

        //处理插入事件
        if (EventType.isWrite(event.getHeader().getEventType())) {
            return insertEventParser.parse(event);
        }

        //删除事件处理
        if (EventType.isDelete(event.getHeader().getEventType())) {
            return deleteEventParser.parse(event);
        }

        return null;
    }
}
