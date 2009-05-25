/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
package org.eclipse.persistence.jaxb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.xml.bind.Binder;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Validator;
import javax.xml.namespace.QName;
import java.util.HashMap;

import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.internal.jaxb.JAXBSchemaOutputResolver;
import org.eclipse.persistence.internal.oxm.schema.SchemaModelGenerator;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jaxb.compiler.Generator;
import org.eclipse.persistence.jaxb.compiler.MarshalCallback;
import org.eclipse.persistence.jaxb.compiler.UnmarshalCallback;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>Provide a TopLink implementation of the JAXBContext interface.
 * <p><b>Responsibilities:</b><ul>
 * <li>Create Marshaller instances</li>
 * <li>Create Unmarshaller instances</li>
 * <li>Create Binder instances</li>
 * <li>Create Introspector instances</li>
 * <li>Create Validator instances</li>
 * <li>Generate Schema Files</li>
 * <ul>
 * <p>This is the TopLink JAXB 2.0 implementation of javax.xml.bind.JAXBContext. This class
 * is created by the JAXBContextFactory and is used to create Marshallers, Unmarshallers, Validators,
 * Binders and Introspectors. A JAXBContext can also be used to create Schema Files.
 * 
 * @see javax.xml.bind.JAXBContext
 * @see org.eclipse.persistence.jaxb.JAXBMarshaller
 * @see org.eclipse.persistence.jaxb.JAXBUnmarshaller
 * @see org.eclipse.persistence.jaxb.JAXBBinder
 * @see org.eclipse.persistence.jaxb.JAXBIntrospector
 * 
 * @author mmacivor
 * @since Oracle TopLink 11.1.1.0.0
 */

public class JAXBContext extends javax.xml.bind.JAXBContext {
    private XMLContext xmlContext;
    private org.eclipse.persistence.jaxb.compiler.Generator generator;    
    private HashMap<QName, Class> qNameToGeneratedClasses;
    private HashMap<QName, Class> qNamesToDeclaredClasses;
    
    public JAXBContext(XMLContext context) {
        super();
        xmlContext = context;
    }
    
    public JAXBContext(XMLContext context, Generator generator) {
        super();
        this.xmlContext = context;
        this.generator = generator;
        this.qNameToGeneratedClasses = generator.getMappingsGenerator().getQNamesToGeneratedClasses();
        this.qNamesToDeclaredClasses = generator.getMappingsGenerator().getQNamesToDeclaredClasses();
    }
    
    public XMLContext getXMLContext() {
        return this.xmlContext;
    }
    
    public void generateSchema(SchemaOutputResolver outputResolver) {
        if (generator == null) {
            List<XMLDescriptor> descriptorsToProcess = new ArrayList<XMLDescriptor>();
            List<Session> sessions = xmlContext.getSessions();
            for (Session session : sessions) {
                Vector<XMLDescriptor> descriptors = session.getProject().getOrderedDescriptors();
                for (XMLDescriptor xDesc : descriptors) {
                    descriptorsToProcess.add(xDesc);
                }
            }
            SchemaModelGenerator smGen = new SchemaModelGenerator();
            smGen.generateSchemas(descriptorsToProcess, null, new JAXBSchemaOutputResolver(outputResolver));
        } else {
            generator.generateSchemaFiles(outputResolver, null);
        }
    }
    
    public Marshaller createMarshaller() {
    	// create a JAXBIntrospector and set it on the marshaller
        JAXBMarshaller marshaller = new JAXBMarshaller(xmlContext.createMarshaller(), new JAXBIntrospector(xmlContext));
        if (generator != null && generator.hasMarshalCallbacks()) {
            // initialize each callback in the map
            for (Iterator callIt = generator.getMarshalCallbacks().keySet().iterator(); callIt.hasNext(); ) {
                MarshalCallback cb = (MarshalCallback) generator.getMarshalCallbacks().get(callIt.next());
                // TODO:  what classloader do we want to use here?
                cb.initialize(generator.getClass().getClassLoader());
            }
            marshaller.setMarshalCallbacks(generator.getMarshalCallbacks());
        }
        marshaller.setQNameToGeneratedClasses(this.qNameToGeneratedClasses);
        return marshaller;
    }

    public Unmarshaller createUnmarshaller() {
        JAXBUnmarshaller unmarshaller = new JAXBUnmarshaller(xmlContext.createUnmarshaller());
        if (generator != null && generator.hasUnmarshalCallbacks()) {
            // initialize each callback in the map
            for (Iterator callIt = generator.getUnmarshalCallbacks().keySet().iterator(); callIt.hasNext(); ) {
                UnmarshalCallback cb = (UnmarshalCallback) generator.getUnmarshalCallbacks().get(callIt.next());
                // TODO:  what classloader do we want to use here?
                cb.initialize(generator.getClass().getClassLoader());
            }
            unmarshaller.setUnmarshalCallbacks(generator.getUnmarshalCallbacks());
        }        
        unmarshaller.setQNamesToDeclaredClasses(this.qNamesToDeclaredClasses);
        return unmarshaller;
    }

    public Validator createValidator() {
        return new JAXBValidator(xmlContext.createValidator());
    }
    
    public Binder createBinder() {
        return new JAXBBinder(this.xmlContext);
    }
    
    public Binder createBinder(Class nodeClass) {
    	if(nodeClass.getName().equals("org.w3c.dom.Node")){
    		return new JAXBBinder(this.xmlContext);
    	}else{
    		throw new UnsupportedOperationException(JAXBException.unsupportedNodeClass(nodeClass.getName()));
    	}
    }
    
    public JAXBIntrospector createJAXBIntrospector() {
        return new JAXBIntrospector(xmlContext);
    }      
    
    public void setQNameToGeneratedClasses(HashMap<QName, Class> qNameToClass) {
    	this.qNameToGeneratedClasses = qNameToClass;
    }
    
    /**
     * ADVANCED:
     * Adjust the OXM metadata to take into accound ORM mapping metadata,
     */
     public void applyORMMetadata(AbstractSession ormSession) {
    	 this.xmlContext.applyORMMetadata(ormSession);
     }

	public HashMap<QName, Class> getQNamesToDeclaredClasses() {
		return qNamesToDeclaredClasses;
	}

	public void setQNamesToDeclaredClasses(
			HashMap<QName, Class> nameToDeclaredClasses) {
		qNamesToDeclaredClasses = nameToDeclaredClasses;
	}
    
}
