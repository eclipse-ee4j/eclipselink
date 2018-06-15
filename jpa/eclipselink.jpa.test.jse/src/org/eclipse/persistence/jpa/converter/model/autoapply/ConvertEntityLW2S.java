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
public class ConvertEntityLW2S {
    @Id
    private long id;
    
    private Long valueConvert;
    
    @Convert(disableConversion=true)
    private Long valueNoConvert;
    
    public ConvertEntityLW2S() {
        
    }
    
    public ConvertEntityLW2S(long id) {
        this.id = id;
    }
    
    public ConvertEntityLW2S(long id, Long valueConvert, Long valueNoConvert) {
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

    public Long getValueConvert() {
        return valueConvert;
    }

    public void setValueConvert(Long valueConvert) {
        this.valueConvert = valueConvert;
    }

    public Long getValueNoConvert() {
        return valueNoConvert;
    }

    public void setValueNoConvert(Long valueNoConvert) {
        this.valueNoConvert = valueNoConvert;
    }

    @Override
    public String toString() {
        return "ConvertEntityLW2S [id=" + id + ", valueConvert=" + valueConvert + ", valueNoConvert=" + valueNoConvert + "]";
    }
    
    
}
