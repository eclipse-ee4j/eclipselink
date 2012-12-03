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
 *     Dave McCann - Nov. 28, 2012 - 2.4.2 - initial implementation
 ******************************************************************************/
 package org.eclipse.persistence.internal.sessions.factories; 

import org.eclipse.persistence.internal.helper.DatabaseType;
import org.eclipse.persistence.internal.helper.SimpleDatabaseType;

/**
 * <b>INTERNAL</b>: a helper class that holds DatabaseType's. 
 * Used to support marshalling Oracle XMLType instances.
 */
public class XMLTypeWrapper extends DatabaseTypeWrapper {

    public XMLTypeWrapper() {
        super();
    }

    public XMLTypeWrapper(DatabaseType wrappedType) {
        super(wrappedType);
    }

    public SimpleDatabaseType getWrappedType() {
        return (SimpleDatabaseType) wrappedDatabaseType;
    }
}
