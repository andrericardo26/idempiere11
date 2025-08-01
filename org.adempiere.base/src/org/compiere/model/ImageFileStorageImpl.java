/******************************************************************************
 * Product: iDempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 2012 Trek Global                                             *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 *****************************************************************************/

package org.compiere.model;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.compiere.util.CLogger;
import org.compiere.util.Util;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Element;

/**
 * File system backed implementation of {@link IImageStore}
 * @author hengsin
 */
public class ImageFileStorageImpl implements IImageStore {
	
	private  String IMAGE_FOLDER_PLACEHOLDER = "%IMAGE_FOLDER%";
	
	private final CLogger log = CLogger.getCLogger(getClass());
	
	//temporary buffer when AD_Image_ID=0
	private byte[] buffer = null;

	private InputStream bufferInputStream;

	@Override
	public byte[] load(MImage image, MStorageProvider prov) {
		String imagePathRoot = getImagePathRoot(prov);
		if ("".equals(imagePathRoot)) {
			throw new IllegalArgumentException("no path defined");
		}
		buffer = null;
		bufferInputStream = null;
		byte[] data = image.getByteData();
		if (data == null) {
			return null;
		}

		File file = parseXML(imagePathRoot, data);
		if (file != null && file.exists()) {
			// read files into byte[]
			final byte[] dataEntry = new byte[(int) file.length()];
			try (FileInputStream fileInputStream = new FileInputStream(file)){
				fileInputStream.read(dataEntry);
			} catch (FileNotFoundException e) {
				log.log(Level.SEVERE, "File Not Found.", e);
			} catch (IOException e1) {
				log.log(Level.SEVERE, "Error Reading The File.", e1);
			}
			return dataEntry;
		} else {
			log.severe("Image file not found or invalid XML entry");
		}
		
		return null;
	}

	
	@Override
	public InputStream loadAsStream(MImage image, MStorageProvider prov) {
		String imagePathRoot = getImagePathRoot(prov);
		if ("".equals(imagePathRoot)) {
			throw new IllegalArgumentException("no path defined");
		}
		buffer = null;
		bufferInputStream = null;
		byte[] data = image.getByteData();
		if (data == null) {
			return null;
		}
		File file = parseXML(imagePathRoot, data);
		try {
			return file != null ? new FileInputStream(file) : null;
		} catch (FileNotFoundException e) {
			log.log(Level.SEVERE, "File Not Found.", e);
		}	
		return null;
	}

	private File parseXML(String imagePathRoot, byte[] data) {
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			final DocumentBuilder builder = factory.newDocumentBuilder();
			final Document document = builder.parse(new ByteArrayInputStream(data));
			final NodeList entries = document.getElementsByTagName("entry");
			if(entries.getLength()!=1){
				log.severe("no image entry found");
			}
			final Node entryNode = entries.item(0);
			final NamedNodeMap attributes = entryNode.getAttributes();
			final Node	 fileNode = attributes.getNamedItem("file");
			if(fileNode==null ){
				log.severe("no filename for entry");
				return null;
			}
			String filePath = fileNode.getNodeValue();
			if (log.isLoggable(Level.FINE)) log.fine("filePath: " + filePath);
			if(filePath!=null){
				filePath = filePath.replaceFirst(IMAGE_FOLDER_PLACEHOLDER, imagePathRoot.replaceAll("\\\\","\\\\\\\\"));
				//just to be sure...
				String replaceSeparator = File.separator;
				if(!replaceSeparator.equals("/")){
					replaceSeparator = "\\\\";
				}
				filePath = filePath.replaceAll("/", replaceSeparator);
				filePath = filePath.replaceAll("\\\\", replaceSeparator);
			}
			if (log.isLoggable(Level.FINE)) log.fine("filePath: " + filePath);
			final File file = new File(filePath);
			if (file.exists()) {				
				return file;
			} else {
				log.severe("file not found: " + file.getAbsolutePath());
				return null;
			}

		} catch (SAXException sxe) {
			// Error generated during parsing)
			Exception x = sxe;
			if (sxe.getException() != null)
				x = sxe.getException();
			log.log(Level.SEVERE, x.getMessage(), x);

		} catch (ParserConfigurationException pce) {
			// Parser with specified options can't be built
			log.log(Level.SEVERE, pce.getMessage(), pce);

		} catch (IOException ioe) {
			// I/O error
			log.log(Level.SEVERE, ioe.getMessage(), ioe);
		}
		return null;
	}
	
	@Override
	public void  save(MImage image, MStorageProvider prov,byte[] inflatedData) {
		if (inflatedData == null || inflatedData.length == 0) {
			image.setByteData(null);
			delete(image, prov);
			return;
		}
		
		if(image.get_ID()==0){
			//set binary data otherwise save will fail
			image.setByteData(new byte[]{'0'});
			buffer = inflatedData;
			bufferInputStream = null;
		} else {
			write(image, prov, inflatedData, null);
		}

	}

	
	@Override
	public void save(MImage image, MStorageProvider prov, InputStream inputStream) {
		if (inputStream == null) {
			image.setByteData(null);
			delete(image, prov);
			return;
		}
		if(image.get_ID()==0){
			//set binary data otherwise save will fail
			image.setByteData(new byte[]{'0'});
			buffer = null;
			bufferInputStream = inputStream;
		} else {
			write(image, prov, null, inputStream);
		}
	}


	/**
	 * Write image data to file
	 * @param image
	 * @param prov
	 * @param inflatedData
	 */
	private void write(MImage image, MStorageProvider prov, byte[] inflatedData, InputStream inputStream) {
		BufferedOutputStream out = null;
		try {
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();			
			
			String imagePathRoot = getImagePathRoot(prov);
			if ("".equals(imagePathRoot)) {
				throw new IllegalArgumentException("no storage path defined");
			}
			// create destination folder
			StringBuilder msgfile = new StringBuilder().append(imagePathRoot)
					.append(image.getImageStoragePath());
			final File destFolder = new File(msgfile.toString());
			if (!destFolder.exists()) {
				if (!destFolder.mkdirs()) {
					log.warning("unable to create folder: " + destFolder.getPath());
				}
			}
			
			// write to path
			msgfile = new StringBuilder().append(imagePathRoot).append(File.separator)
					.append(image.getImageStoragePath()).append(image.get_ID());
			final File destFile = new File(msgfile.toString());

			out = new BufferedOutputStream(new FileOutputStream(destFile));
			if (inflatedData != null) {
				out.write(inflatedData);
			} else if (inputStream != null) {
				byte[] buffer = new byte[8192];
				int length = -1;
				while((length = inputStream.read(buffer)) != -1) {
					out.write(buffer, 0, length);
				}
			}
			out.flush();

			//create xml entry
			final DocumentBuilder builder = factory.newDocumentBuilder();
			final Document document = builder.newDocument();
			final Element root = document.createElement("image");
			document.appendChild(root);
			document.setXmlStandalone(true);
			final Element entry = document.createElement("entry");
			StringBuilder msgsat = new StringBuilder(IMAGE_FOLDER_PLACEHOLDER).append(image.getImageStoragePath()).append(image.get_ID());
			entry.setAttribute("file", msgsat.toString());
			root.appendChild(entry);
			final Source source = new DOMSource(document);
			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			final Result result = new StreamResult(bos);
			final Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.transform(source, result);
			final byte[] xmlData = bos.toByteArray();
			if (log.isLoggable(Level.FINE)) log.fine(bos.toString());
			//store xml in db
			image.setByteData(xmlData);

		} catch (Exception e) {
			log.log(Level.SEVERE, "saveLOBData", e);
			image.setByteData(null);
			throw new RuntimeException(e);
		} finally {
			if(out != null){
				try {
					out.close();
				} catch (Exception e) {	}
			}
		}
	}

	/**
	 * @param prov
	 * @return root path
	 */
	private String getImagePathRoot(MStorageProvider prov) {
		String imagePathRoot = prov.getFolder();
		if (imagePathRoot == null)
			imagePathRoot = "";
		if (Util.isEmpty(imagePathRoot)) {
			log.severe("no image Path defined");
		} else if (!imagePathRoot.endsWith(File.separator)){
			imagePathRoot = imagePathRoot + File.separator;
			log.fine(imagePathRoot);
		}
		return imagePathRoot;
	}

	@Override
	public boolean delete(MImage image, MStorageProvider prov) {
		String imagePathRoot = getImagePathRoot(prov);
		if ("".equals(imagePathRoot)) {
			throw new IllegalArgumentException("no image path defined");
		}
		StringBuilder msgfile = new StringBuilder().append(imagePathRoot)
				.append(image.getImageStoragePath()).append(image.getAD_Image_ID());
		
		File file=new File(msgfile.toString());
		if (file !=null && file.exists()) {
			if (!file.delete()) {
				log.warning("unable to delete " + file.getAbsolutePath());
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isPendingFlush() {
		return (buffer != null && buffer.length > 0) || (bufferInputStream != null);
	}

	@Override
	public void flush(MImage image, MStorageProvider prov) {
		if ((buffer != null && buffer.length > 0) || (bufferInputStream != null)) {
			write(image, prov, buffer, bufferInputStream);
			buffer = null;
			bufferInputStream = null;
		}		
	}

}
