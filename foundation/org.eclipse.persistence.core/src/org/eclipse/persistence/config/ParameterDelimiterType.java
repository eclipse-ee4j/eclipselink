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
//     10/29/2010-2.2 Michael O'Brien
//       - 325167: Make reserved # bind parameter char generic to enable native SQL pass through
package org.eclipse.persistence.config;

/**
 * Parameter delimiter char hint values.
 *
 * The class contains the default value for QueryHints.PARAMETER_DELIMITER query hint.
 * A single char should be specified if the default hash symbol needs to be overridden.
 * <p>i.e. "%"
 *
 * JPA Query Hint Usage:
 *
 * <p><code>query.setHint(QueryHints.PARAMETER_DELIMITER, "%");</code>
 * <p>or
 * <p><code>@QueryHint(name=QueryHints.PARAMETER_DELIMITER, value="%")</code>
 *
 * <p>Hint values are case-insensitive.
 * "" cannot be used - and will be replaced with the default value ParameterDelimiterType.DEFAULT
 *
 * @see QueryHints#PARAMETER_DELIMITER
 */
public class ParameterDelimiterType {
    public static final String  Hash = "#";
    public static final String DEFAULT = Hash;
}
