/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2014, 2015 IBM Corporation and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     08/18/2014-2.5 Jody Grassel (IBM Corporation)
//       - 440802: xml-mapping-metadata-complete does not exclude @Entity annotated entities

package org.eclipse.persistence.testing.models.jpa.xml.xmlmetadatacomplete;

import javax.persistence.Id;

/**
 * Entity defined only by XML mapping.
 *
 * @author jgrassel
 *
 */
public class XMLOnlyEntity {
    private int id;
    private String strData;

    public XMLOnlyEntity() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStrData() {
        return strData;
    }

    public void setStrData(String strData) {
        this.strData = strData;
    }

    @Override
    public String toString() {
        return "XMLOnlyEntity [id=" + id + ", strData=" + strData + "]";
    }
}
