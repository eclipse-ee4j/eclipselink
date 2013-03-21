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
package org.eclipse.persistence.oxm;

/**
 * <p >It is common for an XML document to include one or more namespaces.
 * TopLink supports this using its NamespaceResolver. The namespace resolver maintains
 * pairs of namespace prefixes and URIs. TopLink uses these prefixes in conjunction with the
 * XPath statements you specify on EIS mappings to XML records and XML mappings.
 *
 * <p>Although TopLink captures namespace prefixes in the XPath statements for mappings (if applicable),
 * the input document is not required to use the same namespace prefixes. TopLink will use the namespace
 * prefixes specified in the mapping when creating new documents.
 *
 * <p><em>Code Sample</em><br>
 * <code>
 *  <b>NamespaceResolver resolver = new NamespaceResolver();<br>
 *  resolver.put(    "ns", "urn:namespace-example");<br><br></b>
 *
 *  XMLDescriptor descriptor = new XMLDescriptor();<br>
 *  descriptor.setJavaClass(Customer.class); <br>
 *  descriptor.setDefaultRootElement("<b>ns</b>:customer");<br>
 *  descriptor.setNamespaceResolver(resolver);<br><br>
 *
 *  XMLDirectMapping mapping = new XMLDirectMapping();<br>
 *  mapping.setAttributeName("id");<br>
 *  mapping.setXPath("<b>ns</b>:id/text()");<br>
 *  descriptor.addMapping(mapping);
 *  </code>
 *
 *  @see org.eclipse.persistence.oxm.XMLDescriptor
 *  @see org.eclipse.persistence.eis.EISDescriptor
 *
 */
public class NamespaceResolver extends org.eclipse.persistence.internal.oxm.NamespaceResolver {

    /**
     * Default constructor
     */
    public NamespaceResolver() {
        super();
    }

    /**
     * Copy constructor
     * @since EclipseLink 2.5.0
     */
    public NamespaceResolver(NamespaceResolver namespaceResolver) {
        super(namespaceResolver);
    }

}
