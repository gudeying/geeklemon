package cn.geekelmon.example.ioc.data.mapper;

import cn.geekelmon.data.annotation.LMapper;
import cn.geekelmon.data.annotation.LemonQuery;
import cn.geekelmon.data.sql.QueryType;
import cn.geekelmon.example.ioc.data.entity.TestInfo;

@LMapper
public interface TestInfoMpper {
	@LemonQuery(value = "insert into test_info (t_msg) values (${msg})", queryType = QueryType.INSERT)
	int save(TestInfo info);
}
