import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;

/**
 * CE1: TextBuddy 
 * A0097797Y 
 * Darry Chew 
 * Tutorial ID F12
 * 
 * TextBuddy is used to manipulate text in a file. Users can specify a file name
 * via the program parameters and its contents will be utilized if exists. Users
 * can add, delete, clear and display the contents. All new entries will be
 * appended to the back of the list. The file will be saved when the list has
 * had some changes after an execution. Exit will terminate the program. The
 * command format is given by the example interaction below:
 * 
 *		c:> TextBuddy mytextfile.txt  (OR c:>java  TextBuddy mytextfile.txt)
 *		Welcome to TextBuddy. mytextfile.txt is ready for use
 *		command: add little brown fox
 *		added to mytextfile.txt: �little brown fox�
 *		command: display
 *		1. little brown fox
 *		command: add jumped over the moon
 *		added to mytextfile.txt: �jumped over the moon�
 *		command: display
 *		1. little brown fox
 *		2. jumped over the moon
 *		command: delete 2
 *		deleted from mytextfile.txt: �jumped over the moon�
 *		command: display
 *		1. little brown fox
 *		command: clear
 *		all content deleted from mytextfile.txt
 *		command: display
 *		mytextfile.txt is empty
 *		command: exit
 *		c:> 
 * 
 * Program Assumptions
 * 
 * 1. File name parameter -- The date and time will be used as the default file
 * name if none entered. Contents of file will be will be utilized if exists.
 * 
 * 2. File data storage -- "\n" will be used to separate elements
 * 
 * 3. Invalid commands -- Program will print "invalid command!" and will prompt
 * user to enter new command. Text trailing behind commands (i.e. Display XXXXXX)
 * will be dropped and ignored.
 * 
 * 4. Out of bounds deletion -- Attempt to delete an element of ID smaller than
 * 0 or greater than the list size will prompt "invalid element ID".
 * 
 * 5. Command letter case -- Program will accept commands in any letter case
 * size (capital, small, mixed). i.e. clear, ClEar, CLEAR
 * 
 * 6. Writing of data -- Program will only save to file when there has been
 * modifications made (i.e. after add, delete or clear commands).
 * 
 * @author Darry Chew
 * 
 */
public class TextBuddy {

	private static final String MESSAGE_ADDED_ELEMENT = "added to %s: \"%s\"\n";
	private static final String MESSAGE_CLEAR_LIST = "all content deleted from %s\n";
	private static final String MESSAGE_COMMAND_INPUT = "command: ";
	private static final String MESSAGE_DELETE_ELEMENT = "delete from %s: \"%s\"\n";
	private static final String MESSAGE_ERROR_SAVING = "error encountered when saving %s\n";
	private static final String MESSAGE_ERROR_READING = "error encountered when reading %s\n";
	private static final String MESSAGE_INVALID_COMMAND = "invalid command!\n";
	private static final String MESSAGE_INVALID_ELEMENT_ID = "invalid element ID\n";
	private static final String MESSAGE_LIST_EMPTY = "%s is empty\n";
	private static final String MESSAGE_PRINT_LIST = "%d. %s\n";
	private static final String MESSAGE_WELCOME = "Welcome to TextBuddy. %s is ready for use\n";

	private static final String TEXT_FILE_EXTENSION = ".txt";

	// Booleans used when saving file
	private static final boolean FILE_SAVE_SUCCESSFUL = true;
	private static final boolean FILE_SAVE_UNSUCCESSFUL = false;

	// These are the possible command types
	enum COMMANDS {
		DISPLAY, ADD, DELETE, CLEAR, EXIT
	};

	// Filename of the output data text file
	private String fileName;

	// This ArrayList will be used to store all the user input elements
	private final ArrayList<String> list = new ArrayList<String>();

	private final BufferedReader in = new BufferedReader(new InputStreamReader(
			System.in));

	public static void main(String[] args) {

		TextBuddy textBuddy = new TextBuddy(args);
		textBuddy.execute();
	}

	/**
	 * Constructor process filename input from program arguments and checks if
	 * file exists.
	 * 
	 * @param arg String array of the program input parameters.
	 */
	public TextBuddy(String[] arg) {

		// Check for filename in program input parameter
		if (arg.length > 0) {
			fileName = arg[0];
		} else {
			fileName = getDateTime().concat(TEXT_FILE_EXTENSION);
		}

		checkFileExistance();

		printOut(String.format(MESSAGE_WELCOME, fileName));
	}

	/**
	 * Gets the current date and time.
	 * 
	 * @return The current date and time in "dd-MMM-HH-mm" format.
	 */
	private String getDateTime() {

		SimpleDateFormat format = new SimpleDateFormat("dd-MMM-HH-mm");
		return (format.format(new Date()));
	}

	/**
	 * Reads in the contents of the file if it exists.
	 */
	private void checkFileExistance() {
		try {
			File file = new File(fileName);
			if (file.exists()) {
				// Read in the file contents and add it to the list
				String line;
				BufferedReader br = new BufferedReader(new FileReader(fileName));
				while ((line = br.readLine()) != null) {
					list.add(line);
				}
				br.close();
			}
		} catch (Exception e) {
			list.clear(); // Clear all corrupted data
			printOut(String.format(MESSAGE_ERROR_READING, fileName));
		}
	}

	/**
	 * Main program function. Program will run in a loop until "exit" command
	 * received.
	 */
	public void execute() {

		String output = "";

		while (true) {
			printOut(MESSAGE_COMMAND_INPUT);

			try {
				// Split the command and parameters (if any) entered by the user
				String[] cmd = in.readLine().trim().split(" ", 2);

				// Match user input command with program available commands
				switch (COMMANDS.valueOf(cmd[0].toUpperCase())) {

					case DISPLAY :
						output = printList();
						break;

					case ADD :
						output = addElement(cmd[1]);
						break;

					case DELETE :
						output = deleteElement(cmd[1]);
						break;

					case CLEAR :
						output = clearList();
						break;

					case EXIT :
						System.exit(0);
						break;

					default:
						output = MESSAGE_INVALID_COMMAND;
				}
			} catch (Exception e) {
				output = MESSAGE_INVALID_COMMAND;
			}

			printOut(output);
		}
	}

	/**
	 * Saves the list to specified filename.
	 * 
	 * @return True if file save was successful, else false.
	 */
	private boolean saveToFile() {
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
			return FILE_SAVE_UNSUCCESSFUL;
		}

		return FILE_SAVE_SUCCESSFUL;
	}

	/**
	 * Prints out the list. Each element on one line with a leading serial
	 * number.
	 * 
	 * @return Returns the formatted list to be printed.
	 */
	private String printList() {
		StringBuffer output = new StringBuffer();
		if (list.isEmpty()) {
			System.out.printf(MESSAGE_LIST_EMPTY, fileName);
		} else {
			for (int i = 0; i < list.size(); i++) {
				output.append(String.format(MESSAGE_PRINT_LIST, (i + 1),
						list.get(i)));
			}
		}
		return output.toString();
	}

	/**
	 * Adds an element to the list and saves to file.
	 * 
	 * @param element 	String element to be added to the list.
	 * @return 			Feedback successful/unsuccessful message.
	 */
	private String addElement(String element) {
		list.add(element);

		boolean isFileSaveSuccessful = saveToFile();

		if (isFileSaveSuccessful) {
			return String.format(MESSAGE_ADDED_ELEMENT, fileName, element);
		} else {
			return String.format(MESSAGE_ERROR_SAVING, fileName);
		}
	}

	/**
	 * Parse the parameter string into integer preparing for element deletion.
	 * 
	 * @param param 	User entered parameter.
	 * @return 			Element ID as entered by user if valid.
	 * @throws 			Exception When invalid element ID entered.
	 */
	private int parseID(String param) throws Exception {
		try {
			return Integer.parseInt(param.split(" ", 2)[0]);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Deletes an element from the list and saves to file. Checks if list is
	 * empty or if element ID is out of bounds.
	 * 
	 * @param param User entered parameter containing ID of element to be
	 *            	deleted.
	 * @return 		Feedback successful/unsuccessful message.
	 */
	private String deleteElement(String param) {

		int id = 0;

		// Parse the element ID
		try {
			id = parseID(param);
		} catch (Exception e) {
			return String.format(MESSAGE_INVALID_COMMAND);
		}

		if (id > 0 && list.size() >= id) { // Check if ID is valid
			int index = id - 1; // 0 based indexing list
			String element = list.get(index);
			list.remove(index);

			boolean isFileSaveSuccessful = saveToFile();

			if (isFileSaveSuccessful) {
				return String.format(MESSAGE_DELETE_ELEMENT, fileName, element);
			} else {
				return String.format(MESSAGE_ERROR_SAVING, fileName);
			}

		} else if (list.isEmpty()) {
			return String.format(MESSAGE_LIST_EMPTY, fileName);
		} else {
			return String.format(MESSAGE_INVALID_ELEMENT_ID);
		}
	}

	/**
	 * Clears the list and saves to file.
	 * 
	 * @return Feedback successful/unsuccessful message.
	 */
	private String clearList() {
		list.clear();

		boolean isFileSaveSuccessful = saveToFile();

		if (isFileSaveSuccessful) {
			return String.format(MESSAGE_CLEAR_LIST, fileName);
		} else {
			return String.format(MESSAGE_ERROR_SAVING, fileName);
		}
	}

	private void printOut(String output) {
		System.out.print(output);
	}

}
