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
 *     Matt MacIvor - 2.4 - Initial Implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.jaxb;

import java.lang.reflect.Method;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.mappings.AttributeAccessor;

/**
 * <p>
 * <b>Purpose:</b> Provides a wrapper around an instance of Accessor. Makes reflective 
 * calls to the nested accessor's get and set methods. The nested accessor can 
 * come from either the internal or public package.
 * 
 * @author mmacivor
 * @since EclipseLink 2.4
 */
public class CustomAccessorAttributeAccessor extends AttributeAccessor {

    private Object accessor;
    private Method getMethod;
    private Method setMethod;

    public CustomAccessorAttributeAccessor(Object accessor) {
        this.accessor = accessor;
        
        Class[] getMethodParams = new Class[]{Object.class};
        Class[] setMethodParams = new Class[]{Object.class, Object.class};
        try {
            getMethod = PrivilegedAccessHelper.getDeclaredMethod(accessor.getClass(), "get", getMethodParams);
            setMethod = PrivilegedAccessHelper.getDeclaredMethod(accessor.getClass(), "set", setMethodParams);
        } catch(Exception ex) {
            
        }
    }

    public Object getAttributeValueFromObject(Object object) throws DescriptorException {
        try {
            return PrivilegedAccessHelper.invokeMethod(getMethod, accessor, new Object[]{object});
        } catch(Exception ex) {
            throw JAXBException.errorInvokingAccessor(this.accessor, "get", ex);
        }
    }

    public void setAttributeValueInObject(Object object, Object value) throws DescriptorException {
        try {
            PrivilegedAccessHelper.invokeMethod(setMethod, accessor, new Object[]{object, value});
        } catch(Exception ex) {
            throw JAXBException.errorInvokingAccessor(this.accessor, "set", ex);
        }
    }
}
