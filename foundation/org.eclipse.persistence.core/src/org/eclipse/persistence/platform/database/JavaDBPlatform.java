/*******************************************************************************
* Copyright 2005 Sun Microsystems,  Inc. All rights reserved.
* This program and the accompanying materials are made available under the 
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
* which accompanies this distribution. 
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at 
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     Oracle - initial API and implementation from Oracle TopLink
*     Sun Microsystems
******************************************************************************/

package org.eclipse.persistence.platform.database;

/**
 * <p><b>Purpose</b>: Allows to use JavaDBPlatform as a synonym for DerbyPlatform
 */
public class JavaDBPlatform extends DerbyPlatform {
    // Do not add any code to this class.
    // JavaDB is the official name of databse bundled with glassfish
    // The only purpose of this class is to allow use of JavaDBPlatform as a
    // synonym for DerbyPlatform
    // All the Derby specific code should be added to DerbyPlatform
}
