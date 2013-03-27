/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Matt MacIvor - 2.4.1 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlelement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="employee-data")
@XmlAccessorType(XmlAccessType.FIELD)
public class EmployeeWithConstants {
    
    @XmlElement(name = "constant")
    private static final String ANNOTATED_CONSTANT= "VALUE";

    private static final String UN_ANNOTATED_CONSTANT = "ANOTHER VALUE";
    
    public String id;
    
    public boolean equals(Object obj) {
        return this.id.equals(((EmployeeWithConstants)obj).id);
    }
}
