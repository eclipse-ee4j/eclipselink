/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     bdoughan - Mar 27/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.sdo.helper.jaxbhelper.changesummary;

import java.util.List;

public class Root {

    private String id;
    private String name;
    private List<String> simpleList;
    private Child1 child1;
    private List<Child2> child2;

    public Root() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getSimpleList() {
        return simpleList;
    }

    public void setSimpleList(List<String> simpleList) {
        this.simpleList = simpleList;
    }

    public Child1 getChild1() {
        return child1;
    }

    public void setChild1(Child1 child1) {
        this.child1 = child1;
    }

    public List<Child2> getChild2() {
        return child2;
    }

    public void setChild2(List<Child2> child2) {
        this.child2 = child2;
    }

}
