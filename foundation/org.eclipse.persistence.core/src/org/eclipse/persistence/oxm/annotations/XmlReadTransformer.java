/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.eclipse.persistence.mappings.transformers.AttributeTransformer;

@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface XmlReadTransformer {

    /**
     * User-defined class that must implement the 
     * org.eclipse.persistence.mappings.transformers.AttributeTransformer 
     * interface. The class will be instantiated, its buildAttributeValue will 
     * be used to create the value to be assigned to the attribute.
     * Either transformerClass or method must be specified, but not both.
     */ 
    Class<? extends AttributeTransformer> transformerClass() default AttributeTransformer.class;

    /**
     * The mapped class must have a method with this name which returns a value 
     * to be assigned to the attribute (not assigns the value to the attribute).
     * Either transformerClass or method must be specified, but not both.
     */ 
    String method() default "";

}