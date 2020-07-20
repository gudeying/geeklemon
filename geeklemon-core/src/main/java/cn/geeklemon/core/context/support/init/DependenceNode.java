package cn.geeklemon.core.context.support.init;

import cn.geeklemon.core.bean.factory.ClassDefinition;

import java.util.HashSet;
import java.util.Set;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/9/12 17:02
 * Modified by : kavingu
 */
public class DependenceNode {
    private ClassDefinition node;
    private Set<ClassDefinition> children;

    public DependenceNode(ClassDefinition classDefinition) {
        node = classDefinition;
        children = new HashSet<>();
    }

    @Override
    public int hashCode() {
        return children.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        DependenceNode node = (DependenceNode) obj;
        return node.getNode().equals(node);
    }

    public void addChild(ClassDefinition classDefinition) {
        this.children.add(classDefinition);
    }

    public ClassDefinition getNode() {
        return node;
    }

    public void setNode(ClassDefinition node) {
        this.node = node;
    }

    public Set<ClassDefinition> getChildren() {
        return children;
    }

    public void setChildren(Set<ClassDefinition> children) {
        this.children = children;
    }
}
