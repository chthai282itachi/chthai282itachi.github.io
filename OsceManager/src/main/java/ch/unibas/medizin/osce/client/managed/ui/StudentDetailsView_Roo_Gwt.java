// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.

package ch.unibas.medizin.osce.client.managed.ui;

import ch.unibas.medizin.osce.client.managed.request.StudentOscesProxy;
import ch.unibas.medizin.osce.client.managed.request.StudentProxy;
import ch.unibas.medizin.osce.client.scaffold.place.ProxyDetailsView;
import ch.unibas.medizin.osce.client.scaffold.place.ProxyListView;
import ch.unibas.medizin.osce.shared.Gender;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
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
import java.util.Set;

public abstract class StudentDetailsView_Roo_Gwt extends Composite implements ProxyDetailsView<StudentProxy> {

    @UiField
    SpanElement id;

    @UiField
    SpanElement version;

    @UiField
    SpanElement gender;

    @UiField
    SpanElement name;

    @UiField
    SpanElement preName;

    @UiField
    SpanElement email;

    @UiField
    SpanElement studentOsces;

    StudentProxy proxy;

    @UiField
    SpanElement displayRenderer;

    public void setValue(StudentProxy proxy) {
        this.proxy = proxy;
        id.setInnerText(proxy.getId() == null ? "" : String.valueOf(proxy.getId()));
        version.setInnerText(proxy.getVersion() == null ? "" : String.valueOf(proxy.getVersion()));
        gender.setInnerText(proxy.getGender() == null ? "" : String.valueOf(proxy.getGender()));
        name.setInnerText(proxy.getName() == null ? "" : String.valueOf(proxy.getName()));
        preName.setInnerText(proxy.getPreName() == null ? "" : String.valueOf(proxy.getPreName()));
        email.setInnerText(proxy.getEmail() == null ? "" : String.valueOf(proxy.getEmail()));
        studentOsces.setInnerText(proxy.getStudentOsces() == null ? "" : ch.unibas.medizin.osce.client.scaffold.place.CollectionRenderer.of(ch.unibas.medizin.osce.client.managed.ui.StudentOscesProxyRenderer.instance()).render(proxy.getStudentOsces()));
        displayRenderer.setInnerText(StudentProxyRenderer.instance().render(proxy));
    }
}
