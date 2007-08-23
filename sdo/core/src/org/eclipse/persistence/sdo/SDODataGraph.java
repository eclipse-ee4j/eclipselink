/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/ 
package org.eclipse.persistence.sdo;

import commonj.sdo.DataObject;
import commonj.sdo.ChangeSummary;
import commonj.sdo.Type;
import commonj.sdo.DataGraph;

/**
 * <p><b>Purpose</b>:A data graph is used to package a graph of {@link DataObject data objects} along with their
 * metadata, that is, data describing the data.
 * A data graph also contains a {@link #getChangeSummary change summary}
 * which is used to record changes made to the objects in the graph.
 */
public class SDODataGraph implements DataGraph {
    public SDODataGraph() {
    }

    /**
     * Returns the root {@link DataObject data object} of this data graph.
     * @return the root data object.
     * @see DataObject#getDataGraph
     */
    public DataObject getRootObject() {
        throw new UnsupportedOperationException("Method Not Supported.");
    }

    /**
     * Returns the {@link ChangeSummary change summary} associated with this data graph.
     * @return the change summary.
     * @see ChangeSummary#getDataGraph
     */
    public ChangeSummary getChangeSummary() {
        throw new UnsupportedOperationException("Method Not Supported.");
    }

    /**
     * Returns the {@link Type type} with the given the {@link Type#getURI() URI},
     * or contained by the resource at the given URI,
     * and with the given {@link Type#getName name}.
     * @param uri the namespace URI of a type or the location URI of a resource containing a type.
     * @param typeName name of a type.
     * @return the type with the corresponding namespace and name.
     */
    public Type getType(String uri, String typeName) {
        throw new UnsupportedOperationException("Method Not Supported.");
    }

    /**
     * Creates a new root data object of the {@link #getType specified type}.
     * An exception is thrown if a root object exists.
     * @param namespaceURI namespace of the type.
     * @param typeName name of the type.
     * @return the new root.
     * @throws IllegalStateException if the root object already exists.
     * @see #createRootObject(Type)
     * @see #getType(String, String)
     */
    public DataObject createRootObject(String namespaceURI, String typeName) {
        throw new UnsupportedOperationException("Method Not Supported.");
    }

    /**
     * Creates a new root data object of the specified type.
     * An exception is thrown if a root object exists.
     * @param type the type of the new root.
     * @return the new root.
     * @throws IllegalStateException if the root object already exists.
     * @see #createRootObject(String, String)
     */
    public DataObject createRootObject(Type type) {
        throw new UnsupportedOperationException("Method Not Supported.");
    }
}