/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.indirection;

import java.util.Collection;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.indirection.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.*;

/**
 * UnitOfWorkQueryValueHolder wraps a database-stored object and
 * implements behavior to access it. The object is read from
 * the database by invoking a user-specified query.
 * This value holder is used only in the unit of work.
 *
 * @author    Sati
 */
public class UnitOfWorkQueryValueHolder extends UnitOfWorkValueHolder {
    protected UnitOfWorkQueryValueHolder() {
        super();
    }
    
    protected UnitOfWorkQueryValueHolder(ValueHolderInterface attributeValue, Object clone, DatabaseMapping mapping, UnitOfWorkImpl unitOfWork) {
        super(attributeValue, clone, mapping, unitOfWork);
    }

    public UnitOfWorkQueryValueHolder(ValueHolderInterface attributeValue, Object clone, ForeignReferenceMapping mapping, AbstractRecord row, UnitOfWorkImpl unitOfWork) {
        this(attributeValue, clone, mapping, unitOfWork);
        this.row = row;
    }

    /**
     * Backup the clone attribute value.
     */
    protected Object buildBackupCloneFor(Object cloneAttributeValue) {
        return this.mapping.buildBackupCloneForPartObject(cloneAttributeValue, null, null, getUnitOfWork());
    }

    /**
     * Clone the original attribute value.
     */
    public Object buildCloneFor(Object originalAttributeValue) {
        Integer refreshCascade = null;
        if (wrappedValueHolder instanceof QueryBasedValueHolder){
            refreshCascade = ((QueryBasedValueHolder)getWrappedValueHolder()).getRefreshCascadePolicy();
        }
        return this.mapping.buildCloneForPartObject(originalAttributeValue, null, null, this.relationshipSourceObject, getUnitOfWork(), refreshCascade, true, true);
    }

    /**
     * Ensure that the backup value holder is populated.
     */
    public void setValue(Object theValue) {
        // Must force instantiation to be able to compare with the old value.
        if (!this.isInstantiated) {
            instantiate();
        }
        Object oldValue = getValue();
        super.setValue(theValue);
        updateForeignReferenceSet(theValue, oldValue);
    }

    /**
     * INTERNAL:
     * Here we now must check for bi-directional relationship.
     * If the mapping has a relationship partner then we must maintain the original relationship.
     * We only worry about ObjectReferenceMappings as the collections mappings will be handled by transparentIndirection
     */
    public void updateForeignReferenceRemove(Object value) {
        DatabaseMapping sourceMapping = this.getMapping();
        if (sourceMapping == null) {
            //mapping is a transient attribute. If it does not exist then we have been serialized
            return;
        }

        if (sourceMapping.isPrivateOwned()) {
            // don't null out backpointer on private owned relationship because it will cause an
            // extra update.
            return;
        }

        //	ForeignReferenceMapping partner = (ForeignReferenceMapping)getMapping().getRelationshipPartner();
        ForeignReferenceMapping partner = this.getRelationshipPartnerFor(value);
        if (partner != null) {
            if (value != null) {
                Object unwrappedValue = partner.getDescriptor().getObjectBuilder().unwrapObject(value, getSession());
                Object oldParent = partner.getRealAttributeValueFromObject(unwrappedValue, getSession());
                Object sourceObject = getRelationshipSourceObject();
                
                if (oldParent == null) {
                    // value has already been set
                    return;
                }
                // PERF: If the collection is not instantiated, then do not instantiated it.
                if (partner.isCollectionMapping()) {
                    if ((!(oldParent instanceof IndirectContainer)) || ((IndirectContainer)oldParent).isInstantiated()) {
                        if (!partner.getContainerPolicy().contains(sourceObject, oldParent, getSession())) {
                            // value has already been set
                            return;
                        }
                    }
                }

                if (partner.isObjectReferenceMapping()) {
                    // Check if it's already been set to null
                    partner.setRealAttributeValueInObject(unwrappedValue, null);
                } else if (partner.isCollectionMapping()) {
                    // If it is not in the collection then it has already been removed.
                    partner.getContainerPolicy().removeFrom(sourceObject, oldParent, getSession());
                }
            }
        }
    }

    /**
     * INTERNAL:
     * Here we now must check for bi-directional relationship.
     * If the mapping has a relationship partner then we must maintain the original relationship.
     * We only worry about ObjectReferenceMappings as the collections mappings will be handled by transparentIndirection
     */
    public void updateForeignReferenceSet(Object value, Object oldValue) {
        if ((value != null) && (value instanceof Collection)) {
            //I'm passing a collection into the valueholder not an object
            return;
        }
        if (getMapping() == null) {
            //mapping is a transient attribute. If it does not exist then we have been serialized
            return;
        }

        //	ForeignReferenceMapping partner = (ForeignReferenceMapping)getMapping().getRelationshipPartner();
        ForeignReferenceMapping partner = this.getRelationshipPartnerFor(value);
        if (partner != null) {
            if (value != null) {
                Object unwrappedValue = partner.getDescriptor().getObjectBuilder().unwrapObject(value, getSession());
                Object oldParent = partner.getRealAttributeValueFromObject(unwrappedValue, getSession());
                Object sourceObject = getRelationshipSourceObject();
                Object wrappedSource = getMapping().getDescriptor().getObjectBuilder().wrapObject(sourceObject, getSession());
                
                if (oldParent == sourceObject) {
                    // value has already been set
                    return;
                }
                // PERF: If the collection is not instantiated, then do not instantiated it.
                if (partner.isCollectionMapping()) {
                    if ((!(oldParent instanceof IndirectContainer)) || ((IndirectContainer)oldParent).isInstantiated()) {
                        if (partner.getContainerPolicy().contains(sourceObject, oldParent, getSession())) {
                            // value has already been set
                            return;
                        }
                    }
                }

                // Set the Object that was referencing this value to reference null, or remove value from its collection
                if (oldParent != null) {
                    if (getMapping().isObjectReferenceMapping()) {
                        if (!partner.isCollectionMapping()) {
                            // If the back pointer is a collection it's OK that I'm adding myself into the collection
                            ((ObjectReferenceMapping)getMapping()).setRealAttributeValueInObject(oldParent, null);
                        }
                    } else if (getMapping().isCollectionMapping() && (!partner.isManyToManyMapping())) {
                        getMapping().getContainerPolicy().removeFrom(unwrappedValue, getMapping().getRealAttributeValueFromObject(oldParent, getSession()), getSession());
                    }
                }
                
                if (oldValue != null) {
                    // CR 3487
                    Object unwrappedOldValue = partner.getDescriptor().getObjectBuilder().unwrapObject(oldValue, getSession());

                    // if this object was referencing a different object reset the back pointer on that object
                    if (partner.isObjectReferenceMapping()) {
                        partner.setRealAttributeValueInObject(unwrappedOldValue, null);
                    } else if (partner.isCollectionMapping()) {
                        partner.getContainerPolicy().removeFrom(sourceObject, partner.getRealAttributeValueFromObject(unwrappedOldValue, getSession()), getSession());
                    }
                }

                // Now set the back reference of the value being passed in to point to this object
                if (partner.isObjectReferenceMapping()) {
                    partner.setRealAttributeValueInObject(unwrappedValue, wrappedSource);
                } else if (partner.isCollectionMapping()) {
                    partner.getContainerPolicy().addInto(wrappedSource, oldParent, getSession());
                }
            } else {
                updateForeignReferenceRemove(oldValue);
            }
        }
    }

    /**
     * Helper method to retrieve the relationship partner mapping.  This will take inheritance
     * into account and return the mapping associated with correct subclass if necessary.  This
     * is needed for EJB 2.0 inheritance
     */
    private ForeignReferenceMapping getRelationshipPartnerFor(Object partnerObject) {
        ForeignReferenceMapping partner = (ForeignReferenceMapping)getMapping().getRelationshipPartner();
        if ((partner == null) || (partnerObject == null)) {
            // no partner, nothing to do
            return partner;
        }

        // if the target object is not an instance of the class type associated with the partner
        // mapping, try and look up the same partner mapping but as part of the partnerObject's
        // descriptor.  Only check if inheritance is involved...
        if (partner.getDescriptor().hasInheritance()) {
            ClassDescriptor partnerObjectDescriptor = this.getSession().getDescriptor(partnerObject);
            if (!(partner.getDescriptor().getJavaClass().isAssignableFrom(partnerObjectDescriptor.getJavaClass()))) {
                return (ForeignReferenceMapping)partnerObjectDescriptor.getObjectBuilder().getMappingForAttributeName(partner.getAttributeName());
            }
        }
        return partner;
    }
}
