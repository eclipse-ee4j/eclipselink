/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
//     09/09/2011-2.3.1 Guy Pelletier
//       - 356197: Add new VPD type to MultitenantType
//     09/14/2011-2.3.1 Guy Pelletier
//       - 357533: Allow DDL queries to execute even when Multitenant entities are part of the PU
//     02/04/2013-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support
package org.eclipse.persistence.tools.schemaframework;

import java.io.*;
import java.util.Map;
import java.util.Set;

import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.databaseaccess.FieldTypeDefinition;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.queries.*;

/**
 * <p>
 * <b>Purpose</b>: Define a database object for the purpose of creation and deletion.
 * A database object is an entity such as a table, view, proc, sequence...
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Be able to create and drop the object from the database.
 * </ul>
 */
public abstract class DatabaseObjectDefinition implements Cloneable, Serializable {
    public String name;
    public String qualifier;

    /**
     * INTERNAL:
     * Retrieve a database platform specific definition from session for
     * existing type instance or build a new one when type is {@code null}.
     * @param session Session to search for already existing instances.
     * @param type    Field type class.
     * @param name    Filed type name.
     * @return Database platform specific definition instance.
     * @throws ValidationException when provided type is not valid database type.
     */
    protected static final FieldTypeDefinition getFieldTypeDefinition(
            final AbstractSession session, final Class type, final String name) {
        final FieldTypeDefinition fieldType = type != null
                ? session.getPlatform().getFieldTypeDefinition(type)
                : new FieldTypeDefinition(name);
        if (fieldType == null) {
            throw ValidationException.javaTypeIsNotAValidDatabaseType(type);
        }
        return fieldType;
    }

    /**
     * INTERNAL:
     * Retrieve database platform specific field definition from database
     * specific platform handler for existing type or build a new one when type
     * is {@code null} and type for type name could not be found.
     * @param platform Specific database platform handler.
     * @param type     Field type (will be processed first when available).
     * @param name     Field type name (will be processed as backup option when
     *                 type class is not available).
     * @throws ValidationException when provided type is not valid database type.
     */
    protected static final FieldTypeDefinition getFieldTypeDefinition(
            final DatabasePlatform platform, final Class type, final String name) {
        FieldTypeDefinition fieldType;
        if (type != null) { //translate Java 'type'
            fieldType = platform.getFieldTypeDefinition(type);
            if (fieldType == null) {
                throw ValidationException.javaTypeIsNotAValidDatabaseType(type);
            }
        } else if (name != null) { //translate generic type name
            final Map<String, Class> fieldTypes = platform.getClassTypes();
            final Class typeFromName = fieldTypes.get(name);
            if (typeFromName == null) { // if unknown type name, use as it is
                fieldType = new FieldTypeDefinition(name);
            } else {
                fieldType = platform.getFieldTypeDefinition(typeFromName);
                if (fieldType == null) {
                    throw ValidationException.javaTypeIsNotAValidDatabaseType(typeFromName);
                }
            }
        } else {
            // Both type and typeName is null
            throw ValidationException.javaTypeIsNotAValidDatabaseType(null);
        }
        return fieldType;
    }

    public DatabaseObjectDefinition() {
        this.name = "";
        this.qualifier = "";
    }

    /**
     * INTERNAL:
     * Returns the writer used for creation of this object.
     */
    public abstract Writer buildCreationWriter(AbstractSession session, Writer writer) throws ValidationException;

    /**
     * INTERNAL:
     * Sub classes should override.
     */
    public Writer buildVPDCreationPolicyWriter(AbstractSession session, Writer writer) {
        // Does nothing .. subclasses should override
        return null;
    }

    /**
     * INTERNAL:
     * Sub classes should override.
     */
    public Writer buildVPDCreationFunctionWriter(AbstractSession session, Writer writer) {
        // Does nothing .. subclasses should override
        return null;
    }

    /**
     * INTERNAL:
     * Sub classes should override.
     */
    public Writer buildVPDDeletionWriter(AbstractSession session, Writer writer) {
        // Does nothing .. subclasses should override
        return null;
    }

    /**
     * INTERNAL:
     * Returns the writer used for deletion of this object.
     */
    public abstract Writer buildDeletionWriter(AbstractSession session, Writer writer) throws ValidationException;

    /**
     * PUBLIC:
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException impossible) {
            return null;
        }
    }

    /**
     * INTERNAL:
     * Execute the DDL to create the database schema for this object.
     * Does nothing at this level, subclasses that support this must override
     * this method.
     *
     * @see TableDefinition
     */
    public void createDatabaseSchema(AbstractSession session, Writer writer, Set<String> createdDatabaseSchemas) throws EclipseLinkException {}

    /**
     * INTERNAL:
     * Execute the DDL to create the database schema for this object.
     * Does nothing at this level, subclasses that support this must override
     * this method.
     *
     * @see TableDefinition
     */
    public void createDatabaseSchemaOnDatabase(AbstractSession session, Set<String> createdDatabaseSchemas) throws EclipseLinkException {}

    /**
     * INTERNAL:
     * Either drop from the database directly or write the statement to a file.
     * Database objects are root level entities such as tables, views, procs, sequences...
     */
    public void createObject(AbstractSession session, Writer schemaWriter) throws EclipseLinkException {
        if (schemaWriter == null) {
            this.createOnDatabase(session);
        } else {
            this.buildCreationWriter(session, schemaWriter);

            if (shouldCreateVPDCalls(session)) {
                buildVPDCreationPolicyWriter(session, schemaWriter);
                buildVPDCreationFunctionWriter(session, schemaWriter);
            }
        }
    }

    /**
     * INTERNAL:
     * Execute the DDL to create this object.
     */
    public void createOnDatabase(AbstractSession session) throws EclipseLinkException {
        session.priviledgedExecuteNonSelectingCall(new SQLCall(buildCreationWriter(session, new StringWriter()).toString()));

        if (shouldCreateVPDCalls(session)) {
            session.priviledgedExecuteNonSelectingCall(new SQLCall(buildVPDCreationPolicyWriter(session, new StringWriter()).toString()));
            session.priviledgedExecuteNonSelectingCall(new SQLCall(buildVPDCreationFunctionWriter(session, new StringWriter()).toString()));
        }
    }

    /**
     * INTERNAL:
     * Subclasses who care should override this method, e.g. TableDefinition.
     */
    public boolean shouldCreateDatabaseSchema(Set<String> createdDatabaseSchemas) {
        return false;
    }

    /**
     * INTERNAL:
     * Subclasses who care should override this method.
     */
    public boolean shouldCreateVPDCalls(AbstractSession session) {
        return false;
    }

    /**
     * INTERNAL:
     * Execute the DDL to drop the database schema for this object.
     * Does nothing at this level, subclasses that support this must override
     * this method.
     *
     * @see TableDefinition
     */
    public void dropDatabaseSchema(AbstractSession session, Writer writer) throws EclipseLinkException {}

    /**
     * INTERNAL:
     * Execute the DDL to drop the database schema for this object.
     * Does nothing at this level, subclasses that support this must override
     * this method.
     *
     * @see TableDefinition
     */
    public void dropDatabaseSchemaOnDatabase(AbstractSession session) throws EclipseLinkException {}

    /**
     * INTERNAL:
     * Execute the DDL to drop the object.
     */
    public void dropFromDatabase(AbstractSession session) throws EclipseLinkException {
        session.priviledgedExecuteNonSelectingCall(new SQLCall(buildDeletionWriter(session, new StringWriter()).toString()));

        if (shouldCreateVPDCalls(session)) {
            session.priviledgedExecuteNonSelectingCall(new SQLCall(buildVPDDeletionWriter(session, new StringWriter()).toString()));
        }
    }

    /**
     * INTERNAL:
     * Execute the DDL to drop the object.  Either directly from the database
     * of write out the statement to a file.
     */
    public void dropObject(AbstractSession session, Writer schemaWriter, boolean createSQLFiles) throws EclipseLinkException {
        if (schemaWriter == null) {
            this.dropFromDatabase(session);
        } else {
            buildDeletionWriter(session, schemaWriter);

            if (shouldCreateVPDCalls(session)) {
                buildVPDDeletionWriter(session, schemaWriter);
            }
        }
    }

    /**
     * PUBLIC:
     * Return the database schema associated with this database object.
     *
     * @see TableDefinition
     */
    public String getDatabaseSchema() {
        return null;
    }

    /**
     * INTERNAL:
     * Most major databases support a creator name scope.
     * This means whenever the database object is referenced, it must be qualified.
     */
    public String getFullName() {
        if (getQualifier().equals("")) {
            return getName();
        } else {
            return getQualifier() + "." + getName();
        }
    }

    /**
     * PUBLIC:
     * Return the name of the object.
     * i.e. the table name or the sequence name.
     */
    public String getName() {
        return name;
    }

    /**
     * PUBLIC:
     * Most major databases support a creator name scope.
     * This means whenever the database object is referenced, it must be qualified.
     */
    public String getQualifier() {
        return qualifier;
    }

    /**
     * INTERNAL:
     * Return true is this database object definition has a schema definition.
     */
    protected boolean hasDatabaseSchema() {
        return getDatabaseSchema() != null && ! getDatabaseSchema().equals("");
    }

    /**
     * Execute any statements required after the creation of the object
     * @param session
     * @param createSchemaWriter
     */
    public void postCreateObject(AbstractSession session, Writer createSchemaWriter, boolean createSQLFiles){
    }

    /**
     * Execute any statements required before the deletion of the object
     * @param session
     * @param dropSchemaWriter
     */
    public void preDropObject(AbstractSession session, Writer dropSchemaWriter, boolean createSQLFiles){
    }

    /**
     * PUBLIC:
     * Set the name of the object.
     * i.e. the table name or the sequence name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * PUBLIC:
     * Most major databases support a creator name scope.
     * This means whenever the database object is referenced, it must be qualified.
     */
    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    public String toString() {
        return Helper.getShortClassName(getClass()) + "(" + getFullName() + ")";
    }
}
