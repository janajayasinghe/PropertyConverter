
import java.io.*;
import java.util.*;

/**
 * Creates a new property file by replacing words in the wordingFileName.
 * new file has only key=Value pairs that was replaced from a new word which is in the wordingFileName. Other key=Value pairs are ignored.
 * Review generated property files after the conversion.
 *
 * @author Arjuna Jayasinghe
 * @version 1.0
 */
public class PropertyConverter {
	// Replace only following two variables.
	String projectFolder = "C:\\projects\\careware1\\CWMain\\nurse\\src\\nurse\\applet";
	String suffix = "_en_alan_turing";

	String propFileName = "MessageBundle.properties";
	String wordingFileName = "wordings.properties"; // This property file should be in the same directory where the PropertyConverter(this class) belongs.
	Properties wordings = new Properties();
	InputStream inputStream;
	OutputStream output;

	public void convert() throws IOException {
		loadWordingPropertyFIle();
		if (wordings != null) {
			List<File> propertyFiles = new ArrayList<>();
			listAllFiles(projectFolder, propertyFiles);
			propertyFiles.forEach(file -> {
				try {
					convertFile(file);
				} catch (IOException e) {
					System.out.println("Exception: " + e);
				}
			});
		}
	}

	/**
	 * Adds all property files (propFileName) in the main directory.
	 *
	 * @param directoryName
	 * @param files
	 */
	public void listAllFiles(String directoryName, List<File> files) {
		File directory = new File(directoryName);
		File[] fList = directory.listFiles();
		if (fList != null) {
			for (File file : fList) {
				if (file.isFile() && file.getName().contains(propFileName)) {
					files.add(file);
				} else if (file.isDirectory()) {
					listAllFiles(file.getAbsolutePath(), files);
				}
			}
		}
	}

	/**
	 * Loads the wording property file. (wordingFileName)
	 *
	 * @throws IOException
	 */
	private void loadWordingPropertyFIle() throws IOException {
		InputStream inputStreamWording = getClass().getClassLoader().getResourceAsStream(wordingFileName);
		try {
			wordings.load(inputStreamWording);
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		} finally {
			inputStreamWording.close();
		}
	}

	/**
	 * Creates a new property file by replacing keys by values that are listed in the wordings property file.
	 *
	 * @param file
	 * @throws IOException
	 */
	private void convertFile(File file) throws IOException {
		try {
			Properties propFile = new Properties();
			inputStream = new FileInputStream(file);
			output = new FileOutputStream(getNewPropertyFileName(file));
			Properties newPropFile = new Properties();

			if (inputStream != null) {
				propFile.load(inputStream);
				propFile.forEach((key, value) -> {
					String finalWord = replaceValues(wordings, (String) value);
					if (finalWord != null) {
						// Add key=value pairs to the new property file.
						newPropFile.put(key, finalWord);
					}
				});
				// Save the new property file.
				newPropFile.store(output, null);
				System.out.println("Converted : " + file.getAbsolutePath());
			} else {
				throw new FileNotFoundException("property file '" + file.getAbsolutePath() + "' not found in the classpath");
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		} finally {
			inputStream.close();
		}
	}

	/**
	 * Returns the word after replacing keys by values that are listed in the wordings property file.
	 * returns null if the word is not replced.
	 *
	 * @param wordings
	 * @param word
	 * @return
	 */
	private String replaceValues(Properties wordings, String word) {
		boolean replaced = false;
		Enumeration en = wordings.propertyNames();
		while (en.hasMoreElements()) {
			String key = en.nextElement().toString();
			if (word.contains(key)) {
				word = word.replace(key, wordings.getProperty(key));
				replaced = true;
			}
		}
		return replaced ? word : null;
	}

	/**
	 * returns new name by adding suffix to the property file.
	 *
	 * @param file
	 * @return
	 */
	private String getNewPropertyFileName(File file) {
		String[] fileNames = propFileName.split("\\.");
		String newName = fileNames[0] + suffix + "." + fileNames[1];
		return file.getAbsolutePath().replace(propFileName, newName);
	}

	public static void main(String[] args) throws IOException {
		PropertyConverter propConverter = new PropertyConverter();
		propConverter.convert();
	}

}