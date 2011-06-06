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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class LawnMower extends PowerTool {
    /**
     * Note: this class is currently used to test a processing error only and
     * does not define a table in the InheritedTableManager for further testing
     * in the InheritedModelTestSuite. Expanding this class is ok, but do not
     * add an explicit access type (leave it to discover the access type as
     * FIELD)
     */
            
    @Id
    @GeneratedValue
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
