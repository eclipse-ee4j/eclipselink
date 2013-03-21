/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     10/25/2012-2.5 Guy Pelletier 
 *       - 374688: JPA 2.1 Converter support
 ******************************************************************************/ 
package org.eclipse.persistence.testing.models.jpa21.advanced.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.eclipse.persistence.testing.models.jpa21.advanced.enums.RunningStatus;

@Converter(autoApply=false)
public class RunningStatusConverter implements AttributeConverter<RunningStatus, String> {
    
    @Override
    public String convertToDatabaseColumn(RunningStatus runningStatus) {
        if (runningStatus == null) {
            return null;
        } else if (runningStatus.equals(RunningStatus.D)) {
            return RunningStatus.DOWN_TIME.name();
        } else if (runningStatus.equals(RunningStatus.R)) {
            return RunningStatus.RACING.name();
        } else if (runningStatus.equals(RunningStatus.T)) {
            return RunningStatus.TRAINING.name();
        } else {
            return runningStatus.name();
        } 
    }

    @Override
    public RunningStatus convertToEntityAttribute(String dbData) {
        return (dbData == null) ? null : RunningStatus.valueOf(dbData);
    }
}
