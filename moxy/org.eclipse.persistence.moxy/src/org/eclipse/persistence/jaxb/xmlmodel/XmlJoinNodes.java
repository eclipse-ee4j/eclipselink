/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - August 30/2010 - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb.xmlmodel;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
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
 *         &lt;element name="xml-join-node" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="xml-path" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="referenced-xml-path" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="container-type" type="{http://www.w3.org/2001/XMLSchema}string" default="##default" />
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" default="##default" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "xmlJoinNode"
})
public class XmlJoinNodes
    extends JavaAttribute
{

    @XmlElement(name = "xml-join-node", required = true)
    protected List<XmlJoinNodes.XmlJoinNode> xmlJoinNode;
    @XmlAttribute(name = "container-type")
    protected String containerType;
    @XmlAttribute(name = "type")
    protected String type;

    /**
     * Gets the value of the xmlJoinNode property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the xmlJoinNode property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getXmlJoinNode().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XmlJoinNodes.XmlJoinNode }
     * 
     * 
     */
    public List<XmlJoinNodes.XmlJoinNode> getXmlJoinNode() {
        if (xmlJoinNode == null) {
            xmlJoinNode = new ArrayList<XmlJoinNodes.XmlJoinNode>();
        }
        return this.xmlJoinNode;
    }

    /**
     * Sets the value of the xmlJoinNode property.
     * 
     * @param List of XmlJoinNode instances to be set
     */
    public void setXmlJoinNode(List<XmlJoinNodes.XmlJoinNode> xmlJoinNode) {
        this.xmlJoinNode = xmlJoinNode;
    }

    /**
     * Gets the value of the containerType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContainerType() {
        if (containerType == null) {
            return "##default";
        } else {
            return containerType;
        }
    }

    /**
     * Sets the value of the containerType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContainerType(String value) {
        this.containerType = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        if (type == null) {
            return "##default";
        } else {
            return type;
        }
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="xml-path" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="referenced-xml-path" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class XmlJoinNode {

        @XmlAttribute(name = "xml-path", required = true)
        protected String xmlPath;
        @XmlAttribute(name = "referenced-xml-path", required = true)
        protected String referencedXmlPath;

        /**
         * Gets the value of the xmlPath property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getXmlPath() {
            return xmlPath;
        }

        /**
         * Sets the value of the xmlPath property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setXmlPath(String value) {
            this.xmlPath = value;
        }

        /**
         * Gets the value of the referencedXmlPath property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getReferencedXmlPath() {
            return referencedXmlPath;
        }

        /**
         * Sets the value of the referencedXmlPath property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setReferencedXmlPath(String value) {
            this.referencedXmlPath = value;
        }
    }
}
