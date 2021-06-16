/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
// Denise Smith - October 2013
package org.eclipse.persistence.testing.jaxb.xmlidref.self;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlRootElement;

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
