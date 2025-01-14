package org.eclipse.persistence.testing.models.jpa.advanced.embeddable;

import org.eclipse.persistence.testing.framework.TogglingFastTableCreator;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class EmbeddableTableCreator extends TogglingFastTableCreator {

    public EmbeddableTableCreator() {
        setName("EmbeddableProject");
        addTableDefinition(buildCmp3EmbedVisitorTable());
    }

    public static TableDefinition buildCmp3EmbedVisitorTable() {
        TableDefinition table = createTable("CMP3_EMBED_VISITOR");
        table.addField(createNumericPk("ID"));
        table.addField(createStringColumn("NAME"));
        table.addField(createStringColumn("CODE", 3, true));
        table.addField(createStringColumn("COUNTRY"));
        table.addField(createStringColumn("CONTINENT"));
        table.addField(createStringColumn("NOTE"));
        return table;
    }

}
