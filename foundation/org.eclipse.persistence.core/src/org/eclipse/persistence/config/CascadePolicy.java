/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.config;

/**
 * <p>Cascade policy hint values.
 *
 * <p>The class contains all the valid values for QueryHints.REFRESH_CASCADE query hint.
 *
 * <p>JPA Query Hint Usage:
 *
 * <p>query.setHint(QueryHints.REFRESH_CASCADE, CascadePolicy.CascadeAllParts);<br>
 * or<br>
 * {@literal @QueryHint(name=QueryHints.REFRESH_CASCADE, value=CascadePolicy.CascadeAllParts)}
 *
 * <p>Hint values are case-insensitive.
 * "" could be used instead of default value CascadePolicy.DEFAULT.
 *
 * @see QueryHints
 */
public class CascadePolicy {
    public static final String NoCascading = "NoCascading";
    public static final String CascadePrivateParts = "CascadePrivateParts";
    public static final String CascadeAllParts = "CascadeAllParts";
    public static final String CascadeByMapping = "CascadeByMapping";

    public static final String DEFAULT = CascadeByMapping;

}
