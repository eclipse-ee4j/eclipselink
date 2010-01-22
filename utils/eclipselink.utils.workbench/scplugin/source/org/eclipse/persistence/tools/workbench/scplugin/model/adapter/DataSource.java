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

import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatformRepository;
import org.eclipse.persistence.tools.workbench.scplugin.SCPlugin;
import org.eclipse.persistence.tools.workbench.scplugin.model.EisPlatformManager;


/**
 * 
 * @author Tran Le
 */
public class DataSource {

    private static final int DATABASE_PLATFORM_ID = 1;
    private static final int EIS_PLATFORM_ID = 2;
    private static final int XML_PLATFORM_ID = 3;
    private static final int DEFAULT_PLATFORM_ID = 4;

	private int platformId;
	private String platformName;
	private String platformClassName;

	private DataSource() {
	    
        this.platformClassName = null;
	}
	/**
	 * Constructor for a Database DataSource.
	 */
	public DataSource( DatabasePlatform platform) {

		initializeDbDs( platform);
	}
	/**
	 * Constructor for a EIS DataSource.
	 */
	public DataSource( String shortClassName) {

		initializeEisDs( shortClassName);
	}
	/**
	 * Factory method to build a XML DataSource.
	 */
	static public DataSource buildXmlDataSource() {
	    DataSource ds = new DataSource();
		
	    ds.initializeXmlDs();
		return ds;
	}
	/**
	 * Helper method to get the runtime class name.
	 */
	static public String getClassNameForDatabasePlatform( DatabasePlatform platform) {
	    
	    return platform.getRuntimePlatformClassName();
	}
	/**
	 * Helper method to get the runtime class name.
	 */
	static public String getClassNameForEisPlatform( String shortClassName) {
	    
	    return EisPlatformManager.instance().getRuntimePlatformClassNameForClass( shortClassName);	        
	}
	/**
	 * Factory method to build a Default DataSource.
	 * Used when a Login has not been defined in sessions.xml
	 */
	static public DataSource buildDefault() {
	    DataSource ds = new DataSource();
		
	    ds.initializeDefaultDs();
		return ds;
	}
	/**
	 * Factory method to build a DataSource based on user's preferences.
	 */
	static public DataSource buildPreferedDataSourceFor( SCAdapter adapter) {

        String platformName = adapter.preferences().get( SCPlugin.DATABASE_PLATFORM_PREFERENCE, SCPlugin.DATABASE_PLATFORM_PREFERENCE_DEFAULT);
        DatabasePlatform platform = DatabasePlatformRepository.getDefault().platformNamed( platformName);

	    return new DataSource( platform);
	}
	
	private void initializeDbDs( DatabasePlatform platform) {
	    
	    this.platformId = DATABASE_PLATFORM_ID;
	    this.platformName = platform.getName();
	    this.platformClassName = platform.getRuntimePlatformClassName();
	}

	private void initializeEisDs( String shortClassName) {
	    
	    this.platformId = EIS_PLATFORM_ID;
	    this.platformName = shortClassName;
        this.platformClassName = EisPlatformManager.instance().getRuntimePlatformClassNameFor(shortClassName);	        
	}
	
	private void initializeXmlDs() {
	    
	    this.platformId = XML_PLATFORM_ID;
	    this.platformName = "xml-platform";
	    this.platformClassName = XMLLoginAdapter.DEFAULT_PLATFORM_CLASS_NAME;
	}

	private void initializeDefaultDs() {
	    
	    this.platformId = DEFAULT_PLATFORM_ID;
	    this.platformName = "default-platform";        
	}
	
	public boolean isDatabase() {
	    return this.platformId == DATABASE_PLATFORM_ID;
	}

	public boolean isEis() {
	    return this.platformId == EIS_PLATFORM_ID;
	}

	public boolean isXml() {
	    return this.platformId == XML_PLATFORM_ID;
	}

	public boolean isDefault() {
	    return this.platformId == DEFAULT_PLATFORM_ID;
	}

	public String getName() {
	    return this.platformName;
	}
	
	public String getPlatformClassName() {
	    return this.platformClassName;
	}
	
	LoginAdapter buildLoginAdapter( DatabaseSessionAdapter parent) {

	    if( this.isXml()) {
			return new XMLLoginAdapter( parent);
		}
		else if( this.isDatabase()) {
			return new DatabaseLoginAdapter( parent);
		}
		else if( this.isEis()) {
			return new EISLoginAdapter( parent);
		}
		else if( this.isDefault()) {
			return parent.buildDefaultLogin();
		}
		throw new IllegalArgumentException( platformName);
	}

	public String toString() {
		
		return this.getName();
	}
}
