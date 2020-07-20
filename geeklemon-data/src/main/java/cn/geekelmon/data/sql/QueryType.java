package cn.geekelmon.data.sql;

/**
 * @author : Kavin Gu Project Name : redant Description :
 * @version : ${VERSION} 2019/3/2 22:06 Modified by : kavingu
 */
public enum QueryType {
	SELECT, UPDATE, DELETE, INSERT,
	/**
	 * 自动识别，只识别增删改查
	 */
	AUTO,
	/**
	 * 插入并返回自增长主键
	 */
	INSERT_RETURN_GENERATED_KEYS,

	/**
	 * 
	 */
}
