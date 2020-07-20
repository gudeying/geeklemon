package cn.geekelmon.app.api.service.mapper;

import java.util.List;

import cn.geekelmon.app.api.entity.GalleryInfo;
import cn.geekelmon.cache.annotation.LCache;
import cn.geekelmon.data.annotation.LMapper;
import cn.geekelmon.data.annotation.LemonQuery;

@LMapper
public interface GalleryInfoMapper {
	GalleryInfo getById(int id);

	@LemonQuery("select g.*,u.openid user_open_id from gallery g left join user u on u.name=g.user where u.openid = ${userOpenId} limit ${start},${end}")
	List<GalleryInfo> pageListByUser(String userOpenId, int pageStartNum, int pageEndNum);
	
	@LemonQuery(sqlProviderClass=GalleryCountSqlProvider.class,sqlProviderMethod = "count")
	int count(String sql);
}
