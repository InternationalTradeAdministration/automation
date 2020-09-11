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

public class LicenseReport {

	private JFrame frame;
	private static String[] mois = {"ALL", "Jan","Feb","Mar","Apr","May","June",
			"Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
	private String[] countryList = {"ALL", "Canada", "China", "Brazile"};
	private String[] statusList = {"ALL", "Submitted", "Cancelled", "Corrected"};
	LinkedHashMap<String, String> monthNum = new LinkedHashMap<String, String>();
	JLabel customValue = new JLabel("0");
	JLabel weightOfImport = new JLabel("0");
	JLabel countOfLicense = new JLabel("0");
	SimpleDateFormat sampleFormat = new SimpleDateFormat("MM/dd/yyyy");
	private JComboBox countryOFOrigin, status, countryOfMeltAndPour, 
	applicationMonth, importationMonth;
	private JTextField applicationYear;
	private JTextField licenseNumber;
	private JTextField applicantName;
	private JTextField htsNumber;
	private Date stDate, finDate;
	private JTextField importer;
	private JTextField exporter;
	private JTextField importationYear;
	 
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LicenseReport window = new LicenseReport();
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
	public LicenseReport() throws ParseException {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws ParseException 
	 */
	private void initialize() throws ParseException {
		frame = new JFrame();
		frame.setBounds(100, 100, 865, 427);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		stDate = sampleFormat.parse("02/19/2010");
		finDate = sampleFormat.parse("02/19/2010");
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
		countryOfMeltAndPour.setSelectedIndex(0);
		importer.setText("ALL");
		exporter.setText("ALL");
		htsNumber.setText("ALL");
		applicationYear.setText("ALL");
		applicationMonth.setSelectedIndex(0);
		importationYear.setText("ALL");
		importationMonth.setSelectedIndex(0);
		applicantName.setText("ALL");	
		status.setSelectedIndex(0);
		//countOfLicense.setText("ss");
		countOfLicense.setText("0");
		customValue.setText("0");
		weightOfImport.setText("0");
		
			}
		});
		Reset.setBounds(630, 342, 92, 25);
		frame.getContentPane().add(Reset);
		JButton btnCalculate = new JButton("Calculate");
		btnCalculate.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				countOfLicense.setText("...");
				customValue.setText("...");
				weightOfImport.setText("...");
				DecimalFormat df = new DecimalFormat("#,###.00");
				DecimalFormat df1 = new DecimalFormat("#,###");
				DBConnection.connectToDb("simasqlstage.database.windows.net", 
						"SIMAReport", "we$l0v3$s3rf1ng!", "1433", "SIMA");
			/*	String SQL = "select COUNT(Distinct sl.Id) as Count_of_Liceses,"
						+ " convert(DECIMAL,sum (CustomsValue)) as total_Customs_Value_$, "
						+ " sum (Volume) as Total_Weight_Kg"
						+ " from StandardLicense sl  left join  Account acc "
						+ "on sl.accountId = acc.id left join Product p on sl.Id = p.StandardLicenseId";*/
				String sqlQ = buildSqlQuery();
				ResultSet rs = DBConnection.executeSqlQuery(sqlQ);
				try {
					if(rs.getObject("Count_of_Liceses")==null)
						countOfLicense.setText("null");
					else
					countOfLicense.setText( df1.format(rs.getBigDecimal("Count_of_Liceses")));
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					if(rs.getObject("total_Customs_Value_$")==null)
						customValue.setText("null");
					else
					customValue.setText(df.format(rs.getBigDecimal("total_Customs_Value_$")));
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					if(rs.getObject("Total_Weight_Kg")==null) 
						weightOfImport.setText("null");
					else
					weightOfImport.setText(df.format(rs.getBigDecimal("Total_Weight_Kg")));
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
			}//action
		});//btnCalculate.
		btnCalculate.setBounds(734, 342, 103, 25);
		frame.getContentPane().add(btnCalculate);
		JLabel customValueLabel = new JLabel("Custom Value: ....................");
		customValueLabel.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
		customValueLabel.setBounds(12, 278, 190, 25);
		frame.getContentPane().add(customValueLabel);
		JLabel weightOfImportLabel = new JLabel("Weight Of Import: ....................");
		weightOfImportLabel.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
		weightOfImportLabel.setBounds(12, 318, 190, 25);
		frame.getContentPane().add(weightOfImportLabel);
		JLabel countOfLicenseLabel = new JLabel("Count Of Licenses: ....................");
		countOfLicenseLabel.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
		countOfLicenseLabel.setBounds(12, 240, 190, 25);
		frame.getContentPane().add(countOfLicenseLabel);
		customValue.setFont(new Font("Tahoma", Font.BOLD, 14));
		customValue.setBounds(214, 281, 253, 22);
		frame.getContentPane().add(customValue);
		weightOfImport.setFont(new Font("Tahoma", Font.BOLD, 14));
		weightOfImport.setBounds(214, 321, 260, 22);
		frame.getContentPane().add(weightOfImport);
		countOfLicense.setFont(new Font("Tahoma", Font.BOLD, 14));
		countOfLicense.setBounds(214, 243, 286, 22);
		frame.getContentPane().add(countOfLicense);
		JLabel lblNewLabel_4 = new JLabel("Applicant Name");
		lblNewLabel_4.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
		lblNewLabel_4.setBounds(584, 98, 109, 16);
		frame.getContentPane().add(lblNewLabel_4);
		
		JLabel lblNewLabel_5 = new JLabel("License Number");
		lblNewLabel_5.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
		lblNewLabel_5.setBounds(12, 12, 129, 16);
		frame.getContentPane().add(lblNewLabel_5);
		
		JLabel lblProductCategory = new JLabel("HTS Number");
		lblProductCategory.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
		lblProductCategory.setBounds(608, 58, 81, 16);
		frame.getContentPane().add(lblProductCategory);
		
		JLabel lblCountryOfOrgn = new JLabel("Country Of Orgn");
		lblCountryOfOrgn.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
		lblCountryOfOrgn.setBounds(262, 9, 109, 22);
		frame.getContentPane().add(lblCountryOfOrgn);
		
		countryOFOrigin = new JComboBox(countryList);
		countryOFOrigin.setBounds(383, 10, 124, 20);
		frame.getContentPane().add(countryOFOrigin);
		
		applicationYear = new JTextField();
		applicationYear.setText("ALL");
		applicationYear.setBounds(124, 95, 64, 22);
		frame.getContentPane().add(applicationYear);
		applicationYear.setColumns(10);
		
		licenseNumber = new JTextField();
		licenseNumber.setText("ALL");
		licenseNumber.setColumns(10);
		licenseNumber.setBounds(124, 8, 126, 25);
		frame.getContentPane().add(licenseNumber);
		
		applicantName = new JTextField();
		applicantName.setText("ALL");
		applicantName.setColumns(10);
		applicantName.setBounds(700, 95, 129, 22);
		frame.getContentPane().add(applicantName);
		
		htsNumber = new JTextField();
		htsNumber.setText("ALL");
		htsNumber.setColumns(10);
		htsNumber.setBounds(700, 55, 129, 22);
		frame.getContentPane().add(htsNumber);
		
		status = new JComboBox(statusList);
		status.setBounds(700, 133, 129, 22);
		frame.getContentPane().add(status);
		
		countryOfMeltAndPour = new JComboBox(countryList);
		countryOfMeltAndPour.setBounds(700, 9, 129, 22);
		frame.getContentPane().add(countryOfMeltAndPour);
		
		JLabel lblCountryOfMelt = new JLabel("Country Of Pour&Melt");
		lblCountryOfMelt.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
		lblCountryOfMelt.setBounds(543, 9, 150, 22);
		frame.getContentPane().add(lblCountryOfMelt);
		
		importer = new JTextField();
		importer.setText("ALL");
		importer.setColumns(10);
		importer.setBounds(124, 54, 126, 25);
		frame.getContentPane().add(importer);
		
		exporter = new JTextField();
		exporter.setText("ALL");
		exporter.setColumns(10);
		exporter.setBounds(383, 55, 124, 22);
		frame.getContentPane().add(exporter);
		
		JLabel lblImporter = new JLabel("Importer");
		lblImporter.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
		lblImporter.setBounds(45, 58, 67, 16);
		frame.getContentPane().add(lblImporter);
		
		JLabel lblExporter = new JLabel("Exporter");
		lblExporter.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
		lblExporter.setBounds(304, 58, 67, 16);
		frame.getContentPane().add(lblExporter);
		
		JLabel lblApplicantYe = new JLabel("Application Year");
		lblApplicantYe.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
		lblApplicantYe.setBounds(3, 98, 109, 16);
		frame.getContentPane().add(lblApplicantYe);
		
		JLabel lblApplicationMonth = new JLabel("Application Month");
		lblApplicationMonth.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
		lblApplicationMonth.setBounds(242, 98, 129, 16);
		frame.getContentPane().add(lblApplicationMonth);
		
		applicationMonth = new JComboBox(mois);
		applicationMonth.setBounds(383, 96, 66, 20);
		frame.getContentPane().add(applicationMonth);
		
		JLabel lblImportationYear = new JLabel("Importation Year");
		lblImportationYear.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
		lblImportationYear.setBounds(10, 133, 116, 19);
		frame.getContentPane().add(lblImportationYear);
		
		importationYear = new JTextField();
		importationYear.setText("ALL");
		importationYear.setColumns(10);
		importationYear.setBounds(124, 130, 64, 22);
		frame.getContentPane().add(importationYear);
		
		JLabel lblImportationMonth = new JLabel("Importation Month");
		lblImportationMonth.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
		lblImportationMonth.setBounds(242, 134, 129, 16);
		frame.getContentPane().add(lblImportationMonth);
		
		importationMonth = new JComboBox(mois);
		importationMonth.setBounds(383, 132, 66, 20);
		frame.getContentPane().add(importationMonth);
		
		JLabel lblStatus = new JLabel("Status");
		lblStatus.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
		lblStatus.setBounds(642, 136, 109, 16);
		frame.getContentPane().add(lblStatus);		
		
	}
	
	public String buildSqlQuery()
	{
		boolean wr = false, and = false;
		String sqlString = "select COUNT(Distinct sl.Id) as Count_of_Liceses,"
				+ " convert(DECIMAL,sum (CustomsValue)) as total_Customs_Value_$, "
				+ " sum (Volume) as Total_Weight_Kg"
				+ " from StandardLicense sl  left join  Account acc "
				+ "on sl.accountId = acc.id left join Product p on sl.Id = p.StandardLicenseId ";
		if (!licenseNumber.getText().equalsIgnoreCase("ALL")) 
		{
			sqlString = sqlString + " where sl.LicenseNumber ='" +licenseNumber.getText()+"'" ;
			wr = true;
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
		if (!((String) countryOfMeltAndPour.getSelectedItem()).equalsIgnoreCase("ALL")) 
		{
			if(wr) sqlString = sqlString + " and p.CountryofMeltAndPour ='"
					+ ""+countryOfMeltAndPour.getSelectedItem().toString()+"'";
			else
			{
				sqlString = sqlString + " where p.CountryofMeltAndPour ='"
						+ ""+countryOfMeltAndPour.getSelectedItem().toString()+"'";
				wr = true;
			}
		}
		if (!importer.getText().equalsIgnoreCase("ALL")) 
		{
			if (wr)	sqlString = sqlString + " and sl.ImporterName ='" +importer.getText()+"'";
			else
			{
				sqlString = sqlString + " where sl.ImporterName ='" +importer.getText()+"'";
				wr = true;
			}
		}
		if (!exporter.getText().equalsIgnoreCase("ALL")) 
		{
			if (wr)	sqlString = sqlString + " and sl.ImporterName ='" +exporter.getText()+"'";
			else
			{
				sqlString = sqlString + " where sl.ImporterName ='" +exporter.getText()+"'";
				wr = true;
			}
			
		}
		if (!htsNumber.getText().equalsIgnoreCase("ALL")) 
		{
			if (wr)	sqlString = sqlString + " and p.HTSNumber ='" +htsNumber.getText()+"'";
			else
			{
				sqlString = sqlString + " where p.HTSNumber ='" +htsNumber.getText()+"'";
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
		if (!((String) applicationMonth.getSelectedItem()).equalsIgnoreCase("ALL")) 
		{
			if(wr) sqlString = sqlString + " and MONTH(sl.ApplicationDate)  ='"
					+monthNum.get(applicationMonth.getSelectedItem().toString())+"'";
			else
			{
				sqlString = sqlString + " where sl.LicenseStatus  ='"
						+monthNum.get(applicationMonth.getSelectedItem().toString())+"'";
				wr = true;
			}
		}
		if (!((String) importationMonth.getSelectedItem()).equalsIgnoreCase("ALL")) 
		{
			if(wr) sqlString = sqlString + " and MONTH(sl.ExpectedDateOfImportation)  ='"
					+ monthNum.get(importationMonth.getSelectedItem().toString())+"'";
			else
			{
				sqlString = sqlString + " where MONTH(sl.ExpectedDateOfImportation) ='"
						+monthNum.get(importationMonth.getSelectedItem().toString())+"'";
				wr = true;
			}
		}
		if (!((String) status.getSelectedItem()).equalsIgnoreCase("ALL")) 
		{
			if(wr) sqlString = sqlString + " and sl.LicenseStatus  ='"
					+ ""+status.getSelectedItem().toString()+"'";
			else
			{
				sqlString = sqlString + " where sl.LicenseStatus='"
						+ status.getSelectedItem().toString()+"'";
				wr = true;
			}
		}
		if (!applicationYear.getText().equalsIgnoreCase("ALL")) 
		{
			if (wr)	sqlString = sqlString + " and YEAR(sl.ApplicationDate) ='" +applicationYear.getText()+"'";
			else
			{
				sqlString = sqlString + " where YEAR(sl.ApplicationDate) ='" +applicationYear.getText()+"'";
				wr = true;
			}
		}
		if (!importationYear.getText().equalsIgnoreCase("ALL")) 
		{
			if (wr)	sqlString = sqlString + " and YEAR(sl.ExpectedDateOfImportation) ='" +importationYear.getText()+"'";
			else
			{
				sqlString = sqlString + " where YEAR(sl.ExpectedDateOfImportation) ='" +importationYear.getText()+"'";
				wr = true;
			}
		}
		return sqlString;
	}
}
