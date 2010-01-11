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
package org.eclipse.persistence.tools.workbench.scplugin.model.adapter;

import org.eclipse.persistence.internal.sessions.factories.model.log.LoggingOptionsConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class LoggingOptionsConfig
 * 
 * @see LoggingOptionsConfig
 * 
 * @author Tran Le
 */
public class LoggingOptionsAdapter extends SCAdapter {

	/**
	 * Creates a new Discovery for the specified model object.
	 */
    LoggingOptionsAdapter( SCAdapter parent, LoggingOptionsConfig scConfig) {
		
		super( parent, scConfig);
	}
	/**
	 * Creates a new Discovery.
	 */
	protected LoggingOptionsAdapter( SCAdapter parent) {
		
		super( parent);
	}
	/**
	 * Returns this Config Model Object.
	 */
	private final LoggingOptionsConfig config() {
		
		return ( LoggingOptionsConfig)this.getModel();
	}
	protected void initializeDefaults() {

		super.initializeDefaults();

		setShouldLogExceptionStackTrace(false);
		setShouldPrintConnection(true);
		setShouldPrintDate(true);
		setShouldPrintSession(true);
		setShouldPrintThread(false);
	}
	/**
	 * Factory method for building this model.
	 */
	protected Object buildModel() {
		return new LoggingOptionsConfig();
	}
	/**
	 * Returns this config model property..
	 */
	boolean getShouldLogExceptionStackTrace() {
		
		return this.config().getShouldLogExceptionStackTrace().booleanValue();
	}
	/**
	 * Sets this config model property.
	 */
	void setShouldLogExceptionStackTrace( boolean value) {
		
		this.config().setShouldLogExceptionStackTrace( Boolean.valueOf( value));
	}
	/**
	 * Returns this config model property..
	 */
	boolean getShouldPrintConnection() {
		
		return this.config().getShouldPrintConnection().booleanValue();
	}
	/**
	 * Sets this config model property.
	 */
	void setShouldPrintConnection( boolean value) {
		
		this.config().setShouldPrintConnection( Boolean.valueOf( value));
	}
	/**
	 * Returns this config model property..
	 */
	boolean getShouldPrintDate() {
		
		return this.config().getShouldPrintDate().booleanValue();
	}
	/**
	 * Sets this config model property.
	 */
	void setShouldPrintDate( boolean value) {
		
		this.config().setShouldPrintDate( Boolean.valueOf( value));
	}
	/**
	 * Returns this config model property..
	 */
	boolean getShouldPrintSession() {
		
		return this.config().getShouldPrintSession().booleanValue();
	}
	/**
	 * Sets this config model property.
	 */
	void setShouldPrintSession( boolean value) {
		
		this.config().setShouldPrintSession( Boolean.valueOf( value));
	}
	/**
	 * Returns this config model property..
	 */
	boolean getShouldPrintThread() {
		
		return this.config().getShouldPrintThread().booleanValue();
	}
	/**
	 * Sets this config model property.
	 */
	void setShouldPrintThread( boolean value) {
		
		this.config().setShouldPrintThread( Boolean.valueOf( value));
	}
}
