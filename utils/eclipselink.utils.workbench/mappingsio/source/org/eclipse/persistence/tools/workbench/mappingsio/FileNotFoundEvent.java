/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsio;

import java.io.File;
import java.util.EventObject;

/**
 * A "FileNotFound" event gets delivered whenever an "expected" file is not found.
 */
public class FileNotFoundEvent extends EventObject {
	/** the missing file */
	private File missingFile;

	public FileNotFoundEvent(Object source, File missingFile) {
		super(source);
		this.missingFile = missingFile;
	}

	public File getMissingFile() {
		return this.missingFile;
	}

}
