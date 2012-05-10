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
package org.eclipse.persistence.tools.workbench.utility.log;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.ExceptionListener;


/**
 * This exception listener logs any broadcast exceptions to a jdk logger.
 */
public class LoggingExceptionListener
	implements ExceptionListener
{
	private final Logger logger;
	private final Level level;
	private final String message;


	// ********** constructors **********

	/**
	 * Construct a listener that logs exceptions to the specified logger
	 * at the specified level with the specified message.
	 */
	public LoggingExceptionListener(Logger logger, Level level, String message) {
		super();
		this.logger = logger;
		this.level = level;
		this.message = message;
	}

	/**
	 * Construct a listener that logs exceptions to the specified logger
	 * at the specified level with the generic message "Unexpected Exception".
	 */
	public LoggingExceptionListener(Logger logger, Level level) {
		this(logger, level, "Unexpected Exception");
	}

	/**
	 * Construct a listener that logs exceptions to the specified logger
	 * at the SEVERE level with the generic message "Unexpected Exception".
	 */
	public LoggingExceptionListener(Logger logger) {
		this(logger, Level.SEVERE);
	}

	/**
	 * Construct a listener that logs exceptions to the root logger
	 * at the SEVERE level with the generic message "Unexpected Exception".
	 */
	public LoggingExceptionListener() {
		this(Logger.getLogger(null));
	}


	// ********** ExceptionListener implementation **********

	/**
	 * We need to do all this because Logger#log(LogRecord) does not pass through
	 * Logger#doLog(LogRecord) like all the other Logger#log(...) methods.
	 * @see ExceptionListener#exceptionThrown(Thread, Throwable)
	 */
	public void exceptionThrown(Thread thread, Throwable exception) {
		LogRecord logRecord = new LogRecord(this.level, this.message);
		logRecord.setParameters(new Object[] { (thread == null) ? "null" : thread.getName() });
		logRecord.setThrown(exception);
		logRecord.setLoggerName(this.logger.getName());
		logRecord.setResourceBundle(this.logger.getResourceBundle());
		this.logger.log(logRecord);
	}


	// ********** standard methods **********

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return ClassTools.shortClassNameForObject(this);
	}

}
