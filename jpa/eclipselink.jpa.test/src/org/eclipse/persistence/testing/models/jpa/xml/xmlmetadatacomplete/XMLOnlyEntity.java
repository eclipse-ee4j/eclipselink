/*******************************************************************************
 * Copyright (c) 2014 IBM Corporation and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     08/18/2014-2.5 Jody Grassel (IBM Corporation)
 *       - 440802: xml-mapping-metadata-complete does not exclude @Entity annotated entities
 ******************************************************************************/

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
