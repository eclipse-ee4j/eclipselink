/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  

/*
   DESCRIPTION
    This class extends the default functionality of POJOValueStore but implements only
    read/get methods from the underlying POJO objects.

   PRIVATE CLASSES

   NOTES


   MODIFIED    (MM/DD/YY)
     mfobrien    09/05/06 - Creation
                                           - split POJO read/write functionality among subclasses
     mfobrien    11/28/06 - a HelperContext instance must be passed in via
                                            custom constructor or the default static context will be used

 */
package org.eclipse.persistence.testing.sdo.helper.pluggable.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.persistence.sdo.DefaultValueStore;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.ValueStore;
import commonj.sdo.DataObject;
import commonj.sdo.helper.HelperContext;

/**
 * This class extends the default functionality of POJOValueStore but implements only
 * read/get methods from the underlying POJO objects.
 */
public class POJOValueStoreReadOnly extends POJOValueStore {
    public POJOValueStoreReadOnly() {
        super();
    }

    // pass in a specific static or instance HelperContext
    public POJOValueStoreReadOnly(HelperContext aContext) {
        super(aContext);
    }

    public POJOValueStoreReadOnly getInstance(HelperContext aContext) {
        return new POJOValueStoreReadOnly(aContext);
    }

    /**
     * Do not use Reflection to update the POJO, invoke only get() functions
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
            if (isGetMethod) {
                String isManyPostFix = SDOConstants.EMPTY_STRING;

                // TODO: set(null)
                StringBuffer methodName = new StringBuffer(methodPrefix);
                methodName.append(property.getName().substring(0, 1).toUpperCase());
                methodName.append(property.getName().substring(1));
                methodName.append(isManyPostFix);
                Class aReceiverClass = aReceiverObject.getClass();

                Class[] inputArguments = new Class[] {  };
                method = aReceiverClass.getDeclaredMethod(//
                    methodName.toString(), inputArguments);
                aReturnObject = method.invoke(aReceiverObject, (Object[])null);// ok
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