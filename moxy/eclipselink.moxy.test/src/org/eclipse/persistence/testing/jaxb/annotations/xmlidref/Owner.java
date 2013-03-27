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
 *     Denise Smith - 2.4 - October 2012
 ******************************************************************************/

package org.eclipse.persistence.testing.jaxb.annotations.xmlidref;

import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Owner {

    @XmlIDREF
    public Thing m_calendar;
    
    public String m_activityId;
    
    public boolean equals(Object obj){
        if(obj instanceof Owner){
            Owner ownerObject = (Owner)obj;
            if(!this.m_activityId.equals(ownerObject.m_activityId)){
                return false;
            }
            if(m_calendar == null){
                return ownerObject.m_calendar ==null;                
            }else{
                return m_calendar.equals(ownerObject.m_calendar);
            }
        }
        return false;
    }

}
