/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - June 29/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb.xmlmodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for java-attribute complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="java-attribute">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="java-attribute" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "java-attribute")
@XmlSeeAlso({
    XmlJavaTypeAdapter.class,
    XmlValue.class,
    XmlAnyElement.class,
    XmlElementRefs.class,
    org.eclipse.persistence.jaxb.xmlmodel.XmlAttribute.class,
    XmlAnyAttribute.class,
    XmlElements.class,
    XmlElementRef.class,
    XmlTransient.class,
    XmlElement.class
})
public abstract class JavaAttribute {

    @javax.xml.bind.annotation.XmlAttribute(name = "java-attribute")
    protected String javaAttribute;

    /**
     * Gets the value of the javaAttribute property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJavaAttribute() {
        return javaAttribute;
    }

    /**
     * Sets the value of the javaAttribute property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJavaAttribute(String value) {
        this.javaAttribute = value;
    }

}
