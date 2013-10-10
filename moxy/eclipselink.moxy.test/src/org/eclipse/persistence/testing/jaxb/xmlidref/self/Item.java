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
 * Denise Smith - October 2013
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlidref.self;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Item")
public class Item
{
    @XmlAttribute(name="name")
    @XmlID
    public String m_name;

    @XmlAttribute(name="parent")
    @XmlIDREF 
    public Item m_parent;
    
    public boolean equals(Object obj){    
         if(obj instanceof Item){
             Item item = (Item)obj;
             if(!m_name.equals(item.m_name)){
                 return false;
             }
             if(m_parent == null){
                 if(item.m_parent != null){
                     return false;
                 }
             }else{
                 if(item.m_parent == null){
                     return false;
                 }else{
                     return m_parent.m_name.equals(item.m_parent.m_name);
                 }
             }
             if((m_parent == null && item.m_parent != null) || (m_parent != null && item.m_parent == null)){
                 return false;
             }
             return true;
         }
         return false;
    }
    
}
