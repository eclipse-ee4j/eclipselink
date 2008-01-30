/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.converters;

import org.eclipse.persistence.annotations.ConversionValue;

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
	public ConversionValueMetadata(ConversionValue conversionValue) {
		m_dataValue = conversionValue.dataValue();
		m_objectValue = conversionValue.objectValue();
	}
	
	/**
	 * INTERNAL:
	 */
	public String getDataValue() {
		return m_dataValue;
	}
	
	/**
	 * INTERNAL:
	 */
	public String getObjectValue() {
		return m_objectValue;
	}
	
	/**
	 * INTERNAL:
	 */
	public void setDataValue(String dataValue) {
		m_dataValue = dataValue;
	}
	
	/**
	 * INTERNAL:
	 */
	public void setObjectValue(String objectValue) {
		m_objectValue = objectValue;
	}
}
