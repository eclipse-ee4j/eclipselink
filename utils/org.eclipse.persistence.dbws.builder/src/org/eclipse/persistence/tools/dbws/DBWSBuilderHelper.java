/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.tools.dbws;

//javase imports
import java.io.OutputStream;
import java.util.List;

//Java extension library imports
import javax.wsdl.WSDLException;

import org.eclipse.persistence.tools.oracleddl.metadata.CompositeDatabaseType;

public interface DBWSBuilderHelper {

    public void buildDbArtifacts();

    public void buildOROXProjects(NamingConventionTransformer nct);
    public void buildOROXProjects(NamingConventionTransformer nct, List<CompositeDatabaseType> types);

    /**
     * Builds a list of type instances based on procedure/function arguments.
     */
    public List<CompositeDatabaseType> buildTypesList(List<OperationModel> operations);
    
    public void buildSchema(NamingConventionTransformer nct);

    public void buildSessionsXML(OutputStream dbwsSessionsStream);

    public void buildDBWSModel(NamingConventionTransformer nct, OutputStream dbwsServiceStream);

    public void buildProcedureOperation(ProcedureOperationModel procedureOperationModel);
    
    public void writeAttachmentSchema(OutputStream swarefStream);

    public void buildWSDL(OutputStream wsdlStream, NamingConventionTransformer nct) throws WSDLException;

    public void writeWebXML(OutputStream webXmlStream);

    public void generateDBWSProvider(OutputStream sourceProviderStream,
        OutputStream classProviderStream, OutputStream sourceProviderListenerStream,
        OutputStream classProviderListenerStream);

    public void writeSchema(OutputStream dbwsSchemaStream);

    public void writeOROXProjects(OutputStream dbwsOrStream, OutputStream dbwsOxStream);

    public boolean hasTables();
    
    public boolean hasComplexProcedureArgs();

}