/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors: 
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 ******************************************************************************/
package org.eclipse.persistence.internal.descriptors;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.mappings.AttributeAccessor;
import org.eclipse.persistence.sessions.Session;

/**
 * <b>Purpose</b>: A wrapper class for handling cases when the domain object 
 * has a property to map to the database field.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.4
 */
public class MultitenantPrimaryKeyAccessor extends AttributeAccessor {
    public MultitenantPrimaryKeyAccessor() {}
    
    /**
     * The only API of value on this accessor is getValue. To avoid a number
     * of other core changes (to check for null) this accessor extends the
     * regular attribute accessor.
     */
    public Object getValue(Session session) {
        return session.getProperty(getAttributeName());
    }
    
    @Override
    public void initializeAttributes(Class javaClass) throws DescriptorException {
        // Does nothing
    }
    
    @Override
    public Object getAttributeValueFromObject(Object object) throws DescriptorException {
        return null;
    }

    @Override
    public void setAttributeValueInObject(Object object, Object value) throws DescriptorException {
        // Does nothing
    }
}
