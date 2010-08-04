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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}java-attribute">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-abstract-null-policy" minOccurs="0"/>
 *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-access-methods" minOccurs="0"/>
 *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-properties" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="read-only" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="write-only" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="cdata" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "xmlAbstractNullPolicy",
    "xmlAccessMethods",
    "xmlProperties"
})
public class XmlValue
    extends JavaAttribute
{

    @XmlElementRef(name = "xml-abstract-null-policy", namespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", type = JAXBElement.class)
    protected JAXBElement<? extends XmlAbstractNullPolicy> xmlAbstractNullPolicy;
    @XmlElement(name = "xml-access-methods")
    protected XmlAccessMethods xmlAccessMethods;
    @XmlElement(name = "xml-properties")
    protected XmlProperties xmlProperties;
    @XmlAttribute(name = "read-only")
    protected Boolean readOnly;
    @XmlAttribute(name = "write-only")
    protected Boolean writeOnly;
    @XmlAttribute(name = "cdata")
    protected Boolean cdata;

    /**
     * Gets the value of the xmlAbstractNullPolicy property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XmlAbstractNullPolicy }{@code >}
     *     {@link JAXBElement }{@code <}{@link XmlIsSetNullPolicy }{@code >}
     *     {@link JAXBElement }{@code <}{@link XmlNullPolicy }{@code >}
     *     
     */
    public JAXBElement<? extends XmlAbstractNullPolicy> getXmlAbstractNullPolicy() {
        return xmlAbstractNullPolicy;
    }

    /**
     * Sets the value of the xmlAbstractNullPolicy property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XmlAbstractNullPolicy }{@code >}
     *     {@link JAXBElement }{@code <}{@link XmlIsSetNullPolicy }{@code >}
     *     {@link JAXBElement }{@code <}{@link XmlNullPolicy }{@code >}
     *     
     */
    public void setXmlAbstractNullPolicy(JAXBElement<? extends XmlAbstractNullPolicy> value) {
        this.xmlAbstractNullPolicy = ((JAXBElement<? extends XmlAbstractNullPolicy> ) value);
    }

    /**
     * Gets the value of the xmlAccessMethods property.
     * 
     * @return
     *     possible object is
     *     {@link XmlAccessMethods }
     *     
     */
    public XmlAccessMethods getXmlAccessMethods() {
        return xmlAccessMethods;
    }

    /**
     * Sets the value of the xmlAccessMethods property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmlAccessMethods }
     *     
     */
    public void setXmlAccessMethods(XmlAccessMethods value) {
        this.xmlAccessMethods = value;
    }

    /**
     * Gets the value of the xmlProperties property.
     * 
     * @return
     *     possible object is
     *     {@link XmlProperties }
     *     
     */
    public XmlProperties getXmlProperties() {
        return xmlProperties;
    }

    /**
     * Sets the value of the xmlProperties property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmlProperties }
     *     
     */
    public void setXmlProperties(XmlProperties value) {
        this.xmlProperties = value;
    }

    /**
     * Gets the value of the readOnly property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isReadOnly() {
        if (readOnly == null) {
            return false;
        } else {
            return readOnly;
        }
    }

    /**
     * Sets the value of the readOnly property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReadOnly(Boolean value) {
        this.readOnly = value;
    }

    /**
     * Indicates if the isReadOnly flag was set.
     * 
     * @return
     */
    public boolean isSetReadOnly() {
        return this.readOnly != null;
    }

    /**
     * Gets the value of the writeOnly property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isWriteOnly() {
        if (writeOnly == null) {
            return false;
        } else {
            return writeOnly;
        }
    }

    /**
     * Sets the value of the writeOnly property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setWriteOnly(Boolean value) {
        this.writeOnly = value;
    }

    /**
     * Indicates if the isWriteOnly flag was set.
     * 
     * @return
     */
    public boolean isSetWriteOnly() {
        return this.writeOnly != null;
    }

    /**
     * Gets the value of the cdata property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isCdata() {
        if (cdata == null) {
            return false;
        } else {
            return cdata;
        }
    }

    /**
     * Sets the value of the cdata property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCdata(Boolean value) {
        this.cdata = value;
    }
    /**
     * Indicates if the cdata field has been set, i.e. is not null.
     * 
     * @return true if this.cdata is not null, false otherwise
     */
    public boolean isSetCdata() {
        return this.cdata != null;
    }
}
