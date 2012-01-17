/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.internal.nosql.adapters.mongo;

import javax.resource.cci.*;

/**
 * Interaction spec for Mongo JCA adapter.
 *
 * @author James
 * @since EclipseLink 2.4
 */
public class MongoInteractionSpec implements InteractionSpec {
    protected MongoOperation operation;
    protected String collection;

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public MongoOperation getOperation() {
        return operation;
    }

    public void setOperation(MongoOperation operation) {
        this.operation = operation;
    }

    public String toString() {
        return getClass().getSimpleName() + "(" + ")";
    }
}
