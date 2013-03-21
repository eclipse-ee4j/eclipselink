/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - rbarkhouse - 24 February 2012 - 2.3.3 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.jaxb;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.xml.bind.ValidationEventHandler;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.jaxb.IDResolver;
import org.xml.sax.SAXException;

/**
 * INTERNAL:
 * <p>
 * This class provides an implementation of IDResolver that wraps
 * an IDResolver from the Sun JAXB Implementation.
 * </p>
 */
public class IDResolverWrapper extends IDResolver {

    private final static String BIND_METHOD_NAME = "bind";
    private final static String END_DOCUMENT_METHOD_NAME = "endDocument";
    private final static String RESOLVE_METHOD_NAME = "resolve";
    private final static String START_DOCUMENT_METHOD_NAME = "startDocument";
    
    private final static Class[] BIND_PARAMS = new Class[] { CoreClassConstants.STRING, CoreClassConstants.OBJECT };
    private final static Class[] RESOLVE_PARAMS = new Class[] { CoreClassConstants.STRING, CoreClassConstants.CLASS };
    private final static Class[] START_DOCUMENT_PARAMS = new Class[] { ValidationEventHandler.class };

    private Object resolver;
    private Method bindMethod, endDocumentMethod, resolveMethod, startDocumentMethod;
    
    public IDResolverWrapper(Object sunResolver) {
        this.resolver = sunResolver;
        Class resolverClass = sunResolver.getClass();

        try {
            this.bindMethod = PrivilegedAccessHelper.getMethod(resolverClass, BIND_METHOD_NAME, BIND_PARAMS, false);
        } catch (Exception ex) {
            throw XMLMarshalException.errorProcessingIDResolver(BIND_METHOD_NAME, sunResolver, ex);
        }
        try {
            this.endDocumentMethod = PrivilegedAccessHelper.getMethod(resolverClass, END_DOCUMENT_METHOD_NAME, new Class[] {}, false);
        } catch (Exception ex) {
            throw XMLMarshalException.errorProcessingIDResolver(END_DOCUMENT_METHOD_NAME, sunResolver, ex);
        }
        try {
            this.resolveMethod = PrivilegedAccessHelper.getMethod(resolverClass, RESOLVE_METHOD_NAME, RESOLVE_PARAMS, false);
        } catch (Exception ex) {
            throw XMLMarshalException.errorProcessingIDResolver(RESOLVE_METHOD_NAME, sunResolver, ex);
        }
        try {
            this.startDocumentMethod = PrivilegedAccessHelper.getMethod(resolverClass, START_DOCUMENT_METHOD_NAME, START_DOCUMENT_PARAMS, false);
        } catch (Exception ex) {
            throw XMLMarshalException.errorProcessingIDResolver(START_DOCUMENT_METHOD_NAME, sunResolver, ex);
        }
    }

    @Override
    public void bind(Object id, Object obj) throws SAXException {
        try {
            Object[] params = new Object[] { id.toString(), obj };
            PrivilegedAccessHelper.invokeMethod(this.bindMethod, this.resolver, params);
        } catch (Exception ex) {
            throw XMLMarshalException.errorInvokingIDResolver(BIND_METHOD_NAME, this.resolver, ex);
        }
    }
    
    @Override
    public void endDocument() throws SAXException {
        try {
            PrivilegedAccessHelper.invokeMethod(this.endDocumentMethod, this.resolver, new Object[] {});
        } catch (Exception ex) {
            throw XMLMarshalException.errorInvokingIDResolver(END_DOCUMENT_METHOD_NAME, this.resolver, ex);
        }
    }
    
    @Override
    public void startDocument(ValidationEventHandler eventHandler) throws SAXException {
        try {
            Object[] params = new Object[] { eventHandler };
            PrivilegedAccessHelper.invokeMethod(this.startDocumentMethod, this.resolver, params);
        } catch (Exception ex) {
            throw XMLMarshalException.errorInvokingIDResolver(START_DOCUMENT_METHOD_NAME, this.resolver, ex);
        }
    }
    
    @Override
    public Callable<?> resolve(Object id, Class targetType) throws SAXException {
        try {
            Object[] params = new Object[] { id.toString(), targetType };
            return (Callable<?>) PrivilegedAccessHelper.invokeMethod(this.resolveMethod, this.resolver, params);
        } catch (Exception ex) {
            throw XMLMarshalException.errorInvokingIDResolver(RESOLVE_METHOD_NAME, this.resolver, ex);
        }
    }

    @Override
    public Callable<?> resolve(Map<String, Object> id, Class type) throws SAXException {
        // If the user is still using a Sun IDResolver, then they must only have one XML ID,
        // as only EclipseLink supports multiple IDs through XmlKey.  So if this method is called,
        // throw an exception informing the user.
        throw XMLMarshalException.wrappedIDResolverWithMultiID(id.toString(), this.resolver);
    }

    @Override
    public void bind(Map<String, Object> id, Object obj) throws SAXException {
        // If the user is still using a Sun IDResolver, then they must only have one XML ID,
        // as only EclipseLink supports multiple IDs through XmlKey.  So if this method is called,
        // throw an exception informing the user.
        throw XMLMarshalException.wrappedIDResolverWithMultiID(id.toString(), this.resolver);
    }

    public Object getResolver() {
        return this.resolver;
    }

}