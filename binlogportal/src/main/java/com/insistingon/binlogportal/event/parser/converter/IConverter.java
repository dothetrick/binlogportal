package com.insistingon.binlogportal.event.parser.converter;

/**
 * 类型转换器接口
 */
public interface IConverter<T> {
    T convert(Object from);

    T convert(Object from, String type);
}
