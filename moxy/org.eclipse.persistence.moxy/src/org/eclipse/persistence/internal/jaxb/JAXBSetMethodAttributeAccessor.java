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
 * dmccann - May 29/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.jaxb;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import org.eclipse.persistence.internal.descriptors.MethodAttributeAccessor;
import org.eclipse.persistence.internal.helper.Helper;

/**
 * This accessor should be used when processing a class that uses method access,
 * and a has set method but no get method.  The get method is required during 
 * mapping initialization to determine the type of the set method's input 
 * parameter, and an exception will occur if none is present.  Using this
 * custom method accessor, the input parameter of the set method can be
 * set (as a string) and loaded (using the provided classloader) during
 * the initialization phase.
 * 
 * Two methods are overridden to avoid exceptions due to lack of a get
 * method:
 * 
 *   - initializeAttributes
 *   - getAttributeClass
 *
 */
public class JAXBSetMethodAttributeAccessor extends MethodAttributeAccessor {
    String parameterTypeAsString;
    ClassLoader loader;
    Class attributeClassification = CoreClassConstants.OBJECT;

    /**
     * This constructor sets the set method input parameter type (as string) as
     * well as the classloader that will be used to load the associated class
     * during initialization.
     * 
     * @param parameterTypeAsString
     * @param loader
     */
    public JAXBSetMethodAttributeAccessor(String parameterTypeAsString, ClassLoader loader) {
        this.parameterTypeAsString = parameterTypeAsString;
        this.loader = loader;
    }

    /**
     * Override to avoid exceptions due to lack of get method.
     */
    public void initializeAttributes(Class theJavaClass) throws DescriptorException {
        if (getAttributeName() == null) {
            throw DescriptorException.attributeNameNotSpecified();
        }
        try {
            if (!isWriteOnly()) {
                Class[] parameterTypes = new Class[1];
                parameterTypes[0] = Helper.getClassFromClasseName(parameterTypeAsString, loader);
                attributeClassification = parameterTypes[0];
                setSetMethod(Helper.getDeclaredMethod(theJavaClass, setMethodName, parameterTypes));
            }
        } catch (NoSuchMethodException ex) {
            DescriptorException descriptorException = DescriptorException.noSuchMethodWhileInitializingAttributesInMethodAccessor(getSetMethodName(), getGetMethodName(), theJavaClass.getName());
            descriptorException.setInternalException(ex);
            throw descriptorException;
        }
    }

    /**
     * Return the return type of the method accessor.
     */
    public Class getAttributeClass() {
        return attributeClassification;
    }
}
