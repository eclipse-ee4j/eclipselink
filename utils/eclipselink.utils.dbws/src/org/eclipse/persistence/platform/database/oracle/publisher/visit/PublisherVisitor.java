/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman - from Proof-of-concept, become production code
 ******************************************************************************/
package org.eclipse.persistence.platform.database.oracle.publisher.visit;

//EclipseLink imports
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.ProcedureMethod;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.PlsqlRecordType;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.PlsqlTableType;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.SqlArrayType;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.SqlObjectType;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.SqlPackageType;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.SqlTableType;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.SqlToplevelType;

public interface PublisherVisitor {
    
    public void visit(ProcedureMethod method);

    public void visit(SqlObjectType sqlObjectType);

    public void visit(SqlArrayType sqlArrayType);

    public void visit(SqlTableType sqlTableType);

    public void visit(SqlPackageType sqlPackageType);

    public void visit(SqlToplevelType sqlToplevelType);

    public void visit(PlsqlRecordType plsqlRecordType);

    public void visit(PlsqlTableType plsqlTableType);

}