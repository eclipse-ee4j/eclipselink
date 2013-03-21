/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.config;

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
