/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      tware - initial
package org.eclipse.persistence.jpa.rs.util;

import javax.persistence.EntityManager;


public abstract class TransactionWrapper {

    public abstract void beginTransaction(EntityManager em);

    public abstract void commitTransaction(EntityManager em);

    public abstract void rollbackTransaction(EntityManager em);
}
