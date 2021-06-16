/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     tware - initial implementation
package org.eclipse.persistence.internal.jpa.rs.metadata.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder = { "queryName", "returnTypes", "linkTemplate", "jpql" })
public class Query {
    protected String queryName;
    protected String jpql;
    protected LinkTemplate linkTemplate;
    protected List<String> returnTypes = new ArrayList<String>();

    public Query() {
    }

    public Query(String queryName, String jpql, LinkTemplate linkTemplate) {
        this.queryName = queryName;
        this.jpql = jpql;
        this.linkTemplate = linkTemplate;
    }

    public String getQueryName() {
        return queryName;
    }

    public void setQueryName(String queryName) {
        this.queryName = queryName;
    }

    public String getJpql() {
        return jpql;
    }

    public void setJpql(String jpql) {
        this.jpql = jpql;
    }

    public LinkTemplate getLinkTemplate() {
        return linkTemplate;
    }

    public void setLinkTemplate(LinkTemplate linkTemplate) {
        this.linkTemplate = linkTemplate;
    }

    public List<String> getReturnTypes() {
        return returnTypes;
    }

    public void setReturnTypes(List<String> returnTypes) {
        this.returnTypes = returnTypes;
    }
}
