package org.solr.extract.pdf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.Splitter;

/**
 * 
 * @author elias, 2018-07-18
 *
 */
public class ReadPdf {

	/***
	 * Splite one pdf to pages
	 * 
	 * @param pdfullname
	 * @param destpath
	 * @return
	 */
	public static boolean Splitepages(String pdfullname, String destpath) {

		// Loading an existing PDF document "C:/PdfBox_Examples/sample.pdf"
		try {

			System.out.println(pdfullname + " parse");

			File pdffile = new File(pdfullname);

			if (!pdffile.exists()) {
				System.out.println(pdfullname + " file no exist");
				return false;
			}

			String pdffilename = pdffile.getName();

			int dotpos = pdffilename.lastIndexOf(".");

			String fname = pdffilename.substring(0, dotpos);
			String ext = pdffilename.substring(dotpos);

			// dir of split files
			File subdir = new File(destpath);

			if (!subdir.exists())
				subdir.mkdirs();

			PDDocument document = PDDocument.load(pdffile);
			// Instantiating Splitter class
			Splitter splitter = new Splitter();

			// splitting the pages of a PDF document
			List<PDDocument> Pages = splitter.split(document);

			// Creating an iterator
			Iterator<PDDocument> iterator = Pages.listIterator();

			// Saving each page as an individual document
			int i = 1;

			System.out.println(pdfullname + " split");

			while (iterator.hasNext()) {

				PDDocument pd = iterator.next();

				pd.save(destpath + fname + i + ext);

				i++;
			}

			System.out.println(" splited: " + i);

			document.close();

			return true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/***
	 * Extract Content from splited files
	 * 
	 * @param folder
	 * @throws IOException
	 */
	public static void AnalysisFolder(String folder) throws IOException {

		File dir = new File(folder);

		if (dir.isDirectory() == false) {
			System.out.println("Directory does not exists : " + folder);
			return;
		}

		// list out all the file name and filter by the extension
		String[] list = dir.list();

		if (list.length == 0) {
			System.out.println("no files end with : " + folder);
			return;
		}

		ExtractStyleContent esc = new ExtractStyleContent();

		for (String fname : list) {

			if (!fname.endsWith(".pdf"))
				continue;

			int dotpos = fname.lastIndexOf(".");
			String name = fname.substring(0, dotpos);

			String pdf = folder + name + ".pdf";
			String htm = folder + name + ".htm";

			esc.AnalysePdf2Htm(pdf, htm);
		}

	}

	/***
	 * Merge analysed pages to one file
	 * 
	 * @param outfile
	 * @param contentpath
	 * @return
	 */

	public static void MergePages(String htmout, String contentpath) {

		BufferedReader br = null;
		FileReader fr = null;

		BufferedWriter bw = null;
		FileWriter fw = null;

		String ext = "htm";
		try {

			File dir = new File(contentpath);

			if (dir.isDirectory() == false) {
				System.out.println("Directory does not exists : " + contentpath);
				return;
			}

			// list out all the file name and filter by the extension
			String[] list = dir.list();

			if (list.length == 0) {
				System.out.println("no files end with : " + ext);
				return;
			}

			File outfile = new File(htmout);
			fw = new FileWriter(outfile.getAbsoluteFile(), false);
			bw = new BufferedWriter(fw);

			for (String file : list) {

				if (!file.endsWith(ext))
					continue;

				String absfile = new StringBuffer(contentpath).append(File.separator).append(file).toString();

				fr = new FileReader(absfile);
				br = new BufferedReader(fr);

				// System.out.println("file : " + temp);

				String sCurrentLine;

				while ((sCurrentLine = br.readLine()) != null) {
					bw.write(sCurrentLine);
					bw.newLine();

				}

				if (br != null)
					br.close();
				if (fr != null)
					fr.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			try {

				bw.flush();
				if (bw != null)
					bw.close();

				if (fw != null)
					fw.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}
		}

		return;
	}
}