/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     02/25/2009-2.0 Guy Pelletier 
 *       - 265359: JPA 2.0 Element Collections - Metadata processing portions
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.advanced;

import java.util.*;
import java.io.Serializable;
import javax.persistence.*;

import static javax.persistence.GenerationType.*;
import static javax.persistence.InheritanceType.*;
import static javax.persistence.FetchType.*;

import org.eclipse.persistence.annotations.BasicMap;
import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.MapKeyConvert;
import org.eclipse.persistence.annotations.Converter;
import org.eclipse.persistence.annotations.ObjectTypeConverter;
import org.eclipse.persistence.annotations.OptimisticLocking;
import static org.eclipse.persistence.annotations.OptimisticLockingType.SELECTED_COLUMNS;
import org.eclipse.persistence.annotations.PrivateOwned;

/**
 * Buyer object.
 * Used to test optimistic field locking, events, converters and basic-maps.
 */
@SuppressWarnings("deprecation")
@Entity
@Table(
    name="CMP3_BUYER",
    uniqueConstraints = { 
        @UniqueConstraint(columnNames={"BUYER_ID", "BUYER_NAME"}),
        @UniqueConstraint(columnNames={"BUYER_ID", "DESCRIP"})
    }
)
@Inheritance(strategy=JOINED)
@NamedQuery(
	name="findBuyerByName",
	query="SELECT OBJECT(buyer) FROM Buyer buyer WHERE buyer.name = :name"
)
@OptimisticLocking(
    type=SELECTED_COLUMNS,
    selectedColumns=@Column(name="VERSION"),
    cascade=false
)
public class Buyer implements Serializable {
    public enum Weekdays { SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY }
    
    public int pre_update_count = 0;
    public int post_update_count = 0;
    public int pre_remove_count = 0;
    public int post_remove_count = 0;
    public int pre_persist_count = 0;
    public int post_persist_count = 0;
    public int post_load_count = 0;
    
    private Integer id;
    private int version;
    private String name;
    private String description;
    private String gender;
    private EnumSet<Weekdays> buyingDays;
    
    private Map<String, Long> creditCards;
    private static final String AMEX = "Amex";
    private static final String DINERS = "DinersClub";
    private static final String MASTERCARD = "Mastercard";
    private static final String VISA = "Visa";

    private Map<String, Long> creditLines;
    private static final String ROYAL_BANK = "RoyalBank";
    private static final String CANADIAN_IMPERIAL = "CanadianImperial";
    private static final String SCOTIABANK = "Scotiabank";
    private static final String TORONTO_DOMINION = "TorontoDominion";
    
    public Buyer() {
        creditCards = new HashMap<String, Long>();
        creditLines = new HashMap<String, Long>();
    }
    
    public void addAmex(long number) {
        getCreditCards().put(AMEX, new Long(number));
    }
    
    public void addCanadianImperialCreditLine(long number) {
        getCreditLines().put(CANADIAN_IMPERIAL, new Long(number));
    }
    
    public void addDinersClub(long number) {
        getCreditCards().put(DINERS, new Long(number));
    }
    
    public void addMastercard(long number) {
        getCreditCards().put(MASTERCARD, new Long(number));
    }
    
    public void addRoyalBankCreditLine(long number) {
        getCreditLines().put(ROYAL_BANK, new Long(number));
    }
    
    public void addScotiabankCreditLine(long number) {
        getCreditLines().put(SCOTIABANK, new Long(number));
    }
    
    public void addTorontoDominionCreditLine(long number) {
        getCreditLines().put(TORONTO_DOMINION, new Long(number));
    }
    
    public void addVisa(long number) {
        getCreditCards().put(VISA, new Long(number));
    }
    
    public boolean buysSaturdayToSunday() {
        if (buyingDays == null) {
            return false;
        } else {
            return buyingDays.equals(EnumSet.of(Weekdays.SATURDAY, Weekdays.SUNDAY));
        }
    }
    
    public String displayString() {
        StringBuffer sbuff = new StringBuffer();
        sbuff.append("Buyer ").append(getId()).append(": ").append(getName()).append(", ").append(getDescription());
        return sbuff.toString();
    }
    
    // No BasicCollection mapping, this should get serialized
    @Column(name="BUY_DAYS")
    public EnumSet<Weekdays> getBuyingDays() {
        return buyingDays;
    }
    
    @BasicMap(
        fetch=EAGER,
        keyColumn=@Column(name="CARD"),
        keyConverter=@Convert("CreditCard"),
        valueColumn=@Column(name="NUMB"),
        valueConverter=@Convert("Long2String")
    )
    @ObjectTypeConverter(
        name="CreditCard",
        conversionValues={
            @ConversionValue(dataValue="VI", objectValue=VISA),
            @ConversionValue(dataValue="AM", objectValue=AMEX),
            @ConversionValue(dataValue="MC", objectValue=MASTERCARD),
            @ConversionValue(dataValue="DI", objectValue=DINERS)
        }
    )
    @PrivateOwned
    // Default the collection table BUYER_CREDITCARDS
    public Map<String, Long> getCreditCards() {
        return creditCards;
    }
    
    @ElementCollection
    @MapKeyColumn(name="BANK")
    @Column(name="ACCOUNT")
    @CollectionTable(
        name="BUYER_CREDITLINES",
        joinColumns=@JoinColumn(name="BUYER_ID")
    )
    @Convert("Long2String")
    @MapKeyConvert("CreditLine")
    @ObjectTypeConverter(
       name="CreditLine",
       conversionValues={
           @ConversionValue(dataValue="RBC", objectValue=ROYAL_BANK),
           @ConversionValue(dataValue="CIBC", objectValue=CANADIAN_IMPERIAL),
           @ConversionValue(dataValue="SB", objectValue=SCOTIABANK),
           @ConversionValue(dataValue="TD", objectValue=TORONTO_DOMINION)
       }
    )
    @PrivateOwned
    public Map<String, Long> getCreditLines() {
        return creditLines;
    }
    
    @Column(name="DESCRIP")
    public String getDescription() { 
        return description; 
    }
    
    @Convert("customSexConverter")
    @Converter(
        name="customSexConverter",
        converterClass=org.eclipse.persistence.testing.models.jpa.advanced.CustomSexConverter.class
    )
    public String getGender() {
        return gender;
    }
    
    @Id
    @GeneratedValue(strategy=SEQUENCE, generator="BUYER_SEQUENCE_GENERATOR")
    @SequenceGenerator(name="BUYER_SEQUENCE_GENERATOR", sequenceName="BUYER_SEQ", allocationSize=10)
    @Column(name="BUYER_ID")
    public Integer getId() { 
        return id; 
    }
    
    @Column(name="BUYER_NAME")
    public String getName() { 
        return name; 
    }
    
    @Version
    @Column(name="VERSION")
    public int getVersion() { 
        return version; 
    }
    
    public boolean hasAmex(long number) {
        return hasCard(creditCards.get(AMEX), number);
    }
    
    public boolean hasCanadianImperialCreditLine(long number) {
        return hasCreditLine(creditLines.get(CANADIAN_IMPERIAL), number);
    }
    
    private boolean hasCard(Long cardNumber, long number) {
        if (cardNumber == null) {
            return false;
        } else {
            return cardNumber.longValue() == number;
        }
    }
    
    private boolean hasCreditLine(Long creditLineNumber, long number) {
        if (creditLineNumber == null) {
            return false;
        } else {
            return creditLineNumber.longValue() == number;
        }
    }
    
    public boolean hasDinersClub(long number) {
        return hasCard(creditCards.get(DINERS), number);
    }
    
    public boolean hasMastercard(long number) {
        return hasCard(creditCards.get(MASTERCARD), number);
    }
    
    public boolean hasRoyalBankCreditLine(long number) {
        return hasCreditLine(creditLines.get(ROYAL_BANK), number);
    }
    
    public boolean hasScotiabankCreditLine(long number) {
        return hasCreditLine(creditLines.get(SCOTIABANK), number);
    }
    
    public boolean hasTorontoDominionCreditLine(long number) {
        return hasCreditLine(creditLines.get(TORONTO_DOMINION), number);
    }
    
    public boolean hasVisa(long number) {
        return hasCard(creditCards.get(VISA), number);
    }
   
    public boolean isFemale() {
        return gender.equals("Female");
    }
    
    public boolean isMale() {
        return gender.equals("Male");
    }
    
    @PostLoad
    public void postLoad() {
        ++post_load_count;
    }
    
    @PostPersist
    public void postPersist() {
        ++post_persist_count;
    }

    @PostRemove
    public void postRemove() {
        ++post_remove_count;
    }
    
    @PostUpdate
    public void postUpdate() {
        ++post_update_count;
    }
    
    @PreRemove
    public void preRemove() {
        ++pre_remove_count;
    }
    
    @PrePersist
    public void prePersist() {
        ++pre_persist_count;
    }

    @PreUpdate
    public void preUpdate() {
        ++pre_update_count;
    }
    
    public void setBuyingDays(EnumSet<Weekdays> buyingDays) {
        this.buyingDays = buyingDays;
    }
    
    public void setCreditCards(Map<String, Long> creditCards) {
        this.creditCards = creditCards;
    }    
    
    protected void setCreditLines(Map<String, Long> creditLines) {
        this.creditLines = creditLines;
    }
    
    public void setDescription(String description) { 
        this.description = description; 
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public void setId(Integer id) { 
        this.id = id; 
    }
    
    public void setName(String name) { 
        this.name = name; 
    }
    
    public void setSaturdayToSundayBuyingDays() {
        this.buyingDays = EnumSet.of(Weekdays.SATURDAY, Weekdays.SUNDAY);
    }
    
    public void setVersion(int version) { 
        this.version = version; 
    }
}
