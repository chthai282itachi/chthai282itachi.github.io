package ch.unibas.medizin.osce.server.upload;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.JSpinner.DateEditor;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;



import ch.unibas.medizin.osce.domain.Assignment;
import ch.unibas.medizin.osce.domain.Course;
import ch.unibas.medizin.osce.domain.Doctor;
import ch.unibas.medizin.osce.domain.Osce;
import ch.unibas.medizin.osce.domain.OsceDay;
import ch.unibas.medizin.osce.domain.OscePost;
import ch.unibas.medizin.osce.domain.OsceSequence;
import ch.unibas.medizin.osce.domain.PatientInRole;
import ch.unibas.medizin.osce.domain.Student;
import ch.unibas.medizin.osce.server.OsMaFilePathConstant;
import ch.unibas.medizin.osce.shared.OsMaConstant;

public class ExportAssignment  extends HttpServlet {
	
	private static Logger Log = Logger.getLogger(UploadServlet.class);
	
	private ServletConfig servletConfig;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		Log.info("ExportAssignment doGet :osceId :" + request.getParameter("osceId"));
		
		String osceId=request.getParameter("osceId");
		String type=request.getParameter("type");
		String fileName=createHtml(new Long(osceId), new Integer(type));
		
		try{
			
				servletConfig=getServletConfig();
				
				 response.setContentType("application/x-download");
				 if(new Integer(type) ==0)
				    response.setHeader("Content-Disposition", "attachment; filename=" + "assignment_student_"+osceId+".html");
				 else
					 response.setHeader("Content-Disposition", "attachment; filename=" + "assignment_sp_"+osceId+".html");
				   

				Log.info("path :" + fileName);
				//String file=OsMaFilePathConstant.ROLE_IMAGE_FILEPATH+path;
				Log.info(" file :" + fileName);
				
				OutputStream out = response.getOutputStream();
				FileInputStream in = new FileInputStream(fileName);
				
				
				
				byte[] buffer = new byte[4096];
				int length;
				while ((length = in.read(buffer)) > 0){
				    out.write(buffer, 0, length);
				}
				in.close();
				
				File htmlFile=new File(fileName);
				htmlFile.delete();
				out.flush();
			
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		
		 
		
	}
	
	public  String createHtml(Long osceId,int type)
	{
		Osce osce=Osce.findOsce(osceId);
		
		List<OsceDay> osceDays=osce.getOsce_days();
		
		Document doc=createDocument();
		Element root = doc.createElement("osceDays");
		
		//append root to document
		doc.appendChild(root);
		
		int dayOffsetRotation=0;
		int startRotation=0;
		int rotationOffSet=0;
		for(int c=0;c<osceDays.size();c++)
		{
			OsceDay osceDay=osceDays.get(c);
			Element osceDayElement=createEmptyChildNode("osceDay",doc,root);
			
			String osceDayIDNodeValue="Day "+osceDay.getId().toString();
			
			createChildNode("osceDayID", osceDayIDNodeValue, doc, osceDayElement);
			
			List<OsceSequence> osceSequences=osceDay.getOsceSequences();
			
			Element parcoursElement=createEmptyChildNode("parcours",doc,osceDayElement);
			
			for(int a=0;a<osceSequences.size();a++)
			{
				OsceSequence osceSeq=osceSequences.get(a);
				List<Course> courses=osceSeq.getCourses();
				
				if(a!=0)
				{
					startRotation=startRotation+osceSequences.get(a-1).getNumberRotation();
					
				}
				//dayOffsetRotation=dayOffsetRotation+osceSeq.getNumberRotation()+startRotation;
				startRotation=rotationOffSet;
				rotationOffSet = rotationOffSet +  osceSeq.getNumberRotation();
				
				for(int i=0;i<courses.size();i++)
				{
					Course course=courses.get(i);
					Element parcourElement=createEmptyChildNode("parcour",doc,parcoursElement);
					//createChildNode("parcourCss", "accordion-title-selected"+course.getColor(), doc, parcourElement);
					createChildNode("parcourColor", course.getColor(), doc, parcourElement);
					
					Element postsElement=createEmptyChildNode("posts",doc,parcourElement);
					
					List<OscePost> oscePosts=osceSeq.getOscePosts();
					
					for(OscePost oscePost:oscePosts)
					{
						Element postElement=createEmptyChildNode("post",doc,postsElement);
						createChildNode("postName", "Post " +oscePost.getSequenceNumber(), doc, postElement);
						createChildNode("standardizedRole", oscePost.getStandardizedRole().getLongName(), doc, postElement);
					}
					
					//find examiners, rotation and course wise from Assignment table			
					Element rotationsElement=createEmptyChildNode("rotations",doc,parcourElement);
					
					
					
					
					for(int j=startRotation;j<(rotationOffSet);j++)
					{
						
						
						
						Element rotationElement=createEmptyChildNode("rotation",doc,rotationsElement);
						createChildNode("rotationId", "rotation "+(j+1), doc, rotationElement);
						
						//retrieve examiners
						Element examinersElement=createEmptyChildNode("examiners",doc,rotationElement);
						for(OscePost oscePost:oscePosts)
						{
							Element examinerElement=createEmptyChildNode("examiner",doc,examinersElement);
							Assignment assignment=Assignment.findExaminersRoationAndCourseWise(osceDay.getId(), j, course.getId(), oscePost.getId());
							
							Doctor examiner=null;
							if(assignment != null)
							 examiner=assignment.getExaminer();
							
							String examinerName="-";
							if(examiner !=null)
								examinerName=examiner.getPreName() +" "+ examiner.getName();
							else
								examinerName="NA";
							
							createChildNode("examinerName", examinerName, doc, examinerElement);
						}
						
						List<Date> timeStarts=Assignment.findDistinctTimeStartRotationWise(osceDay.getId(), j,type);
						List<Date> timeEnds=null;
						if(type==1)
						{
							timeEnds=Assignment.findDistinctTimeStartRotationWise(osceDay.getId(), j,type);
						}
						
						//retrieve startEndtimes and student for particular course and rotation
						List<Assignment> assignments=Assignment.findAssignmentRotationAndCourseWise(osceDay.getId(), j, course.getId(),type);
						Element startEndTimesElement=createEmptyChildNode("startEndTimes",doc,rotationElement);
						for(int d=0;d<timeStarts.size();d++)
						{
							
							Date timeStart=timeStarts.get(d);
							Date timeEnd=null;
							if(type==1)
								timeEnd=timeEnds.get(d);
							
		
							Element startEndTimeElement=createEmptyChildNode("startEndTime",doc,startEndTimesElement);
							//createChildNode("parcourColor", course.getColor(), doc, startEndTimeElement);
							String timeStartValue=String.format("%tR", timeStart);
									//NumberFormat.getFormat("00").format(timeStart.getHours()) +":" + NumberFormat.getFormat("00").format(timeStart.getMinutes());
							
							Element studentsElement=createEmptyChildNode("students",doc,startEndTimeElement);
							
							Date endTime=null;
							for(int k=0;k<oscePosts.size();k++)
							{
								OscePost oscePost=oscePosts.get(k);
								boolean found=false;
								for(int b=0;b<assignments.size();b++)
								{
									Assignment assignment=assignments.get(b);
									OscePost assignmentPost=assignment.getOscePostRoom().getOscePost();
									
								
										
										
										
											
											
											//for student
											if(oscePost.getId() == assignmentPost.getId() && assignment.getTimeStart().equals(timeStart) && type ==0)
											{
												Element studentElement=createEmptyChildNode("student",doc,studentsElement);
												Student student=assignment.getStudent();
												String studentName="-";
												if(student != null)
													studentName=student.getPreName() +" "+ student.getName();
												else
													studentName="s"+assignment.getSequenceNumber();
												
												endTime=assignment.getTimeEnd();
												
												createChildNode("studentName", studentName, doc, studentElement);
											
												found=true;
												break;
											}
											
											//for SP
											if(timeEnd != null && oscePost.getId() == assignmentPost.getId() && (assignment.getTimeEnd().equals(timeEnd) || assignment.getTimeEnd().after(timeEnd)) &&(assignment.getTimeStart().equals(timeStart) || assignment.getTimeStart().before(timeStart)) && type ==1)
											{
												Element studentElement=createEmptyChildNode("student",doc,studentsElement);
												PatientInRole patientInRole=assignment.getPatientInRole();
												String studentName="-";
												if(patientInRole != null)
													studentName=patientInRole.getPatientInSemester().getStandardizedPatient().getPreName() + " " + patientInRole.getPatientInSemester().getStandardizedPatient().getName();
												else
													studentName="SP"+assignment.getSequenceNumber();
												
												endTime=assignment.getTimeEnd();
												
												createChildNode("studentName", studentName, doc, studentElement);
												
												found=true;
												break;
											}
											
											
										/*	else
											{
												Element studentElement=createEmptyChildNode("student",doc,studentsElement);
												createChildNode("studentName", "-", doc, studentElement);
											}*/
										/*	else if(k==oscePosts.size()-1)
											{
												Element studentElement=createEmptyChildNode("student",doc,studentsElement);
												createChildNode("studentName", "-", doc, studentElement);
												movetoNext=true;
											//	break;
											}
										*/
								/*		if(movetoNext)
										{
											break;
										}
									*/
								}
								
								if(!found && type==0)
								{
									Element studentElement=createEmptyChildNode("student",doc,studentsElement);
									createChildNode("studentName", "NA", doc, studentElement);
								}
								else if(!found && endTime !=null )
								{
									Element studentElement=createEmptyChildNode("student",doc,studentsElement);
									createChildNode("studentName", "NA", doc, studentElement);
								}
							}
							if(type==0)
							{
								timeStartValue=timeStartValue +"-"+ String.format("%tR", endTime);
								createChildNode("startEndTimeValue", timeStartValue, doc, startEndTimeElement);
							}
							else if(endTime !=null)
							{
								timeStartValue=timeStartValue +"-"+ String.format("%tR", endTime);
								createChildNode("startEndTimeValue", timeStartValue, doc, startEndTimeElement);
							}
						}
					}
				}
			}
			
			
		}
		
		return saveXML(doc);
	}
	
	public String saveXML(Document doc)
	{
		try{
			TransformerFactory factory = TransformerFactory.newInstance();
	        Transformer transformer = factory.newTransformer();
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

	        StringWriter sw = new StringWriter();
	        StreamResult result = new StreamResult(sw);
	        DOMSource source = new DOMSource(doc);
	        transformer.transform(source, result);
	        String xmlString = sw.toString();

	       // File file = new File("osMaEntry/gwt/unibas/"+System.currentTimeMillis()+".xml");
	        String path=getServletConfig().getServletContext().getRealPath(OsMaFilePathConstant.assignmentHTML);
	        String fileName=path+System.currentTimeMillis()+".xml";
	        
	        
	        File file = new File(fileName);
	        file.createNewFile();
	        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true)));
	        bw.write(xmlString);
	        bw.flush();
	        bw.close();
	        
	        
	        String htmlFileName=convertXmlToHtml(fileName);
	        
	        return htmlFileName;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String convertXmlToHtml(String fileName)
	{
		 try
	        {
			 
			 	
			 	
	            TransformerFactory tFactory = TransformerFactory.newInstance();
	            String xslPath=getServletConfig().getServletContext().getRealPath(OsMaFilePathConstant.assignmentXslPath);
	            Source xslDoc = new StreamSource(xslPath);
	            Source xmlDoc = new StreamSource(fileName);
	            
	            String path=getServletConfig().getServletContext().getRealPath(OsMaFilePathConstant.assignmentHTML);
	            String outputFileName =path +System.currentTimeMillis()+".html";
	            OutputStream htmlFile = new FileOutputStream(outputFileName);

	            Transformer transformer = tFactory.newTransformer(xslDoc);
	            transformer.transform(xmlDoc, new StreamResult(htmlFile));
	            htmlFile.close();
	            File xmlFile=new File(fileName);
	            xmlFile.delete();
	           return outputFileName;
	        }
	        catch(Exception e)
	        {
	            e.printStackTrace();
	            return null;
	        }
	}
	
	public  Element createChildNode(String nodeName,String nodeValue,Document doc,Element parent)
	{
		 Element element = doc.createElement(nodeName);//create  node
		 parent.appendChild(element);	//append to its parent
	        Text text2 = doc.createTextNode(nodeValue);	//create Text node/ value
	        element.appendChild(text2); 
	        return element;
	}
	public Element createEmptyChildNode(String nodeName,Document doc,Element parent)
	{
		 Element element = doc.createElement(nodeName);//create  node
		 parent.appendChild(element);	//append to its parent
	      //  Text text2 = doc.createTextNode(nodeValue);	//create Text node/ value
	    //    element.appendChild(text2); 
	        return element;
	}
	
	public  Document createDocument()
	{
		try{
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();
        return doc;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
        
	}

}