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
package org.eclipse.persistence.exceptions;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.exceptions.i18n.ExceptionMessageGenerator;
import org.eclipse.persistence.internal.oxm.mappings.Mapping;

/**
 * <P><B>Purpose</B>: XMLMarshalExceptions are raised when issues are encountered
 * during XMLMarshaller or XMLUnmarshaller operations.
 * </P>
 */
public class XMLMarshalException extends ValidationException {
    public static final int INVALID_XPATH_STRING = 25001;
    public static final int INVALID_XPATH_INDEX_STRING = 25002;
    public static final int MARSHAL_EXCEPTION = 25003;
    public static final int UNMARSHAL_EXCEPTION = 25004;
    public static final int VALIDATE_EXCEPTION = 25005;
    public static final int DEFAULT_ROOT_ELEMENT_NOT_SPECIFIED = 25006;
    public static final int DESCRIPTOR_NOT_FOUND_IN_PROJECT = 25007;
    public static final int NO_DESCRIPTOR_WITH_MATCHING_ROOT_ELEMENT = 25008;
    public static final int SCHEMA_REFERENCE_NOT_SET = 25010;
    public static final int NULL_ARGUMENT = 25011;
    public static final int ERROR_RESOLVING_XML_SCHEMA = 25012;
    public static final int ERROR_SETTING_SCHEMAS = 25013;
    public static final int ERROR_INSTANTIATING_SCHEMA_PLATFORM = 25014;
    public static final int NAMESPACE_RESOLVER_NOT_SPECIFIED = 25015;
    public static final int NAMESPACE_NOT_FOUND = 25016;
    public static final int ENUM_CLASS_NOT_SPECIFIED = 25017;
    public static final int FROMSTRING_METHOD_ERROR = 25018;
    public static final int INVALID_ENUM_CLASS_SPECIFIED = 25019;
    public static final int ILLEGAL_STATE_XML_UNMARSHALLER_HANDLER = 25020;
    public static final int INVALID_SWA_REF_ATTRIBUTE_TYPE = 25021;
    public static final int NO_ENCODER_FOR_MIME_TYPE = 25022;
    public static final int NO_DESCRIPTOR_FOUND = 25023;
    public static final int ERROR_INSTANTIATING_UNMAPPED_CONTENTHANDLER = 25024;
    public static final int UNMAPPED_CONTENTHANDLER_DOESNT_IMPLEMENT = 25025;
    public static final int OBJ_NOT_FOUND_IN_CACHE = 25026; 
    public static final int NO_ATTACHMENT_UNMARSHALLER_SET = 25027;
    public static final int UNKNOWN_XSI_TYPE = 25028;
    public static final int SUBCLASS_ATTEMPTED_TO_OVERRIDE_NAMESPACE_DECLARATION = 25029;
    public static final int ERROR_INVOKING_NAMESPACE_PREFIX_MAPPER = 25030;
    public static final int ERROR_PROCESSING_PREFIX_MAPPER = 25031;
    public static final int ERROR_INVOKING_CHARACTER_ESCAPE_HANDLER = 25032;
    public static final int ERROR_PROCESSING_CHARACTER_ESCAPE_HANDLER = 25033;
    public static final int ERROR_INVOKING_ID_RESOLVER = 25034;
    public static final int ERROR_PROCESSING_ID_RESOLVER = 25035;
    public static final int WRAPPED_ID_RESOLVER_WITH_MULTI_ID = 25036;
    public static final int OBJECT_CYCLE_DETECTED = 25037;
    public static final int PLATFORM_NOT_SUPPORTED_WITH_JSON_MEDIA_TYPE = 25038;
    public static final int UNMARSHAL_FROM_STRING_FAILED = 25039;
    public static final int MISSING_ID_FOR_IDREF = 25040;
    public static final int INVALID_ATTRIBUTE_GROUP_NAME = 25041;

    // ==========================================================================================
    protected XMLMarshalException(String message) {
        super(message);
    }

    protected XMLMarshalException(String message, Exception internalException) {
        super(message, internalException);
    }

    // ==========================================================================================
    public static XMLMarshalException invalidXPathString(String xpathString, Exception nestedException) {
        Object[] args = { xpathString };

        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, INVALID_XPATH_STRING, args), nestedException);
        exception.setErrorCode(INVALID_XPATH_STRING);
        exception.setInternalException(nestedException);
        return exception;
    }

    public static XMLMarshalException invalidXPathIndexString(String xpathString) {
        Object[] args = { xpathString };

        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, INVALID_XPATH_INDEX_STRING, args));
        exception.setErrorCode(INVALID_XPATH_INDEX_STRING);
        return exception;
    }

    public static XMLMarshalException marshalException(Exception nestedException) {
        Object[] args = {  };
        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, MARSHAL_EXCEPTION, args), nestedException);
        exception.setErrorCode(MARSHAL_EXCEPTION);
        return exception;
    }

    public static XMLMarshalException unmarshalException() {
        Object[] args = {  };

        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, UNMARSHAL_EXCEPTION, args));
        exception.setErrorCode(UNMARSHAL_EXCEPTION);
        return exception;
    }

    public static XMLMarshalException unmarshalException(Exception nestedException) {
        Object[] args = {  };

        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, UNMARSHAL_EXCEPTION, args), nestedException);
        exception.setErrorCode(UNMARSHAL_EXCEPTION);
        exception.setInternalException(nestedException);
        return exception;
    }

    public static XMLMarshalException validateException(Exception nestedException) {
        Object[] args = {  };

        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, VALIDATE_EXCEPTION, args), nestedException);
        exception.setErrorCode(VALIDATE_EXCEPTION);
        exception.setInternalException(nestedException);
        return exception;
    }

    public static XMLMarshalException defaultRootElementNotSpecified(XMLDescriptor descriptor) {
        Object[] args = { descriptor.getJavaClassName() };

        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, DEFAULT_ROOT_ELEMENT_NOT_SPECIFIED, args));
        exception.setErrorCode(DEFAULT_ROOT_ELEMENT_NOT_SPECIFIED);
        return exception;
    }

    public static XMLMarshalException descriptorNotFoundInProject(String className) {
        Object[] args = { className };

        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, DESCRIPTOR_NOT_FOUND_IN_PROJECT, args));
        exception.setErrorCode(DESCRIPTOR_NOT_FOUND_IN_PROJECT);
        return exception;
    }

    public static XMLMarshalException noDescriptorWithMatchingRootElement(String rootElementName) {
        Object[] args = { rootElementName };

        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, NO_DESCRIPTOR_WITH_MATCHING_ROOT_ELEMENT, args));
        exception.setErrorCode(NO_DESCRIPTOR_WITH_MATCHING_ROOT_ELEMENT);
        return exception;
    }

    public static XMLMarshalException schemaReferenceNotSet(XMLDescriptor descriptor) {
        Object[] args = { descriptor.getJavaClassName() };

        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, SCHEMA_REFERENCE_NOT_SET, args));
        exception.setErrorCode(SCHEMA_REFERENCE_NOT_SET);
        return exception;
    }

    public static XMLMarshalException nullArgumentException() {
        Object[] args = {  };

        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, NULL_ARGUMENT, args));
        exception.setErrorCode(NULL_ARGUMENT);
        return exception;
    }

    public static XMLMarshalException errorResolvingXMLSchema(Exception nestedException) {
        Object[] args = {  };

        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, ERROR_RESOLVING_XML_SCHEMA, args), nestedException);
        exception.setErrorCode(ERROR_RESOLVING_XML_SCHEMA);
        exception.setInternalException(nestedException);
        return exception;
    }

    public static XMLMarshalException errorSettingSchemas(Exception nestedException, Object[] schemas) {
        Object[] args = {  };

        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, ERROR_RESOLVING_XML_SCHEMA, args), nestedException);
        exception.setErrorCode(ERROR_SETTING_SCHEMAS);
        exception.setInternalException(nestedException);
        return exception;
    }

    public static XMLMarshalException errorInstantiatingSchemaPlatform(Exception nestedException) {
        Object[] args = {  };

        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, ERROR_INSTANTIATING_SCHEMA_PLATFORM, args), nestedException);
        exception.setErrorCode(ERROR_INSTANTIATING_SCHEMA_PLATFORM);
        exception.setInternalException(nestedException);
        return exception;
    }

    public static XMLMarshalException namespaceResolverNotSpecified(String localName) {
        Object[] args = { localName };

        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, NAMESPACE_RESOLVER_NOT_SPECIFIED, args));
        exception.setErrorCode(NAMESPACE_RESOLVER_NOT_SPECIFIED);
        return exception;
    }

    public static XMLMarshalException namespaceNotFound(String prefix) {
        Object[] args = { prefix };

        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, NAMESPACE_NOT_FOUND, args));
        exception.setErrorCode(NAMESPACE_NOT_FOUND);
        return exception;
    }

    public static XMLMarshalException enumClassNotSpecified() {
        Object[] args = {  };

        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, ENUM_CLASS_NOT_SPECIFIED, args));
        exception.setErrorCode(ENUM_CLASS_NOT_SPECIFIED);
        return exception;
    }

    public static XMLMarshalException errorInvokingFromStringMethod(Exception nestedException, String className) {
        Object[] args = { className };

        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, FROMSTRING_METHOD_ERROR, args), nestedException);
        exception.setErrorCode(FROMSTRING_METHOD_ERROR);
        exception.setInternalException(nestedException);
        return exception;
    }

    public static XMLMarshalException invalidEnumClassSpecified(Exception nestedException, String className) {
        Object[] args = { className };

        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, INVALID_ENUM_CLASS_SPECIFIED, args), nestedException);
        exception.setErrorCode(INVALID_ENUM_CLASS_SPECIFIED);
        exception.setInternalException(nestedException);
        return exception;
    }

    public static XMLMarshalException illegalStateXMLUnmarshallerHandler() {
        Object[] args = {  };

        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, ILLEGAL_STATE_XML_UNMARSHALLER_HANDLER, args));
        exception.setErrorCode(ILLEGAL_STATE_XML_UNMARSHALLER_HANDLER);
        return exception;
    }

    public static XMLMarshalException invalidSwaRefAttribute(String attributeClassification) {
        Object[] args = { attributeClassification };

        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, INVALID_SWA_REF_ATTRIBUTE_TYPE, args));
        exception.setErrorCode(INVALID_SWA_REF_ATTRIBUTE_TYPE);

        return exception;
    }

    public static XMLMarshalException noEncoderForMimeType(String mimeType) {
        Object[] args = { mimeType };

        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, NO_ENCODER_FOR_MIME_TYPE, args));
        exception.setErrorCode(NO_ENCODER_FOR_MIME_TYPE);

        return exception;
    }

    /**
     * @since EclipseLink 2.5.0
     */
    public static XMLMarshalException noDescriptorFound(Mapping mapping) {
        Object[] args = { mapping.getAttributeName() };

        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, NO_DESCRIPTOR_FOUND, args));
        exception.setErrorCode(NO_DESCRIPTOR_FOUND);
        return exception;
    }

    public static XMLMarshalException noDescriptorFound(DatabaseMapping mapping) {
        Object[] args = { mapping.getAttributeName() };

        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, NO_DESCRIPTOR_FOUND, args));
        exception.setErrorCode(NO_DESCRIPTOR_FOUND);
        return exception;
    }

    public static XMLMarshalException errorInstantiatingUnmappedContentHandler(Exception nestedException, String className) {
        Object[] args = { className };

        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, ERROR_INSTANTIATING_UNMAPPED_CONTENTHANDLER, args), nestedException);
        exception.setErrorCode(ERROR_INSTANTIATING_UNMAPPED_CONTENTHANDLER);
        exception.setInternalException(nestedException);
        return exception;
    }

    public static XMLMarshalException unmappedContentHandlerDoesntImplement(Exception nestedException, String className) {
        Object[] args = { className };

        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, UNMAPPED_CONTENTHANDLER_DOESNT_IMPLEMENT, args), nestedException);
        exception.setErrorCode(UNMAPPED_CONTENTHANDLER_DOESNT_IMPLEMENT);
        exception.setInternalException(nestedException);
        return exception;
    }
    
    public static XMLMarshalException objectNotFoundInCache(String nodeName) {
        Object[] args = { nodeName };

        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, OBJ_NOT_FOUND_IN_CACHE, args));
        exception.setErrorCode(OBJ_NOT_FOUND_IN_CACHE);
        return exception;
    }
    
    public static XMLMarshalException noAttachmentUnmarshallerSet(String cid) {
        Object[] args = { cid };
        
        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, NO_ATTACHMENT_UNMARSHALLER_SET, args));
        exception.setErrorCode(NO_ATTACHMENT_UNMARSHALLER_SET);
        return exception;
    }
    
    /**
     * @since EclipseLink 2.5.0
     */
    public static XMLMarshalException unknownXsiTypeValue(String xsiType, Mapping mapping) {
        Object[] args = {xsiType, mapping};
        
        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, UNKNOWN_XSI_TYPE, args));
        exception.setErrorCode(UNKNOWN_XSI_TYPE);
        return exception;
    }

    public static XMLMarshalException unknownXsiTypeValue(String xsiType, DatabaseMapping mapping) {
        Object[] args = {xsiType, mapping};
        
        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, UNKNOWN_XSI_TYPE, args));
        exception.setErrorCode(UNKNOWN_XSI_TYPE);
        return exception;
    }

    public static XMLMarshalException subclassAttemptedToOverrideNamespaceDeclaration(String prefix, String subClassName, String subClassNamespaceURI, String parentClassName, String parentClassNamespaceURI) {
        Object[] args = {prefix, subClassName, subClassNamespaceURI, parentClassName, parentClassNamespaceURI};

        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, SUBCLASS_ATTEMPTED_TO_OVERRIDE_NAMESPACE_DECLARATION, args));
        exception.setErrorCode(SUBCLASS_ATTEMPTED_TO_OVERRIDE_NAMESPACE_DECLARATION);
        return exception;
    }
    
    public static XMLMarshalException errorInvokingPrefixMapperMethod(String methodName, Object prefixMapper) {
        Object[] args = {methodName, prefixMapper};
        
        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, ERROR_INVOKING_NAMESPACE_PREFIX_MAPPER, args));
        exception.setErrorCode(ERROR_INVOKING_NAMESPACE_PREFIX_MAPPER);
        return exception;
    }
    
    public static XMLMarshalException errorProcessingPrefixMapper(String methodName, Object prefixMapper) {
        Object[] args = {methodName, prefixMapper};
        
        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, ERROR_PROCESSING_PREFIX_MAPPER, args));
        exception.setErrorCode(ERROR_PROCESSING_PREFIX_MAPPER);
        return exception;
    }    

    public static XMLMarshalException errorInvokingCharacterEscapeHandler(String methodName, Object handler, Throwable nestedException) {
        Object[] args = {methodName, handler};
        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, ERROR_INVOKING_CHARACTER_ESCAPE_HANDLER, args));
        exception.setErrorCode(ERROR_INVOKING_CHARACTER_ESCAPE_HANDLER);
        exception.setInternalException(nestedException);
        return exception;
    }

    public static XMLMarshalException errorProcessingCharacterEscapeHandler(String methodName, Object handler, Throwable nestedException) {
        Object[] args = {methodName, handler};
        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, ERROR_PROCESSING_CHARACTER_ESCAPE_HANDLER, args));
        exception.setErrorCode(ERROR_PROCESSING_CHARACTER_ESCAPE_HANDLER);
        exception.setInternalException(nestedException);
        return exception;
    }

    public static XMLMarshalException errorInvokingIDResolver(String methodName, Object resolver, Throwable nestedException) {
        Object[] args = {methodName, resolver};
        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, ERROR_INVOKING_ID_RESOLVER, args));
        exception.setErrorCode(ERROR_INVOKING_ID_RESOLVER);
        exception.setInternalException(nestedException);
        return exception;
    }

    public static XMLMarshalException errorProcessingIDResolver(String methodName, Object resolver, Throwable nestedException) {
        Object[] args = {methodName, resolver};
        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, ERROR_PROCESSING_ID_RESOLVER, args));
        exception.setErrorCode(ERROR_PROCESSING_ID_RESOLVER);
        exception.setInternalException(nestedException);
        return exception;
    }

    public static XMLMarshalException wrappedIDResolverWithMultiID(String keyString, Object resolver) {
        Object[] args = {keyString, resolver};
        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, WRAPPED_ID_RESOLVER_WITH_MULTI_ID, args));
        exception.setErrorCode(WRAPPED_ID_RESOLVER_WITH_MULTI_ID);
        return exception;
    }

    public static XMLMarshalException objectCycleDetected(String objectCycleString) {
        Object[] args = { objectCycleString };
        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, OBJECT_CYCLE_DETECTED, args));
        exception.setErrorCode(OBJECT_CYCLE_DETECTED);
        return exception;
    }
    
    public static XMLMarshalException unsupportedMediaTypeForPlatform() {
        Object[] args = { };
        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, PLATFORM_NOT_SUPPORTED_WITH_JSON_MEDIA_TYPE, args));
        exception.setErrorCode(PLATFORM_NOT_SUPPORTED_WITH_JSON_MEDIA_TYPE);
        return exception;
    }

    public static XMLMarshalException unmarshalFromStringException(String systemId, Exception nestedException) {
        Object[] args = {systemId };
        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, UNMARSHAL_FROM_STRING_FAILED, args));
        exception.setInternalException(nestedException);
        exception.setErrorCode(PLATFORM_NOT_SUPPORTED_WITH_JSON_MEDIA_TYPE);
        return exception;
    } 
    
    public static XMLMarshalException missingIDForIDRef(String classname, Object[] primaryKey) {   
        String id = "";
        for(int i=0;i<primaryKey.length; i++){
            id += primaryKey[i];
            if(i < primaryKey.length -1){
                id += ", ";
            }
        }
        Object[] args = {classname, id};
        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, MISSING_ID_FOR_IDREF, args));        
        exception.setErrorCode(MISSING_ID_FOR_IDREF);
        return exception;
    }
    
    public static XMLMarshalException invalidAttributeGroupName(String groupName, String className) {
        Object[] args = {groupName, className};
        XMLMarshalException exception = new XMLMarshalException(ExceptionMessageGenerator.buildMessage(XMLMarshalException.class, INVALID_ATTRIBUTE_GROUP_NAME, args));
        exception.setErrorCode(INVALID_ATTRIBUTE_GROUP_NAME);
        return exception;
    }
}