package cn.geekelmon.data.session;

import cn.geekelmon.data.LSQLTool;
import cn.geekelmon.data.sql.TransactionType;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSON;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * copy from Voovan Source
 * * Voovan Framework.
 * * WebSite: https://github.com/helyho/Voovan
 */
public class JdbcExecutor implements Closeable {
    private static final Log Logger = LogFactory.get();
    private static Map<Long, JdbcExecutor> JDBCOPERATE_THREAD_LIST = new ConcurrentHashMap<Long, JdbcExecutor>();

    private DataSource dataSource;
    private Connection connection;
    private TransactionType transactionType;
    private Savepoint savepoint = null;
    private Statement statement;
    private ResultSet resultSet;

    private List<JdbcExecutor> bindedJdbcOperate = new ArrayList<JdbcExecutor>();
    private boolean isTransactionFinished = false;

    /**
     * 构造函数
     *
     * @param dataSource      数据源
     * @param transcationType 是否启用事务支持, 设置事务模式
     */
    public JdbcExecutor(DataSource dataSource, TransactionType transcationType) {
        this.dataSource = dataSource;
        this.transactionType = transcationType;
    }

    public JdbcExecutor(DataSource dataSource) {
        this.dataSource = dataSource;
        this.transactionType = TransactionType.NONE;
    }

    /**
     * 获取连接
     *
     * @return 获取数据库连接
     * @throws SQLException SQL 异常
     */
    public synchronized Connection getConnection() throws SQLException {
        long threadId = Thread.currentThread().getId();
        //如果连接不存在,或者连接已关闭则重取一个连接
        if (connection == null || connection.isClosed()) {
            //事务嵌套模式
            if (transactionType == TransactionType.NEST) {
                //判断是否有上层事务
                if (JDBCOPERATE_THREAD_LIST.containsKey(threadId)) {
                    connection = JDBCOPERATE_THREAD_LIST.get(threadId).connection;
                    savepoint = connection.setSavepoint();
                } else {
                    connection = dataSource.getConnection();
                    connection.setAutoCommit(false);
                    JDBCOPERATE_THREAD_LIST.put(threadId, this);
                }
            }
            //孤立事务模式
            else if (transactionType == TransactionType.ALONE) {
                connection = dataSource.getConnection();
                connection.setAutoCommit(false);
            }
            //非事务模式
            else if (transactionType == TransactionType.NONE) {
                connection = dataSource.getConnection();
                connection.setAutoCommit(true);
            }
        }
        return connection;
    }

    /**
     * 提交事务
     *
     * @param isClose 是否关闭数据库连接
     * @throws SQLException SQL 异常
     */
    public synchronized void commit(boolean isClose) throws SQLException {
        if (connection == null) {
            return;
        }

        //关联事务提交
        for (JdbcExecutor bindJdbcOperate : bindedJdbcOperate) {
            if (this.equals(bindJdbcOperate)) {
                if (!bindJdbcOperate.isTransactionFinished) {
                    bindJdbcOperate.commit(isClose);
                }
            }
        }

        if (!connection.isClosed()) {
            connection.commit();

            if (isClose) {
                closeConnection(connection);
            }
        }
        isTransactionFinished = true;
    }

    /**
     * 回滚事务
     *
     * @param isClose 是否关闭数据库连接
     * @throws SQLException SQL 异常
     */
    public synchronized void rollback(boolean isClose) throws SQLException {
        if (connection == null) {
            return;
        }

        //有事务点则为: 子事务, 无事务点则为: 主事务
        //主事务回滚并可关闭连接
        //子事务回滚事务点, 并不可关闭连接
        if (savepoint != null) {
            connection.rollback(savepoint);
        } else {
            //关联事务回滚
            for (JdbcExecutor bindJdbcOperate : bindedJdbcOperate) {
                if (this.equals(bindJdbcOperate)) {
                    if (!bindJdbcOperate.isTransactionFinished) {
                        bindJdbcOperate.rollback(isClose);
                    }
                }
            }

            if (!connection.isClosed()) {
                connection.rollback();

                if (isClose) {
                    closeConnection(connection);
                }
            }
            isTransactionFinished = true;
        }
    }


    public ResultInfo query(String sql, Object[] params) throws SQLException {
        Connection conn = getConnection();
        SQLException exception = null;
        try {
            //构造PreparedStatement
            PreparedStatement preparedStatement = getPreparedStatement(conn, sql, params, false);
            //执行查询
            resultSet = preparedStatement.executeQuery();
            return new ResultInfo(resultSet, this);
        } catch (SQLException e) {
            closeConnection(conn);
            Logger.error("Query execution SQL Error! \n SQL is : \n\t" + sql + ": \n\t ", e);
            exception = e;
        }

        if (exception != null) {
            throw exception;
        }

        return null;
    }


    public int insertWithAutoKey(String sql, Object[] params) throws SQLException {
        return update(sql, params, true);
    }


    public int update(String sql, Object[] params) throws SQLException {
        return update(sql, params, false);
    }

    /**
     * 执行数据库更新
     *
     * @param sqlText sql字符串
     * @param params  参数
     * @return 更新记录数
     * @throws SQLException SQL 异常
     */
    private int update(String sqlText, Object[] params, boolean autoKey) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement preparedStatement = null;
        SQLException exception = null;
        try {
            preparedStatement = getPreparedStatement(conn, sqlText, params, autoKey);
            statement = preparedStatement;
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            Logger.error("Update execution SQL Error! \n SQL is :\n\t " + sqlText + "\nError is: \n\t", e);
            exception = e;
        } finally {
            // 非事务模式执行
            if (transactionType == TransactionType.NONE) {
                closeConnection(preparedStatement);
            } else {
                if (exception != null) {
                    rollback();
                }
                closeStatement(preparedStatement);
            }
        }

        if (exception != null) {
            throw exception;
        }

        return -1;
    }

    /**
     * 执行数据库批量更新
     *
     * @param sqlTexts sql字符串
     * @return 每条 SQL 更新记录数
     * @throws SQLException SQL 异常
     */
    public int[] baseBatch(String[] sqlTexts) throws SQLException {
        Connection conn = getConnection();
        Statement statement = null;
        SQLException exception = null;
        try {

            statement = (Statement) conn.createStatement();

            for (String sqlText : sqlTexts)
                statement.addBatch(sqlText);
            int[] result = statement.executeBatch();

            return result;
        } catch (SQLException e) {
            Logger.error("Batch execution SQL Error! \n SQL is : \n\t" + JSON.toJSON(sqlTexts) + ":\n\t", e);
            exception = e;
        } finally {
            // 非事务模式执行
            if (transactionType == TransactionType.NONE) {
                closeConnection(statement);
            } else {
                if (exception != null) {
                    rollback();
                }
                closeStatement(statement);
            }
        }

        if (exception != null) {
            throw exception;
        }

        return new int[0];
    }

    public List<Object> baseCall(String sqlText, CallType[] callTypes, Object[] params) throws SQLException {
        Connection conn = getConnection();
        CallableStatement callableStatement = null;
        SQLException exception = null;
        try {
            callableStatement = getCallableStatement(conn, sqlText, params);
            statement = callableStatement;
            callableStatement.executeUpdate();
            List<Object> objList = LSQLTool.getCallableStatementResult(callableStatement);
            return objList;
        } catch (SQLException e) {
            Logger.error("Query execution SQL Error! \n SQL is : \n\t" + sqlText + ": \n\t ", e);
            exception = e;
        } finally {
            // 非事务模式执行
            if (transactionType == TransactionType.NONE) {
                closeConnection(callableStatement);
            } else {
                if (exception != null) {
                    rollback();
                }
                closeStatement(callableStatement);
            }
        }

        if (exception != null) {
            throw exception;
        }

        return null;
    }

    private void fillValue(PreparedStatement statement, Object[] params) throws SQLException {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }
        }
    }

    private CallableStatement getCallableStatement(Connection conn, String preparedSql, Object[] params) throws SQLException {
        //定义 jdbc statement 对象
        CallableStatement callableStatement = (CallableStatement) conn.prepareCall(preparedSql);
        fillValue(callableStatement, params);
        //根据存储过程参数定义,注册 OUT 参数
        ParameterMetaData parameterMetaData = callableStatement.getParameterMetaData();
        for (int i = 0; i < parameterMetaData.getParameterCount(); i++) {
            int paramMode = parameterMetaData.getParameterMode(i + 1);
            if (paramMode == ParameterMetaData.parameterModeOut || paramMode == ParameterMetaData.parameterModeInOut) {
                callableStatement.registerOutParameter(i + 1, parameterMetaData.getParameterType(i + 1));
            }
        }
        return callableStatement;
    }

    private PreparedStatement getPreparedStatement(Connection connection, String sql, Object[] params, boolean autoKey) {
        PreparedStatement statement = null;
        try {
            if (autoKey) {
                statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            } else {
                statement = connection.prepareStatement(sql);
            }
            fillValue(statement, params);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return statement;
    }

    /**
     * 提交事务不关闭连接
     *
     * @throws SQLException SQL 异常
     */
    public void commit() throws SQLException {
        commit(true);
    }

    /**
     * 回滚事务不关闭连接
     *
     * @throws SQLException SQL 异常
     */
    public void rollback() throws SQLException {
        rollback(true);
    }


    /**
     * 关闭连接
     *
     * @param resultSet 结果集
     */
    protected static void closeConnection(ResultSet resultSet) {
        try {
            if (resultSet != null) {
                Statement statement = resultSet.getStatement();
                resultSet.close();
                closeConnection(statement);
            }
        } catch (SQLException e) {
            Logger.error(e);
        }

    }

    /**
     * 关闭连接
     *
     * @param statement Statement 对象
     */
    protected static void closeConnection(Statement statement) {
        try {
            if (statement != null) {
                Connection connection = statement.getConnection();
                statement.close();
                closeConnection(connection);
            }
        } catch (SQLException e) {
            Logger.error(e);
        }
    }

    /**
     * 关闭连接
     *
     * @param connection 连接对象
     */
    private static void closeConnection(Connection connection) {
        try {
            if (connection != null) {
                JDBCOPERATE_THREAD_LIST.remove(Thread.currentThread().getId());
                connection.close();
            }
        } catch (SQLException e) {
            Logger.error(e);
        }
    }

    /**
     * 关闭结果集
     *
     * @param resultSet 结果集
     */
    protected static void closeResult(ResultSet resultSet) {
        try {
            if (resultSet != null) {
                Statement statement = resultSet.getStatement();
                resultSet.close();
                closeStatement(statement);
            }
        } catch (SQLException e) {
            Logger.error(e);
        }
    }

    /**
     * 关闭 Statement
     *
     * @param statement Statement 对象
     */
    protected static void closeStatement(Statement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            Logger.error(e);
        }
    }


    @Override
    public void close() throws IOException {
        try {
            if (resultSet != null && !resultSet.isClosed()) {
                closeConnection(this.resultSet);
            } else if (statement != null && !statement.isClosed()) {
                closeConnection(statement);
            } else {
                closeConnection(connection);
            }
        } catch (Exception e) {
            throw new IOException(e);
        }

    }

    public TransactionType getTransactionType() {
        return transactionType;
    }
}
