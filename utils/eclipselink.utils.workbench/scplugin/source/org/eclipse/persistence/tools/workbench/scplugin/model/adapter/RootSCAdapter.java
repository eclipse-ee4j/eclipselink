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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.Preferences;

import org.eclipse.persistence.tools.workbench.scplugin.model.meta.ClassRepository;
import org.eclipse.persistence.tools.workbench.scplugin.model.meta.SCSessionsProperties;
import org.eclipse.persistence.tools.workbench.utility.events.ChangeNotifier;
import org.eclipse.persistence.tools.workbench.utility.events.DefaultChangeNotifier;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;

import org.eclipse.persistence.internal.sessions.factories.XMLSessionConfigWriter;
import org.eclipse.persistence.internal.sessions.factories.model.SessionConfigs;
import org.eclipse.persistence.sessions.factories.SessionManager;

/**
 * Abstract class for root Session Config adapter.
 * 
 * @author Tran Le
 */
abstract class RootSCAdapter extends SCAdapter implements Nominative {

	// property change
	private volatile String name;
	private volatile SessionManager sessionManager;
	private volatile SCSessionsProperties properties;
	private volatile Preferences preferences;
	private volatile File savePath;
	public final static String SAVE_PATH_PROPERTY = "configSavePath";

	/** used to notifier listeners of changes */
	private ChangeNotifier changeNotifier;

	/** used to generate problems */
	private volatile Validator validator;

	/**
	 * Constructor for RootSCAdapter and load the config from file.
	 * @param buildModel 
	 * 				true - creates a new config model
	 * 				false - load config model from file
	 */
	RootSCAdapter( SCSessionsProperties properties, Preferences preferences, boolean buildModel) {
		super();
		this.initialize( properties, preferences);
		
		if( buildModel) {
			this.initialize(  this.buildModel());
			this.initializeDefaults();
			
			this.setConfigClean( true);
		}
		else {
			this.initializeFromModel(  this.load());
			
			this.markEntireBranchClean();
			this.postInitializationFromModel();
			this.setConfigClean( false);	// contains user's config
		}
	}
	/**
	 * Subclasses should override this method to add their children
	 * to the specified collection.
	 * @see #children()
	 */
	protected void addChildrenTo(List children) {

		super.addChildrenTo(children);
		children.add(getClassRepository());
	}
	/**
	 * Initializes session manager.
	 */
	protected void initialize() {
		super.initialize();
	
		this.sessionManager = SessionManager.getManager();		
	}
	/**
	 * Initialize sessions project properties.
	 */
	private void initialize( SCSessionsProperties properties, Preferences preferences) {
		
		this.changeNotifier = DefaultChangeNotifier.instance();
		validator = NULL_VALIDATOR;
		this.properties = properties;
		this.preferences = preferences;
		this.savePath = properties.getPath();
		setName( this.savePath.getName());
		properties.getClassRepository().setParent(this);
	}
	
	private SessionConfigs load() {
		
		SessionConfigs topLinkSessions =
		    this.sessionManager.getInternalMWConfigObjects( getPath().getPath(), buildLoader(), false);
		
		return topLinkSessions; 
	}
	
	private URL[] buildEntries() {
		
		Iterator iter = getClassRepository().classpathEntries();
		URL[] entries = new URL[ getClassRepository().classpathEntriesSize()];
		int index = 0;

		while( iter.hasNext()) {
			try {
				URL url = new URL( "file", "", ( String)iter.next());
				entries[ index++] = url;
			}
			catch( MalformedURLException e) {
				// Can skip an invalid entry, which should in theory never happen
			}
		}
		return entries;
	}
	
	private ClassLoader buildLoader() {

		URLClassLoader loader = new URLClassLoader( buildEntries(), getClass().getClassLoader()) {
			public URL findResource( String name) {
				try {
					if( name.equals( getPath().getPath())) {
						return getPath().toURL();
					}
				}
				catch( MalformedURLException e) {
					// Ignore so that super.findResource(String) will be called
				}
				URL url = super.findResource(name);

				if( url != null)
					return url;

				// Use this class class loader
				return getClass().getResource(name);
			}
		};
		return loader;
	}
	
	public void save( File file) throws IOException {
		
		this.setSavePath( file);
		this.save();
	}
	
	public void save() throws IOException {
        
	    this.preSaving();
    	
    	XMLSessionConfigWriter.write((SessionConfigs)this.getModel(), this.getPath().toString());
    	
	    this.postSaving();

    	// Mark the whole tree as clean
    	markEntireBranchClean();
    }
	    
	public String displayString() {
		return this.name;
	}
	
	public File getPath() {
		return this.properties.getPath();
	}

	public SCSessionsProperties getProperties() {

		return properties;
	}
	
	/**
	 * Returns the Preferences node used by the SC.
	 */
	Preferences preferences() {

		return preferences;
	}

	/**
	 * Returns the <code>ClassRepository</code> that should be used by the
	 * sessions.xml.
	 *
	 * @return The repository for classpath entries and classes
	 */
	public ClassRepository getClassRepository() {
		return this.properties.getClassRepository();
	}

	public File getSaveDirectory() {
		return this.properties.getPath().getParentFile();
	}

	/**
	 * if the save directory changes, we mark the entire
	 * project dirty so it is written out in the new directory
	 */
	private void setSavePath( File savePath) {

		Object old = this.getSavePath();

		this.savePath = savePath;
		this.properties.pathChanged( savePath);
		this.firePropertyChanged( SAVE_PATH_PROPERTY, old, savePath);
		this.setName(savePath.getName());
	}
	
	private File getSavePath() {
		return savePath;
	}

	private static final String CR = System.getProperty("line.separator");
	public void toString( StringBuffer sb) {
		
		sb.append( CR).append( "\t").append( this.displayString() + "( " + this.getConfigFileVersion()+ " )");
	}

	/**
	 * Returns this config model property.
	 */
	public String getName() {
		
		return this.name;
	}
	/**
	 * Sets this config model property.
	 */
	private void setName( String name) {
		
		Object old = this.getName();

		this.name = FileTools.stripExtension( name);
		this.firePropertyChanged( NAME_PROPERTY, old, name);
	}

	/**
	 * as the root node, we must implement this method
	 */
	public ChangeNotifier getChangeNotifier() {
		return this.changeNotifier;
	}

	/**
	 * allow clients to install another change notifier
	 */
	public void setChangeNotifier(ChangeNotifier changeNotifier) {
		this.changeNotifier = changeNotifier;
	}

	/**
	 * as the root node, we must implement this method
	 */
	public Validator getValidator() {
		return validator;
	}

	/**
	 * allow clients to install an active validator
	 */
	public void setValidator(Validator validator) {
		this.validator = validator;
	}

	/**
	 * Mark the object and all its descendants as clean. 
	 * N/A to Root objects.
	 */
	protected void markEntireConfigurationClean() {
	    return;
	}
}
