/*******************************************************************************
 * Copyright (c) 2014 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Rick Curtis - Initial implementation.
 ******************************************************************************/
package org.eclipse.persistence.transaction.was;

public class WebSphereLibertyTransactionController extends WebSphereTransactionController {
    // Class and method to execute to obtain the TransactionManager
    protected final static String TX_MANAGER_FACTORY_CLASS = "com.ibm.tx.jta.TransactionManagerFactory";
    protected final static String TX_MANAGER_FACTORY_METHOD = "getTransactionManager";

    protected String getTxManagerFactoryClass() {
        return this.TX_MANAGER_FACTORY_CLASS;
    }

    protected String getTxManagerFactoryMethod() {
        return TX_MANAGER_FACTORY_METHOD;
    }
}