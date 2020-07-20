package cn.geekelmon.app.api.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

import cn.geekelmon.app.api.entity.Article;
import cn.geekelmon.app.api.entity.ArticlePreview;
import cn.geekelmon.app.api.entity.CommentInfo;
import cn.geekelmon.app.api.entity.PageEntity;
import cn.geekelmon.app.api.entity.SubjectDefine;
import cn.geekelmon.app.api.service.mapper.ArticleOpMapper;
import cn.geekelmon.app.api.util.HtmlUtil;
import cn.geekelmon.cache.annotation.LCache;
import cn.geeklemon.core.bean.factory.InitializingBean;
import cn.geeklemon.core.context.annotation.Autowired;
import cn.geeklemon.core.context.annotation.Bean;
import cn.geeklemon.core.context.annotation.Value;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.PageUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.DbUtil;
import cn.hutool.db.handler.RsHandler;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

/**
 */
@Bean(name = "articleService", single = true)
public class ArticleService implements InitializingBean {

    private static final Log LOG = LogFactory.get();

    @Value(name = "app.serverAddress", defaultValue = "http://127.0.0.1:8080/")
    private String serverAddress;

    @Autowired
    private DataSource dataSource;

    @Value(name = "lemon.service.pageSize", defaultValue = "10")
    private int pageSize;

    @Autowired
    private ArticleOpMapper articleOpMapper;

    // @LCache
    public PageEntity<ArticlePreview> pageArticle(int pageNum) {
        pageNum = pageNum > 0 ? pageNum : 1;
        int start = pageSize * (pageNum - 1);
        int end = (pageSize * pageNum - 1) + pageSize;

        String sql = "SELECT " + "t.id," + "t.src logo," + "t.author," + "t.title," + "t.description summary,"
                + "t.subject," + "t.snum," + "t.znum," + "u.openid, " + "u.userlogo " + "FROM " + "article t "
                + "LEFT JOIN user u ON u.id = t.authorid  " + " ORDER BY " + "t.id DESC " + "LIMIT ?,?";
        String countSql = "select count(*) cnt from article ";
        PageEntity<ArticlePreview> pageEntity = null;
        try {
            Db queryService = DbUtil.use(dataSource);
            List<ArticlePreview> query = queryService.query(sql, articlePreviewListHandler, start, end);
            int count = queryService.query(countSql, countHandler);
            int totalPage = PageUtil.totalPage(count, pageSize);
            pageEntity = new PageEntity<>();
            pageEntity.setHasNext(totalPage > pageNum);
            pageEntity.setObjects(query);
            pageEntity.setPageCount(totalPage);
            pageEntity.setPageNum(pageNum);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pageEntity;
    }

    /**
     * 获取分页的articlePreview
     */
    public PageEntity<ArticlePreview> pageArticle(int pageNum, String subject) throws SQLException {
        pageNum = pageNum > 0 ? pageNum : 1;
        int start = pageSize * (pageNum - 1);
        String sql = "SELECT " + "t.id," + "t.src logo," + "t.author," + "t.title," + "t.description summary,"
                + "t.subject," + "t.snum," + "t.znum," + "u.openid, " + "u.userlogo " + "FROM " + "article t  "
                + "LEFT JOIN user u ON u.id = t.authorid  " + "WHERE " + "t.SUBJECT = ? " + " ORDER BY " + "t.id DESC "
                + "LIMIT ?,? ";
        String countSql = "select count(*) cnt from article where subject = ? ";
        PageEntity<ArticlePreview> pageEntity = null;
        try {
            Db queryService = DbUtil.use(dataSource);
            List<ArticlePreview> query = queryService.query(sql, articlePreviewListHandler, subject, start, pageSize);
            int count = queryService.query(countSql, countHandler, subject);
            int totalPage = PageUtil.totalPage(count, pageSize);
            pageEntity = new PageEntity<>();
            pageEntity.setHasNext(totalPage > pageNum);
            pageEntity.setObjects(query);
            pageEntity.setPageCount(totalPage);
            pageEntity.setPageNum(pageNum);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pageEntity;
    }

//    @LCache(timeOut = 3600 * 1000 * 24)
    @LCache(timeOut = 10 * 1000)
    public Article detail(int articleId, String queryUserOpenId) throws SQLException {
        String sql = "select a.*,u.openid,u.name,u.userlogo from article a left join user u on u.id = a.authorid where a.id = ?";
        Db queryService = DbUtil.use(dataSource);
        Article query = queryService.query(sql, detailHandler, articleId);
        if (StrUtil.isNotBlank(queryUserOpenId)) {
            boolean stared = isStared(queryUserOpenId, articleId);
            query.setStared(stared);
        }
         System.out.println("not cache");
        return query;
    }

    // 缓存24小时
    @LCache(timeOut = 3600 * 1000 * 24)
    public List<SubjectDefine> subjects() throws SQLException {
        String sql = "select distinct(subject) subject from article";
        Db queryService = DbUtil.use(dataSource);
        return queryService.query(sql, subjectListHandler);
    }

    public PageEntity<CommentInfo> pageCommentInfo(int pageNum, int articleId) {
        PageEntity<CommentInfo> pageEntity = null;
        pageNum = pageNum > 0 ? pageNum : 1;
        int start = pageSize * (pageNum - 1);
        try {
            String sql = "select c.id,c.content,c.user,u.userlogo, c.time,c.userid,c.towho,c.parentid "
                    + " from comment c " + " left join user u on u.id = c.userid  "
                    + "where c.articleid = ? and c.parentid =0 order by c.id desc limit ?,? ";
            String countSql = "select count(*) cnt from comment c where c.articleid = ? and parentid = 0";
            Db queryService = DbUtil.use(dataSource);
            List<CommentInfo> infoList = queryService.query(sql, commentRsHandler, articleId, start, pageSize);
            int count = queryService.query(countSql, countHandler, articleId);
            int totalPage = PageUtil.totalPage(count, pageSize);
            pageEntity = new PageEntity<>();
            pageEntity.setHasNext(totalPage > pageNum);
            pageEntity.setObjects(infoList);
            pageEntity.setPageCount(totalPage);
            pageEntity.setPageNum(pageNum);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pageEntity;
    }

    @LCache
    public List<ArticlePreview> relatives(String articleId) {

        String sql = "SELECT id,title,src preview_image FROM article "
                + " WHERE SUBJECT = ( SELECT s.SUBJECT FROM article s WHERE s.id = ? ) and id != ? ORDER BY RAND( ) LIMIT 5";

        try {
            Db queryService = DbUtil.use(dataSource);
            return queryService.query(sql, new RelativeArticlePreview(), articleId, articleId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new LinkedList<>();
    }

    public PageEntity<ArticlePreview> likeTitle(String keyWord, int pageNum) {
        String like = "%" + keyWord.trim() + "%";
        pageNum = pageNum < 1 ? 1 : pageNum;
        int pageSize = 3;
        int start = pageSize * (pageNum - 1);
        try {
            String sql = "SELECT " + "t.id," + "t.src logo," + "t.author," + "t.title," + "t.description summary,"
                    + "t.subject," + "t.snum," + "t.znum," + "u.openid, " + "u.userlogo " + "FROM " + "article t  "
                    + "LEFT JOIN user u ON u.id = t.authorid  " + "WHERE " + "t.title like  ? " + " ORDER BY "
                    + "t.id DESC " + "LIMIT ?,? ";
            String countSql = "select count(*) cnt from article where title like ? ";
            Db queryService = DbUtil.use(dataSource);
            List<ArticlePreview> query = queryService.query(sql, new ArticlePreviewListHandler(), like, start,
                    pageSize);

            int count = queryService.query(countSql, countHandler, like);
            int totalPage = PageUtil.totalPage(count, pageSize);
            PageEntity<ArticlePreview> pageEntity = new PageEntity<>();
            pageEntity.setHasNext(totalPage > pageNum);
            pageEntity.setObjects(query);
            pageEntity.setPageCount(totalPage);
            pageEntity.setPageNum(pageNum);
            return pageEntity;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return 收藏最多的五篇
     */
    public PageEntity<ArticlePreview> top() {
        try {
            String sql = "SELECT " + "t.id," + "t.src logo," + "t.author," + "t.title," + "t.description summary,"
                    + "t.subject," + "t.snum," + "t.znum," + "u.openid, " + "u.userlogo " + "FROM " + "article t  "
                    + "LEFT JOIN user u ON u.id = t.authorid  " + " ORDER BY " + "t.snum DESC " + "LIMIT 0,5 ";
            Db queryService = DbUtil.use(dataSource);
            List<ArticlePreview> list = queryService.query(sql, new ArticlePreviewListHandler());
            PageEntity<ArticlePreview> pageEntity = new PageEntity<>();
            pageEntity.setHasNext(false);
            pageEntity.setObjects(list);
            pageEntity.setPageCount(1);
            pageEntity.setPageNum(1);
            return pageEntity;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new PageEntity<>();

    }

    public PageEntity<ArticlePreview> userPageArticle(String userOpenId, int pageNum) {
        pageNum = pageNum < 1 ? 1 : pageNum;
        int pageSize = 6;
        int start = pageSize * (pageNum - 1);
        String sql = "SELECT t.id," + "       t.src  logo," + "       t.author," + "       t.title,"
                + "       t.description summary," + "       t.subject," + "       t.snum," + "       t.znum,"
                + "       u.openid, " + "       u.userlogo" + "  FROM article t " + "  LEFT JOIN user u"
                + "    ON u.id = t.authorid" + " WHERE u.openid = ? " + " ORDER BY t.id DESC LIMIT ?, ?";
        String countSql = "select count(*) cnt from(SELECT t.id FROM article t LEFT JOIN user u ON u.id = t.authorid WHERE u.openid = ?) lllll ";
        PageEntity<ArticlePreview> pageEntity = null;
        try {
            Db queryService = DbUtil.use(dataSource);
            List<ArticlePreview> query = queryService.query(sql, new ArticlePreviewListHandler(), userOpenId, start,
                    pageSize);

            int count = queryService.query(countSql, countHandler, userOpenId);
            int totalPage = PageUtil.totalPage(count, pageSize);
            pageEntity = new PageEntity<>();
            pageEntity.setHasNext(totalPage > pageNum);
            pageEntity.setObjects(query);
            pageEntity.setPageCount(totalPage);
            pageEntity.setPageNum(pageNum);
            return pageEntity;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PageEntity<ArticlePreview> userStarPageArticle(String userOpenId, int pageNum) {
        pageNum = pageNum < 1 ? 1 : pageNum;
        int pageSize = 6;
        int start = pageSize * (pageNum - 1);
        String sql = "SELECT t.id," + "       t.src         logo," + "       t.author," + "       t.title,"
                + "       t.description summary," + "       t.subject," + "       t.snum, " + "       t.znum, "
                + "       u.openid, " + "       u.userlogo " + "  FROM star_info s " + "  left join article t "
                + "    on t.id = s.article_id " + "  left join user u " + "    on u.id = t.authorid "
                + " WHERE s.user_open_id = ? " + " ORDER BY s.star_date DESC limit ?, ?";
        String countSql = "select count(*) from star_info s where s.user_open_id = ?";
        PageEntity<ArticlePreview> pageEntity = null;
        pageEntity = getPageEntity(userOpenId, pageNum, pageSize, start, sql, countSql);
        if (pageEntity != null)
            return pageEntity;
        return new PageEntity<>();
    }

    public PageEntity<ArticlePreview> userFollowPageArticle(String userOpenId, int pageNum) {
        pageNum = pageNum < 1 ? 1 : pageNum;
        int pageSize = 6;
        int start = pageSize * (pageNum - 1);
        String sql = "SELECT  t.id," + "       t.src  logo," + "       t.author," + "       t.title,"
                + "       t.description summary," + "       t.subject," + "       t.snum," + "       t.znum,"
                + "       u.openid, " + "       u.userlogo" + " FROM follow_info s "
                + " LEFT JOIN user u ON u.openid = s.follow_user_opid " + " LEFT JOIN article t ON t.authorid = u.id "
                + "WHERE s.user_open_id = ? order by t.id desc limit ?,?";
        String countSql = "select count(*) from (" + "select t.id FROM follow_info s "
                + " LEFT JOIN user u ON u.openid = s.user_open_id " + " LEFT JOIN article t ON t.authorid = u.id "
                + "WHERE s.user_open_id = ? ) lll";
        PageEntity<ArticlePreview> pageEntity1 = getPageEntity(userOpenId, pageNum, pageSize, start, sql, countSql);
        if (pageEntity1 != null)
            return pageEntity1;
        return new PageEntity<>();
    }

    private PageEntity<ArticlePreview> getPageEntity(String userOpenId, int pageNum, int pageSize, int start,
                                                     String sql, String countSql) {
        PageEntity<ArticlePreview> pageEntity;
        try {
            Db queryService = DbUtil.use(dataSource);
            List<ArticlePreview> query = queryService.query(sql, articlePreviewListHandler, userOpenId, start,
                    pageSize);
            int count = queryService.query(countSql, countHandler, userOpenId);
            int totalPage = PageUtil.totalPage(count, pageSize);
            pageEntity = new PageEntity<>();
            pageEntity.setHasNext(totalPage > pageNum);
            pageEntity.setObjects(query);
            pageEntity.setPageCount(totalPage);
            pageEntity.setPageNum(pageNum);
            return pageEntity;
        } catch (SQLException e) {
            LOG.error(e);
        }
        return null;
    }

    private RsHandler<List<ArticlePreview>> articlePreviewListHandler = new ArticlePreviewListHandler();

    private RsHandler<Article> detailHandler = new RsHandler<Article>() {
        @Override
        public Article handle(ResultSet rs) throws SQLException {
            try {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String html = rs.getString("content");
                    String content = HtmlUtil.processSrc(html, serverAddress);
                    Article article = new Article.Builder()
                            .browserUrl(serverAddress + "article/detail/" + rs.getString("id"))
                            .collectionNum(rs.getInt("snum")).starNum(rs.getInt("znum")).content(content).id(id)
                            .logoSrc(serverAddress + "images/" + rs.getString("src")).subject(rs.getString("subject"))
                            .title(rs.getString("title")).build();
                    article.setAuthorLogoUrl(serverAddress + "images/userlogo/" + rs.getString("userlogo"));
                    article.setAuthorOpenId(rs.getString("OPENID"));
                    article.setAuthorName(rs.getString("NAME"));
                    article.setWriteDate(rs.getDate("WRITE_DATE"));
                    return article;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    };

    private RsHandler<List<SubjectDefine>> subjectListHandler = new RsHandler<List<SubjectDefine>>() {
        @Override
        public List<SubjectDefine> handle(ResultSet rs) throws SQLException {
            List<SubjectDefine> result = new ArrayList<>();
            while (rs.next()) {
                try {
                    String subject = rs.getString("subject");
                    // 暂时名称和code相同

                    SubjectDefine subjectDefine = new SubjectDefine(subject, subject);
                    result.add(subjectDefine);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }
    };

    private RsHandler<Integer> countHandler = new RsHandler<Integer>() {
        @Override
        public Integer handle(ResultSet rs) throws SQLException {
            try {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
    };

    private RsHandler<List<CommentInfo>> commentRsHandler = new RsHandler<List<CommentInfo>>() {
        @Override
        public List<CommentInfo> handle(ResultSet rs) throws SQLException {
            List<CommentInfo> list = new ArrayList<>();
            try {
                while (rs.next()) {
                    CommentInfo info = new CommentInfo.Builder().id(rs.getInt("id")).timeInfo(rs.getString("time"))
                            .content(rs.getString("content")).parentId(rs.getInt("parentid"))
                            .toWho(rs.getString("towho")).userName(rs.getString("user")).userId(rs.getString("userid"))
                            .userLogo(serverAddress + "images/userlogo/" + rs.getString("userlogo")).build();
                    list.add(info);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return list;
        }
    };

    class RelativeArticlePreview implements RsHandler<List<ArticlePreview>> {

        @Override
        public List<ArticlePreview> handle(ResultSet rs) throws SQLException {
            List<ArticlePreview> list = new LinkedList<>();

            while (rs.next()) {
                try {
                    int id = rs.getInt("id");
                    String title = rs.getString("title");
                    String previewImage = ArticleService.this.serverAddress + "images/" + rs.getString("preview_image");
                    ArticlePreview articlePreview = new ArticlePreview.Builder().id(id).title(title)
                            .previewImageUrl(previewImage).build();
                    list.add(articlePreview);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return list;
        }
    }

    class ArticlePreviewListHandler implements RsHandler<List<ArticlePreview>> {

        @Override
        public List<ArticlePreview> handle(ResultSet rs) throws SQLException {
            List<ArticlePreview> list = new ArrayList<>();

            while (rs.next()) {
                try {
                    ArticlePreview articlePreview = new ArticlePreview.Builder().id(rs.getInt("id"))
                            .authorName(rs.getString("author"))
                            .authorLogo(serverAddress + "images/userlogo/" + rs.getString("userlogo"))
                            .subject(rs.getString("subject")).collectionNum(rs.getInt("snum"))
                            .summary(rs.getString("summary"))
                            .previewImageUrl(serverAddress + "images/" + rs.getString("logo"))
                            .starNum(rs.getInt("znum")).title(rs.getString("title"))

                            .build();
                    articlePreview.setAuthorOpenId(rs.getString("OPENID"));
                    list.add(articlePreview);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return list;
        }
    }

    public void starAction(String userOpenId, int articleId) {
        try {
            int primaryId = articleOpMapper.isUserStaredArticle(userOpenId, articleId);
            if (primaryId < 1) {
                articleOpMapper.starArticle(userOpenId, articleId, DateUtil.date());
            }
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    public void unStar(String userOpenId, int articleId) {
        articleOpMapper.unStarArticle(userOpenId, articleId);
    }

    public int follow(String userOpenId, String targetUserOpenId) {
        int followed = articleOpMapper.isUserFollowed(userOpenId, targetUserOpenId);
        if (followed < 1) {
            articleOpMapper.addFollowInfo(userOpenId, targetUserOpenId, DateUtil.date());
        }
        return 1;
    }

    public int unFollow(String userOpenId, String targetUserOpenId) {
        if (!isFollowed(userOpenId, targetUserOpenId)) {
            return articleOpMapper.unFollow(userOpenId, targetUserOpenId);
        }
        return 1;
    }

    public boolean isStared(String userOpenId, int articleId) {
        int id = articleOpMapper.isUserStaredArticle(userOpenId, articleId);
        return id > 0;
    }

    public boolean isFollowed(String userOpenId, String targetUserOpenId) {
        int id = articleOpMapper.isUserFollowed(userOpenId, targetUserOpenId);
        return id > 0;
    }

    @Override
    public void afterPropsSet() {
        // jdbcTemplate = new JdbcTemplate(dataSource);
    }
}
