package com.example.app;


import java.sql.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import javax.servlet.http.HttpServletRequest;
import java.util.*;


@RestController
public class IndexController {
	private Connection _connection = null;
	private final String dbport = "5432";
	private final String dbname = "icwang_DB";
	private final String passwd = "";
	private final String user = "icwang";
	private void getConnection() {
		try{
			// constructs the connection URL
			String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
			
			// obtain a physical connection
	        	this._connection = DriverManager.getConnection(url, user, passwd);
		}catch(Exception e){
			e.printStackTrace();
			System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
		}
	}

	public void executeUpdate(String sql) throws SQLException {
		getConnection();
		// creates a statement object
		Statement stmt = this._connection.createStatement();

		// issues the update instruction
		stmt.executeUpdate(sql);

		// close the instruction
	    stmt.close();
	}//end executeUpdate

	public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException { 
		getConnection();
		//creates a statement object 
		Statement stmt = this._connection.createStatement (); 
		
		//issues the query instruction 
		ResultSet rs = stmt.executeQuery (query); 
	 
		/*
		 * obtains the metadata object for the returned result set.  The metadata 
		 * contains row and column info. 
		*/ 
		ResultSetMetaData rsmd = rs.getMetaData (); 
		int numCol = rsmd.getColumnCount (); 
		int rowCount = 0; 
	 
		//iterates through the result set and saves the data returned by the query. 
		boolean outputHeader = false;
		List<List<String>> result  = new ArrayList<List<String>>(); 
		while (rs.next()){
			List<String> record = new ArrayList<String>(); 
			for (int i=1; i<=numCol; ++i) 
				record.add(rs.getString (i)); 
			result.add(record); 
		}//end while 
		stmt.close (); 
		return result; 
	}//end executeQueryAndReturnResult
	
	@GetMapping("/")
	public String index() {
		return "index";
	}

	@GetMapping("/staff")
	public String staff() {
		return "staff";
	}

	@GetMapping("/customer")
	public String customer() {
		return "customer";
	}

	@GetMapping()
	@PostMapping("/addcruise")
	public ResponseEntity<?> addCruise(@RequestBody HttpServletRequest request) {
		String cost = request.getParameter("cost");	
		String num_sold = request.getParameter("num_sold");	
		String num_stop = request.getParameter("num_stop");	
		String cost = request.getParameter("cost");	
		String cost = request.getParameter("cost");	
		String cost = request.getParameter("cost");	
		String cost = request.getParameter("cost");	
		String cost = request.getParameter("cost");	
		String cost = request.getParameter("cost");	
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/addship")
	public ResponseEntity<?> addShip(HttpServletRequest request) {
		String model = request.getParameter("model");
		String make = request.getParameter("make");
		String age = request.getParameter("age");
		String seats = request.getParameter("seats");

		try {
			executeUpdate("INSERT INTO Ship( make, model, age,seats) VALUES("+ "'" + make + "', '" + model + "','" + age + "," + seats + "');");
		} catch(SQLException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/addcaptain")
	public ResponseEntity<?> addCaptain(HttpServletRequest request) {
		String fullname = request.getParameter("fullname");
		String nationality = request.getParameter("nationality");

		try {
			executeUpdate("INSERT INTO Captain(fullname, nationality) VALUES("+ "'" + fullname + "', '" + nationality + "');");
		} catch(SQLException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	
	@PostMapping("/booktrip")
	public ResponseEntity<?> bookTrip(HttpServletRequest request) {
		String cruiseId = request.getParameter("cruise_id");
		String customerId = request.getParameter("customer_id");
		String query = "Select (Select Count(*)" +" From CruiseInfo,Ship " + "Where CruiseInfo.ship_id=Ship.id And CruiseInfo.cruise_id=" + cruiseId + " And Ship.seats)- (Select Count(*) From Reservation Where Reservation.cid=" + cruiseId + " And Reservation.status=”R”);";

	}

	
	@GetMapping("/listrepairs/{ship_id}", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> repairs(@RequestParam("ship_id") String ship) {
		try {
			List<List<String>> ret = executeQueryAndReturnResult("select Ship.id, count(*) as c from Repairs, Ship where Repairs.ship_id =" + ship + " group by Ship.id order by c desc;");
			List<HashMap<String, String>> resp = new ArrayList();
			for (List<String> arr:ret) {
				HashMap<String, String> tmp = new HashMap();
				tmp.put("ship_id", arr.get(0));
				tmp.put("times", arr.get(1));
				resp.add(tmp);
			}
				
			return new ResponseEntity<>(resp, HttpStatus.OK);
		} catch(SQLException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/pessengers")
	public ResponseEntity<?> pessengers(@RequestBody HttpServletRequest request) {
		String cruiseId = request.getParameter("cruise_id");
		String status = request.getParameter("status");

		try {
			executeUpdate("INSERT INTO Ship( make, model, age,seats) VALUES("+ "'" + make + "', '" + model + "','" + age + "," + seats + "');");
		} catch(SQLException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(HttpStatus.OK);

	@DeleteMapping("/reservation/{customer_id}/{cruise_id}")
	public ResponseEntity<?> cancel(@RequestParam("customer_id") String customerId, @RequestParam("cruise_id") String cruiseId) {
		try {
			executeUpdate("DELETE FROM Reservation WHERE ccid=" + customerId + " and cid=" + cruiseId + ";");

		} catch(SQLException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
};
