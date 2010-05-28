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
package org.eclipse.persistence.sdo;

import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XSDHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.namespace.QName;
import org.eclipse.persistence.sdo.helper.AttributeMimeTypePolicy;
import org.eclipse.persistence.sdo.helper.InstanceClassConverter;
import org.eclipse.persistence.sdo.helper.ListWrapper;
import org.eclipse.persistence.sdo.helper.SDOMethodAttributeAccessor;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.SDOException;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.FixedMimeTypePolicy;
import org.eclipse.persistence.oxm.mappings.MimeTypePolicy;
import org.eclipse.persistence.oxm.mappings.XMLAnyCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataMapping;
import org.eclipse.persistence.oxm.mappings.XMLChoiceObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLChoiceCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCollectionReferenceMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLFragmentCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLFragmentMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.XMLMapping;
import org.eclipse.persistence.oxm.mappings.XMLNillableMapping;
import org.eclipse.persistence.oxm.mappings.XMLObjectReferenceMapping;
import org.eclipse.persistence.oxm.mappings.XMLTransformationMapping;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.IsSetNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;
import org.eclipse.persistence.sdo.helper.SDOFragmentMappingAttributeAccessor;
import org.eclipse.persistence.sdo.helper.metadata.NamespaceURITransformer;
import org.eclipse.persistence.sdo.helper.metadata.QNameTransformer;

/**
 * <p><b>Purpose</b>:A representation of a Property in the {@link Type type} of a {@link DataObject data object}.
 * <p><b>Responsibilities</b>:<ul>
 * <li> A property represents an element or attribute in XML
 * </ul>
 */
public class SDOProperty implements Property, Serializable {
    private String propertyName;// unique name for this Type within Type
    private SDOType type;// the Type of this Property
    private SDOType containingType;
    private boolean isContainment;// if this Property is containment
    private boolean hasMany;// if this Property is many-valued
    private boolean readOnly;// if this Property is read-only
    private List aliasNames;// a list of alias names for this Property
    private Object defaultValue;// default value of this Property
    private boolean isDefaultSet;// flag whether the default was defined in the schema
    private int indexInType = -1;
    private int indexInDeclaredProperties = -1;
    private SDOProperty opposite;// the opposite Property
    private boolean xsd;
    private String xsdLocalName;
    private boolean global;
    private boolean namespaceQualified;
    private transient DatabaseMapping xmlMapping;
    private Map propertyValues;
    private boolean nullable;
    private QName xsdType;
    private boolean valueProperty;
    private List appInfoElements;
    private Map appInfoMap;
    private boolean nameCollision;
    private String uri;
    private boolean isSubstitutable;
    private Collection<SDOProperty> substitutableElements;
    private boolean finalized;
    private static boolean isActivationAvailable;

    static {
        isActivationAvailable = false;
        try {
            PrivilegedAccessHelper.getClassForName("javax.activation.DataHandler");
            PrivilegedAccessHelper.getClassForName("javax.mail.internet.MimeMultipart");
            isActivationAvailable = true;
        } catch(ClassNotFoundException ex) {
            //ignore this exception and let the boolean remain false;
        }
    }
    // hold the context containing all helpers so that we can preserve inter-helper relationships
    private HelperContext aHelperContext;

    public SDOProperty(HelperContext aContext) {
        aHelperContext = aContext;
    }

    public SDOProperty(HelperContext aContext, String aName) {
        this(aContext);
        setName(aName);
    }

    public SDOProperty(HelperContext aContext, String aName, SDOType aType) {
        this(aContext, aName);
        setType(aType);
    }
    
    public SDOProperty(HelperContext aContext, String aUri, String aName, SDOType aType) {
    	this(aContext, aName, aType);
    	this.setUri(aUri);
    }

    /**
     * Returns the name of the Property.
     * @return the Property name.
     */
    public String getName() {
        return propertyName;
    }

    /**
     * Returns the type of the Property.
     * @return the Property type.
     */
    public SDOType getType() {
        return type;
    }

    /**
     * Returns whether the Property is many-valued.
     * @return <code>true</code> if the Property is many-valued.
     */
    public boolean isMany() {
        return hasMany;
    }

    /**
      * Return whether or not this is an open content property.
      * @return true if this property is an open content property.
      */
    public boolean isOpenContent() {
        int idx = getIndexInType();
        if (idx == -1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns whether the Property is containment, i.e., whether it represents by-value composition.
     * @return <code>true</code> if the Property is containment.
     */
    public boolean isContainment() {
        return isContainment;
    }

    /**
     * Returns the containing type of this Property.
     * @return the Property's containing type.
     * @see Type#getProperties()
     */
    public SDOType getContainingType() {
        return containingType;
    }

    /**
     * Returns the default value this Property will have in a {@link DataObject data object} where the Property hasn't been set.
     * @return the default value.
     */
    public Object getDefault() {
        if (null == defaultValue) {
            // return an Object wrapper for numeric primitives or null
            return type.getPseudoDefault();
        } else {
            return defaultValue;
        }
    }

    /**
     * Returns true if values for this Property cannot be modified using the SDO APIs.
     * When true, DataObject.set(Property property, Object value) throws an exception.
     * Values may change due to other factors, such as services operating on DataObjects.
     * @return true if values for this Property cannot be modified.
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * Returns the opposite Property if the Property is bi-directional or null otherwise.
     * @return the opposite Property if the Property is bi-directional or null
     */
    public SDOProperty getOpposite() {
        return opposite;
    }

    /**
     * Return a list of alias names for this Property.
     * @return a list of alias names for this Property.
     */
    public List getAliasNames() {
        if (aliasNames == null) {
            aliasNames = new ArrayList();
        }
        return aliasNames;
    }

    /**
     * INTERNAL:
     * Assign a string as a unique name of this Property among Properties that belongs
     * to a DataObject.
     * @param name    a string representing unique name of a property of a DataObject.
     */
    public void setName(String name) {
        propertyName = name;
    }

    /**
     * INTERNAL:
     * Assign a Type to this Property.
     * @param type   the type of this property.
     */
    public void setType(Type type) {
        this.type = (SDOType) type;
        if(isNullable()){
        	updateType();
        }
    }

    /**
     * INTERNAL:
     * Set this Property's Containment which shows parent-child relationship in a tree
     * of DataObjects.
     * @param containment     a boolean value showing if this Property is containment.
     */
    public void setContainment(boolean containment) {
        isContainment = containment;
    }

    /**
     * INTERNAL:
     * Set this property as single-valued(false) or many-valued(true).
     * Default is false.
     * @param many    a boolean value if this property is many-valued or not.
     */
    public void setMany(boolean many) {
        hasMany = many;
    }

    /**
     * INTERNAL:
     * Set this Property as read-only Property.
     * @param readOnly    boolean value implying this Property is readonly.
     */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    /**
     * INTERNAL:
     * Set this Property's alias name list which are unique within the Type.
     * @param names     a list of alias name of this Property.
     */
    public void setAliasNames(List names) {
        aliasNames = names;
    }

    /**
     * INTERNAL:
     * Set the containing type of this Property.
     * @param type       a Type which is the containing type of this Property
     */
    public void setContainingType(Type type) {
        containingType = (SDOType) type;
    }

    /**
     * INTERNAL:
     * Set the default value of this Property.
     * @param aDefaultValue     an Object to be the default value of this type.
     */
    public void setDefault(Object aDefaultValue) {
        defaultValue = aDefaultValue;
        isDefaultSet = true;
    }

    /**
     * INTERNAL:
    * Set the opposite Property.  If not null then this Property is a of a bi-directional Property.
    * @param the opposite Property if the Property is bi-directional, otherwise null
    */
    public void setOpposite(Property property) {
        opposite = (SDOProperty) property;
    }

    /**
      * INTERNAL:
      * Set if this property was declared in an XML schema.
      * @param xsd a boolean representing if this property was declared in an XML schema
      */
    public void setXsd(boolean xsd) {
        this.xsd = xsd;
    }

    /**
      * INTERNAL:
      * Returns if this property was declared in an XML schema.  Defaults to false.
      * @return if this property was declared in an XML schema
      */
    public boolean isXsd() {
        return xsd;
    }

    /**
      * INTERNAL:
      * Set the local name of this property.
      * @param xsdLocalName a String representing the local name of this property if it was declared in an XML schema
      */
    public void setXsdLocalName(String xsdLocalName) {
        this.xsdLocalName = xsdLocalName;
    }

    /**
      * INTERNAL:
      * Returns the local name of the Property.
      * @return the local name of the property.
      */
    public String getXsdLocalName() {
        return xsdLocalName;
    }

    /**
     * INTERNAL:
     * Set if the element or attribute corresponding to this Property is namespace qualified in the XSD.
     *  @param namespaceQualified a boolean representing if the element or attribute corresponding to this Property is namespace qualified in the XSD.
     **/
    public void setNamespaceQualified(boolean namespaceQualified) {
        this.namespaceQualified = namespaceQualified;
    }

    /**
      * INTERNAL:
      * Returns if the element or attribute corresponding to this Property should be namespace qualified in the XSD.
      * @return if the element or attribute corresponding to this Property should be namespace qualified in the XSD.
      */
    public boolean isNamespaceQualified() {
        return namespaceQualified;
    }

    /**
      * INTERNAL:
      */
    public void setXmlMapping(DatabaseMapping xmlMapping) {
        this.xmlMapping = xmlMapping;
    }

    /**
      * INTERNAL:
      */
    public DatabaseMapping getXmlMapping() {
        return xmlMapping;
    }

    /**
      * INTERNAL:
      */
    public void setGlobal(boolean global) {
        this.global = global;
    }

    /**
      * INTERNAL:
      */
    public boolean isGlobal() {
        return global;
    }

    /**
      * INTERNAL:
      * Alter the default state of the policy to act as a nillable null policy
     * @param aMapping
     * @param propertyName
      */
    private void setIsSetNillablePolicyOnMapping(XMLNillableMapping aMapping, Object propertyName) {
    	AbstractNullPolicy aNullPolicy = setIsSetPolicyOnMapping(aMapping, propertyName);
    	// Alter unmarshal policy state
    	aNullPolicy.setNullRepresentedByEmptyNode(false);
    	aNullPolicy.setNullRepresentedByXsiNil(true);
    	// Alter marshal policy state
    	aNullPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.XSI_NIL);
    }

    /**
     * INTERNAL
     * Alter the default state of the policy to act as an optional non-nillable null policy
     * @param aMapping
     * @param propertyName
     */
    private void setIsSetOptionalPolicyOnMapping(XMLNillableMapping aMapping, Object propertyName) {
    	AbstractNullPolicy aNullPolicy = setIsSetPolicyOnMapping(aMapping, propertyName);
    	// Alter unmarshal policy state
    	aNullPolicy.setNullRepresentedByEmptyNode(false);
    	aNullPolicy.setNullRepresentedByXsiNil(false);
    	// Alter marshal policy state
    	aNullPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.EMPTY_NODE);//.ABSENT_NODE);
    }

    /**
     * INTERNAL:
     * Create and set an IsSetNodePolicy on the mapping - leaving the policy in default state
     * @param aMapping
     * @param propertyName
     * @return
     */
    private AbstractNullPolicy setIsSetPolicyOnMapping(XMLNillableMapping aMapping, Object propertyName) {
        AbstractNullPolicy aNullPolicy = new IsSetNullPolicy();
        // Set the isSet method signature on policy
        ((IsSetNullPolicy)aNullPolicy).setIsSetMethodName(SDOConstants.SDO_ISSET_METHOD_NAME);
        // Set fields even though defaults are set
        //aNullPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.EMPTY_NODE);
        // Parameter type is always String
        ((IsSetNullPolicy)aNullPolicy).setIsSetParameterTypes(new Class[] { ClassConstants.STRING });
        ((IsSetNullPolicy)aNullPolicy).setIsSetParameters(new Object[] { propertyName });
        aMapping.setNullPolicy(aNullPolicy);
        return aNullPolicy;
    }
    
    /**
      * INTERNAL:
      */
    public void buildMapping(String mappingUri) {
        buildMapping(mappingUri, -1);
    }

    /**
      * INTERNAL:
      */
    public void buildMapping(String mappingUri, int indexToAdd) {
        if (getContainingType().isDataType()) {
            return;
        }

        if (getType().isChangeSummaryType()) {
            buildChangeSummaryMapping();
            addMappingToOwner(false, indexToAdd);
        } else if (isNameCollision()) {
            xmlMapping = new XMLAnyCollectionMapping();
            xmlMapping.setAttributeName(getName());
            addMappingToOwner(true, indexToAdd);
        } else {
            boolean sdoMethodAccessor = true;
            if (!getType().isDataType()) {
                if (getType().isDataObjectType()) {
                    getType().setImplClassName(SDOConstants.SDO_DATA_OBJECT_IMPL_CLASS_NAME);
                    if(getXsdType() != null && !getXsdType().equals(SDOConstants.ANY_TYPE_QNAME)) {
                       if (isMany()) {
                            xmlMapping = buildXMLCompositeCollectionMapping(mappingUri);
                        } else {
                            xmlMapping = buildXMLCompositeObjectMapping(mappingUri);
                        }
                    }else{
                      sdoMethodAccessor = false;
                      if (isMany()) {
                          xmlMapping = buildXMLFragmentCollectionMapping(mappingUri);
                      } else {
                          xmlMapping = buildXMLFragmentMapping(mappingUri);
                      }
                    }
                } else {
                    if (!getType().isFinalized()) {
                        getType().getNonFinalizedReferencingProps().add(this);
                        getType().getNonFinalizedMappingURIs().add(mappingUri);
                        return;
                    }
                    if(isSubstitutable()) {
                        if(isMany()) {
                            xmlMapping = buildXMLChoiceCollectionMapping(mappingUri);
                        } else {
                            xmlMapping = buildXMLChoiceObjectMapping(mappingUri);
                        }
                    }
                    else if (isMany()) {
                        if (isContainment()) {
                            xmlMapping = buildXMLCompositeCollectionMapping(mappingUri);
                        } else {
                            xmlMapping = buildXMLCollectionReferenceMapping(mappingUri);
                        }
                    } else {
                        if (isContainment()) {
                            xmlMapping = buildXMLCompositeObjectMapping(mappingUri);
                        } else {
                            xmlMapping = buildXMLObjectReferenceMapping(mappingUri);
                        }
                    }
                }
            } else {
                if (isMany()) {
                    MimeTypePolicy mimeTypePolicy = getMimeTypePolicy();

                    //Removed check for XSD type since XSD type can't be set via typeHelper.define
                    if (isActivationAvailable && (!aHelperContext.getXSDHelper().isAttribute(this) && ((mimeTypePolicy != null) || 
                            ((getType().getInstanceClass() != null) && getType().getInstanceClass().getName().equals("javax.activation.DataHandler")) ||
                            (getXsdType() != null && getXsdType().equals(XMLConstants.BASE_64_BINARY_QNAME))))) {
                        xmlMapping = buildXMLBinaryDataCollectionMapping(mappingUri, mimeTypePolicy);
                    } else {
                        if(isSubstitutable()) {
                            xmlMapping = buildXMLChoiceCollectionMapping(mappingUri);
                        } else {
                            xmlMapping = buildXMLCompositeDirectCollectionMapping(mappingUri);
                        }
                    }
                } else {
                    MimeTypePolicy mimeTypePolicy = getMimeTypePolicy();

                    //Removed check for XSD type since XSD type can't be set via typeHelper.define
                    if (isActivationAvailable && (!aHelperContext.getXSDHelper().isAttribute(this) && ((mimeTypePolicy != null) || 
                            ((getType().getInstanceClass() != null) && getType().getInstanceClass().getName().equals("javax.activation.DataHandler")) ||
                            (getXsdType() != null && getXsdType().equals(XMLConstants.BASE_64_BINARY_QNAME))))) {
                         xmlMapping = buildXMLBinaryDataMapping(mappingUri, mimeTypePolicy);
                    } else {
                        if(isSubstitutable()) {
                            xmlMapping = buildXMLChoiceObjectMapping(mappingUri);
                        } else {
                            if(XMLConstants.QNAME_QNAME.equals(xsdType)) {
                                xmlMapping = buildXMLTransformationMapping(mappingUri);
                            } else {
                                xmlMapping = buildXMLDirectMapping(mappingUri);
                            }
                        }
                    }
                }
            }
            addMappingToOwner(sdoMethodAccessor, indexToAdd);
        }
    }

    /**
      * INTERNAL:
      */
    public void buildChangeSummaryMapping() {
        XMLCompositeObjectMapping aCMapping = new XMLCompositeObjectMapping();
        aCMapping.setAttributeName(getName());
        String xpath = getQualifiedXPath(getContainingType().getURI(), false);

        aCMapping.setXPath(xpath);
        aCMapping.setGetMethodName("getChangeSummary");
        aCMapping.setSetMethodName("_setChangeSummary");
        aCMapping.setReferenceClass(SDOChangeSummary.class);
        // Handle nillable element support via the nullable property
        if (nullable) {
            setIsSetNillablePolicyOnMapping(aCMapping, propertyName);
        } else {
            // elements or attributes
            setIsSetOptionalPolicyOnMapping(aCMapping, propertyName);
        }
        setXmlMapping(aCMapping);

    }

    /**
      * INTERNAL:
      */
    public void addMappingToOwner(boolean sdoMethodAttributeAccessor, int indexToAdd) {
        if (xmlMapping != null) {
            if (sdoMethodAttributeAccessor) {
                SDOMethodAttributeAccessor accessor = null;
                if (this.getType().isDataType()) {
                    Class theClass = getType().getInstanceClass();
                    accessor = new SDOMethodAttributeAccessor(this, theClass);
                } else {
                    accessor = new SDOMethodAttributeAccessor(this);
                }
                xmlMapping.setAttributeAccessor(accessor);
            }
            if ((getContainingType() != null) && !getContainingType().isDataType()) {
                ClassDescriptor containingDescriptor = getContainingType().getXmlDescriptor();
                xmlMapping.setDescriptor(containingDescriptor);
                XMLMapping mapping = (XMLMapping)getContainingType().getXmlDescriptor().getMappingForAttributeName(getName());
                if (mapping != null) {
                    getContainingType().getXmlDescriptor().getMappings().remove(mapping);
                }
                if(indexToAdd == -1) {
                    getContainingType().getXmlDescriptor().getMappings().add(xmlMapping);
                } else {
                    //iterate over the mappings and find the correct place to insert this mapping relative to the 
                    //indecies of the others.
                    SDOType containingType = getContainingType();
                    Vector<DatabaseMapping> mappings = containingType.getXmlDescriptor().getMappings();
                    boolean added = false;
                    for(int i = 0; i < mappings.size(); i++) {
                        DatabaseMapping next = mappings.get(i);
                        SDOProperty associatedProperty = containingType.getProperty(next.getAttributeName());
                        if(associatedProperty != null && indexToAdd < associatedProperty.getIndexInType()) {
                            mappings.add(i, xmlMapping);
                            added = true;
                            break;
                        }
                    }
                    if(!added) {
                        getContainingType().getXmlDescriptor().getMappings().add(xmlMapping);
                    }
                }
            }
        }
    }

    private DatabaseMapping buildXMLBinaryDataMapping(String mappingUri, MimeTypePolicy mimeTypePolicy) {
        XMLBinaryDataMapping mapping = new XMLBinaryDataMapping();
        mapping.setAttributeName(getName());
        String xpath = getQualifiedXPath(mappingUri, false);
        mapping.setMimeTypePolicy(mimeTypePolicy);
        mapping.setXPath(xpath);

        ((XMLField)mapping.getField()).setSchemaType(XMLConstants.BASE_64_BINARY_QNAME);
        
        if (shouldAddInstanceClassConverter()) {
            InstanceClassConverter converter = new InstanceClassConverter();
            converter.setCustomClass(getType().getInstanceClass());
            mapping.setConverter(converter);
        }

        // Set the null policy on the mapping
        // Use NullPolicy or IsSetNullPolicy 
        if (nullable) { // elements only
            setIsSetNillablePolicyOnMapping(mapping, propertyName);
        } else {
            // elements or attributes
            setIsSetOptionalPolicyOnMapping(mapping, propertyName);
        }
        mapping.getNullPolicy().setNullRepresentedByEmptyNode(true);

        return mapping;
    }

    private DatabaseMapping buildXMLBinaryDataCollectionMapping(String mappingUri, MimeTypePolicy mimeTypePolicy) {
        XMLBinaryDataCollectionMapping mapping = new XMLBinaryDataCollectionMapping();
        mapping.setAttributeName(getName());
        String xpath = getQualifiedXPath(mappingUri, false);

        if (!getType().getInstanceClassName().equals("javax.activation.DataHandler")) {
            mapping.setAttributeElementClass(getType().getInstanceClass());
        }
        mapping.setMimeTypePolicy(mimeTypePolicy);
        mapping.setXPath(xpath);

        ((XMLField)mapping.getField()).setSchemaType(XMLConstants.BASE_64_BINARY_QNAME);

        if (shouldAddInstanceClassConverter()) {
            InstanceClassConverter converter = new InstanceClassConverter();
            converter.setCustomClass(getType().getInstanceClass());
            mapping.setValueConverter(converter);
        }

        // mapping.setShouldInlineBinaryData(true);
        return mapping;
    }

    private DatabaseMapping buildXMLTransformationMapping(String mappingUri) {
        XMLTransformationMapping mapping = new XMLTransformationMapping();
        mapping.setAttributeName(getName());

        String xpath = getQualifiedXPath(mappingUri, true);
        String xpathMinusText;
        int indexOfTextXPath = xpath.lastIndexOf("/text()");
        if (indexOfTextXPath < 0) {
            xpathMinusText = xpath;
        } else {
            xpathMinusText = xpath.substring(0, indexOfTextXPath);
        }
        QNameTransformer transformer = new QNameTransformer(xpath);

        mapping.setAttributeTransformer(transformer);
        mapping.addFieldTransformer(xpath, transformer);
        
        NamespaceResolver nsr = new NamespaceResolver();
        nsr.put(XMLConstants.XMLNS, XMLConstants.XMLNS_URL);

        XMLField field = new XMLField();
        field.setNamespaceResolver(nsr);
        field.setXPath(xpathMinusText + "/@" + XMLConstants.XMLNS + ":" + QNameTransformer.QNAME_NAMESPACE_PREFIX);
        
        mapping.addFieldTransformer(field, new NamespaceURITransformer());
        
        return mapping;
    }
    
    private DatabaseMapping buildXMLDirectMapping(String mappingUri) {
        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName(getName());
        String xpath = getQualifiedXPath(mappingUri, true);
        mapping.setXPath(xpath);

        if (getXsdType() != null) {
            ((XMLField)mapping.getField()).setSchemaType(getXsdType());
        } 

        if (getType().getInstanceClass() != null) {
            if (shouldAddInstanceClassConverter()) {
                InstanceClassConverter converter = new InstanceClassConverter();
                converter.setCustomClass(getType().getInstanceClass());
                mapping.setConverter(converter);
            }
        }

        // Set the null policy on the mapping
        // Use NullPolicy or IsSetNullPolicy 
        if (nullable) { // elements only
            setIsSetNillablePolicyOnMapping(mapping, propertyName);
        } else {
      		// elements or attributes
            setIsSetOptionalPolicyOnMapping(mapping, propertyName);
        }
        mapping.getNullPolicy().setNullRepresentedByEmptyNode(true);
        return mapping;
    }

    private DatabaseMapping buildXMLCompositeDirectCollectionMapping(String mappingUri) {
        XMLCompositeDirectCollectionMapping mapping = new XMLCompositeDirectCollectionMapping();
        mapping.setAttributeName(getName());
        String xpath = getQualifiedXPath(mappingUri, true);

        mapping.setXPath(xpath);
        mapping.setAttributeElementClass(getType().getInstanceClass());

        if (getXsdType() != null) {
            ((XMLField)mapping.getField()).setSchemaType(getXsdType());
        } 

        if (getType().equals(SDOConstants.SDO_STRINGS)) {
            mapping.setUsesSingleNode(true);
        }
        
        if (getType().getInstanceClass() != null) {
            if (shouldAddInstanceClassConverter()) {
                InstanceClassConverter converter = new InstanceClassConverter();
                converter.setCustomClass(getType().getInstanceClass());
                mapping.setValueConverter(converter);
            }
        }

        return mapping;
    }

    private DatabaseMapping buildXMLCompositeCollectionMapping(String mappingUri) {
        XMLCompositeCollectionMapping mapping = new XMLCompositeCollectionMapping();
        mapping.setAttributeName(getName());
        String xpath = getQualifiedXPath(mappingUri, false);

        mapping.setXPath(xpath);

        if (!getType().isDataObjectType()) {
            QName schemaContext = getType().getXmlDescriptor().getSchemaReference().getSchemaContextAsQName(getType().getXmlDescriptor().getNamespaceResolver());
            ((XMLField)mapping.getField()).setLeafElementType(schemaContext);

            mapping.setReferenceClassName(getType().getImplClassName());
            mapping.setReferenceClass(getType().getImplClass());
        }else{
            if(getXsdType()!= null){               
                ((XMLField)mapping.getField()).setLeafElementType(getXsdType());
            }
        }
        mapping.useCollectionClass(ListWrapper.class);
        
        // Set null policy on mapping for xsi:nil support:
        // - aNullPolicy.setNullRepresentedByEmptyNode(false);
        // - aNullPolicy.setNullRepresentedByXsiNil(true);
        setIsSetNillablePolicyOnMapping(mapping, propertyName);
        return mapping;
    }

    private DatabaseMapping buildXMLCompositeObjectMapping(String mappingUri) {
        XMLCompositeObjectMapping mapping = new XMLCompositeObjectMapping();
        mapping.setAttributeName(getName());
        String xpath = getQualifiedXPath(mappingUri, false);

        mapping.setXPath(xpath);

        if (!getType().isDataObjectType()) {
            QName schemaContext = getType().getXmlDescriptor().getSchemaReference().getSchemaContextAsQName(getType().getXmlDescriptor().getNamespaceResolver());
            ((XMLField)mapping.getField()).setLeafElementType(schemaContext);
            mapping.setReferenceClassName(getType().getImplClassName());
            mapping.setReferenceClass(getType().getImplClass());
        }else{
            if(getXsdType()!= null){               
              ((XMLField)mapping.getField()).setLeafElementType(getXsdType());
            }
        }

        // Handle nillable element support via the nullable property
        if (nullable) {
            setIsSetNillablePolicyOnMapping(mapping, propertyName);
        } else {
      		// elements or attributes
            setIsSetOptionalPolicyOnMapping(mapping, propertyName);
        }
        return mapping;
    }

    private DatabaseMapping buildXMLObjectReferenceMapping(String mappingUri) {
        XMLObjectReferenceMapping mapping = new XMLObjectReferenceMapping();
        mapping.setAttributeName(getName());

        if (getType().isDataObjectType()) {
            getType().setImplClassName(SDOConstants.SDO_DATA_OBJECT_IMPL_CLASS_NAME);
        }
        mapping.setReferenceClassName(getType().getImplClassName());
        mapping.setReferenceClass(getType().getImplClass());

        String sourcexpath = getQualifiedXPath(getContainingType().getURI(), true);

        // Get reference ID property if it exists
        SDOProperty targetIDProp = getIDProp(getType());

        if (targetIDProp != null) {
            String targetxpath = targetIDProp.getQualifiedXPath(getType().getURI(), true);
            mapping.addSourceToTargetKeyFieldAssociation(sourcexpath, targetxpath);
        } else {
            throw SDOException.noTargetIdSpecified(getType().getURI(), getType().getName());
        }
        return mapping;
    }
    
    private DatabaseMapping buildXMLChoiceObjectMapping(String mappingUri) {
        XMLChoiceObjectMapping mapping = new XMLChoiceObjectMapping();
        mapping.setAttributeName(getName());

        //First add XPath for this property
        String xPath = getQualifiedXPath(mappingUri, getType().isDataType());
        mapping.addChoiceElement(xPath, getType().getImplClass());
        //For each substitutable property, create the xpath and add it.
        Iterator<SDOProperty> properties = this.getSubstitutableElements().iterator();
        while(properties.hasNext()) {
            SDOProperty nextProp = properties.next();
            xPath = nextProp.getQualifiedXPath(mappingUri, nextProp.getType().isDataType(), getContainingType());
            mapping.addChoiceElement(xPath, nextProp.getType().getImplClass());
        }
        return mapping;
    }
    
    private DatabaseMapping buildXMLChoiceCollectionMapping(String mappingUri) {
        XMLChoiceCollectionMapping mapping = new XMLChoiceCollectionMapping();
        mapping.setAttributeName(getName());
        mapping.useCollectionClass(ListWrapper.class);
        //First add XPath for this property
        String xPath = getQualifiedXPath(mappingUri, getType().isDataType());
        mapping.addChoiceElement(xPath, getType().getImplClass());
        //For each substitutable property, create the xpath and add it.
        Iterator<SDOProperty> properties = this.getSubstitutableElements().iterator();
        while(properties.hasNext()) {
            SDOProperty nextProp = properties.next();
            xPath = nextProp.getQualifiedXPath(mappingUri, nextProp.getType().isDataType(), getContainingType());
            mapping.addChoiceElement(xPath, nextProp.getType().getImplClass());
        }
        return mapping;
    }

    /**
     * INTERNAL:
     * Get the reference ID open content Property if it exists for this Type.
     * @return id Property or null
     */
    private SDOProperty getIDProp(Type aType) {
        return (SDOProperty)aType.getProperty((String)((SDOType)aType).get(SDOConstants.ID_PROPERTY));
    }

    private DatabaseMapping buildXMLCollectionReferenceMapping(String mappingUri) {
        XMLCollectionReferenceMapping mapping = new XMLCollectionReferenceMapping();
        mapping.setAttributeName(getName());

        if (getType().isDataObjectType()) {
            getType().setImplClassName(SDOConstants.SDO_DATA_OBJECT_IMPL_CLASS_NAME);
        }
        mapping.setReferenceClassName(getType().getImplClassName());
        mapping.setReferenceClass(getType().getImplClass());
        mapping.setUsesSingleNode(true);

        mapping.useCollectionClass(ArrayList.class);
        String sourcexpath = getQualifiedXPath(getContainingType().getURI(), true);

        // Get reference ID property if it exists
        SDOProperty targetIDProp = getIDProp(getType());
        if (targetIDProp != null) {
            String targetxpath = targetIDProp.getQualifiedXPath(getType().getURI(), true);
            mapping.addSourceToTargetKeyFieldAssociation(sourcexpath, targetxpath);
        } else {
            throw SDOException.noTargetIdSpecified(getType().getURI(), getType().getName());
        }
        return mapping;
    }

    private boolean shouldAddInstanceClassConverter() {
        Object value = getType().get(SDOConstants.JAVA_CLASS_PROPERTY);
        if (getType().isDataType() && (value != null)) {
            Class instanceClass = getType().getInstanceClass();
            String instanceClassName = getType().getInstanceClassName();
            if (((instanceClassName != null) && instanceClassName.equals("javax.activation.DataHandler")) ||//
                    (instanceClass == ClassConstants.ABYTE) ||//
                    (instanceClass == ClassConstants.APBYTE) ||//
                    (instanceClass == ClassConstants.BYTE) ||//
                    (instanceClass == ClassConstants.PBYTE) ||//
                    (instanceClass == ClassConstants.CHAR) ||//
                    (instanceClass == ClassConstants.PCHAR) ||//
                    (instanceClass == ClassConstants.DOUBLE) ||//
                    (instanceClass == ClassConstants.PDOUBLE) ||//
                    (instanceClass == ClassConstants.FLOAT) ||//
                    (instanceClass == ClassConstants.PFLOAT) ||//
                    (instanceClass == ClassConstants.LONG) ||//
                    (instanceClass == ClassConstants.PLONG) ||//
                    (instanceClass == ClassConstants.SHORT) ||//
                    (instanceClass == ClassConstants.PSHORT) ||//
                    (instanceClass == ClassConstants.INTEGER) ||//
                    (instanceClass == ClassConstants.PINT) ||//
                    (instanceClass == ClassConstants.BIGDECIMAL) ||//
                    (instanceClass == ClassConstants.BIGINTEGER) ||//
                    (instanceClass == ClassConstants.STRING) ||//
                    (instanceClass == ClassConstants.UTILDATE) ||//
                    (instanceClass == ClassConstants.CALENDAR) ||//
                    (instanceClass == ClassConstants.TIME) ||//
                    (instanceClass == ClassConstants.SQLDATE) ||//
                    (instanceClass == ClassConstants.TIMESTAMP)) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
      * INTERNAL:
      */
    public String getXPath() {
        String xpath = getXsdLocalName();
        if (xpath == null) {
            xpath = getName();
        }
        return xpath;
    }

    /**
      * INTERNAL:
      */
    public String getQualifiedXPath(String uri, boolean simple) {
        SDOType containingType = this.getContainingType();
        return getQualifiedXPath(uri, simple, containingType);
    }
    
    private String getQualifiedXPath(String uri, boolean simple, SDOType containingType) {
        if (valueProperty) {
            return "text()";
        }
        String xpath = getXPath();
        String prefix = null;
        if (isNamespaceQualified()) {
            prefix = containingType.getXmlDescriptor().getNonNullNamespaceResolver().resolveNamespaceURI(uri);
        }

        if (aHelperContext.getXSDHelper().isAttribute(this)) {
            if (prefix != null) {
                xpath = prefix + ":" + xpath;
            }
            xpath = "@" + xpath;
        } else {
            if (prefix != null) {
                xpath = prefix + ":" + xpath;
            }
            if (simple) {
                xpath = xpath + "/text()";
            }
        }
        return xpath;
    }

    public Object get(Property property) {               
        return getPropertyValues().get(property);
    }

    public List getInstanceProperties() {
        return new ArrayList(getPropertyValues().keySet());
    }

    /**
      * INTERNAL:
      */
    public void setPropertyValues(Map properties) {
        this.propertyValues = properties;
    }

    /**
      * INTERNAL:
      */
    public Map getPropertyValues() {
        if (propertyValues == null) {
            propertyValues = new HashMap();
        }
        return propertyValues;
    }

    public void setInstanceProperty(Property property, Object value) {
        getPropertyValues().put(property, value);
        if(SDOConstants.SDOXML_URL.equals(((SDOProperty) property).getUri()) && SDOConstants.SDOXML_DATATYPE.equals(property.getName()) && value instanceof Type) {
            setType((Type)value);
        }
        if(SDOConstants.ORACLE_SDO_URL.equals(((SDOProperty) property).getUri()) && SDOConstants.XML_SCHEMA_TYPE_NAME.equals(property.getName()) && value instanceof Type) {
            Type schemaType = (Type)value;
            QName schemaTypeQName = new QName(schemaType.getURI(), schemaType.getName());
            setXsdType(schemaTypeQName);
        }
    }

    /**
      * INTERNAL:
      */
    public void setIndexInType(int indexInType) {
        this.indexInType = indexInType;
    }

    /**
      * INTERNAL:
      */
    public int getIndexInType() {
        if ((indexInType == -1) && (getContainingType() != null)) {
            indexInType = getContainingType().getProperties().indexOf(this);
        }
        return indexInType;
    }

    /**
      * INTERNAL:
      */
    public void setNullable(boolean nullable) {
        this.nullable = nullable;
        if(nullable && getType() != null){
        	updateType();
        }
    }

    public boolean isNullable() {
        return nullable;
    }

    /**
      * INTERNAL:
      */
    public void setXsdType(QName xsdType) {
        this.xsdType = xsdType;
    }

    /**
      * INTERNAL:
      */
    public QName getXsdType() {
        return xsdType;
    }

    /**
      * INTERNAL:
      */
    public MimeTypePolicy getMimeTypePolicy() {
        String mimeType = (String)get(SDOConstants.MIME_TYPE_PROPERTY);
        if (mimeType != null) {
            return new FixedMimeTypePolicy(mimeType);
        } else {
            mimeType = (String)get(SDOConstants.MIME_TYPE_PROPERTY_PROPERTY);
            if (mimeType != null) {
                return new AttributeMimeTypePolicy(mimeType);
            }
        }
        return null;
    }

    /**
      * INTERNAL:
      */
    public void setIndexInDeclaredProperties(int indexInDeclaredProperties) {
        this.indexInDeclaredProperties = indexInDeclaredProperties;
    }

    /**
      * INTERNAL:
      */
    public int getIndexInDeclaredProperties() {
        if ((indexInDeclaredProperties == -1) && (getContainingType() != null)) {
            indexInDeclaredProperties = getContainingType().getDeclaredProperties().indexOf(this);
        }
        return indexInDeclaredProperties;
    }

    /**
      * INTERNAL:
      */
    public void setValueProperty(boolean valueProperty) {
        this.valueProperty = valueProperty;
    }

    /**
      * INTERNAL:
      */
    public boolean isValueProperty() {
        return valueProperty;
    }

    /**
      * INTERNAL:
      */
    public void setAppInfoElements(List appInfoElements) {
        this.appInfoElements = appInfoElements;
    }

    /**
      * INTERNAL:
      */
    public List getAppInfoElements() {
        return appInfoElements;
    }

    /**
      * INTERNAL:
      */
    public Map getAppInfoMap() {
        if (appInfoMap == null) {
            appInfoMap = ((SDOXSDHelper)aHelperContext.getXSDHelper()).buildAppInfoMap(appInfoElements);
        }
        return appInfoMap;
    }

    public void setNameCollision(boolean nameCollision) {
        this.nameCollision = nameCollision;
    }

    public boolean isNameCollision() {
        return nameCollision;
    }

    /**
     * INTERNAL:
     * Return whether the default value has been set by the schema
     * either via a define by an XSD or a DataObject.
     * @return isDefaultSet
     */
    public boolean isDefaultSet() {
        return isDefaultSet;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }
    
    public XMLFragmentMapping buildXMLFragmentMapping(String uri) {
        XMLFragmentMapping mapping = new XMLFragmentMapping();
        mapping.setAttributeName(getName());
        mapping.setXPath(getQualifiedXPath(uri, false));
        mapping.setAttributeAccessor(new SDOFragmentMappingAttributeAccessor(this, aHelperContext));
        
        return mapping;
    }
    
    public XMLFragmentCollectionMapping buildXMLFragmentCollectionMapping(String mappingUri) {
        XMLFragmentCollectionMapping mapping = new XMLFragmentCollectionMapping();
        mapping.setAttributeName(getName());
        mapping.setXPath(getQualifiedXPath(mappingUri, false));
        mapping.setAttributeAccessor(new SDOFragmentMappingAttributeAccessor(this, aHelperContext));
        
        return mapping;
    }
    
    public boolean isSubstitutable() {
        return this.isSubstitutable;
    }
    
    public void setSubstitutable(boolean substitutable) {
        this.isSubstitutable = substitutable;
    }
    
    public Collection<SDOProperty> getSubstitutableElements() {
        return this.substitutableElements;
    }
    
    public void setSubstitutableElements(Collection<SDOProperty> elements) {
        this.substitutableElements = elements;
    }
    
    public void setFinalized(boolean isFinalized){
      finalized = isFinalized;
    }
    
    public boolean isFinalized(){
      return finalized;
    }
    
    /**
     * Return a unique hashCode (as an int) for this instance.
     */
    public int hashCode() {
        int hash = 7;
        hash = (31 * hash) + ((null == getName()) ? 0 : getName().hashCode());
        hash = (31 * hash) + ((null == getUri()) ? 0 : getUri().hashCode());
        return hash;
    }

    /**
     * Indicate if a given SDOProperty instance is equal to this instance.
     * Equality is determined based on name, uri, and type.  In addition,
     * checking will be done to ensure that both properties are to be
     * serialized in the same manner, ie. both to XML element or both to 
     * XML attribute.
     * 
     * @param obj Object to compare to this SDOProperty instance
     * @return true if obj is equal to this SDOProperty instance, false if not 
     */
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        SDOProperty prop;
        try {
            prop = (SDOProperty) obj;
        } catch (ClassCastException ccx) {
            // not an SDOProperty
            return false;
        }
        // check type
        if (prop.getType() == null) {
            if (this.getType() != null) {
                return false;
            }
        } else if (this.getType() == null || !this.getType().equals(prop.getType())) {
            return false;
        }
        // check name and URI
        if (prop.getName() == null) {
            if (this.getName() != null) {
                return false;
            }
        } else if (this.getName() == null || !this.getName().equals(prop.getName())) {
            return false;
        }
        if (prop.getUri() == null) {
            if (this.getUri() != null) {
                return false;
            }
        } else if (this.getUri() == null || !this.getUri().equals(prop.getUri())) {
            return false;
        }
        // check attribute vs. element
        XSDHelper helper = aHelperContext.getXSDHelper(); 
        if (helper.isAttribute(this)) {
            if (helper.isElement(prop)) {
                return false;
            }
        } else if (helper.isAttribute(prop)) {
            return false;
        }
        return true;
    }
    private void updateType(){    	
        if (type == SDOConstants.SDO_BOOLEAN) {
            setType(SDOConstants.SDO_BOOLEANOBJECT);
        } else if (type == SDOConstants.SDO_BYTE) {
            setType(SDOConstants.SDO_BYTEOBJECT);
        } else if (type == SDOConstants.SDO_CHARACTER) {
            setType(SDOConstants.SDO_CHARACTEROBJECT);
        } else if (type == SDOConstants.SDO_DOUBLE) {
            setType(SDOConstants.SDO_DOUBLEOBJECT);
        } else if (type == SDOConstants.SDO_FLOAT) {
            setType(SDOConstants.SDO_FLOATOBJECT);
        } else if (type == SDOConstants.SDO_INT) {
            setType(SDOConstants.SDO_INTOBJECT);
        } else if (type == SDOConstants.SDO_LONG) {
            setType(SDOConstants.SDO_LONGOBJECT);
        } else if (type == SDOConstants.SDO_SHORT) {
            setType(SDOConstants.SDO_SHORTOBJECT);
        } 
    }   	   
}
