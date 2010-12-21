/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
package org.eclipse.persistence.tools.workbench.uitools.chooser;

/**
 * This will be called when the user presses F3 or chooses
 * 'Go To' in the context menu
 */
public interface NodeSelector 
{       
    /**
     * Select the appropriate Node in the tree or the editor panel.
     */
    void selectNodeFor(Object item);
    
    /**
     * This NodeSelector will do nothing when selectNodeFor(Object) is called
     */
    class DefaultNodeSelector implements NodeSelector {
        
        public void selectNodeFor(Object item) {
            //default is to do nothing
        }
    }
}
