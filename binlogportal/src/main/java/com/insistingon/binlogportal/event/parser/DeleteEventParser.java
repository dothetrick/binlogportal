package com.insistingon.binlogportal.event.parser;

import com.github.shyiko.mysql.binlog.event.DeleteRowsEventData;
import com.github.shyiko.mysql.binlog.event.Event;
import com.insistingon.binlogportal.BinlogPortalException;
import com.insistingon.binlogportal.event.EventEntity;
import com.insistingon.binlogportal.event.EventEntityType;
import com.insistingon.binlogportal.event.parser.converter.CommonConverterProcessor;
import com.insistingon.binlogportal.tablemeta.TableMetaEntity;
import com.insistingon.binlogportal.tablemeta.TableMetaFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DeleteEventParser implements IEventParser {
    private CommonConverterProcessor commonConverterProcessor = new CommonConverterProcessor();

    private TableMetaFactory tableMetaFactory;

    public DeleteEventParser(TableMetaFactory tableMetaFactory) {
        this.tableMetaFactory = tableMetaFactory;
    }

    @Override
    public List<EventEntity> parse(Event event) throws BinlogPortalException {
        List<EventEntity> eventEntityList = new ArrayList<>();
        DeleteRowsEventData deleteRowsEventData = event.getData();
        TableMetaEntity tableMetaEntity = tableMetaFactory.getTableMetaEntity(deleteRowsEventData.getTableId());
        List<Serializable[]> rows = deleteRowsEventData.getRows();
        rows.forEach(rowMap -> {
            List<TableMetaEntity.ColumnMetaData> columnMetaDataList = tableMetaEntity.getColumnMetaDataList();
            String[] after = commonConverterProcessor.convertToString(rowMap, columnMetaDataList);
            List<String> columns = new ArrayList<>();
            List<Object> changeAfter = new ArrayList<>();
            for (int i = 0; i < after.length; i++) {
                columns.add(columnMetaDataList.get(i).getName());
                changeAfter.add(after[i]);
            }

            EventEntity eventEntity = new EventEntity();
            eventEntity.setEvent(event);
            eventEntity.setEventEntityType(EventEntityType.DELETE);
            eventEntity.setDatabaseName(tableMetaEntity.getDbName());
            eventEntity.setTableName(tableMetaEntity.getTableName());
            eventEntity.setColumns(columnMetaDataList);
            eventEntity.setChangeAfter(changeAfter);

            eventEntityList.add(eventEntity);
        });
        return eventEntityList;
    }
}
