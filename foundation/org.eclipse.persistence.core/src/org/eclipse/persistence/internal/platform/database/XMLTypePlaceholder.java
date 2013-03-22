/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
