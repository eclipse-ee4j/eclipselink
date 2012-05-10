/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.oxm;

/**
 * <p><b>Purpose</b>:Provides a means to customise the namespace prefixes used while marshalling
 * An implementation of this class can be set on an instance of XMLMarshaller to allow for 
 * each instance of XMLMarshaller to use different namespace prefixes. 
 */
public abstract class NamespacePrefixMapper {
    
    public abstract String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix);

    /**
     * Returns a list of namespace uris that should be declared at the root of the xml 
     * document being marshalled.
     */
    public String[] getPreDeclaredNamespaceUris() {
        return new String[0];
    }
    
    /**
     * Returns a string array of prefixes and namespace uris to be declared at the root of 
     * the document. This eliminates the need of implementing both getPredeclaredNamespaceUris
     * and getPreferredPrefix since the prefix and uri can be associated here.
     * @return
     */
    public String[] getPreDeclaredNamespaceUris2() {
        return new String[0];
    }
    /**
     * Returns a string array of prefixes and namespace uris that are already available in
     * this context. Only required when marshalling to an output stream or a writer, since
     * it's not possible to determine which namespaces are already in scope. 
     * @return
     */
    public String[] getContextualNamespaceDecls() {
        return new String[0];
    }
    
    /**
     * Return true if this prefix mapper applies to the media type provided.
     */
    public boolean supportsMediaType(MediaType mediaType) {
        return true;
    }
}
