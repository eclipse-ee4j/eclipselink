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
 *     Matt MacIvor - 2.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.jaxb;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;

/**
 * <p><b>Purpose:</b> This class provides a wrapper for an instance of AccessorFactory.
 * The instance can be from either the internal or public package. This class will 
 * reflectively invoke the createFieldAccessor and createPropertyAccessor methods 
 * on the underlying AccessorFactory to create Accessor instances.
 * <p>
 * @author mmacivor
 * @since EclipseLink 2.4
 * 
 */
public class AccessorFactoryWrapper {
    private static final String ACCESSOR_FACTORY_CREATE_FIELD_ACCESSOR = "createFieldAccessor";
    private static final String ACCESSOR_FACTORY_CREATE_PROPERTY_ACCESSOR = "createPropertyAccessor";

    private Object accessorFactory;
    private Method createFieldAccessorMethod;
    private Method createPropertyAccessorMethod;
    
    
    public AccessorFactoryWrapper(Object factory) {
        this.accessorFactory = factory;
        Class accessorClass = factory.getClass();
        try {
            createPropertyAccessorMethod = PrivilegedAccessHelper.getDeclaredMethod(accessorClass, ACCESSOR_FACTORY_CREATE_PROPERTY_ACCESSOR, new Class[]{Class.class, Method.class, Method.class});
            createFieldAccessorMethod = PrivilegedAccessHelper.getDeclaredMethod(accessorClass, ACCESSOR_FACTORY_CREATE_FIELD_ACCESSOR, new Class[]{Class.class, Field.class, boolean.class});
        } catch(Exception ex) {
            throw JAXBException.invalidAccessorFactory(accessorClass, ex);
        }
    }
    
    public Object createFieldAccessor(Class beanClass, Field field, boolean isReadOnly) {
        try {
            return PrivilegedAccessHelper.invokeMethod(createFieldAccessorMethod, accessorFactory, new Object[]{beanClass, field, isReadOnly});
        } catch(Exception ex) {
            throw JAXBException.errorCreatingFieldAccessor(accessorFactory, ex);
        }
    }
    
    public Object createPropertyAccessor(Class beanClass, Method getMethod, Method setMethod) {
        try {
            return PrivilegedAccessHelper.invokeMethod(createPropertyAccessorMethod, accessorFactory, new Object[]{beanClass, getMethod, setMethod});
        } catch(Exception ex) {
            throw JAXBException.errorCreatingPropertyAccessor(accessorFactory, ex);
        }
    }
    
    
}
