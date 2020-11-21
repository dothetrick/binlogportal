package com.insistingon.binlogportal.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.shyiko.mysql.binlog.event.Event;
import com.insistingon.binlogportal.tablemeta.TableMetaEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 事件实体，简化binlog事件，方便处理
 */
public class EventEntity {
    //保存原始binlog事件信息
    Event event;

    EventEntityType eventEntityType;

    String databaseName;

    String tableName;

    List<TableMetaEntity.ColumnMetaData> columns;

    List<Object> changeBefore;

    List<Object> changeAfter;

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public EventEntityType getEventEntityType() {
        return eventEntityType;
    }

    public void setEventEntityType(EventEntityType eventEntityType) {
        this.eventEntityType = eventEntityType;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<TableMetaEntity.ColumnMetaData> getColumns() {
        return columns;
    }

    public void setColumns(List<TableMetaEntity.ColumnMetaData> columns) {
        this.columns = columns;
    }

    public List<Object> getChangeBefore() {
        return changeBefore;
    }

    public void setChangeBefore(List<Object> changeBefore) {
        this.changeBefore = changeBefore;
    }

    public List<Object> getChangeAfter() {
        return changeAfter;
    }

    public void setChangeAfter(List<Object> changeAfter) {
        this.changeAfter = changeAfter;
    }

    @Override
    public String toString() {
        return "EventEntity{" +
                "event=" + event +
                ", eventEntityType=" + eventEntityType +
                ", databaseName='" + databaseName + '\'' +
                ", tableName='" + tableName + '\'' +
                ", columns=" + columns +
                ", changeBefore=" + changeBefore +
                ", changeAfter=" + changeAfter +
                '}';
    }

    public String getJsonFormatData() {
        Map<String, Object> params = new HashMap<>();
        params.put("change_type", this.getEventEntityType().getDesc());
        Map<String, String[]> data = new HashMap<>();
        for (int i = 0; i < this.getColumns().size(); i++) {
            String before = "";
            if (this.getChangeBefore() != null) {
                before = this.getChangeBefore().get(i) != null ? this.getChangeBefore().get(i).toString() : "";
            }
            String after = "";
            if (this.getChangeAfter() != null) {
                after = this.getChangeAfter().get(i) != null ? this.getChangeAfter().get(i).toString() : "";
            }
            String[] subData = new String[]{
                    before,
                    after,
                    Objects.equals(before, after) ? "0" : "1"
            };
            data.put(this.getColumns().get(i).getName(), subData);
        }
        params.put("change_data", data);
        params.put("table_name", this.getTableName());
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(params);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
