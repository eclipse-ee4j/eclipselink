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
import java.util.Enumeration;
import org.w3c.dom.Document;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.sessions.Project;
import deprecated.xml.stream.XMLStreamAccessor;
import deprecated.xml.stream.XMLStreamDatabase;
import deprecated.xml.stream.XMLStreamLogin;
import deprecated.transform.DataResult;

/**
 * <p>
 * <b>Purpose</b>:
 * <p> Write objects to XML format, used by XMLTransformer
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  There is no direct replacement API.
 */
public class XMLResult implements DataResult {
    private Document resultDocument;

    public XMLResult() {
        super();
    }

    public Document getResultDocument() {
        return resultDocument;
    }

    public void storeObjects(Project project, Vector objects) {
        // Convert the Document into a TopLink compatible XMLStreamDatabase
        XMLStreamDatabase xmlStreamDatabase = new XMLStreamDatabase();

        // Create the login and assign it to the Project
        XMLStreamLogin xmlStreamLogin = new XMLStreamLogin();
        xmlStreamLogin.setXMLStreamDatabase(xmlStreamDatabase);
        xmlStreamLogin.setAccessorClass(XMLStreamAccessor.class);
        project.setLogin(xmlStreamLogin);

        DatabaseSession session = project.createDatabaseSession();
        session.login();

        // write objects to xml database
        Enumeration enumtr = objects.elements();
        if (enumtr.hasMoreElements()) {
            UnitOfWork uow = session.acquireUnitOfWork();
            do {
                uow.registerNewObject(enumtr.nextElement());
            } while (enumtr.hasMoreElements());

            uow.commit();
        }
        session.logout();

        // Set the resultDocument from xml database
        resultDocument = xmlStreamDatabase.getDocument();
    }
}