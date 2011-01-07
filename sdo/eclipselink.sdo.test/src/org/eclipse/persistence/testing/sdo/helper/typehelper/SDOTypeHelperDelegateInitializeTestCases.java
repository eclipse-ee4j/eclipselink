/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith July 14, 2009
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.helper.typehelper;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOHelperContext;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.impl.HelperProvider;

public class SDOTypeHelperDelegateInitializeTestCases extends junit.framework.TestCase  {

	private TypeHelper typeHelper;
	private boolean customContext;
	
    public SDOTypeHelperDelegateInitializeTestCases(String name) {
        super(name);
        customContext = Boolean.getBoolean("customContext");
    }
    
    public void setUp(){
        HelperContext aHelperContext;
    	if (customContext) {
            // default to instance of a HelperContext
            aHelperContext = new SDOHelperContext();
        } else {
            // default to static context (Global)
            aHelperContext = HelperProvider.getDefaultContext();
        }
    	typeHelper = aHelperContext.getTypeHelper();
    }
    
    public void testHelperContextValid(){
    	Collection<SDOType> types = ((SDOTypeHelper)typeHelper).getTypesHashMap().values();
    	validateTypes(types);
    	types = ((SDOTypeHelper)typeHelper).getWrappersHashMap().values();
    	validateTypes(types);
    	
    }
    
    private void validateTypes(Collection<SDOType> types){
    	Iterator<SDOType> iter = types.iterator();
    	while(iter.hasNext()){
    		validateType(iter.next());
    	}
    }
    
    private void validateType(SDOType type){
    	assertNotNull(type);
    	List<SDOProperty> properties = type.getProperties();
    	for(SDOProperty nextProp: properties){
    		assertNotNull(nextProp);
    		assertNotNull(nextProp.getType());
    	}
    	
    }

}
