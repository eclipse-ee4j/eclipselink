/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      tware - initial 
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs.service;

import javax.ws.rs.Path;

/**
 * This interface is provided as a means to provide the base path for the JPA-RS REST service
 * in a manner that is portable between Jersey 1.x and Jersey 2.x.
 * 
 * @see JPARSApplication
 * @author tware
 *
 */
@Path("/")
public interface ServicePathDefinition {

}
