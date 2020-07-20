package cn.geekelmon.data.support.jdbc;

import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.util.ReflectUtil;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class PreparedStatementInterceptor implements MethodInterceptor {
	private PreparedStatement statement;
	private List<SoftReference<ResultSet>> openResultSets = new ArrayList<>();

	public PreparedStatementInterceptor(PreparedStatement statement) {
		this.statement = statement;
	}

	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		if (method.getName().equals("close")) {
			// 先关闭关联的resultSet
			closeResultSet();
		}
		// 关闭statement
		Object object = ReflectUtil.invoke(statement, method, args);

		if (ResultSet.class.isAssignableFrom(method.getReturnType())) {
			// 记录每次的resultSet
			openResultSets.add(new SoftReference<ResultSet>((ResultSet) object));
		}
		return object;
	}

	private void closeResultSet() {
		for (SoftReference<ResultSet> softReference : openResultSets) {
			try {
				softReference.get().close();
			} catch (SQLException e) {

			}
		}
		openResultSets.clear();
	}

}
