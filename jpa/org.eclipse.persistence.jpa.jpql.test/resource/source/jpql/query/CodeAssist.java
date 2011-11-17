package jpql.query;

import java.util.Collection;
import java.util.Map;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
@SuppressWarnings("unused")
public class CodeAssist {

	@Basic
	private String name;
	@Id
	private long id;
	@ManyToOne
	private Employee manager;
	@ManyToMany
	private Collection<Employee> employees;
	@OneToMany
	private Map<Customer, String> customerMap;
	@OneToMany
	private Map<Customer, Address> customerMapAddress;
}