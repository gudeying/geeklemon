package cn.geekelmon.data.support;

public interface JdbcExecutorFactory {
	LJdbcExecutor getLJdbcExcutor(boolean resourceManaged);

	void release(LJdbcExecutor executor);

	LJdbcExecutor getJdbcExcutor();

	/**
	 * 获取新的executor，不会加入到当前线程
	 * 
	 * @return
	 */
	LJdbcExecutor newJdbcExcutor();
}
