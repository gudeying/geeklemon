package cn.geekelmon.data.support;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import cn.geekelmon.data.session.CallType;

public interface LJdbcExecutor {
	/**
	 * 以此判断是否需要关闭连接，如果上层负责关闭，就不需要关闭
	 * 
	 * @return
	 */
	default boolean transactionManaged() {
		return false;
	}

	Connection getConnection();

	/**
	 * 注意处理 资源关闭
	 * 
	 * @throws SQLException
	 */
	ResultSet query(String sql, Object[] params) throws SQLException;

	int update(String sql, Object[] params) throws SQLException;

	int insertReturnAutoKey(String sql, Object[] params) throws SQLException;

	List<Object> call(String sql, CallType[] callTypes, Object[] params) throws SQLException;

	void beginTransaction() throws SQLException;

	void commit() throws SQLException;

	void rollBack() throws SQLException;

	/**
	 * 不一定关闭connection，如果上层负责管理，就不会关闭
	 */
	void close();

	/**
	 * 强制关闭connection
	 */
	void forceClose();

	/**
	 * 是否可以，connection不为空，未关闭
	 * 
	 * @return
	 */
	boolean available();
}
