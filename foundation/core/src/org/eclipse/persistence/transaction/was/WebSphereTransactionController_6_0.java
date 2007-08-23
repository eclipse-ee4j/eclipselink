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

/**
 * <p>
 * <b>Purpose</b>: TransactionController implementation for WebSphere 6.0
 * <p>
 * <b>Description</b>: Implements the required behaviour for controlling 
 * transactions in WebSphere 6.0. We need this class because 5.1 changed the TransactionManagerFactory
 * package names.
 * <p>
 * @see org.eclipse.persistence.transaction.JTATransactionController
 */
public class WebSphereTransactionController_6_0 extends WebSphereTransactionController_5_1 {
    public WebSphereTransactionController_6_0() {
        super();
    }
}
