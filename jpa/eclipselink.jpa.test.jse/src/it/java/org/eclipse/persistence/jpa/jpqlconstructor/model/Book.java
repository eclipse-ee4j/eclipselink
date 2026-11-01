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
package org.eclipse.persistence.jpa.jpqlconstructor.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity(name = "ICRBook")
@Table(name = "JPA_ICR_BOOK")
@NamedQuery(name = "ICR.summary", query = "select b.id, b.title from ICRBook b order by b.id",
        resultClass = Results.Summary.class)
@NamedQuery(name = "ICR.raw", query = "select b.id, b.title from ICRBook b order by b.id")
@NamedQuery(name = "ICR.entity", query = "select b from ICRBook b where b.id = :id",
        resultClass = Results.BookResult.class)
public class Book {
    @Id
    public Integer id;
    public String title;
    public String subtitle;
    public int quantity;
    public short smallQuantity;
    public byte tinyQuantity;
    @Column(precision = 12, scale = 2)
    public BigDecimal amount;
    @Convert(converter = CodeConverter.class)
    public Results.Code code;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AUTHOR_ID")
    public Author author;
    @ElementCollection
    @CollectionTable(name = "JPA_ICR_TAG", joinColumns = @JoinColumn(name = "BOOK_ID"))
    @Column(name = "TAG_TEXT")
    public List<String> tags = new ArrayList<>();
    @ElementCollection
    @CollectionTable(name = "JPA_ICR_SCORE", joinColumns = @JoinColumn(name = "BOOK_ID"))
    @MapKeyColumn(name = "SCORE_KEY")
    @Column(name = "SCORE_VALUE")
    public Map<String, Integer> scores = new HashMap<>();
    @ElementCollection
    @CollectionTable(name = "JPA_ICR_LABEL", joinColumns = @JoinColumn(name = "BOOK_ID"))
    @MapKeyColumn(name = "LABEL_KEY")
    @Column(name = "LABEL_VALUE")
    public Map<String, String> labels = new HashMap<>();
    @ElementCollection
    @CollectionTable(name = "JPA_ICR_CODE", joinColumns = @JoinColumn(name = "BOOK_ID"))
    @MapKeyColumn(name = "CODE_KEY")
    @Column(name = "CODE_VALUE")
    @Convert(attributeName = "key", converter = CodeConverter.class)
    @Convert(converter = CodeConverter.class)
    public Map<Results.Code, Results.Code> codes = new HashMap<>();

    @Column(name = "DECLARED_VALUE")
    @Convert(converter = ObjectConverter.class)
    public Object declaredValue;

    public Book() {
    }

    public Book(Integer id, String title) {
        this.id = id;
        this.title = title;
    }

    public Book(Integer id, String title, String subtitle, int quantity, Author author) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.quantity = quantity;
        this.smallQuantity = (short) quantity;
        this.tinyQuantity = (byte) quantity;
        this.amount = new BigDecimal(quantity + ".25");
        this.code = new Results.Code(title);
        this.declaredValue = title;
        this.author = author;
        this.tags.add(title);
        this.scores.put("rating", quantity);
        this.codes.put(new Results.Code("code-key"), new Results.Code(title));
    }
}
