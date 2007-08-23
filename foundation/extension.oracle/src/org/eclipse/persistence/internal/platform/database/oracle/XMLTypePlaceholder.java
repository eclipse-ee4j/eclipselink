/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.platform.database.oracle;

import org.eclipse.persistence.internal.helper.NoConversion;

/**
 * This is a dummy class which is used as a stand in for
 * oracle.xdb.XMLType in class comparisions to prevent exceptions.
 * @author  mmacivor
 * @since TopLink OracleAS TopLink 10<i>g</i> (10.1.3)
 */
public class XMLTypePlaceholder implements NoConversion, Oracle9Specific {
}