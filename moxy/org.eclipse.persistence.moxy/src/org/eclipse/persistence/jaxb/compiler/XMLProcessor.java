/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaModelInput;
import org.eclipse.persistence.jaxb.xmlmodel.JavaAttribute;
import org.eclipse.persistence.jaxb.xmlmodel.JavaType;
import org.eclipse.persistence.jaxb.xmlmodel.XmlAnyAttribute;
import org.eclipse.persistence.jaxb.xmlmodel.XmlAnyElement;
import org.eclipse.persistence.jaxb.xmlmodel.XmlAttribute;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;
import org.eclipse.persistence.jaxb.xmlmodel.XmlElement;
import org.eclipse.persistence.jaxb.xmlmodel.XmlElementRef;
import org.eclipse.persistence.jaxb.xmlmodel.XmlElementRefs;
import org.eclipse.persistence.jaxb.xmlmodel.XmlElements;
import org.eclipse.persistence.jaxb.xmlmodel.XmlJavaTypeAdapter;
import org.eclipse.persistence.jaxb.xmlmodel.XmlNsForm;
import org.eclipse.persistence.jaxb.xmlmodel.XmlSchema;
import org.eclipse.persistence.jaxb.xmlmodel.XmlTransient;
import org.eclipse.persistence.jaxb.xmlmodel.XmlValue;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings.JavaTypes;
import org.eclipse.persistence.jaxb.xmlmodel.XmlSchema.XmlNs;
import org.eclipse.persistence.oxm.NamespaceResolver;

public class XMLProcessor {
    private Map<String, XmlBindings> xmlBindingMap;
    private JavaModelInput jModelInput;
    private AnnotationsProcessor aProcessor;

    /**
     * This is the preferred constructor.
     * 
     * @param bindings
     */
    public XMLProcessor(Map<String, XmlBindings> bindings) {
        this.xmlBindingMap = bindings;
    }

    /**
     * Process XmlBindings on a per package basis for a given AnnotationsPorcessor instance.
     * 
     * @param annotationsProcessor
     */
    public void processXML(AnnotationsProcessor annotationsProcessor, JavaModelInput jModelInput) {
        this.jModelInput = jModelInput;
        this.aProcessor = annotationsProcessor;
        annotationsProcessor.init();
        
        // build a map of packages to JavaClass so we only process the JavaClasses for a given package
        // additional classes - i.e. ones from packages not listed in XML - will be processed later
        Map<String, ArrayList<JavaClass>> pkgToClassMap = buildPackageToJavaClassMap();
        
        // process each XmlBindings in the map
        XmlBindings xmlBindings;
        for (String packageName : xmlBindingMap.keySet()) {
            ArrayList classesToProcess = pkgToClassMap.get(packageName);
            if (classesToProcess == null) {
                continue;
            }

            xmlBindings = xmlBindingMap.get(packageName);

            // handle @XmlSchema override
            NamespaceInfo nsInfo = processXmlSchema(xmlBindings, packageName);
            if (nsInfo != null) {
                annotationsProcessor.addPackageToNamespaceMapping(packageName, nsInfo);
            }

            // build an array of JavaModel classes to process
            JavaClass[] javaClasses = (JavaClass[]) classesToProcess.toArray(new JavaClass[classesToProcess.size()]);

            // pre-build the TypeInfo objects
            Map<String, TypeInfo> typeInfoMap = annotationsProcessor.preBuildTypeInfo(javaClasses);

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
                }
            }

            // apply package-level @XmlJavaTypeAdapters
            if (xmlBindings.getXmlJavaTypeAdapters() != null) {
                Map<String, TypeInfo> typeInfos = aProcessor.getTypeInfosForPackage(packageName);
                for (TypeInfo tInfo : typeInfos.values()) {
                    List<XmlJavaTypeAdapter> adapters = xmlBindings.getXmlJavaTypeAdapters().getXmlJavaTypeAdapter();
                    for (XmlJavaTypeAdapter xja : adapters) {
                        JavaClass adapterClass = jModelInput.getJavaModel().getClass(xja.getValue());
                        JavaClass boundType = jModelInput.getJavaModel().getClass(xja.getType());
                        if (boundType != null) {
                            tInfo.addAdapterClass(adapterClass, boundType);
                        }
                    }
                }
            }

            // post-build the TypeInfo objects
            javaClasses = annotationsProcessor.postBuildTypeInfo(javaClasses);

            // now trigger the annotations processor to process the classes
            annotationsProcessor.processJavaClasses(javaClasses);

            // get the generated TypeInfo
            Map<String, TypeInfo> typeInfosForPackage = annotationsProcessor.getTypeInfosForPackage(packageName);

            // update TypeInfo objects based on the JavaTypes
            jTypes = xmlBindings.getJavaTypes();
            if (jTypes != null) {
                for (JavaType javaType : jTypes.getJavaType()) {
                    TypeInfo typeInfo = typeInfosForPackage.get(javaType.getName());
                    if (javaType != null) {
                        processJavaType(javaType, typeInfo, nsInfo);
                    }
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
        
        // need to ensure that any bound types (from XmlJavaTypeAdapter) have TypeInfo
        // objects built for them - SchemaGenerator will require a descriptor for each
        Map<String, TypeInfo> typeInfos = (Map<String, TypeInfo>) aProcessor.getTypeInfo().clone();
        for (String key : typeInfos.keySet()) {
            JavaClass[] jClassArray;
            TypeInfo tInfo = typeInfos.get(key);
            for (Property prop : tInfo.getPropertyList()) {
                if (prop.isSetXmlJavaTypeAdapter()) {
                    jClassArray = new JavaClass[] { prop.getActualType() };
                    aProcessor.buildNewTypeInfo(jClassArray);
                }
            }
        }
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
                // TODO: we should log a warning here
                if (oldProperty == null) {
                    continue;
                }
                Property newProperty = processJavaAttribute(javaAttribute, oldProperty, nsInfo);
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
    private Property processJavaAttribute(JavaAttribute javaAttribute, Property oldProperty, NamespaceInfo nsInfo) {
        if (javaAttribute instanceof XmlAnyAttribute) {
            return processXmlAnyAttribute((XmlAnyAttribute) javaAttribute, oldProperty);
        } else if (javaAttribute instanceof XmlAnyElement) {
            return processXmlAnyElement((XmlAnyElement) javaAttribute, oldProperty);
        } else if (javaAttribute instanceof XmlAttribute) {
            return processXmlAttribute((XmlAttribute) javaAttribute, oldProperty, nsInfo);
        } else if (javaAttribute instanceof XmlElement) {
            return processXmlElement((XmlElement) javaAttribute, oldProperty, nsInfo);
        } else if (javaAttribute instanceof XmlElements) {
            return processXmlElements((XmlElements) javaAttribute, oldProperty);
        } else if (javaAttribute instanceof XmlElementRef) {
            return processXmlElementRef((XmlElementRef) javaAttribute, oldProperty);
        } else if (javaAttribute instanceof XmlElementRefs) {
            return processXmlElementRefs((XmlElementRefs) javaAttribute, oldProperty);
        } else if (javaAttribute instanceof XmlTransient) {
            return processXmlTransient((XmlTransient) javaAttribute, oldProperty);
        } else if (javaAttribute instanceof XmlValue) {
            return processXmlValue((XmlValue) javaAttribute, oldProperty);
        } else if (javaAttribute instanceof XmlJavaTypeAdapter) {
            return processXmlJavaTypeAdapter((XmlJavaTypeAdapter) javaAttribute, oldProperty);
        }
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

    private Property processXmlAnyAttribute(XmlAnyAttribute xmlAnyAttribute, Property oldProperty) {
        return oldProperty;
    }

    private Property processXmlAnyElement(XmlAnyElement xmlAnyElement, Property oldProperty) {
        return oldProperty;
    }

    /**
     * XmlAttribute override will completely replace the existing values.  This will set name, 
     * namespace and required on the given property.
     *  
     * @param xmlAttribute
     * @param oldProperty
     * @param nsInfo
     * @return
     */
    private Property processXmlAttribute(XmlAttribute xmlAttribute, Property oldProperty, NamespaceInfo nsInfo) {
        // set isAttribute
        oldProperty.setIsAttribute(true);

        // set required
        oldProperty.setIsRequired(xmlAttribute.isRequired());

        // set schema name
        QName qName;
        String name = xmlAttribute.getName();
        if (name.equals("##default")) {
            name = oldProperty.getPropertyName();
        }
        if (xmlAttribute.getNamespace().equals("##default")) {
            if (nsInfo.isElementFormQualified()) {
                qName = new QName(nsInfo.getNamespace(), name);
            } else {
                qName = new QName(name);
            }
        } else {
            qName = new QName(xmlAttribute.getNamespace(), name);
        }
        oldProperty.setSchemaName(qName);
        
        return oldProperty;
    }

    /**
     * XmlElement override will completely replace the existing values.
     * 
     * @param xmlElement
     * @param oldProperty
     * @return
     */
    private Property processXmlElement(XmlElement xmlElement, Property oldProperty, NamespaceInfo nsInfo) {
        // set required
        oldProperty.setIsRequired(xmlElement.isRequired());

        // set nillable
        oldProperty.setNillable(xmlElement.isNillable());

        // set defaultValue
        if (xmlElement.getDefaultValue().equals("\u0000")) {
            oldProperty.setDefaultValue(null);
        } else {
            oldProperty.setDefaultValue(xmlElement.getDefaultValue());
        }

        // set schema name
        QName qName;
        String name = xmlElement.getName();
        if (name.equals("##default")) {
            name = oldProperty.getPropertyName();
        }
        if (xmlElement.getNamespace().equals("##default")) {
            if (nsInfo.isElementFormQualified()) {
                qName = new QName(nsInfo.getNamespace(), name);
            } else {
                qName = new QName(name);
            }
        } else {
            qName = new QName(xmlElement.getNamespace(), name);
        }
        oldProperty.setSchemaName(qName);

        // set type
        if (xmlElement.getType().equals("javax.xml.bind.annotation.XmlElement.DEFAULT")) {
            // if xmlElement has no type, and the property type was set via @XmlElement, reset it to
            // the original value
            if (oldProperty.isXmlElementType()) {
                oldProperty.setType(oldProperty.getOriginalType());
            }
        } else {
            oldProperty.setType(jModelInput.getJavaModel().getClass(xmlElement.getType()));
        }

        return oldProperty;
    }

    private Property processXmlElements(XmlElements xmlElements, Property oldProperty) {
        return oldProperty;
    }

    private Property processXmlElementRef(XmlElementRef xmlElementRef, Property oldProperty) {
        return oldProperty;
    }

    private Property processXmlElementRefs(XmlElementRefs xmlElementRefs, Property oldProperty) {
        return oldProperty;
    }

    private Property processXmlTransient(XmlTransient xmlTransient, Property oldProperty) {
        oldProperty.setTransient(true);
        return oldProperty;
    }

    private Property processXmlValue(XmlValue xmlValue, Property oldProperty) {
        return oldProperty;
    }

    /**
     * Process an XmlSchema. This involves creating a NamespaceInfo instance and populating it based
     * on the given XmlSchema.
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

        // make sure defaults are set, not null
        nsInfo.setLocation(schema.getLocation() == null ? "##generate" : schema.getLocation());
        nsInfo.setNamespace(schema.getNamespace() == null ? "" : schema.getNamespace());
        NamespaceResolver nsr = new NamespaceResolver();
        // process XmlNs
        for (XmlNs xmlns : schema.getXmlNs()) {
            nsr.put(xmlns.getPrefix(), xmlns.getNamespaceUri());
        }
        nsInfo.setNamespaceResolver(nsr);
        return nsInfo;
    }

    /**
     * Combines classes from XmlBindings and JavaModel into a single array. Duplicates will not
     * exist in the array.
     * 
     * @param xmlBindings
     * @param jModelInput
     * @return an array of JavaClass instances based on a given JavaModel
     */
    private JavaClass[] getClassesToProcess(XmlBindings xmlBindings) {
        ArrayList allClasses = new ArrayList<JavaClass>();
        // add binding classes - the Java Model will be used to get a JavaClass via class name
        JavaTypes jTypes = xmlBindings.getJavaTypes();
        if (jTypes != null) {
            for (JavaType javaType : jTypes.getJavaType()) {
                allClasses.add(jModelInput.getJavaModel().getClass(javaType.getName()));
            }
        }
        // add any other classes that aren't declared via external metadata
        for (JavaClass jClass : jModelInput.getJavaClasses()) {
            if (!AnnotationsProcessor.classExistsInArray(jClass.getQualifiedName(), allClasses)) {
                allClasses.add(jClass);
            }
        }
        return (JavaClass[]) allClasses.toArray(new JavaClass[allClasses.size()]);
    }
    
    /**
     * Convenience method for building a Map of package to classes.
     * 
     * @return
     */
    private Map<String, ArrayList<JavaClass>> buildPackageToJavaClassMap() {
        Map<String, ArrayList<JavaClass>> theMap = new HashMap<String, ArrayList<JavaClass>>();

        XmlBindings xmlBindings;
        for (String packageName : xmlBindingMap.keySet()) {
            xmlBindings = xmlBindingMap.get(packageName);
            ArrayList classes = new ArrayList<JavaClass>();
            // add binding classes - the Java Model will be used to get a JavaClass via class name
            JavaTypes jTypes = xmlBindings.getJavaTypes();
            if (jTypes != null) {
                for (JavaType javaType : jTypes.getJavaType()) {
                    classes.add(jModelInput.getJavaModel().getClass(javaType.getName()));
                }
            }
            theMap.put(packageName, classes);
        }
        
        // add any other classes that aren't declared via external metadata
        for (JavaClass jClass : jModelInput.getJavaClasses()) {
            // need to verify that the class isn't already in one of our lists
            String pkg = jClass.getPackageName();
            ArrayList<JavaClass> existingClasses = theMap.get(pkg);
            if (existingClasses != null) {
                if (!AnnotationsProcessor.classExistsInArray(jClass.getQualifiedName(), existingClasses)) {
                    existingClasses.add(jClass);
                }
            } else {
                ArrayList classes = new ArrayList<JavaClass>();
                classes.add(jClass);
                theMap.put(pkg, classes);
            }
        }
        
        return theMap;
    }
}