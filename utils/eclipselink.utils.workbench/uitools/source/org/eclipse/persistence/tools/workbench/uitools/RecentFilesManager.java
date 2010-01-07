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
package org.eclipse.persistence.tools.workbench.uitools;

import java.io.File;

import org.eclipse.persistence.tools.workbench.utility.Model;

/**
 * This interface defines the methods necessary to maintain a
 * list of recently-opened files. All state change listeners will
 * be notified of any changes in the list.
 */
public interface RecentFilesManager extends Model {

	/**
	 * This is the suggested default maximum number
	 * of recently-opened files that should be maintained
	 * by a manager.
	 */
	int DEFAULT_MAX_SIZE = 4;
	
	/**
	 * This is the maximimum maximum number of 
	 * recently-opened files that should be maintained
	 * by a manager.
	 * In other words, the max size should never 
	 * be larger than this.
	 */
	int MAX_MAX_SIZE = 9;

	/**
	 * Return the maximum number of recently-opened files
	 * the manager will maintain.
	 */
	int getMaxSize();

	/**
	 * Set the maximum number of recently-opened files
	 * the manager will maintain. If this causes the list to
	 * change (specifically, if the new maximum is less than
	 * the current size of the list), a StateChange event
	 * will be fired. The maximum must be greater than zero.
	 */
	void setMaxSize(int maxSize);

	/**
	 * Add the specified file to the top of the list of
	 * recently-opened files. If the file is already in the
	 * list, it is moved to the top of the list. If this
	 * causes the list to change, a StateChange event
	 * will be fired.
	 */
	void setMostRecentFile(File file);

	/**
	 * Return the list of recently-opened files, most-recent
	 * files first. If there are no recent files, return an empty array.
	 */
	File[] getRecentFiles();
	
	/**
	 * Remove the given file from the recents
	 */
	void removeRecentFile(File file);
}
