/*
 * Copyright (c) 2013, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     24 April 2013-2.5.1 ailitche
//       SerializedObjectPolicy initial API and implementation
package org.eclipse.persistence.annotations;

import jakarta.persistence.Column;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * SerializedObject annotation is used to set an
 * {@linkplain org.eclipse.persistence.descriptors.SerializedObjectPolicy} on an Entity or MappedSuperClass.
 * <p>
 * If {@linkplain org.eclipse.persistence.descriptors.SerializedObjectPolicy} is specified Eclipselink writes out
 * the whole entity object with its privately owned (and nested privately owned) entities and element collections into an additional
 * (likely BLOB) field in the database. That field could be specified in the annotation, it defaults to "<em>SOP</em>" in the main table.
 * <p><b>Example:</b>
 * {@snippet :
 *  @Entity
 *  @SerializedObject(MySerializedObjectPolicy.class);
 *  public class Employee {
 *      ...
 *  }
 * }
 * {@snippet :
 *  @Entity
 *  @SerializedObject(value = MySerializedObjectPolicy.class, column = @Column(name="SERIALIZED"));
 *  public class Address {
 *      ...
 *  }
 * }
 *
 * If {@linkplain org.eclipse.persistence.descriptors.SerializedObjectPolicy} is set on an entity then
 * {@linkplain org.eclipse.persistence.descriptors.SerializedObjectPolicy SerializedObjectPolicies}
 * with the same field are set on all inheriting entities.
 * <p>
 * The query that uses {@linkplain org.eclipse.persistence.descriptors.SerializedObjectPolicy} extracts the whole object from that field.
 * To read object(s) using {@linkplain org.eclipse.persistence.descriptors.SerializedObjectPolicy} the query should specify
 * {@linkplain org.eclipse.persistence.config.QueryHints#SERIALIZED_OBJECT}
 * <p><b>Example:</b>
 * {@snippet :
 *  Query q = em.createQuery("SELECT e FROM Employee e").setHint(org.eclipse.persistence.config.QueryHints.SERIALIZED_OBJECT, "true");
 * }
 * {@snippet :
 *  Map<String, Object> hints = new HashMap<>();
 *  hints.put("eclipselink.serialized-object", "true");
 *  Address a = em.find(Address.class, id, hints);
 * }
 *
 * <p>
 * The goal is to make reads from the database faster.
 * The drawback is slower writes into the database.
 * So {@linkplain org.eclipse.persistence.descriptors.SerializedObjectPolicy} may make sense for read-only / read-mostly application
 * for Entity, which always loads all its dependent entities and / or ElementCollections.
 * <p>
 * In case the serialized object column contains null or obsolete version of the object,
 * the query using {@linkplain org.eclipse.persistence.descriptors.SerializedObjectPolicy} would either throw exception or - if all other fields have been read, too -
 * would build the object using these fields (exactly as in case {@linkplain org.eclipse.persistence.descriptors.SerializedObjectPolicy} is not used).
 * <p>
 * Note that currently no default implementation of {@linkplain org.eclipse.persistence.descriptors.SerializedObjectPolicy} is available
 * and this class should be provided by the user.
 *
 * @see org.eclipse.persistence.descriptors.SerializedObjectPolicy
 * @see org.eclipse.persistence.config.QueryHints#SERIALIZED_OBJECT
 * @author ailitche
 * @since EclipseLink 2.5.1
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface SerializedObject {
    /**
     * The Class that implements {@linkplain org.eclipse.persistence.descriptors.SerializedObjectPolicy} interface.
     * This class must be specified.
     */
    Class<?> value();

    /**
     * The column that holds the serialized object.
     * <p>
     * By default, it's a BLOB column named "<em>SOP</em>" in entity's main table.
     */
    Column column() default @Column(name="SOP");
}
