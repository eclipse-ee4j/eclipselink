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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.RecordFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.eclipse.persistence.eis.EISDescriptor;
import org.eclipse.persistence.eis.EISLogin;
import org.eclipse.persistence.eis.mappings.*;
import org.eclipse.persistence.internal.eis.adapters.xmlfile.XMLFileConnectionFactory;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.queries.Call;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import org.eclipse.persistence.sessions.factories.XMLProjectWriter;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.w3c.dom.*;

public abstract class EISMappingTestCases extends OXTestCase {
    protected DatabaseSession session;
    protected Connection connection;
    protected XMLFileConnectionFactory connectionFactory;
    protected RecordFactory recordFactory;
    protected Class rootClass;
    protected Document controlDocument;
    private String resourceName;
    private DocumentBuilder parser;

    public EISMappingTestCases(String name) {
        super(name);
        setupParser();
        //doc pres is not supported with EIS
        if (platform == Platform.DOC_PRES) {
        	platform = Platform.DOM;
        }
    }

    abstract protected String getTestDocument();

    abstract protected Object getControlObject();

    abstract protected java.util.ArrayList getRootClasses();

    abstract protected void createTables();

    protected void setupParser() {
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setNamespaceAware(true);
            builderFactory.setIgnoringElementContentWhitespace(true);
            parser = builderFactory.newDocumentBuilder();
        } catch (Exception e) {
            e.printStackTrace();
            this.fail("An exception occurred during setup");
        }
    }

    protected void createRequiredTables() {
        try {
            createTables();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void setProject(Project project) {
        if (!(metadata == Metadata.JAVA)) {
            String directorySetting = (String)((EISLogin)project.getDatasourceLogin()).getProperty("directory");
            StringWriter stringWriter = new StringWriter();
            new XMLProjectWriter().write(project, stringWriter);
            log("DEPLOYMENT XML: " + stringWriter.toString());

            Project newProject = new XMLProjectReader().read(new StringReader(stringWriter.toString()));
            Map descriptors = project.getDescriptors();
            Iterator keysIterator = descriptors.keySet().iterator();
            while (keysIterator.hasNext()) {
                Class nextClass = (Class)keysIterator.next();
                EISDescriptor next = (EISDescriptor)descriptors.get(nextClass);
                for (int i = 0; i < next.getMappings().size(); i++) {
                    DatabaseMapping nextMapping = (DatabaseMapping)next.getMappings().get(i);
                    if (nextMapping instanceof EISOneToOneMapping) {
                        String attrName = nextMapping.getAttributeName();
                        Call oldMappingCall = ((EISOneToOneMapping)nextMapping).getSelectionQuery().getDatasourceCall();
                        if (oldMappingCall != null) {
                            ((EISOneToOneMapping)newProject.getDescriptor(nextClass).getMappingForAttributeName(attrName)).setSelectionCall(oldMappingCall);
                        }
                    } else if (nextMapping instanceof EISOneToManyMapping) {
                        String attrName = nextMapping.getAttributeName();
                        ((EISOneToManyMapping)newProject.getDescriptor(nextClass).getMappingForAttributeName(attrName)).setSelectionCall(((EISOneToManyMapping)nextMapping).getSelectionQuery().getDatasourceCall());
                    }
                }
                newProject.getDescriptor(nextClass).getQueryManager().setDeleteCall(next.getQueryManager().getDeleteCall());
                newProject.getDescriptor(nextClass).getQueryManager().setInsertCall(next.getQueryManager().getInsertCall());
                newProject.getDescriptor(nextClass).getQueryManager().setReadAllCall(next.getQueryManager().getReadAllCall());
                newProject.getDescriptor(nextClass).getQueryManager().setReadObjectCall(next.getQueryManager().getReadObjectCall());
                newProject.getDescriptor(nextClass).getQueryManager().setUpdateCall(next.getQueryManager().getUpdateCall());
            }

            ((EISLogin)newProject.getDatasourceLogin()).setProperty("directory", directorySetting);
            session = newProject.createDatabaseSession();
        } else {
            session = project.createDatabaseSession();
        }
    }

    public void setUp() {
        try {
            connection = connect();
            recordFactory = connectionFactory.getRecordFactory();
        } catch (Exception e) {
            e.printStackTrace();
            this.fail("An exception occurred during setup");
        }
    }

    public Connection connect() throws ResourceException {
        connectionFactory = (XMLFileConnectionFactory)new XMLFileConnectionFactory();
        connection = connectionFactory.getConnection();

        if (useLogging) {
            session.setLogLevel(SessionLog.FINEST);
        } else {
            session.setLogLevel(SessionLog.OFF);
        }
        return connection;
    }

    public void tearDown() {
        try {
            connection.close();
            session.logout();
        } catch (Exception e) {
            this.fail("An exception occurred during teardown:");
            e.printStackTrace();
        }
    }

    public void testObjectToXMLDocument() throws Exception {
        updateProjectForWriting();

        createTables();

        Object objectToInsert = this.getControlObject();
        if (objectToInsert instanceof List) {
            int size = ((List)objectToInsert).size();
            for (int i = 0; i < size; i++) {
                session.insertObject(((List)objectToInsert).get(i));
            }
        } else {
            session.insertObject(this.getControlObject());
        }

        log("**testObjectToXMLDocument**");
        log("****Expected:");
        log(getControlDocument());
        log("\n****Actual:");
        Document testDocument = logTestDocument();

        assertXMLIdentical(getControlDocument(), testDocument);

    }

    public void testXMLDocumentToObject() throws Exception {
        updateProjectForReading();

        Vector objects = new Vector();
        for (int i = 0; i < getRootClasses().size(); i++) {
            objects.addAll(session.readAllObjects((Class)getRootClasses().get(i)));
        }

        log("**testXMLDocumentToObject**");
        log("****Expected:");
        log(getControlObject().toString());
        log("***Actual:");
        this.assertTrue(objects.size() > 0);
        if (objects.size() > 1) {
            log(objects.toString());
            this.assertTrue(((java.util.ArrayList)getControlObject()).size() == objects.size());
            this.assertEquals(getControlObject(), objects);
        } else if (objects.size() == 1) {
            log(objects.elementAt(0).toString());
            this.assertEquals(getControlObject(), objects.elementAt(0));
        }
    }

    protected void updateProjectForReading() throws Exception {
        if (session.isConnected()) {
            session.logout();
        }
        String directory = (String)((EISLogin)session.getDatasourceLogin()).getProperty("directory");
        ((EISLogin)session.getDatasourceLogin()).setProperty("directory", directory + "/reading");

        session.login();
    }

    protected void updateProjectForWriting() throws Exception {
        if (session.isConnected()) {
            session.logout();
        }
        String directory = (String)((EISLogin)session.getDatasourceLogin()).getProperty("directory");
        ((EISLogin)session.getDatasourceLogin()).setProperty("directory", directory + "/writing");

        session.login();
    }

    public void testObjectToXMLDocumentUOW() throws Exception {
        updateProjectForWriting();
        createTables();

        Object objectToInsert = this.getControlObject();
        if (objectToInsert instanceof List) {
            int size = ((List)objectToInsert).size();
            for (int i = 0; i < size; i++) {
                UnitOfWork uow = session.acquireUnitOfWork();
                uow.registerObject(((List)objectToInsert).get(i));
                uow.commit();
            }
        } else {
            UnitOfWork uow = session.acquireUnitOfWork();
            uow.registerObject(this.getControlObject());
            uow.commit();
        }

        log("**testObjectToXMLDocumentUOW**");
        log("****Expected:");
        log(getControlDocument());
        log("****Actual:");
        Document testDocument = logTestDocument();

        assertXMLIdentical(getControlDocument(), testDocument);
    }

    public void testXMLDocumentToObjectUOW() throws Exception {
        updateProjectForReading();
        session.getIdentityMapAccessor().initializeAllIdentityMaps();
        Vector objects = new Vector();
        for (int i = 0; i < getRootClasses().size(); i++) {
            UnitOfWork uow = session.acquireUnitOfWork();
            objects.addAll(uow.readAllObjects((Class)getRootClasses().get(i)));
        }

        log("**testXMLDocumentToObjectUOW**");
        log("****Expected:");
        log(getControlObject().toString());
        log("***Actual:");
        if (objects.size() > 1) {
            log(objects.toString());
            this.assertTrue(((java.util.ArrayList)getControlObject()).size() == objects.size());
        } else {
            log(objects.elementAt(0).toString());
            this.assertEquals(getControlObject(), objects.elementAt(0));
        }
    }

    public void testUOWDelete() throws Exception {
        updateProjectForWriting();

        createTables();
        //Insert some objects
        log("**testUOWDelete**");
        Object objectToInsert = this.getControlObject();
        if (objectToInsert instanceof List) {
            int size = ((List)objectToInsert).size();
            for (int i = 0; i < size; i++) {
                session.insertObject(((List)objectToInsert).get(i));
            }
        } else {
            session.insertObject(this.getControlObject());
        }

        log("****After inserts:");
        logTestDocument();

        UnitOfWork uow = session.acquireUnitOfWork();
        Object objectsRead = uow.readAllObjects(getSourceClass());

        if (objectsRead instanceof Vector) {
            uow.deleteAllObjects((Vector)objectsRead);
        } else {
            uow.deleteObject(objectsRead);
        }

        uow.commit();

        session.getIdentityMapAccessor().initializeAllIdentityMaps();
        Vector afterDeleteObjects = (Vector)session.readAllObjects(getSourceClass());
        this.assertEquals("Objects were not all deleted", 0, afterDeleteObjects.size());

        log("****After commit deletes:");
        logTestDocument();
    }

    abstract protected Class getSourceClass();

    protected Document logTestDocument() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(getTestDocument());

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder parser = builderFactory.newDocumentBuilder();
        Document returnDocument = parser.parse(inputStream);
        inputStream.close();
        log(returnDocument);
        removeEmptyTextNodes(returnDocument);

        return returnDocument;
    }

    protected void setControlDocument(String xmlResource) throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(xmlResource);
        resourceName = xmlResource;
        controlDocument = parser.parse(inputStream);
        removeEmptyTextNodes(controlDocument);
    }

    protected Document getControlDocument() {
        return controlDocument;
    }
}
