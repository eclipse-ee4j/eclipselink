/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.framework.resources;

import javax.swing.KeyStroke;

/**
 * This interface provides a convenient protocol for clients
 * to fetch the accelerator for a given key. The key is typically the
 * same key used to fetch the accelerator's action label from a
 * StringRepository, with ".ACCELERATOR" appended to it.
 * 
 * @see StringRepository
 */
public interface AcceleratorRepository {

    boolean hasAccelerator(String key);
    
	KeyStroke getAccelerator(String key);


	// ********** null implementation **********

	/**
	 * This instance will throw an exception for any non-null key
	 * and return null for any null key.
	 */
	AcceleratorRepository NULL_INSTANCE =
		new AcceleratorRepository() {
            public boolean hasAccelerator(String key) {
                return key == null;
            }
            public KeyStroke getAccelerator(String key) {
				if (key == null) {
					return null;
				}
				throw new MissingAcceleratorException("Missing accelerator: " + key, key);
			}
			public String toString() {
				return "NullAcceleratorRepository";
			}
		};

}
