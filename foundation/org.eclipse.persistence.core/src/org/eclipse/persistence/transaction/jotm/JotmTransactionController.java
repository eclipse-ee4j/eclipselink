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
 *     James Sutherland - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.transaction.jotm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;

import javax.transaction.TransactionManager;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.security.PrivilegedGetMethod;
import org.eclipse.persistence.internal.security.PrivilegedMethodInvoker;
import org.eclipse.persistence.transaction.JTATransactionController;

/**
 * <p>
 * <b>Purpose</b>: TransactionController implementation for JOTM
 * <p>
 * <b>Description</b>: Implements the required behavior for controlling JTA
 * transactions in JOTM.
 * <p>
 * @see org.eclipse.persistence.transaction.JTATransactionController
 */
public class JotmTransactionController extends JTATransactionController {
    // Class and method to execute to obtain the TransactionManager
    protected final static String TX_CURRENT_FACTORY_CLASS = "org.objectweb.jotm.Current";
    protected final static String TX_CURRENT_FACTORY_METHOD = "getCurrent";
    protected final static String TX_MANAGER_FACTORY_METHOD = "getTransactionManager";

    public JotmTransactionController() {
        super();
    }

    /**
     * INTERNAL:
     * Obtain and return the JTA TransactionManager on this platform
     */
    protected TransactionManager acquireTransactionManager() throws Exception {
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
            try{
                Class clazz = (Class) AccessController.doPrivileged(new PrivilegedClassForName(TX_CURRENT_FACTORY_CLASS));
                Method method = AccessController.doPrivileged(new PrivilegedGetMethod(clazz, TX_CURRENT_FACTORY_METHOD, null, false));
                Method txMethod = AccessController.doPrivileged(new PrivilegedGetMethod(clazz, TX_MANAGER_FACTORY_METHOD, null, false));
                Object current = AccessController.doPrivileged(new PrivilegedMethodInvoker(method, null, null));
                return (TransactionManager) AccessController.doPrivileged(new PrivilegedMethodInvoker(txMethod, current, null));
            }catch (PrivilegedActionException ex){
                if (ex.getCause() instanceof ClassNotFoundException){
                    throw (ClassNotFoundException)ex.getCause();
                }
                if (ex.getCause() instanceof NoSuchMethodException){
                    throw (NoSuchMethodException)ex.getCause();
                }
                if (ex.getCause() instanceof IllegalAccessException){
                    throw (IllegalAccessException)ex.getCause();
                }
                if (ex.getCause() instanceof InvocationTargetException){
                    throw (InvocationTargetException)ex.getCause();
                }
                throw (RuntimeException) ex.getCause();
            }
        }else{
            Class clazz = PrivilegedAccessHelper.getClassForName(TX_CURRENT_FACTORY_CLASS);
            Method method = PrivilegedAccessHelper.getMethod(clazz, TX_CURRENT_FACTORY_METHOD, null, false);
            Method txMethod = PrivilegedAccessHelper.getMethod(clazz, TX_MANAGER_FACTORY_METHOD, null, false);
            Object current = PrivilegedAccessHelper.invokeMethod(method, null, null);
            return (TransactionManager)PrivilegedAccessHelper.invokeMethod(txMethod, current, null);
        }
    }
}
