/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.config;

/**
 * This class contains predefined StructConverter types.
 *
 * Users can use these types to define what StructConverter to use. For instance:
 *
 * {@literal @StructConverter(name="JGeom", converter="JGEOMETRY")}
 *
 * @author tware
 *
 */
public class StructConverterType {

    public static final String JGeometry = "JGEOMETRY";
}
