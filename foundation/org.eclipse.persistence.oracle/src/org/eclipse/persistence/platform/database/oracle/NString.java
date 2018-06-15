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
package org.eclipse.persistence.platform.database.oracle;

import org.eclipse.persistence.internal.helper.NoConversion;
import org.eclipse.persistence.internal.platform.database.Oracle9Specific;


/**
 * This class can be used to define the dataType with an ObjectTypeConverter
 * to have EclipseLink bind the object string value as an NCLOB Oracle type.
 */
public class NString implements NoConversion, Oracle9Specific {
}
