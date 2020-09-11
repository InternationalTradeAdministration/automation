import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import com.toedter.calendar.JCalendar;
import com.toedter.components.JSpinField;
import com.toedter.calendar.JDateChooser;
import java.awt.Font;
import javax.swing.JTextArea;

public class CorrReport {

	private JFrame frame;
	private static String[] mois = {"ALL", "Jan","Feb","Mar","Apr","May","June",
			"Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
	private String[] countryList = {"ALL", "Canada", "China", "Brazil"};
	private String[] statusList = {"ALL", "Submitted", "Cancelled", "Corrected"};
	LinkedHashMap<String, String> monthNum = new LinkedHashMap<String, String>();
	JLabel countOfApplicants = new JLabel("0");
	JLabel correspondenceVolume = new JLabel("0");
	SimpleDateFormat sampleFormat = new SimpleDateFormat("MM/dd/yyyy");
	private JComboBox countryOFOrigin, 
	createdMonth;
	private JTextField createdYear;
	private JTextField licenseNumber;
	private JTextField applicantName;
	private JTextField sentFrom;
	private JTextArea top10Countries, top10Applicants, volumeByCreatedDate;
	 
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CorrReport window = new CorrReport();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws ParseException 
	 */
	public CorrReport() throws ParseException {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws ParseException 
	 */
	private void initialize() throws ParseException {
		frame = new JFrame();
		frame.setBounds(100, 100, 900, 704);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		JButton Reset = new JButton("Reset");
		monthNum.put("ALL", "0");monthNum.put("Jan", "1");
		monthNum.put("Feb", "2");monthNum.put("Mar", "3");
		monthNum.put("Apr", "4");monthNum.put("May", "5");
		monthNum.put("June", "6");monthNum.put("Jul", "7");
		monthNum.put("Aug", "8");monthNum.put("Sep", "9");
		monthNum.put("Oct", "10");monthNum.put("Nov", "11");
		monthNum.put("Dec", "12");
		Reset.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {			
		licenseNumber.setText("ALL");
		countryOFOrigin.setSelectedIndex(0);
		createdYear.setText("ALL");
		createdMonth.setSelectedIndex(0);
		applicantName.setText("ALL");	
		//countOfLicense.setText("ss");
		correspondenceVolume.setText("0");
		countOfApplicants.setText("0");
		sentFrom.setText("ALL");
		top10Countries.setText("N/A");
		top10Applicants.setText("N/A");
		volumeByCreatedDate.setText("N/A");
		
			}
		});
		Reset.setBounds(663, 592, 92, 25);
		frame.getContentPane().add(Reset);
		JButton btnCalculate = new JButton("Calculate");
		btnCalculate.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				String selectClause="", gbClause="";
				correspondenceVolume.setText("...");
				countOfApplicants.setText("...");
				//DecimalFormat df = new DecimalFormat("#,###.00");
				DecimalFormat df1 = new DecimalFormat("#,###");
				DBConnection.connectToDb("simasqlstage.database.windows.net", 
						"SIMAReport", "we$l0v3$s3rf1ng!", "1433", "SIMA");
				
				selectClause = "select COUNT(Distinct corr.Id) as Count_of_emails,"
						+ " COUNT(Distinct acc.Id) as Count_of_applicants " 
				 + "from Account acc left join StandardLicense sl on acc.id = sl.AccountId "
				 + "left join Correspondence corr on sl.id = corr.StandardLicenseId ";
				
				String sqlQ = buildSqlQuery(selectClause, gbClause);
				ResultSet rs = DBConnection.executeSqlQuery(sqlQ);
				try {
					rs.first();
					if(rs.getObject("Count_of_emails")==null)
						correspondenceVolume.setText("null");
					else
						System.out.println(df1.format(rs.getBigDecimal("Count_of_emails")));
					correspondenceVolume.setText( df1.format(rs.getBigDecimal("Count_of_emails")));
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					if(rs.getObject("Count_of_applicants")==null)
						countOfApplicants.setText("null");
					else
					countOfApplicants.setText(df1.format(rs.getBigDecimal("Count_of_applicants")));
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//Volume by Year
				selectClause = "select YEAR(corr.Created) as YEAR, "
						+ "COUNT(Distinct corr.Id) as Correspondence_Volume"
						 +" from Account acc"
						 +" left join StandardLicense sl on acc.id = sl.AccountId"
						 +" left join Correspondence corr on sl.id = corr.StandardLicenseId";
				gbClause = "group by YEAR(corr.Created)";
				sqlQ = buildSqlQuery(selectClause, gbClause);
				rs = DBConnection.executeSqlQuery(sqlQ);
				String result="", yr, cv, appName,originCountry;
				try {
					while (rs.next()) {
					     yr = rs.getString(1);
					     cv = rs.getString(2);
					    result = result + yr+":----------> "+cv+ "\n" ;
					    // Do whatever you want to do with these 2 values
					}
				volumeByCreatedDate.setText(result);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
				
				//
				
				//top 10 applicants by volume
				selectClause = "select Top 10 CONCAT (acc.givenName , ' ', acc.surname) as Applicant_Name," 
				 +" COUNT(Distinct corr.Id) as Correspondence_Volume from Account acc"
				 +" left join StandardLicense sl on acc.id = sl.AccountId"
				 +" left join Correspondence corr on sl.id = corr.StandardLicenseId";
				gbClause ="group by CONCAT (acc.givenName , ' ', acc.surname) order by COUNT(Distinct corr.Id) desc";
				sqlQ = buildSqlQuery(selectClause, gbClause);
				rs = DBConnection.executeSqlQuery(sqlQ);
				result="";
				try {
					while (rs.next()) {
					     appName = rs.getString(1);
					     cv = rs.getString(2);
					    result = result  + appName+":----------> "+cv+ "\n";
					    // Do whatever you want to do with these 2 values
					}
				top10Applicants.setText(result);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//top 10 Countries of origine by volume
				selectClause = "select Top 10  sl.CountryofOrigin, COUNT(Distinct corr.Id) as Correspondence_Volume"
				+" from Account acc left join StandardLicense sl on acc.id = sl.AccountId"
				+" left join Correspondence corr on sl.id = corr.StandardLicenseId";
				gbClause ="group by  sl.CountryofOrigin order by COUNT(Distinct corr.Id) desc";
				sqlQ = buildSqlQuery(selectClause, gbClause);
				rs = DBConnection.executeSqlQuery(sqlQ);
				result="";
				try {
					while (rs.next()) {
					     originCountry = rs.getString(1);
					     cv = rs.getString(2);
					    result = result + originCountry+":----------> "+cv + "\n" ;
					    // Do whatever you want to do with these 2 values
					}
				top10Countries.setText(result);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}//action
		});//btnCalculate.
		btnCalculate.setBounds(767, 592, 103, 25);
		frame.getContentPane().add(btnCalculate);
		JLabel customValueLabel = new JLabel("Count Of Applicants: ..........");
		customValueLabel.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
		customValueLabel.setBounds(12, 173, 190, 25);
		frame.getContentPane().add(customValueLabel);
		JLabel countOfLicenseLabel = new JLabel("Correspondence Volume: ....................");
		countOfLicenseLabel.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
		countOfLicenseLabel.setBounds(12, 135, 190, 25);
		frame.getContentPane().add(countOfLicenseLabel);
		countOfApplicants.setFont(new Font("Tahoma", Font.BOLD, 14));
		countOfApplicants.setBounds(214, 173, 253, 22);
		frame.getContentPane().add(countOfApplicants);
		correspondenceVolume.setFont(new Font("Tahoma", Font.BOLD, 14));
		correspondenceVolume.setBounds(213, 135, 286, 22);
		frame.getContentPane().add(correspondenceVolume);
		JLabel lblNewLabel_4 = new JLabel("Applicant Name");
		lblNewLabel_4.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
		lblNewLabel_4.setBounds(502, 13, 109, 16);
		frame.getContentPane().add(lblNewLabel_4);
		
		JLabel lblNewLabel_5 = new JLabel("License Number");
		lblNewLabel_5.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
		lblNewLabel_5.setBounds(502, 60, 129, 16);
		frame.getContentPane().add(lblNewLabel_5);
		
		JLabel lblCountryOfOrgn = new JLabel("Country Of Orgn");
		lblCountryOfOrgn.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
		lblCountryOfOrgn.setBounds(12, 57, 109, 22);
		frame.getContentPane().add(lblCountryOfOrgn);
		
		countryOFOrigin = new JComboBox(countryList);
		countryOFOrigin.setBounds(133, 58, 124, 20);
		frame.getContentPane().add(countryOFOrigin);
		
		createdYear = new JTextField();
		createdYear.setText("ALL");
		createdYear.setBounds(133, 10, 64, 22);
		frame.getContentPane().add(createdYear);
		createdYear.setColumns(10);
		
		licenseNumber = new JTextField();
		licenseNumber.setText("ALL");
		licenseNumber.setColumns(10);
		licenseNumber.setBounds(623, 56, 126, 25);
		frame.getContentPane().add(licenseNumber);
		
		applicantName = new JTextField();
		applicantName.setText("ALL");
		applicantName.setColumns(10);
		applicantName.setBounds(623, 10, 129, 22);
		frame.getContentPane().add(applicantName);
		
		JLabel lblApplicantYe = new JLabel("Created Year");
		lblApplicantYe.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
		lblApplicantYe.setBounds(12, 13, 109, 16);
		frame.getContentPane().add(lblApplicantYe);
		
		JLabel lblApplicationMonth = new JLabel("Created Month");
		lblApplicationMonth.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
		lblApplicationMonth.setBounds(284, 13, 109, 16);
		frame.getContentPane().add(lblApplicationMonth);
		
		createdMonth = new JComboBox(mois);
		createdMonth.setBounds(393, 11, 74, 18);
		frame.getContentPane().add(createdMonth);
		
		JLabel lblFrom = new JLabel("From");
		lblFrom.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
		lblFrom.setBounds(284, 60, 55, 16);
		frame.getContentPane().add(lblFrom);
		
		sentFrom = new JTextField();
		sentFrom.setText("ALL");
		sentFrom.setColumns(10);
		sentFrom.setBounds(341, 57, 126, 25);
		frame.getContentPane().add(sentFrom);
		
		volumeByCreatedDate = new JTextArea(); 
		volumeByCreatedDate.setBounds(12, 315, 200, 237);
		frame.getContentPane().add(volumeByCreatedDate);
		
		JLabel lblVolumeByCreated = new JLabel("Volume By Created Date");
		lblVolumeByCreated.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
		lblVolumeByCreated.setBounds(12, 267, 190, 25);
		frame.getContentPane().add(lblVolumeByCreated);
		
		JLabel lblTopApplicants = new JLabel("Top 10 Applicants By Volume");
		lblTopApplicants.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
		lblTopApplicants.setBounds(267, 267, 190, 25);
		frame.getContentPane().add(lblTopApplicants);
		
		top10Applicants = new JTextArea();
		top10Applicants.setBounds(267, 315, 200, 237);
		frame.getContentPane().add(top10Applicants);
		
		JLabel lblTopCountries = new JLabel("Top 10 Countries By Volume");  
		lblTopCountries.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
		lblTopCountries.setBounds(502, 267, 190, 25);
		frame.getContentPane().add(lblTopCountries);
		
		top10Countries = new JTextArea();
		top10Countries.setBounds(512, 315, 200, 237);
		frame.getContentPane().add(top10Countries);
		
	}
	
	public String buildSqlQuery(String sqlString, String gbClause)
	{
		boolean wr = false, and = false;
		if (!createdYear.getText().equalsIgnoreCase("ALL")) 
		{
			sqlString = sqlString + " where YEAR(corr.Created) ='" +createdYear.getText()+"'" ;
			wr = true;
		}
		if (!((String) createdMonth.getSelectedItem()).equalsIgnoreCase("ALL")) 
		{
			if(wr)
			sqlString = sqlString + " and MONTH(corr.Created) = '"+monthNum.get(createdMonth.getSelectedItem().toString())+"'";
			else
			{
				sqlString = sqlString + " where MONTH(corr.Created) = '"+monthNum.get(createdMonth.getSelectedItem().toString())+"'";
				wr = true;
			}
		}
		if (!applicantName.getText().equalsIgnoreCase("ALL")) 
		{
			if (wr)	sqlString = sqlString + " and CONCAT (acc.givenName , ' ', acc.surname)='" +applicantName.getText()+"'";
			else
			{
				sqlString = sqlString + " where CONCAT (acc.givenName , ' ', acc.surname)='" +applicantName.getText()+"'";
				wr = true;
			}
		}
		if (!((String) countryOFOrigin.getSelectedItem()).equalsIgnoreCase("ALL")) 
		{
			if(wr)
			sqlString = sqlString + " and sl.CountryofOrigin = '"+countryOFOrigin.getSelectedItem().toString()+"'";
			else
			{
				sqlString = sqlString + " where sl.CountryofOrigin = '"+countryOFOrigin.getSelectedItem().toString()+"'";
				wr = true;
			}
		}
		
		if (!sentFrom.getText().equalsIgnoreCase("ALL")) 
		{
			if (wr)	sqlString = sqlString + " and corr.[From] = '" +sentFrom.getText()+"'";
			else
			{
				sqlString = sqlString + " where  and corr.[From] = '" +sentFrom.getText()+"'";
				wr = true;
			}
		}
		if (!licenseNumber.getText().equalsIgnoreCase("ALL")) 
		{
			if (wr)	sqlString = sqlString + " and sl.LicenseNumber ='" +licenseNumber.getText()+"'";
			else
			{
				sqlString = sqlString + " where sl.LicenseNumber ='" +licenseNumber.getText()+"'";
				wr = true;
			}
		}
		System.out.println(sqlString + " " + gbClause);
		return sqlString + " " + gbClause;
	}
}
