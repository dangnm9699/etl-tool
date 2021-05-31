package app.connection;

import app.custom.Workspace;
import app.model.Column;
import app.model.MappingSelect;
import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import tech.tablesaw.api.*;

import java.io.*;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static tech.tablesaw.api.ColumnType.BOOLEAN;

public class ConnectorExcel implements Connector {
    private String exelPath;
    private boolean hasLabel;
    private final List<String> listTable;
    private String table;
    private Workbook workBook;
    private Sheet sheet;

    public ConnectorExcel(String exelPath) throws IOException {
        super();
        this.exelPath = exelPath;
//        BufferedReader reader;
//        reader = new BufferedReader(new FileReader(path));
//        InputStream inputStream = new FileInputStream(new File(exelPath));
////        StreamingReader
        InputStream exel = new FileInputStream(exelPath);
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(5)    // number of rows to keep in memory (defaults to 10)
                .bufferSize(128)// buffer size to use when reading InputStream to file (defaults to 1024)
                .open(exel);
//        this.workBook = workbook;

//        System.out.println("fuck "+ this.workBook.getNumberOfSheets());
        this.listTable = new ArrayList<String>();
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            this.listTable.add(workbook.getSheetName(i));
        }
        workbook.close();
        exel.close();


    }

    public String getTable() {
        return this.table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Sheet getSheet() {
        return this.sheet;
    }

    public String getExelPath() {
        return this.exelPath;
    }

    public void setExelPath(String path) {
        this.exelPath = path;
    }

    public List<String> getListSheet() {
        return this.listTable;
    }

    public boolean getHasLabel() {
        return this.hasLabel;
    }

    public void setHasLabel(boolean t) {
        this.hasLabel = t;
    }

    public Table retrieveData(boolean hasLabel, String tableName, List<MappingSelect> mappingSelects, int maxEntries) throws IOException {
        List<String> selectedField = new ArrayList<>();
        for (MappingSelect mappingSelect : mappingSelects) {
            if (mappingSelect.isSelected()) selectedField.add(mappingSelect.getExternal());
        }

        InputStream is = new FileInputStream(new File(exelPath));
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
                .bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
                .open(is);
        Table table = Table.create();
        Sheet sheet = workbook.getSheet(tableName);
        int i = 0;
        for (Row r : sheet) {
            i += 1;
            if (i == 1) {
                if (hasLabel) {

//                    List<String> field = new ArrayList<>();
//                    Iterator<Cell> cellIterator = r.cellIterator();
//                    while (cellIterator.hasNext()) {
//                        Cell cell = cellIterator.next();
////                        cell.getNumericCellValue();
////                        field.add(String.valueOf(cell.getStringCellValue()));
//                        table.addColumns(StringColumn.create(String.valueOf(cell.getStringCellValue())));
//                    }
//                    System.out.println(field);

                    for (Cell c : r) {
                        int index = c.getColumnIndex();
                        ColumnType type = mappingSelects.get(index).getType();
                        if (type.equals(ColumnType.DOUBLE)){
                            table.addColumns(DoubleColumn.create(String.valueOf(c.getStringCellValue())));
                        }else if (type.equals(ColumnType.STRING)){
                            table.addColumns(StringColumn.create(String.valueOf(c.getStringCellValue())));
                        }else if (type.equals(ColumnType.FLOAT)){
                            table.addColumns(DoubleColumn.create(String.valueOf(c.getStringCellValue())));
                        }else if (type.equals(ColumnType.INTEGER)){
                            table.addColumns(DoubleColumn.create(String.valueOf(c.getStringCellValue())));
                        }else if (type.equals(ColumnType.LOCAL_DATE)){
                            table.addColumns(DateColumn.create(String.valueOf(c.getStringCellValue())));
                        }else {
                            table.addColumns(StringColumn.create(String.valueOf(c.getStringCellValue())));
                        }
                    }
                    continue;
                } else {
//                List<String> field = new ArrayList<>();
                    Iterator<Cell> cellIterator = r.cellIterator();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
//                        cell.getNumericCellValue();
//                    field.add(String.valueOf(cell.getStringCellValue()));
                        table.addColumns(StringColumn.create("column " + cell.getColumnIndex()));
                    }
                }

//                System.out.println(field);
            }

            if(i > maxEntries){
                break;
            }
            for (Cell c : r) {



//                    c.setCellType(CellType.NUMERIC);
                int index = c.getColumnIndex();
                ColumnType type = mappingSelects.get(index).getType();
                if (type.equals(ColumnType.DOUBLE)){
                    try {
                        table.column(index).appendObj(c.getNumericCellValue());
                    }catch (Exception e){
                        table.column(index).appendObj((double)0);
                    }
//                    table.column(index).appendCell(Double.valueOf(c.getNumericCellValue()).toString());
                }else if (type.equals(ColumnType.STRING)){
                    try {
                        table.column(index).appendCell(c.getStringCellValue());
                    }catch (Exception e){
                        table.column(index).appendCell("");
                    }
//                    table.column(index).appendCell(c.getStringCellValue());
                }else if (type.equals(ColumnType.FLOAT)){
                    try {
                        table.column(index).appendObj( (double)c.getNumericCellValue());
                    }catch (Exception e){
                        table.column(index).appendObj((double)0);
                    }
//                    System.out.println("Hello World "+ c.getNumericCellValue());
//                    table.column(index).appendCell(String.valueOf( (float)c.getNumericCellValue()));
                }else if (type.equals(ColumnType.INTEGER)){
                    try {
                        table.column(index).appendObj((int)c.getNumericCellValue());
                    }catch (Exception e){
                        table.column(index).appendObj((int)0);
                    }
//                    table.column(index).appendCell(String.valueOf( (int)c.getNumericCellValue()));
                }else if (type.equals(ColumnType.LOCAL_DATE)){
                    try {
                        table.column(index).appendObj((c.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()));
                    }catch (Exception e){
                        table.column(index).appendObj((new Date()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                    }
//                    table.column(index).appendCell((c.getDateCellValue()).toString());
                }else {
                    table.column(index).appendObj(c.getStringCellValue());
                }

//                table.column(index).appendCell(String.valueOf(c.getStringCellValue()));
//                    System.out.println(c.getStringCellValue());
            }
        }
        workbook.close();
        is.close();

//        for (int j=0;j<table.rowCount();j++){
//            if (!selectedField.contains(table.column(j))){
//                table.removeColumns(j);
//            }
//        }

        table.retainColumns(selectedField.toArray(new String[selectedField.size()]));
        System.out.println("table read " +table );
        return table;
    }


    public List<String> retrieveFieldName(String tableName) throws IOException {
        List<String> field = new ArrayList<String>();
        InputStream exel = new FileInputStream(exelPath);
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(5)    // number of rows to keep in memory (defaults to 10)
                .bufferSize(128)// buffer size to use when reading InputStream to file (defaults to 1024)
                .open(exel);
        if (hasLabel) {
//            System.out.println("dua1 ");
            Iterator<Row> iterator = workbook.getSheet(tableName).iterator();
            if (iterator.hasNext()) {
//                System.out.println("treu2 ");
                Row nextRow = iterator.next();
                Iterator<Cell> cellIterator = nextRow.cellIterator();
                while (cellIterator.hasNext()) {
                    field.add(cellIterator.next().getStringCellValue());
                }
            }
        } else {
            Iterator<Row> iterator = workbook.getSheet(tableName).iterator();

            if (iterator.hasNext()) {
//                System.out.println("treu1 ");
                Row nextRow = iterator.next();
                Iterator<Cell> cellIterator = nextRow.cellIterator();
                while (cellIterator.hasNext()) {
                    field.add("column " + cellIterator.next().getColumnIndex());
                }
            }
        }
//        System.out.println("ko hieur2 "+field);
        workbook.close();
        exel.close();
        return field;
    }

    public List<Column> retrieveColumns(String tableName, List<Column> listColumn) throws FileNotFoundException {
        List<Column> columns = new ArrayList<>();
//        System.out.println("ko hieur 1 "+tableName);
        List<String> listNames = null;
        try {
            listNames = retrieveFieldName(tableName);
        } catch (IOException exception) {
            Workspace.appendLn(exception.getMessage());
            exception.printStackTrace();
        }
//        System.out.println("ko hieur "+listNames);
        for (int i = 0; i < listNames.size(); i++) {
            if (listColumn.size()>0){
                columns.add(
                        new Column(
                                listNames.get(i),
                                listColumn.get(i).getType()
                        ));
            }else {
                columns.add(
                        new Column(
                                listNames.get(i),
                                ColumnType.STRING
                        ));
            }

        }

        return columns;
    }

    public int getLastIndex(String tableName) throws IOException {
        InputStream is = new FileInputStream(new File(exelPath));
        int i = 0;
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(2)    // number of rows to keep in memory (defaults to 10)
                .bufferSize(128)     // buffer size to use when reading InputStream to file (defaults to 1024)
                .open(is);
        Sheet sheet = workbook.getSheet(tableName);
//        System.out.println(sheet.getSheetName());
//        i = sheet.getLastRowNum();
        for (Row r : sheet) {
            i += 1;
        }
        System.out.println("last index " + i);
        is.close();
        workbook.close();

        return i;


    }

    public void insert(Table table, String tableName) throws IOException {
        FileInputStream inputStream = new FileInputStream(exelPath);
        XSSFWorkbook wb_template = new XSSFWorkbook(inputStream);
        int numberColumn = wb_template.getSheet(tableName).getRow(0).getPhysicalNumberOfCells();
        inputStream.close();
        List<Column> list = new ArrayList<Column>();
        List<Column> columns = this.retrieveColumns(tableName,list);
        SXSSFWorkbook wb = new SXSSFWorkbook(wb_template); // keep 100 rows in memory, exceeding rows will be flushed to disk
        wb.setCompressTempFiles(true);

        SXSSFSheet sh = wb.getSheet(tableName);
        sh.setRandomAccessWindowSize(50);
        int rowindex = getLastIndex(tableName);
//        406829
//        System.out.println("table Coulunm "+table.rowCount());
//        System.out.println("table shape "+table.shape());

//        System.out.println("table 40690i "+table.row(406828));
//        System.out.println("table 40690i "+table.row(406830));
//        System.out.println("table 40690i "+table.row(406829));

        for (int rownum = 0; rownum < table.rowCount(); rownum++) {
//            System.out.println("rownum "+rownum);
            Row row = sh.createRow(rowindex);
//            Iterator<Cell> cellIterator = row.cellIterator();
//            while (cellIterator.hasNext()){
//                Cell cell = cellIterator.next();
//                if(this.listTable.contains(table.column(cell.getColumnIndex()))){
//
//                }
            for (int cellnum = 0; cellnum < columns.size(); cellnum++) {
//                System.out.println("cellnum "+cellnum);
                Cell cell = row.createCell(cellnum);
                if (table.columnNames().contains(columns.get(cellnum).getName())) {
                    cell.setCellValue(table.column(columns.get(cellnum).getName()).getString(rownum));
                } else {
                    cell.setCellValue("");
                }

//                System.out.println(table.row(rownum).getString(cellnum));
//                cell.setCellValue(table.column(cellnum).getString(rownum));

            }
            rowindex++;
        }

        FileOutputStream out = new FileOutputStream(exelPath);
        wb.write(out);
        out.close();
        // dispose of temporary files backing this workbook on disk
        wb.dispose();

    }

    public Workbook getWorkbook(InputStream inputStream, String excelFilePath) throws IOException {
        Workbook workbook = null;
        if (excelFilePath.endsWith("xlsx")) {
            workbook = new XSSFWorkbook(inputStream);
//        } else if (excelFilePath.endsWith("xls")) {
//            workbook = new HSSFWorkbook(inputStream);
        } else {
            throw new IllegalArgumentException("The specified file is not Excel file");
        }

        return workbook;
    }

    @Override
    public void String() {

    }

    @Override
    public void Connect() {

    }
}
