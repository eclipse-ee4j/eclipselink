/*******************************************************************************
 * Copyright (c) 1998, 2015 Oracle, IBM Corporation and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     Rick Curtis -- Refactor to facilitate adding WebSphereLibertyTransactionController.
 ******************************************************************************/  
package org.eclipse.persistence.transaction.was;

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
 * <b>Purpose</b>: TransactionController implementation for WebSphere
 * <p>
 * <b>Description</b>: Implements the required behavior for controlling transactions
 * in WebSphere
 *
 * @see org.eclipse.persistence.transaction.JTATransactionController
 */
public class WebSphereTransactionController extends JTATransactionController {
    // Class and method to execute to obtain the TransactionManager
    protected final static String TX_MANAGER_FACTORY_CLASS = "com.ibm.ws.Transaction.TransactionManagerFactory";
    protected final static String TX_MANAGER_FACTORY_METHOD = "getTransactionManager";

    public WebSphereTransactionController() {
        super();
    }

    protected String getTxManagerFactoryClass() {
        return TX_MANAGER_FACTORY_CLASS;
    }

    protected String getTxManagerFactoryMethod() {
        return TX_MANAGER_FACTORY_METHOD;
    }
        
    /**
     * INTERNAL:
     * Obtain and return the JTA TransactionManager on this platform.
     * This will be called once when the controller is initialized.
     */
    protected TransactionManager acquireTransactionManager() throws Exception {
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
            try{
                Class clazz = AccessController.doPrivileged(new PrivilegedClassForName(getTxManagerFactoryClass()));
                Method method = AccessController.doPrivileged(new PrivilegedGetMethod(clazz, getTxManagerFactoryMethod(), null, false));
                return (TransactionManager) AccessController.doPrivileged(new PrivilegedMethodInvoker(method, null, null));
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
            Class clazz = PrivilegedAccessHelper.getClassForName(getTxManagerFactoryClass());
            Method method = PrivilegedAccessHelper.getMethod(clazz, getTxManagerFactoryMethod(), null, false);
            return (TransactionManager)PrivilegedAccessHelper.invokeMethod(method, null, null);
        }
    }
}
