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

import org.eclipse.persistence.mappings.transformers.FieldTransformer;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for {@linkplain org.eclipse.persistence.oxm.mappings.XMLTransformationMapping}.
 * XmlWriteTransformer defines transformation of the attribute value to a single
 * XML value (XPath is specified in the XmlWriteTransformer).
 * <p>
 * One or more XmlWriteTransformer annotations may be specified directly on the method or
 * attribute. Multiple occurrences of {@linkplain XmlWriteTransformer} annotation
 * can be wrapped into {@linkplain XmlWriteTransformers} annotation. No XmlWriteTransformers specified for read-only
 * mapping. Unless the {@linkplain org.eclipse.persistence.oxm.mappings.XMLTransformationMapping} is write-only,
 * it should have a {@linkplain XmlReadTransformer} defining transformation of XML value(s) into attribute value.
 *
 * @see XmlReadTransformer
 * @see XmlTransformation
 * @see XmlWriteTransformers
 *
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
@Repeatable(XmlWriteTransformers.class)
public @interface XmlWriteTransformer {
    /**
     * User-defined class that must implement the
     * {@linkplain org.eclipse.persistence.mappings.transformers.FieldTransformer} interface.
     * The class will be instantiated, its {@linkplain org.eclipse.persistence.mappings.transformers.FieldTransformer#buildFieldValue(Object, String, org.eclipse.persistence.sessions.Session)}
     * will be used to create the value to be written into XML document.
     * <p>
     * Either transformerClass or {@linkplain #method()}  must be specified, but not both.
     */
    Class<? extends FieldTransformer> transformerClass() default FieldTransformer.class;

    /**
     * The mapped class must have a method with this name which returns a value
     * to be written into the XML document.
     * <p>
     * The method may require an {@linkplain jakarta.xml.bind.annotation.XmlTransient} annotation to avoid being mapped as
     * an XmlElement by default.
     * <p>
     * Either {@linkplain #transformerClass()} or method must be specified, but not both.
     */
    String method() default "";

    /**
     * Specify here the XPath into which the value should be written.
     * <p>
     * The only case when this could be skipped is if a single XmlWriteTransformer
     * annotates an attribute - the attribute's name will be
     * used as an element name.
     */
    String xmlPath();

}
