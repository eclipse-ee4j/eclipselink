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
//     David McCann - 2.5.0 - Sept.14, 2012 - Initial Implementation
package org.eclipse.persistence.tools.dbws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.jaxb.xmlmodel.JavaType;
import org.eclipse.persistence.jaxb.xmlmodel.JavaType.JavaAttributes;
import org.eclipse.persistence.jaxb.xmlmodel.XmlAbstractNullPolicy;
import org.eclipse.persistence.jaxb.xmlmodel.XmlAccessType;
import org.eclipse.persistence.jaxb.xmlmodel.XmlAttribute;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;
import org.eclipse.persistence.jaxb.xmlmodel.XmlElement;
import org.eclipse.persistence.jaxb.xmlmodel.XmlIsSetNullPolicy;
import org.eclipse.persistence.jaxb.xmlmodel.XmlMarshalNullRepresentation;
import org.eclipse.persistence.jaxb.xmlmodel.XmlNsForm;
import org.eclipse.persistence.jaxb.xmlmodel.XmlNullPolicy;
import org.eclipse.persistence.jaxb.xmlmodel.XmlRootElement;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings.JavaTypes;
import org.eclipse.persistence.jaxb.xmlmodel.XmlSchema;
import org.eclipse.persistence.jaxb.xmlmodel.XmlSchema.XmlNs;
import org.eclipse.persistence.jaxb.xmlmodel.XmlSchemaType;
import org.eclipse.persistence.jaxb.xmlmodel.XmlType;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.XMLMapping;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.IsSetNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.NullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;

import static org.eclipse.persistence.oxm.XMLConstants.EMPTY_STRING;
import static org.eclipse.persistence.tools.dbws.Util.DOT;
import static org.eclipse.persistence.tools.dbws.Util.XML_MIME_PREFIX;

/**
 * This class is responsible for generating one or more EclipseLink XmlBindings
 * objects based on a given list of XMLDescriptors.
 *
 */
public class XmlBindingsGenerator {
    public static final String SIMPLE_XML_FORMAT_PKG = "org.eclipse.persistence.internal.xr.sxf";
    public static final String DATAHANDLER_CLASSNAME = "javax.activation.DataHandler";

    /**
     * Generate one or more XmlBindings based on a given list of
     * XML Descriptor instances.
     *
     */
    public static List<XmlBindings> generateXmlBindings(List<ClassDescriptor> descriptors) {
        // group the descriptors by package name
        HashMap<String, List<XMLDescriptor>> descriptorMap = new HashMap<String, List<XMLDescriptor>>();
        for (ClassDescriptor cdesc : descriptors) {
            XMLDescriptor xdesc = (XMLDescriptor) cdesc;
            String packageName = getPackageName(xdesc.getJavaClassName());
            // don't process the simple-xml-format descriptor
            if (packageName.equals(SIMPLE_XML_FORMAT_PKG)) {
                continue;
            }
            List<XMLDescriptor> descriptorList = descriptorMap.get(packageName);
            if (descriptorList == null) {
                descriptorList = new ArrayList<XMLDescriptor>();
                descriptorMap.put(packageName, descriptorList);
            }
            descriptorList.add(xdesc);
        }

        List<XmlBindings> bindingsList = new ArrayList<XmlBindings>();
        // generate an XmlBindings for each package
        for (String pkg : descriptorMap.keySet()) {
            List<XMLDescriptor> xdescList = descriptorMap.get(pkg);

            XmlBindings xmlBindings = generateXmlBindings(pkg, xdescList);
            if (xmlBindings != null) {
                bindingsList.add(xmlBindings);
            }
        }
        return bindingsList;
    }

    /**
     * Generate an XmlBindings instance based on a list of XML descriptors.
     *
     * OXM metadata files are processed on a per package basis, hence it is
     * assumed that the given list of descriptors are from the same package.
     *
     */
    public static XmlBindings generateXmlBindings(String packageName, List<XMLDescriptor> descriptors) {
        String defaultNamespace = null;
        Map<String, String> prefixMap = new HashMap<String, String>();

        JavaTypes jTypes = new JavaTypes();
        for (XMLDescriptor xdesc : descriptors) {
            // get xml-schema info from one of the descriptors, and add prefix
            // mappings to the map
            if (xdesc.getNamespaceResolver() != null) {
                if (defaultNamespace == null) {
                    defaultNamespace = xdesc.getNamespaceResolver().getDefaultNamespaceURI();
                }
                Map<String, String> preMap = xdesc.getNamespaceResolver().getPrefixesToNamespaces();
                for (String pfx : preMap.keySet()) {
                    // ignore mime prefix/url for now
                    if (!pfx.equals(XML_MIME_PREFIX)) {
                        prefixMap.put(pfx, preMap.get(pfx));
                    }
                }
            }
            // generate a JavaType instance for the XML descriptor
            jTypes.getJavaType().add(generateJavaType(xdesc));
        }

        XmlBindings xmlBindings = null;
        // if there are no JavaTypes, there's nothing to do
        if (jTypes.getJavaType().size() > 0) {
            xmlBindings = new XmlBindings();
            xmlBindings.setJavaTypes(jTypes);
            xmlBindings.setPackageName(packageName);
            // handle XmlSchema
            if (defaultNamespace != null || !prefixMap.isEmpty()) {
                XmlSchema xSchema = new XmlSchema();
                xSchema.setNamespace(defaultNamespace == null ? EMPTY_STRING : defaultNamespace);
                xSchema.setElementFormDefault(XmlNsForm.QUALIFIED);
                // handle XmlNs
                if (!prefixMap.isEmpty()) {
                    XmlNs xmlNs;
                    for (String pfx : prefixMap.keySet()) {
                        xmlNs = new XmlNs();
                        xmlNs.setNamespaceUri(prefixMap.get(pfx));
                        xmlNs.setPrefix(pfx);
                        xSchema.getXmlNs().add(xmlNs);
                    }
                }
                xmlBindings.setXmlSchema(xSchema);
            }
        }
        return xmlBindings;
    }

    /**
     * Process a given XMLDescriptor and return a JavaType instance.
     *
     */
    protected static JavaType generateJavaType(XMLDescriptor xdesc) {
        String defaultNamespace = null;
        if (xdesc.getNamespaceResolver() != null) {
            defaultNamespace = xdesc.getNamespaceResolver().getDefaultNamespaceURI();
        }
        String schemaContext = null;
        if (xdesc.getSchemaReference() != null) {
            schemaContext = xdesc.getSchemaReference().getSchemaContext();
        }

        JavaType jType = new JavaType();
        jType.setName(getClassName(xdesc.getJavaClassName()));
        jType.setXmlAccessorType(XmlAccessType.FIELD);

        // handle XmlType
        if (schemaContext != null) {
            XmlType xType = new XmlType();
            xType.setName(schemaContext.substring(1, schemaContext.length()));
            if (defaultNamespace != null) {
                xType.setNamespace(defaultNamespace);
            }
            jType.setXmlType(xType);
        }
        // handle XmlRootElement
        XmlRootElement xmlRootElt = new XmlRootElement();
        xmlRootElt.setName(xdesc.getDefaultRootElement());
        if (defaultNamespace != null) {
            xmlRootElt.setNamespace(defaultNamespace);
        }
        jType.setXmlRootElement(xmlRootElt);
        jType.setJavaAttributes(new JavaAttributes());
        // generate an XmlAttribute or XmlElement for each mapping
        for (Iterator<DatabaseMapping> xmapIt = xdesc.getMappings().iterator(); xmapIt.hasNext();) {
            XMLMapping xMap = (XMLMapping) xmapIt.next();
            if (((XMLField) xMap.getField()).getXPathFragment().isAttribute()) {
                JAXBElement<XmlAttribute> jAtt = generateXmlAttribute(xMap);
                if (jAtt != null) {
                    jType.getJavaAttributes().getJavaAttribute().add(jAtt);
                }
            } else {
                JAXBElement<XmlElement> jElt = generateXmlElement(xMap);
                if (jElt != null) {
                    jType.getJavaAttributes().getJavaAttribute().add(jElt);
                }
            }
        }
        return jType;
    }

    /**
     * Process a given XMLMapping and return a JAXBElement<XmlAttribute>.
     *
     * Expected mappings are:
     * <ul>
     * <li>org.eclipse.persistence.oxm.mappings.XMLBinaryDataMapping
     * <li>org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping
     * <li>org.eclipse.persistence.oxm.mappings.XMLDirectMapping
     * </ul>
     */
    protected static JAXBElement<XmlAttribute> generateXmlAttribute(XMLMapping xmap) {
        XmlAttribute xAtt = null;
        if (xmap instanceof XMLDirectMapping) {
            // could be an XMLBinaryDataMapping instance
            if (xmap instanceof XMLBinaryDataMapping) {
                xAtt = processXMLBinaryDataMappingAttribute((XMLBinaryDataMapping) xmap);
            } else {
                xAtt = processXMLDirectMappingAttribute((XMLDirectMapping) xmap);
            }
        } else if (xmap instanceof XMLCompositeDirectCollectionMapping) {
            xAtt = processXMLCompositeDirectCollectionMappingAttribute((XMLCompositeDirectCollectionMapping) xmap);
        }
        if (xAtt == null) {
            return null;
        }
        return new JAXBElement<XmlAttribute>(new QName("http://www.eclipse.org/eclipselink/xsds/persistence/oxm", "xml-attribute"), XmlAttribute.class, xAtt);
    }

    /**
     * Process a given XMLMapping and return a JAXBElement<XmlElement>.
     *
     * Expected mappings are:
     * <ul>
     * <li>org.eclipse.persistence.oxm.mappings.XMLBinaryDataMapping
     * <li>org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping
     * <li>org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping
     * <li>org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping
     * <li>org.eclipse.persistence.oxm.mappings.XMLDirectMapping
     * </ul>
     */
    protected static JAXBElement<XmlElement> generateXmlElement(XMLMapping xmap) {
        XmlElement xElt = null;
        if (xmap instanceof XMLCompositeObjectMapping) {
            xElt = processXMLCompositeObjectMapping((XMLCompositeObjectMapping) xmap);
        } else if (xmap instanceof XMLCompositeCollectionMapping) {
            xElt = processXMLCompositeCollectionMapping((XMLCompositeCollectionMapping) xmap);
        } else if (xmap instanceof XMLCompositeDirectCollectionMapping) {
            xElt = processXMLCompositeDirectCollectionMapping((XMLCompositeDirectCollectionMapping) xmap);
        } else if (xmap instanceof XMLDirectMapping) {
            // could be an XMLBinaryDataMapping instance
            if (xmap instanceof XMLBinaryDataMapping) {
                xElt = processXMLBinaryDataMapping((XMLBinaryDataMapping) xmap);
            } else {
                xElt = processXMLDirectMapping((XMLDirectMapping) xmap);
            }
        }
        if (xElt == null) {
            return null;
        }
        return new JAXBElement<XmlElement>(new QName("http://www.eclipse.org/eclipselink/xsds/persistence/oxm", "xml-element"), XmlElement.class, xElt);
    }

    /**
     * Process a given XMLMapping and return an XmlAttribute.
     *
     */
    protected static XmlAttribute processXMLMapping(XMLField xfld, String attName, String xpath, String attClassification,
            AbstractNullPolicy nullPolicy, String containerName, boolean inlineBinary, boolean isSWARef, String mimeType) {

        XmlAttribute xAtt = new XmlAttribute();
        xAtt.setJavaAttribute(attName);
        xAtt.setXmlPath(xpath);
        if (xfld.isRequired()) {
            xAtt.setRequired(true);
        }
        if (inlineBinary) {
            xAtt.setXmlInlineBinaryData(true);
        }
        if (isSWARef) {
            xAtt.setXmlAttachmentRef(true);
        }
        if (attClassification != null) {
            xAtt.setType(attClassification);
        }
        if (containerName != null) {
            xAtt.setContainerType(containerName);
        }
        if (mimeType != null) {
            xAtt.setXmlMimeType(mimeType);
        }
        QName schemaType = xfld.getSchemaType();
        if (schemaType != null) {
            XmlSchemaType xSchemaType = new XmlSchemaType();
            xSchemaType.setName(schemaType.getLocalPart());
            xAtt.setXmlSchemaType(xSchemaType);
        }
        if (!isDefaultNullPolicy(nullPolicy)) {
            XmlAbstractNullPolicy xmlNullPolicy;

            if (nullPolicy instanceof NullPolicy) {
                xmlNullPolicy = new XmlNullPolicy();
                ((XmlNullPolicy) xmlNullPolicy).setIsSetPerformedForAbsentNode(nullPolicy.getIsSetPerformedForAbsentNode());
            } else {
                xmlNullPolicy = new XmlIsSetNullPolicy();
            }
            xmlNullPolicy.setEmptyNodeRepresentsNull(nullPolicy.isNullRepresentedByEmptyNode());
            xmlNullPolicy.setXsiNilRepresentsNull(nullPolicy.isNullRepresentedByXsiNil());
            xmlNullPolicy.setNullRepresentationForXml(XmlMarshalNullRepresentation.fromValue(nullPolicy.getMarshalNullRepresentation().toString()));

            xAtt.setXmlAbstractNullPolicy(new JAXBElement<XmlAbstractNullPolicy>(new QName("http://www.eclipse.org/eclipselink/xsds/persistence/oxm", "xml-null-policy"), XmlAbstractNullPolicy.class, xmlNullPolicy));
        }
        return xAtt;
    }

    /**
     * Process a given XMLBinaryDataMapping and return an XmlAttribute.
     *
     */
    protected static XmlAttribute processXMLBinaryDataMappingAttribute(XMLBinaryDataMapping xmap) {
        // JAXB expects a DataHandler in the object model for SwaRef
        String attClassName = xmap.getAttributeClassificationName();
        if (xmap.isSwaRef()) {
            attClassName = DATAHANDLER_CLASSNAME;
        }

        return processXMLMapping(
                (XMLField)xmap.getField(),
                xmap.getAttributeName(),
                xmap.getXPath(),
                attClassName,
                xmap.getNullPolicy(),
                null,
                xmap.shouldInlineBinaryData(),
                xmap.isSwaRef(),
                xmap.getMimeType());
    }


    /**
     * Process a given XMLCompositeDirectCollectionMapping and return an XmlAttribute.
     *
     */
    protected static XmlAttribute processXMLCompositeDirectCollectionMappingAttribute(XMLCompositeDirectCollectionMapping xmap) {
        return processXMLMapping(
                (XMLField)xmap.getField(),
                xmap.getAttributeName(),
                xmap.getXPath(),
                null,
                xmap.getNullPolicy(),
                xmap.getContainerPolicy().getContainerClassName(),
                false,
                false,
                null);
    }

    /**
     * Process a given XMLDirectMapping and return an XmlAttribute.
     *
     */
    protected static XmlAttribute processXMLDirectMappingAttribute(XMLDirectMapping xmap) {
        return processXMLMapping(
                (XMLField)xmap.getField(),
                xmap.getAttributeName(),
                xmap.getXPath(),
                xmap.getAttributeClassificationName(),
                xmap.getNullPolicy(),
                null,
                false,
                false,
                null);
    }

    /**
     * Process a given XMLMapping and return an XmlElement.
     *
     */
    protected static XmlElement processXMLMapping(XMLField xfld, String attName, String xpath, String attClassification,
            AbstractNullPolicy nullPolicy, boolean isCDATA, String containerName, boolean inlineBinary, boolean isSWARef, String mimeType) {

        XmlElement xElt = new XmlElement();
        xElt.setJavaAttribute(attName);
        xElt.setXmlPath(xpath);
        if (xfld.isRequired()) {
            xElt.setRequired(true);
        }
        if (isCDATA) {
            xElt.setCdata(true);
        }
        if (inlineBinary) {
            xElt.setXmlInlineBinaryData(true);
        }
        if (isSWARef) {
            xElt.setXmlAttachmentRef(true);
        }
        if (attClassification != null) {
            xElt.setType(attClassification);
        }
        if (containerName != null) {
            xElt.setContainerType(containerName);
        }
        if (mimeType != null) {
            xElt.setXmlMimeType(mimeType);
        }
        QName schemaType = xfld.getSchemaType();
        if (schemaType != null) {
            XmlSchemaType xSchemaType = new XmlSchemaType();
            xSchemaType.setName(schemaType.getLocalPart());
            xElt.setXmlSchemaType(xSchemaType);
        }
        if (!isDefaultNullPolicy(nullPolicy)) {
            XmlAbstractNullPolicy xmlNullPolicy;

            if (nullPolicy instanceof NullPolicy) {
                xmlNullPolicy = new XmlNullPolicy();
                ((XmlNullPolicy) xmlNullPolicy).setIsSetPerformedForAbsentNode(nullPolicy.getIsSetPerformedForAbsentNode());
            } else {
                xmlNullPolicy = new XmlIsSetNullPolicy();
            }
            xmlNullPolicy.setEmptyNodeRepresentsNull(nullPolicy.isNullRepresentedByEmptyNode());
            xmlNullPolicy.setXsiNilRepresentsNull(nullPolicy.isNullRepresentedByXsiNil());
            xmlNullPolicy.setNullRepresentationForXml(XmlMarshalNullRepresentation.fromValue(nullPolicy.getMarshalNullRepresentation().toString()));

            xElt.setXmlAbstractNullPolicy(new JAXBElement<XmlAbstractNullPolicy>(new QName("http://www.eclipse.org/eclipselink/xsds/persistence/oxm", "xml-null-policy"), XmlAbstractNullPolicy.class, xmlNullPolicy));
        }
        return xElt;
    }

    /**
     * Process a given XMLDirectMapping and return an XmlElement.
     *
     */
    protected static XmlElement processXMLDirectMapping(XMLDirectMapping xmap) {
        return processXMLMapping(
                (XMLField)xmap.getField(),
                xmap.getAttributeName(),
                xmap.getXPath(),
                xmap.getAttributeClassificationName(),
                xmap.getNullPolicy(),
                xmap.isCDATA(),
                null,
                false,
                false,
                null);
    }

    /**
     * Process a given XMLBinaryDataMapping and return an XmlElement.
     *
     */
    protected static XmlElement processXMLBinaryDataMapping(XMLBinaryDataMapping xmap) {
        // JAXB expects a DataHandler in the object model for SwaRef
        String attClassName = xmap.getAttributeClassificationName();
        if (xmap.isSwaRef()) {
            attClassName = DATAHANDLER_CLASSNAME;
        }

        return processXMLMapping(
                (XMLField)xmap.getField(),
                xmap.getAttributeName(),
                xmap.getXPath(),
                attClassName,
                xmap.getNullPolicy(),
                xmap.isCDATA(),
                null,
                xmap.shouldInlineBinaryData(),
                xmap.isSwaRef(),
                xmap.getMimeType());
    }

    /**
     * Process a given XMLCompositeDirectCollectionMapping and return an XmlElement.
     *
     */
    protected static XmlElement processXMLCompositeDirectCollectionMapping(XMLCompositeDirectCollectionMapping xmap) {
        return processXMLMapping(
                (XMLField)xmap.getField(),
                xmap.getAttributeName(),
                xmap.getXPath(),
                null,
                xmap.getNullPolicy(),
                xmap.isCDATA(),
                xmap.getContainerPolicy().getContainerClassName(),
                false,
                false,
                null);
    }

    /**
     * Process a given XMLCompositeObjectMapping and return an XmlElement.
     *
     */
    protected static XmlElement processXMLCompositeObjectMapping(XMLCompositeObjectMapping xmap) {
        return processXMLMapping(
                (XMLField)xmap.getField(),
                xmap.getAttributeName(),
                xmap.getXPath(),
                xmap.getReferenceClassName(),
                xmap.getNullPolicy(),
                false,
                null,
                false,
                false,
                null);
    }

    /**
     * Process a given XMLCompositeCollectionMapping and return an XmlElement
     *
     */
    protected static XmlElement processXMLCompositeCollectionMapping(XMLCompositeCollectionMapping xmap) {
        return processXMLMapping(
                (XMLField)xmap.getField(),
                xmap.getAttributeName(),
                xmap.getXPath(),
                xmap.getReferenceClassName(),
                xmap.getNullPolicy(),
                false,
                xmap.getContainerPolicy().getContainerClassName(),
                false,
                false,
                null);
    }

    /**
     * Convenience methods that returns the package name for a given fully
     * qualified Java class name.
     *
     */
    protected static String getPackageName(String javaClassName) {
        // handle 'default' package
        if (javaClassName.lastIndexOf(DOT) == -1) {
            return EMPTY_STRING;
        }
        return javaClassName.substring(0, javaClassName.lastIndexOf(DOT));
    }

    /**
     * Convenience methods that returns the class name w/o package
     * for a given fully qualified Java class name.
     *
     */
    protected static String getClassName(String javaClassName) {
        // handle 'default' package
        if (javaClassName.lastIndexOf(DOT) == -1) {
            return javaClassName;
        }
        return javaClassName.substring(javaClassName.lastIndexOf(DOT)+1, javaClassName.length());
    }

    /**
     * Indicates is a given AbstractNullPolicy is the default.  This is useful
     * if it is not desirable to write out the default policy in the oxm
     * metadata file.
     *
     * The default policy is NullPolicy, with the following set:
     * <ul>
     * <li>isNullRepresentedByXsiNil = false
     * <li>isNullRepresentedByEmptyNode = true
     * <li>isSetPerformedForAbsentNode = true
     * <li>marshalNullRepresentation = XMLNullRepresentationType.ABSENT_NODE
     * </ul>
     */
    protected static boolean isDefaultNullPolicy(AbstractNullPolicy nullPolicy) {
        // default policy is NullPolicy
        if (nullPolicy instanceof IsSetNullPolicy) {
            return false;
        }
        boolean xsiNil = nullPolicy.isNullRepresentedByXsiNil();
        boolean emptyNode = nullPolicy.isNullRepresentedByEmptyNode();
        boolean setForAbsent = nullPolicy.getIsSetPerformedForAbsentNode();
        XMLNullRepresentationType marshalNull = nullPolicy.getMarshalNullRepresentation();

        return (!xsiNil && emptyNode && setForAbsent && marshalNull == XMLNullRepresentationType.ABSENT_NODE);
    }
}
