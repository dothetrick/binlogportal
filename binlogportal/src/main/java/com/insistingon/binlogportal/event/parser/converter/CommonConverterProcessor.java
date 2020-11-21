package com.insistingon.binlogportal.event.parser.converter;

import com.insistingon.binlogportal.tablemeta.TableMetaEntity;

import java.io.Serializable;
import java.util.List;

public class CommonConverterProcessor {
    public String[] convertToString(Serializable[] serializables, List<TableMetaEntity.ColumnMetaData> columnMetaDataList) {
        String[] res = new String[serializables.length];
        StringConverter stringConverter = new StringConverter();
        for (int i = 0; i < serializables.length; i++) {
            res[i] = stringConverter.convert(serializables[i], columnMetaDataList.get(i).getType());
        }
        return res;
    }
}
