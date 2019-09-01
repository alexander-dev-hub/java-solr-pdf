package org.solr.extract.pdf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

/**
 * 
 * @author elias, 2018-07-18
 *
 */
public class ExtractStyleContent {

	/**
	 * 
	 * @param document
	 * @return
	 * @throws IOException
	 */
	String extractStyled(PDDocument document) throws IOException {

		PDFTextStripper stripper = new PDFStyledTextStripper();
		stripper.setSortByPosition(true);
		String ret = stripper.getText(document);
		StringBuilder sb = new StringBuilder("<p class=\"text\">");
		sb.append(ret).append("</p>");

		return sb.toString();
	}

	/**
	 * 
	 * @param filename
	 * @param s
	 */
	void saveParsedPdfContent(String filename, String s) {
		try (FileOutputStream fos = new FileOutputStream(new File(filename))) {

			PrintWriter pr = new PrintWriter(fos);
			pr.write(s);

			pr.flush();
			pr.close();
			fos.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/***
	 * Analyse One pdf and save htm
	 * 
	 * @param inpdf
	 * @param outhtm
	 */
	public void AnalysePdf2Htm(String inpdf, String outhtm) {

		try (PDDocument document = PDDocument.load(new File(inpdf))) {

			System.out.println(inpdf + " extract with style");

			String styled = extractStyled(document);

			saveParsedPdfContent(outhtm, styled);

			document.close();
			System.out.println("***********************************");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

		}
	}
}
