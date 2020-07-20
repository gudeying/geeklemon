package cn.geeklemon.core.context.Processor;

import java.lang.reflect.Field;

public interface FieldPostProcessor {
    void process(Object bean, Field field);
}
