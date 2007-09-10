/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.transaction.was;

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
 * <b>Purpose</b>: TransactionController implementation for WebSphere 5.0
 * <p>
 * <b>Description</b>: Implements the required behaviour for controlling transactions
 * in WebSphere 5.0. Since 5.0 provides support for JTA the TransactionManager must
 * be set on the instance.
 * <p>
 * @see org.eclipse.persistence.transaction.JTATransactionController
 */
public class WebSphereTransactionController_5_0 extends JTATransactionController {
    // Class and method to execute to obtain the TransactionManager
    protected final static String TX_MANAGER_FACTORY_CLASS = "com.ibm.ejs.jts.jta.TransactionManagerFactory";
    protected final static String TX_MANAGER_FACTORY_METHOD = "getTransactionManager";

    public WebSphereTransactionController_5_0() {
        super();
    }

    /**
     * INTERNAL:
     * Obtain and return the JTA TransactionManager on this platform.
     * This will be called once when the controller is initialized.
     */
    protected TransactionManager acquireTransactionManager() throws Exception {
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
            try{
                Class clazz = (Class)AccessController.doPrivileged(new PrivilegedClassForName(TX_MANAGER_FACTORY_CLASS));
                Method method = (Method)AccessController.doPrivileged(new PrivilegedGetMethod(clazz, TX_MANAGER_FACTORY_METHOD, null, false));
                return (TransactionManager)AccessController.doPrivileged(new PrivilegedMethodInvoker(method, null, null));
            }catch (PrivilegedActionException ex){
                throw (Exception)ex.getCause();
            }
         } else {
             Class clazz = PrivilegedAccessHelper.getClassForName(TX_MANAGER_FACTORY_CLASS);
             Method method = PrivilegedAccessHelper.getMethod(clazz, TX_MANAGER_FACTORY_METHOD, null, false);
             return (TransactionManager)PrivilegedAccessHelper.invokeMethod(method, null, null);
        }
    }
}