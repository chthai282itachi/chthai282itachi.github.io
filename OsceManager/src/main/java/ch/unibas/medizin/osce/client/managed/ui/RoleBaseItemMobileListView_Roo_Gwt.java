// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.

package ch.unibas.medizin.osce.client.managed.ui;

import ch.unibas.medizin.osce.client.managed.request.RoleBaseItemProxy;
import ch.unibas.medizin.osce.client.managed.request.RoleItemAccessProxy;
import ch.unibas.medizin.osce.client.managed.request.RoleSubItemValueProxy;
import ch.unibas.medizin.osce.client.managed.request.RoleTableItemProxy;
import ch.unibas.medizin.osce.client.managed.request.RoleTemplateProxy;
import ch.unibas.medizin.osce.client.scaffold.ScaffoldMobileApp;
import ch.unibas.medizin.osce.client.scaffold.ui.MobileProxyListView;
import ch.unibas.medizin.osce.shared.ItemDefination;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.client.DateTimeFormatRenderer;
import com.google.gwt.text.shared.AbstractRenderer;
import com.google.gwt.text.shared.AbstractSafeHtmlRenderer;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class RoleBaseItemMobileListView_Roo_Gwt extends MobileProxyListView<RoleBaseItemProxy> {

    protected Set<String> paths = new HashSet<String>();

    public RoleBaseItemMobileListView_Roo_Gwt(String buttonText, SafeHtmlRenderer<ch.unibas.medizin.osce.client.managed.request.RoleBaseItemProxy> renderer) {
        super(buttonText, renderer);
    }

    public void init() {
        paths.add("item_name");
        paths.add("item_defination");
    }
}
