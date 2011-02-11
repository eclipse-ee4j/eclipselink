package jpql.query;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;

@Entity
@NamedQuery(name="project.abs", query="SELECT ABS(p.id) FROM Project p")
public final class Project
{
	private String name;
	@Id private int id;
	@ManyToMany
	private List<Employee> employees;

	public Project()
	{
		super();
	}

	public int getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}
}