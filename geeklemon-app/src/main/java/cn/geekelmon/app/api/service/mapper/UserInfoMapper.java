package cn.geekelmon.app.api.service.mapper;

import cn.geekelmon.app.api.entity.UserInfo;
import cn.geekelmon.data.annotation.LMapper;
import cn.geekelmon.data.annotation.LemonQuery;
import cn.geekelmon.data.sql.QueryType;

@LMapper
public interface UserInfoMapper {
    @LemonQuery(queryType = QueryType.UPDATE, sqlProviderMethod = "update")
    int update(UserInfo info);

    @LemonQuery(value = "select * from user where openid = ${openId}")
    UserInfo getUserByOpenId(String openId);
}
