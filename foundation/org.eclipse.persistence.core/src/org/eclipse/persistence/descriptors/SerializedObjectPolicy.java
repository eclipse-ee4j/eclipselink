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
//     24 April 2013-2.5.1 ailitche
//       SerializedObjectPolicy initial API and implementation
package org.eclipse.persistence.descriptors;

import java.io.Serializable;
import java.util.List;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;

/**
 * SerializedObjectPolicy (SOP) could be set on a non-aggregate descriptor.
 *
 * If SerializedObjectPolicy is specified Eclipselink writes out the whole entity object with its
 * privately owned (and nested privately owned) entities and element collections into an additional
 * (likely BLOB) field in the database.
 *
 * If SerializedObjectPolicy is set on an entity then SerializedObjectPolicies with the same field are set
 * on all inheriting entities.
 *
 * The goal is to make reads from the database faster.
 * The draw back is slower writes into the database.
 * So SerializedObjectPolicy may make sense for read-only / read-mostly application
 * for Entity, which always loads all its dependent entities and / or ElementCollections.
 *
 * To use SerializedObjectPolicy, ObjectLevelReadQuery should set a boolean flag
 * @see ObjectLevelReadQuery#setShouldUseSerializedObjectPolicy
 *
 * In case the serialized object column contains null or obsolete version of the object
 * the query using SerializedObjectPolicy would either throw exception or - if all other fields have been read, too -
 * would build the object using these fields (exactly as in case SerializedObjectPolicy is not used).
 *
 * Note that currently no default implementation of SerializedObjectPolicy is available
 * and this class should be provided by the user.
 *
 * @author ailitche
 * @since EclipseLink 2.5.1
 *
 * @see org.eclipse.persistence.annotations.SerializedObject
 */
public interface SerializedObjectPolicy extends Cloneable, Serializable {
    /** get owning descriptor */
    ClassDescriptor getDescriptor();
    /** set owning descriptor */
    void setDescriptor(ClassDescriptor descriptor);
    /** get the field that stores sopObject in the database (sopField) */
    DatabaseField getField();
    /** set the field that stores sopObject in the database (sopField) */
    void setField(DatabaseField field);

    SerializedObjectPolicy clone();
    /** instantiate policy for child descriptor */
    SerializedObjectPolicy instantiateChild();

    /** unless the field is already initialized by parent descriptor, add the field to the owning descriptor and set field's type */
    void initializeField(AbstractSession session);
    /** initialize the policy */
    void initialize(AbstractSession session);
    /** postinitialize the policy: determine which mappings are included, which fields should be selected */
    void postInitialize(AbstractSession session);

    /**
     * Lists the database fields that should be read by the query using the policy.
     * To allow recovery in case of null or invalid sopObject, then this method should return all the fields define by descriptor
     * (descriptor.getFields()).
     */
    List<DatabaseField> getSelectionFields();
    /**
     * Lists the database fields that should be read by the query using the policy, in case all inherited objects are read using outer joining.
     * To allow recovery in case of null or invalid sopObject, then this method should return all the fields define by descriptor
     * (descriptor.getAllFields()).
     */
    List<DatabaseField> getAllSelectionFields();

    /** Serialize the object and put the result into the row as a value corresponding to the policy field */
    void putObjectIntoRow(AbstractRecord databaseRow, Object object, AbstractSession session);
    /**
     * Deserialize the object from the value corresponding to the policy field, nullify that value, set the object into the row using setSopObject, also return it.
     * If the object is null or invalid, behaviour depend on whether the policy allows the query to recover or not (see comments to getFieldsToSelect and getAllFieldsToSelect methods):
     * if recovery is possible then the method should return null, otherwise throw QueryException (query is a parameter of this method only because it's required by QueryException).
     */
    Object getObjectFromRow(AbstractRecord databaseRow, AbstractSession session, ObjectLevelReadQuery query);
}
