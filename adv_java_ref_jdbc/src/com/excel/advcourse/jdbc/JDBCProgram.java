package com.excel.advcourse.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Types;

import org.apache.log4j.Logger;

public class JDBCProgram {


	private static Logger log = Logger.getLogger(JDBCProgram.class);	

	static Connection con = null;
	static Statement stmt =null;
	static PreparedStatement pstmt = null;
	CallableStatement cs = null;
	ResultSet rs = null;

	private static String driver = "oracle.jdbc.driver.OracleDriver";
	static String connectionURL = "jdbc:oracle:thin:@localhost:1521/XE";
	/*
	 * MAIN METHOD
	 */
	public static void main(String ...arg) throws Exception {
//		new JDBCProgram().selectQuery(100);
//		new JDBCProgram().preparedStatement(100,"Steven");
//				new JDBCProgram().createTable();
//				new JDBCProgram().createProcedureShowEmployees();
//				new JDBCProgram().callProcedure();
//				new JDBCProgram().insertRowInDBAndRollBack(28, "28");
//				new JDBCProgram().savePoint();
//				new JDBCProgram().addBatch();
	}

	static{
		if(log.isDebugEnabled())
			log.debug("Loading Driver....");

		if(log.isInfoEnabled())
			log.info("Loading Driver....");

		try{
			Class.forName(driver);//Loading a driver...

			//			Driver myDriver = new oracle.jdbc.driver.OracleDriver(); 	
			//			DriverManager.registerDriver( myDriver );

		}catch(Exception exp){
			exp.printStackTrace();
			log.error("Issues in Class Not Found"+ exp.getMessage());
		}

		try{
			con = DriverManager.getConnection(connectionURL,"hr","hr");
		}catch(Exception exp){
			exp.printStackTrace();
		}
	}


	/**
	 * 
	 */
	public void addBatch(){
		try{
			con.setAutoCommit(false); 
			Statement stmt = con.createStatement();  

			String SQL = "INSERT INTO MYEXCEL " + "VALUES (107, 'Rita')";
			stmt.addBatch(SQL);

			String SQL2 = "INSERT INTO MYEXCEL " + "VALUES (105, 'Rita')";
			stmt.addBatch(SQL2);

			String SQL3 = "update myexcel set name ='RAJA' where id = 107";
			stmt.addBatch(SQL3);

			int[] intArray = stmt.executeBatch();
			log.info(intArray.length);

		}catch(SQLException se){
			try{
				log.error("rolling back...");
				con.rollback(); 

			}catch(Exception exp){
				exp.printStackTrace();
			}
		}finally{
			closeConnections(rs,stmt,con);
		}
	}
	/**
	 * 
	 */
	public void savePoint(){
		Savepoint savepoint1= null;
		Savepoint savepoint2= null;

		try{
			con.setAutoCommit(false); 
			Statement stmt = con.createStatement(); //set a Savepoint 

			savepoint1 = con.setSavepoint("Savepoint1"); 
			String SQL = "INSERT INTO MYEXCEL " + "VALUES (106, 'Rita6')";


			stmt.executeUpdate(SQL); //Submit a malformed SQL statement that breaks String SQL = "INSERTED IN Employees " + "VALUES (107, 22, 'Sita', 'Tez')"; stmt.executeUpdate(SQL); // If there is no error, commit the changes. conn.commit();

			savepoint2 = con.setSavepoint("Savepoint2"); 
			String SQL2 = "INSERT INTO MYEXCEL " + "VALUES (107, 'Rita7')";

			stmt.executeUpdate(SQL2);
			
			int i = 4/0;
		}catch(Exception se){ // If there is any error. 
			try{
				log.error("rolling back...");
				con.rollback(savepoint2); 

			}catch(Exception exp){
				exp.printStackTrace();
			}
		}finally{
			closeConnections(rs,stmt,con);
		}
	}


	public void insertRowInDBAndRollBack(int id , String name){
		try{
			con.setAutoCommit(false);

			pstmt = con.prepareStatement("insert into myExcel values(?,?)");

			pstmt.setInt(1, id);
			pstmt.setString(2, name);
			
//			pstmt.setInt(1, Integer.parseInt(id+"a"));
//			pstmt.setString(2, name+"1");

			int intValue = pstmt.executeUpdate();
			
			log.info("Return value from insert "+ intValue);
			int i = 4/0;
			
			con.commit();
		}catch(Exception exp){
			exp.printStackTrace();
			try{
				con.rollback();
			}catch(Exception exp1){
				exp1.printStackTrace();
			}
		}finally{
			closeConnections(rs,stmt,con);
		}
	}


	public void insertRowInDB(int id , String name){
		try{
			pstmt = con.prepareStatement("insert into myExcel values(?,?)");
			pstmt.setInt(1, id);
			pstmt.setString(2, name);
			int intValue = 0;
			intValue = pstmt.executeUpdate();
			log.info("Return value from insert "+ intValue);
		}catch(SQLException exp){
			exp.printStackTrace();
		}finally{
			closeConnections(rs,stmt,con);
		}
	}

	/**
	 * @param args
	 * Statement
	 */
	public void selectQuery(int empid) {
		try{
			stmt = con.createStatement();
			rs = stmt.executeQuery("select * from employees where employee_id = "+empid);
			while(rs.next()){
				System.out.println(rs.getString("first_name") + " "+ rs.getString("last_name") +" "+ rs.getString(1));
			}

		}catch(SQLException exp){
			exp.printStackTrace();
		}finally{
			closeConnections(rs,stmt,con);
		}
	}

	/*
	 * 
	 */
	public void preparedStatement(int empid,String name){
		try{
			pstmt = con.prepareStatement("select * from employees where employee_id = ? and first_name = ?");
			pstmt.setInt(1, empid);
			pstmt.setString(2, name);
			
			rs = pstmt.executeQuery();
			while(rs.next()){
				System.out.println(rs.getString("first_name") + " "+ rs.getString("last_name") +" "+ rs.getString(1));
			}
		}catch(SQLException exp){
			exp.printStackTrace();
		}finally{
			closeConnections(rs,stmt,con);
		}
	}
	/**
	 * 
	 */
	public void createTable() {
		try{
			stmt = con.createStatement();
			boolean isExecuted = stmt.execute("create table MyExcel(id number(4) , name varchar2(20))");
			System.out.println("Table got created..."+ isExecuted);
		}catch(SQLException exp){
			exp.printStackTrace();
		}finally{
			closeConnections(rs,stmt,con);
		}
	}
	private void closeConnections(ResultSet rs , Statement stmt , Connection con){
		try{
			if(rs != null){
				rs.close();
			}
			if(stmt != null){
				stmt.close();
			}
			if(con != null){
				con.close();
			}
		}catch(Exception exp){
			exp.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	public void callProcedure(){
		try{
			cs = con.prepareCall("{call SHOW_EMPLOYEES(?,?)}");

			cs.setString(1, "Nancy");
			cs.registerOutParameter(2, Types.INTEGER);
			
			cs.executeQuery();

			int empid = cs.getInt(2);

			System.out.println("JDBCProgram.callProcedure()"+ empid);
		}catch(Exception exp){
			exp.printStackTrace();
		}finally{
			closeConnections(rs,stmt,con);			
		}
	}
	/**
	 * 
	 * @throws SQLException
	 */
	public void	createProcedureShowEmployees()
			throws SQLException {
		String createProcedure = null;
		String queryDrop =
				"DROP PROCEDURE  SHOW_EMPLOYEES";

		createProcedure =
				"create procedure SHOW_EMPLOYEES(out myLN) IS " +
						"begin " +
						"SELECT EMP.LAST_NAME into myLN FROM EMPLOYEES EMP , DEPARTMENTS DEPT" +
						" where dept.department_id = emp.department_id"+
						" and EMP.first_NAME ='Nancy';"+
						" end;";
		//		Statement stmt = null;
		//		Statement stmtDrop = null;
		//		try {
		//			System.out.println("Calling DROP PROCEDURE");
		//			stmtDrop = con.createStatement();
		//			//stmtDrop.execute(queryDrop);
		//		} catch (SQLException e) {
		//			e.printStackTrace();
		//		} finally {
		//			if (stmtDrop != null)
		//			{
		//				stmtDrop.close();
		//			}
		//		}

		try {
			stmt = con.createStatement();
			stmt.executeUpdate(createProcedure);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (stmt != null) { stmt.close(); }
		}
	}

}