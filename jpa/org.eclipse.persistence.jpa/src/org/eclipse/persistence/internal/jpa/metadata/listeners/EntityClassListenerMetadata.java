/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
package org.eclipse.persistence.internal.jpa.metadata.listeners;

import java.lang.reflect.Method;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;

/**
 * A callback listener for those entities that define callback methods. 
 * Callback methods on an entity must be signatureless, hence, this class 
 * overrides behavior from MetadataEntityListener.
 * 
 * @author Guy Pelletier
 * @since TopLink 10.1.3/EJB 3.0 Preview
 */
public class EntityClassListenerMetadata extends EntityListenerMetadata {	
    /**
     * INTERNAL: 
     */
    public EntityClassListenerMetadata(EntityAccessor classAccessor) {
        super(classAccessor.getJavaClass());
        
        // Set any XML defined call back method names.
        setPostLoad(classAccessor.getPostLoad());
        setPostPersist(classAccessor.getPostPersist());
        setPostRemove(classAccessor.getPostRemove());
        setPostUpdate(classAccessor.getPostUpdate());
        setPrePersist(classAccessor.getPrePersist());
        setPreRemove(classAccessor.getPreRemove());
        setPreUpdate(classAccessor.getPreUpdate());
    }
    
    /**
     * INTERNAL: (Override from MetadataEntityListener)
     * For entity classes listener methods, they need to override listeners 
     * from mapped superclasses for the same method. So we need to override 
     * this method and make the override check instead of it throwing an
     * exception for multiple lifecycle methods for the same event.
     */
    public void addEventMethod(String event, Method method) {
        if (! hasOverriddenEventMethod(method, event)) {
            super.addEventMethod(event, method);
        }
    }
    
    /**
     * INTERNAL:
     */
    public Class getListenerClass() {
        return getEntityClass();
    }
	
    /**
     * INTERNAL: 
     */
    protected void invokeMethod(String event, DescriptorEvent descriptorEvent) {
        Object[] objectList = {};
        invokeMethod(getEventMethod(event), descriptorEvent.getObject(), objectList, descriptorEvent);
    }

    /**
     * INTERNAL:
     */
    protected void validateMethod(Method method) {
        if (method.getParameterTypes().length > 0) {
            throw ValidationException.invalidEntityCallbackMethodArguments(getEntityClass(), method.getName());
        } else {
            // So far so good, now check the method modifiers.
            validateMethodModifiers(method);
        }
    }
}
