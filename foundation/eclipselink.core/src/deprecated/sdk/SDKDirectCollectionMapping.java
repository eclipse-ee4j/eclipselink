/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package deprecated.sdk;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.ChangeRecord;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeDirectCollectionMapping;
import org.eclipse.persistence.internal.helper.*;

/**
 * <code>SDKDirectCollectionMapping</code> consolidates the behavior of mappings that
 * map collections of "native" data objects (e.g. <code>String</code>s).
 * These are objects that do
 * not have their own descriptor and repeat within the database
 * row for the containing object. (Sorta like a <code>DirectCollectionMapping</code>
 * without the additional table.)
 *
 * @see SDKDescriptor
 * @see SDKFieldValue
 * @see deprecated.sdk.SDKCollectionMappingHelper
 * @see deprecated.sdk.SDKCollectionChangeRecord
 * @see deprecated.sdk.SDKOrderedCollectionChangeRecord
 *
 * @author Big Country
 * @since TOPLink/Java 3.0
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.eis}
 */
public class SDKDirectCollectionMapping extends AbstractCompositeDirectCollectionMapping implements SDKCollectionMapping {

    /**
     * Default constructor.
     */
    public SDKDirectCollectionMapping() {
        super();
    }

    /**
     * PUBLIC:
     * Return the name of the field that holds the nested collection.
     */
    public String getFieldName() {
        return this.getField().getName();
    }

    /**
     * PUBLIC:
     * Set the name of the field that holds the nested collection.
     */
    public void setFieldName(String fieldName) {
        this.setField(new DatabaseField(fieldName));
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
     * Build and return the change record that results
     * from comparing the two direct collection attributes.
     */
    public ChangeRecord compareForChange(Object clone, Object backup, ObjectChangeSet owner, AbstractSession session) {
        return (new SDKCollectionMappingHelper(this)).compareForChange(clone, backup, owner, session);
    }

    /**
     * INTERNAL:
     * Compare the attributes belonging to this mapping for the objects.
     */
    public boolean compareObjects(Object object1, Object object2, AbstractSession session) {
        return (new SDKCollectionMappingHelper(this)).compareObjects(object1, object2, session);
    }

    /**
     * INTERNAL:
     * Merge changes from the source to the target object.
     */
    public void mergeChangesIntoObject(Object target, ChangeRecord changeRecord, Object source, MergeManager mergeManager) {
        (new SDKCollectionMappingHelper(this)).mergeChangesIntoObject(target, changeRecord, source, mergeManager);
    }

    /**
     * INTERNAL:
     * Merge changes from the source to the target object.
     * Simply replace the entire target collection.
     */
    public void mergeIntoObject(Object target, boolean isTargetUnInitialized, Object source, MergeManager mergeManager) {
        (new SDKCollectionMappingHelper(this)).mergeIntoObject(target, isTargetUnInitialized, source, mergeManager);
    }

    /**
     * ADVANCED:
     * This method is used to have an object add to a collection once the changeSet is applied
     * The referenceKey parameter should only be used for direct Maps.
     */
    public void simpleAddToCollectionChangeRecord(Object referenceKey, Object changeSetToAdd, ObjectChangeSet changeSet, AbstractSession session) {
        (new SDKCollectionMappingHelper(this)).simpleAddToCollectionChangeRecord(referenceKey, changeSetToAdd, changeSet, session);
    }

    /**
     * ADVANCED:
     * This method is used to have an object removed from a collection once the changeSet is applied
     * The referenceKey parameter should only be used for direct Maps.
     */
    public void simpleRemoveFromCollectionChangeRecord(Object referenceKey, Object changeSetToRemove, ObjectChangeSet changeSet, AbstractSession session) {
        (new SDKCollectionMappingHelper(this)).simpleRemoveFromCollectionChangeRecord(referenceKey, changeSetToRemove, changeSet, session);
    }
}