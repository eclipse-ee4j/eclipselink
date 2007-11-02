/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

package org.eclipse.persistence.internal.helper;

// Javse imports

// Java extension imports

// EclipseLink imports

/**
 * <b>PUBLIC</b>: Interface used to categorize arguments to Stored Procedures as either
 * 'simple' (use subclass SimpleDatabaseType) or 'complex' (use subclass ComplexDatabaseType) 
 *
 * <b>This version is 'stubbed' out until the solution to the 'order-of-out-args' issues is checked in
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since Oracle TopLink 11.x.x
 */
public interface DatabaseType {

    public boolean isComplexDatabaseType();
    
    public int getSqlCode();

    public int getConversionCode();
    
    public String getTypeName();

}
