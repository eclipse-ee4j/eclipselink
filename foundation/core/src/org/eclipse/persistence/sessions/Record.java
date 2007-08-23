/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.sessions;

import java.util.Map;

/**
 * This interface defines the public interface for the TopLink DatabaseRecord (was Record),
 * and the other record types XMLRecord, EISRecord.
 * The Map API should be used to access the record data.
 * The data is keyed on the field name, or XPath, and the value is the field data value.
 * @author  mmacivor
 * @since   10.1.3
 */
public interface Record extends Map {
    // Uses Map API.
}