package cn.geekelmon.app.api.service;

import cn.geekelmon.app.api.entity.UserInfo;
import cn.geekelmon.cache.annotation.LCache;
import cn.geeklemon.core.context.annotation.Autowired;
import cn.geeklemon.core.context.annotation.Bean;
import cn.geeklemon.core.context.annotation.Value;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.handler.RsHandler;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/11/8 15:58
 * Modified by : kavingu
 */
@Bean(single = true)
public class UserService {
    @Value(name = "app.serverAddress", defaultValue = "http://127.0.0.1:8080/")
    private String serverAddress;

    @Autowired
    private DataSource dataSource;


    private UserInfoRsHandler passwordUserHandler = new UserInfoRsHandler(true);
    private UserInfoRsHandler noPasswordUserInfoRsHandler = new UserInfoRsHandler(false);

    @LCache(log = false)
    public UserInfo user(String openId) {
        return user(openId, false);
    }

    public UserInfo getUserByName(String name) {
        if (StrUtil.isBlank(name)) {
            return null;
        }
        try {
            String sql = "select u.id,u.name,u.pasw,u.age,u.sex,u.userlogo,u.openid,u.mail,u.level,u.autograph,u.nickname " +
                    "from user u where u.name = ?";
            return Db.use(dataSource).query(sql, passwordUserHandler, name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public UserInfo user(String openId, boolean keepPassword) {
        if (StrUtil.isBlank(openId)) {
            return null;
        }
        try {
            String sql = "select u.id,u.name,u.pasw,u.age,u.sex,u.userlogo,u.openid,u.mail,u.level,u.autograph,u.nickname " +
                    "from user u where u.openid = ?";
            UserInfo query = Db.use(dataSource).query(sql, noPasswordUserInfoRsHandler, openId);
            if (!keepPassword && query != null) {
                query.setPassword("");
            }
            return query;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private class UserInfoRsHandler implements RsHandler<UserInfo> {
        private boolean keepPassword;

        private UserInfoRsHandler(boolean keepPassword) {
            this.keepPassword = keepPassword;
        }

        @Override
        public UserInfo handle(ResultSet rs) throws SQLException {
            try {
                if (rs.next()) {
                    UserInfo userInfo = new UserInfo.Builder()
                            .openId(rs.getString("openid"))
                            .age(rs.getInt("age"))
                            .level(rs.getInt("level"))
                            .mail(rs.getString("mail"))
                            .name(rs.getString("name"))
                            .nickName(rs.getString("nickname"))
                            .signature(rs.getString("autograph"))
                            .userLogo(serverAddress + "images/userlogo/" + rs.getString("userlogo"))
                            .sex(rs.getString("sex"))
                            .build();
                    if (keepPassword) {
                        userInfo.setPassword(rs.getString("pasw"));
                    }
                    return userInfo;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
