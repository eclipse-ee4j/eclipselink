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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.bind.JAXBElement;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.platform.DOMPlatform;
import org.eclipse.persistence.oxm.platform.SAXPlatform;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.sessions.factories.MissingDescriptorListener;
import org.eclipse.persistence.internal.sessions.factories.ObjectPersistenceRuntimeXMLProject_11_1_1;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import org.eclipse.persistence.sessions.factories.XMLProjectWriter;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class OXTestCase extends XMLTestCase {
    protected static XMLInputFactory XML_INPUT_FACTORY;
    protected static XMLOutputFactory XML_OUTPUT_FACTORY;
    protected static Class staxResultClass;
    protected static String staxResultClassName = "javax.xml.transform.stax.StAXResult";
    protected static Class staxSourceClass;
    protected static String staxSourceClassName = "javax.xml.transform.stax.StAXSource";
    protected static Constructor staxResultStreamWriterConstructor;
    protected static Constructor staxResultEventWriterConstructor;
    protected static Constructor staxSourceStreamReaderConstructor;
    protected static Constructor staxSourceEventReaderConstructor;

    static {
        try {
            XML_INPUT_FACTORY = XMLInputFactory.newInstance();
            XML_OUTPUT_FACTORY = XMLOutputFactory.newInstance();
        } catch(javax.xml.stream.FactoryConfigurationError error){
            XML_INPUT_FACTORY = null;
            XML_OUTPUT_FACTORY = null;
        }
        try {
            staxResultClass = PrivilegedAccessHelper.getClassForName(staxResultClassName);
            staxResultStreamWriterConstructor = PrivilegedAccessHelper.getConstructorFor(staxResultClass, new Class[]{XMLStreamWriter.class}, true);
            staxResultEventWriterConstructor = PrivilegedAccessHelper.getConstructorFor(staxResultClass, new Class[]{XMLEventWriter.class}, true);
        } catch(Exception ex) {
            staxResultClass = null;
        }
	    	    
        try {
            staxSourceClass = PrivilegedAccessHelper.getClassForName(staxSourceClassName);
            staxSourceStreamReaderConstructor = PrivilegedAccessHelper.getConstructorFor(staxSourceClass, new Class[]{XMLStreamReader.class}, true);
            staxSourceEventReaderConstructor = PrivilegedAccessHelper.getConstructorFor(staxSourceClass, new Class[]{XMLEventReader.class}, true);
        } catch(Exception ex) {
            staxSourceClass = null;
        }
    }
	
    public boolean useLogging = false;    
    public static enum Platform { DOM, SAX, DOC_PRES };
    public static enum Metadata { JAVA, XML_TOPLINK, XML_ECLIPSELINK };
    public static Platform platform;;
    public static Metadata metadata;
    // Constants
    public static final String PLATFORM_KEY = "platformType";
    public static final String PLATFORM_DOM = "DOM";
    public static final String PLATFORM_DOC_PRES = "DOC_PRES";
    public static final String PLATFORM_SAX = "SAX";
    public static final String METADATA_KEY = "metadataType";
    public static final String METADATA_JAVA = "JAVA";
    public static final String METADATA_TOPLINK = "XML_TOPLINK";
    public static final String METADATA_XML_ECLIPSELINK = "XML_ECLIPSELINK";
    
    public OXTestCase(String name) {
        super(name);
        useLogging = Boolean.getBoolean("useLogging");
        platform = getPlatform();
        metadata = getMetadata();
    }
    
    public XMLContext getXMLContext(String name) {
        Session session = SessionManager.getManager().getSession(name, false);
        Project project = session.getProject();
        return getXMLContext(project);
    }

    public XMLContext getXMLContext(Project project) {
        if (platform == Platform.DOC_PRES) {
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
        Project newProject = originalProject;
        
        switch (metadata) {
		case JAVA:
			break;
		default:
            try {
                // Write the deployment XML file to deploymentXML-file.xml
                String fileName = "deploymentXML-file.xml";
                FileWriter fWriter = new FileWriter(fileName);
                write(originalProject, fWriter);
                fWriter.close();
                // Also write the deployment XML file to a stringwriter for logging
                if (useLogging) {
                    StringWriter stringWriter = new StringWriter();
                    write(originalProject, stringWriter);
                    log("DEPLOYMENT XML " + stringWriter.toString());
                }
                // Read the deploymentXML-file.xml back in with XMLProjectReader							
                FileInputStream inStream = new FileInputStream(fileName);
                FileReader fileReader = new FileReader(fileName);
                newProject = XMLProjectReader.read(fileReader, classLoader);
                inStream.close();
                fileReader.close();
                File f = new File(fileName);
                f.delete();
            } catch (Exception e) {
                e.printStackTrace();
                StringWriter stringWriter = new StringWriter();
                write(originalProject, stringWriter);
                StringReader reader = new StringReader(stringWriter.toString());
                log("DEPLOYMENT XML" + stringWriter.toString());
                newProject = XMLProjectReader.read(reader, classLoader);
            }
		}

        if ((newProject.getDatasourceLogin() == null) || (!(newProject.getDatasourceLogin() instanceof XMLLogin))) {
        	newProject.setDatasourceLogin(new XMLLogin());
        }
        
        switch (platform) {
        case SAX: 
        	newProject.getDatasourceLogin().setPlatform(new SAXPlatform());
        	break;
        default: 
        	newProject.getDatasourceLogin().setPlatform(new DOMPlatform());
        }
        return newProject;
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
        String description = "";
        
        switch (metadata) {
		case XML_ECLIPSELINK:
        	description = "Deployment XML w/";
			break;
		case XML_TOPLINK:
        	description = "TL Deployment XML w/";
			break;
		default:
        	description = "Java Project Source w/";
		}

        switch (platform) {
        case DOC_PRES:
        	description += "Doc Pres: ";
        	break;
        case DOM:
        	description += "DOM Parsing: ";
        	break;
        default:
        	description += "SAX Parsing: ";
        }

        return description + shortClassName + ": " + super.getName();
    }

    public static void removeEmptyTextNodes(Node node) {
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

    public static String removeWhiteSpaceFromString(String s) {
        String returnString = s.replaceAll(" ", "");
        returnString = returnString.replaceAll("\n", "");
        returnString = returnString.replaceAll("\t", "");
        returnString = returnString.replaceAll("\r", "");

        return returnString;
    }
    
    /**
     * Return the Platform to be used based on the "platformType" 
     * System property
     * 
     * @see Platform
     */
    public Platform getPlatform() {
        String platformStr = System.getProperty(PLATFORM_KEY, PLATFORM_SAX);
        if (platformStr.equals(PLATFORM_SAX)) {
        	return Platform.SAX;
        }
        if (platformStr.equals(PLATFORM_DOM)) {
        	return Platform.DOM;
        }
        return Platform.DOC_PRES; 
    }

    /**
     * Return the Metadata type based on the "metadataType" 
     * System property
     * 
     * @see Metadata
     */
    public Metadata getMetadata() {
        String metadataStr = System.getProperty(METADATA_KEY, METADATA_JAVA);
        if (metadataStr.equals(METADATA_JAVA)) {
        	return Metadata.JAVA;
        }
        if (metadataStr.equals(METADATA_XML_ECLIPSELINK)) {
        	return Metadata.XML_ECLIPSELINK;
        }
        return Metadata.XML_TOPLINK;
    }
    
    // Write out deployment XML
    public void write(Project project, Writer writer) {
    	// Write out EclipseLink deployment XML
    	if (metadata == Metadata.XML_ECLIPSELINK) {
    		XMLProjectWriter.write(project, writer);
    		return;
    	}
    	// Write out TL deployment XML 
        XMLContext context = new XMLContext(new ObjectPersistenceRuntimeXMLProject_11_1_1());
        context.getSession(project).getEventManager().addListener(new MissingDescriptorListener());
        XMLMarshaller marshaller = context.createMarshaller();
        marshaller.marshal(project, writer);
        try {
            writer.flush();
        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }
    }
    
    public void compareJAXBElementObjects(JAXBElement controlObj, JAXBElement testObj) {
        assertEquals(controlObj.getName().getLocalPart(), testObj.getName().getLocalPart());
        assertEquals(controlObj.getName().getNamespaceURI(), testObj.getName().getNamespaceURI());
        assertEquals(controlObj.getDeclaredType(), testObj.getDeclaredType());

        Object controlValue = controlObj.getValue();
        Object testValue = testObj.getValue();

        if(controlValue == null) {
            if(testValue == null){
                return;
            }
            fail("Test value should have been null");
        }else{
            if(testValue == null){
                fail("Test value should not have been null");
            }
        }

        if(controlValue.getClass().isArray()){
            compareArrays(controlValue, testValue);
        }
        else if (controlValue instanceof Collection){
            Collection controlCollection = (Collection)controlValue;
            Collection testCollection = (Collection)testValue;
            Iterator<Object> controlIter = controlCollection.iterator();
            Iterator<Object> testIter = testCollection.iterator();
            assertEquals(controlCollection.size(), testCollection.size());
            while(controlIter.hasNext()){
                Object nextControl = controlIter.next();
                Object nextTest = testIter.next();
                compareValues(nextControl, nextTest);
            }
        }else{
            compareValues(controlValue, testValue);
        }
    }
  

    protected void compareArrays(Object controlValue, Object testValue) {
        assertTrue("Test array is not an Array", testValue.getClass().isArray());
        int controlSize = Array.getLength(controlValue);
        assertTrue("Control and test arrays are not the same length", controlSize == Array.getLength(testValue));
        for(int x=0; x<controlSize; x++) {
            Object controlItem = Array.get(controlValue, x);
            Object testItem = Array.get(testValue, x);
            if(null == controlItem) {
                assertEquals(null, testItem);
                Class controlItemClass = controlItem.getClass();
                if(controlItemClass.isArray()) {
                    compareArrays(controlItem, testItem);
                } else {
                    assertEquals(controlItem, testItem);
                }
            }
        }
    }
    
    protected String loadFileToString(String fileName){
        StringBuffer sb = new StringBuffer();
        try {            
            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str;
            while (bufferedReader.ready()) {
                sb.append(bufferedReader.readLine());
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
        return sb.toString();
    }
}
