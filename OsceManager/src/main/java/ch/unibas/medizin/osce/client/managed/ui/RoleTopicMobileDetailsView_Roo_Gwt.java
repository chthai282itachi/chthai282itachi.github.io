// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.

package ch.unibas.medizin.osce.client.managed.ui;

import ch.unibas.medizin.osce.client.managed.request.OscePostBlueprintProxy;
import ch.unibas.medizin.osce.client.managed.request.RoleTopicProxy;
import ch.unibas.medizin.osce.client.managed.request.SpecialisationProxy;
import ch.unibas.medizin.osce.client.managed.request.StandardizedRoleProxy;
import ch.unibas.medizin.osce.client.scaffold.place.ProxyDetailsView;
import ch.unibas.medizin.osce.shared.StudyYears;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.List;
import java.util.Set;

public abstract class RoleTopicMobileDetailsView_Roo_Gwt extends Composite implements ProxyDetailsView<RoleTopicProxy> {

    @UiField
    Element id;

    @UiField
    Element version;

    @UiField
    Element name;

    @UiField
    Element description;

    @UiField
    Element studyYear;

    @UiField
    Element slotsUntilChange;

    @UiField
    Element standardizedRoles;

    @UiField
    Element specialisation;

    @UiField
    Element oscePostBlueprints;

    RoleTopicProxy proxy;

    public void setValue(RoleTopicProxy proxy) {
        this.proxy = proxy;
        id.setInnerText(proxy.getId() == null ? "" : String.valueOf(proxy.getId()));
        version.setInnerText(proxy.getVersion() == null ? "" : String.valueOf(proxy.getVersion()));
        name.setInnerText(proxy.getName() == null ? "" : String.valueOf(proxy.getName()));
        description.setInnerText(proxy.getDescription() == null ? "" : String.valueOf(proxy.getDescription()));
        studyYear.setInnerText(proxy.getStudyYear() == null ? "" : String.valueOf(proxy.getStudyYear()));
        slotsUntilChange.setInnerText(proxy.getSlotsUntilChange() == null ? "" : String.valueOf(proxy.getSlotsUntilChange()));
        standardizedRoles.setInnerText(proxy.getStandardizedRoles() == null ? "" : ch.unibas.medizin.osce.client.scaffold.place.CollectionRenderer.of(ch.unibas.medizin.osce.client.managed.ui.StandardizedRoleProxyRenderer.instance()).render(proxy.getStandardizedRoles()));
        specialisation.setInnerText(proxy.getSpecialisation() == null ? "" : ch.unibas.medizin.osce.client.managed.ui.SpecialisationProxyRenderer.instance().render(proxy.getSpecialisation()));
        oscePostBlueprints.setInnerText(proxy.getOscePostBlueprints() == null ? "" : ch.unibas.medizin.osce.client.scaffold.place.CollectionRenderer.of(ch.unibas.medizin.osce.client.managed.ui.OscePostBlueprintProxyRenderer.instance()).render(proxy.getOscePostBlueprints()));
    }
}
