/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
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
