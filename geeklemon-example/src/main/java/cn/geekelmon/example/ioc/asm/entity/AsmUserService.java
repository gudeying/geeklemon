package cn.geekelmon.example.ioc.asm.entity;

import java.io.Serializable;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/10/18 9:21
 * Modified by : kavingu
 */
public class AsmUserService implements Serializable {
    /**
     * 如果AsmTestUser不能clone，克隆也会失败
     */
    private AsmTestUser asmTestUser;

    public AsmUserService(AsmTestUser asmTestUser) {
        this.asmTestUser = asmTestUser;
    }

    public AsmTestUser getAsmTestUser() {
        return asmTestUser;
    }

    public void setAsmTestUser(AsmTestUser asmTestUser) {
        this.asmTestUser = asmTestUser;
    }
}
