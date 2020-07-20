package cn.geekelmon.app.api.service.mapper;

import java.util.Date;

import cn.geekelmon.data.annotation.LMapper;
import cn.geekelmon.data.annotation.LemonQuery;
import cn.geekelmon.data.sql.QueryType;

@LMapper
public interface ArticleOpMapper {

	@LemonQuery("select primary_id from star_info s where s.user_open_id = ${userOpenId} and s.article_id = ${articleId}")
	int isUserStaredArticle(String userOpenId, int articleId);

	@LemonQuery("INSERT into star_info (user_open_id,article_id,star_date) VALUES (${userOpenId},${articleId},${starDate})")
	int starArticle(String userOpenId, int articleId, Date starDate);

	@LemonQuery("delete from star_info s where s.user_open_id = ${userOpenId} and s.article_id = ${articleId}")
	int unStarArticle(String userOpenId, int articleId);

	@LemonQuery("select primary_id from follow_info s where s.user_open_id = ${userOpenId} and s.follow_user_opid = ${targetUserOpenId}")
	int isUserFollowed(String userOpenId, String targetUserOpenId);

	@LemonQuery(queryType = QueryType.INSERT, value = "INSERT into follow_info (user_open_id,follow_user_opid,follow_date) VALUES (${userOpenId},${targetUserOpenId},${date})")
	int addFollowInfo(String userOpenId, String targetUserOpenId, Date date);

	@LemonQuery(queryType = QueryType.DELETE, value = "DELETE from follow_info s where s.user_open_id = ${userOpenId} and s.follow_user_opid = ${targetUserOpenId}")
	int unFollow(String userOpenId, String targetUserOpenId);

}
