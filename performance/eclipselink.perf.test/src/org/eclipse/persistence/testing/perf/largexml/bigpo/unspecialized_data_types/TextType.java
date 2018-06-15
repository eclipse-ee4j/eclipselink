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
package org.eclipse.persistence.testing.perf.largexml.bigpo.unspecialized_data_types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.BuildingNumberType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.ConditionType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.CountrySubentityType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.DepartmentType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.DescriptionType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.DistrictType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.ExtensionType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.FloorType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.InformationType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.InstructionsType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.LineType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.LocationType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.LossRiskType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.MailType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.NoteType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.PlacardEndorsementType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.PlacardNotationType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.PostboxType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.ReasonType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.RegionType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.RoomType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.TelefaxType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.TelephoneType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.TermsType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.TimezoneOffsetType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.ValueType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.ZoneType;


/**
 *
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:cct="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentTypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;DT&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Text. Type&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:RepresentationTerm&gt;Text&lt;/ccts:RepresentationTerm&gt;&lt;/ccts:Component&gt;
 * </pre>
 *
 *
 * <p>Java class for TextType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="TextType">
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
@XmlType(name = "TextType")
@XmlSeeAlso({
    ConditionType.class,
    InstructionsType.class,
    RoomType.class,
    InformationType.class,
    ZoneType.class,
    CountrySubentityType.class,
    DistrictType.class,
    MailType.class,
    TelephoneType.class,
    NoteType.class,
    LocationType.class,
    LineType.class,
    TermsType.class,
    TimezoneOffsetType.class,
    ExtensionType.class,
    TelefaxType.class,
    ValueType.class,
    ReasonType.class,
    FloorType.class,
    LossRiskType.class,
    RegionType.class,
    PlacardEndorsementType.class,
    DepartmentType.class,
    PlacardNotationType.class,
    DescriptionType.class,
    PostboxType.class,
    BuildingNumberType.class
})
public class TextType
    extends org.eclipse.persistence.testing.perf.largexml.bigpo.core_component_types.TextType
{


}
