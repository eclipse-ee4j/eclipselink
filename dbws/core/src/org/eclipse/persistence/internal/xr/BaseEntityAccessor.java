/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

package org.eclipse.persistence.internal.xr;

// Javase imports

// Java extension imports

// TopLink imports
import org.eclipse.persistence.mappings.AttributeAccessor;

/**
 * <p>
 * <b>INTERNAL:</b> BaseEntityAccessor is used by dynamically generated
 * subclasses of {@link BaseEntity} to 'close over' information for the
 * psuedo-attributes in the <tt>Object[] attributes</tt> array inherited from
 * {@link BaseEntity}. The required information is an attributeName, and a
 * field-index into the <tt>Object[] attributes</tt> array
 * 
 * @author Mike Norman - michael.norman@oracle.com
 * @since Oracle TopLink 11.x.x
 */

@SuppressWarnings("serial")
public class BaseEntityAccessor extends AttributeAccessor {

    protected int fieldIdx;

    public BaseEntityAccessor(String attributeName, int fieldIdx) {
        super();
        setAttributeName(attributeName);
        this.fieldIdx = fieldIdx;
    }

    @Override
    public Object getAttributeValueFromObject(Object object) {
        return ((BaseEntity) object).get(fieldIdx);
    }

    @Override
    public void setAttributeValueInObject(Object object, Object value) {
        ((BaseEntity) object).set(fieldIdx, value);
    }

}
