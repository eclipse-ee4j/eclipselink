/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      gonural - initial 
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs.util.list;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.internal.jpa.rs.metadata.model.Query;
import org.eclipse.persistence.jpa.rs.config.ConfigDefaults;

/**
 * This class is used to wrap collection of Query objects
 * @see Query
 * 
 * @author gonural
 *
 */
@XmlRootElement(name = ConfigDefaults.JPARS_LIST_GROUPING_NAME)
public class QueryList {
    private List<Query> list;

    public QueryList() {
    }

    @XmlElement(name = ConfigDefaults.JPARS_LIST_ITEM_NAME)
    public List<Query> getList() {
        return list;
    }

    public void setList(List<Query> list) {
        this.list = list;
    }
}
