/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.5 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.core.descriptors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.core.queries.CoreAttributeGroup;
import org.eclipse.persistence.internal.core.descriptors.CoreInstantiationPolicy;
import org.eclipse.persistence.internal.core.descriptors.CoreObjectBuilder;
import org.eclipse.persistence.internal.core.helper.CoreField;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;

/**
 * INTERNAL
 * A abstraction of descriptor capturing behavior common to all persistence 
 * types.
 */
public abstract class CoreDescriptor<
    ATTRIBUTE_GROUP extends CoreAttributeGroup,
    DESCRIPTOR_EVENT_MANAGER extends CoreDescriptorEventManager,
    FIELD extends CoreField,
    INHERITANCE_POLICY extends CoreInheritancePolicy,
    INSTANTIATION_POLICY extends CoreInstantiationPolicy,
    LIST extends List,
    OBJECT_BUILDER extends CoreObjectBuilder> implements Serializable {

    protected DESCRIPTOR_EVENT_MANAGER eventManager;
    protected FIELD field;
    protected INSTANTIATION_POLICY instantiationPolicy;
    protected INHERITANCE_POLICY inheritancePolicy;
    protected OBJECT_BUILDER objectBuilder;
    
    protected Map<String, ATTRIBUTE_GROUP> attributeGroups;

    
    /**
     * Adds the attribute group to this descriptor. 
     * @param group
     */
    public void addAttributeGroup(ATTRIBUTE_GROUP group) {
        if (this.attributeGroups == null){
            this.attributeGroups = new HashMap<String, ATTRIBUTE_GROUP>();
        }
        this.attributeGroups.put(group.getName(), group);
    }
    

    /**
     * PUBLIC:
     * Returns the attribute group corresponding to the name provided.
     * If no group is found with the specified name, null is returned.
     */
    public ATTRIBUTE_GROUP getAttributeGroup(String name){
        if (this.attributeGroups == null){
            return null;
        }else if (name != null){
            return this.attributeGroups.get(name);
        }else{
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("null_argument_get_attributegroup"));
        }
    }
    
    /**
     * ADVANCED:
     * Returns the attribute groups for this Descriptor.
     */
    public Map<String, ATTRIBUTE_GROUP> getAttributeGroups(){
        return this.attributeGroups;
    }    
       
    /**
     * PUBLIC:
     * Get the event manager for the descriptor.  The event manager is responsible
     * for managing the pre/post selectors.
     */
    public abstract DESCRIPTOR_EVENT_MANAGER getEventManager();

    /**
     * PUBLIC:
     * The inheritance policy is used to define how a descriptor takes part in inheritance.
     * All inheritance properties for both child and parent classes is configured in inheritance policy.
     * Caution must be used in using this method as it lazy initializes an inheritance policy.
     * Calling this on a descriptor that does not use inheritance will cause problems, #hasInheritance() must always first be called.
     */
    public abstract INHERITANCE_POLICY getInheritancePolicy();

    /**
     * INTERNAL:
     * Returns the instantiation policy.
     */
    public abstract INSTANTIATION_POLICY getInstantiationPolicy();

    /**
     * PUBLIC:
     * Return the java class.
     */
    public abstract Class getJavaClass();

    /**
     * INTERNAL:
     * Return the object builder
     */
    public abstract OBJECT_BUILDER getObjectBuilder();

    /**
     * PUBLIC:
     * Return the names of all the primary keys.
     */
    public abstract List<String> getPrimaryKeyFieldNames();

    /**
     * INTERNAL:
     * Return all the primary key fields
     */
    public abstract List<FIELD> getPrimaryKeyFields();

    /**
     * INTERNAL:
     * searches first descriptor than its ReturningPolicy for an equal field
     */
    public abstract FIELD getTypedField(FIELD field);

    /**
     * INTERNAL:
     * returns true if a DescriptorEventManager has been set.
     */ 
    public abstract boolean hasEventManager();

    /**
     * INTERNAL:
     * Return if this descriptor is involved in inheritance, (is child or parent).
     * Note: If this class is part of table per class inheritance strategy this
     * method will return false. 
     * @see hasTablePerClassPolicy()
     */
    public abstract boolean hasInheritance();

    /**
     * INTERNAL:
     * Set the event manager for the descriptor.  The event manager is responsible
     * for managing the pre/post selectors.
     */
    public abstract void setEventManager(DESCRIPTOR_EVENT_MANAGER eventManager);

    /**
     * INTERNAL:
     * Sets the inheritance policy.
     */
    public abstract void setInheritancePolicy(INHERITANCE_POLICY inheritancePolicy);

    /**
     * INTERNAL:
     * Sets the instantiation policy.
     */
    public abstract void setInstantiationPolicy(INSTANTIATION_POLICY instantiationPolicy);

    /**
    * PUBLIC:
    * Set the Java class that this descriptor maps.
    * Every descriptor maps one and only one class.
    */
    public abstract void setJavaClass(Class javaClass);

    /**
     * INTERNAL:
     * Set the ObjectBuilder.
     */
    protected abstract void setObjectBuilder(OBJECT_BUILDER objectBuilder);

    /**
     * PUBLIC:
     * User can specify a vector of all the primary key field names if primary key is composite.
     *
     * @see #addPrimaryKeyFieldName(String)
     */
    public abstract void setPrimaryKeyFieldNames(LIST primaryKeyFieldNames);

    /**
     * PUBLIC:
     * User can specify a vector of all the primary key field names if primary key is composite.
     *
     * @see #addPrimaryKeyFieldName(String)
     */
    public abstract void setPrimaryKeyFields(List<FIELD> primaryKeyFields); 
}