/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Martin Vojtek - 2.6.0 - initial implementation
package org.eclipse.persistence.testing.perf.largexml.bigpo.core_component_parameters;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ContextType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ContextType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0}IndustryClassification" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0}Geopolitical" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0}BusinessProcess" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0}OfficialConstraint" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0}ProductClassification" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0}BusinessProcessRole" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0}SupportingRole" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0}SystemCapability" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContextType", propOrder = {
    "industryClassification",
    "geopolitical",
    "businessProcess",
    "officialConstraint",
    "productClassification",
    "businessProcessRole",
    "supportingRole",
    "systemCapability"
})
public class ContextType {

    @XmlElement(name = "IndustryClassification")
    protected List<String> industryClassification;
    @XmlElement(name = "Geopolitical")
    protected List<String> geopolitical;
    @XmlElement(name = "BusinessProcess")
    protected List<String> businessProcess;
    @XmlElement(name = "OfficialConstraint")
    protected List<String> officialConstraint;
    @XmlElement(name = "ProductClassification")
    protected List<String> productClassification;
    @XmlElement(name = "BusinessProcessRole")
    protected List<String> businessProcessRole;
    @XmlElement(name = "SupportingRole")
    protected List<String> supportingRole;
    @XmlElement(name = "SystemCapability")
    protected List<String> systemCapability;

    /**
     * Gets the value of the industryClassification property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the industryClassification property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIndustryClassification().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getIndustryClassification() {
        if (industryClassification == null) {
            industryClassification = new ArrayList<String>();
        }
        return this.industryClassification;
    }

    /**
     * Gets the value of the geopolitical property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the geopolitical property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGeopolitical().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getGeopolitical() {
        if (geopolitical == null) {
            geopolitical = new ArrayList<String>();
        }
        return this.geopolitical;
    }

    /**
     * Gets the value of the businessProcess property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the businessProcess property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBusinessProcess().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getBusinessProcess() {
        if (businessProcess == null) {
            businessProcess = new ArrayList<String>();
        }
        return this.businessProcess;
    }

    /**
     * Gets the value of the officialConstraint property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the officialConstraint property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOfficialConstraint().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getOfficialConstraint() {
        if (officialConstraint == null) {
            officialConstraint = new ArrayList<String>();
        }
        return this.officialConstraint;
    }

    /**
     * Gets the value of the productClassification property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the productClassification property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProductClassification().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getProductClassification() {
        if (productClassification == null) {
            productClassification = new ArrayList<String>();
        }
        return this.productClassification;
    }

    /**
     * Gets the value of the businessProcessRole property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the businessProcessRole property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBusinessProcessRole().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getBusinessProcessRole() {
        if (businessProcessRole == null) {
            businessProcessRole = new ArrayList<String>();
        }
        return this.businessProcessRole;
    }

    /**
     * Gets the value of the supportingRole property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the supportingRole property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSupportingRole().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getSupportingRole() {
        if (supportingRole == null) {
            supportingRole = new ArrayList<String>();
        }
        return this.supportingRole;
    }

    /**
     * Gets the value of the systemCapability property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the systemCapability property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSystemCapability().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getSystemCapability() {
        if (systemCapability == null) {
            systemCapability = new ArrayList<String>();
        }
        return this.systemCapability;
    }

}
