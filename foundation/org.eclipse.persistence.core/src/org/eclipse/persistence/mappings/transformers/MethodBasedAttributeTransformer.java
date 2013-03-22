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
package org.eclipse.persistence.mappings.transformers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetMethodParameterTypes;
import org.eclipse.persistence.internal.security.PrivilegedMethodInvoker;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;

/**
 *  @version $Header: MethodBasedAttributeTransformer.java 18-sep-2006.14:33:54 gyorke Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 *  This class is used to preserve the old method of doing Attribute Transformations
 *  on a transformation mapping. It is used internally when the older API is used on
 *  a TransformationMapping, and handles doing invocations on the user's domain class
 */
public class MethodBasedAttributeTransformer implements AttributeTransformer {
    protected transient Method attributeTransformationMethod;
    protected AbstractTransformationMapping mapping;
    protected String methodName;

    public MethodBasedAttributeTransformer() {
    }

    public MethodBasedAttributeTransformer(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String name) {
        methodName = name;
    }

    public Method getAttributeTransformationMethod() {
        return attributeTransformationMethod;
    }

    public void setAttributeTransformationMethod(Method theMethod) {
        attributeTransformationMethod = theMethod;
    }

    /**
     * INTERNAL:
     * Initilizes the transformer. Looks up the transformation method on the
     * domain class using reflection. This method can have either 1 or 2 parameters.
     */
    public void initialize(AbstractTransformationMapping mapping) {
        this.mapping = mapping;
        try {
            // look for the one-argument version first
            Class[] parameterTypes = new Class[1];
            parameterTypes[0] = ClassConstants.Record_Class;
            attributeTransformationMethod = Helper.getDeclaredMethod(mapping.getDescriptor().getJavaClass(), methodName, parameterTypes);
        } catch (Exception ex) {
            try {
                //now look for the one-argument version with Record
                Class[] parameterTypes = new Class[1];
                parameterTypes[0] = ClassConstants.Record_Class;
                attributeTransformationMethod = Helper.getDeclaredMethod(mapping.getDescriptor().getJavaClass(), methodName, parameterTypes);
            } catch (Exception ex2) {
                try {
                    // if the one-argument version is not there, look for the two-argument version
                    Class[] parameterTypes = new Class[2];
                    parameterTypes[0] = ClassConstants.Record_Class;
                    parameterTypes[1] = ClassConstants.PublicInterfaceSession_Class;
                    attributeTransformationMethod = Helper.getDeclaredMethod(mapping.getDescriptor().getJavaClass(), methodName, parameterTypes);
                } catch (Exception ex3) {
                    try {
                        //now look for the 2 argument version using Record and sessions Session
                        Class[] parameterTypes = new Class[2];
                        parameterTypes[0] = ClassConstants.Record_Class;
                        parameterTypes[1] = ClassConstants.SessionsSession_Class;
                        attributeTransformationMethod = Helper.getDeclaredMethod(mapping.getDescriptor().getJavaClass(), methodName, parameterTypes);
                    } catch (NoSuchMethodException exception) {
                        throw DescriptorException.noSuchMethodOnInitializingAttributeMethod(mapping.getAttributeMethodName(), mapping, exception);
                    } catch (SecurityException exception) {
                        throw DescriptorException.securityOnInitializingAttributeMethod(mapping.getAttributeMethodName(), mapping, exception);
                    }
                }
            }
        }
        if (attributeTransformationMethod.getReturnType() == ClassConstants.Void_Class) {
            throw DescriptorException.returnTypeInGetAttributeAccessor(methodName, mapping);
        }
    }

    /**
     * INTERNAL:
     * Build the attribute value by invoking the user's transformation method.
     */
    public Object buildAttributeValue(Record record, Object object, Session session) {
        Class[] parameterTypes = null;
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
            try{
                parameterTypes = (Class[])AccessController.doPrivileged(new PrivilegedGetMethodParameterTypes(attributeTransformationMethod));
            }catch (PrivilegedActionException ex){
                throw (RuntimeException)ex.getCause();
            }
        }else{
            parameterTypes = PrivilegedAccessHelper.getMethodParameterTypes(attributeTransformationMethod);
        }
        Object[] parameters = new Object[parameterTypes.length];
        parameters[0] = record;
        if (parameters.length == 2) {
            parameters[1] = session;
        }

        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try{
                    return AccessController.doPrivileged(new PrivilegedMethodInvoker(attributeTransformationMethod, object, parameters));
                }catch (PrivilegedActionException ex){
                    if (ex.getCause() instanceof IllegalArgumentException){
                        throw (IllegalArgumentException) ex.getCause();
                    }
                    if (ex.getCause() instanceof InvocationTargetException){
                        throw (InvocationTargetException) ex.getCause();
                    }
                    throw (RuntimeException) ex.getCause();
                }
            }else {
                return PrivilegedAccessHelper.invokeMethod(attributeTransformationMethod, object, parameters);
            }
        } catch (IllegalAccessException exception) {
            throw DescriptorException.illegalAccessWhileInvokingAttributeMethod(mapping, exception);
        } catch (IllegalArgumentException exception) {
            throw DescriptorException.illegalArgumentWhileInvokingAttributeMethod(mapping, exception);
        } catch (InvocationTargetException exception) {
            throw DescriptorException.targetInvocationWhileInvokingAttributeMethod(mapping, exception);
        }
    }
}
