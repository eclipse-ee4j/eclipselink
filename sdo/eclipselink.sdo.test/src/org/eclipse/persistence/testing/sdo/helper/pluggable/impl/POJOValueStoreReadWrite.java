/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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

/*
   DESCRIPTION
    This class extends the default read/write functionality of POJOValueStore.
    Both read/get/isSet and write/set/unSet function is implemented on the underlying POJO Objects

   PRIVATE CLASSES

   NOTES


   MODIFIED    (MM/DD/YY)
     mfobrien    09/05/06 - Creation
     mfobrien    11/28/06 - a HelperContext instance must be passed in via
                                            custom constructor or the default static context will be used

 */
package org.eclipse.persistence.testing.sdo.helper.pluggable.impl;

import commonj.sdo.helper.HelperContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import org.eclipse.persistence.sdo.SDOConstants;

/**
 * This class extends the default read/write functionality of POJOValueStore.
 * Both read/get/isSet and write/set/unSet function is implemented on the underlying POJO Objects
 */
public class POJOValueStoreReadWrite extends POJOValueStore {
    public POJOValueStoreReadWrite() {
        super();
    }

    // pass in a specific static or instance HelperContext
    public POJOValueStoreReadWrite(HelperContext aContext) {
        super(aContext);
    }

    public POJOValueStoreReadWrite getInstance(HelperContext aContext) {
        return new POJOValueStoreReadWrite(aContext);
    }

    /**
     * Use reflection to get/set on an object
     * Constraints:
     *     The aParamObject must be a POJO or contain a POJO inside a ListWrapper
     * @param isGetMethod
     * @param aReceiverObject
     * @param methodPrefix
     * @param propertyName
     * @param aParamObject
     * @return
     */
    protected Object invokeMethodPrivate(//
    boolean isMany,//
    boolean isGetMethod,//
    Object aReceiverObject,// 
    String methodPrefix,// 
    commonj.sdo.Property property,//
    Object aParamObject) {
        // Note: verify parameters but allow a null aParamObject for set functions
        if (aReceiverObject == null) {
            throw new IllegalArgumentException(ERROR_PLUGGABLE_MSG_OBJECT_NULL);
        }
        if (property == null) {
            throw new IllegalArgumentException(ERROR_PLUGGABLE_MSG_PROPERTY_NAME_NULL);
        }
        if (methodPrefix == null) {
            throw new IllegalArgumentException(ERROR_PLUGGABLE_MSG_PREFIX_NULL);
        }

        Throwable failure;
        Object aReturnObject = null;
        Method method = null;
        try {

            /**
             * Case: default value of a type like id=int is returned wrapped as an Integer
             * get base primitive if method does not exist - search get method[] array first<br>
             * Use reflection and a method pointer to get property value
             * match the prefix name to what your model uses (getName, unSetName, resetName)<br>
             */
            String isManyPostFix = SDOConstants.EMPTY_STRING;

            // TODO: set(null)
            StringBuffer methodName = new StringBuffer(methodPrefix);
            methodName.append(property.getName().substring(0, 1).toUpperCase());
            methodName.append(property.getName().substring(1));
            methodName.append(isManyPostFix);
            StringBuffer getMethodName = new StringBuffer(METHOD_PREFIX_FOR_GET);
            getMethodName.append(property.getName().substring(0, 1).toUpperCase());
            getMethodName.append(property.getName().substring(1));

            Class aReceiverClass = aReceiverObject.getClass();

            // get all methods for search
            Method[] methods = aReceiverClass.getDeclaredMethods();
            Object[] arguments = new Object[] { aParamObject };
            Class aReturnType = null;

            // search get methods for return type - use this for inputArguments
            for (int i = 0; i < methods.length; i++) {
                //System.out.println(getMethodName + ":" + methods[i].getName() + ":" + methods[i].getReturnType().getName());
                if (getMethodName.toString().equals(methods[i].getName())) {
                    //System.out.println(getMethodName + ":" + methods[i].getName() + ":" + methods[i].getReturnType().getName());
                    aReturnType = methods[i].getReturnType();//.getClass(); 
                }
            }

            /*
                        // invoke function based on return type
                        if(!isGetMethod) {
                            Class[] inputArguments = new Class[] { aReturnType };
                            method = aReceiverClass.getDeclaredMethod(methodName.toString(), inputArguments);
                            arguments = new Object[] { aParamObject };
                            aReturnObject = method.invoke(aReceiverObject, arguments);
                        } else {
                            Class[] inputArguments = new Class[] { };
                            method = aReceiverClass.getDeclaredMethod(methodName.toString(), inputArguments);
                            arguments = new Object[] {  };
                            aReturnObject = method.invoke(aReceiverObject, arguments);
                        }
            */

            // Note: parameters cannot be instantiated - they must be inline local
            if (aParamObject != null) {
                // Case: setProperty(Object)
                method = aReceiverClass.getDeclaredMethod(//
                    methodName.toString(),//
                    new Class[] { aParamObject.getClass() });
                arguments = new Object[] { aParamObject };
                aReturnObject = method.invoke(aReceiverObject, arguments);
            } else {
                // Issue: 20060810-1: passing null varargs to set methods using invoke
                // Case: setProperty(Object = null) or getProperty()
                if (!isGetMethod) {
                    Class[] inputArguments = new Class[] {  };
                    method = aReceiverClass.getDeclaredMethod(//
                        methodName.toString(), inputArguments);
                    arguments = new Object[] {  };

                    aReturnObject = method.invoke(aReceiverObject, arguments);

                    //aReturnObject = method.invoke(aReceiverObject, (Object[])null); // ok
                    //aReturnObject = method.invoke(aReceiverObject, null); // ok
                    //aReturnObject = method.invoke(aReceiverObject, new Class[] {}); // ok
                    //aReturnObject = method.invoke(aReceiverObject, (Object[])null);
                    //aReturnObject = method.invoke(aReceiverObject, new Object[] { aParamObject });
                } else {
                    Class[] inputArguments = new Class[] {  };
                    method = aReceiverClass.getDeclaredMethod(//
                        methodName.toString(), inputArguments);
                    aReturnObject = method.invoke(aReceiverObject, (Object[])null);// ok
                }
            }
        } catch (NoSuchMethodException nsme) {
            // TODO: throw or propagate exception
            nsme.printStackTrace();
            failure = nsme;
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
            failure = iae.getCause();
        } catch (IllegalArgumentException iare) {
            iare.printStackTrace();
            failure = iare.getCause();
        } catch (InvocationTargetException ite) {
            ite.printStackTrace();
            failure = ite;
        }

        //throw failure;
        return aReturnObject;
    }
}