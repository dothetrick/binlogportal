package com.insistingon.binlogportal.event.parser;

import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.WriteRowsEventData;
import com.insistingon.binlogportal.event.EventEntity;
import com.insistingon.binlogportal.event.parser.converter.CommonConverterProcessor;
import com.insistingon.binlogportal.tablemeta.TableMetaEntity;
import com.insistingon.binlogportal.tablemeta.TableMetaFactory;
import com.insistingon.binlogportal.BinlogPortalException;
import com.insistingon.binlogportal.event.EventEntityType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InsertEventParser implements IEventParser {
    CommonConverterProcessor commonConverterProcessor = new CommonConverterProcessor();

    TableMetaFactory tableMetaFactory;

    public InsertEventParser(TableMetaFactory tableMetaFactory) {
        this.tableMetaFactory = tableMetaFactory;
    }

    @Override
    public List<EventEntity> parse(Event event) throws BinlogPortalException {
        WriteRowsEventData writeRowsEventData = event.getData();
        TableMetaEntity tableMetaEntity = tableMetaFactory.getTableMetaEntity(writeRowsEventData.getTableId());
        List<Serializable[]> rows = writeRowsEventData.getRows();
        List<EventEntity> eventEntityList = new ArrayList<>();
        rows.forEach(rowMap -> {
            List<TableMetaEntity.ColumnMetaData> columnMetaDataList = tableMetaEntity.getColumnMetaDataList();
            String[] after = commonConverterProcessor.convertToString(rowMap, columnMetaDataList);
            List<Object> changeAfter = new ArrayList<>();
            Collections.addAll(changeAfter, after);

            EventEntity eventEntity = new EventEntity();
            eventEntity.setEvent(event);
            eventEntity.setEventEntityType(EventEntityType.INSERT);
            eventEntity.setDatabaseName(tableMetaEntity.getDbName());
            eventEntity.setTableName(tableMetaEntity.getTableName());
            eventEntity.setColumns(columnMetaDataList);
            eventEntity.setChangeAfter(changeAfter);

            eventEntityList.add(eventEntity);
        });
        return eventEntityList;
    }
}
