
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
package org.eclipse.persistence.transaction.sunas;

import org.eclipse.persistence.transaction.glassfish.GlassfishTransactionController;

/**
 * <p>
 * <b>Purpose</b>: TransactionController implementation for SunAS9 JTA
 * <p>
 * <b>Description</b>: Implements the required behavior for controlling JTA
 * transactions in SunAS9. The JTA TransactionManager must be set on the instance.
 * <p>
 * @see org.eclipse.persistence.transaction.JTATransactionController
 * 
 * @deprecated since 2.5 replaced by GlassfishTransactionController
 */
@Deprecated
public class SunAS9TransactionController extends GlassfishTransactionController {

    public SunAS9TransactionController() {
        super();
    }
}
