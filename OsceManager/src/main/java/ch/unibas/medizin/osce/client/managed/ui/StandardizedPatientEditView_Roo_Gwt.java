// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.

package ch.unibas.medizin.osce.client.managed.ui;

import ch.unibas.medizin.osce.client.managed.activity.StandardizedPatientEditActivityWrapper;
import ch.unibas.medizin.osce.client.managed.activity.StandardizedPatientEditActivityWrapper.View;
import ch.unibas.medizin.osce.client.managed.request.AnamnesisFormProxy;
import ch.unibas.medizin.osce.client.managed.request.BankaccountProxy;
import ch.unibas.medizin.osce.client.managed.request.DescriptionProxy;
import ch.unibas.medizin.osce.client.managed.request.LangSkillProxy;
import ch.unibas.medizin.osce.client.managed.request.NationalityProxy;
import ch.unibas.medizin.osce.client.managed.request.ProfessionProxy;
import ch.unibas.medizin.osce.client.managed.request.StandardizedPatientProxy;
import ch.unibas.medizin.osce.client.scaffold.place.ProxyEditView;
import ch.unibas.medizin.osce.client.scaffold.ui.*;
import ch.unibas.medizin.osce.shared.Gender;
import ch.unibas.medizin.osce.shared.MaritalStatus;
import ch.unibas.medizin.osce.shared.WorkPermission;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.requestfactory.client.RequestFactoryEditorDriver;
import com.google.gwt.requestfactory.shared.RequestFactory;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.AbstractRenderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.LongBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.datepicker.client.DateBox;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public abstract class StandardizedPatientEditView_Roo_Gwt extends Composite implements View<StandardizedPatientEditView> {

    @UiField(provided = true)
    ValueListBox<Gender> gender = new ValueListBox<Gender>(new AbstractRenderer<ch.unibas.medizin.osce.shared.Gender>() {

        public String render(ch.unibas.medizin.osce.shared.Gender obj) {
            return obj == null ? "" : String.valueOf(obj);
        }
    });

    @UiField
    TextBox name;

    @UiField
    TextBox preName;

    @UiField
    TextBox street;

    @UiField
    TextBox city;

    @UiField
    IntegerBox postalCode;

    @UiField
    TextBox telephone;

    @UiField
    TextBox telephone2;

    @UiField
    TextBox mobile;

    @UiField
    IntegerBox height;

    @UiField
    IntegerBox weight;

    @UiField
    TextBox immagePath;

    @UiField
    TextBox videoPath;

    @UiField(provided = true)
    ValueListBox<NationalityProxy> nationality = new ValueListBox<NationalityProxy>(ch.unibas.medizin.osce.client.managed.ui.NationalityProxyRenderer.instance(), new com.google.gwt.requestfactory.ui.client.EntityProxyKeyProvider<ch.unibas.medizin.osce.client.managed.request.NationalityProxy>());

    @UiField(provided = true)
    ValueListBox<ProfessionProxy> profession = new ValueListBox<ProfessionProxy>(ch.unibas.medizin.osce.client.managed.ui.ProfessionProxyRenderer.instance(), new com.google.gwt.requestfactory.ui.client.EntityProxyKeyProvider<ch.unibas.medizin.osce.client.managed.request.ProfessionProxy>());

    @UiField
    DateBox birthday;

    @UiField
    TextBox email;

    @UiField(provided = true)
    ValueListBox<DescriptionProxy> descriptions = new ValueListBox<DescriptionProxy>(ch.unibas.medizin.osce.client.managed.ui.DescriptionProxyRenderer.instance(), new com.google.gwt.requestfactory.ui.client.EntityProxyKeyProvider<ch.unibas.medizin.osce.client.managed.request.DescriptionProxy>());

    @UiField(provided = true)
    ValueListBox<BankaccountProxy> bankAccount = new ValueListBox<BankaccountProxy>(ch.unibas.medizin.osce.client.managed.ui.BankaccountProxyRenderer.instance(), new com.google.gwt.requestfactory.ui.client.EntityProxyKeyProvider<ch.unibas.medizin.osce.client.managed.request.BankaccountProxy>());

    @UiField(provided = true)
    ValueListBox<MaritalStatus> maritalStatus = new ValueListBox<MaritalStatus>(new AbstractRenderer<ch.unibas.medizin.osce.shared.MaritalStatus>() {

        public String render(ch.unibas.medizin.osce.shared.MaritalStatus obj) {
            return obj == null ? "" : String.valueOf(obj);
        }
    });

    @UiField(provided = true)
    ValueListBox<WorkPermission> workPermission = new ValueListBox<WorkPermission>(new AbstractRenderer<ch.unibas.medizin.osce.shared.WorkPermission>() {

        public String render(ch.unibas.medizin.osce.shared.WorkPermission obj) {
            return obj == null ? "" : String.valueOf(obj);
        }
    });

    @UiField
    TextBox socialInsuranceNo;

    @UiField(provided = true)
    ValueListBox<AnamnesisFormProxy> anamnesisForm = new ValueListBox<AnamnesisFormProxy>(ch.unibas.medizin.osce.client.managed.ui.AnamnesisFormProxyRenderer.instance(), new com.google.gwt.requestfactory.ui.client.EntityProxyKeyProvider<ch.unibas.medizin.osce.client.managed.request.AnamnesisFormProxy>());

    @UiField
    LangSkillSetEditor langskills;

    public void setProfessionPickerValues(Collection<ProfessionProxy> values) {
        profession.setAcceptableValues(values);
    }

    public void setMaritalStatusPickerValues(Collection<MaritalStatus> values) {
        maritalStatus.setAcceptableValues(values);
    }

    public void setLangskillsPickerValues(Collection<LangSkillProxy> values) {
        langskills.setAcceptableValues(values);
    }

    public void setDescriptionsPickerValues(Collection<DescriptionProxy> values) {
        descriptions.setAcceptableValues(values);
    }

    public void setBankAccountPickerValues(Collection<BankaccountProxy> values) {
        bankAccount.setAcceptableValues(values);
    }

    public void setGenderPickerValues(Collection<Gender> values) {
        gender.setAcceptableValues(values);
    }

    public void setAnamnesisFormPickerValues(Collection<AnamnesisFormProxy> values) {
        anamnesisForm.setAcceptableValues(values);
    }

    public void setWorkPermissionPickerValues(Collection<WorkPermission> values) {
        workPermission.setAcceptableValues(values);
    }

    public void setNationalityPickerValues(Collection<NationalityProxy> values) {
        nationality.setAcceptableValues(values);
    }
}
