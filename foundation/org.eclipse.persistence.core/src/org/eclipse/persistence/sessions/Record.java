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
package org.eclipse.persistence.sessions;

import java.util.Map;

/**
 * This interface defines the public interface for the EclipseLink DatabaseRecord (was Record),
 * and the other record types XMLRecord, EISRecord.
 * The Map API should be used to access the record data.
 * The data is keyed on the field name, or XPath, and the value is the field data value.
 * @author  mmacivor
 * @since   10.1.3
 */
public interface Record extends Map {
    // Uses Map API.
}
