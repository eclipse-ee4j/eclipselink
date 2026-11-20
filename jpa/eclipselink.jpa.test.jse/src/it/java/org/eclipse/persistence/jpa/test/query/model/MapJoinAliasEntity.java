/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.jpa.test.query.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.annotations.MapKeyConvert;
import org.eclipse.persistence.annotations.TypeConverter;

@Entity
@Table(name = "JPA_MAP_ALIAS_OWNER")
public class MapJoinAliasEntity {

    @Id
    private Long id;

    @ElementCollection
    @CollectionTable(name = "JPA_MAP_ALIAS_VALUES", joinColumns = @JoinColumn(name = "OWNER_ID"))
    @MapKeyColumn(name = "ENV_KEY", length = 30)
    @Column(name = "ENV_VALUE", length = 64)
    private Map<String, String> environment = new LinkedHashMap<>();

    @ElementCollection
    @CollectionTable(name = "JPA_MAP_ALIAS_NAMES", joinColumns = @JoinColumn(name = "OWNER_ID"))
    @Column(name = "OS_NAME", length = 64)
    private List<String> operatingSystems = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "JPA_MAP_ALIAS_AMOUNTS", joinColumns = @JoinColumn(name = "OWNER_ID"))
    @MapKeyColumn(name = "AMOUNT_KEY", length = 30)
    @Column(name = "AMOUNT_VALUE", length = 30)
    @Convert(converter = LongToStringConverter.class)
    private Map<String, Long> amounts = new LinkedHashMap<>();

    @ElementCollection
    @CollectionTable(name = "JPA_MAP_ALIAS_NUMBERS", joinColumns = @JoinColumn(name = "OWNER_ID"))
    @Column(name = "NUMBER_VALUE", length = 30)
    @Convert(converter = LongToStringConverter.class)
    private List<Long> numbers = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "JPA_MAP_ALIAS_MIXED", joinColumns = @JoinColumn(name = "OWNER_ID"))
    @MapKeyColumn(name = "AMOUNT_KEY")
    @Column(name = "AMOUNT_VALUE", length = 30)
    @Convert(converter = LongToStringConverter.class)
    @TypeConverter(name = "aliasKeyAsNumber", objectType = String.class, dataType = Integer.class)
    @MapKeyConvert("aliasKeyAsNumber")
    private Map<String, Long> mixedAmounts = new LinkedHashMap<>();

    public MapJoinAliasEntity() {
    }

    public MapJoinAliasEntity(long id, String operatingSystem, String editor) {
        this.id = id;
        environment.put("osName", operatingSystem);
        environment.put("editor", editor);
        environment.put("comparison", "Windows");
        operatingSystems.add(operatingSystem);
        amounts.put("main", id + 6);
        numbers.add(id + 6);
        mixedAmounts.put("101", id + 6);
    }

    public Long getId() {
        return id;
    }

    @Converter
    public static class LongToStringConverter implements AttributeConverter<Long, String> {
        @Override
        public String convertToDatabaseColumn(Long value) {
            return value == null ? null : value.toString();
        }

        @Override
        public Long convertToEntityAttribute(String value) {
            return value == null ? null : Long.valueOf(value);
        }
    }
}
