/*
 * Copyright (c) 1998, 2020 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jaxb;

import java.util.Map;

import jakarta.xml.bind.Marshaller;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.oxm.Root;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.jaxb.compiler.MarshalCallback;
import org.eclipse.persistence.oxm.XMLMarshalListener;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>Provide a wrapper for a JAXB 2.0 Marshal Listener that implements
 * XMLMarshalListener
 * <p><b>Responsibilities:</b><ul>
 * <li>Implement the XMLMarshalListener API</li>
 * <li>Delegate event callbacks to the wrapped Listener instance</li>
 * <li>Perform JAXB 2.0 Class-Based marshal event callbacks</li>
 * </ul>
 *
 * @see jakarta.xml.bind.Marshaller.Listener
 * @see org.eclipse.persistence.oxm.XMLMarshalListener
 */

public class JAXBMarshalListener implements XMLMarshalListener {
    private Marshaller.Listener listener;
    private Map classBasedMarshalEvents;
    private jakarta.xml.bind.Marshaller marshaller;
    private JAXBContext jaxbContext;

    public JAXBMarshalListener(JAXBContext context, jakarta.xml.bind.Marshaller marshaller) {
        jaxbContext = context;
        this.marshaller = marshaller;
    }

    public void setListener(Marshaller.Listener jaxbListener) {
        this.listener = jaxbListener;
    }

    public Marshaller.Listener getListener() {
        return listener;
    }

    @Override
    public void beforeMarshal(final Object obj) {
        if(classBasedMarshalEvents != null) {
            MarshalCallback callback = (MarshalCallback)classBasedMarshalEvents.get(obj.getClass().getName());
            if(callback != null && callback.getBeforeMarshalCallback() != null) {
                PrivilegedAccessHelper.callDoPrivilegedWithException(
                        () -> PrivilegedAccessHelper.invokeMethod(callback.getBeforeMarshalCallback(), obj, new Object[]{marshaller}),
                        (ex) -> XMLMarshalException.marshalException(ex)
                );
            }
        }
        if (listener != null) {
            listener.beforeMarshal(
                    obj instanceof Root
                            ? jaxbContext.createJAXBElementFromXMLRoot((Root) obj, ((Root) obj).getDeclaredType()) : obj
            );
        }
    }
    @Override
    public void afterMarshal(final Object obj) {
        if(classBasedMarshalEvents != null) {
            MarshalCallback callback = (MarshalCallback)classBasedMarshalEvents.get(obj.getClass().getName());
            if (callback != null && callback.getAfterMarshalCallback() != null) {
                PrivilegedAccessHelper.callDoPrivilegedWithException(
                        () -> PrivilegedAccessHelper.invokeMethod(callback.getAfterMarshalCallback(), obj, new Object[]{marshaller}),
                        (ex) -> XMLMarshalException.marshalException(ex)
                );
            }
        }
        if (listener != null) {
            listener.afterMarshal(
                    obj instanceof Root
                            ? jaxbContext.createJAXBElementFromXMLRoot((Root) obj, ((Root) obj).getDeclaredType()) : obj
            );
        }
    }

    public void setClassBasedMarshalEvents(Map events) {
        this.classBasedMarshalEvents = events;
    }
}
