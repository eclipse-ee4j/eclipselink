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
// Denise Smith - October 2013
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
