/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.eis.adapters.xmlfile;

import javax.resource.cci.InteractionSpec;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.w3c.dom.Element;

/**
 * Interaction spec for XML file JCA adapter.
 * Supports CRUD access to an XML file based on XPath and XQuery.
 * Also supports in-memory DOM access.
 *
 * @author James
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class XMLFileInteractionSpec implements InteractionSpec {
    protected transient NamespaceResolver namespaceResolver;
    protected String xPath;
    protected String xQuery;
    protected int interactionType;
    protected String fileName;
    protected transient Element dom;

    /** Type constants for defining interaction type. */
    public static final int INSERT = 0;
    public static final int READ = 1;
    public static final int UPDATE = 2;
    public static final int DELETE = 3;

    /**
     * Default constructor.
     */
    public XMLFileInteractionSpec() {
        this.interactionType = READ;
    }

    public XMLFileInteractionSpec(NamespaceResolver namespaceResolver) {
        this.interactionType = READ;
        this.namespaceResolver = namespaceResolver;
    }

    /**
     * Return the path to the element within the document to be accessed.
     */
    public String getXPath() {
        return xPath;
    }

    /**
     * Return the namespaceResolver to be used to resolve any namespace prefixes in the XPath.
     */
    public NamespaceResolver getNamespaceResolver() {
        return namespaceResolver;
    }

    /**
     * Return the namespaceResolver to be used to resolve any namespace prefixes in the XPath.
     */
    public void setNamespaceResolver(NamespaceResolver namespaceResolver) {
        this.namespaceResolver = namespaceResolver;
    }

    /**
     * Set the path to the element within the document to be accessed.
     */
    public void setXPath(String xPath) {
        this.xPath = xPath;
    }

    /**
     * Return the query to the element within the document to be accessed.
     */
    public String getXQuery() {
        return xQuery;
    }

    /**
     * Set the query to the element within the document to be accessed.
     */
    public void setXQuery(String xQuery) {
        this.xQuery = xQuery;
    }

    /**
     * Return the CRUD interaction type of the interaction.
     */
    public int getInteractionType() {
        return interactionType;
    }

    /**
     * Set the CRUD interaction type of the interaction.
     */
    public void setInteractionType(int interactionType) {
        this.interactionType = interactionType;
    }

    /**
     * Return the name of file to be accessed.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Set the name of file to be accessed.
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Return the XML DOM element to be accessed directly.
     * This can be used for in-memory DOM access.
     */
    public Element getDOM() {
        return dom;
    }

    /**
     * Set the XML DOM element to be accessed directly.
     * This can be used for in-memory DOM access.
     */
    public void setDOM(Element dom) {
        this.dom = dom;
    }

    @Override
    public String toString() {
        return "XMLFileInteractionSpec(" + getFileName() + ":" + getXQuery() + ")";
    }
}
