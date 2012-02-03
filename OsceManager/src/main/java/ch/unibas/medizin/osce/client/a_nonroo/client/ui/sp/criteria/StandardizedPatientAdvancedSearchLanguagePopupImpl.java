package ch.unibas.medizin.osce.client.a_nonroo.client.ui.sp.criteria;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import ch.unibas.medizin.osce.client.i18n.Messages;
import ch.unibas.medizin.osce.client.managed.request.LangSkillProxy;
import ch.unibas.medizin.osce.client.managed.request.SpokenLanguageProxy;
import ch.unibas.medizin.osce.client.style.widgets.IconButton;
import ch.unibas.medizin.osce.domain.LangSkill;
import ch.unibas.medizin.osce.shared.LangSkillLevel;
import ch.unibas.medizin.osce.shared.PossibleFields;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.text.shared.AbstractRenderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.Widget;

public class StandardizedPatientAdvancedSearchLanguagePopupImpl extends PopupPanel 
	implements StandardizedPatientAdvancedSearchLanguagePopup {

	private static StandardizedPatientAdvancedSearchLanguagePopupImplUiBinder uiBinder = GWT
			.create(StandardizedPatientAdvancedSearchLanguagePopupImplUiBinder.class);

	interface StandardizedPatientAdvancedSearchLanguagePopupImplUiBinder
			extends
			UiBinder<Widget, StandardizedPatientAdvancedSearchLanguagePopupImpl> {
	}
	
	@UiField
	IconButton addLanguageButton;
	@UiField
	IconButton closeBoxButton;
	@UiField
	IconButton languageButton;
	
	@UiField (provided=true)
	ValueListBox<SpokenLanguageProxy> language = new ValueListBox<SpokenLanguageProxy>(new AbstractRenderer<SpokenLanguageProxy>() {
        public String render(SpokenLanguageProxy obj) {
            return obj == null ? "" : String.valueOf(obj.getLanguageName());
        }
    });
	
	@UiField (provided=true)
	ValueListBox<LangSkillLevel> skill = new ValueListBox<LangSkillLevel>(new AbstractRenderer<LangSkillLevel>() {
        public String render(LangSkillLevel obj) {
            return obj == null ? "" : String.valueOf(obj.toString());
        }
    });

	public StandardizedPatientAdvancedSearchLanguagePopupImpl() {
		setWidget(uiBinder.createAndBindUi(this));
		skill.setAcceptableValues(Arrays.asList(LangSkillLevel.values()));
		addLanguageButton.setText(Messages.ADD);
		languageButton.setText(Messages.LANGUAGES);
	}
	
	@UiHandler("addLanguageButton")
	public void addLanguageButtonClicked(ClickEvent event) {
		// TODO delegate.clickedyclick();
		delegate.addLanguageButtonClicked(language.getValue(), skill.getValue());
		this.hide();
	}

	@UiHandler("closeBoxButton")
	public void closeBoxButtonClicked(ClickEvent event) {
		this.hide();
	}
	
	@UiHandler("languageButton")
	public void languageButtonClicked(ClickEvent event) {
		this.hide();
	}
	
	private Delegate delegate;
	@Override
	public void setDelegate(Delegate delegate) {
		this.delegate = delegate;
	}

	@Override
	public void display(Button addLanguage) {
		this.show();
		this.setPopupPosition(addLanguage.getAbsoluteLeft() - 5, addLanguage.getAbsoluteTop() - getOffsetHeight()/2 - 4);
	}

	@Override
	public ValueListBox<SpokenLanguageProxy> getLanguage() {
		return language;
	}

	@Override
	public void setLanguagePickerValues(List<SpokenLanguageProxy> values) {
		language.setAcceptableValues(values);
	}
}
