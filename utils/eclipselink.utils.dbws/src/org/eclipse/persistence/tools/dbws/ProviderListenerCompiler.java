/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman - July 13 2010
 *       fix for https://bugs.eclipse.org/bugs/show_bug.cgi?id=318207
 ******************************************************************************/
package org.eclipse.persistence.tools.dbws;

public class ProviderListenerCompiler extends InMemoryCompiler {

    static final String PROVIDER_LISTENER_NAME = "_dbws.ProviderListener";
    
    public ProviderListenerCompiler() {
        super(PROVIDER_LISTENER_NAME);
    }

}