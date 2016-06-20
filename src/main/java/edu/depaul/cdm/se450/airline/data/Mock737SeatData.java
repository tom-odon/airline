package edu.depaul.cdm.se450.airline.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Mock737SeatData implements SeatData {
    public static final String[] AVAILABLE_AISLE = {"A", "B", "C", "D", "E", "F"};
    public static final int MIN_SEAT_ROW = 1;
    public static final int MAX_SEAT_ROW = 20;
    
	private static Logger log = Logger.getLogger(MockAirportService.class.getName());
	
	private static final String DB_DRIVER = "org.hsqldb.jdbc.JDBCDriver";
  
    private String connectionURL = "jdbc:hsqldb:hsql://localhost/mydb";
    private String userName = "SA";
    private String password = "SA";
    
    public Mock737SeatData(String connectionString, String user, String password) {
        this.connectionURL = connectionString;
        this.userName = user;
        this.password = password;
    }
    
    public Mock737SeatData(){
    	
    }
        
    /**
     * Returns all the seats and whether it is available or not
     * @param flightInfo
     * @return 
     */
    @Override
    public List<AirplaneSeat> getSeatAvailability(String flightInfo)  {
        ArrayList<AirplaneSeat> seats = new ArrayList<AirplaneSeat>();
        try { 
        	Class.forName(DB_DRIVER);
        	
        	Connection con = getConnection();
        	
        	Statement stmt = con.createStatement();
        	
        	ResultSet rs = stmt.executeQuery("select * from mock737seatdata");
        	
        	while (rs.next()){
        		AirplaneSeat a = map(rs);
        		if(a.isAvailable())
        		seats.add(a);        	
        	}
        	
        	rs.close();
        	stmt.close();
        	con.close();
    	
    	} catch (SQLException | ClassNotFoundException ex) {
    		// Will have to work out the proper exception handling b/c this is not 
    		// proper
    		log.log(Level.SEVERE, null, ex);
    	}
        
        return seats;
    }
  
	/**
     * 
     * @param flightInfo
     * @param row
     * @param aisle
     * @return 
     */
    @Override
    public boolean isSeatAvailable(String flightInfo, int row, String aisle) {
        // TODO
        // Call getSeatAvailability with flightInfo and determine whether the 
    	// specific seat is available or not
        List<AirplaneSeat> seats = getSeatAvailability(flightInfo);
        String seatNum = row + aisle;
        for (Iterator<AirplaneSeat> iter = seats.iterator(); iter.hasNext(); ) {
        	AirplaneSeat checkSeat = iter.next();
        	if (seatNum.equals(checkSeat.getSeatNum()) && checkSeat.isAvailable() )
        		return true;
        }
    	return false;
    }
    
    private AirplaneSeat map(ResultSet rs) {
    	String seatNumber  = null;
    	boolean isAvailable = false;
        try {
			    seatNumber = (Integer.toString(rs.getInt(2))+ rs.getString(3));
			    isAvailable = rs.getBoolean(4);
			} catch (SQLException ex) {
			    log.log(Level.SEVERE, null, ex);
			    }
        AirplaneSeat a = new AirplaneSeat(seatNumber, isAvailable);
		return a;
        }
    
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionURL, userName, password);
    }
}
