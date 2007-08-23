/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.platform.database.oracle;

import org.eclipse.persistence.internal.helper.NoConversion;
import org.eclipse.persistence.internal.platform.database.oracle.Oracle9Specific;


/**
 * This class can be used to define the dataType with an ObjectTypeConverter
 * to have TopLink bind the object string value as an NCLOB Oracle type.
 */
public class NClob implements NoConversion, Oracle9Specific {
}