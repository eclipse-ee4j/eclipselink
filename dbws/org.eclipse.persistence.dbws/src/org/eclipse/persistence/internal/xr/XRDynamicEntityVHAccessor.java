/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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
package org.eclipse.persistence.internal.xr;

// EclipseLink imports
import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;

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

    public XRDynamicEntityVHAccessor(String attributeName, int fieldIdx) {
        super(attributeName, fieldIdx);
    }

    @Override
    public Object getAttributeValueFromObject(Object object) {

        XRDynamicEntity entity = (XRDynamicEntity)object;
        // if the attribute's slot is empty, put in a dummy ValueHolder
        // and return null
        if (entity.get(fieldIdx) == null) {
            entity.set(fieldIdx, new ValueHolder());
            return null;
        }
        // return the value from the attribute's existing ValueHolder
        return ((ValueHolderInterface)entity.get(fieldIdx)).getValue();
    }

    @Override
    public void setAttributeValueInObject(Object object, Object value) {

        XRDynamicEntity entity = (XRDynamicEntity)object;
        // ValueHolders go directly into the attribute's slot
        if (value instanceof ValueHolderInterface) {
            entity.set(fieldIdx, value);
            return;
        }
        // what if this is the first time setting - the attribute's slot
        // will be null and needs a dummy ValueHolder
        if (entity.get(fieldIdx) == null) {
            entity.set(fieldIdx, new ValueHolder());
        }
        // put the value into the attribute's existing ValueHolder
        ((ValueHolderInterface)entity.get(fieldIdx)).setValue(value);
    }
}