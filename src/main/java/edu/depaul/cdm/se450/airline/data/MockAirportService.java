package edu.depaul.cdm.se450.airline.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MockAirportService implements AirportService {
	
	private static Logger log = Logger.getLogger(MockAirportService.class.getName());
	
	private static final String DB_DRIVER = "org.hsqldb.jdbc.JDBCDriver";

    private static final Airport AIRPORT_ORD = new Airport("ORD", "O'Hare International", "Chicago", "IL");
    private static final Airport AIRPORT_MIA = new Airport("MIA", "Miami International", "Miami", "FL");
    private static final Airport AIRPORT_MCO = new Airport("MCO", "Orlando International", "Orlando", "FL");
  
    private String connectionURL = "jdbc:hsqldb:hsql://localhost/mydb";
    private String userName = "SA";
    private String password = "SA";
    
    public MockAirportService(String connectionString, String user, String password) {
        this.connectionURL = connectionString;
        this.userName = user;
        this.password = password;
    }
    
    public MockAirportService(){
    	
    }
    
    /**
     * 
     * @param airline
     * @return list of airports serviced by the airline
     */
    @Override
    public List<Airport> getAirportsServiced(String airline) {
    	List<Airport> airports;
        airports = new ArrayList<>();
        
        try { 
        	Class.forName(DB_DRIVER);
        	
        	Connection con = getConnection();
        	
        	Statement stmt = con.createStatement();
        	
        	ResultSet rs = stmt.executeQuery("select origin, destination from mock737airportservice");
        	
        	while (rs.next()){
        		Airport a = map1(rs);
        		airports.add(a);        	
        	}
        	
        	rs.close();
        	stmt.close();
        	con.close();
    	
    	} catch (SQLException | ClassNotFoundException ex) {
    		// Will have to work out the proper exception handling b/c this is not 
    		// proper
    		log.log(Level.SEVERE, null, ex);
    	}
        
    	Set<Airport> hs = new HashSet<>();
        hs.addAll(airports);
        airports.clear();
        airports.addAll(hs);
        return airports;
    }
    

	/**
     * Determines whether the specific airline services particular airport
     * @param airline
     * @param airport
     * @return 
     */
    @Override
    public boolean isAirportServicedBy(String airline, String airport) {
        // TODO:  Call getAirportsServiced for airline, from the list of 
    	// airport determine if the airport that is being requested is 
    	// in the list or not
    	// ***VARIABLES SHOULD BE RE-NAMED TO SPECIFY AIRPORT CODE, NOT NAME***
    	List<Airport> airports = getAirportsServiced(airline);
        String airportRequested = airport;
        for (Iterator<Airport> iter = airports.iterator(); iter.hasNext(); ) {
        	Airport checkAirport = iter.next();
        	if (airportRequested.equals(checkAirport.getAirportCode()) )
        		return true;
        }
        return false;
    }
    
    /**
     * Returns the list of Flights for a given airline for given origination and destination airport
     * @param airline
     * @param originationAirPort
     * @param destinationAirPort
     * @return 
     */
    public List<FlightInfo> getFlightsFor(String airline, String originationAirPort, 
    		String destinationAirPort) {
        // TODO:  Call getFlightsFor for a airline and return subset of list which 
    	// contains the origination and destination airport
    	List<FlightInfo> flights = new ArrayList<>();
    	List<FlightInfo> servicePoints = getFlightsFor(airline);
    	for (Iterator<FlightInfo> iter = servicePoints.iterator(); iter.hasNext(); ) {
        	FlightInfo checkFlight = iter.next();
        	if (originationAirPort.equals(checkFlight.getOrigination().getAirportCode() )
        		&& destinationAirPort.equals(checkFlight.getDestination().getAirportCode()))
        		flights.add(checkFlight);
        }
   
    		return flights;	
    }

    
    /**
     * Flight information for a given airline
     * @param airline
     * @return 
     */
    @Override
    public List<FlightInfo> getFlightsFor(String airline) {
        List<FlightInfo> flights = new ArrayList<>();
        try { 
        	Class.forName(DB_DRIVER);
        	
        	Connection con = getConnection();
        	
        	Statement stmt = con.createStatement();
        	
        	ResultSet rs = stmt.executeQuery("select * from mock737airportservice");
        	
        	while (rs.next()){
        		FlightInfo a = map2(rs);
        		flights.add(a);        	
        	}
        	
        	rs.close();
        	stmt.close();
        	con.close();
    	
    	} catch (SQLException | ClassNotFoundException ex) {
    		// Will have to work out the proper exception handling b/c this is not 
    		// proper
    		log.log(Level.SEVERE, null, ex);
    	}
        
        return flights;
    }
    
    private Airport map1(ResultSet rs) {
    	Airport a = new Airport();
        try {
			    a.setAirportCode(rs.getString(2));
			} catch (SQLException ex) {
			    log.log(Level.SEVERE, null, ex);
			    }   
		return a;
        }
    
    private FlightInfo map2(ResultSet rs) {
    	FlightInfo a;
    	String flightNum = null, flightTime = null;
    	Airport origin = null, destination = null;
        try {
			flightNum = rs.getString(1) + Integer.toString(rs.getInt(4));
			origin = new Airport(rs.getString(2));
			destination = new Airport(rs.getString(3));
			flightTime = rs.getString(5);
			} catch (SQLException ex) {
			    log.log(Level.SEVERE, null, ex);
			    }   
		a = new FlightInfo(flightNum, origin, destination, flightTime);
		return a;
        }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionURL, userName, password);
    }
}
