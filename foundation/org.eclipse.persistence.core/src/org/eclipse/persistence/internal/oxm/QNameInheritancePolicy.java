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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.oxm;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.xml.namespace.QName;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.descriptors.MultitenantPolicy;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import org.eclipse.persistence.internal.core.helper.CoreField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.record.XMLRecord;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: A Subclass of Inheritance Policy to be used with XML
 * Descriptors. If the class indicator field is an xsi:type, the value of that
 * field may be a qualified type name. For example xsi:type="myns:my-type-name".
 * Since any given XML document can use different prefixes for these namespaces,
 * we must be able to find the class based on QName instead of just the string
 * "myns:my-type-name".</p>
 * @author  mmacivor
 * @since   10.1.3
 */
public class QNameInheritancePolicy extends InheritancePolicy {
    //used for initialization. Prefixed type names will be changed to QNames.
    private NamespaceResolver namespaceResolver;

    private boolean usesXsiType = false;

    public QNameInheritancePolicy() {
        super();
    }

    public QNameInheritancePolicy(ClassDescriptor desc) {
        super(desc);
    }
    
    /**
     * Override to control order of uniqueTables, child tablenames should be first since 
     * getDefaultRootElement on an XMLDescriptor will return the first table.
     */
    protected void updateTables(){
        // Unique is required because the builder can add the same table many times.
        Vector<DatabaseTable> childTables = getDescriptor().getTables();
        Vector<DatabaseTable> parentTables = getParentDescriptor().getTables();
        Vector<DatabaseTable> uniqueTables = Helper.concatenateUniqueVectors(childTables, parentTables);
        getDescriptor().setTables(uniqueTables);
        
        
        // After filtering out any duplicate tables, set the default table
        // if one is not already set. This must be done now before any other
        // initialization occurs. In a joined strategy case, the default 
        // table will be at an index greater than 0. Which is where
        // setDefaultTable() assumes it is. Therefore, we need to send the 
        // actual default table instead.
        if (childTables.isEmpty()) {
            getDescriptor().setInternalDefaultTable();
        } else {
            getDescriptor().setInternalDefaultTable(uniqueTables.get(uniqueTables.indexOf(childTables.get(0))));
        }
    }
    
    /**
     * INTERNAL:
     * Allow the inheritance properties of the descriptor to be initialized.
     * The descriptor's parent must first be initialized.
     */
    @Override
    public void preInitialize(AbstractSession session) throws DescriptorException {
        // Override for OXM inheritence to avoid initializing unrequired fields
        // on the descriptor
        if (isChildDescriptor()) {
            updateTables();
            
            // Clone the multitenant policy and set on child descriptor.
            if (getParentDescriptor().hasMultitenantPolicy()) {
                MultitenantPolicy clonedMultitenantPolicy = (MultitenantPolicy) getParentDescriptor().getMultitenantPolicy().clone(getDescriptor());
                getDescriptor().setMultitenantPolicy(clonedMultitenantPolicy);
            }
            
            setClassIndicatorMapping(getParentDescriptor().getInheritancePolicy().getClassIndicatorMapping());
            setShouldUseClassNameAsIndicator(getParentDescriptor().getInheritancePolicy().shouldUseClassNameAsIndicator());

            // Initialize properties.
            getDescriptor().setPrimaryKeyFields(getParentDescriptor().getPrimaryKeyFields());
            getDescriptor().setAdditionalTablePrimaryKeyFields(Helper.concatenateMaps(getParentDescriptor().getAdditionalTablePrimaryKeyFields(), getDescriptor().getAdditionalTablePrimaryKeyFields()));

            setClassIndicatorField(getParentDescriptor().getInheritancePolicy().getClassIndicatorField());

            //if child has sequencing setting, do not bother to call the parent
            if (!getDescriptor().usesSequenceNumbers()) {
                getDescriptor().setSequenceNumberField(getParentDescriptor().getSequenceNumberField());
                getDescriptor().setSequenceNumberName(getParentDescriptor().getSequenceNumberName());
            }
        } else {
            // This must be done now before any other initialization occurs. 
            getDescriptor().setInternalDefaultTable();
        }

        initializeClassExtractor(session);

        if (!isChildDescriptor()) {
            // build abstract class indicator field.
            if ((getClassIndicatorField() == null) && (!hasClassExtractor())) {
                session.getIntegrityChecker().handleError(DescriptorException.classIndicatorFieldNotFound(getDescriptor(), getDescriptor()));
            }
            if (getClassIndicatorField() != null) {
                setClassIndicatorField(getDescriptor().buildField(getClassIndicatorField()));
                // Determine and set the class indicator classification.
                if (shouldUseClassNameAsIndicator()) {
                    getClassIndicatorField().setType(CoreClassConstants.STRING);
                } else if (!getClassIndicatorMapping().isEmpty()) {
                    Class type = null;
                    Iterator fieldValuesEnum = getClassIndicatorMapping().values().iterator();
                    while (fieldValuesEnum.hasNext() && (type == null)) {
                        Object value = fieldValuesEnum.next();
                        if (value.getClass() != getClass().getClass()) {
                            type = value.getClass();
                        }
                    }
                    getClassIndicatorField().setType(type);
                }
                getDescriptor().getFields().addElement(getClassIndicatorField());
            }
        }
    }    

    /**
     * INTERNAL:
     * Initialized the inheritance properties of the descriptor once the mappings are initialized.
     * This is done before formal postInitialize during the end of mapping initialize.
     */
    public void initialize(AbstractSession session) {
        super.initialize(session);

        // If we have a namespace resolver, check any of the class-indicator values
        // for prefixed type names and resolve the namespaces.
        if (!this.shouldUseClassNameAsIndicator()){ 
            // Must first clone the map to avoid concurrent modification.
            Iterator<Map.Entry> entries = new HashMap(getClassIndicatorMapping()).entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry entry = entries.next();
                Object key = entry.getKey();
                XPathFragment frag = ((XMLField) getClassIndicatorField()).getXPathFragment();
                if (frag.getLocalName().equals(Constants.SCHEMA_TYPE_ATTRIBUTE) && frag.getNamespaceURI() != null && frag.getNamespaceURI().equals(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI)) {
                    usesXsiType = true;
                }
                if (key instanceof String) {
                    XPathQName qname;

                    String indicatorValue = (String) key;
                    if (!usesXsiType || namespaceResolver == null) {
                        qname = new XPathQName(indicatorValue, true);
                    } else {
                        int index = indicatorValue.indexOf(Constants.COLON);
                        if (index != -1 && namespaceResolver != null) {
                            String prefix = indicatorValue.substring(0, index);
                            String localPart = indicatorValue.substring(index + 1);
                            String uri = namespaceResolver.resolveNamespacePrefix(prefix);
                            qname = new XPathQName(uri, localPart, true);
                        } else {
                            qname = new XPathQName(namespaceResolver.getDefaultNamespaceURI(), indicatorValue, true);
                        }
                    }
                    getClassIndicatorMapping().put(qname, entry.getValue());
                } else if (key instanceof QName) {
                    XPathQName xpathQName = new XPathQName((QName) key, true);
                    getClassIndicatorMapping().put(xpathQName, entry.getValue());
                }
            }
        }

        //bug 6012173 - changed to initialize namespare uri on indicator field
        //need to be able to compare uri and local name during marshal to see if field is xsi type field
        if(getClassIndicatorField() != null){
          XMLField classIndicatorXMLField;
          try {
              classIndicatorXMLField = (XMLField)getClassIndicatorField();
          } catch (ClassCastException ex) {
              classIndicatorXMLField = new XMLField(getClassIndicatorField().getName());            
              setClassIndicatorField(classIndicatorXMLField);
          }
          XPathFragment frag = classIndicatorXMLField.getLastXPathFragment();                        
          if ((frag != null) && frag.hasNamespace() && frag.getPrefix() !=null && (namespaceResolver != null)) {
              String uri = namespaceResolver.resolveNamespacePrefix(frag.getPrefix());
              classIndicatorXMLField.getLastXPathFragment().setNamespaceURI(uri);
          }          
        }
    }

    /**
     * INTERNAL:
     * This method is invoked only for the abstract descriptors.
     */
    public Class classFromRow(AbstractRecord rowFromDatabase, AbstractSession session) throws DescriptorException {
        ((XMLRecord) rowFromDatabase).setSession(session);
        if (hasClassExtractor() || shouldUseClassNameAsIndicator()) {
            return super.classFromRow(rowFromDatabase, session);
        }
        Object indicator = rowFromDatabase.get(getClassIndicatorField());        
        
        if (indicator == AbstractRecord.noEntry) {
            return null;
        }

        if (indicator == null) {
            return null;
        }
        
        Class concreteClass;
        if (indicator instanceof String) {
            boolean namespaceAware = ((XMLRecord) rowFromDatabase).isNamespaceAware();
            String indicatorValue = (String)indicator;
            int index = indicatorValue.indexOf(((XMLRecord)rowFromDatabase).getNamespaceSeparator());
            if (index == -1) {
                if (namespaceAware && usesXsiType) {
                    String uri = ((XMLRecord)rowFromDatabase).resolveNamespacePrefix(null);
                    if (uri == null && ((XMLRecord)rowFromDatabase).getNamespaceResolver() != null) {
                    	uri = ((XMLRecord)rowFromDatabase).getNamespaceResolver().getDefaultNamespaceURI();
                    } 
                    XPathQName qname = new XPathQName(uri, indicatorValue, namespaceAware);                        
                    concreteClass = (Class)this.classIndicatorMapping.get(qname);
                } else {
                    XPathQName qname = new XPathQName(indicatorValue, namespaceAware);
                    concreteClass = (Class)this.classIndicatorMapping.get(qname);
                }
            } else {
                String prefix = indicatorValue.substring(0, index);
                String localPart = indicatorValue.substring(index + 1);
                String uri = ((XMLRecord)rowFromDatabase).resolveNamespacePrefix(prefix);
                if (uri != null) {
                    XPathQName qname = new XPathQName(uri, localPart, namespaceAware);
                    concreteClass = (Class)this.classIndicatorMapping.get(qname);
                } else {
                    concreteClass = (Class)this.classIndicatorMapping.get(indicatorValue);
                }
            }
        } else {
            concreteClass = (Class)this.classIndicatorMapping.get(indicator);
        }
        if (concreteClass == null) {
            throw DescriptorException.missingClassForIndicatorFieldValue(indicator, getDescriptor());
        }
        return concreteClass;
    }

    public void setNamespaceResolver(NamespaceResolver resolver) {
        this.namespaceResolver = resolver;
    }

    /**
     * PUBLIC:
     * To set the class indicator field name.
     * This is the name of the field in the table that stores what type of object this is.
     */
    public void setClassIndicatorFieldName(String fieldName) {
        if (fieldName == null) {
            setClassIndicatorField(null);
        } else {
            setClassIndicatorField(new XMLField(fieldName));
        }
    }
    
    /**
     * INTERNAL:
     * Add abstract class indicator information to the database row.  This is
     * required when building a row for an insert or an update of a concrete child
     * descriptor.
     */
    public void addClassIndicatorFieldToRow(AbstractRecord databaseRow) {
        if (hasClassExtractor()) {
            return;
        }

        CoreField field = getClassIndicatorField();
        Object value = getClassIndicatorValue();
        
        if(usesXsiType){        
            boolean namespaceAware = ((XMLRecord)databaseRow).isNamespaceAware() || ((XMLRecord)databaseRow).hasCustomNamespaceMapper();
            if(value instanceof String){
            	if(namespaceAware){
            		if(((XMLRecord)databaseRow).getNamespaceSeparator() != Constants.COLON){
                 	   value= ((String)value).replace(Constants.COLON, ((XMLRecord)databaseRow).getNamespaceSeparator());
                 	}
            	}else{
            		 int colonIndex = ((String)value).indexOf(Constants.COLON);
                     if(colonIndex > -1){
                         value = ((String)value).substring(colonIndex + 1);
             	    }		
            	}
            }        
        }
        
        databaseRow.put(field, value);
    }
}
