package cn.geekelmon.app.api.service;

import cn.geekelmon.app.api.entity.VersionInfo;
import cn.geeklemon.core.bean.factory.InitializingBean;
import cn.geeklemon.core.context.annotation.Autowired;
import cn.geeklemon.core.context.annotation.Bean;
import cn.geeklemon.core.context.annotation.Value;
import cn.hutool.db.Db;
import cn.hutool.db.handler.RsHandler;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;


@Bean
public class VersionService implements InitializingBean {
    private static final Log LOG = LogFactory.get();
    @Autowired
    private DataSource dataSource;
    @Value(name = "app.serverAddress", defaultValue = "http://127.0.0.1:8080/")
    private String serverAddress;

    private Db queryService;


    public VersionInfo versionInfo(int versionCode) {
        String sql = "select PRIMARY_ID,V_CODE,V_NAME,V_CONTENT,IGNORABLE,LOAD_URL,MD5,M_SIZE " +
                " from app_version where v_code = ? ORDER BY V_CODE DESC LIMIT 0,1";
        try {
            return queryService.query(sql, new VersionHandler(true), versionCode);
        } catch (SQLException e) {
            LOG.error(e);
        }
        return null;
    }

    public VersionInfo lastVersion() {
        String sql = "select PRIMARY_ID,V_CODE,V_NAME,V_CONTENT,IGNORABLE,LOAD_URL,MD5,M_SIZE " +
                " from app_version ORDER BY V_CODE DESC LIMIT 0,1";
        try {
            VersionInfo versionInfo = queryService.query(sql, new VersionHandler(true));
            return versionInfo;
        } catch (SQLException e) {
            e.printStackTrace();
            LOG.error(e);
        }
        return null;
    }


    public List<VersionInfo> versionList() {
        String sql = "select PRIMARY_ID,V_CODE,V_NAME,V_CONTENT,IGNORABLE,LOAD_URL,MD5,M_SIZE " +
                " from app_version ORDER BY V_CODE DESC ";

        try {
            queryService.query(sql, new RsHandler<List<VersionInfo>>() {
                @Override
                public List<VersionInfo> handle(ResultSet rs) throws SQLException {
                    VersionHandler handler = new VersionHandler(false);
                    List<VersionInfo> lists = new LinkedList<>();
                    while (rs.next()) {
                        lists.add(handler.handle(rs));
                    }
                    return lists;
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new LinkedList<>();

    }


    @Override
    public void afterPropsSet() {
        queryService = Db.use(dataSource);
    }

    private class VersionHandler implements RsHandler<VersionInfo> {

        private boolean next;

        private VersionHandler(boolean next) {
            this.next = next;
        }

        @Override
        public VersionInfo handle(ResultSet rs) throws SQLException {
            boolean bo = true;
            if (next) {
                //list的时候不用next
                bo = rs.next();
            }
            if (bo) {
                int id = rs.getInt("PRIMARY_ID");
                int code = rs.getInt("PRIMARY_ID");
                String name = rs.getString("V_NAME");
                String content = rs.getString("V_CONTENT");
                int ignorable = rs.getInt("IGNORABLE");
                String url = rs.getString("LOAD_URL");
                String md5 = rs.getString("MD5");
                int size = rs.getInt("M_SIZE");
                VersionInfo info = new VersionInfo();
                info.setId(id)
                        .setVersionCode(code)
                        .setVersionName(name)
                        .setContent(content)
                        .setIgnorable(ignorable > 0)
                        .setLoadUrl(serverAddress + url)
                        .setMd5(md5)
                        .setSize(size);
                return info;
            }
            return null;
        }
    }
}
