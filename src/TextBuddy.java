/**
 * CE1: TextBuddy
 * A0097797Y
 * Darry Chew
 * Tutorial Group 7
 * 
 * Program Assumptions
 * 
 * 1. File Name parameter
 * 	--	TextBuddy accepts no file name parameter
 * 		the date and time will be used as the default file name
 * 	--	If file already exists, contents will be read from the file.
 * 	--	If file does not exist, the file will be created with the specified filename parameter
 * 		during the write operation of the program.
 * 
 * 2. File Data Storage
 * 	--	Data txt file to be in the same directory
 * 	--	Line Feed "\n" will be used to separate elements
 * 
 * 3. Invalid Commands
 * 	-- 	Program will print "invalid command!" and prompt user to enter new command
 * 
 * 4. Out of bounds deletion
 * 	--	Attempt to delete an element of id smaller than 0 or greater than the list size will
 * 		prompt "invalid element ID".
 * 
 * 5. Command letter case
 * 	--	Program will accept command in any letter case (capital, small, mixed).
 * 		i.e. DELETE, delete, DeLeTe  
 * 
 * 6. Writing of data
 * 	--	Program will only write to file when the commands add, 
 * 		delete or clear are successfully executed.
 * 
 * @author Darry Chew
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;

public class TextBuddy {

	enum COMMANDS {
		DISPLAY, ADD, DELETE, CLEAR, EXIT
	};

	private static final String INVALID_COMMAND = "invalid command!";
	private static final String INVALID_ELEMENT_ID = "invalid element ID";

	/** filename of the text file **/
	private String fileName;

	private BufferedReader in = new BufferedReader(new InputStreamReader(
			System.in));

	private ArrayList<String> list = new ArrayList<String>();

	public static void main(String[] args) {

		TextBuddy textBuddy = new TextBuddy(args);
		textBuddy.exe();

	}

	/**
	 * Constructor with filename.
	 * 
	 * @param arg String array of the program input parameters.
	 */
	public TextBuddy(String[] arg) {

		// check for input filename parameter
		if (arg.length > 0) {
			fileName = arg[0];
		} else {
			fileName = getDateTime();
		}

		checkFileExistance();

		System.out.println("Welcome to TextBuddy. " + fileName
				+ " is ready for use");
	}

	/**
	 * Returns the current date and time
	 */
	public String getDateTime() {

		// No filename parameter input. Set filename to current date and time
		return (new SimpleDateFormat("dd-MMM-HH-mm").format(new Date()) + ".txt");
	}

	/**
	 * Reads in the contents of the file if it exists.
	 */
	private void checkFileExistance() {
		try {
			File file = new File(fileName);
			if (file.exists()) {
				String ln;
				BufferedReader br = new BufferedReader(new FileReader(fileName));
				while ((ln = br.readLine()) != null) {
					list.add(ln);
				}
				br.close();
			}
		} catch (Exception e) {
			// e.printStackTrace();
			System.out.println("error encountered when reading " + fileName);
		}
	}

	/**
	 * Main program function. Program will run in a loop until "exit" command
	 * received.
	 */
	public void exe() {

		boolean exitFlag = false;

		/** Run program till "exit" command executed **/
		while (!exitFlag) {
			System.out.print("command: ");

			try {
				// Split the command and parameters (if any) entered by the user
				String[] cmd = in.readLine().split(" ", 2);

				switch (COMMANDS.valueOf(cmd[0].toUpperCase())) {

					case DISPLAY :
						printList();
						break;

					case ADD :
						addElement(cmd[1]);
						break;

					case DELETE :
						deleteElement(Integer.parseInt(cmd[1]));
						break;

					case CLEAR :
						clearList();
						break;

					case EXIT :
						exitFlag = true;
						break;

					default:
						System.out.println(INVALID_COMMAND);
				}
			} catch (Exception e) {
				// e.printStackTrace();
				System.out.println(INVALID_COMMAND);
			}
		}
	}

	/**
	 * Writes the list to specified filename.
	 * 
	 * @return True if write was successful, else false.
	 */
	private boolean writeToFile() {
		try {
			FileWriter file = new FileWriter(fileName);
			String output = "";

			// Format the elements to have a suffix of "\n"
			for (String ln : list) {
				output = output + ln + "\n";
			}

			file.write(output);
			file.flush();
			file.close();
		} catch (IOException e) {
			// e.printStackTrace();
			System.out.println("error encountered when saving " + fileName);
			return false;
		}

		return true;
	}

	/**
	 * Prints out the list. Each element on one line with a leading serial
	 * number.
	 */
	private void printList() {
		if (list.isEmpty()) {
			System.out.println(fileName + " is empty");
		} else {
			for (int i = 0; i < list.size(); i++) {
				System.out.println((i + 1) + ". " + list.get(i));
			}
		}
	}

	/**
	 * Adds an element to the list.
	 * 
	 * @param str String element to be added to the list.
	 */
	private void addElement(String str) {
		list.add(str);

		if (writeToFile()) {
			System.out.println("added to " + fileName + ": \"" + str + "\"");
		}
	}

	/**
	 * Deletes an element from the list. Checks if list is empty or if element
	 * ID is out of bounds.
	 * 
	 * @param id ID of element to be deleted.
	 */
	private void deleteElement(int id) {
		if (id > 0 && list.size() >= id) { // Check if ID is valid
			// 0 based indexing list
			String str = (String) list.get(id - 1);
			list.remove(id - 1);

			if (writeToFile()) {
				System.out.println("deleted from " + fileName + ": \"" + str
						+ "\"");
			}

		} else if (list.isEmpty()) {
			System.out.println(fileName + " is empty");
		} else {
			System.out.println(INVALID_ELEMENT_ID);
		}
	}

	/**
	 * Clears the list
	 */
	private void clearList() {
		list.clear();

		if (writeToFile()) {
			System.out.println("all content deleted from " + fileName);
		}
	}

}