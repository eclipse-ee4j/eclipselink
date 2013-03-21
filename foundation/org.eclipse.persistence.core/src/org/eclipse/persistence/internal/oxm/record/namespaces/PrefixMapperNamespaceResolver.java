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

import org.eclipse.persistence.internal.oxm.Namespace;
import org.eclipse.persistence.internal.oxm.NamespacePrefixMapper;
import org.eclipse.persistence.oxm.NamespaceResolver;

/**
 * INTERNAL:
 * <p><b>Purpose:</b> Provides an implementation of NamespaceResolver that wraps a
 * NamespacePrefixMapper. This NamespaceResolver initializes itself from the prefixmapper
 * and the descriptor's namespace resolver. It's set on a marshal record, when the XMLMarshaller
 * is using a custom namespace mapper. 
 *
 */
public class PrefixMapperNamespaceResolver extends NamespaceResolver {

    private NamespacePrefixMapper prefixMapper;
    private org.eclipse.persistence.internal.oxm.NamespaceResolver contextualNamespaces;
    
    public PrefixMapperNamespaceResolver(NamespacePrefixMapper mapper, org.eclipse.persistence.internal.oxm.NamespaceResolver nestedResolver) {
        prefixMapper = mapper;
        String[] declarations = mapper.getContextualNamespaceDecls();
        
        if(declarations != null && declarations.length > 0) {
        	contextualNamespaces = new NamespaceResolver();
            for(int i = 0; i < declarations.length - 1; i += 2) {
                String prefix = declarations[i];
                String uri = declarations[i + 1];
                contextualNamespaces.put(prefix, uri);
            }
        }
        String defaultUri= null;
        if(nestedResolver != null){
	        for(Object next:nestedResolver.getNamespaces()) {
	            Namespace ns = (Namespace)next;
	            String uri = ns.getNamespaceURI();
	            String existingPrefix = null;
	            if(contextualNamespaces != null) {
	                existingPrefix = contextualNamespaces.resolveNamespaceURI(uri);
	            }
	            //if there's already a prefix for this uri, don't bother adding it
	            if(existingPrefix == null) {
	                String originalPrefix = ns.getPrefix();
	            
	                //ask prefixMapper for a new prefix for this uri
	                String prefix = prefixMapper.getPreferredPrefix(uri, originalPrefix, true);
	            
	                if(prefix != null) {
	                    this.put(prefix, uri);
	                } else {
	                    this.put(originalPrefix, uri);
	                }
	            }
	        }
	        defaultUri = nestedResolver.getDefaultNamespaceURI();
        }
        
        if(defaultUri != null) {
            String prefix = prefixMapper.getPreferredPrefix(defaultUri, "", false);
            if("".equals(prefix) || prefix == null) {
                //Use as default?
                this.setDefaultNamespaceURI(defaultUri);
            } else {
                this.put(prefix, defaultUri);
            }
        } 
        
        String[] uris = mapper.getPreDeclaredNamespaceUris();
        
        if(uris != null && uris.length > 0) {
            for(int i = 0; i < uris.length; i++) {
                String uri = uris[i];
                
                String prefix = prefixMapper.getPreferredPrefix(uri, null, true);
                if(prefix != null) {
                    this.put(prefix, uri);
                } else {
                    this.put(this.generatePrefix(), uri);
                }
            }
        }
        
        declarations = prefixMapper.getPreDeclaredNamespaceUris2();
        if(declarations != null && declarations.length > 0) {
            for(int i = 0; i < declarations.length - 1; i += 2) {
                String prefix = declarations[i];
                String uri = declarations[i + 1];
                this.put(prefix, uri);
            }
        }        
    }
    
    @Override
    public String resolveNamespaceURI(String uri) {
   
    	String prefix = null;
    	if(contextualNamespaces != null) {
    		prefix = contextualNamespaces.resolveNamespaceURI(uri);
    	}
        if(prefix == null) {
        	prefix = super.resolveNamespaceURI(uri);
        }
        return prefix;
    }
    
    @Override
    public void put(String prefix, String uri) {
        String newPrefix = prefixMapper.getPreferredPrefix(uri, prefix, true);
        if(newPrefix == null || newPrefix.length() == 0) {
            super.put(prefix, uri);
        } else {
            super.put(newPrefix, uri);
        }
    }

    public NamespacePrefixMapper getPrefixMapper() {
        return this.prefixMapper;
    }

}