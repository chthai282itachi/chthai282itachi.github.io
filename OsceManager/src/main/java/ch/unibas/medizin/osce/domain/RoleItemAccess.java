package ch.unibas.medizin.osce.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.ManyToMany;
import javax.persistence.PersistenceContext;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooEntity(table = "role_item_access")
public class RoleItemAccess {
	
	@PersistenceContext(unitName="persistenceUnit")
    transient EntityManager entityManager;
	
	@Column(name="name")
	private String name;
	
//	@ManyToMany(cascade = CascadeType.ALL, mappedBy = "roleItemAccess")
//    private Set<RoleTableItem> roleTableItem = new HashSet<RoleTableItem>();
	
	@ManyToMany(/*cascade = CascadeType.ALL,*/ mappedBy = "roleItemAccess")
    private Set<RoleBaseItem> roleBaseItem = new HashSet<RoleBaseItem>();
}
