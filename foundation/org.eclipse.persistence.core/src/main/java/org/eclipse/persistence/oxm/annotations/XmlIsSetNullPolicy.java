/*
 * Copyright (c) 2011, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle = 2.2 - Initial contribution
package org.eclipse.persistence.oxm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provide a means of using annotations to customise the handling of null values
 * and their xml representation.
 * <p>This annotation provides the user with a mechanism to customise the way that EclipseLink handles
 * the reading and writing of null values. This version of {@linkplain org.eclipse.persistence.oxm.mappings.nullpolicy.NullPolicy}
 * makes use of an <em>isSet</em> method to differentiate between values that were explicitly set to null vs values
 * which are null due to being unset. In this case the marshal behaviour specified by the <em>nullRepresentationForXml</em>
 * will only be used if the property was set. A set is only performed during unmarshal if an element was present in the
 * document. The following values can be specified:
 * <ul>
 *   <li><em>xsiNilRepresentsNull</em> - This indicates that during unmarshal, an element with an xsi:nil="true"
 * attribute specified should be unmarshalled as "null" into the object.</li>
 *   <li><em>emptyNodeRepresentsNull</em> - This indicates that during unmarshal, an empty node "{@code <element/>}"
 * should be unmarshalled to as null in the object model.</li>
 *   <li><em>nullRepresentationForXml</em> - Determines how a null value in the object model is written out
 * to XML.</li>
 *   <li><em>isSetMethodName</em> - The name of the isSet method on the object owning this property. This
 * will be invoked to determine if a value of null was set or unset.</li>
 *   <li><em>isSetMethodParameters</em> - A list of any parameter values and their types to be passed into
 * the isSet method when invoked at runtime.</li>
 * </ul>
 *
 * @see XmlNullPolicy
 * @see XmlMarshalNullRepresentation
 * @see XmlParameter
 * @see org.eclipse.persistence.oxm.mappings.nullpolicy.NullPolicy
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlIsSetNullPolicy {
    boolean xsiNilRepresentsNull() default false;

    boolean emptyNodeRepresentsNull() default false;

    XmlMarshalNullRepresentation nullRepresentationForXml() default XmlMarshalNullRepresentation.ABSENT_NODE;

    String isSetMethodName();

    XmlParameter[] isSetParameters() default {};
}
