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
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     07/15/2010-2.2 Guy Pelletier 
 *       -311395 : Multiple lifecycle callback methods for the same lifecycle event
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.listeners;

import java.lang.reflect.Method;
import java.util.List;

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.exceptions.ValidationException;

/**
 * A callback listener for those entities that define callback methods. 
 * Callback methods on an entity must be signatureless, hence, this class 
 * overrides behavior from EntityListener.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public class EntityClassListener extends EntityListener {    
    /**
     * INTERNAL: 
     */
    public EntityClassListener(Class entityClass) {
        super(entityClass);
    }
    
    /**
     * INTERNAL:
     * For entity classes listener methods, they need to override listeners 
     * from mapped superclasses for the same method. So we need to override 
     * this method and make the override check instead of it throwing an
     * exception for multiple lifecycle methods for the same event.
     */
    @Override
    public void addEventMethod(String event, Method method) {
        if (! hasOverriddenEventMethod(method, event)) {
            super.addEventMethod(event, method);
        }
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public Class getListenerClass() {
        return getEntityClass();
    }
    
    /**
     * INTERNAL: 
     */
    @Override
    protected void invokeMethod(String event, DescriptorEvent descriptorEvent) {
        // We must invoke callback methods in reverse order. They are added
        // from the entity first followed by its mapped superclasses.
        if (hasEventMethods(event)) {
            List<Method> eventMethods = getEventMethods(event);
            
            for (int i = eventMethods.size() - 1; i >= 0; i--) {
                Method method = eventMethods.get(i);
                Object[] objectList = {};
                invokeMethod(method, descriptorEvent.getObject(), objectList, descriptorEvent);
            }
        }
    }

    /**
     * INTERNAL:
     */
    @Override
    protected void validateMethod(Method method) {
        if (method.getParameterTypes().length > 0) {
            throw ValidationException.invalidEntityCallbackMethodArguments(getEntityClass(), method.getName());
        } else {
            // So far so good, now check the method modifiers.
            validateMethodModifiers(method);
        }
    }
}
