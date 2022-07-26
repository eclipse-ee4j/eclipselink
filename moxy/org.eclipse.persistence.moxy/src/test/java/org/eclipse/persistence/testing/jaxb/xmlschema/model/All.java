/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
// Denise Smith - 2.3
package org.eclipse.persistence.testing.jaxb.xmlschema.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 *
 *    Only elements allowed inside
 *
 * <p>Java class for all complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>{@code
 * <complexType name="all">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}explicitGroup">
 *       <group ref="{http://www.w3.org/2001/XMLSchema}allModel"/>
 *       <attribute name="minOccurs" default="1">
 *         <simpleType>
 *           <restriction base="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger">
 *             <enumeration value="0"/>
 *             <enumeration value="1"/>
 *           </restriction>
 *         </simpleType>
 *       </attribute>
 *       <attribute name="maxOccurs" default="1">
 *         <simpleType>
 *           <restriction base="{http://www.w3.org/2001/XMLSchema}allNNI">
 *             <enumeration value="1"/>
 *           </restriction>
 *         </simpleType>
 *       </attribute>
 *       <anyAttribute processContents='lax' namespace='##other'/>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "all")
public class All
    extends ExplicitGroup
{


}
