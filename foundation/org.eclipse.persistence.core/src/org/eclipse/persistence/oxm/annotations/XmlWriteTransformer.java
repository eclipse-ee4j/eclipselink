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

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.eclipse.persistence.mappings.transformers.FieldTransformer;

/**
 * Annotation for org.eclipse.persistence.mappings.oxm.XMLTransformationMapping.
 * WriteTransformer defines transformation of the attribute value to a single
 * XML value (XPath is specified in the WriteTransformer).
 *  
 * A single XmlWriteTransformer may be specified directly on the method or 
 * attribute. Multiple XmlWriteTransformers should be wrapped into 
 * XmlWriteTransformers annotation. No XmlWriteTransformers specified for read-only 
 * mapping. Unless the XMLTransformationMapping is write-only, it should have a 
 * ReadTransformer, it defines transformation of XML value(s) 
 * into attribute value.
 *
 * @see org.eclipse.persistence.oxm.annotations.XmlReadTransformer
 * @see org.eclipse.persistence.oxm.annotations.XmlTransformation
 * @see org.eclipse.persistence.oxm.annotations.XmlWriteTransformers
 * 
 */

@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface XmlWriteTransformer {

    /**
     * User-defined class that must implement the
     * org.eclipse.persistence.mappings.transformers.FieldTransformer interface.
     * The class will be instantiated, its buildFieldValue will be used to 
     * create the value to be written into XML document.
     * Either transformerClass or method must be specified, but not both.
     */ 
    Class<? extends FieldTransformer> transformerClass() default FieldTransformer.class;

    /**
     * The mapped class must have a method with this name which returns a value 
     * to be written into the XML document.
     * The method may require an XmlTransient annotation to avoid being mapped as 
     * an XmlElement  by default.
     * Either transformerClass or method must be specified, but not both.
     */ 
    String method() default "";

    /**
     * Specify here the XPath into which the value should be written.
     * The only case when this could be skipped is if a single WriteTransformer
     * annotates an attribute - the attribute's name will be
     * used as an element name.
     */ 
    String xmlPath();

}