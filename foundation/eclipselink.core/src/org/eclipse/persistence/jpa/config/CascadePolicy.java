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
 * Cascade policy hint values.
 * 
 * The class contains all the valid values for EclipseLinkQueryHints.REFRESH_CASCADE query hint.
 * 
 * JPA Query Hint Usage:
 * 
 * query.setHint(EclipseLinkQueryHints.REFRESH_CASCADE, CascadePolicy.CascadeAllParts);
 * or 
 * @QueryHint(name=EclipseLinkQueryHints.REFRESH_CASCADE, value=CascadePolicy.CascadeAllParts)
 * 
 * Hint values are case-insensitive.
 * "" could be used instead of default value CascadePolicy.DEFAULT.
 * 
 * @see EclipseLinkQueryHints
 */
public class CascadePolicy {
    public static final String NoCascading = "NoCascading";
    public static final String CascadePrivateParts = "CascadePrivateParts";
    public static final String CascadeAllParts = "CascadeAllParts";
    public static final String CascadeByMapping = "CascadeByMapping";
 
    public static final String DEFAULT = CascadeByMapping;

}
