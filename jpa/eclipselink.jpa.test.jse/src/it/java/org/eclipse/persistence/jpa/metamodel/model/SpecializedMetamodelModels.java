/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.jpa.metamodel.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Basic;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.persistence.metamodel.BooleanAttribute;
import jakarta.persistence.metamodel.CollectionAttribute;
import jakarta.persistence.metamodel.ComparableAttribute;
import jakarta.persistence.metamodel.EmbeddableType;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.MapAttribute;
import jakarta.persistence.metamodel.MappedSuperclassType;
import jakarta.persistence.metamodel.NumericAttribute;
import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import jakarta.persistence.metamodel.TemporalAttribute;
import jakarta.persistence.metamodel.TextAttribute;

/** Models deliberately mix specialized and legacy canonical declarations. */
public final class SpecializedMetamodelModels {
    private SpecializedMetamodelModels() {
    }

    public enum Level { LOW, HIGH }

    public static final class Code implements Comparable<Code>, Serializable {
        private final String value;

        public Code(String value) { this.value = value; }
        public String value() { return value; }
        @Override public int compareTo(Code other) { return value.compareTo(other.value); }
        @Override public boolean equals(Object other) { return other instanceof Code code && value.equals(code.value); }
        @Override public int hashCode() { return Objects.hash(value); }
    }

    @Converter
    public static class CodeConverter implements AttributeConverter<Code, String> {
        @Override public String convertToDatabaseColumn(Code value) { return value == null ? null : value.value(); }
        @Override public Code convertToEntityAttribute(String value) { return value == null ? null : new Code(value); }
    }

    @Converter
    public static class StringNumberConverter implements AttributeConverter<String, Integer> {
        @Override public Integer convertToDatabaseColumn(String value) { return value == null ? null : Integer.valueOf(value); }
        @Override public String convertToEntityAttribute(Integer value) { return value == null ? null : value.toString(); }
    }

    @Converter
    public static class LevelConverter implements AttributeConverter<Level, String> {
        @Override public String convertToDatabaseColumn(Level value) { return value == null ? null : value.name(); }
        @Override public Level convertToEntityAttribute(String value) { return value == null ? null : Level.valueOf(value); }
    }

    public static final class GenericValue<T> implements Comparable<GenericValue<T>>, Serializable {
        private final String value;

        public GenericValue(String value) { this.value = value; }
        public String value() { return value; }
        @Override public int compareTo(GenericValue<T> other) { return value.compareTo(other.value); }
        @Override public boolean equals(Object other) { return other instanceof GenericValue<?> that && value.equals(that.value); }
        @Override public int hashCode() { return value.hashCode(); }
    }

    public static final class GenericNumber<T> extends Number implements Comparable<GenericNumber<T>> {
        private final int value;

        public GenericNumber(int value) { this.value = value; }
        @Override public int intValue() { return value; }
        @Override public long longValue() { return value; }
        @Override public float floatValue() { return value; }
        @Override public double doubleValue() { return value; }
        @Override public int compareTo(GenericNumber<T> other) { return Integer.compare(value, other.value); }
        @Override public boolean equals(Object other) { return other instanceof GenericNumber<?> that && value == that.value; }
        @Override public int hashCode() { return Integer.hashCode(value); }
    }

    public static final class GenericWrong<T> implements Comparable<GenericWrong<Integer>>, Serializable {
        private final String value;

        public GenericWrong(String value) { this.value = value; }
        public String value() { return value; }
        @Override public int compareTo(GenericWrong<Integer> other) { return value.compareTo(other.value); }
        @Override public boolean equals(Object other) { return other instanceof GenericWrong<?> that && value.equals(that.value); }
        @Override public int hashCode() { return value.hashCode(); }
    }

    public static class GenericOuter<T> implements Serializable {
        public class Inner<U> implements Comparable<GenericOuter<T>.Inner<U>>, Serializable {
            private final String value;

            public Inner(String value) { this.value = value; }
            public String value() { return value; }
            @Override public int compareTo(GenericOuter<T>.Inner<U> other) { return value.compareTo(other.value); }
        }

        public class WrongInner<U> implements Comparable<GenericOuter<Integer>.WrongInner<U>>, Serializable {
            private final String value;

            public WrongInner(String value) { this.value = value; }
            public String value() { return value; }
            @Override public int compareTo(GenericOuter<Integer>.WrongInner<U> other) { return value.compareTo(other.value); }
        }
    }

    @Converter
    public static class GenericValueConverter implements AttributeConverter<GenericValue<String>, String> {
        @Override public String convertToDatabaseColumn(GenericValue<String> value) { return value == null ? null : value.value(); }
        @Override public GenericValue<String> convertToEntityAttribute(String value) { return value == null ? null : new GenericValue<>(value); }
    }

    @Converter
    public static class GenericNumberConverter implements AttributeConverter<GenericNumber<String>, String> {
        @Override public String convertToDatabaseColumn(GenericNumber<String> value) { return value == null ? null : Integer.toString(value.intValue()); }
        @Override public GenericNumber<String> convertToEntityAttribute(String value) { return value == null ? null : new GenericNumber<>(Integer.parseInt(value)); }
    }

    @Converter
    public static class GenericWrongConverter implements AttributeConverter<GenericWrong<String>, String> {
        @Override public String convertToDatabaseColumn(GenericWrong<String> value) { return value == null ? null : value.value(); }
        @Override public GenericWrong<String> convertToEntityAttribute(String value) { return value == null ? null : new GenericWrong<>(value); }
    }

    @Converter
    public static class GenericInnerConverter implements AttributeConverter<GenericOuter<String>.Inner<Integer>, String> {
        @Override public String convertToDatabaseColumn(GenericOuter<String>.Inner<Integer> value) { return value == null ? null : value.value(); }
        @Override public GenericOuter<String>.Inner<Integer> convertToEntityAttribute(String value) {
            return value == null ? null : new GenericOuter<String>().new Inner<Integer>(value);
        }
    }

    @Converter
    public static class GenericWrongInnerConverter implements AttributeConverter<GenericOuter<String>.WrongInner<Integer>, String> {
        @Override public String convertToDatabaseColumn(GenericOuter<String>.WrongInner<Integer> value) { return value == null ? null : value.value(); }
        @Override public GenericOuter<String>.WrongInner<Integer> convertToEntityAttribute(String value) {
            return value == null ? null : new GenericOuter<String>().new WrongInner<Integer>(value);
        }
    }

    @MappedSuperclass
    public static class Base<T> {
        @Id
        public Integer id;
        @Version
        public int version;
        @Column(name = "BASE_LABEL")
        public String inheritedLabel;
        @Convert(converter = GenericValueConverter.class)
        public GenericValue<T> inheritedValue;
        @Convert(converter = GenericNumberConverter.class)
        public GenericNumber<T> inheritedNumber;
        @Convert(converter = GenericWrongConverter.class)
        public GenericWrong<T> inheritedWrong;
    }

    @StaticMetamodel(Base.class)
    public static class Base_ {
        public static volatile MappedSuperclassType<Base> class_;
        public static volatile NumericAttribute<Base, Integer> id;
        public static volatile NumericAttribute<Base, Integer> version;
        public static volatile TextAttribute<Base> inheritedLabel;
        public static volatile SingularAttribute<Base, GenericValue> inheritedValue;
        public static volatile SingularAttribute<Base, GenericNumber> inheritedNumber;
        public static volatile SingularAttribute<Base, GenericWrong> inheritedWrong;
    }

    @Embeddable
    public static class Details implements Comparable<Details> {
        @Column(name = "DETAIL_TEXT")
        public String text;
        @Column(name = "DETAIL_RANK")
        public Integer rank;
        public Details() { }
        public Details(String text, Integer rank) { this.text = text; this.rank = rank; }
        @Override public int compareTo(Details other) { return text.compareTo(other.text); }
    }

    @StaticMetamodel(Details.class)
    public static class Details_ {
        public static volatile EmbeddableType<Details> class_;
        public static volatile TextAttribute<Details> text;
        public static volatile NumericAttribute<Details, Integer> rank;
    }

    @Entity(name = "SpecializedMetaTarget")
    @Table(name = "JPA_SM_TARGET")
    public static class Target implements Comparable<Target> {
        @Id
        public Integer id;
        public String label;
        public Target() { }
        public Target(Integer id, String label) { this.id = id; this.label = label; }
        @Override public int compareTo(Target other) { return id.compareTo(other.id); }
    }

    @StaticMetamodel(Target.class)
    public static class Target_ {
        public static volatile EntityType<Target> class_;
        public static volatile NumericAttribute<Target, Integer> id;
        public static volatile TextAttribute<Target> label;
    }

    @Entity(name = "SpecializedMetaOwner")
    @Table(name = "JPA_SM_OWNER")
    public static class Owner extends Base<String> {
        @Basic(optional = false)
        @Column(name = "TEXT_VALUE", nullable = false)
        public String text;
        public int quantity;
        public boolean active;
        public LocalDate published;
        @Column(precision = 12, scale = 2)
        public BigDecimal amount;
        @Column(name = "LETTER_VALUE")
        public Character letter;
        @Enumerated(EnumType.ORDINAL)
        public Level ordinalLevel;
        @Enumerated(EnumType.STRING)
        public Level stringLevel;
        @Convert(converter = LevelConverter.class)
        public Level convertedLevel;
        @Convert(converter = CodeConverter.class)
        public Code convertedCode;
        @Convert(converter = StringNumberConverter.class)
        public String numericText;
        @Convert(converter = GenericValueConverter.class)
        public GenericValue<String> genericValue;
        @Convert(converter = GenericNumberConverter.class)
        public GenericNumber<String> genericNumber;
        @Convert(converter = GenericWrongConverter.class)
        public GenericWrong<String> genericWrong;
        @Convert(converter = GenericInnerConverter.class)
        public GenericOuter<String>.Inner<Integer> innerValue;
        @Convert(converter = GenericWrongInnerConverter.class)
        public GenericOuter<String>.WrongInner<Integer> wrongInnerValue;
        @Embedded
        public Details details;
        @ManyToOne
        @JoinColumn(name = "TARGET_ID")
        public Target target;
        @ElementCollection
        @CollectionTable(name = "JPA_SM_TAGS", joinColumns = @JoinColumn(name = "OWNER_ID"))
        @Column(name = "TAG_VALUE")
        public Set<String> tags = new HashSet<>();
        @ElementCollection
        @CollectionTable(name = "JPA_SM_LIST", joinColumns = @JoinColumn(name = "OWNER_ID"))
        @OrderColumn(name = "ITEM_ORDER")
        @Column(name = "ITEM_VALUE")
        public List<String> items = new ArrayList<>();
        @ElementCollection
        @CollectionTable(name = "JPA_SM_COLL", joinColumns = @JoinColumn(name = "OWNER_ID"))
        @Column(name = "ENTRY_VALUE")
        public Collection<String> entries = new ArrayList<>();
        @ElementCollection
        @CollectionTable(name = "JPA_SM_MAP", joinColumns = @JoinColumn(name = "OWNER_ID"))
        @MapKeyColumn(name = "SCORE_KEY")
        @Column(name = "SCORE_VALUE")
        public Map<String, Integer> scores = new HashMap<>();
    }

    @StaticMetamodel(Owner.class)
    public static class Owner_ extends Base_ {
        public static volatile EntityType<Owner> class_;
        public static volatile TextAttribute<Owner> text;
        public static volatile NumericAttribute<Owner, Integer> quantity;
        public static volatile BooleanAttribute<Owner> active;
        public static volatile TemporalAttribute<Owner, LocalDate> published;
        public static volatile NumericAttribute<Owner, BigDecimal> amount;
        public static volatile ComparableAttribute<Owner, Character> letter;
        public static volatile ComparableAttribute<Owner, Level> ordinalLevel;
        public static volatile ComparableAttribute<Owner, Level> stringLevel;
        public static volatile ComparableAttribute<Owner, Level> convertedLevel;
        public static volatile ComparableAttribute<Owner, Code> convertedCode;
        public static volatile TextAttribute<Owner> numericText;
        public static volatile ComparableAttribute<Owner, GenericValue<String>> genericValue;
        public static volatile NumericAttribute<Owner, GenericNumber<String>> genericNumber;
        public static volatile SingularAttribute<Owner, GenericWrong<String>> genericWrong;
        public static volatile ComparableAttribute<Owner, GenericOuter<String>.Inner<Integer>> innerValue;
        public static volatile SingularAttribute<Owner, GenericOuter<String>.WrongInner<Integer>> wrongInnerValue;
        public static volatile SingularAttribute<Owner, Details> details;
        public static volatile SingularAttribute<Owner, Target> target;
        public static volatile SetAttribute<Owner, String> tags;
        public static volatile ListAttribute<Owner, String> items;
        public static volatile CollectionAttribute<Owner, String> entries;
        public static volatile MapAttribute<Owner, String, Integer> scores;
    }

    @Entity(name = "SpecializedMetaProperty")
    @Table(name = "JPA_SM_PROPERTY")
    public static class PropertyOwner {
        private Integer id;
        private String text;
        private int quantity;
        private GenericValue<String> genericValue;
        private GenericNumber<String> genericNumber;
        private GenericWrong<String> genericWrong;
        @Id public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        @Column(name = "TEXT_VALUE") public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        @Convert(converter = GenericValueConverter.class)
        public GenericValue<String> getGenericValue() { return genericValue; }
        public void setGenericValue(GenericValue<String> value) { this.genericValue = value; }
        @Convert(converter = GenericNumberConverter.class)
        public GenericNumber<String> getGenericNumber() { return genericNumber; }
        public void setGenericNumber(GenericNumber<String> value) { this.genericNumber = value; }
        @Convert(converter = GenericWrongConverter.class)
        public GenericWrong<String> getGenericWrong() { return genericWrong; }
        public void setGenericWrong(GenericWrong<String> value) { this.genericWrong = value; }
    }

    @StaticMetamodel(PropertyOwner.class)
    public static class PropertyOwner_ {
        public static volatile EntityType<PropertyOwner> class_;
        public static volatile NumericAttribute<PropertyOwner, Integer> id;
        public static volatile TextAttribute<PropertyOwner> text;
        public static volatile NumericAttribute<PropertyOwner, Integer> quantity;
        public static volatile ComparableAttribute<PropertyOwner, GenericValue<String>> genericValue;
        public static volatile NumericAttribute<PropertyOwner, GenericNumber<String>> genericNumber;
        public static volatile SingularAttribute<PropertyOwner, GenericWrong<String>> genericWrong;
    }

    @Entity(name = "SpecializedMetaLegacy")
    @Table(name = "JPA_SM_LEGACY")
    public static class Legacy {
        @Id public Integer id;
        @Column(name = "TEXT_VALUE") public String text;
        public int quantity;
        public boolean active;
        public LocalDate published;
    }

    @StaticMetamodel(Legacy.class)
    public static class Legacy_ {
        public static volatile EntityType<Legacy> class_;
        public static volatile SingularAttribute<Legacy, Integer> id;
        public static volatile SingularAttribute<Legacy, String> text;
        public static volatile SingularAttribute<Legacy, Integer> quantity;
        public static volatile SingularAttribute<Legacy, Boolean> active;
        public static volatile SingularAttribute<Legacy, LocalDate> published;
    }

    @Entity(name = "SpecializedMetaPartial")
    @Table(name = "JPA_SM_PARTIAL")
    public static class Partial {
        @Id public Integer id;
        @Column(name = "TEXT_VALUE") public String text;
    }

    @StaticMetamodel(Partial.class)
    public static class Partial_ {
        public static volatile NumericAttribute<Partial, Integer> id;
        // Older or hand-written canonical classes may omit class_ and attributes.
    }

    @Entity(name = "SpecializedMetaAbsent")
    @Table(name = "JPA_SM_ABSENT")
    public static class Absent {
        @Id public Integer id;
        @Column(name = "TEXT_VALUE") public String text;
    }

    // Mapped only by the negative test's explicit ORM file, never archive scanning.
    public static class Invalid {
        public Integer id;
    }

    public interface UnsupportedAttribute<X, Y> extends SingularAttribute<X, Y> {
    }

    @StaticMetamodel(Invalid.class)
    public static class Invalid_ {
        // A nonstandard subtype cannot receive a standard provider proxy.
        public static volatile UnsupportedAttribute<Invalid, Integer> id;
    }
}
