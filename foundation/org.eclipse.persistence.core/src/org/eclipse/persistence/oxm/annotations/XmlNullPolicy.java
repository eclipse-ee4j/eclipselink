/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle = 2.2 - Initial contribution
 ******************************************************************************/
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
 * <li>emptyNodeRepresentsNull - This indicates that during unmarshal, an empty node <code>"&lt;element/>"</code>
 * should be unmarshalled to as null in the object model.</li>
 * <li>isSetPerformedForAbsentNode - If this is set to true, then for each mapped element that was absent from
 * the document during unmarshal, the property in java will be explicitly set to null. </li>
 * <li>nullRepresentationForXml - Determines how a null value in the object model is written out
 * to XML. </li></ul>
 * 
 * @see XmlNullPolicy
 * @see XmlMarshalNullRepresentation
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlNullPolicy {

    boolean xsiNilRepresentsNull() default false;

    boolean emptyNodeRepresentsNull() default false;
    
    boolean isSetPerformedForAbsentNode() default true;

    XmlMarshalNullRepresentation nullRepresentationForXml() default org.eclipse.persistence.oxm.annotations.XmlMarshalNullRepresentation.ABSENT_NODE;
}
