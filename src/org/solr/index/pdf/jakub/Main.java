package org.solr.index.pdf.jakub;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.solr.extract.pdf.AnalysePdf;
import org.xml.sax.SAXException;

/**
 * 
 * @author elias, 2018-07-18
 *
 */
public class Main {

	private static String solrServerUrl = "http://localhost:8983/solr/docs";

	/**
	 * 
	 * @param args
	 * @throws IOException
	 * @throws SAXException
	 * @throws SolrServerException
	 */
	public static void main(String[] args) throws IOException, SAXException, SolrServerException {
		// TODO Auto-generated method stub

		System.out.println("Please enter the path of the root directory which includes PDF documents.\n"
				+ "For example, E:/TestDocuments/ on Windows, /var/testdata/ on Linux or Mac OS.");

		InputStreamReader isr = null;
		BufferedReader br = null;

		isr = new InputStreamReader(System.in);
		br = new BufferedReader(isr);
		
		String rootDirectory = br.readLine();
		if(rootDirectory.lastIndexOf("/") < rootDirectory.length() - 1)
			rootDirectory += "/";
			
		AnalysePdf.analysePdfFolder(rootDirectory);

		SolrClient solrClient = new HttpSolrClient.Builder(solrServerUrl).build();

		List<File> htmlfiles = FileUtil.getFiles(new File(rootDirectory));

		if (htmlfiles.size() > 0) {

			for (File file : htmlfiles) {


				if (!FileUtil.getFileType(file).equalsIgnoreCase("htm"))
					continue;

				Document doc = Jsoup.parse(file, "UTF-8");
				Elements headers = doc.select("p.header");

				String content = IOStreamUtil.streamToString(new FileInputStream(file));
				String fileName = file.getName().substring(0, file.getName().lastIndexOf("."));
				String year = fileName.substring(0, 4);
				String month = fileName.substring(5, 7);
				String version = "";

				int len = fileName.length();
				for (int i = 1; i < len; i++) {
					if (fileName.substring(len - i, len - i + 1).equalsIgnoreCase("v")) {
						version = fileName.substring(len - i);
						break;
					}
				}

				for (Element header : headers) {
					String headerValue = header.text().trim();

					if (!headerValue.equals("") && !headerValue.startsWith("http")) {

						Element headerElement = header.nextElementSibling();
						String text = headerElement.text();
						if (headerElement.nextElementSibling() != null) {
							if (headerElement.nextElementSibling().hasClass("text"))
								text += headerElement.nextElementSibling().text();
						}

						// Preparing the Solr document
						SolrInputDocument solrDoc = new SolrInputDocument();

						// Adding fields to the document
						solrDoc.addField("content", content);
						solrDoc.addField("paragraph_header", header.text());
						solrDoc.addField("paragraph_txt", text);
						solrDoc.addField("url", file.getAbsolutePath().replaceAll("htm", "pdf"));
						solrDoc.addField("year", year);
						solrDoc.addField("month", month);
						solrDoc.addField("version", version);
						
						if(version.equals(""))
							solrDoc.addField("sorting", year + "_" + month + "_" + version);
						else
							solrDoc.addField("sorting", year + "_" + month);
						
						solrDoc.addField("stream_size", file.length());
						solrDoc.addField("resourcename", file.getName().replaceAll("htm", "pdf"));
						solrDoc.addField("content_type", "pdf");

						// Adding the document to Solr
						solrClient.add(solrDoc);
					}
				}
				if(file.exists())
					file.delete();
			}
		}

		System.out.println("Commiting...");

		// Saving the changes
		solrClient.commit();

		System.out.println("Completed!...");
	}
}