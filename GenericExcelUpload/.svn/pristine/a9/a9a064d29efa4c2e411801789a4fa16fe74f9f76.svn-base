package bean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import java.math.BigDecimal;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import java.util.TreeMap;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.input.RichInputFile;

import oracle.adf.view.rich.context.AdfFacesContext;

import oracle.binding.OperationBinding;

import oracle.jbo.AttributeDef;
import oracle.jbo.Row;
import oracle.jbo.ViewCriteria;
import oracle.jbo.ViewCriteriaManager;
import oracle.jbo.ViewCriteriaRow;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.BlobDomain;

import oracle.jbo.server.ViewObjectImpl;

import oracle.security.crypto.util.InvalidFormatException;

import org.apache.myfaces.trinidad.model.UploadedFile;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.util.IOUtils;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import utils.ADFUtils;
import utils.JSFUtils;


public class ExcelUploadBB {

    private RichInputFile fileUploadBinding;

    public ExcelUploadBB() {
        super();
    }
    private Map downloadMap = new HashMap<String, ArrayList<String>>();
    private ArrayList sheetName;
    private UploadedFile file;
    private String fileName;
    private BlobDomain blobObj;
    private InputStream inputstream;
    private String hiddenOutput; 
    private String queryDefault = "0";
//    private String exportedSelectedType;

     ViewObject InterfaceMasterVO =ADFUtils.findIterator("INTERFACE_MASTER_ROVOIterator").getViewObject();

    public void downLoadTemplate(FacesContext facesContext,
                                 OutputStream outputStream) throws IOException {

        ViewObject vo = ADFUtils.findIterator("Dummy_ROVOIterator").getViewObject();
        Row row = vo.getCurrentRow();
        String parentIfaceId = row.getAttribute("ParentFaceId") != null ? row.getAttribute("ParentFaceId").toString() : "0";
        String childFaceId = row.getAttribute("ChildFaceId") != null ? row.getAttribute("ChildFaceId").toString() : "0";

        //        String action = ADFUtils.evaluateEL("#{bindings.action.inputValue}") != null ? ADFUtils.evaluateEL("#{bindings.action.inputValue}").toString() : null;

//        // System.err.println("parentIfaceId-->" + parentIfaceId);
//        // System.err.println("childFaceId-->" + childFaceId);
        downloadMap = prepareDownload(childFaceId, parentIfaceId, false);
//        // System.err.println("downloadMap-->" + downloadMap);
        constructWorkbook(facesContext, outputStream, false);
    }
    
    public void downLoadTempData(FacesContext facesContext,
                                 OutputStream outputStream) throws IOException {

        ViewObject vo = ADFUtils.findIterator("Dummy_ROVOIterator").getViewObject();
        Row row = vo.getCurrentRow();
        String parentIfaceId = row.getAttribute("ParentFaceId") != null ? row.getAttribute("ParentFaceId").toString() : "0";
        String childFaceId = row.getAttribute("ChildFaceId") != null ? row.getAttribute("ChildFaceId").toString() : "0";
//        // System.err.println("parentIfaceId-->" + parentIfaceId);
//        // System.err.println("childFaceId-->" + childFaceId);
        
        downloadMap = prepareDownload(childFaceId, parentIfaceId, true); 
        constructWorkbook(facesContext, outputStream, true);
    }

    public Map<String, ArrayList<String>> prepareDownload(String ifaceId, String parentIfaceId, boolean tempData) {
        
        Map returnMap = new HashMap<String, ArrayList<String>>();
        sheetName = new ArrayList<String>();
        ViewObject vo =
            ADFUtils.findIterator("INTERFACE_MASTER_ROVOIterator").getViewObject();
 
            if (!"0".equals(ifaceId)) {
                vo.applyViewCriteria(vo.getViewCriteriaManager().getViewCriteria("FindByIFaceId"),
                                 false);
                vo.setNamedWhereClauseParam("BV_IFACE_ID", ifaceId);
            } else {
                vo.applyViewCriteria(vo.getViewCriteriaManager().getViewCriteria("FindByParentIfaceId"),
                                 false);
                vo.setNamedWhereClauseParam("BV_PIFACE_ID", parentIfaceId);
            } 
        

        vo.setRangeSize(-1);
        vo.executeQuery();
        Row[] rows = vo.getAllRowsInRange();
        ViewObject mappingvo;
        Row[] innerRows;
        Row row;
        Row innerRow;
        ArrayList listObj;
        ArrayList columnList;
        ArrayList mandatoryList;
//        // System.err.println("rows.length-->" + rows.length);
        for (int i = 0; i < rows.length; i++) {
            listObj = new ArrayList<String>();
            columnList = new ArrayList<String>();
            mandatoryList = new ArrayList<String>();
            row = rows[i];
            String[] arr;

            mappingvo =
                    ADFUtils.findIterator("EXCEL_STG_MAPPING_ROVOIterator").getViewObject();
            mappingvo.applyViewCriteria(mappingvo.getViewCriteriaManager().getViewCriteria("findByFaceId"));
            mappingvo.setNamedWhereClauseParam("BV_IFACE_ID",
                                               row.getAttribute("IfaceId"));
            mappingvo.setRangeSize(-1);
            mappingvo.executeQuery();
            innerRows = mappingvo.getAllRowsInRange();
//            // System.err.println("innerRows.length-->" + innerRows.length);
            for (int count = 0; count < innerRows.length; count++) {
                innerRow = innerRows[count];
                listObj.add((String)innerRow.getAttribute("PromptName"));
                columnList.add((String)innerRow.getAttribute("StgColumnName"));
                mandatoryList.add((String)innerRow.getAttribute("Mandatory"));
            }
            returnMap.put((String)row.getAttribute("StagingTable"), listObj);
            returnMap.put((String)row.getAttribute("StagingTable") + "_Col",
                          columnList);
            returnMap.put((String)row.getAttribute("StagingTable") +
                          "_Mandatory", mandatoryList);
            sheetName.add((String)row.getAttribute("StagingTable"));
        }
        //Getting details for default tab
        
        vo.applyViewCriteria(vo.getViewCriteriaManager().getViewCriteria("FindParentByID"),false);
        vo.setNamedWhereClauseParam("BV_PARENT_ID", parentIfaceId);
        vo.executeQuery();
        Row parentRow = vo.first();
        queryDefault = parentRow.getAttribute("DefaultTab")!=null?parentRow.getAttribute("DefaultTab").toString():"0";
        System.err.println("test--->"+parentRow.getAttribute("IfaceName"));
        System.err.println("test--->"+parentRow.getAttribute("DefaultTab"));
        System.err.println("queryDefault--->"+queryDefault);

        return returnMap;
    }

    public void constructWorkbook(FacesContext facesContext,
                                  OutputStream outputStream, boolean tempData) throws IOException {
        try {
            HSSFWorkbook workbook = new HSSFWorkbook();
            ArrayList listObj = new ArrayList<String>();
            ArrayList colList = new ArrayList<String>();
            ArrayList mandatoryList = new ArrayList<String>();
            ArrayList queryList = new ArrayList<String>();
            ArrayList defaultSheetsName = new ArrayList<String>();
            //generating default sheets
            ViewObject vo;
            ViewCriteria vc;
            ViewCriteriaRow criteriaRow;
            Row[] rows;
            Row row; 
            // Generating Template - default data from view
            if(!"0".equals(queryDefault))
            { 
            vo = ADFUtils.getApplicationModuleForDataControl("AppModuleDataControl").findViewObject("DynamicROVO");
            vo.remove();  
            vo = ADFUtils.getApplicationModuleForDataControl("AppModuleDataControl").createViewObjectFromQueryStmt("DynamicROVO", queryDefault);  
            vo.setRangeSize(-1);
            vo.executeQuery();
            rows = vo.getAllRowsInRange();
            AttributeDef[] att = vo.getAttributeDefs();
            AttributeDef tempAtt;
            ArrayList attributeList = new ArrayList<String>();
            HSSFSheet sheetDefault = workbook.createSheet("Reference Data(READ_ONLY)");
            sheetDefault.createFreezePane(0, 1);
            for(int count=0;count<att.length;count++){
                tempAtt = att[count];
                attributeList.add(tempAtt.getName().toString());
                sheetDefault.setColumnWidth(count, 5500);
            }
            HSSFRow rowheadDefault = sheetDefault.createRow((short) 0);
            CellStyle hdrStyle = workbook.createCellStyle();
            hdrStyle.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
            hdrStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            for(int sheetHeader=0;sheetHeader<attributeList.size();sheetHeader++){
                Cell c = rowheadDefault.createCell(sheetHeader);
                c.setCellValue(attributeList.get(sheetHeader).toString());
                c.setCellStyle(hdrStyle);
            }
            for(int sheetRow=0;sheetRow<rows.length;sheetRow++){
                row = rows[sheetRow];
                rowheadDefault = sheetDefault.createRow((short) sheetRow+1);
                for(int sheetHeader=0;sheetHeader<attributeList.size();sheetHeader++){
                    rowheadDefault.createCell(sheetHeader).setCellValue(
                            row.getAttribute(attributeList.get(sheetHeader).toString())!=null ? 
                            row.getAttribute(attributeList.get(sheetHeader).toString()).toString() 
                            : null);
                }
            }
            sheetDefault.protectSheet("readOnly");
            }
            
            //Generating the template
            for (int i = 0; i < sheetName.size(); i++) {
                String sheetName = this.sheetName.get(i).toString();
                HSSFSheet sheet = workbook.createSheet(sheetName);
                sheet.createFreezePane(0, 1);
                listObj = (ArrayList<String>)downloadMap.get(sheetName);
                colList =
                        (ArrayList<String>)downloadMap.get(sheetName + "_Col");
                mandatoryList =
                        (ArrayList<String>)downloadMap.get(sheetName + "_Mandatory");
                for (int sheetColumn = 0; sheetColumn < listObj.size();
                     sheetColumn++) {
                    sheet.setColumnWidth(sheetColumn, 5500);
                }
                if(tempData){
                    sheet.setColumnWidth(listObj.size(), 5500);
                    sheet.setColumnWidth(listObj.size()+1, 5500);
                }

                HSSFRow rowhead = sheet.createRow((short)0);
                String selectColumns = "";
                Cell cell;
                CellStyle mandatoryStyle = workbook.createCellStyle();
                mandatoryStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                mandatoryStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                String mandatory = null;
                for (int sheetHeader = 0; sheetHeader < listObj.size();
                     sheetHeader++) {
                    mandatory = (String)mandatoryList.get(sheetHeader);
                    cell = rowhead.createCell(sheetHeader);
                    if (mandatory != null && mandatory.equalsIgnoreCase("Y")) {
                        cell.setCellValue("* " +
                                          listObj.get(sheetHeader).toString());
                        cell.setCellStyle(mandatoryStyle);
                    } else {
                        cell.setCellValue(listObj.get(sheetHeader).toString());
                    }
                    if(tempData){
                        if(sheetHeader==0){
                            selectColumns = selectColumns.concat(colList.get(sheetHeader).toString());
                        }else{
                            selectColumns = selectColumns.concat(","+colList.get(sheetHeader).toString());
                        }
                    }
                }
                //Writing data to template from the interface staging table
                if(tempData){
                    rowhead.createCell(listObj.size()).setCellValue("INTERFACE STATUS");
                    rowhead.createCell(listObj.size()+1).setCellValue("ERROR MESSAGE");
                    String Query = "SELECT ";
                    Query = Query.concat(selectColumns);
                    Query = Query.concat(",INTERFACE_STATUS_FLAG,ERR_DESCRIPTION");
                    Query = Query.concat(" FROM "+sheetName);  //WHERE BATCH_ID="+batchId);
//                    if(exportedSelectedType!=null){
//                        Query = Query.concat(" AND INTERFACE_STATUS_FLAG='"+exportedSelectedType+"'");
//                    }
                    vo = ADFUtils.getApplicationModuleForDataControl("AppModuleDataControl").findViewObject("DynamicROVO");
                    vo.remove();  
                    vo = ADFUtils.getApplicationModuleForDataControl("AppModuleDataControl").createViewObjectFromQueryStmt("DynamicROVO", Query);  
                    vo.setRangeSize(-1);
                    vo.executeQuery();
                    rows = vo.getAllRowsInRange();
                    for(int excelCount=0;excelCount<rows.length;excelCount++){
                        row=rows[excelCount];
                        rowhead = sheet.createRow((short) excelCount+1);
                //                        cellStyle = workbook.createCellStyle();
                //                        createHelper = workbook.getCreationHelper();
                //                        dateFormat = createHelper.createDataFormat().getFormat("MM-dd-yyyy");
                //                        cellStyle.setDataFormat(dateFormat);
                        AttributeDef[] attr = vo.getAttributeDefs();
                        for(int sheetHeader=0;sheetHeader<attr.length;sheetHeader++){
                            rowhead.createCell(sheetHeader).setCellValue(row.getAttribute(sheetHeader)!=null ? row.getAttribute(sheetHeader).toString() : null);
                        }
                    }
                }
                //End of Writing data to template from the interface staging table

            }
            workbook.write(outputStream);
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
            // System.out.println("Exception in export:" + e.getMessage());
        }
    }
 
    public void fileUploadVCL(ValueChangeEvent valueChangeEvent) throws IOException {
        // System.err.println("--VCL");
        ViewObject vo = ADFUtils.findIterator("Dummy_ROVOIterator").getViewObject();
        RichInputFile inputFileComponent = (RichInputFile)valueChangeEvent.getComponent();
        file = (UploadedFile)valueChangeEvent.getNewValue();
        fileName = file.getFilename();
         
        if (file.getContentType().equalsIgnoreCase("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
                        file.getContentType().equalsIgnoreCase("application/xlsx") ||
                        file.getContentType().equalsIgnoreCase("application/kset")) {
            try {
                // System.err.println("1");
                        inputstream = file.getInputStream();
                        blobObj = createBlobDomain(file);
                        inputFileComponent.setValid(true);
            } catch (IOException e) {
                        e.printStackTrace();
            }
        }else if (file.getContentType().equalsIgnoreCase("application/vnd.ms-excel")) {
            // System.err.println("2");
                if (file.getFilename().toUpperCase().endsWith(".XLS")) {
                    try {
                        // System.err.println("3");
                                inputstream = file.getInputStream();
                                blobObj = createBlobDomain(file);
                                inputFileComponent.setValid(true);
                    } catch (IOException e) {
                                e.printStackTrace();
                    }
                }
        }
        else{
            // System.err.println("4");
            file = null;
            inputstream = null;
            FacesContext.getCurrentInstance().addMessage( inputFileComponent.getClientId(FacesContext.getCurrentInstance())
                                                                    , new FacesMessage(FacesMessage.SEVERITY_ERROR
                                                                                       , "Incorrect File"
                                                                                       , "Upload the valid file"
                                                                                       )
                                                                    );
            inputFileComponent.resetValue();
            inputFileComponent.setValid(false);
        }  
        
        if(file!=null){
            // System.err.println("File is not NULL");
        }else{
            // System.err.println("File is  NULL");
        }
        if(inputstream!=null){
            // System.err.println("inputstream is not NULL");
        }else{
            // System.err.println("inputstream is  NULL");
        }
        vo.getCurrentRow().setAttribute("File", blobObj);  
    
    }

    private BlobDomain createBlobDomain(UploadedFile file) {
        InputStream in = null;
        BlobDomain blobDomain = null;
        OutputStream out = null;

        try {
            in = file.getInputStream();
            blobDomain = new BlobDomain();
            out = blobDomain.getBinaryOutputStream();
            IOUtils.copy(in, out);
            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.fillInStackTrace();
        }

        return blobDomain;
    }
    
    public void onClickUpload(ActionEvent actionEvent) {
        ViewObject vo = ADFUtils.findIterator("Dummy_ROVOIterator").getViewObject();
        BlobDomain blob = (BlobDomain)vo.getCurrentRow().getAttribute("File");
        InputStream iStream = blob.getBinaryStream(); 
        if(blob!=null){
            // System.err.println("blob obj is not NULL");
        }else{
            // System.err.println("blob obj is  NULL");
        }
        if(iStream!=null){
            // System.err.println("InputStream obj is not NULL");
        }else{
            // System.err.println("InputStream obj is  NULL");
        }
         
         
       
        
        ViewObject fVO = ADFUtils.findIterator("Dummy_ROVOIterator").getViewObject();
        Row currRow = fVO.getCurrentRow();
        String parentIfaceId = currRow.getAttribute("ParentFaceId") != null ? currRow.getAttribute("ParentFaceId").toString() : "0";
        String ifaceId = currRow.getAttribute("ChildFaceId") != null ? currRow.getAttribute("ChildFaceId").toString() : "0";
        
        String action = "U"; 
        
        if(action!=null && action.equalsIgnoreCase("U")){
//            if(inputstream==null && file==null){
            if(iStream==null){ 
            // System.err.println("File and input streams are NULL");
            
                fileUploadBinding.setValid(false);
                FacesContext.getCurrentInstance().addMessage( fileUploadBinding.getClientId(FacesContext.getCurrentInstance())
                                                                        , new FacesMessage(FacesMessage.SEVERITY_ERROR
                                                                                           , "File Missing"
                                                                                           , "Please Upload the file"
                                                                                           )
                                                                        );
            }
            else{
                try{
                    Map processedData = processExcel(iStream);
                    // System.err.println("Data--->"+processedData);
                    OperationBinding operationBinding = ADFUtils.findOperation("uploadData");
                    operationBinding.getParamsMap().put("ifaceId", ifaceId);
                    operationBinding.getParamsMap().put("parentIfaceId", parentIfaceId);
                    operationBinding.getParamsMap().put("processedData", processedData);
                    String batchId = (String)operationBinding.execute();
                    AdfFacesContext adfFacesContext = AdfFacesContext.getCurrentInstance();
                    if(batchId.equalsIgnoreCase("sheetError")){
                        file = null;
                        inputstream = null;
                        fileUploadBinding.resetValue();
                        fileUploadBinding.setValid(true);
                        adfFacesContext.addPartialTarget(fileUploadBinding);
                        String expMsg =
                                    "<html><body>" + "The upload excel does not contain the sheets according to the child interface name selected!!" +
                                    "<br/><br/>" + "Please upload the correct excel or choose the appropriate child interface name!!" +
                                    "<br/><br/>" + "<b>Note: </b>Don't Remove or Add any sheet from the downloaded template!!" +
                                    "</body></html>";
                        JSFUtils.addFacesErrorMessage(expMsg); 
                    }
                    else if(batchId.equalsIgnoreCase("issueinupload")){
                        file = null;
                        inputstream = null;
                        fileUploadBinding.resetValue();
                        fileUploadBinding.setValid(true);
                        adfFacesContext.addPartialTarget(fileUploadBinding);
                        String expMsg =
                                    "<html><body>" + "There is some issue while uploading the file!! Please contact the support team!!" +
                                    "</body></html>";
                        JSFUtils.addFacesErrorMessage(expMsg); 
                    }
                    else{
//                            operationBinding = ADFUtils.findOperation("CreateInsert");
//                            operationBinding.execute();
//                            ViewObject vo = ADFUtils.findIterator("xxdmInterfaceVO1Iterator").getViewObject();
//                            vo.getCurrentRow().setAttribute("BatchId", batchId);
//                            vo.getCurrentRow().setAttribute("FileName", fileName);
//                            vo.getCurrentRow().setAttribute("IfaceId", ifaceId!=null ? ifaceId : parentIfaceId);
//                            vo.getCurrentRow().setAttribute("Status", "U");
//                            vo.getCurrentRow().setAttribute("UploadedFile", blobObj);
                            operationBinding = ADFUtils.findOperation("Commit");
                            operationBinding.execute();
                            ADFContext.getCurrent().getPageFlowScope().put("showProcessButton", "true");
//                            vo.applyViewCriteria(vo.getViewCriteriaManager().getViewCriteria("ByBatchId"));
//                            vo.setNamedWhereClauseParam("b_batchId", batchId);
//                            vo.executeQuery();
//                            file = null;
//                            inputstream = null;
//                            fileUploadBinding.resetValue();
//                            fileUploadBinding.setValid(true);
                            JSFUtils.addFacesInformationMessage("Uploaded Successfully");
//                            ADFUtils.setEL("#{bindings.uploadedBatchId.inputValue}", batchId);
//                            ADFUtils.setEL("#{bindings.validateProcessIfaceId.inputValue}",ifaceId!=null ? ifaceId : parentIfaceId);
//                            ADFUtils.setEL("#{bindings.validatedBatchId.inputValue}", batchId);
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                    // System.out.println("Exception in upload excel:"+e.getMessage());
                    JSFUtils.addFacesErrorMessage("There is an issue in the Upload");
                }
            }
        }
    }
    
    public Map processExcel(InputStream iStream) throws IOException, InvalidFormatException, Exception {
        Map<String,Map> processedWorkBook = new HashMap<String,Map>();
        // Creating a Workbook from inputstream
        Workbook workbook = WorkbookFactory.create(iStream); 
        String sheetName;
        for(int i=0;i<workbook.getNumberOfSheets();i++){
            Sheet sheet = workbook.getSheetAt(i);
            int lastRow = sheet.getLastRowNum(); 
            sheetName = workbook.getSheetName(i);
            if(sheetName.equalsIgnoreCase("Reference Data(READ_ONLY)")){
                continue;
            }
            
        // Create a DataFormatter to format and get each cell's value as String
        DataFormatter dataFormatter = new DataFormatter();
        Map<Integer, Map> excelRowValuesMap = new TreeMap<Integer, Map>();
        //Iterating over Rows and Columns using Java 8 forEach with lambda
         
        //Looping over entire row
        for(int ri=0; ri<=lastRow; ri++){ 
            org.apache.poi.ss.usermodel.Row row = sheet.getRow(ri);
            Map<Integer, String> excelColumnValuesMap = new HashMap<Integer, String>();
             
            for(int ci=0;ci<row.getLastCellNum();ci++){ 
                // System.err.println("ROW--"+ri + " :CELL--"+ci);
                Cell cell = row.getCell(ci , org.apache.poi.ss.usermodel.Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                String cellValue = dataFormatter.formatCellValue(cell)!=""?dataFormatter.formatCellValue(cell):"";
                if (row.getRowNum() != 0) {
                    // System.out.println("index==>"+cell.getColumnIndex());
                    // System.out.println("cellValue==>"+cellValue);
                    excelColumnValuesMap.put(cell.getColumnIndex(), cellValue);
                } 
            }
            if (row.getRowNum() != 0) {
                excelRowValuesMap.put(row.getRowNum(), excelColumnValuesMap);
            }
        }  
            
            processedWorkBook.put(sheet.getSheetName(),excelRowValuesMap);
        }
        // Closing the workbook
        workbook.close();
        //iterate map values for display
        return processedWorkBook;
    }


    public void setFileUploadBinding(RichInputFile fileUploadBinding) {
        this.fileUploadBinding = fileUploadBinding;
    }

    public RichInputFile getFileUploadBinding() {
        return fileUploadBinding;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setHiddenOutput(String hiddenOutput) {
        this.hiddenOutput = hiddenOutput;
    }

    public String getHiddenOutput() {
        String faceId = (String)ADFUtils.evaluateEL("#{pageFlowScope.parentInterfaceId}");
        if(faceId!=null && !"0".equals(faceId)  && !faceId.equals("")){
        oracle.jbo.domain.Number faceIdBD = new oracle.jbo.domain.Number(Integer.parseInt(faceId));
        ADFUtils.setEL("#{bindings.ParentFaceId.inputValue}", faceIdBD); 
        }
        return hiddenOutput;
    }

    public void onClickProcess(ActionEvent actionEvent) {
        ViewObject vo = ADFUtils.findIterator("Dummy_ROVOIterator").getViewObject();
        Row row = vo.getCurrentRow();
        String parentIfaceId = row.getAttribute("ParentFaceId") != null ? row.getAttribute("ParentFaceId").toString() : "0";
        String childFaceId = row.getAttribute("ChildFaceId") != null ? row.getAttribute("ChildFaceId").toString() : "0";

        // System.err.println("parentIfaceId ==>"+parentIfaceId );
        // System.err.println("childFaceId ==>"+childFaceId);    
        
        
        ViewCriteria vc = InterfaceMasterVO.createViewCriteria();
        ViewCriteriaRow vcRow = vc.createViewCriteriaRow();
        vcRow.setAttribute("IfaceId", parentIfaceId);
        vc.addRow(vcRow);
        InterfaceMasterVO.applyViewCriteria(vc);
        InterfaceMasterVO.executeQuery();

       // System.out.println("="+InterfaceMasterVO.getEstimatedRowCount());
        String pkgName=InterfaceMasterVO.first().getAttribute("ValidationProc")==null?"0":InterfaceMasterVO.first().getAttribute("ValidationProc").toString();        
        
        // System.err.println("pkgName11==>"+pkgName);
//        InterfaceMasterVO.applyViewCriteria(vo.getViewCriteriaManager().getViewCriteria("FindByIFaceId"),false);
//        InterfaceMasterVO.setNamedWhereClauseParam("BV_IFACE_ID", parentIfaceId);
//        InterfaceMasterVO.executeQuery();   
        
        if(pkgName.equalsIgnoreCase("0")){
            JSFUtils.addFacesErrorMessage("Error: Package Not Found");            
        }else{
            // System.err.println("pkgName==>"+pkgName);
            // System.err.println("parentIfaceId ==>"+parentIfaceId );
            // System.err.println("childFaceId ==>"+childFaceId);    
        }
        
        ArrayList <String> list = new ArrayList<String>();
        
        list=PackageCall.processDataToDB(parentIfaceId, childFaceId, pkgName);
        
        if(list.get(4)!=null){
            if(list.get(4).toString().equalsIgnoreCase("S")){
                JSFUtils.addFacesInformationMessage(list.get(0).toString());
                JSFUtils.addFacesInformationMessage(list.get(1).toString());     
                JSFUtils.addFacesInformationMessage(list.get(2).toString());     
                JSFUtils.addFacesInformationMessage(list.get(3).toString());     
            }else{
                JSFUtils.addFacesErrorMessage(list.get(0).toString());
                JSFUtils.addFacesErrorMessage(list.get(1).toString());     
                JSFUtils.addFacesErrorMessage(list.get(2).toString());     
                JSFUtils.addFacesErrorMessage(list.get(3).toString());     
            }
        }else{
            JSFUtils.addFacesErrorMessage("Error: "+pkgName+"Package Failed");
        }
        
        ADFContext.getCurrent().getPageFlowScope().put("showErrorDownloadButton", "true");
        JSFUtils.addFacesErrorMessage("Process not configured !");
        
    }
}
