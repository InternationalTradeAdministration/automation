import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;

public class DBConnection 
{
	private static String connectionUrl;
	public static Connection con;
	private static Statement stmt;
	DBConnection()
	{
		//
	}
	public static void connectToDb(String server, 
									String user, 
									String password, 
									String port, 
									String dbName)
	{
		connectionUrl = "jdbc:sqlserver://"+server+":"+port+";"
        		+ "databaseName="+dbName+";"
        		+ "user="+user+";"
        		+ "password="+password;

        try 
        {
        con = DriverManager.getConnection(connectionUrl);
        System.out.println("Connected Successfully ...");
        } 
        catch (SQLException e) 
        {
        	System.out.println("Connection issue:");
            e.printStackTrace();
        }
	}
	
	public static ResultSet executeSqlQuery(String sqlQuery)
	{
		ResultSet rs = null;
		 try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sqlQuery);
			if (rs.next())
            {
				System.out.println(rs.getRow());
                System.out.println(rs.getString("Count_of_Liceses") + " " 
            + rs.getString("total_Customs_Value_$") + " "+ rs.getString("Total_Weight_Kg"));
            }else
            {
            	System.out.println("No results has returned");
            }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
	}
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
	

		        // Create a variable for the connection string.
		        connectionUrl = "jdbc:sqlserver://simasqlstage.database.windows.net:1433;"
		        		+ "databaseName=SIMA;"
		        		+ "user=SIMAReport;"
		        		+ "password=we$l0v3$s3rf1ng!";

		        try (Connection con = DriverManager.getConnection(connectionUrl);
		        	Statement stmt = con.createStatement();) {
		            String SQL = "select COUNT(Distinct sl.Id) as Count_of_Liceses,"
				            +" SUM (CustomsValue) as total_Customs_Value_$,  sum (Volume) as Total_Weight_Kg"
				            +" from StandardLicense sl  left join  Account acc on sl.accountId = acc.id"
				            +" left join Product p on sl.Id = p.StandardLicenseId";
				            /*+" where "
				            +" sl.ApplicationDate >= '06/10/2020'"
				            +" and sl.ApplicationDate <= '06/11/2020' "
				            + " and sl.CountryofOrigin = 'Algeria'";*/
		           // String SQL = "select count(*) as this from standardlicense";
		            ResultSet rs = stmt.executeQuery(SQL);
		            System.out.println(rs);
		            // Iterate through the data in the result set and display it.
		            while (rs.next())
		            {
		                System.out.println(rs.getString("Count_of_Liceses") + " " 
		            + rs.getFloat("total_Customs_Value_$") + " "+ rs.getString("Total_Weight_Kg"));
		            }
		        }
		        // Handle any errors that may have occurred.
		        catch (SQLException e) {
		            e.printStackTrace();
		    }

	}

}
