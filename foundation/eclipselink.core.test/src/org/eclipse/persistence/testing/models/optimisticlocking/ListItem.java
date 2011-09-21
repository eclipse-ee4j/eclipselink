package org.eclipse.persistence.testing.models.optimisticlocking;

import java.util.Vector;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.descriptors.VersionLockingPolicy;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class ListItem {

    private int id;
    private String description;
    private int version;
    private ListHolder holder;
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public int getVersion() {
        return version;
    }
    public void setVersion(int version) {
        this.version = version;
    }
    public ListHolder getHolder() {
        return holder;
    }
    public void setHolder(ListHolder holder) {
        this.holder = holder;
    }
    
    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        // SECTION: DESCRIPTOR
        descriptor.setJavaClass(ListItem.class);
        Vector vector = new Vector();
        vector.addElement("OL_ITEM");
        descriptor.setTableNames(vector);
        descriptor.addPrimaryKeyFieldName("OL_ITEM.ID");

        // SECTION: PROPERTIES
        descriptor.setIdentityMapClass(org.eclipse.persistence.internal.identitymaps.FullIdentityMap.class);
        descriptor.setSequenceNumberName("OL_ITEM_SEQ");
        descriptor.setSequenceNumberFieldName("ID");
        descriptor.setExistenceChecking("Check cache");
        descriptor.setIdentityMapSize(100);
        
        VersionLockingPolicy lockingPolicy = new VersionLockingPolicy();
        lockingPolicy.setWriteLockFieldName("OL_ITEM.VERSION");
        lockingPolicy.setIsStoredInCache(false);
        descriptor.setOptimisticLockingPolicy(lockingPolicy);
        
        // SECTION: COPY POLICY
        descriptor.createCopyPolicy("constructor");

        // SECTION: INSTANTIATION POLICY
        descriptor.createInstantiationPolicy("constructor");

        // SECTION: DIRECTTOFIELDMAPPING
        org.eclipse.persistence.mappings.DirectToFieldMapping directtofieldmapping = new org.eclipse.persistence.mappings.DirectToFieldMapping();
        directtofieldmapping.setAttributeName("id");
        directtofieldmapping.setIsReadOnly(false);
        directtofieldmapping.setFieldName("OL_ITEM.ID");
        descriptor.addMapping(directtofieldmapping);
        
        // SECTION: DIRECTTOFIELDMAPPING
        org.eclipse.persistence.mappings.DirectToFieldMapping directtofieldmapping1 = new org.eclipse.persistence.mappings.DirectToFieldMapping();
        directtofieldmapping1.setAttributeName("version");
        directtofieldmapping1.setIsReadOnly(false);
        directtofieldmapping1.setFieldName("OL_ITEM.VERSION");
        descriptor.addMapping(directtofieldmapping1);
        
        org.eclipse.persistence.mappings.DirectToFieldMapping directtofieldmapping2 = new org.eclipse.persistence.mappings.DirectToFieldMapping();
        directtofieldmapping2.setAttributeName("description");
        directtofieldmapping2.setIsReadOnly(false);
        directtofieldmapping2.setFieldName("OL_ITEM.DESCR");
        descriptor.addMapping(directtofieldmapping2);
        
        // SECTION: ONETOONEMAPPING
        org.eclipse.persistence.mappings.OneToOneMapping onetoonemapping = new org.eclipse.persistence.mappings.OneToOneMapping();
        onetoonemapping.setAttributeName("holder");
        onetoonemapping.setIsReadOnly(false);
        onetoonemapping.setUsesIndirection(false);
        onetoonemapping.setReferenceClass(ListHolder.class);
        onetoonemapping.setIsPrivateOwned(false);
        onetoonemapping.addForeignKeyFieldName("OL_ITEM.HOLDER_ID", "OL_HOLDER.ID");
        descriptor.addMapping(onetoonemapping);

        return descriptor;
    }
    
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("OL_ITEM");

        definition.addIdentityField("ID", java.math.BigDecimal.class, 15);
        
        definition.addField("VERSION", Integer.class, 30);
        definition.addField("DESCR", java.lang.String.class);
        definition.addField("HOLDER_ID", java.math.BigDecimal.class, 15);
        definition.addField("ITEM_ORDER", Integer.class, 15);
        
        return definition;
    }
}
