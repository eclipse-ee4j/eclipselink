/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced;

import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * Session and descriptor customizer.
 */
public class Customizer implements SessionCustomizer, DescriptorCustomizer {
    static HashMap<String, Integer> sessionCalls = new HashMap<>();
    static HashMap<String, Integer> descriptorCalls = new HashMap<>();

    @Override
    public void customize(Session session) {
        String sessionName = session.getName();
        Integer numberOfCalls = sessionCalls.get(sessionName);
        int num = 0;
        if(numberOfCalls != null) {
            num = numberOfCalls;
        }
        sessionCalls.put(sessionName, num + 1);

        session.getEventManager().addListener(new SessionEventAdapter() {
            @Override
            public void postLogin(SessionEvent event) {
                if (event.getSession().getPlatform().isPostgreSQL()) {
                    event.getSession().setQueryTimeoutDefault(0);
                }
            }
        });
    }

    @Override
    public void customize(ClassDescriptor descriptor) {
        String javaClassName = descriptor.getJavaClass().getName();
        Integer numberOfCalls = descriptorCalls.get(javaClassName);
        int num = 0;
        if(numberOfCalls != null) {
            num = numberOfCalls;
        }
        descriptorCalls.put(javaClassName, num + 1);
    }

    public static Map<String, Integer> getSessionCalls() {
        return sessionCalls;
    }

    public static Map<String, Integer> getDescriptorCalls() {
        return descriptorCalls;
    }

    public static int getNumberOfCallsForSession(String sessionName) {
        Integer numberOfCalls = sessionCalls.get(sessionName);
        if(numberOfCalls == null) {
            return 0;
        } else {
            return numberOfCalls;
        }
    }

    public static int getNumberOfCallsForClass(String javaClassName) {
        Integer numberOfCalls = descriptorCalls.get(javaClassName);
        if(numberOfCalls == null) {
            return 0;
        } else {
            return numberOfCalls;
        }
    }
}
