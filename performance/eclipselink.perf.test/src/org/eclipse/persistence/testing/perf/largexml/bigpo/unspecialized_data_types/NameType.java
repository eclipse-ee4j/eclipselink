/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Martin Vojtek - 2.6.0 - initial implementation
package org.eclipse.persistence.testing.perf.largexml.bigpo.unspecialized_data_types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.BuildingNameType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.CityNameType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.HolderNameType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.RegistrationNameType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.StreetNameType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.core_component_types.TextType;


/**
 *
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:cct="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentTypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;DT&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Name. Type&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:RepresentationTerm&gt;Name&lt;/ccts:RepresentationTerm&gt;&lt;ccts:DataType&gt;Text. Type&lt;/ccts:DataType&gt;&lt;/ccts:Component&gt;
 * </pre>
 *
 *
 * <p>Java class for NameType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="NameType">
 *   &lt;simpleContent>
 *     &lt;restriction base="&lt;urn:oasis:names:specification:ubl:schema:xsd:CoreComponentTypes-1.0>TextType">
 *       &lt;attribute name="languageID" type="{http://www.w3.org/2001/XMLSchema}language" />
 *     &lt;/restriction>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NameType")
@XmlSeeAlso({
    BuildingNameType.class,
    RegistrationNameType.class,
    StreetNameType.class,
    CityNameType.class,
    org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.NameType.class,
    HolderNameType.class
})
public class NameType
    extends TextType
{


}
