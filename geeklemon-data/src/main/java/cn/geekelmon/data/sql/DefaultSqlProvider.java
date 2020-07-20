package cn.geekelmon.data.sql;

import cn.geekelmon.data.LSQLTool;
import cn.geekelmon.data.annotation.LTable;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 */
public class DefaultSqlProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSqlProvider.class);

    /**
     * 根据实体类的field情况拼接动态插入sql，null的field不会进入插入语句
     *
     * @param bean 实体类
     * @return sql
     */
    public String insert(Object bean) {
        try {
            if (BeanUtil.isBean(bean.getClass())) {
                Map<String, String> map = LSQLTool.getNotNullBeanColumnNameWithFieldName(bean);
                if (map.entrySet().size() < 1) {
                    LOGGER.error("{} nothing to be inserted,check field annotated with @LColumn and not null", bean);
                    return "";
                }
                LTable table = bean.getClass().getAnnotation(LTable.class);
                if (table == null) {
                    LOGGER.error("{} can not find table name,check object annotated with @LTable and its value not null", bean);
                    return "";
                }
                String tableName = LSQLTool.isBlank(table.value()) ? bean.getClass().getSimpleName() : table.value();
                StringBuilder sql = new StringBuilder("INSERT INTO ").append(tableName).append("(");
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    sql.append(entry.getKey()).append(",");
                }
                sql.deleteCharAt(sql.length() - 1).append(")").append(" ").append("VALUES (");
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    sql.append("${")
                            .append(entry.getValue()).append("}")
                            .append(",");
                }
                sql.deleteCharAt(sql.length() - 1).append(")");
                return sql.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOGGER.error("the object {} has no even one setter ", bean);
        return "";
    }

    /**
     * 根据实体类的field情况拼接动态更新sql，null的field不会更新
     *
     * @param bean 实体类
     * @return sql
     */
    public String update(Object bean) {
        try {
            if (BeanUtil.isBean(bean.getClass())) {
                Map<String, String> map = LSQLTool.getNotNullBeanColumnNameWithFieldName(bean);
                if (map.entrySet().size() < 1) {
                    LOGGER.error("{} nothing to be updated,check field annotated with @LColumn and not null", bean);
                    return "";
                }
                LTable table = bean.getClass().getAnnotation(LTable.class);
                if (table == null || StrUtil.isBlank(table.value())) {
                    LOGGER.error("{} can not find table name,check object annotated with @LTable and its value not null", bean);
                    return "";
                }
                Map<String, String> tableIdMap = LSQLTool.getLTableIdMap(bean);
                if (tableIdMap.entrySet().size() < 1) {
                    LOGGER.error("{} update should have ids,try annotate a field with @Lid", bean);
                    return "";
                }
                tableIdMap.forEach((key, val) -> {
                    /**
                     * id值不更新
                     */
                    map.remove(key);
                });
                if (map.entrySet().size() < 1) {
                    LOGGER.error("{} nothing to be updated,check field annotated with @LColumn and not null", bean);
                    return "";
                }
                String tableName = LSQLTool.isBlank(table.value()) ? bean.getClass().getSimpleName() : table.value();
                StringBuilder sql = new StringBuilder("UPDATE  ").append(tableName).append(" SET ");
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    sql.append(entry.getKey()).append(" = ").append("${").append(entry.getValue()).append("}").append(",");
                }
                sql.deleteCharAt(sql.length() - 1).append(" ").append("WHERE ");
                for (Map.Entry<String, String> entry : tableIdMap.entrySet()) {
                    sql.append(entry.getKey()).append(" = ").append("${").append(entry.getValue())
                            .append("}")
                            .append(" and ");
                }
                sql.delete(sql.length() - 4, sql.length() - 1);
                return sql.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    String value(Object... params) {
        return null;
    }
}
