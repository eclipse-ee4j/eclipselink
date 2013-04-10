/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.xmlvalue;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

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
       
}
