/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metadata.columns;

/**
 * Object to hold onto common attribute/association override metadata.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public class OverrideMetadata {
	private boolean m_loadedFromXML;
	
	// The java class for which this override metadata was defined for.
	private String m_javaClassName;
	// The physical location where it was found. A java class or mapping file.
	private String m_location;
	private String m_name;
	
	/**
	 * INTERNAL:
	 * Assumed to be used solely for OX loading.
	 */
	protected OverrideMetadata() {
		m_loadedFromXML = true;
	}
	
	/**
	 * INTERNAL:
	 * Assumed to be used solely for Annotation loading.
	 */
	protected OverrideMetadata(String javaClassName) {
		m_loadedFromXML = false;
		m_javaClassName = javaClassName;
		m_location = javaClassName;
	}
	
	/**
	 * INTERNAL:
	 */
	public String getJavaClassName() {
		return m_javaClassName;
	}
	
	/**
	 * INTERNAL:
	 */
	public String getLocation() {
		return m_location;
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public String getName() {
		return m_name;
	}
	
	/**
	 * INTERNAL:
	 */
	public boolean loadedFromXML() {
		return m_loadedFromXML;
	}
	
	/**
	 * INTERNAL:
	 */
	public void setLocation(String location) {
		m_location = location;
	}
	
	/**
	 * INTERNAL:
	 */
	public void setJavaClassName(String javaClassName) {
		m_javaClassName = javaClassName;
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public void setName(String name) {
		m_name = name;
	}
}
