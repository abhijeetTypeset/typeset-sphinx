package utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader {

	FileInputStream fi;
	FileOutputStream fo;
	XSSFSheet sheet;
	XSSFWorkbook workbook;
	XSSFWorkbook wo;
	Properties prop;
	public static HashMap<String, String> inputData = new HashMap<String, String>();
	String[] needValues = { "email", "password", "formatSearch", "paragraphConent", "heading", "reftitle",
			"refauthor" };

	public void closeFile() throws IOException {
		this.fi.close();

	}

	/**
	 * Returns list of element on a specific row no
	 *
	 * @param j
	 * @param sheet
	 * @return
	 */
	public ArrayList<String> data(int j, String sheet) {

		final ArrayList<String> value = new ArrayList<String>();
		final XSSFRow row = this.sheet.getRow(j);
		for (int i = 1; i < row.getLastCellNum(); i++) {
			try {

				String str = row.getCell(i).getStringCellValue();
				str = str.trim();
				if (!str.equals("")) {
					value.add(i + "%" + str);
				}

			} catch (final NullPointerException e) {

			}

		}
		return value;
	}

	/**
	 * Returns complete list of data from a column name
	 *
	 * @param sheetname
	 * @param columnname
	 * @return
	 * @throws IOException
	 */
	public ArrayList<String> dataProviderByRow(String sheetname, String columnname) throws IOException {
		String str = null;
		int column = 0;

		final ArrayList<String> data = new ArrayList<String>();
		final XSSFRow r = this.sheet.getRow(0);
		for (int i = 0; i < r.getLastCellNum(); i++) {
			try {
				if (r.getCell(i).getStringCellValue().equals(columnname)) {
					column = i;
					break;
				}
			}

			catch (final Exception e) {
				System.out.println(e.getMessage() + "   --NO such sheet exist");
			}
		}
		for (int i = 1; i < this.sheet.getPhysicalNumberOfRows(); i++) {
			final XSSFRow row = this.sheet.getRow(i);

			try {

				if (row.getCell(column).getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
					row.getCell(column).setCellType(Cell.CELL_TYPE_STRING);
					str = row.getCell(column).getStringCellValue();
					str = str.trim();
					data.add(str);

				} else if (row.getCell(column).getCellType() == XSSFCell.CELL_TYPE_STRING) {
					str = row.getCell(column).getStringCellValue();
					str = str.trim();
					data.add(str);
				}

			}

			catch (final NullPointerException e) {

			}

		}
		return data;
	}

	/**
	 * Returns data present in specified sheet on row no and column no
	 *
	 * @param sheetname
	 * @param row
	 * @param column
	 * @return
	 * @throws IOException
	 */
	public String readFromColumn(int row, int column) throws IOException // To
	{
		String data = null;
		final XSSFRow r = this.sheet.getRow(row);
		if (r.getCell(column).getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
			r.getCell(column).setCellType(Cell.CELL_TYPE_STRING);
			data = r.getCell(column).getStringCellValue();
			data = data.trim();

		} else if (r.getCell(column).getCellType() == XSSFCell.CELL_TYPE_STRING) {
			data = r.getCell(column).getStringCellValue();
			data = data.trim();
		}

		return data;
	}

	/**
	 * Load file path
	 *
	 * @param file
	 * @throws IOException
	 */
	public void testDataFile(String file) throws IOException {
		this.fi = new FileInputStream(file);
		this.workbook = new XSSFWorkbook(this.fi);
		this.sheet = this.workbook.getSheetAt(0);
	}

	public void readData(String filename) throws IOException {
		testDataFile(filename);
		int rows = sheet.getPhysicalNumberOfRows();
		for (int idx = 0; idx < rows; idx++) {
			try {
				String key = readFromColumn(idx, 0);
				String value = readFromColumn(idx, 1);
				inputData.put(key, value);
			} catch (Exception e) {

			}
		}
	}

	public void verifyAllInputsPresent() throws Exception {
		boolean allInputsPresent = true;
		for (String s : needValues) {
			if (!inputData.containsKey(s)) {
				allInputsPresent = false;
				System.out.println("Input " + s + " not provided.");
			}
		}
		if (!allInputsPresent) {
			throw new Exception("All required inputs not provided");
		}

	}
}
