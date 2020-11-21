package com.insistingon.binlogportal.event.parser.converter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class StringConverter implements IConverter<String> {

    @Override
    public String convert(Object from) {
        if (from == null) {
            return null;
        }
        if (from.getClass() == byte[].class) {
            return new String((byte[]) from);
        }
        return String.valueOf(from);
    }

    @Override
    public String convert(Object from, String type) {
        if (from == null) {
            return "";
        }
        List<String> timeToStringTypeList = Arrays.asList("DATETIME", "TIMESTAMP");
        if (timeToStringTypeList.contains(type)) {
            return LocalDateTime.ofEpochSecond((Long) from / 1000, 0, ZoneOffset.ofHours(8)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        if (Objects.equals(type, "DATE")) {
            return LocalDateTime.ofEpochSecond((Long) from / 1000, 0, ZoneOffset.ofHours(8)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        if (from.getClass() == byte[].class) {
            return new String((byte[]) from);
        }
        return String.valueOf(from);
    }
}
