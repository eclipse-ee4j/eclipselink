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
 * dmccann - June 17/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb.compiler;

import static org.eclipse.persistence.jaxb.javamodel.Helper.getQualifiedJavaTypeName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.javamodel.Helper;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaField;
import org.eclipse.persistence.jaxb.javamodel.JavaHasAnnotations;
import org.eclipse.persistence.jaxb.javamodel.JavaMethod;
import org.eclipse.persistence.jaxb.javamodel.JavaModelInput;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaClassImpl;
import org.eclipse.persistence.jaxb.xmlmodel.JavaAttribute;
import org.eclipse.persistence.jaxb.xmlmodel.JavaType;
import org.eclipse.persistence.jaxb.xmlmodel.XmlNamedObjectGraph;
import org.eclipse.persistence.jaxb.xmlmodel.XmlAbstractNullPolicy;
import org.eclipse.persistence.jaxb.xmlmodel.XmlAccessType;
import org.eclipse.persistence.jaxb.xmlmodel.XmlAnyAttribute;
import org.eclipse.persistence.jaxb.xmlmodel.XmlAnyElement;
import org.eclipse.persistence.jaxb.xmlmodel.XmlAttribute;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;
import org.eclipse.persistence.jaxb.xmlmodel.XmlElement;
import org.eclipse.persistence.jaxb.xmlmodel.XmlElementRef;
import org.eclipse.persistence.jaxb.xmlmodel.XmlElementRefs;
import org.eclipse.persistence.jaxb.xmlmodel.XmlElementWrapper;
import org.eclipse.persistence.jaxb.xmlmodel.XmlElements;
import org.eclipse.persistence.jaxb.xmlmodel.XmlEnum;
import org.eclipse.persistence.jaxb.xmlmodel.XmlEnumValue;
import org.eclipse.persistence.jaxb.xmlmodel.XmlInverseReference;
import org.eclipse.persistence.jaxb.xmlmodel.XmlJavaTypeAdapter;
import org.eclipse.persistence.jaxb.xmlmodel.XmlJavaTypeAdapters;
import org.eclipse.persistence.jaxb.xmlmodel.XmlJoinNodes;
import org.eclipse.persistence.jaxb.xmlmodel.XmlMap;
import org.eclipse.persistence.jaxb.xmlmodel.XmlNsForm;
import org.eclipse.persistence.jaxb.xmlmodel.XmlRegistry;
import org.eclipse.persistence.jaxb.xmlmodel.XmlSchema;
import org.eclipse.persistence.jaxb.xmlmodel.XmlSchemaType;
import org.eclipse.persistence.jaxb.xmlmodel.XmlSchemaTypes;
import org.eclipse.persistence.jaxb.xmlmodel.XmlTransformation;
import org.eclipse.persistence.jaxb.xmlmodel.XmlTransient;
import org.eclipse.persistence.jaxb.xmlmodel.XmlValue;
import org.eclipse.persistence.jaxb.xmlmodel.JavaType.JavaAttributes;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings.JavaTypes;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings.XmlEnums;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings.XmlRegistries;
import org.eclipse.persistence.jaxb.xmlmodel.XmlProperties.XmlProperty;
import org.eclipse.persistence.jaxb.xmlmodel.XmlSchema.XmlNs;
import org.eclipse.persistence.oxm.XMLNameTransformer;
import org.eclipse.persistence.oxm.NamespaceResolver;

/**
 * INTERNAL:
 * <p>
 * Purpose: XMLProcessor is used to process the meta data provided
 * in external OXM XML files.  This information is then used in conjunction with the 
 * information from AnnotationsProcess to generate schemas (via SchemaGenerator) 
 * and mappings (via MappingsGenerator).
 * </p>
 * 
 * <p>
 * As a general rule meta data provided in external OXM XML files overrides meta data
 * specified through annotations.
 * </p>
 * @see org.eclipse.persistence.jaxb.compiler.Generator 
 */
public class XMLProcessor {
    private Map<String, XmlBindings> xmlBindingMap;
    private JavaModelInput jModelInput;
    private AnnotationsProcessor aProcessor;
    private JAXBMetadataLogger logger;
    private static final String COLON = ":";
    private static final String SLASH = "/";
    private static final String SELF = ".";
    private static final String OPEN_BRACKET =  "[";
    private static final String IS_STR = "is";
    private static final String GET_STR = "get";
    private static final String SET_STR = "set";
    private static final String JAVA_LANG_OBJECT = "java.lang.Object";
    public static final String DEFAULT = "##default";
    public static final String GENERATE = "##generate";
    

    /**
     * This is the preferred constructor.
     * 
     * @param bindings
     */
    public XMLProcessor(Map<String, XmlBindings> bindings) {
        this.xmlBindingMap = bindings;
    }
    
    /**
     * Process XmlBindings on a per package basis for a given
     * AnnotationsProcessor instance.
     * 
     * @param annotationsProcessor
     */
    public void processXML(AnnotationsProcessor annotationsProcessor, JavaModelInput jModelInput, TypeMappingInfo[] typeMappingInfos, JavaClass[] originalJavaClasses) {
        this.jModelInput = jModelInput;
        this.aProcessor = annotationsProcessor;
        this.aProcessor.setHasXmlBindings(true);
        Map<String, XmlEnum> xmlEnumMap = new HashMap<String, XmlEnum>();
        aProcessor.init(originalJavaClasses, typeMappingInfos);

        // build a map of packages to JavaClass so we only process the
        // JavaClasses for a given package additional classes - i.e. ones from
        // packages not listed in XML - will be processed later
        Map<String, ArrayList<JavaClass>> pkgToClassMap = buildPackageToJavaClassMap();

        // process each XmlBindings in the map
        XmlBindings xmlBindings;
        for (String packageName : xmlBindingMap.keySet()) {
            ArrayList classesToProcess = pkgToClassMap.get(packageName);
            if (classesToProcess == null) {
                getLogger().logWarning("jaxb_metadata_warning_no_classes_to_process", new Object[] { packageName });
                continue;
            }

            xmlBindings = xmlBindingMap.get(packageName);

            // handle @XmlSchema override
            NamespaceInfo nsInfo = processXmlSchema(xmlBindings, packageName);
            if (nsInfo != null) {
                aProcessor.addPackageToNamespaceMapping(packageName, nsInfo);
            }
            
            // handle xml-registries
            // add an entry to the map of registries keyed on factory class name for each
            XmlRegistries xmlRegs = xmlBindings.getXmlRegistries();  
            if (xmlRegs != null) {
                for (XmlRegistry xmlReg : xmlRegs.getXmlRegistry()) {
                    aProcessor.addXmlRegistry(xmlReg.getName(), xmlReg);
                }
            }

            // build an array of JavaModel classes to process
            JavaClass[] javaClasses = (JavaClass[]) classesToProcess.toArray(new JavaClass[classesToProcess.size()]);

            // handle xml-enums
            // build a map of enum class names to XmlEnum objects
            XmlEnums xmlEnums = xmlBindings.getXmlEnums();
            if (xmlEnums != null) {
                for (XmlEnum xmlEnum : xmlEnums.getXmlEnum()) {                    
                    xmlEnumMap.put(getQualifiedJavaTypeName(xmlEnum.getJavaEnum(), packageName), xmlEnum);
                }
            }
            
            //handle superclass override
            if(xmlBindings.getJavaTypes() != null) {
                List<JavaType> types = xmlBindings.getJavaTypes().getJavaType();
                for(JavaType next:types) {
                    JavaClass typeClass = jModelInput.getJavaModel().getClass(getQualifiedJavaTypeName(next.getName(), packageName));
                    if(typeClass != null && typeClass.getClass() == JavaClassImpl.class) {
                        if(next.getSuperType() != null && !(next.getSuperType().equals(DEFAULT))) {
                            JavaClass newSuperClass = jModelInput.getJavaModel().getClass(next.getSuperType());
                            ((JavaClassImpl)typeClass).setSuperClassOverride(newSuperClass);
                        }
                    }
                }
            }
            // pre-build the TypeInfo objects
            Map<String, TypeInfo> typeInfoMap = aProcessor.preBuildTypeInfo(javaClasses);

            // handle package-level xml-schema-types
            List<XmlSchemaType> xmlSchemaTypes = null;
            XmlSchemaTypes sTypes = xmlBindings.getXmlSchemaTypes();
            if (sTypes != null) {
                xmlSchemaTypes = sTypes.getXmlSchemaType();
            } else {
                xmlSchemaTypes = new ArrayList<XmlSchemaType>();
            }
            // handle package-level xml-schema-type
            if (xmlBindings.getXmlSchemaType() != null) {
                xmlSchemaTypes.add(xmlBindings.getXmlSchemaType());
            }
            // process each xml-schema-type entry
            for (XmlSchemaType sType : xmlSchemaTypes) {
                JavaClass jClass = aProcessor.getHelper().getJavaClass(sType.getType());
                if (jClass != null) {
                    aProcessor.processSchemaType(sType.getName(), sType.getNamespace(), jClass.getQualifiedName());
                }
            }

            PackageInfo packageInfo = aProcessor.getPackageToPackageInfoMappings().get(packageName);
            if (packageInfo == null) {
                packageInfo = new PackageInfo();                             
            }
            if(xmlBindings.isSetXmlAccessorType()) {
                packageInfo.setAccessType(xmlBindings.getXmlAccessorType());
            }
            JavaTypes jTypes = xmlBindings.getJavaTypes();
            if (jTypes != null) {
                for (JavaType javaType : jTypes.getJavaType()) {
                    TypeInfo info = typeInfoMap.get(getQualifiedJavaTypeName(javaType.getName(), packageName));

                    // package/class override order:
                    // 1 - xml class-level
                    // 2 - java object class-level
                    // 3 - xml package-level
                    // 4 - package-info.java

                    // handle class-level @XmlJavaTypeAdapter override
                    if (javaType.getXmlJavaTypeAdapter() != null) {
                        info.setXmlJavaTypeAdapter(javaType.getXmlJavaTypeAdapter());
                    }
                    
                    // handle class-level @XmlNameTransformer
                    String transformerClassName = javaType.getXmlNameTransformer();
                    
                    XMLNameTransformer transformer = getXMLNameTransformerClassFromString(transformerClassName);
                    if(transformer != null){
                        info.setXmlNameTransformer(transformer);
                    }
                    // handle class-level @XmlExtensible override
                    if (javaType.getXmlVirtualAccessMethods() != null) {
                        info.setXmlVirtualAccessMethods(javaType.getXmlVirtualAccessMethods());
                    }

                    // handle class-level @XmlAccessorOrder override
                    if (javaType.isSetXmlAccessorOrder()) {
                        info.setXmlAccessOrder(javaType.getXmlAccessorOrder());
                    } else if (!info.isSetXmlAccessOrder()) {
                        // handle package-level @XmlAccessorOrder override
                        if (xmlBindings.isSetXmlAccessorOrder()) {
                            info.setXmlAccessOrder(xmlBindings.getXmlAccessorOrder());
                        } else {
                            // finally, check the NamespaceInfo
                            info.setXmlAccessOrder(packageInfo.getAccessOrder());
                        }
                    }

                    // handle class-level @XmlAccessorType override
                    if (javaType.isSetXmlAccessorType()) {
                        info.setXmlAccessType(javaType.getXmlAccessorType());
                    }
                    // handle @XmlInlineBinaryData override
                    if (javaType.isSetXmlInlineBinaryData()) {
                        info.setInlineBinaryData(javaType.isXmlInlineBinaryData());
                    }
                    // handle @XmlTransient override
                    if (javaType.isSetXmlTransient()) {
                        info.setXmlTransient(javaType.isXmlTransient());
                    }
                    // handle @XmlRootElement
                    if (javaType.getXmlRootElement() != null) {
                        info.setXmlRootElement(javaType.getXmlRootElement());
                    }
                    // handle @XmlSeeAlso override
                    if (javaType.getXmlSeeAlso() != null && javaType.getXmlSeeAlso().size() > 0) {
                        info.setXmlSeeAlso(javaType.getXmlSeeAlso());
                    }
                    // handle @XmlType override
                    if (javaType.getXmlType() != null) {
                        info.setXmlType(javaType.getXmlType());
                    }
                    // handle @XmlCustomizer override
                    if (javaType.getXmlCustomizer() != null) {
                        info.setXmlCustomizer(javaType.getXmlCustomizer());
                    }
                    // handle @XmlClassExtractor override
                    if (javaType.getXmlClassExtractor() != null) {
                        info.setClassExtractorName(javaType.getXmlClassExtractor().getClazz());
                    }
                    // handle @XmlProperties override
                    if (javaType.getXmlProperties() != null && javaType.getXmlProperties().getXmlProperty().size() > 0) {
                        // may need to merge with @XmlProperties (xml wins in the case of a conflict)
                        if (info.getUserProperties() != null) {
                            info.setUserProperties(mergeUserPropertyMap(javaType.getXmlProperties().getXmlProperty(), info.getUserProperties()));
                        } else {
                            info.setUserProperties(createUserPropertyMap(javaType.getXmlProperties().getXmlProperty()));
                        }
                    }
                    // handle @XmlDiscriminatorNode override
                    if (javaType.getXmlDiscriminatorNode() != null) {
                        info.setXmlDiscriminatorNode(javaType.getXmlDiscriminatorNode());
                    }
                    // handle @NamedObjectGraph/@NamedObjectGraphs override
                    if (javaType.getXmlNamedObjectGraphs() != null) {
                        List<XmlNamedObjectGraph> currentGraphs = info.getObjectGraphs();
                        for(XmlNamedObjectGraph nextGraph:javaType.getXmlNamedObjectGraphs().getXmlNamedObjectGraph()) {
                            //check to see if a graph with the same name already exists
                            //if so, remove it and replace it with the one from xml.
                            //if not, add the new one
                            for(XmlNamedObjectGraph nextExistingGraph: currentGraphs) {
                                if(nextGraph.getName().equals(nextExistingGraph.getName())) {
                                    currentGraphs.remove(nextExistingGraph);
                                    break;
                                }
                            }
                        }
                        currentGraphs.addAll(javaType.getXmlNamedObjectGraphs().getXmlNamedObjectGraph());
                    }
                    // handle @XmlDiscriminatorValue override
                    if (javaType.getXmlDiscriminatorValue() != null) {
                        info.setXmlDiscriminatorValue(javaType.getXmlDiscriminatorValue());
                    }
                }
            }
            //apply package-level @XmlNameTransformer
            Map<String, TypeInfo> typeInfos = aProcessor.getTypeInfosForPackage(packageName);
            String transformerClassName = xmlBindings.getXmlNameTransformer();
            
            XMLNameTransformer transformer = getXMLNameTransformerClassFromString(transformerClassName);
            if(transformer != null){
               packageInfo.setXmlNameTransformer(transformer);
            }
            // apply package-level @XmlJavaTypeAdapters
            for (TypeInfo tInfo : typeInfos.values()) {
            	if(xmlBindings.getXmlJavaTypeAdapters() != null){
	                List<XmlJavaTypeAdapter> adapters = xmlBindings.getXmlJavaTypeAdapters().getXmlJavaTypeAdapter();
	                for (XmlJavaTypeAdapter xja : adapters) {
	                    try {
	                        JavaClass adapterClass = jModelInput.getJavaModel().getClass(xja.getValue());
	                        JavaClass boundType = jModelInput.getJavaModel().getClass(xja.getType());
	                        if (boundType != null) {
	                            tInfo.addPackageLevelAdapterClass(adapterClass, boundType);
	                            packageInfo.getPackageLevelAdaptersByClass().put(boundType.getQualifiedName(), adapterClass);
	                        }
	                    } catch(JAXBException e) {
	                        throw JAXBException.invalidPackageAdapterClass(xja.getValue(), packageName);
	                    }
	                }
            	}
            }            
            
        }
        for (String packageName : xmlBindingMap.keySet()) {
            ArrayList classesToProcess = pkgToClassMap.get(packageName);
            if (classesToProcess == null) {
                getLogger().logWarning("jaxb_metadata_warning_no_classes_to_process", new Object[] { packageName });
                continue;
            }

            xmlBindings = xmlBindingMap.get(packageName);
            JavaClass[] javaClasses = (JavaClass[]) classesToProcess.toArray(new JavaClass[classesToProcess.size()]);
            // post-build the TypeInfo objects
            javaClasses = aProcessor.postBuildTypeInfo(javaClasses);



            // get the generated TypeInfo
            Map<String, TypeInfo> typeInfosForPackage = aProcessor.getTypeInfosForPackage(packageName);

            // update xml-enum info if necessary
            for (Entry<String, TypeInfo> entry : typeInfosForPackage.entrySet()) {
                TypeInfo tInfo = entry.getValue();
                if (tInfo.isEnumerationType()) {
                    EnumTypeInfo etInfo = (EnumTypeInfo) tInfo;
                    XmlEnum xmlEnum = xmlEnumMap.get(etInfo.getClassName());
                    if (xmlEnum != null) {
                        JavaClass restrictionClass = aProcessor.getHelper().getJavaClass(xmlEnum.getValue());
                        // default to String if necessary
                        if (restrictionClass == null) {
                            restrictionClass = jModelInput.getJavaModel().getClass(String.class);
                        }
                        etInfo.setRestrictionBase(aProcessor.getSchemaTypeFor(restrictionClass));
                        for (XmlEnumValue xmlEnumValue : xmlEnum.getXmlEnumValue()) {
                            // overwrite any existing entries (from annotations)
                            etInfo.addJavaFieldToXmlEnumValuePair(true, xmlEnumValue.getJavaEnumValue(), xmlEnumValue.getValue());
                        }
                    }
                }
            }

            // update TypeInfo objects based on the JavaTypes
            JavaTypes jTypes = xmlBindings.getJavaTypes();
            if (jTypes != null) {
                PackageInfo packageInfo = aProcessor.getPackageToPackageInfoMappings().get(packageName);
                NamespaceInfo nsInfo = null;
                if(null != packageInfo) {
                    nsInfo = packageInfo.getNamespaceInfo();
                }
                for (JavaType javaType : jTypes.getJavaType()) {
                    processJavaType(javaType, typeInfosForPackage.get(getQualifiedJavaTypeName(javaType.getName(), packageName)), nsInfo);
                }
            }
            // remove the entry for this package from the map
            pkgToClassMap.remove(packageName);
        }

        // now process remaining classes
        Iterator<ArrayList<JavaClass>> classIt = pkgToClassMap.values().iterator();
        while (classIt.hasNext()) {
            ArrayList<JavaClass> jClassList = classIt.next();
            JavaClass[] jClassArray = (JavaClass[]) jClassList.toArray(new JavaClass[jClassList.size()]);
            aProcessor.buildNewTypeInfo(jClassArray);
            aProcessor.processJavaClasses(jClassArray);
        }

        // need to ensure that any bound types (from XmlJavaTypeAdapter) have TypeInfo 
        // objects built for them - SchemaGenerator will require a descriptor for each
        Map<String, TypeInfo> typeInfos = (Map<String, TypeInfo>) aProcessor.getTypeInfo().clone();
        for (Entry<String, TypeInfo> entry : typeInfos.entrySet()) {
            JavaClass[] jClassArray;
            for (Property prop : entry.getValue().getPropertyList()) {
                if (prop.isSetXmlJavaTypeAdapter()) {
                    jClassArray = new JavaClass[] { prop.getActualType() };
                    aProcessor.buildNewTypeInfo(jClassArray);
                }
            }
        }

        // now trigger the annotations processor to process the classes
        ArrayList<JavaClass> jClasses = aProcessor.getTypeInfoClasses();

        // If multiple bindings (packages) are supplied, re-process super classes
        // (in case super and sub classes were in different packages)
        if (xmlBindingMap.size() > 1) {
            for (JavaClass c : jClasses) {
                TypeInfo ti = aProcessor.getTypeInfo().get(c.getQualifiedName());
                aProcessor.processPropertiesSuperClass(c, ti);
            }
        }

        aProcessor.processPropertyTypes(jClasses.toArray(new JavaClass[jClasses.size()]));
        aProcessor.finalizeProperties();
        aProcessor.createElementsForTypeMappingInfo();
        aProcessor.processJavaClasses(null);
    }

    private XMLNameTransformer getXMLNameTransformerClassFromString(String transformerClassName){
    	XMLNameTransformer transformer = null;
        if(transformerClassName != null){
           Class nameTransformerClass;

             try {
                 nameTransformerClass = Class.forName(transformerClassName);			
             } catch (ClassNotFoundException ex) {
                 throw JAXBException.exceptionWithNameTransformerClass(transformerClassName, ex);
             }  
             try {
                 transformer = (XMLNameTransformer) nameTransformerClass.newInstance();
         	} catch (InstantiationException ex) {
                 throw JAXBException.exceptionWithNameTransformerClass(transformerClassName, ex);
             } catch (IllegalAccessException ex) {
                 throw JAXBException.exceptionWithNameTransformerClass(transformerClassName, ex);
             } 
         }
    	 return transformer;
    	
    }
    
    /**
     * Process a given JavaType's attributes.
     * 
     * @param javaType
     * @param typeInfo
     * @param nsInfo
     */
    private void processJavaType(JavaType javaType, TypeInfo typeInfo, NamespaceInfo nsInfo) {
        // process field/property overrides
        if (null != javaType.getJavaAttributes()) {
            List<String> processedPropertyNames = new ArrayList<String>();
            for (JAXBElement jaxbElement : javaType.getJavaAttributes().getJavaAttribute()) {
                JavaAttribute javaAttribute = (JavaAttribute) jaxbElement.getValue();

                Property originalProperty = typeInfo.getOriginalProperties().get(javaAttribute.getJavaAttribute());
                if(javaAttribute.getXmlAccessorType() != null) {
                    originalProperty = processPropertyForAccessorType(typeInfo, javaAttribute, originalProperty);
                }
                if (originalProperty == null) {
                    if (typeInfo.getXmlVirtualAccessMethods() != null) {
                        Property newProperty = new Property(this.aProcessor.getHelper());
                        newProperty.setPropertyName(javaAttribute.getJavaAttribute());
                        newProperty.setExtension(true);

                        String attributeType = null;

                        if (javaAttribute instanceof XmlElement) {
                            attributeType = ((XmlElement) javaAttribute).getType();
                        } else if (javaAttribute instanceof XmlAttribute) {
                            attributeType = ((XmlAttribute) javaAttribute).getType();
                        }

                        if (attributeType != null && attributeType.equals("DEFAULT")) {
                            newProperty.setType(jModelInput.getJavaModel().getClass(attributeType));
                        } else {
                            newProperty.setType(jModelInput.getJavaModel().getClass(Helper.STRING));
                        }

                        originalProperty = newProperty;
                        typeInfo.addProperty(javaAttribute.getJavaAttribute(), newProperty);
                    } else {
                        getLogger().logWarning(JAXBMetadataLogger.NO_PROPERTY_FOR_JAVA_ATTRIBUTE, new Object[] { javaAttribute.getJavaAttribute(), javaType.getName() });
                        continue;
                    }
                }

                boolean alreadyProcessed = processedPropertyNames.contains(javaAttribute.getJavaAttribute()); 
                Property propToProcess;

                // In the case where there is more than one javaAttribute for the same Property 
                // (multiple mappings to same attribute) clone the original and put it in a 
                // separate Map; otherwise, update the property as per usual
                if (alreadyProcessed) {
                    propToProcess = (Property) originalProperty.clone();
                } else {
                    propToProcess = typeInfo.getProperties().get(javaAttribute.getJavaAttribute());
                }
                
                processJavaAttribute(typeInfo, javaAttribute, propToProcess, nsInfo, javaType);

                // (Bug 346081) if discover a transient attribute apply same behavior as transient annotation and remove
                if(propToProcess.isTransient()){
                    typeInfo.getPropertyList().remove(propToProcess);
                }
                
                // if we are dealing with multiple mappings for the same attribute, leave the existing
                // property as-is and update the additionalProperties list on the owning TypeInfo
                if (alreadyProcessed) {
                    List<Property> additionalProps = null;
                    if(typeInfo.hasAdditionalProperties()) {
                        additionalProps = typeInfo.getAdditionalProperties().get(javaAttribute.getJavaAttribute());
                    }
                    if (additionalProps == null) {
                        additionalProps = new ArrayList<Property>();
                    }
                    additionalProps.add(propToProcess);
                    typeInfo.getAdditionalProperties().put(javaAttribute.getJavaAttribute(), additionalProps);
                } else {
                    // single mapping case; update the TypeInfo as per usual 
                    typeInfo.getProperties().put(javaAttribute.getJavaAttribute(), propToProcess);
                    // keep track of processed property names
                    processedPropertyNames.add(javaAttribute.getJavaAttribute());
                }
            }
        }
    }

    private Property processPropertyForAccessorType(TypeInfo typeInfo, JavaAttribute javaAttribute, Property originalProperty) {
        if(originalProperty == null) {
            Property prop = createProperty(typeInfo, javaAttribute);
            if(prop != null) {
                typeInfo.addProperty(prop.getPropertyName(), prop);
            }
            return prop;
        }
        
        if((javaAttribute.getXmlAccessorType() == XmlAccessType.FIELD && !(originalProperty.isMethodProperty())) || 
                javaAttribute.getXmlAccessorType() == XmlAccessType.PROPERTY && originalProperty.isMethodProperty()) {
            return originalProperty;
        }
        
        originalProperty.setMethodProperty(!(originalProperty.isMethodProperty()));
        if(originalProperty.isMethodProperty()) {
            //figure out get and set method names. See if they exist. 
            JavaClass jClass = this.jModelInput.getJavaModel().getClass(typeInfo.getJavaClassName());

            String propName = originalProperty.getPropertyName();
            propName = Character.toUpperCase(propName.charAt(0)) + propName.substring(1);
            String getMethodName = GET_STR + propName;
            String setMethodName = SET_STR + propName;
            
            JavaMethod getMethod = jClass.getDeclaredMethod(getMethodName, new JavaClass[]{});
            if(getMethod == null) {
                getMethodName = IS_STR + propName;
                getMethod = jClass.getDeclaredMethod(getMethodName, new JavaClass[]{});
            }
            JavaMethod setMethod = jClass.getDeclaredMethod(setMethodName, new JavaClass[]{originalProperty.getType()});
            if(getMethod != null) {
                originalProperty.setGetMethodName(getMethodName);
            }
            if(setMethod != null) {
                originalProperty.setSetMethodName(setMethodName);
            }
        } else {
            originalProperty.setGetMethodName(null);
            originalProperty.setSetMethodName(null);
            originalProperty.setMethodProperty(false);
        }
        
        return originalProperty;
    }

    private Property createProperty(TypeInfo info, JavaAttribute javaAttribute) {
        XmlAccessType xmlAccessorType = javaAttribute.getXmlAccessorType();
        //Property prop = new Property();
        //prop.setPropertyName(javaAttribute.getJavaAttribute());
        String propName = javaAttribute.getJavaAttribute();
        JavaHasAnnotations element = null;
        JavaClass pType = null;
        JavaClass jClass = this.jModelInput.getJavaModel().getClass(info.getJavaClassName());
        if(xmlAccessorType == XmlAccessType.PROPERTY) {
            //set up accessor method names
            String name  = Character.toUpperCase(propName.charAt(0)) + propName.substring(1);
            String getMethodName = GET_STR + name;
            String setMethodName = SET_STR + name;
            
            JavaMethod jMethod = jClass.getDeclaredMethod(getMethodName, new JavaClass[]{});
            if(jMethod == null) {
                getMethodName = IS_STR + name;
                jMethod = jClass.getDeclaredMethod(getMethodName, new JavaClass[]{});
            }
            if(jMethod != null) {
                pType = jMethod.getReturnType();
                element = jMethod;
            } else {
                //look for a set method if type is set on javaAttribute
                for (Object next:jClass.getDeclaredMethods()) {
                    JavaMethod nextMethod = (JavaMethod)next;
                    if(nextMethod.getName().equals(setMethodName) && nextMethod.getParameterTypes().length == 1) {
                        pType = nextMethod.getParameterTypes()[0];
                        element = nextMethod;
                    }
                    
                }
                if(element == null) {
                    return null;
                }
            }
        } else {
            JavaField jField = jClass.getDeclaredField(propName);
            if(jField == null) {
                return null;
            }
            pType = jField.getResolvedType();
            element = jField;
        }
        
        return this.aProcessor.buildNewProperty(info, jClass, element, propName, pType);
    }

    /**
     * Process a given JavaAtribute.
     * 
     * @param javaAttribute
     * @param oldProperty
     * @param nsInfo
     * @return
     */
    private Property processJavaAttribute(TypeInfo typeInfo, JavaAttribute javaAttribute, Property oldProperty, NamespaceInfo nsInfo, JavaType javaType) {
        if (javaAttribute instanceof XmlAnyAttribute) {
            return processXmlAnyAttribute((XmlAnyAttribute) javaAttribute, oldProperty, typeInfo, javaType);
        } 
        if (javaAttribute instanceof XmlAnyElement) {
            return processXmlAnyElement((XmlAnyElement) javaAttribute, oldProperty, typeInfo, javaType);
        }
        if (javaAttribute instanceof XmlAttribute) {
            return processXmlAttribute((XmlAttribute) javaAttribute, oldProperty, typeInfo, nsInfo, javaType);
        }
        if (javaAttribute instanceof XmlElement) {
            return processXmlElement((XmlElement) javaAttribute, oldProperty, typeInfo, nsInfo, javaType);
        } 
        if (javaAttribute instanceof XmlElements) {
            return processXmlElements((XmlElements) javaAttribute, oldProperty, typeInfo);
        }
        if (javaAttribute instanceof XmlElementRef) {
            return processXmlElementRef((XmlElementRef) javaAttribute, oldProperty, typeInfo);
        }
        if (javaAttribute instanceof XmlElementRefs) {
            return processXmlElementRefs((XmlElementRefs) javaAttribute, oldProperty, typeInfo);
        }
        if (javaAttribute instanceof XmlTransient) {
            return processXmlTransient((XmlTransient) javaAttribute, oldProperty);
        }
        if (javaAttribute instanceof XmlValue) {
            return processXmlValue((XmlValue) javaAttribute, oldProperty, typeInfo, javaType);
        }
        if (javaAttribute instanceof XmlJavaTypeAdapter) {
            return processXmlJavaTypeAdapter((XmlJavaTypeAdapter) javaAttribute, oldProperty);
        }
        if (javaAttribute instanceof XmlInverseReference) {
            return processXmlInverseReference((XmlInverseReference)javaAttribute, oldProperty, typeInfo);
        }
        if (javaAttribute instanceof XmlTransformation) {
            return processXmlTransformation((XmlTransformation)javaAttribute, oldProperty, typeInfo);
        }
        if (javaAttribute instanceof XmlJoinNodes) {
            return processXmlJoinNodes((XmlJoinNodes) javaAttribute, oldProperty, typeInfo); 
        }
        getLogger().logWarning("jaxb_metadata_warning_invalid_java_attribute", new Object[] { javaAttribute.getClass() });
        return null;
    }

    /**
     * Handle property-level XmlJavaTypeAdapter
     * 
     * @param xmlAdapter
     * @param oldProperty
     * @return
     */
    private Property processXmlJavaTypeAdapter(XmlJavaTypeAdapter xmlAdapter, Property oldProperty) {
        oldProperty.setXmlJavaTypeAdapter(xmlAdapter);
        return oldProperty;
    }

    /**
     * Handle xml-inverse-reference.
     * 
     * @param xmlInverseReference
     * @param oldProperty
     * @return
     */
    private Property processXmlInverseReference(XmlInverseReference xmlInverseReference, Property oldProperty, TypeInfo info) {
        resetProperty(oldProperty, info);
        oldProperty.setInverseReference(true, false);
        oldProperty.setInverseReferencePropertyName(xmlInverseReference.getMappedBy());
        if (xmlInverseReference.getXmlAccessMethods() != null) {
            oldProperty.setInverseReferencePropertyGetMethodName(xmlInverseReference.getXmlAccessMethods().getGetMethod());
            oldProperty.setInverseReferencePropertySetMethodName(xmlInverseReference.getXmlAccessMethods().getSetMethod());
        }
        // set user-defined properties
        if (xmlInverseReference.getXmlProperties() != null  && xmlInverseReference.getXmlProperties().getXmlProperty().size() > 0) {
            oldProperty.setUserProperties(createUserPropertyMap(xmlInverseReference.getXmlProperties().getXmlProperty()));
        }
        // check for container type
        if (!xmlInverseReference.getContainerType().equals(DEFAULT)) {
            setContainerType(oldProperty, xmlInverseReference.getContainerType());
        }
        // set type
        if (!xmlInverseReference.getType().equals(DEFAULT)) {
            JavaClass pType = jModelInput.getJavaModel().getClass(xmlInverseReference.getType());
            if (aProcessor.getHelper().isCollectionType(oldProperty.getType())) {
                oldProperty.setGenericType(pType);
            } else {
                oldProperty.setType(pType);
            }
            oldProperty.setHasXmlElementType(true);
            // may need to generate a type info for the type
            if (aProcessor.shouldGenerateTypeInfo(pType) && aProcessor.getTypeInfo().get(pType.getQualifiedName()) == null) {
                aProcessor.buildNewTypeInfo(new JavaClass[] { pType });
            }
        }
        return oldProperty;
    }
    
    /**
     * Handle xml-any-attribute.
     * 
     * @param xmlAnyAttribute
     * @param oldProperty
     * @param tInfo
     * @param javaType
     * @return
     */
    private Property processXmlAnyAttribute(XmlAnyAttribute xmlAnyAttribute, Property oldProperty, TypeInfo tInfo, JavaType javaType) {
        // if oldProperty is already an Any (via @XmlAnyAttribute annotation)
        // there's nothing to do
        if (oldProperty.isAnyAttribute()) {
            return oldProperty;
        }
        
        // type has to be a java.util.Map
        if (!aProcessor.getHelper().isMapType(oldProperty.getType())) {
            if (oldProperty.getType().getClass().getName().contains("OXMJavaClassImpl")) {
                JavaClass pType = jModelInput.getJavaModel().getClass("java.util.Map");
                oldProperty.setType(pType);
            } else {
                throw org.eclipse.persistence.exceptions.JAXBException.anyAttributeOnNonMap(oldProperty.getPropertyName());
            }
        }

        // reset any existing values
        resetProperty(oldProperty, tInfo);

        oldProperty.setIsAnyAttribute(true);
        tInfo.setAnyAttributePropertyName(oldProperty.getPropertyName());

        // handle XmlPath
        if (xmlAnyAttribute.getXmlPath() != null) {
            oldProperty.setXmlPath(xmlAnyAttribute.getXmlPath());
        }
        // handle get/set methods
        if (xmlAnyAttribute.getXmlAccessMethods() != null) {
            oldProperty.setMethodProperty(true);
            oldProperty.setGetMethodName(xmlAnyAttribute.getXmlAccessMethods().getGetMethod());
            oldProperty.setSetMethodName(xmlAnyAttribute.getXmlAccessMethods().getSetMethod());
        }
        // handle read-only
        if (xmlAnyAttribute.isSetReadOnly()) {
            oldProperty.setReadOnly(xmlAnyAttribute.isReadOnly());
        }
        // handle write-only
        if (xmlAnyAttribute.isSetWriteOnly()) {
            oldProperty.setWriteOnly(xmlAnyAttribute.isWriteOnly());
        }
        // set user-defined properties
        if (xmlAnyAttribute.getXmlProperties() != null  && xmlAnyAttribute.getXmlProperties().getXmlProperty().size() > 0) {
            oldProperty.setUserProperties(createUserPropertyMap(xmlAnyAttribute.getXmlProperties().getXmlProperty()));
        }
        // check for container type
        if (!xmlAnyAttribute.getContainerType().equals(DEFAULT)) {
            setContainerType(oldProperty, xmlAnyAttribute.getContainerType());
        }
        return oldProperty;
    }

    /**
     * Handle xml-any-element. If the property was annotated with @XmlAnyElement
     * in code all values will be overridden.
     * 
     * @param xmlAnyElement
     * @param oldProperty
     * @param tInfo
     * @param javaType
     * @return
     */
    private Property processXmlAnyElement(XmlAnyElement xmlAnyElement, Property oldProperty, TypeInfo tInfo, JavaType javaType) {
  
    	processObjectFactory(tInfo);
    	
        // reset any existing values
        resetProperty(oldProperty, tInfo);

        // set xml-any-element specific properties
        oldProperty.setIsAny(true);
        oldProperty.setDomHandlerClassName(xmlAnyElement.getDomHandler());
        oldProperty.setLax(xmlAnyElement.isLax());
        oldProperty.setMixedContent(xmlAnyElement.isXmlMixed());
        oldProperty.setXmlJavaTypeAdapter(xmlAnyElement.getXmlJavaTypeAdapter());

        // update TypeInfo
        tInfo.setMixed(xmlAnyElement.isXmlMixed());
        tInfo.setAnyElementPropertyName(oldProperty.getPropertyName());

        // handle XmlPath
        if (xmlAnyElement.getXmlPath() != null) {
            oldProperty.setXmlPath(xmlAnyElement.getXmlPath());
        }
        // handle get/set methods
        if (xmlAnyElement.getXmlAccessMethods() != null) {
            oldProperty.setMethodProperty(true);
            oldProperty.setGetMethodName(xmlAnyElement.getXmlAccessMethods().getGetMethod());
            oldProperty.setSetMethodName(xmlAnyElement.getXmlAccessMethods().getSetMethod());
        }
        // handle read-only
        if (xmlAnyElement.isSetReadOnly()) {
            oldProperty.setReadOnly(xmlAnyElement.isReadOnly());
        }
        // handle write-only
        if (xmlAnyElement.isSetWriteOnly()) {
            oldProperty.setWriteOnly(xmlAnyElement.isWriteOnly());
        }
        // set user-defined properties
        if (xmlAnyElement.getXmlProperties() != null  && xmlAnyElement.getXmlProperties().getXmlProperty().size() > 0) {
            oldProperty.setUserProperties(createUserPropertyMap(xmlAnyElement.getXmlProperties().getXmlProperty()));
        }
        // check for container type
        if (!xmlAnyElement.getContainerType().equals(DEFAULT)) {
            setContainerType(oldProperty, xmlAnyElement.getContainerType());
        }
        // check for xml-element-refs
        if (xmlAnyElement.getXmlElementRefs() != null) {
            oldProperty.setXmlElementRefs(xmlAnyElement.getXmlElementRefs().getXmlElementRef());
            oldProperty.setIsReference(true);
            
            boolean required = true;
            for (XmlElementRef eltRef : xmlAnyElement.getXmlElementRefs().getXmlElementRef()) {
                required = required && eltRef.isRequired();
            }
            oldProperty.setIsRequired(required);
            if (xmlAnyElement.getXmlElementRefs().isSetXmlMixed()) {
                oldProperty.setMixedContent(xmlAnyElement.getXmlElementRefs().isXmlMixed());
            }
        }
        
        return oldProperty;
    }

    /**
     * XmlAttribute override will completely replace the existing values.
     * 
     * @param xmlAttribute
     * @param oldProperty
     * @param nsInfo
     * @return
     */
    private Property processXmlAttribute(XmlAttribute xmlAttribute, Property oldProperty, TypeInfo typeInfo, NamespaceInfo nsInfo, JavaType javaType) {
        // reset any existing values
        resetProperty(oldProperty, typeInfo);

        // handle xml-id
        if (xmlAttribute.isXmlId()) {
            oldProperty.setIsXmlId(true);
            typeInfo.setIDProperty(oldProperty);
        } else {
            // account for XmlID un-set via XML
            if (typeInfo.getIDProperty() != null && typeInfo.getIDProperty().getPropertyName().equals(oldProperty.getPropertyName())) {
                typeInfo.setIDProperty(null);
            }
        }

        // handle xml-idref
        oldProperty.setIsXmlIdRef(xmlAttribute.isXmlIdref());

        // handle xml-key
        if (xmlAttribute.isXmlKey()) {
            typeInfo.addXmlKeyProperty(oldProperty);
        }

        // set isAttribute
        oldProperty.setIsAttribute(true);

        // set xml-inline-binary-data
        oldProperty.setisInlineBinaryData(xmlAttribute.isXmlInlineBinaryData());

        String name;
        String namespace;
        
        // handle XmlPath
        // if xml-path is set, we ignore name/namespace
        if (xmlAttribute.getXmlPath() != null) {
            oldProperty.setXmlPath(xmlAttribute.getXmlPath());
            name = getNameFromXPath(xmlAttribute.getXmlPath(), oldProperty.getPropertyName(), true);
            namespace = DEFAULT;
        } else {
            // no xml-path, so use name/namespace from xml-attribute
            name = xmlAttribute.getName();
            namespace = xmlAttribute.getNamespace();
        }

        if (javaType.getXmlType() != null && javaType.getXmlType().getNamespace() != null  &&
                (xmlAttribute.getNamespace() != null && xmlAttribute.getNamespace().equals(DEFAULT))) {
            // Inherit type-level namespace if there is one
            namespace = javaType.getXmlType().getNamespace();
        }

        // set schema name
        QName qName;
        if (name.equals(DEFAULT)) {
            name = typeInfo.getXmlNameTransformer().transformAttributeName(oldProperty.getPropertyName());
        }
        if (namespace.equals(DEFAULT)) {
            if (nsInfo.isAttributeFormQualified()) {
                qName = new QName(nsInfo.getNamespace(), name);
            } else {
                qName = new QName(name);
            }
        } else {
            qName = new QName(namespace, name);
        }
        oldProperty.setSchemaName(qName);

        // check for container type
        if (!xmlAttribute.getContainerType().equals(DEFAULT)) {
            setContainerType(oldProperty, xmlAttribute.getContainerType());
        }
        // set type
        if (!xmlAttribute.getType().equals(DEFAULT)) {
            JavaClass pType = jModelInput.getJavaModel().getClass(xmlAttribute.getType());
            if (aProcessor.getHelper().isCollectionType(oldProperty.getType())) {
                oldProperty.setGenericType(pType);
            } else {
                oldProperty.setType(pType);
            }
            oldProperty.setHasXmlElementType(true);
            // may need to generate a type info for the type
            if (aProcessor.shouldGenerateTypeInfo(pType) && aProcessor.getTypeInfo().get(pType.getQualifiedName()) == null) {
                aProcessor.buildNewTypeInfo(new JavaClass[] { pType });
            }
        }
        
        reapplyPackageAndClassAdapters(oldProperty, typeInfo);
        // handle XmlJavaTypeAdapter
        if (xmlAttribute.getXmlJavaTypeAdapter() != null) {
            oldProperty.setXmlJavaTypeAdapter(xmlAttribute.getXmlJavaTypeAdapter());
        }

        // handle required - for required, if set by user than true/false;  
        // if not set by user, true if property type == primitive
        if (xmlAttribute.isSetRequired()) {
            oldProperty.setIsRequired(xmlAttribute.isRequired());
        } else if (oldProperty.getActualType().isPrimitive()) {
            oldProperty.setIsRequired(true);
        }
        
        // handle xml-mime-type
        if (xmlAttribute.getXmlMimeType() != null) {
            oldProperty.setMimeType(xmlAttribute.getXmlMimeType());
        }

        // handle xml-attachment-ref
        if (xmlAttribute.isXmlAttachmentRef()) {
            oldProperty.setIsSwaAttachmentRef(true);
            oldProperty.setSchemaType(Constants.SWA_REF_QNAME);
        } else if (aProcessor.isMtomAttachment(oldProperty)) {
            oldProperty.setIsMtomAttachment(true);
            oldProperty.setSchemaType(Constants.BASE_64_BINARY_QNAME);
        }

        // handle xml-schema-type
        if (xmlAttribute.getXmlSchemaType() != null) {
            oldProperty.setSchemaType(new QName(xmlAttribute.getXmlSchemaType().getNamespace(), xmlAttribute.getXmlSchemaType().getName()));
        }
        // handle get/set methods
        if (xmlAttribute.getXmlAccessMethods() != null) {
            oldProperty.setMethodProperty(true);
            oldProperty.setGetMethodName(xmlAttribute.getXmlAccessMethods().getGetMethod());
            oldProperty.setSetMethodName(xmlAttribute.getXmlAccessMethods().getSetMethod());
        }
        // handle read-only
        if (xmlAttribute.isSetReadOnly()) {
            oldProperty.setReadOnly(xmlAttribute.isReadOnly());
        }
        // handle write-only
        if (xmlAttribute.isSetWriteOnly()) {
            oldProperty.setWriteOnly(xmlAttribute.isWriteOnly());
        }
        // handle null policy
        if (xmlAttribute.getXmlAbstractNullPolicy() != null) {
            JAXBElement jaxbElt = xmlAttribute.getXmlAbstractNullPolicy();
            oldProperty.setNullPolicy((XmlAbstractNullPolicy) jaxbElt.getValue());
        }
        // set user-defined properties
        if (xmlAttribute.getXmlProperties() != null  && xmlAttribute.getXmlProperties().getXmlProperty().size() > 0) {
            oldProperty.setUserProperties(createUserPropertyMap(xmlAttribute.getXmlProperties().getXmlProperty()));
        }
        return oldProperty;
    }

    /**
     * XmlElement override will completely replace the existing values.
     * 
     * @param xmlElement
     * @param oldProperty
     * @param typeInfo
     * @param nsInfo
     * @return
     */
    private Property processXmlElement(XmlElement xmlElement, Property oldProperty, TypeInfo typeInfo, NamespaceInfo nsInfo, JavaType javaType) {
        // reset any existing values
        resetProperty(oldProperty, typeInfo);

        if (xmlElement.getXmlMap() != null) {
            processXmlMap(xmlElement.getXmlMap(), oldProperty);
        }

        if (xmlElement.isXmlLocation()) {
            if (!aProcessor.getHelper().getJavaClass(Constants.LOCATOR_CLASS).isAssignableFrom(oldProperty.getType())) {
                throw JAXBException.invalidXmlLocation(oldProperty.getPropertyName(), oldProperty.getType().getName());
            }
            oldProperty.setXmlLocation(true);
        }

        // handle xml-id
        if (xmlElement.isXmlId()) {
            oldProperty.setIsXmlId(true);
            typeInfo.setIDProperty(oldProperty);
        } else {
            // account for XmlID un-set via XML
            if (typeInfo.getIDProperty() != null && typeInfo.getIDProperty().getPropertyName().equals(oldProperty.getPropertyName())) {
                typeInfo.setIDProperty(null);
            }
        }
        
        if(xmlElement.getXmlInverseReference() != null){
        	String mappedBy = xmlElement.getXmlInverseReference().getMappedBy();
        	oldProperty.setInverseReference(true, true);
        	oldProperty.setInverseReferencePropertyName(mappedBy);
        }

        // handle xml-idref
        oldProperty.setIsXmlIdRef(xmlElement.isXmlIdref());

        // handle xml-key
        if (xmlElement.isXmlKey()) {
            typeInfo.addXmlKeyProperty(oldProperty);
        }

        // set required
        oldProperty.setIsRequired(xmlElement.isRequired());

        // set xml-inline-binary-data
        oldProperty.setisInlineBinaryData(xmlElement.isXmlInlineBinaryData());

        // set nillable
        oldProperty.setNillable(xmlElement.isNillable());

        // set defaultValue
        if (xmlElement.getDefaultValue().equals("\u0000")) {
            oldProperty.setDefaultValue(null);
        } else {
            oldProperty.setDefaultValue(xmlElement.getDefaultValue());
        }

        String name;
        String namespace;

        // handle XmlPath / XmlElementWrapper
        // if xml-path is set, we ignore xml-element-wrapper, as well as name/namespace on xml-element
        if (xmlElement.getXmlPath() != null) {
            oldProperty.setXmlPath(xmlElement.getXmlPath());
            name = getNameFromXPath(xmlElement.getXmlPath(), oldProperty.getPropertyName(), false);           
            namespace = DEFAULT;
        } else {
            // no xml-path, so use name/namespace from xml-element, and process wrapper
            name = xmlElement.getName();
            namespace = xmlElement.getNamespace();
            XmlElementWrapper xmlElementWrapper = xmlElement.getXmlElementWrapper();
            if (xmlElementWrapper != null) {
                if (DEFAULT.equals(xmlElementWrapper.getName())) {
                    xmlElementWrapper.setName(typeInfo.getXmlNameTransformer().transformElementName(oldProperty.getPropertyName()));
                }
                oldProperty.setXmlElementWrapper(xmlElement.getXmlElementWrapper());
                if(oldProperty.isMap()){
                	name = xmlElement.getXmlElementWrapper().getName();
                    namespace = xmlElement.getXmlElementWrapper().getNamespace();
                }
            }
        }

        if (javaType.getXmlType() != null && javaType.getXmlType().getNamespace() != null  &&
                (xmlElement.getNamespace() != null && xmlElement.getNamespace().equals(DEFAULT))) {
            // Inherit type-level namespace if there is one
            namespace = javaType.getXmlType().getNamespace();
        }

        // set schema name
        QName qName;
        if (name.equals(DEFAULT)) {
            name = typeInfo.getXmlNameTransformer().transformElementName(oldProperty.getPropertyName());
        }
        if (namespace.equals(DEFAULT)) {
            if (nsInfo.isElementFormQualified()) {
                qName = new QName(nsInfo.getNamespace(), name);
            } else {
                qName = new QName(name);
            }
        } else {
            qName = new QName(namespace, name);
        }
        oldProperty.setSchemaName(qName);

        // check for container type
        if (!xmlElement.getContainerType().equals(DEFAULT)) {
            setContainerType(oldProperty, xmlElement.getContainerType());
        }
        // set type
        if (xmlElement.getType().equals("javax.xml.bind.annotation.XmlElement.DEFAULT")) {
            // if xmlElement has no type, and the property type was set via
            // @XmlElement, reset it to the original value
            if (oldProperty.isXmlElementType()) {
                oldProperty.setType(oldProperty.getOriginalType());
            }
        } else if (xmlElement.getXmlMap() != null) {
            getLogger().logWarning(JAXBMetadataLogger.INVALID_TYPE_ON_MAP, new Object[] { xmlElement.getName() });
        } else {
            JavaClass pType = jModelInput.getJavaModel().getClass(xmlElement.getType());
            if(aProcessor.getHelper().isCollectionType(oldProperty.getType())) {
                oldProperty.setGenericType(pType);
            } else {
                oldProperty.setType(pType);
            }
            oldProperty.setHasXmlElementType(true);
            // may need to generate a type info for the type
            if (aProcessor.shouldGenerateTypeInfo(pType) && aProcessor.getTypeInfo().get(pType.getQualifiedName()) == null) {
                aProcessor.buildNewTypeInfo(new JavaClass[] { pType });
            }
        }

        reapplyPackageAndClassAdapters(oldProperty, typeInfo);
        // handle XmlJavaTypeAdapter
        if (xmlElement.getXmlJavaTypeAdapter() != null) {
            try {
                oldProperty.setXmlJavaTypeAdapter(xmlElement.getXmlJavaTypeAdapter());
            } catch(JAXBException e) {
                throw JAXBException.invalidPropertyAdapterClass(xmlElement.getXmlJavaTypeAdapter().getValue(), xmlElement.getJavaAttribute(), javaType.getName());
            }
        }

        // for primitives we always set required, a.k.a. minOccurs="1"
        if (!oldProperty.isRequired()) {
            JavaClass ptype = oldProperty.getActualType();
            oldProperty.setIsRequired(ptype.isPrimitive() || ptype.isArray() && ptype.getComponentType().isPrimitive());
        }

        // handle xml-list
        if (xmlElement.isSetXmlList()) {
            // Make sure XmlList annotation is on a collection or array
            if (!aProcessor.getHelper().isCollectionType(oldProperty.getType()) && !oldProperty.getType().isArray()) {
                throw JAXBException.invalidList(oldProperty.getPropertyName());
            }
            oldProperty.setIsXmlList(xmlElement.isXmlList());
        }

        // handle xml-mime-type
        if (xmlElement.getXmlMimeType() != null) {
            oldProperty.setMimeType(xmlElement.getXmlMimeType());
        }

        // handle xml-attachment-ref
        if (xmlElement.isXmlAttachmentRef()) {
            oldProperty.setIsSwaAttachmentRef(true);
            oldProperty.setSchemaType(Constants.SWA_REF_QNAME);
        } else if (aProcessor.isMtomAttachment(oldProperty)) {
            oldProperty.setIsMtomAttachment(true);
            oldProperty.setSchemaType(Constants.BASE_64_BINARY_QNAME);
        }

        // handle xml-schema-type
        if (xmlElement.getXmlSchemaType() != null) {
            oldProperty.setSchemaType(new QName(xmlElement.getXmlSchemaType().getNamespace(), xmlElement.getXmlSchemaType().getName()));
        }
        // handle get/set methods
        if (xmlElement.getXmlAccessMethods() != null) {
            oldProperty.setMethodProperty(true);
            oldProperty.setGetMethodName(xmlElement.getXmlAccessMethods().getGetMethod());
            oldProperty.setSetMethodName(xmlElement.getXmlAccessMethods().getSetMethod());
        }
        // handle read-only
        if (xmlElement.isSetReadOnly()) {
            oldProperty.setReadOnly(xmlElement.isReadOnly());
        }
        // handle write-only
        if (xmlElement.isSetWriteOnly()) {
            oldProperty.setWriteOnly(xmlElement.isWriteOnly());
        }
        // handle cdata
        if (xmlElement.isSetCdata()) {
            oldProperty.setCdata(xmlElement.isCdata());
        }
        // handle null policy
        if (xmlElement.getXmlAbstractNullPolicy() != null) {
            JAXBElement jaxbElt = xmlElement.getXmlAbstractNullPolicy();
            oldProperty.setNullPolicy((XmlAbstractNullPolicy) jaxbElt.getValue());
        }
        // set user-defined properties
        if (xmlElement.getXmlProperties() != null  && xmlElement.getXmlProperties().getXmlProperty().size() > 0) {
            oldProperty.setUserProperties(createUserPropertyMap(xmlElement.getXmlProperties().getXmlProperty()));
        }
        return oldProperty;
    }

    private Property processXmlMap(XmlMap xmlMap, Property oldProperty) {
        XmlMap.Key mapKey = xmlMap.getKey();
        XmlMap.Value mapValue = xmlMap.getValue();

        if (mapKey != null && mapKey.getType() != null) {
            oldProperty.setKeyType(jModelInput.getJavaModel().getClass(mapKey.getType()));
        } else {
            oldProperty.setKeyType(jModelInput.getJavaModel().getClass(JAVA_LANG_OBJECT));
        }

        if (mapValue != null && mapValue.getType() != null) {
            oldProperty.setValueType(jModelInput.getJavaModel().getClass(mapValue.getType()));
        } else {
            oldProperty.setValueType(jModelInput.getJavaModel().getClass(JAVA_LANG_OBJECT));
        }

        return oldProperty;
    }

    /**
     * Process XmlElements.
     * 
     * The XmlElements object will be set on the property, and it will be
     * flagged as a 'choice'.
     * 
     * @param xmlElements
     * @param oldProperty
     * @param tInfo
     * @return
     */
    private Property processXmlElements(XmlElements xmlElements, Property oldProperty, TypeInfo tInfo) {
        resetProperty(oldProperty, tInfo);
        oldProperty.setChoice(true);
        oldProperty.setXmlElements(xmlElements);
        // handle idref
        oldProperty.setIsXmlIdRef(xmlElements.isXmlIdref());
        // handle XmlElementWrapper
        if (xmlElements.getXmlElementWrapper() != null) {
            oldProperty.setXmlElementWrapper(xmlElements.getXmlElementWrapper());
        }
        // handle get/set methods
        if (xmlElements.getXmlAccessMethods() != null) {
            oldProperty.setMethodProperty(true);
            oldProperty.setGetMethodName(xmlElements.getXmlAccessMethods().getGetMethod());
            oldProperty.setSetMethodName(xmlElements.getXmlAccessMethods().getSetMethod());
        }
        // handle read-only
        if (xmlElements.isSetReadOnly()) {
            oldProperty.setReadOnly(xmlElements.isReadOnly());
        }
        // handle write-only
        if (xmlElements.isSetWriteOnly()) {
            oldProperty.setWriteOnly(xmlElements.isWriteOnly());
        }
        // set user-defined properties
        if (xmlElements.getXmlProperties() != null  && xmlElements.getXmlProperties().getXmlProperty().size() > 0) {
            oldProperty.setUserProperties(createUserPropertyMap(xmlElements.getXmlProperties().getXmlProperty()));
        }
        // check for container type
        if (!xmlElements.getContainerType().equals(DEFAULT)) {
            setContainerType(oldProperty, xmlElements.getContainerType());
        }
        // check for xml-join-nodes
        if (xmlElements.hasXmlJoinNodes()) {
            // store the List of XmlJoinNodes so we can access them when
            // we process the choice elements in AnnotationsProcessor
            oldProperty.setXmlJoinNodesList(xmlElements.getXmlJoinNodes());
        }
        // handle XmlJavaTypeAdapter
        if (xmlElements.getXmlJavaTypeAdapter() != null) {
            try {
                oldProperty.setXmlJavaTypeAdapter(xmlElements.getXmlJavaTypeAdapter());
            } catch(JAXBException e) {
                throw JAXBException.invalidPropertyAdapterClass(xmlElements.getXmlJavaTypeAdapter().getValue(), xmlElements.getJavaAttribute(), tInfo.getJavaClassName());
            }
        }
        return oldProperty;
    }

    /**
     * Process an xml-element-ref.
     * 
     * @param xmlElementRef
     * @param oldProperty
     * @param info
     * @return
     */
    private Property processXmlElementRef(XmlElementRef xmlElementRef, Property oldProperty, TypeInfo info) {
    	processObjectFactory(info);
    	
    	resetProperty(oldProperty, info);

        List<XmlElementRef> eltRefs = new ArrayList<XmlElementRef>();
        eltRefs.add(xmlElementRef);
        oldProperty.setXmlElementRefs(eltRefs);
        oldProperty.setIsReference(true);
        oldProperty.setIsRequired(xmlElementRef.isRequired());
        
        // handle XmlAdapter
        if (xmlElementRef.getXmlJavaTypeAdapter() != null) {
            oldProperty.setXmlJavaTypeAdapter(xmlElementRef.getXmlJavaTypeAdapter());
        }
        // check for container type
        if (!xmlElementRef.getContainerType().equals(DEFAULT)) {
            setContainerType(oldProperty, xmlElementRef.getContainerType());
        }
        // handle XmlElementWrapper
        if (xmlElementRef.getXmlElementWrapper() != null) {
            oldProperty.setXmlElementWrapper(xmlElementRef.getXmlElementWrapper());
        }
        // set user-defined properties
        if (xmlElementRef.getXmlProperties() != null  && xmlElementRef.getXmlProperties().getXmlProperty().size() > 0) {
            oldProperty.setUserProperties(createUserPropertyMap(xmlElementRef.getXmlProperties().getXmlProperty()));
        }
        if (xmlElementRef.isSetXmlMixed()) {
            oldProperty.setMixedContent(xmlElementRef.isXmlMixed());
        }
        // handle get/set methods
        if (xmlElementRef.getXmlAccessMethods() != null) {
            oldProperty.setMethodProperty(true);
            oldProperty.setGetMethodName(xmlElementRef.getXmlAccessMethods().getGetMethod());
            oldProperty.setSetMethodName(xmlElementRef.getXmlAccessMethods().getSetMethod());
        }
        // handle read-only
        if (xmlElementRef.isSetReadOnly()) {
            oldProperty.setReadOnly(xmlElementRef.isReadOnly());
        }
        // handle write-only
        if (xmlElementRef.isSetWriteOnly()) {
            oldProperty.setWriteOnly(xmlElementRef.isWriteOnly());
        }
        return oldProperty;
    }

    /**
     * Process an xml-element-refs.
     * 
     * @param xmlElementRefs
     * @param oldProperty
     * @param info
     * @return
     */
    private Property processXmlElementRefs(XmlElementRefs xmlElementRefs, Property oldProperty, TypeInfo info) {
    	processObjectFactory(info);
    	
        resetProperty(oldProperty, info);

        List<XmlElementRef> eltRefs = new ArrayList<XmlElementRef>();
        boolean required = true;
        for (XmlElementRef eltRef : xmlElementRefs.getXmlElementRef()) {
            eltRefs.add(eltRef);
            required = required && eltRef.isRequired();
        }

        oldProperty.setXmlElementRefs(eltRefs);
        oldProperty.setIsReference(true);
        oldProperty.setIsRequired(required);
        
        // handle XmlAdapter
        if (xmlElementRefs.getXmlJavaTypeAdapter() != null) {
            oldProperty.setXmlJavaTypeAdapter(xmlElementRefs.getXmlJavaTypeAdapter());
        }
        // handle XmlElementWrapper
        if (xmlElementRefs.getXmlElementWrapper() != null) {
            oldProperty.setXmlElementWrapper(xmlElementRefs.getXmlElementWrapper());
        }
        // set user-defined properties
        if (xmlElementRefs.getXmlProperties() != null  && xmlElementRefs.getXmlProperties().getXmlProperty().size() > 0) {
            oldProperty.setUserProperties(createUserPropertyMap(xmlElementRefs.getXmlProperties().getXmlProperty()));
        }
        if (xmlElementRefs.isSetXmlMixed()) {
            oldProperty.setMixedContent(xmlElementRefs.isXmlMixed());
        }
        // handle get/set methods
        if (xmlElementRefs.getXmlAccessMethods() != null) {
            oldProperty.setMethodProperty(true);
            oldProperty.setGetMethodName(xmlElementRefs.getXmlAccessMethods().getGetMethod());
            oldProperty.setSetMethodName(xmlElementRefs.getXmlAccessMethods().getSetMethod());
        }
        // handle read-only
        if (xmlElementRefs.isSetReadOnly()) {
            oldProperty.setReadOnly(xmlElementRefs.isReadOnly());
        }
        // handle write-only
        if (xmlElementRefs.isSetWriteOnly()) {
            oldProperty.setWriteOnly(xmlElementRefs.isWriteOnly());
        }
        return oldProperty;
    }

    private Property processXmlTransient(XmlTransient xmlTransient, Property oldProperty) {
        if (xmlTransient.isXmlLocation()) {
            if (!aProcessor.getHelper().getJavaClass(Constants.LOCATOR_CLASS).isAssignableFrom(oldProperty.getType())) {
                throw JAXBException.invalidXmlLocation(oldProperty.getPropertyName(), oldProperty.getType().getName());
            }
            oldProperty.setXmlLocation(true);
        }
        oldProperty.setTransient(true);
        return oldProperty;
    }

    private Property processXmlValue(XmlValue xmlValue, Property oldProperty, TypeInfo info, JavaType javaType) {
        // reset any existing values
        resetProperty(oldProperty, info);

        oldProperty.setIsXmlValue(true);
        info.setXmlValueProperty(oldProperty);
        
        // handle get/set methods
        if (xmlValue.getXmlAccessMethods() != null) {
            oldProperty.setMethodProperty(true);
            oldProperty.setGetMethodName(xmlValue.getXmlAccessMethods().getGetMethod());
            oldProperty.setSetMethodName(xmlValue.getXmlAccessMethods().getSetMethod());
        }
        // check for container type
        if (!xmlValue.getContainerType().equals(DEFAULT)) {
            setContainerType(oldProperty, xmlValue.getContainerType());
        }
        // set type
        if (!xmlValue.getType().equals(DEFAULT)) {
            JavaClass pType = jModelInput.getJavaModel().getClass(xmlValue.getType());
            if (aProcessor.getHelper().isCollectionType(oldProperty.getType())) {
                oldProperty.setGenericType(pType);
            } else {
                oldProperty.setType(pType);
            }
            oldProperty.setHasXmlElementType(true);
            // may need to generate a type info for the type
            if (aProcessor.shouldGenerateTypeInfo(pType) && aProcessor.getTypeInfo().get(pType.getQualifiedName()) == null) {
                aProcessor.buildNewTypeInfo(new JavaClass[] { pType });
            }
        }
        
        reapplyPackageAndClassAdapters(oldProperty, info);
        // handle XmlJavaTypeAdapter
        if (xmlValue.getXmlJavaTypeAdapter() != null) {
            try {
                oldProperty.setXmlJavaTypeAdapter(xmlValue.getXmlJavaTypeAdapter());
            } catch(JAXBException e) {
                throw JAXBException.invalidPropertyAdapterClass(xmlValue.getXmlJavaTypeAdapter().getValue(), xmlValue.getJavaAttribute(), javaType.getName());
            }
        }
        // handle read-only
        if (xmlValue.isSetReadOnly()) {
            oldProperty.setReadOnly(xmlValue.isReadOnly());
        }
        // handle write-only
        if (xmlValue.isSetWriteOnly()) {
            oldProperty.setWriteOnly(xmlValue.isWriteOnly());
        }
        // handle cdata
        if (xmlValue.isSetCdata()) {
            oldProperty.setCdata(xmlValue.isCdata());
        }
        // handle null policy
        if (xmlValue.getXmlAbstractNullPolicy() != null) {
            JAXBElement jaxbElt = xmlValue.getXmlAbstractNullPolicy();
            oldProperty.setNullPolicy((XmlAbstractNullPolicy) jaxbElt.getValue());
        }
        // set user-defined properties
        if (xmlValue.getXmlProperties() != null  && xmlValue.getXmlProperties().getXmlProperty().size() > 0) {
            oldProperty.setUserProperties(createUserPropertyMap(xmlValue.getXmlProperties().getXmlProperty()));
        }
        return oldProperty;
    }

    /**
     * Process an XmlSchema. This involves creating a NamespaceInfo instance and
     * populating it based on the given XmlSchema.
     * 
     * @param xmlBindings
     * @param packageName
     * @see NamespaceInfo
     * @see AnnotationsProcessor
     * @return newly created namespace info, or null if schema is null
     */
    private NamespaceInfo processXmlSchema(XmlBindings xmlBindings, String packageName) {
        XmlSchema schema = xmlBindings.getXmlSchema();
        if (schema == null) {
            return null;
        }
        // create NamespaceInfo
        NamespaceInfo nsInfo = this.aProcessor.findInfoForNamespace(schema.getNamespace());
        if(nsInfo == null) {
            nsInfo = new NamespaceInfo();
        }
        // process XmlSchema
        XmlNsForm form = schema.getAttributeFormDefault();
        nsInfo.setAttributeFormQualified(form.equals(form.QUALIFIED));
        form = schema.getElementFormDefault();
        nsInfo.setElementFormQualified(form.equals(form.QUALIFIED));

        
        if (!nsInfo.isElementFormQualified() || nsInfo.isAttributeFormQualified()) {
            aProcessor.setDefaultNamespaceAllowed(false);
        }
        
        // make sure defaults are set, not null
        nsInfo.setLocation(schema.getLocation() == null ? GENERATE : schema.getLocation());
        String namespace = schema.getNamespace();
        if(namespace == null) {
            namespace = this.aProcessor.getDefaultTargetNamespace();
        }
        nsInfo.setNamespace(namespace == null ? "" : schema.getNamespace());
        NamespaceResolver nsr = new NamespaceResolver();
        // process XmlNs
        for (XmlNs xmlns : schema.getXmlNs()) {
            nsr.put(xmlns.getPrefix(), xmlns.getNamespaceUri());
        }
        nsInfo.setNamespaceResolver(nsr);
        return nsInfo;
    }
    
    /**
     * Process an XmlTransformation. The info in the XmlTransformation will be
     * used to generate an XmlTransformationMapping in MappingsGenerator.
     * 
     * @param xmlTransformation
     * @param oldProperty
     * @param tInfo
     */
    private Property processXmlTransformation(XmlTransformation xmlTransformation, Property oldProperty, TypeInfo tInfo) {
        // reset any existing values
        resetProperty(oldProperty, tInfo);

        oldProperty.setIsXmlTransformation(true);
        oldProperty.setXmlTransformation(xmlTransformation);
        
        // handle get/set methods
        if (xmlTransformation.getXmlAccessMethods() != null) {
            oldProperty.setMethodProperty(true);
            oldProperty.setGetMethodName(xmlTransformation.getXmlAccessMethods().getGetMethod());
            oldProperty.setSetMethodName(xmlTransformation.getXmlAccessMethods().getSetMethod());
        }
        // set user-defined properties
        if (xmlTransformation.getXmlProperties() != null  && xmlTransformation.getXmlProperties().getXmlProperty().size() > 0) {
            oldProperty.setUserProperties(createUserPropertyMap(xmlTransformation.getXmlProperties().getXmlProperty()));
        }
        aProcessor.getReferencedByTransformer().add(oldProperty.getType().getName());
        return oldProperty;
    }

    /**
     * Process XmlJoinNodes.  This method sets the XmlJoinNodes instance on the Property
     * for use in MappingsGen and SchemaGen.
     * 
     * @param xmlJoinNodes
     * @param oldProperty
     * @return
     */
    private Property processXmlJoinNodes(XmlJoinNodes xmlJoinNodes, Property oldProperty, TypeInfo typeInfo) {
        // reset any existing values
        resetProperty(oldProperty, typeInfo);

        oldProperty.setXmlJoinNodes(xmlJoinNodes);
        
        // check for container type
        if (!xmlJoinNodes.getContainerType().equals(DEFAULT)) {
            setContainerType(oldProperty, xmlJoinNodes.getContainerType());
        }
        // set type
        if (!xmlJoinNodes.getType().equals(DEFAULT)) {
            JavaClass pType = jModelInput.getJavaModel().getClass(xmlJoinNodes.getType());
            if (aProcessor.getHelper().isCollectionType(oldProperty.getType())) {
                oldProperty.setGenericType(pType);
            } else {
                oldProperty.setType(pType);
            }
            oldProperty.setHasXmlElementType(true);
            // may need to generate a type info for the type
            if (aProcessor.shouldGenerateTypeInfo(pType) && aProcessor.getTypeInfo().get(pType.getQualifiedName()) == null) {
                aProcessor.buildNewTypeInfo(new JavaClass[] { pType });
            }
        }
        return oldProperty;
    }
    
    /**
     * Convenience method for building a Map of package to classes.
     * 
     * @return
     */
    private Map<String, ArrayList<JavaClass>> buildPackageToJavaClassMap() {
        Map<String, ArrayList<JavaClass>> theMap = new HashMap<String, ArrayList<JavaClass>>();
        Map<String, ArrayList<JavaClass>> xmlBindingsMap = new HashMap<String, ArrayList<JavaClass>>();

        XmlBindings xmlBindings;
        for (String packageName : xmlBindingMap.keySet()) {
            xmlBindings = xmlBindingMap.get(packageName);
            ArrayList classes = new ArrayList<JavaClass>();
            // add binding classes - the Java Model will be used to get a
            // JavaClass via class name
            JavaTypes jTypes = xmlBindings.getJavaTypes();
            if (jTypes != null) {
                for (JavaType javaType : jTypes.getJavaType()) {
                    addClassToList(classes, javaType.getName(), packageName);
                }
            }

            // add any enum types to the class list
            XmlEnums xmlEnums = xmlBindings.getXmlEnums();
            if (xmlEnums != null) {
                for (XmlEnum xmlEnum : xmlEnums.getXmlEnum()) {                  
                    addClassToList(classes, xmlEnum.getJavaEnum(), packageName);
                }
            }

            theMap.put(packageName, classes);
            xmlBindingsMap.put(packageName, new ArrayList(classes));
        }

        // add any other classes that aren't declared via external metadata
        for (JavaClass jClass : jModelInput.getJavaClasses()) {
            // need to verify that the class isn't already in the bindings file
            // list
            String pkg = jClass.getPackageName();
            ArrayList<JavaClass> existingXmlBindingsClasses = xmlBindingsMap.get(pkg);
            ArrayList<JavaClass> allExistingClasses = theMap.get(pkg);
            if (existingXmlBindingsClasses != null) {
                if (!classExistsInArray(jClass, existingXmlBindingsClasses)) {
                    allExistingClasses.add(jClass);
                }
            } else {
                if (allExistingClasses != null) {
                    allExistingClasses.add(jClass);
                } else {
                    ArrayList classes = new ArrayList<JavaClass>();
                    classes.add(jClass);
                    theMap.put(pkg, classes);
                }
            }
        }

        return theMap;
    }

    
    private void addClassToList(List classes, String name, String packageName){    
        JavaClass nextClass = jModelInput.getJavaModel().getClass(getQualifiedJavaTypeName(name, packageName));
        String nextPackageName = nextClass.getPackageName();
        if(nextPackageName == null || !nextPackageName.equals(packageName)){
            throw JAXBException.javaTypeNotAllowedInBindingsFile(nextPackageName, packageName);
        }
        classes.add(nextClass);
    }
    /**
     * Lazy load the metadata logger.
     * 
     * @return
     */
    private JAXBMetadataLogger getLogger() {
        if (logger == null) {
            logger = new JAXBMetadataLogger();
        }
        return logger;
    }

    /**
     * Convenience method to determine if a class exists in a given ArrayList.
     * The classes are compared via equals() method.
     */
    public boolean classExistsInArray(JavaClass theClass, ArrayList<JavaClass> existingClasses) {
        return aProcessor.getHelper().classExistsInArray(theClass, existingClasses);
    }

    /**
     * Convenience method for resetting a number of properties on a given
     * property.
     * 
     * @param oldProperty
     * @return
     */
    private Property resetProperty(Property oldProperty, TypeInfo tInfo) {
        oldProperty.setIsAttribute(false);
        oldProperty.setHasXmlElementType(false);
        oldProperty.setIsRequired(false);
        oldProperty.setIsXmlList(false);
        oldProperty.setXmlJavaTypeAdapter(null);
        oldProperty.setInverseReferencePropertyName(null);
        oldProperty.setDefaultValue(null);
        oldProperty.setDomHandlerClassName(null);
        oldProperty.setIsSwaAttachmentRef(false);
        oldProperty.setIsXmlIdRef(false);
        oldProperty.setIsXmlTransformation(false);
        oldProperty.setXmlElementWrapper(null);
        oldProperty.setLax(false);
        oldProperty.setNillable(false);
        oldProperty.setMixedContent(false);
        oldProperty.setMimeType(null);
        oldProperty.setTransient(false);
        oldProperty.setChoice(false);
        oldProperty.setIsReference(false);
        oldProperty.setXmlPath(null);
        oldProperty.setReadOnly(false);
        oldProperty.setWriteOnly(false);
        oldProperty.setCdata(false);
        oldProperty.setNullPolicy(null);
        oldProperty.setUserProperties(null);
        oldProperty.setGetMethodName(oldProperty.getOriginalGetMethodName());
        oldProperty.setSetMethodName(oldProperty.getOriginalSetMethodName());
        oldProperty.setXmlTransformation(null);
        oldProperty.setXmlJoinNodes(null);
        if (oldProperty.getGetMethodName() == null && oldProperty.getSetMethodName() == null) {
            oldProperty.setMethodProperty(false);
        }
        
        oldProperty.setIsSwaAttachmentRef(false);
        oldProperty.setIsMtomAttachment(false);
        oldProperty.setSchemaType(null);

        unsetXmlElementRefs(oldProperty, tInfo);
        unsetXmlElements(oldProperty);
        unsetXmlAnyAttribute(oldProperty, tInfo);
        unsetXmlAnyElement(oldProperty, tInfo);
        unsetXmlValue(oldProperty, tInfo);
        unsetXmlID(oldProperty, tInfo);
        unsetXmlKey(oldProperty, tInfo);
        return oldProperty;
    }

    /**
     * Ensure that a given property is not set as an xml-id.
     * 
     * @param oldProperty
     * @param tInfo
     */
    private void unsetXmlID(Property oldProperty, TypeInfo tInfo) {
        oldProperty.setIsXmlId(false);
        if (tInfo.isIDSet() && tInfo.getIDProperty().getPropertyName().equals(oldProperty.getPropertyName())) {
            tInfo.setIDProperty(null);
        }
    }
    
    /**
     * Ensure that a given property is not set as an xml-key.
     * 
     * @param oldProperty
     * @param tInfo
     */
    private void unsetXmlKey(Property oldProperty, TypeInfo tInfo) {
        if (tInfo.hasXmlKeyProperties()) {
            Property propToRemove = null;
            for (Property prop : tInfo.getXmlKeyProperties()) {
                if (prop.getPropertyName().equals(oldProperty.getPropertyName())) {
                    propToRemove = prop;
                }
            }
            if (propToRemove != null) {
                tInfo.getXmlKeyProperties().remove(propToRemove);
            }
        }
    }
    
    /**
     * Ensure that a given property is not set as an xml-element-refs.
     * 
     * @param oldProperty
     * @param tInfo
     */
    private void unsetXmlElementRefs(Property oldProperty, TypeInfo tInfo) {
        if (tInfo.hasElementRefs() && tInfo.getElementRefsPropName().equals(oldProperty.getPropertyName())) {
            tInfo.setElementRefsPropertyName(null);
        }
    }

    /**
     * Ensure that a given property is not set as an xml-elements.
     * 
     * @param oldProperty
     */
    private void unsetXmlElements(Property oldProperty) {
        oldProperty.setXmlElements(null);
        oldProperty.setChoiceProperties(null);
    }

    /**
     * Ensure that a given property is not set as an xml-any-attribute.
     * 
     * @param oldProperty
     * @param tInfo
     */
    private void unsetXmlAnyAttribute(Property oldProperty, TypeInfo tInfo) {
        oldProperty.setIsAnyAttribute(false);
        if (tInfo.isSetAnyAttributePropertyName() && tInfo.getAnyAttributePropertyName().equals(oldProperty.getPropertyName())) {
            tInfo.setAnyAttributePropertyName(null);
        }
    }

    /**
     * Ensure that a given property is not set as an xml-any-element.
     * 
     * @param oldProperty
     * @param tInfo
     */
    private void unsetXmlAnyElement(Property oldProperty, TypeInfo tInfo) {
        oldProperty.setIsAny(false);
        if (tInfo.isSetAnyElementPropertyName() && tInfo.getAnyElementPropertyName().equals(oldProperty.getPropertyName())) {
            tInfo.setAnyElementPropertyName(null);
        }
    }

    /**
     * Ensure that a given property is not set as an xml-value.
     * 
     * @param oldProperty
     * @param tInfo
     */
    private void unsetXmlValue(Property oldProperty, TypeInfo tInfo) {
        oldProperty.setIsXmlValue(false);
        if (tInfo.isSetXmlValueProperty() && tInfo.getXmlValueProperty().getPropertyName().equals(oldProperty.getPropertyName())) {
            tInfo.setXmlValueProperty(null);
        }
    }

    /**
     * Convenience method that returns the field name for a given xml-path.  This method
     * would typically be called when building a QName to set as the 'SchemaName' on
     * a Property.
     *   
     * Examples:
     * - returns 'id' for xml-path '@id'
     * - returns 'managerId' for xml-path 'projects/prj:project/@prj:managerId'
     * - returns 'first-name' for xml-path 'info/personal-info/first-name/text()'
     * - returns 'project' for xml-path 'projects/prj:project/text()'
     * - returns 'data' for xml-path 'pieces-of-data/data[1]/text()'
     * 
     * @param xpath
     * @param propertyName
     * @param isAttribute
     * @return
     */
    public static String getNameFromXPath(String xpath, String propertyName, boolean isAttribute) {
        // handle self mapping
        if (xpath.equals(SELF)) {
            return propertyName;
        }
        
        String name;
        String path;
        
        // may need to strip off '/text()'
        int idx = xpath.indexOf(SLASH + Constants.TEXT);
        if (idx >= 0) {
            path = xpath.substring(0, idx);
        } else {
            path = xpath;
        }
        
        idx = path.lastIndexOf(SLASH);
        if (idx >= 0 && path.length() > 1) {
            name = path.substring(idx+1);
            // may have a prefix
            StringTokenizer stok = new StringTokenizer(name, COLON);
            if (stok.countTokens() == 2) {
                // first token is prefix
                stok.nextToken();
                // second token is the field name
                name = stok.nextToken();
            }
        } else {
            name = path;
        }
        // may need to strip off '@'
        if (isAttribute) {
            idx = name.indexOf(Constants.ATTRIBUTE);
            if (idx >= 0 && name.length() > 1) {
                name = name.substring(idx+1);
            }
        } else {
            // may need to strip of positional info
            idx = name.indexOf(OPEN_BRACKET);
            if (idx != -1) {
                name = name.substring(0, idx);
            }
        }
        return name;
    }
    
    private void processObjectFactory(TypeInfo tInfo){
      	int index = tInfo.getJavaClassName().lastIndexOf(".");
      	if(index > -1){
    	    String objectFactoryClassName = tInfo.getJavaClassName().substring(0, index) + ".ObjectFactory";    	
    	    aProcessor.findAndProcessObjectFactory(objectFactoryClassName);
      	}else{
            aProcessor.findAndProcessObjectFactory("ObjectFactory");
      	}
    }
    
    
    /**
     * Return a Map of user-defined properties.  Typically the key will 
     * be a String (property name) and the value a String or some 
     * other simple type that was converted by ConversionManager,
     * i.e. numerical, boolean, temporal.
     * 
     * @param propList
     * @return
     */
    private Map createUserPropertyMap(List<XmlProperty> propList) {
        return mergeUserPropertyMap(propList, new HashMap());
    }

    /**
     * Return a Map of user-defined properties. The List of properties (from
     * xml) will be merged with the given Map (from annotations).  In the 
     * case of a conflict, xml will win.
     * 
     * Note that this intended to be used when processing type-level user
     * properties, as at the property-level, xml completely replaces any
     * properties set via annotation.
     * 
     * Typically the key will be a String (property name) and the value a 
     * String or some other simple type that was converted by 
     * ConversionManager, i.e. numerical, boolean, temporal.
     * 
     * @param propList
     * @return
     */
    private Map mergeUserPropertyMap(List<XmlProperty> propList, Map existingMap) {
        Map propMap = existingMap;
        for (XmlProperty prop : propList) {
            Object pvalue = prop.getValue();
            if (prop.isSetValueType()) {
                pvalue = XMLConversionManager.getDefaultXMLManager().convertObject(
                        prop.getValue(), XMLConversionManager.getDefaultXMLManager().convertClassNameToClass(prop.getValueType()));
            }
            propMap.put(prop.getName(), pvalue);
        }
        return propMap;
    }

    /**
     * Convenience method for setting the container class on a given property.
     * The generic type will be overwritten, and could be incorrect after
     * doing so, so the original generic type will be retrieved and set after
     * the call to set the container type.
     * 
     * @param property
     * @param containerClassName
     */
    private void setContainerType(Property property, String containerClassName) {
        // store the original generic type as the call to setType will overwrite it
        JavaClass genericType = property.getGenericType();
        // set the type to the container-type value
        property.setType(jModelInput.getJavaModel().getClass(containerClassName));
        // set the original generic type
        property.setGenericType(genericType);
    }
    
    /**
     * This method checks for class and package level adapters after the type of a property has been set.
     * @param prop the property that needs to be updated 
     * @param owningInfo the typeInfo that represents the owner of this property.
     */
    public void reapplyPackageAndClassAdapters(Property prop, TypeInfo owningInfo) {
        if(prop.getXmlJavaTypeAdapter() != null) {
            //if there's already a property level adapter, don't apply package/class level
            return;
        }
        JavaClass type = prop.getActualType();
        //if a class level adapter is present on the target class, set it
        TypeInfo targetInfo = aProcessor.getTypeInfo().get(type.getQualifiedName());
        if(targetInfo != null) {
            if(targetInfo.getXmlJavaTypeAdapter() != null) {
                prop.setXmlJavaTypeAdapter(targetInfo.getXmlJavaTypeAdapter());
            }
        }

        //check for package level adapter. Don't overwrite class level
        if(owningInfo.hasPackageLevelAdaptersByClass()) {
            JavaClass packageLevelAdapter = owningInfo.getPackageLevelAdaptersByClass().get(type.getQualifiedName()); 
            if(packageLevelAdapter != null && prop.getXmlJavaTypeAdapter() == null) {
                org.eclipse.persistence.jaxb.xmlmodel.XmlJavaTypeAdapter xja = new org.eclipse.persistence.jaxb.xmlmodel.XmlJavaTypeAdapter();
                xja.setValue(packageLevelAdapter.getQualifiedName());
                xja.setType(type.getQualifiedName());
               prop.setXmlJavaTypeAdapter(xja);
            }
        }
    }

    /**
     * This method is used to merge several bindings files into one XMLBindings object.
     * @param bindings the list of XmlBindings objects to merge.
     * @return XmlBindings object representing the merged files.
     */
    public static XmlBindings mergeXmlBindings(List<XmlBindings> bindings) {
        if(bindings.size() == 0) {
            return null;
        }
        XmlBindings rootBindings = bindings.get(0);
        for(int i = 1; i < bindings.size(); i++) {
            XmlBindings nextBindings = bindings.get(i);
            if(nextBindings.isSetXmlAccessorOrder()) {
                rootBindings.setXmlAccessorOrder(nextBindings.getXmlAccessorOrder());
            }
            if(nextBindings.isSetXmlAccessorType()) {
                rootBindings.setXmlAccessorType(nextBindings.getXmlAccessorType());
            }
            if(nextBindings.isSetXmlMappingMetadataComplete()) {
                rootBindings.setXmlMappingMetadataComplete(nextBindings.isXmlMappingMetadataComplete());
            }
            mergeJavaTypes(rootBindings.getJavaTypes(), nextBindings.getJavaTypes());

            if(rootBindings.getXmlEnums() == null) {
                rootBindings.setXmlEnums(nextBindings.getXmlEnums());
            } else {
                mergeXmlEnums(rootBindings.getXmlEnums(), nextBindings.getXmlEnums());
            }
            
            if(rootBindings.getXmlSchema() == null) {
                rootBindings.setXmlSchema(nextBindings.getXmlSchema());
            } else if(nextBindings.getXmlSchema() != null){
                mergeXmlSchema(rootBindings.getXmlSchema(), nextBindings.getXmlSchema());
            }
            
            if(rootBindings.getXmlSchemaType() == null) {
                rootBindings.setXmlSchemaTypes(nextBindings.getXmlSchemaTypes());
            } else if(nextBindings.getXmlSchemaTypes() != null){
                mergeXmlSchemaTypes(rootBindings.getXmlSchemaTypes(), nextBindings.getXmlSchemaTypes());
            }
            
            if(rootBindings.getXmlJavaTypeAdapters() == null) {
                rootBindings.setXmlJavaTypeAdapters(nextBindings.getXmlJavaTypeAdapters());
            } else if(nextBindings.getXmlJavaTypeAdapters() != null){
                mergeXmlJavaTypeAdapters(rootBindings.getXmlJavaTypeAdapters(), nextBindings.getXmlJavaTypeAdapters());
            }
        }
        return rootBindings;
    }

    private static void mergeXmlJavaTypeAdapters(XmlJavaTypeAdapters xmlJavaTypeAdapters, XmlJavaTypeAdapters overrideAdapters) {
        List<XmlJavaTypeAdapter> adapterList = xmlJavaTypeAdapters.getXmlJavaTypeAdapter();
        HashMap<String, XmlJavaTypeAdapter> adapterMap = new HashMap<String, XmlJavaTypeAdapter>(adapterList.size());
        for(XmlJavaTypeAdapter next:adapterList) {
            adapterMap.put(next.getType(), next);
        }
        
        for(XmlJavaTypeAdapter next:overrideAdapters.getXmlJavaTypeAdapter()) {
            //If there's already an adapter for this type, replace it's value
            XmlJavaTypeAdapter existingAdapter = adapterMap.get(next.getType());
            if(existingAdapter != null) {
                existingAdapter.setValue(next.getValue());
            } else {
                xmlJavaTypeAdapters.getXmlJavaTypeAdapter().add(next);
            }
        }
    }

    private static void mergeXmlSchemaTypes(XmlSchemaTypes xmlSchemaTypes, XmlSchemaTypes overrideSchemaTypes) {
        List<XmlSchemaType> schemaTypeList = xmlSchemaTypes.getXmlSchemaType();
        HashMap<String, XmlSchemaType> schemaTypeMap = new HashMap<String, XmlSchemaType>(schemaTypeList.size());
        for(XmlSchemaType next:schemaTypeList) {
            schemaTypeMap.put(next.getType(), next);
        }
        
        for(XmlSchemaType next:overrideSchemaTypes.getXmlSchemaType()) {
            //if there's already a schemaType for this type, override it's value
            XmlSchemaType existingType = schemaTypeMap.get(next.getType());
            if(existingType != null) {
                existingType.setName(next.getName());
                existingType.setNamespace(next.getNamespace());
            } else {
                xmlSchemaTypes.getXmlSchemaType().add(next);
            }
        }
    }

    private static void mergeXmlSchema(XmlSchema xmlSchema, XmlSchema overrideSchema) {

        xmlSchema.setAttributeFormDefault(overrideSchema.getAttributeFormDefault());
        xmlSchema.setElementFormDefault(overrideSchema.getElementFormDefault());
        xmlSchema.setLocation(overrideSchema.getLocation());
        xmlSchema.setNamespace(overrideSchema.getNamespace());
        List<XmlNs> xmlNsList = xmlSchema.getXmlNs();
        xmlNsList.addAll(overrideSchema.getXmlNs());
    }

    private static void mergeXmlEnums(XmlEnums xmlEnums, XmlEnums overrideEnum) {
        if(overrideEnum == null) {
            return;
        }
        List<XmlEnum> enumList = xmlEnums.getXmlEnum();
        Map<String, XmlEnum> enumMap = new HashMap<String, XmlEnum>(enumList.size());
        for(XmlEnum next:enumList) {
            enumMap.put(next.getJavaEnum(), next);
        }
        for(XmlEnum next:overrideEnum.getXmlEnum()) {
            XmlEnum existingEnum = enumMap.get(next.getJavaEnum());
            if(existingEnum != null) {
                mergeXmlEnumValues(existingEnum.getXmlEnumValue(), next.getXmlEnumValue());
            } else {
                xmlEnums.getXmlEnum().add(next);
            }
        }
    }

    private static void mergeXmlEnumValues(List<XmlEnumValue> xmlEnumValue, List<XmlEnumValue> overrideXmlEnumValue) {
    
        Map<String, XmlEnumValue> values = new HashMap<String, XmlEnumValue>();
        List<XmlEnumValue> extraValues = new ArrayList<XmlEnumValue>();
        for(XmlEnumValue next:xmlEnumValue){
            values.put(next.getJavaEnumValue(), next);
        }
        
        for(XmlEnumValue next:overrideXmlEnumValue) {
            XmlEnumValue existingValue = values.get(next.getJavaEnumValue());
            if(existingValue == null) {
                extraValues.add(next);
            } else {
                existingValue.setValue(next.getValue());
            }
        }
        xmlEnumValue.addAll(extraValues);
    }

    private static void mergeJavaTypes(JavaTypes javaTypes, JavaTypes overrideJavaTypes) {
        List<JavaType> javaTypeList = javaTypes.getJavaType();
        Map<String, JavaType> javaTypeMap = new HashMap<String, JavaType>(javaTypeList.size());
        for(JavaType next:javaTypeList) {
            javaTypeMap.put(next.getName(), next);
        }
        for(JavaType next:overrideJavaTypes.getJavaType()) {
            JavaType existingType = javaTypeMap.get(next.getName());
            if(existingType == null) {
                javaTypes.getJavaType().add(next);
            } else {
                mergeJavaType(existingType, next);
            }
        }
    }

    private static void mergeJavaType(JavaType existingType, JavaType next) {
        if(next.isSetXmlAccessorOrder()) {
            existingType.setXmlAccessorOrder(next.getXmlAccessorOrder());
        }

        if(next.isSetXmlAccessorType()) {
            existingType.setXmlAccessorType(next.getXmlAccessorType());
        }
        
        if(next.isSetXmlInlineBinaryData()) {
            existingType.setXmlInlineBinaryData(next.isXmlInlineBinaryData());
        }
        
        if(next.isSetXmlTransient()) {
            existingType.setXmlTransient(next.isXmlInlineBinaryData());
        }
        
        if(next.getXmlRootElement() != null) {
            existingType.setXmlRootElement(next.getXmlRootElement());
        }
        
        if(existingType.getXmlProperties() == null) {
            existingType.setXmlProperties(next.getXmlProperties());
        } else if(next.getXmlProperties() != null) {
            existingType.getXmlProperties().getXmlProperty().addAll(next.getXmlProperties().getXmlProperty());
        }
        
        if(next.getXmlType() != null) {
            existingType.setXmlType(next.getXmlType());
        }
        
        existingType.getXmlSeeAlso().addAll(next.getXmlSeeAlso());
        
        
        JavaAttributes attributes = existingType.getJavaAttributes();
        JavaAttributes overrideAttributes = next.getJavaAttributes();
        if(attributes == null) {
            existingType.setJavaAttributes(overrideAttributes);
        } else if(overrideAttributes != null) {
            mergeJavaAttributes(attributes, overrideAttributes, existingType);
        }
        
    }

    private static void mergeJavaAttributes(JavaAttributes attributes, JavaAttributes overrideAttributes, JavaType javaType) {
        List<JAXBElement<? extends JavaAttribute>> attributeList = attributes.getJavaAttribute();
        Map<String, JAXBElement> attributeMap = new HashMap<String, JAXBElement>(attributeList.size());
        
        for(JAXBElement next:attributeList) {
            attributeMap.put(((JavaAttribute)next.getValue()).getJavaAttribute(), next);
        }
        for(JAXBElement next:overrideAttributes.getJavaAttribute()) {
            JAXBElement existingAttribute = attributeMap.get(((JavaAttribute)next.getValue()).getJavaAttribute());
            if(existingAttribute != null) {
                attributes.getJavaAttribute().remove(existingAttribute);
            }
            attributes.getJavaAttribute().add(next);
        }
    }
}