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
        XRDynamicEntity.XRField df = ((XRDynamicEntity)object)._fields[fieldIdx];
        Object v = null;
        if (df.isSet) {
            v = ((ValueHolderInterface)df.fieldValue).getValue();
        }
        else {
            df.fieldValue = new ValueHolder();
        }
        return v;
    }

    @Override
    public void setAttributeValueInObject(Object object, Object value) {
        XRDynamicEntity.XRField df = ((XRDynamicEntity)object)._fields[fieldIdx];
        if (value instanceof ValueHolderInterface) {
            // ValueHolders go directly into the attribute's slot
            df.fieldValue = value;
        }
        else {
            if (!df.isSet) {
                df.fieldValue = new ValueHolder();
            }
            ((ValueHolderInterface)df.fieldValue).setValue(value);
        }
        df.isSet = true;
    }
}