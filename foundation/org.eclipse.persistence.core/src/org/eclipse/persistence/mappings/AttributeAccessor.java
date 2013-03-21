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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.mappings;

import java.io.*;

import org.eclipse.persistence.core.mappings.CoreAttributeAccessor;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.helper.ClassConstants;

/**
 * <p><b>Purpose</b>: This provides an abstract class for setting and retrieving
 * the attribute value for the mapping from an object.
 * It can be used in advanced situations if the attribute
 * requires advanced conversion of the mapping value, or a real attribute does not exist.
 *
 *    @author James
 *    @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public abstract class AttributeAccessor implements CoreAttributeAccessor, Cloneable, Serializable {

    /** Stores the name of the attribute */
    protected String attributeName;
    protected boolean isWriteOnly = false;
    protected boolean isReadOnly = false; 

    /**
     * INTERNAL:
     * Clones itself.
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    /**
     * INTERNAL:
     * Return the attribute name.
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * INTERNAL:
     * Set the attribute name.
     */
    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    /**
     * Return the class type of the attribute.
     */
    public Class getAttributeClass() {
        return ClassConstants.OBJECT;
    }

    /**
     * Allow any initialization to be performed with the descriptor class.
     */
    public void initializeAttributes(Class descriptorClass) throws DescriptorException {
        if (getAttributeName() == null) {
            throw DescriptorException.attributeNameNotSpecified();
        }
    }
    
    /**
     * Returns true if this attribute accessor has been initialized and now stores a reference to the
     * class's attribute.  An attribute accessor can become uninitialized on serialization.
     */
    public boolean isInitialized(){
        return true;
    }

    public boolean isInstanceVariableAttributeAccessor() {
        return false;
    }

    public boolean isMapValueAttributeAccessor(){
        return false;
    }
    
    public boolean isMethodAttributeAccessor() {
        return false;
    }
    
    public boolean isValuesAccessor() {
    	return false;
    }
    
    public boolean isVirtualAttributeAccessor(){
        return false;
    }
    
    /**
     * INTERNAL:
     * @return
     */
    public boolean isWriteOnly() {
        return isWriteOnly;
    }
    
    /**
     * INTERNAL:
     * @param aBoolean
     */
    public void setIsWriteOnly(boolean aBoolean) {
        this.isWriteOnly = aBoolean;
    }
    
    /**
     * INTERNAL
     * @return
     */
    public boolean isReadOnly() {
        return isReadOnly;
    }
    /**
     * INTERNAL
     * @param aBoolean
     */
    public void setIsReadOnly(boolean aBoolean) {
        this.isReadOnly = aBoolean;
    }

    /**
     * Return the attribute value from the object.
     */
    @Override
    public abstract Object getAttributeValueFromObject(Object object) throws DescriptorException;

    /**
     * Set the attribute value into the object.
     */
    @Override
    public abstract void setAttributeValueInObject(Object object, Object value) throws DescriptorException;
}
