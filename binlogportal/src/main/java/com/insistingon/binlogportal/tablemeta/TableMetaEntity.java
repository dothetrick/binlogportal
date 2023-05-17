package com.insistingon.binlogportal.tablemeta;

import java.util.ArrayList;
import java.util.List;

public class TableMetaEntity {
    private Long tableId;
    private String dbName;
    private String tableName;
    private List<ColumnMetaData> columnMetaDataList = new ArrayList<>();
    private String createSql;

    public static class ColumnMetaData {
        String name;
        String type;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<ColumnMetaData> getColumnMetaDataList() {
        return columnMetaDataList;
    }

    public void setColumnMetaDataList(List<ColumnMetaData> columnMetaDataList) {
        this.columnMetaDataList = columnMetaDataList;
    }

    public String getCreateSql() {
        return createSql;
    }

    public void setCreateSql(String createSql) {
        this.createSql = createSql;
    }
}
