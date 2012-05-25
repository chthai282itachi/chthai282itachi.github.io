// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.

package ch.unibas.medizin.osce.client.managed.request;

import ch.unibas.medizin.osce.shared.ItemDefination;
import com.google.gwt.requestfactory.shared.EntityProxy;
import com.google.gwt.requestfactory.shared.ProxyForName;
import java.util.List;
import java.util.Set;
import org.springframework.roo.addon.gwt.RooGwtMirroredFrom;

@RooGwtMirroredFrom("ch.unibas.medizin.osce.domain.RoleBaseItem")
@ProxyForName("ch.unibas.medizin.osce.domain.RoleBaseItem")
public interface RoleBaseItemProxy extends EntityProxy {

    abstract Long getId();

    abstract void setId(Long id);

    abstract Integer getVersion();

    abstract void setVersion(Integer version);

    abstract ItemDefination getItem_defination();

    abstract void setItem_defination(ItemDefination item_defination);

    abstract String getItem_name();

    abstract void setItem_name(String item_name);

    abstract Boolean getDeleted();

    abstract void setDeleted(Boolean deleted);

    abstract RoleTemplateProxy getRoleTemplate();

    abstract void setRoleTemplate(RoleTemplateProxy roleTemplate);

    abstract Integer getSort_order();

    abstract void setSort_order(Integer sort_order);

    abstract List<ch.unibas.medizin.osce.client.managed.request.RoleTableItemProxy> getRoleTableItem();

    abstract void setRoleTableItem(List<RoleTableItemProxy> roleTableItem);

    abstract Set<ch.unibas.medizin.osce.client.managed.request.RoleItemAccessProxy> getRoleItemAccess();

    abstract void setRoleItemAccess(Set<RoleItemAccessProxy> roleItemAccess);
}
