/*******************************************************************************
* Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
* mmacivor - Feb 06/2009 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.internal.oxm.accessor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.persistence.core.mappings.CoreAttributeAccessor;
import org.eclipse.persistence.descriptors.changetracking.ChangeTracker;
import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.indirection.WeavedAttributeValueHolderInterface;
import org.eclipse.persistence.mappings.AttributeAccessor;
/**
 * INTERNAL:
 * A custom AttriuteAccessor to be used when the same object is mapped in both
 * OXM and ORM. This will bridge the gap between the two for attributes that use
 * ValueHolders. Specifically for JPA weaving.
 * @author matt.macivor
 *
 */
public class OrmAttributeAccessor extends AttributeAccessor {
    private AttributeAccessor ormAccessor;
    private CoreAttributeAccessor oxmAccessor;
    private boolean isValueHolderProperty;
    private boolean isChangeTracking;

    public OrmAttributeAccessor(AttributeAccessor ormAccessor, CoreAttributeAccessor oxmAccessor) {
        this.ormAccessor = ormAccessor;
        this.oxmAccessor = oxmAccessor;
    }
    
    public void setValueHolderProperty(boolean isValueHolder) {
        isValueHolderProperty = isValueHolder;
    }
    
    public void setChangeTracking(boolean changeTracking) {
        this.isChangeTracking = changeTracking;
    }
    
    public boolean isValueHolderProperty() {
        return this.isValueHolderProperty;
    }
    
    public boolean isChangeTracking() {
        return this.isChangeTracking;
    }

    public Object getAttributeValueFromObject(Object object) {
        if(isValueHolderProperty) {
            ValueHolderInterface vh = (ValueHolderInterface)ormAccessor.getAttributeValueFromObject(object);
            if(vh != null && !vh.isInstantiated()) {
                Object value = vh.getValue();
                oxmAccessor.setAttributeValueInObject(object, value);
                if(vh instanceof WeavedAttributeValueHolderInterface) {
                    ((WeavedAttributeValueHolderInterface)vh).setIsCoordinatedWithProperty(true);
                }
            }
        }
        return oxmAccessor.getAttributeValueFromObject(object);
    }
	
    public void setAttributeValueInObject(Object object, Object value) {
        if(isChangeTracking) {
            Object oldValue = getAttributeValueFromObject(object);
            PropertyChangeListener listener = ((ChangeTracker)object)._persistence_getPropertyChangeListener();
            if(listener != null) {
                listener.propertyChange(new PropertyChangeEvent(object, oxmAccessor.getAttributeName(), value, oldValue));
            }
        }
        if(isValueHolderProperty) {
            ValueHolderInterface vh = (ValueHolderInterface)ormAccessor.getAttributeValueFromObject(object);
            if(vh == null) {
                vh = new ValueHolder();
                ((ValueHolder)vh).setIsNewlyWeavedValueHolder(true);
            }
            vh.setValue(value);
            ormAccessor.setAttributeValueInObject(object, vh);
        }
        oxmAccessor.setAttributeValueInObject(object, value);
    }
    
    public AttributeAccessor getOrmAccessor() {
        return this.ormAccessor;
    }
    
    public CoreAttributeAccessor getOxmAccessor() {
        return this.oxmAccessor;
    }
    
    public void setOrmAccessor(AttributeAccessor accessor) {
        this.ormAccessor = accessor;
    }
    
    public void setOxmAccessor(AttributeAccessor accessor) {
        this.oxmAccessor = accessor;
    }
    
    public Class getAttributeClass() {
        return oxmAccessor.getAttributeClass();
    }
    
    public boolean isMethodAttributeAccessor() {
        return oxmAccessor.isMethodAttributeAccessor();
    }
    
    public String getAttributeName() {
        return oxmAccessor.getAttributeName();
    }
}
