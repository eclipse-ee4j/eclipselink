/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink

package org.eclipse.persistence.internal.dbws;

// Javase imports
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import static jakarta.xml.soap.SOAPConstants.SOAP_1_2_PROTOCOL;
import static jakarta.xml.soap.SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE;
import static jakarta.xml.soap.SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE;
import static org.eclipse.persistence.internal.oxm.Constants.BASE_64_BINARY;
import static org.eclipse.persistence.internal.oxm.Constants.SCHEMA_INSTANCE_PREFIX;
import static org.eclipse.persistence.internal.oxm.Constants.SCHEMA_PREFIX;
import static org.eclipse.persistence.internal.xr.Util.SERVICE_NAMESPACE_PREFIX;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jakarta.activation.DataHandler;
import javax.xml.namespace.QName;
import jakarta.xml.soap.AttachmentPart;
import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;

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
    protected Map<String, XMLDescriptor> resultDescriptors = new HashMap<>();
    public static final QName RECEIVER_QNAME = new QName(URI_NS_SOAP_1_2_ENVELOPE, "Receiver");
    public static final QName SERVER_QNAME = new QName(URI_NS_SOAP_1_1_ENVELOPE, "Server");

    public SOAPResponseWriter(DBWSAdapter dbwsAdapter) {
        this.dbwsAdapter = dbwsAdapter;
    }

    @SuppressWarnings("unchecked")
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
            if (op instanceof QueryOperation queryOperation) {
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

    public SOAPMessage generateResponse(Operation op, boolean useSOAP12, Exception e) throws SOAPException {
        MessageFactory messageFactory = null;
        if (useSOAP12) {
            messageFactory = MessageFactory.newInstance(SOAP_1_2_PROTOCOL);
        } else {
            messageFactory = MessageFactory.newInstance();
        }
        SOAPMessage message = messageFactory.createMessage();
        message.getSOAPPart().getEnvelope().addNamespaceDeclaration(SCHEMA_PREFIX, W3C_XML_SCHEMA_NS_URI);
        message.getSOAPPart().getEnvelope().addNamespaceDeclaration(SCHEMA_INSTANCE_PREFIX, W3C_XML_SCHEMA_INSTANCE_NS_URI);
        SOAPBody body = message.getSOAPPart().getEnvelope().getBody();
        QName faultCodeQName = null;
        if (useSOAP12) {
            faultCodeQName = RECEIVER_QNAME;
        } else {
            faultCodeQName = SERVER_QNAME;
        }
        body.addFault(faultCodeQName, op.getName() + " failed: " + e);
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
            @SuppressWarnings({"unchecked"})
            Class<? extends SOAPResponse> cls = descriptor.getJavaClass();
            response = cls.getConstructor().newInstance();
        } catch (ReflectiveOperationException ie) {
            throw new SOAPException(ie);
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
