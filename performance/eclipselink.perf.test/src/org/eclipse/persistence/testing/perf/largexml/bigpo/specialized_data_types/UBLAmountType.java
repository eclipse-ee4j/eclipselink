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
package org.eclipse.persistence.testing.perf.largexml.bigpo.specialized_data_types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.ExtensionAmountType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.ExtensionTotalAmountType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.PriceAmountType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.TaxAmountType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.TaxTotalAmountType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.TotalAmountType;


/**
 *
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;DT&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;UBL_ Amount. Type&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;A number of monetary units specified in a currency where the unit of currency is explicitly defined using the UBL Currency Code&lt;/ccts:Definition&gt;&lt;ccts:RepresentationTerm&gt;Amount&lt;/ccts:RepresentationTerm&gt;&lt;/ccts:Component&gt;
 * </pre>
 *
 *
 * <p>Java class for UBLAmountType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="UBLAmountType">
 *   &lt;simpleContent>
 *     &lt;restriction base="&lt;urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0>AmountType">
 *       &lt;attribute name="amountCurrencyID" use="required" type="{urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0}CurrencyCodeContentType" />
 *       &lt;attribute name="amountCurrencyCodeListVersionID" type="{http://www.w3.org/2001/XMLSchema}normalizedString" fixed="0.3" />
 *     &lt;/restriction>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UBLAmountType")
@XmlSeeAlso({
    ExtensionTotalAmountType.class,
    TaxAmountType.class,
    org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.AmountType.class,
    TotalAmountType.class,
    TaxTotalAmountType.class,
    ExtensionAmountType.class,
    PriceAmountType.class
})
public class UBLAmountType
    extends org.eclipse.persistence.testing.perf.largexml.bigpo.unspecialized_data_types.AmountType
{


}
