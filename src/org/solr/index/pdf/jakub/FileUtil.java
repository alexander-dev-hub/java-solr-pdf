package org.solr.index.pdf.jakub;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author elias, 2018-07-18
 *
 */
public class FileUtil {
	
	public static List<File> allFiles = new ArrayList<File>();

	/**
	 * 
	 * @param file
	 * @return
	 */
	public static List<File> getFiles(File file) {
		if(file.isFile())
		{
			allFiles.add(file);
			return allFiles;
		}
		
		File[] files = file.listFiles();
		if (files.length == 0) {
		} else {
			for (File f : files) {
				if (f.isFile()) {
					allFiles.add(f);
				} else if (f.isDirectory()) {
					getFiles(f);
				}
			}
		}
		return allFiles;
	}
	
	/**
	 * 
	 * @param file
	 * @return
	 */
	public static List<File> getVideoFiles(File file) {
		List<File> mp4Files = new ArrayList<File>();
		File[] files = file.listFiles();
		if (files.length == 0) {
			// directory.delete();
		} else {
			for (File f : files) {
				if (f.isFile() && f.getName().contains(".mp4")) {
					System.out.println(f.getAbsolutePath());
					mp4Files.add(f);
				} else if (f.isDirectory()) {
					getVideoFiles(f);
				}
			}
		}
		return mp4Files;
	}

	/**
	 * 
	 * @param f
	 * @return
	 */
	public static String getFileType(File f) {
		String extension = f.getName().substring(f.getName().lastIndexOf(".") + 1, f.getName().length()).toLowerCase();
		return extension;
	}
	
	/**
	 * 
	 * @param f
	 * @return
	 */
	public static String getStrFileSize(File f) {
		long file_size_kbyte = f.length() % 1024 == 0 ? f.length() / 1024 : f.length() / 1024 + 1;
		NumberFormat df = NumberFormat.getInstance();
		((DecimalFormat) df).setDecimalSeparatorAlwaysShown(true);
		return df.format(file_size_kbyte).replace(".", "") + " KB";
	}
}