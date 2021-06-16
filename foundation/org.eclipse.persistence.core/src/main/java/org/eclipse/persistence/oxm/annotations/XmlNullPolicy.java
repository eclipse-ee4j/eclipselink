/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle = 2.2 - Initial contribution
package org.eclipse.persistence.oxm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <b>Purpose:</b> Provide a means of using annotations to customise the handling of null values
 * and their xml representation.
 * <p>This annotation provides the user with a mechanism to customise the way that EclipseLink
 * handles the reading and writing of null values. The following values can be specified:
 * <ul><li>xsiNilRepresentsNull - This indicates that during unmarshal, an element with an xsi:nil="true"
 * attribute specified should be unmarshaled as "null" into the object.</li>
 * <li>emptyNodeRepresentsNull - This indicates that during unmarshal, an empty node <code>"&lt;element/&gt;"</code>
 * should be unmarshalled to as null in the object model.</li>
 * <li>isSetPerformedForAbsentNode - If this is set to true, then for each mapped element that was absent from
 * the document during unmarshal, the property in java will be explicitly set to null. </li>
 * <li>nullRepresentationForXml - Determines how a null value in the object model is written out
 * to XML. </li></ul>
 *
 * @see XmlNullPolicy
 * @see XmlMarshalNullRepresentation
 */
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.TYPE, ElementType.PACKAGE })
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlNullPolicy {

    boolean xsiNilRepresentsNull() default false;

    boolean emptyNodeRepresentsNull() default false;

    boolean isSetPerformedForAbsentNode() default true;

    XmlMarshalNullRepresentation nullRepresentationForXml() default org.eclipse.persistence.oxm.annotations.XmlMarshalNullRepresentation.ABSENT_NODE;
}
