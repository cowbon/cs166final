package com.example.app;


import java.sql.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.logging.Logger;


@Controller
public class IndexController {
	private Connection _connection = null;
	private static final String dbport = "5432";
	private static final String dbname = "icwang_DB";
	private static final String passwd = "";
	private static final String user = "icwang";
	private static final Logger LOGGER = Logger.getLogger(IndexController.class.getName());
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

	public int getCurrSeqVal(String sequence) throws SQLException {
		Statement stmt = this._connection.createStatement ();
		
		ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
		if (rs.next()) return rs.getInt(1);
		return -1;
	}
	
	@GetMapping("/")
	public String index() {
		return "index";
	}

	@GetMapping("/staff")
	public String staff() {
		return "staff";
	}

	@GetMapping("/ship")
	public String ship() {
		return "ship";
	}

	@GetMapping("/cruise")
	public String cruise() {
		return "cruise";
	}
	@GetMapping("/CustomerSelection")
	public String customer() {
		return "CustomerSelection";
	}

	@GetMapping("/list cruise")
	public String cruises() {
		return "list cruise";
	}

	@GetMapping("/list ship")
	public String ships() {
		return "list ship";
	}

	@GetMapping("/captain")
	public String captains() {
		return "captain";
	}

	@GetMapping("/booking")
	public String booking() {
		return "booking";
	}

	@GetMapping("/delete")
	public String delete() {
		return "delete";
	}

	@GetMapping("/list passenger")
	public String passengers() {
		return "list passenger";
	}

	@PostMapping("/addcruise")
	public ResponseEntity<?> addCruise(@RequestBody HttpServletRequest request) {
		String cost = request.getParameter("cost");	
		String num_stops = request.getParameter("num_stops");	
		String actual_departure_date = request.getParameter("actual_departure_date");	
		String actual_arrival_date = request.getParameter("actual_arrival_date");	
		String arrival_port = request.getParameter("arrival_port");	
		String departure_port = request.getParameter("departure_port");
		String ship = request.getParameter("ship_id");
		String captain = request.getParameter("captain_id");
		try {
			executeUpdate("INSERT INTO Cruise(cost, num_sold, num_stops, actual_departure_date, actual_arrival_date, arrival_port, departure_port ) VALUES("+ "'" +
	               cost+ "', 0, '" + num_stops+ "', 'date(2021-05-08)', 'date(2021-05-08)', '" + arrival_port+ "','" +departure_port + "');");
			int cruise_id = getCurrSeqVal("cruise_id_seq");
			executeUpdate("INSERT INTO CruiseInfo(cruise_id,captain_id,ship_id) VALUES("+ "'" + cruise_id+ "', '" +
                       captain + "', '" + ship + "');");
		} catch(SQLException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/addship")
	public ResponseEntity<?> addShip(HttpServletRequest request) {
		String model = request.getParameter("model");
		String make = request.getParameter("make");
		String age = request.getParameter("age");
		String seats = request.getParameter("seats");
		LOGGER.info("model=" + model + " make=" + make + " age=" + age + " seats=" + seats);	
		LOGGER.info("INSERT INTO Ship( make, model, age,seats) VALUES("+ "'" + make + "', '" + model + "'," + age + "," + seats + ");");

		try {
			executeUpdate("INSERT INTO Ship(make, model, age,seats) VALUES("+ "'" + make + "', '" + model + "'," + age + "," + seats + ");");
		} catch(SQLException e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok(null);
	}

	@PostMapping("/addcaptain")
	public ResponseEntity<?> addCaptain(HttpServletRequest request) {
		String fullname = request.getParameter("fullname");
		String nationality = request.getParameter("nationality");

		try {
			executeUpdate("INSERT INTO Captain(fullname, nationality) VALUES("+ "'" + fullname + "', '" + nationality + "');");
			return ResponseEntity.ok().body(null);
		} catch(SQLException e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	
	@PostMapping("/booktrip")
	public ResponseEntity<?> bookTrip(HttpServletRequest request) {
		String cruiseId = request.getParameter("cruise_id");
		String customerId = request.getParameter("customer_id");
		String query = "Select (Select Count(*) From CruiseInfo, Ship Where CruiseInfo.ship_id = Ship.id And CruiseInfo.cruise_id=" + cruiseId + " And Ship.seats)- (Select Count(*) From Reservation Where Reservation.cid=" + cruiseId + " And Reservation.status='R');";
		try {
			List<List<String>> res = executeQueryAndReturnResult(query);
			int remaining = Integer.parseInt(res.get(0).get(0));
			return ResponseEntity.ok(null);
		} catch(SQLException e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@GetMapping(value = "/repairs/{ship_id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> repairs(@RequestParam("ship_id") String ship) {
		try {
			List<List<String>> ret = executeQueryAndReturnResult("select Ship.id, count(*) as c from Repairs, Ship where Repairs.ship_id =" + ship + " group by Ship.id order by c desc;");
			List<HashMap<String, String>> resp = new ArrayList<HashMap<String, String>>();
			for (List<String> arr:ret) {
				HashMap<String, String> tmp = new HashMap<String, String>();
				tmp.put("ship_id", arr.get(0));
				tmp.put("times", arr.get(1));
				resp.add(tmp);
			}
				
			return ResponseEntity.ok(resp);
		} catch(SQLException e) {
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping(value = "/seats/{cruise_id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> seats(@RequestParam("cruise_id") String cruiseId, @RequestParam("date") String date) {
		try {
			List<List<String>> ret = executeQueryAndReturnResult("select (select seats from Ship, CruiseInfo, Schedule where Schedule.cruiseNum = CruiseInfo.cruise_id and CruiseInfo.ship_id = Ship.id and departure_time = date( " + date + ") and cnum = " + cruiseId + ") - (select count(*) from Reservation, Schedule where Reservation.cid = Schedule.cnum and Schedule.cruiseNum = " + cruiseId + " and departure_time = date(" + date + "))");
			List<HashMap<String, String>> resp = new ArrayList<HashMap<String, String>>();
			for (List<String> arr:ret) {
				HashMap<String, String> tmp = new HashMap<String, String>();
				tmp.put("status", arr.get(0));
				tmp.put("count", arr.get(1));
				resp.add(tmp);
			}
				
			return ResponseEntity.ok(resp);
		} catch(SQLException e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping(value = "/pessengers/{cruise_id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> pessengers(@RequestParam("cruise_id") String cruiseId) {
		try {
			List<List<String>> ret = executeQueryAndReturnResult("select status, count(*) from Reservation where cnum = " + cruiseId + " group by status");
			List<HashMap<String, String>> resp = new ArrayList<HashMap<String, String>>();
			for (List<String> arr:ret) {
				HashMap<String, String> tmp = new HashMap<String, String>();
				tmp.put("status", arr.get(0));
				tmp.put("count", arr.get(1));
				resp.add(tmp);
			}
				
			return ResponseEntity.ok(resp);
		} catch(SQLException e) {
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@DeleteMapping("/reservation/{customer_id}/{cruise_id}")
	public ResponseEntity<?> cancel(@RequestParam("customer_id") String customerId, @RequestParam("cruise_id") String cruiseId) {
		try {
			executeUpdate("DELETE FROM Reservation WHERE ccid=" + customerId + " and cid=" + cruiseId + ";");

		} catch(SQLException e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok(null);
	}
};
