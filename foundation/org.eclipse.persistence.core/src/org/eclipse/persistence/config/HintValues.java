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
package org.eclipse.persistence.config;

/**
 * Hint values.
 * 
 * The class defines boolean values used by some EclipseLink query hints.
 * 
 * <p>JPA Query Hint Usage:
 * 
 * <p><code>query.setHint(QueryHints.REFRESH, HintValues.TRUE);</code>
 * <p>or 
 * <p><code>@QueryHint(name=QueryHints.REFRESH, value=HintValues.TRUE)</code>
 * 
 * <p>Hint values are case-insensitive.
 * 
 * @see QueryHints
 */
public class HintValues {
    public static final String TRUE = "True";
    public static final String FALSE = "False";
    public static final String PERSISTENCE_UNIT_DEFAULT = "PersistenceUnitDefault";
}
