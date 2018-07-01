/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2016, 2018 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     03/15/2016 Jody Grassel
//       - 489794: Add support for WebSphere EJBEmbeddable platform.

package org.eclipse.persistence.transaction.was;

public class WebSphereEJBEmbeddableTransactionController extends WebSphereTransactionController {

    // Class and method to execute to obtain the TransactionManager
    private final static String TX_MANAGER_FACTORY_CLASS = "com.ibm.tx.jta.embeddable.EmbeddableTransactionManagerFactory";
    private final static String TX_MANAGER_FACTORY_METHOD = "getTransactionManager";

    protected String getTxManagerFactoryClass() {
        return TX_MANAGER_FACTORY_CLASS;
    }

    protected String getTxManagerFactoryMethod() {
        return TX_MANAGER_FACTORY_METHOD;
    }
}
