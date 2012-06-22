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
 *     dminsky - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.optimisticlocking;

import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.indirection.ValueHolder;

public class Controller extends AbstractVideogameObject {

    protected ValueHolderInterface console;
    
    public Controller() {
        this(null, null);
    }
    
    public Controller(String name, String description) {
        super(name, description);
        this.console = new ValueHolder();
    }

    public GamesConsole getConsole() {
        return (GamesConsole)getConsoleHolder().getValue();
    }
    
    private ValueHolderInterface getConsoleHolder() {
        return this.console;
    }

    public void setConsole(GamesConsole console) {
        getConsoleHolder().setValue(console);
    }
    
    private void setConsoleHolder(ValueHolderInterface holder) {
        this.console = holder;
    }

}
