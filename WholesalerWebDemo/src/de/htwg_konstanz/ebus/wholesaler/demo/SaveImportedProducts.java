package de.htwg_konstanz.ebus.wholesaler.demo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sun.xml.ws.wsdl.writer.document.Part;

import de.htwg_konstanz.ebus.framework.wholesaler.api.bo.BOCategory;
import de.htwg_konstanz.ebus.framework.wholesaler.api.bo.BOInventory;
import de.htwg_konstanz.ebus.framework.wholesaler.api.bo.BOProduct;
import de.htwg_konstanz.ebus.framework.wholesaler.api.bo.BOSupplier;
import de.htwg_konstanz.ebus.framework.wholesaler.api.boa.CategoryBOA;
import de.htwg_konstanz.ebus.framework.wholesaler.api.boa.InventoryBOA;
import de.htwg_konstanz.ebus.framework.wholesaler.api.boa.ProductBOA;
import de.htwg_konstanz.ebus.framework.wholesaler.api.boa.SupplierBOA;
import de.htwg_konstanz.ebus.framework.wholesaler.api.boa._BaseBOA;
import de.htwg_konstanz.ebus.framework.wholesaler.api.security.Security;
import de.htwg_konstanz.ebus.framework.wholesaler.vo.Product;
import de.htwg_konstanz.ebus.framework.wholesaler.vo.voa.iface.ProductstatusVOA;

/**
 * @author tobia
 *
 */
/**
 * @author tobia
 *
 */
public class SaveImportedProducts implements IAction, ErrorHandler {

	public static final String ACTION_SHOW_PRODUCT_LIST = "saveImportedProducts";
	public static final String PARAM_LOGIN_BEAN = "loginBean";
	private static final String PARAM_PRODUCT_LIST = "productList";
	private static final String UPLOAD_DIRECTORY = "C:";
	private static ErrorHandler errorhandler = new ErrorHandlerImpl();

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
	 * @throws IOException
	 */

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
				return fileName.substring(fileName.lastIndexOf('/') + 1).substring(fileName.lastIndexOf('\\') + 1); // MSIE
																													// fix.
			}
		}
		return null;
	}

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response, ArrayList<String> errorList)
			throws IOException, SAXException, ParserConfigurationException, FactoryConfigurationError {
		PrintWriter out = response.getWriter();
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);

		if (isMultipart) {
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			try {
				List<FileItem> fields = upload.parseRequest(request);
				Iterator<FileItem> it = fields.iterator();
				while (it.hasNext())
					try {
						{
							FileItem fileItem = it.next();

							out.println(fileItem.getString());

							// File file = new File(fileItem.getString());

							// Create temp file.
							File temp = File.createTempFile("pattern", ".suffix");

							// Delete temp file when program exits.
							temp.deleteOnExit();

							// Write to temp file
							BufferedWriter out1 = new BufferedWriter(new FileWriter(temp));
							out1.write(fileItem.getString());
							out1.close();

							// parse an XML document into a DOM tree,
							// !!ATTETION the parser saves the whole tree in memory, so pay attention to
							// filesize
							// TODO implement a maximuim file size
							DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
							Document document = parser.parse(temp);

							boolean fileCheck = this.checkImportedFile(document);

							if (fileCheck) {

								 this.createProductInstance(document);

							

							}

						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						System.out.println("Exception");
						e.printStackTrace();
						return "notWellformedXML.jsp";
					}
			} catch (FileUploadException e) {
				System.out.println("Fileupload exception");
				e.printStackTrace();
			}
		}

		return null;
	}

	public void createProductInstance(Document document) {

		// 1. get article elements
		// 2. check if id is already in db
		// 3. if yes then update fields
		// 4. if no then add fields to products

		// Create XPathFactory object
		XPathFactory xpathFactory = XPathFactory.newInstance();

		// Create XPath object
		XPath xpath = xpathFactory.newXPath();

		try {
			// create XPathExpression object
			XPathExpression expr = xpath.compile("/BMECAT/T_NEW_CATALOG/ARTICLE");
			// evaluate expression result on XML document
			NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
			System.out.println(nodes);

			// materialnumber in products
			List<BOProduct> productBOA = ProductBOA.getInstance().findAll();

			// schlüsselkandidat is supplier+ s_aid
			// get highest materialnumber+1
			int materialnumber = this.getNextMaterialNumber(productBOA);

			for (int i = 1; i < nodes.getLength()+1; i++) {

				materialnumber++;
				// create product business object
				BOProduct product = this.createProduct(document, xpath, i);

				// create categorie business object
				BOCategory category = this.createCategory(document, xpath, i);
				product.setCategory(category);

				// product inventory
				BOInventory inventory = this.createInventoryObject(materialnumber);
			//	product.setInventory(inventory);
				// supplier
				BOSupplier supplier = this.createSupplier();
				product.setSupplier(supplier);
				product.setMaterialNumber(materialnumber);
				product.setInventory(inventory);

				// set inventory amount
				 product.setInventoryAmount(1000);
				this.saveToDatabase(product, inventory, category);
				
				
				

			}

		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}



	}

	private void saveToDatabase(BOProduct product, BOInventory inventory, BOCategory category) {
		int materialnumber = product.getMaterialNumber();
		
		
		
		
		// check if product is not already existing
		if (ProductBOA.getInstance().findByMaterialNumber(materialnumber) == null) {
			ProductBOA.getInstance().saveOrUpdate(product);
			
			
		}
		
		
		// check if inventory is not already existing
		if (InventoryBOA.getInstance().findByMaterialNumber(materialnumber) == null) {
			InventoryBOA.getInstance().saveOrUpdate(inventory);
			
		}
		BOInventory invne = InventoryBOA.getInstance().findByMaterialNumber(materialnumber) ;

		if (this.checkIfCategoryExist(category.getShortDescription(), category.getLongDescription())) {

			CategoryBOA.getInstance().saveOrUpdate(category);
		}
		
		
		_BaseBOA.getInstance().commit();

	}

	private BOSupplier createSupplier() {
		BOSupplier supplier = SupplierBOA.getInstance().findSupplierById("10");
		return supplier;
	}

	private BOCategory createCategory(Document document, XPath xpath, int i) throws XPathExpressionException {
		BOCategory category = new BOCategory();

		// shortdescription in products
		XPathExpression SHORTDESCRIPTION = xpath
				.compile("/BMECAT/T_NEW_CATALOG/ARTICLE[" + i + "]/ARTICLE_DETAILS/DESCRIPTION_SHORT");
		String shortDescription = (String) SHORTDESCRIPTION.evaluate(document, XPathConstants.STRING);
		System.out.println(shortDescription);

		// longdescription in products
		XPathExpression LONGDESCRIPTION = xpath
				.compile("/BMECAT/T_NEW_CATALOG/ARTICLE[" + i + "]/ARTICLE_DETAILS/DESCRIPTION_LONG");
		String longDescription = (String) LONGDESCRIPTION.evaluate(document, XPathConstants.STRING);
		System.out.println(longDescription);

		category.setShortDescription(shortDescription);
		category.setLongDescription(longDescription);
		return category;
	}

	private boolean checkIfCategoryExist(String shortDescription, String longDescription) {

		// check if there is already a category with the same short and long description
		List<BOCategory> boCategories = CategoryBOA.getInstance().findAll();
		for (BOCategory categoryObject : boCategories) {

			boolean sameShortDescription = categoryObject.getShortDescription().equals(shortDescription);
			boolean sameLongDescription = categoryObject.getLongDescription().equals(longDescription);

			if (sameShortDescription && sameLongDescription) {
				return false;

			}

		}
		return true;

	}

	/**
	 * Create and returns the ProductBO filled with information from xml import
	 * 
	 * @param document
	 * @param xpath
	 * @param i
	 * @return BOProduct
	 * @throws XPathExpressionException
	 */
	private BOProduct createProduct(Document document, XPath xpath, int i) throws XPathExpressionException {

		BOProduct product = new BOProduct();

		// ordernumber_supplier in products
		XPathExpression SUPPLIER_AID = xpath.compile("/BMECAT/T_NEW_CATALOG/ARTICLE[" + i + "]/SUPPLIER_AID");
		String supplier_aid = (String) SUPPLIER_AID.evaluate(document, XPathConstants.STRING);
		product.setOrderNumberSupplier(supplier_aid);

		// shortdescription in products
		XPathExpression SHORTDESCRIPTION = xpath
				.compile("/BMECAT/T_NEW_CATALOG/ARTICLE[" + i + "]/ARTICLE_DETAILS/DESCRIPTION_SHORT");
		String shortDescription = (String) SHORTDESCRIPTION.evaluate(document, XPathConstants.STRING);
		product.setShortDescription(shortDescription);

		// longdescription in products
		XPathExpression LONGDESCRIPTION = xpath
				.compile("/BMECAT/T_NEW_CATALOG/ARTICLE[" + i + "]/ARTICLE_DETAILS/DESCRIPTION_LONG");
		String longDescription = (String) LONGDESCRIPTION.evaluate(document, XPathConstants.STRING);
		product.setLongDescription(longDescription);

		// ordernumber customer
		product.setOrderNumberCustomer(supplier_aid);

		// shortdescription customer
		product.setShortDescriptionCustomer(shortDescription);

		// longdescription customer
		product.setLongDescriptionCustomer(longDescription);

		// MANUFACTURER_TYPE_DESCR
		XPathExpression MANUFACTURER_TYPE_DESCR = xpath
				.compile("/BMECAT/T_NEW_CATALOG/ARTICLE[" + i + "]/ARTICLE_DETAILS/MANUFACTURER_TYPE_DESCR");
		String manufacturerTypeDescription = (String) MANUFACTURER_TYPE_DESCR.evaluate(document, XPathConstants.STRING);
		product.setManufacturerTypeDescription(manufacturerTypeDescription);

		// MANUFACTURER_NAME
		XPathExpression MANUFACTURER_NAME = xpath
				.compile("/BMECAT/T_NEW_CATALOG/ARTICLE[" + i + "]/ARTICLE_DETAILS/MANUFACTURER_NAME");
		String manufacturer = (String) MANUFACTURER_NAME.evaluate(document, XPathConstants.STRING);
		product.setManufacturer(manufacturer);

		return product;
	}

	/**
	 * returns an inventory object and increments the inventory with 1
	 * 
	 * @param materialnumber
	 * @return
	 */
	/**
	 * @param materialnumber
	 * @return
	 */
	private BOInventory createInventoryObject(int materialnumber) {
		InventoryBOA inventoryBOA = InventoryBOA.getInstance();
		BOInventory inventoryObject = inventoryBOA.findByMaterialNumber(materialnumber);

		BOInventory inventory = new BOInventory();

		// if there is already a inventory increment inventory with 1
		if (inventoryObject != null) {
			int inventoryNumber = inventoryObject.getInventoryNumber();
			inventory.setInventory(inventoryNumber + 1);
			inventory.setProduct(materialnumber);
			
			inventory.setThresholdReorder(100);

		} else {
			// if there is no inventory for the new material number
			inventory.setInventory(1000);
			inventory.setProduct(materialnumber);
			inventory.setThresholdReorder(100);

		}

		return inventory;
	}

	public boolean checkImportedFile(Document document) throws IOException, SAXException {

		// create a SchemaFactory capable of understanding WXS schemas
		SchemaFactory factory1 = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		// load a WXS schema, represented by a Schema instance
		Source schemaFile = new StreamSource(new File(
				"C:\\Users\\tobia\\Documents\\EBUT\\Aufgabenblatt3\\Testdaten\\bmecat_new_catalog_1_2_simple_without_NS (1).xsd"));
		Schema schema = factory1.newSchema(schemaFile);

		// create a Validator instance, which can be used to validate an instance
		// document
		Validator validator = schema.newValidator();
		validator.setErrorHandler(errorhandler);

		// validate the DOM tree
		try {
			validator.validate(new DOMSource(document));
			System.out.println("document is valid");
			return true;
		} catch (SAXException e) {
			e.printStackTrace();
			System.out.println("document is not valid");

			return false;

		} catch (IllegalArgumentException e) {
			System.out.println("illegal argument exception");
		} catch (NullPointerException e) {
			System.out.println("Nullpointexception");
		}

		return false;

	}

	/**
	 * Returns the highest materialnumber +1 in order to avoid duplicate id's
	 * 
	 * @param boProduct
	 *            contains the productlist
	 * @return int highest materialnumber
	 */
	public int getNextMaterialNumber(List<BOProduct> boProduct) {

		int firstNumber = boProduct.get(0).getMaterialNumber();

		for (int i = 0; i < boProduct.size(); i++) {
			int currentNumber = boProduct.get(i).getMaterialNumber();

			if (currentNumber > firstNumber) {
				firstNumber = currentNumber;
			}

		}

		return firstNumber;

	}

	@Override
	public void error(SAXParseException arg0) throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void fatalError(SAXParseException arg0) throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void warning(SAXParseException arg0) throws SAXException {
		// TODO Auto-generated method stub

	}

}
