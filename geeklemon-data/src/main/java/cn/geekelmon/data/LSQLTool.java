package cn.geekelmon.data;

import cn.geekelmon.data.annotation.LColumn;
import cn.geekelmon.data.annotation.LId;
import cn.geekelmon.data.annotation.LParam;
import cn.geekelmon.data.sql.QueryType;
import cn.geekelmon.data.tool.ReflectTool;
import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LSQLTool {

	public static Object getOneBeanFromResultSet(Class<?> beanCls, ResultSet resultSet)
			throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException,
			InstantiationException {
		Map<String, Object> map = null;
		if (resultSet.next()) {
			/* if的话就之取一个 */
			map = resultSetToMap(resultSet);
		}
		if (map != null) {
			Object o = getBeanFromMap(beanCls, map);
			return o;
		}
		return null;
	}

	public static List<Object> getBeanListByResultSet(Class<?> cls, ResultSet resultSet) throws SQLException,
			NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {

		List<Object> resultList = new ArrayList<Object>();
		while (resultSet != null && resultSet.next()) {
			Map<String, Object> map = resultSetToMap(resultSet);
			if (map != null) {
				resultList.add(getBeanFromMap(cls, map));
			}
		}
		return resultList;
	}

	/**
	 * resultSet转为list<Map>
	 *
	 * @param resultSet
	 *            resultSet
	 * @return list<Map>
	 */
	public static List<Map> getListMapByResultSet(ResultSet resultSet) throws SQLException, NoSuchMethodException,
			InstantiationException, IllegalAccessException, InvocationTargetException {
		List<Map> resultList = new ArrayList<>();
		while (resultSet != null && resultSet.next()) {
			Map<String, Object> map = resultSetToMap(resultSet);
			if (map != null) {
				resultList.add(map);
			}
		}
		return resultList;
	}

	/**
	 * 从sql ResultSet中取值的方法名称 例如getString
	 *
	 * @param databaseType
	 *            resultSet.getMetaData().getColumnType
	 * @return 从resultSet中取值的方法名称
	 */
	public static String getDataMethod(int databaseType) {
		switch (databaseType) {
		case Types.CHAR:
			return "getString";
		case Types.VARCHAR:
			return "getString";
		case Types.LONGVARCHAR:
			return "getString";
		case Types.NCHAR:
			return "getString";
		case Types.LONGNVARCHAR:
			return "getString";
		case Types.NUMERIC:
			return "getBigDecimal";
		case Types.DECIMAL:
			return "getBigDecimal";
		case Types.BIT:
			return "getBoolean";
		case Types.BOOLEAN:
			return "getBoolean";
		case Types.TINYINT:
			return "getByte";
		case Types.SMALLINT:
			return "getShort";
		case Types.INTEGER:
			return "getInt";
		case Types.BIGINT:
			return "getLong";
		case Types.REAL:
			return "getFloat";
		case Types.FLOAT:
			return "getFloat";
		case Types.DOUBLE:
			return "getDouble";
		case Types.BINARY:
			return "getBytes";
		case Types.VARBINARY:
			return "getBytes";
		case Types.LONGVARBINARY:
			return "getBytes";
		case Types.DATE:
			return "getDate";
		case Types.TIME:
			return "getTime";
		case Types.TIMESTAMP:
			return "getTimestamp";
		case Types.CLOB:
			return "getClob";
		case Types.BLOB:
			return "getBlob";
		case Types.ARRAY:
			return "getArray";
		default:
			return "getString";
		}

	}

	/**
	 * java类型与sql类型的对应
	 *
	 * @param obj
	 * @return
	 */
	public static int getSqlTypes(Object obj) {
		Class<?> objectClass = obj.getClass();
		if (char.class == objectClass) {
			return Types.CHAR;
		} else if (String.class == objectClass) {
			return Types.VARCHAR;
		} else if (BigDecimal.class == objectClass) {
			return Types.NUMERIC;
		} else if (Boolean.class == objectClass) {
			return Types.BIT;
		} else if (Byte.class == objectClass) {
			return Types.TINYINT;
		} else if (Short.class == objectClass) {
			return Types.SMALLINT;
		} else if (Integer.class == objectClass) {
			return Types.INTEGER;
		} else if (Long.class == objectClass) {
			return Types.BIGINT;
		} else if (Float.class == objectClass) {
			return Types.FLOAT;
		} else if (Double.class == objectClass) {
			return Types.DOUBLE;
		} else if (Byte[].class == objectClass) {
			return Types.BINARY;
		} else if (java.util.Date.class == objectClass) {
			return Types.DATE;
		} else if (Time.class == objectClass) {
			return Types.TIME;
		} else if (java.sql.Timestamp.class == objectClass) {
			return Types.TIMESTAMP;
		} else if (java.sql.Clob.class == objectClass) {
			return Types.CLOB;
		} else if (java.sql.Blob.class == objectClass) {
			return Types.BLOB;
		} else if (Object[].class == objectClass) {
			return Types.ARRAY;
		}
		return 0;
	}

	/**
	 * bean属性与字段的map
	 *
	 * @param cls
	 * @return {bean中的字段名，数据库中的字段名}
	 */
	public static Map<String, String> getFieldLabelMap(Class<?> cls) {
		Field[] fields = cls.getDeclaredFields();
		Map<String, String> map = new HashMap<>(fields.length);
		for (Field field : fields) {
			map.put(field.getName(), getFieldToColumnName(field));
		}
		return map;
	}

	/**
	 * <p>
	 * 根据Field和fieldName获取属性值
	 * </p>
	 * <p>
	 * Field的get(obj)如果是基础类型，也会包装成相应的Object，所以之后判断是否赋值只需判断是否为null
	 * <p/>
	 *
	 * @param target
	 * @param fieldName
	 * @return 如果找到属性，就获取属性值，如果没有找到，返回null
	 * @throws IllegalAccessException
	 */
	public static Object getFieldValue(Object target, String fieldName) throws IllegalAccessException {
		Field[] fields = target.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (field.getName().equals(fieldName)) {
				field.setAccessible(true);
				return field.get(target);
			}
		}
		return null;
	}

	/**
	 * 根据属性上的注解获取注解的值（代表数据库中的字段名）
	 *
	 * @param field
	 * @return 如果Column注解没有注明字段名，则返回属性名
	 */
	private static String getFieldToColumnName(Field field) {
		LColumn column = field.getAnnotation(LColumn.class);
		if (column == null) {
			PropertyDescriptor descriptor = BeanUtil.getPropertyDescriptor(field.getDeclaringClass(), field.getName());
			if (descriptor != null) {
				Method method = descriptor.getWriteMethod();
				if (method != null) {
					column = method.getAnnotation(LColumn.class);
				}
			}
		}
		String fieldName = field.getName().toUpperCase();
		if (column != null && (!isBlank(column.value()))) {
			fieldName = column.value();
		}
		/* 数据库返回来的metaData label是大写的 */
		return fieldName.toUpperCase();
	}

	/**
	 * 获取数据库名称对应实体类field的名称
	 *
	 * @param cls
	 *            class
	 * @return map key:数据库名称，value：实体类的field名称
	 */
	public static Map<String, String> getBeanColumnNameWithFieldName(Class<?> cls) {
		Map<String, String> map = new HashMap<>();
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			LColumn lColumn = AnnotationUtil.getAnnotation(field, LColumn.class);
			if (null != lColumn) {
				map.put(lColumn.value(), field.getName());
			}
		}
		return map;
	}

	/**
	 * 获取数据库名称对应实体非空的field的名称
	 *
	 * @param bean
	 *            实体类
	 * @return map key:数据库名称，value：实体类的field名称
	 */
	public static Map<String, String> getNotNullBeanColumnNameWithFieldName(Object bean) {
		Class<?> cls = bean.getClass();
		Map<String, String> map = new HashMap<>();
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			try {
				Object o = field.get(bean);
				LColumn lColumn = AnnotationUtil.getAnnotation(field, LColumn.class);
				if (null != lColumn && ObjectUtil.isNotNull(o)) {
					map.put(lColumn.value(), field.getName());
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

		}
		return map;
	}

	public static boolean isBlank(CharSequence str) {
		int length;
		if ((str == null) || ((length = str.length()) == 0)) {
			return true;
		}
		for (int i = 0; i < length; i++) {
			// 只要有一个非空字符即为非空字符串
			if (!CharUtil.isBlankChar(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @return Map，字段名是大写的 调用之前首先 resultSet.next()
	 */
	public static Map<String, Object> resultSetToMap(ResultSet resultSet)
			throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Map<String, Object> map = new HashMap<>();
		HashMap<String, Integer> columns = new HashMap<String, Integer>();
		int columnSize = resultSet.getMetaData().getColumnCount();
		// 字段信息{字段名：类型}
		for (int i = 1; i <= columnSize; i++) {
			columns.put(resultSet.getMetaData().getColumnLabel(i), resultSet.getMetaData().getColumnType(i));
		}
		// 组装Map
		for (Map.Entry<String, Integer> columnEntry : columns.entrySet()) {
			String methodName = getDataMethod(columnEntry.getValue());
			Object value = ReflectTool.invokeMethod(resultSet, methodName, columnEntry.getKey());
			map.put(columnEntry.getKey().toUpperCase(), value);
		}
		return map;
	}

	/**
	 * <br/>
	 * 简单String、int等类型属性<br>
	 * 数据库关联暂时不支持<br>
	 *
	 * @param cls
	 * @param mapArg
	 * @param <T>
	 *            target Class
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBeanFromMap(Class<T> cls, Map<String, ?> mapArg)
			throws IllegalAccessException, InstantiationException {
		Object obj = cls.newInstance();
		Map<String, String> fieldMap = getFieldLabelMap(cls);
		for (Map.Entry<String, String> entry : fieldMap.entrySet()) {
			try {
				Field field = obj.getClass().getDeclaredField(entry.getKey());
				if (field != null) {
					field.setAccessible(true);
					Object o = mapArg.get(entry.getValue());
					Class<?> fieldType = field.getType();

					Object convertValue = Convert.convert(fieldType, o);
					if (ObjectUtil.isNotNull(convertValue)) {
						field.set(obj, convertValue);
					}
				}
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
		return (T) obj;
	}

	/**
	 * 获取方法中@LParam注解的参数名
	 *
	 * @param method
	 *            方法
	 * @param i
	 *            该方法的第几个参数
	 * @return 如果有注解，返回注解值，如果没有，返回null
	 */
	public static String getParamNameFromAnnotation(Method method, int i) {
		String paramName = null;
		Object[] paramAnnos = method.getParameterAnnotations()[i];// 获取第i个参数的注解
		if (paramAnnos == null) {
			// 没有注解就返回null
			return null;
		}
		for (Object paramAnno : paramAnnos) {
			if (paramAnno instanceof LParam) {
				paramName = ((LParam) paramAnno).value();
				break;
			}
		}
		return paramName;
	}

	/**
	 * 获取注解过的参数在方法中的参数位置
	 *
	 * @param method
	 *            method
	 * @return Map {参数名，参数位置}
	 */
	public static Map<String, Integer> getParamsMapNamed(Method method) {
		final Map<String, Integer> params = new HashMap<>();
		final Class<?>[] argTypes = method.getParameterTypes();
		for (int i = 0; i < argTypes.length; i++) {
			String name = getParamNameFromAnnotation(method, i);
			if (name != null) {
				params.put(name, i);
			}
		}
		return params;
	}

	/**
	 * 将有占位符的sql语句占位符替换为 ？
	 *
	 * @param Lsql
	 * @return
	 */
	public static String getPreparedStatement(String Lsql) {
		String sql = Lsql;
		Pattern regex = Pattern.compile("\\$\\{([^}]*)\\}");
		Matcher matcher = regex.matcher(sql);
		while (matcher.find()) {
			sql = sql.replace(matcher.group(0), "?");
		}
		return sql;
	}

	/**
	 * 获取一个bean字段非空的属性 {非空sql label名：值}
	 *
	 * @param object
	 * @return
	 */
	public static Map<String, Object> getNotNullField_Sql_Label_Map(Object object) throws IllegalAccessException {
		Field[] fields = object.getClass().getDeclaredFields();
		Map<String, Object> map = new HashMap<>(fields.length);
		for (Field field : fields) {
			field.setAccessible(true);
			if (field.get(object) != null) {
				String name = getFieldToColumnName(field);
				map.put(name, field.get(object));
			}
		}
		return map;
	}

	public static <T> T getPrimeValueFromResultSet(ResultSet resultSet, Class<T> type) {
		try {
			if (resultSet.next()) {
				Map<String, Object> map = resultSetToMap(resultSet);
				for (Map.Entry<String, Object> entry : map.entrySet()) {
					Object value = entry.getValue();
					return Convert.convert(type, value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 基本类型
	 **/
	private static final Class<?>[] PRI_TYPE = { String.class, boolean.class, byte.class, short.class, int.class,
			long.class, float.class, double.class, char.class, Boolean.class, Byte.class, Short.class, Integer.class,
			Long.class, Float.class, Double.class, Character.class, BigInteger.class, BigDecimal.class };

	/**
	 * 判断是否为基本类型
	 *
	 * @param cls
	 *            需要进行判断的Type
	 * @return 是否为基本类型
	 */
	public static boolean isPriType(Type cls) {
		for (Class<?> priType : PRI_TYPE) {
			if (cls == priType) {
				return true;
			}
		}
		return false;
	}

	public static List<Object> getCallableStatementResult(CallableStatement statement) throws SQLException {
		ArrayList<Object> result = new ArrayList<Object>();
		ParameterMetaData parameterMetaData = statement.getParameterMetaData();

		// 遍历参数信息
		for (int i = 0; i < parameterMetaData.getParameterCount(); i++) {
			int paramMode = parameterMetaData.getParameterMode(i + 1);

			// 如果是带有 out 属性的参数,则对其进行取值操作
			if (paramMode == ParameterMetaData.parameterModeOut || paramMode == ParameterMetaData.parameterModeInOut) {
				// 取值方法名
				String methodName = getDataMethod(parameterMetaData.getParameterType(i + 1));
				Object value;
				try {

					// 获得取值方法参数参数是 int 类型的对应方法
					Method method = ReflectUtil.getMethod(CallableStatement.class, methodName,
							new Class[] { int.class });

					// 反射调用方法
					value = ReflectUtil.invoke(statement, methodName, i + 1);
					result.add(value);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	/**
	 * 获取 @LId 注解的field
	 *
	 * @param bean
	 * @return
	 */
	public static Map<String, String> getLTableIdMap(Object bean) {
		Class<?> beanClass = bean.getClass();
		Map<String, String> map = new HashMap<>();
		Field[] declaredFields = beanClass.getDeclaredFields();
		for (Field field : declaredFields) {
			LId id = AnnotationUtil.getAnnotation(field, LId.class);
			LColumn lColumn = AnnotationUtil.getAnnotation(field, LColumn.class);
			if (ObjectUtil.isNotNull(id) && ObjectUtil.isNotNull(lColumn) && StrUtil.isNotBlank(lColumn.value())) {
				map.put(lColumn.value(), field.getName());
			}
		}
		return map;
	}

	public static QueryType getSqlQueryType(String sql) {
		String sqlUpper = sql.trim().toUpperCase();
		if (sqlUpper.startsWith("SELECT")) {
			return QueryType.SELECT;
		}
		if (sqlUpper.startsWith("INSERT")) {
			return QueryType.INSERT;
		}
		if (sqlUpper.startsWith("DELETE")) {
			return QueryType.DELETE;
		}
		if (sqlUpper.startsWith("UPDATE")) {
			return QueryType.UPDATE;
		}

		return null;
	}

}
