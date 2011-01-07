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

import java.awt.Image;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.jaxb.JAXBSchemaOutputResolver;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.oxm.schema.SchemaModelGenerator;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jaxb.compiler.Generator;
import org.eclipse.persistence.jaxb.compiler.MarshalCallback;
import org.eclipse.persistence.jaxb.compiler.UnmarshalCallback;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.sessions.Session;

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
 *
 * @author mmacivor
 */

public class JAXBContext extends javax.xml.bind.JAXBContext {

    private static final Map<String, Boolean> PARSER_FEATURES = new HashMap<String, Boolean>(2);
    static {
        PARSER_FEATURES.put("http://apache.org/xml/features/validation/schema/normalized-value", false);
        PARSER_FEATURES.put("http://apache.org/xml/features/validation/schema/element-default", false);
    }

    protected XMLContext xmlContext;
    private org.eclipse.persistence.jaxb.compiler.Generator generator;
    private HashMap<QName, Class> qNameToGeneratedClasses;
    private HashMap<String, Class> classToGeneratedClasses;
    private HashMap<QName, Class> qNamesToDeclaredClasses;
    private HashMap<Type, QName> typeToSchemaType;
    private TypeMappingInfo[] boundTypes;
    private Map<TypeMappingInfo, Class> typeMappingInfoToGeneratedType;
    private Map<Type, TypeMappingInfo> typeToTypeMappingInfo;
    private Map<TypeMappingInfo, JAXBContext.RootLevelXmlAdapter> typeMappingInfoToJavaTypeAdapters;

    protected JAXBContext() {
        super();
    }

    /**
     * Create a JAXBContext for a given XMLContext.  The XMLContext contains the 
     * metadata about the Object to XML mappings.
     */
    public JAXBContext(XMLContext context) {
        super();
        xmlContext = context;
    }

    /**
     * Create a JAXBContext. The XMLContext contains the metadata about the 
     * Object to XML mappings.
     */
    public JAXBContext(XMLContext context, Generator generator, Type[] boundTypes) {

        this(context);
        this.generator = generator;
        this.qNameToGeneratedClasses = generator.getMappingsGenerator().getQNamesToGeneratedClasses();
        this.classToGeneratedClasses = generator.getMappingsGenerator().getClassToGeneratedClasses();
        this.qNamesToDeclaredClasses = generator.getMappingsGenerator().getQNamesToDeclaredClasses();
        this.boundTypes = new TypeMappingInfo[boundTypes.length];
        for(int i =0; i< boundTypes.length; i++){
        	TypeMappingInfo newTypeInfo = new TypeMappingInfo();
        	newTypeInfo.setType(boundTypes[i]);
        	this.boundTypes[i] = newTypeInfo;
        }
    }
    

    /**
     * Create a JAXBContext.  The XMLContext contains the metadata about the 
     * Object to XML mappings.
     */
    public JAXBContext(XMLContext context, Generator generator, TypeMappingInfo[] boundTypes) {

        this(context);
        this.generator = generator;
        this.qNameToGeneratedClasses = generator.getMappingsGenerator().getQNamesToGeneratedClasses();
        this.classToGeneratedClasses = generator.getMappingsGenerator().getClassToGeneratedClasses();
        this.qNamesToDeclaredClasses = generator.getMappingsGenerator().getQNamesToDeclaredClasses();
        this.typeMappingInfoToGeneratedType = generator.getAnnotationsProcessor().getTypeMappingInfoToGeneratedClasses();
        this.setTypeMappingInfoToJavaTypeAdapaters(createAdaptersForAdapterClasses(generator.getAnnotationsProcessor().getTypeMappingInfoToAdapterClasses()));
        this.boundTypes = boundTypes;
    }

    /**
     * Return the XMLContext associated with this JAXBContext. 
     */
    public XMLContext getXMLContext() {
        return this.xmlContext;
    }
        
    /**
     * Generate a Schema for this JAXBContext
     *  
     * @param outputResolver Class that decides where the schema file (of the given namespace URI) will be written
     */
    public void generateSchema(SchemaOutputResolver outputResolver) {
        generateSchema(outputResolver, null);
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
        if (generator == null) {
            List<XMLDescriptor> descriptorsToProcess = new ArrayList<XMLDescriptor>();
            List<Session> sessions = xmlContext.getSessions();
            for (Session session : sessions) {
                List<XMLDescriptor> descriptors = (List<XMLDescriptor>)(List)session.getProject().getOrderedDescriptors();
                for (XMLDescriptor xDesc : descriptors) {
                    descriptorsToProcess.add(xDesc);
                }
            }
            SchemaModelGenerator smGen = new SchemaModelGenerator();
            smGen.generateSchemas(descriptorsToProcess, null, new JAXBSchemaOutputResolver(outputResolver), additonalGlobalElements);
        } else {
            generator.generateSchemaFiles(outputResolver, additonalGlobalElements);
        }
    }

    /** 
     * Create a JAXBMarshaller.  The JAXBMarshaller is used to convert Java objects
     * to XML.
     */    
    public JAXBMarshaller createMarshaller() {
        // create a JAXBIntrospector and set it on the marshaller
        JAXBMarshaller marshaller = new JAXBMarshaller(xmlContext.createMarshaller(), new JAXBIntrospector(xmlContext));
        if (generator != null && generator.hasMarshalCallbacks()) {
            // initialize each callback in the map
            for (Iterator callIt = generator.getMarshalCallbacks().keySet().iterator(); callIt.hasNext(); ) {
                MarshalCallback cb = (MarshalCallback) generator.getMarshalCallbacks().get(callIt.next());
                cb.initialize(generator.getClass().getClassLoader());
            }
            marshaller.setMarshalCallbacks(generator.getMarshalCallbacks());
        }
        marshaller.setQNameToGeneratedClasses(this.qNameToGeneratedClasses);
        //marshaller.setClassToGeneratedClasses(this.classToGeneratedClasses);
        marshaller.setJaxbContext(this);
        return marshaller;
    }

    /** 
     * Create a JAXBUnmarshaller.  The JAXBUnmarshaller is used to convert XML into
     * Java objects.    
     */    
    public JAXBUnmarshaller createUnmarshaller() {
        JAXBUnmarshaller unmarshaller = new JAXBUnmarshaller(xmlContext.createUnmarshaller(PARSER_FEATURES));
        if (generator != null && generator.hasUnmarshalCallbacks()) {
            // initialize each callback in the map
            for (Iterator callIt = generator.getUnmarshalCallbacks().keySet().iterator(); callIt.hasNext(); ) {
                UnmarshalCallback cb = (UnmarshalCallback) generator.getUnmarshalCallbacks().get(callIt.next());
                cb.initialize(generator.getClass().getClassLoader());
            }
            unmarshaller.setUnmarshalCallbacks(generator.getUnmarshalCallbacks());
        }
        unmarshaller.setJaxbContext(this);
        return unmarshaller;
    }

    /** 
     * Create a JAXBValidator.  The JAXBValidator is used to validate Java objects against
     * an XSD.   
     */ 
    public JAXBValidator createValidator() {
        return new JAXBValidator(xmlContext.createValidator());
    }

    /** 
     * Create a JAXBBinder.  The JAXBBinder is used to preserve unmapped XML Data.     
     */ 
    public JAXBBinder createBinder() {
        return new JAXBBinder(this.xmlContext);
    }     

    /** 
     * Create a JAXBBinder.  The JAXBBinder is used to preserve unmapped XML Data.
     *
     * @param nodeClass The DOM Node class to use     
     */ 
    public <T> JAXBBinder createBinder(Class<T> nodeClass) {
        if (nodeClass.getName().equals("org.w3c.dom.Node")) {
            return new JAXBBinder(this.xmlContext);
        } else {
            throw new UnsupportedOperationException(JAXBException.unsupportedNodeClass(nodeClass.getName()));
        }
    }

    /**
     * Creates a JAXBIntrospector object.  The JAXBIntrospector allows the user to 
     * access certain pieces of metadata about an instance of a JAXB bound class.
     */
    public JAXBIntrospector createJAXBIntrospector() {
        return new JAXBIntrospector(xmlContext);
    }

    /**
     * INTERNAL:
     * Set the map containing which QName corresponds to which generated class.
     */
    public void setQNameToGeneratedClasses(HashMap<QName, Class> qNameToClass) {
        this.qNameToGeneratedClasses = qNameToClass;
    }

    /**
     * INTERNAL:
     * Get the map containing which Class (by name) corresponds to which generated class.
     */
    public HashMap<String, Class> getClassToGeneratedClasses() {
        return classToGeneratedClasses;
    }

    /**
     * INTERNAL:
     * Set the map containing which Class (by name) corresponds to which generated class.
     */
    public void setClassToGeneratedClasses(HashMap<String, Class> classToClass) {
        this.classToGeneratedClasses = classToClass;
    }

    /**
     * ADVANCED:
     * Adjust the OXM metadata to take into accound ORM mapping metadata
     */
     public void applyORMMetadata(AbstractSession ormSession) {
        this.xmlContext.applyORMMetadata(ormSession);
     }

     /**
      * INTERNAL:
      * Get the map of which QName corresponds to which declared class.
      */
    public HashMap<QName, Class> getQNamesToDeclaredClasses() {
        return qNamesToDeclaredClasses;
    }

    /**
     *  INTERNAL:
     *  Set the map of which QName corresponds to which declared class.
     */
    public void setQNamesToDeclaredClasses(
            HashMap<QName, Class> nameToDeclaredClasses) {
        qNamesToDeclaredClasses = nameToDeclaredClasses;
    }

    /**
     * INTERNAL:
     * Get the map for which array class (by name) corresponds to which generated class
     */
    public Map<String, Class>getArrayClassesToGeneratedClasses(){
        return generator.getAnnotationsProcessor().getArrayClassesToGeneratedClasses();
    }

    /**
     * INTERNAL:
     * Get the map for which collection class (by Type) corresponds to which generated class
     */
    public Map<Type, Class>getCollectionClassesToGeneratedClasses(){
        return generator.getAnnotationsProcessor().getCollectionClassesToGeneratedClasses();
    }
    
    /**
     * INTERNAL:
     * Populate the map of which Type corresponds to which QName.
     * The keys should be all the boundTypes used to create the JAXBContext.
     * If the JAXBContext was not created with the constructor that takes a Type[] then 
     * this Map will be empty.
     */
    public void initTypeToSchemaType() {
        this.typeToSchemaType = new HashMap<Type, QName>();
        
        if(typeToTypeMappingInfo == null || typeToTypeMappingInfo.size() == 0){
        	return;
        }
        
        Iterator descriptors = xmlContext.getSession(0).getProject().getOrderedDescriptors().iterator();

        //Add schema types generated for mapped domain classes
        while (descriptors.hasNext()) {
            XMLDescriptor next = (XMLDescriptor)descriptors.next();
            Class javaClass = next.getJavaClass();

            if (next.getSchemaReference() != null){
                QName schemaType = next.getSchemaReference().getSchemaContextAsQName(next.getNamespaceResolver());
                Type type = null;
                if (generator != null) {
                	
                    type = generator.getAnnotationsProcessor().getGeneratedClassesToCollectionClasses().get(javaClass);
                    
                    if (type == null) {
                        JavaClass arrayClass = (JavaClass)generator.getAnnotationsProcessor().getGeneratedClassesToArrayClasses().get(javaClass);
                        if (arrayClass != null) {
                            String arrayClassName = arrayClass.getName();
                            try {
                                type = PrivilegedAccessHelper.getClassForName(arrayClassName);
                            } catch (Exception ex) {}
                        }      
                        
                        if(type == null && getTypeMappingInfoToGeneratedType() != null){                             	
                        	Iterator<Map.Entry<TypeMappingInfo, Class>> iter = getTypeMappingInfoToGeneratedType().entrySet().iterator();
                        	while(iter.hasNext()){
                        		Map.Entry<TypeMappingInfo, Class> entry = iter.next();
                        		if(entry.getValue().equals(javaClass)){
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
        	for (TypeMappingInfo next:this.boundTypes) {
                if (this.typeToSchemaType.get(next) == null) {
                	Type nextType = next.getType();
                	QName name = getSchemaTypeForTypeMappingInfo(nextType);
                	if (name != null) {
                        this.typeToSchemaType.put(nextType, name);
                    }                	                	
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     * Get the QName which the given Type corresponds to.
     * Valid types should be all the boundTypes used to create the JAXBContext.
     * If the JAXBContext was not created with the construction that takes a Type[] then 
     * this will be return null.
     */
    private QName getSchemaTypeForTypeMappingInfo(Type type){    	
        QName name = null;
        //Check for annotation overrides
        if (type instanceof Class) {
            name = this.generator.getAnnotationsProcessor().getUserDefinedSchemaTypes().get(((Class)type).getName());
            if (name == null) {        	
            	Class theClass = (Class)type;
                //Change default for byte[] to Base64 (JAXB 2.0 default)
                if (type == ClassConstants.ABYTE || type == ClassConstants.APBYTE || type == Image.class || type == Source.class || theClass.getCanonicalName().equals("javax.activation.DataHandler") ) {
                    name = XMLConstants.BASE_64_BINARY_QNAME;
                } else if(type == ClassConstants.OBJECT){
                	name = XMLConstants.ANY_TYPE_QNAME;
                } else if(type == ClassConstants.XML_GREGORIAN_CALENDAR) {
                    name = XMLConstants.ANY_SIMPLE_TYPE_QNAME;
                } else {
                    name = (QName)XMLConversionManager.getDefaultJavaTypes().get(type);
                }
            }
            
        }
        
    	return name;
    }
    
    
    /**
     * INTERNAL:
     * Get the map of which TypeMappingInfo corresponds to which QName.
     * The keys should be all the boundTypes used to create the JAXBContext.
     * If the JAXBContext was not created with the constructor that takes a TypeMappingInfo[]  
     * this Map will be empty.
     */    
    public Map<TypeMappingInfo, QName> getTypeMappingInfoToSchemaType() {
        if(typeToTypeMappingInfo != null && typeToTypeMappingInfo.size() >0){
        	return new HashMap<TypeMappingInfo, QName>();
        }

        return generator.getAnnotationsProcessor().getTypeMappingInfoToSchemaType();
    }

    /**
     * INTERNAL:
     * Get the map of which Type corresponds to which QName.
     * The keys should be all the boundTypes used to create the JAXBContext.
     * If the JAXBContext was not created with the constructor that takes a Type[] then 
     * this Map will be empty.
     */
    public HashMap<java.lang.reflect.Type, QName> getTypeToSchemaType() {
    	if(typeToSchemaType == null){
    		initTypeToSchemaType();
    	}   
        return typeToSchemaType;
    }
     
    Map<TypeMappingInfo, Class> getTypeMappingInfoToGeneratedType() {
        return this.typeMappingInfoToGeneratedType;
    }
    
    Map<Type, TypeMappingInfo> getTypeToTypeMappingInfo() {
        return this.typeToTypeMappingInfo;
    }
    
    void setTypeToTypeMappingInfo(Map<Type, TypeMappingInfo> typeToMappingInfo) {
        this.typeToTypeMappingInfo = typeToMappingInfo;
        this.generator.setTypeToTypeMappingInfo(typeToMappingInfo);
    }
    
    void setTypeMappingInfoToJavaTypeAdapaters(Map<TypeMappingInfo, JAXBContext.RootLevelXmlAdapter> typeMappingInfoToAdapters) {
        this.typeMappingInfoToJavaTypeAdapters = typeMappingInfoToAdapters;
    }
    
    private Map<TypeMappingInfo, JAXBContext.RootLevelXmlAdapter> createAdaptersForAdapterClasses(Map<TypeMappingInfo, Class> typeMappingInfoToAdapterClasses) {
        Map<TypeMappingInfo, JAXBContext.RootLevelXmlAdapter> typeMappingInfoToAdapters = new HashMap<TypeMappingInfo, JAXBContext.RootLevelXmlAdapter>();
        for(Entry<TypeMappingInfo, Class> entry : typeMappingInfoToAdapterClasses.entrySet()) {
            Class adapterClass = entry.getValue();
            if(adapterClass != null) {
                try {
                    XmlAdapter adapter = (XmlAdapter)adapterClass.newInstance();
                    Class boundType = getBoundTypeForXmlAdapterClass(adapterClass);
                    RootLevelXmlAdapter rootLevelXmlAdapter = new RootLevelXmlAdapter(adapter, boundType);
                    
                    typeMappingInfoToAdapters.put(entry.getKey(), rootLevelXmlAdapter);
                } catch(Exception ex) {}
            }
        }
        return typeMappingInfoToAdapters;
    }
    
    private Class getBoundTypeForXmlAdapterClass(Class adapterClass) {
        Class boundType = Object.class;

        for (Method method:PrivilegedAccessHelper.getDeclaredMethods(adapterClass)) {
            if (method.getName().equals("marshal")) {
                Class returnType = PrivilegedAccessHelper.getMethodReturnType(method);
                if(!returnType.getName().equals(boundType.getName())) {
                    boundType = returnType;
                    break;
                }
            }
        }
        return boundType;
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
        return this.typeMappingInfoToJavaTypeAdapters;
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
        return this.xmlContext.getValueByXPath(object, xPath, namespaceResolver, returnType);
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
        this.xmlContext.setValueByXPath(object, xPath, namespaceResolver, value);
    }

}
