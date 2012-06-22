/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.mappings.structures;

import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.ChangeRecord;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeDirectCollectionMapping;


/**
 * <p><b>Purpose:</b>
 * In an object-relational data model, structures can contain "Arrays" or collections of other data-types.
 * In Oracle 8i, a "VARRAY" is typically used to represent a collection of primitive data or aggregate structures.
 * These arrays are stored with their parent structure in the same table.
 *
 * @see StructureMapping
 * @see NestedTableMapping
 * @see ReferenceMapping
 */
public class ArrayMapping extends AbstractCompositeDirectCollectionMapping implements ArrayCollectionMapping{

    /**
     * Default constructor.
     */
    public ArrayMapping() {
        super();
    }
    
    /**
     * PUBLIC:
     * Set the name of the field that holds the nested collection.
     */
    public void setFieldName(String fieldName) {
        this.setField(new ObjectRelationalDatabaseField(fieldName));
    }
    
    
    /**
     * PUBLIC:
     * Return the name of the structure.
     * This is the name of the user-defined data type as defined on the database.
     */
    public String getStructureName() {
        return this.getElementDataTypeName();
    }

    /**
     * PUBLIC:
     * Set the name of the structure.
     * This is the name of the user-defined data type as defined on the database.
     */
    public void setStructureName(String structureName) {
        this.setElementDataTypeName(structureName);
    }
    
    
    /**
     * PUBLIC:
     * Return the "data type" associated with each element
     * in the nested collection.
     * Depending on the data store, this could be optional.
     */
    public String getElementDataTypeName() {
        return elementDataTypeName;
    }

    /**
     * PUBLIC:
     * Set the "data type" associated with each element
     * in the nested collection.
     * Depending on the data store, this could be optional.
     */
    public void setElementDataTypeName(String elementDataTypeName) {
        this.elementDataTypeName = elementDataTypeName;
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean isRelationalMapping() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Initialize the mapping.
     */
    public void initialize(AbstractSession session) throws DescriptorException {
        super.initialize(session);
        if (this.getStructureName().length() == 0) {
            throw DescriptorException.structureNameNotSetInMapping(this);
        }

        // For bug 2730536 convert the field to be an ObjectRelationalDatabaseField.
        ObjectRelationalDatabaseField field = (ObjectRelationalDatabaseField)getField();
        field.setSqlType(java.sql.Types.ARRAY);
        field.setSqlTypeName(getStructureName());
    }
    
    /**
     * INTERNAL:
     * Build and return the change record that results
     * from comparing the two direct collection attributes.
     */
    public ChangeRecord compareForChange(Object clone, Object backup, ObjectChangeSet owner, AbstractSession session) {
        return (new ArrayCollectionMappingHelper(this)).compareForChange(clone, backup, owner, session);
    }

    /**
     * INTERNAL:
     * Compare the attributes belonging to this mapping for the objects.
     */
    public boolean compareObjects(Object object1, Object object2, AbstractSession session) {
        return (new ArrayCollectionMappingHelper(this)).compareObjects(object1, object2, session);
    }

    /**
     * INTERNAL:
     * Merge changes from the source to the target object.
     */
    public void mergeChangesIntoObject(Object target, ChangeRecord changeRecord, Object source, MergeManager mergeManager, AbstractSession targetSession) {
        (new ArrayCollectionMappingHelper(this)).mergeChangesIntoObject(target, changeRecord, source, mergeManager, targetSession);
    }

    /**
     * INTERNAL:
     * Merge changes from the source to the target object.
     * Simply replace the entire target collection.
     */
    public void mergeIntoObject(Object target, boolean isTargetUnInitialized, Object source, MergeManager mergeManager, AbstractSession targetSession) {
        (new ArrayCollectionMappingHelper(this)).mergeIntoObject(target, isTargetUnInitialized, source, mergeManager, targetSession);
    }

    /**
     * ADVANCED:
     * This method is used to have an object add to a collection once the changeSet is applied
     * The referenceKey parameter should only be used for direct Maps.
     */
    public void simpleAddToCollectionChangeRecord(Object referenceKey, Object changeSetToAdd, ObjectChangeSet changeSet, AbstractSession session) {
        (new ArrayCollectionMappingHelper(this)).simpleAddToCollectionChangeRecord(referenceKey, changeSetToAdd, changeSet, session);
    }

    /**
     * ADVANCED:
     * This method is used to have an object removed from a collection once the changeSet is applied
     * The referenceKey parameter should only be used for direct Maps.
     */
    public void simpleRemoveFromCollectionChangeRecord(Object referenceKey, Object changeSetToRemove, ObjectChangeSet changeSet, AbstractSession session) {
        (new ArrayCollectionMappingHelper(this)).simpleRemoveFromCollectionChangeRecord(referenceKey, changeSetToRemove, changeSet, session);
    }

}
