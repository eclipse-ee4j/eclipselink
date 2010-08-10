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
 *     dclarke, mnorman - Dynamic Persistence
 *       http://wiki.eclipse.org/EclipseLink/Development/Dynamic 
 *       (https://bugs.eclipse.org/bugs/show_bug.cgi?id=200045)
 *
 ******************************************************************************/
package org.eclipse.persistence.internal.xr;

//javase imports
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//EclipseLink imports
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.internal.dynamic.DynamicPropertiesManager;

/**
 * Local cache of property names
 * 
 * @author mnorman
 */
public class XRDynamicPropertiesManager extends DynamicPropertiesManager {

    // local static cache of property names
    protected Set<String> propertyNames = null;

    public XRDynamicPropertiesManager() {
        super();
        setType(new XRDynamicType()); // dummy impl of DynamicType
        setInitializatonPolicy(new XRDynamicPropertiesInitializatonPolicy());
    }
    
    public void setPropertyNames(Set<String> propertyNames) {
        // One-time initialization: only if this.propertiesNameSet is null
        if (this.propertyNames == null) {
            this.propertyNames = propertyNames;
        }
    }

    public boolean contains(String propertyName) {
        return propertyNames.contains(propertyName);
    }

    public List<String> getPropertyNames() {
        List<String> tmp = new ArrayList<String>();
        if (propertyNames != null) {
            for (String propertyName : propertyNames) {
                tmp.add(propertyName);
            }
        }
        return tmp;
    }

    @Override
    public void checkSet(String propertyName, Object value) {
        // no-op
    }

    @Override
    public void postConstruct(DynamicEntity entity) {
        super.postConstruct(entity);
        // TODO - build custom Fetch group artifacts for sparse merge
    }

}
