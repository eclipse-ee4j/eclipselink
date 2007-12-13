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

import org.eclipse.persistence.platform.database.oracle.PLSQLStoredProcedureCall;

// Javse imports

// EclipseLink imports

/**
 * <b>PUBLIC</b>: Marker interface for Complex Database types
 * (e.g. PL/SQL Records, PL/SQL Index tables)
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since Oracle TopLink 11.x.x
 */
public interface ComplexDatabaseType extends DatabaseType, Cloneable {
    
    public boolean hasCompatibleType();
    public String getCompatibleType();
  
    public ComplexDatabaseType clone();
    
    public void setCall(PLSQLStoredProcedureCall call);
}