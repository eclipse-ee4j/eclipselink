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
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaModel;
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
import org.eclipse.persistence.jaxb.xmlmodel.XmlNsForm;
import org.eclipse.persistence.jaxb.xmlmodel.XmlSchema;
import org.eclipse.persistence.jaxb.xmlmodel.XmlTransient;
import org.eclipse.persistence.jaxb.xmlmodel.XmlValue;
import org.eclipse.persistence.jaxb.xmlmodel.XmlSchema.XmlNs;
import org.eclipse.persistence.oxm.NamespaceResolver;

public class XMLProcessor {
    
    private Map<String, XmlBindings> xmlBindingMap;
    private AnnotationsProcessor annotationsProcessor;
        
    /**
     * This is the preferred constructor.
     * 
     * @param bindings
     */
    public XMLProcessor(Map<String, XmlBindings> bindings) {
        this.xmlBindingMap = bindings;
    }
    
    /**
     * 
     * @param annotationsProcessor
     */
    public void processXML(AnnotationsProcessor annotationsProcessor, JavaModelInput jModelInput) {
        this.annotationsProcessor = annotationsProcessor;
        
        // process each XmlBindings in the map
        XmlBindings xmlBindings; 
        for (String packageName : xmlBindingMap.keySet()) {
            xmlBindings = xmlBindingMap.get(packageName);

            // handle @XmlSchema override
            HashMap<String, NamespaceInfo> additionalNamespaceInfo = processXmlSchema(xmlBindings);
            if (additionalNamespaceInfo != null) {
                annotationsProcessor.setPackageToNamespaceMappings(additionalNamespaceInfo);
            }

            // build an array of JavaModel classes
            int idx = 0;
            JavaClass[] javaClasses = new JavaClass[xmlBindings.getJavaTypes().getJavaType().size()];
            for (JavaType javaType : xmlBindings.getJavaTypes().getJavaType()) {
                JavaClass jClass = jModelInput.getJavaModel().getClass(javaType.getName());
                javaClasses[idx++] = jClass; 
            }
            // pre-build the TypeInfo objects
            annotationsProcessor.init();
            Map<String, TypeInfo> typeInfoMap = annotationsProcessor.preBuildTypeInfo(javaClasses);

            for (JavaType javaType : xmlBindings.getJavaTypes().getJavaType()) {
                TypeInfo info = typeInfoMap.get(javaType.getName());
                // handle @XmlAccessorType override
                if (javaType.isSetXmlAccessorType()) {
                    info.setXmlAccessType(javaType.getXmlAccessorType());
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
            }
            
            // post-build the TypeInfo objects
            javaClasses = annotationsProcessor.postBuildTypeInfo(javaClasses);
            
            // now trigger the annotations processor to process the classes
            annotationsProcessor.processJavaClasses(javaClasses);
            
            // get the generated TypeInfo
            Map<String, TypeInfo> typeInfosForPackage = annotationsProcessor.getTypeInfosForPackage(packageName);

            // update TypeInfo objects based on the JavaTypes
            for (JavaType javaType : xmlBindings.getJavaTypes().getJavaType()) {
                TypeInfo typeInfo = typeInfosForPackage.get(javaType.getName());
                if (javaType != null) {
                    processJavaType(javaType, typeInfo);
                }
            }
        }
    }
    
    private void processJavaType(JavaType javaType, TypeInfo typeInfo) {
        // process field/property overrides
        if (null != javaType.getJavaAttributes()) {
            for (JAXBElement jaxbElement : javaType.getJavaAttributes().getJavaAttribute()) {
                JavaAttribute javaAttribute = (JavaAttribute) jaxbElement.getValue();
                Property oldProperty = typeInfo.getProperties().get(javaAttribute.getJavaAttribute());
                Property newProperty = processJavaAttribute(javaAttribute, oldProperty);
                typeInfo.getProperties().put(javaAttribute.getJavaAttribute(), newProperty);
            }
        }        
    }
    
    private Property processJavaAttribute(JavaAttribute javaAttribute, Property oldProperty) {
        if (javaAttribute instanceof XmlAnyAttribute) {
           return processXmlAnyAttribute((XmlAnyAttribute) javaAttribute, oldProperty);
        } else if (javaAttribute instanceof XmlAnyElement) {
            return processXmlAnyElement((XmlAnyElement) javaAttribute, oldProperty);
        } else if (javaAttribute instanceof XmlAttribute) {
            return processXmlAttribute((XmlAttribute) javaAttribute, oldProperty);
        } else if (javaAttribute instanceof XmlElement) {
            return processXmlElement((XmlElement) javaAttribute, oldProperty);
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
        }
        return null;
    }
    
    private Property processXmlAnyAttribute(XmlAnyAttribute xmlAnyAttribute, Property oldProperty) {
        return oldProperty;
    }
    
    private Property processXmlAnyElement(XmlAnyElement xmlAnyElement, Property oldProperty) {
        return oldProperty;
    }
    
    private Property processXmlAttribute(XmlAttribute xmlAttribute, Property oldProperty) {
        return oldProperty;
    }

    private Property processXmlElement(XmlElement xmlElement, Property oldProperty) {
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
     * Process an XmlSchema.  This involves creating a NamespaceInfo instance,
     * populating it based on the given XmlSchema, and putting the newly
     * created NamespaceInfo object in the annotations processor's 
     * 'packageToNamespaceMappings' map.
     * 
     * Here it is assumed that the XmlSchema configuration is to be 
     * applied to the one and only package contained within the 
     * XmlBindings file. 
     * 
     * @param xmlBindings
     * @see NamespaceInfo
     * @see AnnotationsProcessor
     * @return HashMap of additional package name to namespace info, or null if schema is null
     */
    private HashMap<String, NamespaceInfo> processXmlSchema(XmlBindings xmlBindings) {
        XmlSchema schema = xmlBindings.getXmlSchema();
        if (schema == null) {
            return null;
        }
        HashMap<String, NamespaceInfo> additionalNamespaceInfo = new HashMap<String, NamespaceInfo>();
        // create NamespaceInfo
        NamespaceInfo nsInfo = new NamespaceInfo();
        // process XmlSchema
        XmlNsForm form = schema.getAttributeFormDefault();
        nsInfo.setAttributeFormQualified(form.equals(form.QUALIFIED));
        form = schema.getElementFormDefault();
        nsInfo.setElementFormQualified(form.equals(form.QUALIFIED));
        
        // make sure defaults are set, not null
        nsInfo.setLocation(schema.getLocation()==null?"##generate":schema.getLocation());
        nsInfo.setNamespace(schema.getNamespace()==null?"":schema.getNamespace());
        NamespaceResolver nsr = new NamespaceResolver();
        // process XmlNs
        for (XmlNs xmlns : schema.getXmlNs()) {
            nsr.put(xmlns.getPrefix(), xmlns.getNamespaceUri());
        }
        nsInfo.setNamespaceResolver(nsr);
        
        // assume this applies to each package
        for (JavaType jType : xmlBindings.getJavaTypes().getJavaType()) {
            String typeName = jType.getName();
            String typePackageName = "";
            int lastDot = typeName.lastIndexOf('.');
            if (lastDot > 0) {
                typePackageName = typeName.substring(0, lastDot);
            }
            additionalNamespaceInfo.put(typePackageName, nsInfo);
            // at this point we assume that there is one exlipselink-oxm.xml per package,
            // so we might as well break out after we set the new NamespaceInfo for the
            // one and only package
            break;
        }
        return additionalNamespaceInfo;
    }
    
    /**
     * Process an XmlSeeAlso.  Each string in the list will be loaded by the JavaModel; any non-null
     * JavaClass instances returned will be added to the list of existingSeeAlsoClasses.
     * 
     * @param existingSeeAlsoClasses
     * @param seeAlsoClassNames
     * @param jModel
     * @return
     */
    private List<JavaClass> processXmlSeeAlso(List<String> seeAlsoClassNames, JavaModel jModel) {
        List<JavaClass> seeAlsoClasses = new ArrayList<JavaClass>();
        for (String seeAlso : seeAlsoClassNames) {
            JavaClass jClass = jModel.getClass(seeAlso);
            if (jClass != null) {
                seeAlsoClasses.add(jClass);
            }
        }
        return seeAlsoClasses;
    }
    
    private List<JavaClass> processXmlSeeAlso(List<JavaClass> existingSeeAlsoClasses, List<String> seeAlsoClassNames, JavaModel jModel) {
        List<JavaClass> seeAlsoClasses = existingSeeAlsoClasses;
        for (String seeAlso : seeAlsoClassNames) {
            JavaClass jClass = jModel.getClass(seeAlso);
            if (jClass != null) {
                seeAlsoClasses.add(jClass);
            }
        }
        return seeAlsoClasses;
    }
}