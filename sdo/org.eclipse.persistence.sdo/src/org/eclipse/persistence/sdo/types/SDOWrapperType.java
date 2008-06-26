/*******************************************************************************
* Copyright (c) 1998, 2008 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms
* of the Eclipse Public License v1.0 and Eclipse Distribution License v1.0
* which accompanies this distribution.
* 
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
* rbarkhouse - May 26 2008 - 1.0M8 - Initial implementation
******************************************************************************/

package org.eclipse.persistence.sdo.types;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOMethodAttributeAccessor;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;
import org.eclipse.persistence.sdo.helper.extension.SDOUtil;

import commonj.sdo.Type;

/**
 * Wrapper for XML datatypes. 
 * @author rbarkhou
 */
public class SDOWrapperType extends SDOType implements Type {

	private static final List EMPTY_LIST = new ArrayList(0);

	private String typeName;

	public SDOWrapperType(Type aPropertyType, String aTypeName, SDOTypeHelper aSDOTypeHelper) {
		this(aPropertyType, aTypeName, aSDOTypeHelper, null);
	}	
	
	public SDOWrapperType(Type aPropertyType, String aTypeName, SDOTypeHelper aSDOTypeHelper, QName aSchemaType) {
		super(aSDOTypeHelper);
		
		xmlDescriptor.setNamespaceResolver(null);
		
		typeName = aTypeName;
		
        SDOProperty valueProperty = new SDOProperty(aHelperContext);
        valueProperty.setName("value");
        valueProperty.setType(aPropertyType);
        valueProperty.setXsdType(aSchemaType);
        addDeclaredProperty(valueProperty);
        
        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("value");
        mapping.setAttributeClassification(aPropertyType.getInstanceClass());
        mapping.setXPath("text()");
        ((XMLField) mapping.getField()).setSchemaType(aSchemaType);

        SDOMethodAttributeAccessor accessor = null;
        accessor = new SDOMethodAttributeAccessor(valueProperty);
        mapping.setAttributeAccessor(accessor);        
        
        valueProperty.setXmlMapping(mapping);

        xmlDescriptor.addMapping(mapping);
        
        xmlDescriptor.setIsWrapper(true);

        String mangledTypeName = SDOUtil.className(aTypeName, true);
        setInstanceClassName("org.eclipse.persistence.sdo." + mangledTypeName + "Wrapper");
        setImplClassName("org.eclipse.persistence.sdo." + mangledTypeName + "WrapperImpl");

        getInstanceClass();
        getImplClass();
	}

    public List getAliasNames() {
        return EMPTY_LIST;
    }

    public List getBaseTypes() {
        return EMPTY_LIST;
    }

    public String getName() {
        return typeName;
    }

    public String getURI() {
        return SDOConstants.ORACLE_SDO_URL;
    }

    public boolean isAbstract() {
        return false;
    }

    public boolean isDataType() {
        return false;
    }

    public boolean isOpen() {
        return false;
    }

    public boolean isSequenced() {
        return false;
    }
    
    public boolean isFinalized() {
    	return true;
    }
    
    public String toString() {
    	StringBuffer str = new StringBuffer();
    	str.append(getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()));
    	str.append("{uri=");
    	str.append(getURI());
    	str.append(" name=");
    	str.append(getName());
    	str.append("}");
    	
    	return str.toString();
    }
}