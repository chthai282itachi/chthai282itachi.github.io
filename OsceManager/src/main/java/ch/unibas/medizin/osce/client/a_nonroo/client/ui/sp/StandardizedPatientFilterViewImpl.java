package ch.unibas.medizin.osce.client.a_nonroo.client.ui.sp;

import java.util.List;
import java.util.Date;
import java.util.ArrayList;
import java.util.Iterator;

import ch.unibas.medizin.osce.client.i18n.Messages;
import ch.unibas.medizin.osce.client.style.widgets.IconButton;

import com.allen_sauer.gwt.log.client.Log;
import com.gargoylesoftware.htmlunit.AlertHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.datepicker.client.DateBox;


import ch.unibas.medizin.osce.client.a_nonroo.client.SearchCriteria;
import ch.unibas.medizin.osce.client.a_nonroo.client.Comparison;

public class StandardizedPatientFilterViewImpl extends PopupPanel {

	private static StandardizedPatientFilterPopupUiBinder uiBinder = GWT.create(StandardizedPatientFilterPopupUiBinder.class);

	interface StandardizedPatientFilterPopupUiBinder extends
			UiBinder<Widget, StandardizedPatientFilterViewImpl> {
	}
	
	private class CheckBoxItem {
		public CheckBox checkbox;
		public String name;
		
		public CheckBoxItem(CheckBox box, String n) {
			checkbox = box;
			name = n;
		}
	}
	
	private ArrayList<CheckBoxItem> fields = new ArrayList<CheckBoxItem>();
	private int maxApplicableFilters;
	private int minApplicableFilters = 1;
	private int checkedItems = 0;
	
	private SearchCriteria criteria;
	
	@UiField
	FocusPanel filterPanelRoot;
	@UiField
	CheckBox name;
	@UiField
	CheckBox prename;
	@UiField
	CheckBox street;
	@UiField
	CheckBox city;
	@UiField
	CheckBox postalCode;
	@UiField
	CheckBox telephone;
	@UiField
	CheckBox telephone2;
	@UiField
	CheckBox mobile;
	@UiField
	CheckBox email;
	@UiField
	CheckBox bankName;
	@UiField
	CheckBox bankBIC;
	@UiField
	CheckBox bankIBAN;
	@UiField
	CheckBox description;
	
	@UiField
	IconButton resetButton;
	
	@UiField
	ListBox patientGender;
	@UiField
	ListBox haveScars;
	@UiField
	TextBox weightFrom;
	@UiField
	TextBox weightTo;
	@UiField
	TextBox heightFrom;
	@UiField
	TextBox heightTo;
	
	@UiField
	Label patientGenderLabel;
	@UiField
	Label haveScarsLabel;
	@UiField
	Label weightLabel;
	@UiField
	Label heightLabel;
	
	@UiField
	DateBox patientBirthday;
	@UiField
	Label patientBirthdayLabel;
	
	

	@UiHandler("resetButton")
	void onClick(ClickEvent e) {
		Iterator<CheckBoxItem> iter = fields.iterator();
		while(iter.hasNext()) {
			iter.next().checkbox.setValue(false);
		}
		name.setValue(true);
		prename.setValue(true);
		checkedItems = 2;
	}
	
	private class CheckBoxChangeHandler implements ValueChangeHandler<Boolean> {
		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			Iterator<CheckBoxItem> iter = fields.iterator();
			CheckBoxItem e;
			
			int uncheckedBoxes = 0;
			while(iter.hasNext()) {
				e = iter.next();
				if (e.checkbox.getValue() == false) {
					uncheckedBoxes++;
				}
				e.checkbox.setEnabled(true);
			}
			
			if (uncheckedBoxes >= fields.size() - minApplicableFilters) {
				iter = fields.iterator();
				while(iter.hasNext()) {
					e = iter.next();
					if (e.checkbox.getValue())
						e.checkbox.setEnabled(false);
				}
			} else if (fields.size() - uncheckedBoxes >= maxApplicableFilters) {
				iter = fields.iterator();
				while(iter.hasNext()) {
					e = iter.next();
					if (!e.checkbox.getValue())
						e.checkbox.setEnabled(false);
				}
			}
			
			checkedItems = fields.size() - uncheckedBoxes;
			
			String msg = "Searching for: ";
			String filters[] = getFilters();
			for (int i=0; i < filters.length; i++)
				msg = msg + filters[i] + ", ";
			Log.info(msg);
		}
	}
	
	public StandardizedPatientFilterViewImpl() {
		super(true);
		
		criteria = new SearchCriteria();
		
		final StandardizedPatientView view = (StandardizedPatientView)this.getParent();
		
		add(uiBinder.createAndBindUi(this));
		filterPanelRoot.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				updateCriteria();

				// TODO: handle it from view
				//view.updateSearch();
				
				hide();
			}
		});
		
		resetButton.setText(Messages.RESET_FILTERS);
		
		initCheckBox(name, "name", Messages.NAME);
		initCheckBox(prename, "pre_name", Messages.PRENAME);
		initCheckBox(street, "street", Messages.STREET);
		initCheckBox(city, "city", Messages.CITY);
		initCheckBox(postalCode, "postal_code", Messages.PLZ);
		initCheckBox(telephone, "telephone", Messages.TELEPHONE);
		initCheckBox(telephone2, "telephone2", Messages.TELEPHONE + " 2");
		initCheckBox(mobile, "mobile", Messages.MOBILE);
		initCheckBox(email, "email", Messages.EMAIL);
		initCheckBox(bankName, "bank_account.bank_name", Messages.BANK_NAME);
		initCheckBox(bankBIC, "bank_account.bic", Messages.BANK_BIC);
		initCheckBox(bankIBAN, "bank_account.IBAN", Messages.BANK_IBAN);
		initCheckBox(description, "description", Messages.DESCRIPTION);
		
		name.setValue(true);
		prename.setValue(true);
		checkedItems = 2;
		
		maxApplicableFilters = fields.size();
		
		Iterator<CheckBoxItem> fieldIter = fields.iterator();
		while (fieldIter.hasNext()) {
			CheckBox box = fieldIter.next().checkbox;
			box.addValueChangeHandler(new CheckBoxChangeHandler());
		}
		
		/* Advanced Search Items */
		
		// gender  
		
		patientGenderLabel.setText(Messages.GENDER);
				
		patientGender.addItem(Messages.ALL,"-1");
		patientGender.addItem(Messages.MALE,"0");
		patientGender.addItem(Messages.FEMALE,"1");
		
		// gender  
		
		haveScarsLabel.setText(Messages.SCAR);
		
		haveScars.setTitle(Messages.SCAR);
		
		haveScars.addItem(Messages.NO_MATTER,"-1");
		haveScars.addItem(Messages.YES,"1");
		haveScars.addItem(Messages.NO,"0");
		
		// weight
		
		weightLabel.setText(Messages.WEIGHT);
		weightFrom.setTitle(Messages.FROM);
		weightTo.setTitle(Messages.TO);
		
		// height
		
		heightLabel.setText(Messages.HEIGHT);
		heightFrom.setTitle(Messages.FROM);
		heightTo.setTitle(Messages.TO);
		
		// height
		
		patientBirthdayLabel.setText(Messages.BIRTHDAY);
		patientBirthday.setTitle(Messages.BIRTHDAY);
		
	}
	
	private void initCheckBox(CheckBox uiField, String name, String text) {
		uiField.setText(text);
		fields.add(new CheckBoxItem(uiField, name));
	}
	
	/*
	private void initListBox(ListBox uiField, String name, String text) {
		uiField.setText(text);
		fields.add(new ListBoxItem(uiField, name));
	}
	
	private void initFromTo(ListBox uiFieldFrom, ListBox uiFieldTo, String name, String text) {
		uiFieldFrom.setText(Messages.FROM);
		uiFieldTo.setText(Messages.TO);
		fields.add(new CheckBoxItem(uiField, name));
	}
	*/
	
	/**
	 * Sets the maximum number of filters that can be active at once.
	 * @param n
	 */
	public void setMaxApplicableFilters(int n) {
		maxApplicableFilters = n;
	}
	
	/**
	 * Sets the minimum number of filters that must be active at once.
	 * @param n
	 */
	public void setMinApplicableFilters(int n) {
		if (minApplicableFilters > fields.size())
			minApplicableFilters = fields.size();
		minApplicableFilters = n;
	}

	public void updateCriteria() {
		
		criteria.clean();
		
		// scars
		
		if(haveScars.getSelectedIndex() == 1) {
			
			criteria.add("scar",Comparison.EQUALS,"1");
			
		} else if(haveScars.getSelectedIndex() == 2) {
			
			criteria.add("scar",Comparison.EQUALS,"0");
			
		}
		
		// gender
		
		if(patientGender.getSelectedIndex() == 1) {
			
			criteria.add("gender",Comparison.EQUALS,"1");
			
		} else if(patientGender.getSelectedIndex() == 2) {
			
			criteria.add("gender",Comparison.EQUALS,"2");
			
		}
		
		// weight
		Integer wf = null;
		try { wf = Integer.parseInt(weightFrom.getValue()); } catch(Throwable e) {}
		if(wf!=null) criteria.add("weight",Comparison.MORE, wf.toString());
				
		Integer wt = null;
		try { wt = Integer.parseInt(weightTo.getValue()); } catch(Throwable e) {}
		if(wt!=null) criteria.add("weight",Comparison.LESS, wt.toString());
		
		// height
		Integer hf = null;
		try { hf = Integer.parseInt(heightFrom.getValue()); } catch(Throwable e) {}
		if(hf!=null) criteria.add("height",Comparison.MORE, hf.toString());
				
		Integer ht = null;
		try { ht = Integer.parseInt(heightTo.getValue()); } catch(Throwable e) {}
		if(ht!=null) criteria.add("height",Comparison.LESS, ht.toString());
		
		//birthday
		Date d = patientBirthday.getValue();
		

		
	}
	
	public SearchCriteria getCriteria() {
		return criteria;
	}
	
	/**
	 * Returns a string array of all db fields to search in
	 * @return
	 */
	public String[] getFilters() {
		String filters[] = new String[checkedItems];
		int i = 0;
		
		Iterator<CheckBoxItem> fieldIter = fields.iterator();
		while (fieldIter.hasNext()) {
			CheckBoxItem item = fieldIter.next();
			if (item.checkbox.getValue())
				filters[i++] = item.name;
		}
		
		return filters;
	}


}