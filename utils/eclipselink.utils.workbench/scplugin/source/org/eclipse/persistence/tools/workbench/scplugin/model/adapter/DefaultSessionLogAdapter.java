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

import java.io.File;
import java.util.List;

import org.eclipse.persistence.tools.workbench.scplugin.SCProblemsConstants;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

import org.eclipse.persistence.internal.sessions.factories.model.log.DefaultSessionLogConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class DefaultSessionLogConfig
 * 
 * @see DefaultSessionLogConfig
 * 
 * @author Tran Le
 */
public final class DefaultSessionLogAdapter extends LogAdapter {
	// property change
	public final static String FILE_NAME_PROPERTY = "filename";
	public final static String LOG_LEVEL_PROPERTY = "logLevel";

	public static final String INFO_LOG_LEVEL = "info";
	public static final String SEVERE_LOG_LEVEL = "severe";
	public static final String WARNING_LOG_LEVEL = "warning";
	public static final String CONFIG_LOG_LEVEL = "config";
	public static final String FINE_LOG_LEVEL = "fine";
	public static final String FINER_LOG_LEVEL = "finer";
	public static final String FINEST_LOG_LEVEL = "finest";
	public static final String ALL_LOG_LEVEL = "all";
	public static final String OFF_LOG_LEVEL = "off";

	public static final String DEFAULT_LOG_LEVEL = INFO_LOG_LEVEL;
	public static final String DEFAULT_LOG_FILE = "standard output";
	public static final String[] VALID_LOG_LEVEL = {
		CONFIG_LOG_LEVEL,
		INFO_LOG_LEVEL,
		WARNING_LOG_LEVEL,
		SEVERE_LOG_LEVEL,
		FINE_LOG_LEVEL,
		FINER_LOG_LEVEL,
		FINEST_LOG_LEVEL,
		ALL_LOG_LEVEL,
		OFF_LOG_LEVEL
	};
	/**
	 * Creates a new DefaultSessionLogAdapter for the specified model object.
	 */
	DefaultSessionLogAdapter( SCAdapter parent, DefaultSessionLogConfig scConfig) {
		
		super( parent, scConfig);
	}
	/**
	 * Creates a new DefaultSessionLogAdapter.
	 */
	protected DefaultSessionLogAdapter( SCAdapter parent, String fileName, String logLevel) {
		
		super( parent);
		
		this.setFileName( fileName);
		this.setLogLevel( logLevel);
	}
	/**
	 * Factory method for building this model.
	 */
	protected Object buildModel() {
		return new DefaultSessionLogConfig();
	}
	/**
	 * Returns this fileName.
	 */
	public String getFileName() {

		String name = this.getFileNameInternal();

		if (name != null)
			name = name.replace('/', File.separatorChar);

		return name;
	}
	/**
	 * Returns this fileName.
	 * Internaly stores null when "standard output"
	 */
	private String getFileNameInternal() {

		String name = this.log().getFilename();

		if (name == null)
			name =  DefaultSessionLogAdapter.DEFAULT_LOG_FILE;

		return name;
	}
	/**
	 * Sets this fileName and the config model.
	 */
	public void setFileName( String name) {
		
		Object old = this.log().getFilename();
		String newName = name;

		if (newName != null)
			newName = newName.replace('\\', '/');

		this.setFilenameInternal( newName);
		this.firePropertyChanged( FILE_NAME_PROPERTY, old, name);
	}
	/**
	 * Sets this fileName and the config model.
	 */
	private void setFilenameInternal( String name) {
		
		if( name != null && name.equals( DefaultSessionLogAdapter.DEFAULT_LOG_FILE))
			this.log().setFilename( null);		    
		else
		    this.log().setFilename( name);
	}
	/**
	 * Returns this logLevel.
	 */
	public String getLogLevel() {
		
		return this.log().getLogLevel();
	}
	/**
	 * Sets this name and the config model.
	 */
	public void setLogLevel( String logLevel) {
		
		Object old = this.getLogLevel();
		
		this.log().setLogLevel( logLevel);
		this.firePropertyChanged( LOG_LEVEL_PROPERTY, old, logLevel);
	}
	
	public void toString( StringBuffer sb) {
		
		sb.append( this.getFileName());
	}
	
	/**
	 * Returns this Config Model Object.
	 */
	private final DefaultSessionLogConfig log() {
		
		return ( DefaultSessionLogConfig)this.getModel();
	}
	/**
	 * Add any problems from this adapter to the given set.
	 */
	protected void addProblemsTo( List branchProblems) {
	
		super.addProblemsTo( branchProblems);
	
		verifyProblemFileName( branchProblems);
	}

	private void verifyProblemFileName( List branchProblems) {

		String fileName = getFileName();

		if( !DEFAULT_LOG_FILE.equals( fileName) && StringTools.stringIsEmpty( fileName)) {
			branchProblems.add( buildProblem( SCProblemsConstants.DEFAULT_LOGGING_FILE_NAME, getParent().displayString()));
		}
	}
}
