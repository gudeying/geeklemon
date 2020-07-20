package cn.geekelmon.data.support.jdbc;

import java.lang.ref.SoftReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.util.ReflectUtil;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class ConnectionInterceptor implements MethodInterceptor {
	private List<SoftReference<Statement>> statementsReference = new ArrayList<SoftReference<Statement>>();
	protected Connection underlyingConnection;

	public ConnectionInterceptor(Connection connection) {
		this.underlyingConnection = connection;
	}

	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		if (method.getName().equals("close")) {
			closeStatement();
			// 关闭connection
			Object result = ReflectUtil.invoke(underlyingConnection, method, args);

			// this.underlyingConnection = null;
			return result;
		}
		Object result = null;

		try {
			result = ReflectUtil.invoke(underlyingConnection, method, args);
		} catch (Exception e) {
			if (e instanceof InvocationTargetException) {
				InvocationTargetException exception = (InvocationTargetException) e;
				throw exception.getTargetException();
			}
		}
		if (method.getName().equals("prepareCall")) {
			CallableStatement callableStatement = (CallableStatement) result;
			PreparedStatement proxyCall = JdbcProxy.getPreparedStatement(callableStatement, CallableStatement.class);
			statementsReference.add(new SoftReference<Statement>(proxyCall));

			return proxyCall;
		}
		if (method.getName().equals("prepareStatement")) {
			PreparedStatement preparedStatement = (PreparedStatement) result;
			PreparedStatement proxyPrepared = JdbcProxy.getPreparedStatement(preparedStatement,
					PreparedStatement.class);
			statementsReference.add(new SoftReference<Statement>(proxyPrepared));
			return proxyPrepared;
		}

		return result;
	}

	/**
	 * 保存的是持有真正 statement的代理类，调用其close会先关闭resultSet，在关闭真正的statement
	 */
	private void closeStatement() {
		for (SoftReference<Statement> softReference : statementsReference) {
			try {
				softReference.get().close();
			} catch (SQLException e) {

			}
		}
		statementsReference.clear();
	}
}
