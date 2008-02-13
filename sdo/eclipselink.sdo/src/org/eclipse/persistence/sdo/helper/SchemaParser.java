/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
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
import commonj.sdo.helper.HelperContext;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.internal.oxm.schema.SchemaModelProject;
import org.eclipse.persistence.internal.oxm.schema.model.*;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.sessions.Project;

/**
 * <p><b>Purpose</b>: Class to walk the Schema model org.eclipse.persistence.internal.oxm.schema.model
 * @see org.eclipse.persistence.sdo.helper.SDOTypesGenerator
 * @see org.eclipse.persistence.sdo.helper.SDOClassGenerator
 */
public abstract class SchemaParser {
    private Project schemaProject;

    /** Visibility reduced from [public] in 2.1.0. May 15 2007 */
    protected Schema rootSchema;
    protected HashMap processedComplexTypes;
    protected HashMap processedSimpleTypes;
    protected HashMap processedElements;
    protected HashMap processedAttributes;
    protected Map itemNameToSDOName;
    private boolean processImports;
    private boolean returnAllTypes;
    protected java.util.List namespaceResolvers;
    protected boolean inRestriction;
    // hold the context containing all helpers so that we can preserve inter-helper relationships
    protected HelperContext aHelperContext;
    

    public SchemaParser(HelperContext aHelperContext) {
        processedComplexTypes = new HashMap();
        processedSimpleTypes = new HashMap();
        processedElements = new HashMap();
        processedAttributes = new HashMap();
        itemNameToSDOName = new HashMap();
        namespaceResolvers = new ArrayList();
        this.aHelperContext = aHelperContext;
    }

    public abstract void startNewComplexType(String targetNamespace, String name, String xsdLocalName, ComplexType complexType);

    public abstract void startNewSimpleType(String targetNamespace, String name, String xsdLocalName,  SimpleType simpleType);

    public abstract void processSimpleElement(String targetNamespace, String defaultNamespace, String ownerName, TypeDefParticle typeDefParticle, Element element, boolean isQualified, boolean isGlobal, boolean isMany);

    public abstract void processTypeDef(String targetNamespace, String defaultNamespace, String ownerName, TypeDefParticle typeDefParticle);

    public abstract void processSimpleAttribute(String targetNamespace, String defaultNamespace, String ownerName, Attribute attribute, boolean isGlobal, boolean isQualified);

    public abstract void processAny(String targetNamespace, String defaultNamespace, Any any, String ownerName, TypeDefParticle typeDefParticle);

    public abstract void processBaseType(String targetNamespace, String defaultNamespace, String ownerName, String baseType, boolean simple);

    protected abstract void processUnion(String targetNamespace, String defaultNamespace, String ownerName, Union union);

    protected abstract void processList(String targetNamespace, String defaultNamespace, String ownerName, List list);

    protected abstract void processImport(Import theImport);

    protected abstract void processInclude(Include theInclude);

    public abstract void initialize();

    protected abstract void processAnyAttribute(String targetNamespace, String defaultNamespace, String ownerName);

    public void processSchema(Schema parsedSchema) {
        rootSchema = parsedSchema;
        initialize();
        namespaceResolvers.add(rootSchema.getNamespaceResolver());
        processIncludes(rootSchema.getIncludes());
        processImports(rootSchema.getImports());
        processGlobalAttributes(rootSchema);
        processGlobalElements(rootSchema);
        processGlobalSimpleTypes(rootSchema);
        processGlobalComplexTypes(rootSchema);

        postProcessing();
    }

    protected void postProcessing() {
    }

    private void processImports(java.util.List imports) {
        if ((imports == null) || (imports.size() == 0) || !isProcessImports()) {
            return;
        }
        Iterator iter = imports.iterator();
        while (iter.hasNext()) {
            Import nextImport = (Import)iter.next();
            processImport(nextImport);
        }
    }

    private void processIncludes(java.util.List includes) {
        if ((includes == null) || (includes.size() == 0) || !isProcessImports()) {
            return;
        }
        Iterator iter = includes.iterator();
        while (iter.hasNext()) {
            Include nextInclude = (Include)iter.next();            
            processInclude(nextInclude);
        }
    }

    private void processGlobalComplexTypes(Schema schema) {
        Collection complexTypes = schema.getTopLevelComplexTypes().values();
        if (complexTypes == null) {
            return;
        }

        Iterator complexTypesIter = complexTypes.iterator();
        while (complexTypesIter.hasNext()) {
            ComplexType complexType = (ComplexType)complexTypesIter.next();
            processGlobalComplexType(schema.getTargetNamespace(), schema.getDefaultNamespace(), complexType);
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

    private void processGlobalSimpleTypes(Schema schema) {
        Collection simpleTypes = schema.getTopLevelSimpleTypes().values();
        if (simpleTypes == null) {
            return;
        }

        Iterator simpleTypesIter = simpleTypes.iterator();
        while (simpleTypesIter.hasNext()) {
            SimpleType simpleType = (SimpleType)simpleTypesIter.next();
            processGlobalSimpleType(schema.getTargetNamespace(), schema.getDefaultNamespace(), simpleType);
        }
    }

    private void processGlobalSimpleType(String targetNamespace, String defaultNamespace, SimpleType simpleType) {
        QName qname = new QName(targetNamespace, simpleType.getName());
        Object processed = processedSimpleTypes.get(qname);

        if (processed == null) {
            processSimpleType(targetNamespace, defaultNamespace, simpleType.getName(), simpleType);
            processedSimpleTypes.put(qname, simpleType);
        }
    }

    private void processGlobalElements(Schema schema) {
        Collection elements = schema.getTopLevelElements().values();
        if (elements == null) {
            return;
        }
        Iterator elementsIter = elements.iterator();
        while (elementsIter.hasNext()) {
            Element nextElement = (Element)elementsIter.next();
            processGlobalElement(schema.getTargetNamespace(), schema.getDefaultNamespace(), nextElement);
        }
        //process substitution groups after properties have been created for all elements
        elementsIter = elements.iterator();
        while (elementsIter.hasNext()) {
            Element nextElement = (Element)elementsIter.next();
            if(nextElement.getSubstitutionGroup() != null) {
                String substitutionGroup = nextElement.getSubstitutionGroup();
                String localName = null;
                String uri = null;

                int index = substitutionGroup.indexOf(':');
                if (index != -1) {
                    String prefix = substitutionGroup.substring(0, index);
                    localName = substitutionGroup.substring(index + 1, substitutionGroup.length());
                    uri = getURIForPrefix(schema.getDefaultNamespace(), prefix);
                } else {
                    localName = substitutionGroup;
                    uri = schema.getDefaultNamespace();
                }
                SDOProperty rootProp = (SDOProperty)aHelperContext.getXSDHelper().getGlobalProperty(uri, localName, true);
                SDOProperty thisProperty = (SDOProperty)aHelperContext.getXSDHelper().getGlobalProperty(schema.getTargetNamespace(), nextElement.getName(), true);
                if(rootProp != null && thisProperty != null) {
                    if(rootProp.getSubstitutableElements() == null) {
                        rootProp.setSubstitutableElements(new java.util.ArrayList<SDOProperty>());
                        rootProp.setSubstitutable(true);
                    }
                    rootProp.getSubstitutableElements().add(thisProperty);
                }
            }
        }
    }

    private boolean addNextNamespaceResolver(Map attributesMap) {
        NamespaceResolver nr = new NamespaceResolver();
        Iterator iter = attributesMap.keySet().iterator();
        while (iter.hasNext()) {
            QName key = (QName)iter.next();
            if (key.getNamespaceURI().equals(XMLConstants.XMLNS_URL)) {
                String value = (String)attributesMap.get(key);
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

    private void processGlobalElement(String targetNamespace, String defaultNamespace, Element element) {      
        if (element.getName() != null) {
            QName qname = new QName(targetNamespace, element.getName());
            Object processed = processedElements.get(qname);

            if (processed == null) {
                //??startGlobalElement(element);
                processElement(targetNamespace, defaultNamespace, null, null, element, true, false);
                processedElements.put(qname, element);
            }
        } else {
            startGlobalElement(targetNamespace, defaultNamespace, element);
            processElement(targetNamespace, defaultNamespace, null, null, element, true, false);
        }       

        //TODO: ???finishGlobalElement(element.getName());
    }

    protected void processComplexType(String targetNamespace, String defaultNamespace, String name, ComplexType complexType) {
        if (complexType == null) {
            return;
        }
        boolean addedNR = addNextNamespaceResolver(complexType.getAttributesMap());
        boolean newType = startComplexType(targetNamespace, defaultNamespace, name, complexType);
        if (newType) {
            if (complexType.getComplexContent() != null) {
                processComplexContent(targetNamespace, defaultNamespace, complexType.getComplexContent());
                finishComplexType(targetNamespace, defaultNamespace, name);
            } else if (complexType.getSimpleContent() != null) {
                processSimpleContent(targetNamespace, defaultNamespace, complexType.getSimpleContent());
                finishComplexType(targetNamespace, defaultNamespace, name);
            } else {
                startTypeDefintion(targetNamespace, defaultNamespace, name);
                if (complexType.getChoice() != null) {
                    processChoice(targetNamespace, defaultNamespace, name, complexType.getChoice(), false);
                } else if (complexType.getSequence() != null) {
                    processSequence(targetNamespace, defaultNamespace, name, complexType.getSequence(), false);
                } else if (complexType.getAll() != null) {
                    processAll(targetNamespace, defaultNamespace, name, complexType.getAll(), false);
                }

                processOrderedAttributes(targetNamespace, defaultNamespace, name, complexType.getOrderedAttributes());
                finishComplexType(targetNamespace, defaultNamespace, name);
            }
        }
        if (addedNR) {
            namespaceResolvers.remove(namespaceResolvers.size() - 1);
        }
    }

    private void processOrderedAttributes(String targetNamespace, String defaultNamespace, String name, java.util.List orderedAttributes) {
        for (int i = 0; i < orderedAttributes.size(); i++) {
            Object next = orderedAttributes.get(i);
            if (next instanceof Attribute) {
                processAttribute(targetNamespace, defaultNamespace, name, (Attribute)next, false);
            } else if (next instanceof AttributeGroup) {
                processAttributeGroup(targetNamespace, defaultNamespace, name, (AttributeGroup)next);
            }
        }
    }

    public boolean typesExists(String targetNamespace, String name) {
        if ((targetNamespace != null) && (targetNamespace.equals(SDOConstants.SDOJAVA_URL) || targetNamespace.equals(SDOConstants.SDO_URL) || targetNamespace.equals(SDOConstants.SDOXML_URL))) {
            return true;
        }

        QName qname = new QName(targetNamespace, name);
        Object processed = processedComplexTypes.get(qname);
        if (processed != null) {
            return true;
        }
        return false;
    }

    //return true if a new type was created
    public boolean startComplexType(String targetNamespace, String defaultNamespace, String name, ComplexType complexType) {
        String nameValue = (String)complexType.getAttributesMap().get(SDOConstants.SDOXML_NAME_QNAME);
        String originalName = name;
        if (nameValue != null) {
            itemNameToSDOName.put(name, nameValue);
            originalName = name;
            name = nameValue;
        }

        //check if already processed, if yes return false because a new type was not started else start new type and return true
        boolean alreadyExists = typesExists(targetNamespace, name);
        if (!alreadyExists) {
            startNewComplexType(targetNamespace, name,originalName, complexType);
            return true;
        }

        return false;
    }

    public void startTypeDefintion(String targetNamespace, String defaultNamespace, String name) {
    }

    public void finishComplexType(String targetNamespace, String defaultNamespace, String name) {
    }

    public void finishNestedComplexType(String targetNamespace, String defaultNamespace, TypeDefParticle typeDefParticle, String name) {
    }

    public boolean startSimpleType(String targetNamespace, String defaultNamespace, String name, String xsdLocalName,  SimpleType simpleType) {
        boolean alreadyExists = typesExists(targetNamespace, name);
        if (!alreadyExists) {
            startNewSimpleType(targetNamespace, name, xsdLocalName, simpleType);
            return true;
        }
        return false;
    }

    public void finishSimpleType(String targetNamespace, String defaultNamespace, String name, SimpleType simpleType) {
    }

    public void startGlobalElement(String targetNamespace, String defaultNamespace, Element element) {
    }

    public void finishGlobalElement(String targetNamespace, String defaultNamespace, String name) {
    }

    private void processChoice(String targetNamespace, String defaultNamespace, String ownerName, Choice choice, boolean isMany) {
        if (choice != null) {
            processTypeDef(targetNamespace, defaultNamespace, ownerName, choice);

            java.util.List orderedItems = choice.getOrderedElements();
            for (int i = 0; i < orderedItems.size(); i++) {
                Object next = orderedItems.get(i);
                if (!isMany && maxOccursGreaterThanOne(choice.getMaxOccurs())) {
                    isMany = true;
                }

                if (next instanceof Choice) {
                    processChoice(targetNamespace, defaultNamespace, ownerName, (Choice)next, isMany);
                } else if (next instanceof Sequence) {
                    processSequence(targetNamespace, defaultNamespace, ownerName, (Sequence)next, isMany);
                } else if (next instanceof Any) {
                    processAny(targetNamespace, defaultNamespace, (Any)next, ownerName, choice);//isMany??
                } else if (next instanceof Element) {
                    processElement(targetNamespace, defaultNamespace, ownerName, choice, (Element)next, false, isMany);
                } else if (next instanceof Group) {
                    processGroup(targetNamespace, defaultNamespace, ownerName, choice, (Group)next, isMany);
                }
            }
        }
    }

    private void processSequence(String targetNamespace, String defaultNamespace, String ownerName, Sequence sequence, boolean isMany) {
        if (sequence != null) {
            processTypeDef(targetNamespace, defaultNamespace, ownerName, sequence);

            java.util.List orderedItems = sequence.getOrderedElements();
            for (int i = 0; i < orderedItems.size(); i++) {
                Object next = orderedItems.get(i);
                if (!isMany && maxOccursGreaterThanOne(sequence.getMaxOccurs())) {
                    isMany = true;
                }
                if (next instanceof Choice) {
                    processChoice(targetNamespace, defaultNamespace, ownerName, (Choice)next, isMany);
                } else if (next instanceof Sequence) {
                    processSequence(targetNamespace, defaultNamespace, ownerName, (Sequence)next, isMany);
                } else if (next instanceof Any) {
                    processAny(targetNamespace, defaultNamespace, (Any)next, ownerName, sequence);//isMany?
                } else if (next instanceof Element) {
                    processElement(targetNamespace, defaultNamespace, ownerName, sequence, (Element)next, false, isMany);
                } else if (next instanceof Group) {
                    processGroup(targetNamespace, defaultNamespace, ownerName, sequence, (Group)next, isMany);
                }
            }
        }
    }

    private void processAll(String targetNamespace, String defaultNamespace, String ownerName, All all, boolean isMany) {
        if (all != null) {
            processTypeDef(targetNamespace, defaultNamespace, ownerName, all);
            if (!isMany && maxOccursGreaterThanOne(all.getMaxOccurs())) {
                isMany = true;
            }
            java.util.List elements = all.getElements();
            for (int i = 0; i < elements.size(); i++) {
                Object next = elements.get(i);
                if (next instanceof Element) {
                    processElement(targetNamespace, defaultNamespace, ownerName, all, (Element)next, false, isMany);
                }
            }
        }
    }

    private void processComplexContent(String targetNamespace, String defaultNamespace, ComplexContent complexContent) {
        if (complexContent != null) {
            if (complexContent.getExtension() != null) {
                processExtension(targetNamespace, defaultNamespace, complexContent.getOwnerName(), complexContent.getExtension(), false);
            } else {
                if (complexContent.getRestriction() != null) {
                    processRestriction(targetNamespace, defaultNamespace, complexContent.getOwnerName(), complexContent.getRestriction());
                }
            }
        }
    }

    private void processSimpleContent(String targetNamespace, String defaultNamespace, SimpleContent simpleContent) {
        if (simpleContent != null) {
            if (simpleContent.getExtension() != null) {
                processExtension(targetNamespace, defaultNamespace, simpleContent.getOwnerName(), simpleContent.getExtension(), true);
            } else {
                if (simpleContent.getRestriction() != null) {
                    processRestriction(targetNamespace, defaultNamespace, simpleContent.getOwnerName(), simpleContent.getRestriction());
                }
            }
        }
    }

    private void processExtension(String targetNamespace, String defaultNamespace, String ownerName, Extension extension, boolean simpleContent) {
        if (extension != null) {
            String qualifiedType = extension.getBaseType();
            processBaseType(targetNamespace, defaultNamespace, extension.getOwnerName(), qualifiedType, simpleContent);

            //TODO: typedefparticle all seq choice 
            //TODO: attrDecls						
            if (extension.getChoice() != null) {
                processChoice(targetNamespace, defaultNamespace, ownerName, extension.getChoice(), false);
            } else if (extension.getSequence() != null) {
                processSequence(targetNamespace, defaultNamespace, ownerName, extension.getSequence(), false);
            } else if (extension.getAll() != null) {
            }

            processOrderedAttributes(targetNamespace, defaultNamespace, ownerName, extension.getOrderedAttributes());
        }
    }

    private void processRestriction(String targetNamespace, String defaultNamespace, String ownerName, Restriction restriction) {
        if (restriction != null) {
            String qualifiedType = restriction.getBaseType();
            processBaseType(targetNamespace, defaultNamespace, ownerName, qualifiedType);
            boolean alreadyIn = inRestriction;
            if (!alreadyIn) {
                inRestriction = true;
            }

            //TODO: typedefparticle all seq choice 
            //TODO: attrDecls						
            if (restriction.getChoice() != null) {
                processChoice(targetNamespace, defaultNamespace, ownerName, restriction.getChoice(), false);
            } else if (restriction.getSequence() != null) {
                processSequence(targetNamespace, defaultNamespace, ownerName, restriction.getSequence(), false);
            } else if (restriction.getAll() != null) {
            }

            processAttributes(targetNamespace, defaultNamespace, ownerName, restriction.getAttributes());
            if (!alreadyIn) {
                inRestriction = false;
            }
        }
    }

    protected void processGlobalAttributes(Schema schema) {
        Collection attributes = schema.getTopLevelAttributes().values();
        if (attributes == null) {
            return;
        }
        Iterator attributesIter = attributes.iterator();
        while (attributesIter.hasNext()) {
            Attribute nextAttribute = (Attribute)attributesIter.next();
            processGlobalAttribute(schema.getTargetNamespace(), schema.getDefaultNamespace(), nextAttribute);
        }
    }

    private void processGlobalAttribute(String targetNamespace, String defaultNamespace, Attribute attribute) {
        if (attribute.getName() != null) {
            QName qname = new QName(targetNamespace, attribute.getName());
            Object processed = processedAttributes.get(qname);

            if (processed == null) {
                processAttribute(targetNamespace, defaultNamespace, null, attribute, true);
                processedAttributes.put(qname, attribute);
            }
        } else {
            processAttribute(targetNamespace, defaultNamespace, null, attribute, true);
        }
    }

    protected void processElement(String targetNamespace, String defaultNamespace, String ownerName, TypeDefParticle typeDefParticle, Element element, boolean isGlobal, boolean isMany) {
    
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
            //TODO: if this is nested we need to add new type to owner            
            processComplexType(targetNamespace, defaultNamespace, element.getName(), complexType);

            processSimpleElement(targetNamespace, defaultNamespace, ownerName, typeDefParticle, element, qualified, isGlobal, isMany);

            /*finishNestedComplexType(typeDefParticle, element.getName());
            }else{
                finishComplexType(element.getName());
            }
            */
        } else if (element.getSimpleType() != null) {
            processSimpleType(targetNamespace, defaultNamespace, element.getName(), element.getSimpleType());
            processSimpleElement(targetNamespace, defaultNamespace, ownerName, typeDefParticle, element, qualified, isGlobal, isMany);
        } else {
            processSimpleElement(targetNamespace, defaultNamespace, ownerName, typeDefParticle, element, qualified, isGlobal, isMany);
        }
        if (addedNR) {
            namespaceResolvers.remove(namespaceResolvers.size() - 1);
        }
    }

    private void processGroup(String targetNamespace, String defaultNamespace, String ownerName, TypeDefParticle typeDefParticle, Group group, boolean isMany) {
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
                uri = getURIForPrefix(targetNamespace, prefix);
            } else {
                localName = groupName;
                uri = targetNamespace;
            }
            Group globalGroup = rootSchema.getGroup(uri, localName);
            if (globalGroup != null) {
                if (globalGroup.getChoice() != null) {
                    globalGroup.getChoice().setMaxOccurs(group.getMaxOccurs());                    
                    processChoice(targetNamespace, defaultNamespace, ownerName, globalGroup.getChoice(), isMany);
                } else if (globalGroup.getSequence() != null) {
                    globalGroup.getSequence().setMaxOccurs(group.getMaxOccurs());
                    processSequence(targetNamespace, defaultNamespace, ownerName, globalGroup.getSequence(), isMany);
                } else if (globalGroup.getAll() != null) {
                    globalGroup.getAll().setMaxOccurs(group.getMaxOccurs());
                    processAll(targetNamespace, defaultNamespace, ownerName, globalGroup.getAll(), isMany);
                }
            }
        }
    }

    protected void processAttribute(String targetNamespace, String defaultNamespace, String ownerName, Attribute attribute, boolean isGlobal) {
        SimpleType simpleType = attribute.getSimpleType();
        if (simpleType != null) {
            processSimpleType(targetNamespace, defaultNamespace, attribute.getName(), simpleType);
            processSimpleAttribute(targetNamespace, defaultNamespace, ownerName, attribute, isGlobal, rootSchema.isAttributeFormDefault());
        } else {
            processSimpleAttribute(targetNamespace, defaultNamespace, ownerName, attribute, isGlobal, rootSchema.isAttributeFormDefault());
        }
    }

    private void processAttributeGroup(String targetNamespace, String defaultNamespace, String ownerName, AttributeGroup attributeGroup) {
        String attributeGroupName = attributeGroup.getRef();
        if (attributeGroupName != null) {
            int idx = attributeGroupName.indexOf(":");
            String prefix = null;
            String localName = null;
            String uri = null;
            if (idx > -1) {
                localName = attributeGroupName.substring(idx + 1, attributeGroupName.length());
                prefix = attributeGroupName.substring(0, idx);
                uri = getURIForPrefix(targetNamespace, prefix);
            } else {
                localName = attributeGroupName;
                uri = targetNamespace;
            }
            AttributeGroup globalAttributeGroup = rootSchema.getAttributeGroup(uri, localName);
            if (globalAttributeGroup != null) {
                int size = globalAttributeGroup.getAttributes().size();
                if (globalAttributeGroup.getAnyAttribute() != null) {
                    processAnyAttribute(targetNamespace, defaultNamespace, ownerName);
                }
                for (int j = 0; j < size; j++) {
                    processAttribute(targetNamespace, defaultNamespace, ownerName, (Attribute)globalAttributeGroup.getAttributes().get(j), false);
                }
            }
        }
    }

    private void processAttributes(String targetNamespace, String defaultNamespace, String ownerName, java.util.List attributes) {
        if (attributes == null) {
            return;
        }
        for (int i = 0; i < attributes.size(); i++) {
            Attribute nextAttribute = (Attribute)attributes.get(i);
            processAttribute(targetNamespace, defaultNamespace, ownerName, nextAttribute, false);
        }
    }

    protected void processSimpleType(String targetNamespace, String defaultNamespace, String sdoTypeName, SimpleType simpleType) {
        if (simpleType == null) {
            return;
        }
        boolean addedNR = addNextNamespaceResolver(simpleType.getAttributesMap());
        String name = sdoTypeName;
        String originalName = name;
        String nameValue = (String)simpleType.getAttributesMap().get(SDOConstants.SDOXML_NAME_QNAME);
        if (nameValue != null) {
            itemNameToSDOName.put(sdoTypeName, nameValue);
            name = nameValue;
        }

        boolean newType = startSimpleType(targetNamespace, defaultNamespace, name, originalName, simpleType);
        if (newType) {
            Restriction restriction = simpleType.getRestriction();

            if (restriction != null) {
                processRestriction(targetNamespace, defaultNamespace, sdoTypeName, restriction);
            }
            List list = simpleType.getList();
            if (list != null) {
                processList(targetNamespace, defaultNamespace, sdoTypeName, list);
            }

            Union union = simpleType.getUnion();
            if (union != null) {
                processUnion(targetNamespace, defaultNamespace, sdoTypeName, union);
            }

            finishSimpleType(targetNamespace, defaultNamespace, sdoTypeName, simpleType);
        }
        if (addedNR) {
            namespaceResolvers.remove(namespaceResolvers.size() - 1);
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
            XMLContext context = new XMLContext(getSchemaProject());
            XMLUnmarshaller unmarshaller = context.createUnmarshaller();

            Schema schema = (Schema)unmarshaller.unmarshal(xsdSource);
            //populate Imports            
            java.util.List imports = schema.getImports();            
            Iterator iter = imports.iterator();
            while (iter.hasNext()) {                
                Import nextImport = (Import)iter.next();

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
                Include nextInclude = (Include)includesIter.next();

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

    public void setSchemaProject(Project schemaProject) {
        this.schemaProject = schemaProject;
    }

    public Project getSchemaProject() {
        if (schemaProject == null) {
            schemaProject = new SchemaModelProject();

        }
        return schemaProject;
    }

    protected String getURIForPrefix(String targetNamespace, String prefix) {
        if (rootSchema == null) {
            return null;
        }
        String uri = null;
        if (prefix != null) {
            uri = rootSchema.getNamespaceResolver().resolveNamespacePrefix(prefix);
        }
        if (uri == null) {
            uri = targetNamespace;
        }
        return uri;
    }

    protected boolean maxOccursGreaterThanOne(String maxOccurs) {
        if (maxOccurs == null) {
            return false;
        }
        if (maxOccurs.equalsIgnoreCase(Occurs.UNBOUNDED)) {
            return true;
        }        
        return !maxOccurs.equals(Occurs.ONE);        
    }

    protected void processGlobalItem(String targetNamespace, String defaultNamespace, String qualifiedName) {
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

        SimpleType simpleType = (SimpleType)rootSchema.getTopLevelSimpleTypes().get(localName);
        if (simpleType == null) {
            ComplexType complexType = (ComplexType)rootSchema.getTopLevelComplexTypes().get(localName);
            if (complexType == null) {
                Element element = (Element)rootSchema.getTopLevelElements().get(localName);
                if (element == null) {
                    Attribute attribute = (Attribute)rootSchema.getTopLevelAttributes().get(localName);
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

    protected void processBaseType(String targetNamespace, String defaultNamespace, String ownerName, String baseType) {
        processBaseType(targetNamespace, defaultNamespace, ownerName, baseType, false);
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
}