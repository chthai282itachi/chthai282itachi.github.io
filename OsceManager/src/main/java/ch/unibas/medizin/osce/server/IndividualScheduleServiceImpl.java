/**
 * 
 */
package ch.unibas.medizin.osce.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import ch.unibas.medizin.osce.client.IndividualScheduleService;
import ch.unibas.medizin.osce.domain.Assignment;
import ch.unibas.medizin.osce.domain.Doctor;
import ch.unibas.medizin.osce.domain.OsceDay;
import ch.unibas.medizin.osce.domain.PatientInRole;
import ch.unibas.medizin.osce.domain.PatientInSemester;
import ch.unibas.medizin.osce.domain.RoleBaseItem;
import ch.unibas.medizin.osce.domain.RoleItemAccess;
import ch.unibas.medizin.osce.domain.RoleSubItemValue;
import ch.unibas.medizin.osce.domain.RoleTableItem;
import ch.unibas.medizin.osce.domain.RoleTableItemValue;
import ch.unibas.medizin.osce.domain.StandardizedPatient;
import ch.unibas.medizin.osce.domain.StandardizedRole;
import ch.unibas.medizin.osce.domain.Student;
import ch.unibas.medizin.osce.shared.ItemDefination;
import ch.unibas.medizin.osce.shared.OsMaConstant;
import ch.unibas.medizin.osce.shared.util;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * @author 
 *
 */
@SuppressWarnings("serial")
public class IndividualScheduleServiceImpl extends RemoteServiceServlet implements IndividualScheduleService
{
	
	static Font subTitleFont = new Font(Font.FontFamily.TIMES_ROMAN, 11,Font.BOLD);
	static Font paragraphTitleFont = new Font(Font.FontFamily.TIMES_ROMAN, 14,Font.BOLD);
	
	@Override
	@SuppressWarnings("rawtypes")
	public String generateSPPDFUsingTemplate(String templateName,Map templateVariables,List<Long> standPatientId,Long semesterId)	
	{
		Log.info("Call generateMailPDFUsingTemplate" + standPatientId.size());
		
		Document document = null;
		File file = null;
		PdfWriter writer =null;
		HTMLWorker htmlWorker = null;
		
		
		
		String fileContents = null;
		String mailMessage = null;	
		String tempMailMessage=null;
		String tempSPPatientInRole=null;
			
		int index = 0;
		try {
			
			document = new Document();
			file = new File("StandardizedPatient.pdf");
			writer = PdfWriter.getInstance(document, new FileOutputStream(file));
			
			document.open();
			
			htmlWorker = new HTMLWorker(document);
			
			fileContents = this.getTemplateContent(templateName)[1];		
					
			String titleContent =new String();                
			String tempOsceDayContent =new String();     
			String osceDayContent =new String();     
			String tempScheduleContent  =new String();     
			String scheduleContent =new String();			
			String tempBreakContent =new String();     
			String breakContent =new String();     
			String tempScriptContent =new String();     
			String scriptContent =new String();     
			
			String scriptDetail=new String();
			
			for(index=0; index < standPatientId.size(); index++)
			{
				StandardizedPatient standardizedPatient= StandardizedPatient.findStandardizedPatient(standPatientId.get(index));
				
				mailMessage="";
				titleContent="";
				tempOsceDayContent="";
				osceDayContent="";
				tempScheduleContent="";				
				scheduleContent="";
				tempBreakContent="";
				breakContent="";
				tempScriptContent="";
				scriptContent="";

				mailMessage = fileContents;	
				tempMailMessage=fileContents;
								
				titleContent=mailMessage.substring(mailMessage.indexOf("[TITLE SEPARATOR]"), mailMessage.indexOf("[TITLE SEPARATOR.]"));
				tempOsceDayContent=mailMessage.substring(mailMessage.indexOf("[OSCE_DAY SEPARATOR]"), mailMessage.indexOf("[OSCE_DAY SEPARATOR.]"));
				tempScheduleContent=mailMessage.substring(mailMessage.indexOf("[SCHEDULE SEPARATOR]"), mailMessage.indexOf("[SCHEDULE SEPARATOR.]"));				
				tempBreakContent=mailMessage.substring(mailMessage.indexOf("[BREAK SEPARATOR]"), mailMessage.indexOf("[BREAK SEPARATOR.]"));
				/*tempScriptContent=mailMessage.substring(mailMessage.indexOf("[SCRIPT SEPARATOR]"), mailMessage.indexOf("[SCRIPT SEPARATOR.]"));*/
				
				titleContent=titleContent.replace("[TITLE SEPARATOR]","");
				titleContent=titleContent.replace("[TITLE SEPARATOR.]","");
				tempOsceDayContent=tempOsceDayContent.replace("[OSCE_DAY SEPARATOR]","");
				tempOsceDayContent=tempOsceDayContent.replace("[OSCE_DAY SEPARATOR.]","");
				tempBreakContent=tempBreakContent.replace("[BREAK SEPARATOR]", "");
				tempBreakContent=tempBreakContent.replace("[BREAK SEPARATOR.]", "");
				tempScheduleContent=tempScheduleContent.replace("[SCHEDULE SEPARATOR]", "");
				tempScheduleContent=tempScheduleContent.replace("[SCHEDULE SEPARATOR.]", "");
				/*tempScriptContent=tempScriptContent.replace("[SCRIPT SEPARATOR]","");
				tempScriptContent=tempScriptContent.replace("[SCRIPT SEPARATOR.]","");*/
				
						
				titleContent = titleContent.replace("[NAME]",standardizedPatient.getName());
				titleContent = titleContent.replace("[PRENAME]",standardizedPatient.getPreName());	
				
				writeInPDFFile(titleContent,document);
				
				mailMessage=titleContent;

				List<PatientInSemester> patientInSemesters=PatientInSemester.findPatientInSemesterBySemesterPatient(standardizedPatient.getId(), semesterId);							
				Log.info("patientInSemesters Size:" + patientInSemesters.size());					
				
				Iterator pisIterator = patientInSemesters.iterator();				
			    while(pisIterator.hasNext())
			    {
			    	PatientInSemester patientInSemester=(PatientInSemester) pisIterator.next();
			    	Set<PatientInRole> lstPatientInRole=patientInSemester.getPatientInRole();
			    	
			    	Log.info("Total Patient In Role " + lstPatientInRole.size() + " for Semester: " + patientInSemester.getId());
			    	Iterator pirIterator = lstPatientInRole.iterator();
			    	
			    	while ( pirIterator.hasNext()) 
			    	{
						PatientInRole patientInRole = (PatientInRole) pirIterator.next();
						List<Long> distinctOsceDayByPIRId=Assignment.findDistinctOsceDayIdByPatientInRoleId(patientInRole.getId());
						Log.info("Find Total " + distinctOsceDayByPIRId.size()+" distinct Osce Day for Patient In Role " + patientInRole.getId());
																		
						Iterator distinctOsceDayIterator = distinctOsceDayByPIRId.iterator();
						while ( distinctOsceDayIterator.hasNext()) 
						{
							
							Long osceDayId = (Long) distinctOsceDayIterator.next();
							
							scheduleContent="";	
							osceDayContent="";	
							scriptDetail="";
							
							List<Assignment> assignment=Assignment.findAssignmentsByOsceDayAndPIRId(osceDayId, patientInRole.getId());
							Log.info("Find Total " + assignment.size()+" assignment for Osce Day "+osceDayId+" and Patient In Role " + patientInRole.getId());
							
							OsceDay osceDayEntity=OsceDay.findOsceDay(osceDayId);
							
							tempOsceDayContent=tempMailMessage.substring(tempMailMessage.indexOf("[OSCE_DAY SEPARATOR]"), tempMailMessage.indexOf("[OSCE_DAY SEPARATOR.]"));
							tempOsceDayContent=tempOsceDayContent.replace("[OSCE_DAY SEPARATOR]","");
							tempOsceDayContent=tempOsceDayContent.replace("[OSCE_DAY SEPARATOR.]","");
							
							//tempOsceDayContent=tempOsceDayContent.replace("[OSCE]",osceDayEntity.getOsce().getName());
							String osceLable = osceDayEntity.getOsce().getStudyYear()==null?"":osceDayEntity.getOsce().getStudyYear() + "." + osceDayEntity.getOsce().getSemester().getSemester().name();
							tempOsceDayContent=tempOsceDayContent.replace("[OSCE]",osceLable);
							String date=""+osceDayEntity.getOsceDate().getDate()+"-"+(osceDayEntity.getOsceDate().getMonth()+1) +"-"+(osceDayEntity.getOsceDate().getYear()+1900);
							Log.info("Date "+date+" for Osce Day: " + osceDayEntity.getId());
							tempOsceDayContent=tempOsceDayContent.replace("[DATE]",date);														
							
							////*osceDayContent=osceDayContent+tempOsceDayContent;
							////*Log.info("OSCE DAY CONTENT: " + osceDayContent);
							////*mailMessage=mailMessage+osceDayContent;
							
							Iterator assignmentIterator = assignment.iterator();
							
							scheduleContent="";
							//int tempSp=0;							
							while(assignmentIterator.hasNext())
							{								
								Assignment assignmentStandardizedPatient=(Assignment)assignmentIterator.next();
								Log.info("Assignment: "+ assignmentStandardizedPatient.getId() + " on OSCE DAY: " + osceDayId + " for SPorPIR "+ patientInRole.getId());
								
								//if(tempSp==0)
								//{
									tempOsceDayContent=tempOsceDayContent.replace("[ROLE]",""+assignmentStandardizedPatient.getOscePostRoom().getOscePost().getStandardizedRole().getLongName());
									
									Long roleItemAccessId=findIdforRoleItemAccessByName(standardizedPatient.getName());
									Log.info("Role Item Access for Standardized Patient: " + standardizedPatient.getName() + "is " + roleItemAccessId);
									
									//Long roleItemAccessId=1L;
									////scriptDetail+=createRoleScriptDetails(assignmentStandardizedPatient.getOscePostRoom().getOscePost().getStandardizedRole(),roleItemAccessId);
									////Log.info("Script Detail: " + scriptDetail);
																		
								//}
								//tempSp++;
								
								tempScheduleContent=tempMailMessage.substring(tempMailMessage.indexOf("[SCHEDULE SEPARATOR]"), tempMailMessage.indexOf("[SCHEDULE SEPARATOR.]"));
								tempScheduleContent=tempScheduleContent.replace("[SCHEDULE SEPARATOR]", "");
								tempScheduleContent=tempScheduleContent.replace("[SCHEDULE SEPARATOR.]", "");
								
								String startTime=""+(assignmentStandardizedPatient.getTimeStart().getHours())+":"+(assignmentStandardizedPatient.getTimeStart().getMinutes())+":"+(assignmentStandardizedPatient.getTimeStart().getSeconds());
								String endTime=""+(assignmentStandardizedPatient.getTimeEnd().getHours())+":"+(assignmentStandardizedPatient.getTimeEnd().getMinutes())+":"+(assignmentStandardizedPatient.getTimeEnd().getSeconds());
								
								tempScheduleContent=tempScheduleContent.replace("[START TIME]", startTime);
								tempScheduleContent=tempScheduleContent.replace("[END TIME]", endTime);	
								
								tempScheduleContent=tempScheduleContent.replace("[POST]", assignmentStandardizedPatient.getOscePostRoom().getOscePost().getId().toString());
								tempScheduleContent=tempScheduleContent.replace("[ROOM]", assignmentStandardizedPatient.getOscePostRoom().getRoom().getRoomNumber().toString());
								
								scheduleContent=scheduleContent+tempScheduleContent;
							}
							
							Log.info("Osce Day Content: " + osceDayContent);
							osceDayContent=osceDayContent+tempOsceDayContent;
							mailMessage=mailMessage+osceDayContent;
							
							writeInPDFFile(osceDayContent,document);
							
							Log.info("Schedule Content: " + scheduleContent);
							mailMessage=mailMessage+scheduleContent;
							writeInPDFFile(scheduleContent,document);
							
							Paragraph roleTemplateEL = new Paragraph();							
							addEmptyLine(roleTemplateEL, 1);
							document.add(roleTemplateEL);
						
							/*Iterator assignmentIterator1 = assignment.iterator();
							int tempSp1=0;	
							while(assignmentIterator1.hasNext())
							{								
								Assignment assignmentStandardizedPatient=(Assignment)assignmentIterator1.next();
								Log.info("Assignment1 : "+ assignmentStandardizedPatient.getId() + " on OSCE DAY: " + osceDayId + " for SPorPIR "+ patientInRole.getId());
								
								if(tempSp1==0)
								{
									//tempOsceDayContent=tempOsceDayContent.replace("[ROLE]",""+assignmentStandardizedPatient.getOscePostRoom().getOscePost().getStandardizedRole().getLongName());
									
									Long roleItemAccessId=findIdforRoleItemAccessByName(standardizedPatient.getName());
									Log.info("Role Item Access for Standardized Patient1 : " + standardizedPatient.getName() + "is " + roleItemAccessId);
									
									//Long roleItemAccessId=1L;
									//scriptDetail+=createRoleScriptDetails(assignmentStandardizedPatient.getOscePostRoom().getOscePost().getStandardizedRole(),roleItemAccessId,document);
									createRoleScriptDetails(assignmentStandardizedPatient.getOscePostRoom().getOscePost().getStandardizedRole(),roleItemAccessId,document);
									Log.info("Script Detail1 : " + scriptDetail);
																		
								}
								tempSp1++;																
							}*/
							
													
							
						
							//scriptDetail=scriptDetail.replace("[]","");
							//mailMessage=mailMessage+scriptDetail;
							
							//writeInPDFFile(scriptDetail,document);
							
						}
						
						
					}
			    }
				
				Log.info("File Content: " + fileContents);
				Log.info("After Modification: " + mailMessage);
										
				//htmlWorker.parse(new StringReader(mailMessage));
				document.newPage();
				
			}
			
			document.close();
			
			return "StandardizedPatient.pdf";

		} catch (Exception e) {
			Log.error("in SummoningsServiceImpl.generateMailPDFUsingTemplate: "	+ e.getMessage());
			e.printStackTrace();
			return null;
		}finally{
			document = null;
			file = null;
			writer =null;
			htmlWorker = null;
			fileContents = null;
		}
	}
	
	
	private void writeInPDFFile(String documentContent,Document document) 
	{	
		Log.info("Call writeInPDFFile with Content" + documentContent);
		try
		{
			HTMLWorker htmlWorker = new HTMLWorker(document);
			htmlWorker.parse(new StringReader(documentContent));		
		} 
		catch (Exception e) 
		{
			Log.error("in SummoningsServiceImpl.generateMailPDFUsingTemplate: "	+ e.getMessage());
		}
		finally
		{
		document = null;
		}
	}


	private Long findIdforRoleItemAccessByName(String name) 
	{
		List<RoleItemAccess> roleItemAccess=RoleItemAccess.findAllRoleItemAccesses();
		Iterator<RoleItemAccess> iterator=roleItemAccess.iterator();
		while(iterator.hasNext())
		{
			RoleItemAccess ria=iterator.next();
			if(ria.getName().compareToIgnoreCase(name)==0)
			{
				return ria.getId();				
			}			
		}
		return 0L;
	}


	@Override
	@SuppressWarnings("rawtypes")
	public String generateStudentPDFUsingTemplate(String templateName, List<Long> studId, Long semesterId)
	{
		Log.info("Call generateStudentPDFUsingTemplate : " + studId.size());
		Log.info("Semester Proxy ID: " + semesterId);
		
		Document document = null;
		File file = null;
		PdfWriter writer =null;
		HTMLWorker htmlWorker = null;
		
		String fileContents = null;
		String mailMessage = null;	
		String tempMailMessage=null;
		
		String titleContentStud;	// [TITLE SEPARATOR]
		String tempOsceDayContentStud; // [OSCE_DAY SEPARATOR]
		String osceDayContentStud; // [OSCE_DAY SEPARATOR]
		String tempScheduleContentStud; // [SCHEDULE SEPERATOR]
		String scheduleContentStud; // [SCHEDULE SEPERATOR]		
		String tempBreakContentStud; //[BREAK SEPARATOR]
		String breakContentStud; //[BREAK SEPARATOR]
		

		int index = 0;
		try {
			
			document = new Document();
			file = new File("Student.pdf");
			writer = PdfWriter.getInstance(document, new FileOutputStream(file));
			
			document.open();
			
			htmlWorker = new HTMLWorker(document);
			
			fileContents = this.getTemplateContent(templateName)[1];

			
			titleContentStud=new String();
			tempOsceDayContentStud=new String();
			osceDayContentStud=new String();
			tempScheduleContentStud=new String();
			scheduleContentStud=new String();				
			tempBreakContentStud=new String();
			breakContentStud=new String();
			
			for(int i=0;i<studId.size();i++)
			{							
				Log.info("File Content: " + fileContents);
				Log.info("After Modification: " + mailMessage);
								
				Student student= Student.findStudent(studId.get(i));
				
				mailMessage="";
				titleContentStud="";
				tempOsceDayContentStud="";
				osceDayContentStud="";
				tempScheduleContentStud="";				
				scheduleContentStud="";
				tempBreakContentStud="";
				breakContentStud="";
				
				mailMessage = fileContents;	
				tempMailMessage=fileContents;
								
				titleContentStud=mailMessage.substring(mailMessage.indexOf("[TITLE SEPARATOR]"), mailMessage.indexOf("[TITLE SEPARATOR.]"));
				tempOsceDayContentStud=mailMessage.substring(mailMessage.indexOf("[OSCE_DAY SEPARATOR]"), mailMessage.indexOf("[OSCE_DAY SEPARATOR.]"));
				tempScheduleContentStud=mailMessage.substring(mailMessage.indexOf("[SCHEDULE SEPARATOR]"), mailMessage.indexOf("[SCHEDULE SEPARATOR.]"));				
				tempBreakContentStud=mailMessage.substring(mailMessage.indexOf("[BREAK SEPARATOR]"), mailMessage.indexOf("[BREAK SEPARATOR.]"));				
				
				titleContentStud=titleContentStud.replace("[TITLE SEPARATOR]","");
				titleContentStud=titleContentStud.replace("[TITLE SEPARATOR.]","");
				tempOsceDayContentStud=tempOsceDayContentStud.replace("[OSCE_DAY SEPARATOR]","");
				tempOsceDayContentStud=tempOsceDayContentStud.replace("[OSCE_DAY SEPARATOR.]","");
				tempBreakContentStud=tempBreakContentStud.replace("[BREAK SEPARATOR]", "");
				tempBreakContentStud=tempBreakContentStud.replace("[BREAK SEPARATOR.]", "");
				tempScheduleContentStud=tempScheduleContentStud.replace("[SCHEDULE SEPARATOR]", "");
				tempScheduleContentStud=tempScheduleContentStud.replace("[SCHEDULE SEPARATOR.]", "");				
										
				titleContentStud = titleContentStud.replace("[NAME]",util.getEmptyIfNull(student.getName()));
				titleContentStud = titleContentStud.replace("[PRENAME]",util.getEmptyIfNull(student.getPreName()));									
				mailMessage=titleContentStud;
								
			/*	Set<Assignment> assignment=student.getAssignments();
				Log.info("Total "+assignment.size()+" assignment found for Student " + student.getId());
				
				if(assignment.size()>0)
				{									
					Iterator iterator = assignment.iterator();			
					while ( iterator.hasNext())	// Gives Total Assignment for Student 
					{
						Assignment studAssignment = (Assignment) iterator.next();
						Log.info("Assignment Id:" + studAssignment.getId());	*/
						
						List<Long> distinctOsceDay = Assignment.findDistinctOsceDayByStudentId(student.getId());
						Log.info("Total "+ distinctOsceDay.size()+"Distinct Osce Day for Student "+ student.getId());
						
						Iterator distinctOsceDayIterator=distinctOsceDay.iterator();
						while(distinctOsceDayIterator.hasNext())
						{
							scheduleContentStud="";	
							osceDayContentStud="";	
							Long osceDay=(Long)distinctOsceDayIterator.next();
							//Log.info("Distinct Osce Day Id: " + osceDay);
							List<Assignment> assignmentsByOsceDayForParticularStudent=Assignment.findAssignmentsByOsceDayAndStudent(osceDay, student.getId());
							//Log.info("Assignment for Osce Day " +osceDay +" Total "+assignmentsByOsceDayForParticularStudent.size());
							Iterator assignmentIterator=assignmentsByOsceDayForParticularStudent.iterator();
							
							OsceDay osceDayEntity=OsceDay.findOsceDay(osceDay);
							
							tempOsceDayContentStud=tempMailMessage.substring(tempMailMessage.indexOf("[OSCE_DAY SEPARATOR]"), tempMailMessage.indexOf("[OSCE_DAY SEPARATOR.]"));
							tempOsceDayContentStud=tempOsceDayContentStud.replace("[OSCE_DAY SEPARATOR]","");
							tempOsceDayContentStud=tempOsceDayContentStud.replace("[OSCE_DAY SEPARATOR.]","");
							
							//tempOsceDayContentStud=tempOsceDayContentStud.replace("[OSCE]",osceDayEntity.getOsce().getName());							
							String osceLable = osceDayEntity.getOsce().getStudyYear()==null?"":osceDayEntity.getOsce().getStudyYear() + "." + osceDayEntity.getOsce().getSemester().getSemester().name();
							tempOsceDayContentStud=tempOsceDayContentStud.replace("[OSCE]",osceLable);
							
							String date=""+osceDayEntity.getOsceDate().getDate()+"-"+(osceDayEntity.getOsceDate().getMonth()+1) +"-"+(osceDayEntity.getOsceDate().getYear()+1900);
							Log.info("Date "+date+" for Osce Day: " + osceDayEntity.getId());
							tempOsceDayContentStud=tempOsceDayContentStud.replace("[DATE]",date);														
							osceDayContentStud=osceDayContentStud+tempOsceDayContentStud;
							Log.info("OSCE DAY CONTENT: " + osceDayContentStud);
							mailMessage=mailMessage+osceDayContentStud;
														
							while(assignmentIterator.hasNext())
							{
								Assignment assignment=(Assignment)assignmentIterator.next();
								Log.info("assignment Id: " + assignment.getId());
								
								tempScheduleContentStud=tempMailMessage.substring(tempMailMessage.indexOf("[SCHEDULE SEPARATOR]"), tempMailMessage.indexOf("[SCHEDULE SEPARATOR.]"));
								tempScheduleContentStud=tempScheduleContentStud.replace("[SCHEDULE SEPARATOR]", "");
								tempScheduleContentStud=tempScheduleContentStud.replace("[SCHEDULE SEPARATOR.]", "");
								
								String startTime=""+(assignment.getTimeStart().getHours())+":"+(assignment.getTimeStart().getMinutes())+":"+(assignment.getTimeStart().getSeconds());
								String endTime=""+(assignment.getTimeEnd().getHours())+":"+(assignment.getTimeEnd().getMinutes())+":"+(assignment.getTimeEnd().getSeconds());
								
								tempScheduleContentStud=tempScheduleContentStud.replace("[START TIME]", startTime);
								tempScheduleContentStud=tempScheduleContentStud.replace("[END TIME]", endTime);	
								
								tempScheduleContentStud=tempScheduleContentStud.replace("[POST]", assignment.getOscePostRoom().getOscePost().getId().toString());
								tempScheduleContentStud=tempScheduleContentStud.replace("[ROOM]", assignment.getOscePostRoom().getRoom().getRoomNumber().toString());
								
								scheduleContentStud=scheduleContentStud+tempScheduleContentStud;
							}
							
							Log.info("SCHEDULE CONTENT: " + scheduleContentStud);
							mailMessage=mailMessage+scheduleContentStud+"<br>";		
							
							tempBreakContentStud=tempBreakContentStud.replace("[LONG BREAK]", osceDayEntity.getOsce().getLongBreak().toString());
							tempBreakContentStud=tempBreakContentStud.replace("[LUNCH BREAK]", osceDayEntity.getOsce().getLunchBreak().toString());
							breakContentStud=tempBreakContentStud;
							Log.info("BREAK CONTENT: " + breakContentStud);
							mailMessage=mailMessage+breakContentStud;
						}
				/*	}
				}
				else
				{
					Log.info("No Assignment for Student");
				}*/
				
				
				htmlWorker.parse(new StringReader(mailMessage));
				document.newPage();
			}
						
			document.close();
			
			return "Student.pdf";

			
		}
		 catch (Exception e) {
				Log.error("in SummoningsServiceImpl.generateMailPDFUsingTemplate: "	+ e.getMessage());
				e.printStackTrace();
				return null;
			}finally{
				document = null;
				file = null;
				writer =null;
				htmlWorker = null;
				fileContents = null;
			}
			
		
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public String generateExaminerPDFUsingTemplate(String templateName, List<Long> examinerId, Long semesterId)
	{
		Log.info("Call generateExaminerPDFUsingTemplate : " + examinerId.size());
		Log.info("Semester Proxy ID: " + semesterId);
		
		List<Long> idCheckList=new ArrayList<Long>();
		
		Document document = null;
		File file = null;
		PdfWriter writer =null;
		HTMLWorker htmlWorker = null;
		
		String fileContents = null;
		String mailMessage = null;	
		String tempMailMessage=null;
		
		String titleContentExaminer;	// [TITLE SEPARATOR]
		String tempOsceDayContentExaminer; // [OSCE_DAY SEPARATOR]
		String osceDayContentExaminer; // [OSCE_DAY SEPARATOR]
		String tempScheduleContentExaminer; // [SCHEDULE SEPERATOR]
		String scheduleContentExaminer; // [SCHEDULE SEPERATOR]		
		String tempBreakContentExaminer; //[BREAK SEPARATOR]
		String breakContentExaminer; //[BREAK SEPARATOR]
		
		String scriptDetail=new String();
		
		try
		{
			
			document = new Document();
			file = new File("Examiner.pdf");
			writer = PdfWriter.getInstance(document, new FileOutputStream(file));
			
			document.open();
			
			htmlWorker = new HTMLWorker(document);
			
			fileContents = this.getTemplateContent(templateName)[1];

			
			titleContentExaminer=new String();
			tempOsceDayContentExaminer=new String();
			osceDayContentExaminer=new String();
			tempScheduleContentExaminer=new String();
			scheduleContentExaminer=new String();				
			tempBreakContentExaminer=new String();
			breakContentExaminer=new String();
			
			for(int i=0;i<examinerId.size();i++)
			{							
				Log.info("File Content: " + fileContents);
				Log.info("After Modification: " + mailMessage);
								
				Doctor examiner= Doctor.findDoctor(examinerId.get(i));
				
				mailMessage="";
				titleContentExaminer="";
				tempOsceDayContentExaminer="";
				osceDayContentExaminer="";
				tempScheduleContentExaminer="";				
				scheduleContentExaminer="";
				tempBreakContentExaminer="";
				breakContentExaminer="";
				
				mailMessage = fileContents;	
				tempMailMessage=fileContents;
								
				titleContentExaminer=mailMessage.substring(mailMessage.indexOf("[TITLE SEPARATOR]"), mailMessage.indexOf("[TITLE SEPARATOR.]"));
				tempOsceDayContentExaminer=mailMessage.substring(mailMessage.indexOf("[OSCE_DAY SEPARATOR]"), mailMessage.indexOf("[OSCE_DAY SEPARATOR.]"));
				tempScheduleContentExaminer=mailMessage.substring(mailMessage.indexOf("[SCHEDULE SEPARATOR]"), mailMessage.indexOf("[SCHEDULE SEPARATOR.]"));				
				tempBreakContentExaminer=mailMessage.substring(mailMessage.indexOf("[BREAK SEPARATOR]"), mailMessage.indexOf("[BREAK SEPARATOR.]"));				
				
				titleContentExaminer=titleContentExaminer.replace("[TITLE SEPARATOR]","");
				titleContentExaminer=titleContentExaminer.replace("[TITLE SEPARATOR.]","");
				tempOsceDayContentExaminer=tempOsceDayContentExaminer.replace("[OSCE_DAY SEPARATOR]","");
				tempOsceDayContentExaminer=tempOsceDayContentExaminer.replace("[OSCE_DAY SEPARATOR.]","");
				tempBreakContentExaminer=tempBreakContentExaminer.replace("[BREAK SEPARATOR]", "");
				tempBreakContentExaminer=tempBreakContentExaminer.replace("[BREAK SEPARATOR.]", "");
				tempScheduleContentExaminer=tempScheduleContentExaminer.replace("[SCHEDULE SEPARATOR]", "");
				tempScheduleContentExaminer=tempScheduleContentExaminer.replace("[SCHEDULE SEPARATOR.]", "");				
										
				titleContentExaminer = titleContentExaminer.replace("[NAME]",util.getEmptyIfNull(examiner.getName()));
				titleContentExaminer = titleContentExaminer.replace("[PRENAME]",util.getEmptyIfNull(examiner.getPreName()));									
				
				mailMessage=titleContentExaminer;				
				writeInPDFFile(titleContentExaminer,document);
				
				List<Long> distinctOsceDay = Assignment.findDistinctOsceDayByExaminerId(examiner.getId());
				Log.info("Total "+ distinctOsceDay.size()+"Distinct Osce Day for Examiner "+ examiner.getId());
				
				Iterator distinctOsceDayIterator=distinctOsceDay.iterator();				
				while(distinctOsceDayIterator.hasNext())
				{							
					scheduleContentExaminer="";							
					Long osceDay=(Long)distinctOsceDayIterator.next();					
					
					//&&List<Long> distinctPatientInRoleByOsceDayAndExaminer=Assignment.findDistinctPIRByOsceDayAndExaminer(osceDay,  examiner.getId());
					List<Long> distinctPatientInRoleByOsceDayAndExaminer=Assignment.findDistinctoscePostRoomByOsceDayAndExaminer(osceDay,  examiner.getId());
					Iterator distinctPatientInRoleByOsceDayAndExaminerIterator=distinctPatientInRoleByOsceDayAndExaminer.iterator();
					
					int xyz=0;
					while(distinctPatientInRoleByOsceDayAndExaminerIterator.hasNext())
					{
						osceDayContentExaminer="";	
						scriptDetail="";
						
						Long patientInRoleId=(Long)distinctPatientInRoleByOsceDayAndExaminerIterator.next();
								
						Log.info("~~~Distinct PatientInRole By OsceDayAndExaminer" + patientInRoleId + "Osce Day: " + osceDay);
						
						Log.info("~~~~OSCE DAY: " + osceDay);
						
						OsceDay osceDayEntity=OsceDay.findOsceDay(osceDay);
						
						tempOsceDayContentExaminer=tempMailMessage.substring(tempMailMessage.indexOf("[OSCE_DAY SEPARATOR]"), tempMailMessage.indexOf("[OSCE_DAY SEPARATOR.]"));
						tempOsceDayContentExaminer=tempOsceDayContentExaminer.replace("[OSCE_DAY SEPARATOR]","");
						tempOsceDayContentExaminer=tempOsceDayContentExaminer.replace("[OSCE_DAY SEPARATOR.]","");

						//tempOsceDayContentExaminer=tempOsceDayContentExaminer.replace("[OSCE]",osceDayEntity.getOsce().getName());
						
						String osceLable = osceDayEntity.getOsce().getStudyYear()==null?"":osceDayEntity.getOsce().getStudyYear() + "." + osceDayEntity.getOsce().getSemester().getSemester().name();
						tempOsceDayContentExaminer=tempOsceDayContentExaminer.replace("[OSCE]",osceLable);
						
						String date=""+osceDayEntity.getOsceDate().getDate()+"-"+(osceDayEntity.getOsceDate().getMonth()+1) +"-"+(osceDayEntity.getOsceDate().getYear()+1900);
						tempOsceDayContentExaminer=tempOsceDayContentExaminer.replace("[DATE]",date);

						//&&List<Assignment> assignment=Assignment.findAssignmentsByOsceDayExaminerAndPIR(osceDay, examiner.getId(),patientInRoleId);
						List<Assignment> assignment=Assignment.findAssignmentsByOsceDayExaminerAndOscePostRoomId(osceDay, examiner.getId(),patientInRoleId);
						
						//List<Assignment> assignment=Assignment.findAssignmentsByOsceDayExaminerAndPIR(osceDay, examiner.getId(),3);
						Iterator assignmentIterator=assignment.iterator();
									
						scheduleContentExaminer="";						
						while(assignmentIterator.hasNext())
						{
														
							Assignment assignmentExaminer=(Assignment)assignmentIterator.next();
							Log.info("Assignment: "+ assignmentExaminer.getId() + " on OSCE DAY: " + osceDay + " for Examiner " + examiner.getId() + " in PatientInRole " + patientInRoleId);
							tempOsceDayContentExaminer=tempOsceDayContentExaminer.replace("[ROLE]",""+assignmentExaminer.getOscePostRoom().getOscePost().getStandardizedRole().getLongName());							
							
							tempScheduleContentExaminer=tempMailMessage.substring(tempMailMessage.indexOf("[SCHEDULE SEPARATOR]"), tempMailMessage.indexOf("[SCHEDULE SEPARATOR.]"));
							tempScheduleContentExaminer=tempScheduleContentExaminer.replace("[SCHEDULE SEPARATOR]", "");
							tempScheduleContentExaminer=tempScheduleContentExaminer.replace("[SCHEDULE SEPARATOR.]", "");
							
							String startTime=""+(assignmentExaminer.getTimeStart().getHours())+":"+(assignmentExaminer.getTimeStart().getMinutes())+":"+(assignmentExaminer.getTimeStart().getSeconds());
							String endTime=""+(assignmentExaminer.getTimeEnd().getHours())+":"+(assignmentExaminer.getTimeEnd().getMinutes())+":"+(assignmentExaminer.getTimeEnd().getSeconds());
							
							tempScheduleContentExaminer=tempScheduleContentExaminer.replace("[START TIME]", startTime);
							tempScheduleContentExaminer=tempScheduleContentExaminer.replace("[END TIME]", endTime);	
							
							tempScheduleContentExaminer=tempScheduleContentExaminer.replace("[POST]", assignmentExaminer.getOscePostRoom().getOscePost().getId().toString());
							tempScheduleContentExaminer=tempScheduleContentExaminer.replace("[ROOM]", assignmentExaminer.getOscePostRoom().getRoom().getRoomNumber().toString());
							
							scheduleContentExaminer=scheduleContentExaminer+tempScheduleContentExaminer;
						}
						Log.info("Osce Day Content: " + osceDayContentExaminer);
						osceDayContentExaminer=osceDayContentExaminer+tempOsceDayContentExaminer;
						mailMessage=mailMessage+osceDayContentExaminer;
						writeInPDFFile(osceDayContentExaminer,document);
						
						Log.info("Schedule Content: " + scheduleContentExaminer);
						mailMessage=mailMessage+scheduleContentExaminer;
						writeInPDFFile(scheduleContentExaminer,document);
						
						Paragraph roleTemplateEL = new Paragraph();							
						addEmptyLine(roleTemplateEL, 1);
						document.add(roleTemplateEL);
						
						
						int tempEx1=0;
						Iterator assignmentIterator1=assignment.iterator();
						while(assignmentIterator1.hasNext())
						{
														
							Assignment assignmentExaminer=(Assignment)assignmentIterator1.next();
							Log.info("Assignment1 : "+ assignmentExaminer.getId() + " on OSCE DAY: " + osceDay + " for Examiner " + examiner.getId() + " in PatientInRole " + patientInRoleId);
							Log.info("tempEx1 : " + tempEx1);
							if(tempEx1==0)
							{								
								Long roleItemAccessId=findIdforRoleItemAccessByName(examiner.getName());
								Log.info("Role Item Access for Examiner1 : " + examiner.getName() + "is " + roleItemAccessId);
								
								//Long roleItemAccessId=1L;
								//scriptDetail+=createRoleScriptDetails(assignmentExaminer.getOscePostRoom().getOscePost().getStandardizedRole(),roleItemAccessId,document);
								createRoleScriptDetails(assignmentExaminer.getOscePostRoom().getOscePost().getStandardizedRole(),roleItemAccessId,document);
								Log.info("Script Detail1 : " + scriptDetail);
								
							}
							tempEx1++;														
						}
						
						//scriptDetail=scriptDetail.replace("[]","");
						//mailMessage=mailMessage+scriptDetail;
					}
															
					
				}
				
				//htmlWorker.parse(new StringReader(mailMessage));
				document.newPage();
			}
			
			document.close();
			return "Examiner.pdf";
		}
		catch (Exception e) 
		{
			Log.error("in SummoningsServiceImpl.generateMailPDFUsingTemplate: "	+ e.getMessage());
			e.printStackTrace();
			return null;
		}
		finally
		{
			document = null;
			file = null;
			writer =null;
			htmlWorker = null;
			fileContents = null;
		}		
		
	}
	
	private void createRoleScriptDetails(StandardizedRole standardizedRole, Long roleItemAccessId, Document document) 
	{
		String scriptDetail=new String();
		scriptDetail="";
		Paragraph detailsTemplate = new Paragraph();
		if (standardizedRole.getRoleTemplate() != null) 
		{
			detailsTemplate.add(new Chunk("Role Script Template" + ": "
					+ standardizedRole.getRoleTemplate().getTemplateName(),
					paragraphTitleFont));

			addEmptyLine(detailsTemplate, 1);

			try {
				document.add(detailsTemplate);
			} catch (DocumentException e) {
				Log.error("in PdfUtil.addDetails(): " + e.getMessage());
			}

			//createRoleScriptDetails();
		}
		
		
		List<RoleBaseItem> roleBaseItems = standardizedRole.getRoleTemplate().getRoleBaseItem();

		Log.info("roleBaseItems size " + roleBaseItems.size());

		if (roleBaseItems.size() > 0) 
		{
			//isValueAvailable[2] = true;
			for (RoleBaseItem roleBaseItem : roleBaseItems) 
			{
				Set<RoleItemAccess> roleItemAccesses = roleBaseItem.getRoleItemAccess();

				boolean isAccessGiven = false;
				/*if (roleItemAccessId == 0L) 
				{
					isAccessGiven = true;
				} 
				else 
				{*/
					for (Iterator<RoleItemAccess> iterator = roleItemAccesses.iterator(); iterator.hasNext();) 
					{
						RoleItemAccess roleItemAccess = (RoleItemAccess) iterator.next();
						if (roleItemAccess.getId().longValue() == roleItemAccessId.longValue()) 
						{
							isAccessGiven = true;
							break;
						}

					}
				/*}*/
				Log.info("isAccessGiven : " + isAccessGiven);
				if (isAccessGiven) 
				{
					String itemName = (roleBaseItem.getItem_name() != null) ? roleBaseItem.getItem_name() : "-";
					Paragraph details = new Paragraph();

					details.add(new Chunk("Role Base Item Name" + ": "+ itemName, subTitleFont));
					//details.add(new Chunk("Role Base Item Name" + ": "+ itemName));
					//scriptDetail=scriptDetail+"<br>"+"Role Base Item Name: " + itemName+"<br>";					
					try 
					{
						document.add(details);
					} 
					catch (DocumentException e) 
					{
						Log.error("in PdfUtil.addDetails(): " + e.getMessage());
					}
					
					

					if (roleBaseItem.getItem_defination() == ItemDefination.table_item) 
					{

						PdfPTable roleScriptTable = new PdfPTable(2);

						roleScriptTable.addCell("Item Name: ");
						roleScriptTable.addCell("Item Value: ");
						// roleBaseItem.getRoleTableItem().get(0).getRoleTableItemValue().
						boolean isRoleScriptAdded = false;

						List<RoleTableItem> roleTableItemList = roleBaseItem.getRoleTableItem();

						for (Iterator<RoleTableItem> iterator = roleTableItemList.iterator(); iterator.hasNext();) 
						{
							RoleTableItem roleTableItem = (RoleTableItem) iterator.next();

							Set<RoleTableItemValue> roleTableItemValueSet = roleTableItem.getRoleTableItemValue();

							for (Iterator<RoleTableItemValue> iterator2 = roleTableItemValueSet.iterator(); iterator2.hasNext();) 
							{
								RoleTableItemValue roleTableItemValue = (RoleTableItemValue) iterator2.next();
								if (roleTableItemValue.getStandardizedRole().getId() == standardizedRole.getId()) 
								{

									isRoleScriptAdded = true;
									//roleScriptTable.addCell(getPdfCell(roleTableItem.getItemName()));
									//roleScriptTable.addCell(getPdfCell(roleTableItemValue.getValue()));
									roleScriptTable.addCell(roleTableItem.getItemName());
									roleScriptTable.addCell(roleTableItemValue.getValue());
								}
							}
						}

						Log.info("isRoleScriptAdded " +isRoleScriptAdded );
						
						if (isRoleScriptAdded) 
						{
							//roleScriptTable.addCell(getPdfCellBold(" "));
							// roleScriptTable.addCell(getPdfCell(" "));
							//roleScriptTable.addCell("PDF CELL STRING");
							//roleScriptTable.addCell("PDF CELL STRING");

							roleScriptTable.setWidthPercentage(100);

							Log.info("Table Detail Row: "
									+ roleScriptTable.size());
							Log.info("Table Detail Column: "
									+ roleScriptTable.getNumberOfColumns());

							// try
							// {
							// document.add(createPara(roleScriptTable, ""));
							// scriptDetail=scriptDetail+roleScriptTable;
							Log.info("Write in PDF file ");
							////writeInPDFFile(scriptDetail, document);

							roleScriptTable.setWidthPercentage(100);
							try 
							{
								Log.info(" Write table in pdf file");
								Paragraph detailTableEL = new Paragraph();
								addEmptyLine(detailTableEL, 1);
								document.add(detailTableEL);
								
								document.add(createPara(roleScriptTable, ""));
							} 
							catch (DocumentException e) 
							{
								Log.error("in PdfUtil.addDetails(): "+ e.getMessage());
							}
							/*}
							catch (DocumentException e) 
							{
								Log.error("in PdfUtil.addDetails(): "+ e.getMessage());
							}*/
						}
					}
					else if (roleBaseItem.getItem_defination() == ItemDefination.rich_text_item) 
					{
						List<RoleSubItemValue> roleTableItemList = roleBaseItem.getRoleSubItem();

						for (Iterator<RoleSubItemValue> iterator = roleTableItemList.iterator(); iterator.hasNext();) 
						{
							RoleSubItemValue roleSubItemValue = (RoleSubItemValue) iterator.next();
							if (roleSubItemValue.getStandardizedRole().getId().longValue() == standardizedRole.getId().longValue()) 
							{
								String string = roleSubItemValue.getItemText();
								Log.error("getItemText : " + string);

								HTMLWorker htmlWorker = new HTMLWorker(document);
								try 
								{
									htmlWorker.parse(new StringReader(string));
									addEmptyLine(details, 1);
									details = new Paragraph();									
									document.add(details);
									scriptDetail=scriptDetail+details;
									scriptDetail=scriptDetail+string;
									

								} 
								catch (Exception e) 
								{
									Log.error("in PdfUtil.addDetails(): "+ e.getMessage());
								}

							}
						}

					}

				}

			}
		}		

	}
	
	private void addEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
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
	
	/*@Override
	public String getTemplateContent(String templateName)
	{
		Log.info("get file Name: " + templateName);	
		File file = null;
		try {
			
			file = new File("C:\\PRINTPLAN_TEMPLATE\\"+templateName);
			Log.info("get Absolute file Name: " + file.getAbsolutePath());			
			if(file.isFile())
			{
				return FileUtils.readFileToString(file);
			}
			else
			{
				file = new File("C:\\PRINTPLAN_TEMPLATE\\DefaultTemplateSP.txt");
				return FileUtils.readFileToString(file);
			}
				
			
		} catch (Exception e) {
			Log.error(e.getMessage());
			e.printStackTrace();
			return null;
		}finally{
			file = null;
		}
		
	}*/
	
	 @Override
		public String[] getTemplateContent(String templateName){
			
			File file = null;
			try {
				
				file = new File(OsMaFilePathConstant.PRINT_SCHEDULE_TEMPLATE+ templateName);
				
				if(file.isFile()){
					
					return new String[]{templateName,FileUtils.readFileToString(file)};
				}else{
					
					file = new File(OsMaFilePathConstant.PRINT_SCHEDULE_TEMPLATE+"DefaultTemplateSP.txt");
					
					if(file.isFile())
						return new String[]{"DefaultTemplateSP.txt",FileUtils.readFileToString(file)};
					else
						return new String[]{"",""};
				}
					
				
			} catch (Exception e) {
				Log.error(e.getMessage());
				e.printStackTrace();
				return null;
			}finally{
				file = null;
			}
			
		}
	
	@Override
	public String[] getStudTemplateContent(String templateName)
	{
		Log.info("get file Name: " + templateName);	
		File file = null;
		try {
									
			file = new File(OsMaFilePathConstant.PRINT_SCHEDULE_TEMPLATE+ templateName);
			
			if(file.isFile()){
				
				return new String[]{templateName,FileUtils.readFileToString(file)};
			}else{
				
				file = new File(OsMaFilePathConstant.PRINT_SCHEDULE_TEMPLATE+"DefaultTemplateStud.txt");
				
				if(file.isFile())
					return new String[]{"DefaultTemplateStud.txt",FileUtils.readFileToString(file)};
				else
					return new String[]{"",""};
			}
				
			
		} catch (Exception e) {
			Log.error(e.getMessage());
			e.printStackTrace();
			return null;
		}finally{
			file = null;
		}
		
	}
	
	@Override
	public String[] getExaminerTemplateContent(String templateName)
	{
		Log.info("get file Name: " + templateName);	
		File file = null;
		try {
			
			file = new File(OsMaFilePathConstant.PRINT_SCHEDULE_TEMPLATE+ templateName);
			
			if(file.isFile()){
				
				return new String[]{templateName,FileUtils.readFileToString(file)};
			}else{
				
				file = new File(OsMaFilePathConstant.PRINT_SCHEDULE_TEMPLATE+"DefaultTemplateExaminor.txt");
				
				if(file.isFile())
					return new String[]{"DefaultTemplateExaminor.txt",FileUtils.readFileToString(file)};
				else
					return new String[]{"",""};
			}
				
			
		} catch (Exception e) {
			Log.error(e.getMessage());
			e.printStackTrace();
			return null;
		}finally{
			file = null;
		}
		
	}
	
	@Override
	public Boolean saveTemplate(String templateName,String templateContent)
	{
		Log.info("Go to save Template");
		File file = null;
		try {
			
			file = new File(OsMaFilePathConstant.PRINT_SCHEDULE_TEMPLATE+templateName);
			
			if(file.isFile())
				FileUtils.deleteQuietly(file);
			else
				Log.error("Template file does not exist. New File will be created.");
			
			FileUtils.touch(file);
			FileUtils.writeStringToFile(file, templateContent);
			return true;
			
			
		} catch (Exception e) {
			Log.error(e.getMessage());
			e.printStackTrace();
			return null;
		}finally{
			file = null;
		}
		
	}
	
	@Override
	public Boolean deleteTemplate(String templateName){
		
		File file = null;
		try {
			
			file = new File(OsMaFilePathConstant.PRINT_SCHEDULE_TEMPLATE+templateName);
			
			if(file.isFile()){
				FileUtils.deleteQuietly(file);
				return true;
			}else{
				Log.error("Template file does not exist.");
				return false;
			}
		} catch (Exception e) {
			Log.error(e.getMessage());
			e.printStackTrace();
			return null;
		}finally{
			file = null;
		}
	}




	
}
