package cn.geekelmon.data.annotation;

import cn.geekelmon.data.sql.DefaultSqlProvider;
import cn.geekelmon.data.sql.QueryType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 可以使用{@link LParam 传参}<br/>
 * 也可以按顺序传参数，不使用LParam<br/>
 * 可以传入一个实体类，实体类的 field名称要和sql中的占位参数一致， 并且必须至少有一个只有一个参数的setter
 * {@link cn.hutool.core.bean.BeanUtil#isBean(Class)}<br/>
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface LemonQuery {

	/**
	 * 如果需要运行时生成动态sql，请使用{@link LemonQuery#sqlProviderClass()} 只有该value为空才会使用
	 * sqlProviderClass 方式获取sql
	 *
	 * @return sql
	 */
	String value() default "";

	/**
	 * 执行的类型,增删改查可以自动识别
	 *
	 * @return
	 */
	QueryType queryType() default QueryType.AUTO;

	/**
	 * 可以根据参数动态生成sql，切记该类必须有非空构造方法，因为初始化使用 newInstance();
	 * 示例：{@link DefaultSqlProvider}
	 *
	 * @return sqlProvider
	 */
	Class<?> sqlProviderClass() default DefaultSqlProvider.class;

	/**
	 * 获取动态sql的时候使用的方法，切记传入的参数和该注解所在的方法一致
	 *
	 * @return sqlProviderMethodName
	 */
	String sqlProviderMethod() default "value";
}
