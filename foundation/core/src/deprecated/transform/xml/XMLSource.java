/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package deprecated.transform.xml;

import java.util.Vector;
import org.w3c.dom.Document;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;
import deprecated.xml.stream.XMLStreamAccessor;
import deprecated.xml.stream.XMLStreamDatabase;
import deprecated.xml.stream.XMLStreamLogin;
import deprecated.transform.DataSource;

/**
 * <p>
 * <b>Purpose</b>:
 * <p> Build objects to XML format, used by XMLTransformer
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  There is no direct replacement API.
 */
public class XMLSource implements DataSource {
    private Document sourceDocument;

    public XMLSource() {
        super();
    }

    public void setSourceDocument(Document newSourceDocument) {
        sourceDocument = newSourceDocument;
    }

    public Vector buildObjects(Project project, Class type) {
        // Convert the Document into a TopLink compatible XMLStreamDatabase
        XMLStreamDatabase xmlStreamDatabase = new XMLStreamDatabase(sourceDocument, project);

        // Create the login and assign it to the Project
        XMLStreamLogin xmlStreamLogin = new XMLStreamLogin();
        xmlStreamLogin.setXMLStreamDatabase(xmlStreamDatabase);
        xmlStreamLogin.setAccessorClass(XMLStreamAccessor.class);
        project.setLogin(xmlStreamLogin);

        // Read the objects and return the result
        DatabaseSession session = project.createDatabaseSession();
        session.login();
        Vector result = session.readAllObjects(type);
        session.logout();
        return result;
    }
}