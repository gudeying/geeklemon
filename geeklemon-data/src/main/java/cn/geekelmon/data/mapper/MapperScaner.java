package cn.geekelmon.data.mapper;

import cn.geekelmon.data.LSQLTool;
import cn.geekelmon.data.annotation.LemonQuery;
import cn.geekelmon.data.sql.QueryType;
import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 */
public class MapperScaner {
	private static final Logger LOGGER = LoggerFactory.getLogger(MapperScaner.class);
	private static final Map<Method, MapperMethodInfo> MAPPER_METHOD_INFO_MAP = new ConcurrentHashMap<>();
	private Pattern regex = Pattern.compile("\\$\\{([^}]*)\\}");
	private static MapperScaner instance;

	public static synchronized MapperScaner getInstance() {
		if (instance == null) {
			instance = new MapperScaner();
		}
		return instance;
	}

	public void MapperScaner(Set<Class<?>> classSet) {

		LOGGER.info("mapper数量:" + classSet.size());
		for (Class<?> cls : classSet) {
			Method[] methods = cls.getMethods();
			for (Method method : methods) {
				LemonQuery lemonQuery = method.getAnnotation(LemonQuery.class);
				if (lemonQuery != null) {
					MapperMethodInfo methodInfo = new MapperMethodInfo();
					List<String> paramHolder = new LinkedList<>();
					String querySql = lemonQuery.value();
					QueryType queryType = lemonQuery.queryType();
					if (queryType == QueryType.AUTO) {
						methodInfo.setQueryType(LSQLTool.getSqlQueryType(querySql));
					} else {
						methodInfo.setQueryType(queryType);
					}

					if (StrUtil.isBlank(querySql)) {
						Class<?> providerClass = lemonQuery.sqlProviderClass();
						String providerMethod = lemonQuery.sqlProviderMethod();
						methodInfo.setSqlProviderClass(providerClass);
						methodInfo.setSqlProviderMethodName(providerMethod);
					}
					methodInfo.setSql(LSQLTool.getPreparedStatement(lemonQuery.value()));

					methodInfo.setLsql(lemonQuery.value());
					Map<String, Integer> paramMapNamed = LSQLTool.getParamsMapNamed(method);
					methodInfo.setParamHolder(paramMapNamed);

					Type returnType = method.getReturnType();
					/* void类型 */
					if (returnType == void.class) {
						methodInfo.setReturnType(ReturnType.VOID);
					} else if (returnType == Map.class) {
						methodInfo.setReturnType(ReturnType.MAP);
					} else if (returnType == List.class) {
						Type genericType = method.getGenericReturnType();
						if (genericType instanceof ParameterizedType) {
							ParameterizedType parameterizedType = (ParameterizedType) genericType;
							// 泛型List<T>中的T
							Class<?> entity = (Class<?>) parameterizedType.getActualTypeArguments()[0];
							methodInfo.setArgumentEntity(entity);
							methodInfo.setReturnType(ReturnType.LIST);
							methodInfo.setGenericReturnType(genericType);
						} else {
							// 说明只是List，没有泛型声明T
							LOGGER.error("泛型声明没有具体参数，无法完成映射");
							throw new RuntimeException(method.getName() + "泛型声明没有具体参数，无法完成映射");
						}
					} else if (LSQLTool.isPriType(returnType)) {
						methodInfo.setReturnType(ReturnType.PRIM);
						methodInfo.setReturnCls(returnType);
					} else {
						// 返回的是bean
						methodInfo.setBeanCls(method.getReturnType());
						methodInfo.setReturnType(ReturnType.BEAN);
					}
					MAPPER_METHOD_INFO_MAP.put(method, methodInfo);
				}
			}
		}
	}

	public static Map<Method, MapperMethodInfo> getMapperMethodInfo() {
		return MAPPER_METHOD_INFO_MAP;
	}
}
