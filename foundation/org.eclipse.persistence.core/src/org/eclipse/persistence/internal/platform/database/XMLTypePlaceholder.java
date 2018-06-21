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
