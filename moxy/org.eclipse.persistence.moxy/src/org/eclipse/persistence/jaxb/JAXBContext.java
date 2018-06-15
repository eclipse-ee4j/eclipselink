/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     Marcel Valovy - 2.6 - added ci unmarshalling & BV in JAXB
//     Dmitry Kornilov - 2.6.1 - BeanValidationHelper refactoring
package org.eclipse.persistence.jaxb;

import org.eclipse.persistence.core.queries.CoreAttributeGroup;
import org.eclipse.persistence.core.sessions.CoreProject;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.jaxb.JAXBSchemaOutputResolver;
import org.eclipse.persistence.internal.jaxb.JaxbClassLoader;
import org.eclipse.persistence.internal.jaxb.ObjectGraphImpl;
import org.eclipse.persistence.internal.jaxb.WrappedValue;
import org.eclipse.persistence.internal.jaxb.json.schema.JsonSchemaGenerator;
import org.eclipse.persistence.internal.jaxb.json.schema.model.JsonSchema;
import org.eclipse.persistence.internal.jaxb.many.ManyValue;
import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.Root;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.mappings.ChoiceCollectionMapping;
import org.eclipse.persistence.internal.oxm.mappings.ChoiceObjectMapping;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.internal.oxm.schema.SchemaModelGenerator;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jaxb.compiler.Generator;
import org.eclipse.persistence.jaxb.compiler.MarshalCallback;
import org.eclipse.persistence.jaxb.compiler.UnmarshalCallback;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.reflection.AnnotationHelper;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelImpl;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelInputImpl;
import org.eclipse.persistence.jaxb.json.JsonSchemaOutputResolver;
import org.eclipse.persistence.jaxb.xmlmodel.JavaType;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings.JavaTypes;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.MediaType;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.platform.SAXPlatform;
import org.eclipse.persistence.oxm.platform.XMLPlatform;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.SessionEventListener;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.transform.Source;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.eclipse.persistence.jaxb.javamodel.Helper.getQualifiedJavaTypeName;

/**
 * <p><b>Purpose:</b>Provide a EclipseLink implementation of the JAXBContext interface.
 * <p><b>Responsibilities:</b><ul>
 * <li>Create Marshaller instances</li>
 * <li>Create Unmarshaller instances</li>
 * <li>Create Binder instances</li>
 * <li>Create Introspector instances</li>
 * <li>Create Validator instances</li>
 * <li>Generate Schema Files</li>
 * </ul>
 * <p>This is the EclipseLink JAXB 2.0 implementation of javax.xml.bind.JAXBContext. This class
 * is created by the JAXBContextFactory and is used to create Marshallers, Unmarshallers, Validators,
 * Binders and Introspectors. A JAXBContext can also be used to create Schema Files.
 * <p><b>Bootstrapping:</b>
 * When bootstrapping the JAXBContext from a EclipseLink externalized metadata file(s) a number of
 * input options are available.  The externalized metadata file (one per package) is passed in
 * through a property when creating the JAXBContext.  The key used in the properties map is
 * "eclipselink-oxm-xml".  The externalized metadata file can be set in the properties map in
 * one of three ways:
 * <p>i) For a single externalized metadata file, one of the following can be set in the properties map:<ul>
 * <li>java.io.File</li>
 * <li>java.io.InputStream</li>
 * <li>java.io.Reader</li>
 * <li>java.net.URL</li>
 * <li>javax.xml.stream.XMLEventReader</li>
 * <li>javax.xml.stream.XMLStreamReader</li>
 * <li>javax.xml.transform.Source</li>
 * <li>org.w3c.dom.Node</li>
 * <li>org.xml.sax.InputSource</li></ul>
 * When using one of the above options, the package name must be set via package-name attribute on the
 * xml-bindings element in the externalized metadata file.
 * <p>ii) For multiple externalized metadata files where the package name is specified within each externalized
 * metadata file, a List can be used.  The entries in the List are to be one of the types listed in i) above.
 * <p>iii) For multiple externalized metadata files where the package name is not specified in each externalized
 * metadata file, a Map can be used.  The key must be a String (package name) and  each value in the Map
 * (externalized metadata file) is to be one of the types listed in i) above.
 * <p>Note that in each of the above cases the package name can be set via package-name attribute on the
 * xml-bindings element in the externalized metadata file.  If set, any java-type names in the given metadata
 * file that do not contain the package name will have that package name prepended to it.  Also note that a
 * List or Map can be used for a single externalized metadata file.
 * <p>
 * @see javax.xml.bind.JAXBContext
 * @see org.eclipse.persistence.jaxb.JAXBMarshaller
 * @see org.eclipse.persistence.jaxb.JAXBUnmarshaller
 * @see org.eclipse.persistence.jaxb.JAXBBinder
 * @see org.eclipse.persistence.jaxb.JAXBIntrospector
 * @see org.eclipse.persistence.jaxb.JAXBContextProperties
 *
 * @author mmacivor
 */

public class JAXBContext extends javax.xml.bind.JAXBContext {

    private static final Map<String, Boolean> PARSER_FEATURES = new HashMap<>(2);
    static {
        PARSER_FEATURES.put("http://apache.org/xml/features/validation/schema/normalized-value", false);
        PARSER_FEATURES.put("http://apache.org/xml/features/validation/schema/element-default", false);
    }

    private static final String RI_XML_ACCESSOR_FACTORY_SUPPORT = "com.sun.xml.bind.XmlAccessorFactory";

    /**
      * For JAXB 2 there is no explicitly defined default validation handler
      * and the default event handling only terminates the  operation after
      * encountering a fatal error.
      */
    protected static final ValidationEventHandler DEFAULT_VALIDATION_EVENT_HANDLER = new ValidationEventHandler() {
        @Override
        public boolean handleEvent(ValidationEvent event) {
            return event.getSeverity() < ValidationEvent.FATAL_ERROR;
        }
    };

    private final AtomicBoolean hasLoggedValidatorInfo = new AtomicBoolean();
    protected JAXBContextInput contextInput;

    protected volatile JAXBContextState contextState;
    private XMLInputFactory xmlInputFactory;

    private boolean initializedXMLInputFactory = false;
    private JAXBMarshaller jsonSchemaMarshaller;

    private static volatile BeanValidationHelper beanValidationHelper;
    private static volatile Boolean beanValidationPresent;

    protected JAXBContext() {
        super();
        contextState = new JAXBContextState();
        initBeanValidation();
    }

    protected JAXBContext(JAXBContextInput contextInput) throws javax.xml.bind.JAXBException {
        this.contextInput = contextInput;
        this.contextState = contextInput.createContextState();
        initBeanValidation();
    }

    /**
     * Create a JAXBContext for a given XMLContext.  The XMLContext contains the
     * metadata about the Object to XML mappings.
     */
    public JAXBContext(XMLContext context) {
        contextState = new JAXBContextState(context);
        initBeanValidation();
    }

    /**
     * Create a JAXBContext. The XMLContext contains the metadata about the
     * Object to XML mappings.
     */
    public JAXBContext(XMLContext context, Generator generator, Type[] boundTypes) {
        contextState = new JAXBContextState(context, generator, boundTypes, null);
        initBeanValidation();
    }

    /**
     * Create a JAXBContext.  The XMLContext contains the metadata about the
     * Object to XML mappings.
     */
    public JAXBContext(XMLContext context, Generator generator, TypeMappingInfo[] boundTypes) {
        contextState = new JAXBContextState(context, generator, boundTypes, null);
        initBeanValidation();
    }

    /**
     * Initializes bean validation if javax.validation.api bundle is on the class path.
     */
    private void initBeanValidation() {
        if (beanValidationPresent == null) {
            beanValidationPresent = BeanValidationChecker.isBeanValidationPresent();
        }
        if (beanValidationPresent && beanValidationHelper == null) {
            synchronized (JAXBContext.class) {
                if (beanValidationHelper == null) {
                    // Bean validation is optional
                    beanValidationHelper = new BeanValidationHelper();
                }
            }
        }
    }

    /**
     * Returns BeanValidationHelper. Can return null if bean validation jar is not on class path.
     */
    public BeanValidationHelper getBeanValidationHelper() {
        return beanValidationHelper;
    }

    public XMLInputFactory getXMLInputFactory() {
        if (!initializedXMLInputFactory) {
            try {
                xmlInputFactory = XMLInputFactory.newInstance();
            } catch (FactoryConfigurationError e) {
            } finally {
                initializedXMLInputFactory = true;
            }
        }
        return xmlInputFactory;
    }

    AtomicBoolean getHasLoggedValidatorInfo() {
        return hasLoggedValidatorInfo;
    }

    /**
     * This event is called when context creation is completed,
     * and provides a chance to deference anything that is no longer
     * needed (to reduce the memory footprint of this object).
     */
    void postInitialize() {
        if (this.contextState.generator != null) {
            this.contextState.generator.postInitialize();
        }
    }

    /**
     * ADVANCED:
     * <p>Refresh the underlying metadata based on the inputs that were
     * used to create the JAXBContext.  This is particularly useful when using
     * the virtual property mappings.  The refreshMetadata call could be made
     * in the following way:</p>
     * <pre>org.eclipse.persistence.jaxb.JAXBHelper.getJAXBContext(aJAXBContext).refreshMetadata();</pre>
     * <b>Note:</b>
     * <ul>
     * <li>As instances of Binder maintain a cache, calling refreshMetadata will
     * not affect instances of Binder.  To get the new metadata you must create
     * a new instance of Binder after the refresh metadata call has been made.</li>
     * </ul>
     * @throws javax.xml.bind.JAXBException
     */
    public void refreshMetadata() throws javax.xml.bind.JAXBException {
        JAXBContextState newState = newContextState();
        if (newState != null) {
            contextState = newState;
        }
    }

    /**
     * INTERNAL:
     * Build a new JAXBContextState from the current JAXBContextInput.
     */
    private JAXBContextState newContextState() throws javax.xml.bind.JAXBException {
        if (null == contextInput) {
            return null;
        }
        synchronized (this) {
            JAXBContextState newState = contextInput.createContextState();
            XMLContext xmlContext = getXMLContext();
            xmlContext.setXMLContextState(newState.getXMLContext().getXMLContextState());
            newState.setXMLContext(xmlContext);
            newState.setTypeToTypeMappingInfo(contextState.getTypeToTypeMappingInfo());

            return newState;
        }
    }

    /**
     * INTERNAL:
     * Indicates if this JAXBContext can have its metadata refreshed.
     */
    boolean isRefreshable() {
        return false;
        /*
        if (this.contextInput.properties == null) {
            return true;
        }
        if (this.contextInput.properties.containsKey(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY)) {
            return false;
        }
        return true;
        */
    }

    /**
     * Return the XMLContext associated with this JAXBContext.
     */
    public XMLContext getXMLContext() {
        return contextState.getXMLContext();
    }

    public void setXMLContext(XMLContext xmlContext) {
        contextState.setXMLContext(xmlContext);
    }

    /**
     * Generate a Schema for this JAXBContext
     *
     * @param outputResolver Class that decides where the schema file (of the given namespace URI) will be written
     */
    @Override
    public void generateSchema(SchemaOutputResolver outputResolver)  {
        if(outputResolver instanceof JsonSchemaOutputResolver) {
            generateJsonSchema(outputResolver, ((JsonSchemaOutputResolver)outputResolver).getRootClass());
        } else {
            generateSchema(outputResolver, null);
        }
    }

    public void generateJsonSchema(SchemaOutputResolver outputResolver, Class rootClass) {
        JsonSchemaGenerator generator = new JsonSchemaGenerator(this, this.contextState.properties);
        JsonSchema schema = generator.generateSchema(rootClass);
        try {
            Marshaller m = getJsonSchemaMarshaller();
            m.marshal(schema, outputResolver.createOutput(null, rootClass.getName() + ".json"));
        } catch (Exception ex) {
            throw org.eclipse.persistence.exceptions.JAXBException.exceptionDuringSchemaGeneration(ex);
        }
    }

    private Marshaller getJsonSchemaMarshaller() throws javax.xml.bind.JAXBException {
        if (this.jsonSchemaMarshaller == null) {
            JAXBContext ctx = (JAXBContext) JAXBContextFactory.createContext(new Class[] { JsonSchema.class }, null);
            this.jsonSchemaMarshaller = ctx.createMarshaller();
            this.jsonSchemaMarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, MediaType.APPLICATION_JSON);
            this.jsonSchemaMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            this.jsonSchemaMarshaller.setProperty(MarshallerProperties.JSON_REDUCE_ANY_ARRAYS, true);
        }

        return this.jsonSchemaMarshaller;

    }

    /**
     * Generate a Schema for this JAXBContext
     *
     * @param outputResolver Class that decides where the schema file (of the given namespace URI) will be written
     * @param additonalGlobalElements Map of additional global elements to be added to the generated XSD.
     * Note that if any QName in this map conflicts with another global element (for example from a TypeMappingInfo object)
     * then the element generated from this map will be the one that is present in the XSD.
     */
    public void generateSchema(SchemaOutputResolver outputResolver, Map<QName, Type> additonalGlobalElements) {
        JAXBContextState currentJAXBContextState = contextState;
        if (isRefreshable()) {
            // Recreate context state, to rebuild Generator
            try {
                currentJAXBContextState = newContextState();
            } catch (Exception e) {
                throw JAXBException.exceptionDuringSchemaGeneration(e);
            }
        }
        XMLContext xmlContext = currentJAXBContextState.getXMLContext();
        Generator generator = currentJAXBContextState.getGenerator();
        if (generator == null) {
            SchemaModelGenerator smGen = new SchemaModelGenerator(xmlContext.getOxmConversionManager());
            smGen.generateSchemas(xmlContext.getDescriptors(), null, new JAXBSchemaOutputResolver(outputResolver), additonalGlobalElements);
        } else {
            generator.generateSchemaFiles(outputResolver, additonalGlobalElements);
        }
    }

    /**
     * Create a JAXBMarshaller.  The JAXBMarshaller is used to convert Java objects
     * to XML.
     */
    @Override
    public JAXBMarshaller createMarshaller() throws javax.xml.bind.JAXBException {
        return contextState.createMarshaller(this);
    }

    /**
     * Create a JAXBUnmarshaller.  The JAXBUnmarshaller is used to convert XML into
     * Java objects.
     */
    @Override
    public JAXBUnmarshaller createUnmarshaller() throws javax.xml.bind.JAXBException {
        return contextState.createUnmarshaller(this);
    }

    /**
     * Create a JAXBValidator.  The JAXBValidator is used to validate Java objects against
     * an XSD.
     */
    @Override
    public JAXBValidator createValidator() {
        return new JAXBValidator(getXMLContext().createValidator());
    }

    /**
     * Create a JAXBBinder.  The JAXBBinder is used to preserve unmapped XML Data.
     */
    @Override
    public JAXBBinder createBinder() {
        return contextState.createBinder(this);
    }

    /**
     * Create a JAXBBinder.  The JAXBBinder is used to preserve unmapped XML Data.
     *
     * @param nodeClass The DOM Node class to use
     */
    @Override
    public <T> JAXBBinder createBinder(Class<T> nodeClass) {
        if (nodeClass.getName().equals("org.w3c.dom.Node")) {
            return contextState.createBinder(this);
        } else {
            throw new UnsupportedOperationException(JAXBException.unsupportedNodeClass(nodeClass.getName()));
        }
    }

    /**
     * Creates a JAXBIntrospector object.  The JAXBIntrospector allows the user to
     * access certain pieces of metadata about an instance of a JAXB bound class.
     */
    @Override
    public JAXBIntrospector createJAXBIntrospector() {
        return new JAXBIntrospector(getXMLContext());
    }

    /**
     * INTERNAL:
     * Set the map containing which QName corresponds to which generated class.
     */
    public void setQNameToGeneratedClasses(HashMap<QName, Class> qNameToClass) {
        contextState.setQNameToGeneratedClasses(qNameToClass);
    }

    /**
     * INTERNAL:
     * Get the map containing which Class (by name) corresponds to which generated class.
     */
    public Map<String, Class> getClassToGeneratedClasses() {
        return contextState.getClassToGeneratedClasses();
    }

    /**
     * INTERNAL:
     * Set the map containing which Class (by name) corresponds to which generated class.
     */
    public void setClassToGeneratedClasses(HashMap<String, Class> classToClass) {
        contextState.setClassToGeneratedClasses(classToClass);
    }

    /**
     * ADVANCED:
     * Adjust the OXM metadata to take into account ORM mapping metadata
     */
    public void applyORMMetadata(AbstractSession ormSession) {
        getXMLContext().applyORMMetadata(ormSession);
    }

    /**
     * INTERNAL:
     * Get the map of which QName corresponds to which declared class.
     */
    public Map<QName, Class> getQNamesToDeclaredClasses() {
        return contextState.getQNamesToDeclaredClasses();
    }

    /**
     * INTERNAL:
     * Get the map of which QName corresponds to which generated class.
     */
    Map<QName, Class> getQNameToGeneratedClasses() {
        return contextState.getQNameToGeneratedClasses();
    }

    /**
     *  INTERNAL:
     *  Set the map of which QName corresponds to which declared class.
     */
    public void setQNamesToDeclaredClasses(HashMap<QName, Class> nameToDeclaredClasses) {
        contextState.setQNamesToDeclaredClasses(nameToDeclaredClasses);
    }

    /**
     * INTERNAL:
     * Get the map for which array class (by name) corresponds to which generated class
     */
    public Map<String, Class> getArrayClassesToGeneratedClasses() {
        if (contextState.getGenerator() == null) {
            return null;
        }
        return contextState.getGenerator().getAnnotationsProcessor().getArrayClassesToGeneratedClasses();
    }

    /**
     * INTERNAL:
     * Get the map for which collection class (by Type) corresponds to which generated class
     */
    public Map<Type, Class> getCollectionClassesToGeneratedClasses() {
        if (contextState.getGenerator() == null) {
            return null;
        }
        return contextState.getGenerator().getAnnotationsProcessor().getCollectionClassesToGeneratedClasses();
    }

    /**
     * INTERNAL:
     * Populate the map of which Type corresponds to which QName.
     * The keys should be all the boundTypes used to create the JAXBContext.
     * If the JAXBContext was not created with the constructor that takes a Type[] then
     * this Map will be empty.
     */
    public void initTypeToSchemaType() {
        contextState.initTypeToSchemaType();
    }

    /**
     * INTERNAL:
     * Get the map of which TypeMappingInfo corresponds to which QName.
     * The keys should be all the boundTypes used to create the JAXBContext.
     * If the JAXBContext was not created with the constructor that takes a TypeMappingInfo[]
     * this Map will be empty.
     */
    public Map<TypeMappingInfo, QName> getTypeMappingInfoToSchemaType() {
        return contextState.getTypeMappingInfoToSchemaType();
    }

    /**
     * INTERNAL:
     * Get the map of which Type corresponds to which QName.
     * The keys should be all the boundTypes used to create the JAXBContext.
     * If the JAXBContext was not created with the constructor that takes a Type[] then
     * this Map will be empty.
     */
    public Map<java.lang.reflect.Type, QName> getTypeToSchemaType() {
        return contextState.getTypeToSchemaType();
    }

    Map<TypeMappingInfo, Class> getTypeMappingInfoToGeneratedType() {
        return contextState.getTypeMappingInfoToGeneratedType();
    }

    Map<Type, TypeMappingInfo> getTypeToTypeMappingInfo() {
        return contextState.getTypeToTypeMappingInfo();
    }

    void setTypeToTypeMappingInfo(Map<Type, TypeMappingInfo> typeToMappingInfo) {
        contextState.setTypeToTypeMappingInfo(typeToMappingInfo);
    }

    void setTypeMappingInfoToJavaTypeAdapaters(Map<TypeMappingInfo, JAXBContext.RootLevelXmlAdapter> typeMappingInfoToAdapters) {
        contextState.setTypeMappingInfoToJavaTypeAdapaters(typeMappingInfoToAdapters);
    }

    static class RootLevelXmlAdapter {
        private XmlAdapter xmlAdapter;
        private Class boundType;

        public RootLevelXmlAdapter(XmlAdapter adapter, Class boundType) {
            this.xmlAdapter = adapter;
            this.boundType = boundType;
        }

        public XmlAdapter getXmlAdapter() {
            return xmlAdapter;
        }

        public Class getBoundType() {
            return boundType;
        }

        public void setXmlAdapter(XmlAdapter xmlAdapter) {
            this.xmlAdapter = xmlAdapter;
        }

        public void setBoundType(Class boundType) {
            this.boundType = boundType;
        }
    }

    Map<TypeMappingInfo, RootLevelXmlAdapter> getTypeMappingInfoToJavaTypeAdapters() {
        return contextState.getTypeMappingInfoToJavaTypeAdapters();
    }

    /**
     * Get a value from an object based on an XPath statement.
     *
     * @param <T>
     *      The return type of this method corresponds to the returnType parameter.
     * @param object
     *      The XPath will be executed relative to this object.
     * @param xPath
     *      The XPath statement.
     * @param namespaceResolver
     *      A <tt>NamespaceResolver</tt> containing the prefix/URI pairings from the XPath statement.
     * @param returnType
     *      The return type.
     *
     * @return
     *      The object corresponding to the XPath or null if no result was found.
     */
    public <T> T getValueByXPath(Object object, String xPath, NamespaceResolver namespaceResolver, Class<T> returnType) {
        return getXMLContext().getValueByXPath(object, xPath, namespaceResolver, returnType);
    }

    /**
     * Set a value on an object based on an XPath statement.
     *
     * @param object
     *      The XPath will be executed relative to this object.
     * @param xPath
     *      The XPath statement.
     * @param namespaceResolver
     *      A <tt>NamespaceResolver</tt> containing the prefix/URI pairings from the XPath statement.
     * @param value
     *      The value to be set.
     */
    public void setValueByXPath(Object object, String xPath, NamespaceResolver namespaceResolver, Object value) {
        getXMLContext().setValueByXPath(object, xPath, namespaceResolver, value);
    }

    /**
     * Create a new object instance for a given XML namespace and name.
     *
     * @param namespace
     *      The namespace of the complex type to create a new Java instance of.
     * @param typeName
     *      The XML type name to create a new Java instance of.
     * @param isGlobalType
     *      True if the object to be created represents a global type, false if it
     *      represents a global element.
     *
     * @return
     *      An instance of the Java class mapped to the indicated XML type, or null
     *      if no result was found.
     */
    public Object createByQualifiedName(String namespace, String typeName, boolean isGlobalType) {
        return getXMLContext().createByQualifiedName(namespace, typeName, isGlobalType);
    }

    /**
     * Create a new object instance for a given XPath, relative to the parentObject.
     *
     * @param <T>
     *      The return type of this method corresponds to the returnType parameter.
     * @param parentObject
     *      The XPath will be executed relative to this object.
     * @param xPath
     *      The XPath statement.
     * @param namespaceResolver
     *      A NamespaceResolver containing the prefix/URI pairings from the XPath statement.
     * @param returnType
     *      The return type.
     *
     * @return
     *      An instance of the Java class mapped to the supplied XML type, or null
     *      if no result was found.
     */
    public <T> T createByXPath(Object parentObject, String xPath, NamespaceResolver namespaceResolver, Class<T> returnType) {
        return getXMLContext().createByXPath(parentObject, xPath, namespaceResolver, returnType);
    }

    public ObjectGraph createObjectGraph(Class type) {
        CoreAttributeGroup group = new CoreAttributeGroup(null, type, true);
        return new ObjectGraphImpl(group);
    }

    public ObjectGraph createObjectGraph(String typeName) {
        ClassLoader loader = this.contextInput.classLoader;
        try {
            Class cls = PrivilegedAccessHelper.getClassForName(typeName, true, loader);
            return createObjectGraph(cls);
        } catch (Exception ex) {
            throw ConversionException.couldNotBeConvertedToClass(typeName, Class.class, ex);
        }
    }

    protected JAXBElement createJAXBElementFromXMLRoot(Root xmlRoot, Class declaredType) {
        Object value = xmlRoot.getObject();

        if (value instanceof List) {
            List theList = (List) value;
            for (int i = 0; i < theList.size(); i++) {
                Object next = theList.get(i);
                if (next instanceof Root) {
                    theList.set(i, createJAXBElementFromXMLRoot((Root) next, declaredType));
                }
            }
        } else if (value instanceof WrappedValue) {
            QName qname = new QName(xmlRoot.getNamespaceURI(), xmlRoot.getLocalName());
            return new JAXBElement(qname, ((WrappedValue) value).getDeclaredType(), ((WrappedValue) value).getValue());
        } else if (value instanceof JAXBElement) {
            return (JAXBElement) value;
        } else if (value instanceof ManyValue) {
            value = ((ManyValue) value).getItem();
        }

        QName qname = new QName(xmlRoot.getNamespaceURI(), xmlRoot.getLocalName());

        Map<QName, Class> qNamesToDeclaredClasses = getQNamesToDeclaredClasses();
        if (qNamesToDeclaredClasses != null && qNamesToDeclaredClasses.size() > 0) {
            Class declaredClass = qNamesToDeclaredClasses.get(qname);
            if (declaredClass != null) {
                return createJAXBElement(qname, declaredClass, value);
            }
        }

        Class xmlRootDeclaredType = xmlRoot.getDeclaredType();
        if (xmlRootDeclaredType != null) {
            return createJAXBElement(qname, xmlRootDeclaredType, value);
        }
        return createJAXBElement(qname, declaredType, value);
    }

    protected JAXBElement createJAXBElement(QName qname, Class theClass, Object value) {
        if (theClass == null) {
            return new JAXBElement(qname, Object.class, value);
        }

        if (CoreClassConstants.XML_GREGORIAN_CALENDAR.isAssignableFrom(theClass)) {
            theClass = CoreClassConstants.XML_GREGORIAN_CALENDAR;
        } else if (CoreClassConstants.DURATION.isAssignableFrom(theClass)) {
            theClass = CoreClassConstants.DURATION;
        }

        return new JAXBElement(qname, theClass, value);
    }

    /**
     * Returns true if any Object in this context contains a property annotated with an XmlAttachmentRef
     * annotation.
     * @return
     */
    public boolean hasSwaRef() {
        return contextState.getGenerator().getAnnotationsProcessor().hasSwaRef();
    }

    /**
     * The JAXBContextInput is used to create a JAXBContextState which is responsible for accessing
     * the underlying XMLContext
     */
    public static abstract class JAXBContextInput {

        protected Map properties;
        protected ClassLoader classLoader;

        /**
         * Create a new JAXBContextInput with the specified Map of properties and ClassLoader.
         * @param properties Map of properties.
         * @param classLoader the classLoader to use.  If null then Thread.currentThread().getContextClassLoader() will be used.
         */
        public JAXBContextInput(Map properties, ClassLoader classLoader) {
            this.properties = properties;
            if (null == classLoader) {
                this.classLoader = Thread.currentThread().getContextClassLoader();
            } else {
                this.classLoader = classLoader;
            }
        }

        protected abstract JAXBContextState createContextState() throws javax.xml.bind.JAXBException;

        protected Collection<SessionEventListener> sessionEventListeners() {
            Object eventListenerFromProperties = null;
            if (this.properties != null) {
                eventListenerFromProperties = properties.get(JAXBContextProperties.SESSION_EVENT_LISTENER);
            }

            List<SessionEventListener> eventListeners = null;

            if (null == eventListenerFromProperties) {
                eventListeners = new ArrayList<SessionEventListener>(1);
            } else {
                if (eventListenerFromProperties instanceof SessionEventListener) {
                    eventListeners = new ArrayList<SessionEventListener>(2);
                    eventListeners.add((SessionEventListener) eventListenerFromProperties);
                } else if (eventListenerFromProperties instanceof Collection) {
                    List<SessionEventListener> listeners = (List<SessionEventListener>) eventListenerFromProperties;
                    eventListeners = new ArrayList<SessionEventListener>(listeners.size() + 1);
                    eventListeners.addAll(listeners);
                }
            }

            // disable instantiation policy validation during descriptor initialization
            org.eclipse.persistence.internal.jaxb.SessionEventListener eventListener = new org.eclipse.persistence.internal.jaxb.SessionEventListener();
            eventListener.setShouldValidateInstantiationPolicy(false);
            eventListeners.add(eventListener);
            return eventListeners;
        }

    }

    static class ContextPathInput extends JAXBContextInput {

        private String contextPath;

        ContextPathInput(String contextPath, Map properties, ClassLoader classLoader) {
            super(properties, classLoader);
            this.contextPath = contextPath;
        }

        @Override
        protected JAXBContextState createContextState() throws javax.xml.bind.JAXBException {
            boolean foundMetadata = false;
            List<Class> classes = new ArrayList<Class>();

            // Check properties map for eclipselink-oxm.xml entries
            Map<String, XmlBindings> xmlBindingMap = JAXBContextFactory.getXmlBindingsFromProperties(properties, classLoader);
            foundMetadata = null != xmlBindingMap && !xmlBindingMap.isEmpty();
            classes = getXmlBindingsClassesFromMap(xmlBindingMap, classLoader, classes);

            StringTokenizer tokenizer = new StringTokenizer(contextPath, ":");
            while (tokenizer.hasMoreElements()) {
                String path = tokenizer.nextToken();
                try {
                    Class objectFactory = classLoader.loadClass(path + ".ObjectFactory");
                    if (isJAXB2ObjectFactory(objectFactory, classLoader)) {
                        classes.add(objectFactory);
                        foundMetadata = true;
                    }
                } catch (Exception ex) {
                    // if there's no object factory, don't worry about it. Check for jaxb.index next
                }
                try {
                    // try to load package info just to be safe
                    classLoader.loadClass(path + ".package-info");
                } catch (Exception ex) {
                }
                // Next check for a jaxb.index file in case there's one available
                InputStream jaxbIndex = classLoader.getResourceAsStream(path.replace('.', '/') + "/jaxb.index");
                if (jaxbIndex != null) {
                    foundMetadata = true;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(jaxbIndex));
                    try {
                        String line = reader.readLine();
                        while (line != null) {
                            String className = path + JAXBContextFactory.PKG_SEPARATOR + line.trim();
                            try {
                                classes.add(classLoader.loadClass(className));
                            } catch (Exception ex) {
                                // just ignore for now if the class isn't available.
                            }
                            line = reader.readLine();
                        }
                    } catch (Exception ex) {
                    } finally {
                        try {
                            reader.close();
                        } catch (Exception e) {
                            // ignore
                        }
                    }
                }
            }
            if (foundMetadata) {
                Class[] classArray = new Class[classes.size()];
                for (int i = 0; i < classes.size(); i++) {
                    classArray[i] = classes.get(i);
                }
                return createContextState(classArray, xmlBindingMap);
            }

            Exception sessionLoadingException = null;
            try {
                XMLContext xmlContext = new XMLContext(contextPath, classLoader);
                return new JAXBContextState(xmlContext);
            } catch (Exception exception) {
                sessionLoadingException = exception;
            }
            JAXBException jaxbException = JAXBException.noObjectFactoryOrJaxbIndexInPath(contextPath);
            if (sessionLoadingException != null) {
                jaxbException.setInternalException(sessionLoadingException);
            }
            throw new javax.xml.bind.JAXBException(jaxbException);
        }

        /**
         * This means of creating a JAXBContext is aimed at creating a JAXBContext
         * based on method parameters.  This method is useful when JAXB is used as
         * the binding layer for a Web Service provider.
         */
        private JAXBContextState createContextState(Class[] classesToBeBound, Map<String, XmlBindings> xmlBindings) throws javax.xml.bind.JAXBException {
            JaxbClassLoader loader = PrivilegedAccessHelper.shouldUsePrivilegedAccess()
                    ? AccessController.doPrivileged(new PrivilegedAction<JaxbClassLoader>() {
                        @Override
                        public JaxbClassLoader run() {
                            return new JaxbClassLoader(classLoader, classesToBeBound);
                        }
                    })
                    : new JaxbClassLoader(classLoader, classesToBeBound);
            String defaultTargetNamespace = null;
            AnnotationHelper annotationHelper = null;
            boolean enableXmlAccessorFactory = false;
            if (properties != null) {
                if ((defaultTargetNamespace = (String) properties.get(JAXBContextProperties.DEFAULT_TARGET_NAMESPACE)) == null) {
                    // try looking up the 'old' key
                    defaultTargetNamespace = (String) properties.get(JAXBContextFactory.DEFAULT_TARGET_NAMESPACE_KEY);
                }
                if ((annotationHelper = (AnnotationHelper) properties.get(JAXBContextProperties.ANNOTATION_HELPER)) == null) {
                    // try looking up the 'old' key
                    annotationHelper = (AnnotationHelper) properties.get(JAXBContextFactory.ANNOTATION_HELPER_KEY);
                }
                Boolean xmlAccessorFactorySupport = (Boolean) properties.get(JAXBContextProperties.XML_ACCESSOR_FACTORY_SUPPORT);
                Boolean xmlAccessorFactorySupportRI = (Boolean) properties.get(RI_XML_ACCESSOR_FACTORY_SUPPORT);
                if (Boolean.TRUE.equals(xmlAccessorFactorySupport) || Boolean.TRUE.equals(xmlAccessorFactorySupportRI)) {
                    enableXmlAccessorFactory = true;
                }
            }

            JavaModelImpl jModel;
            if (annotationHelper != null) {
                jModel = new JavaModelImpl(loader, annotationHelper);
            } else {
                jModel = new JavaModelImpl(loader);
            }

            // create Map of package names to metadata complete indicators
            Map<String, Boolean> metadataComplete = new HashMap<String, Boolean>();
            for (String packageName : xmlBindings.keySet()) {
                if (xmlBindings.get(packageName).isXmlMappingMetadataComplete()) {
                    metadataComplete.put(packageName, true);
                }
            }
            if (metadataComplete.size() > 0) {
                jModel.setMetadataCompletePackageMap(metadataComplete);
            }

            jModel.setHasXmlBindings(!xmlBindings.isEmpty());
            JavaModelInputImpl inputImpl = new JavaModelInputImpl(classesToBeBound, jModel);
            if (properties != null) enableFacetsIfPropertySetTrue(inputImpl, properties);
            try {
                Generator generator = new Generator(inputImpl, xmlBindings, loader, defaultTargetNamespace, enableXmlAccessorFactory);
                return createContextState(generator, loader, classesToBeBound, properties);
            } catch (Exception ex) {
                throw new javax.xml.bind.JAXBException(ex.getMessage(), ex);
            }
        }

        private JAXBContextState createContextState(Generator generator, JaxbClassLoader loader, Type[] typesToBeBound, Map properties) throws Exception {
            CoreProject proj = generator.generateProject();
            ConversionManager conversionManager = null;
            if (classLoader != null) {
                conversionManager = new ConversionManager();
                conversionManager.setLoader(loader);
            } else {
                conversionManager = ConversionManager.getDefaultManager();
            }
            proj.convertClassNamesToClasses(conversionManager.getLoader());
            // need to make sure that the java class is set properly on each
            // descriptor when using java classname - req'd for JOT api implementation
            for (Iterator<ClassDescriptor> descriptorIt = proj.getOrderedDescriptors().iterator(); descriptorIt.hasNext();) {
                ClassDescriptor descriptor = descriptorIt.next();
                if (descriptor.getJavaClass() == null) {
                    descriptor.setJavaClass(conversionManager.convertClassNameToClass(descriptor.getJavaClassName()));
                }
            }

            XMLPlatform platform = new SAXPlatform();
            platform.getConversionManager().setLoader(loader);
            XMLContext xmlContext = new XMLContext((Project) proj, loader, sessionEventListeners());

            ((XMLLogin) xmlContext.getSession().getDatasourceLogin()).setEqualNamespaceResolvers(true);

            return new JAXBContextState(xmlContext, generator, typesToBeBound, properties);
        }

        /**
         * Convenience method that returns an array of Classes based on a map given XmlBindings and an
         * array of existing classes. The resulting array will not contain duplicate entries.
         */
        private List<Class> getXmlBindingsClassesFromMap(Map<String, XmlBindings> xmlBindingMap, ClassLoader classLoader, List<Class> existingClasses) {
            List<Class> additionalClasses = existingClasses;
            // for each xmlBindings
            for (Entry<String, XmlBindings> entry : xmlBindingMap.entrySet()) {
                additionalClasses = getXmlBindingsClasses(entry.getValue(), classLoader, additionalClasses);
            }
            return additionalClasses;
        }

        /**
         * Convenience method that returns a list of Classes based on a given XmlBindings and an array
         * of existing classes. The resulting array will not contain duplicate entries.
         */
        private List<Class> getXmlBindingsClasses(XmlBindings xmlBindings, ClassLoader classLoader, List<Class> existingClasses) {
            List<Class> additionalClasses = existingClasses;
            JavaTypes jTypes = xmlBindings.getJavaTypes();
            if (jTypes != null) {
                for (JavaType javaType : jTypes.getJavaType()) {
                    try {
                        Class jClass = classLoader.loadClass(getQualifiedJavaTypeName(javaType.getName(), xmlBindings.getPackageName()));
                        if (!additionalClasses.contains(jClass)) {
                            additionalClasses.add(jClass);
                        }
                    } catch (ClassNotFoundException e) {
                        throw org.eclipse.persistence.exceptions.JAXBException.couldNotLoadClassFromMetadata(javaType.getName());
                    }
                }
            }
            return additionalClasses;
        }

        private boolean isJAXB2ObjectFactory(Class objectFactoryClass, ClassLoader classLoader) {
            try {
                Class xmlRegistry = PrivilegedAccessHelper.getClassForName("javax.xml.bind.annotation.XmlRegistry", false, classLoader);
                if (objectFactoryClass.isAnnotationPresent(xmlRegistry)) {
                    return true;
                }
                return false;
            } catch (Exception ex) {
                return false;
            }
        }

    }

    static class TypeMappingInfoInput extends JAXBContextInput {

        private TypeMappingInfo[] typeMappingInfo;

        TypeMappingInfoInput(TypeMappingInfo[] typeMappingInfo, Map properties, ClassLoader classLoader) {
            super(properties, classLoader);
            this.typeMappingInfo = Arrays.copyOf(typeMappingInfo, typeMappingInfo.length);

            Arrays.sort(this.typeMappingInfo, new Comparator<TypeMappingInfo>() {
                @Override
                public int compare(TypeMappingInfo javaClass1, TypeMappingInfo javaClass2) {
                    String sourceName = getNameForType(javaClass1.getType());
                    String targetName = getNameForType(javaClass2.getType());
                    if(sourceName == null ||  targetName == null){
                        return -1;
                    }

                    return sourceName.compareTo(targetName);
                }

                private String getNameForType(Type type) {
                    if (type instanceof Class) {
                        return ((Class)type).getCanonicalName();
                    } else if (type instanceof GenericArrayType) {
                        Class genericTypeClass = (Class) ((GenericArrayType) type).getGenericComponentType();
                        return genericTypeClass.getCanonicalName();
                    } else {
                        // assume parameterized type
                        ParameterizedType pType = (ParameterizedType) type;
                        return ((Class)pType.getRawType()).getCanonicalName();
                    }
                }
            });
        }

        @Override
        protected JAXBContextState createContextState() throws javax.xml.bind.JAXBException {
            // Check properties map for eclipselink-oxm.xml entries
            Map<String, XmlBindings> xmlBindings = JAXBContextFactory.getXmlBindingsFromProperties(properties, classLoader);
            String defaultTargetNamespace = null;
            AnnotationHelper annotationHelper = null;
            boolean enableXmlAccessorFactory = false;
            if (properties != null) {
                if ((defaultTargetNamespace = (String) properties.get(JAXBContextProperties.DEFAULT_TARGET_NAMESPACE)) == null) {
                    // try looking up the 'old' key
                    defaultTargetNamespace = (String) properties.get(JAXBContextFactory.DEFAULT_TARGET_NAMESPACE_KEY);
                }
                if ((annotationHelper = (AnnotationHelper) properties.get(JAXBContextProperties.ANNOTATION_HELPER)) == null) {
                    // try looking up the 'old' key
                    annotationHelper = (AnnotationHelper) properties.get(JAXBContextFactory.ANNOTATION_HELPER_KEY);
                }
                Boolean xmlAccessorFactorySupport = (Boolean) properties.get(JAXBContextProperties.XML_ACCESSOR_FACTORY_SUPPORT);
                Boolean xmlAccessorFactorySupportRI = (Boolean) properties.get(RI_XML_ACCESSOR_FACTORY_SUPPORT);
                if (Boolean.TRUE.equals(xmlAccessorFactorySupport) || Boolean.TRUE.equals(xmlAccessorFactorySupportRI)) {
                    enableXmlAccessorFactory = true;
                }
            }
            TypeMappingInfo[] typesToBeBound = typeMappingInfo;

            for (Entry<String, XmlBindings> entry : xmlBindings.entrySet()) {
                typesToBeBound = getXmlBindingsClasses(entry.getValue(), classLoader, typesToBeBound);
            }

            final TypeMappingInfo[] types = typesToBeBound;

            JaxbClassLoader loader = PrivilegedAccessHelper.shouldUsePrivilegedAccess()
                    ? AccessController.doPrivileged(new PrivilegedAction<JaxbClassLoader>() {
                        @Override
                        public JaxbClassLoader run() {
                            return new JaxbClassLoader(classLoader, types);
                        }
                    })
                    : new JaxbClassLoader(classLoader, types);

            JavaModelImpl jModel;
            if (annotationHelper != null) {
                jModel = new JavaModelImpl(loader, annotationHelper);
            } else {
                jModel = new JavaModelImpl(loader);
            }

            if (xmlBindings != null) {
                jModel.setHasXmlBindings(!xmlBindings.isEmpty());
                // create Map of package names to metadata complete indicators
                Map<String, Boolean> metadataComplete = new HashMap<String, Boolean>();
                for (String packageName : xmlBindings.keySet()) {
                    if (xmlBindings.get(packageName).isXmlMappingMetadataComplete()) {
                        metadataComplete.put(packageName, true);
                    }
                }

                if (metadataComplete.size() > 0) {
                    jModel.setMetadataCompletePackageMap(metadataComplete);
                }
            }

            JavaModelInputImpl inputImpl = new JavaModelInputImpl(typesToBeBound, jModel);
            if (properties != null) enableFacetsIfPropertySetTrue(inputImpl, properties);
            try {
                Generator generator = new Generator(inputImpl, typesToBeBound, inputImpl.getJavaClasses(), null, xmlBindings, classLoader, defaultTargetNamespace, enableXmlAccessorFactory);
                JAXBContextState contextState = createContextState(generator, loader, typesToBeBound, properties);
                return contextState;
            } catch (Exception ex) {
                throw new javax.xml.bind.JAXBException(ex.getMessage(), ex);
            }
        }

        private JAXBContextState createContextState(Generator generator, JaxbClassLoader loader, TypeMappingInfo[] typesToBeBound, Map properties) throws Exception {
            CoreProject proj = generator.generateProject();
            ConversionManager conversionManager = null;
            if (classLoader != null) {
                conversionManager = new ConversionManager();
                conversionManager.setLoader(loader);
            } else {
                conversionManager = ConversionManager.getDefaultManager();
            }
            proj.convertClassNamesToClasses(conversionManager.getLoader());
            // need to make sure that the java class is set properly on each
            // descriptor when using java classname - req'd for JOT api implementation
            for (ClassDescriptor descriptor : (Iterable<ClassDescriptor>) proj.getOrderedDescriptors()) {
                if (descriptor.getJavaClass() == null) {
                    descriptor.setJavaClass(conversionManager.convertClassNameToClass(descriptor.getJavaClassName()));
                }
            }

            XMLPlatform platform = new SAXPlatform();
            platform.getConversionManager().setLoader(loader);
            XMLContext xmlContext = new XMLContext((Project) proj, loader, sessionEventListeners());

            ((XMLLogin) xmlContext.getSession().getDatasourceLogin()).setEqualNamespaceResolvers(true);

            JAXBContextState contextState = new JAXBContextState(xmlContext, generator, typesToBeBound, properties);

            for (TypeMappingInfo typeMappingInfo : typesToBeBound) {
                Type classToLookup = typeMappingInfo.getType();
                if (contextState.getTypeMappingInfoToGeneratedType() != null && contextState.getTypeMappingInfoToGeneratedType().size() > 0) {
                    Class generatedClass = contextState.getTypeMappingInfoToGeneratedType().get(typeMappingInfo);
                    if (generatedClass != null) {
                        classToLookup = generatedClass;
                    }
                }
                if (classToLookup != null && classToLookup.getClass() == Class.class) {
                    Descriptor xmlDescriptor = (Descriptor) proj.getDescriptor((Class) classToLookup);
                    typeMappingInfo.setXmlDescriptor(xmlDescriptor);
                }
            }

            return contextState;
        }

        /**
         * Convenience method that returns an array of Types based on a given XmlBindings. The resulting
         * array will not contain duplicate entries.
         */
        private static TypeMappingInfo[] getXmlBindingsClasses(XmlBindings xmlBindings, ClassLoader classLoader, TypeMappingInfo[] existingTypes) {
            JavaTypes jTypes = xmlBindings.getJavaTypes();
            if (jTypes != null) {
                List<Class> existingClasses = new ArrayList<Class>(existingTypes.length);
                for (TypeMappingInfo typeMappingInfo : existingTypes) {
                    Type type = typeMappingInfo.getType();
                    if (type == null) {
                        throw org.eclipse.persistence.exceptions.JAXBException.nullTypeOnTypeMappingInfo(typeMappingInfo.getXmlTagName());
                    }
                    // ignore ParameterizedTypes
                    if (type instanceof Class) {
                        Class cls = (Class) type;
                        existingClasses.add(cls);
                    }
                }

                List<TypeMappingInfo> additionalTypeMappingInfos = new ArrayList<TypeMappingInfo>(jTypes.getJavaType().size());

                for (JavaType javaType : jTypes.getJavaType()) {
                    try {
                        Class nextClass = classLoader.loadClass(getQualifiedJavaTypeName(javaType.getName(), xmlBindings.getPackageName()));
                        if (!(existingClasses.contains(nextClass))) {
                            TypeMappingInfo typeMappingInfo = new TypeMappingInfo();
                            typeMappingInfo.setType(nextClass);
                            additionalTypeMappingInfos.add(typeMappingInfo);
                            existingClasses.add(nextClass);
                        }
                    } catch (ClassNotFoundException e) {
                        throw org.eclipse.persistence.exceptions.JAXBException.couldNotLoadClassFromMetadata(javaType.getName());
                    }
                }

                TypeMappingInfo[] allTypeMappingInfos = new TypeMappingInfo[existingTypes.length + additionalTypeMappingInfos.size()];
                System.arraycopy(existingTypes, 0, allTypeMappingInfos, 0, existingTypes.length);
                Object[] additionalTypes = additionalTypeMappingInfos.toArray();
                System.arraycopy(additionalTypes, 0, allTypeMappingInfos, existingTypes.length, additionalTypes.length);
                return allTypeMappingInfos;
            }
            return existingTypes;
        }
    }

    protected static class JAXBContextState {

        private XMLContext xmlContext;
        private org.eclipse.persistence.jaxb.compiler.Generator generator;
        private Map<QName, Class> qNameToGeneratedClasses;
        private Map<String, Class> classToGeneratedClasses;
        private Map<QName, Class> qNamesToDeclaredClasses;
        private Map<Type, QName> typeToSchemaType;
        private TypeMappingInfo[] boundTypes;
        private Map<TypeMappingInfo, Class> typeMappingInfoToGeneratedType;
        private Map<Type, TypeMappingInfo> typeToTypeMappingInfo;
        private Map<TypeMappingInfo, JAXBContext.RootLevelXmlAdapter> typeMappingInfoToJavaTypeAdapters;
        private Map properties;

        protected JAXBContextState() {
        }

        protected JAXBContextState(XMLContext context) {
            xmlContext = context;
            updateNamespaces();
        }

        protected JAXBContextState(XMLContext context, Generator generator, Type[] boundTypes, Map properties) {
            this(context);
            this.generator = generator;
            this.qNameToGeneratedClasses = generator.getMappingsGenerator().getQNamesToGeneratedClasses();
            this.classToGeneratedClasses = generator.getMappingsGenerator().getClassToGeneratedClasses();
            this.qNamesToDeclaredClasses = generator.getMappingsGenerator().getQNamesToDeclaredClasses();
            this.boundTypes = new TypeMappingInfo[boundTypes.length];
            for (int i = 0; i < boundTypes.length; i++) {
                TypeMappingInfo newTypeInfo = new TypeMappingInfo();
                newTypeInfo.setType(boundTypes[i]);
                this.boundTypes[i] = newTypeInfo;
            }
            if (properties != null) {
                this.properties = new HashMap(properties);
            }
        }

        protected JAXBContextState(XMLContext context, Generator generator, TypeMappingInfo[] boundTypes, Map properties) {
            this(context);
            this.generator = generator;
            this.qNameToGeneratedClasses = generator.getMappingsGenerator().getQNamesToGeneratedClasses();
            this.classToGeneratedClasses = generator.getMappingsGenerator().getClassToGeneratedClasses();
            this.qNamesToDeclaredClasses = generator.getMappingsGenerator().getQNamesToDeclaredClasses();
            this.typeMappingInfoToGeneratedType = generator.getAnnotationsProcessor().getTypeMappingInfosToGeneratedClasses();
            this.setTypeMappingInfoToJavaTypeAdapaters(createAdaptersForAdapterClasses(generator.getAnnotationsProcessor().getTypeMappingInfoToAdapterClasses()));
            this.boundTypes = boundTypes;
            if (properties != null) {
                this.properties = new HashMap(properties);
            }
        }

        private Map<TypeMappingInfo, JAXBContext.RootLevelXmlAdapter> createAdaptersForAdapterClasses(Map<TypeMappingInfo, Class> typeMappingInfoToAdapterClasses) {
            Map<TypeMappingInfo, JAXBContext.RootLevelXmlAdapter> typeMappingInfoToAdapters = new HashMap<TypeMappingInfo, JAXBContext.RootLevelXmlAdapter>();
            for (Entry<TypeMappingInfo, Class> entry : typeMappingInfoToAdapterClasses.entrySet()) {
                Class adapterClass = entry.getValue();
                if (adapterClass != null) {
                    try {
                        XmlAdapter adapter = (XmlAdapter) adapterClass.newInstance();
                        Class boundType = getBoundTypeForXmlAdapterClass(adapterClass);
                        RootLevelXmlAdapter rootLevelXmlAdapter = new RootLevelXmlAdapter(adapter, boundType);

                        typeMappingInfoToAdapters.put(entry.getKey(), rootLevelXmlAdapter);
                    } catch (Exception ex) {
                    }
                }
            }
            return typeMappingInfoToAdapters;
        }

        private Class getBoundTypeForXmlAdapterClass(Class adapterClass) {
            Class boundType = Object.class;

            for (Method method : PrivilegedAccessHelper.getDeclaredMethods(adapterClass)) {
                if (method.getName().equals("marshal")) {
                    Class returnType = PrivilegedAccessHelper.getMethodReturnType(method);
                    if (!returnType.getName().equals(boundType.getName())) {
                        boundType = returnType;
                        break;
                    }
                }
            }
            return boundType;
        }

        private void updateNamespaces() {

            Collection descriptors = xmlContext.getSession().getDescriptors().values();

            for (Object descriptor : descriptors) {
                Descriptor desc = (Descriptor) descriptor;
                processXMLDescriptor(new ArrayList<Descriptor>(), desc, desc.getNonNullNamespaceResolver());
            }

        }

        private void processRefClasses(List processed, Set refClasses, org.eclipse.persistence.internal.oxm.NamespaceResolver nr) {
            if (refClasses != null) {
                for (Object refClass : refClasses) {
                    Class nextClass = (Class) refClass;
                    Descriptor desc = (Descriptor) xmlContext.getSession().getProject().getDescriptor(nextClass);
                    processXMLDescriptor(processed, desc, nr);
                }
            }
        }

        private void processXMLDescriptor(List<Descriptor> processed, Descriptor desc, org.eclipse.persistence.internal.oxm.NamespaceResolver nr) {
            if (desc == null || processed.contains(desc)) {
                return;
            }
            processed.add(desc);

            Vector mappings = desc.getMappings();

            for (Object mapping : mappings) {
                DatabaseMapping nextMapping = (DatabaseMapping) mapping;
                Vector fields = nextMapping.getFields();
                updateResolverForFields(fields, nr);
                Descriptor refDesc = (Descriptor) nextMapping.getReferenceDescriptor();
                if (refDesc != null && !processed.contains(refDesc)) {
                    processXMLDescriptor(processed, refDesc, nr);
                }

                if (nextMapping instanceof ChoiceObjectMapping) {
                    Set refClasses = ((ChoiceObjectMapping) nextMapping).getClassToFieldMappings().keySet();
                    processRefClasses(processed, refClasses, nr);
                } else if (nextMapping instanceof ChoiceCollectionMapping) {
                    Set refClasses = ((ChoiceCollectionMapping) nextMapping).getClassToFieldMappings().keySet();
                    processRefClasses(processed, refClasses, nr);
                }
            }
        }

        private void updateResolverForFields(Collection fields, org.eclipse.persistence.internal.oxm.NamespaceResolver nr) {
            for (Object field1 : fields) {
                Field field = (XMLField) field1;
                XPathFragment currentFragment = field.getXPathFragment();

                while (currentFragment != null) {
                    String uri = currentFragment.getNamespaceURI();
                    if (uri != null) {
                        String prefix = currentFragment.getPrefix();
                        if (prefix == null || prefix.isEmpty()) {
                            if (null == nr.getDefaultNamespaceURI()) {
                                nr.setDefaultNamespaceURI(uri);
                            }
                        } else {
                            if (null == nr.resolveNamespacePrefix(prefix)) {
                                nr.put(prefix, uri);
                            }
                        }
                    }
                    currentFragment = currentFragment.getNextFragment();
                }
            }
        }

        private Map<String, Class> getClassToGeneratedClasses() {
            return classToGeneratedClasses;
        }

        private Generator getGenerator() {
            return generator;
        }

        private XMLContext getXMLContext() {
            return this.xmlContext;
        }

        private Map<java.lang.reflect.Type, QName> getTypeToSchemaType() {
            if (typeToSchemaType == null) {
                initTypeToSchemaType();
            }
            return typeToSchemaType;
        }

        private Map<TypeMappingInfo, Class> getTypeMappingInfoToGeneratedType() {
            return this.typeMappingInfoToGeneratedType;
        }

        private Map<TypeMappingInfo, RootLevelXmlAdapter> getTypeMappingInfoToJavaTypeAdapters() {
            return this.typeMappingInfoToJavaTypeAdapters;
        }

        private Map<Type, TypeMappingInfo> getTypeToTypeMappingInfo() {
            return this.typeToTypeMappingInfo;
        }

        private Map<TypeMappingInfo, QName> getTypeMappingInfoToSchemaType() {
            if (typeToTypeMappingInfo != null && typeToTypeMappingInfo.size() > 0) {
                return new HashMap<TypeMappingInfo, QName>();
            }
            return generator.getAnnotationsProcessor().getTypeMappingInfosToSchemaTypes();
        }

        private Map<QName, Class> getQNamesToDeclaredClasses() {
            return qNamesToDeclaredClasses;
        }

        /**
         * INTERNAL:
         * Get the QName which the given Type corresponds to.
         * Valid types should be all the boundTypes used to create the JAXBContext.
         * If the JAXBContext was not created with the construction that takes a Type[] then
         * this will be return null.
         */
        private QName getSchemaTypeForTypeMappingInfo(Type type) {
            QName name = null;
            //Check for annotation overrides
            if (type instanceof Class) {
                name = generator.getAnnotationsProcessor().getUserDefinedSchemaTypes().get(((Class) type).getName());
                if (name == null) {
                    Class theClass = (Class) type;
                    //Change default for byte[] to Base64 (JAXB 2.0 default)
                    if (type == CoreClassConstants.ABYTE || type == CoreClassConstants.APBYTE || type == Image.class || type == Source.class || theClass.getCanonicalName().equals("javax.activation.DataHandler")) {
                        name = Constants.BASE_64_BINARY_QNAME;
                    } else if (type == CoreClassConstants.OBJECT) {
                        name = Constants.ANY_TYPE_QNAME;
                    } else if (type == CoreClassConstants.XML_GREGORIAN_CALENDAR) {
                        name = Constants.ANY_SIMPLE_TYPE_QNAME;
                    } else {
                        name = (QName) XMLConversionManager.getDefaultJavaTypes().get(type);
                    }
                }
            }
            return name;
        }

        private Map<QName, Class> getQNameToGeneratedClasses() {
            return qNameToGeneratedClasses;
        }

        private void initTypeToSchemaType() {
            this.typeToSchemaType = new HashMap<Type, QName>();

            if (typeToTypeMappingInfo == null || typeToTypeMappingInfo.size() == 0) {
                return;
            }

            //Add schema types generated for mapped domain classes
            for (Object o : xmlContext.getSession().getProject().getOrderedDescriptors()) {
                Descriptor next = (Descriptor) o;
                Class javaClass = next.getJavaClass();

                if (next.getSchemaReference() != null) {
                    QName schemaType = next.getSchemaReference().getSchemaContextAsQName(next.getNamespaceResolver());
                    Type type = null;
                    if (generator != null) {

                        type = generator.getAnnotationsProcessor().getGeneratedClassesToCollectionClasses().get(javaClass);

                        if (type == null) {
                            JavaClass arrayClass = generator.getAnnotationsProcessor().getGeneratedClassesToArrayClasses().get(javaClass);
                            if (arrayClass != null) {
                                String arrayClassName = arrayClass.getName();
                                try {
                                    type = PrivilegedAccessHelper.getClassForName(arrayClassName);
                                } catch (Exception ex) {
                                }
                            }

                            if (type == null && getTypeMappingInfoToGeneratedType() != null) {
                                for (Entry<TypeMappingInfo, Class> entry : getTypeMappingInfoToGeneratedType().entrySet()) {
                                    if (entry.getValue().equals(javaClass)) {
                                        type = entry.getKey().getType();
                                        break;
                                    }
                                }
                            }
                        }
                        if (type == null) {
                            type = javaClass;
                        }

                    } else {
                        type = javaClass;
                    }
                    this.typeToSchemaType.put(type, schemaType);
                }
            }

            //Add any types that we didn't generate descriptors for (built in types)
            if (boundTypes != null) {
                for (TypeMappingInfo next : this.boundTypes) {
                    Type nextType = next.getType();
                    if (this.typeToSchemaType.get(nextType) == null) {
                        QName name = getSchemaTypeForTypeMappingInfo(nextType);
                        if (name != null) {
                            this.typeToSchemaType.put(nextType, name);
                        }
                    }
                }
            }
        }

        private void setClassToGeneratedClasses(HashMap<String, Class> classToClass) {
            this.classToGeneratedClasses = classToClass;
        }

        private void setTypeToTypeMappingInfo(Map<Type, TypeMappingInfo> typeToMappingInfo) {
            this.typeToTypeMappingInfo = typeToMappingInfo;
            if (this.generator != null) {
                this.generator.setTypeToTypeMappingInfo(typeToMappingInfo);
            }
        }

        private void setTypeMappingInfoToJavaTypeAdapaters(Map<TypeMappingInfo, JAXBContext.RootLevelXmlAdapter> typeMappingInfoToAdapters) {
            this.typeMappingInfoToJavaTypeAdapters = typeMappingInfoToAdapters;
        }

        private void setQNamesToDeclaredClasses(HashMap<QName, Class> nameToDeclaredClasses) {
            qNamesToDeclaredClasses = nameToDeclaredClasses;
        }

        private void setQNameToGeneratedClasses(Map<QName, Class> qNameToClass) {
            this.qNameToGeneratedClasses = qNameToClass;
        }

        public void setXMLContext(XMLContext xmlContext) {
            this.xmlContext = xmlContext;
        }

        public JAXBMarshaller createMarshaller(JAXBContext jaxbContext) throws javax.xml.bind.JAXBException {
            // create a JAXBIntrospector and set it on the marshaller
            JAXBMarshaller marshaller = new JAXBMarshaller(xmlContext.createMarshaller(), jaxbContext);
            if (generator != null && generator.hasMarshalCallbacks()) {
                // initialize each callback in the map
                ClassLoader classLoader = getXMLContext().getSession(0).getDatasourcePlatform().getConversionManager().getLoader();
                for (Object o : generator.getMarshalCallbacks().keySet()) {
                    MarshalCallback cb = (MarshalCallback) generator.getMarshalCallbacks().get(o);
                    cb.initialize(classLoader);
                }
                marshaller.setMarshalCallbacks(generator.getMarshalCallbacks());
            }
            if (properties != null) {
                setPropertyOnMarshaller(JAXBContextProperties.MEDIA_TYPE, marshaller);
                setPropertyOnMarshaller(JAXBContextProperties.JSON_ATTRIBUTE_PREFIX, marshaller);
                setPropertyOnMarshaller(JAXBContextProperties.NAMESPACE_PREFIX_MAPPER, marshaller);
                setPropertyOnMarshaller(JAXBContextProperties.JSON_INCLUDE_ROOT, marshaller);
                setPropertyOnMarshaller(JAXBContextProperties.JSON_VALUE_WRAPPER, marshaller);
                setPropertyOnMarshaller(JAXBContextProperties.JSON_NAMESPACE_SEPARATOR, marshaller);
                setPropertyOnMarshaller(JAXBContextProperties.OBJECT_GRAPH, marshaller);
                setPropertyOnMarshaller(JAXBContextProperties.JSON_WRAPPER_AS_ARRAY_NAME, marshaller);
                setPropertyOnMarshaller(JAXBContextProperties.BEAN_VALIDATION_MODE, marshaller);
                setPropertyOnMarshaller(JAXBContextProperties.BEAN_VALIDATION_FACTORY, marshaller);
                setPropertyOnMarshaller(JAXBContextProperties.BEAN_VALIDATION_GROUPS, marshaller);
                setPropertyOnMarshaller(JAXBContextProperties.BEAN_VALIDATION_NO_OPTIMISATION, marshaller);
                setPropertyOnMarshaller(JAXBContextProperties.JSON_TYPE_COMPATIBILITY, marshaller);
                setPropertyOnMarshaller(JAXBContextProperties.JSON_USE_XSD_TYPES_WITH_PREFIX, marshaller);
            }

            return marshaller;
        }

        public JAXBUnmarshaller createUnmarshaller(JAXBContext jaxbContext) throws javax.xml.bind.JAXBException {

            JAXBUnmarshaller unmarshaller = new JAXBUnmarshaller(xmlContext.createUnmarshaller(PARSER_FEATURES), jaxbContext);
            if (generator != null && generator.hasUnmarshalCallbacks()) {
                // initialize each callback in the map
                ClassLoader classLoader = getXMLContext().getSession(0).getDatasourcePlatform().getConversionManager().getLoader();
                for (Object o : generator.getUnmarshalCallbacks().keySet()) {
                    UnmarshalCallback cb = (UnmarshalCallback) generator.getUnmarshalCallbacks().get(o);
                    cb.initialize(classLoader);
                }
                unmarshaller.setUnmarshalCallbacks(generator.getUnmarshalCallbacks());
            }
            if (properties != null) {
                setPropertyOnUnmarshaller(JAXBContextProperties.MEDIA_TYPE, unmarshaller);
                setPropertyOnUnmarshaller(JAXBContextProperties.JSON_ATTRIBUTE_PREFIX, unmarshaller);
                setPropertyOnUnmarshaller(JAXBContextProperties.NAMESPACE_PREFIX_MAPPER, unmarshaller);
                setPropertyOnUnmarshaller(JAXBContextProperties.JSON_INCLUDE_ROOT, unmarshaller);
                setPropertyOnUnmarshaller(JAXBContextProperties.JSON_VALUE_WRAPPER, unmarshaller);
                setPropertyOnUnmarshaller(JAXBContextProperties.JSON_NAMESPACE_SEPARATOR, unmarshaller);
                setPropertyOnUnmarshaller(JAXBContextProperties.OBJECT_GRAPH, unmarshaller);
                setPropertyOnUnmarshaller(JAXBContextProperties.JSON_WRAPPER_AS_ARRAY_NAME, unmarshaller);
                setPropertyOnUnmarshaller(JAXBContextProperties.UNMARSHALLING_CASE_INSENSITIVE, unmarshaller);
                setPropertyOnUnmarshaller(JAXBContextProperties.BEAN_VALIDATION_MODE, unmarshaller);
                setPropertyOnUnmarshaller(JAXBContextProperties.BEAN_VALIDATION_FACTORY, unmarshaller);
                setPropertyOnUnmarshaller(JAXBContextProperties.BEAN_VALIDATION_GROUPS, unmarshaller);
                setPropertyOnUnmarshaller(JAXBContextProperties.BEAN_VALIDATION_NO_OPTIMISATION, unmarshaller);
                setPropertyOnUnmarshaller(JAXBContextProperties.JSON_TYPE_COMPATIBILITY, unmarshaller);
                setPropertyOnUnmarshaller(JAXBContextProperties.JSON_USE_XSD_TYPES_WITH_PREFIX, unmarshaller);
            }
            return unmarshaller;
        }

        public JAXBBinder createBinder(JAXBContext context) {
        XMLMarshaller marshaller = null;
        XMLUnmarshaller unmarshaller = null;
        try {
            marshaller = createMarshaller(context).getXMLMarshaller();
            unmarshaller = createUnmarshaller(context).getXMLUnmarshaller();
        } catch (javax.xml.bind.JAXBException e) {
            // log something
            marshaller = context.getXMLContext().createMarshaller();
            unmarshaller = context.getXMLContext().createUnmarshaller();
        }

        return new JAXBBinder(context, marshaller, unmarshaller);
        }

        private void setPropertyOnMarshaller(String propertyName, JAXBMarshaller marshaller) throws PropertyException {
            Object propertyValue = properties.get(propertyName);
            if (propertyValue != null) {
                marshaller.setProperty(propertyName, propertyValue);
            }
        }

        private void setPropertyOnUnmarshaller(String propertyName, JAXBUnmarshaller unmarshaller) throws PropertyException {
            Object propertyValue = properties.get(propertyName);
            if (propertyValue != null) {
                unmarshaller.setProperty(propertyName, propertyValue);
            }
        }

    }

    private static void enableFacetsIfPropertySetTrue(JavaModelInputImpl inputImpl, Map properties) {
        Object propertyValue = properties.get(JAXBContextProperties.BEAN_VALIDATION_FACETS);
        if (propertyValue != null) inputImpl.setFacets((Boolean) propertyValue);
    }
}
