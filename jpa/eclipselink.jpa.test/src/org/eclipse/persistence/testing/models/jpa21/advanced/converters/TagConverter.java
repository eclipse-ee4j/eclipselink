/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     07/16/2013-2.5.1 Guy Pelletier
//       - 412384: Applying Converter for parameterized basic-type for joda-time's DateTime does not work
package org.eclipse.persistence.testing.models.jpa21.advanced.converters;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.eclipse.persistence.testing.models.jpa21.advanced.RunnerTag;

@Converter(autoApply=true)
public class TagConverter implements AttributeConverter<List<RunnerTag>, String> {

    @Override
    public String convertToDatabaseColumn(List<RunnerTag> attribute) {
        if (attribute == null) {
            return null;
        } else {
            String toReturn = null;

            for (RunnerTag tag : attribute) {
                if (toReturn == null) {
                    toReturn = tag.getDescription();
                } else {
                    toReturn = "-" + tag.getDescription();
                }
            }

            return toReturn;
        }
    }

    @Override
    public List<RunnerTag> convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        } else {
            List<RunnerTag> toReturn = new ArrayList<RunnerTag>();

            StringTokenizer st = new StringTokenizer(dbData, "-");
            while (st.hasMoreElements()) {
                toReturn.add(new RunnerTag((String) st.nextElement()));
            }

            return toReturn;
        }
    }
}

