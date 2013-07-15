/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     07/16/2013-2.5.1 Guy Pelletier 
 *       - 412384: Applying Converter for parameterized basic-type for joda-time's DateTime does not work
 ******************************************************************************/ 
package org.eclipse.persistence.testing.models.jpa21.advanced;

public class RunnerTag {
    protected String description;

    public RunnerTag(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }

    public void setTag(String description) {
        this.description = description;
    }
}
