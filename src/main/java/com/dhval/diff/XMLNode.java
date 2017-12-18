package com.dhval.diff;

import java.util.LinkedList;
import java.util.List;

public class XMLNode {

    public String name;
    public String value;
    public String path = "";

    List<XMLNode> children = new LinkedList<>();;

    public List<XMLNode> getChildren() {
        return children;
    }

}
