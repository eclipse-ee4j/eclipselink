/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlidref;

import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "owner")
public class OwnerIntegerId {

    @XmlIDREF
    public ThingIntegerId m_calendar;

    public String m_activityId;

    public boolean equals(Object obj){
        if(obj instanceof OwnerIntegerId ownerObject){
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
