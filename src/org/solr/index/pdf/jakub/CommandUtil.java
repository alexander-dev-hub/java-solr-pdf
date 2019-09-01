package org.solr.index.pdf.jakub;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 
 * @author elias, 2018-07-18
 *
 */
public class CommandUtil {

	/**
	 * 
	 * @param cmd
	 * @return
	 */
	public static InputStream run(String cmd) {
		try {
			String cmds[] = new String[3];
			if (isWindows()) {
				// Windows
				cmds[0] = "cmd";
				cmds[1] = "/c";
				cmds[2] = cmd;
			} else {
				// Linux
				cmds[0] = "/bin/sh";
				cmds[1] = "-c";
				cmds[2] = cmd;
			}
			Process process = Runtime.getRuntime().exec(cmds);
			process.waitFor();
			InputStream is = process.getInputStream();

			if (process.exitValue() == 0) {

				return is;
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 
	 * @return
	 */
	public static String macAddressInfo() {
		try {
			String cmds[] = new String[3];
			String result = null;
			String regEx = null;
			if (isWindows()) {
				// Windows
				cmds[0] = "cmd";
				cmds[1] = "/c";
				cmds[2] = "ipconfig /all";
				regEx = "(.*)[^-](\\w{2}[-]\\w{2}[-]\\w{2}[-]\\w{2}[-]\\w{2}[-]\\w{2})$";
			} else {
				// Linux
				cmds[0] = "/bin/sh";
				cmds[1] = "-c";
				cmds[2] = "ifconfig -a";
				regEx = "(.*)[^-](\\w{2}[:]\\w{2}[:]\\w{2}[:]\\w{2}[:]\\w{2}[:]\\w{2})$";
			}
			Process process = Runtime.getRuntime().exec(cmds);
			process.waitFor();
			InputStream is = process.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			String line = "";
			while ((line = br.readLine()) != null) {
				if(line.matches(regEx)) {
					result = line.replaceAll(regEx, "$2");
					break;
				}
			}

			if (process.exitValue() == 0) {
				return result;
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 
	 * @return
	 */
	public static String javaInfo() {

		String result = null;
		try {
			InputStream is = run("set");
			if(is != null) {
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				
				String line = "";
				
				while ((line = br.readLine()) != null) {
					if(line.contains("JAVA_HOME")) {
						result = line.split("=")[1];
						break;
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Checks if this is Windows.
	 */
	public static boolean isWindows() {
		return ((File.separator).equals("\\"));
	}
}
