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
 *     09/23/2009-2.0  mobrien 
 *       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)  
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.metamodel;

import static javax.persistence.FetchType.EAGER;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity(name="EnclosureMetamodel")
@Table(name="CMP3_MM_ENCLOSURE")
@IdClass(org.eclipse.persistence.testing.models.jpa.metamodel.EnclosureIdClassPK.class)
public class Enclosure implements java.io.Serializable {

    private static final long serialVersionUID = -8761221910595496687L;
    
    @Id
    @Column(name="TYPE")
    public String type;
    @Id
    @Column(name="LENGTH")
    protected String length;
    @Id
    @Column(name="WIDTH")
    private String width;
    
    // The M:1 side is the owning side
    @Id
    @ManyToOne
    @JoinColumn(name="PERSON_ID")
/*    @JoinTable(name="CMP3_MM_MANUF_MM_ENCLOSURE_MAPUC9", 
            joinColumns = @JoinColumn(name="ENCLOSURE_MAP_ID"), 
            inverseJoinColumns =@JoinColumn(name="MANUF_ID"))*/   
    private Manufacturer mappedManufacturerUC9;
  
/*    @Id
    @GeneratedValue(strategy=TABLE, generator="ENCLOSURE_MM_TABLE_GENERATOR")
    @TableGenerator(
        name="ENCLOSURE_MM_TABLE_GENERATOR", 
        table="CMP3_MM_ENCLOSURE_SEQ", 
        pkColumnName="SEQ_MM_NAME", 
        valueColumnName="SEQ_MM_COUNT",
        pkColumnValue="CUST_MM_SEQ"
    )
    @Column(name="ENCLOSURE_ID")    
    private Integer id;
*/
    
    //@Version
    //@Column(name="ENCLOSURE_VERSION")
    //private int version;

    // The M:1 side is the owning side for "enclosureUC10"
    @ManyToOne(fetch=EAGER)
/*    @JoinTable(name="CMP3_MM_COMPUTER_MM_ENCLOSURE", 
            joinColumns = @JoinColumn(name="ENCLOSURE_ID"), 
            inverseJoinColumns = @JoinColumn(name="COMPUTER_ID"))*/   
    private Computer computerUC10;
    
    public Enclosure() {}

    public EnclosureIdClassPK buildPK(){
        EnclosureIdClassPK pk = new EnclosureIdClassPK();
        pk.setLength(this.getLength());
        pk.setWidth(this.getWidth());
        pk.setType(this.getType());
        pk.setMappedManufacturerUC9(this.getMappedManufacturer().getId());
        return pk;
    }

    @Override
    public boolean equals(Object enclosure) {
        if (enclosure.getClass() != Enclosure.class) {
            return false;
        }        
        return ((Enclosure) enclosure).buildPK().equals(buildPK());
    }
    
    @Override
    public int hashCode() {
        if (null != type && null != length && null != width) {
            return 9232 * type.hashCode() * length.hashCode() * width.hashCode();
        } else {
            return super.hashCode();
        }
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }
    
/*    public int getVersion() { 
        return version; 
    }
    
    protected void setVersion(int version) {
        this.version = version;
    }
*/
/*    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
*/

    public Computer getComputer() {
        return computerUC10;
    }

    public void setComputer(Computer computer) {
        this.computerUC10 = computer;
    }

    
    public Manufacturer getMappedManufacturer() {
        return mappedManufacturerUC9;
    }

    public void setMappedManufacturer(Manufacturer mappedManufacturer) {
        this.mappedManufacturerUC9 = mappedManufacturer;
    }
    
}
