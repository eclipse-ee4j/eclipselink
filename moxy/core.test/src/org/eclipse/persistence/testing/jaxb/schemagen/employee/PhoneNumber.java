/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.schemagen.employee;

import javax.xml.bind.annotation.*;

@XmlType(name = "phone-number")
@XmlAccessorType(XmlAccessType.FIELD)
public class PhoneNumber {
    
    @XmlValue
    public String number;
    
    //@XmlAnyAttribute
    //public java.util.Map dialingInfo;
}