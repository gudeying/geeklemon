package cn.geekelmon.data.support;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import cn.geekelmon.data.session.JdbcExecutor;
import cn.geekelmon.data.support.jdbc.JdbcProxy;
import io.netty.util.concurrent.FastThreadLocal;

public class DefaultJdbcExcutorFactory implements JdbcExecutorFactory {
	private FastThreadLocal<LJdbcExecutor> excutorLocal = new FastThreadLocal<>();
	private DataSource dataSource;

	public DefaultJdbcExcutorFactory(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public synchronized LJdbcExecutor getLJdbcExcutor(boolean resourceManaged) {
		LJdbcExecutor executor = excutorLocal.get();
		if (executor != null && executor.available()) {
			return executor;
		} else {
			try {
				Connection connection = dataSource.getConnection();
				if (resourceManaged) {
					connection.setAutoCommit(false);
				}
				Connection proxyConn = JdbcProxy.getConnection(connection);

				LJdbcExecutor newJdbcExcutor = new DefaultJdbcExcutor(this, proxyConn, resourceManaged);

				excutorLocal.set(newJdbcExcutor);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return excutorLocal.get();
	}

	@Override
	public void release(LJdbcExecutor executor) {
		excutorLocal.remove();
	}

	@Override
	public LJdbcExecutor getJdbcExcutor() {
		return getLJdbcExcutor(true);
	}

	@Override
	public LJdbcExecutor newJdbcExcutor() {
		Connection connection;
		try {
			connection = dataSource.getConnection();
			return new DefaultJdbcExcutor(null, connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
