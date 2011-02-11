package jpql.query;

import java.util.Date;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries
({
   @NamedQuery(name="alias.param1", query="SELECT a.alias FROM Alias AS a WHERE (a.alias IS NULL AND :param1 IS NULL) OR a.alias = :param1"),
   @NamedQuery(name="alias.key",    query="SELECT KEY(k) FROM Alias a JOIN a.ids k"),
   @NamedQuery(name="alias.value1", query="SELECT VALUE(v) FROM Alias a JOIN a.ids v"),
   @NamedQuery(name="alias.value2", query="SELECT v FROM Alias a JOIN a.ids v"),
   @NamedQuery(name="alias.entry",  query="SELECT ENTRY(e) FROM Alias a JOIN a.ids e")
})
public final class Alias
{
	private String alias;
	private Map<String, Date> ids;

	public Alias()
	{
		super();
	}

	public String getAlias()
	{
		return alias;
	}

	public void setAlias(String alias)
	{
		this.alias = alias;
	}
}