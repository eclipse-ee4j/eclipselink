/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
//     Marcel Valovy - 2.6.0 - added case insensitive unmarshalling property,
//                             added Bean Validation support.
package org.eclipse.persistence.jaxb;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;

import org.eclipse.persistence.core.queries.CoreAttributeGroup;
import org.eclipse.persistence.exceptions.BeanValidationException;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import org.eclipse.persistence.internal.jaxb.IDResolverWrapper;
import org.eclipse.persistence.internal.jaxb.ObjectGraphImpl;
import org.eclipse.persistence.internal.jaxb.WrappedValue;
import org.eclipse.persistence.internal.jaxb.many.ManyValue;
import org.eclipse.persistence.internal.localization.JAXBLocalization;
import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.Root;
import org.eclipse.persistence.internal.oxm.StrBuffer;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.oxm.mappings.DirectCollectionMapping;
import org.eclipse.persistence.internal.oxm.record.XMLEventReaderInputSource;
import org.eclipse.persistence.internal.oxm.record.XMLEventReaderReader;
import org.eclipse.persistence.internal.oxm.record.XMLStreamReaderInputSource;
import org.eclipse.persistence.internal.oxm.record.XMLStreamReaderReader;
import org.eclipse.persistence.internal.oxm.record.namespaces.PrefixMapperNamespaceResolver;
import org.eclipse.persistence.jaxb.JAXBContext.RootLevelXmlAdapter;
import org.eclipse.persistence.jaxb.attachment.AttachmentUnmarshallerAdapter;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.LogLevel;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.oxm.IDResolver;
import org.eclipse.persistence.oxm.MediaType;
import org.eclipse.persistence.oxm.NamespacePrefixMapper;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>To Provide an implementation of the JAXB 2.0 Unmarshaller Interface
 * <p><b>Responsibilities:</b>
 * <ul>
 * <li>Provide a JAXB wrapper on the XMLUnmarshaller API</li>
 * <li>Perform XML to Object Conversions</li>
 * </ul>
 * <p>This implementation of the JAXB 2.1/2.2 Unmarshaller interface provides the required functionality
 * by acting as a thin wrapper on the existing XMLMarshaller API.
 *
 * @author mmacivor
 * @since Oracle TopLink 11.1.1.0.0
 * @see javax.xml.bind.Unmarshaller
 * @see org.eclipse.persistence.jaxb.UnmarshallerProperties
 * @see org.eclipse.persistence.oxm.XMLUnmarshaller
 */
public class JAXBUnmarshaller implements Unmarshaller {

    private JAXBBeanValidator beanValidator;

    private BeanValidationMode beanValidationMode;

    // The actual type is ValidatorFactory. It's done due to optional nature of javax.validation.
    private Object prefValidatorFactory;
    private boolean bvNoOptimisation = false;
    private Class<?>[] beanValidationGroups;

    private final XMLUnmarshaller xmlUnmarshaller;
    private final JAXBContext jaxbContext;
    private ValidationEventHandler validationEventHandler;

    public static final String XML_JAVATYPE_ADAPTERS = "xml-javatype-adapters";
    public static final String STAX_SOURCE_CLASS_NAME = "javax.xml.transform.stax.StAXSource";

    private static final String SUN_ID_RESOLVER = "com.sun.xml.bind.IDResolver";
    private static final String SUN_JSE_ID_RESOLVER = "com.sun.xml.internal.bind.IDResolver";

    public JAXBUnmarshaller(XMLUnmarshaller newXMLUnmarshaller, JAXBContext jaxbContext) {
        this.jaxbContext = jaxbContext;
        validationEventHandler = JAXBContext.DEFAULT_VALIDATION_EVENT_HANDLER;
        beanValidationMode = BeanValidationMode.AUTO;
        if (BeanValidationChecker.isBeanValidationPresent()) {
            beanValidator = JAXBBeanValidator.getUnmarshallingBeanValidator(this.jaxbContext);
        }
        xmlUnmarshaller = newXMLUnmarshaller;
        xmlUnmarshaller.setValidationMode(XMLUnmarshaller.NONVALIDATING);
        xmlUnmarshaller.setUnmarshalListener(new JAXBUnmarshalListener(this));
        xmlUnmarshaller.setErrorHandler(new JAXBErrorHandler(validationEventHandler));
        // Disable any warning exceptions when an unmapped element is found, if the
        // validationEventHandler and errorHandler are set to default values
        xmlUnmarshaller.setWarnOnUnmappedElement(false);
    }

    public XMLUnmarshaller getXMLUnmarshaller() {
        return xmlUnmarshaller;
    }

    @Override
    public Object unmarshal(File file) throws JAXBException {
        try {
            Object value = xmlUnmarshaller.unmarshal(file);
            return validateAndTransformIfRequired(value); // xml object
        } catch (XMLMarshalException xmlMarshalException) {
            throw handleXMLMarshalException(xmlMarshalException);
        } catch (BeanValidationException bve) {
            throw new UnmarshalException(bve.getMessage(), String.valueOf(bve.getErrorCode()), bve);
        }
    }



    @Override
    public Object unmarshal(InputStream inputStream) throws JAXBException {
        try {
            if (xmlUnmarshaller.isAutoDetectMediaType() || xmlUnmarshaller.getMediaType() == MediaType.APPLICATION_JSON || null == jaxbContext.getXMLInputFactory() || XMLUnmarshaller.NONVALIDATING != xmlUnmarshaller.getValidationMode()) {
                return validateAndTransformIfRequired(xmlUnmarshaller.unmarshal(inputStream)); // xml bindings + object inside inputStream
            } else {
                if (null == inputStream) {
                    throw XMLMarshalException.nullArgumentException();
                }
                XMLStreamReader xmlStreamReader;
                xmlStreamReader = jaxbContext.getXMLInputFactory().createXMLStreamReader(inputStream);
                Object value = unmarshal(xmlStreamReader);
                xmlStreamReader.close();
                return value;
            }
        } catch(JAXBException jaxbException) {
            throw jaxbException;
        } catch (BeanValidationException bve) {
            throw new UnmarshalException(bve.getMessage(), String.valueOf(bve.getErrorCode()), bve);
        } catch (XMLMarshalException xmlMarshalException) {
            throw handleXMLMarshalException(xmlMarshalException);
        } catch (Exception exception) {
            throw new UnmarshalException(exception);
        }
    }

    @Override
    public Object unmarshal(URL url) throws JAXBException {
        try {
            Object value = xmlUnmarshaller.unmarshal(url);
            return validateAndTransformIfRequired(value); // xml bindings + object
        } catch (XMLMarshalException xmlMarshalException) {
            throw handleXMLMarshalException(xmlMarshalException);
        } catch (BeanValidationException bve) {
            throw new UnmarshalException(bve.getMessage(), String.valueOf(bve.getErrorCode()), bve);
        }
    }

    @Override
    public Object unmarshal(InputSource inputSource) throws JAXBException {
        try {
            Object value = xmlUnmarshaller.unmarshal(inputSource);
            return validateAndTransformIfRequired(value); // xml bindings + object
        } catch (XMLMarshalException xmlMarshalException) {
            throw handleXMLMarshalException(xmlMarshalException);
        } catch (BeanValidationException bve) {
            throw new UnmarshalException(bve.getMessage(), String.valueOf(bve.getErrorCode()), bve);
        }
    }

    @Override
    public Object unmarshal(Reader reader) throws JAXBException {

        try {
            if (xmlUnmarshaller.isAutoDetectMediaType()   || xmlUnmarshaller.getMediaType() == MediaType.APPLICATION_JSON || null == jaxbContext.getXMLInputFactory() || XMLUnmarshaller.NONVALIDATING != xmlUnmarshaller.getValidationMode()) {

                return validateAndTransformIfRequired(xmlUnmarshaller.unmarshal(reader)); // xml bindings + object inside reader
            } else {
                if (null == reader) {
                    throw XMLMarshalException.nullArgumentException();
                }
                XMLStreamReader xmlStreamReader = jaxbContext.getXMLInputFactory().createXMLStreamReader(reader);
                Object value = unmarshal(xmlStreamReader);
                xmlStreamReader.close();
                return value;
            }
        } catch(JAXBException jaxbException) {
            throw jaxbException;
        } catch (XMLMarshalException xmlMarshalException) {
            throw handleXMLMarshalException(xmlMarshalException);
        } catch (BeanValidationException bve) {
            throw new UnmarshalException(bve.getMessage(), String.valueOf(bve.getErrorCode()), bve);
        } catch (Exception exception) {
            throw new UnmarshalException(exception);
        }
    }

    @Override
    public Object unmarshal(Node node) throws JAXBException {
        try {
            Object value = xmlUnmarshaller.unmarshal(node);
            return validateAndTransformIfRequired(value); // xml bindings + object
        } catch (XMLMarshalException xmlMarshalException) {
            throw handleXMLMarshalException(xmlMarshalException);
        } catch (BeanValidationException bve) {
            throw new UnmarshalException(bve.getMessage(), String.valueOf(bve.getErrorCode()), bve);
        }
    }

    private JAXBElement validateAndBuildJAXBElement(Object obj, Class declaredClass) throws BeanValidationException {
        if (beanValidator != null && beanValidator.shouldValidate(obj, beanValidationMode, prefValidatorFactory, bvNoOptimisation)) {
            beanValidator.validate(obj, beanValidationGroups);
        }
        return buildJAXBElementFromObject(obj, declaredClass);
    }

    /**
     * Create a JAXBElement instance.  If the object is an instance
     * of XMLRoot, we will use its field values to create the
     * JAXBElement.  If the object is not an XMLRoot instance, we
     * will have to determine the 'name' value.  This will be done
     * using the object's descriptor default root element - any
     * prefix will be resolved, and a QName created.
     *
     * @param obj
     * @return
     */
    private JAXBElement buildJAXBElementFromObject(Object obj, Class declaredClass) {
        // if an XMLRoot was returned, the root element != the default root
        // element of the object being marshalled to - need to create a
        // JAXBElement from the returned XMLRoot object
        if (obj instanceof Root) {
            JAXBElement jaxbElement = jaxbContext.createJAXBElementFromXMLRoot(((Root)obj), declaredClass);
            if(((Root)obj).isNil()) {
                jaxbElement.setNil(((Root)obj).isNil());
                jaxbElement.setValue(null);
            }
            return jaxbElement;
        }

        if(obj instanceof JAXBElement) {
            return (JAXBElement) obj;
        }

        // at this point, the default root element of the object being marshalled
        // to == the root element - here we need to create a JAXBElement
        // instance using information from the returned object
        org.eclipse.persistence.sessions.Session sess = xmlUnmarshaller.getXMLContext().getSession(obj);
        Descriptor desc = (Descriptor) sess.getClassDescriptor(obj);

        // here we are assuming that if we've gotten this far, there
        // must be a default root element set on the descriptor.  if
        // this is incorrect, we need to check for null and throw an
        // exception
        String rootName = desc.getDefaultRootElement();
        if (rootName == null) {
            return jaxbContext.createJAXBElement(new QName(""), obj.getClass(), obj);
        }
        String rootNamespaceUri = null;
        int idx = rootName.indexOf(":");
        if (idx != -1) {
            rootNamespaceUri = desc.getNamespaceResolver().resolveNamespacePrefix(rootName.substring(0, idx));
            rootName = rootName.substring(idx + 1);
        }

        QName qname;
        if (rootNamespaceUri == null) {
            qname = new QName(rootName);
        } else {
            qname = new QName(rootNamespaceUri, rootName);
        }
        if(declaredClass != null){
            return jaxbContext.createJAXBElement(qname, declaredClass, obj);
        }else{
            return jaxbContext.createJAXBElement(qname, obj.getClass(), obj);
        }
    }

    @Override
    public JAXBElement unmarshal(Node node, Class javaClass) throws JAXBException {
        if(null == javaClass) {
            throw new IllegalArgumentException();
        }
        try {
            Class classToUnmarshalTo = getClassToUnmarshalTo(javaClass);
            if(jaxbContext.getArrayClassesToGeneratedClasses() != null) {
                Class generatedClass = jaxbContext.getArrayClassesToGeneratedClasses().get(javaClass.getCanonicalName());
                if(generatedClass != null){
                    classToUnmarshalTo = generatedClass;
                }
            }
            return validateAndBuildJAXBElement(xmlUnmarshaller.unmarshal(node, classToUnmarshalTo), javaClass); // xmlbindings + object
        } catch (XMLMarshalException xmlMarshalException) {
            throw handleXMLMarshalException(xmlMarshalException);
        }
    }

    @Override
    public Object unmarshal(Source source) throws JAXBException {
        try {
            Object value = xmlUnmarshaller.unmarshal(source);
            return validateAndTransformIfRequired(value); // xml bindings + object
        } catch (XMLMarshalException xmlMarshalException) {
            throw handleXMLMarshalException(xmlMarshalException);
        } catch (BeanValidationException bve) {
            throw new UnmarshalException(bve.getMessage(), String.valueOf(bve.getErrorCode()), bve);
        }
    }

    @Override
    public JAXBElement unmarshal(Source source, Class javaClass) throws JAXBException {
        if(null == javaClass) {
            throw new IllegalArgumentException();
        }
        Class classToUnmarshalTo = getClassToUnmarshalTo(javaClass);

        try {
            return validateAndBuildJAXBElement(xmlUnmarshaller.unmarshal(source, classToUnmarshalTo), javaClass); // json object + xml bindings
        } catch (XMLMarshalException xmlMarshalException) {
            throw handleXMLMarshalException(xmlMarshalException);
        }
    }

    private JAXBElement unmarshal(Source source, Class javaClass, Class declaredType) {
        Class classToUnmarshalTo = javaClass;
        if(jaxbContext.getArrayClassesToGeneratedClasses() != null) {
            Class generatedClass = jaxbContext.getArrayClassesToGeneratedClasses().get(javaClass.getCanonicalName());
            if(generatedClass != null){
                classToUnmarshalTo = generatedClass;
            }
        }
        return validateAndBuildJAXBElement(xmlUnmarshaller.unmarshal(source, classToUnmarshalTo), declaredType); // never used in tests. (I guess its only for ParameterizedTypes)
    }

    public JAXBElement unmarshal(Source source, Type type) throws JAXBException {
        if(null == type) {
            throw new IllegalArgumentException();
        }
        try {
            if(jaxbContext.getTypeToTypeMappingInfo() != null) {
                TypeMappingInfo tmi = jaxbContext.getTypeToTypeMappingInfo().get(type);
                if(tmi != null) {
                    return unmarshal(source, tmi);
                }
            }

            Class unmarshalClass = jaxbContext.getCollectionClassesToGeneratedClasses().get(type);
            if(unmarshalClass != null){
                JAXBElement unmarshalled =  unmarshal(source, unmarshalClass, Object.class);
                Class declaredClass;
                if(type instanceof Class){
                    declaredClass = (Class)type;
                }else{
                    declaredClass = Object.class;
                }
                JAXBElement returnVal = new JAXBElement(unmarshalled.getName(), declaredClass, unmarshalled.getScope(), unmarshalled.getValue());
                return returnVal;
            }else if(type instanceof Class){
                return  unmarshal(source, (Class)type, Object.class);
            }
            return null;
        } catch (XMLMarshalException xmlMarshalException) {
            throw handleXMLMarshalException(xmlMarshalException);
        }
    }

    /**
     * Unmarshal the object based on the binding metadata associated with the
     * TypeMappingInfo.
     */
    public JAXBElement unmarshal(Source source, TypeMappingInfo type) throws JAXBException {
        try {
            Class unmarshalClass = jaxbContext.getTypeMappingInfoToGeneratedType().get(type);
            RootLevelXmlAdapter adapter = jaxbContext.getTypeMappingInfoToJavaTypeAdapters().get(type);
            if(unmarshalClass != null){
                JAXBElement unmarshalled = unmarshal(source, unmarshalClass);
                Class declaredClass = null;
                if(type.getType() instanceof Class){
                    declaredClass = (Class)type.getType();
                }else{
                    declaredClass = Object.class;
                }
                Object value = unmarshalled.getValue();
                if(adapter != null) {
                    try {
                        value = adapter.getXmlAdapter().unmarshal(value);
                    } catch(Exception ex) {
                        throw new JAXBException(XMLMarshalException.marshalException(ex));
                    }
                }
                JAXBElement returnVal = new JAXBElement(unmarshalled.getName(), declaredClass, unmarshalled.getScope(), value);
                return returnVal;
            }else if(type.getType() instanceof Class){
                if(adapter != null) {
                    JAXBElement element = unmarshal(source, adapter.getBoundType());
                    try {
                        Object value = adapter.getXmlAdapter().unmarshal(element.getValue());
                        element.setValue(value);
                        return element;
                    } catch(Exception ex) {
                        throw new JAXBException(XMLMarshalException.marshalException(ex));
                    }
                }
                return  unmarshal(source, (Class)type.getType());
            } else if(type.getType() instanceof ParameterizedType) {
                return unmarshal(source, ((ParameterizedType)type.getType()).getRawType());
            }
            return null;
        } catch (XMLMarshalException xmlMarshalException) {
            throw handleXMLMarshalException(xmlMarshalException);
        }
    }

    @Override
    public JAXBElement unmarshal(XMLStreamReader streamReader, Class javaClass) throws JAXBException {
        if(null == streamReader || null == javaClass) {
            throw new IllegalArgumentException();
        }
        try {
            XMLStreamReaderReader staxReader = new XMLStreamReaderReader(xmlUnmarshaller);
            XMLStreamReaderInputSource inputSource = new XMLStreamReaderInputSource(streamReader);
            if(XMLConversionManager.getDefaultJavaTypes().get(javaClass) != null ||CoreClassConstants.XML_GREGORIAN_CALENDAR.isAssignableFrom(javaClass) ||CoreClassConstants.DURATION.isAssignableFrom(javaClass)) {
                PrimitiveContentHandler primitiveContentHandler = new PrimitiveContentHandler(javaClass);
                staxReader.setContentHandler(primitiveContentHandler);
                staxReader.parse(inputSource);
                return primitiveContentHandler.getJaxbElement();
            }
            Class classToUnmarshalTo = getClassToUnmarshalTo(javaClass);
            JAXBElement unmarshalled = validateAndBuildJAXBElement(xmlUnmarshaller.unmarshal(staxReader, inputSource, classToUnmarshalTo), javaClass); // xmlbindings + object (xmlelement) + "nomappings.SomeClass" + "jaxb.stax.EndEventRoot"

            if(classToUnmarshalTo != javaClass){
                JAXBElement returnVal = new JAXBElement(unmarshalled.getName(), javaClass, unmarshalled.getScope(), unmarshalled.getValue());
                return returnVal;
            }
            return unmarshalled;
        } catch (XMLMarshalException xmlMarshalException) {
            throw handleXMLMarshalException(xmlMarshalException); // Exception [EclipseLink-25004] cvc-maxInclusive-valid: Value '1234567' is not facet-valid with respect to maxInclusive '999999' for type 'id-type'.
        } catch (Exception e) {
            throw new JAXBException(e);
        }
    }

    public JAXBElement unmarshal(XMLStreamReader streamReader, Type type) throws JAXBException {
        if(null == streamReader || null == type) {
            throw new IllegalArgumentException();
        }
        try {
            if(jaxbContext.getTypeToTypeMappingInfo() != null) {
                TypeMappingInfo tmi = jaxbContext.getTypeToTypeMappingInfo().get(type);
                if(tmi != null) {
                    return unmarshal(streamReader, tmi);
                }
            }

            Class unmarshalClass = jaxbContext.getCollectionClassesToGeneratedClasses().get(type);
            if(unmarshalClass != null){
                JAXBElement unmarshalled = unmarshal(streamReader, unmarshalClass);
                Class declaredClass = null;
                if(type instanceof Class){
                    declaredClass = (Class)type;
                }else{
                    declaredClass = Object.class;
                }
                JAXBElement returnVal = new JAXBElement(unmarshalled.getName(), declaredClass, unmarshalled.getScope(), unmarshalled.getValue());
                return returnVal;

            }else if(type instanceof Class){
                return  unmarshal(streamReader, (Class)type);
            }
            return null;
        } catch (XMLMarshalException xmlMarshalException) {
            throw handleXMLMarshalException(xmlMarshalException);
        }
    }

    /**
     * Unmarshal the object based on the binding metadata associated with the
     * TypeMappingInfo.
     */
    public JAXBElement unmarshal(XMLStreamReader streamReader, TypeMappingInfo type) throws JAXBException {
        try {
            Descriptor xmlDescriptor = type.getXmlDescriptor();

            if (type.getType() instanceof Class) {
                Class javaClass = (Class) type.getType();
                Class componentClass = javaClass.getComponentType();
                if (javaClass.isArray() && javaClass != CoreClassConstants.APBYTE && javaClass != CoreClassConstants.ABYTE && XMLConversionManager.getDefaultJavaTypes().get(componentClass) != null) {
                    // Top-level array.  Descriptor will be for an EL-generated class, containing one DirectCollection mapping.
                    DirectCollectionMapping mapping = (DirectCollectionMapping) xmlDescriptor.getMappings().get(0);

                    XMLStreamReaderReader staxReader = new XMLStreamReaderReader(xmlUnmarshaller);
                    staxReader.setErrorHandler(xmlUnmarshaller.getErrorHandler());

                    PrimitiveArrayContentHandler primitiveArrayContentHandler = new PrimitiveArrayContentHandler(javaClass, componentClass, mapping.usesSingleNode());
                    staxReader.setContentHandler(primitiveArrayContentHandler);

                    XMLStreamReaderInputSource inputSource = new XMLStreamReaderInputSource(streamReader);
                    staxReader.parse(inputSource);
                    return primitiveArrayContentHandler.getJaxbElement();
                }
            }

            if(null != xmlDescriptor && null == getSchema()) {
                RootLevelXmlAdapter adapter= null;
                if(jaxbContext.getTypeMappingInfoToJavaTypeAdapters().size() >0){
                    adapter = jaxbContext.getTypeMappingInfoToJavaTypeAdapters().get(type);
                }
                UnmarshalRecord wrapper = (UnmarshalRecord) xmlDescriptor.getObjectBuilder().createRecordFromXMLContext(xmlUnmarshaller.getXMLContext());
                org.eclipse.persistence.internal.oxm.record.UnmarshalRecord unmarshalRecord = wrapper.getUnmarshalRecord();
                XMLStreamReaderReader staxReader = new XMLStreamReaderReader(xmlUnmarshaller);
                unmarshalRecord.setUnmarshaller(xmlUnmarshaller);
                unmarshalRecord.setXMLReader(staxReader);
                staxReader.setContentHandler(unmarshalRecord);
                staxReader.parse(streamReader);
                Object value = null;
                if(unmarshalRecord.isNil()) {
                    value = null;
                } else {
                    value = unmarshalRecord.getCurrentObject();
                }
                if(value instanceof WrappedValue){
                    value = ((WrappedValue)value).getValue();
                }

                if(value instanceof ManyValue){
                    value = ((ManyValue)value).getItem();
                }
                if(adapter != null) {
                    try {
                        value = adapter.getXmlAdapter().unmarshal(value);
                    } catch(Exception ex) {
                        throw new JAXBException(XMLMarshalException.marshalException(ex));
                    }
                }
                Class declaredClass = null;
                if(type.getType() instanceof Class){
                    declaredClass = (Class)type.getType();
                }else{
                    declaredClass = Object.class;
                }
                return new JAXBElement(new QName(unmarshalRecord.getRootElementNamespaceUri(), unmarshalRecord.getLocalName()), declaredClass, value);
            }
            if(jaxbContext.getTypeMappingInfoToGeneratedType() == null) {
                return unmarshal(streamReader, type.getType());
            }
            RootLevelXmlAdapter adapter= null;
            if(jaxbContext.getTypeMappingInfoToJavaTypeAdapters().size() >0){
                adapter = jaxbContext.getTypeMappingInfoToJavaTypeAdapters().get(type);
            }
            Class unmarshalClass = null;
            if(jaxbContext.getTypeMappingInfoToGeneratedType().size() >0){
                unmarshalClass = jaxbContext.getTypeMappingInfoToGeneratedType().get(type);
            }

            if(unmarshalClass != null){
                JAXBElement unmarshalled = unmarshal(streamReader, unmarshalClass);
                Class declaredClass = null;
                if(type.getType() instanceof Class){
                    declaredClass = (Class)type.getType();
                }else{
                    declaredClass = Object.class;
                }
                Object value = unmarshalled.getValue();
                if(adapter != null) {
                    try {
                        value = adapter.getXmlAdapter().unmarshal(value);
                    } catch(Exception ex) {
                        throw new JAXBException(XMLMarshalException.marshalException(ex));
                    }
                }
                JAXBElement returnVal = new JAXBElement(unmarshalled.getName(), declaredClass, unmarshalled.getScope(), value);
                return returnVal;
            }else if(type.getType() instanceof Class){
                if(adapter != null) {
                    JAXBElement element = unmarshal(streamReader, adapter.getBoundType());
                    try {
                        Object value = adapter.getXmlAdapter().unmarshal(element.getValue());
                        element.setValue(value);
                        return element;
                    } catch(Exception ex) {
                        throw new JAXBException(XMLMarshalException.marshalException(ex));
                    }
                }
                return  unmarshal(streamReader, (Class)type.getType());
            } else if(type.getType() instanceof ParameterizedType) {
                return unmarshal(streamReader, ((ParameterizedType)type.getType()).getRawType());
            }
            return null;
        } catch (XMLMarshalException xmlMarshalException) {
            throw handleXMLMarshalException(xmlMarshalException);
        } catch (SAXException e) {
            throw new JAXBException(e);
        }
    }

    @Override
    public Object unmarshal(XMLStreamReader streamReader) throws JAXBException {
        if(null == streamReader) {
            throw new IllegalArgumentException();
        }
        try {
            XMLStreamReaderReader staxReader = new XMLStreamReaderReader(xmlUnmarshaller);
            XMLStreamReaderInputSource inputSource = new XMLStreamReaderInputSource(streamReader);
            Object value = xmlUnmarshaller.unmarshal(staxReader, inputSource);
            return validateAndTransformIfRequired(value); // xml bindings + object
        } catch (XMLMarshalException xmlMarshalException) {
            throw handleXMLMarshalException(xmlMarshalException);
        } catch (BeanValidationException bve) {
            throw new UnmarshalException(bve.getMessage(), String.valueOf(bve.getErrorCode()), bve);
        }
    }

    @Override
    public JAXBElement unmarshal(XMLEventReader eventReader, Class javaClass) throws JAXBException {
        if(null == eventReader || null == javaClass) {
            throw new IllegalArgumentException();
        }
        try {
            Class classToUnmarshalTo = getClassToUnmarshalTo(javaClass);
            XMLEventReaderReader staxReader = new XMLEventReaderReader(xmlUnmarshaller);
            XMLEventReaderInputSource inputSource = new XMLEventReaderInputSource(eventReader);
            JAXBElement unmarshalled =  validateAndBuildJAXBElement(xmlUnmarshaller.unmarshal(staxReader, inputSource, classToUnmarshalTo), javaClass); // json object + xml bindings

            if(classToUnmarshalTo != javaClass){
                JAXBElement returnVal = new JAXBElement(unmarshalled.getName(), javaClass, unmarshalled.getScope(), unmarshalled.getValue());
                return returnVal;
            }
            return unmarshalled;
        } catch (XMLMarshalException xmlMarshalException) {
            throw handleXMLMarshalException(xmlMarshalException);
        }
    }

    public JAXBElement unmarshal(XMLEventReader eventReader, Type type) throws JAXBException {
        if(null == eventReader || null == type) {
            throw new IllegalArgumentException();
        }
        try {
            if(jaxbContext.getTypeToTypeMappingInfo() != null) {
                TypeMappingInfo tmi = jaxbContext.getTypeToTypeMappingInfo().get(type);
                if(tmi != null) {
                    return unmarshal(eventReader, tmi);
                }
            }
            Class unmarshalClass = jaxbContext.getCollectionClassesToGeneratedClasses().get(type);
            if(unmarshalClass != null){
                JAXBElement unmarshalled = unmarshal(eventReader, unmarshalClass);
                Class declaredClass = null;
                if(type instanceof Class){
                    declaredClass = (Class)type;
                }else{
                    declaredClass = Object.class;
                }
                JAXBElement returnVal = new JAXBElement(unmarshalled.getName(), declaredClass, unmarshalled.getScope(), unmarshalled.getValue());
                return returnVal;
            }else if(type instanceof Class){
                return  unmarshal(eventReader, (Class)type);
            }
            return null;
        } catch (XMLMarshalException xmlMarshalException) {
            throw handleXMLMarshalException(xmlMarshalException);
        }
    }

    /**
     * Unmarshal the object based on the binding metadata associated with the
     * TypeMappingInfo.
     */
    public JAXBElement unmarshal(XMLEventReader eventReader, TypeMappingInfo type) throws JAXBException {
        try {
            if(jaxbContext.getTypeMappingInfoToGeneratedType() == null) {
                return unmarshal(eventReader, type.getType());
            }

            Class unmarshalClass = jaxbContext.getTypeMappingInfoToGeneratedType().get(type);
            RootLevelXmlAdapter adapter = jaxbContext.getTypeMappingInfoToJavaTypeAdapters().get(type);
            if(unmarshalClass != null){
                JAXBElement unmarshalled = unmarshal(eventReader, unmarshalClass);
                Class declaredClass = null;
                if(type.getType() instanceof Class){
                    declaredClass = (Class)type.getType();
                }else{
                    declaredClass = Object.class;
                }
                Object value = unmarshalled.getValue();
                if(adapter != null) {
                    try {
                        value = adapter.getXmlAdapter().unmarshal(value);
                    } catch(Exception ex) {
                        throw new JAXBException(XMLMarshalException.marshalException(ex));
                    }
                }
                JAXBElement returnVal = new JAXBElement(unmarshalled.getName(), declaredClass, unmarshalled.getScope(), value);
                return returnVal;
            }else if(type.getType() instanceof Class){
                if(adapter != null) {
                    JAXBElement element = unmarshal(eventReader, adapter.getBoundType());
                    try {
                        Object value = adapter.getXmlAdapter().unmarshal(element.getValue());
                        element.setValue(value);
                        return element;
                    } catch(Exception ex) {
                        throw new JAXBException(XMLMarshalException.marshalException(ex));
                    }
                }
                return  unmarshal(eventReader, (Class)type.getType());
            } else if(type.getType() instanceof ParameterizedType) {
                return unmarshal(eventReader, ((ParameterizedType)type.getType()).getRawType());
            }
            return null;
        } catch (XMLMarshalException xmlMarshalException) {
            throw handleXMLMarshalException(xmlMarshalException);
        }
    }

    @Override
    public Object unmarshal(XMLEventReader eventReader) throws JAXBException {
        if(null == eventReader) {
            throw new IllegalArgumentException();
        }
        try {
            XMLEventReaderReader staxReader = new XMLEventReaderReader(xmlUnmarshaller);
            XMLEventReaderInputSource inputSource = new XMLEventReaderInputSource(eventReader);
            Object value = xmlUnmarshaller.unmarshal(staxReader, inputSource);
            return validateAndTransformIfRequired(value); // xml bindings + object
        } catch (XMLMarshalException xmlMarshalException) {
            throw handleXMLMarshalException(xmlMarshalException);
        } catch (BeanValidationException bve) {
            throw new UnmarshalException(bve.getMessage(), String.valueOf(bve.getErrorCode()), bve);
        }
    }

    @Override
    public UnmarshallerHandler getUnmarshallerHandler() {
        return new JAXBUnmarshallerHandler(this);
    }

    @Override
    public void setValidating(boolean validate) throws JAXBException {
        if (validate) {
            xmlUnmarshaller.setValidationMode(XMLUnmarshaller.SCHEMA_VALIDATION);
        } else {
            xmlUnmarshaller.setValidationMode(XMLUnmarshaller.NONVALIDATING);
        }
    }

    @Override
    public boolean isValidating() throws JAXBException {
        return xmlUnmarshaller.getValidationMode() != XMLUnmarshaller.NONVALIDATING;
    }

    @Override
    public void setEventHandler(ValidationEventHandler newValidationEventHandler) throws JAXBException {
        if (null == newValidationEventHandler) {
            validationEventHandler = JAXBContext.DEFAULT_VALIDATION_EVENT_HANDLER;
        } else {
            validationEventHandler = newValidationEventHandler;
        }
        xmlUnmarshaller.setErrorHandler(new JAXBErrorHandler(validationEventHandler));
        // Disable any warning exceptions when an unmapped element is found, if the
        // validationEventHandler and errorHandler are set to default values
        xmlUnmarshaller.setWarnOnUnmappedElement(validationEventHandler != JAXBContext
                .DEFAULT_VALIDATION_EVENT_HANDLER);
    }

    @Override
    public ValidationEventHandler getEventHandler() throws JAXBException {
        return validationEventHandler;
    }

    /**
     * Set a property on the JAXBUnmarshaller. Attempting to set any unsupported
     * property will result in a javax.xml.bind.PropertyException.
     * @see org.eclipse.persistence.jaxb.UnmarshallerProperties
     */
    @Override
    public void setProperty(String key, Object value) throws PropertyException {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        SessionLog logger = AbstractSessionLog.getLog();
        if (logger.shouldLog(SessionLog.FINE, SessionLog.MOXY)) {
            logger.log(SessionLog.FINE, SessionLog.MOXY, "moxy_set_unmarshaller_property", new Object[] {key, value});
        }
        if (MOXySystemProperties.moxyLogPayload != null && xmlUnmarshaller.isLogPayload() == null) {
            xmlUnmarshaller.setLogPayload(MOXySystemProperties.moxyLogPayload);
        }
        if (key.equals(UnmarshallerProperties.MEDIA_TYPE)) {
            MediaType mType = null;
            if(value instanceof MediaType) {
                mType = (MediaType) value;
            } else if(value instanceof String) {
                mType = MediaType.getMediaType((String)value);
            }
            if(mType == null){
                throw new PropertyException(key, Constants.EMPTY_STRING);
            }
            xmlUnmarshaller.setMediaType(mType);
        } else if (key.equals(UnmarshallerProperties.UNMARSHALLING_CASE_INSENSITIVE)){
            if(value == null){
                throw new PropertyException(key, Constants.EMPTY_STRING);
            }
            xmlUnmarshaller.setCaseInsensitive((Boolean)value);
        } else if (key.equals(UnmarshallerProperties.AUTO_DETECT_MEDIA_TYPE)){
            if(value == null){
                throw new PropertyException(key, Constants.EMPTY_STRING);
            }
            xmlUnmarshaller.setAutoDetectMediaType((Boolean)value);
        } else if (key.equals(UnmarshallerProperties.JSON_ATTRIBUTE_PREFIX)){
            xmlUnmarshaller.setAttributePrefix((String)value);
        } else if (UnmarshallerProperties.JSON_INCLUDE_ROOT.equals(key)) {
            if(value == null){
                throw new PropertyException(key, Constants.EMPTY_STRING);
            }
            xmlUnmarshaller.setIncludeRoot((Boolean)value);
        } else if (UnmarshallerProperties.JSON_NAMESPACE_PREFIX_MAPPER.equals(key)){
            if (value == null){
                xmlUnmarshaller.setNamespaceResolver(null);
            } else if (value instanceof Map){
                Map<String, String> namespaces = (Map<String, String>)value;
                NamespaceResolver nr = new NamespaceResolver();
                Iterator<Entry<String, String>> namesapcesIter = namespaces.entrySet().iterator();
                for (int i=0;i<namespaces.size(); i++){
                    Entry<String, String> nextEntry = namesapcesIter.next();
                    nr.put(nextEntry.getValue(), nextEntry.getKey());
                }
                xmlUnmarshaller.setNamespaceResolver(nr);
            } else if (value instanceof NamespacePrefixMapper){
                xmlUnmarshaller.setNamespaceResolver(new PrefixMapperNamespaceResolver((NamespacePrefixMapper)value, null));
            }
        } else if (UnmarshallerProperties.JSON_VALUE_WRAPPER.equals(key)){
            xmlUnmarshaller.setValueWrapper((String)value);
        } else if (UnmarshallerProperties.JSON_NAMESPACE_SEPARATOR.equals(key)){
            if(value == null){
                throw new PropertyException(key, Constants.EMPTY_STRING);
            }
            xmlUnmarshaller.setNamespaceSeparator((Character)value);
        } else if (UnmarshallerProperties.JSON_USE_XSD_TYPES_WITH_PREFIX.equals(key)) {
            xmlUnmarshaller.getJsonTypeConfiguration().setUseXsdTypesWithPrefix((Boolean)value);
        } else if (UnmarshallerProperties.JSON_TYPE_COMPATIBILITY.equals(key)) {
            xmlUnmarshaller.getJsonTypeConfiguration().setJsonTypeCompatibility((Boolean)value);
        } else if (UnmarshallerProperties.JSON_TYPE_ATTRIBUTE_NAME.equals(key)) {
            xmlUnmarshaller.getJsonTypeConfiguration().setJsonTypeAttributeName((String)value);
        } else if (UnmarshallerProperties.ID_RESOLVER.equals(key)) {
            setIDResolver((IDResolver) value);
        } else if (SUN_ID_RESOLVER.equals(key) || SUN_JSE_ID_RESOLVER.equals(key)) {
            if(value == null){
                setIDResolver(null);
            }else {
                setIDResolver(new IDResolverWrapper(value));
            }
        } else if (UnmarshallerProperties.OBJECT_GRAPH.equals(key)) {
            if(value instanceof ObjectGraphImpl) {
                xmlUnmarshaller.setUnmarshalAttributeGroup(((ObjectGraphImpl) value).getAttributeGroup());
            } else if(value instanceof String || value == null) {
                xmlUnmarshaller.setUnmarshalAttributeGroup(value);
            } else {
                throw org.eclipse.persistence.exceptions.JAXBException.invalidValueForObjectGraph(value);
            }
        } else if (UnmarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME.equals(key)) {
            xmlUnmarshaller.setWrapperAsCollectionName((Boolean) value);
        } else if (UnmarshallerProperties.BEAN_VALIDATION_MODE.equals(key)){
            if(value == null){
                throw new PropertyException(key, Constants.EMPTY_STRING);
            }
            this.beanValidationMode = ((BeanValidationMode) value);
        } else if (UnmarshallerProperties.BEAN_VALIDATION_FACTORY.equals(key)) {
            // Null value is allowed
            this.prefValidatorFactory = value;
        } else if (UnmarshallerProperties.BEAN_VALIDATION_GROUPS.equals(key)) {
            if (value == null) {
                throw new PropertyException(key, Constants.EMPTY_STRING);
            }
            this.beanValidationGroups = ((Class<?>[]) value);
        } else if (UnmarshallerProperties.BEAN_VALIDATION_NO_OPTIMISATION.equals(key)) {
            if(value == null){
                throw new PropertyException(key, Constants.EMPTY_STRING);
            }
            this.bvNoOptimisation = ((boolean) value);
        } else if (UnmarshallerProperties.DISABLE_SECURE_PROCESSING.equals(key)) {
            if(value == null){
                throw new PropertyException(key, Constants.EMPTY_STRING);
            }
            boolean disabled = value instanceof String
                    ? Boolean.parseBoolean((String) value)
                    : (boolean) value;
            xmlUnmarshaller.setDisableSecureProcessing(disabled);
        } else if (UnmarshallerProperties.MOXY_LOG_PAYLOAD.equals(key)) {
            xmlUnmarshaller.setLogPayload(((boolean) value));
        } else if (UnmarshallerProperties.MOXY_LOGGING_LEVEL.equals(key)) {
            if (value instanceof String) {
                AbstractSessionLog.getLog().setLevel(LogLevel.toValue((String) value).getId(), SessionLog.MOXY);
            } else {
                AbstractSessionLog.getLog().setLevel(((LogLevel) value).getId(), SessionLog.MOXY);
            }
        } else {
            throw new PropertyException(key, value);
        }
    }

    /**
     * Get a property from the JAXBMarshaller. Attempting to get any unsupported
     * property will result in a javax.xml.bind.PropertyException
     * See <a href="#supportedProps">Supported Properties</a>.
     * @see org.eclipse.persistence.jaxb.UnmarshallerProperties
     */
    @Override
    public Object getProperty(String key) throws PropertyException {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        if (key.equals(UnmarshallerProperties.MEDIA_TYPE)) {
            return xmlUnmarshaller.getMediaType();
        } else if (key.equals(UnmarshallerProperties.AUTO_DETECT_MEDIA_TYPE)) {
            return xmlUnmarshaller.isAutoDetectMediaType();
        } else if (key.equals(UnmarshallerProperties.UNMARSHALLING_CASE_INSENSITIVE)) {
            return xmlUnmarshaller.isCaseInsensitive();
        } else if (key.equals(UnmarshallerProperties.JSON_ATTRIBUTE_PREFIX)) {
            return xmlUnmarshaller.getAttributePrefix();
        } else if (key.equals(UnmarshallerProperties.JSON_INCLUDE_ROOT)) {
            return xmlUnmarshaller.isIncludeRoot();
        }  else if (key.equals(UnmarshallerProperties.JSON_NAMESPACE_SEPARATOR)) {
            return xmlUnmarshaller.getNamespaceSeparator();
        } else if (key.equals(UnmarshallerProperties.JSON_NAMESPACE_PREFIX_MAPPER)) {
            if(xmlUnmarshaller.getNamespaceResolver() == null){
                return null;
            }
            if (xmlUnmarshaller.getNamespaceResolver() instanceof PrefixMapperNamespaceResolver) {
                PrefixMapperNamespaceResolver wrapper = (PrefixMapperNamespaceResolver) xmlUnmarshaller.getNamespaceResolver();
                return wrapper.getPrefixMapper();
            } else {
                Map<String, String> nsMap = new HashMap<String, String>();
                Map<String, String> prefixesToNS = xmlUnmarshaller.getNamespaceResolver().getPrefixesToNamespaces();
                // Reverse the prefixesToNS map
                Iterator<Entry<String, String>> namesapcesIter = prefixesToNS.entrySet().iterator();
                for (int i = 0; i < prefixesToNS.size(); i++) {
                    Entry<String, String> nextEntry = namesapcesIter.next();
                    nsMap.put(nextEntry.getValue(), nextEntry.getKey());
                }
                return nsMap;
            }
        } else if (key.equals(UnmarshallerProperties.JSON_VALUE_WRAPPER)) {
            return xmlUnmarshaller.getValueWrapper();
        } else if (UnmarshallerProperties.JSON_USE_XSD_TYPES_WITH_PREFIX.equals(key)) {
            return xmlUnmarshaller.getJsonTypeConfiguration().isUseXsdTypesWithPrefix();
        } else if (UnmarshallerProperties.JSON_TYPE_COMPATIBILITY.equals(key)) {
            return xmlUnmarshaller.getJsonTypeConfiguration().isJsonTypeCompatibility();
        } else if (MarshallerProperties.JSON_TYPE_ATTRIBUTE_NAME.equals(key)) {
            return xmlUnmarshaller.getJsonTypeConfiguration().getJsonTypeAttributeName();
        } else if (UnmarshallerProperties.ID_RESOLVER.equals(key)) {
            return xmlUnmarshaller.getIDResolver();
        } else if (SUN_ID_RESOLVER.equals(key) || SUN_JSE_ID_RESOLVER.equals(key)) {
            IDResolverWrapper wrapper = (IDResolverWrapper) xmlUnmarshaller.getIDResolver();
            if(wrapper == null){
                return null;
            }
            return wrapper.getResolver();
        } else if (UnmarshallerProperties.OBJECT_GRAPH.equals(key)) {
            Object graph = xmlUnmarshaller.getUnmarshalAttributeGroup();
            if(graph instanceof CoreAttributeGroup) {
                return new ObjectGraphImpl((CoreAttributeGroup)graph);
            }
            return graph;
        } else if(UnmarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME.equals(key)) {
            return xmlUnmarshaller.isWrapperAsCollectionName();
        } else if (UnmarshallerProperties.BEAN_VALIDATION_MODE.equals(key)) {
            return this.beanValidationMode;
        } else if (UnmarshallerProperties.BEAN_VALIDATION_FACTORY.equals(key)) {
            return this.prefValidatorFactory;
        } else if (UnmarshallerProperties.BEAN_VALIDATION_GROUPS.equals(key)) {
            return this.beanValidationGroups;
        } else if (UnmarshallerProperties.BEAN_VALIDATION_NO_OPTIMISATION.equals(key)) {
            return this.bvNoOptimisation;
        } else if (UnmarshallerProperties.DISABLE_SECURE_PROCESSING.equals(key)) {
            return xmlUnmarshaller.isSecureProcessingDisabled();
        } else if (UnmarshallerProperties.MOXY_LOG_PAYLOAD.equals(key)) {
            return xmlUnmarshaller.isLogPayload();
        }
        throw new PropertyException(key);
    }

    @Override
    public Unmarshaller.Listener getListener() {
        return ((JAXBUnmarshalListener)xmlUnmarshaller.getUnmarshalListener()).getListener();
    }

    @Override
    public void setListener(Unmarshaller.Listener listener) {
        ((JAXBUnmarshalListener)xmlUnmarshaller.getUnmarshalListener()).setListener(listener);
    }

    @Override
    public XmlAdapter getAdapter(Class javaClass) {
        HashMap result = (HashMap) xmlUnmarshaller.getProperty(XML_JAVATYPE_ADAPTERS);
        if (result == null) {
            return null;
        }
        return (XmlAdapter) result.get(javaClass);
    }

    @Override
    public void setAdapter(Class javaClass, XmlAdapter adapter) {
        HashMap result = (HashMap) xmlUnmarshaller.getProperty(XML_JAVATYPE_ADAPTERS);
        if (result == null) {
            result = new HashMap();
            xmlUnmarshaller.getProperties().put(XML_JAVATYPE_ADAPTERS, result);
        }
        result.put(javaClass, adapter);
    }

    @Override
    public void setAdapter(XmlAdapter adapter) {
        setAdapter(adapter.getClass(), adapter);
    }

    @Override
    public void setSchema(Schema schema) {
        this.xmlUnmarshaller.setSchema(schema);
    }

    @Override
    public Schema getSchema() {
        return this.xmlUnmarshaller.getSchema();
    }

    @Override
    public AttachmentUnmarshaller getAttachmentUnmarshaller() {
        if(xmlUnmarshaller.getAttachmentUnmarshaller() == null) {
            return null;
        }
        return ((AttachmentUnmarshallerAdapter)xmlUnmarshaller.getAttachmentUnmarshaller()).getAttachmentUnmarshaller();
    }

    @Override
    public void setAttachmentUnmarshaller(AttachmentUnmarshaller unmarshaller) {
        if(unmarshaller == null) {
            xmlUnmarshaller.setAttachmentUnmarshaller(null);
        } else {
            xmlUnmarshaller.setAttachmentUnmarshaller(new AttachmentUnmarshallerAdapter(unmarshaller));
        }
    }

    public void setUnmarshalCallbacks(Map callbacks) {
        ((JAXBUnmarshalListener)xmlUnmarshaller.getUnmarshalListener()).setClassBasedUnmarshalEvents(callbacks);
    }

    private Object validateAndTransformIfRequired(Object obj) throws BeanValidationException {
        if (beanValidator != null && beanValidator.shouldValidate(obj, beanValidationMode, prefValidatorFactory, bvNoOptimisation)) {
            beanValidator.validate(obj, beanValidationGroups);
        }
        return createJAXBElementOrUnwrapIfRequired(obj);
    }

    private Object createJAXBElementOrUnwrapIfRequired(Object value) {
        if(value instanceof Root){
            JAXBElement jaxbElement = jaxbContext.createJAXBElementFromXMLRoot((Root)value, Object.class);
            jaxbElement.setNil(((Root) value).isNil());
            return jaxbElement;
        } else if(value instanceof WrappedValue) {
            return ((WrappedValue)value).getValue();
        }
        return value;
    }


    public JAXBContext getJaxbContext() {
        return jaxbContext;
    }

    private Class getClassToUnmarshalTo(Class originalClass) {
        Class classToUnmarshalTo = originalClass;
        if(jaxbContext.getArrayClassesToGeneratedClasses() != null && jaxbContext.getArrayClassesToGeneratedClasses().size() >0) {
            Class generatedClass = jaxbContext.getArrayClassesToGeneratedClasses().get(originalClass.getCanonicalName());
            if(generatedClass != null){
                classToUnmarshalTo = generatedClass;
            }
        }
        if(jaxbContext.getCollectionClassesToGeneratedClasses() != null && jaxbContext.getCollectionClassesToGeneratedClasses().size() >0){
            Class generatedClass = jaxbContext.getCollectionClassesToGeneratedClasses().get(originalClass);
            if(generatedClass != null){
                classToUnmarshalTo = generatedClass;
            }
        }
        if(jaxbContext.getTypeToTypeMappingInfo() != null){
            TypeMappingInfo tmi = jaxbContext.getTypeToTypeMappingInfo().get(originalClass);
            if(tmi != null && jaxbContext.getTypeMappingInfoToGeneratedType() != null) {
                Class generatedClass = jaxbContext.getTypeMappingInfoToGeneratedType().get(tmi);
                if(generatedClass != null){
                    classToUnmarshalTo = generatedClass;
                }
            }
        }
        return classToUnmarshalTo;
    }

    private JAXBException handleXMLMarshalException(XMLMarshalException xmlMarshalException) {
        if(xmlMarshalException.getErrorCode() == XMLMarshalException.NULL_ARGUMENT) {
            throw new IllegalArgumentException(xmlMarshalException);
        } else {
            return new UnmarshalException(xmlMarshalException);
        }
    }


    /**
     * Return this Unmarshaller's custom IDResolver.
     *
     * @see IDResolver
     * @since 2.3.3
     * @return the custom IDResolver, or null if one has not been specified.
     */
    public IDResolver getIDResolver() {
        return getXMLUnmarshaller().getIDResolver();
    }

    /**
     * Set this Unmarshaller's custom IDResolver.
     *
     * @see IDResolver
     * @since 2.3.3
     */
    public void setIDResolver(IDResolver idResolver) {
        getXMLUnmarshaller().setIDResolver(idResolver);
    }

    /**
     * Returns constraint violations stored in the underlying
     * {@link org.eclipse.persistence.jaxb.JAXBBeanValidator} instance.
     *
     * @return set of constraint violations from last unmarshal
     */
    public Set<ConstraintViolationWrapper<Object>> getConstraintViolations() {
        if (beanValidator != null) {
            return beanValidator.getConstraintViolations();
        }
        return Collections.emptySet();
    }

    private static class PrimitiveContentHandler<T> extends DefaultHandler {

        private Class<T> clazz;
        private JAXBElement<T> jaxbElement;
        private Map<String, String> namespaces = new HashMap<String, String>(3);
        private StringBuilder stringBuilder = new StringBuilder();
        private String xsiType;
        private boolean xsiNil;

        public PrimitiveContentHandler(Class<T> clazz) {
            this.clazz = clazz;
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            stringBuilder.append(ch, start, length);
        }

        @Override
        public void endElement(String namespaceURI, String localName, String qualifiedName) throws SAXException {
            XMLConversionManager xcm = XMLConversionManager.getDefaultXMLManager();
            T value;
            if(xsiNil) {
                value = null;
            } else if(null == xsiType) {
                if (clazz == CoreClassConstants.ABYTE || clazz == CoreClassConstants.APBYTE || clazz.getCanonicalName().equals("javax.activation.DataHandler")) {
                    value = (T) xcm.convertObject(stringBuilder.toString(), clazz, Constants.BASE_64_BINARY_QNAME);
                } else {
                    value = (T) xcm.convertObject(stringBuilder.toString(), clazz);
                }
            } else {
                int colonIndex = xsiType.indexOf(':');

                String typePrefix;
                String typeName;
                if(colonIndex == -1) {
                    typePrefix = Constants.EMPTY_STRING;
                    typeName = xsiType;
                } else {
                    typePrefix = xsiType.substring(0, colonIndex);
                    typeName = xsiType.substring(colonIndex + 1);
                }
                String typeNamespace = namespaces.get(typePrefix);
                QName typeQName = new QName(typeNamespace, typeName);
                value = (T) xcm.convertObject(stringBuilder.toString(), clazz, typeQName);

            }

            QName qName;
            if(namespaceURI != null && namespaceURI.length() == 0) {
                qName = new QName(qualifiedName);
            } else {
                qName = new QName(namespaceURI, localName);
            }
            jaxbElement = new JAXBElement<T>(qName, clazz, value);
        }

        public JAXBElement<T> getJaxbElement() {
            return jaxbElement;
        }

        @Override
        public void startElement(String namespaceURI, String localName, String qualifiedName, Attributes attributes) throws SAXException {
            String xsiNilValue = attributes.getValue(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, Constants.SCHEMA_NIL_ATTRIBUTE);
            if (xsiNilValue != null) {
                xsiNil = xsiNilValue.equals(Constants.BOOLEAN_STRING_TRUE) || xsiNilValue.equals("1");
            }

            if (!xsiNil) {
                xsiType = attributes.getValue(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, Constants.SCHEMA_TYPE_ATTRIBUTE);
            }
        }

        @Override
        public void startPrefixMapping(String prefix, String uri)
                throws SAXException {
            namespaces.put(prefix, uri);
        }

    }

    private static class PrimitiveArrayContentHandler<T, E> extends DefaultHandler {

        private Class<T> arrayClass;
        private Class<E> componentClass;
        private JAXBElement<T> jaxbElement;

        private QName qName;

        private StrBuffer stringBuffer = new StrBuffer();

        private boolean xsiNil;
        private boolean singleNode;
        private boolean acceptCharacters = false;

        private T unmarshalledArray;
        private int currentIndex = 0;
        private int currentSize = 10; // INITIAL SIZE

        private XMLConversionManager xcm = XMLConversionManager.getDefaultXMLManager();

        public PrimitiveArrayContentHandler(Class<T> arrayClass, Class<E> componentClass, boolean usesSingleNode) {
            this.arrayClass = arrayClass;
            this.componentClass = componentClass;
            this.singleNode = usesSingleNode;
            this.unmarshalledArray = (T) Array.newInstance(componentClass, currentSize);
        }

        @Override
        public void startElement(String namespaceURI, String localName, String qualifiedName, Attributes attributes) throws SAXException {
            if (localName.equals("item") || singleNode) {
                acceptCharacters = true;
            }

            String xsiNilValue = attributes.getValue(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, Constants.SCHEMA_NIL_ATTRIBUTE);
            if (xsiNilValue != null) {
                xsiNil = xsiNilValue.equals(Constants.BOOLEAN_STRING_TRUE) || xsiNilValue.equals("1");
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (acceptCharacters) {
                stringBuffer.append(ch, start, length);
            }
        }

        @Override
        public void endElement(String namespaceURI, String localName, String qualifiedName) throws SAXException {
            acceptCharacters = false;

            if (!qualifiedName.equals("item")) {
                if (namespaceURI != null && namespaceURI.length() == 0) {
                    qName = new QName(qualifiedName);
                } else {
                    qName = new QName(namespaceURI, localName);
                }
                if (!singleNode) {
                    return;
                }
            }

            if (singleNode) {
                endElementSingleNode();
                return;
            }

            E value;

            if (xsiNil) {
                value = null;
            } else {
                value = (E) xcm.convertObject(stringBuffer.toString(), componentClass);
            }
            addValue(value);
            stringBuffer.reset();
        }

        private void endElementSingleNode() {
            acceptCharacters = false;

            E value;

            if (xsiNil) {
                addValue(null);
                stringBuffer.reset();
                return;
            }

            StringTokenizer st = new StringTokenizer(stringBuffer.toString());

            while (st.hasMoreTokens()) {
                String nextToken = st.nextToken();
                value = (E) xcm.convertObject(nextToken, componentClass);
                addValue(value);
            }
            stringBuffer.reset();
        }

        private void addValue(E value) {
            if (currentIndex == currentSize) {
                growArray();
            }
            Array.set(unmarshalledArray, currentIndex, value);
            currentIndex++;
        }

        private void growArray() {
            int newSize = currentSize * 2;
            T newArray = (T) Array.newInstance(componentClass, newSize);
            System.arraycopy(unmarshalledArray, 0, newArray, 0, currentSize);
            unmarshalledArray = newArray;
            currentSize = newSize;
        }

        public JAXBElement<T> getJaxbElement() {
            if (null == jaxbElement) {
                // "trim" array
                T newArray = (T) Array.newInstance(componentClass, currentIndex);
                System.arraycopy(unmarshalledArray, 0, newArray, 0, currentIndex);

                jaxbElement = new JAXBElement<T>(qName, arrayClass, newArray);
            }
            return jaxbElement;
        }
    }
}
