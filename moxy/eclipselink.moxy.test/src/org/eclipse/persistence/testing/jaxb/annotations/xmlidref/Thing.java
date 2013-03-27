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
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Thing extends BaseThing {

    @XmlID
	public String m_calendarId;

    public boolean equals(Object obj){
        if(obj instanceof Thing){
            return m_calendarId.equals(((Thing)obj).m_calendarId);
        }
        return false;
    }
        
}
