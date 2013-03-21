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
* mmacivor - October 16th 2009 - 2.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.oxm.mappings;

import org.eclipse.persistence.internal.descriptors.InstanceVariableAttributeAccessor;
import org.eclipse.persistence.internal.descriptors.MethodAttributeAccessor;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.mappings.AttributeAccessor;

/**
 * <p><b>Purpose:</b> Provides a means to configure bidirectional relationship 
 * maintenance for OXM mappings.  
 * ant
 * @author mmacivor
 */
public class BidirectionalPolicy {
    private AttributeAccessor bidirectionalTargetAccessor;
    private ContainerPolicy bidirectionalTargetContainerPolicy;
    
    /**
     * Sets the AttributeAccessor that is used to get and set the value of the 
     * container on the target object.
     * 
     * @param anAttributeAccessor - the accessor to be used.
     */
    public void setBidirectionalTargetAccessor(AttributeAccessor anAttributeAccessor) {
        this.bidirectionalTargetAccessor = anAttributeAccessor;
    }
    
    /**
     * Sets the name of the backpointer attribute on the target object. Used to 
     * populate the backpointer. If the specified attribute doesn't exist on 
     * the reference class of this mapping, a DescriptorException will be thrown
     * during initialize.
     * 
     * @param attributeName - the name of the backpointer attribute to be populated
     */
    public void setBidirectionalTargetAttributeName(String attributeName) {
        if(attributeName != null) {
            if(this.bidirectionalTargetAccessor == null) {
                this.bidirectionalTargetAccessor = new InstanceVariableAttributeAccessor();
            }
            this.getBidirectionalTargetAccessor().setAttributeName(attributeName);
        }
    }
    
    /**
     * Gets the name of the backpointer attribute on the target object. Used to 
     * populate the backpointer.
     */    
    public String getBidirectionalTargetAttributeName() {
        if(this.bidirectionalTargetAccessor == null) {
            return null;
        }
        return this.getBidirectionalTargetAccessor().getAttributeName();
    }
    
    /**
     * Sets the method name to be used when accessing the value of the back pointer 
     * on the target object of this mapping. If the specified method doesn't exist
     * on the reference class of this mapping, a DescriptorException will be thrown
     * during initialize.
     * 
     * @param methodName - the getter method to be used.
     */
    public void setBidirectionalTargetGetMethodName(String methodName) {
        if (methodName == null) {
            return;
        }
        
        if(this.bidirectionalTargetAccessor == null) {
            bidirectionalTargetAccessor = new MethodAttributeAccessor();
        }

        // This is done because setting attribute name by defaults create InstanceVariableAttributeAccessor 
        if (!getBidirectionalTargetAccessor().isMethodAttributeAccessor()) {
            String attributeName = this.bidirectionalTargetAccessor.getAttributeName();
            setBidirectionalTargetAccessor(new MethodAttributeAccessor());
            getBidirectionalTargetAccessor().setAttributeName(attributeName);
        }

        ((MethodAttributeAccessor)getBidirectionalTargetAccessor()).setGetMethodName(methodName);     
    }
    
    /**
     * Sets the name of the method to be used when setting the value of the back pointer 
     * on the target object of this mapping. If the specified method doesn't exist
     * on the reference class of this mapping, a DescriptorException will be thrown
     * during initialize.
     * 
     * @param methodName - the setter method to be used.
     */
    public void setBidirectionalTargetSetMethodName(String methodName) {
        if (methodName == null) {
            return;
        }

        if(this.bidirectionalTargetAccessor == null) {
            this.bidirectionalTargetAccessor = new MethodAttributeAccessor();
        }
        // This is done because setting attribute name by defaults create InstanceVariableAttributeAccessor     
        if (!getBidirectionalTargetAccessor().isMethodAttributeAccessor()) {
            String attributeName = this.bidirectionalTargetAccessor.getAttributeName();
            setBidirectionalTargetAccessor(new MethodAttributeAccessor());
            getBidirectionalTargetAccessor().setAttributeName(attributeName);
        }

        ((MethodAttributeAccessor)getBidirectionalTargetAccessor()).setSetMethodName(methodName);
    }    
    
    /**
     * Gets the name of the method to be used when accessing the value of the 
     * back pointer on the target object of this mapping.
     */    
    public String getBidirectionalTargetGetMethodName() {
        if (getBidirectionalTargetAccessor() == null || !getBidirectionalTargetAccessor().isMethodAttributeAccessor()) {
            return null;
        }
        return ((MethodAttributeAccessor)getBidirectionalTargetAccessor()).getGetMethodName();
    }
    
    /**
     * Gets the name of the method to be used when setting the value of the 
     * back pointer on the target object of this mapping.
     */    
    public String getBidirectionalTargetSetMethodName() {
        if (getBidirectionalTargetAccessor() == null || !getBidirectionalTargetAccessor().isMethodAttributeAccessor()) {
            return null;
        }
        return ((MethodAttributeAccessor)getBidirectionalTargetAccessor()).getSetMethodName();
    }  
    
    public ContainerPolicy getBidirectionalTargetContainerPolicy() {
        return this.bidirectionalTargetContainerPolicy;
    }
    
    public void setBidirectionalTargetContainerPolicy(ContainerPolicy cp) {
        this.bidirectionalTargetContainerPolicy = cp;
    }
    
    public void setBidirectionalTargetContainerClass(Class cls) {
        if(this.bidirectionalTargetContainerPolicy == null) {
            this.bidirectionalTargetContainerPolicy = ContainerPolicy.buildPolicyFor(cls);
        } else {
            this.bidirectionalTargetContainerPolicy.setContainerClass(cls);
        }
    }
    
    /**
     * Gets the AttributeAccessor that is used to get and set the value of the
     * container on the target object.
     */
    public AttributeAccessor getBidirectionalTargetAccessor() {
        return this.bidirectionalTargetAccessor;
    }
    
    
}
