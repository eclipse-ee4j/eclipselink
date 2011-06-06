/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * DefaultIconRepository uses a IconResourceFileNameMap to associate
 * an icon key with the resource file containing the icon (typically a .gif).
 * Icons are loaded and cached the first time they are requested; all
 * subsequent requests for loaded icons are fulfilled by the cache.
 */
public class DefaultIconRepository implements IconRepository {

	/**
	 * The the names of the resource files containing
	 * the actual icons. Keyed by icon key.
	 */
	private IconResourceFileNameMap resourceFileNames;

	/**
	 * Cache the icons as they are requested, so we don't have
	 * to fetch them from the files every time. Keyed by icon key.
	 */
	private final Map cache;


	// ********** constructors **********

	/**
	 * Use this constructor when constructing an instance of a
	 * subclass that overrides #getResourceFileNames()
	 */
	public DefaultIconRepository() {
		this(IconResourceFileNameMap.NULL_INSTANCE);
	}

	public DefaultIconRepository(IconResourceFileNameMap resourceFileNames) {
		super();
		if (resourceFileNames == null) {
			throw new NullPointerException();
		}
		this.resourceFileNames = resourceFileNames;
		this.cache = new HashMap();
	}


	// ********** IconRepository implementation **********

 	/**
	 * @see IconRepository#hasIcon(String)
	 */
	public boolean hasIcon(String key) {
		return (key == null) ||
				this.cache.containsKey(key) ||
				this.canLoadIcon(key);
	}

	/**
	 * @see IconRepository#getIcon(String)
	 */
	public synchronized Icon getIcon(String key) {
		// allow clients to request a "non-icon"
		if (key == null) {
			return null;
		}
		// first check the cache
		Icon icon = (Icon) this.cache.get(key);
		if (icon != null) {
			return icon;
		}
		// load the icon from a file and cache it
		icon = this.loadIcon(key);
		this.cache.put(key, icon);
		return icon;
	}


	// ********** internal methods **********

	/**
	 * Load and return the icon corresponding to the specified key.
	 * This method will be called, on demand, once for each key.
	 */
	protected Icon loadIcon(String key) {
		String resourceFileName = this.getResourceFileNames().getResourceFileName(key);

		URL url = this.getClass().getClassLoader().getResource(resourceFileName);
		if (url == null) {
			throw new MissingIconException("Missing icon file: " + key + " => " + resourceFileName, key);
		}

		return new ImageIcon(url);
	}

	/**
	 * Return whether the icon corresponding to the specified key
	 * can be loaded.
	 */
	protected boolean canLoadIcon(String key) {
		String resourceFileName;
		if (this.getResourceFileNames().hasResourceFileName(key)) {
			resourceFileName = this.getResourceFileNames().getResourceFileName(key);
		} else {
			return false;
		}

		return this.getClass().getClassLoader().getResource(resourceFileName) != null;
	}

	protected IconResourceFileNameMap getResourceFileNames() {
		return this.resourceFileNames;
	}

	public String toString() {
		return StringTools.buildToStringFor(this);
	}

}
