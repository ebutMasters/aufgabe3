package de.htwg_konstanz.ebus.wholesaler.demo;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ErrorHandlerImpl implements ErrorHandler {

	@Override
	public void error(SAXParseException arg0) throws SAXException {
		// TODO Auto-generated method stub
		
		System.out.println("hallo");

	}

	@Override
	public void fatalError(SAXParseException arg0) throws SAXException {
		// TODO Auto-generated method stub
		System.out.println("hallo");
	}

	@Override
	public void warning(SAXParseException arg0) throws SAXException {
		// TODO Auto-generated method stub
		System.out.println("hallo");
	}

}
