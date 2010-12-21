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
package org.eclipse.persistence.tools.workbench.framework.resources;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * This abstract class can simplify the coding of an IconResourceFileNameMap.
 * Subclasses need only implement #getEntries() to return a two-dimensional
 * array of strings that pairs up icon keys with their associated resource file
 * names. Similar to ListResourceBundle.
 */
public abstract class AbstractIconResourceFileNameMap implements IconResourceFileNameMap {
	/** resource file names, keyed by icon key */
	private final Map resourceFileNames;

	protected AbstractIconResourceFileNameMap() {
		super();
		this.resourceFileNames = this.buildResourceFileNames();
	}

	protected Map buildResourceFileNames() {
		String[][] entries = this.getEntries();
		Map result = new HashMap(entries.length);
		for (int i = 0; i < entries.length; i++) {
			String[] entry = entries[i];
			String key = entry[0];
			String value = entry[1];
			Object previous = result.put(key, value);
			if (previous != null) {
				throw new IllegalStateException(
					"An icon resource file name is already associated with the key \""
					+ key + "\" (previous: \"" + previous + "\"; duplicate: \"" + value
					+ "\"). Duplicates are not allowed.");
			}
		}
		return result;
	}

	protected abstract String[][] getEntries();

    /**
	 * @see IconResourceFileNameMap#hasResourceFileName(String)
	 */
    public boolean hasResourceFileName(String key) {
        return this.resourceFileNames.containsKey(key);
    }

	/**
	 * @see IconResourceFileNameMap#getResourceFileName(String)
	 */
	public String getResourceFileName(String key) {
		String resourceFileName = (String) this.resourceFileNames.get(key);
		if (resourceFileName == null) {
			throw new MissingIconException("There is no icon resource file name associated with the key \"" + key + "\".", key);
		}
		return resourceFileName;
	}
    
	public String toString() {
		return StringTools.buildToStringFor(this, String.valueOf(this.resourceFileNames.size()) + " icons");
	}

}
