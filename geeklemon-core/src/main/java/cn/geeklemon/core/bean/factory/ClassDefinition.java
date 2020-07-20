package cn.geeklemon.core.bean.factory;

public class ClassDefinition {
    private String beanName;
    private Class<?> target;
    private boolean single;
    private boolean initOnlyOnUse;

    public ClassDefinition(String beanName, Class<?> target, boolean single, boolean initOnlyOnUse) {
        this.beanName = beanName;
        this.target = target;
        this.single = single;
        this.initOnlyOnUse = initOnlyOnUse;
    }

    public boolean isSingle() {
        return single;
    }

    public void setSingle(boolean single) {
        this.single = single;
    }

    public boolean isInitOnlyOnUse() {
        return initOnlyOnUse;
    }

    public void setInitOnlyOnUse(boolean initOnlyOnUse) {
        this.initOnlyOnUse = initOnlyOnUse;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public Class<?> getTarget() {
        return target;
    }

    public void setTarget(Class<?> target) {
        this.target = target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassDefinition that = (ClassDefinition) o;

        if (beanName != null ? !beanName.equals(that.beanName) : that.beanName != null) return false;
        return target != null ? target.equals(that.target) : that.target == null;
    }

    @Override
    public int hashCode() {
        int result = beanName != null ? beanName.hashCode() : 0;
        result = 31 * result + (target != null ? target.hashCode() : 0);
        return result;
    }
}
