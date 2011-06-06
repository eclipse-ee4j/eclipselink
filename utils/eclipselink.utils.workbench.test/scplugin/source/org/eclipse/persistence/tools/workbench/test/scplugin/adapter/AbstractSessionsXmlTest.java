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
package org.eclipse.persistence.tools.workbench.test.scplugin.adapter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.eclipse.persistence.internal.sessions.factories.XMLSessionConfigWriter;
import org.eclipse.persistence.internal.sessions.factories.model.SessionConfigs;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

/**
 * Abstract class for testing Session Config.
 * 
 * @author Tran Le
 */
public abstract class AbstractSessionsXmlTest {

	private Object scConfig;
	private SessionManager sessionManager;
	private File xmlFile;

	/**
	 * Constructor for RootSCAdapter and load the config from file.
	 * @param buildModel 
	 * 				true - creates a new config model
	 * 				false - load config model from file
	 */
	public AbstractSessionsXmlTest( File xmlFile, boolean buildModel) {

		this.initialize( xmlFile);	
		
		if( buildModel) {
			this.initializeFromModel(  this.buildModel());
		}
		else {
			this.initializeFromModel(  this.load());
		}
	}
	/**
	 * Internal: Factory method for building this model.
	 */
	protected abstract Object buildModel();

	/**
	 * Initialize sessions project properties.
	 */
	private void initialize( File xmlFile) {
		
		this.xmlFile = xmlFile;
	
		this.sessionManager = SessionManager.getManager();
	}
	/**
	 * Initializes this model from config.
	 */
	protected void initializeFromModel( Object scConfig) {
		
		this.scConfig = scConfig;
	}	
	
	private SessionConfigs load() {
		
		SessionConfigs topLinkSessions =
				this.sessionManager.getInternalMWConfigObjects( this.getPath().getPath(), this.buildLoader());
		
		return topLinkSessions; 
	}
	
	public File getPath() {
		return this.xmlFile.getAbsoluteFile();
	}
	
	private void setSavePath( File savePath) {

		this.xmlFile = savePath;
	}
	/**
	 * Internal: Returns the adapter's Config Object.
	 */
	protected Object getModel() {
	
		return this.scConfig;
	}
	
	private ClassLoader buildLoader() {

		URLClassLoader loader = new URLClassLoader(new URL[0], getClass().getClassLoader()) {
			public URL findResource(String name) {
				try {
					if (name.equals(getPath().getPath())) {
						return getPath().toURL();
					}
				}
				catch (MalformedURLException e) {
					// Ignore so that super.findResource(String) will be called
				}

				return super.findResource(name);
			}
		};
	
		return loader;
	}
	
	public void save() throws IOException {
		
		FileWriter writer = new FileWriter( this.getPath());
		
		XMLSessionConfigWriter.write(( SessionConfigs)this.getModel(), writer);
		writer.close();

	}
		
	public void save( File file) throws IOException {
		
		this.setSavePath( file);
		this.save();
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		StringTools.buildSimpleToStringOn(this, sb);
		sb.append(" (");
		this.toString(sb);
		sb.append(')');
		return sb.toString();
	}

	public void toString( StringBuffer sb) {
		
		sb.append( "\n\t").append( this.getPath().getName());
	}
}
