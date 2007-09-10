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
 * Hint values.
 * 
 * The class defines boolean values used by some TopLink query hint.
 * 
 * <p>JPA Query Hint Usage:
 * 
 * <p><code>query.setHint(EclipseLinkQueryHints.REFRESH, HintValues.TRUE);</code>
 * <p>or 
 * <p><code>@QueryHint(name=EclipseLinkQueryHints.REFRESH, value=HintValues.TRUE)</code>
 * 
 * <p>Hint values are case-insensitive.
 * 
 * @see EclipseLinkQueryHints
 */
public class HintValues {
    public static final String TRUE = "True";
    public static final String FALSE = "False";
    public static final String PERSISTENCE_UNIT_DEFAULT = "PersistenceUnitDefault";
}
