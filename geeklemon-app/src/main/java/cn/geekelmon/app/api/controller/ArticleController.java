package cn.geekelmon.app.api.controller;

import cn.geekelmon.app.api.entity.*;
import cn.geekelmon.app.api.exception.ControllerException;
import cn.geekelmon.app.api.exception.ControllerExceptionHandler;
import cn.geekelmon.app.api.service.ArticleService;
import cn.geekelmon.cache.annotation.LCache;
import cn.geeklemon.core.aop.extra.ExceptionAvoid;
import cn.geeklemon.core.context.annotation.Autowired;
import cn.geeklemon.core.context.annotation.Bean;
import cn.geeklemon.server.controller.annotation.Controller;
import cn.geeklemon.server.controller.annotation.Mapping;
import cn.geeklemon.server.controller.annotation.Param;
import cn.geeklemon.server.request.HttpRequest;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author : Kavin Gu Project Name : geeklemon Description :
 * @version : ${VERSION} 2019/11/5 15:14 Modified by : kavingu
 */
@Controller
@Bean(single = true)
public class ArticleController {
	@Autowired
	private ArticleService service;

	// @Log("获取文章列表")
	@Mapping(path = "/app/info/article/page")
	public ApiEntity<PageEntity<ArticlePreview>> pagePreview(HttpRequest request) {
		String pageNum = request.getParameter("pageNum");
		if (StrUtil.isNotBlank(pageNum)) {
			int num = Integer.parseInt(pageNum);
			try {
				String subject = request.getParameter("subject");
				PageEntity<ArticlePreview> list = null;
				if (StrUtil.isNotBlank(subject)) {
					list = service.pageArticle(num, subject);
				} else {
					list = service.pageArticle(num);
				}
				return new ApiEntity<PageEntity<ArticlePreview>>(list);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return new ApiEntity<>("内部出错,未找到合适的参数：pageNum--" + pageNum);
	}

	/**
	 * 用户的文章
	 *
	 * @param userOpenId
	 * @param request
	 * @return
	 */
	@ExceptionAvoid(handler = ControllerExceptionHandler.class)
	@Mapping(path = "/app/info/user/movements/mine")
	public ApiEntity<PageEntity<ArticlePreview>> userPageArticle(@Param(key = "userOpenId") String userOpenId,
			HttpRequest request) {
		int pageNum = 1;
		String requestPageNum = request.getParameter("pageNum");
		if (NumberUtil.isNumber(requestPageNum)) {
			pageNum = Integer.parseInt(requestPageNum);
		}
		PageEntity<ArticlePreview> pageEntity = service.userPageArticle(userOpenId, pageNum);
		return new ApiEntity<>(pageEntity);
	}

	@ExceptionAvoid(handler = ControllerExceptionHandler.class)
	@Mapping(path = "/app/info/article/detail")
	public ApiEntity<Article> detail(@Param(key = "id") String id, HttpRequest request) throws Exception {
		Exception exception = null;
		try {
			String userOpenId = request.getParameter("userOpenId");
			Article detail = service.detail(Integer.parseInt(id), userOpenId);
			return new ApiEntity<>(detail);
		} catch (Exception e) {
			e.printStackTrace();
			exception = e;
		} finally {
			if (exception != null) {
				throw new ControllerException("内部出错，未找到合适的参数：id --" + id);
			}
		}
		return new ApiEntity<>("内部出错，未找到合适的参数：id --" + id);
	}

	@ExceptionAvoid(handler = ControllerExceptionHandler.class)
	@Mapping(path = "/app/info/article/comments")
	public ApiEntity<PageEntity<CommentInfo>> comments(@Param(key = "articleId") String articleId,
			@Param(key = "pageNum") String pageNum) {
		int id = 0;
		int num = 1;
		id = Integer.parseInt(articleId);
		num = Integer.parseInt(pageNum);
		PageEntity<CommentInfo> pageEntity = service.pageCommentInfo(num, id);
		return new ApiEntity<>(pageEntity);
	}

	@ExceptionAvoid(handler = ControllerExceptionHandler.class)
	@Mapping(path = "/app/info/article/subjects")
	public ApiEntity<List<SubjectDefine>> subjects() throws Exception {
		List<SubjectDefine> subjects = service.subjects();
		return new ApiEntity<>(subjects);
	}

	/**
	 * 文章相关推荐
	 *
	 * @return
	 */
	@Mapping(path = "/app/info/article/relative")
	public ApiEntity<List<ArticlePreview>> relativeArticles(@Param(key = "articleId") String articleId) {
		List<ArticlePreview> list = service.relatives(articleId);
		return new ApiEntity<>(list);
	}

	@ExceptionAvoid(handler = ControllerExceptionHandler.class)
	@Mapping(path = "/app/info/article/search/suggestions")
	public ApiEntity<List<String>> searchSuggestions(@Param(key = "kewWord") String kewWord) {
		return new ApiEntity<>(new LinkedList<>());
	}

	@Mapping(path = "/app/info/article/search/page")
	public ApiEntity<PageEntity<ArticlePreview>> search(@Param(key = "keyWord") String kewWord,
			@Param(key = "pageNum", notNull = false, notBlank = false) String pageNum) {
		int num = 1;
		num = NumberUtil.parseInt(pageNum);
		return new ApiEntity<>(service.likeTitle(kewWord, num));
	}

	@ExceptionAvoid(handler = ControllerExceptionHandler.class)
	@Mapping(path = "/app/info/article/top")
	public ApiEntity<PageEntity<ArticlePreview>> topFive() {
		return new ApiEntity<>(service.top());
	}

	@Mapping(path = "/app/info/user/movements/star")
	public ApiEntity<PageEntity<ArticlePreview>> userPageStarArticles(@Param(key = "userOpenId") String useOpenId,
			@Param(key = "pageNum") String page) {
		try {
			int pageNum = NumberUtil.parseInt(page);
			return new ApiEntity<>(service.userStarPageArticle(useOpenId, pageNum));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ApiEntity<>("内部出错");
	}

	@Mapping(path = "/app/info/user/movements/follow")
	public ApiEntity<PageEntity<ArticlePreview>> userPageFollowArticles(@Param(key = "userOpenId") String useOpenId,
			@Param(key = "pageNum") String page) {
		try {
			int pageNum = NumberUtil.parseInt(page);
			return new ApiEntity<>(service.userFollowPageArticle(useOpenId, pageNum));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ApiEntity<>("内部出错");
	}

	@ExceptionAvoid(handler = ControllerExceptionHandler.class)
	@Mapping(path = "/app/info/article/detail/${id}")
	public ApiEntity<Article> pathDetail(@Param(key = "id") String id, HttpRequest request) throws Exception {
		String userOpenId = request.getParameter("userOpenId");
		Article detail = service.detail(Integer.parseInt(id), userOpenId);
		return new ApiEntity<>(detail);
	}
}
