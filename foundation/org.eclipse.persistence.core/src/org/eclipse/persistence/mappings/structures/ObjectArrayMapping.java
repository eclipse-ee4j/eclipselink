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
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.ChangeRecord;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeCollectionMapping;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.descriptors.ClassDescriptor;

/**
 * <p><b>Purpose:</b>
 * In an object-relational data model, structures can contain "Arrays" or collections of other data-types.
 * In Oracle 8i, a <code>Varray</code> is typically used to represent a collection of primitive data or aggregate structures.
 * These arrays are stored with their parent structure in the same table.<p>
 *
 * ArrayMapping is used to map a collection of primitive data <p>
 * ObjectArrayMapping is used to map a collection of Oracle data-type
 *
 * <p>NOTE: Only Oracle8i supports Varray type.
 *
 * @author King (Yaoping) Wang
 * @since TOPLink/Java 3.0
 *
 * @see ArrayMapping
 */
public class ObjectArrayMapping extends AbstractCompositeCollectionMapping  implements ArrayCollectionMapping{

    /** Arrays require a structure name, this is the ADT defined for the VARRAY. */
    protected String structureName;

    /**
     * PUBLIC:
     * Return the name of the structure.
     * This is the name of the user defined data type as defined on the database.
     */
    public String getStructureName() {
        return structureName;
    }

    /**
     * INTERNAL:
     * Initialize the mapping.
     */
    public void initialize(AbstractSession session) throws DescriptorException {
        super.initialize(session);
        if ((getStructureName() == null) || getStructureName().length() == 0) {
            throw DescriptorException.structureNameNotSetInMapping(this);
        }

        // For bug 2730536 convert the field to be an ObjectRelationalDatabaseField.
        ObjectRelationalDatabaseField field = (ObjectRelationalDatabaseField)getField();
        field.setSqlType(java.sql.Types.ARRAY);
        field.setSqlTypeName(getStructureName());

        // May require native connection in WLS to avoid wrapping wrapped.
        getDescriptor().setIsNativeConnectionRequired(true);
    }

    public void setFieldName(String fieldName) {
        this.setField(new ObjectRelationalDatabaseField(fieldName));
    }

    /**
     * PUBLIC:
     * Set the name of the structure.
     * This is the name of the user defined data type as defined on the database.
     */
    public void setStructureName(String structureName) {
        this.structureName = structureName;
    }
    
    @Override
    protected Object buildCompositeObject(ClassDescriptor descriptor, AbstractRecord nestedRow, ObjectBuildingQuery query, CacheKey parentCacheKey, JoinedAttributeManager joinManager, AbstractSession targetSession) {
        Object element = descriptor.getObjectBuilder().buildNewInstance();
        descriptor.getObjectBuilder().buildAttributesIntoObject(element, parentCacheKey, nestedRow, query, joinManager, false, targetSession);
        return element;
    }

    @Override
    protected AbstractRecord buildCompositeRow(Object attributeValue, AbstractSession session, AbstractRecord parentRow, WriteType writeType) {
        return this.getObjectBuilder(attributeValue, session).buildRow(attributeValue, session, writeType);
    }
    
    /**
     * INTERNAL:
     * Build and return the change record that results
     * from comparing the two aggregate collection attributes.
     */
    public ChangeRecord compareForChange(Object clone, Object backup, ObjectChangeSet owner, AbstractSession session) {
        // Fixed to match build-update-row.
        if (session.isClassReadOnly(this.getReferenceClass())) {
            return null;
        }
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
        //Helper.toDo("bjv: need to figure out how to handle read-only elements...");
        if (mergeManager.getSession().isClassReadOnly(this.getReferenceClass())) {
            return;
        }

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

    /**
     * INTERNAL:
     */
    @Override
    public boolean isRelationalMapping() {
        return true;
    }
}
