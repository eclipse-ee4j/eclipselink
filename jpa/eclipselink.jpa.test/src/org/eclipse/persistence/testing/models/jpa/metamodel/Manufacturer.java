/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     06/30/2009-2.0  mobrien - finish JPA Metadata API modifications in support
 *       of the Metamodel implementation for EclipseLink 2.0 release involving
 *       Map, ElementCollection and Embeddable types on MappedSuperclass descriptors
 *       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)  
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.metamodel;

import static javax.persistence.CascadeType.ALL;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.BasicCollection;

// retain name attribute for testing
@Entity(name="ManuMetamodel")
@Table(name="CMP3_MM_MANUF")
public class Manufacturer extends Corporation implements java.io.Serializable{
    private static final long serialVersionUID = 5796354087505114955L;

    @Version
    @Column(name="MANUF_VERSION")
    private int version;

    // 322166 test case
    @BasicCollection(valueColumn=@Column(name="PARAM"))
    private Collection<String> paramCollection;
    
    // If a JoinTable with a JoinColumn is used - then we need a mappedBy on the inverse side here
    @OneToMany(cascade=ALL, mappedBy="manufacturer")
    private Set<Computer> computers = new HashSet<Computer>();

    // If a JoinTable with a JoinColumn is used - then we need a mappedBy on the inverse side here
    @OneToMany(cascade=ALL, mappedBy="employer")
    private List<HardwareDesigner> hardwareDesigners = new ArrayList<HardwareDesigner>();
    
    // If a JoinTable with a JoinColumn is used - then we need a mappedBy on the inverse side here
    //@OneToMany(cascade=ALL, mappedBy="manufacturer")
    //private Collection<SoftwareDesigner> softwareDesigners = new HashSet<SoftwareDesigner>();

    // If a JoinTable with a JoinColumn is used - then we need a mappedBy on the inverse side here
    @OneToMany(cascade=ALL, mappedBy="mappedEmployer")
    private Map<String, HardwareDesigner> hardwareDesignersMap;// = new HashMap<String, HardwareDesigner>();
    
    // The following MapAttribute use cases are referenced in Design Issue 63
    // http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_63:_20090824:_Add_Map_support_for_.40MapKey_to_MapAttribute
    // UC 1a: Generics KV set, no @MapKey present, PK is singular field
    @OneToMany(cascade=ALL, mappedBy="mappedEmployerUC1a")
    private Map<String, HardwareDesigner> hardwareDesignersMapUC1a;
    
    // UC 2: Generics KV set, @MapKey is present
    @OneToMany(cascade=ALL, mappedBy="mappedEmployerUC2")
    @MapKey(name="name")
    private Map<String, HardwareDesigner> hardwareDesignersMapUC2;
    
    // UC 4: No Generics KV set, @MapKey is present
    @OneToMany(targetEntity=HardwareDesigner.class, cascade=ALL, mappedBy="mappedEmployerUC4")
    @MapKey(name="name")
    private Map hardwareDesignersMapUC4;
    
    // UC 6: No Generics KV set, no targetEntity set, @MapKey is *(set/unset)
    // INVALID
    //@OneToMany(cascade=ALL, mappedBy="mappedEmployerUC6")
    //private Map hardwareDesignersMapUC6;
    
    // UC 7: Generics KV set, targetEntity is also set, @MapKey is *(set/unset)
    // Same as UC1a - that is missing the @MapKey
    @OneToMany(targetEntity=HardwareDesigner.class, cascade=ALL, mappedBy="mappedEmployerUC7")
    private Map<String, HardwareDesigner> hardwareDesignersMapUC7;
    
    // UC8: no targetEntity, MapKey uses name default
    @OneToMany(cascade=ALL, mappedBy="mappedEmployerUC8")
    @MapKey // name attribute will default to "id"
    private Map<Integer, HardwareDesigner> hardwareDesignersMapUC8;
    
    // UC9: no targetEntity, no MapKey, but generics are set (MapKey has an IdClass with an Embeddable)
    @OneToMany(cascade=CascadeType.ALL, mappedBy="mappedManufacturerUC9")
    private Map<Board, Enclosure> enclosureByBoardMapUC9;
    
    // UC12:  mapKey defined via generics and is a java class defined as an IdClass on the element(value) class 
    
    // Define Uppercase Object non-java.lang Basic types
    //private Object anObject; // Not supported in JPA
    private Boolean aBooleanObject;
    private Byte aByteObject;
    private Short aShortObject;    
    private Integer anIntegerObject;
    private Long aLongObject;
    private BigInteger aBigIntegerObject;    
    private BigDecimal aBigDecimalObject;    
    private Float aFloatObject;
    private Double aDoubleObject;
    private Character aCharacterObject;
    //private Enum anEnum;
    
    
    // Define lowercase primitive non-java.lang un-boxed Basic types
    private boolean aBoolean;
    private byte aByte;
    private short aShort;    
    private int anInt;
    private long aLong;
    private float aFloat;
    private double aDouble;
    private char aChar;
    //public enum anEnum { one, two, three};
    
    // Define static types
    
    public Manufacturer() {
    }

    public int getVersion() { 
        return version; 
    }
    
    protected void setVersion(int version) {
        this.version = version;
    }


    public Collection<Computer> getComputers() { 
        return computers; 
    }
    
    public void setComputers(Set<Computer> newValue) { 
        this.computers = newValue; 
    }

    public void addComputer(Computer aComputer) {
        getComputers().add(aComputer);
        aComputer.setManufacturer(this);
    }

    public void removeComputer(Computer aComputer) {
        getComputers().remove(aComputer);
        aComputer.setManufacturer(null);
    }

    public List<HardwareDesigner> getHardwareDesigners() {
        return hardwareDesigners;
    }

    public void setHardwareDesigners(List<HardwareDesigner> designers) {
        this.hardwareDesigners = designers;
    }

    public Map<String, HardwareDesigner> getHardwareDesignersMap() {
        return hardwareDesignersMap;
    }

    public void setHardwareDesignersMap(Map<String, HardwareDesigner> hardwareDesignersMap) {
        this.hardwareDesignersMap = hardwareDesignersMap;
    }

    public void addHardwareDesignerToMap(HardwareDesigner aDesigner) {
        this.getHardwareDesignersMap().put(aDesigner.getName(), aDesigner);
        aDesigner.setMappedEmployer(this);
    }

    public void removeHardwareDesignerFromMapr(HardwareDesigner aDesigner) {
        this.getHardwareDesignersMap().remove(aDesigner.getName());
        aDesigner.setMappedEmployer(null);
    }

    public Map<String, HardwareDesigner> getHardwareDesignersMapUC1a() {
        return hardwareDesignersMapUC1a;
    }

    public void addHardwareDesignerToMapUC1a(HardwareDesigner aDesigner) {
        this.getHardwareDesignersMapUC1a().put(aDesigner.getName(), aDesigner);
        aDesigner.setMappedEmployerUC1a(this);
    }

    public void setHardwareDesignersMapUC1a(
            Map<String, HardwareDesigner> hardwareDesignersMapUC1a) {
        this.hardwareDesignersMapUC1a = hardwareDesignersMapUC1a;
    }

    public Map<String, HardwareDesigner> getHardwareDesignersMapUC2() {
        return hardwareDesignersMapUC2;
    }

    public void addHardwareDesignerToMapUC2(HardwareDesigner aDesigner) {
        this.getHardwareDesignersMapUC2().put(aDesigner.getName(), aDesigner);
        aDesigner.setMappedEmployerUC2(this);
    }

    public void setHardwareDesignersMapUC2(
            Map<String, HardwareDesigner> hardwareDesignersMapUC2) {
        this.hardwareDesignersMapUC2 = hardwareDesignersMapUC2;
    }

    public Map getHardwareDesignersMapUC4() {
        return hardwareDesignersMapUC4;
    }

    public void addHardwareDesignerToMapUC4(HardwareDesigner aDesigner) {
        this.getHardwareDesignersMapUC4().put(aDesigner.getName(), aDesigner);
        aDesigner.setMappedEmployerUC4(this);
    }

    public void setHardwareDesignersMapUC4(Map hardwareDesignersMapUC4) {
        this.hardwareDesignersMapUC4 = hardwareDesignersMapUC4;
    }

    public Map<String, HardwareDesigner> getHardwareDesignersMapUC7() {
        return hardwareDesignersMapUC7;
    }

    public void addHardwareDesignerToMapUC7(HardwareDesigner aDesigner) {
        this.getHardwareDesignersMapUC7().put(aDesigner.getName(), aDesigner);
        aDesigner.setMappedEmployerUC7(this);
    }

    public void setHardwareDesignersMapUC7(
            Map<String, HardwareDesigner> hardwareDesignersMapUC7) {
        this.hardwareDesignersMapUC7 = hardwareDesignersMapUC7;
    }

    public Map<Integer, HardwareDesigner> getHardwareDesignersMapUC8() {
        return hardwareDesignersMapUC8;
    }

    public void setHardwareDesignersMapUC8(
            Map<Integer, HardwareDesigner> hardwareDesignersMapUC8) {
        this.hardwareDesignersMapUC8 = hardwareDesignersMapUC8;
    }

    /*
    // Not supported in JPA
    public Object getAnObject() {
        return anObject;
    }

    // Not suppoted in JPA
    public void setAnObject(Object anObject) {
        this.anObject = anObject;
    }*/

    public Boolean getaBooleanObject() {
        return aBooleanObject;
    }

    public void setaBooleanObject(Boolean aBooleanObject) {
        this.aBooleanObject = aBooleanObject;
    }

    public Byte getaByteObject() {
        return aByteObject;
    }

    public void setaByteObject(Byte aByteObject) {
        this.aByteObject = aByteObject;
    }

    public Short getaShortObject() {
        return aShortObject;
    }

    public void setaShortObject(Short aShortObject) {
        this.aShortObject = aShortObject;
    }

    public Integer getAnIntegerObject() {
        return anIntegerObject;
    }

    public void setAnIntegerObject(Integer anIntegerObject) {
        this.anIntegerObject = anIntegerObject;
    }

    public Long getaLongObject() {
        return aLongObject;
    }

    public void setaLongObject(Long aLongObject) {
        this.aLongObject = aLongObject;
    }

    public BigInteger getaBigIntegerObject() {
        return aBigIntegerObject;
    }

    public void setaBigIntegerObject(BigInteger aBigIntegerObject) {
        this.aBigIntegerObject = aBigIntegerObject;
    }

    public Float getaFloatObject() {
        return aFloatObject;
    }

    public void setaFloatObject(Float aFloatObject) {
        this.aFloatObject = aFloatObject;
    }

    public Double getaDoubleObject() {
        return aDoubleObject;
    }

    public void setaDoubleObject(Double aDoubleObject) {
        this.aDoubleObject = aDoubleObject;
    }

    public Character getaCharacterObject() {
        return aCharacterObject;
    }

    public void setaCharacterObject(Character aCharacterObject) {
        this.aCharacterObject = aCharacterObject;
    }

    public boolean isaBoolean() {
        return aBoolean;
    }

    public void setaBoolean(boolean aBoolean) {
        this.aBoolean = aBoolean;
    }

    public byte getaByte() {
        return aByte;
    }

    public void setaByte(byte aByte) {
        this.aByte = aByte;
    }

    public short getaShort() {
        return aShort;
    }

    public void setaShort(short aShort) {
        this.aShort = aShort;
    }

    public int getAnInt() {
        return anInt;
    }

    public void setAnInt(int anInt) {
        this.anInt = anInt;
    }

    public long getaLong() {
        return aLong;
    }

    public void setaLong(long aLong) {
        this.aLong = aLong;
    }

    public float getaFloat() {
        return aFloat;
    }

    public void setaFloat(float aFloat) {
        this.aFloat = aFloat;
    }

    public double getaDouble() {
        return aDouble;
    }

    public void setaDouble(double aDouble) {
        this.aDouble = aDouble;
    }

    public char getaChar() {
        return aChar;
    }

    public void setaChar(char aChar) {
        this.aChar = aChar;
    }

    public BigDecimal getaBigDecimalObject() {
        return aBigDecimalObject;
    }

    public void setaBigDecimal(BigDecimal aBigDecimalObject) {
        this.aBigDecimalObject = aBigDecimalObject;
    }
    
    public Map<Board, Enclosure> getEnclosureByBoardMapUC9() {
        return enclosureByBoardMapUC9;
    }

    public void setEnclosureByBoardMapUC9(Map<Board, Enclosure> enclosureByBoardMapUC9) {
        this.enclosureByBoardMapUC9 = enclosureByBoardMapUC9;
    }


}
