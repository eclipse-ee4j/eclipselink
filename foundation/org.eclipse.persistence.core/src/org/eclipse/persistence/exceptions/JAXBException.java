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
* mmacivor - June 11/2008 - 1.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.exceptions;

import javax.xml.namespace.QName;

import org.eclipse.persistence.exceptions.i18n.ExceptionMessageGenerator;

/**
 * <b>Purpose:</b>
 * <ul><li>This class provides an implementation of EclipseLinkException specific to the EclipseLink JAXB implementation</li>
 * </ul>
 * <p/>
 * <b>Responsibilities:</b>
 * <ul>
 * <li>Return a JAXBException that can be thrown around input parameters.
 * <li>Return a JAXBException that wraps an existing exception with additional input parameters.
 * </ul>
 * @since Oracle EclipseLink 1.0
 */
public class JAXBException extends EclipseLinkException {

    public static final int NO_OBJECT_FACTORY_OR_JAXB_INDEX_IN_PATH = 50000;
    public static final int FACTORY_METHOD_OR_ZERO_ARG_CONST_REQ = 50001;
    public static final int FACTORY_CLASS_WITHOUT_FACTORY_METHOD = 50002;
    public static final int FACTORY_METHOD_NOT_DECLARED = 50003;
    public static final int ANY_ATTRIBUTE_ON_NON_MAP_PROPERTY = 50004;
    public static final int MULTIPLE_ANY_ATTRIBUTE_MAPPING = 50005;
    public static final int INVALID_XML_ELEMENT_REF = 50006;
    public static final int NAME_COLLISION = 50007;
    public static final int UNSUPPORTED_NODE_CLASS = 50008;
    public static final int TRANSIENT_IN_PROP_ORDER = 50009;
    public static final int XMLVALUE_ATTRIBUTE_CONFLICT = 50010;
    public static final int SUBCLASS_CANNOT_HAVE_XMLVALUE = 50011;
    public static final int NON_EXISTENT_PROPERTY_IN_PROP_ORDER = 50012;
    public static final int MISSING_PROPERTY_IN_PROP_ORDER = 50013;
    public static final int INVALID_TYPE_FOR_XMLVALUE_PROPERTY = 50014;
    public static final int INVALID_XML_ELEMENT_WRAPPER = 50015;
    public static final int INVALID_ID = 50016;
    public static final int INVALID_IDREF = 50017;
    public static final int INVALID_LIST = 50018;
    public static final int VALUE_PARAMETER_TYPE_INCORRECT_FOR_OXM_XML = 50019;
    public static final int KEY_PARAMETER_TYPE_INCORRECT = 50021;
    public static final int VALUE_PARAMETER_TYPE_INCORRECT = 50022;
    public static final int NULL_METADATA_SOURCE = 50023;
    public static final int NULL_MAP_KEY = 50024;
    public static final int COULD_NOT_LOAD_CLASS_FROM_METADATA = 50025;
    public static final int COULD_NOT_CREATE_CONTEXT_FOR_XML_MODEL = 50026;
    public static final int COULD_NOT_UNMARSHAL_METADATA = 50027;
    public static final int COULD_NOT_CREATE_CUSTOMIZER_INSTANCE = 50028;
    public static final int INVALID_CUSTOMIZER_CLASS = 50029;
    public static final int ID_ALREADY_SET = 50030;
    public static final int XMLVALUE_ALREADY_SET = 50031;
    public static final int XMLANYELEMENT_ALREADY_SET = 50032;
    public static final int COULD_NOT_INITIALIZE_DOM_HANDLER_CONVERTER = 50033;
    public static final int INVALID_TYPE_FOR_XMLATTRIBUTEREF_PROPERTY = 50034;
    public static final int INVALID_XMLELEMENT_IN_XMLELEMENTS = 50035;
    public static final int NULL_TYPE_ON_TYPEMAPPINGINFO = 50036;
    public static final int JAVATYPE_NOT_ALLOWED_IN_BINDINGS_FILE = 50037;
    public static final int CANNOT_CREATE_DYNAMIC_CONTEXT_FROM_CLASSES = 50038;
    public static final int CANNOT_INITIALIZE_FROM_NODE = 50039;
    public static final int ERROR_CREATING_DYNAMICJAXBCONTEXT = 50040;
    public static final int ENUM_CONSTANT_NOT_FOUND = 50041;
    public static final int NULL_SESSION_NAME = 50042;
    public static final int NULL_SOURCE = 50043;
    public static final int NULL_INPUT_STREAM = 50044;
    public static final int NULL_NODE = 50045;
    public static final int XJC_BINDING_ERROR = 50046;
    public static final int CLASS_NOT_FOUND_EXCEPTION = 50047;
    public static final int READ_TRANSFORMER_HAS_BOTH_CLASS_AND_METHOD = 50048;
    public static final int READ_TRANSFORMER_HAS_NEITHER_CLASS_NOR_METHOD = 50049;
    public static final int WRITE_TRANSFORMER_HAS_BOTH_CLASS_AND_METHOD = 50050;
    public static final int WRITE_TRANSFORMER_HAS_NEITHER_CLASS_NOR_METHOD = 50051;
    public static final int WRITE_TRANSFORMER_HAS_NO_XMLPATH = 50052;
    public static final int NO_SUCH_WRITE_TRANSFORMATION_METHOD = 50053;
    public static final int TRANSFORMER_CLASS_NOT_FOUND = 50054;
    public static final int OXM_KEY_NOT_FOUND = 50055;
    public static final int INVALID_REF_CLASS = 50056;
    public static final int TRANSIENT_REF_CLASS = 50057;
    public static final int NO_ID_OR_KEY_ON_JOIN_TARGET = 50058;
    public static final int INVALID_REF_XML_PATH = 50059;
    public static final int INVALID_IDREF_CLASS = 50060;
    public static final int ADAPTER_CLASS_NOT_LOADED = 50061;
    public static final int ADAPTER_CLASS_METHOD_EXCEPTION = 50062;
    public static final int ADAPTER_CLASS_COULD_NOT_BE_INSTANTIATED = 50063;
    public static final int INVALID_ADAPTER_CLASS = 50064;
    public static final int INVALID_PACKAGE_ADAPTER_CLASS = 50065;
    public static final int INVALID_TYPE_ADAPTER_CLASS = 50066;
    public static final int INVALID_PROPERTY_ADAPTER_CLASS = 50067;
    public static final int NULL_METADATA_FILE = 50068;
    public static final int BINDINGS_PKG_NOT_SET = 50069;
    public static final int INCORRECT_NUMBER_OF_XMLJOINNODES_ON_XMLELEMENTS = 50070;
    public static final int INVALID_XML_PATH_ATTRIBUTE = 50071;
    public static final int DUPLICATE_PROPERTY_NAME = 50072;
    public static final int SAME_PROPERTY_IN_MULTIPLE_BINDINGS_FILES = 50073;
    public static final int EXCEPTION_WITH_NAME_TRANSFORMER_CLASS = 50074;
    public static final int EXCEPTION_DURING_NAME_TRANSFORMATION = 50075;
    public static final int UNABLE_TO_LOAD_METADATA_FROM_LOCATION = 50076;
    public static final int CANNOT_REFRESH_METADATA = 50077;
    public static final int XJB_NOT_SOURCE = 50078;
    public static final int XSD_IMPORT_NOT_SOURCE = 50079;
    public static final int INVALID_XMLLOCATION = 50080;
    public static final int EXCEPTION_DURING_SCHEMA_GEN = 50081;
    public static final int JSON_VALUE_WRAPPER_REQUIRED = 50082;
    public static final int ERROR_INSTANTIATING_ACCESSOR_FACTORY = 50083;
    public static final int INVALID_ACCESSOR_FACTORY = 50084;
    public static final int ERROR_CREATING_FIELD_ACCESSOR = 50085;
    public static final int ERROR_CREATING_PROPERTY_ACCESSOR = 50086;
    public static final int ERROR_INVOKING_ACCESSOR = 50087;
    public static final int INVALID_ENUM_VALUE = 50088;
    public static final int INVALID_INTERFACE = 50089;
    public static final int INVALID_VALUE_FOR_OBJECT_GRAPH = 50090;
    public static final int DUPLICATE_ELEMENT_NAME = 50091;
    public static final int MULTIPLE_XMLELEMREF = 50092;


    protected JAXBException(String message) {
        super(message);
    }

    protected JAXBException(String message, Exception internalException) {
        super(message, internalException);
    }

    public static JAXBException noObjectFactoryOrJaxbIndexInPath(String path) {
        Object[] args = { path };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, NO_OBJECT_FACTORY_OR_JAXB_INDEX_IN_PATH, args));
        exception.setErrorCode(NO_OBJECT_FACTORY_OR_JAXB_INDEX_IN_PATH);
        return exception;
    }

    public static JAXBException factoryMethodOrConstructorRequired(String className) {
        Object[] args = { className };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, FACTORY_METHOD_OR_ZERO_ARG_CONST_REQ, args));
        exception.setErrorCode(FACTORY_METHOD_OR_ZERO_ARG_CONST_REQ);
        return exception;
    }

    public static JAXBException factoryClassWithoutFactoryMethod(String className) {
        Object[] args = { className };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, FACTORY_CLASS_WITHOUT_FACTORY_METHOD, args));
        exception.setErrorCode(FACTORY_CLASS_WITHOUT_FACTORY_METHOD);
        return exception;
    }

    public static JAXBException factoryMethodNotDeclared(String methodName, String className) {
        Object[] args = { methodName, className };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, FACTORY_METHOD_NOT_DECLARED, args));
        exception.setErrorCode(FACTORY_METHOD_NOT_DECLARED);
        return exception;

    }

    public static JAXBException multipleAnyAttributeMapping(String className) {
        Object[] args = { className };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, MULTIPLE_ANY_ATTRIBUTE_MAPPING, args));
        exception.setErrorCode(MULTIPLE_ANY_ATTRIBUTE_MAPPING);
        return exception;
    }

    public static JAXBException anyAttributeOnNonMap(String propertyName) {
        Object[] args = { propertyName };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, ANY_ATTRIBUTE_ON_NON_MAP_PROPERTY, args));
        exception.setErrorCode(ANY_ATTRIBUTE_ON_NON_MAP_PROPERTY);
        return exception;
    }

    public static JAXBException invalidElementRef(String propertyName, String className) {
        Object[] args = { propertyName, className };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, INVALID_XML_ELEMENT_REF, args));
        exception.setErrorCode(INVALID_XML_ELEMENT_REF);
        return exception;
    }

    public static JAXBException invalidElementWrapper(String propertyName) {
        Object[] args = { propertyName };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, INVALID_XML_ELEMENT_WRAPPER, args));
        exception.setErrorCode(INVALID_XML_ELEMENT_WRAPPER);
        return exception;
    }

    public static JAXBException invalidId(String propertyName) {
        Object[] args = { propertyName };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, INVALID_ID, args));
        exception.setErrorCode(INVALID_ID);
        return exception;
    }

    public static JAXBException invalidIdRef(String propertyName, String className) {
        Object[] args = { propertyName, className };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, INVALID_IDREF, args));
        exception.setErrorCode(INVALID_IDREF);
        return exception;
    }

    public static JAXBException invalidList(String propertyName) {
        Object[] args = { propertyName };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, INVALID_LIST, args));
        exception.setErrorCode(INVALID_LIST);
        return exception;
    }

    public static JAXBException nameCollision(String uri, String name) {
        Object[] args = { uri, name };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, NAME_COLLISION, args));
        exception.setErrorCode(NAME_COLLISION);
        return exception;
    }

    public static JAXBException unsupportedNodeClass(String className) {
        Object[] args = { className };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, UNSUPPORTED_NODE_CLASS, args));
        exception.setErrorCode(UNSUPPORTED_NODE_CLASS);
        return exception;
    }

    public static JAXBException transientInProporder(String fieldName) {
        Object[] args = { fieldName };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, TRANSIENT_IN_PROP_ORDER, args));
        exception.setErrorCode(TRANSIENT_IN_PROP_ORDER);
        return exception;
    }

    public static JAXBException nonExistentPropertyInPropOrder(String fieldName) {
        Object[] args = { fieldName };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, NON_EXISTENT_PROPERTY_IN_PROP_ORDER, args));
        exception.setErrorCode(NON_EXISTENT_PROPERTY_IN_PROP_ORDER);
        return exception;
    }
    
    public static JAXBException missingPropertyInPropOrder(String fieldName) {        
        Object[] args = { fieldName, "" };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, MISSING_PROPERTY_IN_PROP_ORDER, args));
        exception.setErrorCode(MISSING_PROPERTY_IN_PROP_ORDER);
        return exception;
    }

    public static JAXBException missingPropertyInPropOrder(String fieldName, String className) {
        Object[] args = { fieldName, className };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, MISSING_PROPERTY_IN_PROP_ORDER, args));
        exception.setErrorCode(MISSING_PROPERTY_IN_PROP_ORDER);
        return exception;
    }

    public static JAXBException propertyOrFieldShouldBeAnAttribute(String fieldName) {
        Object[] args = { fieldName };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, XMLVALUE_ATTRIBUTE_CONFLICT, args));
        exception.setErrorCode(XMLVALUE_ATTRIBUTE_CONFLICT);
        return exception;
    }

    public static JAXBException propertyOrFieldCannotBeXmlValue(String fieldName) {
        Object[] args = { fieldName };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, SUBCLASS_CANNOT_HAVE_XMLVALUE, args));
        exception.setErrorCode(SUBCLASS_CANNOT_HAVE_XMLVALUE);
        return exception;
    }

    public static JAXBException invalidTypeForXmlValueField(String fieldName) {
        Object[] args = { fieldName };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, INVALID_TYPE_FOR_XMLVALUE_PROPERTY, args));
        exception.setErrorCode(INVALID_TYPE_FOR_XMLVALUE_PROPERTY);
        return exception;
    }

    /**
     * This exception would typically be used by JAXBContextFactory during externalized metadata processing (i.e.
     * eclipselink-oxm.xml).  This exception applies to the case where the Key parameter type of the package name to
     * metadata source map is something other than String.  We require Map<String, Source>.
     *
     * @return
     */
    public static JAXBException incorrectKeyParameterType() {
        Object[] args = {};
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, KEY_PARAMETER_TYPE_INCORRECT, args));
        exception.setErrorCode(KEY_PARAMETER_TYPE_INCORRECT);
        return exception;
    }

    /**
     * This exception would typically be used by JAXBContextFactory during externalized metadata processing (i.e.
     * eclipselink-oxm.xml).  This exception applies to the case where the Value parameter type (of the package
     * name to metadata source map) is something other than Source.  We require Map<String, Source>.
     *
     * @return
     */
    public static JAXBException incorrectValueParameterType() {
        Object[] args = {};
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, VALUE_PARAMETER_TYPE_INCORRECT, args));
        exception.setErrorCode(VALUE_PARAMETER_TYPE_INCORRECT);
        return exception;
    }

    /**
     * This exception would typically be used by JAXBContextFactory during externalized metadata processing (i.e.
     * eclipselink-oxm.xml).  This exception applies to the case where the Value parameter type associated with
     * the 'eclipselink-oxm-xml' Key (in the properties map) is something other than Map<String, Source>.
     *
     * @return
     */
    public static JAXBException incorrectValueParameterTypeForOxmXmlKey() {
        Object[] args = {};
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, VALUE_PARAMETER_TYPE_INCORRECT_FOR_OXM_XML, args));
        exception.setErrorCode(VALUE_PARAMETER_TYPE_INCORRECT_FOR_OXM_XML);
        return exception;
    }

    /**
     * This exception would typically be used by JAXBContextFactory during externalized metadata processing (i.e.
     * eclipselink-oxm.xml).  This exception applies to the case where the Value (in the package name
     * to metadata source map) is null.
     *
     * @param key
     * @return
     */
    public static JAXBException nullMetadataSource(String key) {
        Object[] args = { key };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, NULL_METADATA_SOURCE, args));
        exception.setErrorCode(NULL_METADATA_SOURCE);
        return exception;
    }

    /**
     * This exception would typically be used by JAXBContextFactory during externalized metadata processing (i.e.
     * eclipselink-oxm.xml).  This exception applies to the case where the handle to the OXM metadata file is
     * null.
     *
     * @return
     */
    public static JAXBException nullMetadataSource() {
        Object[] args = { };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, NULL_METADATA_FILE, args));
        exception.setErrorCode(NULL_METADATA_FILE);
        return exception;
    }

    /**
     * This exception would typically be used by JAXBContextFactory during externalized metadata processing (i.e.
     * eclipselink-oxm.xml).  This exception applies to the case where the handle to the OXM metadata file has
     * not been passes in with an associated String (for package name) and the unmarshalled XmlBindings object
     * does not have a package-name set.
     *
     * @return
     */
    public static JAXBException packageNotSetForBindingException() {
        Object[] args = { };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, BINDINGS_PKG_NOT_SET, args));
        exception.setErrorCode(BINDINGS_PKG_NOT_SET);
        return exception;
    }

    /**
     * This exception would typically be used by JAXBContextFactory during externalized metadata processing (i.e.
     * eclipselink-oxm.xml).  This exception applies to the case where the Key (in the package name
     * to metadata source map) is null.
     *
     * @return
     */
    public static JAXBException nullMapKey() {
        Object[] args = {};
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, NULL_MAP_KEY, args));
        exception.setErrorCode(NULL_MAP_KEY);
        return exception;
    }

    /**
     * This exception would typically be used by JAXBContextFactory during externalized metadata processing (i.e.
     * eclipselink-oxm.xml).  This exception applies to the case where a class that is declared in the metadata
     * file cannot be loaded by the classloader.
     *
     * @param classname
     * @return
     */
    public static JAXBException couldNotLoadClassFromMetadata(String classname) {
        Object[] args = { classname };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, COULD_NOT_LOAD_CLASS_FROM_METADATA, args));
        exception.setErrorCode(COULD_NOT_LOAD_CLASS_FROM_METADATA);
        return exception;
    }

    /**
     * This exception would typically be used by JAXBContextFactory during externalized metadata processing (i.e.
     * eclipselink-oxm.xml).  This exception applies to the case where JAXBContext creation fails for our
     * XmlModel.
     *
     * @return
     */
    public static JAXBException couldNotCreateContextForXmlModel() {
        Object[] args = {};
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, COULD_NOT_CREATE_CONTEXT_FOR_XML_MODEL, args));
        exception.setErrorCode(COULD_NOT_CREATE_CONTEXT_FOR_XML_MODEL);
        return exception;
    }

    /**
     * This exception would typically be used by JAXBContextFactory during externalized metadata processing (i.e.
     * eclipselink-oxm.xml).  This exception applies to the case where JAXBContext creation fails for our
     * XmlModel.
     *
     * @param ex
     * @return
     */
    public static JAXBException couldNotCreateContextForXmlModel(Exception ex) {
        Object[] args = { ex };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, COULD_NOT_CREATE_CONTEXT_FOR_XML_MODEL, args), ex);
        exception.setErrorCode(COULD_NOT_CREATE_CONTEXT_FOR_XML_MODEL);
        return exception;
    }

    /**
     * This exception would typically be used by JAXBContextFactory during externalized metadata processing (i.e.
     * eclipselink-oxm.xml).  This exception applies to the case where an exception occurs while unmarshalling
     * the eclipselink metadata file.
     *
     * @param e
     * @return
     */
    public static JAXBException couldNotUnmarshalMetadata(Exception e) {
        Object[] args = {};
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, COULD_NOT_UNMARSHAL_METADATA, args), e);
        exception.setErrorCode(COULD_NOT_UNMARSHAL_METADATA);
        return exception;
    }

    /**
     * This exception should be used when a descriptor customizer instance cannot be created.
     *
     * @param e
     * @param javaClassName
     * @param customizerClassName
     * @return
     */
    public static JAXBException couldNotCreateCustomizerInstance(Exception e, String customizerClassName) {
        Object[] args = { customizerClassName };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, COULD_NOT_CREATE_CUSTOMIZER_INSTANCE, args), e);
        exception.setErrorCode(COULD_NOT_CREATE_CUSTOMIZER_INSTANCE);
        return exception;
    }

    /**
     * This exception would typically be thrown when a customizer class is set
     * that is not an instance of DescriptorCustomizer.
     *
     * @param e
     * @param customizerClassName
     * @return
     */
    public static JAXBException invalidCustomizerClass(Exception e, String customizerClassName) {
        Object[] args = { customizerClassName };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, INVALID_CUSTOMIZER_CLASS, args), e);
        exception.setErrorCode(INVALID_CUSTOMIZER_CLASS);
        return exception;
    }

    /**
     * This exception should be used when an attempt is made to set an ID property
     * when one has already been set.
     *
     * @param propertyName attempting to set this property as ID
     * @param idPropertyName existing ID property
     * @param className class in question
     * @return
     */
    public static JAXBException idAlreadySet(String propertyName, String idPropertyName, String className) {
        Object[] args = { propertyName, className, idPropertyName };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, ID_ALREADY_SET, args));
        exception.setErrorCode(ID_ALREADY_SET);
        return exception;
    }

    /**
     * This exception should be used when an attempt is made to set an XmlValue property
     * when one has already been set.
     *
     * @param propertyName attempting to set this property as XmlValue
     * @param xmlValuePropertyName existing XmlValue property
     * @param className class in question
     * @return
     */
    public static JAXBException xmlValueAlreadySet(String propertyName, String xmlValuePropertyName, String className) {
        Object[] args = { className, propertyName, xmlValuePropertyName };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, XMLVALUE_ALREADY_SET, args));
        exception.setErrorCode(XMLVALUE_ALREADY_SET);
        return exception;
    }

    /**
     * This exception should be used when an attempt is made to set an XmlAnyElement
     * property when one has already been set.
     *
     * @param propertyName attempting to set this property as XmlAnyElement
     * @param xmlAnyElementPropertyName existing XmlAnyElement property
     * @param className class in question
     * @return
     */
    public static JAXBException xmlAnyElementAlreadySet(String propertyName, String xmlAnyElementPropertyName, String className) {
        Object[] args = { className, propertyName, xmlAnyElementPropertyName };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, XMLANYELEMENT_ALREADY_SET, args));
        exception.setErrorCode(XMLANYELEMENT_ALREADY_SET);
        return exception;
    }

    /**
     * This exception should be used when DomHandlerConverter initialization fails.
     *
     * @param nestedException
     * @param domHandlerClassName
     * @param propertyName
     * @return
     */
    public static JAXBException couldNotInitializeDomHandlerConverter(Exception nestedException, String domHandlerClassName, String propertyName) {
        Object[] args = { domHandlerClassName, propertyName };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, COULD_NOT_INITIALIZE_DOM_HANDLER_CONVERTER, args), nestedException);
        exception.setErrorCode(COULD_NOT_INITIALIZE_DOM_HANDLER_CONVERTER);
        return exception;
    }

    /**
     * This exception should be used when an @XmlAttributeRef or xml-attribute-ref appears
     * on a non-DataHandler property.
     *
     * @param propertyName
     * @param className
     * @return
     */
    public static JAXBException invalidAttributeRef(String propertyName, String className) {
        Object[] args = { propertyName, className };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, INVALID_TYPE_FOR_XMLATTRIBUTEREF_PROPERTY, args));
        exception.setErrorCode(INVALID_TYPE_FOR_XMLATTRIBUTEREF_PROPERTY);
        return exception;
    }

    /**
     * This exception should be used when XmlElements and XmlIDREF are set on a property,
     * but one or more of the XmlElement entries in the list has a type that does not
     * have an XmlID property.
     *
     * @param propertyName
     * @param elementName
     * @return
     */
    public static JAXBException invalidXmlElementInXmlElementsList(String propertyName, String elementName) {
        Object[] args = { propertyName, elementName };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, INVALID_XMLELEMENT_IN_XMLELEMENTS, args));
        exception.setErrorCode(INVALID_XMLELEMENT_IN_XMLELEMENTS);
        return exception;
    }

    /**
     * This exception should be used when a TypeMappingInfo is specified but the Type is not set on it.
     * @param tagName
     * @return
     */
    public static JAXBException nullTypeOnTypeMappingInfo(QName tagName) {
        Object[] args = { tagName };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, NULL_TYPE_ON_TYPEMAPPINGINFO, args));
        exception.setErrorCode(NULL_TYPE_ON_TYPEMAPPINGINFO);
        return exception;
    }

    /**
     * This exception should be used when a TypeMappingInfo is specified but the Type is not set on it.
     * @param tagName
     * @return
     */
    public static JAXBException javaTypeNotAllowedInBindingsFile(String javaTypePackage, String bindingsPackage) {
        Object[] args = { javaTypePackage, bindingsPackage };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, JAVATYPE_NOT_ALLOWED_IN_BINDINGS_FILE, args));
        exception.setErrorCode(JAVATYPE_NOT_ALLOWED_IN_BINDINGS_FILE);
        return exception;
    }

    /**
     * This exception is thrown if the user tries to create a new instance of DynamicJAXBContext using an
     * array of actual classes.
     */
    public static JAXBException cannotCreateDynamicContextFromClasses() {
        Object[] args = { };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, CANNOT_CREATE_DYNAMIC_CONTEXT_FROM_CLASSES, args));
        exception.setErrorCode(CANNOT_CREATE_DYNAMIC_CONTEXT_FROM_CLASSES);
        return exception;
    }

    /**
     * This exception is thrown if the user tries to create a new instance of DynamicJAXBContext using an
     * Node, that is not an instance of either Document or Element.
     */
    public static JAXBException cannotInitializeFromNode() {
        Object[] args = { };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, CANNOT_INITIALIZE_FROM_NODE, args));
        exception.setErrorCode(CANNOT_INITIALIZE_FROM_NODE);
        return exception;
    }

    /**
     * This is a general exception, thrown if a problem was encountered during DynamicJAXBContext creation.
     */
    public static JAXBException errorCreatingDynamicJAXBContext(Exception nestedException) {
        Object[] args = { };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, ERROR_CREATING_DYNAMICJAXBCONTEXT, args), nestedException);
        exception.setErrorCode(ERROR_CREATING_DYNAMICJAXBCONTEXT);
        return exception;
    }

    /**
     * This exception is thrown if the user tries to get a non-existant enum constant from an enum class.
     */
    public static JAXBException enumConstantNotFound(String name) {
        Object[] args = { name };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, ENUM_CONSTANT_NOT_FOUND, args));
        exception.setErrorCode(ENUM_CONSTANT_NOT_FOUND);
        return exception;
    }

    /**
     * This exception is thrown if the user tries bootstrap from sessions.xml but provides a null sessionNames parameter.
     */
    public static JAXBException nullSessionName() {
        Object[] args = { };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, NULL_SESSION_NAME, args));
        exception.setErrorCode(NULL_SESSION_NAME);
        return exception;
    }

    /**
     * This exception is thrown if the user tries bootstrap from XML Schema but provides a null Source parameter.
     */
    public static JAXBException nullSource() {
        Object[] args = { };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, NULL_SOURCE, args));
        exception.setErrorCode(NULL_SOURCE);
        return exception;
    }

    /**
     * This exception is thrown if the user tries bootstrap from XML Schema but provides a null InputStream parameter.
     */
    public static JAXBException nullInputStream() {
        Object[] args = { };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, NULL_INPUT_STREAM, args));
        exception.setErrorCode(NULL_INPUT_STREAM);
        return exception;
    }

    /**
     * This exception is thrown if the user tries bootstrap from XML Schema but provides a null Node parameter.
     */
    public static JAXBException nullNode() {
        Object[] args = { };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, NULL_NODE, args));
        exception.setErrorCode(NULL_NODE);
        return exception;
    }

    /**
     * This exception is thrown if XJC was unable to generate a CodeModel.
     */
    public static JAXBException xjcBindingError() {
        Object[] args = { };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, XJC_BINDING_ERROR, args));
        exception.setErrorCode(XJC_BINDING_ERROR);
        return exception;
    }

    public static JAXBException classNotFoundException(String className) {
        Object[] args = { className };
        JAXBException exception = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, CLASS_NOT_FOUND_EXCEPTION, args));
        exception.setErrorCode(CLASS_NOT_FOUND_EXCEPTION);
        return exception;
    }

    /**
     * PUBLIC:
     * Cause: ReadTransformer for the specified attribute of the specified class
     * specifies both class and method.
     */
    public static JAXBException readTransformerHasBothClassAndMethod(String propertyName) {
        Object[] args = { propertyName };
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, READ_TRANSFORMER_HAS_BOTH_CLASS_AND_METHOD, args));
        validationException.setErrorCode(READ_TRANSFORMER_HAS_BOTH_CLASS_AND_METHOD);
        return validationException;
    }

    /**
     * PUBLIC:
     * Cause: ReadTransformer for the specified attribute of the specified class
     * specifies neither class nor method.
     */
    public static JAXBException readTransformerHasNeitherClassNorMethod(String propertyName) {
        Object[] args = { propertyName };
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, READ_TRANSFORMER_HAS_NEITHER_CLASS_NOR_METHOD, args));
        validationException.setErrorCode(READ_TRANSFORMER_HAS_NEITHER_CLASS_NOR_METHOD);
        return validationException;
    }

    /**
     * PUBLIC:
     * Cause: WriteTransformer for the specified attribute of the specified class and specified xml-path
     * specifies both class and method.
     */
    public static JAXBException writeTransformerHasBothClassAndMethod(String propertyName, String xmlPath) {
        Object[] args = { propertyName, xmlPath };
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, WRITE_TRANSFORMER_HAS_BOTH_CLASS_AND_METHOD, args));
        validationException.setErrorCode(WRITE_TRANSFORMER_HAS_BOTH_CLASS_AND_METHOD);
        return validationException;
    }

    /**
     * PUBLIC:
     * Cause: WriteTransformer for the specified attribute of the specified class and specified xml-path
     * specifies neither class nor method.
     */
    public static JAXBException writeTransformerHasNeitherClassNorMethod(String propertyName, String xmlPath) {
        Object[] args = { propertyName, xmlPath };
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, WRITE_TRANSFORMER_HAS_NEITHER_CLASS_NOR_METHOD, args));
        validationException.setErrorCode(WRITE_TRANSFORMER_HAS_NEITHER_CLASS_NOR_METHOD);
        return validationException;
    }

    /**
     * PUBLIC:
     * Cause: WriteTransformer for the specified attribute of the specified class
     * has no xml-path specified, or the specified xml-path is invalid.
     */
    public static JAXBException writeTransformerHasNoXmlPath(String propertyName) {
        Object[] args = { propertyName };
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, WRITE_TRANSFORMER_HAS_NO_XMLPATH, args));
        validationException.setErrorCode(WRITE_TRANSFORMER_HAS_NO_XMLPATH);
        return validationException;
    }

    /**
     * PUBLIC:
     * Cause: Write transformation method does not exist, or has the wrong number
     * or type of parameters.
     */
    public static JAXBException noSuchWriteTransformationMethod(String methodName) {
        Object[] args = { methodName };
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, NO_SUCH_WRITE_TRANSFORMATION_METHOD, args));
        validationException.setErrorCode(NO_SUCH_WRITE_TRANSFORMATION_METHOD);
        return validationException;
    }

    /**
     * PUBLIC:
     * Cause: Transformer class could not be loaded.
     */
    public static JAXBException transformerClassNotFound(String className) {
        Object[] args = { className };
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, TRANSFORMER_CLASS_NOT_FOUND, args));
        validationException.setErrorCode(TRANSFORMER_CLASS_NOT_FOUND);
        return validationException;
    }

    /**
     * PUBLIC:
     * Cause: Properties passed to createDynamicFromOXM did not contain
     * ECLIPSELINK_OXM_FILE_KEY, or was null.
     */
    public static JAXBException oxmKeyNotFound() {
        Object[] args = { };
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, OXM_KEY_NOT_FOUND, args));
        validationException.setErrorCode(OXM_KEY_NOT_FOUND);
        return validationException;
    }

    /**
     * PUBLIC:
     * Cause: The referenced class (i.e. actualType) of the Property containing the
     * XmlJoinNodes declaration does not have an associated TypeInfo.
     */
    public static JAXBException invalidXmlJoinNodeReferencedClass(String propertyName, String referencedClassName) {
        Object[] args = { propertyName, referencedClassName };
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, INVALID_REF_CLASS, args));
        validationException.setErrorCode(INVALID_REF_CLASS);
        return validationException;
    }

    /**
     * PUBLIC:
     * Cause:  The reference class (i.e. actualType) of a given Property is marked
     * transient.  I.e. List<Address> addresses;  where Address is marked
     * transient.
     */
    public static JAXBException invalidReferenceToTransientClass(String className, String propertyName, String referencedClassName) {
        Object[] args = { className, propertyName, referencedClassName };
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, TRANSIENT_REF_CLASS, args));
        validationException.setErrorCode(TRANSIENT_REF_CLASS);
        return validationException;
    }

    /**
     * PUBLIC:
     * Cause:  The target class has no ID or Key properties set.
     */
    public static JAXBException noKeyOrIDPropertyOnJoinTarget(String className, String propertyName, String referencedClassName) {
        Object[] args = { className, propertyName, referencedClassName };
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, NO_ID_OR_KEY_ON_JOIN_TARGET, args));
        validationException.setErrorCode(NO_ID_OR_KEY_ON_JOIN_TARGET);
        return validationException;
    }

    /**
     * PUBLIC:
     * Cause: No ID or Key property exists on the target class with an XPath == referencedXmlPath.
     */
    public static JAXBException invalidReferencedXmlPathOnJoin(String className, String propertyName, String referencedClassName, String referencedXmlPath) {
        Object[] args = { className, propertyName, referencedClassName, referencedXmlPath };
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, INVALID_REF_XML_PATH, args));
        validationException.setErrorCode(INVALID_REF_XML_PATH);
        return validationException;
    }

    /**
     * PUBLIC:
     * Cause: The referenced class (i.e. actualType) of the Property containing the
     * XmlIDREF declaration does not have an associated TypeInfo.
     */
    public static JAXBException invalidIDREFClass(String className, String propertyName, String referencedClassName) {
        Object[] args = { className, propertyName, referencedClassName };
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, INVALID_IDREF_CLASS, args));
        validationException.setErrorCode(INVALID_IDREF_CLASS);
        return validationException;
    }

    /**
     * PUBLIC:
     * Cause: The adapter class set on XMLJavaTypeConverter could not be loaded. This is
     * most likely due to an incorrect class name or the wrong classloader being set on
     * XMLConversionManager.
     */
    public static JAXBException adapterClassNotLoaded(String adapterClassName, Exception ex) {
        Object[] args = { adapterClassName };
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, ADAPTER_CLASS_NOT_LOADED, args), ex);
        validationException.setErrorCode(ADAPTER_CLASS_NOT_LOADED);
        return validationException;
    }

    /**
     * PUBLIC:
     * Cause: An exception occurred while attempting to get the declared methods from
     * the adapter class.
     */
    public static JAXBException adapterClassMethodsCouldNotBeAccessed(String adapterClassName, Exception ex) {
        Object[] args = { adapterClassName };
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, ADAPTER_CLASS_METHOD_EXCEPTION, args), ex);
        validationException.setErrorCode(ADAPTER_CLASS_METHOD_EXCEPTION);
        return validationException;
    }

    /**
     * PUBLIC:
     * Cause: An exception occurred while attempting to get a new instance of
     * the adapter class.
     */
    public static JAXBException adapterClassCouldNotBeInstantiated(String adapterClassName, Exception ex) {
        Object[] args = { adapterClassName };
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, ADAPTER_CLASS_COULD_NOT_BE_INSTANTIATED, args), ex);
        validationException.setErrorCode(ADAPTER_CLASS_COULD_NOT_BE_INSTANTIATED);
        return validationException;
    }

    /**
     * PUBLIC:
     * Cause: The adapter class does not extend javax.xml.bind.annotation.adapters.XmlAdapter.
     */
    public static JAXBException invalidAdapterClass(String adapterClassName) {
        Object[] args = { adapterClassName };
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, INVALID_ADAPTER_CLASS, args));
        validationException.setErrorCode(INVALID_ADAPTER_CLASS);
        return validationException;
    }

    /**
     * PUBLIC:
     * Cause: The package level adapter class set on XMLJavaTypeConverter could not be
     * loaded. This is most likely due to an incorrect class name or the wrong
     * classloader being set on XMLConversionManager.
     *
     * @param adapterClassName the name of the XmlAdapterClass
     * @param packageName name of the target package
     */
    public static JAXBException invalidPackageAdapterClass(String adapterClassName, String packageName) {
        Object[] args = { adapterClassName, packageName };
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, INVALID_PACKAGE_ADAPTER_CLASS, args));
        validationException.setErrorCode(INVALID_PACKAGE_ADAPTER_CLASS);
        return validationException;
    }

    /**
     * PUBLIC:
     * Cause: The type level adapter class set on XMLJavaTypeConverter could not be
     * loaded. This is most likely due to an incorrect class name or the wrong
     * classloader being set on XMLConversionManager.
     *
     * @param adapterClassName the name of the XmlAdapterClass
     * @param typeName name of the target type
     */
    public static JAXBException invalidTypeAdapterClass(String adapterClassName, String typeName) {
        Object[] args = { adapterClassName, typeName };
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, INVALID_TYPE_ADAPTER_CLASS, args));
        validationException.setErrorCode(INVALID_TYPE_ADAPTER_CLASS);
        return validationException;
    }

    /**
     * PUBLIC:
     * Cause: The field/property level adapter class set on XMLJavaTypeConverter could
     * not be loaded. This is most likely due to an incorrect class name or the wrong
     * classloader being set on XMLConversionManager.
     *
     * @param adapterClassName the name of the XmlAdapterClass
     * @param propName the name of the field/property
     * @param typeName name of the owning type
     */
    public static JAXBException invalidPropertyAdapterClass(String adapterClassName, String propName, String typeName) {
        Object[] args = { adapterClassName, propName, typeName };
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, INVALID_PROPERTY_ADAPTER_CLASS, args));
        validationException.setErrorCode(INVALID_PROPERTY_ADAPTER_CLASS);
        return validationException;
    }

    /**
     * PUBLIC:
     * Cause: There is a different number of XmlElements and XmlJoinNodes entries in a given
     * XmlElementsJoinNodes.  There must be an equal number of each.
     *
     * @param propertyName name of the Property containing the XmlElementsJoinNodes
     * @param className name of the owning class
     * @return
     */
    public static JAXBException incorrectNumberOfXmlJoinNodesOnXmlElements(String propertyName, String className) {
        Object[] args = { propertyName, className };
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, INCORRECT_NUMBER_OF_XMLJOINNODES_ON_XMLELEMENTS, args));
        validationException.setErrorCode(INCORRECT_NUMBER_OF_XMLJOINNODES_ON_XMLELEMENTS);
        return validationException;
    }

    /**
     * When the target of an XmlPaths -> XmlPath is an attribute, it must be nested, and
     * not the root of the path.
     *
     * @param propertyName name of the Property containing the XmlPaths
     * @param className name of the owning class
     * @param xpath the offending XmlPath
     * @return
     */
    public static JAXBException invalidXmlPathWithAttribute(String propertyName, String className, String xpath) {
        Object[] args = { propertyName, className, xpath };
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, INVALID_XML_PATH_ATTRIBUTE, args));
        validationException.setErrorCode(INVALID_XML_PATH_ATTRIBUTE);
        return validationException;
    }

    public static JAXBException duplicatePropertyName(String propertyName, String className) {
        Object[] args = {propertyName, className};
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, DUPLICATE_PROPERTY_NAME, args));
        validationException.setErrorCode(DUPLICATE_PROPERTY_NAME);
        return validationException;
    }

    public static JAXBException samePropertyInMultipleFiles(String propertyName, String className) {
        Object[] args = {propertyName, className};
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, SAME_PROPERTY_IN_MULTIPLE_BINDINGS_FILES, args));
        validationException.setErrorCode(SAME_PROPERTY_IN_MULTIPLE_BINDINGS_FILES);
        return validationException;

    }

    /**
     * PUBLIC:
     * Cause: An exception occurred while attempting to get a new instance of
     * the transformer class.
     */
    public static JAXBException exceptionWithNameTransformerClass(String nametransformerClassName, Exception ex) {
        Object[] args = { nametransformerClassName };
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, EXCEPTION_WITH_NAME_TRANSFORMER_CLASS, args), ex);
        validationException.setErrorCode(EXCEPTION_WITH_NAME_TRANSFORMER_CLASS);
        return validationException;
    }

    /**
     * PUBLIC:
     * Cause: An exception occurred during transformation to an xml name
     */
    public static JAXBException exceptionDuringNameTransformation(String nameBeingTransformed, String nametransformerClassName, Exception ex) {
        Object[] args = { nameBeingTransformed, nametransformerClassName };
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, EXCEPTION_DURING_NAME_TRANSFORMATION, args), ex);
        validationException.setErrorCode(EXCEPTION_DURING_NAME_TRANSFORMATION);
        return validationException;
    }

    public static JAXBException unableToLoadMetadataFromLocation(String location) {
        Object[] args = { location };
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, UNABLE_TO_LOAD_METADATA_FROM_LOCATION, args));
        validationException.setErrorCode(UNABLE_TO_LOAD_METADATA_FROM_LOCATION);
        return validationException;
    }

    public static JAXBException cannotRefreshMetadata() {
        Object[] args = { };
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, CANNOT_REFRESH_METADATA, args));
        validationException.setErrorCode(CANNOT_REFRESH_METADATA);
        return validationException;
    }

    public static JAXBException xjbNotSource() {
        Object[] args = { };
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, XJB_NOT_SOURCE, args));
        validationException.setErrorCode(XJB_NOT_SOURCE);
        return validationException;
    }

    public static JAXBException xsdImportNotSource() {
        Object[] args = { };
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, XSD_IMPORT_NOT_SOURCE, args));
        validationException.setErrorCode(XSD_IMPORT_NOT_SOURCE);
        return validationException;
    }

    /**
     * Cause: @XmlLocation is set on a non-Locator property.
     */
    public static JAXBException invalidXmlLocation(String propertyName, String propertyType) {
        Object[] args = { propertyName, propertyType };
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, INVALID_XMLLOCATION, args));
        validationException.setErrorCode(INVALID_XMLLOCATION);
        return validationException;
    }

    /**
     * PUBLIC:
     * Cause: An exception occurred during schema generation.
     */
    public static JAXBException exceptionDuringSchemaGeneration(Exception ex) {
        Object[] args = { };
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, EXCEPTION_DURING_SCHEMA_GEN, args), ex);
        validationException.setErrorCode(EXCEPTION_DURING_SCHEMA_GEN);
        return validationException;
    }
    
    /**
     * PUBLIC:
     * Cause: An exception occurred marshalling json
     */
    public static JAXBException jsonValuePropertyRequired(Object value) {
        Object[] args = { value };
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, JSON_VALUE_WRAPPER_REQUIRED, args));
        validationException.setErrorCode(JSON_VALUE_WRAPPER_REQUIRED);
        return validationException;
    }
    
    /**
     * PUBLIC:
     * Cause: An exception occured while trying to instantiate an instance of the
     * user specified AccessorFactory
     */
    public static JAXBException errorInstantiatingAccessorFactory(Object factoryClass, Exception nestedException) {
        Object[] args = {factoryClass};
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, ERROR_INSTANTIATING_ACCESSOR_FACTORY, args), nestedException);
        validationException.setErrorCode(ERROR_INSTANTIATING_ACCESSOR_FACTORY);
        return validationException;
    }
    
    public static JAXBException invalidAccessorFactory(Object accessorFactoryClass, Exception nestedException) {
        Object[] args = {accessorFactoryClass};
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, INVALID_ACCESSOR_FACTORY, args), nestedException);
        validationException.setErrorCode(INVALID_ACCESSOR_FACTORY);
        return validationException;
    }
    
    public static JAXBException errorCreatingFieldAccessor(Object accessorFactory, Exception nestedException) {
        Object[] args = {accessorFactory};
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, ERROR_CREATING_FIELD_ACCESSOR, args), nestedException);
        validationException.setErrorCode(ERROR_CREATING_FIELD_ACCESSOR);
        return validationException;
    }
    
    public static JAXBException errorCreatingPropertyAccessor(Object accessorFactory, Exception nestedException) {
        Object[] args = {accessorFactory};
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, ERROR_CREATING_PROPERTY_ACCESSOR, args), nestedException);
        validationException.setErrorCode(ERROR_CREATING_PROPERTY_ACCESSOR);
        return validationException;
    }
    
    public static JAXBException errorInvokingAccessor(Object accessor, String method, Exception nestedException) {
        Object[] args = {method, accessor};
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, ERROR_INVOKING_ACCESSOR, args), nestedException);
        validationException.setErrorCode(ERROR_INVOKING_ACCESSOR);
        return validationException;
    }
    
    public static JAXBException invalidEnumValue(Object value, String theClassName, Exception nestedException) {
        Object[] args = {value, theClassName};
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, INVALID_ENUM_VALUE, args), nestedException);
        validationException.setErrorCode(INVALID_ENUM_VALUE);
        return validationException;
    }
    
    public static JAXBException invalidInterface(String interfaceName) {
        Object[] args = {interfaceName};
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, INVALID_INTERFACE, args));
        validationException.setErrorCode(INVALID_INTERFACE);
        return validationException;
    }
    
    public static JAXBException invalidValueForObjectGraph(Object value) {
        Object[] args = {value};
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class,  INVALID_VALUE_FOR_OBJECT_GRAPH, args));
        validationException.setErrorCode(INVALID_VALUE_FOR_OBJECT_GRAPH);
        return validationException;
    }

    public static JAXBException duplicateElementName(QName qName) {
        Object[] args = {qName};
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, DUPLICATE_ELEMENT_NAME, args));
        validationException.setErrorCode(DUPLICATE_ELEMENT_NAME);
        return validationException;
    }

    public static JAXBException multipleXmlElementRef(String propertyTypeName, String className) {
        Object[] args = { propertyTypeName, className };
        JAXBException validationException = new JAXBException(ExceptionMessageGenerator.buildMessage(JAXBException.class, MULTIPLE_XMLELEMREF, args));
        validationException.setErrorCode(MULTIPLE_XMLELEMREF);
        return validationException;
    }
}