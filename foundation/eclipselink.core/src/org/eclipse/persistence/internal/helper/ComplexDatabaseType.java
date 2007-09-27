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
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.util.Vector;

// Java extension imports

// EclipseLink imports
import org.eclipse.persistence.internal.databaseaccess.OutputParameterForCallableStatement;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.StoredProcedureCall;

/**
 * <b>PUBLIC</b>: Marker interface for Complex Database types
 * (e.g. PL/SQL Records, PL/SQL Index tables)
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since Oracle TopLink 11.x.x
 */
public interface ComplexDatabaseType extends DatabaseType {
    
    public Object getValueForInParameter(Object parameter, Vector parametersValues,
        AbstractRecord translationRow, AbstractRecord modifyRow, AbstractSession session,
        boolean shouldBind);

    public void setComplexOutParameterValue(PreparedStatement statement, int index);

    public void buildOutputRow(AbstractRecord row, CallableStatement statement, int index);

    public void setCall(StoredProcedureCall storedProcedureCall);
}
