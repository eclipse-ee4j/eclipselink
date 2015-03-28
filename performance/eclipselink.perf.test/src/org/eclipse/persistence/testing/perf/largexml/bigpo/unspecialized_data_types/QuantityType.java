/*******************************************************************************
 * Copyright (c) 2014, 2015  Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Martin Vojtek - 2.6.0 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.perf.largexml.bigpo.unspecialized_data_types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.BackorderQuantityType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.PackQuantityType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.order.PackagesQuantityType;


/**
 *
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:cct="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentTypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;DT&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Quantity. Type&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:RepresentationTerm&gt;Quantity&lt;/ccts:RepresentationTerm&gt;&lt;/ccts:Component&gt;
 * </pre>
 *
 *
 * <p>Java class for QuantityType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="QuantityType">
 *   &lt;simpleContent>
 *     &lt;restriction base="&lt;urn:oasis:names:specification:ubl:schema:xsd:CoreComponentTypes-1.0>QuantityType">
 *       &lt;attribute name="quantityUnitCode" use="required" type="{http://www.w3.org/2001/XMLSchema}normalizedString" />
 *       &lt;attribute name="quantityUnitCodeListID" type="{http://www.w3.org/2001/XMLSchema}normalizedString" />
 *       &lt;attribute name="quantityUnitCodeListAgencyID" type="{http://www.w3.org/2001/XMLSchema}normalizedString" />
 *       &lt;attribute name="quantityUnitCodeListAgencyName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuantityType")
@XmlSeeAlso({
    PackagesQuantityType.class,
    org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.QuantityType.class,
    BackorderQuantityType.class,
    PackQuantityType.class
})
public class QuantityType
    extends org.eclipse.persistence.testing.perf.largexml.bigpo.core_component_types.QuantityType
{


}
