package cn.geekelmon.data.support;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.management.RuntimeErrorException;

import cn.geekelmon.data.LSQLTool;
import cn.geekelmon.data.session.CallType;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

public class DefaultJdbcExcutor implements LJdbcExecutor {
	private static final Log Logger = LogFactory.get();
	private boolean resourceManaged;
	private Connection connection;
	private JdbcExecutorFactory executorFactory;

	public DefaultJdbcExcutor(JdbcExecutorFactory executorFactory, Connection connection) {
		this.connection = connection;
		this.executorFactory = executorFactory;
	}

	public DefaultJdbcExcutor(JdbcExecutorFactory executorFactory, Connection connection, boolean resourceManaged) {
		this.connection = connection;
		this.resourceManaged = resourceManaged;
		this.executorFactory = executorFactory;
	}

	@Override
	public Connection getConnection() {
		return connection;
	}

	@Override
	public ResultSet query(String sql, Object[] params) throws SQLException {
		SQLException exception = null;
		try {
			// 构造PreparedStatement
			PreparedStatement preparedStatement = getPreparedStatement(connection, sql, params, false);
			// 执行查询
			ResultSet resultSet = preparedStatement.executeQuery();
			return resultSet;
		} catch (SQLException e) {
			closeConnection();
			Logger.error("Query execution SQL Error! \n SQL is : \n\t" + sql + ": \n\t ");
		} finally {
			if (exception != null) {
				throw exception;
			}
		}
		return null;

	}

	@Override
	public int update(String sql, Object[] params) throws SQLException {
		PreparedStatement preparedStatement = null;
		SQLException exception = null;
		try {
			preparedStatement = getPreparedStatement(connection, sql, params, false);
			return preparedStatement.executeUpdate();
		} catch (SQLException e) {
			Logger.error("Update execution SQL Error! \n SQL is :\n\t " + sql + "\nError is: \n\t", e);
		} finally {
			if (exception != null) {
				throw exception;
			}
		}

		return -1;
	}

	@Override
	public int insertReturnAutoKey(String sql, Object[] params) throws SQLException {
		return update(sql, params, true);
	}

	@Override
	public List<Object> call(String sql, CallType[] callTypes, Object[] params) throws SQLException {
		CallableStatement callableStatement = null;
		SQLException exception = null;
		try {
			callableStatement = getCallableStatement(connection, sql, params);
			callableStatement.executeUpdate();
			List<Object> objList = LSQLTool.getCallableStatementResult(callableStatement);
			return objList;
		} catch (SQLException e) {
			closeConnection();
			Logger.error("Query execution SQL Error! \n SQL is : \n\t" + sql + ": \n\t ", e);
		} finally {
			if (exception != null) {
				throw exception;
			}
		}
		return null;
	}

	private PreparedStatement getPreparedStatement(Connection connection, String sql, Object[] params,
			boolean autoKey) {
		PreparedStatement statement = null;
		try {
			if (autoKey) {
				statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			} else {
				statement = connection.prepareStatement(sql);
			}
			fillValue(statement, params);
		} catch (SQLException e) {
			Logger.error("Update execution SQL Error! \n SQL is :\n\t " + sql + "\nError is: \n\t", e);
		}
		return statement;
	}

	private void fillValue(PreparedStatement statement, Object[] params) throws SQLException {
		if (params != null) {
			for (int i = 0; i < params.length; i++) {
				statement.setObject(i + 1, params[i]);
			}
		}
	}

	private CallableStatement getCallableStatement(Connection conn, String preparedSql, Object[] params)
			throws SQLException {
		// 定义 jdbc statement 对象
		CallableStatement callableStatement = (CallableStatement) conn.prepareCall(preparedSql);
		fillValue(callableStatement, params);
		// 根据存储过程参数定义,注册 OUT 参数
		ParameterMetaData parameterMetaData = callableStatement.getParameterMetaData();
		for (int i = 0; i < parameterMetaData.getParameterCount(); i++) {
			int paramMode = parameterMetaData.getParameterMode(i + 1);
			if (paramMode == ParameterMetaData.parameterModeOut || paramMode == ParameterMetaData.parameterModeInOut) {
				callableStatement.registerOutParameter(i + 1, parameterMetaData.getParameterType(i + 1));
			}
		}
		return callableStatement;
	}

	private int update(String sql, Object[] params, boolean autoKey) throws SQLException {
		PreparedStatement preparedStatement = null;
		SQLException exception = null;
		try {
			preparedStatement = getPreparedStatement(connection, sql, params, autoKey);
			return preparedStatement.executeUpdate();
		} catch (Exception e) {
			Logger.error("Update execution SQL Error! \n SQL is :\n\t " + sql + "\nError is: \n\t", e);
		} finally {
			if (exception != null) {
				throw exception;
			}
		}

		return -1;
	}

	/**
	 * 如果上层管理关闭资源，则不需要关闭
	 * 
	 * @param connection
	 */
	private void closeConnection() {
		if (transactionManaged()) {
			return;
		}
		try {
			if (this.executorFactory != null) {
				this.executorFactory.release(this);
			}
			/**
			 * 由于传入的是代理的connection，直接关闭connection即可
			 */
			connection.close();
		} catch (SQLException e) {
		}
	}

	@Override
	public boolean transactionManaged() {
		return resourceManaged;
	}

	@Override
	public void beginTransaction() {
		try {
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void commit() {
		try {
			if (connection.getAutoCommit()) {
				return;
			}
			connection.commit();
		} catch (SQLException e) {
		}
	}

	@Override
	public void rollBack() {
		try {
			if (connection.getAutoCommit()) {
				return;
			}
			connection.rollback();
		} catch (SQLException e) {
		}
	}

	@Override
	public void close() {
		closeConnection();
	}

	@Override
	public void forceClose() {
		try {

			connection.close();
			if (this.executorFactory != null) {
				this.executorFactory.release(this);
			}
			this.executorFactory = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean available() {
		if (getConnection() == null) {
			return false;
		}
		try {
			if (connection.isClosed()) {
				return false;
			}
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

}
