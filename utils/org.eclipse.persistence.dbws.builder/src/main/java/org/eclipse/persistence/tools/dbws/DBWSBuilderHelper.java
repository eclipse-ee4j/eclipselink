/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Mike Norman - from Proof-of-concept, become production code
package org.eclipse.persistence.tools.dbws;

//javase imports
import java.io.OutputStream;
import java.util.List;

//Java extension library imports
import javax.wsdl.WSDLException;

import org.eclipse.persistence.tools.oracleddl.metadata.CompositeDatabaseType;

public interface DBWSBuilderHelper {

    void buildDbArtifacts();

    void buildOROXProjects(NamingConventionTransformer nct);
    void buildOROXProjects(NamingConventionTransformer nct, List<CompositeDatabaseType> types);

    /**
     * Builds a list of type instances based on procedure/function arguments.
     */
    List<CompositeDatabaseType> buildTypesList(List<OperationModel> operations);

    void buildSchema(NamingConventionTransformer nct);

    void buildSessionsXML(OutputStream dbwsSessionsStream);

    void buildDBWSModel(NamingConventionTransformer nct, OutputStream dbwsServiceStream);

    void buildProcedureOperation(ProcedureOperationModel procedureOperationModel);

    void writeAttachmentSchema(OutputStream swarefStream);

    void buildWSDL(OutputStream wsdlStream, NamingConventionTransformer nct) throws WSDLException;

    void writeWebXML(OutputStream webXmlStream);

    /**
     * Write the (optional) deployment descriptor to the given OutputStream.
     */
    void writeDeploymentDescriptor(OutputStream deploymentDescriptorStream);

    void generateDBWSProvider(OutputStream sourceProviderStream,
                              OutputStream classProviderStream, OutputStream sourceProviderListenerStream,
                              OutputStream classProviderListenerStream);

    void writeSchema(OutputStream dbwsSchemaStream);

    void writeOROXProjects(OutputStream dbwsOrStream, OutputStream dbwsOxStream);

    boolean hasTables();
}
