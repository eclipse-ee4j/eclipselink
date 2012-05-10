/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

import java.util.EventListener;

/**
 * A "FileNotFound" event is fired whenever an "expected" file is not found:
 *     - during a read, a file that is referenced by one of the sub-component
 *       containers is not found (the expected object is simply not added to
 *       the container)
 *     - during a write, a file that existed during the previous read or write and
 *       has been marked for deletion no longer exists (this *should* not cause
 *       a problem but we might want to notify the user)
 */
public interface FileNotFoundListener extends EventListener {

	/**
	 * This method gets called when an "expected" file is not found.
	 * 
	 * @param e  A FileNotFoundEvent object describing the
	 * "expected" file.
	 */
	void fileNotFound(FileNotFoundEvent e);


	FileNotFoundListener NULL_INSTANCE =
		new FileNotFoundListener() {
			public void fileNotFound(FileNotFoundEvent e) {
				// do nothing
			}
			public String toString() {
				return "NullFileNotFoundListener";
			}
		};

}
