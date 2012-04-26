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
package org.eclipse.persistence.jaxb;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.helpers.DefaultValidationEventHandler;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.validation.Schema;

import java.lang.reflect.Type;

import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

import org.eclipse.persistence.oxm.CharacterEscapeHandler;
import org.eclipse.persistence.oxm.MediaType;
import org.eclipse.persistence.oxm.NamespacePrefixMapper;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.record.MarshalRecord;
import org.eclipse.persistence.oxm.record.XMLEventWriterRecord;
import org.eclipse.persistence.oxm.record.XMLStreamWriterRecord;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.jaxb.many.ManyValue;
import org.eclipse.persistence.internal.jaxb.WrappedValue;
import org.eclipse.persistence.internal.oxm.record.CharacterEscapeHandlerWrapper;
import org.eclipse.persistence.internal.oxm.record.namespaces.MapNamespacePrefixMapper;
import org.eclipse.persistence.internal.oxm.record.namespaces.NamespacePrefixMapperWrapper;

import org.eclipse.persistence.jaxb.JAXBContext.RootLevelXmlAdapter;
import org.eclipse.persistence.jaxb.attachment.*;

/**
 * INTERNAL:
 * <p>
 * <b>Purpose:</b>To Provide an implementation of the JAXB 2.0 Marshaller
 * Interface
 * <p>
 * <b>Responsibilities:</b>
 * <ul>
 * <li>Provide a JAXB wrapper on the XMLMarshaller API</li>
 * <li>Perform Object to XML Conversions</li>
 * </ul>
 * <p>
 * This implementation of the JAXB 2.0 Marshaller interface provides the
 * required functionality by acting as a thin wrapper on the existing
 * XMLMarshaller API.
 * 
 * @author mmacivor
 * @since Oracle TopLink 11.1.1.0.0
 * @see javax.xml.bind.Marshaller
 * @see org.eclipse.persistence.oxm.XMLMarshaller
 */

public class JAXBMarshaller implements javax.xml.bind.Marshaller {

    private ValidationEventHandler validationEventHandler;
    private XMLMarshaller xmlMarshaller;
    private JAXBContext jaxbContext;

    public static final String XML_JAVATYPE_ADAPTERS = "xml-javatype-adapters";

    /**
     * The Constant NAMESPACE_PREFIX_MAPPER. Provides a means to customize the namespace prefixes used 
     * while marshalling to XML.  Used for both marshal and unmarshal when mediaType is set to "application/json".
     * Value is either a Map<String, String> of URIs to prefixes, or an implementation of 
     * org.eclipse.persistence.oxm.NamespacePrefixMapper.
     * @since 2.3.3 
     */
    public static final String NAMESPACE_PREFIX_MAPPER = JAXBContext.NAMESPACE_PREFIX_MAPPER;
    private static final String SUN_NAMESPACE_PREFIX_MAPPER = "com.sun.xml.bind.namespacePrefixMapper";
    private static final String SUN_JSE_NAMESPACE_PREFIX_MAPPER = "com.sun.xml.internal.bind.namespacePrefixMapper";

    /**
     * The Constant INDENT_STRING. Property used to set the string used when indenting formatted marshalled documents.
     * The default for formatted documents is &quot;   &quot; (three spaces).
     * @since 2.3.3
     */
    public static final String INDENT_STRING = "eclipselink.indent-string";
    private static final String SUN_INDENT_STRING = "com.sun.xml.bind.indentString";
    private static final String SUN_JSE_INDENT_STRING = "com.sun.xml.internal.bind.indentString";

    /**
     * The Constant CHARACTER_ESCAPE_HANDLER.  Allows for customization of character escaping when marshalling.
     * Value should be an implementation of org.eclipse.persistence.oxm.CharacterEscapeHandler.
     * @since 2.3.3
     */
    public static final String CHARACTER_ESCAPE_HANDLER = "eclipselink.character-escape-handler";
    private static final String SUN_CHARACTER_ESCAPE_HANDLER = "com.sun.xml.bind.marshaller.CharacterEscapeHandler";
    private static final String SUN_JSE_CHARACTER_ESCAPE_HANDLER = "com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler";

    // XML_DECLARATION is the "opposite" to JAXB_FRAGMENT.  If XML_DECLARATION is set to false it means JAXB_FRAGMENT should be set to true.
    private static final String XML_DECLARATION = "com.sun.xml.bind.xmlDeclaration";

    /**
     * The Constant MEDIA_TYPE. This can be used to set the media type.
     * Supported values are "application/xml" and "application/json".
     * @since 2.4
     */
    public static final String MEDIA_TYPE = JAXBContext.MEDIA_TYPE;

    /**
     * The Constant ID_RESOLVER.  This can be used to specify a custom
     * IDResolver class, to allow for customization of ID/IDREF processing.
     * @since 2.3.3
     */
    public static final String ID_RESOLVER = JAXBContext.ID_RESOLVER;

    /**
     * The Constant JSON_ATTRIBUTE_PREFIX. This can be used to specify a prefix to prepend
     * to attributes.  Only applicable if media type is "application/json".
     * @since 2.4
     */
    public static final String JSON_ATTRIBUTE_PREFIX = JAXBContext.JSON_ATTRIBUTE_PREFIX;

    /**
     * The Constant JSON_INCLUDE_ROOT. This can be used  to specify if the
     * @XmlRootElement should be marshalled.  Only applicable if media type is "application/json".
     * @since 2.4
     */
    public static final String JSON_INCLUDE_ROOT = JAXBContext.JSON_INCLUDE_ROOT;

    /**
     * The Constant JSON_VALUE_WRAPPER.  This can be used to specify the wrapper
     * that will be used around things mapped with @XmlValue.  Only applicable if media type is "application/json".
     * @since 2.4
     */
    public static final String JSON_VALUE_WRAPPER = JAXBContext.JSON_VALUE_WRAPPER;

    /**
     * This constructor initializes various settings on the XML marshaller, and
     * stores the provided JAXBIntrospector instance for usage in marshal()
     * calls.
     * 
     * @param newXMLMarshaller
     * @param newIntrospector
     */
    public JAXBMarshaller(XMLMarshaller newXMLMarshaller, JAXBIntrospector newIntrospector) {
        super();
        validationEventHandler = new DefaultValidationEventHandler();
        xmlMarshaller = newXMLMarshaller;
        xmlMarshaller.setEncoding("UTF-8");
        xmlMarshaller.setFormattedOutput(false);
        JAXBMarshalListener listener = new JAXBMarshalListener(this);
        xmlMarshaller.setMarshalListener(listener);
        xmlMarshaller.getProperties().put(XMLConstants.JAXB_MARSHALLER, this);
    }

    /**
     * Create an instance of XMLRoot populated from the contents of the provided
     * JAXBElement. XMLRoot will be used to hold the contents of the JAXBElement
     * while the marshal operation is performed by TopLink OXM. This will avoid
     * adding any runtime dependencies to TopLink.
     * 
     * @param elt
     * @return
     */
    private XMLRoot createXMLRootFromJAXBElement(JAXBElement elt) {
        // create an XMLRoot to hand into the marshaller
        XMLRoot xmlroot = new XMLRoot();
        Object objectValue = elt.getValue();
        xmlroot.setObject(objectValue);
        QName qname = elt.getName();
        xmlroot.setLocalName(qname.getLocalPart());
        xmlroot.setNamespaceURI(qname.getNamespaceURI());
        xmlroot.setDeclaredType(elt.getDeclaredType());
        xmlroot.setNil(elt.isNil());
        if (elt.getDeclaredType() == ClassConstants.ABYTE || elt.getDeclaredType() == ClassConstants.APBYTE || 
                elt.getDeclaredType().getCanonicalName().equals("javax.activation.DataHandler") ||
                elt.getDeclaredType().isEnum()) {
            // need a binary data mapping so need to wrap
            Class generatedClass = getClassToGeneratedClasses().get(elt.getDeclaredType().getCanonicalName());
            if(!elt.getDeclaredType().isEnum()) {
                xmlroot.setSchemaType(XMLConstants.BASE_64_BINARY_QNAME);
            }
            if (generatedClass != null && WrappedValue.class.isAssignableFrom(generatedClass)) {
                ClassDescriptor desc = xmlMarshaller.getXMLContext().getSession(generatedClass).getDescriptor(generatedClass);
                Object newObject = desc.getInstantiationPolicy().buildNewInstance();
                ((WrappedValue) newObject).setValue(objectValue);
                xmlroot.setObject(newObject);
                return xmlroot;
            }
        } else {
            xmlroot.setSchemaType((QName) org.eclipse.persistence.internal.oxm.XMLConversionManager.getDefaultJavaTypes().get(elt.getDeclaredType()));
        }

        if (elt instanceof WrappedValue) {
            xmlroot.setObject(elt);
            return xmlroot;
        }
        Map<QName, Class> qNameToGeneratedClasses = jaxbContext.getQNameToGeneratedClasses();
        if (qNameToGeneratedClasses != null) {
            Class theClass = qNameToGeneratedClasses.get(qname);
            if (theClass != null && WrappedValue.class.isAssignableFrom(theClass)) {
                ClassDescriptor desc = xmlMarshaller.getXMLContext().getSession(theClass).getDescriptor(theClass);
                Object newObject = desc.getInstantiationPolicy().buildNewInstance();
                ((WrappedValue) newObject).setValue(objectValue);
                xmlroot.setObject(newObject);
                return xmlroot;
            }
        }

        Class generatedClass = null;
        if (jaxbContext.getTypeMappingInfoToGeneratedType() != null) {
            if (jaxbContext.getTypeToTypeMappingInfo() != null) {
                if (elt.getDeclaredType() != null && elt.getDeclaredType().isArray()) {
                    TypeMappingInfo tmi = jaxbContext.getTypeToTypeMappingInfo().get(elt.getDeclaredType());
                    generatedClass = jaxbContext.getTypeMappingInfoToGeneratedType().get(tmi);
                } else if (elt instanceof JAXBTypeElement) {
                    Type objectType = ((JAXBTypeElement) elt).getType();
                    TypeMappingInfo tmi = jaxbContext.getTypeToTypeMappingInfo().get(objectType);
                    generatedClass = jaxbContext.getTypeMappingInfoToGeneratedType().get(tmi);
                }
            }
        } else {
            if (elt.getDeclaredType() != null && elt.getDeclaredType().isArray()) {
                if (jaxbContext.getArrayClassesToGeneratedClasses() != null) {
                    generatedClass = jaxbContext.getArrayClassesToGeneratedClasses().get(elt.getDeclaredType().getCanonicalName());
                }
            } else if (elt instanceof JAXBTypeElement) {
                Type objectType = ((JAXBTypeElement) elt).getType();
                generatedClass = jaxbContext.getCollectionClassesToGeneratedClasses().get(objectType);
            }
        }

        if (generatedClass != null) {
            ClassDescriptor desc = xmlMarshaller.getXMLContext().getSession(generatedClass).getDescriptor(generatedClass);
            Object newObject = desc.getInstantiationPolicy().buildNewInstance();
            ((ManyValue) newObject).setItem(objectValue);
            xmlroot.setObject(newObject);
        }

        return xmlroot;
    }

    public XmlAdapter getAdapter(Class javaClass) {
        HashMap result = (HashMap) xmlMarshaller.getProperty(XML_JAVATYPE_ADAPTERS);
        if (result == null) {
            return null;
        }
        return (XmlAdapter) result.get(javaClass);
    }

    public AttachmentMarshaller getAttachmentMarshaller() {
        if (xmlMarshaller.getAttachmentMarshaller() == null) {
            return null;
        }
        return ((AttachmentMarshallerAdapter) xmlMarshaller.getAttachmentMarshaller()).getAttachmentMarshaller();
    }

    public ValidationEventHandler getEventHandler() throws JAXBException {
        return validationEventHandler;
    }

    public Marshaller.Listener getListener() {
        return ((JAXBMarshalListener) xmlMarshaller.getMarshalListener()).getListener();
    }

    public Node getNode(Object object) throws JAXBException {
        throw new UnsupportedOperationException();
    }

    public Object getProperty(String key) throws PropertyException {
        if (key == null) {
            throw new IllegalArgumentException();
        } else if (JAXB_FORMATTED_OUTPUT.equals(key)) {
            return xmlMarshaller.isFormattedOutput();
        } else if (JAXB_ENCODING.equals(key)) {
            return xmlMarshaller.getEncoding();
        } else if (JAXB_SCHEMA_LOCATION.equals(key)) {
            return xmlMarshaller.getSchemaLocation();
        } else if (JAXB_NO_NAMESPACE_SCHEMA_LOCATION.equals(key)) {
            return xmlMarshaller.getNoNamespaceSchemaLocation();
        } else if (XMLConstants.JAXB_FRAGMENT.equals(key)) {
            return xmlMarshaller.isFragment();
        } else if (JAXBContext.MEDIA_TYPE.equals(key)) {
            return xmlMarshaller.getMediaType().getName();
        } else if (NAMESPACE_PREFIX_MAPPER.equals(key)) {
            return xmlMarshaller.getNamespacePrefixMapper();
        } else if (INDENT_STRING.equals(key) || SUN_INDENT_STRING.equals(key) || SUN_JSE_INDENT_STRING.equals(key)) {
            return xmlMarshaller.getIndentString();
        } else if (CHARACTER_ESCAPE_HANDLER.equals(key)) {
            return xmlMarshaller.getCharacterEscapeHandler();
        } else if (XML_DECLARATION.equals(key)) {
            return !xmlMarshaller.isFragment();
        } else if (JAXBContext.JSON_ATTRIBUTE_PREFIX.equals(key)) {
            return xmlMarshaller.getAttributePrefix();
        } else if (JAXBContext.JSON_INCLUDE_ROOT.equals(key)) {
            return xmlMarshaller.isIncludeRoot();
        } else if (JAXBContext.JSON_VALUE_WRAPPER.equals(key)) {
            return xmlMarshaller.getValueWrapper(); 
        } else if (SUN_CHARACTER_ESCAPE_HANDLER.equals(key) || SUN_JSE_CHARACTER_ESCAPE_HANDLER.equals(key)) {
            if (xmlMarshaller.getCharacterEscapeHandler() instanceof CharacterEscapeHandlerWrapper) {
                CharacterEscapeHandlerWrapper wrapper = (CharacterEscapeHandlerWrapper) xmlMarshaller.getCharacterEscapeHandler();
                return wrapper.getHandler();
            }
            return xmlMarshaller.getCharacterEscapeHandler();
        } else if (SUN_NAMESPACE_PREFIX_MAPPER.equals(key) || SUN_JSE_NAMESPACE_PREFIX_MAPPER.equals(key)) {
            NamespacePrefixMapperWrapper wrapper = (NamespacePrefixMapperWrapper) xmlMarshaller.getNamespacePrefixMapper();
            return wrapper.getPrefixMapper();
        }
        throw new PropertyException(key);
    }

    public Schema getSchema() {
        return xmlMarshaller.getSchema();
    }

    public void marshal(Object object, ContentHandler contentHandler) throws JAXBException {
        if (object == null || contentHandler == null) {
            throw new IllegalArgumentException();
        }

        if (object instanceof JAXBElement) {
            // use the JAXBElement's properties to populate an XMLRoot
            object = createXMLRootFromJAXBElement((JAXBElement) object);
        } else if(object != null && object.getClass().isEnum()) {
            object = wrapEnumeration(object, object.getClass());
        }
        try {
            xmlMarshaller.marshal(object, contentHandler);
        } catch (Exception e) {
            throw new MarshalException(e);
        }
    }

    private Object wrapEnumeration(Object object, Class enumerationClass) {
        Class generatedClass = this.getClassToGeneratedClasses().get(enumerationClass.getName());
        if (generatedClass != null && WrappedValue.class.isAssignableFrom(generatedClass)) {
            ClassDescriptor desc = xmlMarshaller.getXMLContext().getSession(generatedClass).getDescriptor(generatedClass);
            Object newObject = desc.getInstantiationPolicy().buildNewInstance();
            ((WrappedValue) newObject).setValue(object);
            object = newObject;
        }
        return object;
    }

    public void marshal(Object object, XMLEventWriter eventWriter) throws JAXBException {
        if (object == null || eventWriter == null) {
            throw new IllegalArgumentException();
        }
        // let the JAXBIntrospector determine if the object is a JAXBElement
        if (object instanceof JAXBElement) {
            // use the JAXBElement's properties to populate an XMLRoot
            object = createXMLRootFromJAXBElement((JAXBElement) object);
        } else if(object != null && object.getClass().isEnum()) {
            object = wrapEnumeration(object, object.getClass());
        }
        try {
            XMLEventWriterRecord record = new XMLEventWriterRecord(eventWriter);
            record.setMarshaller(this.xmlMarshaller);
            this.xmlMarshaller.marshal(object, record);
        } catch (Exception ex) {
            throw new MarshalException(ex);
        }
    }

    public void marshal(Object object, XMLEventWriter eventWriter, TypeMappingInfo type) throws JAXBException {
        if (jaxbContext.getTypeMappingInfoToGeneratedType() == null) {
            marshal(object, eventWriter);
        } else {
            JAXBElement element = null;
            Object value = object;
            if (object instanceof JAXBElement) {
                // use the JAXBElement's properties to populate an XMLRoot
                element = (JAXBElement) object;
                value = element.getValue();
            }

            RootLevelXmlAdapter adapter = jaxbContext.getTypeMappingInfoToJavaTypeAdapters().get(type);
            if (adapter != null) {
                try {
                    value = adapter.getXmlAdapter().marshal(value);
                } catch (Exception ex) {
                    throw new JAXBException(XMLMarshalException.marshalException(ex));
                }
            }

            value = wrapObject(value, element, type);
            marshal(value, eventWriter);
        }
    }

    public void marshal(Object object, Node node) throws JAXBException {
        if (object == null || node == null) {
            throw new IllegalArgumentException();
        }
        // let the JAXBIntrospector determine if the object is a JAXBElement
        if (object instanceof JAXBElement) {
            // use the JAXBElement's properties to populate an XMLRoot
            object = createXMLRootFromJAXBElement((JAXBElement) object);
        } else if(object != null && object.getClass().isEnum()) {
            object = wrapEnumeration(object, object.getClass());
        }
        try {
            xmlMarshaller.marshal(object, node);
        } catch (Exception e) {
            throw new MarshalException(e);
        }
    }

    public void marshal(Object object, OutputStream outputStream) throws JAXBException {
        if (object == null || outputStream == null) {
            throw new IllegalArgumentException();
        }
        // let the JAXBIntrospector determine if the object is a JAXBElement
        if (object instanceof JAXBElement) {
            // use the JAXBElement's properties to populate an XMLRoot
            object = createXMLRootFromJAXBElement((JAXBElement) object);
        } else if(object != null && object.getClass().isEnum()) {
            object = wrapEnumeration(object, object.getClass());
        }
        try {
            xmlMarshaller.marshal(object, outputStream);
        } catch (Exception e) {
            throw new MarshalException(e);
        }
    }

    public void marshal(Object object, File file) throws JAXBException {
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            try {
                marshal(object, outputStream);
            } finally {
                outputStream.close();
            }
        } catch (Exception ex) {
            throw new MarshalException(ex);
        }
    }

    public void marshal(Object object, Result result) throws JAXBException {
        if (object == null || result == null) {
            throw new IllegalArgumentException();
        }
        // let the JAXBIntrospector determine if the object is a JAXBElement
        if (object instanceof JAXBElement) {
            // use the JAXBElement's properties to populate an XMLRoot
            object = createXMLRootFromJAXBElement((JAXBElement) object);
        } else if(object != null && object.getClass().isEnum()) {
            object = wrapEnumeration(object, object.getClass());
        }
        try {
            xmlMarshaller.marshal(object, result);
        } catch (Exception e) {
            throw new MarshalException(e);
        }
    }

    public void marshal(Object object, Result result, TypeMappingInfo type) throws JAXBException {
        if (jaxbContext.getTypeMappingInfoToGeneratedType() == null) {
            marshal(object, result);
        } else {
            JAXBElement element = null;
            Object value = object;
            if (object instanceof JAXBElement) {
                // use the JAXBElement's properties to populate an XMLRoot
                element = (JAXBElement) object;
                value = element.getValue();
            }
            RootLevelXmlAdapter adapter = jaxbContext.getTypeMappingInfoToJavaTypeAdapters().get(type);
            if (adapter != null) {
                try {
                    value = adapter.getXmlAdapter().marshal(value);
                } catch (Exception ex) {
                    throw new JAXBException(XMLMarshalException.marshalException(ex));
                }
            }
            value = wrapObject(value, element, type);
            marshal(value, result);
        }
    }

    public void marshal(Object object, XMLStreamWriter streamWriter) throws JAXBException {
        if (object == null || streamWriter == null) {
            throw new IllegalArgumentException();
        }
        // let the JAXBIntrospector determine if the object is a JAXBElement
        if (object instanceof JAXBElement) {
            // use the JAXBElement's properties to populate an XMLRoot
            object = createXMLRootFromJAXBElement((JAXBElement) object);
        } else if(object != null && object.getClass().isEnum()) {
            object = wrapEnumeration(object, object.getClass());
        }
        try {
            XMLStreamWriterRecord record = new XMLStreamWriterRecord(streamWriter);
            record.setMarshaller(this.xmlMarshaller);
            this.xmlMarshaller.marshal(object, record);
        } catch (Exception ex) {
            throw new MarshalException(ex);
        }
    }

    public void marshal(Object object, XMLStreamWriter streamWriter, TypeMappingInfo type) throws JAXBException {
        if (jaxbContext.getTypeMappingInfoToGeneratedType() == null) {
            marshal(object, streamWriter);
        } else {
            JAXBElement element = null;
            Object value = object;
            if (object instanceof JAXBElement) {
                // use the JAXBElement's properties to populate an XMLRoot
                element = (JAXBElement) object;
                value = element.getValue();
            }

            RootLevelXmlAdapter adapter = jaxbContext.getTypeMappingInfoToJavaTypeAdapters().get(type);
            if (adapter != null) {
                try {
                    value = adapter.getXmlAdapter().marshal(value);
                } catch (Exception ex) {
                    throw new JAXBException(XMLMarshalException.marshalException(ex));
                }
            }

            value = wrapObject(value, element, type);
            marshal(value, streamWriter);
        }
    }

    private Object wrapObject(Object object, JAXBElement wrapperElement, TypeMappingInfo typeMappingInfo) {
        Class generatedClass = jaxbContext.getTypeMappingInfoToGeneratedType().get(typeMappingInfo);
        if(generatedClass != null && object == null && wrapperElement != null) {
            return wrapObjectInXMLRoot(wrapperElement, object);
        }
        if (generatedClass != null && WrappedValue.class.isAssignableFrom(generatedClass)) {
            ClassDescriptor desc = xmlMarshaller.getXMLContext().getSession(generatedClass).getDescriptor(generatedClass);
            Object newObject = desc.getInstantiationPolicy().buildNewInstance();
            ((WrappedValue) newObject).setValue(object);
            object = newObject;
        } else if (generatedClass != null) {
            // should be a many value
            ClassDescriptor desc = xmlMarshaller.getXMLContext().getSession(generatedClass).getDescriptor(generatedClass);
            Object newObject = desc.getInstantiationPolicy().buildNewInstance();
            ((ManyValue) newObject).setItem(object);
            object = newObject;
        }

        if (null == wrapperElement) {
            XMLRoot xmlRoot = new XMLRoot();
            QName xmlTagName = typeMappingInfo.getXmlTagName();
            if (null == xmlTagName) {
                return object;
            }
            xmlRoot.setNamespaceURI(typeMappingInfo.getXmlTagName().getNamespaceURI());
            xmlRoot.setLocalName(typeMappingInfo.getXmlTagName().getLocalPart());
            xmlRoot.setObject(object);
            return xmlRoot;

        }
        return wrapObjectInXMLRoot(wrapperElement, object);
    }

    private XMLRoot wrapObjectInXMLRoot(JAXBElement wrapperElement, Object value) {
        XMLRoot xmlroot = new XMLRoot();
        Object objectValue = value;
        xmlroot.setObject(objectValue);
        QName qname = wrapperElement.getName();
        xmlroot.setLocalName(qname.getLocalPart());
        xmlroot.setNamespaceURI(qname.getNamespaceURI());
        xmlroot.setDeclaredType(wrapperElement.getDeclaredType());
        if(value != null) {
            if (value.getClass() == ClassConstants.ABYTE || value.getClass() == ClassConstants.APBYTE || 
                    value.getClass().getCanonicalName().equals("javax.activation.DataHandler")) {
                xmlroot.setSchemaType(XMLConstants.BASE_64_BINARY_QNAME);
            }
        }
        return xmlroot;
    }

    public void marshal(Object object, Writer writer) throws JAXBException {
        if (object == null || writer == null) {
            throw new IllegalArgumentException();
        }
        // let the JAXBIntrospector determine if the object is a JAXBElement
        if (object instanceof JAXBElement) {
            // use the JAXBElement's properties to populate an XMLRoot
            object = createXMLRootFromJAXBElement((JAXBElement) object);
        } else if(object != null && object.getClass().isEnum()) {
            object = wrapEnumeration(object, object.getClass());
        }
        try {
            xmlMarshaller.marshal(object, writer);
        } catch (Exception e) {
            throw new MarshalException(e);
        }
    }

    public void marshal(Object object, MarshalRecord record) throws JAXBException {
        if (object == null || record == null) {
            throw new IllegalArgumentException();
        }
        // let the JAXBIntrospector determine if the object is a JAXBElement
        if (object instanceof JAXBElement) {
            // use the JAXBElement's properties to populate an XMLRoot
            object = createXMLRootFromJAXBElement((JAXBElement) object);
        } else if(object != null && object.getClass().isEnum()) {
            object = wrapEnumeration(object, object.getClass());
        }
        try {
            record.setMarshaller(xmlMarshaller);
            xmlMarshaller.marshal(object, record);
        } catch (Exception e) {
            throw new MarshalException(e);
        }
    }
    
    public void marshal(Object object, MarshalRecord record, TypeMappingInfo type) throws JAXBException {
        if (jaxbContext.getTypeMappingInfoToGeneratedType() == null) {
            marshal(object, record);
        } else {
            JAXBElement element = null;
            Object value = object;
            if (object instanceof JAXBElement) {
                // use the JAXBElement's properties to populate an XMLRoot
                element = (JAXBElement) object;
                value = element.getValue();
            }
            RootLevelXmlAdapter adapter = jaxbContext.getTypeMappingInfoToJavaTypeAdapters().get(type);
            if (adapter != null) {
                try {
                    value = adapter.getXmlAdapter().marshal(value);
                } catch (Exception ex) {
                    throw new JAXBException(XMLMarshalException.marshalException(ex));
                }
            }
            value = wrapObject(value, element, type);
            marshal(value, record);            
        }
    }
    
    public void setAdapter(Class javaClass, XmlAdapter adapter) {
        HashMap result = (HashMap) xmlMarshaller.getProperty(XML_JAVATYPE_ADAPTERS);
        if (result == null) {
            result = new HashMap();
            xmlMarshaller.getProperties().put(XML_JAVATYPE_ADAPTERS, result);
        }
        result.put(javaClass, adapter);
    }

    public void setAdapter(XmlAdapter adapter) {
        setAdapter(adapter.getClass(), adapter);
    }

    public void setAttachmentMarshaller(AttachmentMarshaller attachmentMarshaller) {
        if (attachmentMarshaller == null) {
            xmlMarshaller.setAttachmentMarshaller(null);
        } else {
            xmlMarshaller.setAttachmentMarshaller(new AttachmentMarshallerAdapter(attachmentMarshaller));
        }
    }

    public void setEventHandler(ValidationEventHandler newValidationEventHandler) throws JAXBException {
        if (null == newValidationEventHandler) {
            validationEventHandler = new DefaultValidationEventHandler();
        } else {
            validationEventHandler = newValidationEventHandler;
        }
        xmlMarshaller.setErrorHandler(new JAXBErrorHandler(validationEventHandler));
    }

    public void setListener(Marshaller.Listener listener) {
        ((JAXBMarshalListener) xmlMarshaller.getMarshalListener()).setListener(listener);
    }

    public void setMarshalCallbacks(java.util.HashMap callbacks) {
        ((JAXBMarshalListener) xmlMarshaller.getMarshalListener()).setClassBasedMarshalEvents(callbacks);
    }

    public void setProperty(String key, Object value) throws PropertyException {
        try {
            if (key == null) {
                throw new IllegalArgumentException();
            } else if (JAXB_FORMATTED_OUTPUT.equals(key)) {
                Boolean formattedOutput = (Boolean) value;
                xmlMarshaller.setFormattedOutput(formattedOutput.booleanValue());
            } else if (JAXB_ENCODING.equals(key)) {
                xmlMarshaller.setEncoding((String) value);
            } else if (JAXB_SCHEMA_LOCATION.equals(key)) {
                xmlMarshaller.setSchemaLocation((String) value);
            } else if (JAXB_NO_NAMESPACE_SCHEMA_LOCATION.equals(key)) {
                xmlMarshaller.setNoNamespaceSchemaLocation((String) value);
            } else if (XMLConstants.JAXB_FRAGMENT.equals(key)) {
                Boolean fragment = (Boolean) value;
                xmlMarshaller.setFragment(fragment.booleanValue());
            } else if(NAMESPACE_PREFIX_MAPPER.equals(key)) { 
            	if(value instanceof Map){
            		NamespacePrefixMapper namespacePrefixMapper = new MapNamespacePrefixMapper((Map)value);
            		xmlMarshaller.setNamespacePrefixMapper(namespacePrefixMapper);
            	}else{
                    xmlMarshaller.setNamespacePrefixMapper((NamespacePrefixMapper)value);
            	}
            } else if(SUN_NAMESPACE_PREFIX_MAPPER.equals(key) || SUN_JSE_NAMESPACE_PREFIX_MAPPER.equals(key)) {
                xmlMarshaller.setNamespacePrefixMapper(new NamespacePrefixMapperWrapper(value));
            } else if (INDENT_STRING.equals(key) || SUN_INDENT_STRING.equals(key) || SUN_JSE_INDENT_STRING.equals(key)) {
                xmlMarshaller.setIndentString((String) value);
            } else if (CHARACTER_ESCAPE_HANDLER.equals(key)) {
                xmlMarshaller.setCharacterEscapeHandler((CharacterEscapeHandler) value);
            } else if (SUN_CHARACTER_ESCAPE_HANDLER.equals(key) || SUN_JSE_CHARACTER_ESCAPE_HANDLER.equals(key)) {
                if (value == null) {
                    xmlMarshaller.setCharacterEscapeHandler(null);
                } else {
                    xmlMarshaller.setCharacterEscapeHandler(new CharacterEscapeHandlerWrapper(value));
                }
            } else if (XML_DECLARATION.equals(key)) {
                Boolean fragment = !(Boolean) value;
                xmlMarshaller.setFragment(fragment.booleanValue());
            } else if (JAXBContext.MEDIA_TYPE.equals(key)) {
            	MediaType mType = MediaType.getMediaTypeByName((String)value);
            	if(mType != null){
            	   xmlMarshaller.setMediaType(mType);
            	}else{
            	   throw new PropertyException(key, value);
            	}
            } else if (JAXBContext.JSON_ATTRIBUTE_PREFIX.equals(key)) {            	
            	xmlMarshaller.setAttributePrefix((String)value);            	
            } else if (JAXBContext.JSON_INCLUDE_ROOT.equals(key)) {            	
            	xmlMarshaller.setIncludeRoot((Boolean)value);     
            } else if(JAXBContext.JSON_VALUE_WRAPPER.equals(key)){
            	xmlMarshaller.setValueWrapper((String)value); 
            } else {
                throw new PropertyException(key, value);
            }
        } catch (ClassCastException exception) {
            throw new PropertyException(key, exception);
        }
    }

    public void setSchema(Schema schema) {
        this.xmlMarshaller.setSchema(schema);
    }

    private HashMap<String, Class> getClassToGeneratedClasses() {
        return jaxbContext.getClassToGeneratedClasses();
    }

    public JAXBContext getJaxbContext() {
        return jaxbContext;
    }

    public void setJaxbContext(JAXBContext jaxbContext) {
        this.jaxbContext = jaxbContext;
    }

    public XMLMarshaller getXMLMarshaller() {
        return this.xmlMarshaller;
    }

}
