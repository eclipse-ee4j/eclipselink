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

import javax.swing.Icon;

import org.eclipse.persistence.internal.sessions.factories.model.log.LogConfig;
import org.eclipse.persistence.internal.sessions.factories.model.log.LoggingOptionsConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class LogConfig
 * 
 * @see LogConfig
 * 
 * @author Tran Le
 * @version 1.0a
 */
public abstract class LogAdapter extends SCAdapter {
	// property change
	private volatile LoggingOptionsAdapter loggingOptions;
	public final static String LOG_OPTIONS_PROPERTY = "logOptions";
	public final static String SHOULD_LOG_EXCEPTION_STACK_TRACE_PROPERTY = "shouldLogExceptionStackTrace";
	public final static String SHOULD_PRINT_CONNECTION_PROPERTY = "shouldPrintConnection";
	public final static String SHOULD_PRINT_DATE_PROPERTY = "shouldPrintDate";
	public final static String SHOULD_PRINT_SESSION_PROPERTY = "shouldPrintSession";
	public final static String SHOULD_PRINT_THREAD_PROPERTY = "shouldPrintThread";

	/**
	 * Creates a new LogAdapter for the specified model object.
	 */
	LogAdapter( SCAdapter parent, LogConfig scConfig) {
		super( parent, scConfig);
	}
	/**
	 * Creates a new LogAdapter.
	 */
	protected LogAdapter( SCAdapter parent) {
		
		super( parent);
	}

	public Icon icon() {
		return null;
	}
	/**
	 * Returns this Config Model Object.
	 */
	private final LogConfig logConfig() {
		
		return ( LogConfig)this.getModel();
	}
	
	private LoggingOptionsAdapter buildLoggingOptions() {

	    LoggingOptionsAdapter loggingOptions = new LoggingOptionsAdapter( this);
		this.logConfig().setLoggingOptions(( LoggingOptionsConfig)loggingOptions.getModel());

		return loggingOptions;
	}
	
	private LoggingOptionsAdapter removeLoggingOptions() {

		this.logConfig().setLoggingOptions( null);
		return null;
	}
	
	private LoggingOptionsConfig getLoggingOptionsConfig() {
		
		return this.logConfig().getLoggingOptions();
	}
	/**
	 * Returns this logging adapter.
	 */
	private LoggingOptionsAdapter getLoggingOptions() {
		
		return this.loggingOptions;
	}
	/**
	 * Initializes this new model.
	 */
	protected void initialize( Object newConfig) {
		super.initialize( newConfig);

		this.loggingOptions = null;
	}
	/**
	 * Initializes this adapter from the config model.
	 */
	protected void initializeFromModel( Object scConfig) {
		super.initializeFromModel( scConfig);

		this.loggingOptions = ( LoggingOptionsAdapter)this.adapt( this.getLoggingOptionsConfig());
	}
	/**
	 * Returns true if options are used.
	 */
	public boolean optionsIsEnable() {
		
		return this.loggingOptions != null;
	}
	
	public void enableOptions() {
	    LoggingOptionsAdapter old = this.loggingOptions;
	    
		this.loggingOptions = this.buildLoggingOptions();
		this.firePropertyChanged( LOG_OPTIONS_PROPERTY, old, this.loggingOptions);
	}
	
	public void disableOptions() {
		Object old = this.loggingOptions;

		this.loggingOptions = this.removeLoggingOptions();
		this.firePropertyChanged( LOG_OPTIONS_PROPERTY, old, this.loggingOptions);
	}
	
	public boolean getShouldLogExceptionStackTrace() {
		
		return this.getLoggingOptions().getShouldLogExceptionStackTrace();
	}
	/**
	 * Sets this name and the config model.
	 */
	public void setShouldLogExceptionStackTrace( boolean value) {
		
		boolean old = this.getLoggingOptions().getShouldLogExceptionStackTrace();

		this.getLoggingOptions().setShouldLogExceptionStackTrace( value);
		this.firePropertyChanged( SHOULD_LOG_EXCEPTION_STACK_TRACE_PROPERTY, old, value);
	}

	public boolean getShouldPrintConnection() {
		
		return this.getLoggingOptions().getShouldPrintConnection();
	}
	/**
	 * Sets this name and the config model.
	 */
	public void setShouldPrintConnection( boolean value) {
		
		boolean old = this.getLoggingOptions().getShouldPrintConnection();

		this.getLoggingOptions().setShouldPrintConnection( value);
		this.firePropertyChanged( SHOULD_PRINT_CONNECTION_PROPERTY, old, value);
	}

	public boolean getShouldPrintDate() {
		
		return this.getLoggingOptions().getShouldPrintDate();
	}
	/**
	 * Sets this name and the config model.
	 */
	public void setShouldPrintDate( boolean value) {
		
		boolean old = this.getLoggingOptions().getShouldPrintDate();

		this.getLoggingOptions().setShouldPrintDate( value);
		this.firePropertyChanged( SHOULD_PRINT_DATE_PROPERTY, old, value);
	}

	public boolean getShouldPrintSession() {
		
		return this.getLoggingOptions().getShouldPrintSession();
	}
	/**
	 * Sets this name and the config model.
	 */
	public void setShouldPrintSession( boolean value) {
		
		boolean old = this.getLoggingOptions().getShouldPrintSession();

		this.getLoggingOptions().setShouldPrintSession( value);
		this.firePropertyChanged( SHOULD_PRINT_SESSION_PROPERTY, old, value);
	}

	public boolean getShouldPrintThread() {
		
		return this.getLoggingOptions().getShouldPrintThread();
	}
	/**
	 * Sets this name and the config model.
	 */
	public void setShouldPrintThread( boolean value) {
		
		boolean old = this.getLoggingOptions().getShouldPrintThread();

		this.getLoggingOptions().setShouldPrintThread( value);
		this.firePropertyChanged( SHOULD_PRINT_THREAD_PROPERTY, old, value);
	}
}
