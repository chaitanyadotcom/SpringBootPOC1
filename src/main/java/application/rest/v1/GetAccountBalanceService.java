package application.rest.v1;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import application.dao.JdbcExample;


@RestController
public class GetAccountBalanceService{

	@Autowired
	private JdbcExample jdbcQuery;
	
	private static  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public String jsonTemplate = "{\r\n" + 
			"	\"status\" : \"200\",\r\n" + 
			"	\"statusDesc\" : \"SUCCESS\",\r\n" + 
			"	\"asofDate\" : \"DATESTRING\",\r\n" + 
			"	\"balance\": \"BALANCE\"\r\n" + 
			"}";
	
	public String errorJson= "{\r\n" + 
			"	\"status\" : \"500\",\r\n" + 
			"	\"statusDesc\" : \"Invalid Data or No Data Found\",\r\n" + 
			"	\"asofDate\" : \"DATE_STRING\",\r\n" + 
			"	\"balance\": \"0\"\r\n" + 
			"}";
	
	private static HashMap<String, String> balanceDetails = new HashMap<String,String>();
	
	static {
		balanceDetails.put("12345", "97998.25");
		balanceDetails.put("45678", "89656.68");
		balanceDetails.put("99999", "46466.99");
	}
	
	@RequestMapping(value = "/account/balance/{plan}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<String> getAccountBalance(@PathVariable String plan,
			@RequestParam(name="SSN") String SSN, HttpServletRequest request) throws Exception {
		ResponseEntity<String> response = null;
		boolean invalidParameters = Boolean.FALSE;
		
		String currentDate = sdf.format(new java.util.Date());
		
		jsonTemplate=jsonTemplate.replaceAll("DATESTRING", currentDate);
		errorJson=errorJson.replaceAll("DATESTRING", currentDate);
		String acctBal = jdbcQuery.getAccountBalance(SSN);
		if ((null == plan || plan.trim().length() < 1) || (null == SSN || SSN.trim().length() < 1)
				|| null == acctBal) {			
			invalidParameters = Boolean.TRUE;
			response = new ResponseEntity<String>(errorJson, HttpStatus.BAD_REQUEST); 
		}
		
		
		if(!invalidParameters ) {
			
			
			response = new ResponseEntity<String>(jsonTemplate.replaceAll("BALANCE", acctBal), HttpStatus.OK);
		}
		
		System.out.println("Response JSON --> "+ response.toString());
		
		return response;
	}
	
}
