/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.sessions;

import java.util.Enumeration;
import java.util.Vector;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.mappings.Association;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.sessions.Record;

/**
 * <p>
 * <b>Purpose</b>: To record the changes for attributes that can be represented as Transformation Mapping
 * <p>
 */
public class TransformationMappingChangeRecord extends ChangeRecord implements org.eclipse.persistence.sessions.changesets.TransformationMappingChangeRecord {
    protected AbstractRecord rowCollection;

    /**
     * This default constructor is reference internally by SDK XML project to mapp this class
     */
    public TransformationMappingChangeRecord() {
        super();
    }

    /**
     * This is the basic constructor for this change Record.
     * This change record stores the fields that have changed as that is our current method of comparing
     * changes.
     */
    public TransformationMappingChangeRecord(ObjectChangeSet owner) {
        this.owner = owner;
    }

    /**
     * ADVANCED:
     * This method is used to access the changes of the fields in a trnasformation mapping.
     * @return org.eclipse.persistence.sessions.Record
     */
    public Record getRecord() {
        if (rowCollection == null) {
            this.rowCollection = new DatabaseRecord();
        }
        return rowCollection;
    }

    /**
     * INTERNAL:
     * This method will be used to merge one record into another
     */
    public void mergeRecord(ChangeRecord mergeFromRecord, UnitOfWorkChangeSet mergeToChangeSet, UnitOfWorkChangeSet mergeFromChangeSet) {
        this.rowCollection = (AbstractRecord)((TransformationMappingChangeRecord)mergeFromRecord).getRecord();
    }

    /**
     * This method is used to add a changed value to the changeRecord.  The changes in a transformation
     * mapping are recorded on the database field level
     * @param Record Record the values from the transformation mapping
     */
    public void setRow(AbstractRecord record) {
        this.rowCollection = record;
    }

    /**
     *  INTERNAL:
     *  Use by SDK XML project to mapp TransformationMappingChangeRecord
     *  Return collection of Assocition where key is the field name an value is the value of the coresponding field name
     */
    public Vector getAssociationsFromRow() {
        Vector associations = new Vector();

        for (Enumeration enumtr = ((AbstractRecord)getRecord()).getFields().elements(); enumtr.hasMoreElements();) {
            DatabaseField key = (DatabaseField)enumtr.nextElement();
            Object value = getRecord().get(key);

            if (!(value == AbstractRecord.noEntry)) {
                Association anAssociation = new Association();
                anAssociation.setKey(key.getQualifiedName());
                anAssociation.setValue(value);
                associations.add(anAssociation);
            }
        }

        if (associations.size() == 0) {
            return null;
        }
        return associations;
    }

    /**
     *  INTERNAL:
     *  Use by SDK XML project to mapp TransformationMappingChangeRecord
     *  Parse collection of Assocition of field name an value and add them to this database row
     *  @java.util. Vetor associations
     */
    public void setRowFromAssociations(Vector associations) {
    	AbstractRecord newRow = new DatabaseRecord();
        for (Enumeration enumtr = associations.elements(); enumtr.hasMoreElements();) {
            Association association = (Association)enumtr.nextElement();
            newRow.put((String)association.getKey(), association.getValue());
        }
        setRow(newRow);
    }

    /**
     * INTERNAL:
     * This method will be used to update the objectsChangeSets references
     * There is nothing to do for this mapping type as there are no object
     * references
     */
    public void updateReferences(UnitOfWorkChangeSet mergeToChangeSet, UnitOfWorkChangeSet mergeFromChangeSet) {
    }
}