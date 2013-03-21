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
 *     Matt MacIvor - initial implementation (2.3.3)
 ******************************************************************************/  
package org.eclipse.persistence.internal.oxm.record.namespaces;

import java.lang.reflect.Method;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.oxm.NamespacePrefixMapper;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>This class provides an implementation of NamespacePrefixMapper that wraps
 * an implementation of the equivalent NamespacePrefixMapper from the JAXB ReferenceImplementation
 * This allows for backwards compatibility with the JAXB RI. 
 */
public class NamespacePrefixMapperWrapper extends NamespacePrefixMapper {
    private static String GET_PREF_PREFIX_METHOD_NAME = "getPreferredPrefix";
    private static String GET_PRE_DECL_NAMESPACE_URIS_METHOD_NAME = "getPreDeclaredNamespaceUris";
    private static String GET_PRE_DECL_NAMESPACE_URIS2_METHOD_NAME = "getPreDeclaredNamespaceUris2";
    private static String GET_CONTEXTUAL_NAMESPACE_DECL_METHOD_NAME = "getContextualNamespaceDecls";
    
    private static final Class[] EMPTY_CLASS_ARRAY = new Class[]{};
    private static final Class[] PREF_PREFIX_PARAM_TYPES = new Class[] {CoreClassConstants.STRING, CoreClassConstants.STRING, CoreClassConstants.PBOOLEAN};
    
    private Object prefixMapper;
    private Method getPreferredPrefixMethod;
    private Method getPredeclaredNamespaceUrisMethod;
    private Method getPredeclaredNamespaceUris2Method;
    private Method getContextualNamespaceDeclsMethod;
    
    public NamespacePrefixMapperWrapper(Object prefixMapper) {
        this.prefixMapper = prefixMapper;
        Class prefixMapperClass = prefixMapper.getClass();
        try {
            this.getPreferredPrefixMethod = PrivilegedAccessHelper.getMethod(prefixMapperClass, GET_PREF_PREFIX_METHOD_NAME, PREF_PREFIX_PARAM_TYPES, false);
        } catch(Exception ex) {
        	throw XMLMarshalException.errorProcessingPrefixMapper(GET_PREF_PREFIX_METHOD_NAME, prefixMapper);
        }
        try {
            this.getPredeclaredNamespaceUrisMethod = PrivilegedAccessHelper.getMethod(prefixMapperClass, GET_PRE_DECL_NAMESPACE_URIS_METHOD_NAME, EMPTY_CLASS_ARRAY, false);
        } catch(Exception ex) {
        	throw XMLMarshalException.errorProcessingPrefixMapper(GET_PRE_DECL_NAMESPACE_URIS_METHOD_NAME, prefixMapper);
        }
        try {
            this.getPredeclaredNamespaceUris2Method = PrivilegedAccessHelper.getMethod(prefixMapperClass, GET_PRE_DECL_NAMESPACE_URIS2_METHOD_NAME, EMPTY_CLASS_ARRAY, false);
        } catch(Exception ex) {
        	throw XMLMarshalException.errorProcessingPrefixMapper(GET_PRE_DECL_NAMESPACE_URIS2_METHOD_NAME, prefixMapper);
        }
        try {
            this.getContextualNamespaceDeclsMethod = PrivilegedAccessHelper.getMethod(prefixMapperClass, GET_CONTEXTUAL_NAMESPACE_DECL_METHOD_NAME, EMPTY_CLASS_ARRAY, false);
        } catch(Exception ex) {
        	throw XMLMarshalException.errorProcessingPrefixMapper(GET_CONTEXTUAL_NAMESPACE_DECL_METHOD_NAME, prefixMapper);
        }
    }
    
    
    @Override
    public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
        try {
            return (String)PrivilegedAccessHelper.invokeMethod(getPreferredPrefixMethod, prefixMapper, new Object[]{namespaceUri, suggestion, requirePrefix});
        } catch(Exception ex) {
            throw XMLMarshalException.errorInvokingPrefixMapperMethod(GET_PREF_PREFIX_METHOD_NAME, prefixMapper);
        }
    }
    
    @Override
    public String[] getContextualNamespaceDecls() {
        try {
            return (String[])PrivilegedAccessHelper.invokeMethod(this.getContextualNamespaceDeclsMethod, prefixMapper, new Object[]{});
        } catch(Exception ex) {
            throw XMLMarshalException.errorInvokingPrefixMapperMethod(GET_CONTEXTUAL_NAMESPACE_DECL_METHOD_NAME, prefixMapper);
        }
    }
    
    @Override
    public String[] getPreDeclaredNamespaceUris() {
        try {
            return (String[])PrivilegedAccessHelper.invokeMethod(getPredeclaredNamespaceUrisMethod, prefixMapper, new Object[]{});
        } catch(Exception ex) {
            throw XMLMarshalException.errorInvokingPrefixMapperMethod(GET_PRE_DECL_NAMESPACE_URIS_METHOD_NAME, prefixMapper);
        }
    }
    
    @Override
    public String[] getPreDeclaredNamespaceUris2() {
        try {
            return (String[])PrivilegedAccessHelper.invokeMethod(getPredeclaredNamespaceUris2Method, prefixMapper, new Object[]{});
        } catch(Exception ex) {
            throw XMLMarshalException.errorInvokingPrefixMapperMethod(GET_PRE_DECL_NAMESPACE_URIS2_METHOD_NAME, prefixMapper);
        }
    }

    public Object getPrefixMapper() {
        return this.prefixMapper;
    }

}
