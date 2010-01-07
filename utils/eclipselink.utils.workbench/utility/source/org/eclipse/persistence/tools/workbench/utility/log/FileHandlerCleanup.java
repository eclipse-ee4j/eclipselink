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
package org.eclipse.persistence.tools.workbench.utility.log;

import java.io.File;
import java.util.logging.FileHandler;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * This runnable will delete a jdk logging file handle's lock file on system exit.
 * This is a work-around to a known issue with the jdk logging system.
 * The bug (4775533) is supposed to be fixed in jdk 1.5 (Tiger).
 */
// TODO remove when we move to jdk 1.5(?) ~bjv
public class FileHandlerCleanup implements Runnable {
	private FileHandler fileHandler;
	private String lockFileName;

	/**
	 * Register a shut down hook for the specified file handler.
	 */
	public static void register(FileHandler fileHandler) {
		Runtime.getRuntime().addShutdownHook(buildFileHandlerCleanupThread(fileHandler));
	}

	/**
	 * Build a thread to be executed at system exit. The thread will
	 * clean up after the specified handler.
	 */
	private static Thread buildFileHandlerCleanupThread(FileHandler fileHandler) {
		return new Thread(new FileHandlerCleanup(fileHandler), "File Handler Cleanup");
	}

	/**
	 * Construct a clean-up runnable for the specified file handler.
	 */
	private FileHandlerCleanup(FileHandler fileHandler) {
		super();
		this.fileHandler = fileHandler;
		this.initialize();
	}

	/**
	 * Hack into the file handler for the lock file name.
	 */
	private void initialize() {
		this.lockFileName = (String) ClassTools.getFieldValue(this.fileHandler, "lockFileName");
	}

	/**
	 * First make sure the file handler is closed...
	 * (It may have already been closed by the LogManager.Cleaner,
	 * but we can't be sure of that since it takes place in different
	 * shutdown thread.)
	 * ...then delete the file handler's lock file.
	 * @see Runnable#run()
	 */
	public void run() {
		this.fileHandler.close();
		new File(this.lockFileName).delete();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return StringTools.buildToStringFor(this, this.lockFileName);
	}

}
