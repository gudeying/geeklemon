package cn.geekelmon.app.api.service;

import java.util.List;

import cn.geekelmon.app.api.entity.GalleryInfo;
import cn.geekelmon.app.api.entity.PageEntity;
import cn.geekelmon.app.api.service.mapper.GalleryInfoMapper;
import cn.geeklemon.core.context.annotation.Autowired;
import cn.geeklemon.core.context.annotation.Bean;
import cn.hutool.core.util.PageUtil;

@Bean
public class GalleryService {
	@Autowired
	private GalleryInfoMapper galleryInfoMapper;
	private int pageSize = 9;

	public PageEntity<GalleryInfo> getPageByUserOpenId(String userOpenId, int pageNum) {

		pageNum = pageNum > 0 ? pageNum : 1;
		int start = pageSize * (pageNum - 1);
		int end = (pageSize * pageNum - 1) + pageSize;

		String countSql = "select count(*) from gallery g left join user u on u.name=g.user where u.openid = '"
				+ userOpenId + "'";
		int count = getCount(countSql);
		int totalPage = PageUtil.totalPage(count, pageSize);

		List<GalleryInfo> list = galleryInfoMapper.pageListByUser(userOpenId, start, end);

		PageEntity<GalleryInfo> pageEntity = new PageEntity<>();
		pageEntity.setHasNext(totalPage > pageNum);
		pageEntity.setPageCount(totalPage);
		pageEntity.setPageNum(pageNum);
		pageEntity.setObjects(list);

		return pageEntity;
	}

	private int getCount(String sql) {
//		return 10;
		 return galleryInfoMapper.count(sql);
	}
}
