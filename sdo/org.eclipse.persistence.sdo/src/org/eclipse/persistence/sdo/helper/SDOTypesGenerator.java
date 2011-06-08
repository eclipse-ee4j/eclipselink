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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.sdo.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import org.eclipse.persistence.exceptions.SDOException;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.oxm.schema.SchemaModelProject;
import org.eclipse.persistence.internal.oxm.schema.model.All;
import org.eclipse.persistence.internal.oxm.schema.model.Annotation;
import org.eclipse.persistence.internal.oxm.schema.model.Any;
import org.eclipse.persistence.internal.oxm.schema.model.Attribute;
import org.eclipse.persistence.internal.oxm.schema.model.AttributeGroup;
import org.eclipse.persistence.internal.oxm.schema.model.Choice;
import org.eclipse.persistence.internal.oxm.schema.model.ComplexContent;
import org.eclipse.persistence.internal.oxm.schema.model.ComplexType;
import org.eclipse.persistence.internal.oxm.schema.model.Element;
import org.eclipse.persistence.internal.oxm.schema.model.Extension;
import org.eclipse.persistence.internal.oxm.schema.model.Group;
import org.eclipse.persistence.internal.oxm.schema.model.Import;
import org.eclipse.persistence.internal.oxm.schema.model.Include;
import org.eclipse.persistence.internal.oxm.schema.model.List;
import org.eclipse.persistence.internal.oxm.schema.model.NestedParticle;
import org.eclipse.persistence.internal.oxm.schema.model.Occurs;
import org.eclipse.persistence.internal.oxm.schema.model.Restriction;
import org.eclipse.persistence.internal.oxm.schema.model.Schema;
import org.eclipse.persistence.internal.oxm.schema.model.Sequence;
import org.eclipse.persistence.internal.oxm.schema.model.SimpleComponent;
import org.eclipse.persistence.internal.oxm.schema.model.SimpleContent;
import org.eclipse.persistence.internal.oxm.schema.model.SimpleType;
import org.eclipse.persistence.internal.oxm.schema.model.TypeDefParticle;
import org.eclipse.persistence.internal.oxm.schema.model.Union;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.extension.SDOUtil;
import org.eclipse.persistence.sdo.types.SDODataType;
import org.eclipse.persistence.sdo.types.SDOWrapperType;
import org.eclipse.persistence.sessions.Project;

import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;

/**
 * <p><b>Purpose</b>: Called from XSDHelper define methods to generate SDO Types from a Schema
 *
 * @see commonj.sdo.XSDHelper
 */
public class SDOTypesGenerator {
    private Project schemaProject;
    private Schema rootSchema;
    private HashMap processedComplexTypes;
    private HashMap processedSimpleTypes;
    private HashMap processedElements;
    private HashMap processedAttributes;
    private Map itemNameToSDOName;
    private boolean processImports;
    private boolean returnAllTypes;
    private java.util.List namespaceResolvers;
    private boolean inRestriction;
    // hold the context containing all helpers so that we can preserve inter-helper relationships
    private HelperContext aHelperContext;

    private java.util.List<SDOType> anonymousTypes;
    private java.util.Map<QName, Type> generatedTypes;
    private java.util.Map<QName, SDOType> generatedTypesByXsdQName;
    private java.util.Map<QName, Property> generatedGlobalElements;
    private java.util.Map<QName, Property> generatedGlobalAttributes;
    private String packageName;
    private java.util.List<NonContainmentReference> nonContainmentReferences;
    private Map<Type, java.util.List<GlobalRef>> globalRefs;
    private boolean isImportProcessor;

    public SDOTypesGenerator(HelperContext aContext) {
        anonymousTypes = new ArrayList<SDOType>();
        generatedTypesByXsdQName = new HashMap<QName, SDOType>();
        processedComplexTypes = new HashMap();
        processedSimpleTypes = new HashMap();
        processedElements = new HashMap();
        processedAttributes = new HashMap();
        itemNameToSDOName = new HashMap();
        namespaceResolvers = new ArrayList();
        this.aHelperContext = aContext;
    }

    public java.util.List<Type> define(Source xsdSource, SchemaResolver schemaResolver) {
        return define(xsdSource, schemaResolver, false, true);
    }

    public java.util.List<Type> define(Source xsdSource, SchemaResolver schemaResolver, boolean includeAllTypes, boolean processImports) {
        Schema schema = getSchema(xsdSource, schemaResolver);
        return define(schema, includeAllTypes, processImports);
    }

    public java.util.List<Type> define(Schema schema, boolean includeAllTypes, boolean processImports) {
        // Initialize the List of Types before we process the schema
        java.util.List<Type> returnList = new ArrayList<Type>();

        setReturnAllTypes(includeAllTypes);
        setProcessImports(processImports);
        processSchema(schema);

        returnList.addAll(getGeneratedTypes().values());
        returnList.addAll(anonymousTypes);

        if (!this.isImportProcessor()) {
            java.util.List descriptorsToAdd = new ArrayList(returnList);
            Iterator<Type> iter = descriptorsToAdd.iterator();
            while (iter.hasNext()) {
                SDOType nextSDOType = (SDOType) iter.next();
                if (!nextSDOType.isFinalized()) {
                    //Only throw this error if we're not processing an import.
                    throw SDOException.typeReferencedButNotDefined(nextSDOType.getURI(), nextSDOType.getName());
                }

                Iterator<Property> propertiesIter = nextSDOType.getProperties().iterator();
                while (propertiesIter.hasNext()) {
                    SDOProperty prop = (SDOProperty) propertiesIter.next();
                    if (prop.getType().isDataType() && prop.isContainment()) {
                        // If isDataType is true, then isContainment has to be false.
                        // This property was likely created as a stub, and isContainment never got reset
                        // when the property was fully defined.
                        // This problem was uncovered in bug 6809767
                        prop.setContainment(false);
                    }
                }

            }
            Iterator<Property> propertiesIter = getGeneratedGlobalElements().values().iterator();
            while (propertiesIter.hasNext()) {
                SDOProperty nextSDOProperty = (SDOProperty) propertiesIter.next();
                if (!nextSDOProperty.isFinalized()) {
                    //Only throw this error if we're not processing an import.
                    throw SDOException.referencedPropertyNotFound(nextSDOProperty.getUri(), nextSDOProperty.getName());
                }
            }

            propertiesIter = getGeneratedGlobalAttributes().values().iterator();
            while (propertiesIter.hasNext()) {
                SDOProperty nextSDOProperty = (SDOProperty) propertiesIter.next();
                if (!nextSDOProperty.isFinalized()) {
                    //Only throw this error if we're not processing an import.
                    throw SDOException.referencedPropertyNotFound(nextSDOProperty.getUri(), nextSDOProperty.getName());
                }
            }

            iter = getGeneratedTypes().values().iterator();
            //If we get here all types were finalized correctly
            while (iter.hasNext()) {
                SDOType nextSDOType = (SDOType) iter.next();
                ((SDOTypeHelper) aHelperContext.getTypeHelper()).addType(nextSDOType);
            }

            Iterator anonymousIterator = getAnonymousTypes().iterator();

            while (anonymousIterator.hasNext()) {
                SDOType nextSDOType = (SDOType) anonymousIterator.next();
                ((SDOTypeHelper) aHelperContext.getTypeHelper()).getAnonymousTypes().add(nextSDOType);
            }

            // add any base types to the list
            for (int i=0; i<descriptorsToAdd.size(); i++) {
                SDOType nextSDOType = (SDOType) descriptorsToAdd.get(i);
                if (!nextSDOType.isDataType() && !nextSDOType.isSubType() && nextSDOType.isBaseType()) {
                    nextSDOType.setupInheritance(null);
                } else if (!nextSDOType.isDataType() && nextSDOType.isSubType() && !getGeneratedTypes().values().contains(nextSDOType.getBaseTypes().get(0))) {
                    SDOType baseType = (SDOType) nextSDOType.getBaseTypes().get(0);
                    while (baseType != null) {
                        descriptorsToAdd.add(baseType);
                        if (baseType.getBaseTypes().size() == 0) {
                            // baseType should now be root of inheritance
                            baseType.setupInheritance(null);
                            baseType = null;
                        } else {
                            baseType = (SDOType) baseType.getBaseTypes().get(0);
                        }
                    }
                }
            }
            
            ((SDOXMLHelper) aHelperContext.getXMLHelper()).addDescriptors(descriptorsToAdd);

            //go through generatedGlobalProperties and add to xsdhelper
            Iterator<QName> qNameIter = getGeneratedGlobalElements().keySet().iterator();
            while (qNameIter.hasNext()) {
                QName nextQName = qNameIter.next();
                SDOProperty nextSDOProperty = (SDOProperty) getGeneratedGlobalElements().get(nextQName);
                ((SDOXSDHelper) aHelperContext.getXSDHelper()).addGlobalProperty(nextQName, nextSDOProperty, true);
            }

            qNameIter = getGeneratedGlobalAttributes().keySet().iterator();
            while (qNameIter.hasNext()) {
                QName nextQName = qNameIter.next();
                SDOProperty nextSDOProperty = (SDOProperty) getGeneratedGlobalAttributes().get(nextQName);
                ((SDOXSDHelper) aHelperContext.getXSDHelper()).addGlobalProperty(nextQName, nextSDOProperty, false);
            }

            Iterator<java.util.List<GlobalRef>> globalRefsIter = getGlobalRefs().values().iterator();
            while (globalRefsIter.hasNext()) {
                java.util.List<GlobalRef> nextList = globalRefsIter.next();
                if (nextList.size() > 0) {
                    GlobalRef ref = nextList.get(0);
                    throw SDOException.referencedPropertyNotFound(((SDOProperty) ref.getProperty()).getUri(), ref.getProperty().getName());
                }
            }
        }

        return returnList;
    }

    private void processSchema(Schema parsedSchema) {
        rootSchema = parsedSchema;
        initialize();
        namespaceResolvers.add(rootSchema.getNamespaceResolver());
        processIncludes(rootSchema.getIncludes());
        processImports(rootSchema.getImports());
        preprocessGlobalTypes(rootSchema);
        processGlobalAttributes(rootSchema);
        processGlobalElements(rootSchema);
        processGlobalSimpleTypes(rootSchema);
        processGlobalComplexTypes(rootSchema);

        postProcessing();
    }

    private void processImports(java.util.List imports) {
        if ((imports == null) || (imports.size() == 0) || !isProcessImports()) {
            return;
        }
        Iterator iter = imports.iterator();
        while (iter.hasNext()) {
            Import nextImport = (Import) iter.next();
            try {
                processImportIncludeInternal(nextImport);
            } catch (Exception e) {
                throw SDOException.errorProcessingImport(nextImport.getSchemaLocation(), nextImport.getNamespace(), e);
            }
        }
    }

    private void processIncludes(java.util.List includes) {
        if ((includes == null) || (includes.size() == 0) || !isProcessImports()) {
            return;
        }
        Iterator iter = includes.iterator();
        while (iter.hasNext()) {
            Include nextInclude = (Include) iter.next();
            try {
                processImportIncludeInternal(nextInclude);
            } catch (Exception e) {
                throw SDOException.errorProcessingInclude(nextInclude.getSchemaLocation(), e);
            }
        }
    }

    /**
     * INTERNAL:
     * This function is referenced by processImport or processInclude possibly recursively
     * @param Include theImportOrInclude
     * @throws Exception
     */
    private void processImportIncludeInternal(Include theImportOrInclude) throws Exception {
        if (theImportOrInclude.getSchema() != null) {
            SDOTypesGenerator generator = new SDOTypesGenerator(aHelperContext);
            generator.setAnonymousTypes(getAnonymousTypes());
            generator.setGeneratedTypes(getGeneratedTypes());
            generator.setGeneratedTypesByXsdQName(getGeneratedTypesByXsdQName());
            generator.setGeneratedGlobalElements(getGeneratedGlobalElements());
            generator.setGeneratedGlobalAttributes(getGeneratedGlobalAttributes());
            // Both imports and includes are treated the same when checking for a mid-schema tree walk state
            generator.setIsImportProcessor(true);
            // May throw an IAE if a global type: local part cannot be null when creating a QName
            java.util.List<Type> importedTypes = generator.define(theImportOrInclude.getSchema(), isReturnAllTypes(), isProcessImports());
            processedComplexTypes.putAll(generator.processedComplexTypes);
            processedSimpleTypes.putAll(generator.processedSimpleTypes);
            processedElements.putAll(generator.processedElements);
            processedAttributes.putAll(generator.processedAttributes);
            if (null != importedTypes) {
                for (int i = 0, size = importedTypes.size(); i < size; i++) {
                    SDOType nextType = (SDOType) importedTypes.get(i);
                    getGeneratedTypes().put(nextType.getQName(), nextType);
                }
            }

            //copy over any global properties
            Iterator<QName> globalPropsIter = generator.getGeneratedGlobalElements().keySet().iterator();
            while (globalPropsIter.hasNext()) {
                QName nextKey = globalPropsIter.next();
                getGeneratedGlobalElements().put(nextKey, generator.getGeneratedGlobalElements().get(nextKey));
            }

            globalPropsIter = generator.getGeneratedGlobalAttributes().keySet().iterator();
            while (globalPropsIter.hasNext()) {
                QName nextKey = globalPropsIter.next();
                getGeneratedGlobalAttributes().put(nextKey, generator.getGeneratedGlobalAttributes().get(nextKey));
            }

            //copy over any unfinished globalRefs
            Iterator<Type> globalRefsIter = generator.getGlobalRefs().keySet().iterator();
            while (globalRefsIter.hasNext()) {
                Type nextKey = globalRefsIter.next();
                getGlobalRefs().put(nextKey, generator.getGlobalRefs().get(nextKey));
            }
        }
    }

    private boolean typesExists(String targetNamespace, String sdoTypeName) {
        boolean alreadyProcessed = false;

        if ((targetNamespace != null) && (targetNamespace.equals(SDOConstants.SDOJAVA_URL) || targetNamespace.equals(SDOConstants.SDO_URL) || targetNamespace.equals(SDOConstants.SDOXML_URL))) {
            alreadyProcessed = true;
        } else {
            QName qname = new QName(targetNamespace, sdoTypeName);
            Object processed = processedComplexTypes.get(qname);
            if (processed != null) {
                alreadyProcessed = true;
            }
        }

        if (!alreadyProcessed) {
            SDOType lookup = (SDOType) aHelperContext.getTypeHelper().getType(targetNamespace, sdoTypeName);
            if ((lookup != null) && lookup.isFinalized()) {
                if (isReturnAllTypes()) {
                    QName qname = new QName(targetNamespace, sdoTypeName);
                    getGeneratedTypes().put(qname, lookup);
                }
                return true;
            } else if (lookup == null) {
                QName qname = new QName(targetNamespace, sdoTypeName);
                SDOType processed = (SDOType) getGeneratedTypes().get(qname);
                if (processed != null && processed.isFinalized()) {
                    alreadyProcessed = true;
                }
            }
        }

        return alreadyProcessed;
    }

    private void processGlobalComplexTypes(Schema schema) {
        Collection complexTypes = schema.getTopLevelComplexTypes().values();
        if (complexTypes == null) {
            return;
        }

        Iterator complexTypesIter = complexTypes.iterator();
        while (complexTypesIter.hasNext()) {
            ComplexType complexType = (ComplexType) complexTypesIter.next();
            String targetNamespace = schema.getTargetNamespace();
            if(null == targetNamespace) {
                targetNamespace = "";
            }
            processGlobalComplexType(targetNamespace, schema.getDefaultNamespace(), complexType);
        }
    }

    private void processGlobalComplexType(String targetNamespace, String defaultNamespace, ComplexType complexType) {
        QName qname = new QName(targetNamespace, complexType.getName());
        Object processed = processedComplexTypes.get(qname);

        if (processed == null) {
            processComplexType(targetNamespace, defaultNamespace, complexType.getName(), complexType);
            processedComplexTypes.put(qname, complexType);
        }
    }

    private SDOType processComplexType(String targetNamespace, String defaultNamespace, String name, ComplexType complexType) {
        if (complexType == null) {
            return null;
        }
        boolean addedNR = addNextNamespaceResolver(complexType.getAttributesMap());
        SDOType newType = startComplexType(targetNamespace, defaultNamespace, name, complexType);
        if (newType != null) {
            if (complexType.getComplexContent() != null) {
                processComplexContent(targetNamespace, defaultNamespace, complexType.getComplexContent(), newType);
                finishComplexType(newType);
            } else if (complexType.getSimpleContent() != null) {
                processSimpleContent(targetNamespace, defaultNamespace, complexType.getSimpleContent(), newType);
                finishComplexType(newType);
            } else {
                if (complexType.getChoice() != null) {
                    processChoice(targetNamespace, defaultNamespace, newType, complexType.getChoice(), false);
                } else if (complexType.getSequence() != null) {
                    processSequence(targetNamespace, defaultNamespace, newType, complexType.getSequence(), false);
                } else if (complexType.getAll() != null) {
                    processAll(targetNamespace, defaultNamespace, newType, complexType.getAll(), false);
                }

                processOrderedAttributes(targetNamespace, defaultNamespace, newType, complexType.getOrderedAttributes());
                finishComplexType(newType);
            }
        }
        if (addedNR) {
            namespaceResolvers.remove(namespaceResolvers.size() - 1);
        }
        
        return newType;
    }

    //return true if a new type was created
    private SDOType startComplexType(String targetNamespace, String defaultNamespace, String name, ComplexType complexType) {
        String nameValue = (String) complexType.getAttributesMap().get(SDOConstants.SDOXML_NAME_QNAME);
        String originalName = name;
        if (nameValue != null) {
            itemNameToSDOName.put(name, nameValue);
            originalName = name;
            name = nameValue;
        }

        //check if already processed, if yes return false because a new type was not started else start new type and return true
        boolean alreadyExists = false;
        if(null != complexType.getName()) {
            alreadyExists = typesExists(targetNamespace, name);
        }

        if (!alreadyExists) {
            return startNewComplexType(targetNamespace, name, originalName, complexType);
        }

        return null;
    }

    private SDOType startNewComplexType(String targetNamespace, String sdoTypeName, String xsdLocalName, ComplexType complexType) {
        SDOType currentType;
        if(null == complexType.getName()) {
            currentType = createSDOTypeForName(targetNamespace, sdoTypeName, xsdLocalName);    		
        } else {
            currentType = getGeneratedTypesByXsdQName().get(new QName(targetNamespace, complexType.getName()));
        }

        if (complexType.isMixed()) {
            currentType.setMixed(true);
            currentType.setSequenced(true);
            // currentType.setOpen(true); Remove as part of SDO JIRA-106
        }

        if (complexType.getAnyAttribute() != null) {
            currentType.setOpen(true);
        }
        currentType.setAbstract(complexType.isAbstractValue());
        currentType.setDataType(false);

        String value = (String) complexType.getAttributesMap().get(SDOConstants.SDOXML_ALIASNAME_QNAME);
        if (value != null) {
            XMLConversionManager xmlConversionManager = ((SDOXMLHelper) aHelperContext.getXMLHelper()).getXmlConversionManager();
            java.util.List names = (java.util.List) xmlConversionManager.convertObject(value, java.util.List.class);
            currentType.setAliasNames(names);
        }

        String sequencedValue = (String) complexType.getAttributesMap().get(SDOConstants.SDOXML_SEQUENCE_QNAME);
        if (sequencedValue != null) {
            currentType.setSequenced(Boolean.valueOf(sequencedValue));
        }
        Annotation annotation = complexType.getAnnotation();
        if (annotation != null) {
            java.util.List documentation = annotation.getDocumentation();
            if ((documentation != null) && (documentation.size() > 0)) {
                currentType.setInstanceProperty(SDOConstants.DOCUMENTATION_PROPERTY, documentation);
            }
        }
        currentType.preInitialize(packageName, namespaceResolvers);
        if (complexType.getAnnotation() != null) {
            currentType.setAppInfoElements(complexType.getAnnotation().getAppInfo());
        }
        
        return currentType;
    }

    private void finishComplexType(SDOType currentType) {
        currentType.postInitialize();
    }

    private void processOrderedAttributes(String targetNamespace, String defaultNamespace, SDOType owningType, java.util.List orderedAttributes) {
        for (int i = 0; i < orderedAttributes.size(); i++) {
            Object next = orderedAttributes.get(i);
            if (next instanceof Attribute) {
                processAttribute(targetNamespace, defaultNamespace, owningType, (Attribute) next, false);
            } else if (next instanceof AttributeGroup) {
                processAttributeGroup(targetNamespace, defaultNamespace, owningType, (AttributeGroup) next);
            }
        }
    }

    private void processGlobalAttributes(Schema schema) {
        Collection attributes = schema.getTopLevelAttributes().values();
        if (attributes == null) {
            return;
        }
        Iterator attributesIter = attributes.iterator();
        while (attributesIter.hasNext()) {
            Attribute nextAttribute = (Attribute) attributesIter.next();
            String targetNamespace = schema.getTargetNamespace();
            if(null == targetNamespace) {
                targetNamespace = "";
            }
            processGlobalAttribute(targetNamespace, schema.getDefaultNamespace(), nextAttribute);
        }
    }

    private void processGlobalAttribute(String targetNamespace, String defaultNamespace, Attribute attribute) {
        if (attribute.getName() != null) {
            QName qname = new QName(targetNamespace, attribute.getName());
            Object processed = processedAttributes.get(qname);

            if (processed == null) {
                SDOType attributeType = processAttribute(targetNamespace, defaultNamespace, null, attribute, true);
                processedAttributes.put(qname, attribute);
                if(null != attributeType && !getGeneratedTypes().containsKey(attributeType.getQName())) {
                    getGeneratedTypes().put(attributeType.getQName(), attributeType);
                    anonymousTypes.remove(attributeType);
                }
            }
        } else {
            processAttribute(targetNamespace, defaultNamespace, null, attribute, true);
        }
    }

    private void processGroup(String targetNamespace, String defaultNamespace, SDOType owningType, TypeDefParticle typeDefParticle, Group group, boolean isMany) {
        if (!isMany && maxOccursGreaterThanOne(group.getMaxOccurs())) {
            isMany = true;
        }

        String groupName = group.getRef();
        if (groupName != null) {
            int idx = groupName.indexOf(":");
            String prefix = null;
            String localName = null;
            String uri = null;
            if (idx > -1) {
                localName = groupName.substring(idx + 1, groupName.length());
                prefix = groupName.substring(0, idx);
                uri = getURIForPrefix(prefix);
            } else {
                localName = groupName;
                uri = targetNamespace;
            }
            Group globalGroup = rootSchema.getGroup(uri, localName);
            if (globalGroup != null) {
                if (globalGroup.getChoice() != null) {
                    globalGroup.getChoice().setMaxOccurs(group.getMaxOccurs());
                    processChoice(targetNamespace, defaultNamespace, owningType, globalGroup.getChoice(), isMany);
                } else if (globalGroup.getSequence() != null) {
                    globalGroup.getSequence().setMaxOccurs(group.getMaxOccurs());
                    processSequence(targetNamespace, defaultNamespace, owningType, globalGroup.getSequence(), isMany);
                } else if (globalGroup.getAll() != null) {
                    globalGroup.getAll().setMaxOccurs(group.getMaxOccurs());
                    processAll(targetNamespace, defaultNamespace, owningType, globalGroup.getAll(), isMany);
                }
            }
        }
    }

    private SDOType processAttribute(String targetNamespace, String defaultNamespace, SDOType owningType, Attribute attribute, boolean isGlobal) {
        SimpleType simpleType = attribute.getSimpleType();
        if (simpleType != null) {
            SDOType propertyType = processSimpleType(targetNamespace, defaultNamespace, attribute.getName(), simpleType);
            processSimpleAttribute(targetNamespace, defaultNamespace, owningType, attribute, isGlobal, rootSchema.isAttributeFormDefault(), propertyType);
            propertyType.setXsdLocalName(attribute.getName());
            propertyType.setXsd(true);
            return propertyType;
        } else {
            processSimpleAttribute(targetNamespace, defaultNamespace, owningType, attribute, isGlobal, rootSchema.isAttributeFormDefault() || isGlobal, null);
            return null;
        }
    }

    private void processAttributeGroup(String targetNamespace, String defaultNamespace, SDOType owningType, AttributeGroup attributeGroup) {
        String attributeGroupName = attributeGroup.getRef();
        if (attributeGroupName != null) {
            int idx = attributeGroupName.indexOf(":");
            String prefix = null;
            String localName = null;
            String uri = null;
            if (idx > -1) {
                localName = attributeGroupName.substring(idx + 1, attributeGroupName.length());
                prefix = attributeGroupName.substring(0, idx);
                uri = getURIForPrefix(prefix);
            } else {
                localName = attributeGroupName;
                uri = targetNamespace;
            }
            AttributeGroup globalAttributeGroup = rootSchema.getAttributeGroup(uri, localName);
            if (globalAttributeGroup != null) {
                int size = globalAttributeGroup.getAttributes().size();
                if (globalAttributeGroup.getAnyAttribute() != null) {
                    processAnyAttribute(targetNamespace, defaultNamespace, owningType);
                }
                for (int j = 0; j < size; j++) {
                    processAttribute(targetNamespace, defaultNamespace, owningType, (Attribute) globalAttributeGroup.getAttributes().get(j), false);
                }
            }
        }
    }

    private void processAttributes(String targetNamespace, String defaultNamespace, SDOType owningType, java.util.List attributes) {
        if (attributes == null) {
            return;
        }
        for (int i = 0; i < attributes.size(); i++) {
            Attribute nextAttribute = (Attribute) attributes.get(i);
            processAttribute(targetNamespace, defaultNamespace, owningType, nextAttribute, false);
        }
    }

    private void processGlobalSimpleTypes(Schema schema) {
        Collection simpleTypes = schema.getTopLevelSimpleTypes().values();
        if (simpleTypes == null) {
            return;
        }

        Iterator simpleTypesIter = simpleTypes.iterator();
        while (simpleTypesIter.hasNext()) {
            SimpleType simpleType = (SimpleType) simpleTypesIter.next();
            String targetNamespace = schema.getTargetNamespace();
            if(null == targetNamespace) {
                targetNamespace = "";
            }
            processGlobalSimpleType(targetNamespace, schema.getDefaultNamespace(), simpleType);
        }
    }

    private void processGlobalSimpleType(String targetNamespace, String defaultNamespace, SimpleType simpleType) {
        QName qname = new QName(targetNamespace, simpleType.getName());
        if (!processedSimpleTypes.containsKey(qname)) {
        	processSimpleType(targetNamespace, defaultNamespace, simpleType.getName(), simpleType);
            processedSimpleTypes.put(qname, simpleType);
        }
    }

    private SDOType startSimpleType(String targetNamespace, String defaultNamespace, String name, String xsdLocalName, SimpleType simpleType) {
        boolean alreadyExists = typesExists(targetNamespace, name);
        if (!alreadyExists) {
            return startNewSimpleType(targetNamespace, defaultNamespace, name, xsdLocalName, simpleType);
        }
        if(this.returnAllTypes) {
            return this.getExisitingType(targetNamespace, name);
        } else {
            return null;
        }
    }

    private SDOType startNewSimpleType(String targetNamespace, String defaultNamespace, String sdoTypeName, String xsdLocalName, SimpleType simpleType) {
        SDOType currentType;
         if(null == simpleType.getName()) {
            currentType = createSDOTypeForName(targetNamespace, sdoTypeName, xsdLocalName);
            currentType.setDataType(true);
        } else {
            currentType = getGeneratedTypesByXsdQName().get(new QName(targetNamespace, simpleType.getName()));
        }

        // Defining a new simple type from XSD.
        // See also: SDOTypeHelperDelegate:define for "types from DataObjects" equivalent

        SDOTypeHelper typeHelper = ((SDOTypeHelper) aHelperContext.getTypeHelper());

        // If this simple type is a restriction, get the QName for the base type and
        // include it when creating the WrapperType.  The QName will be used during
        // conversions (eg. "myBinaryElement" could be a restriction of base64Binary
        // or hexBinary.

        QName baseTypeQName = null;
        if (simpleType.getRestriction() != null) {
            String baseType = simpleType.getRestriction().getBaseType();
            baseTypeQName = this.getQNameForString(defaultNamespace, baseType);
            SDOType baseSDOType = getTypeForXSDQName(baseTypeQName);
            currentType.addBaseType(baseSDOType);

            currentType.setInstanceClass(baseSDOType.getInstanceClass());
        }

        // Create the new WrapperType
        SDOWrapperType wrapperType = new SDOWrapperType(currentType, sdoTypeName, typeHelper, baseTypeQName);

        // Register WrapperType with maps on TypeHelper
        typeHelper.getWrappersHashMap().put(currentType.getQName(), wrapperType);
        typeHelper.getTypesHashMap().put(wrapperType.getQName(), wrapperType);
        typeHelper.getImplClassesToSDOType().put(wrapperType.getXmlDescriptor().getJavaClass(), wrapperType);

        // Add descriptor to XMLHelper
        ArrayList list = new ArrayList(1);
        list.add(wrapperType);
        ((SDOXMLHelper) aHelperContext.getXMLHelper()).addDescriptors(list);

        if (simpleType.getAnnotation() != null) {
            currentType.setAppInfoElements(simpleType.getAnnotation().getAppInfo());
        }
        
        return currentType;
    }

    private SDOType processSimpleType(String targetNamespace, String defaultNamespace, String sdoTypeName, SimpleType simpleType) {
        if (simpleType == null) {
            return null;
        }
        boolean addedNR = addNextNamespaceResolver(simpleType.getAttributesMap());
        String name = sdoTypeName;
        String originalName = name;
        String nameValue = (String) simpleType.getAttributesMap().get(SDOConstants.SDOXML_NAME_QNAME);
        if (nameValue != null) {
            itemNameToSDOName.put(sdoTypeName, nameValue);
            name = nameValue;
        }

        SDOType newType = startSimpleType(targetNamespace, defaultNamespace, name, originalName, simpleType);
        if (newType != null) {
            Restriction restriction = simpleType.getRestriction();

            if (restriction != null) {
                processRestriction(targetNamespace, defaultNamespace, newType, restriction);
            }
            List list = simpleType.getList();
            if (list != null) {
                processList(targetNamespace, defaultNamespace, sdoTypeName, list, newType);
            }

            Union union = simpleType.getUnion();
            if (union != null) {
                processUnion(targetNamespace, defaultNamespace, sdoTypeName, union, newType);
            }

            finishSimpleType(targetNamespace, defaultNamespace, sdoTypeName, simpleType, newType);
        }
        if (addedNR) {
            namespaceResolvers.remove(namespaceResolvers.size() - 1);
        }
        
        return newType;
    }

    private void finishSimpleType(String targetNamespace, String defaultNamespace, String sdoTypeName, SimpleType simpleType, SDOType currentType) {
        String value = (String) simpleType.getAttributesMap().get(SDOConstants.SDOXML_ALIASNAME_QNAME);
        if (value != null) {
            XMLConversionManager xmlConversionManager = ((SDOXMLHelper) aHelperContext.getXMLHelper()).getXmlConversionManager();
            java.util.List names = (java.util.List) xmlConversionManager.convertObject(value, java.util.List.class);
            currentType.setAliasNames(names);
        }

        String instanceClassValue = (String) simpleType.getAttributesMap().get(SDOConstants.SDOJAVA_INSTANCECLASS_QNAME);
        if (instanceClassValue != null) {
            currentType.setInstanceProperty(SDOConstants.JAVA_CLASS_PROPERTY, instanceClassValue);
            currentType.setBaseTypes(null);
        }

        String extendedInstanceClassValue = (String) simpleType.getAttributesMap().get(SDOConstants.SDOJAVA_EXTENDEDINSTANCECLASS_QNAME);
        if (extendedInstanceClassValue != null) {
            currentType.setInstanceClassName(extendedInstanceClassValue);
        }
        currentType.postInitialize();
    }

    private void processChoice(String targetNamespace, String defaultNamespace, SDOType owningType, Choice choice, boolean isMany) {
        if (choice != null) {
            processTypeDef(targetNamespace, defaultNamespace, owningType, choice);

            java.util.List orderedItems = choice.getOrderedElements();
            for (int i = 0; i < orderedItems.size(); i++) {
                Object next = orderedItems.get(i);
                if (!isMany && maxOccursGreaterThanOne(choice.getMaxOccurs())) {
                    isMany = true;
                }

                if (next instanceof Choice) {
                    processChoice(targetNamespace, defaultNamespace, owningType, (Choice) next, isMany);
                } else if (next instanceof Sequence) {
                    processSequence(targetNamespace, defaultNamespace, owningType, (Sequence) next, isMany);
                } else if (next instanceof Any) {
                    processAny(targetNamespace, defaultNamespace, (Any) next, owningType, choice);//isMany??
                } else if (next instanceof Element) {
                    processElement(targetNamespace, defaultNamespace, owningType, choice, (Element) next, false, isMany);
                } else if (next instanceof Group) {
                    processGroup(targetNamespace, defaultNamespace, owningType, choice, (Group) next, isMany);
                }
            }
        }
    }

    private void processSequence(String targetNamespace, String defaultNamespace, SDOType owningType, Sequence sequence, boolean isMany) {
        if (sequence != null) {
            processTypeDef(targetNamespace, defaultNamespace, owningType, sequence);

            java.util.List orderedItems = sequence.getOrderedElements();
            for (int i = 0; i < orderedItems.size(); i++) {
                Object next = orderedItems.get(i);
                if (!isMany && maxOccursGreaterThanOne(sequence.getMaxOccurs())) {
                    isMany = true;
                }
                if (next instanceof Choice) {
                    processChoice(targetNamespace, defaultNamespace, owningType, (Choice) next, isMany);
                } else if (next instanceof Sequence) {
                    processSequence(targetNamespace, defaultNamespace, owningType, (Sequence) next, isMany);
                } else if (next instanceof Any) {
                    processAny(targetNamespace, defaultNamespace, (Any) next, owningType, sequence);//isMany?
                } else if (next instanceof Element) {
                    processElement(targetNamespace, defaultNamespace, owningType, sequence, (Element) next, false, isMany);
                } else if (next instanceof Group) {
                    processGroup(targetNamespace, defaultNamespace, owningType, sequence, (Group) next, isMany);
                }
            }
        }
    }

    private void processAll(String targetNamespace, String defaultNamespace, SDOType owningType, All all, boolean isMany) {
        if (all != null) {
            owningType.setSequenced(true);
            
            processTypeDef(targetNamespace, defaultNamespace, owningType, all);
            if (!isMany && maxOccursGreaterThanOne(all.getMaxOccurs())) {
                isMany = true;
            }
            java.util.List elements = all.getElements();
            for (int i = 0; i < elements.size(); i++) {
                Object next = elements.get(i);
                if (next instanceof Element) {
                    processElement(targetNamespace, defaultNamespace, owningType, all, (Element) next, false, isMany);
                }
            }
        }
    }

    private void processComplexContent(String targetNamespace, String defaultNamespace, ComplexContent complexContent, SDOType owningType) {
        if (complexContent != null) {
            if (complexContent.getExtension() != null) {
                processExtension(targetNamespace, defaultNamespace, owningType, complexContent.getExtension(), false);
            } else if (complexContent.getRestriction() != null) {
                processRestriction(targetNamespace, defaultNamespace, owningType, complexContent.getRestriction());
            }
        }
    }

    private void processSimpleContent(String targetNamespace, String defaultNamespace, SimpleContent simpleContent, SDOType owningType) {
        if (simpleContent != null) {
            if (simpleContent.getExtension() != null) {
                processExtension(targetNamespace, defaultNamespace, owningType, simpleContent.getExtension(), true);
            } else {
                if (simpleContent.getRestriction() != null) {
                    processRestriction(targetNamespace, defaultNamespace, owningType, simpleContent.getRestriction());
                }
            }
        }
    }

    private void processExtension(String targetNamespace, String defaultNamespace, SDOType owningType, Extension extension, boolean simpleContent) {
        if (extension != null) {
            String qualifiedType = extension.getBaseType();
            SDOType baseType = getSDOTypeForName(targetNamespace, defaultNamespace, qualifiedType);
            QName baseQName = getQNameForString(defaultNamespace, qualifiedType);

            if(baseType != null) {
                if(!baseType.isFinalized() && baseQName.getNamespaceURI().equals(targetNamespace)) {
                    if(baseType.isDataType()) {
                        SimpleType baseSimpleType = (SimpleType) rootSchema.getTopLevelSimpleTypes().get(baseQName.getLocalPart());
                        if(baseSimpleType != null){
                            processGlobalSimpleType(targetNamespace, defaultNamespace, baseSimpleType);
                        }
                    } else {
                        ComplexType baseComplexType = (ComplexType) rootSchema.getTopLevelComplexTypes().get(baseQName.getLocalPart());
                        if(baseComplexType != null){
                            processGlobalComplexType(targetNamespace, defaultNamespace, baseComplexType);
                        }
                    }
                }
                owningType.setOpen(owningType.isOpen() || baseType.isOpen());
            } 
            if (qualifiedType != null) {
                processBaseType(baseType, targetNamespace, defaultNamespace, owningType, qualifiedType, simpleContent);
            }
            if (extension.getChoice() != null) {
                processChoice(targetNamespace, defaultNamespace, owningType, extension.getChoice(), false);
            } else if (extension.getSequence() != null) {
                processSequence(targetNamespace, defaultNamespace, owningType, extension.getSequence(), false);
            } else if (extension.getAll() != null) {
            }

            processOrderedAttributes(targetNamespace, defaultNamespace, owningType, extension.getOrderedAttributes());
        }
    }

    private void processRestriction(String targetNamespace, String defaultNamespace, SDOType owningType, Restriction restriction) {
        if (restriction != null) {
            String qualifiedType = restriction.getBaseType();
            Type baseType = processBaseType(targetNamespace, defaultNamespace, owningType, qualifiedType, false, restriction);
            boolean alreadyIn = inRestriction;
            if (!alreadyIn) {
                inRestriction = true;
            }

            if (restriction.getChoice() != null) {
                processChoice(targetNamespace, defaultNamespace, owningType, restriction.getChoice(), false);
            } else if (restriction.getSequence() != null) {
                processSequence(targetNamespace, defaultNamespace, owningType, restriction.getSequence(), false);
            } else if (restriction.getAll() != null) {
                processAll(targetNamespace, defaultNamespace, owningType, restriction.getAll(), false);
            }

            processAttributes(targetNamespace, defaultNamespace, owningType, restriction.getAttributes());
            if (!alreadyIn) {
                inRestriction = false;
            }
            owningType.setOpen(owningType.isOpen() || baseType.isOpen());
        }
    }

    private void processUnion(String targetNamespace, String defaultNamespace, String sdoTypeName, Union union, SDOType type) {
        if (union != null) {
            java.util.List allMemberTypes = union.getAllMemberTypes();
            String firstInstanceClassName = null;
            for (int i = 0; i < allMemberTypes.size(); i++) {
                String nextMemberType = (String) allMemberTypes.get(i);
                SDOType typeForMemberType = getTypeForName(targetNamespace, defaultNamespace, nextMemberType);
                QName baseQName = this.getQNameForString(defaultNamespace, nextMemberType);
                if(!typeForMemberType.isFinalized() && baseQName.getNamespaceURI().equals(targetNamespace)) {
                    SimpleType baseSimpleType = (SimpleType) rootSchema.getTopLevelSimpleTypes().get(baseQName.getLocalPart());
                    processSimpleType(targetNamespace, defaultNamespace, baseQName.getLocalPart(), baseSimpleType);
                }
                if (i == 0) {
                    firstInstanceClassName = typeForMemberType.getInstanceClassName();
                    if (firstInstanceClassName == null) {
                        break;
                    }
                } else {
                    String nextClassName = typeForMemberType.getInstanceClassName();
                    if (!firstInstanceClassName.equals(nextClassName)) {
                        type.setInstanceClass(java.lang.Object.class);
                        return;
                    }
                }

            }
            if (firstInstanceClassName != null) {
                type.setInstanceClassName(firstInstanceClassName);
            } else {
                type.setInstanceClass(java.lang.Object.class);
            }
        }
    }

    private boolean addNextNamespaceResolver(Map attributesMap) {
        NamespaceResolver nr = new NamespaceResolver();
        Iterator iter = attributesMap.keySet().iterator();
        while (iter.hasNext()) {
            QName key = (QName) iter.next();
            if (key.getNamespaceURI().equals(XMLConstants.XMLNS_URL)) {
                String value = (String) attributesMap.get(key);
                String prefix = key.getLocalPart();
                int index = prefix.indexOf(':');
                if (index > -1) {
                    prefix = prefix.substring(index + 1, prefix.length());
                }
                nr.put(prefix, value);
            }
        }
        if (nr.getPrefixes().hasMoreElements()) {
            namespaceResolvers.add(nr);
            return true;
        }
        return false;
    }

    private void processList(String targetNamespace, String defaultNamespace, String sdoTypeName, List list, SDOType type) {
        if (list != null) {
            type.setXsdList(true);
            type.setInstanceClass(ClassConstants.List_Class);
        }
    }

    private Type processBaseType(String targetNamespace, String defaultNamespace, SDOType owningType, String qualifiedName, boolean simpleContentExtension, Restriction restriction) {
        if (qualifiedName == null) {
            return null;
        }

        SDOType baseType = getSDOTypeForName(targetNamespace, defaultNamespace, qualifiedName);
        QName baseQName = getQNameForString(defaultNamespace, qualifiedName);

        if(!baseType.isFinalized() && baseQName.getNamespaceURI().equals(targetNamespace)) {
            if(baseType.isDataType()) {
                SimpleType baseSimpleType = (SimpleType) rootSchema.getTopLevelSimpleTypes().get(baseQName.getLocalPart());
                processGlobalSimpleType(targetNamespace, defaultNamespace, baseSimpleType);
            } else {
                ComplexType baseComplexType = (ComplexType) rootSchema.getTopLevelComplexTypes().get(baseQName.getLocalPart());
                processGlobalComplexType(targetNamespace, defaultNamespace, baseComplexType);
            }
        }

        // When the XSD type is one of the following, and there are facets (maxInclusive, 
        // maxExclusive) constraining the range to be within the range of int then the 
        // Java instance class is int
        if (baseQName.equals(XMLConstants.INTEGER_QNAME) ||
                baseQName.equals(SDOConstants.POSITIVEINTEGER_QNAME) ||
                baseQName.equals(SDOConstants.NEGATIVEINTEGER_QNAME) ||
                baseQName.equals(SDOConstants.NONPOSITIVEINTEGER_QNAME) ||
                baseQName.equals(SDOConstants.NONNEGATIVEINTEGER_QNAME) ||
                baseQName.equals(XMLConstants.LONG_QNAME) ||
                baseQName.equals(SDOConstants.UNSIGNEDLONG_QNAME)) {

            boolean alreadySet = false;
            String value = restriction.getMaxInclusive();
            if (value != null) {
                if (Integer.parseInt(value) <= Integer.MAX_VALUE) {
                    baseType = getTypeForXSDQName(XMLConstants.INT_QNAME);
                    alreadySet = true;
                }
            }
            // if maxInclusive was processed, no need to handle maxExclusive 
            if (!alreadySet) {
                value = restriction.getMaxExclusive();
                if (value != null) {
                    if (Integer.parseInt(value) < Integer.MAX_VALUE) {
                        baseType = getTypeForXSDQName(XMLConstants.INT_QNAME);
                    }
                }
            }
        }

        processBaseType(baseType, targetNamespace, defaultNamespace, owningType, qualifiedName, simpleContentExtension);
        return baseType;
    }

    private void processBaseType(SDOType baseType, String targetNamespace, String defaultNamespace, SDOType owningType, String qualifiedName, boolean simpleContentExtension) {
        if (simpleContentExtension && baseType.isDataType()) {
            if (owningType != null) {
                SDOProperty prop = new SDOProperty(aHelperContext);
                prop.setName("value");
                prop.setType(baseType);
                prop.setValueProperty(true);
                prop.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
                owningType.addDeclaredProperty(prop);
                prop.buildMapping(null, -1);
                prop.setFinalized(true);

            }
            return;
        }

        java.util.List<Type> baseTypes = new ArrayList<Type>();
        baseTypes.add(baseType);

        if (owningType != null) {
            if (owningType.isDataType()) {
                owningType.setInstanceClassName(baseType.getInstanceClassName());
                if (baseType.getInstanceClass() != null) {
                    owningType.setInstanceClass(baseType.getInstanceClass());
                }

                QName baseQName = getQNameForString(defaultNamespace, qualifiedName);
                if ((baseQName.equals(XMLConstants.BASE_64_BINARY_QNAME)) || (baseQName.equals(XMLConstants.HEX_BINARY_QNAME)) || (baseQName.equals(XMLConstants.DATE_QNAME)) || (baseQName.equals(XMLConstants.TIME_QNAME))
                        || (baseQName.equals(XMLConstants.DATE_TIME_QNAME))) {
                    owningType.setXsdType(baseQName);
                }
            }

            if (!owningType.getBaseTypes().contains(baseType)) {
                owningType.addBaseType(baseType);
            }
        }
    }

    private void processTypeDef(String targetNamespace, String defaultNamespace, SDOType owningType, TypeDefParticle typeDefParticle) {
        if (maxOccursGreaterThanOne(typeDefParticle.getMaxOccurs())) {
            if (!owningType.isSequenced() && shouldBeSequenced(typeDefParticle)) {
                owningType.setSequenced(true);
            }
        }
    }

    private boolean shouldBeSequenced(TypeDefParticle typeDefParticle) {
        java.util.List elements = typeDefParticle.getElements();
        if ((elements != null) && (elements.size() > 1)) {
            return true;
        }
        if (typeDefParticle instanceof Sequence) {
            java.util.List allItems = ((Sequence) typeDefParticle).getOrderedElements();
            for (int i = 0; i < allItems.size(); i++) {
                Object nextItem = allItems.get(i);
                if (nextItem instanceof TypeDefParticle) {
                    boolean sequenced = shouldBeSequenced((TypeDefParticle) nextItem);
                    if (sequenced) {
                        return true;
                    }
                }
            }
        } else if (typeDefParticle instanceof Choice) {
            java.util.List allItems = ((Choice) typeDefParticle).getOrderedElements();
            for (int i = 0; i < allItems.size(); i++) {
                Object nextItem = allItems.get(i);
                if (nextItem instanceof TypeDefParticle) {
                    boolean sequenced = shouldBeSequenced((TypeDefParticle) nextItem);
                    if (sequenced) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void processAny(String targetNamespace, String defaultNamespace, Any any, SDOType owningType, TypeDefParticle typeDefParticle) {
        if (any == null) {
            return;
        }

        if (((NestedParticle) typeDefParticle).hasAny()) {
            owningType.setOpen(true);
            owningType.setSequenced(true);
        }

    }

    private void processGlobalElements(Schema schema) {
        Collection elements = schema.getTopLevelElements().values();
        if (elements == null) {
            return;
        }
        Iterator elementsIter = elements.iterator();
        while (elementsIter.hasNext()) {
            Element nextElement = (Element) elementsIter.next();
            String targetNamespace = schema.getTargetNamespace();
            if(null == targetNamespace) {
                targetNamespace = "";
            }
            processGlobalElement(targetNamespace, schema.getDefaultNamespace(), nextElement);
        }
        //process substitution groups after properties have been created for all elements
        processSubstitutionGroups(elements, schema.getTargetNamespace(), schema.getDefaultNamespace());
    }

    private void processGlobalElement(String targetNamespace, String defaultNamespace, Element element) {
        if (element.getName() != null) {
            QName qname = new QName(targetNamespace, element.getName());
            Object processed = processedElements.get(qname);

            if (processed == null) {
                SDOType elementType = processElement(targetNamespace, defaultNamespace, null, null, element, true, true);
                processedElements.put(qname, element);
                if(null != elementType && !getGeneratedTypes().containsKey(elementType.getQName())) {
                    getGeneratedTypes().put(elementType.getQName(), elementType);
                    anonymousTypes.remove(elementType);
                }
            }
        } else {
            processElement(targetNamespace, defaultNamespace, null, null, element, true, true);
        }
    }

    private SDOType processElement(String targetNamespace, String defaultNamespace, SDOType owningType, TypeDefParticle typeDefParticle, Element element, boolean isGlobal, boolean isMany) {
        SDOType type = null;
        boolean addedNR = addNextNamespaceResolver(element.getAttributesMap());

        ComplexType complexType = element.getComplexType();
        boolean qualified = true;
        if (!isGlobal) {
            qualified = rootSchema.isElementFormDefault();
        }
        if (!isMany && maxOccursGreaterThanOne(element.getMaxOccurs())) {
            isMany = true;
        }

        if (complexType != null) {
            type = processComplexType(targetNamespace, defaultNamespace, element.getName(), complexType);
            type.setXsdLocalName(element.getName());
            type.setXsd(true);
            processSimpleElement(targetNamespace, defaultNamespace, owningType, type, typeDefParticle, element, qualified, isGlobal, isMany);
        } else if (element.getSimpleType() != null) {
            type = processSimpleType(targetNamespace, defaultNamespace, element.getName(), element.getSimpleType());
            type.setXsdLocalName(element.getName());
            type.setXsd(true);
            processSimpleElement(targetNamespace, defaultNamespace, owningType, type, typeDefParticle, element, qualified, isGlobal, isMany);
        } else {
            processSimpleElement(targetNamespace, defaultNamespace, owningType, null, typeDefParticle, element, qualified, isGlobal, isMany);
        }
        if (addedNR) {
            namespaceResolvers.remove(namespaceResolvers.size() - 1);
        }
        return type;
    }

    private void processSimpleElement( //
            String targetNamespace,//
            String defaultNamespace,//
            SDOType owningType,//
            SDOType sdoPropertyType,//
            TypeDefParticle typeDefParticle,//
            Element element,//
            boolean isQualified,//
            boolean isGlobal,//
            boolean isMany) {
        if (element == null) {
            return;
        }

        String manyValue = (String) element.getAttributesMap().get(SDOConstants.SDOXML_MANY_QNAME);
        if (manyValue != null) {
            isMany = Boolean.valueOf(manyValue);
        }

        SDOProperty p = null;
        String typeName = null;
        String mappingUri = null;
        if (typeDefParticle != null) {
            mappingUri = owningType.getURI();
        }

        if (element.getRef() != null) {
            String ref = element.getRef();
            String localName = null;
            String uri = null;

            int index = ref.indexOf(':');
            if (index != -1) {
                String prefix = ref.substring(0, index);
                localName = ref.substring(index + 1, ref.length());
                uri = getURIForPrefix(prefix);
            } else {
                localName = ref;
                uri = defaultNamespace;
            }

            Property lookedUp = owningType.getProperty(localName);
            if (lookedUp != null) {
                if (inRestriction) {
                    return;
                }
                updateCollisionProperty(owningType, (SDOProperty) lookedUp);
            } else {
                SDOProperty theProp = new SDOProperty(aHelperContext);
                theProp.setName(localName);

                theProp.setGlobal(false);
                theProp.setContainment(true);
                theProp.setXsd(true);
                theProp.setMany(isMany);
                theProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
                if (element.getAnnotation() != null) {
                    java.util.List doc = element.getAnnotation().getDocumentation();
                    if (doc != null) {
                        theProp.setInstanceProperty(SDOConstants.DOCUMENTATION_PROPERTY, doc);
                    }
                }

                owningType.addDeclaredProperty(theProp);

                GlobalRef globalRef = new GlobalRef();
                globalRef.setProperty(theProp);

                globalRef.setIsElement(true);
                globalRef.setOwningType(owningType);
                globalRef.setUri(uri);
                globalRef.setLocalName(localName);
                addGlobalRef(globalRef);
            }
            return;
        } else {
            if (isGlobal) {
                SDOProperty lookedUpProp = getExistingGlobalProperty(targetNamespace, element.getName(), true);
                if (lookedUpProp != null && lookedUpProp.isFinalized()) {
                    return;
                }
            }
            p = createNewProperty(targetNamespace, element.getName(), isQualified, isGlobal, true, element.isNillable(), element.getAnnotation());
            if (element.getAnnotation() != null) {
                p.setAppInfoElements(element.getAnnotation().getAppInfo());
            }

            if (element.getType() != null) {
                typeName = element.getType();
                p.setName(element.getName());
                QName qname = getQNameForString(defaultNamespace, typeName);
                if (isGlobal) {
                    // only process the root schema's global items if qname uri == target namespace
                    if (qname.getNamespaceURI().equals(targetNamespace)) {
                        //if type is found set it other wise process new type
                        processGlobalItem(targetNamespace, defaultNamespace, qname.getLocalPart());
                    }
                }

                if ((qname.equals(XMLConstants.QNAME_QNAME)) || (qname.equals(XMLConstants.BASE_64_BINARY_QNAME)) || (qname.equals(XMLConstants.HEX_BINARY_QNAME)) || (qname.equals(XMLConstants.DATE_QNAME)) || (qname.equals(XMLConstants.TIME_QNAME)) || (qname.equals(XMLConstants.DATE_TIME_QNAME))) {
                    p.setXsdType(qname);
                }

                String mimeType = (String) element.getAttributesMap().get(SDOConstants.XML_MIME_TYPE_QNAME);
                if (mimeType != null) {
                    p.setInstanceProperty(SDOConstants.MIME_TYPE_PROPERTY, mimeType);
                }
                String mimeTypePropName = (String) element.getAttributesMap().get(SDOConstants.XML_MIME_TYPE_PROPERTY_QNAME);
                if (mimeTypePropName != null) {
                    p.setInstanceProperty(SDOConstants.MIME_TYPE_PROPERTY_PROPERTY, mimeTypePropName);
                }

                sdoPropertyType = getSDOTypeForName(targetNamespace, defaultNamespace, typeName);
                if(!sdoPropertyType.isFinalized() && qname.getNamespaceURI().equals(targetNamespace)) {
                    if(sdoPropertyType.isDataType()) {
                        SimpleType baseSimpleType = (SimpleType) rootSchema.getTopLevelSimpleTypes().get(qname.getLocalPart());
                        processGlobalSimpleType(targetNamespace, defaultNamespace, baseSimpleType);
                    }
                } 

                if ((p.getXsdType() == null) && (sdoPropertyType.getXsdType() != null)) {
                    p.setXsdType(sdoPropertyType.getXsdType());
                }

                if (sdoPropertyType.isDataType()) {
                    p.setContainment(false);
                }
            } else if (element.getComplexType() != null) {
                typeName = element.getName();
                p.setName(element.getComplexType().getNameOrOwnerName());
            } else if (element.getSimpleType() != null) {
                typeName = element.getName();
                p.setName(element.getName());
                if (sdoPropertyType.isDataType()) {
                    p.setContainment(false);
                }
            } else {
                //we assume the xsd type to be any simple type
                p.setName(element.getName());
                sdoPropertyType = ((SDOTypeHelper) aHelperContext.getTypeHelper()).getSDOTypeFromXSDType(SDOConstants.ANY_TYPE_QNAME);
            }
        }
        sdoPropertyType = processSimpleComponentAnnotations(owningType, element, p, targetNamespace, defaultNamespace, sdoPropertyType);

        p.setType(sdoPropertyType);
        setDefaultValue(p, element);

        p.setMany(isMany);

        if (p.getType().isChangeSummaryType()) {
            p.setReadOnly(true);
        }

        if (typeDefParticle != null) {
            updateOwnerAndBuildMapping(owningType, p, defaultNamespace, targetNamespace, element, typeName, mappingUri);
        }
        if (isGlobal) {
            //we have a global element
            addRootElementToDescriptor(p, targetNamespace, element.getName());
        }
        p.setFinalized(true);
    }

    private SDOProperty processRef(GlobalRef globalRef) {
        boolean isElement = globalRef.isElement();
        SDOProperty p = null;

        SDOProperty refProp = getExistingGlobalProperty(globalRef.getUri(), globalRef.getLocalName(), isElement);

        if (refProp != null && refProp.isFinalized()) {
            p = (SDOProperty) globalRef.getProperty();
            p.setValueProperty(refProp.isValueProperty());
            p.setNullable(refProp.isNullable());
            p.setName(refProp.getName());
            p.setXsdLocalName(refProp.getXsdLocalName());
            p.setNamespaceQualified(refProp.isNamespaceQualified());
            if (refProp.hasAliasNames()) {
                p.setAliasNames(refProp.getAliasNames());
            }
            p.setDefault(refProp.getDefault());
            p.setSubstitutable(refProp.isSubstitutable());
            p.setSubstitutableElements(refProp.getSubstitutableElements());

            if (p.getType() == null) {
                p.setType(refProp.getType());
                if (refProp.getType().isDataType()) {
                    p.setContainment(false);
                }
            }
            p.setOpposite(refProp.getOpposite());
            p.setReadOnly(refProp.isReadOnly());
            p.setXsd(refProp.isXsd());
            p.setAppInfoElements(refProp.getAppInfoElements());

            int index = ((SDOProperty) globalRef.getProperty()).getIndexInDeclaredProperties();
            p.buildMapping(globalRef.getUri(), index);
            p.setFinalized(true);
        } else {
            if (isImportProcessor) {
                p = new SDOProperty(aHelperContext);
                p.setGlobal(true);
                p.setUri(globalRef.getUri());
                p.setName(globalRef.getLocalName());
                QName qname = new QName(globalRef.getUri(), globalRef.getLocalName());
                if (isElement) {
                    p.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
                    getGeneratedGlobalElements().put(qname, p);
                } else {
                    getGeneratedGlobalAttributes().put(qname, p);
                }
            } else {
                throw SDOException.referencedPropertyNotFound(globalRef.getUri(), globalRef.getLocalName());
            }
        }
        return p;
    }

    private void updateCollisionProperty(SDOType owningType, SDOProperty p) {
        owningType.setSequenced(true);
        SDOType baseType = owningType;
        while (baseType.isSubType()) {
            baseType = (SDOType) baseType.getBaseTypes().get(0);
            baseType.setSequenced(true);
        }
        p.setNameCollision(true);
        p.setType(SDOConstants.SDO_OBJECT);
        p.setContainment(true);
        p.setMany(true);
        p.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
    }

    private SDOProperty createNewProperty(String targetNamespace, String xsdLocalName, boolean isQualified, boolean isGlobal, boolean isElement, boolean isNillable, Annotation annotation) {
        SDOProperty p = null;
        if (isGlobal) {
            p = getExistingGlobalProperty(targetNamespace, xsdLocalName, isElement);
        }
        if (p == null) {
            p = new SDOProperty(aHelperContext);
        }
        p.setGlobal(isGlobal);
        p.setXsd(true);
        p.setNullable(isNillable);
        if (isElement) {
            p.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        }
        p.setXsdLocalName(xsdLocalName);
        p.setNamespaceQualified(isQualified);
        p.setContainment(true);
        if (isGlobal) {
            QName qname = new QName(targetNamespace, xsdLocalName);
            if (isElement) {
                getGeneratedGlobalElements().put(qname, p);
            } else {
                getGeneratedGlobalAttributes().put(qname, p);
            }
        }

        if (annotation != null) {
            java.util.List documentation = annotation.getDocumentation();
            if ((documentation != null) && (documentation.size() > 0)) {
                p.setInstanceProperty(SDOConstants.DOCUMENTATION_PROPERTY, documentation);
            }
        }

        return p;
    }

    private void addRootElementToDescriptor(SDOProperty p, String targetNamespace, String xsdName) {
        if (!p.getType().isDataType()) {
            NamespaceResolver nr = p.getType().getXmlDescriptor().getNamespaceResolver();
            String prefix = null;
            if (nr != null) {
                prefix = nr.resolveNamespaceURI(targetNamespace);
            }
            if ((prefix == null) || prefix.equals(SDOConstants.EMPTY_STRING)) {
                p.getType().getXmlDescriptor().addRootElement(xsdName);
            } else {
                p.getType().getXmlDescriptor().addRootElement(prefix + //
                        SDOConstants.SDO_XPATH_NS_SEPARATOR_FRAGMENT + xsdName);
            }
        } else {
            // Types from Schema: isDataType() == true so:
            //   - find the SDOWrapperType from TypeHelper's WrappersHashMap
            //   - set the root element name on it
            //   - add the descriptor to XMLContext's DescriptorsByQName map

            // See also: SDOTypeHelperDelegate:defineOpenContentProperty

            SDOTypeHelper typeHelper = (SDOTypeHelper) aHelperContext.getTypeHelper();

            SDOWrapperType wrapperType = (SDOWrapperType) typeHelper.getWrappersHashMap().get(p.getType().getQName());

            XMLDescriptor d = wrapperType.getXmlDescriptor(p.getXsdType());
            if (wrapperType != null) {
                d.addRootElement(xsdName);

                QName descriptorQname = new QName(targetNamespace, xsdName);
                ((SDOXMLHelper) aHelperContext.getXMLHelper()).getXmlContext().addDescriptorByQName(descriptorQname, d);
            }
        }
    }

    private void updateOwnerAndBuildMapping(SDOType owningType, SDOProperty p, String defaultNamespace, String targetNamespace, SimpleComponent simpleComponent, String typeName, String mappingUri) {
        boolean buildMapping = true;
        Property lookedUp = owningType.getProperty(p.getName());

        if (lookedUp != null) {
            p = (SDOProperty) lookedUp;
            if (inRestriction) {
                return;
            }
            updateCollisionProperty(owningType, p);
        } else {
            owningType.addDeclaredProperty(p);
        }

        QName xsdType = getQNameForString(defaultNamespace, typeName);

        if ((xsdType != null) && xsdType.getNamespaceURI().equals(XMLConstants.SCHEMA_URL)) {
            if (xsdType.getLocalPart().equals(SDOConstants.ID)) {
                owningType.setInstanceProperty(SDOConstants.ID_PROPERTY, p.getName());
            } else if (xsdType.getLocalPart().equals(SDOConstants.IDREF)) {
                p.setContainment(false);
                String propertyTypeValue = (String) simpleComponent.getAttributesMap().get(SDOConstants.SDOXML_PROPERTYTYPE_QNAME);
                if (propertyTypeValue != null) {
                    buildMapping = false;
                }
            } else if (xsdType.getLocalPart().equals(SDOConstants.IDREFS)) {
                p.setContainment(false);
                String propertyTypeValue = (String) simpleComponent.getAttributesMap().get(SDOConstants.SDOXML_PROPERTYTYPE_QNAME);
                if (propertyTypeValue != null) {
                    buildMapping = false;
                    p.setMany(true);
                } else {
                    p.setMany(false);
                }
            }
        }

        if (buildMapping) {
            p.buildMapping(mappingUri);
        }
    }

    private void setDefaultValue(SDOProperty p, SimpleComponent sc) {
        if (sc.getFixed() != null) {
            Object convertedValue = convertDefaultValue(p.getType(), sc.getFixed());
            p.setDefault(convertedValue);
        } else if (sc.getDefaultValue() != null) {
            Object convertedValue = convertDefaultValue(p.getType(), sc.getDefaultValue());
            p.setDefault(convertedValue);
        } else {
            if (p.getType().getQName().equals(SDOConstants.SDO_STRING.getQName()) && sc.isSetDefaultValue()) {
                p.setDefault("");
            }
        }
    }

    private Object convertDefaultValue(Type type, String stringValue) {
        if (type != null) {
            Class javaClass = ((SDOTypeHelper) aHelperContext.getTypeHelper()).getJavaWrapperTypeForSDOType(type);
            if (javaClass != null) {
                Object objectValue = ((SDODataHelper) aHelperContext.getDataHelper()).convertFromStringValue(stringValue, javaClass);
                return objectValue;
            }
        }
        return stringValue;
    }

    private void processSimpleAttribute(String targetNamespace, String defaultNamespace, SDOType owningType, Attribute attribute, boolean isGlobal, boolean isQualified, SDOType sdoPropertyType) {
        if (attribute == null) {
            return;
        }

        SDOProperty p = null;
        
        String typeName = null;
        String mappingUri = null;
        if (owningType != null) {
            mappingUri = owningType.getURI();
        }
        if (attribute.getRef() != null) {
            String ref = attribute.getRef();

            String localName = null;
            String uri = null;

            int index = ref.indexOf(':');
            if (index != -1) {
                String prefix = ref.substring(0, index);
                localName = ref.substring(index + 1, ref.length());
                uri = getURIForPrefix(prefix);
            } else {
                localName = ref;
                uri = defaultNamespace;
            }

            Property lookedUp = owningType.getProperty(localName);
            if (lookedUp != null) {
                if (inRestriction) {
                    return;
                }
                updateCollisionProperty(owningType, (SDOProperty) lookedUp);
            } else {
                SDOProperty theProp = new SDOProperty(aHelperContext);
                SDOProperty lookedUpProp = getExistingGlobalProperty(uri, localName, false);
                if (lookedUpProp != null) {
                    theProp.setNamespaceQualified(lookedUpProp.isNamespaceQualified());
                    theProp.setUri(uri);
                    theProp.setType(lookedUpProp.getType());
                }
                theProp.setName(localName);
                theProp.setGlobal(false);
                theProp.setContainment(false);
                theProp.setXsd(true);
                theProp.setMany(false);
                if (attribute.getAnnotation() != null) {
                    java.util.List doc = attribute.getAnnotation().getDocumentation();
                    if (doc != null) {
                        theProp.setInstanceProperty(SDOConstants.DOCUMENTATION_PROPERTY, doc);
                    }
                }
                theProp.setFinalized(true);
                GlobalRef globalRef = new GlobalRef();
                globalRef.setProperty(theProp);
                owningType.addDeclaredProperty(theProp);

                globalRef.setIsElement(false);
                globalRef.setOwningType(owningType);
                globalRef.setUri(uri);
                globalRef.setLocalName(localName);
                addGlobalRef(globalRef);
            }
            return;

        } else {
            if (isGlobal) {
                SDOProperty lookedUpProp = getExistingGlobalProperty(targetNamespace, attribute.getName(), false);
                if (lookedUpProp != null && lookedUpProp.isFinalized()) {
                    return;
                }
            }

            p = createNewProperty(targetNamespace, attribute.getName(), isQualified, isGlobal, false, false, attribute.getAnnotation());
            if (attribute.getAnnotation() != null) {
                p.setAppInfoElements(attribute.getAnnotation().getAppInfo());
            }

            typeName = attribute.getType();
            if (typeName != null) {
                p.setName(attribute.getName());
                QName qname = getQNameForString(defaultNamespace, typeName);
                if (isGlobal) {
                    //if type is found set it other wise process new type
                    processGlobalItem(targetNamespace, defaultNamespace, typeName);
                }
                if ((qname.equals(XMLConstants.QNAME_QNAME)) || (qname.equals(XMLConstants.BASE_64_BINARY_QNAME)) || (qname.equals(XMLConstants.HEX_BINARY_QNAME)) || (qname.equals(XMLConstants.DATE_QNAME)) || (qname.equals(XMLConstants.TIME_QNAME)) || (qname.equals(XMLConstants.DATE_TIME_QNAME))) {
                    p.setXsdType(qname);
                }
                sdoPropertyType = getSDOTypeForName(targetNamespace, defaultNamespace, typeName);
                if(!sdoPropertyType.isFinalized() && qname.getNamespaceURI().equals(targetNamespace)) {
                    if(sdoPropertyType.isDataType()) {
                        SimpleType baseSimpleType = (SimpleType) rootSchema.getTopLevelSimpleTypes().get(qname.getLocalPart());
                        processGlobalSimpleType(targetNamespace, defaultNamespace, baseSimpleType);
                    }
                } 
                if ((p.getXsdType() == null) && (sdoPropertyType.getXsdType() != null)) {
                    p.setXsdType(sdoPropertyType.getXsdType());
                }
            } else if (attribute.getSimpleType() != null) {
                p.setName(attribute.getName());
                typeName = attribute.getName();
            } else {
                p.setName(attribute.getName());
                sdoPropertyType = ((SDOTypeHelper) aHelperContext.getTypeHelper()).getSDOTypeFromXSDType(SDOConstants.ANY_TYPE_QNAME);
            }
        }
        sdoPropertyType = processSimpleComponentAnnotations(owningType, attribute, p, targetNamespace, defaultNamespace, sdoPropertyType);
        p.setType(sdoPropertyType);
        p.setContainment(false);
        setDefaultValue(p, attribute);

        if (p.getType().isChangeSummaryType()) {
            p.setReadOnly(true);
        }

        if (owningType != null) {
            updateOwnerAndBuildMapping(owningType, p, defaultNamespace, targetNamespace, attribute, typeName, mappingUri);
        }
        p.setFinalized(true);
    }

    private SDOType processSimpleComponentAnnotations(SDOType owningType, SimpleComponent simpleComponent, SDOProperty p, String targetNamespace, String defaultNamespace, SDOType sdoPropertyType) {
        //aliasName annotation
        String aliasNamesValue = (String) simpleComponent.getAttributesMap().get(SDOConstants.SDOXML_ALIASNAME_QNAME);
        if (aliasNamesValue != null) {
            XMLConversionManager xmlConversionManager = ((SDOXMLHelper) aHelperContext.getXMLHelper()).getXmlConversionManager();
            java.util.List names = (java.util.List) xmlConversionManager.convertObject(aliasNamesValue, java.util.List.class);
            p.setAliasNames(names);
        }

        //readOnly annotation
        String readOnlyValue = (String) simpleComponent.getAttributesMap().get(SDOConstants.SDOXML_READONLY_QNAME);
        if (readOnlyValue != null) {
            p.setReadOnly(Boolean.valueOf(readOnlyValue));
        }

        //dataType annotation
        String dataTypeValue = (String) simpleComponent.getAttributesMap().get(SDOConstants.SDOXML_DATATYPE_QNAME);
        if (dataTypeValue != null) {
            QName xsdQName = ((SDOTypeHelper) aHelperContext.getTypeHelper()).getXSDTypeFromSDOType(sdoPropertyType);
            if ((xsdQName == null) && !sdoPropertyType.isDataType()) {
                xsdQName = new QName(sdoPropertyType.getURI(), sdoPropertyType.getName());
            }
            p.setXsdType(xsdQName);
            SDOType sdoType = getSDOTypeForName(targetNamespace, defaultNamespace, dataTypeValue);
            sdoPropertyType = sdoType;
            Property xmlDataTypeProperty = aHelperContext.getTypeHelper().getOpenContentProperty(SDOConstants.SDOXML_URL, SDOConstants.SDOXML_DATATYPE);
            p.setInstanceProperty(xmlDataTypeProperty, sdoType);
        }

        //string annotation
        String stringValue = (String) simpleComponent.getAttributesMap().get(SDOConstants.SDOXML_STRING_QNAME);
        if (stringValue != null) {
            QName xsdTypeQName = getQNameForString(defaultNamespace, simpleComponent.getType());
            p.setXsdType(xsdTypeQName);
            sdoPropertyType = SDOConstants.SDO_STRING;
        }

        //name annotation
        String nameValue = (String) simpleComponent.getAttributesMap().get(SDOConstants.SDOXML_NAME_QNAME);
        if (nameValue != null) {
            //itemNameToSDOName.put(sdoTypeName, nameValue);
            p.setName(nameValue);
            if (p.isGlobal() && targetNamespace != null) {
                QName propertyName = new QName(targetNamespace, nameValue);
                ((SDOTypeHelper) aHelperContext.getTypeHelper()).getOpenContentProperties().put(propertyName, p);
            }
        } else {
            if (p.isGlobal() && targetNamespace != null) {
                QName propertyName = new QName(targetNamespace, p.getName());
                ((SDOTypeHelper) aHelperContext.getTypeHelper()).getOpenContentProperties().put(propertyName, p);
            }
        }

        // propertyType annotation
        String propertyTypeValue = (String) simpleComponent.getAttributesMap().get(SDOConstants.SDOXML_PROPERTYTYPE_QNAME);
        if (propertyTypeValue != null) {
            String uri = targetNamespace;
            int colonIndex = propertyTypeValue.indexOf(':');
            if (colonIndex > -1) {
                String prefix = propertyTypeValue.substring(0, colonIndex);
                uri = getURIForPrefix(prefix);
            }
            NonContainmentReference nonContainmentReference = new NonContainmentReference();
            nonContainmentReference.setPropertyTypeName(propertyTypeValue);
            nonContainmentReference.setPropertyTypeURI(uri);
            nonContainmentReference.setOwningType(owningType);
            nonContainmentReference.setOwningProp(p);
            String oppositePropValue = (String) simpleComponent.getAttributesMap().get(SDOConstants.SDOXML_OPPOSITEPROPERTY_QNAME);

            nonContainmentReference.setOppositePropName(oppositePropValue);
            getNonContainmentReferences().add(nonContainmentReference);
        }

        return sdoPropertyType;
    }

    private void postProcessing() {
        int size = getNonContainmentReferences().size();
        for (int i = 0; i < size; i++) {
            NonContainmentReference nonContainmentReference = getNonContainmentReferences().get(i);
            SDOType owningType = nonContainmentReference.getOwningType();

            if (owningType != null) {
                String propertyTypeName = nonContainmentReference.getPropertyTypeName();
                String propertyTypeUri = nonContainmentReference.getPropertyTypeURI();

                SDOType oppositeType = getSDOTypeForName(propertyTypeUri, propertyTypeUri, propertyTypeName);
                if (oppositeType != null) {
                    SDOProperty owningProp = nonContainmentReference.getOwningProp();
                    if (owningProp != null) {
                        // Spec sect 9.2 (1) oppositeType.dataType must be false
                        if (oppositeType.isDataType()) {
                            throw SDOException.propertyTypeAnnotationTargetCannotBeDataTypeTrue(//
                                    oppositeType.getName(), owningProp.getName());
                        }
                        owningProp.setType(oppositeType);
                        owningProp.setContainment(false);
                        owningProp.buildMapping(owningProp.getType().getURI());
                        // Bidirectional property name
                        String oppositePropName = nonContainmentReference.getOppositePropName();
                        if (oppositePropName != null) {
                            SDOProperty prop = oppositeType.getProperty(oppositePropName);
                            owningProp.setOpposite(prop);
                            prop.setOpposite(owningProp);
                        }
                    }
                }
            }
        }

        Iterator<Type> iter = getGlobalRefs().keySet().iterator();
        while (iter.hasNext()) {
            Object nextKey = iter.next();
            java.util.List<GlobalRef> value = getGlobalRefs().get(nextKey);
            java.util.List refsToRemove = new ArrayList();
            if (value != null) {
                for (int i = 0; i < value.size(); i++) {
                    GlobalRef nextGlobalRef = value.get(i);
                    SDOProperty p = processRef(nextGlobalRef);
                    if (p.isFinalized()) {
                        refsToRemove.add(nextGlobalRef);
                    }
                }
            }
            for (int i = 0; i < refsToRemove.size(); i++) {
                value.remove(refsToRemove.get(i));
            }
        }
    }

    private void addGlobalRef(GlobalRef ref) {
        java.util.List<GlobalRef> refs = getGlobalRefs().get(ref.getOwningType());
        if (null == refs) {
            refs = new ArrayList<GlobalRef>();
            refs.add(ref);
            getGlobalRefs().put(ref.getOwningType(), refs);
        } else {
            refs.add(ref);
        }
    }

    /**
     * INTERNAL:
     * Initialize this SchemaParser by configuring the package name based on the targetNamespace.
     */
    private void initialize() {
        if (null == packageName) {
            String packageValue = (String) rootSchema.getAttributesMap().get(SDOConstants.SDOJAVA_PACKAGE_QNAME);
            if (null != packageValue) {
                packageName = packageValue;
            } else if ((null == rootSchema.getTargetNamespace()) || rootSchema.getTargetNamespace().equals(SDOConstants.EMPTY_STRING)) {
                packageName = SDOConstants.JAVA_TYPEGENERATION_NO_NAMESPACE;
            } else {
                packageName = SDOUtil.getPackageNameFromURI(rootSchema.getTargetNamespace());
            }
            packageName += SDOConstants.JAVA_PACKAGE_NAME_SEPARATOR;
        }
    }

    private SDOType createSDOTypeForName(String targetNamespace, String name, String xsdLocalName) {
        SDOType returnType = null;
        int index = name.indexOf(':');
        if (index != -1) {
            String prefix = name.substring(0, index);
            String localName = name.substring(index + 1, name.length());
            String theURI = getURIForPrefix(prefix);
            returnType = getOrCreateType(theURI, localName, xsdLocalName);
        } else {
            // returnType = getOrCreateType(targetNamespace, name, xsdLocalName);
            SDOType sdoType = new SDOType(targetNamespace, name, (SDOTypeHelper)aHelperContext.getTypeHelper());
            this.anonymousTypes.add(sdoType);
            return sdoType;
        }
        return returnType;
    }

    private SDOType getSDOTypeForName(String targetNamespace, String defaultNamespace, String name) {
        return getSDOTypeForName(targetNamespace, defaultNamespace, true, name);
    }

    private SDOType getSDOTypeForName(String targetNamespace, String defaultNamespace, boolean checkDefaultNamespace, String name) {
        int index = name.indexOf(':');
        if (index != -1) {
            String prefix = name.substring(0, index);
            String localName = name.substring(index + 1, name.length());
            String theURI = getURIForPrefix(prefix);
            QName qname = new QName(theURI, localName);
            SDOType sdoType = generatedTypesByXsdQName.get(qname);
            if(null == sdoType) {
                sdoType = ((SDOTypeHelper) aHelperContext.getTypeHelper()).getSDOTypeFromXSDType(qname);
                if (null == sdoType) {
                    sdoType = getExisitingType(theURI, localName);
                    if (null == sdoType) {
                        sdoType = findSdoType(targetNamespace, defaultNamespace, name, localName, theURI);
                    }
                }
            }
            if (null == sdoType) {
                sdoType = getOrCreateType(theURI, localName, localName);
                if (!sdoType.isFinalized()) {
                    //if it's not finalized, then it's new, so add it to the generated types map
                    getGeneratedTypes().put(new QName(sdoType.getURI(), sdoType.getName()), sdoType);
                }
            }
            return sdoType;

        } else {
            String sdoName = (String) itemNameToSDOName.get(name);
            if (sdoName != null) {
                name = sdoName;
            }

            SDOType sdoType = null;
            if (checkDefaultNamespace && (defaultNamespace != null)) {
                QName qname = new QName(defaultNamespace, name);
                sdoType = ((SDOTypeHelper) aHelperContext.getTypeHelper()).getSDOTypeFromXSDType(qname);
                if(null == sdoType) {
                    sdoType = generatedTypesByXsdQName.get(qname);
                }
            }
            if (null == sdoType) {
                sdoType = getExisitingType(targetNamespace, name);
                if (null == sdoType) {
                    return findSdoType(targetNamespace, defaultNamespace, name, name, targetNamespace);
                }
                return getOrCreateType(targetNamespace, name, name);
            }
            return sdoType;
        }
    }

    //Since types aren't registered until the end of the define call we need to check typeHelper
    //and the generatedTypesmap to see if a type already exists.  The if the type doesn't exist
    //we create a new one
    private SDOType getOrCreateType(String uri, String typeName, String xsdLocalName) {
        String lookupName = typeName;
        int index = lookupName.indexOf(':');
        if (index != -1) {
            lookupName = lookupName.substring(index + 1, lookupName.length());
        }
        SDOTypeHelper sdoTypeHelper = (SDOTypeHelper) aHelperContext.getTypeHelper();
        SDOType returnType = (SDOType) sdoTypeHelper.getType(uri, lookupName);
        if (returnType == null) {
            QName qname = new QName(uri, lookupName);
            returnType = (SDOType) getGeneratedTypes().get(qname);

            if (returnType == null) {
                QName xsdQName = new QName(uri, xsdLocalName);
                returnType = getTypeForXSDQName(xsdQName);
                if (returnType == null) {
                    returnType = new SDOType(uri, lookupName, sdoTypeHelper);
                    returnType.setXsd(true);
                    returnType.setXsdLocalName(xsdLocalName);
                } else {
                    returnType.setQName(qname);
                }
            }
        }
        return returnType;
    }

    //Since global properties aren't registered until the end of the define call we need to check XSDhelper
    //and the generatedProperties map to see if a type already exists
    private SDOProperty getExistingGlobalProperty(String uri, String localName, boolean isElement) {
        SDOProperty prop = (SDOProperty) aHelperContext.getXSDHelper().getGlobalProperty(uri, localName, isElement);
        if (prop == null) {
            QName qName = new QName(uri, localName);
            if (isElement) {
                prop = (SDOProperty) getGeneratedGlobalElements().get(qName);
            } else {
                prop = (SDOProperty) getGeneratedGlobalAttributes().get(qName);
            }
        }
        return prop;
    }

    //Since types aren't registered until the end of the define call we need to check type helper
    //and the generatedTypesmap to see if a type already exists
    private SDOType getExisitingType(String uri, String localName) {
        SDOType type = (SDOType) ((SDOTypeHelper) aHelperContext.getTypeHelper()).getType(uri, localName);
        if (type == null) {
            QName qName = new QName(uri, localName);
            type = (SDOType) getGeneratedTypes().get(qName);
        }
        return type;
    }

    private SDOType findSdoType(String targetNamespace, String defaultNamespace, String qualifiedName, String localName, String theURI) {
        //need to also check imports
        SDOType type = getExisitingType(theURI, localName);

        // Do not execute the following if theURI isn't the target namespace
        if (type == null && ((theURI != null && theURI.equals(targetNamespace)) || (theURI == null && targetNamespace == null))) {
            processGlobalItem(targetNamespace, defaultNamespace, qualifiedName);
            String sdoName = (String) itemNameToSDOName.get(localName);
            if (sdoName != null) {
                localName = sdoName;
            }
            type = getExisitingType(theURI, localName);
        }
        if (null == type) {
            type = getOrCreateType(theURI, localName, localName);

            if (!type.isFinalized()) {
                //if it's not finalized, then it's new, so add it to the generated types map
                getGeneratedTypes().put(new QName(type.getURI(), type.getName()), type);
            }

        }
        return type;
    }

    public void setGeneratedTypes(Map<QName, Type> generatedTypes) {
        this.generatedTypes = generatedTypes;
    }
    
    public void setGeneratedGlobalElements(Map<QName, Property> generatedElements) {
        this.generatedGlobalElements = generatedElements;
    }

    public void setGeneratedGlobalAttributes(Map<QName, Property> generatedAttributes) {
        this.generatedGlobalAttributes = generatedAttributes;
    }

    public java.util.List<SDOType> getAnonymousTypes() {
        return anonymousTypes;
    }

    public void setAnonymousTypes(java.util.List<SDOType> anonymousTypes) {
        this.anonymousTypes = anonymousTypes;
    }

    public Map<QName, Type> getGeneratedTypes() {
        if (null == generatedTypes) {
            generatedTypes = new HashMap<QName, Type>();
        }
        return generatedTypes;
    }

    public Map<QName, Property> getGeneratedGlobalElements() {
        if (null == generatedGlobalElements) {
            generatedGlobalElements = new HashMap<QName, Property>();
        }
        return generatedGlobalElements;
    }

    public Map<QName, SDOType> getGeneratedTypesByXsdQName() {
        return generatedTypesByXsdQName;
    }

    public void setGeneratedTypesByXsdQName(Map<QName, SDOType> generatedTypesByXsdQName) {
        this.generatedTypesByXsdQName = generatedTypesByXsdQName;
    }

    public Map<QName, Property> getGeneratedGlobalAttributes() {
        if (null == generatedGlobalAttributes) {
            generatedGlobalAttributes = new HashMap<QName, Property>();
        }
        return generatedGlobalAttributes;
    }

    private void processSubstitutionGroups(Collection elements, String targetNamespace, String defaultNamespace) {
        Iterator elementsIter = elements.iterator();
        while (elementsIter.hasNext()) {
            Element nextElement = (Element) elementsIter.next();
            if (nextElement.getSubstitutionGroup() != null) {
                String substitutionGroup = nextElement.getSubstitutionGroup();
                String localName = null;
                String uri = null;

                int index = substitutionGroup.indexOf(':');
                if (index != -1) {
                    String prefix = substitutionGroup.substring(0, index);
                    localName = substitutionGroup.substring(index + 1, substitutionGroup.length());
                    uri = getURIForPrefix(prefix);
                } else {
                    localName = substitutionGroup;
                    uri = defaultNamespace;
                }
                SDOProperty rootProp = getExistingGlobalProperty(uri, localName, true);
                SDOProperty thisProperty = getExistingGlobalProperty(targetNamespace, nextElement.getName(), true);

                if (rootProp != null && thisProperty != null) {
                    if (rootProp.getSubstitutableElements() == null) {
                        rootProp.setSubstitutableElements(new java.util.ArrayList<SDOProperty>());
                        rootProp.setSubstitutable(true);
                    }
                    rootProp.getSubstitutableElements().add(thisProperty);
                }
            }
        }
    }

    private void processAnyAttribute(String targetNamespace, String defaultNamespace, SDOType owningType) {
        owningType.setOpen(true);
    }

    private SDOType getTypeForName(String targetNamespace, String defaultNamespace, String typeName) {
        Object value = getGeneratedTypes().get(typeName);
        if (value != null) {
            return (SDOType) value;
        } else {
            String sdoName = (String) itemNameToSDOName.get(typeName);
            if (sdoName != null) {
                return getTypeForName(targetNamespace, defaultNamespace, sdoName);
            } else {
                return getSDOTypeForName(targetNamespace, defaultNamespace, false, typeName);
            }
        }
    }

    private QName getQNameForString(String defaultNamespace, String name) {
        if (null == name) {
            return null;
        }
        int index = name.indexOf(':');
        if (index != -1) {
            String prefix = name.substring(0, index);
            String localName = name.substring(index + 1, name.length());
            String theURI = getURIForPrefix(prefix);
            QName qname = new QName(theURI, localName);
            return qname;
        } else {
            QName qname = new QName(defaultNamespace, name);
            return qname;
        }
    }

    private void processGlobalItem(String targetNamespace, String defaultNamespace, String qualifiedName) {
        if (rootSchema == null) {
            return;
        }
        String localName = null;
        int index = qualifiedName.indexOf(':');
        if (index != -1) {
            localName = qualifiedName.substring(index + 1, qualifiedName.length());
        } else {
            localName = qualifiedName;
        }

        SimpleType simpleType = (SimpleType) rootSchema.getTopLevelSimpleTypes().get(localName);
        if (simpleType == null) {
            ComplexType complexType = (ComplexType) rootSchema.getTopLevelComplexTypes().get(localName);
            if (complexType == null) {
                Element element = (Element) rootSchema.getTopLevelElements().get(localName);
                if (element == null) {
                    Attribute attribute = (Attribute) rootSchema.getTopLevelAttributes().get(localName);
                    if (attribute != null) {
                        processGlobalAttribute(targetNamespace, defaultNamespace, attribute);
                    }
                } else {
                    processGlobalElement(targetNamespace, defaultNamespace, element);
                }
            } else {
                processGlobalComplexType(targetNamespace, defaultNamespace, complexType);
            }
        } else {
            processGlobalSimpleType(targetNamespace, defaultNamespace, simpleType);
        }
    }

    /**
     * Return a Schema for the given Source object.
     *
     * A SchemaResolverWrapper is created to wrap the given SchemaResolver.  The wrapper
     * will prevent schemas from being processed multiple times (such as in the case of
     * circular includes/imports)
     *
     * This method should not be called recursively if a given schema could potentially
     * and undesirably be processed multiple times (again, such as in the case of
     * circular includes/imports)
     *
     * @param xsdSource
     * @param schemaResolver the schema resolver to be used to resolve imports/includes
     * @return
     */
    public Schema getSchema(Source xsdSource, SchemaResolver schemaResolver) {
        // Create a resolver wrapper that will prevent schemas from being processed
        // multiple times (such as in the case of circular includes/imports)
        return getSchema(xsdSource, new SchemaResolverWrapper(schemaResolver));
    }

    /**
     * Return a Schema for the given Source object.
     *
     * Since this method is called recursively, and the SchemaResolverWrapper is stateful,
     * the resolver wrapper must be created outside of this method.
     *
     * @param xsdSource
     * @param schemaResolverWrapper wraps the schema resolver to be used to resolve imports/includes
     * @return
     */
    public Schema getSchema(Source xsdSource, SchemaResolverWrapper schemaResolverWrapper) {
        try {
            xsdSource = schemaResolverWrapper.resolveSchema(xsdSource);

            XMLContext context = new XMLContext(getSchemaProject());
            XMLUnmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setEntityResolver(schemaResolverWrapper.getSchemaResolver());

            Schema schema = (Schema) unmarshaller.unmarshal(xsdSource);
            //populate Imports
            java.util.List imports = schema.getImports();
            Iterator iter = imports.iterator();
            while (iter.hasNext()) {
                Import nextImport = (Import) iter.next();

                Source referencedSchema = getReferencedSchema(xsdSource, nextImport.getNamespace(), nextImport.getSchemaLocation(), schemaResolverWrapper);
                if (referencedSchema != null) {
                    Schema importedSchema = getSchema(referencedSchema, schemaResolverWrapper);
                    nextImport.setSchema(importedSchema);
                }
            }

            //populate includes
            java.util.List includes = schema.getIncludes();
            Iterator includesIter = includes.iterator();
            while (includesIter.hasNext()) {
                Include nextInclude = (Include) includesIter.next();

                Source referencedSchema = getReferencedSchema(xsdSource, schema.getTargetNamespace(), nextInclude.getSchemaLocation(), schemaResolverWrapper);
                if (referencedSchema != null) {
                    Schema includedSchema = getSchema(referencedSchema, schemaResolverWrapper);
                    nextInclude.setSchema(includedSchema);
                }
            }
            return schema;
        } catch (Exception e) {
            //Error processing Import/Include
            e.printStackTrace();
            return null;
        }
    }

    private Source getReferencedSchema(Source xsdSource, String namespace, String schemaLocation, SchemaResolverWrapper schemaResolverWrapper) {
        if (namespace.equals(SDOConstants.SDOJAVA_URL) || namespace.equals(SDOConstants.SDO_URL) || namespace.equals(SDOConstants.SDOXML_URL)) {
            return null;
        }
        return schemaResolverWrapper.resolveSchema(xsdSource, namespace, schemaLocation);
    }

    public Project getSchemaProject() {
        if (schemaProject == null) {
            schemaProject = new SchemaModelProject();

        }
        return schemaProject;
    }

    public Schema getRootSchema() {
        return rootSchema;
    }

    public void setProcessImports(boolean processImports) {
        this.processImports = processImports;
    }

    public boolean isProcessImports() {
        return processImports;
    }

    public void setReturnAllTypes(boolean returnAllTypes) {
        this.returnAllTypes = returnAllTypes;
    }

    public boolean isReturnAllTypes() {
        return returnAllTypes;
    }

    private boolean maxOccursGreaterThanOne(String maxOccurs) {
        if (maxOccurs == null) {
            return false;
        }
        if (maxOccurs.equalsIgnoreCase(Occurs.UNBOUNDED)) {
            return true;
        }
        return !maxOccurs.equals(Occurs.ONE);
    }

    private String getURIForPrefix(String prefix) {
        String uri = null;

        for (int i = namespaceResolvers.size() - 1; i >= 0; i--) {
            NamespaceResolver next = (NamespaceResolver) namespaceResolvers.get(i);
            uri = next.resolveNamespacePrefix(prefix);
            if ((uri != null) && !uri.equals(SDOConstants.EMPTY_STRING)) {
                break;
            }
        }

        if (null == uri) {
            throw SDOException.prefixUsedButNotDefined(prefix);
        }
        return uri;
    }

    public class NonContainmentReference {
        private SDOType owningType;
        private SDOProperty owningProp;
        private String propertyTypeName;
        private String propertyTypeURI;
        private String oppositePropName;

        public void setOwningType(SDOType owningType) {
            this.owningType = owningType;
        }

        public SDOType getOwningType() {
            return owningType;
        }

        public void setOwningProp(SDOProperty owningProp) {
            this.owningProp = owningProp;
        }

        public SDOProperty getOwningProp() {
            return owningProp;
        }

        public void setPropertyTypeName(String propertyTypeName) {
            this.propertyTypeName = propertyTypeName;
        }

        public String getPropertyTypeName() {
            return propertyTypeName;
        }

        public void setPropertyTypeURI(String propertyTypeURI) {
            this.propertyTypeURI = propertyTypeURI;
        }

        public String getPropertyTypeURI() {
            return propertyTypeURI;
        }

        public void setOppositePropName(String oppositePropName) {
            this.oppositePropName = oppositePropName;
        }

        public String getOppositePropName() {
            return oppositePropName;
        }
    }

    public class GlobalRef {
        private SDOType owningType;
        private boolean isElement;
        private String uri;
        private String localName;
        private Property property;

        public void setOwningType(SDOType owningType) {
            this.owningType = owningType;
        }

        public SDOType getOwningType() {
            return owningType;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String theUri) {
            uri = theUri;
        }

        public Property getProperty() {
            return property;
        }

        public void setProperty(Property theProperty) {
            property = theProperty;
        }

        public String getLocalName() {
            return localName;
        }

        public void setLocalName(String theLocalName) {
            localName = theLocalName;
        }

        public boolean isElement() {
            return isElement;
        }

        public void setIsElement(boolean isElem) {
            isElement = isElem;
        }
    }

    private java.util.List<NonContainmentReference> getNonContainmentReferences() {
        if (null == nonContainmentReferences) {
            nonContainmentReferences = new ArrayList<NonContainmentReference>();
        }
        return nonContainmentReferences;
    }

    private Map<Type, java.util.List<GlobalRef>> getGlobalRefs() {
        if (null == globalRefs) {
            globalRefs = new HashMap<Type, java.util.List<GlobalRef>>();
        }
        return globalRefs;
    }

    public boolean isImportProcessor() {
        return isImportProcessor;
    }

    public void setIsImportProcessor(boolean isImport) {
        isImportProcessor = isImport;
    }

    private void preprocessGlobalTypes(Schema schema) {
        String targetNamespace = schema.getTargetNamespace();

        // Global Complex Types
        Collection<ComplexType> globalComplexTypes = schema.getTopLevelComplexTypes().values();
        for(ComplexType globalComplexType : globalComplexTypes) {
            QName xsdQName = new QName(targetNamespace, globalComplexType.getName());
            SDOType sdoType = preprocessComplexType(globalComplexType, schema);
            sdoType.setXsdType(xsdQName);
            generatedTypesByXsdQName.put(xsdQName, sdoType);
        }

        // Global Simple Types
        Collection<SimpleType> globalSimpleTypes = schema.getTopLevelSimpleTypes().values();
        for(SimpleType globalSimpleType : globalSimpleTypes) {
            QName xsdQName = new QName(targetNamespace, globalSimpleType.getName());
            SDOType sdoType = preprocessSimpleType(globalSimpleType, schema);
            sdoType.setXsdType(xsdQName);
            generatedTypesByXsdQName.put(xsdQName, sdoType);
        }
    }

    /**
     * Return the SDOType (new or existing) corresponding to this complex type.
     */
    private SDOType preprocessComplexType(ComplexType complexType, Schema schema) {
        String typeName = (String) complexType.getAttributesMap().get(SDOConstants.SDOXML_NAME_QNAME);
        if(null == typeName) {
            typeName = complexType.getName();
        }

        SDOTypeHelper sdoTypeHelper = (SDOTypeHelper) aHelperContext.getTypeHelper();
        String typeURI = schema.getTargetNamespace();
        SDOType sdoType = (SDOType) sdoTypeHelper.getType(typeURI, typeName);
        QName qName = new QName(schema.getTargetNamespace(), complexType.getName());

        if(null == sdoType) {
            sdoType = (SDOType)getGeneratedTypes().get(qName);
            if(sdoType == null) {
                sdoType = new SDOType(typeURI, typeName, sdoTypeHelper);
                sdoType.setXsdLocalName(complexType.getName());
                sdoType.preInitialize(packageName, namespaceResolvers);
            }
            sdoType.setXsd(true);
            if(!sdoType.getQName().equals(sdoType.getXsdType())) {
            // sdoType.setInstanceProperty(nameProperty, typeName);
            }
            getGeneratedTypesByXsdQName().put(qName, sdoType);
            getGeneratedTypes().put(sdoType.getQName(), sdoType);
        } else if(!returnAllTypes) {
            processedComplexTypes.put(qName, complexType);
        }
        return sdoType;
    }

    /**
     * Return the SDODataType (new or existing) corresponding to this simple type.
     */
    private SDODataType preprocessSimpleType(SimpleType simpleType, Schema schema) {
        String typeName = (String) simpleType.getAttributesMap().get(SDOConstants.SDOXML_NAME_QNAME);
        if(null == typeName) {
            typeName = simpleType.getName();
        }

        SDOTypeHelper sdoTypeHelper = (SDOTypeHelper) aHelperContext.getTypeHelper();
        String typeURI = schema.getTargetNamespace();
        SDODataType sdoDataType = (SDODataType) sdoTypeHelper.getType(typeURI, typeName);
        QName qName = new QName(schema.getTargetNamespace(), simpleType.getName());
        if(null == sdoDataType) {
            SDOType existingType = (SDOType)getGeneratedTypes().get(qName);
            if(null == existingType) {
                existingType = (SDOType) aHelperContext.getTypeHelper().getType(qName.getNamespaceURI(), qName.getLocalPart());   
            }
            if(existingType != null && existingType.isFinalized()) {
                return (SDODataType)existingType;
            }
            sdoDataType = new SDODataType(typeURI, typeName, sdoTypeHelper);
            String instanceClassValue = (String) simpleType.getAttributesMap().get(SDOConstants.SDOJAVA_INSTANCECLASS_QNAME);
            if (instanceClassValue != null) {
                sdoDataType.setInstanceProperty(SDOConstants.JAVA_CLASS_PROPERTY, instanceClassValue);
            }

            if(existingType != null) {
                //Existing type was started in an import, but not as an instance of
                //SDODataType. Remove original type and copy referencing properties
                generatedTypes.remove(qName);
                generatedTypesByXsdQName.remove(qName);
                Iterator nonFinalizedProps = existingType.getNonFinalizedReferencingProps().iterator();
                Iterator nonFinalizedUris = existingType.getNonFinalizedMappingURIs().iterator();
                while(nonFinalizedProps.hasNext()) {
                    SDOProperty next = (SDOProperty)nonFinalizedProps.next();
                    next.setType(sdoDataType);
                    sdoDataType.getNonFinalizedReferencingProps().add(next);
                    sdoDataType.getNonFinalizedMappingURIs().add(nonFinalizedUris.next());
                }
                if(anonymousTypes.contains(existingType)) {
                  anonymousTypes.remove(existingType);  
                }
            }
            sdoDataType.setXsdLocalName(simpleType.getName());
            sdoDataType.setXsd(true);
            if(!sdoDataType.getQName().equals(sdoDataType.getXsdType())) {
                // sdoDataType.setInstanceProperty(nameProperty, typeName);
            }
            generatedTypesByXsdQName.put(qName, sdoDataType);
            getGeneratedTypes().put(sdoDataType.getQName(), sdoDataType);
        } else if(!returnAllTypes) {
            processedSimpleTypes.put(qName, simpleType);
        }
        return sdoDataType;
    }

    private SDOType getTypeForXSDQName(QName xsdQName) {
        SDOType sdoType = generatedTypesByXsdQName.get(xsdQName);
        if(null == sdoType) {
            SDOTypeHelper sdoTypeHelper = (SDOTypeHelper)aHelperContext.getTypeHelper(); 
            sdoType = sdoTypeHelper.getSDOTypeFromXSDType(xsdQName);
        }
        return sdoType;
    }

}
