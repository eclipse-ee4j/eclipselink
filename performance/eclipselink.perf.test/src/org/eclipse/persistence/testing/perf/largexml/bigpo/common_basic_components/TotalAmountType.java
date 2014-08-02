/*******************************************************************************
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.specialized_data_types.UBLAmountType;


/**
 * <p>Java class for TotalAmountType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="TotalAmountType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0>UBLAmountType">
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TotalAmountType")
public class TotalAmountType
    extends UBLAmountType
{


}
