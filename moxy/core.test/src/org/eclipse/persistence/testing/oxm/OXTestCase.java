/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.platform.DOMPlatform;
import org.eclipse.persistence.oxm.platform.SAXPlatform;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import org.eclipse.persistence.sessions.factories.XMLProjectWriter;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class OXTestCase extends XMLTestCase {
    public boolean useLogging = false;
    public boolean deploymentXML = false;
    public boolean useSAX = false;
    public boolean useDocPres = false;

    public OXTestCase(String name) {
        super(name);
        deploymentXML = Boolean.getBoolean("useDeploymentXML");
        useSAX = Boolean.getBoolean("useSAXParsing");
        useLogging = Boolean.getBoolean("useLogging");
        useDocPres = Boolean.getBoolean("useDocPres");
    }

    public XMLContext getXMLContext(String name) {
        Session session = SessionManager.getManager().getSession(name, false);
        Project project = session.getProject();
        return getXMLContext(project);
    }

    public XMLContext getXMLContext(Project project) {
        if (this.useDocPres) {
            java.util.Collection descriptors = project.getDescriptors().values();
            java.util.Iterator iter = descriptors.iterator();
            while (iter.hasNext()) {
                ClassDescriptor nextDesc = (ClassDescriptor)iter.next();
                if (nextDesc instanceof org.eclipse.persistence.oxm.XMLDescriptor) {
                    ((org.eclipse.persistence.oxm.XMLDescriptor)nextDesc).setShouldPreserveDocument(true);
                }
            }
        }

        Project newProject = this.getNewProject(project, null);
        return new XMLContext(newProject);
    }

    public XMLContext getXMLContext(Project project, ClassLoader classLoader) {
        ConversionManager.getDefaultManager().setLoader(classLoader);
        Project newProject = this.getNewProject(project, classLoader);
        newProject.getDatasourceLogin().getDatasourcePlatform().getConversionManager().setLoader(classLoader);
        return new XMLContext(newProject);
    }

    public Project getNewProject(Project originalProject) {
        return getNewProject(originalProject, null);
    }

    public Project getNewProject(Project originalProject, ClassLoader classLoader) {
        if (deploymentXML) {
            Project newProject = null;
            try {
                //write the deployment XML file to deploymentXML-file.xml
                XMLProjectWriter writer = new XMLProjectWriter();
                String fileName = "deploymentXML-file.xml";
                java.io.FileWriter fWriter = new FileWriter(fileName);
                writer.write(originalProject, fWriter);
                fWriter.close();

                //also write the deployment XML file to a stringwriter for logging
                if (useLogging) {
                    StringWriter stringWriter = new StringWriter();
                    XMLProjectWriter.write(originalProject, stringWriter);
                    log("DEPLOYMENT XML " + stringWriter.toString());
                }

                //read the deploymentXML-file.xml back in with XMLProjectReader							
                FileInputStream inStream = new FileInputStream(fileName);
                FileReader fileReader = new FileReader(fileName);
                XMLProjectReader projectReader = new XMLProjectReader();
                newProject = projectReader.read(fileReader, classLoader);
                inStream.close();
                fileReader.close();
                File f = new File(fileName);
                f.delete();

            } catch (Exception e) {
                e.printStackTrace();
                StringWriter stringWriter = new StringWriter();
                XMLProjectWriter writer = new XMLProjectWriter();
                String fileName = "";
                writer.write(originalProject, stringWriter);

                StringReader reader = new StringReader(stringWriter.toString());

                log("DEPLOYMENT XML" + stringWriter.toString());
                XMLProjectReader projectReader = new XMLProjectReader();
                newProject = projectReader.read(reader, classLoader);

            }

            if ((newProject.getDatasourceLogin() == null) || (!(newProject.getDatasourceLogin() instanceof XMLLogin))) {
                newProject.setDatasourceLogin(new XMLLogin());
            }
            newProject.getDatasourceLogin().setPlatform(new DOMPlatform());
            return newProject;

        } else {
            if ((originalProject.getDatasourceLogin() == null) || (!(originalProject.getDatasourceLogin() instanceof XMLLogin))) {
                originalProject.setDatasourceLogin(new XMLLogin());
            }
            if (useSAX) {
                originalProject.getDatasourceLogin().setPlatform(new SAXPlatform());
            } else {
                originalProject.getDatasourceLogin().setPlatform(new DOMPlatform());
            }
            return originalProject;
        }
    }

    protected void log(Document document) {
        if (!useLogging) {
            return;
        }

        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(System.out);

            //transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            //transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, false);
            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void log(String string) {
        if (useLogging) {
            System.out.println(string);
        }
    }

    protected void log(byte[] bytes) {
        if (useLogging) {
            for (int i = 0; i < bytes.length; i++) {
                System.out.print(bytes[i]);
            }
        }
    }

    public String getName() {
        String longClassName = this.getClass().getName();
        String shortClassName = longClassName.substring(longClassName.lastIndexOf(".") + 1, longClassName.length() - 1);
        if (useDocPres) {
            return "Doc Pres:" + shortClassName + ": " + super.getName();
        } else if (deploymentXML) {
            return "Deployment XML:" + shortClassName + ": " + super.getName();
        } else if (useSAX) {
            return "SAX Parsing: " + shortClassName + ": " + super.getName();
        }
        return shortClassName + ": " + super.getName();

    }

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

    protected String removeWhiteSpaceFromString(String s) {
        String returnString = s.replaceAll(" ", "");
        returnString = returnString.replaceAll("\n", "");
        returnString = returnString.replaceAll("\t", "");
        returnString = returnString.replaceAll("\r", "");

        return returnString;
    }
}