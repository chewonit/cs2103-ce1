/*
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
 * 	--	Program will only write to file when the commands add, delete or clear are successfully executed.
 * 
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;

import java.text.SimpleDateFormat;

/**
 * Manage a list of items. Accepted user commands are display, add, delete,
 * clear, exit.
 * 
 * @author Darry Chew
 * 
 */
public class TextBuddy {

	private final int DISPLAY_ID = 0;
	private final int ADD_ID = 1;
	private final int DELETE_ID = 2;
	private final int CLEAR_ID = 3;
	private final int EXIT_ID = 4;

	/** filename of the text file **/
	private String fileName;

	private BufferedReader in = new BufferedReader(new InputStreamReader(
			System.in));

	private HashMap<String, Integer> hm = new HashMap<String, Integer>();
	private ArrayList<String> list = new ArrayList<String>();

	public static void main(String[] args) {

		TextBuddy tb;

		// check for input filename parameter
		if (args.length > 0) {
			tb = new TextBuddy(args[0]);
		} else {
			tb = new TextBuddy();
		}

		tb.exe();
	}

	/**
	 * Constructor with no filename input. Set filename to current date and
	 * time.
	 */
	public TextBuddy() {

		// No filename parameter input. Set filename to current date and time
		this(new SimpleDateFormat("dd-MMM-HH-mm").format(new Date()) + ".txt");
	}

	/**
	 * Constructor with filename.
	 * 
	 * @param args Name of the text file to load/save list.
	 */
	public TextBuddy(String arg) {

		// Set filename to user input parameter
		fileName = arg;

		// Check if file exists, read in the contents if it exist
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

		// Initialize HashMap
		// HashMap is to assign an unique id to each control command
		hm.put("display", DISPLAY_ID);
		hm.put("add", ADD_ID);
		hm.put("delete", DELETE_ID);
		hm.put("clear", CLEAR_ID);
		hm.put("exit", EXIT_ID);

		System.out.println("Welcome to TextBuddy. " + fileName
				+ " is ready for use");
	}

	/**
	 * Main program function. Program will run in a loop until "exit" command
	 * received.
	 */
	public void exe() {

		/** Run program till "exit" command executed **/
		boolean exit = false;

		while (!exit) {
			System.out.print("command: ");

			try {
				// Split the command and parameters if any
				String[] cmd = in.readLine().split(" ", 2);

				switch (hm.get(cmd[0].toLowerCase())) {

					case DISPLAY_ID : // Display case
						printList();
						break;

					case ADD_ID : // Add case
						addElement(cmd[1]);
						break;

					case DELETE_ID : // Delete case
						deleteElement(Integer.parseInt(cmd[1]));
						break;

					case CLEAR_ID : // Clear case
						clearList();
						break;

					case EXIT_ID : // Exit case
						exit = true;
						break;

					default:
						System.out.println("invalid command!");
				}
			} catch (Exception e) {
				// e.printStackTrace();
				System.out.println("invalid command!");
			}
		}
	}

	/**
	 * Writes the list to specified filename.
	 * 
	 * @return	True if write was successful, else false. 
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
	 * Prints out the list.
	 */
	private void printList() {
		if (list.isEmpty()) {
			System.out.println(fileName + " is empty");
		} else {
			// Print out the list with a leading serial number
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
			System.out.println("invalid element ID");
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