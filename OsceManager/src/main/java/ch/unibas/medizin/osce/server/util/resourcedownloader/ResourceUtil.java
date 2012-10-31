package ch.unibas.medizin.osce.server.util.resourcedownloader;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.io.output.ByteArrayOutputStream;

import ch.unibas.medizin.osce.domain.AdvancedSearchCriteria;
import ch.unibas.medizin.osce.domain.StandardizedPatient;
import ch.unibas.medizin.osce.domain.StandardizedRole;
import ch.unibas.medizin.osce.shared.ResourceDownloadProps;
import ch.unibas.medizin.osce.shared.Sorting;

import com.allen_sauer.gwt.log.client.Log;

import flexjson.JSONDeserializer;

public class ResourceUtil {

	public static void setResource(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		int ordinal = Integer.parseInt(request
				.getParameter(ResourceDownloadProps.ENTITY));
		ResourceDownloadProps.Entity entity =  ResourceDownloadProps.Entity.values()[ordinal];
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		String fileName = "default.pdf";

		switch (entity) {
		case STANDARDIZED_PATIENT: {
			fileName = setStandardizedPatientResource(request, os);
			break;
		}

		case STANDARDIZED_PATIENT_EXPORT: {
			fileName = setStandardizedPatientExportResource(request, response,
					os);
			break;
		}
		case STANDARDIZED_ROLE: {
			fileName = setStandardizedRoleResource(request, os);
			break;
		}

		default: {
			Log.info("Error in entity : " + entity);
			break;
		}
		}

		sendFile(response, os.toByteArray(), fileName);
		os = null;
	}

//	private static String setStandardizedPatientExportResource(
//			HttpServletRequest request, HttpServletResponse response,
//			ByteArrayOutputStream os) {
//
//		HttpSession session = request.getSession();
//		
//		String name = (String) session.getAttribute(ResourceDownloadProps.NAME);
//		Sorting order = (Sorting) session.getAttribute(ResourceDownloadProps.SORT_ORDER);
//		String quickSearchTerm = (String) session
//				.getAttribute(ResourceDownloadProps.QUICK_SEARCH_TERM);
//		List<String> searchThrough = (List<String>) session
//				.getAttribute(ResourceDownloadProps.SEARCH_THROUGH_KEY);	
//		List<AdvancedSearchCriteria> searchCriterias  = (List<AdvancedSearchCriteria>) session
//				.getAttribute(ResourceDownloadProps.SEARCH_CRITERIA_MASTER_KEY);
//		int rangeStart = (Integer) session
//				.getAttribute(ResourceDownloadProps.RANGE_START);
//		int rangeLength = (Integer) session
//				.getAttribute(ResourceDownloadProps.RANGE_LENGTH);	
//
//		String fileName = "default.csv";
//		if(name != null && order != null && quickSearchTerm != null) {
//			fileName = StandardizedPatient
//					.getCSVMapperFindPatientsByAdvancedSearchAndSortUsingServlet(
//							name, order, quickSearchTerm, searchThrough,
//							searchCriterias, rangeStart,
//							rangeLength, os);
//			session.removeAttribute(ResourceDownloadProps.NAME);
//			session.removeAttribute(ResourceDownloadProps.SORT_ORDER);
//			session.removeAttribute(ResourceDownloadProps.QUICK_SEARCH_TERM);
//			session.removeAttribute(ResourceDownloadProps.SEARCH_THROUGH_KEY);
//			session.removeAttribute(ResourceDownloadProps.SEARCH_CRITERIA_MASTER_KEY);
//			session.removeAttribute(ResourceDownloadProps.RANGE_START);
//			session.removeAttribute(ResourceDownloadProps.RANGE_LENGTH);
//		}
//		
//		return fileName;
//	}

	private static String setStandardizedPatientExportResource(
			HttpServletRequest request, HttpServletResponse response,
			ByteArrayOutputStream os) {

		
		HttpSession session = request.getSession();
		String fileName = "default.csv";
		
		if(session.getAttribute(ResourceDownloadProps.SP_LIST) != null) {
			List<Long> ids = (List<Long>) session.getAttribute(ResourceDownloadProps.SP_LIST);
	
			
			fileName = StandardizedPatient
					.getCSVMapperFindPatientsByAdvancedSearchAndSortForSP(ids,
						os);
			session.removeAttribute(ResourceDownloadProps.SP_LIST);
		}
		
		return fileName;
	}
	private static String getPayload(List<Cookie> cookies, final String key, HttpServletResponse response) {
		
		String payload = null;
		Cookie cookie = (Cookie) CollectionUtils.find(cookies,
				new Predicate() {

			@Override
			public boolean evaluate(Object object) {
				String currentKey = "";
				try {
					currentKey = URLDecoder.decode(((Cookie) object)
							.getName(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					Log.error("Error in Key ",e);						
				}
						
				return currentKey.equals(key);
			}
		});
		
		if(cookie != null) {
			payload = cookie.getValue();
			cookie.setMaxAge(0);
			response.addCookie(cookie);
		}
		
		return payload; 
	}

	private static String setStandardizedPatientResource(
			HttpServletRequest request, ByteArrayOutputStream os) {

		Long id = Long
				.parseLong(request.getParameter(ResourceDownloadProps.ID));
		String locale = request.getParameter(ResourceDownloadProps.LOCALE);
		String fileName = StandardizedPatient
				.getPdfPatientsBySearchUsingServlet(id, locale, os);
		return fileName;
	}

	private static String setStandardizedRoleResource(
			HttpServletRequest request, ByteArrayOutputStream os) {

		Long id = Long
				.parseLong(request.getParameter(ResourceDownloadProps.ID));
		String locale = request.getParameter(ResourceDownloadProps.LOCALE);
		String[] filter = request
				.getParameterValues(ResourceDownloadProps.FILTER);
		Long selectedRoleItemAccess = Long.parseLong(request
				.getParameter(ResourceDownloadProps.SELECTED_ROLE_ITEM_ACCESS));

		String fileName = StandardizedRole
				.getRolesPrintPdfBySearchUsingServlet(id,
						Arrays.asList(filter), selectedRoleItemAccess, locale,
						os);

		return fileName;
	}

	private static void sendFile(HttpServletResponse response, byte[] resource,
			String fileName) throws IOException {
		ServletOutputStream stream = null;
		stream = response.getOutputStream();
		response.setContentType("application/x-download");
		response.addHeader("Content-Disposition", "inline; filename=\""
				+ fileName + "\"");
		response.setContentLength((int) resource.length);
		stream.write(resource);
		stream.close();
	}

}