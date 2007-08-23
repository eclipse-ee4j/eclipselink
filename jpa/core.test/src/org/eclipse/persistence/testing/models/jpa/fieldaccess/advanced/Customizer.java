/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.internal.sessions.factories.DescriptorCustomizer;
import org.eclipse.persistence.internal.sessions.factories.SessionCustomizer;

/**
 * Session and descriptor customizer.
 */
public class Customizer implements SessionCustomizer, DescriptorCustomizer {
    static HashMap sessionCalls = new HashMap();
    static HashMap descriptorCalls = new HashMap();

    public void customize(Session session) {
        String sessionName = session.getName();
        Integer numberOfCalls = (Integer)sessionCalls.get(sessionName);
        int num = 0;
        if(numberOfCalls != null) {
            num = numberOfCalls.intValue();
        }
        sessionCalls.put(sessionName, new Integer(num + 1));
    }
    
    public void customize(ClassDescriptor descriptor) {
        String javaClassName = descriptor.getJavaClass().getName();
        Integer numberOfCalls = (Integer)descriptorCalls.get(javaClassName);
        int num = 0;
        if(numberOfCalls != null) {
            num = numberOfCalls.intValue();
        }
        descriptorCalls.put(javaClassName, new Integer(num + 1));
    }
    
    public static Map getSessionCalls() {
        return sessionCalls;
    }

    public static Map getDescriptorCalls() {
        return descriptorCalls;
    }
    
    public static int getNumberOfCallsForSession(String sessionName) {
        Integer numberOfCalls = (Integer)sessionCalls.get(sessionName);
        if(numberOfCalls == null) {
            return 0;
        } else {
            return numberOfCalls.intValue();
        }
    }

    public static int getNumberOfCallsForClass(String javaClassName) {
        Integer numberOfCalls = (Integer)descriptorCalls.get(javaClassName);
        if(numberOfCalls == null) {
            return 0;
        } else {
            return numberOfCalls.intValue();
        }
    }
}
