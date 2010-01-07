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
package org.eclipse.persistence.tools.workbench.mappingsio;

/**
 * This interface defines a callback object that checks whether a project
 * reader should continue with the reading of a legacy project.
 */
public interface LegacyProjectReadCallback {

	/**
	 * Check whether the Project I/O Manager (Project Reader) should
	 * continue with the reading of a project of the specified version.
	 * Throw a client-determined runtime exception if the project should
	 * not be read.
	 */
	void checkLegacyRead(String schemaVersion);


	LegacyProjectReadCallback NULL_INSTANCE =
		new LegacyProjectReadCallback() {
			public void checkLegacyRead(String schemaVersion) {
				// do nothing - allowing the legacy project read to continue
			}
			public String toString() {
				return "NullLegacyProjectReadCallback";
			}
		};

}
