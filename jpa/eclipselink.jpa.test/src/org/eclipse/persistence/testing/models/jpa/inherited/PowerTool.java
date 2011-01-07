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
 *     06/22/2010-2.2 Guy Pelletier 
 *       - 308729: Persistent Unit deployment exception when mappedsuperclass has no annotations but has lifecycle callbacks
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.inherited;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;

@MappedSuperclass
public class PowerTool {
    
    /**
     * Note: this class is currently used to test a processing error (when a 
     * lifecycle callback method annotation is incorrectly used to determine
     * a PROPERTY access type)
     * 
     * Please do not change this class, it should only contain the one mapped
     * lifecycle method below.
     */
    
    public static int POWER_TOOL_PRE_PERSIST_COUNT = 0;
    
    @PrePersist
    public void forSale() {
        POWER_TOOL_PRE_PERSIST_COUNT++;
    }
}
