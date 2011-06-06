/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - 2.3 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschematype;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class EmployeeWithAnnotationOnMethod {
    private int id;
    private java.util.GregorianCalendar hireDate;


    @javax.xml.bind.annotation.XmlElement
    @javax.xml.bind.annotation.XmlID
    @javax.xml.bind.annotation.XmlSchemaType(name="string")
    public int getId() {
        return id;
    }

    @javax.xml.bind.annotation.XmlElement
    @javax.xml.bind.annotation.XmlSchemaType(name="date")
    public java.util.GregorianCalendar getHireDate() {
        return hireDate;
    }
}
