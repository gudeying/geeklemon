package cn.geekelmon.app.api.controller;

import cn.geekelmon.app.api.entity.ApiEntity;
import cn.geekelmon.app.api.entity.GalleryInfo;
import cn.geekelmon.app.api.entity.PageEntity;
import cn.geekelmon.app.api.service.GalleryService;
import cn.geeklemon.core.context.annotation.Autowired;
import cn.geeklemon.server.controller.annotation.Controller;
import cn.geeklemon.server.controller.annotation.Mapping;
import cn.geeklemon.server.controller.annotation.Param;

@Controller
public class GalleryController {
	@Autowired
	private GalleryService service;

	@Mapping(path = "/app/info/gallery/user/page")
	public ApiEntity<PageEntity<GalleryInfo>> page(@Param(key = "userOpenId") String userOpenId,
			@Param(key = "pageNum", notNull = false) Integer pageNum) {
		if (null == pageNum || pageNum < 1) {
			pageNum = 1;
		}
		return new ApiEntity<PageEntity<GalleryInfo>>(service.getPageByUserOpenId(userOpenId, pageNum));
	}
}
