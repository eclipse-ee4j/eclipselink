/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
//     06/12/2017-2.7 Lukas Jungmann
//       - 518155: [jpa22] add support for repeatable annotations
package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.eclipse.persistence.sessions.serializers.JavaSerializer;
import org.eclipse.persistence.sessions.serializers.Serializer;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;

/**
 * A SerializedConverter is used to serialize an object's value into a database binary, character, or XML field.
 * This annotation allows a named converter that can be used in mappings.
 *
 * A converter must be be uniquely identified by name and can be defined at
 * the class level and can be specified within an Entity,
 * MappedSuperclass and Embeddable class.
 *
 * The usage of a SerializedConverter is always specified via the Converter annotation and
 * is supported on a Basic, or ElementCollection mapping.
 *
 * @see org.eclipse.persistence.annotations.Converter
 * @see org.eclipse.persistence.sessions.serializers.Serializer
 * @author James Sutherland
 * @since EclipseLink 2.6
 */
@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
@Repeatable(SerializedConverters.class)
public @interface SerializedConverter {
    /**
     * (Required) Name this converter. The name should be unique across the
     * whole persistence unit.
     */
    String name();

    /**
     * Allows a package name to be passed to the serializer.
     * This is used by some serializers such as XML, JSON to initialize the
     * JAXB context from the classes in the package or a jaxb.index file.
     */
    String serializerPackage() default "";

    /**
     * The serializer class to be used. This class must implement the
     * org.eclipse.persistence.sessions.serializers.Serializer interface.
     */
    Class<? extends Serializer> serializerClass() default JavaSerializer.class;
}
