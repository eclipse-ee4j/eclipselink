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

import java.lang.reflect.Method;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.XMLDescriptor;

/**
 * <p><b>Purpose:</b>An extnesion of XMLDescriptor that's used for sequened
 * objects.
 * <p><b>Responsibilities:</b>
 */
public class XMLSequencedDescriptor extends XMLDescriptor {
    private String getSettingsMethodName;
    private Method getSettingsMethod;

    public void initialize(AbstractSession session) throws DescriptorException {
        super.initialize(session);
        if(shouldPreserveDocument()) {
            this.objectBuilder = new XMLSequencedObjectBuilder(this);
        }
        if(getGetSettingsMethodName() != null) {
            try {
                this.getSettingsMethod = PrivilegedAccessHelper.getDeclaredMethod(this.getJavaClass(), this.getGetSettingsMethodName(), new Class[0]);
            } catch(Exception ex) {
            
            }
        }
    }    
    /**
     * INTERNAL:
     * Get the method that will be used to obtain an ordered list of TopLinkSetting objects
     * at runtime. Only used with Sequenced objects
     * @return The name of the method to be invoked.
     */
    public String getGetSettingsMethodName() {
        return this.getSettingsMethodName;
    }
    
    /**
     * INTERNAL:
     * Set the name of the method to be invoked to obtain an ordered list of TopLinkSetting
     * objects at runtime. Only used with Sequenced objects.
     * @param methodName: The name of the method.
     */
    public void setGetSettingsMethodName(String methodName) {
        this.getSettingsMethodName = methodName;
    }

    /**
     * INTERNAL:
     * Return the actual method to be invoked to obtain an ordered list of TopLinkSetting objects
     * Only used with Sequenced Objects. Is set during initialize.
     * @return The method to be invoked.
     */
    public Method getGetSettingsMethod() {
        return this.getSettingsMethod;
    }
    
}
