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
package org.eclipse.persistence.jaxb;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.validation.Schema;

import java.lang.reflect.Array;
import java.lang.reflect.Type;

import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

import org.eclipse.persistence.oxm.CharacterEscapeHandler;
import org.eclipse.persistence.oxm.JSONWithPadding;
import org.eclipse.persistence.oxm.MediaType;
import org.eclipse.persistence.oxm.NamespacePrefixMapper;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.record.MarshalRecord;
import org.eclipse.persistence.oxm.record.XMLEventWriterRecord;
import org.eclipse.persistence.oxm.record.XMLStreamWriterRecord;
import org.eclipse.persistence.core.queries.CoreAttributeGroup;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.jaxb.many.ManyValue;
import org.eclipse.persistence.internal.jaxb.ObjectGraphImpl;
import org.eclipse.persistence.internal.jaxb.WrappedValue;
import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.Root;
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
 * <p>This implementation of the JAXB 2.1/2.2 Marshaller interface provides the
 * required functionality by acting as a thin wrapper on the existing 
 * XMLMarshaller API.</p>
 * 
 * @author mmacivor
 * @since Oracle TopLink 11.1.1.0.0
 * @see javax.xml.bind.Marshaller
 * @see org.eclipse.persistence.jaxb.MarshallerProperties
 * @see org.eclipse.persistence.oxm.XMLMarshaller
 */

public class JAXBMarshaller implements javax.xml.bind.Marshaller {

    private ValidationEventHandler validationEventHandler;
    private XMLMarshaller xmlMarshaller;
    private JAXBContext jaxbContext;

    public static final String XML_JAVATYPE_ADAPTERS = "xml-javatype-adapters";

    private static final String SUN_NAMESPACE_PREFIX_MAPPER = "com.sun.xml.bind.namespacePrefixMapper";
    private static final String SUN_JSE_NAMESPACE_PREFIX_MAPPER = "com.sun.xml.internal.bind.namespacePrefixMapper";

    private static final String SUN_INDENT_STRING = "com.sun.xml.bind.indentString";
    private static final String SUN_JSE_INDENT_STRING = "com.sun.xml.internal.bind.indentString";

    private static final String SUN_CHARACTER_ESCAPE_HANDLER = "com.sun.xml.bind.marshaller.CharacterEscapeHandler";
    private static final String SUN_JSE_CHARACTER_ESCAPE_HANDLER = "com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler";

    // XML_DECLARATION is the "opposite" to JAXB_FRAGMENT.  If XML_DECLARATION is set to false it means JAXB_FRAGMENT should be set to true.
    private static final String XML_DECLARATION = "com.sun.xml.bind.xmlDeclaration";

    private static final String XML_HEADERS = "com.sun.xml.bind.xmlHeaders";

    private static final String OBJECT_IDENTITY_CYCLE_DETECTION = "com.sun.xml.bind.objectIdentitityCycleDetection";

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
        validationEventHandler = JAXBContext.DEFAULT_VALIDATION_EVENT_HANDER;
        xmlMarshaller = newXMLMarshaller;
        xmlMarshaller.setEncoding("UTF-8");
        xmlMarshaller.setFormattedOutput(false);
        xmlMarshaller.getProperties().put(Constants.JAXB_MARSHALLER, this);
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
    private Root createXMLRootFromJAXBElement(JAXBElement elt) {
        // create an XMLRoot to hand into the marshaller
    	Root xmlroot = new Root();
        Object objectValue = elt.getValue();
        xmlroot.setObject(objectValue);
        QName qname = elt.getName();
        xmlroot.setLocalName(qname.getLocalPart());
        xmlroot.setNamespaceURI(qname.getNamespaceURI());
        xmlroot.setDeclaredType(elt.getDeclaredType());
        xmlroot.setNil(elt.isNil());
        if (elt.getDeclaredType() == CoreClassConstants.ABYTE || elt.getDeclaredType() == CoreClassConstants.APBYTE || 
                elt.getDeclaredType().getCanonicalName().equals("javax.activation.DataHandler") ||
                elt.getDeclaredType().isEnum()) {
            // need a binary data mapping so need to wrap
            Class generatedClass = getClassToGeneratedClasses().get(elt.getDeclaredType().getCanonicalName());
            if(!elt.getDeclaredType().isEnum()) {
                xmlroot.setSchemaType(Constants.BASE_64_BINARY_QNAME);
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

    /**
     * Get a property from the JAXBMarshaller. Attempting to get any unsupported
     * property will result in a javax.xml.bind.PropertyException 
     * @see org.eclipse.persistence.jaxb.MarshallerProperties
     */
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
        } else if (Constants.JAXB_FRAGMENT.equals(key)) {
            return xmlMarshaller.isFragment();
        } else if (MarshallerProperties.MEDIA_TYPE.equals(key)) {
            return xmlMarshaller.getMediaType();
        } else if (MarshallerProperties.NAMESPACE_PREFIX_MAPPER.equals(key)) {
            return xmlMarshaller.getNamespacePrefixMapper();
        } else if (MarshallerProperties.INDENT_STRING.equals(key) || SUN_INDENT_STRING.equals(key) || SUN_JSE_INDENT_STRING.equals(key)) {
            return xmlMarshaller.getIndentString();
        } else if (MarshallerProperties.CHARACTER_ESCAPE_HANDLER.equals(key)) {
            return xmlMarshaller.getCharacterEscapeHandler();
        } else if (XML_DECLARATION.equals(key)) {
            return !xmlMarshaller.isFragment();
        } else if (XML_HEADERS.equals(key)) {
            return xmlMarshaller.getXmlHeader();
        } else if (OBJECT_IDENTITY_CYCLE_DETECTION.equals(key)) {
            return xmlMarshaller.isEqualUsingIdenity();
        } else if (MarshallerProperties.JSON_ATTRIBUTE_PREFIX.equals(key)) {
            return xmlMarshaller.getAttributePrefix();
        } else if (MarshallerProperties.JSON_INCLUDE_ROOT.equals(key)) {
            return xmlMarshaller.isIncludeRoot();
        } else if (MarshallerProperties.JSON_VALUE_WRAPPER.equals(key)) {
            return xmlMarshaller.getValueWrapper(); 
        } else if (MarshallerProperties.JSON_NAMESPACE_SEPARATOR.equals(key)) {
            return xmlMarshaller.getNamespaceSeparator();
        } else if (MarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME.equals(key)) {
            return xmlMarshaller.isWrapperAsCollectionName();
        } else if (SUN_CHARACTER_ESCAPE_HANDLER.equals(key) || SUN_JSE_CHARACTER_ESCAPE_HANDLER.equals(key)) {
            if (xmlMarshaller.getCharacterEscapeHandler() instanceof CharacterEscapeHandlerWrapper) {
                CharacterEscapeHandlerWrapper wrapper = (CharacterEscapeHandlerWrapper) xmlMarshaller.getCharacterEscapeHandler();
                return wrapper.getHandler();
            }
            return xmlMarshaller.getCharacterEscapeHandler();
        } else if (SUN_NAMESPACE_PREFIX_MAPPER.equals(key) || SUN_JSE_NAMESPACE_PREFIX_MAPPER.equals(key)) {
            NamespacePrefixMapperWrapper wrapper = (NamespacePrefixMapperWrapper) xmlMarshaller.getNamespacePrefixMapper();
            if(wrapper == null){
            	return null;
            }
            return wrapper.getPrefixMapper();
        } else if (MarshallerProperties.OBJECT_GRAPH.equals(key)) {
            Object graph = xmlMarshaller.getMarshalAttributeGroup();
            if(graph instanceof CoreAttributeGroup) {
                return new ObjectGraphImpl((CoreAttributeGroup)graph);
            }
            return graph;
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

        object = modifyObjectIfNeeded(object);

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
        object = modifyObjectIfNeeded(object);

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
        object = modifyObjectIfNeeded(object);

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
        object = modifyObjectIfNeeded(object);

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
        object = modifyObjectIfNeeded(object);

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

            if(jaxbContext.getTypeMappingInfoToJavaTypeAdapters().size() > 0){
                RootLevelXmlAdapter adapter = jaxbContext.getTypeMappingInfoToJavaTypeAdapters().get(type);
           
	            if (adapter != null) {
	                try {
	                    value = adapter.getXmlAdapter().marshal(value);
	                } catch (Exception ex) {
	                    throw new JAXBException(XMLMarshalException.marshalException(ex));
	                }
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
        object = modifyObjectIfNeeded(object);
        
        try {
            XMLStreamWriterRecord record = new XMLStreamWriterRecord(streamWriter);
            record.setMarshaller(this.xmlMarshaller);
            this.xmlMarshaller.marshal(object, record);
        } catch (Exception ex) {
            throw new MarshalException(ex);
        }
    }
    
    private Object modifyObjectIfNeeded(Object obj){    	
    	if(obj instanceof Collection){
    		Collection objectList = (Collection)obj;    		
    		List newList = new ArrayList(objectList.size());
    		for(Object o:objectList){
    			newList.add(modifySingleObjectIfNeeded(o));
    		} 
    		return newList;
        }else if(obj.getClass().isArray()){
            int arraySize = Array.getLength(obj);
            List newList = new ArrayList(arraySize);
            for(int x=0; x<arraySize; x++) {
                newList.add(modifySingleObjectIfNeeded(Array.get(obj, x)));
            }
            return newList;
        }else{
    		return modifySingleObjectIfNeeded(obj);
    	}    	
    }
    
    private Object modifySingleObjectIfNeeded(Object obj){
    	// let the JAXBIntrospector determine if the object is a JAXBElement
        if (obj instanceof JAXBElement) {
            // use the JAXBElement's properties to populate an XMLRoot
            return createXMLRootFromJAXBElement((JAXBElement) obj);
        } else if(obj != null && obj.getClass().isEnum()) {
        	return wrapEnumeration(obj, obj.getClass());
        } else if (obj instanceof JSONWithPadding){
        	Object nestedObject = ((JSONWithPadding)obj).getObject();
        	if(nestedObject != null){
	        	Object newNestedObject = modifyObjectIfNeeded(nestedObject);
	        	if(nestedObject != newNestedObject){
	        		return new JSONWithPadding(newNestedObject,((JSONWithPadding)obj).getCallbackName());
	        	}
        	}             
        }
    	return obj;
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
            if(jaxbContext.getTypeMappingInfoToJavaTypeAdapters().size() > 0){
                RootLevelXmlAdapter adapter = jaxbContext.getTypeMappingInfoToJavaTypeAdapters().get(type);
	            if (adapter != null) {
	                try {
	                    value = adapter.getXmlAdapter().marshal(value);
	                } catch (Exception ex) {
	                    throw new JAXBException(XMLMarshalException.marshalException(ex));
	                }
	            }
            }

            value = wrapObject(value, element, type);
            marshal(value, streamWriter);
        }
    }

    private Object wrapObject(Object object, JAXBElement wrapperElement, TypeMappingInfo typeMappingInfo) {
    	if(jaxbContext.getTypeMappingInfoToGeneratedType().size() > 0){
	        Class generatedClass = jaxbContext.getTypeMappingInfoToGeneratedType().get(typeMappingInfo);
	        if(generatedClass != null && object == null && wrapperElement != null) {
            return wrapObjectInXMLRoot(wrapperElement, object, typeMappingInfo);
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
    	}

        if (null == wrapperElement) {
        	Root xmlRoot = new Root();
            QName xmlTagName = typeMappingInfo.getXmlTagName();
            if (null == xmlTagName) {
                return object;
            }
            xmlRoot.setNamespaceURI(typeMappingInfo.getXmlTagName().getNamespaceURI());
            xmlRoot.setLocalName(typeMappingInfo.getXmlTagName().getLocalPart());
            xmlRoot.setObject(object);
            return xmlRoot;

        }
        return wrapObjectInXMLRoot(wrapperElement, object, typeMappingInfo);
    }

    private Root wrapObjectInXMLRoot(JAXBElement wrapperElement, Object value, TypeMappingInfo typeMappingInfo) {
    	Root xmlroot = new Root();
        Object objectValue = value;
        xmlroot.setObject(objectValue);
        QName qname = wrapperElement.getName();
        xmlroot.setLocalName(qname.getLocalPart());
        xmlroot.setNamespaceURI(qname.getNamespaceURI());
        xmlroot.setDeclaredType(wrapperElement.getDeclaredType());
        if(typeMappingInfo != null){
        	xmlroot.setSchemaType(typeMappingInfo.getSchemaType());
        } else if(value != null) {
            if (value.getClass() == CoreClassConstants.ABYTE || value.getClass() == CoreClassConstants.APBYTE || 
                    value.getClass().getCanonicalName().equals("javax.activation.DataHandler")) {
                xmlroot.setSchemaType(Constants.BASE_64_BINARY_QNAME);
            }
        }
        return xmlroot;
    }

    public void marshal(Object object, Writer writer) throws JAXBException {
        if (object == null || writer == null) {
            throw new IllegalArgumentException();
        }
        object = modifyObjectIfNeeded(object);

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
        object = modifyObjectIfNeeded(object);

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
            validationEventHandler = JAXBContext.DEFAULT_VALIDATION_EVENT_HANDER;
        } else {
            validationEventHandler = newValidationEventHandler;
        }
        xmlMarshaller.setErrorHandler(new JAXBErrorHandler(validationEventHandler));
    }

    public void setListener(Marshaller.Listener listener) {
        if(xmlMarshaller.getMarshalListener() == null) {
            xmlMarshaller.setMarshalListener(new JAXBMarshalListener(jaxbContext, this));
        }
        ((JAXBMarshalListener) xmlMarshaller.getMarshalListener()).setListener(listener);
    }

    public void setMarshalCallbacks(java.util.HashMap callbacks) {
        if(callbacks == null || callbacks.isEmpty()) {
            return;
        }
        if(xmlMarshaller.getMarshalListener() == null) {
            xmlMarshaller.setMarshalListener(new JAXBMarshalListener(jaxbContext, this));
        }
        ((JAXBMarshalListener) xmlMarshaller.getMarshalListener()).setClassBasedMarshalEvents(callbacks);
    }

    /**
     * Set a property on the JAXBMarshaller. Attempting to set any unsupported
     * property will result in a javax.xml.bind.PropertyException 
     * @see org.eclipse.persistence.jaxb.MarshallerProperties
     */
    public void setProperty(String key, Object value) throws PropertyException {
        try {
            if (key == null) {
                throw new IllegalArgumentException();
            } else if (Constants.JAXB_FRAGMENT.equals(key)) {
            	if(value == null){
                 	throw new PropertyException(key, Constants.EMPTY_STRING);                	
                }         
                Boolean fragment = (Boolean) value;
                xmlMarshaller.setFragment(fragment.booleanValue());
            } else if (JAXB_FORMATTED_OUTPUT.equals(key)) {
            	if(value == null){
                 	throw new PropertyException(key, Constants.EMPTY_STRING);                	
                 }
                Boolean formattedOutput = (Boolean) value;
                xmlMarshaller.setFormattedOutput(formattedOutput.booleanValue());
            } else if (JAXB_ENCODING.equals(key)) {
                xmlMarshaller.setEncoding((String) value);
            } else if (JAXB_SCHEMA_LOCATION.equals(key)) {
                xmlMarshaller.setSchemaLocation((String) value);
            } else if (JAXB_NO_NAMESPACE_SCHEMA_LOCATION.equals(key)) {
                xmlMarshaller.setNoNamespaceSchemaLocation((String) value);
            } else if(MarshallerProperties.NAMESPACE_PREFIX_MAPPER.equals(key)) { 
            	if(value == null){
            		xmlMarshaller.setNamespacePrefixMapper(null);
            	}else if(value instanceof Map){
            		NamespacePrefixMapper namespacePrefixMapper = new MapNamespacePrefixMapper((Map)value);
            		xmlMarshaller.setNamespacePrefixMapper(namespacePrefixMapper);
            	}else{
                    xmlMarshaller.setNamespacePrefixMapper((NamespacePrefixMapper)value);
            	}
            } else if(SUN_NAMESPACE_PREFIX_MAPPER.equals(key) || SUN_JSE_NAMESPACE_PREFIX_MAPPER.equals(key)) {
            	if(value == null){
            		xmlMarshaller.setNamespacePrefixMapper(null);
            	}else{
                    xmlMarshaller.setNamespacePrefixMapper(new NamespacePrefixMapperWrapper(value));
            	}
            } else if (MarshallerProperties.INDENT_STRING.equals(key) || SUN_INDENT_STRING.equals(key) || SUN_JSE_INDENT_STRING.equals(key)) {
                xmlMarshaller.setIndentString((String) value);
            } else if (MarshallerProperties.JSON_MARSHAL_EMPTY_COLLECTIONS.equals(key)){
            	xmlMarshaller.setMarshalEmptyCollections((Boolean) value);
            } else if (MarshallerProperties.JSON_REDUCE_ANY_ARRAYS.equals(key)){
                xmlMarshaller.setReduceAnyArrays((Boolean) value);
            } else if (MarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME.equals(key)) {
                xmlMarshaller.setWrapperAsCollectionName((Boolean) value);
            } else if (MarshallerProperties.CHARACTER_ESCAPE_HANDLER.equals(key)) {
                xmlMarshaller.setCharacterEscapeHandler((CharacterEscapeHandler) value);
            } else if (SUN_CHARACTER_ESCAPE_HANDLER.equals(key) || SUN_JSE_CHARACTER_ESCAPE_HANDLER.equals(key)) {
                if (value == null) {
                    xmlMarshaller.setCharacterEscapeHandler(null);
                } else {
                    xmlMarshaller.setCharacterEscapeHandler(new CharacterEscapeHandlerWrapper(value));
                }
            } else if (XML_DECLARATION.equals(key)) {
            	if(value == null){
                 	throw new PropertyException(key, Constants.EMPTY_STRING);                	
                 }
                Boolean fragment = !(Boolean) value;
                xmlMarshaller.setFragment(fragment.booleanValue());
            } else if (XML_HEADERS.equals(key)) {
                xmlMarshaller.setXmlHeader((String) value);
            } else if (OBJECT_IDENTITY_CYCLE_DETECTION.equals(key)) {
            	if(value == null){
                 	throw new PropertyException(key, Constants.EMPTY_STRING);                	
                 }
                xmlMarshaller.setEqualUsingIdenity(((Boolean) value).booleanValue());
            } else if (MarshallerProperties.MEDIA_TYPE.equals(key)) {
                MediaType mType = null;
                if(value instanceof MediaType) {
                    mType = (MediaType) value;
                } else if(value instanceof String) {
                    mType = MediaType.getMediaType((String)value);
                }
                if(mType == null){
                	throw new PropertyException(key, Constants.EMPTY_STRING);                	
                }                 
                xmlMarshaller.setMediaType(mType);
            } else if (MarshallerProperties.JSON_ATTRIBUTE_PREFIX.equals(key)) {
                xmlMarshaller.setAttributePrefix((String)value);
            } else if (MarshallerProperties.JSON_INCLUDE_ROOT.equals(key)) {
            	 if(value == null){
                 	throw new PropertyException(key, Constants.EMPTY_STRING);                	
                 }    
                xmlMarshaller.setIncludeRoot((Boolean)value);                
            } else if(MarshallerProperties.JSON_VALUE_WRAPPER.equals(key)){
                if(value == null || (((String)value).length() == 0)){
                    throw new PropertyException(key, Constants.EMPTY_STRING);
                }                  
                xmlMarshaller.setValueWrapper((String)value); 
            } else if(MarshallerProperties.JSON_NAMESPACE_SEPARATOR.equals(key)){
            	if(value == null){
                 	throw new PropertyException(key, Constants.EMPTY_STRING);                	
                 }
                xmlMarshaller.setNamespaceSeparator((Character)value);
            } else if(MarshallerProperties.OBJECT_GRAPH.equals(key)) {
                if(value == null) {
                    xmlMarshaller.setMarshalAttributeGroup(null);
                } else if(value instanceof ObjectGraphImpl) {
                    xmlMarshaller.setMarshalAttributeGroup(((ObjectGraphImpl)value).getAttributeGroup());
                } else if(value.getClass() == ClassConstants.STRING){
                    xmlMarshaller.setMarshalAttributeGroup(value);
                } else {
                    throw org.eclipse.persistence.exceptions.JAXBException.invalidValueForObjectGraph(value);
                }
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
