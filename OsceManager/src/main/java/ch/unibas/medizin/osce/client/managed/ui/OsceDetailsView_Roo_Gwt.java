// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.

package ch.unibas.medizin.osce.client.managed.ui;

import ch.unibas.medizin.osce.client.managed.request.OsceDayProxy;
import ch.unibas.medizin.osce.client.managed.request.OscePostBlueprintProxy;
import ch.unibas.medizin.osce.client.managed.request.OsceProxy;
import ch.unibas.medizin.osce.client.managed.request.SemesterProxy;
import ch.unibas.medizin.osce.client.managed.request.StudentOscesProxy;
import ch.unibas.medizin.osce.client.managed.request.TaskProxy;
import ch.unibas.medizin.osce.client.scaffold.place.ProxyDetailsView;
import ch.unibas.medizin.osce.client.scaffold.place.ProxyListView;
import ch.unibas.medizin.osce.shared.OsceStatus;
import ch.unibas.medizin.osce.shared.StudyYears;
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

public abstract class OsceDetailsView_Roo_Gwt extends Composite implements ProxyDetailsView<OsceProxy> {

    @UiField
    SpanElement id;

    @UiField
    SpanElement version;

    @UiField
    SpanElement studyYear;

    @UiField
    SpanElement maxNumberStudents;

    @UiField
    SpanElement name;

    @UiField
    SpanElement shortBreak;

    @UiField
    SpanElement shortBreakSimpatChange;

    @UiField
    SpanElement longBreak;

    @UiField
    SpanElement lunchBreak;

    @UiField
    SpanElement middleBreak;

    @UiField
    SpanElement numberPosts;

    @UiField
    SpanElement numberCourses;

    @UiField
    SpanElement postLength;

    @UiField
    SpanElement isRepeOsce;

    @UiField
    SpanElement numberRooms;

    @UiField
    SpanElement isValid;

    @UiField
    SpanElement osceStatus;

    @UiField
    SpanElement semester;

    @UiField
    SpanElement osce_days;

    @UiField
    SpanElement oscePostBlueprints;

    @UiField
    SpanElement tasks;

    @UiField
    SpanElement osceStudents;

    @UiField
    SpanElement copiedOsce;

    OsceProxy proxy;

    @UiField
    SpanElement displayRenderer;

    public void setValue(OsceProxy proxy) {
        this.proxy = proxy;
        id.setInnerText(proxy.getId() == null ? "" : String.valueOf(proxy.getId()));
        version.setInnerText(proxy.getVersion() == null ? "" : String.valueOf(proxy.getVersion()));
        studyYear.setInnerText(proxy.getStudyYear() == null ? "" : String.valueOf(proxy.getStudyYear()));
        maxNumberStudents.setInnerText(proxy.getMaxNumberStudents() == null ? "" : String.valueOf(proxy.getMaxNumberStudents()));
        name.setInnerText(proxy.getName() == null ? "" : String.valueOf(proxy.getName()));
        shortBreak.setInnerText(proxy.getShortBreak() == null ? "" : String.valueOf(proxy.getShortBreak()));
        shortBreakSimpatChange.setInnerText(proxy.getShortBreakSimpatChange() == null ? "" : String.valueOf(proxy.getShortBreakSimpatChange()));
        longBreak.setInnerText(proxy.getLongBreak() == null ? "" : String.valueOf(proxy.getLongBreak()));
        lunchBreak.setInnerText(proxy.getLunchBreak() == null ? "" : String.valueOf(proxy.getLunchBreak()));
        middleBreak.setInnerText(proxy.getMiddleBreak() == null ? "" : String.valueOf(proxy.getMiddleBreak()));
        numberPosts.setInnerText(proxy.getNumberPosts() == null ? "" : String.valueOf(proxy.getNumberPosts()));
        numberCourses.setInnerText(proxy.getNumberCourses() == null ? "" : String.valueOf(proxy.getNumberCourses()));
        postLength.setInnerText(proxy.getPostLength() == null ? "" : String.valueOf(proxy.getPostLength()));
        isRepeOsce.setInnerText(proxy.getIsRepeOsce() == null ? "" : String.valueOf(proxy.getIsRepeOsce()));
        numberRooms.setInnerText(proxy.getNumberRooms() == null ? "" : String.valueOf(proxy.getNumberRooms()));
        isValid.setInnerText(proxy.getIsValid() == null ? "" : String.valueOf(proxy.getIsValid()));
        osceStatus.setInnerText(proxy.getOsceStatus() == null ? "" : String.valueOf(proxy.getOsceStatus()));
        semester.setInnerText(proxy.getSemester() == null ? "" : ch.unibas.medizin.osce.client.managed.ui.SemesterProxyRenderer.instance().render(proxy.getSemester()));
        osce_days.setInnerText(proxy.getOsce_days() == null ? "" : ch.unibas.medizin.osce.client.scaffold.place.CollectionRenderer.of(ch.unibas.medizin.osce.client.managed.ui.OsceDayProxyRenderer.instance()).render(proxy.getOsce_days()));
        oscePostBlueprints.setInnerText(proxy.getOscePostBlueprints() == null ? "" : ch.unibas.medizin.osce.client.scaffold.place.CollectionRenderer.of(ch.unibas.medizin.osce.client.managed.ui.OscePostBlueprintProxyRenderer.instance()).render(proxy.getOscePostBlueprints()));
        tasks.setInnerText(proxy.getTasks() == null ? "" : ch.unibas.medizin.osce.client.scaffold.place.CollectionRenderer.of(ch.unibas.medizin.osce.client.managed.ui.TaskProxyRenderer.instance()).render(proxy.getTasks()));
        osceStudents.setInnerText(proxy.getOsceStudents() == null ? "" : ch.unibas.medizin.osce.client.scaffold.place.CollectionRenderer.of(ch.unibas.medizin.osce.client.managed.ui.StudentOscesProxyRenderer.instance()).render(proxy.getOsceStudents()));
        copiedOsce.setInnerText(proxy.getCopiedOsce() == null ? "" : ch.unibas.medizin.osce.client.managed.ui.OsceProxyRenderer.instance().render(proxy.getCopiedOsce()));
        displayRenderer.setInnerText(OsceProxyRenderer.instance().render(proxy));
    }
}
