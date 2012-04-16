/*******************************************************************************
* Copyright (c) 1998, 2011 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
* dmccann - Mar 2/2009 - 2.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.oxm.schemamodelgenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.descriptors.Namespace;
import org.eclipse.persistence.internal.oxm.schema.SchemaModelGenerator;
import org.eclipse.persistence.internal.oxm.schema.SchemaModelProject;
import org.eclipse.persistence.internal.oxm.schema.model.Schema;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.platform.SAXPlatform;
import org.eclipse.persistence.oxm.platform.XMLPlatform;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.jaxb.JAXBXMLComparer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Parent class for the schema model generation test cases.
 *
 */
public class GenerateSchemaTestCases extends TestCase {
    protected SchemaModelGenerator sg;
    protected DocumentBuilder parser;
    protected JAXBXMLComparer comparer;
    protected static String TMP_DIR;
    
    public GenerateSchemaTestCases(String name) throws Exception {
        super(name);
        TMP_DIR = (System.getenv("T_WORK") == null ? "" : (System.getenv("T_WORK") + "/"));
        sg = new SchemaModelGenerator();
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setIgnoringElementContentWhitespace(true);
        builderFactory.setNamespaceAware(true);
        parser = builderFactory.newDocumentBuilder();
        comparer = new JAXBXMLComparer();
    }
    
    /**
     * Login the session to ensure that the descriptors are initialized.
     *  
     * @param prj
     */
    protected void loginProject(Project prj) {
        XMLPlatform platform = new SAXPlatform();
        prj.setLogin(new XMLLogin(platform));
        DatabaseSession session = prj.createDatabaseSession();
        session.setLogLevel(SessionLog.OFF);
        for (Iterator<ClassDescriptor> descriptorIt = prj.getOrderedDescriptors().iterator(); descriptorIt.hasNext();) {
            ClassDescriptor descriptor = descriptorIt.next();
            if (descriptor.getJavaClass() == null) {
                descriptor.setJavaClass(session.getDatasourcePlatform().getConversionManager().convertClassNameToClass(descriptor.getJavaClassName()));
            }
        }
        session.login();
    }
    
    /**
     * Write the given schema to the T_WORK folder.
     * 
     * @param generatedSchema
     */
    protected void writeSchema(Schema generatedSchema) {
        try {
            Project p = new SchemaModelProject();
            Vector namespaces = generatedSchema.getNamespaceResolver().getNamespaces();
            for (int i = 0; i < namespaces.size(); i++) {
                Namespace next = (Namespace)namespaces.get(i);
                ((XMLDescriptor) p.getDescriptor(Schema.class)).getNamespaceResolver().put(next.getPrefix(), next.getNamespaceURI());
            }
            XMLContext context = new XMLContext(p);
            XMLMarshaller marshaller = context.createMarshaller();
            FileWriter generatedSchemaWriter = new FileWriter(new File(TMP_DIR + "generatedSchema.xsd"));
            marshaller.marshal(generatedSchema, generatedSchemaWriter);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * For debugging - write the given schema to System out.
     * 
     * @param generatedSchema
     */
    protected void outputSchema(Schema generatedSchema){
        Project p = new SchemaModelProject();
        Vector namespaces = generatedSchema.getNamespaceResolver().getNamespaces();
        for (int i = 0; i < namespaces.size(); i++) {
            Namespace next = (Namespace)namespaces.get(i);
            ((XMLDescriptor)p.getDescriptor(Schema.class)).getNamespaceResolver().put(next.getPrefix(), next.getNamespaceURI());
        }
        XMLContext context = new XMLContext(p);
        XMLMarshaller marshaller = context.createMarshaller();
        StringWriter generatedSchemaWriter = new StringWriter();
        marshaller.marshal(generatedSchema, generatedSchemaWriter);
        System.out.println(generatedSchemaWriter.toString());
    }

    /**
     * Compares a schema model Schema (as a string) against a string representation 
     * of an XML schema.
     * 
     * @param generatedSchema
     * @param controlSchemaName
     */
    protected boolean compareSchemaStrings(Schema generatedSchema, String controlSchema){
        Project p = new SchemaModelProject();
        Vector namespaces = generatedSchema.getNamespaceResolver().getNamespaces();
        for (int i = 0; i < namespaces.size(); i++) {
            Namespace next = (Namespace)namespaces.get(i);
            ((XMLDescriptor) p.getDescriptor(Schema.class)).getNamespaceResolver().put(next.getPrefix(), next.getNamespaceURI());
        }
        XMLContext context = new XMLContext(p);
        XMLMarshaller marshaller = context.createMarshaller();
        StringWriter generatedSchemaWriter = new StringWriter();
        marshaller.marshal(generatedSchema, generatedSchemaWriter);
        return generatedSchemaWriter.toString().equals(controlSchema);
    }
    
    /**
     * Utility for reading in an XML schema file and returning it as a string.
     * 
     * @param reader
     * @param available
     * @return
     */
    protected String getSchemaAsString(BufferedReader reader, int available) {
        StringBuffer sb = new StringBuffer(available);
        try {
            char[] chars = new char[available];
            int numRead = 0;
            while( (numRead = reader.read(chars)) > -1){
                sb.append(String.valueOf(chars));   
            }
            reader.close();
        } catch (Exception x) {
            x.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * Return a Document for a given XML Schema.
     *  
     * @param xsdResource indicates the .xsd file to be parsed into a Document
     * @return
     * @see Document
     */
    protected Document getDocument(String xsdResource) {
        Document document = null;
        try {
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(xsdResource);
            document = parser.parse(inputStream);
            removeEmptyTextNodes(document);
            inputStream.close();
        } catch (Exception x) {
            x.printStackTrace();           
        }
        return document;
    }

    /**
     * Return a Document for a given EclipseLink schema model Schema.
     * 
     * @param schema
     * @return
     * @see Document
     */
    protected Document getDocument(Schema schema) {
        Document document = parser.newDocument();

        Project p = new SchemaModelProject();
        for (Iterator<Namespace> nameIt = schema.getNamespaceResolver().getNamespaces().iterator(); nameIt.hasNext(); ) {
            Namespace next = nameIt.next();
            ((XMLDescriptor) p.getDescriptor(Schema.class)).getNamespaceResolver().put(next.getPrefix(), next.getNamespaceURI());
        }
        
        XMLContext context = new XMLContext(p);
        XMLMarshaller marshaller = context.createMarshaller();
        marshaller.marshal(schema, document);

        return document;
    }

    /**
     * Removes any empty child text nodes.
     * 
     * @param node
     */
    protected void removeEmptyTextNodes(Node node) {
        NodeList nodeList = node.getChildNodes();
        Node childNode;
        for (int x = nodeList.getLength() - 1; x >= 0; x--) {
            childNode = nodeList.item(x);
            if (childNode.getNodeType() == Node.TEXT_NODE) {
                if (childNode.getNodeValue().trim().equals("")) {
                    node.removeChild(childNode);
                }
            } else if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                removeEmptyTextNodes(childNode);
            }
        }
    }
}
