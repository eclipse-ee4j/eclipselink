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
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.relationships;

import javax.persistence.*;

import org.eclipse.persistence.annotations.VariableOneToOne;
import org.eclipse.persistence.annotations.DiscriminatorClass;
import org.eclipse.persistence.annotations.InstantiationCopyPolicy;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.DiscriminatorType.INTEGER;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.TABLE;


@Entity
@Table(name="CMP3_ITEM")
@NamedQuery(
        name="findAllItemsByName",
        query="SELECT OBJECT(item) FROM Item item WHERE item.name = ?1"
)
@InstantiationCopyPolicy // explicitly exercise the code that sets this (even though it is the default)
public class Item implements java.io.Serializable {
    private Integer itemId;
    private int version;
    private String name;
    private String description;
    private Manufacturer manufacturer;
    private Distributor distributor;

    public Item() {}

    @Id
    @GeneratedValue(strategy=TABLE, generator="ITEM_TABLE_GENERATOR")
    @TableGenerator(
            name="ITEM_TABLE_GENERATOR", 
            table="CMP3_CUSTOMER_SEQ", 
            pkColumnName="SEQ_NAME", 
            valueColumnName="SEQ_COUNT",
            pkColumnValue="ITEM_SEQ"
    )
    @Column(name="ID")
    public Integer getItemId() { 
        return itemId; 
    }

    public void setItemId(Integer id) { 
        this.itemId = id; 
    }

    @Version
    @Column(name="ITEM_VERSION")
    protected int getVersion() { 
        return version; 
    }

    protected void setVersion(int version) { 
        this.version = version; 
    }

    public String getDescription() { 
        return description; 
    }

    public void setDescription(String desc) { 
        this.description = desc; 
    }

    public String getName() { 
        return name; 
    }

    public void setName(String name) {
        this.name = name; 
    }

    // The @VariableOneToOne definition purposely left off to test defaulting.
    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    @VariableOneToOne(
            targetInterface=Distributor.class,
            cascade=PERSIST,
            fetch=LAZY,
            discriminatorColumn=@DiscriminatorColumn(name="DISTRIBUTOR_TYPE", discriminatorType=INTEGER),
            discriminatorClasses={
                @DiscriminatorClass(discriminator="1", value=MegaBrands.class),
                @DiscriminatorClass(discriminator="2", value=Namco.class)
            }
    )
    @JoinColumn(name="DISTRIBUTOR_ID", referencedColumnName="distributorId")
    public Distributor getDistributor() {
        return distributor;
    }

    public void setDistributor(Distributor distributor) {
        this.distributor = distributor;
    }
}
