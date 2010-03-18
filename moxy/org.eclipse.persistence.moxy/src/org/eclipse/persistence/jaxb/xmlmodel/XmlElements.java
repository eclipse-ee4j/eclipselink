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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
 *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-element" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-element-wrapper" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="xml-idref" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="xml-list" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "xmlElement",
    "xmlElementWrapper"
})
public class XmlElements
    extends JavaAttribute
{

    @javax.xml.bind.annotation.XmlElement(name = "xml-element")
    protected List<org.eclipse.persistence.jaxb.xmlmodel.XmlElement> xmlElement;
    @javax.xml.bind.annotation.XmlElement(name = "xml-element-wrapper")
    protected XmlElementWrapper xmlElementWrapper;
    @XmlAttribute(name = "xml-idref")
    protected Boolean xmlIdref;
    @XmlAttribute(name = "xml-list")
    protected Boolean xmlList;

    /**
     * Gets the value of the xmlElement property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the xmlElement property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getXmlElement().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link org.eclipse.persistence.jaxb.xmlmodel.XmlElement }
     * 
     * 
     */
    public List<org.eclipse.persistence.jaxb.xmlmodel.XmlElement> getXmlElement() {
        if (xmlElement == null) {
            xmlElement = new ArrayList<org.eclipse.persistence.jaxb.xmlmodel.XmlElement>();
        }
        return this.xmlElement;
    }

    /**
     * Gets the value of the xmlElementWrapper property.
     * 
     * @return
     *     possible object is
     *     {@link XmlElementWrapper }
     *     
     */
    public XmlElementWrapper getXmlElementWrapper() {
        return xmlElementWrapper;
    }

    /**
     * Sets the value of the xmlElementWrapper property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmlElementWrapper }
     *     
     */
    public void setXmlElementWrapper(XmlElementWrapper value) {
        this.xmlElementWrapper = value;
    }

    /**
     * Gets the value of the xmlIdref property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isXmlIdref() {
        if (xmlIdref == null) {
            return false;
        } else {
            return xmlIdref;
        }
    }

    /**
     * Sets the value of the xmlIdref property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setXmlIdref(Boolean value) {
        this.xmlIdref = value;
    }

    /**
     * Gets the value of the xmlList property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isXmlList() {
        if (xmlList == null) {
            return false;
        } else {
            return xmlList;
        }
    }

    /**
     * Sets the value of the xmlList property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setXmlList(Boolean value) {
        this.xmlList = value;
    }

}
