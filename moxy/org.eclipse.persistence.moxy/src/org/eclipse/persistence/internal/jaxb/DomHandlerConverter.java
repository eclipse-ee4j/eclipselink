/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.jaxb;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.DomHandler;
import javax.xml.transform.Source;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;

import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.jaxb.JAXBErrorHandler;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.mappings.converters.XMLConverter;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.platform.xml.XMLTransformer;
import org.eclipse.persistence.sessions.Session;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>Provide support for the JAXB DomHandler API through the use of an EclipseLink
 * Converter.
 * <p><b>Responsibilities:</b><ul>
 * <li>Wrap a provided instance of a DomHandler implementation.</li>
 * <li>Invoke the createUnmarshaller method on DomHandler and to a transform on unmarshal</li>
 * <li>Invoke the marshal method on the DomHandler on a marshal</li>
 * </ul>
 *
 * @author mmacivor
 *
 */
public class DomHandlerConverter implements XMLConverter {
    private DomHandler domHandler;
    private XMLPlatform xmlPlatform;
    private String domHandlerClassName;
    private Class elementClass;
    private Class resultType;

    public DomHandlerConverter(String domHandlerClassName) {
        this.domHandlerClassName = domHandlerClassName;
    }

    public void initialize(DatabaseMapping mapping, Session session) {
        try {
            ConversionManager cMgr = session.getDatasourcePlatform().getConversionManager();
            Class<? extends DomHandler> domHandlerClass = PrivilegedAccessHelper.getClassForName(domHandlerClassName, true, cMgr.getLoader());

            Constructor cons = PrivilegedAccessHelper.getDeclaredConstructorFor(domHandlerClass, new Class[]{}, true);
            this.domHandler = (DomHandler)PrivilegedAccessHelper.invokeConstructor(cons, new Object[]{});

            Method createUnmarshallerMethod = PrivilegedAccessHelper.getDeclaredMethod(domHandlerClass, "createUnmarshaller", new Class[]{ValidationEventHandler.class});
            resultType = PrivilegedAccessHelper.getMethodReturnType(createUnmarshallerMethod);

            Method getElementMethod = PrivilegedAccessHelper.getDeclaredMethod(domHandlerClass, "getElement", new Class[]{resultType});
            elementClass = PrivilegedAccessHelper.getMethodReturnType(getElementMethod);

            xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
        } catch(Exception ex) {
            throw JAXBException.couldNotInitializeDomHandlerConverter(ex, domHandlerClassName, mapping.getAttributeName());
        }
    }

    public Object convertDataValueToObjectValue(Object dataValue, Session session, XMLUnmarshaller unmarshaller) {
        if(dataValue instanceof org.w3c.dom.Element) {
            ErrorHandler handler = unmarshaller.getErrorHandler();
            Result result = null;
            if(handler != null && handler instanceof JAXBErrorHandler) {
                result = domHandler.createUnmarshaller(((JAXBErrorHandler)handler).getValidationEventHandler());
            } else {
                result = domHandler.createUnmarshaller(null);
            }
            if(result instanceof DOMResult){
            	((DOMResult) result).setNode((org.w3c.dom.Element)dataValue);
            }else{
                xmlPlatform.newXMLTransformer().transform((org.w3c.dom.Element)dataValue, result);                
            }
            return domHandler.getElement(result);       
        }
        return dataValue;
    }

    public Object convertObjectValueToDataValue(Object objectValue, Session session, XMLMarshaller marshaller) {
        if (objectValue != null && elementClass.isAssignableFrom(objectValue.getClass())) {
            ErrorHandler handler = marshaller.getErrorHandler();
            Source source = null;
            if(handler != null && handler instanceof JAXBErrorHandler) {
                source = domHandler.marshal(objectValue, ((JAXBErrorHandler)handler).getValidationEventHandler());
            } else {
                source = domHandler.marshal(objectValue, null);
            }
            DOMResult result = new DOMResult();
             if(source instanceof DOMSource){
            	Node n = ((DOMSource)source).getNode();
            	if(n.getNodeType() == Node.DOCUMENT_NODE){
            		return ((Document)n).getDocumentElement();
            	}
            	return n;
            }else{                
                XMLTransformer xmlTransformer = xmlPlatform.newXMLTransformer();
                xmlTransformer.setFormattedOutput(marshaller.isFormattedOutput());
                xmlTransformer.transform(source, result);
                return result.getNode().getFirstChild();
            }
        }
        return objectValue;
    }

    public boolean isMutable() {
        return true;
    }

    public Object convertDataValueToObjectValue(Object dataValue, Session session) {
        return convertDataValueToObjectValue(dataValue, session, null);
    }

    public Object convertObjectValueToDataValue(Object objectValue, Session session) {
        return convertObjectValueToDataValue(objectValue, session, null);
    }

}
