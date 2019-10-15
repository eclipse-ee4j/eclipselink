/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2016 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     12/05/2016-2.6 Jody Grassel
//       - 443546: Converter autoApply does not work for primitive types

package org.eclipse.persistence.jpa.converter.model.autoapply;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ConvertEntityB2S {
    @Id
    private long id;
    
    private boolean valueConvert;
    
    @Convert(disableConversion=true)
    private boolean valueNoConvert;
    
    public ConvertEntityB2S() {
        
    }
    
    public ConvertEntityB2S(long id) {
        this.id = id;
    }
    
    public ConvertEntityB2S(long id, boolean valueConvert, boolean valueNoConvert) {
        this.id = id;
        this.valueConvert = valueConvert;
        this.valueNoConvert = valueNoConvert;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean getValueConvert() {
        return valueConvert;
    }

    public void setValueConvert(boolean valueConvert) {
        this.valueConvert = valueConvert;
    }

    public boolean getValueNoConvert() {
        return valueNoConvert;
    }

    public void setValueNoConvert(boolean valueNoConvert) {
        this.valueNoConvert = valueNoConvert;
    }

    @Override
    public String toString() {
        return "ConvertEntityB2S [id=" + id + ", valueConvert=" + valueConvert + ", valueNoConvert=" + valueNoConvert + "]";
    }
    
    
}
