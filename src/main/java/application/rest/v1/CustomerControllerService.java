package application.rest.v1;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import application.dao.JdbcExample;


@RestController
public class CustomerControllerService{

	@Autowired
	private JdbcExample jdbcQuery;
	
	private static  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public String jsonTemplate = "{\r\n" + 
			"	\"status\" : \"200\",\r\n" + 
			"	\"statusDesc\" : \"SUCCESS\",\r\n" + 
			"	\"First Name\" : \"FIRSTNAME\",\r\n" + 
			"	\"Last Name\" : \"LASTNAME\",\r\n" + 
			"	\"Last Name\" : \"LASTNAME\",\r\n" +
			"	\"Date of Birth\" : \"DOB\",\r\n" +
			"	\"SSN\" : \"SSN\",\r\n" +
			"	\"Password\": \"PWD\"\r\n" + 
			"}";
	
	public String errorJson= "{\r\n" + 
			"	\"status\" : \"500\",\r\n" + 
			"	\"statusDesc\" : \"Invalid Data or No Customer Data Found\",\r\n" + 
			"	\"asofDate\" : \"DATE_STRING\",\r\n" + 
			"	\"balance\": \"0\"\r\n" + 
			"}";
	
	private static HashMap<String, String> balanceDetails = new HashMap<String,String>();
	
	@RequestMapping(value = "/api/customer/insert", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<String> insertCustomerDetails(
			@RequestParam(name="firstName") String firstName, @RequestParam(name="lastName") String lastName, 
			@RequestParam(name="DOB") String DOB,@RequestParam(name="SSN") String SSN,
			@RequestParam(name="password") String password, HttpServletRequest request) throws Exception {
		ResponseEntity<String> response = null;
		JSONObject json = new JSONObject();
		String currentDate = sdf.format(new java.util.Date());
		
		jsonTemplate=jsonTemplate.replaceAll("DATESTRING", currentDate);
		errorJson=errorJson.replaceAll("DATESTRING", currentDate);
		
		if ((null == firstName || firstName.trim().length() < 1) || (null == lastName || lastName.trim().length() < 1) || (null == DOB || DOB.trim().length() < 1)
				|| (null == SSN || SSN.trim().length() < 1) || (null == password || password.trim().length() < 1)) 
		{			
			response = new ResponseEntity<String>(errorJson, HttpStatus.BAD_REQUEST); 
		} else {
		
			int result = jdbcQuery.insertCustomerInfo(firstName, lastName, DOB, SSN, password);
			
			if(result >0) {
			
				json.put("Status", "200");
				json.put("Status Message", "Successfully Inserted Customer Details");
				
				response = new ResponseEntity<String>( json.toString(), HttpStatus.OK);
			}
		}
		
		System.out.println("Response JSON --> "+ response.toString());
		
		return response;
	}
	
	@RequestMapping(value = "/api/customer/get", method = RequestMethod.GET, produces = "application/json")
	 @ResponseBody
	public Map<String, String> getCustomerDetails(
			@RequestParam(name="fName") String fName, 
			@RequestParam(name="lName") String lName,
			@RequestParam(name="customerId") String customerId,
			HttpServletRequest request) throws Exception {
		
		
		ResponseEntity<JSONObject> response = null;
		Map<String, String> result = new HashMap<String, String>();;
		try {
//			JSONObject json = new JSONObject();
			String currentDate = sdf.format(new java.util.Date());
			
			jsonTemplate=jsonTemplate.replaceAll("DATESTRING", currentDate);
			errorJson=errorJson.replaceAll("DATESTRING", currentDate);
			
			
				result = jdbcQuery.getCustomerInfo(fName, lName, customerId);
				
				if(null != result && result.size() >0) {
				
					result.put("Status", "200");
					
//					result.put("Customers", new JSONObject(result));
					
//					response =  new ResponseEntity<JSONObject>(json,HttpStatus.OK);
				}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.put("Error Status", "303");
			result.put("Error Message", "Invalid data provided ");
			
//			response =  ResponseEntity.badRequest(). body(errorJson);
//			response = new ResponseEntity<JSONObject>(new JSONObject(errorJson), HttpStatus.BAD_REQUEST); 
		}
		
		
		System.out.println("Response JSON --> "+ result.toString());
		
		return result;
	}
	
}
