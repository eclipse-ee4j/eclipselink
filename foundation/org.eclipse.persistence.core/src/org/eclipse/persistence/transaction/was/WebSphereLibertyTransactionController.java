/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2014, 2018 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Rick Curtis - Initial implementation.
//     03/15/2016 Jody Grassel
//       - 489794: Add support for WebSphere EJBEmbeddable platform.
package org.eclipse.persistence.transaction.was;

public class WebSphereLibertyTransactionController extends WebSphereTransactionController {
    // Class and method to execute to obtain the TransactionManager
    private final static String TX_MANAGER_FACTORY_CLASS = "com.ibm.tx.jta.TransactionManagerFactory";
    private final static String TX_MANAGER_FACTORY_METHOD = "getTransactionManager";

    @Override
    protected String getTxManagerFactoryClass() {
        return TX_MANAGER_FACTORY_CLASS;
    }

    @Override
    protected String getTxManagerFactoryMethod() {
        return TX_MANAGER_FACTORY_METHOD;
    }
}
