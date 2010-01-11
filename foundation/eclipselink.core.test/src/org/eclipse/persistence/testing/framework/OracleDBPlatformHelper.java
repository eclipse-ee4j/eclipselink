/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.framework;

import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;
import org.eclipse.persistence.platform.database.OraclePlatform;

/**
 * Used to separate Oracle-Specific tests from the main test framework.  Returns OraclePlatform from all its
 * methods.  Subclasses can override.
 * 
 * @author tware
 *
 */
public class OracleDBPlatformHelper {

	private static OracleDBPlatformHelper singleton;
	
	public static OracleDBPlatformHelper getInstance() {
		
		if (singleton == null) {
			Class helperClass = null;
			
			try {
				helperClass = (Class) new PrivilegedClassForName("org.eclipse.persistence.testing.framework.oracle.OracleDBPlatformHelper").run();
			} catch (ClassNotFoundException cnfe) {
				helperClass = OracleDBPlatformHelper.class;
			}
			try {
				singleton = (OracleDBPlatformHelper) new PrivilegedNewInstanceFromClass(helperClass).run();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				throw new RuntimeException("Helper create failed: " + helperClass);
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				throw new RuntimeException("Helper create failed: " + helperClass);
			}
		}
		return singleton;
	}
	
	public OraclePlatform getOracle8Platform(){
		return new OraclePlatform();
	}
	
	public OraclePlatform getOracle9Platform(){
		return new OraclePlatform();
	}
}
