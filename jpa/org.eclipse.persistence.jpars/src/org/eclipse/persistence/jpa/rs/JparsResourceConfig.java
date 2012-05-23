/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.rs;

import org.eclipse.persistence.jpa.rs.exceptions.ClassNotFoundExceptionMapper;
import org.eclipse.persistence.jpa.rs.exceptions.IOExceptionMapper;
import org.eclipse.persistence.jpa.rs.exceptions.IllegalAccessExceptionMapper;
import org.eclipse.persistence.jpa.rs.exceptions.IllegalArgumentExceptionMapper;
import org.eclipse.persistence.jpa.rs.exceptions.IllegalStateExceptionMapper;
import org.eclipse.persistence.jpa.rs.exceptions.InvocationTargetExceptionMapper;
import org.eclipse.persistence.jpa.rs.exceptions.JAXBExceptionMapper;
import org.eclipse.persistence.jpa.rs.exceptions.JPARSExceptionMapper;
import org.eclipse.persistence.jpa.rs.exceptions.MalformedURLExceptionMapper;
import org.eclipse.persistence.jpa.rs.exceptions.NamingExceptionMapper;
import org.eclipse.persistence.jpa.rs.exceptions.NoResultExceptionMapper;
import org.eclipse.persistence.jpa.rs.exceptions.NoSuchMethodExceptionMapper;
import org.eclipse.persistence.jpa.rs.exceptions.NonUniqueResultExceptionExceptionMapper;

import com.sun.jersey.api.core.DefaultResourceConfig;

public class JparsResourceConfig extends DefaultResourceConfig {
    public JparsResourceConfig() {
        getExplicitRootResources().put("/", Service.class);
        getClasses().add(ClassNotFoundExceptionMapper.class);
        getClasses().add(IllegalAccessExceptionMapper.class);
        getClasses().add(IllegalArgumentExceptionMapper.class);
        getClasses().add(IllegalStateExceptionMapper.class);
        getClasses().add(InvocationTargetExceptionMapper.class);
        getClasses().add(IOExceptionMapper.class);
        getClasses().add(JAXBExceptionMapper.class);
        getClasses().add(JPARSExceptionMapper.class);
        getClasses().add(MalformedURLExceptionMapper.class);
        getClasses().add(NamingExceptionMapper.class);
        getClasses().add(NonUniqueResultExceptionExceptionMapper.class);
        getClasses().add(NoResultExceptionMapper.class);
        getClasses().add(NoSuchMethodExceptionMapper.class);
        
    }
}