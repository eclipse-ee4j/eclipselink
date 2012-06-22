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
package org.eclipse.persistence.tools.workbench.uitools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

import org.eclipse.persistence.tools.workbench.utility.AbstractModel;

/**
 * This implementation of the RecentFilesManager interface uses the Java
 * Preferences framework to maintain the list of recently-opened files.
 * It stores the list of files in a preferences node as a series of preferences
 * keyed by integer strings, starting with 1 ("1", "2", "3",...).
 * 
 * The largest this can be is 9, the smallest 0.
 */
public final class PreferencesRecentFilesManager
	extends AbstractModel
	implements RecentFilesManager
{
	/**
	 * A node with a list of file names keyed by integers indicating
	 * their order in the list. For example:
	 * 	"1" -> "c:/dev/foo/foo.mwp"
	 * 	"2" -> "c:/dev/bar/bar.mwp"
	 */
	private Preferences recentFilesNode;

	/** Listens for outside changes to the recent files. */
	private PreferenceChangeListener recentFilesListener;

	/** Cache the recent files to improve synchronization. */
	private List recentFiles;

	/**
	 * A node with a preference for the maximum number
	 * of recent files allowed.
	 */
	private Preferences maxSizeNode;

	/** The key to the max size preference. */
	String maxSizeKeyName;

	/** Listens for outside changes to the max size. */
	private PreferenceChangeListener maxSizeListener;

	/** Cache the max size so we can tell when it has changed. */
	private int maxSize;


	// ********** constructors **********

	/**
	 * Construct a manager for the specified recent files and
	 * maximum size preferences settings.
	 */
	public PreferencesRecentFilesManager(Preferences recentFilesNode, Preferences maxSizeNode, String maxSizeKeyName) {
		super();
		this.initialize(recentFilesNode, maxSizeNode, maxSizeKeyName);
	}


	// ********** initialization **********

	/**
	 * Build the listeners.
	 */
	protected void initialize() {
		super.initialize();
		this.recentFilesListener = this.buildRecentFilesListener();
		this.maxSizeListener = this.buildMaxSizeListener();
	}

	private PreferenceChangeListener buildRecentFilesListener() {
		return new PreferenceChangeListener() {
			public void preferenceChange(PreferenceChangeEvent evt) {
				PreferencesRecentFilesManager.this.recentFilesChanged(evt.getKey(), evt.getNewValue());
			}
			public String toString() {
				return "recent files listener";
			}
		};
	}

	private PreferenceChangeListener buildMaxSizeListener() {
		return new PreferenceChangeListener() {
			public void preferenceChange(PreferenceChangeEvent evt) {
				if (evt.getKey().equals(PreferencesRecentFilesManager.this.maxSizeKeyName)) {
					PreferencesRecentFilesManager.this.maxSizeChanged();
				}
			}
			public String toString() {
				return "max size listener";
			}
		};
	}

	/**
	 * Begin listening to the nodes so we can
	 * propagate any events.
	 */
	private void initialize(Preferences recentFilesPreferences, Preferences maxSizePreferences, String maxSizePrefKeyName) {
		this.recentFilesNode = recentFilesPreferences;
		recentFilesPreferences.addPreferenceChangeListener(this.recentFilesListener);

		this.maxSizeNode = maxSizePreferences;
		this.maxSizeKeyName = maxSizePrefKeyName;
		maxSizePreferences.addPreferenceChangeListener(this.maxSizeListener);

		this.maxSize = this.buildMaxSize();
		this.checkMaxSize(this.maxSize);
		this.recentFiles = this.buildRecentFiles();
	}


	// ********** RecentFilesManager implementation **********

	/**
	 * @see org.eclipse.persistence.tools.workbench.uitools.RecentFilesManager#getMaxSize()
	 */
	public synchronized int getMaxSize() {
		return this.maxSize;
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.uitools.RecentFilesManager#setMaxSize(int)
	 */
	public synchronized void setMaxSize(int maxSize) {
		if (this.maxSize == maxSize) {
			return;	// no change
		}
		this.checkMaxSize(maxSize);
		this.maxSize = maxSize;

		// rebuild the recent files list and see whether it has changed
		List oldRecentFiles = this.recentFiles;
		this.recentFiles = this.buildRecentFiles();
		if ( ! oldRecentFiles.equals(this.recentFiles)) {
			this.fireStateChanged();
		}

		// forward the change to the preferences node
		this.maxSizeNode.putInt(this.maxSizeKeyName, maxSize);
	}

	/**
	 * @see RecentFilesManager#getRecentFiles()
	 */
	public synchronized File[] getRecentFiles() {
		return (File[]) this.recentFiles.toArray(new File[this.recentFiles.size()]);
	}

	/**
	 * @see RecentFilesManager#setMostRecentFile(java.io.File)
	 */
	public synchronized void setMostRecentFile(File file) {
		int index = this.recentFiles.indexOf(file);
		if (index == 0) {
			return;	// the file is already at the top of the list
		}

		if (index == -1) {
			// the file is a new addition to the list
			this.recentFiles.add(0, file);
			if (this.recentFiles.size() > this.maxSize) {
				// remove extra files
				while (this.recentFiles.size() > this.maxSize) {
					this.recentFiles.remove(this.recentFiles.size() - 1);
				}
			}
			this.fireStateChanged();
			// replace all the preferences
			this.replacePreferences(this.recentFiles);
		} else {
			// the file is already on the list - move it to the top
			this.recentFiles.remove(index);
			this.recentFiles.add(0, file);
			this.fireStateChanged();
			// replace only the preferences up to where the moved file was originally located
			this.replacePreferences(this.recentFiles.subList(0, index + 1));
		}
	}

	public synchronized void removeRecentFile(File file) {
		int index = this.recentFiles.indexOf(file);
		if (index == -1) {
			return;// the file is not in the list
		}
		this.recentFiles.remove(index);
		this.fireStateChanged();
		this.recentFilesNode.remove(Integer.toString(this.recentFiles.size() + 1)); // Always remove the last entry
		this.replacePreferences(this.recentFiles); 
	}
	
	// ********** internal methods **********

	/**
	 * Get the max size from the appropriate preference.
	 */
	private int buildMaxSize() {
		return this.maxSizeNode.getInt(this.maxSizeKeyName, DEFAULT_MAX_SIZE);
	}

	/**
	 * The maximum size must be greater than zero.
	 */
	private void checkMaxSize(int size) {
		if (size < 0) {
			throw new IllegalArgumentException("the maximum size must not be negative");
		}
		else if (size > MAX_MAX_SIZE) {
			throw new IllegalArgumentException("the maximum size must be less than " + MAX_MAX_SIZE);
		}
	}

	/**
	 * Get the recent files list from the preferences node.
	 */
	private List buildRecentFiles() {
		List result = new ArrayList(this.maxSize);
		int count = 1;
		String fileName = this.recentFilesNode.get(Integer.toString(count), null);
		while ((fileName != null) && (count <= this.maxSize)) {
			result.add(new File(fileName));
			count++;
			fileName = this.recentFilesNode.get(Integer.toString(count), null);
		}
		return result;
	}

	/**
	 * Replace the preferences with the entries in the
	 * specified list.
	 */
	private void replacePreferences(List files) {
		for (int i = 0; i < files.size(); i++) {
			// preference "indexes" are 1-based
			this.recentFilesNode.put(Integer.toString(i + 1), ((File) files.get(i)).getPath());
		}
	}

	/**
	 * The underlying recent files list changed; either because we
	 * changed it in #replacePreferences(List) or a third-party changed
	 * it. If this is called because of our own change, nothing will
	 * happen because the old and new files are the same.
	 */
	synchronized void recentFilesChanged(String key, String newValue) {
		int count = 0;
		try {
			count = Integer.parseInt(key);
		} catch (NumberFormatException ex) {
			return;		// non-integer key - ignore
		}

		if ( ! Integer.toString(count).equals(key)) {
			return;		// "unnormalized" key (e.g. "007") - ignore
		}

		if ((count < 1) || (count > this.recentFiles.size() + 1) || (count > this.maxSize)) {
			return;		// out of range key - ignore
		}

		int index = count - 1;
		if (newValue == null) {
			// a file was removed by a third-party - truncate the list
			while (this.recentFiles.size() > index) {
				this.recentFiles.remove(this.recentFiles.size() - 1);
			}
			this.fireStateChanged();
			return;
		}

		File newFile = new File(newValue);
		if (index == this.recentFiles.size()) {
			// a file was added to the end by a third-party and we are still under our max size;
			this.recentFiles.add(newFile);
			this.fireStateChanged();
			return;
		}

		if (newFile.equals(this.recentFiles.get(index))) {
			return;		// same file - probably a result of #replacePreferences(List)
		}

		// one of the existing files was changed by a third-party
		this.recentFiles.set(index, newFile);
		this.fireStateChanged();
	}

	/**
	 * The underlying max size preference changed; either because
	 * we changed it in #setMaxSize(int) or a third-party changed it.
	 * If this is called because of our own change, nothing will
	 * happen because the old and new values are the same.
	 */
	synchronized void maxSizeChanged() {
		int newMaxSize = this.buildMaxSize();
		if (this.maxSize == newMaxSize) {
			return;	// no change
		}
		this.checkMaxSize(newMaxSize);
		this.maxSize = newMaxSize;

		// rebuild the recent files list and see whether it has changed
		List oldRecentFiles = this.recentFiles;
		this.recentFiles = this.buildRecentFiles();
		if ( ! oldRecentFiles.equals(this.recentFiles)) {
			this.fireStateChanged();
		}
	}

}
