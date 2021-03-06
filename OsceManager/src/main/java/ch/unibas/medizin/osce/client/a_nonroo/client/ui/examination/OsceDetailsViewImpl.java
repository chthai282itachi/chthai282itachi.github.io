/**
 * 
 */
package ch.unibas.medizin.osce.client.a_nonroo.client.ui.examination;




import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.unibas.medizin.osce.client.a_nonroo.client.ui.renderer.EnumRenderer;
import ch.unibas.medizin.osce.client.managed.request.AdministratorProxy;
import ch.unibas.medizin.osce.client.managed.request.OsceProxy;
import ch.unibas.medizin.osce.client.managed.request.OsceSettingsProxy;
import ch.unibas.medizin.osce.client.managed.request.TaskProxy;
import ch.unibas.medizin.osce.client.style.resources.MyCellTableResources;
import ch.unibas.medizin.osce.client.style.widgets.IconButton;
import ch.unibas.medizin.osce.client.style.widgets.TabPanelHelper;
import ch.unibas.medizin.osce.client.style.widgets.cell.IconCell;
import ch.unibas.medizin.osce.shared.BucketInfoType;
import ch.unibas.medizin.osce.shared.OsMaConstant;
import ch.unibas.medizin.osce.shared.OsceCreationType;
import ch.unibas.medizin.osce.shared.StudyYears;
import ch.unibas.medizin.osce.shared.i18n.OsceConstants;
import ch.unibas.medizin.osce.shared.i18n.OsceConstantsWithLookup;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.AbstractRenderer;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author dk
 *
 */
public class OsceDetailsViewImpl extends Composite implements  OsceDetailsView{

	private static OsceDetailsViewImplUiBinder uiBinder = GWT
			.create(OsceDetailsViewImplUiBinder.class);

	interface OsceDetailsViewImplUiBinder extends
	UiBinder<Widget, OsceDetailsViewImpl> {
	}

	@UiField
	Label labelLongNameHeader;

	int left=0,top=0;

	
	private Delegate delegate;
	
	OsceSettingsProxy osceSettingsProxy;
	
	OsceConstantsWithLookup enumConstants = GWT.create(OsceConstantsWithLookup.class);
	

	/**
	 * Because this class has a default constructor, it can
	 * be used as a binder template. In other words, it can be used in other
	 * *.ui.xml files as follows:
	 * <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder"
	 *   xmlns:g="urn:import:**user's package**">
	 *  <g:**UserClassName**>Hello!</g:**UserClassName>
	 * </ui:UiBinder>
	 * Note that depending on the widget that is used, it may be necessary to
	 * implement HasHTML instead of HasText.
	 */
	public OsceDetailsViewImpl() {
		CellTable.Resources tableResources = GWT.create(MyCellTableResources.class);
		table = new CellTable<TaskProxy>(OsMaConstant.TABLE_PAGE_SIZE, tableResources);

		initWidget(uiBinder.createAndBindUi(this));
		
		osceDetailPanel.selectTab(0);
		osceDetailPanel.getTabBar().setTabText(0, constants.manageOsces());
		osceDetailPanel.getTabBar().setTabText(1, constants.osceSettings());
		TabPanelHelper.moveTabBarToBottom(osceDetailPanel);
		
		
		filterPanel = new OsceTaskPopViewImpl();
		filterPanel.addCloseHandler(new CloseHandler<PopupPanel>() {
		
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				Log.info("filter panel close");
				/*if (filterPanel.selectionChanged()) {
					filterPanel.clearSelectionChanged();
					//delegate.performSearch(searchBox.getValue(), getSearchFilters());
					delegate.performSearch(searchBox.getValue(), getSearchFilters(),getTableFilters(),getWhereFilters());
				}*/
			}
			
		});
		
		labelLongNameHeader.setText(constants.manageOsces());
		
		/*labelOsce labelStudyYear labelIsRepetion labelRepetitionForOsce labelMaxStudents 
		labelMaxCircuits  labelMaxRooms labelStationLength labelShortBreak labelShortBreakSPChange labelLunchBreak
		labelLongBreak labelMediumBreak*/
		
		labelTitleGeneral.setInnerText(constants.general());
		labelTitleAttributes.setInnerText(constants.attributes());
		labelTitleBreaks.setInnerText(constants.breaks());
		
		labelRemark.setInnerText(constants.remark());
		labelStudyYear.setInnerText(constants.studyYears());
		labelIsRepetion.setInnerText(constants.osceIsRepe());
		labelRepetitionForOsce.setInnerText(constants.osceRepe());
		labelMaxStudents.setInnerText(constants.osceMaxStudents());
		labelMaxCircuits.setInnerText(constants.osceMaxCircuits());
		labelMaxRooms.setInnerText(constants.osceMaxRooms());
		labelStationLength.setInnerText(constants.osceStationLength());
		labelShortBreak.setInnerText(constants.osceShortBreak());
		labelShortBreakSPChange.setInnerText(constants.osceSimpatsInShortBreak());
		labelLunchBreak.setInnerText(constants.osceLunchBreak());
		labelLongBreak.setInnerText(constants.osceLongBreak());
		labelMediumBreak.setInnerText(constants.osceMediumBreak());
		labelLunchBreakRequiredTime.setInnerText(constants.osceLunchBreakRequiredFiled());
		labelLOngBreakRequiredTime.setInnerText(constants.osceLongBreakRequiredFiled());
		labelOsceCreationType.setInnerText(constants.osceCreationType());
		
		labelTitleSettings.setInnerText(constants.osceSettings());
		lblBackUpPeriod.setInnerText(constants.backUpPeriod());
		lblBucketName.setInnerText(constants.bucketName());
		lblUnit.setInnerText(constants.timeUnit());
		lblPointNxtExaminee.setInnerText(constants.pointNxtExaminee());
		lblEncryptionType.setInnerText(constants.encryptionType());
		lblSymmetricKey.setInnerText(constants.symmetricKey());
		//lblExamReviewMode.setInnerText(constants.examReviewMode());
		lblPassword.setInnerText(constants.password());
		lblSettingPaasword.setInnerText(constants.settingPassword());
		labelOtherInfo.setInnerText(constants.otherInformation());
		lblUsername.setInnerText(constants.userName());
		lblHost.setInnerText(constants.host());
		lblScreenSaverText.setInnerText(constants.osceScreenSaverText());
		lblScreenSaverTime.setInnerText(constants.screenSaverTime());
		lblAutoSelection.setInnerText(constants.autoSelection());
		labelBucketType.setInnerText(constants.bucketType());
		exportSettingsQRCode.setText(constants.exportSettingsQRCode());
		exportXml.setText(constants.exportSettingsXml());
		newButton.setText(constants.osceAddTask());
		labelIsFormativeOsce.setInnerText(constants.isFormativeOsce());
		
		init();
		
		/*deadline.getTextBox().setReadOnly(true);
		deadline.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				// TODO Auto-generated method stub
				

				Date today = new Date();
				Date futureDate=new Date();
				futureDate.setYear(today.getYear()+2);
				
				Date date = new Date();
				Date d=new Date();
			//	Calendar cal = Calendar.getInstance();
				d.setYear(date.getYear()+2);
			if(event.getValue().before(today))
			{
				Window.alert("Date should be past date");
				deadline.setValue(null);
			}
			if(event.getValue().after(futureDate))
			{
				Window.alert("Date should be not allowed after 2 year");
				deadline.setValue(null);
			}
			}

			
		});*/
	}

	private OsceConstants constants = GWT.create(OsceConstants.class);
	
	@UiField
	public IconButton edit;
	
	
	@UiField
	public IconButton delete;

	@UiField
	Image arrow;
	
	@UiField
	DisclosurePanel osceDisclosurePanel;
	
	@UiField
	SpanElement labelTitleGeneral;
	
	@UiField
	SpanElement labelTitleSettings;

	@UiField
	SpanElement labelTitleAttributes;
	
	@UiField
	SpanElement labelOtherInfo;

	@UiField
	SpanElement labelTitleBreaks;
	
	@UiField
	SpanElement name;

	@UiField
	SpanElement studyYear;
	
	@UiField
	SpanElement isRepetion;
	
	@UiField
	SpanElement repetitionForOsce;
	
	@UiField
	SpanElement maxStud;

	@UiField
	SpanElement maxNumberStudents;
	
	@UiField
	SpanElement maxRooms;
	

	@UiField
	SpanElement postLength;

	@UiField
	SpanElement shortBreak;

	@UiField
	SpanElement LongBreak;

	@UiField
	SpanElement lunchBreak;

	@UiField
	SpanElement MiddleBreak;
	
	@UiField
	SpanElement shortBreakSPChange;
	
	
	
	@UiField
	SpanElement longBreakRequiredTime;
	
	@UiField
	SpanElement lunchBreakRequiredTime;
	
	
	
	@UiField
	TabPanel osceDetailPanel;
	
	@UiField (provided = true)
	CellTable<TaskProxy> table;
	
	
	@UiField 
	SpanElement labelRemark;
	@UiField
	SpanElement labelStudyYear;
	
	@UiField
	SpanElement labelIsRepetion;
	
	@UiField
	SpanElement labelRepetitionForOsce;
	
	@UiField
	SpanElement labelMaxStudents;
	@UiField
	SpanElement labelMaxCircuits;
	
	@UiField
	SpanElement labelMaxRooms;
	
	
	@UiField
	SpanElement labelStationLength;
	
	
	@UiField
	SpanElement labelShortBreak;
	
	@UiField
	SpanElement labelShortBreakSPChange;
	
	
	@UiField
	SpanElement labelLunchBreak;
	@UiField
	SpanElement labelLongBreak;
	@UiField
	SpanElement labelMediumBreak;
	
	
	@UiField
	SpanElement labelLunchBreakRequiredTime;
	
	@UiField
	SpanElement labelLOngBreakRequiredTime;
	
	@UiField
	SpanElement labelOsceCreationType;
	
	@UiField
	SpanElement osceCreationType;
	
	//Settings Tab fields start
	
	@UiField
	SpanElement labelBucketType;
	
	@UiField
	SpanElement bucketType;
	
	@UiField
	SpanElement lblHost;
	
	@UiField
	SpanElement host;
	
	@UiField
	SpanElement lblUsername;
	
	@UiField
	SpanElement username;
	
	@UiField
	SpanElement lblBucketName;
	
	@UiField
	SpanElement bucketName;
	
	@UiField
	SpanElement lblBackUpPeriod;
	
	@UiField
	SpanElement backupPeriod;
	
	
	@UiField
	SpanElement lblPointNxtExaminee;
	
	@UiField
	SpanElement pointNxtExaminee;
	
	@UiField
	SpanElement lblEncryptionType;
	
	@UiField
	SpanElement encryptionType;
	
	@UiField
	SpanElement lblSymmetricKey;
	
	@UiField
	SpanElement symmetricKey;
	
	/*@UiField
	SpanElement lblExamReviewMode;
	
	@UiField
	SpanElement examMode;*/
	
	@UiField
	SpanElement lblUnit;
	
	@UiField
	SpanElement unit;
	
	@UiField
	SpanElement lblSettingPaasword;
	
	@UiField
	SpanElement settingPassword;
	
	@UiField
	SpanElement lblPassword;
	
	@UiField
	SpanElement password;
	
	@UiField
	IconButton exportSettingsQRCode;

	@UiField
	IconButton exportXml;
	
	@UiField
	SpanElement lblScreenSaverText;
	
	@UiField
	SpanElement screenSaverText;
	
	@UiField
	SpanElement lblScreenSaverTime;
	
	@UiField
	SpanElement screenSaverTime;
	
	@UiField
	SpanElement lblAutoSelection;
	
	@UiField
	SpanElement autoSelection;
	
	@UiField
	SpanElement labelIsFormativeOsce;
	
	@UiField
	SpanElement isFormativeOsce;
	/* @UiField
	    DateBox deadline;

	@UiField
	TextBox taskName;
	
	@UiField
	Button save;
	*/
	public Boolean isedit;
	
	/*@UiField
	public IconButton filterButton;*/
	
	@UiField
	public IconButton newButton;
	
	private OsceTaskPopViewImpl  filterPanel;

	/*@UiField(provided = true)
	ValueListBox<AdministratorProxy> administrator = new ValueListBox<AdministratorProxy>(ch.unibas.medizin.osce.client.managed.ui.AdministratorProxyRenderer.instance(), new com.google.gwt.requestfactory.ui.client.EntityProxyKeyProvider<ch.unibas.medizin.osce.client.managed.request.AdministratorProxy>());
*/
	
	 
	 protected Set<String> paths = new HashSet<String>();
	
	OsceProxy proxy;
	TaskProxy editProxy;
	
	List<TaskProxy> l ;
	
	/*shortBreak
	LongBreak
	lunchBreak*/

	
	@UiHandler("newButton")
	public void newButtonclick(ClickEvent event) {
//System.out.println("Mouse over");
		Log.info("filter panel call");
			showFilterPanel((Widget) event.getSource());
	}
	
	private void showFilterPanel(Widget eventSource) {
		int x = eventSource.getAbsoluteLeft();
		int y = eventSource.getAbsoluteTop();
		filterPanel.setPopupPosition(x, y);
		filterPanel.show();
		//Issue # 122 : Replace pull down with autocomplete.
		//filterPanel.administrator.setRenderer(new AdministratorProxyRenderer());
		filterPanel.administrator.setRenderer(new AbstractRenderer<AdministratorProxy>() {

			@Override
			public String render(AdministratorProxy object) {
				// TODO Auto-generated method stub
				if(object!=null)
				{
				return object.getName();
				}
				else
				{
					return "";
				}
			}
		});
		filterPanel.administrator.setSelected(null);
		//Issue # 122 : Replace pull down with autocomplete.
		filterPanel.deadline.setValue(null);
		filterPanel.taskName.setValue(null);
		//Log.info(filterPanel.getSpecialisationBox().getValue());
	}
	
	public void init()
	{
		isedit=false;
		filterPanel.isedit=false;
		
		table.addDomHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) 
			{
				left=event.getClientX();
				top=event.getClientY();
				
			}
		}, ClickEvent.getType());
		
		/*deadline.getDatePicker().addShowRangeHandler(new ShowRangeHandler<Date>() {
			
			@Override
			public void onShowRange(ShowRangeEvent<Date> event) {
				
				
				event.fire(event.getSource(), new Date(), new Date(2013, 3, 25));
				
			}
		});*/
		
		
		/*deadline.getDatePicker().addShowRangeHandler(new ShowRangeHandler<Date>()
				{
				    @Override
				    public void onShowRange(final ShowRangeEvent<Date> dateShowRangeEvent)
				    {
				        if(deadline.getDatePicker().isVisible())
				        {
				    	Date today = new Date(2012, 8, 5);
				        
				            deadline.getDatePicker().setTransientEnabledOnDates(false, today);
				        }
				        
				    }
				});
	*/
		
		paths.add("name");
		table.addColumn(new TextColumn<TaskProxy>() {
			{ this.setSortable(true); }
	
			Renderer<java.lang.String> renderer = new AbstractRenderer<java.lang.String>() {
	
				public String render(java.lang.String obj) {
					return obj == null ? "" : String.valueOf(obj);
				}
			};
	
			@Override
			public String getValue(TaskProxy object) {
				if(object==null || object.getName()==null)
				{
					return renderer.render("");
				}
				else
				{
					String s=""+object.getName();
					return renderer.render(s);
				}
			}
		}, constants.osceTaskName());
		
		paths.add("deadliine");
		table.addColumn(new TextColumn<TaskProxy>() {
			{ this.setSortable(true); }
	
			Renderer<java.lang.String> renderer = new AbstractRenderer<java.lang.String>() {
	
				public String render(java.lang.String obj) {
					return obj == null ? "" : String.valueOf(obj);
				}
			};
	
			@Override
			public String getValue(TaskProxy object) {
				
				if(object==null || object.getDeadline()==null)
				{
					return renderer.render("");
				}
				else
				{

		           
		             String s =""+object.getDeadline().getDate()+"."+object.getDeadline().getMonth()+"."+(object.getDeadline().getYear()+1900);
					  System.out.println("date--"+s);
					return renderer.render(s);
				}
			}
		}, constants.osceTaskDeadline());
		
		Column<TaskProxy, Boolean> checkColumn = new Column<TaskProxy, Boolean>(new IsDoneCell()) {
			@Override
		    public Boolean getValue(TaskProxy object) {
		    	// Get the value from the selection model.
		    	return object.getIsDone(); 
		    }
		};
		table.addColumn(checkColumn,constants.osceTaskDone());
		
		
		addColumn(new ActionCell<TaskProxy>(
				OsMaConstant.EDIT_ICON, new ActionCell.Delegate<TaskProxy>() {
					public void execute(TaskProxy task) {
						//Window.alert("You clicked " + institution.getInstitutionName());
						
							editClicked(task);
					}
	
					
				}), "", new GetValue<TaskProxy>() {
			public TaskProxy getValue(TaskProxy scar) {
				return scar;
			}		
		}, null);
		
		addColumn(new ActionCell<TaskProxy>(
				OsMaConstant.DELETE_ICON, new ActionCell.Delegate<TaskProxy>() {
					public void execute(final TaskProxy task) {
						//Window.alert("You clicked " + institution.getInstitutionName());
						final MessageConfirmationDialogBox valueUpdateDialogBox=new MessageConfirmationDialogBox(constants.warning());
						valueUpdateDialogBox.showYesNoDialog(constants.reallyDelete());
						valueUpdateDialogBox.getYesBtn().addClickHandler(new ClickHandler() {
							
							@Override
							public void onClick(ClickEvent event) {
								delegate.deleteClicked(task);
								valueUpdateDialogBox.hide();
								
							}
						});
						
						valueUpdateDialogBox.getNoBtnl().addClickHandler(new ClickHandler() {
							
							@Override
							public void onClick(ClickEvent event) {
								valueUpdateDialogBox.hide();
								
								
							}
						});

						/*if(Window.confirm(constants.reallyDelete())) {
							delegate.deleteClicked(task);
						}*/
					}	
				}), "", new GetValue<TaskProxy>() {
			public TaskProxy getValue(TaskProxy scar) {
				return scar;
			}
		}, null);
		
		table.addColumnStyleName(2, "iconCol");
		table.addColumnStyleName(3, "iconCol");
		table.addColumnStyleName(4, "iconCol");
			
		//table.addColumn(new StatusColumn(), constants.answered());
		
		
	    
	  // Checkbox column. This table will uses a checkbox column for selection.
	  // Alternatively, you can call cellTable.setSelectionEnabled(true) to enable
	  // mouse selection.
		
		//ProvidesKey<TaskProxy> keyProvider = ((AbstractHasData<TaskProxy>) table).getKeyProvider();
		
	//	final SelectionModel<TaskProxy> selectionModel=new MultiSelectionModel<TaskProxy>(keyProvider);
		
		//table.setSelectionModel(selectionModel, DefaultSelectionEventManager.<TaskProxy> createCheckboxManager());
	}
	
	private class IsDoneCell extends CheckboxCell {
		public IsDoneCell() {
			super(true, false);
		}
		
		@Override
		public void onBrowserEvent(final Context context, Element parent, Boolean value, 
		      NativeEvent event, ValueUpdater<Boolean> valueUpdater) {
			if(((TaskProxy)context.getKey()).getIsDone()==true) {
				 super.onBrowserEvent(context, parent, true, event, valueUpdater);
				 this.setValue(context, parent, true);
				
		  //  Log.info("checkBox Clicked "+((TaskProxy)context.getKey()).getIsDone());
		   // SafeHtmlBuilder sb=new SafeHtmlBuilder();
		   // sb.append(SafeHtmlUtils.fromSafeConstant("<input type=\"checkbox\" tabindex=\"-1\" checked/>"));
		//    this.render(context, value, sb);
		    
			} else {
				this.setValue(context, parent, false);
				 super.onBrowserEvent(context, parent, value, event, valueUpdater);
				 final MessageConfirmationDialogBox valueUpdateDialogBox=new MessageConfirmationDialogBox(constants.warning());
					valueUpdateDialogBox.showYesNoDialog("Do you really want to change it active?");
					valueUpdateDialogBox.getYesBtn().addClickHandler(new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							 delegate.editForDone((TaskProxy)context.getKey());
							valueUpdateDialogBox.hide();
							
						}
					});
					
					valueUpdateDialogBox.getNoBtnl().addClickHandler(new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							valueUpdateDialogBox.hide();
							
							
						}
					});


				 
				
				
				 Log.info("checkBox Clicked "+((TaskProxy)context.getKey()).getIsDone());
				   // SafeHtmlBuilder sb=new SafeHtmlBuilder();
				   // sb.append(SafeHtmlUtils.fromSafeConstant("<input type=\"checkbox\" tabindex=\"-1\" checked/>"));
				 //   this.render(context, true, sb);
			 }
		  }
		 
		 @Override
		public void render(com.google.gwt.cell.client.Cell.Context context,
				Boolean value, SafeHtmlBuilder sb) {
			// TODO Auto-generated method stub
			//super.render(context, value, sb);
			if(((TaskProxy)context.getKey()).getIsDone()==true) {
				// ((TaskProxy)context.getKey()).setIsDone(true);
				super.render(context, true, sb);
			}
			else {
				super.render(context, value, sb);
			}
		}
	};
	
	public void editClicked(TaskProxy task)
	{
		isedit=true;
		
		/*taskName.setText(task.getName());
		deadline.setValue(task.getDeadline());
		administrator.setValue(task.getAdministrator());*/
		editProxy=task;
		
		filterPanel.isedit=true;
		filterPanel.taskName.setText(task.getName());
		filterPanel.deadline.setValue(task.getDeadline());
		//Issue # 122 : Replace pull down with autocomplete.
		//filterPanel.administrator.setRenderer(new AdministratorProxyRenderer());
		filterPanel.administrator.setRenderer(new AbstractRenderer<AdministratorProxy>() {

			@Override
			public String render(AdministratorProxy object) {
				// TODO Auto-generated method stub
				if(object!=null)
				{
				return object.getName();
				}
				else
				{
					return "";
				}
				
			}
		});
		filterPanel.administrator.setSelected(task.getAdministrator());
		//Issue # 122 : Replace pull down with autocomplete.
		filterPanel.editProxy=task;
		//filterPanel.show();
		
		Log.info("filterPanel width : " + filterPanel.getOffsetWidth());
		filterPanel.setPopupPosition(left-400, top);		
		filterPanel.show();
		
	}
	
	private <C> void addColumn(Cell<C> cell, String headerText,
			final GetValue<C> getter, FieldUpdater<TaskProxy, C> fieldUpdater) {
		Column<TaskProxy, C> column = new Column<TaskProxy, C>(cell) {
			@Override
			public C getValue(TaskProxy object) {
				return getter.getValue(object);
			}
		};
		column.setFieldUpdater(fieldUpdater);
		if (cell instanceof AbstractEditableCell<?, ?>) {
			editableCells.add((AbstractEditableCell<?, ?>) cell);
		}
		table.addColumn(column, headerText);
	}
	private static interface GetValue<C> {
		C getValue(TaskProxy contact);
	}

	private List<AbstractEditableCell<?, ?>> editableCells;
	
	
	public String[] getPaths() {
		return paths.toArray(new String[paths.size()]);
	}

	
	//spec
private class StatusColumn extends Column<TaskProxy, Integer> {
		
		public StatusColumn() {
			super(new IconCell(new String[] {"closethick", "check"}, new String[] {constants.answerPending(), constants.answerGiven()}));
		}
		
		@Override
		public Integer getValue(TaskProxy proxy) {
			boolean questionAnswered = !(proxy.getIsDone() == null);
			return (questionAnswered) ? 0 : 1;
		}
	}
	//spec
	
	@Override
	public void setValue(OsceProxy proxy) {
		this.proxy = proxy;
		
		if(proxy==null)
		{
			Log.info("No osce value");
			return;
		}

		/*name studyYear isRepetion repetitionForOsce maxStud
		maxNumberStudents maxRooms postLength shortBreak
		LongBreak lunchBreak MiddleBreak shortBreakSPChange*/
		
		
		if(proxy.getName()=="")
		{
			name.setInnerText("");
		}
		else
		{
			name.setInnerText(proxy.getName() == null ? "" : String.valueOf(proxy.getName()));
		}
		
		
		studyYear.setInnerText(new EnumRenderer<StudyYears>().render(proxy.getStudyYear()));
		
		if(proxy.getIsRepeOsce()==null || !proxy.getIsRepeOsce()) {
			isRepetion.setInnerHTML(OsMaConstant.UNCHECK_ICON.asString());
		} else {
			isRepetion.setInnerHTML(OsMaConstant.CHECK_ICON.asString());
		}
		
		if(proxy.getCopiedOsce()==null) {
			Log.info("osce null--");
			labelRepetitionForOsce.setInnerText("");
			repetitionForOsce.setInnerText("");
		} else {
			Log.info("osce null else--"+proxy.getCopiedOsce().getName());
			repetitionForOsce.setInnerText(proxy.getCopiedOsce().getName() == null ? "" : proxy.getCopiedOsce().getName());
		}
		
		if (OsceCreationType.Automatic.equals(proxy.getOsceCreationType()))
		{
			if(proxy.getMaxNumberStudents()==null)
			{
				maxStud.setInnerText("");
				
			}
			else
			{
				maxStud.setInnerText(proxy.getMaxNumberStudents() == 0 ? "" : String.valueOf(proxy.getMaxNumberStudents()));
			}
			
			
			if(proxy.getNumberCourses()==null)
			{
				maxNumberStudents.setInnerText("");
				
			}
			else
			{
			maxNumberStudents.setInnerText(proxy.getNumberCourses() == 0 ? "" : String.valueOf(proxy.getNumberCourses()));
			}
			
			
			if(proxy.getNumberRooms()==null)
			{
				maxRooms.setInnerText("");
				
			}
			else
			{
				maxRooms.setInnerText(proxy.getNumberRooms() == 0 ? "" : String.valueOf(proxy.getNumberRooms()));
			}
		}
		else if (OsceCreationType.Manual.equals(proxy.getOsceCreationType()))
		{
			Document.get().getElementById("maxNumberTd").removeFromParent();
			Document.get().getElementById("maxNumber").removeFromParent();
			Document.get().getElementById("maxStudentId").removeFromParent();
			Document.get().getElementById("maxStudent").removeFromParent();
			Document.get().getElementById("maxRoomId").removeFromParent();
			Document.get().getElementById("maxRoom").removeFromParent();
		}		
		
		if(proxy.getPostLength()==null)
		{
			postLength.setInnerText("");
			
		}
		else
		{
			postLength.setInnerText(proxy.getPostLength() == 0 ? "" : String.valueOf(proxy.getPostLength()));
		}
	
		
		if(proxy.getShortBreak()==null)
		{
			shortBreak.setInnerText("");
			
		}
		else
		{
		shortBreak.setInnerText(proxy.getShortBreak() == 0 ? "" : String.valueOf(proxy.getShortBreak()));
		}
		
		if(proxy.getShortBreakSimpatChange()==null)
		{
			shortBreakSPChange.setInnerText("");
			
		}
		else
		{
			shortBreakSPChange.setInnerText(proxy.getShortBreakSimpatChange() == 0 ? "" : String.valueOf(proxy.getShortBreakSimpatChange()));
		}
		
		if(proxy.getMiddleBreak()==null)
		{
			MiddleBreak.setInnerText("");
			
		}
		else
		{
			MiddleBreak.setInnerText(proxy.getMiddleBreak() == 0 ? "" : String.valueOf(proxy.getMiddleBreak()));
		}
		
		if(proxy.getLongBreak()==null)
		{
			LongBreak.setInnerText("");
			
		}
		else
		{
		LongBreak.setInnerText(proxy.getLongBreak() == 0 ? "" : String.valueOf(proxy.getLongBreak()));
		}
		if(proxy.getLunchBreak()==null)
		{
			lunchBreak.setInnerText("");
			
		}
		else
		{
		lunchBreak.setInnerText(proxy.getLunchBreak() == 0 ? "" : String.valueOf(proxy.getLunchBreak()));
		}
		
		if(proxy.getLunchBreakRequiredTime()==null)
		{
			lunchBreakRequiredTime.setInnerText("");
			
		}
		else
		{
			lunchBreakRequiredTime.setInnerText(proxy.getLunchBreakRequiredTime() == 0 ? "" : String.valueOf(proxy.getLunchBreakRequiredTime()));
		}
		
		if(proxy.getLongBreakRequiredTime()==null)
		{
			longBreakRequiredTime.setInnerText("");
			
		}
		else
		{
		longBreakRequiredTime.setInnerText(proxy.getLongBreakRequiredTime() == 0 ? "" : String.valueOf(proxy.getLongBreakRequiredTime()));
		}
		
		if (proxy.getOsceCreationType() == null)
		{
			osceCreationType.setInnerText("");
		}
		else
		{
			osceCreationType.setInnerText(proxy.getOsceCreationType().toString());
		}
		
		if(proxy.getIsFormativeOsce() == null || proxy.getIsFormativeOsce() == false) {
			isFormativeOsce.setInnerHTML(OsMaConstant.UNCHECK_ICON.asString());
		} else {
			isFormativeOsce.setInnerHTML(OsMaConstant.CHECK_ICON.asString());
		}
		
		System.out.println("total task"+ proxy.getTasks().size());
		
		table.setRowCount(proxy.getTasks().size());
	
		
		 List<TaskProxy> list = new ArrayList<TaskProxy>(proxy.getTasks());
		
		System.out.println("l size"+list.size());
		table.setRowData(list);
	
		
		
		
	}

	@UiField
	SpanElement displayRenderer;

	private Presenter presenter;

	@Override
	public void setDelegate(Delegate delegate) {
		this.delegate = delegate;
	}

	@Override
	public void setPresenter(Presenter nationalityActivity) {
		this.presenter =  nationalityActivity;
	}

	public Widget asWidget() {
		return this;
	}

	public boolean confirm(String msg) {
		return Window.confirm(msg);
	}

	public OsceProxy getValue() {
		return proxy;
	}
	
	
	
	
	@UiHandler("arrow")
	void handleClick(ClickEvent e) {
		if (osceDisclosurePanel.isOpen()) {
			osceDisclosurePanel.setOpen(false);
			arrow.setUrl("osMaEntry/gwt/unibas/images/right.png");// set
																				// url
																				// of
																				// up
																				// image

		} else {
			osceDisclosurePanel.setOpen(true);
			arrow.setUrl("osMaEntry/gwt/unibas/images/arrowdownselect.png");// set
																				// url
																				// of
																				// down
																				// image
		}

	}
	
	@UiHandler("edit")
	public void onEditClicked(ClickEvent e) {
		delegate.osceEditClicked();
	}
	
	@UiHandler("delete")
	public void onDeleteClicked(ClickEvent e) {
		delegate.osceDeleteClicked();
	}

	@UiHandler("exportSettingsQRCode")
	public void onExportSettingsQRCodeClicked(ClickEvent e){
		delegate.exportSettingsQRCodeClicked(osceSettingsProxy);
	}
	
	@UiHandler("exportXml")
	public void exportXmlClicked(ClickEvent event){
		delegate.exportXmlClicked(osceSettingsProxy);
	}
	@Override
	public CellTable<TaskProxy> getTable() {
		// TODO Auto-generated method stub
		return null;
	}
	/*
	@Override
	public void setAdministratorValue(List<AdministratorProxy> emptyList) {
		// TODO Auto-generated method stub
		administrator.setAcceptableValues(emptyList);
	}
	*/
	
	/*@UiHandler ("save")
	public void newButtonClicked(ClickEvent event) {
		
				
		
		Date today = new Date();
		Date futureDate=new Date();
		futureDate.setYear(today.getYear()+2);
		
		if(taskName.getValue().length()<3 )
		{
			Window.alert("please enter proper  name of atleast 3 charater");
			return;
		}
		else if(administrator.getValue()==null)
		{
			Window.alert("please select administrator value");
			return;
		}
		
		else if(deadline.getValue()==null)
		{
			Window.alert("please select deadline date");
			return;
		}
		else if(isedit==true)
		{
			if(deadline.getValue().after(futureDate))
			{
				Window.alert("Please enter proper date");
				return;
			}
		}
		else if(deadline.getValue().after(futureDate) || deadline.getValue().before(today) )
		{
			Window.alert("please enter proper date");
			return;
		}
		
	//	delegate.saveClicked(isedit,taskName.getText(),administrator.getValue(),deadline.getValue(),proxy,editProxy);
		isedit=false;
		taskName.setValue("");
		deadline.getTextBox().setValue("");

		
		
	}*/

	@Override
	public OsceTaskPopViewImpl getPopView() {
		// TODO Auto-generated method stub
		return filterPanel;
	}

	@Override
	public void setAdministratorValue(List<AdministratorProxy> emptyList) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setOsceSettings(OsceSettingsProxy response) {
		this.osceSettingsProxy=response;
		
		if(osceSettingsProxy == null){
			return;
		}
		
		if(osceSettingsProxy.getInfotype().equals(BucketInfoType.FTP)){
		bucketType.setInnerText(response.getInfotype().name());	
		lblHost.setInnerText(constants.host());
		lblUsername.setInnerText(constants.userName());
		lblBucketName.setInnerText(constants.basePath());
		}
		
		if(osceSettingsProxy.getInfotype().equals(BucketInfoType.S3)){
		bucketType.setInnerText(response.getInfotype().name());	
		lblHost.getStyle().setDisplay(Display.NONE);
		host.getStyle().setDisplay(Display.NONE);
		lblUsername.setInnerText(constants.accessKey());
		lblBucketName.setInnerText(constants.bucketName());
		}
		username.setInnerText(osceSettingsProxy.getUsername());
		host.setInnerText(osceSettingsProxy.getHost());
		bucketName.setInnerText(osceSettingsProxy.getBucketName());
		backupPeriod.setInnerText(String.valueOf(osceSettingsProxy.getBackupPeriod() == null?"" :osceSettingsProxy.getBackupPeriod()));
		unit.setInnerText(enumConstants.getString(osceSettingsProxy.getTimeunit().toString()));
		password.setInnerText(osceSettingsProxy.getPassword());
		settingPassword.setInnerText(osceSettingsProxy.getSettingPassword());
		
		if(osceSettingsProxy.getNextExaminee()==null || !osceSettingsProxy.getNextExaminee()) {
			pointNxtExaminee.setInnerHTML(OsMaConstant.UNCHECK_ICON.asString());
		} else {
			pointNxtExaminee.setInnerHTML(OsMaConstant.CHECK_ICON.asString());
		}
		encryptionType.setInnerText(enumConstants.getString(osceSettingsProxy.getEncryptionType().toString()));
		symmetricKey.setInnerText(osceSettingsProxy.getSymmetricKey());
		screenSaverText.setInnerText(osceSettingsProxy.getScreenSaverText());
		/*if(osceSettingsProxy.getReviewMode()==null || !osceSettingsProxy.getReviewMode()) {
			examMode.setInnerHTML(OsMaConstant.UNCHECK_ICON.asString());
		} else {
			examMode.setInnerHTML(OsMaConstant.CHECK_ICON.asString());
		}*/
		screenSaverTime.setInnerText(String.valueOf(osceSettingsProxy.getScreenSaverTime() == null?"" :osceSettingsProxy.getScreenSaverTime()));

		if(osceSettingsProxy.getAutoSelection()==null || !osceSettingsProxy.getAutoSelection()) {
			autoSelection.setInnerHTML(OsMaConstant.UNCHECK_ICON.asString());
		} else {
			autoSelection.setInnerHTML(OsMaConstant.CHECK_ICON.asString());
		}
	}
	
/*	@UiHandler("table")
	public void checkBoxClicked(SelectionChangeEvent event)
	{
		
	}
	*/
	/*@Override
	public CellTable<TaskProxy> getTable() {
		return table;
	}*/
	
}
