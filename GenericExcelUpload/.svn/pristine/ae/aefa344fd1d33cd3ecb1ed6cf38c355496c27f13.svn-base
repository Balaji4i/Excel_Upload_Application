package bean;

import java.sql.SQLException;
import java.sql.Types;

import java.util.ArrayList;

import oracle.adf.share.ADFContext;

import oracle.jbo.server.ApplicationModuleImpl;

import utils.ADFUtils;
import utils.DBUtils;
import utils.JSFUtils;

public class PackageCall {
    public PackageCall() {
        super();
    }
    
    public static String processDataToDB() {  
        return "";
    }
    
    private static Object[][] dobProcArgs = null;
    
    public static ArrayList processDataToDB(String parentId, String childId, String packagename) { 
        
        String p_total_count=null;
        String p_success_count=null;
        String p_error_count=null;
        String p_msg=null;
        String p_msg_code=null;
        String p_pkg_name=null;
        
        ArrayList <String> returnList = new ArrayList<String>();
        String userName = ADFContext.getCurrent().getSessionScope().get("userName")==null?"API":
                          ADFContext.getCurrent().getSessionScope().get("userName").toString();

        // Parent id
        oracle.jbo.domain.Number p_iface_id_id = new oracle.jbo.domain.Number();
        try {
            p_iface_id_id = new oracle.jbo.domain.Number(parentId);
        } catch (SQLException e) {

        }
        // child id
        oracle.jbo.domain.Number p_iface_dtl_id_id = new oracle.jbo.domain.Number();
        try {
            p_iface_dtl_id_id = new oracle.jbo.domain.Number(childId);
        } catch (SQLException e) {

        }
        
        p_pkg_name=packagename;
        ArrayList list = new ArrayList();
        
        ApplicationModuleImpl am =
            (ApplicationModuleImpl)ADFUtils.getApplicationModuleForDataControl("rootAMDataControl");
        String qry = "call "+p_pkg_name+"(?,?,?,?,?,?,?,?)";
        dobProcArgs =
                new Object[][] { 
                                { "IN",  p_iface_id_id,         Types.NUMERIC },//0
                                { "IN",  p_iface_dtl_id_id,     Types.NUMERIC },//1
                                { "IN",  userName,              Types.VARCHAR },//2
                                { "OUT", p_total_count,         Types.VARCHAR },//3
                                { "OUT", p_success_count,       Types.VARCHAR },//4
                                { "OUT", p_error_count,         Types.VARCHAR },//5
                                { "OUT", p_msg,                 Types.VARCHAR },//6
                                { "OUT", p_msg_code,            Types.VARCHAR } //7
                                };

        try {
            DBUtils.callDBStoredProcedure(am.getDBTransaction(), qry, dobProcArgs);
            
            p_total_count=(String) dobProcArgs[3][1]; 
            p_success_count=(String) dobProcArgs[4][1]; 
            p_error_count=(String) dobProcArgs[5][1]; 
            p_msg=(String) dobProcArgs[6][1]; 
            p_msg_code=(String) dobProcArgs[7][1]; 


            
        } catch (SQLException e) {
            JSFUtils.addFacesInformationMessage("Please check, Error !");
        }
        
//        System.out.println("Pkg working==");
//        System.out.println("p_total_count=="+p_total_count);
//        System.out.println("p_success_count=="+p_success_count);
//        System.out.println("p_error_count=="+p_error_count);
//        System.out.println("p_msg=="+p_msg);
//        System.out.println("p_msg_code=="+p_msg_code);
       
// total count
        if(p_total_count!=null){
            returnList.add(p_total_count);
        }
// success count        
        if(p_success_count!=null){
            returnList.add(p_success_count);
        }
// error count                
        if(p_error_count!=null){
            returnList.add(p_error_count);
        }
// msg                        
        if(p_msg!=null){
            returnList.add(p_msg);
        }
// msg count                        
        if(p_msg_code!=null){
            returnList.add(p_msg_code);
        }

        return returnList;
    }
    
//    
    
    
    
    
    
    
}
