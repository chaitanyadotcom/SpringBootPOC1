package application.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.stereotype.Component;

@Component
public class JdbcExample
{
	
   
    private Connection getSqlServerConnection() throws Exception{
    	Connection conn = null;
    	if( null == conn) {
    		conn = DriverManager.getConnection("jdbc:sqlserver://arxeadsqlencrypttest.database.windows.net:1433;database=ecommerce1;user=arxeadsqltest@arxeadsqlencrypttest;password=ARxWP007;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;");
			System.out.println("Connection -- "+ conn);
    	}
    	
    	return conn;
    }
    
    /** Method to insert the Customer details w r to the customer details passed with auto encrypting of SSN & PWD.
     * 
     * @param fName
     * @param lName
     * @param dateofBirth
     * @param socialSecurity
     * @param password
     * @return
     */
    public int insertCustomerInfo(String fName, String lName, String dateofBirth, String socialSecurity, String password) {
    	int records = 0;
    	Connection conn =null;
    	
    	try {
    					
			String query = "INSERT INTO Customer (FirstName, LastName, DOB, SSN, PWD) \n" + 
					"    VALUES( '"+fName+"', '"+lName+"', '"+dateofBirth+"', \n" + 
					"    EncryptByKey(Key_GUID('SSN_Key_01'), '"+socialSecurity+ "'), \n" + 
					"    EncryptByKey(Key_GUID('SSN_Key_01'), '"+password+"'))";
			

			System.out.println("Query --> "+ query);
			String query1 = "OPEN SYMMETRIC KEY SSN_Key_01 DECRYPTION BY CERTIFICATE MyCertificate;\n";
			
			conn = getSqlServerConnection();
			
			Statement stmt = conn.createStatement();
			
			 records = stmt.executeUpdate(query1);
			
			System.out.println("Executed result --> "+ records);
			
			records = stmt.executeUpdate(query);
			
			System.out.println("Executed result --> "+ records);
			
			conn.commit();
			conn.close();
    		
    	} catch(Throwable th) {
    		th.printStackTrace();
    		closeConnection(conn);
    	}
    	return records;
    }
    
    /** Method to get the customer details w r to SSN passed.
     * 
     * @param socialSecurity
     * @return
     */
    public Map<String, String> getCustomerInfo(String fName, String lName, String customerId) {
    	Map<String, String> customer = new HashMap<String, String>();
    	Connection conn =null;
    	try {
    					
			StringBuilder query = new StringBuilder("SELECT FirstName, LastName, DOB,SSN, CONVERT(VARCHAR(50), DecryptByKey(SSN)) AS DecryptedSSN,\n" + 
					"PWD, CONVERT(VARCHAR(50), DecryptByKey(PWD))\n" + 
					" AS DecryptedPWD FROM Customer");
			
			if(null != customerId & customerId.trim().length() >0 ) {
				query.append(" where customerId = "+customerId);
			} 
			if(null != fName & fName.trim().length() >0 ) {
				query.append(" and FirstName = '"+fName+"'");
			}
			if(null != lName & lName.trim().length() >0 ) {
				query.append(" and LastName = '"+lName+"'");
			}
			

			System.out.println("Query --> "+ query.toString());
			String query1 = "OPEN SYMMETRIC KEY SSN_Key_01 DECRYPTION BY CERTIFICATE MyCertificate;\n";
			String query2 = "Use ecommerce1;";
			
			conn = getSqlServerConnection();
			Statement stmt = conn.createStatement();
			
			

			
			int records = stmt.executeUpdate(query1);
				
				System.out.println("Executed result --> "+ records);
			
			ResultSet rs  = stmt.executeQuery(query.toString());
			
			while(rs.next()) {
				customer.put("firstName",rs.getString("FirstName"));
				customer.put("LastName",rs.getString("LastName"));
				customer.put("DOB",rs.getString("DOB"));
				customer.put("EncryptedSSN",rs.getString("SSN"));
				customer.put("DecryptedSSN",rs.getString("DecryptedSSN"));
				customer.put("EncryptedPWD",rs.getString("PWD"));
				customer.put("DecryptedPWD",rs.getString("DecryptedPWD"));
			}
			
			rs.close();
			stmt.close();
			
			conn.close();
			conn = null;
    		
    	} catch(Throwable th) {
    		th.printStackTrace();
    		closeConnection(conn);
    	}
    	return customer;
    }
    
    private void closeConnection(Connection conn)  {
    	try {
			if(null != conn) {
				conn.close();
				conn = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

}