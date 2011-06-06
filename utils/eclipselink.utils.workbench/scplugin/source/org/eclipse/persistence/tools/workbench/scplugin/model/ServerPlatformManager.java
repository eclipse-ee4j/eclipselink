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
package org.eclipse.persistence.tools.workbench.scplugin.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;


public class ServerPlatformManager extends SCPlatformManager {

	private Map configs;

	public static final String NO_SERVER_ID = "NoServerPlatform";
    public static final String WEBLOGIC_10_ID = "WebLogic_10_Platform";
    public static final String WEBLOGIC_9_ID = "WebLogic_9_Platform";
    public static final String WEBLOGIC_8_1_ID = "WebLogic_8_1_Platform";
    public static final String WEBLOGIC_7_0_ID = "WebLogic_7_0_Platform";
    public static final String WEBLOGIC_6_1_ID = "WebLogic_6_1_Platform";
    public static final String WEBSPHERE_7_ID = "WebSphere_7_Platform";
    public static final String WEBSPHERE_6_1_ID = "WebSphere_6_1_Platform";
    public static final String WEBSPHERE_6_0_ID = "WebSphere_6_0_Platform";
    public static final String WEBSPHERE_5_1_ID = "WebSphere_5_1_Platform";
    public static final String WEBSPHERE_5_0_ID = "WebSphere_5_0_Platform";
    public static final String WEBSPHERE_4_0_ID = "WebSphere_4_0_Platform";
    public static final String JBOSS_ID  = "JBossPlatform";
    public static final String SUNAS_ID = "SunAS9ServerPlatform";
    public static final String CUSTOM_SERVER_ID  = "CustomServerPlatform";

	private static ServerPlatformManager INSTANCE;
	
	private ServerPlatformManager() {
		super();
	}
	
	@Override
	protected void initialize() {
		super.initialize();
		this.configs = new HashMap();
		this.buildConfigs();
	}

	/**
	 * singleton support
	 */
	public static synchronized ServerPlatformManager instance() {
		if( INSTANCE == null) {
			INSTANCE = new ServerPlatformManager();
		}
		return INSTANCE;
	}	
	
	protected void addConfig( String id, String configClassName) {
		this.configs.put( id, configClassName);
	}
	
	protected void buidPlatforms() {
	    
	    this.addPlatform( NO_SERVER_ID, "org.eclipse.persistence.platform.server.NoServerPlatform");
	    this.addPlatform( WEBLOGIC_9_ID, "org.eclipse.persistence.platform.server.wls.WebLogic_9_Platform");
	    this.addPlatform( WEBLOGIC_10_ID, "org.eclipse.persistence.platform.server.wls.WebLogic_10_Platform");
	    this.addPlatform( WEBSPHERE_6_1_ID, "org.eclipse.persistence.platform.server.was.WebSphere_6_1_Platform");
	    this.addPlatform( WEBSPHERE_7_ID, "org.eclipse.persistence.platform.server.was.WebSphere_7_Platform");
	    this.addPlatform( JBOSS_ID, "org.eclipse.persistence.platform.server.jboss.JBossPlatform");
	    this.addPlatform( SUNAS_ID, "org.eclipse.persistence.platform.server.sunas.SunAS9ServerPlatform");
	    this.addPlatform( CUSTOM_SERVER_ID, "org.eclipse.persistence.platform.server.CustomServerPlatform");
	}

	protected void buildConfigs() {
	    
	    this.addConfig( NO_SERVER_ID, "null");
	    this.addConfig( WEBLOGIC_9_ID, "org.eclipse.persistence.internal.sessions.factories.model.platform.WebLogic_9_PlatformConfig");
	    this.addConfig( WEBLOGIC_10_ID, "org.eclipse.persistence.internal.sessions.factories.model.platform.WebLogic_10_PlatformConfig");
	    this.addConfig( WEBSPHERE_6_1_ID, "org.eclipse.persistence.internal.sessions.factories.model.platform.WebSphere_6_1_PlatformConfig");
	    this.addConfig( WEBSPHERE_7_ID, "org.eclipse.persistence.internal.sessions.factories.model.platform.WebSphere_7_0_PlatformConfig");
	    this.addConfig( JBOSS_ID, "org.eclipse.persistence.internal.sessions.factories.model.platform.JBossPlatformConfig");
	    this.addConfig( SUNAS_ID, "org.eclipse.persistence.internal.sessions.factories.model.platform.SunAS9PlatformConfig");
	    this.addConfig( CUSTOM_SERVER_ID, "org.eclipse.persistence.internal.sessions.factories.model.platform.CustomServerPlatformConfig");
	}

	public Iterator platformShortNames() {
	    
		return new TransformationIterator( this.platformIds()) {
			protected Object transform( Object next) {
				String id = ( String)next;
				
		        return ClassTools.shortNameForClassNamed( getRuntimePlatformClassNameFor( id));
			}
		};
	}
	
	public Iterator configIds() {
	    
	    return this.configs.keySet().iterator();
	}
	
	public Iterator configNames() {
	    
	    return this.configs.values().iterator();
	}
	
	public Iterator configShortNames() {
		return new TransformationIterator( this.configNames()) {
			protected Object transform( Object next) {
				return ClassTools.shortNameForClassNamed(( String)next);
			}
		};
	}
	
	public String getRuntimePlatformConfigClassNameForPlatformId( String platformId) {
	    if (configs.containsKey(platformId)) {
	    	return (String)configs.get(platformId);
	    }
		throw new IllegalArgumentException( "missing platform config named: "
				+ platformId);
	}

}
