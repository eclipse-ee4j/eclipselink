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
 *  06/12/2008-1.0M9 James Sutherland
 ******************************************************************************/  
package org.eclipse.persistence.sessions.factories;

/**
 * This class handles migration from TopLink when broad imports were used.
 * This class was moved to config.
 * @author James Sutherland
 * @Deprecated replaced by org.eclipse.persistence.config.DescriptorCustomizer
 */
@Deprecated
public interface DescriptorCustomizer extends org.eclipse.persistence.config.DescriptorCustomizer {

}
