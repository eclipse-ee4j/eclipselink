package jpql.query;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

@Entity
@NamedQueries
({
   @NamedQuery(name="customer.findAll",   query="SELECT c FROM Customer c"),
   @NamedQuery(name="customer.name",      query="select c.firstName FROM Customer c Group By c.firstName HAVING c.firstName = concat(:fname, :lname)"),
   @NamedQuery(name="customer.substring", query="select count(c) FROM Customer c JOIN c.aliases a GROUP BY a.alias HAVING a.alias = SUBSTRING(:string1, :int1, :int2)"),
   @NamedQuery(name="customer.area",      query="SELECT Distinct Object(c) From Customer c, IN(c.home.phones) p where p.area LIKE :area"),
   @NamedQuery(name="customer.city",      query="SELECT c from Customer c where c.home.city IN :city"),
   @NamedQuery(name="customer.new",       query="SELECT new com.titan.domain.Name(c.firstName, c.lastName) FROM Customer c")
})
@SuppressWarnings("unused")
public class Customer implements Serializable {
	@Column(name="FIRST_NAME")
	private String firstName;
	@Column(name="HAS_GOOD_CREDIT")
	private Long hasGoodCredit;
	@Id
	@Column(nullable = false)
	private Long id;
	@Column(name="LAST_NAME")
	private String lastName;
	@ManyToOne
	@JoinColumn(name = "ADDRESS_ID")
	private Address address;
	@OneToMany(mappedBy = "customer")
	private List<Phone> phoneList;
	@OrderBy
	@OneToMany(mappedBy = "customer")
	private List<Alias> aliases;
	@OneToOne
	private Home home;
	@Transient
	private String title;
	@OneToOne
	private Dept dept;
}