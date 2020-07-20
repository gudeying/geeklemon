package cn.geekelmon.data.context;

/**
 */
public class MapperDefine {
    private String beanName;
    private Class<?> srcClass;
    private Object result;

    public MapperDefine(String beanName, Class<?> srcClass, Object result) {
        this.beanName = beanName;
        this.srcClass = srcClass;
        this.result = result;
    }

    public Class<?> getSrcClass() {
        return srcClass;
    }

    public MapperDefine setSrcClass(Class<?> srcClass) {
        this.srcClass = srcClass;
        return this;
    }

    public String getBeanName() {
        return beanName;
    }

    public MapperDefine setBeanName(String beanName) {
        this.beanName = beanName;
        return this;
    }

    public Object getResult() {
        return result;
    }

    public MapperDefine setResult(Object result) {
        this.result = result;
        return this;
    }
}
