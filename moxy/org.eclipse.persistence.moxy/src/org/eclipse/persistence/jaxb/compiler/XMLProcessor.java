/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaModelInput;
import org.eclipse.persistence.jaxb.xmlmodel.JavaAttribute;
import org.eclipse.persistence.jaxb.xmlmodel.JavaType;
import org.eclipse.persistence.jaxb.xmlmodel.XmlAbstractNullPolicy;
import org.eclipse.persistence.jaxb.xmlmodel.XmlAnyAttribute;
import org.eclipse.persistence.jaxb.xmlmodel.XmlAnyElement;
import org.eclipse.persistence.jaxb.xmlmodel.XmlAttribute;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;
import org.eclipse.persistence.jaxb.xmlmodel.XmlElement;
import org.eclipse.persistence.jaxb.xmlmodel.XmlElementRef;
import org.eclipse.persistence.jaxb.xmlmodel.XmlElementRefs;
import org.eclipse.persistence.jaxb.xmlmodel.XmlElements;
import org.eclipse.persistence.jaxb.xmlmodel.XmlEnum;
import org.eclipse.persistence.jaxb.xmlmodel.XmlEnumValue;
import org.eclipse.persistence.jaxb.xmlmodel.XmlInverseReference;
import org.eclipse.persistence.jaxb.xmlmodel.XmlJavaTypeAdapter;
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
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings.JavaTypes;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings.XmlEnums;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings.XmlRegistries;
import org.eclipse.persistence.jaxb.xmlmodel.XmlProperties.XmlProperty;
import org.eclipse.persistence.jaxb.xmlmodel.XmlSchema.XmlNs;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;

public class XMLProcessor {
    private Map<String, XmlBindings> xmlBindingMap;
    private JavaModelInput jModelInput;
    private AnnotationsProcessor aProcessor;
    private JAXBMetadataLogger logger;
    private static final String COLON = ":";
    private static final String SLASH = "/";
    private static final String SELF = ".";
    private static final String OPEN_BRACKET =  "[";
    private static final String DEFAULT = "##default";
    private static final String JAVA_LANG_OBJECT = "java.lang.Object";

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
        Map<String, XmlEnum> xmlEnumMap = new HashMap<String, XmlEnum>();
        annotationsProcessor.init(originalJavaClasses, typeMappingInfos);

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
                annotationsProcessor.addPackageToNamespaceMapping(packageName, nsInfo);
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
                    xmlEnumMap.put(xmlEnum.getJavaEnum(), xmlEnum);
                }
            }

            // pre-build the TypeInfo objects
            Map<String, TypeInfo> typeInfoMap = annotationsProcessor.preBuildTypeInfo(javaClasses);

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

            nsInfo = annotationsProcessor.getPackageToNamespaceMappings().get(packageName);

            JavaTypes jTypes = xmlBindings.getJavaTypes();
            if (jTypes != null) {
                for (JavaType javaType : jTypes.getJavaType()) {
                    TypeInfo info = typeInfoMap.get(javaType.getName());

                    // package/class override order:
                    // 1 - xml class-level
                    // 2 - java object class-level
                    // 3 - xml package-level
                    // 4 - package-info.java

                    // handle class-level @XmlJavaTypeAdapter override
                    if (javaType.getXmlJavaTypeAdapter() != null) {
                        info.setXmlJavaTypeAdapter(javaType.getXmlJavaTypeAdapter());
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
                            info.setXmlAccessOrder(nsInfo.getAccessOrder());
                        }
                    }

                    // handle class-level @XmlAccessorType override
                    if (javaType.isSetXmlAccessorType()) {
                        info.setXmlAccessType(javaType.getXmlAccessorType());
                    } else if (!info.isSetXmlAccessType()) {
                        if (xmlBindings.isSetXmlAccessorType()) {
                            // handle package-level @XmlAccessorType override
                            info.setXmlAccessType(xmlBindings.getXmlAccessorType());
                        } else {
                            // finally, check the NamespaceInfo
                            info.setXmlAccessType(nsInfo.getAccessType());
                        }
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
                    // handle @XmlDiscriminatorValue override
                    if (javaType.getXmlDiscriminatorValue() != null) {
                        info.setXmlDiscriminatorValue(javaType.getXmlDiscriminatorValue());
                    }
                }
            }

            // apply package-level @XmlJavaTypeAdapters
            if (xmlBindings.getXmlJavaTypeAdapters() != null) {
                Map<String, TypeInfo> typeInfos = aProcessor.getTypeInfosForPackage(packageName);
                for (TypeInfo tInfo : typeInfos.values()) {
                    List<XmlJavaTypeAdapter> adapters = xmlBindings.getXmlJavaTypeAdapters().getXmlJavaTypeAdapter();
                    for (XmlJavaTypeAdapter xja : adapters) {
                        try {
                            JavaClass adapterClass = jModelInput.getJavaModel().getClass(xja.getValue());
                            JavaClass boundType = jModelInput.getJavaModel().getClass(xja.getType());
                            if (boundType != null) {
                                tInfo.addPackageLevelAdapterClass(adapterClass, boundType);
                            }
                        } catch(JAXBException e) {
                            throw JAXBException.invalidPackageAdapterClass(xja.getValue(), packageName);
                        }
                    }
                }
            }

            // post-build the TypeInfo objects
            javaClasses = annotationsProcessor.postBuildTypeInfo(javaClasses);

            // now trigger the annotations processor to process the classes
            annotationsProcessor.processJavaClasses(null);

            // get the generated TypeInfo
            Map<String, TypeInfo> typeInfosForPackage = annotationsProcessor.getTypeInfosForPackage(packageName);

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
            jTypes = xmlBindings.getJavaTypes();
            if (jTypes != null) {
                for (JavaType javaType : jTypes.getJavaType()) {
                    processJavaType(javaType, typeInfosForPackage.get(javaType.getName()), nsInfo);
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
            annotationsProcessor.buildNewTypeInfo(jClassArray);
            annotationsProcessor.processJavaClasses(jClassArray);
        }

        // need to ensure that any bound types (from XmlJavaTypeAdapter) have
        // TypeInfo objects built for them - SchemaGenerator will require a
        // descriptor for each
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
        ArrayList<JavaClass> jClasses = aProcessor.getTypeInfoClasses();
        aProcessor.processPropertyTypes(jClasses.toArray(new JavaClass[jClasses.size()]));
        
        aProcessor.finalizeProperties();
        aProcessor.createElementsForTypeMappingInfo();
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
            for (JAXBElement jaxbElement : javaType.getJavaAttributes().getJavaAttribute()) {
                JavaAttribute javaAttribute = (JavaAttribute) jaxbElement.getValue();
                Property oldProperty = typeInfo.getProperties().get(javaAttribute.getJavaAttribute());
                if (oldProperty == null) {
                    getLogger().logWarning(JAXBMetadataLogger.NO_PROPERTY_FOR_JAVA_ATTRIBUTE, new Object[] { javaAttribute.getJavaAttribute(), javaType.getName() });
                    continue;
                }
                Property newProperty = processJavaAttribute(typeInfo, javaAttribute, oldProperty, nsInfo, javaType);
                typeInfo.getProperties().put(javaAttribute.getJavaAttribute(), newProperty);
            }
        }
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
        } else if (javaAttribute instanceof XmlAnyElement) {
            return processXmlAnyElement((XmlAnyElement) javaAttribute, oldProperty, typeInfo, javaType);
        } else if (javaAttribute instanceof XmlAttribute) {
            return processXmlAttribute((XmlAttribute) javaAttribute, oldProperty, typeInfo, nsInfo);
        } else if (javaAttribute instanceof XmlElement) {
            return processXmlElement((XmlElement) javaAttribute, oldProperty, typeInfo, nsInfo, javaType);
        } else if (javaAttribute instanceof XmlElements) {
            return processXmlElements((XmlElements) javaAttribute, oldProperty, typeInfo);
        } else if (javaAttribute instanceof XmlElementRef) {
            return processXmlElementRef((XmlElementRef) javaAttribute, oldProperty, typeInfo);
        } else if (javaAttribute instanceof XmlElementRefs) {
            return processXmlElementRefs((XmlElementRefs) javaAttribute, oldProperty, typeInfo);
        } else if (javaAttribute instanceof XmlTransient) {
            return processXmlTransient((XmlTransient) javaAttribute, oldProperty);
        } else if (javaAttribute instanceof XmlValue) {
            return processXmlValue((XmlValue) javaAttribute, oldProperty, typeInfo, javaType);
        } else if (javaAttribute instanceof XmlJavaTypeAdapter) {
            return processXmlJavaTypeAdapter((XmlJavaTypeAdapter) javaAttribute, oldProperty);
        } else if (javaAttribute instanceof XmlInverseReference) {
            return processXmlInverseReference((XmlInverseReference)javaAttribute, oldProperty);
        } else if (javaAttribute instanceof XmlTransformation) {
            return processXmlTransformation((XmlTransformation)javaAttribute, oldProperty, typeInfo);
        } else if (javaAttribute instanceof XmlJoinNodes) {
            return processXmlJoinNodes((XmlJoinNodes) javaAttribute, oldProperty); 
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

    private Property processXmlInverseReference(XmlInverseReference xmlInverseReference, Property oldProperty) {
        oldProperty.setInverseReference(true);
        oldProperty.setInverseReferencePropertyName(xmlInverseReference.getMappedBy());
        if(xmlInverseReference.getXmlAccessMethods() != null) {
            oldProperty.setInverseReferencePropertyGetMethodName(xmlInverseReference.getXmlAccessMethods().getGetMethod());
            oldProperty.setInverseReferencePropertySetMethodName(xmlInverseReference.getXmlAccessMethods().getSetMethod());
        }
        // set user-defined properties
        if (xmlInverseReference.getXmlProperties() != null  && xmlInverseReference.getXmlProperties().getXmlProperty().size() > 0) {
            oldProperty.setUserProperties(createUserPropertyMap(xmlInverseReference.getXmlProperties().getXmlProperty()));
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
        if (!oldProperty.getType().getName().equals("java.util.Map")) {
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
    private Property processXmlAttribute(XmlAttribute xmlAttribute, Property oldProperty, TypeInfo typeInfo, NamespaceInfo nsInfo) {
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

        // set schema name
        QName qName;
        if (name.equals(DEFAULT)) {
            name = oldProperty.getPropertyName();
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
        if (!xmlAttribute.getContainerType().equals(DEFAULT)) {
            setContainerType(oldProperty, xmlAttribute.getContainerType());
        }
        // set type
        if (!xmlAttribute.getType().equals(DEFAULT)) {
            JavaClass pType = jModelInput.getJavaModel().getClass(xmlAttribute.getType());
            if (aProcessor.isCollectionType(oldProperty.getType())) {
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

        // handle xml-mime-type
        if (xmlAttribute.getXmlMimeType() != null) {
            oldProperty.setMimeType(xmlAttribute.getXmlMimeType());
        }

        // handle xml-attachment-ref
        if (xmlAttribute.isXmlAttachmentRef()) {
            oldProperty.setIsSwaAttachmentRef(true);
            oldProperty.setSchemaType(XMLConstants.SWA_REF_QNAME);
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
            if (xmlElement.getXmlElementWrapper() != null) {
                oldProperty.setXmlElementWrapper(xmlElement.getXmlElementWrapper());
            }
        }

        // set schema name
        QName qName;
        if (name.equals(DEFAULT)) {
            name = oldProperty.getPropertyName();
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
            if(aProcessor.isCollectionType(oldProperty.getType())) {
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
            if (!aProcessor.isCollectionType(oldProperty) && !oldProperty.getType().isArray()) {
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
            oldProperty.setSchemaType(XMLConstants.SWA_REF_QNAME);
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
        resetProperty(oldProperty, info);

        List<XmlElementRef> eltRefs = new ArrayList<XmlElementRef>();
        eltRefs.add(xmlElementRef);
        oldProperty.setXmlElementRefs(eltRefs);
        oldProperty.setIsReference(true);
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
        resetProperty(oldProperty, info);

        List<XmlElementRef> eltRefs = new ArrayList<XmlElementRef>();
        for (XmlElementRef eltRef : xmlElementRefs.getXmlElementRef()) {
            eltRefs.add(eltRef);
        }

        oldProperty.setXmlElementRefs(eltRefs);
        oldProperty.setIsReference(true);
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
            if (aProcessor.isCollectionType(oldProperty.getType())) {
                oldProperty.setGenericType(pType);
            } else {
                oldProperty.setType(pType);
            }
            // may need to generate a type info for the type
            if (aProcessor.shouldGenerateTypeInfo(pType) && aProcessor.getTypeInfo().get(pType.getQualifiedName()) == null) {
                aProcessor.buildNewTypeInfo(new JavaClass[] { pType });
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
        NamespaceInfo nsInfo = new NamespaceInfo();
        // process XmlSchema
        XmlNsForm form = schema.getAttributeFormDefault();
        nsInfo.setAttributeFormQualified(form.equals(form.QUALIFIED));
        form = schema.getElementFormDefault();
        nsInfo.setElementFormQualified(form.equals(form.QUALIFIED));

        
        if (!nsInfo.isElementFormQualified() || nsInfo.isAttributeFormQualified()) {
            aProcessor.setDefaultNamespaceAllowed(false);
        }
        
        // make sure defaults are set, not null
        nsInfo.setLocation(schema.getLocation() == null ? "##generate" : schema.getLocation());
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
    private Property processXmlJoinNodes(XmlJoinNodes xmlJoinNodes, Property oldProperty) {
        oldProperty.setXmlJoinNodes(xmlJoinNodes);
        
        // check for container type
        if (!xmlJoinNodes.getContainerType().equals(DEFAULT)) {
            setContainerType(oldProperty, xmlJoinNodes.getContainerType());
        }
        // set type
        if (!xmlJoinNodes.getType().equals(DEFAULT)) {
            JavaClass pType = jModelInput.getJavaModel().getClass(xmlJoinNodes.getType());
            if (aProcessor.isCollectionType(oldProperty.getType())) {
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
                    JavaClass nextClass = jModelInput.getJavaModel().getClass(javaType.getName());
                    String nextPackageName = nextClass.getPackageName();
                    if(nextPackageName == null || !nextPackageName.equals(packageName)){
                        throw JAXBException.javaTypeNotAllowedInBindingsFile(nextPackageName, packageName);
                    }
                    classes.add(nextClass);
                }
            }

            // add any enum types to the class list
            XmlEnums xmlEnums = xmlBindings.getXmlEnums();
            if (xmlEnums != null) {
                for (XmlEnum xmlEnum : xmlEnums.getXmlEnum()) {
                    classes.add(jModelInput.getJavaModel().getClass(xmlEnum.getJavaEnum()));
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
        unsetXmlElementRefs(oldProperty, tInfo);
        unsetXmlElements(oldProperty);
        unsetXmlAnyAttribute(oldProperty, tInfo);
        unsetXmlAnyElement(oldProperty, tInfo);
        unsetXmlValue(oldProperty, tInfo);
        unsetXmlID(oldProperty, tInfo);
        unsetXmlKey(oldProperty, tInfo);
        reapplyPackageAndClassAdapters(oldProperty, tInfo);
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
            tInfo.getXmlKeyProperties().remove(oldProperty);
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
        int idx = xpath.indexOf(SLASH + XMLConstants.TEXT);
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
            idx = name.indexOf(XMLConstants.ATTRIBUTE);
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
    
    public void reapplyPackageAndClassAdapters(Property prop, TypeInfo owningInfo) {
        JavaClass type = prop.getActualType();
        //if a class level adapter is present on the target class, set it
        TypeInfo targetInfo = aProcessor.getTypeInfo().get(type.getQualifiedName());
        if(targetInfo != null) {
            if(targetInfo.getXmlJavaTypeAdapter() != null) {
                prop.setXmlJavaTypeAdapter(targetInfo.getXmlJavaTypeAdapter());
            }
        }

        //check for package level adapter. Don't overwrite class level
        JavaClass packageLevelAdapter = owningInfo.getPackageLevelAdaptersByClass().get(type.getQualifiedName()); 
        if(packageLevelAdapter != null && prop.getXmlJavaTypeAdapter() == null) {
            org.eclipse.persistence.jaxb.xmlmodel.XmlJavaTypeAdapter xja = new org.eclipse.persistence.jaxb.xmlmodel.XmlJavaTypeAdapter();
            xja.setValue(packageLevelAdapter.getQualifiedName());
            xja.setType(type.getQualifiedName());
            prop.setXmlJavaTypeAdapter(xja);
        }
        
    }
}
