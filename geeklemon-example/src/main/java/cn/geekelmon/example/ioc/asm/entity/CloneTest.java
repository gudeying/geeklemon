package cn.geekelmon.example.ioc.asm.entity;

import cn.hutool.core.util.ObjectUtil;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/10/18 9:21
 * Modified by : kavingu
 */
public class CloneTest {
    public static void main(String[] args) {
        AsmUserService userService = new AsmUserService(new AsmTestUser());
        AsmUserService userService1Clone = ObjectUtil.clone(userService);
//clone ：Serializable，Cloneable,
// 深度克隆的时候  内部的 Field 也需要实现，而且大多数情况下这样业务需求都需要深度克隆
        System.out.println(userService.getAsmTestUser() == userService1Clone.getAsmTestUser());
    }
}
