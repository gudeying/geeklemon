package cn.geekelmon.data.mapper;

import cn.geekelmon.data.sql.QueryType;
import cn.geekelmon.data.sql.TransactionType;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author : Kavin Gu
 * Project Name : redant
 * Description :
 * @version : ${VERSION} 2019/3/3 22:16
 * Modified by : kavingu
 */
public class MapperMethodInfo {
    /**
     * {参数名，方法传入的参数位置index}
     */
    private Map<String, Integer> paramHolder;
    /**
     * 返回类型
     * void、List、Map、int,bean
     */
    private ReturnType returnType;
    /**
     * 查询返回bean时才生效
     */
    private Class<?> beanCls;

    /**
     * 返回的GenericReturnTypeg
     * 暂时不用了
     */
    @Deprecated
    private Type genericReturnType;
    /**
     * 如果是List，这个代表泛型中的ActualTypeArgument
     */
    private Class<?> argumentEntity;
    /**
     * 注解中的sql，已经转换为？的preparedStatement
     */
    private String lsql;

    /**
     * 注解中的值
     */
    private String sql;

    /**
     * SELECT INSERT UPDATE DELETE DYNAMIC_UPDATE DYNAMIC_INSERT
     */
    private QueryType queryType;

    /**
     * 返回值是基础类型时才使用
     */
    private Type returnCls;

    private Class<?> sqlProviderClass;
    private String sqlProviderMethodName;

    public Class<?> getSqlProviderClass() {
        return sqlProviderClass;
    }

    public MapperMethodInfo setSqlProviderClass(Class<?> sqlProviderClass) {
        this.sqlProviderClass = sqlProviderClass;
        return this;
    }

    public String getSqlProviderMethodName() {
        return sqlProviderMethodName;
    }

    public MapperMethodInfo setSqlProviderMethodName(String sqlProviderMethodName) {
        this.sqlProviderMethodName = sqlProviderMethodName;
        return this;
    }

    /**
     * 是否使用事务，默认为没有事务
     */
    private TransactionType transactionType = TransactionType.NONE;

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public Type getReturnCls() {
        return returnCls;
    }

    public void setReturnCls(Type returnCls) {
        this.returnCls = returnCls;
    }

    public Map<String, Integer> getParamHolder() {
        return paramHolder;
    }

    public ReturnType getReturnType() {
        return returnType;
    }

    public void setReturnType(ReturnType returnType) {
        this.returnType = returnType;
    }

    public Type getGenericReturnType() {
        return genericReturnType;
    }

    public void setGenericReturnType(Type genericReturnType) {
        this.genericReturnType = genericReturnType;
    }

    public void addParamIndex(String name, Integer index) {
        paramHolder.put(name, index);
    }

    public void setParamHolder(Map<String, Integer> paramHolder) {
        this.paramHolder = paramHolder;
    }

    public Class<?> getArgumentEntity() {
        return argumentEntity;
    }

    public void setArgumentEntity(Class<?> argumentEntity) {
        this.argumentEntity = argumentEntity;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public QueryType getQueryType() {
        return queryType;
    }

    public void setQueryType(QueryType queryType) {
        this.queryType = queryType;
    }

    public String getLsql() {
        return lsql;
    }

    public void setLsql(String lsql) {
        this.lsql = lsql;
    }

    public Class<?> getBeanCls() {
        return beanCls;
    }

    public void setBeanCls(Class<?> beanCls) {
        this.beanCls = beanCls;
    }
}
