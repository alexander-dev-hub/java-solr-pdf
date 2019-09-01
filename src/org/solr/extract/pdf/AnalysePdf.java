package org.solr.extract.pdf;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * 
 * @author elias, 2018-07-18
 *
 */
public class AnalysePdf {

	/**
	 * 
	 * @param pdffolder
	 * @throws IOException
	 */
	public static void analysePdfFolder(String pdffolder) throws IOException {
		String htmout = "";

		File dir = new File(pdffolder);

		if (dir.isDirectory() == false) {
			System.out.println("Pdf Dir does not exists : " + pdffolder);
			return;
		}

		// list out all the file name and filter by the extension
		String[] list = dir.list();

		if (list.length == 0) {
			System.out.println("Pdf Dir no files end with : " + pdffolder);
			return;
		}

		for (String pdffilename : list) {

			if (!pdffilename.endsWith(".pdf"))
				continue;

			int dotpos = pdffilename.lastIndexOf(".");

			String pdfsubname = pdffilename.substring(0, dotpos);

			String pdfpagepath = pdffolder + pdfsubname + File.separator;

			String pdffullname = pdffolder + pdffilename;

			htmout = pdffullname.replace(".pdf", ".htm");

			if (new File(htmout).exists())
				continue;

			// Split pages of one pdf

			ReadPdf.Splitepages(pdffullname, pdfpagepath);

			// Extract subpages
			ReadPdf.AnalysisFolder(pdfpagepath);

			// Merge subpages to one htm
			ReadPdf.MergePages(htmout, pdfpagepath);

			FileUtils.forceDelete(new File(pdfpagepath));

		}
	}
}