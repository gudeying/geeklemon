package cn.geeklemon.core.context.support;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/9/9 16:29
 * Modified by : kavingu
 */
public class ContextVariables {
    private String [] scanPackage;
    private String [] resourcePath;
    private String appName;
    private String printBanner;

    public String[] getScanPackage() {
        return scanPackage;
    }

    public void setScanPackage(String[] scanPackage) {
        this.scanPackage = scanPackage;
    }

    public String[] getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String[] resourcePath) {
        this.resourcePath = resourcePath;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPrintBanner() {
        return printBanner;
    }

    public void setPrintBanner(String printBanner) {
        this.printBanner = printBanner;
    }
}
