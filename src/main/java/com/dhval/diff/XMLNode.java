package com.dhval.diff;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class XMLNode {

    public String name;
    public String value;
    public String path = "";

    List<XMLNode> children = new LinkedList<>();

    public List<XMLNode> getChildren() {
        return children;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XMLNode xmlNode = (XMLNode) o;
        return Objects.equals(name, xmlNode.name) &&
                Objects.equals(path, xmlNode.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, path);
    }
}
