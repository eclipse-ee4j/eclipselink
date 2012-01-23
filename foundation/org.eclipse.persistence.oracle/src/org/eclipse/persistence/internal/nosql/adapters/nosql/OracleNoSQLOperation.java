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
package org.eclipse.persistence.internal.nosql.adapters.nosql;

/**
 * Defines the valid Oracle NoSQL operations.
 *
 * @author James
 * @since EclipseLink 2.4
 */
public enum OracleNoSQLOperation {
    GET, PUT, PUT_IF_ABSENT, PUT_IF_PRESENT, PUT_IF_VERSION, DELETE, DELETE_IF_VERSION, ITERATOR
}
