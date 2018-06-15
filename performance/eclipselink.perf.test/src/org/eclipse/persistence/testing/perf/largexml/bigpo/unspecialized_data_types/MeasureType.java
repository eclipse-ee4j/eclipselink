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
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.DurationMeasureType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.LatitudeDegreesMeasureType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.LatitudeMinutesMeasureType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.LongitudeDegreesMeasureType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.LongitudeMinutesMeasureType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.VolumeMeasureType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.WeightMeasureType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.order.ValidityDurationMeasureType;


/**
 *
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:cct="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentTypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;DT&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Measure. Type&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:RepresentationTerm&gt;Measure&lt;/ccts:RepresentationTerm&gt;&lt;/ccts:Component&gt;
 * </pre>
 *
 *
 * <p>Java class for MeasureType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="MeasureType">
 *   &lt;simpleContent>
 *     &lt;restriction base="&lt;urn:oasis:names:specification:ubl:schema:xsd:CoreComponentTypes-1.0>MeasureType">
 *       &lt;attribute name="measureUnitCode" use="required" type="{http://www.w3.org/2001/XMLSchema}normalizedString" />
 *       &lt;attribute name="measureUnitCodeListVersionID" type="{http://www.w3.org/2001/XMLSchema}normalizedString" />
 *     &lt;/restriction>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MeasureType")
@XmlSeeAlso({
    ValidityDurationMeasureType.class,
    org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.MeasureType.class,
    WeightMeasureType.class,
    LatitudeDegreesMeasureType.class,
    DurationMeasureType.class,
    LongitudeMinutesMeasureType.class,
    LongitudeDegreesMeasureType.class,
    VolumeMeasureType.class,
    LatitudeMinutesMeasureType.class
})
public class MeasureType
    extends org.eclipse.persistence.testing.perf.largexml.bigpo.core_component_types.MeasureType
{


}
