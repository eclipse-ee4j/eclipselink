/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.jpa.config;

/**
 * PessimisticLock hint values.
 * 
 * The class contains all the valid values for EclipseLinkQueryHints.PESSIMISTIC_LOCK query hint.
 * 
 * <p>JPA Query Hint Usage:
 * 
 * <p><code>query.setHint(EclipseLinkQueryHints.PESSIMISTIC_LOCK, PessimisticLock.Lock);</code>
 * <p>or 
 * <p><code>@QueryHint(name=EclipseLinkQueryHints.PESSIMISTIC_LOCK, value=PessimisticLock.Lock)</code>
 * 
 * <p>Hint values are case-insensitive.
 * "" could be used instead of default value PessimisticLock.DEFAULT.
 * 
 * @see EclipseLinkQueryHints
 */
public class PessimisticLock {
    public static final String  NoLock = "NoLock";
    public static final String  Lock = "Lock";
    public static final String  LockNoWait = "LockNoWait";
 
    public static final String DEFAULT = NoLock;
}
