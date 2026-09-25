/*******************************************************************************
 * Copyright (c) 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package net.openchrom.chromatogram.xxd.report.supplier.excel.template.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.chemclipse.model.core.IChromatogram;

import net.openchrom.chromatogram.xxd.report.supplier.excel.template.settings.ChromatogramReportSettings;

public class MascEscapeReportWriter {

	public static final String DESCRIPTION = "Excel Template";
	public static final String FILE_EXTENSION = ".xltx";
	public static final String FILE_NAME = DESCRIPTION.replaceAll("\\s", "") + FILE_EXTENSION;
	public static final String FILTER_EXTENSION = "*" + FILE_EXTENSION;
	public static final String FILTER_NAME = DESCRIPTION + " (*" + FILE_EXTENSION + ")";

	public void generate(File file, boolean append, List<IChromatogram> chromatograms, ChromatogramReportSettings reportSettings) throws IOException {

		try (FileInputStream fileInputStreamTemplate = new FileInputStream(reportSettings.getTemplate())) {
			try (XSSFWorkbook workbookTemplate = new XSSFWorkbook(fileInputStreamTemplate)) {
				try (XSSFWorkbook workbookNew = new XSSFWorkbook()) {
					for(int i = 0; i < workbookTemplate.getNumberOfSheets(); i++) {
						copySheet(workbookTemplate.getSheetAt(i), workbookNew.createSheet());
					}
					XSSFSheet amdisReportSheet = workbookTemplate.getSheet("Amdis report Sample");
					createHeader(amdisReportSheet);
					try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
						workbookNew.write(fileOutputStream);
					}
				}

			}
		}
	}

	private void createHeader(XSSFSheet sheet) {

		XSSFRow headerRow = sheet.createRow(0);
		createTextCell(headerRow, 0, "FileName");
		createTextCell(headerRow, 1, "CAS");
		createTextCell(headerRow, 2, "Name");
		createTextCell(headerRow, 3, "RT");
		createTextCell(headerRow, 4, "RI");
		createTextCell(headerRow, 5, "Width");
		createTextCell(headerRow, 6, "Purity");
		createTextCell(headerRow, 7, "Model");
		createTextCell(headerRow, 8, "Min. Abund.");
		createTextCell(headerRow, 9, "Amount");
		createTextCell(headerRow, 10, "Scan");
		createTextCell(headerRow, 11, "Peak Tailing");
		createTextCell(headerRow, 12, "S/N (total)");
		createTextCell(headerRow, 13, "Base Peak");
		createTextCell(headerRow, 14, "Max. Amount");
		createTextCell(headerRow, 15, "Area");
		createTextCell(headerRow, 16, "Intgr.Signal");
		createTextCell(headerRow, 17, "Max. Area");
		createTextCell(headerRow, 18, "Extra Width");
		createTextCell(headerRow, 19, "Models");
		createTextCell(headerRow, 20, "Frac. Good");
		createTextCell(headerRow, 21, "RI-RI(lib)");
		createTextCell(headerRow, 22, "Net");
		createTextCell(headerRow, 23, "Weighted");
		createTextCell(headerRow, 24, "Simple");
		createTextCell(headerRow, 25, "Reverse");
		createTextCell(headerRow, 26, "Corrections");
		createTextCell(headerRow, 27, "(m/z)");
		createTextCell(headerRow, 28, "S/N (m/z)");
		createTextCell(headerRow, 29, "Area % (m/z)");
		createTextCell(headerRow, 30, "Conc.");
		createTextCell(headerRow, 31, "RT-RT(lib)");
	}

	private void createTextCell(XSSFRow row, int column, String value) {

		XSSFCell cell = row.createCell(column);
		cell.setCellType(CellType.STRING);
		cell.setCellValue(value);
	}

	private static void copySheet(XSSFSheet sheetSource, XSSFSheet sheetSink) {

		for(int column = 0; column < sheetSource.getRow(0).getLastCellNum(); column++) {
			sheetSink.setColumnWidth(column, sheetSource.getColumnWidth(column));
		}

		for(Row rowSource : sheetSource) {
			Row rowSink = sheetSink.createRow(rowSource.getRowNum());
			for(Cell cellSource : rowSource) {
				Cell cellSink = rowSink.createCell(cellSource.getColumnIndex());
				copyCell(cellSource, cellSink);
			}
		}
	}

	private static void copyCell(Cell cellSource, Cell cellSink) {

		switch(cellSource.getCellType()) {
			case STRING:
				cellSink.setCellValue(cellSource.getStringCellValue());
				break;
			case NUMERIC:
				cellSink.setCellValue(cellSource.getNumericCellValue());
				break;
			case BOOLEAN:
				cellSink.setCellValue(cellSource.getBooleanCellValue());
				break;
			case FORMULA:
				cellSink.setCellFormula(cellSource.getCellFormula());
				break;
			case BLANK:
				cellSink.setBlank();
				break;
			default:
				break;
		}
		CellStyle cellStyleSink = cellSink.getSheet().getWorkbook().createCellStyle();
		cellStyleSink.cloneStyleFrom(cellSource.getCellStyle());
		cellSink.setCellStyle(cellStyleSink);
	}
}
