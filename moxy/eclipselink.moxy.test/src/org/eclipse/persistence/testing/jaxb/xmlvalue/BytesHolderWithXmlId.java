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
//     Denise Smith - initial API and implementation
package org.eclipse.persistence.testing.jaxb.xmlvalue;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlValue;
import java.util.Arrays;

public class BytesHolderWithXmlId {

    @XmlValue
    public byte[] theBytes;

    @XmlAttribute(name = "Id")
    @XmlID
    @XmlSchemaType(name = "ID")
    public String id;

    public boolean equals(Object o) {
        if(!(o instanceof BytesHolderWithXmlId) || o == null) {
            return false;
        } else {
            if (theBytes == null){
                if(((BytesHolderWithXmlId)o).theBytes != null){
                    return false;
                }
            }else {
                if(((BytesHolderWithXmlId)o).theBytes == null){
                    return false;
                }
            }
            if (theBytes.length != ((BytesHolderWithXmlId)o).theBytes.length){
                return false;
            }
            for (int i=0; i<theBytes.length; i++) {
                if (theBytes[i] != ((BytesHolderWithXmlId)o).theBytes[i]) {
                    return false;
                }
            }
            if(id == null){
                if( ((BytesHolderWithXmlId)o).id != null){
                    return false;
                }
            }else if(!id.equals(((BytesHolderWithXmlId)o).id)){
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = theBytes != null ? Arrays.hashCode(theBytes) : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }
}
