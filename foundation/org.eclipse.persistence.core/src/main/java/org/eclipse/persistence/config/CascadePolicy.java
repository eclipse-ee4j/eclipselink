/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
