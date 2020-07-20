package cn.geekelmon.data.session;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.geekelmon.data.LSQLTool;
import cn.geekelmon.data.mapper.MapperMethodInfo;
import cn.geekelmon.data.mapper.ReturnType;
import cn.geekelmon.data.sql.TransactionType;
import cn.geekelmon.data.tool.ReflectTool;
import cn.geeklemon.core.util.PrimitiveTypeUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ClassUtil;

/**
 * @author : Kavin Gu Project Name : geeklemon Description :
 * @version : ${VERSION} 2020/1/13 12:57 Modified by : kavingu
 */
public class ResultInfo {
	private static final Logger LOGGER = LoggerFactory.getLogger(ResultInfo.class);
	private final ResultSet resultSet;
	private final JdbcExecutor jdbcExecutor;

	public ResultInfo(ResultSet resultSet, JdbcExecutor jdbcExecutor) {

		this.resultSet = resultSet;
		this.jdbcExecutor = jdbcExecutor;
	}

	public ResultSet getResultSet() {
		return resultSet;
	}

	public Object getResult(MapperMethodInfo mapperMethodInfo) {
		try {
			ReturnType returnType = mapperMethodInfo.getReturnType();
			if (returnType == ReturnType.BEAN) {
				Class<?> bean = mapperMethodInfo.getBeanCls();
				try {
					Object o = LSQLTool.getOneBeanFromResultSet(bean, resultSet);
					if (o != null) {
						return bean.cast(o);
					}
					return null;
				} catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException
						| InstantiationException | SQLException e) {
					LOGGER.error(e.getMessage());
				}

			}
			if (returnType == ReturnType.MAP) {

				try {
					return LSQLTool.resultSetToMap(resultSet);
				} catch (SQLException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
					LOGGER.error(e.getMessage());
				}
			}
			if (returnType == ReturnType.LIST) {
				Class<?> bean = mapperMethodInfo.getArgumentEntity();
				try {
					if (Map.class.isAssignableFrom(bean)) {
						return LSQLTool.getListMapByResultSet(resultSet);
					}
					return LSQLTool.getBeanListByResultSet(bean, resultSet);
				} catch (SQLException | InstantiationException | InvocationTargetException | NoSuchMethodException
						| IllegalAccessException e) {
					LOGGER.error(e.getMessage());
				}
			}
			if (returnType == ReturnType.PRIM) {
				Type returnCls = mapperMethodInfo.getReturnCls();
				try {
					/*
					 * 有的dataSource不能使用getObject，会直接报错，比如 Druid;
					 */
					int databaseType = resultSet.getMetaData().getColumnType(1);
					String name = resultSet.getMetaData().getColumnName(1);

					boolean next = resultSet.next();
					String methodName = LSQLTool.getDataMethod(databaseType);

					Object object = ReflectTool.invokeMethod(resultSet, methodName, name);

					if (object != null) {
						return Convert.convert(returnCls, object);
					} else if (ClassUtil.isPrimitiveWrapper((Class<?>) returnCls)) {
						return object;
					}
					if (null ==object) {
						return PrimitiveTypeUtil.getPriDefaultValue((Class<?>) returnCls);
					}
				} catch (SQLException e) {
					LOGGER.error(e.getMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 非事务模式执行
			if (jdbcExecutor.getTransactionType() == TransactionType.NONE) {
				JdbcExecutor.closeConnection(resultSet);
			} else {
				JdbcExecutor.closeResult(resultSet);
			}
		}
		return null;
	}
}
