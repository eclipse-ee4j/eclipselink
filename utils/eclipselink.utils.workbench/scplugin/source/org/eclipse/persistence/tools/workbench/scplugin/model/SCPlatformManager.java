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
package org.eclipse.persistence.tools.workbench.scplugin.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


public abstract class SCPlatformManager {

    // key = aStringID, value = platformClassName
	private Map platforms;
	
	protected SCPlatformManager() {

		initialize();
	}

	protected void initialize() {

		this.platforms = new HashMap();
		this.buidPlatforms();
	}
	
	protected void addPlatform( String id, String platformClassName) {
		this.platforms.put( id, platformClassName);
	}
	
	protected abstract void buidPlatforms();
	
	public Iterator platformIds() {
	    
	    return this.platforms.keySet().iterator();
	}
	
	public Iterator platformNames() {
	    
	    return this.platforms.values().iterator();
	}
	
	public Iterator platformShortNames() {
		return new TransformationIterator( this.platformNames()) {
			protected Object transform( Object next) {
				return ClassTools.shortNameForClassNamed(( String)next);
			}
		};
	}

	public String getRuntimePlatformClassNameForClass( String shortClassName) {
	    for( Iterator i = platformNames(); i.hasNext(); ) {
	        String className = ( String)i.next();
			if( className.endsWith( shortClassName)) {
				return className;
			}
		}
		throw new IllegalArgumentException( "missing database type named: "
				+ shortClassName);
	}
	
	public String getRuntimePlatformClassNameFor( String key) {
	    
	    return ( String)this.platforms.get( key);
	}
	
	public String getIdFor( String shortClassName) {

	    for( Iterator i = this.platforms.keySet().iterator(); i.hasNext(); ) {
	        String key = ( String)i.next();
	        String value = ( String)this.platforms.get( key);
			if( value.endsWith( shortClassName))
				return key;
		}
		throw new IllegalArgumentException( "missing plaform named: " + shortClassName);
	}
	
	public boolean serverPlatformIsSupported( String className) {
	    if( StringTools.stringIsEmpty( className)) return false;
	    
	    return this.platforms.containsValue( className);
	}
	
	public int platformsSize() {
		return this.platforms.size();
	}
}
