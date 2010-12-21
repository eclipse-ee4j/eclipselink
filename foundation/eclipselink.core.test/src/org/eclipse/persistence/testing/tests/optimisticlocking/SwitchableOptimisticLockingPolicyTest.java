/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dminsky - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.optimisticlocking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.persistence.descriptors.AllFieldsLockingPolicy;
import org.eclipse.persistence.descriptors.ChangedFieldsLockingPolicy;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorQueryManager;
import org.eclipse.persistence.descriptors.SelectedFieldsLockingPolicy;
import org.eclipse.persistence.descriptors.TimestampLockingPolicy;
import org.eclipse.persistence.descriptors.VersionLockingPolicy;

import org.eclipse.persistence.internal.descriptors.OptimisticLockingPolicy;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.sessions.UnitOfWork;

import org.eclipse.persistence.testing.models.optimisticlocking.AbstractVideogameObject;
import org.eclipse.persistence.testing.framework.TestCase;

/**
 * Test updating a value from a null DB value to a non-null value and back again.
 * EL bug 319759
 * @author dminsky
 */
public class SwitchableOptimisticLockingPolicyTest extends TestCase {

    protected Exception tlException;
    protected Class optimisticLockingPolicyClass;
    protected Map<Class, OptimisticLockingPolicy> oldOptimisticLockingPolicies;

    public SwitchableOptimisticLockingPolicyTest(Class optimisticLockingPolicyClass) {
        super();
        setName(getName() + " (" + Helper.getShortClassName(optimisticLockingPolicyClass) + ")");
        this.optimisticLockingPolicyClass = optimisticLockingPolicyClass;
        this.oldOptimisticLockingPolicies = new HashMap();
    }
    
    public void setup() {
        Set<Class> keys = getOldOptimisticLockingPolicies().keySet();
        Iterator<Class> iterator = new ArrayList(keys).iterator();
        while (iterator.hasNext()) {
            Class classToModify = iterator.next();
            
            // Validate usage
            if (!AbstractVideogameObject.class.isAssignableFrom(classToModify)) {
                throwError(classToModify.getName() + " does not implement " + AbstractVideogameObject.class.getName());
            }
            if (!OptimisticLockingPolicy.class.isAssignableFrom(getOptimisticLockingPolicyClass())) {
                throwError(getOptimisticLockingPolicyClass().getName() + " does not implement " + OptimisticLockingPolicy.class.getName());
            }
            
            // Cache descriptor and old locking policy
            ClassDescriptor descriptor = getSession().getDescriptor(classToModify);
            getOldOptimisticLockingPolicies().put(classToModify, descriptor.getOptimisticLockingPolicy());
            
            // Switch the policy
            AbstractVideogameObject instance = (AbstractVideogameObject) Helper.getInstanceFromClass(classToModify);
            if (getOptimisticLockingPolicyClass() == ChangedFieldsLockingPolicy.class) {
                instance.configureChangedFieldsLockingOn(descriptor);
            } else if (getOptimisticLockingPolicyClass() == AllFieldsLockingPolicy.class) {
                instance.configureAllFieldsLockingOn(descriptor);
            } else if (getOptimisticLockingPolicyClass() == VersionLockingPolicy.class) {
                instance.configureVersionLockingOn(descriptor);
            } else if (getOptimisticLockingPolicyClass() == SelectedFieldsLockingPolicy.class) {
                instance.configureSelectedFieldsLockingOn(descriptor);
            } else if (getOptimisticLockingPolicyClass() == TimestampLockingPolicy.class) {
                instance.configureTimestampLockingOn(descriptor);
            } else {
                throwError("Invalid optimistic locking policy " + getOptimisticLockingPolicyClass().getName());
            }
            
            // Reinitialize the Descriptor
            descriptor.getOptimisticLockingPolicy().initialize((AbstractSession) getSession());
            // Reinitialize the query manager & update call cache
            descriptor.setQueryManager(new DescriptorQueryManager());
            descriptor.getQueryManager().initialize((AbstractSession)getSession());
        }
    }
    
    public void deleteObject(Object object) {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.deleteObject(uow.readObject(object));
        uow.commit();
    }
    
    public void verify() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        if (this.tlException != null) {
            throwError("An exception occurred updating the object on the DB: ", tlException);
        }
    }

    public void reset() {
        Set<Class> keys = getOldOptimisticLockingPolicies().keySet();
        
        Iterator<Class> iterator = new ArrayList(keys).iterator();
        while (iterator.hasNext()) {
            Class classToModify = iterator.next();
            ClassDescriptor descriptor = getSession().getDescriptor(classToModify);
            
            // re-init old optimistic locking policy
            OptimisticLockingPolicy oldOptimisticLockingPolicy = getOldOptimisticLockingPolicies().get(classToModify);
            descriptor.setOptimisticLockingPolicy(oldOptimisticLockingPolicy);
            
            if (descriptor.getOptimisticLockingPolicy() != null) {
                descriptor.getOptimisticLockingPolicy().initialize((AbstractSession)getSession());
            }
            
            // Reinitialize the query manager & update call cache
            descriptor.setQueryManager(new DescriptorQueryManager());
            descriptor.getQueryManager().initialize((AbstractSession)getSession());
        }
        
        this.tlException = null;
        this.oldOptimisticLockingPolicies.clear();
    }
    
    public void addClassToModify(Class clazz) {
        getOldOptimisticLockingPolicies().put(clazz, null);
    }
    
    public Class getOptimisticLockingPolicyClass() {
        return optimisticLockingPolicyClass;
    }

    public void setOptimisticLockingPolicyClass(Class optimisticLockingPolicyClass) {
        this.optimisticLockingPolicyClass = optimisticLockingPolicyClass;
    }

    public Map<Class, OptimisticLockingPolicy> getOldOptimisticLockingPolicies() {
        return oldOptimisticLockingPolicies;
    }

    public void setOldOptimisticLockingPolicies(Map<Class, OptimisticLockingPolicy> oldOptimisticLockingPolicy) {
        this.oldOptimisticLockingPolicies = oldOptimisticLockingPolicy;
    }
    
}
