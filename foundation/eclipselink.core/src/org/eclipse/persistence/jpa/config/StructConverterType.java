/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.jpa.config;

/**
 * This class contains predefined StructConverter types.
 * 
 * Users can use these types to define what StructConverter to use. For instance:
 * 
 * @StructConverter(name="JGeom", converter="JGEOMETRY")
 *
 * @author tware
 *
 */
public class StructConverterType {

    public static final String JGeometry = "JGEOMETRY";
}
