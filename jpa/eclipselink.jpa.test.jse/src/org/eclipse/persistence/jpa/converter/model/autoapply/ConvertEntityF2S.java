/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2016 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     12/05/2016-2.6 Jody Grassel
//       - 443546: Converter autoApply does not work for primitive types

package org.eclipse.persistence.jpa.converter.model.autoapply;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ConvertEntityF2S {
    @Id
    private long id;
    
    private float valueConvert;
    
    @Convert(disableConversion=true)
    private float valueNoConvert;
    
    public ConvertEntityF2S() {
        
    }
    
    public ConvertEntityF2S(long id) {
        this.id = id;
    }
    
    public ConvertEntityF2S(long id, float valueConvert, float valueNoConvert) {
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

    public float getValueConvert() {
        return valueConvert;
    }

    public void setValueConvert(float valueConvert) {
        this.valueConvert = valueConvert;
    }

    public float getValueNoConvert() {
        return valueNoConvert;
    }

    public void setValueNoConvert(float valueNoConvert) {
        this.valueNoConvert = valueNoConvert;
    }

    @Override
    public String toString() {
        return "ConvertEntityF2S [id=" + id + ", valueConvert=" + valueConvert + ", valueNoConvert=" + valueNoConvert + "]";
    }
    
    
}
