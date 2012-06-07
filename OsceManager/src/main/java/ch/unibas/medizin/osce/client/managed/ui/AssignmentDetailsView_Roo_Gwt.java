// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.

package ch.unibas.medizin.osce.client.managed.ui;

import ch.unibas.medizin.osce.client.managed.request.AssignmentProxy;
import ch.unibas.medizin.osce.client.managed.request.DoctorProxy;
import ch.unibas.medizin.osce.client.managed.request.OsceDayProxy;
import ch.unibas.medizin.osce.client.managed.request.OscePostRoomProxy;
import ch.unibas.medizin.osce.client.managed.request.PatientInRoleProxy;
import ch.unibas.medizin.osce.client.managed.request.StudentProxy;
import ch.unibas.medizin.osce.client.scaffold.place.ProxyDetailsView;
import ch.unibas.medizin.osce.client.scaffold.place.ProxyListView;
import ch.unibas.medizin.osce.shared.AssignmentTypes;
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

public abstract class AssignmentDetailsView_Roo_Gwt extends Composite implements ProxyDetailsView<AssignmentProxy> {

    @UiField
    SpanElement id;

    @UiField
    SpanElement version;

    @UiField
    SpanElement type;

    @UiField
    SpanElement slotNumber;

    @UiField
    SpanElement timeStart;

    @UiField
    SpanElement timeEnd;

    @UiField
    SpanElement osceDay;

    @UiField
    SpanElement oscePostRoom;

    @UiField
    SpanElement student;

    @UiField
    SpanElement patientInRole;

    @UiField
    SpanElement examiner;

    AssignmentProxy proxy;

    @UiField
    SpanElement displayRenderer;

    public void setValue(AssignmentProxy proxy) {
        this.proxy = proxy;
        id.setInnerText(proxy.getId() == null ? "" : String.valueOf(proxy.getId()));
        version.setInnerText(proxy.getVersion() == null ? "" : String.valueOf(proxy.getVersion()));
        type.setInnerText(proxy.getType() == null ? "" : String.valueOf(proxy.getType()));
        slotNumber.setInnerText(proxy.getSlotNumber() == null ? "" : String.valueOf(proxy.getSlotNumber()));
        timeStart.setInnerText(proxy.getTimeStart() == null ? "" : DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_MEDIUM).format(proxy.getTimeStart()));
        timeEnd.setInnerText(proxy.getTimeEnd() == null ? "" : DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_MEDIUM).format(proxy.getTimeEnd()));
        osceDay.setInnerText(proxy.getOsceDay() == null ? "" : ch.unibas.medizin.osce.client.managed.ui.OsceDayProxyRenderer.instance().render(proxy.getOsceDay()));
        oscePostRoom.setInnerText(proxy.getOscePostRoom() == null ? "" : ch.unibas.medizin.osce.client.managed.ui.OscePostRoomProxyRenderer.instance().render(proxy.getOscePostRoom()));
        student.setInnerText(proxy.getStudent() == null ? "" : ch.unibas.medizin.osce.client.managed.ui.StudentProxyRenderer.instance().render(proxy.getStudent()));
        patientInRole.setInnerText(proxy.getPatientInRole() == null ? "" : ch.unibas.medizin.osce.client.managed.ui.PatientInRoleProxyRenderer.instance().render(proxy.getPatientInRole()));
        examiner.setInnerText(proxy.getExaminer() == null ? "" : ch.unibas.medizin.osce.client.managed.ui.DoctorProxyRenderer.instance().render(proxy.getExaminer()));
        displayRenderer.setInnerText(AssignmentProxyRenderer.instance().render(proxy));
    }
}
