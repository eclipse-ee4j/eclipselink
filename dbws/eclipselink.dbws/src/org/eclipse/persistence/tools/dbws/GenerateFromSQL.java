/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

package org.eclipse.persistence.tools.dbws;

// Javase imports
import java.io.FilterOutputStream;
import java.io.IOException;
import java.util.ArrayList;

// Java extension imports

// EclipseLink imports
import org.eclipse.persistence.dbws.DBWSModelProject;
import org.eclipse.persistence.internal.oxm.schema.SchemaModelProject;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLMarshaller;
import static org.eclipse.persistence.tools.dbws.Util.addSimpleXMLFormat;

public class GenerateFromSQL extends BaseGenerator {

    protected ArrayList<SQLOperation> sqlOperations = new ArrayList<SQLOperation>();

    public GenerateFromSQL() {
        super();
    }

    public void addDBWSToStream(FilterOutputStream fos) throws IOException {

        initXRServiceModel();
        for (SQLOperation sqlOperation : sqlOperations) {
            sqlOperation.buildOperation(xrServiceModel, schema, getDatabasePlatform(), getConnection());
        }
        XMLContext context = new XMLContext(new DBWSModelProject());
        XMLMarshaller marshaller = context.createMarshaller();
        marshaller.marshal(xrServiceModel, fos);
    }

    public void addSchemaToStream(FilterOutputStream fos) throws IOException {
        super.addSchemaToStream(fos);
        addSimpleXMLFormat(schema);
        XMLContext context = new XMLContext(new SchemaModelProject());
        XMLMarshaller marshaller = context.createMarshaller();
        marshaller.marshal(schema, fos);
    }

    public void addSQLOperation(SQLOperation operation) {
        sqlOperations.add(operation);
    }
}
