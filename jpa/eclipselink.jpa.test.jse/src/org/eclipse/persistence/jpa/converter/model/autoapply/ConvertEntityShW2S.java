/*******************************************************************************
 * Copyright (c) 2016 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     12/05/2016-2.6 Jody Grassel
 *       - 443546: Converter autoApply does not work for primitive types
 ******************************************************************************/

package org.eclipse.persistence.jpa.converter.model.autoapply;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ConvertEntityShW2S {
    @Id
    private long id;
    
    private Short valueConvert;
    
    @Convert(disableConversion=true)
    private Short valueNoConvert;
    
    public ConvertEntityShW2S() {
        
    }
    
    public ConvertEntityShW2S(long id) {
        this.id = id;
    }
    
    public ConvertEntityShW2S(long id, Short valueConvert, Short valueNoConvert) {
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

    public Short getValueConvert() {
        return valueConvert;
    }

    public void setValueConvert(Short valueConvert) {
        this.valueConvert = valueConvert;
    }

    public Short getValueNoConvert() {
        return valueNoConvert;
    }

    public void setValueNoConvert(Short valueNoConvert) {
        this.valueNoConvert = valueNoConvert;
    }

    @Override
    public String toString() {
        return "ConvertEntityShW2S [id=" + id + ", valueConvert=" + valueConvert + ", valueNoConvert=" + valueNoConvert + "]";
    }
    
    
}
