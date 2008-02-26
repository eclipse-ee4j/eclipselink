/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
package org.eclipse.persistence.internal.jpa.metadata.converters;

/**
 * INTERNAL:
 * Object to hold onto conversion values.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public class ConversionValueMetadata {
	private String m_dataValue;
	private String m_objectValue;
	
	/**
	 * INTERNAL:
	 */
	public ConversionValueMetadata() {}
	
	/**
	 * INTERNAL:
	 */
	public ConversionValueMetadata(Object conversionValue) {
		m_dataValue = (String)MetadataHelper.invokeMethod("dataValue", conversionValue); 
		m_objectValue = (String)MetadataHelper.invokeMethod("objectValue", conversionValue);  
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public String getDataValue() {
		return m_dataValue;
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public String getObjectValue() {
		return m_objectValue;
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public void setDataValue(String dataValue) {
		m_dataValue = dataValue;
	}
	
	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public void setObjectValue(String objectValue) {
		m_objectValue = objectValue;
	}
}
