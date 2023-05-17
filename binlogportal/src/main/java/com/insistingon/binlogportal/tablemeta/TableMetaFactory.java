package com.insistingon.binlogportal.tablemeta;

import com.insistingon.binlogportal.config.SyncConfig;
import com.insistingon.binlogportal.BinlogPortalException;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class TableMetaFactory {
    private final Map<Long, TableMetaEntity> tableMetaEntityIdMap = new HashMap<>();
    private final SyncConfig syncConfig;

    public TableMetaFactory(SyncConfig syncConfig) {
        this.syncConfig = syncConfig;
    }

    public TableMetaEntity getTableMetaEntity(Long tableId) {
        return tableMetaEntityIdMap.get(tableId);
    }

    public TableMetaEntity getTableMetaEntity(Long tableId, String dbName, String tableName) throws BinlogPortalException {
        if (tableMetaEntityIdMap.containsKey(tableId)) {
            return tableMetaEntityIdMap.get(tableId);
        }else {
            try {
                TableMetaEntity tableMetaEntity = getTableMetaEntityFromDb(dbName, tableName, tableId);
                tableMetaEntityIdMap.put(tableId, tableMetaEntity);
                return tableMetaEntity;
            } catch (SQLException e) {
                throw new BinlogPortalException("getTableMetaEntity error", e);
            }
        }
    }

    private TableMetaEntity getTableMetaEntityFromDb(String dbName, String tableName, Long tableId) throws SQLException {
        try (Connection connection = getConnection(dbName)) {
            DatabaseMetaData dbmd = connection.getMetaData();
            ResultSet rs = dbmd.getColumns(dbName, dbName, tableName, null);
            TableMetaEntity tableMetaEntity = new TableMetaEntity();
            tableMetaEntity.setTableId(tableId);
            tableMetaEntity.setDbName(dbName);
            tableMetaEntity.setTableName(tableName);
            while (rs.next()) {
                TableMetaEntity.ColumnMetaData columnMetaData = new TableMetaEntity.ColumnMetaData();
                columnMetaData.setName(rs.getString("COLUMN_NAME"));
                columnMetaData.setType(rs.getString("TYPE_NAME"));
                tableMetaEntity.getColumnMetaDataList().add(columnMetaData);
            }
            return tableMetaEntity;
        }
    }

    private Connection getConnection(String dbName) throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://" + syncConfig.getHost() + ":" + syncConfig.getPort() + "/" + dbName, syncConfig.getUserName(), syncConfig.getPassword());
    }
}
