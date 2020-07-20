package cn.geekelmon.data.support.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;

import cn.hutool.core.exceptions.ValidateException;
import net.sf.cglib.proxy.Enhancer;

public class JdbcProxy {
	private JdbcProxy() {
		throw new ValidateException("");
	}

	/**
	 * 代理 Connection使得代理关闭connection自动关闭prepareCall，preparedStatement
	 * 
	 * @param connection
	 * @return
	 */
	public static Connection getConnection(Connection connection) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(Connection.class);
		enhancer.setCallback(new ConnectionInterceptor(connection));

		return (Connection) enhancer.create();
	}

	/**
	 * 
	 * 代理使得关闭statement时关闭resultSet；
	 * 
	 * @param preparedStatement
	 * @return
	 */
	public static PreparedStatement getPreparedStatement(PreparedStatement preparedStatement,
			Class<? extends PreparedStatement> preparedStatementClass) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(preparedStatementClass);
		enhancer.setCallback(new PreparedStatementInterceptor(preparedStatement));

		return (PreparedStatement) enhancer.create();

	}

}