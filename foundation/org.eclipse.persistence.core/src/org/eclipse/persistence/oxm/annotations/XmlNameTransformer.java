/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - EclipseLink 2.3 - Initial Implementation
 ******************************************************************************/  
package org.eclipse.persistence.oxm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.eclipse.persistence.oxm.XMLNameTransformer;

/**
 * An XmlNameTransformer allows for a user defined class to transform names. 
 * The class has the following restriction:
 *  - It must implement the org.eclipse.persistence.oxm.XmlNameTransformer interface 
 * 
 * This method will be used to decide what XML name to create from a Java class or attribute name
 * 
 * The XmlNameTransformer must only be set on a package
 */
@Target({ElementType.PACKAGE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlNameTransformer {

    /**
     * (Required) Defines the name of the XML name transformer that should be 
     * applied to names.
     */	
	Class <? extends XMLNameTransformer> value();
}