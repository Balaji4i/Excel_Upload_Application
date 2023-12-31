package model.am;

import java.sql.CallableStatement;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Map;

import model.am.common.AppModule;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.server.ApplicationModuleImpl;
import oracle.jbo.server.DBTransaction;
import oracle.jbo.server.SequenceImpl;
import oracle.jbo.server.ViewObjectImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Sun Jun 28 16:20:46 IST 2020
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class AppModuleImpl extends ApplicationModuleImpl implements AppModule {
    /**
     * This is the default constructor (do not remove).
     */
    public AppModuleImpl() {
    }

    /**
     * Container's getter for EXCEL_STG_MAPPING_ROVO.
     * @return EXCEL_STG_MAPPING_ROVO
     */
    public ViewObjectImpl getEXCEL_STG_MAPPING_ROVO() {
        return (ViewObjectImpl)findViewObject("EXCEL_STG_MAPPING_ROVO");
    }

    /**
     * Container's getter for INTERFACE_MASTER_ROVO.
     * @return INTERFACE_MASTER_ROVO
     */
    public ViewObjectImpl getINTERFACE_MASTER_ROVO() {
        return (ViewObjectImpl)findViewObject("INTERFACE_MASTER_ROVO");
    }

    /**
     * Container's getter for Dummy_ROVO.
     * @return Dummy_ROVO
     */
    public ViewObjectImpl getDummy_ROVO() {
        return (ViewObjectImpl)findViewObject("Dummy_ROVO");
    }
    
    public String getSequence(String Seq) {
        SequenceImpl s = new SequenceImpl(Seq, getDBTransaction());
        return s.getSequenceNumber().toString();
    }

    public String uploadData(String ifaceId, String parentIfaceId, Map processedData) {
        String returnValue = null;
        String batchId = getSequence("xxdm_batch_id_s");
        ViewObject vo = getINTERFACE_MASTER_ROVO();
        if (!"0".equals(ifaceId)) {
//            System.err.println("if one");
            vo.applyViewCriteria(vo.getViewCriteriaManager().getViewCriteria("FindByIFaceId"), false);
            vo.setNamedWhereClauseParam("BV_IFACE_ID", ifaceId);
        } else {
//            System.err.println("if two");
            vo.applyViewCriteria(vo.getViewCriteriaManager().getViewCriteria("FindByParentIfaceId"), false);
            vo.setNamedWhereClauseParam("BV_PIFACE_ID", parentIfaceId);
        }
        vo.setRangeSize(-1);
        vo.executeQuery();
//        System.err.println("Process Data Size = "+processedData.size());
//        System.err.println("Vo count = "+vo.getEstimatedRowCount());
        if (processedData.size() != vo.getEstimatedRowCount()) {
            return "sheetError";
        }
        Row[] rows = vo.getAllRowsInRange();
        ViewObject mappingvo;
        Row[] innerRows;
        Row row;
        Row innerRow;
        ArrayList defaultValueList;
        ArrayList dataTypeList;
        ArrayList columnNameList;
        ArrayList displayInPage;
        CallableStatement cst = null;
        String insertQuery = null;
        for (int i = 0; i < rows.length; i++) {
            defaultValueList = new ArrayList<String>();
            dataTypeList = new ArrayList<String>();
            columnNameList = new ArrayList<String>();
            displayInPage = new ArrayList<String>();
            row = rows[i];
            String stagingTable = row.getAttribute("StagingTable").toString();
            Map stagingData = (Map)processedData.get(stagingTable);
//            System.err.println("ProcessedData--->"+processedData);
//            System.err.println("DATA--->"+stagingData);
            if (stagingData == null) {
                return "sheetError";
            }
            mappingvo = getEXCEL_STG_MAPPING_ROVO();
            mappingvo.applyViewCriteria(mappingvo.getViewCriteriaManager().getViewCriteria("forUpload"));
            mappingvo.setNamedWhereClauseParam("BV_IFACE_ID", row.getAttribute("IfaceId"));
            mappingvo.setRangeSize(-1);
            mappingvo.executeQuery();
            innerRows = mappingvo.getAllRowsInRange();
            String defaultValue;
            String dataType;
            String columnName;
            String displayPage;
            DBTransaction bdTransaction = this.getDBTransaction();
            // System.out.println("Staging table:" + stagingTable);
            deleteAllData(stagingTable);
            for (int count = 0; count < innerRows.length; count++) {
                innerRow = innerRows[count];
                defaultValue =
                        innerRow.getAttribute("DefaultValue") != null ? innerRow.getAttribute("DefaultValue").toString() :
                        "N/A";
                dataType =
                        innerRow.getAttribute("DataType") != null ? innerRow.getAttribute("DataType").toString() :
                        null;
                columnName =
                        innerRow.getAttribute("StgColumnName") != null ? innerRow.getAttribute("StgColumnName").toString() :
                        null;
                displayPage =
                        innerRow.getAttribute("DisplayInPage") != null ? innerRow.getAttribute("DisplayInPage").toString() :
                        null;
                if (innerRow.getAttribute("StgColumnName").toString().equalsIgnoreCase("BATCH_ID")) {
                    defaultValue = batchId;
                }
                defaultValueList.add(defaultValue);
                dataTypeList.add(dataType);
                columnNameList.add(columnName);
                displayInPage.add(displayPage);
            }
//            System.err.println("defaultValueList-->"+defaultValueList);
//            System.err.println("dataTypeList-->"+dataTypeList);
//            System.err.println("columnNameList-->"+columnNameList);
//            System.err.println("displayInPage-->"+displayInPage);
            
            Object[] keys = stagingData.keySet().toArray();
            // System.out.println("No.Of records:" + keys.length);
            for (int j = 0; j < keys.length; j++) {
                insertQuery = "";
                Map columnData = (Map)stagingData.get(keys[j]);
                String colName = "";
                for (int column = 0; column < columnNameList.size();
                     column++) {
                    if (column == 0) {
                        colName = colName + columnNameList.get(column);
                    } else {
                        colName = colName + "," + columnNameList.get(column);
                    }
                }
                insertQuery =
                        "INSERT INTO " + stagingTable + "(" + colName + ") VALUES(";
//                System.err.println("QRY-->"+insertQuery);
                
                for (int col = 0; col < defaultValueList.size(); col++) {
                    int defaultFlag = 0;
                    String temp;
                    if (displayInPage.get(col) != null &&
                        displayInPage.get(col).toString().equals("Y")) {
                        if (columnData.get(col) == null) {
                            defaultFlag = 1;
                        }
                        temp = (String)(columnData.get(col) != null ? columnData.get(col) :
                        defaultValueList.get(col));
                    } else {
                        temp = (String)defaultValueList.get(col);
                    }
                    if (temp.equalsIgnoreCase("N/A")) {
                        temp = null;
                    }
                    if (dataTypeList.get(col).toString().equals("NUMBER")) {
                        if (col == 0) {
                            insertQuery = insertQuery.concat(temp);
                        } else {
                            insertQuery = insertQuery.concat("," + temp);
                        }
                    } else if (dataTypeList.get(col).toString().equals("VARCHAR")) {
                        if (col == 0) {
                            if (temp != null) {
                                insertQuery =
                                        insertQuery.concat("'" + temp + "'");
                            } else {
                                insertQuery = insertQuery.concat("" + temp);
                            }
                        } else {
                            if (temp != null) {
                                insertQuery =
                                        insertQuery.concat(",'" + temp + "'");
                            } else {
                                insertQuery = insertQuery.concat("," + temp);
                            }
                        }
                    } else if (dataTypeList.get(col).toString().equals("DATE")) {
                        if (defaultFlag == 1) {
                            if (col == 0) {
                                insertQuery = insertQuery.concat(temp);
                            } else {
                                insertQuery = insertQuery.concat("," + temp);
                            }
                        } else if (defaultFlag == 0) {
                            if (col == 0) {
                                insertQuery = insertQuery.concat(temp);
                            } else {
                                insertQuery = insertQuery.concat("," + temp);
                            }
                        }
                    }
                }
                insertQuery = insertQuery.concat(")");
                System.err.println("QRY-->"+insertQuery);
                try {
                    cst = bdTransaction.createCallableStatement(insertQuery, 0);
                    cst.executeUpdate();
                    if (cst != null && !cst.isClosed()) {
                        cst.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // System.out.println("Exception at execute Query");
                    this.getDBTransaction().rollback();
                    returnValue = "issueinupload";
                    break;
                } finally {
                    try {
                        if (cst != null && !cst.isClosed()) {
                            cst.close();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (returnValue == null) {
                returnValue = batchId;
            }
        }

        return returnValue;
    }
    public String  parentInterfaceId(String parentInterfaceName){
//        System.err.println("Filtering Parent LOV");
           if(parentInterfaceName!=null){
               ViewObject vo = getINTERFACE_MASTER_ROVO();
               vo.applyViewCriteria(vo.getViewCriteriaManager().getViewCriteria("SearchByName"),false);
               vo.setNamedWhereClauseParam("BV_IFACE_NAME", parentInterfaceName);
               vo.executeQuery();
               if(vo.getEstimatedRowCount()>0){
                   Row row = vo.first();
                   return row.getAttribute("IfaceId").toString();
               }
           }
           return "0";
       }

    /**
     * Container's getter for DynamicROVO1.
     * @return DynamicROVO1
     */
    public ViewObjectImpl getDynamicROVO() {
        return (ViewObjectImpl)findViewObject("DynamicROVO");
    }
    
    public void deleteAllData(String stagingTable) {
        
        CallableStatement cst = null;
        String insertQuery = null; 
            DBTransaction bdTransaction = this.getDBTransaction();
           
                insertQuery =
                        "TRUNCATE TABLE " + stagingTable;  
                // System.out.println("TRUCN QRY-->"+insertQuery);
                try {
                    cst = bdTransaction.createCallableStatement(insertQuery, 0);
                    cst.executeUpdate();
                    if (cst != null && !cst.isClosed()) {
                        cst.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // System.out.println("Exception at execute Query"); 
                } finally {
                    try {
                        if (cst != null && !cst.isClosed()) {
                            cst.close();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }  
}
