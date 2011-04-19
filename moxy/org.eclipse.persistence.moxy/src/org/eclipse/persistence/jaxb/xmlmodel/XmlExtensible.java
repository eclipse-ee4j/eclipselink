/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * rbarkhouse - 2011 April 12 - 2.3 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb.xmlmodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@javax.xml.bind.annotation.XmlRootElement(name = "xml-extensible")
public class XmlExtensible {

    @XmlAttribute(name="get-method")
    protected String getMethod;

    @XmlAttribute(name="set-method")
    protected String setMethod;

    public String getGetMethod() {
        return getMethod;
    }

    public void setGetMethod(String getMethodName) {
        this.getMethod = getMethodName;
    }

    public String getSetMethod() {
        return setMethod;
    }

    public void setSetMethod(String setMethodName) {
        this.setMethod = setMethodName;
    }

}