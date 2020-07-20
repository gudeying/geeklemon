package cn.geekelmon.data.sql;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.geekelmon.data.LSQLTool;
import cn.geekelmon.data.mapper.MapperMethodInfo;
import cn.geekelmon.data.mapper.MapperScaner;
import cn.geekelmon.data.mapper.ReturnType;
import cn.geekelmon.data.support.JdbcExecutorFactory;
import cn.geekelmon.data.support.LJdbcExecutor;
import cn.geekelmon.data.support.ResultSetHandler;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class MapperInterceptor implements MethodInterceptor {
	private static final Logger LOGGER = LoggerFactory.getLogger(SQLProxy.class);

	private JdbcExecutorFactory executorFactory;

	public MapperInterceptor(JdbcExecutorFactory factory) {
		this.executorFactory = factory;
	}

	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {

		MapperMethodInfo mapperMethodInfo = MapperScaner.getMapperMethodInfo().get(method);
		if (mapperMethodInfo == null) {
			// return proxy.invokeSuper(obj, args);
			return null;
		}
		LJdbcExecutor executor = executorFactory.getLJdbcExcutor(false);
		Pattern regex = Pattern.compile("\\$\\{([^}]*)\\}");
		// {参数名：位置}
		int i = 0;
		// 按照参数位置顺序放入数据，在preparedStatement中来填充参数
		String lsql = mapperMethodInfo.getLsql();// 原始sql，带有参数名称占位
		String sql = mapperMethodInfo.getSql();// 参数占位替换为?

		if (StrUtil.isBlank(lsql)) {
			Class<?> providerClass = mapperMethodInfo.getSqlProviderClass();
			String providerMethodName = mapperMethodInfo.getSqlProviderMethodName();
			Object newInstance = providerClass.newInstance();
			Object invoke = ReflectUtil.invoke(newInstance, providerMethodName, args);
			String convert = Convert.convert(String.class, invoke);
			if (StrUtil.isBlank(convert)) {
				LOGGER.error("{} sql text is blank ! ", method.toString());
				return null;
			}
			QueryType queryType = mapperMethodInfo.getQueryType();
			if (queryType == null || queryType == QueryType.AUTO) {
				mapperMethodInfo.setQueryType(LSQLTool.getSqlQueryType(convert));
			}
			lsql = convert;
			sql = LSQLTool.getPreparedStatement(lsql);
		}
		Matcher matcher = regex.matcher(lsql);
		int count = 0;// 获取sql中需要的参数个数
		while (matcher.find()) {
			count++;
		}
		matcher.reset();
		int paramCount = method.getParameterCount();
		Map<String, Integer> parMap = mapperMethodInfo.getParamHolder();
		Object[] paramHolder = new Object[count];

		if (paramCount == 1 && BeanUtil.isBean(args[0].getClass())) {
			while (matcher.find()) {
				String paramName = matcher.group(1); // 占位符的名称 例如userName
				// args[0]就是传入的bean
				Object realValue = LSQLTool.getFieldValue(args[0], paramName);
				if (realValue == null) {
					throw new RuntimeException(paramName + "参数无法获取");
				}
				paramHolder[i] = realValue;
				i++;
			}

		} else if (paramCount == 1 && args[0] instanceof Map) {
			Map paramMap = (Map) args[0];
			while (matcher.find()) {
				String paramName = matcher.group(1); // userName
				// 默认使用方法中的参数位置
				Object realValue = paramMap.get(paramName);
				paramHolder[i] = realValue;
				i++;
			}
		} else {
			while (matcher.find()) {
				String paramName = matcher.group(1); // userName
				String matchStr = matcher.group(0); // ${userName}
				// 默认使用方法中的参数位置
				Object realValue = args[i];
				Integer location = parMap.get(paramName);
				if (location != null) {
					// 使用注解中的参数位置
					realValue = args[location];
				}
				paramHolder[i] = realValue;
				i++;
			}
		}

		Object result = null;
		Exception exception = null;
		try {
			switch (mapperMethodInfo.getQueryType()) {
			case DELETE:
				int update = executor.update(sql, paramHolder);
				result = queryUpdateOrDelete(update, mapperMethodInfo);
				break;
			case INSERT:
				result = executor.update(sql, paramHolder);
				break;
			case INSERT_RETURN_GENERATED_KEYS:
				result = executor.insertReturnAutoKey(sql, paramHolder);
				break;
			case UPDATE:
				result = queryUpdateOrDelete(executor.update(sql, paramHolder), mapperMethodInfo);
				break;
			case SELECT:
				ResultSet resultSet = executor.query(sql, paramHolder);
				ResultSetHandler handler = new ResultSetHandler(resultSet, executor);
				result = handler.getResult(mapperMethodInfo);
				break;
			}
		} catch (SQLException e) {
			exception = e;
			if (!executor.transactionManaged()) {
				executor.rollBack();
			}
		} finally {
			if (!executor.transactionManaged()) {
				executor.commit();
			}
			executor.close();
			if (exception != null) {
				throw exception;
			}
		}

		return result;
	}

	/**
	 *
	 */
	/**
	 * update、delete、一般返回int或void
	 *
	 * @return
	 * @throws SQLException
	 */
	private Object queryUpdateOrDelete(int resultRow, MapperMethodInfo mapperMethodInfo) throws SQLException {
		ReturnType returnType = mapperMethodInfo.getReturnType();
		if (returnType == ReturnType.INT) {
			return resultRow;
		}
		return null;
	}
}
