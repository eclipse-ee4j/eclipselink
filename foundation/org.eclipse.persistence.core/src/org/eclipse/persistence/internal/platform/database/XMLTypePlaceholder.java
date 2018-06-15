/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.platform.database;

import org.eclipse.persistence.internal.helper.NoConversion;

/**
 * This is a dummy class which is used as a stand in for
 * oracle.xdb.XMLType in class comparisions to prevent exceptions.
 * @author  mmacivor
 * @since TopLink OracleAS TopLink 10<i>g</i> (10.1.3)
 */
public class XMLTypePlaceholder implements NoConversion, Oracle9Specific {
}
