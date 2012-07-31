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
 * 		dclarke/tware - initial 
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs;

import javax.ejb.EJB;
import javax.ejb.Singleton;

/**
 * JAX-RS application interface JPA-RS
 * 
 * @author dclarke
 * @since EclipseLink 2.4.0
 */
@Singleton
public class Service extends ServiceBase {

    public void setPersistenceFactory(PersistenceFactoryBase factory) {
        this.factory = factory;
    }


}
