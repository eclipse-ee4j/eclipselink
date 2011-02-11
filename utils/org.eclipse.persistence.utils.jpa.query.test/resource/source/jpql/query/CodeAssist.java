package jpql.query;

import java.util.Collection;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.NamedQueries;

@Entity
@NamedQueries
({
	@NamedQuery(name="codeAssist.code_1", query="SELECT c.name FROM CodeAssist c"),
	@NamedQuery(name="codeAssist.code_2", query="SELECT c.manager.name FROM CodeAssist c"),
	@NamedQuery(name="codeAssist.code_3", query="SELECT c.employees. FROM CodeAssist c"),
	@NamedQuery(name="codeAssist.code_4", query="SELECT e. FROM CodeAssist c JOIN c.employees e"),
	@NamedQuery(name="codeAssist.code_5", query="SELECT e. FROM CodeAssist c, IN c.employees e"),
	@NamedQuery(name="codeAssist.code_6", query="SELECT a.alias FROM CodeAssist c, IN c.customerMap cust, IN(KEY(cust).aliases) a")
})
public class CodeAssist
{
	private String name;
	private long id;
	@ManyToOne
	private Employee manager;
	private Collection<Employee> employees;
	private Map<Customer, String> customerMap;
	private Map<Customer, Address> customerMapAddress;
}