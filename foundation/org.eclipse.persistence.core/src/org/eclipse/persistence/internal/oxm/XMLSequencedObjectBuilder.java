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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.record.XMLRecord;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>
 */
public class XMLSequencedObjectBuilder extends XMLObjectBuilder {
    public XMLSequencedObjectBuilder(ClassDescriptor descriptor) {
        super(descriptor);
    }
    
    public void writeOutMappings(XMLRecord record, Object object, AbstractSession session) {
        // PERF: Avoid synchronized enumerator as is concurrency bottleneck.
        Collection settings = getSettingsFromObject(object);
        if(settings != null) {
            Iterator settingsIter = settings.iterator();
            while(settingsIter.hasNext()) {
                XMLSetting next = (XMLSetting)settingsIter.next();
                next.getMapping().writeSingleValue(next.getValue(), object, record, session);
            }
        }
    }
    
    public Collection getSettingsFromObject(Object obj) {
        Method getSettingsMethod = ((XMLSequencedDescriptor)getDescriptor()).getGetSettingsMethod();
        try {
            Collection settings = (Collection)PrivilegedAccessHelper.invokeMethod(getSettingsMethod, obj, new Object[0]);
            return settings;
        } catch(IllegalAccessException ex) {
            
        } catch(InvocationTargetException ex) {
            
        }
        return new java.util.ArrayList();
    }

    
}
