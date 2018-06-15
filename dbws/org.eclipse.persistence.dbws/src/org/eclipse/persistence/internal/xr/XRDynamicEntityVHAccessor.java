/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.xr;

//javase imports
import java.util.Map;

//EclipseLink imports
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.dynamic.DynamicEntityImpl.PropertyWrapper;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * <p>
 * <b>INTERNAL:</b> XRDynamicEntityVHAccessor is similar to
 * {@link XRDynamicEntityAccessor}; however, it is used exclusively by O-X mappings
 * as they require attribute navigation <i>through</i> the ValueHolder to the
 * contained value.
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since EclipseLink 1.x
 */

@SuppressWarnings("serial")
public class XRDynamicEntityVHAccessor extends XRDynamicEntityAccessor {

    public XRDynamicEntityVHAccessor(DatabaseMapping mapping) {
        super(mapping);
    }

    @Override
    public Object getAttributeValueFromObject(Object entity) throws DescriptorException {
        Map<String, PropertyWrapper> propertiesMap = ((XRDynamicEntity)entity).getPropertiesMap();
        PropertyWrapper wrapper = propertiesMap.get(attributeName);
        Object v = null;
        if (wrapper.isSet()) {
            v = ((ValueHolderInterface)wrapper.getValue()).getValue();
        }
        return v;
    }

    @Override
    public void setAttributeValueInObject(Object entity, Object value) throws DescriptorException {
        Map<String, PropertyWrapper> propertiesMap = ((XRDynamicEntity)entity).getPropertiesMap();
        PropertyWrapper wrapper = propertiesMap.get(attributeName);
        if (value instanceof ValueHolderInterface) {
            // ValueHolders go directly into the PropertyWrapper
            wrapper.setValue(value);
        }
        else {
            if (!wrapper.isSet()) {
                wrapper.setValue(new ValueHolder(value));
            }
            else {
                ((ValueHolderInterface)wrapper.getValue()).setValue(value);
            }
        }
    }
}
