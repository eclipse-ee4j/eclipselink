/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.internal.dbws;

// Javase imports
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import static javax.xml.soap.SOAPConstants.SOAP_1_2_PROTOCOL;
import static javax.xml.soap.SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE;
import static javax.xml.soap.SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE;
import static org.eclipse.persistence.internal.xr.Util.SERVICE_NAMESPACE_PREFIX;
import static org.eclipse.persistence.oxm.XMLConstants.BASE_64_BINARY;
import static org.eclipse.persistence.oxm.XMLConstants.SCHEMA_PREFIX;
import static org.eclipse.persistence.oxm.XMLConstants.SCHEMA_INSTANCE_PREFIX;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.eclipse.persistence.internal.oxm.schema.model.ComplexType;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.internal.xr.Attachment;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.internal.xr.QueryOperation;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.mappings.XMLAnyObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;

public class SOAPResponseWriter {

    protected DBWSAdapter dbwsAdapter;
    protected Map<String, XMLDescriptor> resultDescriptors = new HashMap<String, XMLDescriptor>();

    public SOAPResponseWriter(DBWSAdapter dbwsAdapter) {
        this.dbwsAdapter = dbwsAdapter;
    }

    public void initialize() {
        SOAPResponseClassLoader loader =
            new SOAPResponseClassLoader(Thread.currentThread().getContextClassLoader());
        NamespaceResolver nr = new NamespaceResolver();
        nr.put(SERVICE_NAMESPACE_PREFIX, dbwsAdapter.getExtendedSchema().getTargetNamespace());
        for (Operation op : dbwsAdapter.getOperationsList()) {
            String className = op.getName() + "_Response";
            Class<?> opClass = loader.buildClass(className);
            XMLDescriptor descriptor = new XMLDescriptor();
            descriptor.setDefaultRootElement(SERVICE_NAMESPACE_PREFIX + ":" + op.getName() + "Response");
            descriptor.setNamespaceResolver(nr);
            descriptor.setJavaClass(opClass);
            if (op instanceof QueryOperation) {
                QueryOperation queryOperation = (QueryOperation)op;
                if (queryOperation.isSimpleXMLFormat()) {
                    XMLAnyObjectMapping mapping = new XMLAnyObjectMapping();
                    mapping.setUseXMLRoot(true);
                    mapping.setAttributeName("result");
                    mapping.setXPath(SERVICE_NAMESPACE_PREFIX + ":" + "result");
                    descriptor.addMapping(mapping);
                    mapping.initialize((AbstractSession)dbwsAdapter.getOXSession());
                }
                else if (queryOperation.isAttachment()) {
                    Attachment attachment = queryOperation.getResult().getAttachment();
                    XMLBinaryDataMapping mapping = new XMLBinaryDataMapping();
                    mapping.setAttributeName("result");
                    mapping.setXPath(SERVICE_NAMESPACE_PREFIX + ":" + "result");
                    mapping.setSwaRef(true);
                    mapping.setShouldInlineBinaryData(false);
                    mapping.setMimeType(attachment.getMimeType());
                    descriptor.addMapping(mapping);
                }
                else {
                    QName type = queryOperation.getResult().getType();
                    String localElement = type.getLocalPart();
                    // look for top-level complex types
                    Set<Map.Entry<String, ComplexType>> entrySet =
                        dbwsAdapter.getSchema().getTopLevelComplexTypes().entrySet();
                    for (Map.Entry<String, ComplexType> me : entrySet) {
                        if (me.getValue().getName().equals(type.getLocalPart())) {
                            localElement = me.getKey();
                            break;
                        }
                    }
                    XMLDescriptor typeDescriptor =
                        dbwsAdapter.getDescriptorsByQName().get(type);
                    if (typeDescriptor != null) {
                        if (queryOperation.isCollection()) {
                            XMLCompositeCollectionMapping mapping =
                                new XMLCompositeCollectionMapping();
                            mapping.setAttributeName("result");
                            mapping.setReferenceClass(typeDescriptor.getJavaClass());
                            mapping.useCollectionClass(ArrayList.class);
                            mapping.setXPath(SERVICE_NAMESPACE_PREFIX + ":" + "result/" + localElement);
                            descriptor.getNamespaceResolver().setDefaultNamespaceURI(
                                typeDescriptor.getNamespaceResolver().getDefaultNamespaceURI());
                            descriptor.addMapping(mapping);
                            mapping.initialize((AbstractSession)dbwsAdapter.getOXSession());
                        }
                        else {
                            XMLCompositeObjectMapping mapping = new XMLCompositeObjectMapping();
                            mapping.setAttributeName("result");
                            mapping.setReferenceClass(typeDescriptor.getJavaClass());
                            mapping.setXPath(SERVICE_NAMESPACE_PREFIX + ":" + "result/" + localElement);
                            descriptor.getNamespaceResolver().setDefaultNamespaceURI(
                                typeDescriptor.getNamespaceResolver().getDefaultNamespaceURI());
                            descriptor.addMapping(mapping);
                            mapping.initialize((AbstractSession)dbwsAdapter.getOXSession());
                        }
                    }
                    else {
                        if (type.equals(new QName(W3C_XML_SCHEMA_NS_URI, "any"))) {
                            XMLAnyObjectMapping mapping = new XMLAnyObjectMapping();
                            mapping.setAttributeName("result");
                            mapping.setXPath(SERVICE_NAMESPACE_PREFIX + ":" + "result");
                            descriptor.addMapping(mapping);
                        }
                        else if (type.equals(new QName(W3C_XML_SCHEMA_NS_URI, BASE_64_BINARY))) {
                            XMLBinaryDataMapping mapping = new XMLBinaryDataMapping();
                            mapping.setAttributeName("result");
                            mapping.setXPath(SERVICE_NAMESPACE_PREFIX + ":" + "result");
                            mapping.setShouldInlineBinaryData(true);
                            ((XMLField)mapping.getField()).setSchemaType(type);
                            descriptor.addMapping(mapping);
                        }
                        else {
                            XMLDirectMapping mapping = new XMLDirectMapping();
                            mapping.setAttributeName("result");
                            mapping.setXPath(SERVICE_NAMESPACE_PREFIX + ":" + "result/text()");
                            descriptor.addMapping(mapping);
                        }
                    }
                }
            }
            dbwsAdapter.getOXSession().getProject().addDescriptor(descriptor);
            ((DatabaseSessionImpl)dbwsAdapter.getOXSession())
                .initializeDescriptorIfSessionAlive(descriptor);
            dbwsAdapter.getXMLContext().storeXMLDescriptorByQName(descriptor);
            resultDescriptors.put(op.getName(), descriptor);
        }
    }

    public SOAPMessage generateResponse(Operation op, boolean useSOAP12, Exception e)
    throws SOAPException {
        MessageFactory messageFactory = null;
        if (useSOAP12) {
            messageFactory = MessageFactory.newInstance(SOAP_1_2_PROTOCOL);
        }
        else {
            messageFactory = MessageFactory.newInstance();
        }
        SOAPMessage message = messageFactory.createMessage();
        message.getSOAPPart().getEnvelope().addNamespaceDeclaration(SCHEMA_PREFIX, W3C_XML_SCHEMA_NS_URI);
        message.getSOAPPart().getEnvelope().addNamespaceDeclaration(SCHEMA_INSTANCE_PREFIX, W3C_XML_SCHEMA_INSTANCE_NS_URI);
        SOAPBody body = message.getSOAPPart().getEnvelope().getBody();
        QName serverQName = null;
        if (useSOAP12) {
            serverQName = new QName(URI_NS_SOAP_1_2_ENVELOPE, "Server");
        }
        else {
            serverQName = new QName(URI_NS_SOAP_1_1_ENVELOPE, "Server");
        }
        body.addFault(serverQName, op.getName() + " failed: " + e);
        return message;
    }

    public SOAPMessage generateResponse(Operation op, boolean useSOAP12, Object result) throws SOAPException {
        MessageFactory messageFactory = null;
        if (useSOAP12) {
            messageFactory = MessageFactory.newInstance(SOAP_1_2_PROTOCOL);
        }
        else {
            messageFactory = MessageFactory.newInstance();
        }
        SOAPMessage message = messageFactory.createMessage();
        message.getSOAPPart().getEnvelope().addNamespaceDeclaration(SCHEMA_PREFIX, W3C_XML_SCHEMA_NS_URI);
        message.getSOAPPart().getEnvelope().addNamespaceDeclaration(SCHEMA_INSTANCE_PREFIX, W3C_XML_SCHEMA_INSTANCE_NS_URI);
        SOAPBody body = message.getSOAPPart().getEnvelope().getBody();

        XMLDescriptor descriptor = resultDescriptors.get(op.getName());
        SOAPResponse response = null;
        try {
            response = (SOAPResponse) descriptor.getJavaClass().newInstance();
        } catch (InstantiationException ie) {
            throw new SOAPException(ie);
        } catch (IllegalAccessException iae) {
            throw new SOAPException(iae);
        }
        response.setResult(result);

        SOAPAttachmentHandler attachmentHandler = new SOAPAttachmentHandler();
        XMLMarshaller marshaller = dbwsAdapter.getXMLContext().createMarshaller();
        marshaller.setAttachmentMarshaller(attachmentHandler);
        marshaller.marshal(response, body);

        if (attachmentHandler.hasAttachments()) {
            // add attachments to message
            for (String id : attachmentHandler.getAttachments().keySet()) {
                DataHandler attachment = attachmentHandler.getAttachments().get(id);
                AttachmentPart part = message.createAttachmentPart(attachment);
                part.setContentType(attachment.getContentType());
                String contentId = "<" + id.substring(4) + ">";
                part.setContentId(contentId);
                part.setMimeHeader("Content-Transfer-Encoding", "binary");
                message.addAttachmentPart(part);
            }
        }
        message.saveChanges();

        return message;
    }
}