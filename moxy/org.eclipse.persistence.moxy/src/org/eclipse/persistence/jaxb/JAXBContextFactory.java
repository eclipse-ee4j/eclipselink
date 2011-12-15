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

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;

import org.eclipse.persistence.jaxb.JAXBContext.JAXBContextInput;
import org.eclipse.persistence.jaxb.JAXBContext.ContextPathInput;
import org.eclipse.persistence.jaxb.JAXBContext.TypeMappingInfoInput;
import org.eclipse.persistence.jaxb.compiler.CompilerHelper;
import org.eclipse.persistence.jaxb.compiler.XMLProcessor;
import org.eclipse.persistence.jaxb.metadata.MetadataSource;
import org.eclipse.persistence.jaxb.xmlmodel.JavaType;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings.JavaTypes;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * <p>
 * <b>Purpose:</b>An EclipseLink specific JAXBContextFactory. This class can be specified in a
 * jaxb.properties file to make use of EclipseLink's JAXB 2.1 implementation.
 * <p>
 * <b>Responsibilities:</b>
 * <ul>
 * <li>Create a JAXBContext from an array of Classes and a Properties object</li>
 * <li>Create a JAXBContext from a context path and a classloader</li>
 * </ul>
 * <p>
 * This class is the entry point into in EclipseLink's JAXB 2.1 Runtime. It provides the required
 * factory methods and is invoked by javax.xml.bind.JAXBContext.newInstance() to create new
 * instances of JAXBContext. When creating a JAXBContext from a contextPath, the list of classes is
 * derived either from an ObjectFactory class (schema-to-java) or a jaxb.index file
 * (java-to-schema).
 *
 * @author mmacivor
 * @see javax.xml.bind.JAXBContext
 * @see org.eclipse.persistence.jaxb.JAXBContext
 * @see org.eclipse.persistence.jaxb.compiler.Generator
 */
public class JAXBContextFactory {

    public static final String ECLIPSELINK_OXM_XML_KEY = "eclipselink-oxm-xml";
    public static final String DEFAULT_TARGET_NAMESPACE_KEY = "defaultTargetNamespace";
    public static final String ANNOTATION_HELPER_KEY = "annotationHelper";
    public static final String PKG_SEPARATOR = ".";

    /**
     * Create a JAXBContext on the array of Class objects.  The JAXBContext will
     * also be aware of classes reachable from the classes in the array.
     */
    public static javax.xml.bind.JAXBContext createContext(Class[] classesToBeBound, Map properties) throws JAXBException {
        ClassLoader loader = null;
        if (classesToBeBound.length > 0) {
            loader = classesToBeBound[0].getClassLoader();
        }
        return createContext(classesToBeBound, properties, loader);
    }

    /**
     * Create a JAXBContext on the array of Class objects.  The JAXBContext will
     * also be aware of classes reachable from the classes in the array.
     */
    public static javax.xml.bind.JAXBContext createContext(Class[] classesToBeBound, Map properties, ClassLoader classLoader) throws JAXBException {
        Type[] types = new Type[classesToBeBound.length];
        System.arraycopy(classesToBeBound, 0, types, 0, classesToBeBound.length);
        return createContext(types, properties, classLoader);
    }

    /**
     * Create a JAXBContext on context path.  The JAXBContext will
     * also be aware of classes reachable from the classes on the context path.
     */
    public static javax.xml.bind.JAXBContext createContext(String contextPath, ClassLoader classLoader) throws JAXBException {
        return createContext(contextPath, classLoader, null);
    }

    /**
     * Create a JAXBContext on context path.  The JAXBContext will
     * also be aware of classes reachable from the classes on the context path.
     */
    public static javax.xml.bind.JAXBContext createContext(String contextPath, ClassLoader classLoader, Map properties) throws JAXBException {
        JAXBContextInput contextInput = new ContextPathInput(contextPath, properties, classLoader);
        JAXBContext context = new JAXBContext(contextInput);
        if (context.isRefreshable()) {
            context.postInitialize();
        }
        return context;
    }

    /**
     * Create a JAXBContext on the array of Type objects.  The JAXBContext will
     * also be aware of classes reachable from the types in the array.  The
     * preferred means of creating a Type aware JAXBContext is to create the
     * JAXBContext with an array of TypeMappingInfo objects.
     */
    public static javax.xml.bind.JAXBContext createContext(Type[] typesToBeBound, Map properties, ClassLoader classLoader) throws JAXBException {
        Map<Type, TypeMappingInfo> typeToTypeMappingInfo = new HashMap<Type, TypeMappingInfo>();
        TypeMappingInfo[] typeMappingInfo = new TypeMappingInfo[typesToBeBound.length];
        for(int i = 0; i < typesToBeBound.length; i++) {
            TypeMappingInfo tmi = new TypeMappingInfo();
            tmi.setType(typesToBeBound[i]);
            typeToTypeMappingInfo.put(typesToBeBound[i], tmi);
            typeMappingInfo[i] = tmi;
        }

        JAXBContext context = (JAXBContext)createContext(typeMappingInfo, properties, classLoader);
        context.setTypeToTypeMappingInfo(typeToTypeMappingInfo);

        return context;
    }

    /**
     * Create a JAXBContext on the array of TypeMappingInfo objects.  The
     * JAXBContext will also be aware of classes reachable from the types in the
     * array.  This is the preferred means of creating a Type aware JAXBContext.
     */
    public static javax.xml.bind.JAXBContext createContext(TypeMappingInfo[] typesToBeBound, Map properties, ClassLoader classLoader) throws JAXBException {
        JAXBContextInput contextInput = new TypeMappingInfoInput(typesToBeBound, properties, classLoader);
        JAXBContext context = new JAXBContext(contextInput);
        if (context.isRefreshable()) {
            context.postInitialize();
        }
        return context;
    }

    /**
     * Convenience method for processing a properties map and creating a map of 
     * package names to XmlBindings instances.
     *
     * It is assumed that the given map's key will be ECLIPSELINK_OXM_XML_KEY,
     * and the value will be:
     * 
     * 1)  Map<String, Object>
     *     - Object is one of those listed in 3) below
     * 2)  List<Object>
     *     - Object is one of those listed in 3) below
     *     - Bindings file must contain package-name attribute on 
     *       xml-bindings element
     * 3)  One of:
     *     - java.io.File
     *     - java.io.InputStream
     *     - java.io.Reader
     *     - java.net.URL
     *     - javax.xml.stream.XMLEventReader
     *     - javax.xml.stream.XMLStreamReader
     *     - javax.xml.transform.Source
     *     - org.w3c.dom.Node
     *     - org.xml.sax.InputSource
     *      
     *     - Bindings file must contain package-name attribute on 
     *       xml-bindings element
     */
    public static Map<String, XmlBindings> getXmlBindingsFromProperties(Map properties, ClassLoader classLoader) {
        Map<String, List<XmlBindings>> bindings = new HashMap<String, List<XmlBindings>>();
        Object value;
        if (properties != null && ((value = properties.get(ECLIPSELINK_OXM_XML_KEY)) != null)) {
            // handle Map<String, Object>
            if (value instanceof Map) {
                Map<String, Object> metadataFiles = null;
                try {
                    metadataFiles = (Map<String, Object>) properties.get(ECLIPSELINK_OXM_XML_KEY);
                } catch (ClassCastException x) {
                    throw org.eclipse.persistence.exceptions.JAXBException.incorrectValueParameterTypeForOxmXmlKey();
                }
                if (metadataFiles != null) {
                    for(Entry<String, Object> entry : metadataFiles.entrySet()) {
                        String key = null;
                        List<XmlBindings> xmlBindings = new ArrayList<XmlBindings>();
                        try {
                            key = entry.getKey();
                            if (key == null) {
                                throw org.eclipse.persistence.exceptions.JAXBException.nullMapKey();
                            }
                        } catch (ClassCastException cce) {
                            throw org.eclipse.persistence.exceptions.JAXBException.incorrectKeyParameterType();
                        }
                        Object metadataSource = entry.getValue();
                        if (metadataSource == null) {
                            throw org.eclipse.persistence.exceptions.JAXBException.nullMetadataSource(key);
                        }
                        if(metadataSource instanceof List) {
                            for(Object next: (List)metadataSource) {
                                XmlBindings binding = getXmlBindings(next, classLoader, properties);
                                if(binding != null) {
                                    xmlBindings.add(binding);
                                }
                            }
                        } else {
                            XmlBindings binding = getXmlBindings(metadataSource, classLoader, properties);
                            if(binding != null) {
                                xmlBindings.add(binding);
                            }
                        }
                        if (xmlBindings != null) {
                            bindings.put(key, xmlBindings);
                        }
                    }
                }
            // handle List<Object>
            } else if (value instanceof List) {
                for (Object metadataSource : (List) value) {
                    if (metadataSource == null) {
                        throw org.eclipse.persistence.exceptions.JAXBException.nullMetadataSource();
                    }
                    bindings = processBindingFile(bindings, metadataSource, classLoader, properties);
                }
            // handle Object
            } else {
                bindings = processBindingFile(bindings, value, classLoader, properties);
            }
        }
        Map<String, XmlBindings> mergedBindings = new HashMap<String, XmlBindings>(bindings.size());
        for(Entry<String, List<XmlBindings>> next:bindings.entrySet()) {
            mergedBindings.put(next.getKey(), XMLProcessor.mergeXmlBindings(next.getValue()));
        }
        return mergedBindings;
    }
    
    /**
     * Processing a bindings file and add it to a given Map of package name to binding
     * files.
     * 
     * @param originalBindings Map of bindings to be updated
     * @param bindingHandle handle to bindings file
     * @param classLoader
     * @return
     */
    private static Map<String, List<XmlBindings>> processBindingFile(Map<String, List<XmlBindings>> originalBindings, Object bindingHandle, ClassLoader classLoader, Map<String, Object> properties) {
        Map<String, List<XmlBindings>> bindingMap = originalBindings;
        XmlBindings binding = getXmlBindings(bindingHandle, classLoader, properties);
        if (binding != null) {
            String key = binding.getPackageName();
            if (key.equals(XMLProcessor.DEFAULT)) {
                throw org.eclipse.persistence.exceptions.JAXBException.packageNotSetForBindingException();
            }
            // may need to prepend the package-name
            JavaTypes jTypes = binding.getJavaTypes();
            if (jTypes != null) {
                for (JavaType javaType : jTypes.getJavaType()) {
                    String javaTypeName = javaType.getName();
                    if (!(javaTypeName.contains(key))) {
                        javaType.setName(key + PKG_SEPARATOR + javaTypeName);
                    }
                }
            }
            List<XmlBindings> existingBindings = bindingMap.get(key);
            if(existingBindings != null) {
                existingBindings.add(binding);
            } else {
                existingBindings = new ArrayList<XmlBindings>();
                existingBindings.add(binding);
                bindingMap.put(key, existingBindings);
            }
        }
        return bindingMap;
    }

    /**
     * Convenience method for creating an XmlBindings object based on a given Object. The method
     * will load the eclipselink metadata model and unmarshal the Object. This assumes that the
     * Object represents the eclipselink-oxm.xml metadata file to be unmarshalled.
     * 
     * @param metadata assumed to be one of:  File, InputSource, InputStream, Reader, Source
     */
    private static XmlBindings getXmlBindings(Object metadata, ClassLoader classLoader, Map<String, Object> properties) {
        XmlBindings xmlBindings = null;
        Unmarshaller unmarshaller;
        // only create the JAXBContext for our XmlModel once
        JAXBContext jaxbContext = CompilerHelper.getXmlBindingsModelContext();
        try {
            unmarshaller = jaxbContext.createUnmarshaller();
            if (metadata instanceof File) {
                xmlBindings = (XmlBindings) unmarshaller.unmarshal((File) metadata);
            } else if (metadata instanceof InputSource) {
                xmlBindings = (XmlBindings) unmarshaller.unmarshal((InputSource) metadata);
            } else if (metadata instanceof InputStream) {
                xmlBindings = (XmlBindings) unmarshaller.unmarshal((InputStream) metadata);
            } else if (metadata instanceof Node) {
                xmlBindings = (XmlBindings) unmarshaller.unmarshal((Node) metadata);
            } else if (metadata instanceof Reader) {
                xmlBindings = (XmlBindings) unmarshaller.unmarshal((Reader) metadata);
            } else if (metadata instanceof Source) {
                xmlBindings = (XmlBindings) unmarshaller.unmarshal((Source) metadata);
            } else if (metadata instanceof URL) {
                xmlBindings = (XmlBindings) unmarshaller.unmarshal((URL) metadata);
            } else if (metadata instanceof XMLEventReader) {
                xmlBindings = (XmlBindings) unmarshaller.unmarshal((XMLEventReader) metadata);
            } else if (metadata instanceof XMLStreamReader) {
                xmlBindings = (XmlBindings) unmarshaller.unmarshal((XMLStreamReader) metadata);
            } else if (metadata instanceof MetadataSource){
                xmlBindings = ((MetadataSource)metadata).getXmlBindings(properties, classLoader);
            } else if (metadata instanceof String) {
                if(((String)metadata).length() == 0) {
                    throw org.eclipse.persistence.exceptions.JAXBException.unableToLoadMetadataFromLocation((String)metadata);
                }
                URL url = null;
                try {
                    url = new URL((String)metadata);
                } catch(MalformedURLException ex) {
                    url = classLoader.getResource((String)metadata);
                }
                if(url != null) {
                    xmlBindings = (XmlBindings)unmarshaller.unmarshal(url);
                } else {
                    //throw exception
                    throw org.eclipse.persistence.exceptions.JAXBException.unableToLoadMetadataFromLocation((String)metadata);
                }
            } else {
                throw org.eclipse.persistence.exceptions.JAXBException.incorrectValueParameterTypeForOxmXmlKey();
            }
        } catch (JAXBException jaxbEx) {
            throw org.eclipse.persistence.exceptions.JAXBException.couldNotUnmarshalMetadata(jaxbEx);
        }
        return xmlBindings;
    }



}