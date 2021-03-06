package ch.unibas.medizin.osce.server.util.file;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import ch.unibas.medizin.osce.domain.CheckList;
import ch.unibas.medizin.osce.domain.ChecklistCriteria;
import ch.unibas.medizin.osce.domain.ChecklistItem;
import ch.unibas.medizin.osce.domain.ChecklistOption;
import ch.unibas.medizin.osce.domain.File;
import ch.unibas.medizin.osce.domain.RoleBaseItem;
import ch.unibas.medizin.osce.domain.RoleItemAccess;
import ch.unibas.medizin.osce.domain.RoleSubItemValue;
import ch.unibas.medizin.osce.domain.RoleTableItem;
import ch.unibas.medizin.osce.domain.RoleTableItemValue;
import ch.unibas.medizin.osce.domain.StandardizedRole;
import ch.unibas.medizin.osce.domain.UsedMaterial;
import ch.unibas.medizin.osce.server.OsMaFilePathConstant;
import ch.unibas.medizin.osce.server.i18n.GWTI18N;
import ch.unibas.medizin.osce.shared.ItemDefination;
import ch.unibas.medizin.osce.shared.util;
import ch.unibas.medizin.osce.shared.i18n.OsceConstants;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class RolePrintPdfUtil extends PdfUtil {

	private StandardizedRole standardizedRole;

	//private Document document = new Document();
	private OsceConstants constants;
	//private OsceConstantsWithLookup enumConstants;
	private String title;
	//private PdfWriter writer;
	// private List<String> itemsList;
	private Long roleItemAccessId;
	private boolean isValueAvailable[];
	
	protected Font tabFont = new Font(Font.FontFamily.TIMES_ROMAN, 13, Font.BOLD);
	
	public RolePrintPdfUtil() {
		super();		
	}

	public RolePrintPdfUtil(String locale){
		super(locale);
		try {
			constants = GWTI18N.create(OsceConstants.class, locale.toString());
		} catch (IOException e) 
		{
			log.error("Error: ", e);
		}
	}
	
	public RolePrintPdfUtil(Locale locale) {
		super(locale);
	}

	public void writeFile(String fileName, StandardizedRole standardizedRole,
			List<String> itemsList, Long roleItemAccessId) {
		
		try {
			this.standardizedRole = standardizedRole;
			// this.itemsList = itemsList;
			this.roleItemAccessId = roleItemAccessId;
			this.isValueAvailable = new boolean[4];

			//title = constants.standardizedRole() + " "+ standardizedRole.getLongName();
			title = "";
			writer = PdfWriter.getInstance(document, new FileOutputStream(
					fileName));
			document.open();
			addMetaData();
			addHeader();
			addTitle();

			for (String items : itemsList) {
				log.info("items is : " + items);
				log.info("basicData"
						+ (items.compareToIgnoreCase(constants.basicData()) == 0));
				log.info("checkList"
						+ (items.compareToIgnoreCase(constants.checkList()) == 0));
				log.info("roomMaterials"
						+ (items.compareToIgnoreCase(constants.roomMaterials()) == 0));
				log.info("fileDetail"
						+ (items.compareToIgnoreCase(constants.fileDetail()) == 0));

				if (items.compareToIgnoreCase(constants.basicData()) == 0) {
					log.info("items is : addDetails");
					addDetails();
					
				} else if (items.compareTo(constants.checkList()) == 0) {
					log.info("items is : addCheckList");
					addCheckListDetails();
					
				} else if (items.compareTo(constants.roomMaterials()) == 0) {
					log.info("items is : addRoomMaterial");
					
					if (itemsList.contains(constants.basicData()) || itemsList.contains(constants.checkList()))
						document.newPage();
					
					addRoomMaterialDetails();
				} else if (items.compareTo(constants.fileDetail()) == 0) {
					log.info("items is : addFiles");
					
					if (itemsList.contains(constants.basicData()) || itemsList.contains(constants.checkList()) || itemsList.contains(constants.roomMaterials()))
						document.newPage();
					
					addFileDetails();
				}
			}

			// log.info("@@@@@ roleItemAccessId " + roleItemAccessId);
			if (roleItemAccessId >= 0L) {
				addRoleScriptDetials();
			}

			Paragraph preface = new Paragraph();
			addEmptyLine(preface, 5);
			// addSignature(document);

			document.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void writeFile(StandardizedRole standardizedRole,
			List<String> itemsList, Long roleItemAccessId,OutputStream out) {
		try {
			this.standardizedRole = standardizedRole;
			// this.itemsList = itemsList;
			this.roleItemAccessId = roleItemAccessId;
			this.isValueAvailable = new boolean[4];			
			//title = constants.standardizedRole() + " "+ util.getEmptyIfNull(standardizedRole.getLongName());
			title = "";
			writer = PdfWriter.getInstance(document, out);
			document.open();
			addMetaData();
			addHeader();
			addTitle();

			for (String items : itemsList) {
				log.info("==========> items is : " + items);
				log.info("basicData:     "
						+ (items.compareToIgnoreCase(constants.basicData()) == 0));
				log.info("checkList:     "
						+ (items.compareToIgnoreCase(constants.checkList()) == 0));
				log.info("roomMaterials: "
						+ (items.compareToIgnoreCase(constants.roomMaterials()) == 0));
				log.info("fileDetail:    "
						+ (items.compareToIgnoreCase(constants.fileDetail()) == 0));

				if (items.compareToIgnoreCase(constants.basicData()) == 0) {
					log.info("items is : addDetails");
					addDetails();
				} else if (items.compareTo(constants.checkList()) == 0) {
					log.info("items is : addCheckList");
					addCheckListDetails();
				} else if (items.compareTo(constants.roomMaterials()) == 0) {
					log.info("items is : addRoomMaterial");
					
					if (itemsList.contains(constants.basicData()) || itemsList.contains(constants.checkList()))
						document.newPage();
					
					addRoomMaterialDetails();
				} else if (items.compareTo(constants.fileDetail()) == 0) {
					log.info("items is : addFiles");
					
					if (itemsList.contains(constants.basicData()) || itemsList.contains(constants.checkList()) || itemsList.contains(constants.roomMaterials()))
						document.newPage();
					
					addFileDetails();
				}
			}

			// log.info("@@@@@ roleItemAccessId " + roleItemAccessId);
			if (roleItemAccessId >= 0L) {
				addRoleScriptDetials();
			}

			Paragraph preface = new Paragraph();
			addEmptyLine(preface, 5);
			// addSignature(document);

			document.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// private void addSignature(Document document) {
	// try {
	// Paragraph signPara = new Paragraph(
	// StandardizedPatientDetailsConstants.signatureDoc, smallFont);
	//
	// emailFont.setColor(BaseColor.BLUE);
	// Anchor anchor = new Anchor(
	// StandardizedPatientDetailsConstants.emailSignDoc, emailFont);
	// anchor.setReference(StandardizedPatientDetailsConstants.emailSignDoc);
	// signPara.add(anchor);
	// signPara.setAlignment(Element.ALIGN_LEFT);
	//
	// Paragraph paragraph = new Paragraph();
	// addEmptyLine(paragraph, 1);
	// document.add(paragraph);
	//
	// document.add(signPara);
	// } catch (DocumentException e) {
	// log.error("In addSignature of Pdf Creation:" + e.getMessage());
	// }
	//
	// }

	private void addTitle()
	{
		Paragraph titleDetails = new Paragraph();
		Font roleTitleFont = new Font(Font.FontFamily.TIMES_ROMAN, 15,Font.BOLD);
		titleDetails.add(new Chunk(constants.standardizedRole()+": "+ util.getEmptyIfNull(standardizedRole.getLongName()), roleTitleFont));
		addEmptyLine(titleDetails, 1);
		try {
			document.add(titleDetails);
		} catch (DocumentException e) {
			log.error("in PdfUtil.addDetails(): " + e.getMessage());
		}
	}
	private void addMetaData() {
		document.addTitle(title);
		document.addSubject(constants.roleDetail());
		document.addAuthor(System.getProperty("user.name"));
		document.addCreator(System.getProperty("user.name"));
		document.addCreationDate();
	}

	private void addHeader() {
		Paragraph preface = new Paragraph();
		addEmptyLine(preface, 2);
		Paragraph headerParagraph = new Paragraph(title, headerFont);
		headerParagraph.setAlignment(Element.ALIGN_CENTER
				| Element.ALIGN_MIDDLE);
		preface.add(headerParagraph);
//		Paragraph confidentialityParagraph = new Paragraph(
//				constants.handledConfidentially(), smallFont);
//		preface.add(confidentialityParagraph);
		addEmptyLine(preface, 1);

		try {
			document.add(preface);
		} catch (Exception e) {
			log.error("In addStatndadizedPatientImage: " + e.getMessage());
		}
	}

	private void addDetails() {
		PdfPTable table = createDetailsTable();
		
		if (isValueAvailable[0]) {
			Paragraph details = new Paragraph();			
			table.setSpacingBefore(titleTableSpacing);
			details.add(new Chunk(constants.roleDetail(), paragraphTitleFont));
			details.add(table);
			addEmptyLine(details, 1);

			try {
				document.add(details);
			} catch (DocumentException e) {
				log.error("in PdfUtil.addDetails(): " + e.getMessage());
			}
		}
	}

	private void addRoleScriptDetials() {
		Paragraph details = new Paragraph();
		if (standardizedRole.getRoleTemplate() != null) {
			details.add(new Chunk(constants.roleScriptTemplate() + ": "
					+ standardizedRole.getRoleTemplate().getTemplateName(),
					paragraphTitleFont));
			addEmptyLine(details, 1);

			try {
				document.add(details);
			} catch (DocumentException e) {
				log.error("in PdfUtil.addDetails(): " + e.getMessage());
			}

			createRoleScriptDetails();
		}
	}

	private void createRoleScriptDetails() {

		List<RoleBaseItem> roleBaseItems = standardizedRole.getRoleTemplate()
				.getRoleBaseItem();

		log.info("roleBaseItems size " + roleBaseItems.size());

		if (roleBaseItems.size() > 0) {
			isValueAvailable[2] = true;
			for (RoleBaseItem roleBaseItem : roleBaseItems) {
				Set<RoleItemAccess> roleItemAccesses = roleBaseItem
						.getRoleItemAccess();

				boolean isAccessGiven = false;
				if (roleItemAccessId == 0L) {
					isAccessGiven = true;
				} else {
					for (Iterator<RoleItemAccess> iterator = roleItemAccesses
							.iterator(); iterator.hasNext();) {
						RoleItemAccess roleItemAccess = (RoleItemAccess) iterator
								.next();
						// log.info("```````````roleItemAccess.getId()"
						// + roleItemAccess.getId());
						// log.info("`````````````roleItemAccessId"
						// + roleItemAccessId);
						if (roleItemAccess.getId().longValue() == roleItemAccessId
								.longValue()) {
							isAccessGiven = true;
							break;
						}

					}
				}
				log.info("isAccessGiven : " + isAccessGiven);
				if (isAccessGiven) {
					String itemName = (roleBaseItem.getItem_name() != null) ? roleBaseItem
							.getItem_name() : "-";
					Paragraph details = new Paragraph();

					details.add(new Chunk(itemName, subTitleFont));

					// addEmptyLine(details, 1);

					try {
						document.add(details);
					} catch (DocumentException e) {
						log.error("in PdfUtil.addDetails(): " + e.getMessage());
					}

					if (roleBaseItem.getItem_defination() == ItemDefination.table_item) {

						PdfPTable roleScriptTable = new PdfPTable(2);

						roleScriptTable.addCell(getPdfCellBold(constants
								.itemName()));
						roleScriptTable.addCell(getPdfCellBold(constants
								.itemValue()));
						// roleBaseItem.getRoleTableItem().get(0).getRoleTableItemValue().
						boolean isRoleScriptAdded = false;

						List<RoleTableItem> roleTableItemList = roleBaseItem
								.getRoleTableItem();

						for (Iterator<RoleTableItem> iterator = roleTableItemList
								.iterator(); iterator.hasNext();) {
							RoleTableItem roleTableItem = (RoleTableItem) iterator
									.next();

							Set<RoleTableItemValue> roleTableItemValueSet = roleTableItem
									.getRoleTableItemValue();

							for (Iterator<RoleTableItemValue> iterator2 = roleTableItemValueSet
									.iterator(); iterator2.hasNext();) {
								RoleTableItemValue roleTableItemValue = (RoleTableItemValue) iterator2
										.next();
								if (roleTableItemValue.getStandardizedRole()
										.getId() == standardizedRole.getId()) {

									isRoleScriptAdded = true;
									roleScriptTable
											.addCell(getPdfCell(roleTableItem
													.getItemName()));
									roleScriptTable
											.addCell(getPdfCell(roleTableItemValue
													.getValue()));
								}

							}

						}

						if (isRoleScriptAdded) {
							roleScriptTable.addCell(getPdfCellBold(" "));
							roleScriptTable.addCell(getPdfCell(" "));

							roleScriptTable.setWidthPercentage(100);
							try {
								document.add(createPara(roleScriptTable, ""));
							} catch (DocumentException e) {
								log.error("in PdfUtil.addDetails(): "
										+ e.getMessage());
							}
						}
					} else if (roleBaseItem.getItem_defination() == ItemDefination.rich_text_item) {

						List<RoleSubItemValue> roleTableItemList = roleBaseItem
								.getRoleSubItem();

						for (Iterator<RoleSubItemValue> iterator = roleTableItemList
								.iterator(); iterator.hasNext();) {
							RoleSubItemValue roleSubItemValue = (RoleSubItemValue) iterator
									.next();
							if (roleSubItemValue.getStandardizedRole().getId()
									.longValue() == standardizedRole.getId()
									.longValue()) {

								String string = roleSubItemValue.getItemText();
								log.error("getItemText : " + string);

								HTMLWorker htmlWorker = new HTMLWorker(document);
								try {
									htmlWorker.parse(new StringReader(string));

									details = new Paragraph();
									addEmptyLine(details, 1);
									document.add(details);

								} catch (Exception e) {
									log.error("in PdfUtil.addDetails(): "
											+ e.getMessage());
								}

							}
						}

					}

				}

			}
		}

	}

	private void addFileDetails() {
		Paragraph details = new Paragraph();
		PdfPTable table = createFileDetailsTable();
		if (isValueAvailable[1]) {
			table.setSpacingBefore(titleTableSpacing);
			details.add(new Chunk(constants.fileDetail() + " "
					+ constants.details(), paragraphTitleFont));
			details.add(table);
			addEmptyLine(details, 1);

			try {
				//document.newPage();
				document.add(details);
			} catch (DocumentException e) {
				log.error("in PdfUtil.addDetails(): " + e.getMessage());
			}
		}
	}

	private void addRoomMaterialDetails() {
		Paragraph details = new Paragraph();
		PdfPTable table = createRoomMaterialDetailsTable();
		if (isValueAvailable[2]) {
			table.setSpacingBefore(titleTableSpacing);
			details.add(new Chunk(constants.roomMaterials() + " "
					+ constants.details(), paragraphTitleFont));
			details.add(table);
			addEmptyLine(details, 1);

			try {
				//document.newPage();
				document.add(details);
			} catch (DocumentException e) {
				log.error("in PdfUtil.addDetails(): " + e.getMessage());
			}
		}
	}

	private void addCheckListDetails() {
		
		Paragraph titleDetails = new Paragraph();
		Font roleTitleFont = new Font(Font.FontFamily.TIMES_ROMAN, 15,Font.BOLD);
		titleDetails.add(new Chunk(constants.standardizedRole()+": "+ util.getEmptyIfNull(standardizedRole.getLongName()), roleTitleFont));
		addEmptyLine(titleDetails, 1);
		
		if ((standardizedRole.getCheckList() != null)
				&& (standardizedRole.getCheckList().getCheckListTopics() != null)
				&& (standardizedRole.getCheckList().getCheckListTopics().size() > 0)) {
			Paragraph details = new Paragraph();
			
			String checkListTitle = (standardizedRole.getCheckList().getTitle() != null) 
					? " " + standardizedRole.getCheckList().getTitle() : "";
					
			details.add(new Chunk(constants.checkList() + ": " + checkListTitle, paragraphTitleFont));			
			addEmptyLine(details, 1);

			try {
				//document.add(titleDetails);
				//document.add(createDetailsTable());
				//Paragraph emptyLineParagraph=new Paragraph();
				//addEmptyLine(emptyLineParagraph, 1);
				//document.add(emptyLineParagraph);
				document.add(details);
			} catch (DocumentException e) {
				log.error("in PdfUtil.addDetails(): " + e.getMessage());
			}
		}
		if(standardizedRole.getCheckList() != null){
			createCheckListTabs(standardizedRole.getCheckList().getId());
		}
	}

	private void createCheckListTabs(Long checklistId) {
		
		List<ChecklistItem> checklistTabs = ChecklistItem.findAllChecklistTabsByChecklistId(checklistId);
		for(ChecklistItem checklistTab: checklistTabs){
			
			String tabText=checklistTab.getName() ;
			Paragraph tabParagraph = new Paragraph(new Chunk(tabText,tabFont));
			try {
				addEmptyLine(tabParagraph, 1);
				document.add(tabParagraph);
			} catch (DocumentException e) {
				e.printStackTrace();
			}
			createCheckListDetailsTable(checklistTab.getId());
		}
	}

	private Paragraph createPara(PdfPTable pdfPTable, String header) {

		Paragraph details = new Paragraph();
		// pdfPTable.setSpacingBefore(titleTableSpacing);
		// addEmptyLine(details, 1);
		if (header.compareTo("") != 0)
			details.add(new Chunk(header, subTitleFont));
		details.add(pdfPTable);
		return details;
	}
	
	private PdfPCell getAnswerCell(String question, List<ChecklistOption> options) {
		Collections.sort(options);
		String[] answers = new String[options.size()];
		for (int i=0; i < options.size(); i++) {
			ChecklistOption option = options.get(i);		
			answers[i] = option.getOptionName() + " (" + option.getValue() + ")";
		}
		return getCheckBoxCell(question, answers, null);
	}
	
	private PdfPTable createCheckListQuestionTable(List<ChecklistItem> questions) {
		if (questions != null && questions.size() > 0) {
		PdfPTable table = new PdfPTable(new float[] { 0.7f, 0.3f });
			
			int i = 0;
			int j=0;
			for (ChecklistItem question: questions) {
				String questionText = question.getName();
				if (questionText != null) {
					Chunk questionChunk = new Chunk(questionText, boldFont);					
					Chunk criteriaChunk = null;
					Chunk instructionChunk = null;
					
					if (question.getDescription() != null) {
						instructionChunk = new Chunk(OsMaFilePathConstant.SPACING_QUESTION_DESCRIPTION + question.getDescription(), FontFactory.getFont(FontFactory.HELVETICA, 10, Font.ITALIC, new BaseColor(181,185,200)));						
					}

					if (question.getCheckListCriterias().size() > 0) {
						//StringBuilder sb = new StringBuilder(questionText + "\n\n");
						StringBuilder sb = new StringBuilder("\n\n");
						List<ChecklistCriteria> criteria = question.getCheckListCriterias();
						Collections.sort(criteria);
						for (ChecklistCriteria criterion : criteria) {
							sb.append(criterion.getCriteria() + ", ");
						}
						sb.replace(sb.length() - 2, sb.length(), "");						
						criteriaChunk = new Chunk(sb.toString(), smallItalicFont);						
					}
					
					Paragraph questionParagraph = new Paragraph(questionChunk);											
					if (instructionChunk != null)
					{												
						questionParagraph.add(instructionChunk);						
					}
					if (criteriaChunk != null)
					{																													
						questionParagraph.add(criteriaChunk);						
					}
					
					PdfPCell questionCell = getPdfCell(questionParagraph, 1, 1);						
					PdfPCell answerCell = getAnswerCell(questionText, question.getCheckListOptions());							
					if(j==0)
					{
						questionCell.setBorder(Rectangle.TOP);
						answerCell.setBorder(Rectangle.TOP);
						j++;
					}
					if (i < questions.size()) {
						////questionCell.setBorder(Rectangle.BOTTOM);
						////answerCell.setBorder(Rectangle.BOTTOM);
						questionCell.setPaddingTop(05.0f);
						questionCell.setPaddingBottom(05.0f);						
						questionCell.setBorderWidthBottom(0.5f);
						answerCell.setPaddingTop(05.0f);
						answerCell.setPaddingBottom(05.0f);
						answerCell.setBorderWidthBottom(0.5f);
					}																				
					table.addCell(questionCell);			
					table.addCell(answerCell);
				}
			}			
			table.setWidthPercentage(100.0f);
			return table;
		}
		return null;
	}

	private PdfPTable createRoomMaterialDetailsTable() {

		PdfPTable roomMaterialTable = new PdfPTable(3);

		List<UsedMaterial> usedMaterials = UsedMaterial
				.findUsedMaterialsByStandardizedRoleIDpdf(standardizedRole.getId());
		log.info("usedMaterials size " + usedMaterials.size());

		roomMaterialTable.addCell(getPdfCellBold(constants.roomMaterialName()));
		roomMaterialTable.addCell(getPdfCellBold(constants.roomMaterialNumber()));
		roomMaterialTable.addCell(getPdfCellBold(constants.roomMaterialUser()));
		if (usedMaterials.size() > 0) {
			isValueAvailable[2] = true;
			for (UsedMaterial usedMaterial : usedMaterials) {

				String materialName = (usedMaterial.getMaterialList() != null) ? usedMaterial
						.getMaterialList().getName() : "-";
				String materialCount = (usedMaterial.getMaterialCount() != null) ? ""
						+ usedMaterial.getMaterialCount()
						: "-";
				String usedFor = (usedMaterial.getUsed_from() != null) ? ""
						+ enumConstants.getString(usedMaterial.getUsed_from()
								.toString()) : "-";

				roomMaterialTable.addCell(getPdfCell(materialName));
				roomMaterialTable.addCell(getPdfCell(materialCount, italicFont,
						0, 0));
				roomMaterialTable
						.addCell(getPdfCell(usedFor, italicFont, 0, 0));

			}

			roomMaterialTable.addCell(getPdfCellBold(" "));
			roomMaterialTable.addCell(getPdfCell(" "));
		}
		roomMaterialTable.setWidthPercentage(100);

		return roomMaterialTable;
	}

	private void createCheckListDetailsTable(Long checklistTabId) {
		isValueAvailable[3] = true;
		CheckList checkList = standardizedRole.getCheckList();

		List<ChecklistItem> checklistTopics = ChecklistItem.findChecklistTopicByChecklistTab(checklistTabId);
		//List<ChecklistTopic> checklistTopics = checkList.getCheckListTopics();

		log.info("CheckList size " + checklistTopics.size());
		
		Paragraph checklistDetails = new Paragraph();

		for (ChecklistItem checklistTopic : checklistTopics) {
			
			String chkListTitle = checklistTopic.getName();
			if (chkListTitle != null) {
				
				Paragraph titleParagraph = new Paragraph(new Chunk(chkListTitle, subTitleFont));
				try {
					document.add(titleParagraph);
				} catch (DocumentException e) {
					log.error("in RolePrintPdfUtil.createCheckListDetailsTable(): " + e.getMessage());
				}
			}
			
			String chkListDesc = checklistTopic.getDescription();
			if (chkListDesc != null) {
				Paragraph descriptionParagraph = new Paragraph(new Chunk(chkListDesc, defaultFont));
				////addEmptyLine(descriptionParagraph, 1);				
				try {
					document.add(descriptionParagraph);
				} catch (DocumentException e) {
					log.error("in PdfUtil.addDetails(): " + e.getMessage());
				}
			}
			
						
			try {
				List<ChecklistItem> checklistQuestionList = ChecklistItem.findChecklistQuestionByChecklistTopic(checklistTopic.getId());
				
				if(checklistQuestionList!=null && checklistQuestionList.size()>0)
				{
					PdfPTable table = createCheckListQuestionTable(checklistQuestionList);
					table.setSpacingBefore(05.0f);
					table.setSpacingAfter(20.0f);
					document.add(table);
				}
			} catch (DocumentException ex) {
				log.error("in RolePrintPdfUtil.createCheckListDetailsTable(): " + ex.getMessage(),ex);
			}
		}
	}

	private PdfPTable createFileDetailsTable() {

		PdfPTable fileTable = new PdfPTable(2);

		List<File> files = File.getFilesByStandardizedRoleID(standardizedRole
				.getId());
		log.info("File size " + files.size());
		if (files.size() > 0) {
			isValueAvailable[1] = true;

			fileTable.addCell(getPdfCellBold(constants.filePath()));
			fileTable.addCell(getPdfCellBold(constants.fileDescription()));

			for (File file : files) {

				String fileDesc = (file.getDescription() != null) ? file
						.getDescription() : "-";
				String filePath = (file.getPath() != null) ? file.getPath()
						: "-";
				fileTable.addCell(getPdfCell(fileDesc));
				fileTable.addCell(getPdfCell(filePath, italicFont, 0, 0));
			}

			fileTable.addCell(getPdfCellBold(" "));
			fileTable.addCell(getPdfCell(" "));

			fileTable.setWidthPercentage(100);
		}

		return fileTable;
	}

	private PdfPTable createDetailsTable() {
		PdfPTable table = new PdfPTable(new float[] { 0.2f, 0.3f, 0.2f, 0.3f });

		String shortName = (standardizedRole.getShortName() != null) ? standardizedRole
				.getShortName().toString() : "-";
		String longName = (standardizedRole.getLongName() != null) ? standardizedRole
				.getLongName() : "-";
		String roleType = (standardizedRole.getRoleType() != null) ? enumConstants
				.getString(standardizedRole.getRoleType().toString()) : "-";

		String studyYear = (standardizedRole.getStudyYear() != null) ? enumConstants
				.getString(standardizedRole.getStudyYear().toString()) : "-";

		/*String factor = (standardizedRole.getFactor() != null) ? enumConstants
				.getString(standardizedRole.getFactor().toString()) : "-";
				
		String sum = (standardizedRole.getSum() != null) ? enumConstants
				.getString(standardizedRole.getSum().toString()) : "-";
		*/		
		String factor = (standardizedRole.getFactor() != null) ? standardizedRole.getFactor().toString() : "-";
						
		String sum = (standardizedRole.getSum() != null) ? standardizedRole.getSum().toString(): "-";

		table.addCell(getPdfCellBold(constants.roleAcronym() + ":"));
		// TODO format date
		table.addCell(getPdfCell(shortName));

		table.addCell(getPdfCellBold(constants.name() + ":"));
		table.addCell(getPdfCell(longName));

		table.addCell(getPdfCellBold(constants.roleType() + ":"));
		table.addCell(getPdfCell(roleType));

		table.addCell(getPdfCellBold(constants.studyYears() + ":"));
		table.addCell(getPdfCell(studyYear));

		table.addCell(getPdfCellBold(constants.factor() + ":"));
		table.addCell(getPdfCell(factor));
		
		table.addCell(getPdfCellBold(constants.sum() + ":"));
		table.addCell(getPdfCell(sum));

		table.addCell(getPdfCellBold(" "));
		table.addCell(getPdfCell(" "));
		isValueAvailable[0] = true;

		table.setWidthPercentage(100);
		return table;
	}
	
//	private void createCheckTopicQuesDetailsTable(
//	List<ChecklistQuestion> checklistQuestions) {
//
//if (checklistQuestions != null && checklistQuestions.size() > 0) {
//	log.info("checklistQuestions size " + checklistQuestions.size());
//	
//	for (ChecklistQuestion checklistQuestion : checklistQuestions) {
//		String question = (checklistQuestion.getQuestion() != null) ? checklistQuestion
//				.getQuestion() : "-";
//		String instruction = (checklistQuestion.getInstruction() != null) ? ""
//				+ checklistQuestion.getInstruction()
//				: "-";
//
//		// log.info(" ~~ question " + question);
//		// log.info(" ~~~~~~ instruction " + instruction);
//		PdfPTable chkListQuesTable = new PdfPTable(4);
//
//		chkListQuesTable.addCell(getPdfCellBold(constants
//				.roleQuestionName()));
//		chkListQuesTable.addCell(getPdfCell(question));
//		chkListQuesTable.addCell(getPdfCellBold(constants
//				.roleQuestionInstruction()));
//		chkListQuesTable.addCell(getPdfCell(instruction, italicFont, 0,
//				0));
//
//		chkListQuesTable.setWidthPercentage(100);
//		// chkListQuesTable.setSpacingBefore(titleTableSpacing);
//
//		try {
//			Paragraph details = new Paragraph();
//			addEmptyLine(details, 1);
//			document.add(details);
//
//			document.add(createPara(chkListQuesTable, ""));
//		} catch (DocumentException e) {
//			log.error("in PdfUtil.addDetails(): " + e.getMessage());
//		}
//
//		PdfPTable criteriaTable = new PdfPTable(1);
//
//		List<ChecklistCriteria> checklistQuestionSet = checklistQuestion
//				.getCheckListCriterias();
//
//		log.info("checklistQuestionSet size : "
//				+ checklistQuestionSet.size());
//		StringBuffer criteriaBuffer = new StringBuffer("");
//
//		boolean isFirst = false;
//		for (ChecklistCriteria checklistCriteria : checklistQuestionSet) {
//
//			String criteria = (checklistCriteria.getCriteria() != null) ? ""
//					+ checklistCriteria.getCriteria()
//					: "-";
//			if (isFirst) {
//				criteriaBuffer.append(", ");
//			} else {
//				isFirst = true;
//			}
//
//			log.info(" ~~~~~~ criteria " + criteria);
//			criteriaBuffer.append(criteria);
//
//		}
//
//		criteriaTable.addCell(getPdfCell(criteriaBuffer.toString(),
//				italicFont, 0, 0));
//
//		Paragraph paragraph = new Paragraph();
//		// pdfPTable.setSpacingBefore(titleTableSpacing);
//		// addEmptyLine(details, 1);
//
//		paragraph.add(constants.criterion() + " : "
//				+ criteriaBuffer.toString());
//
//		// PdfPCell pdfPCell = new PdfPCell(createPara(criteriaTable,
//		// ""));
//		// pdfPCell.setBorder(Rectangle.NO_BORDER);
//		//
//		// chkListQuesTable.addCell(pdfPCell);
//
//		try {
//			// document.add(createPara(criteriaTable,
//			// constants.criterion()));
//
//			document.add(paragraph);
//		} catch (DocumentException e) {
//			log.error("in PdfUtil.addDetails(): " + e.getMessage());
//		}
//
//		// PdfPTable optionTable = new PdfPTable(3);
//		PdfPTable optionTable = new PdfPTable(2);
//
//		log.info("getCheckListOptions size : "
//				+ checklistQuestion.getCheckListOptions().size());
//
//		optionTable.addCell(getPdfCellBold(constants.roleOptionName()));
//		// optionTable.addCell(getPdfCellBold(constants.name()));
//		optionTable.addCell(getPdfCellBold(constants.roleOptionValue()));
//
//		List<ChecklistOption> checklistOptions = checklistQuestion
//				.getCheckListOptions();
//		for (ChecklistOption checklistOption : checklistOptions) {
//
//			String optionName = (checklistOption.getOptionName() != null) ? ""
//					+ checklistOption.getOptionName()
//					: "-";
//			// String name = (checklistOption.getName() != null) ? ""
//			// + checklistOption.getName() : "-";
//			String value = (checklistOption.getValue() != null) ? ""
//					+ checklistOption.getValue() : "-";
//
//			log.info("optionName " + optionName);
//			// log.info("name " + name);
//			log.info("value " + value);
//
//			optionTable
//					.addCell(getPdfCell(optionName, italicFont, 0, 0));
//			// optionTable.addCell(getPdfCell(name, italicFont, 0, 0));
//			optionTable.addCell(getPdfCell(value, italicFont, 0, 0));
//
//		}
//		// chkListQuesTable.addCell(optionTable);
//
//		// pdfPCell = new PdfPCell(createPara(optionTable, ""));
//		// pdfPCell.setBorder(Rectangle.NO_BORDER);
//		//
//		// chkListQuesTable.addCell(pdfPCell);
//
//		try {
//			document.add(createPara(optionTable,
//					constants.optionDetail()));
//		} catch (DocumentException e) {
//			log.error("in PdfUtil.addDetails(): " + e.getMessage());
//		}
//
//	}
//}
//
//}
}