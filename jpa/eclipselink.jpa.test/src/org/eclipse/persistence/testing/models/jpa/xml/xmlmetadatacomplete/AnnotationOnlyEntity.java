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

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity defined only by annotation.  Should be completely ignored in xml-mapping-metadata-complete PU's.
 *
 * @author jgrassel
 *
 */
@Entity
@Table(name="ANOONLYENT")
public class AnnotationOnlyEntity {
    @Id
    private int id;

    private String strData;

    public AnnotationOnlyEntity() {

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
        return "AnnotationOnlyEntity [id=" + id + ", strData=" + strData + "]";
    }
}
