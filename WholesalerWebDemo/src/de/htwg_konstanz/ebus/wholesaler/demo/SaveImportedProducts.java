package de.htwg_konstanz.ebus.wholesaler.demo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

import com.sun.xml.ws.wsdl.writer.document.Part;

import de.htwg_konstanz.ebus.framework.wholesaler.api.boa.ProductBOA;
import de.htwg_konstanz.ebus.framework.wholesaler.api.security.Security;

public class SaveImportedProducts implements IAction {

	public static final String ACTION_SHOW_PRODUCT_LIST = "saveImportedProducts";
	public static final String PARAM_LOGIN_BEAN = "loginBean";
	private static final String PARAM_PRODUCT_LIST = "productList";
	private static final String UPLOAD_DIRECTORY = "C:";

	/**
	 * The ProductListAction loads all available products from the database.
	 * <p>
	 * After loading, the action puts all products into an List-Object and makes
	 * them available for the corresponding view (JSP-Page) via the HTTPSession.
	 *
	 * @author tdi
	 * 
	 *         public static final String ACTION_SHOW_PRODUCT_LIST =
	 *         "showProductList"; public static final String PARAM_LOGIN_BEAN =
	 *         "loginBean"; private static final String PARAM_PRODUCT_LIST =
	 *         "productList";
	 * 
	 *         public ProductListAction() { super(); }
	 * 
	 *         /** The execute method is automatically called by the dispatching
	 *         sequence of the {@link ControllerServlet}.
	 * 
	 * @param request
	 *            the HttpServletRequest-Object provided by the servlet engine
	 * @param response
	 *            the HttpServletResponse-Object provided by the servlet engine
	 * @param errorList
	 *            a Stringlist for possible error messages occured in the
	 *            corresponding action
	 * @return the redirection URL
	 */
	public String execute(HttpServletRequest request, HttpServletResponse response, ArrayList<String> errorList) {
		// get the login bean from the session
		LoginBean loginBean = (LoginBean) request.getSession(true).getAttribute(PARAM_LOGIN_BEAN);

		try {
		        List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
		        for (FileItem item : items) {
		            if (item.isFormField()) {
		                // Process regular form field (input type="text|radio|checkbox|etc", select, etc).
		                String fieldName = item.getFieldName();
		                String fieldValue = item.getString();
		                // ... (do your job here)
		            } else {
		                // Process form file field (input type="file").
		                String fieldName = item.getFieldName();
		                String fileName = FilenameUtils.getName(item.getName());
		            //    InputStream fileContent = item.getInputStream();
		                // ... (do your job here)
		            }
		        }
		    } catch (FileUploadException e) {
		       
		    }
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// ensure that the user is logged in
		if (loginBean != null && loginBean.isLoggedIn()) {
			// ensure that the user is allowed to execute this action (authorization)
			// at this time the authorization is not fully implemented.
			// -> use the "Security.RESOURCE_ALL" constant which includes all resources.
			if (Security.getInstance().isUserAllowed(loginBean.getUser(), Security.RESOURCE_ALL,
					Security.ACTION_READ)) {
				// find all available products and put it to the session
				List<?> productList = ProductBOA.getInstance().findAll();

				request.getSession(true).setAttribute(PARAM_PRODUCT_LIST, productList);

				// redirect to the product page
				return "import.jsp";
			} else {
				// authorization failed -> show error message
				errorList.add("You are not allowed to perform this action!");

				// redirect to the welcome page
				return "welcome.jsp";
			}
		} else
			// redirect to the login page
			return "login.jsp";
	}

	/**
	 * Each action itself decides if it is responsible to process the corrensponding
	 * request or not. This means that the {@link ControllerServlet} will ask each
	 * action by calling this method if it is able to process the incoming action
	 * request, or not.
	 * 
	 * @param actionName
	 *            the name of the incoming action which should be processed
	 * @return true if the action is responsible, else false
	 */
	public boolean accepts(String actionName) {
		return actionName.equalsIgnoreCase(ACTION_SHOW_PRODUCT_LIST);
	}
	
	private static String getSubmittedFileName(Part part) {
	    for (String cd : ((HttpServletRequest) part).getHeader("content-disposition").split(";")) {
	        if (cd.trim().startsWith("filename")) {
	            String fileName = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
	            return fileName.substring(fileName.lastIndexOf('/') + 1).substring(fileName.lastIndexOf('\\') + 1); // MSIE fix.
	        }
	    }
	    return null;
	}

}
