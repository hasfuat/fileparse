package fileparser.com.tune.play;

import java.io.BufferedWriter;

import org.apache.hive.hcatalog.streaming.ConnectionError;
import org.apache.hive.hcatalog.streaming.DelimitedInputWriter;
import org.apache.hive.hcatalog.streaming.HiveEndPoint;
import org.apache.hive.hcatalog.streaming.ImpersonationFailed;
import org.apache.hive.hcatalog.streaming.InvalidColumn;
import org.apache.hive.hcatalog.streaming.InvalidPartition;
import org.apache.hive.hcatalog.streaming.InvalidTable;
import org.apache.hive.hcatalog.streaming.PartitionCreationFailed;
import org.apache.hive.hcatalog.streaming.SerializationError;
import org.apache.hive.hcatalog.streaming.StreamingConnection;
import org.apache.hive.hcatalog.streaming.StreamingException;
import org.apache.hive.hcatalog.streaming.TransactionBatch;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import fileparser.com.tune.MyGame.rawLog;

import com.twitter.chill.Base64.InputStream;

public class Utility {

	//private static ArrayList<String> rowList;


	public static Properties readParameters(String path) {
		Properties p = new Properties();
		java.io.InputStream in = null;
		try {
			in = new FileInputStream(path);
			p.load(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return p;

	}
	/**
	 * 
	 * @param conf
	 * @param driverName
	 * @throws SQLException
	 * @throws InterruptedException 
	 * @throws InvalidColumn 
	 * @throws SerializationError 
	 * @throws StreamingException 
	 * @throws ClassNotFoundException 
	 */
	
	public static void hiveEndPoint() throws InterruptedException, ClassNotFoundException, SerializationError, InvalidColumn, StreamingException{
		
		
		String dbName = "default";
		String tblName = "test_hive";
		ArrayList<String> partitionVals = new ArrayList<String>();
		//partitionVals.add("Asia");
		//partitionVals.add("India");
		String serdeClass = "org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe";
		 
		String ctable = "create table if not exists test_hive(date string, value string) row format delimited fields terminated by ','"; 
		
		HiveEndPoint hiveEP = new HiveEndPoint("thrift://localhost:10000", dbName, tblName, null);
			 //("thrift://localhost:10000", dbName, tblName, partitionVals); thrift://localhost:10000
		 
		 
		 
		//-------   Thread 1  -------//
		StreamingConnection connection = hiveEP.newConnection(true);
		//field name is column name for folders.
		String nameOfcolumn[] = new String[2];
		nameOfcolumn[0] = "col1";
		nameOfcolumn[1] = "col2";
		
		
		DelimitedInputWriter writer = new    DelimitedInputWriter(nameOfcolumn, ",", hiveEP);
		
		TransactionBatch txnBatch = connection.fetchTransactionBatch(1000, writer);
		
		
		
		
	///// Batch 1 - First TXN
		txnBatch.beginNextTransaction();
		txnBatch.write("1,Hello streaming".getBytes());
		txnBatch.write("2,Welcome to streaming".getBytes());
		txnBatch.commit();
		 
		 
		if(txnBatch.remainingTransactions() > 0) {
		///// Batch 1 - Second TXN
		txnBatch.beginNextTransaction();
		txnBatch.write("3,Roshan Naik".getBytes());
		txnBatch.write("4,Alan Gates".getBytes());
		txnBatch.write("5,Owen O’Malley".getBytes());
		txnBatch.commit();
		 
		 
		txnBatch.close();
		connection.close();
		}
		 
		 
		
		/*int i = 0;
		//while(txnBatch.remainingTransactions()>0){
			
			txnBatch.beginNextTransaction();
			//String[] values = rowList.get(i).split(",");
			//String v1 = values[0];
			//String v2 = values[1];
			txnBatch.write("hello2,world" .getBytes());
			i++;
		//}
		
		try {
			txnBatch.close();
		} catch (StreamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		connection.close();*/
		
		/*txnBatch = connection.fetchTransactionBatch(10, writer);
		 
		 
		///// Batch 2 - First TXN
		txnBatch.beginNextTransaction();
		txnBatch.write("6,David Schorow".getBytes());
		txnBatch.write("7,Sushant Sowmyan".getBytes());
		txnBatch.commit();
		 
		 
		if(txnBatch.remainingTransactions() > 0) {
		///// Batch 2 - Second TXN
		txnBatch.beginNextTransaction();
		txnBatch.write("8,Ashutosh Chauhan".getBytes());
		txnBatch.write("9,Thejas Nair".getBytes());
		txnBatch.commit();
		 
		 
		txnBatch.close();
		}
		 
		 
		connection.close();
		 
		 */
		//-------   Thread 2  -------//
		 
		/* 
		StreamingConnection connection2 = hiveEP.newConnection(true);
		DelimitedInputWriter writer2 =
		                     new DelimitedInputWriter(nameOfcolumn,",", hiveEP);
		TransactionBatch txnBatch2= connection.fetchTransactionBatch(1000, writer2);
		 
		 
		///// Batch 1 - First TXN
		txnBatch2.beginNextTransaction();
		txnBatch2.write("21,Venkat Ranganathan".getBytes());
		txnBatch2.write("22,Bowen Zhang".getBytes());
		txnBatch2.commit();
		 
		 
		///// Batch 1 - Second TXN
		txnBatch2.beginNextTransaction();
		txnBatch2.write("23,Venkatesh Seetaram".getBytes());
		txnBatch2.write("24,Deepesh Khandelwal".getBytes());
		txnBatch2.commit();
		 
		 
		txnBatch2.close();
		connection.close();
		 
		 
		 
		txnBatch = connection.fetchTransactionBatch(10, writer);
		 
		 
		///// Batch 2 - First TXN
		txnBatch.beginNextTransaction();
		txnBatch.write("26,David Schorow".getBytes());
		txnBatch.write("27,Sushant Sowmyan".getBytes());
		txnBatch.commit();
		 
		 
		txnBatch2.close();
		connection2.close();
		*/
		
		
		
	}

	public static void loaddatatohive(Configuration conf, String driverName)
			throws SQLException, StreamingException, InterruptedException, ClassNotFoundException {
		Connection con = null;
		Statement st = null;

		/*try {
			Class.forName(driverName);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			con = DriverManager.getConnection(
					"jdbc:hive2://localhost:10000/default", "hadoop", "");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			st = con.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String table = "test_for_read";
		String c = "create table if not exists test_hive(date string, value string) row format delimited fields terminated by ','";

		st.execute(c);
		System.out.println("Table created successfully");
		// load data inpath '/authoritative/output.csv' into table test_hive;
		String query = "load data inpath '/authoritative/output.csv' into table test_hive";
		int q = st.executeUpdate(query);
		System.out.println("Boolean q :" + q);*/
		
		
		
		
	///// Stream five records in two transactions /////
		  
			// Assumed HIVE table Schema:
			///String ctable = "create table alerts ( id int , msg string ) partitioned by (continent string, country string) clustered by (id) into 5 buckets stored as orc"; // currently ORC is required for streaming
			//String table = "test_for_read";
			
			  
			//-------   MAIN THREAD  ------- //
			
			 
			/*///// Batch 1 - First TXN
			txnBatch.beginNextTransaction();
			txnBatch.write(rowList[0].getBytes());
			txnBatch.write("2,Welcome to streaming".getBytes());
			txnBatch.commit();
			 
			 
			if(txnBatch.remainingTransactions() > 0) {
			///// Batch 1 - Second TXN
			txnBatch.beginNextTransaction();
			txnBatch.write("3,Roshan Naik".getBytes());
			txnBatch.write("4,Alan Gates".getBytes());
			txnBatch.write("5,Owen O’Malley".getBytes());
			txnBatch.commit();
			 
			 
			txnBatch.close();
			connection.close();
			}
			 */
			 
			/*txnBatch = connection.fetchTransactionBatch(10, writer);
			 
			 
			///// Batch 2 - First TXN
			txnBatch.beginNextTransaction();
			txnBatch.write("6,David Schorow".getBytes());
			txnBatch.write("7,Sushant Sowmyan".getBytes());
			txnBatch.commit();
			 
			 
			if(txnBatch.remainingTransactions() > 0) {
			///// Batch 2 - Second TXN
			txnBatch.beginNextTransaction();
			txnBatch.write("8,Ashutosh Chauhan".getBytes());
			txnBatch.write("9,Thejas Nair" getBytes());
			txnBatch.commit();
			 
			 
			txnBatch.close();
			}
			 
			 
			connection.close();
			 
			 
			//-------   Thread 2  -------//
			 
			 
			StreamingConnection connection2 = hiveEP.newConnection(true);
			DelimitedInputWriter writer2 =
			                     new DelimitedInputWriter(nameOfcolumn,",", hiveEP);
			TransactionBatch txnBatch2= connection.fetchTransactionBatch(10, writer2);
			 
			 
			///// Batch 1 - First TXN
			txnBatch2.beginNextTransaction();
			txnBatch2.write("21,Venkat Ranganathan".getBytes());
			txnBatch2.write("22,Bowen Zhang".getBytes());
			txnBatch2.commit();
			 
			 
			///// Batch 1 - Second TXN
			txnBatch2.beginNextTransaction();
			txnBatch2.write("23,Venkatesh Seetaram".getBytes());
			txnBatch2.write("24,Deepesh Khandelwal".getBytes());
			txnBatch2.commit();
			 
			 
			txnBatch2.close();
			connection.close();
			 
			 
			 
			txnBatch = connection.fetchTransactionBatch(10, writer);
			 
			 
			///// Batch 2 - First TXN
			txnBatch.beginNextTransaction();
			txnBatch.write("26,David Schorow".getBytes());
			txnBatch.write("27,Sushant Sowmyan".getBytes());
			txnBatch.commit();
			 
			 
			txnBatch2.close();
			connection2.close();*/
		
		
		
	
	}
	
	/**
	 * 
	 * @param files the files
	 * @param starttime the start time
	 * @param endtime the end time
	 * @param conf the conf for connecting to hive
	 * @param driverName the driver
	 * @throws IOException throws exception
	 */

	public static void getdata(ArrayList<String> files, Time starttime,
			Time endtime, Configuration conf, String driverName)
			throws IOException {
		byte[] data = null;

		FileSystem fs = FileSystem.get(conf);
		Path po = new Path("/authoritative/output.csv");
		FSDataOutputStream out = fs.create(po);

		for (int k = 0; k < files.size(); k++) {
			Path p = new Path(files.get(k));

			FSDataInputStream file = fs.open(p);

			data = IOUtils.toByteArray(new GZIPInputStream(file));
			file.read(data);

			System.out.println("data from file" + files.get(k));

			ByteBuffer b = ByteBuffer.wrap(data);

			int size = 0, i = 0;

			byte[] record = null;
			ArrayList<String> datapos = new ArrayList<String>();
			rawLog rl = null;

			while (b.hasRemaining()) {

				size = b.getInt();

				String s = String.valueOf(size) + ","
						+ String.valueOf(b.position());
				datapos.add(s);
				int pos = size + b.position();

				b.position(pos);

			}

			if (k == 0) {
				System.out.println("data from file" + files.get(k));

				while (i < datapos.size()) {

					String[] str = datapos.get(i).split(",");

					record = new byte[Integer.parseInt(str[0])];
					b.position(Integer.parseInt(str[1]));

					b.get(record, 0, Integer.parseInt(str[0]));

					ByteBuffer br = ByteBuffer.wrap(record);
					rl = rawLog.getRootAsrawLog(br);
					String[] logtime = rl.created().split(" ");
					String[] cur = logtime[1].split(":");
					System.out.println(logtime[0] + " " + logtime[1]);

					@SuppressWarnings("deprecation")
					Time curtime = new Time(Integer.parseInt(cur[0]),
							Integer.parseInt(cur[1]), Integer.parseInt(cur[2]));
					String[] time = logtime[1].split(":");
					if (curtime.after(starttime) || curtime.equals(starttime)) {
						
						String csvLine = logtime[0] + "," + logtime[1] + "\n";
						//rowList.add(csvLine);
						//out.writeBytes(logtime[0] + "," + logtime[1] + "\n");
						System.out.println("This is the record: " + " "
								+ rl.created());
					}

					i++;

				}
			} else if (k == files.size() - 1) {
				System.out.println("data from file" + files.get(k));

				while (i < datapos.size()) {

					String[] str = datapos.get(i).split(",");

					record = new byte[Integer.parseInt(str[0])];
					b.position(Integer.parseInt(str[1]));

					b.get(record, 0, Integer.parseInt(str[0]));

					ByteBuffer br = ByteBuffer.wrap(record);
					rl = rawLog.getRootAsrawLog(br);
					String[] logtime = rl.created().split(" ");
					String[] cur = logtime[1].split(":");
					System.out.println(logtime[0] + " " + logtime[1]);

					@SuppressWarnings("deprecation")
					Time curtime = new Time(Integer.parseInt(cur[0]),
							Integer.parseInt(cur[1]), Integer.parseInt(cur[2]));
					System.out.println(curtime);
					if (curtime.before(endtime) || curtime.equals(endtime)) {
						out.writeBytes(logtime[0] + "," + logtime[1] + "\n");
						System.out.println("This is the record: " + " "
								+ rl.created());
					}
					i++;

				}

			} else {
				while (i < datapos.size()) {

					String[] str = datapos.get(i).split(",");

					record = new byte[Integer.parseInt(str[0])];
					b.position(Integer.parseInt(str[1]));

					b.get(record, 0, Integer.parseInt(str[0]));

					ByteBuffer br = ByteBuffer.wrap(record);
					rl = rawLog.getRootAsrawLog(br);
					String[] logtime = rl.created().split(" ");
					String[] time = logtime[1].split(":");
					//rowList.add(csvLine);
					//out.writeBytes(logtime[0] + "," + logtime[1] + "\n");
					String csvLine = logtime[0] + "," + logtime[1] + "\n" ;
					//rowList.add(csvLine);
					System.out.println("This is the record: " + " "
							+ rl.created());

					i++;
				}
			}
		}
		out.close();
		try {
			loaddatatohive(conf, driverName);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StreamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param files the list of files
	 * @param folders the list of folders
	 * @param patterns the pattern of files
	 * @param fs1
	 */
	public static void getfiles(ArrayList<String> files,
			ArrayList<String> folders, ArrayList<String> patterns,
			FileSystem fs1) {
		ArrayList<String> file_not_found = new ArrayList<String>();
		for (String f : folders) {

			Path folder = new Path(f);

			FileStatus[] status;
			try {
				status = fs1.listStatus(folder);
				for (int p = 0; p < status.length; p++) {
					String file = status[p].getPath().getName();
					// System.out.println("file is: "+file);
					for (int s = 0; s < patterns.size(); s++) {
						System.out.println("pattern matching:" + file);
						if (file.matches(patterns.get(s))) {
							System.out.println("String matches: " + file
									+ "with " + patterns.get(s));
							files.add(f + "/" + file);
							break;
						}
					}

				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				file_not_found.add(folder.toString());
				System.out.println("File " + folder + "not found");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				System.out.println("These are the folders that were not found");
				for (int w = 0; w < file_not_found.size(); w++) {
					System.out.println(file_not_found.get(w));
				}
			}

		}
	}
	
	
	/**
	 * 
	 * @param patterns the pattern of the files
	 * @param startdate the start date
	 * @param enddate the enddate
	 * @param revision_id the revision id 
	 * @param producer_id the producer id
	 * @param sequence_num the sequence number
	 * @param advertiser_id the advertiser id
	 * @param prison_id the prison id
	 */

	public static void getpatterns(ArrayList<String> patterns, Date startdate,
			Date enddate, String revision_id, ArrayList<Integer> producer_id,
			String sequence_num, ArrayList<Integer> advertiser_id,
			ArrayList<Integer> prison_id) {
		for (int h = 0; h < advertiser_id.size(); h++) {
			String shard_id = String.valueOf((advertiser_id.get(h)) % 21);

			System.out.println("Shard_id :" + shard_id);
			StringBuilder patternname = new StringBuilder();

			// TODO : Right now we are returning every revision which is less
			// than the specified revision_id. A filtering needs to be done so
			// that it returns files with the highest revision number.

			patternname.append("shard" + shard_id + "_\\d{8}_rev\\d{1,2}");
			if (prison_id.size() == 0 && producer_id.size() == 0) {
				System.out.println("Inside first if");
				StringBuilder pattern = new StringBuilder(
						patternname.toString());

				pattern.append("_prison\\d{2}_batcher\\d{2}_seq\\d{1,2}.fb.gz");
				System.out.println("Pattern: " + pattern.toString());
				patterns.add(pattern.toString());
			} else if (prison_id.size() == 0 && producer_id.size() != 0) {
				System.out.println("Inside second if");
				for (int i2 = 0; i2 < producer_id.size(); i2++) {

					StringBuilder pattern = new StringBuilder(
							patternname.toString());
					String producer = String
							.format("%02d", producer_id.get(i2));

					pattern.append("_prison\\d{2}_batcher" + producer
							+ "_seq\\d{1,2}.fb.gz");
					System.out.println("Pattern: " + pattern.toString());
					patterns.add(pattern.toString());

				}
			} else if (prison_id.size() != 0 && producer_id.size() == 0) {
				System.out.println("Inside third if");
				for (int i2 = 0; i2 < prison_id.size(); i2++) {

					StringBuilder pattern = new StringBuilder(
							patternname.toString());
					String prison = String.format("%02d", prison_id.get(i2));

					pattern.append("_prison" + prison
							+ "_batcher\\d{1,2}_seq\\d{1,2}.fb.gz");
					System.out.println("Pattern: " + pattern.toString());
					patterns.add(pattern.toString());

				}
			} else {
				System.out.println("Inside last if");
				for (int i2 = 0; i2 < prison_id.size(); i2++) {

					StringBuilder pattern = new StringBuilder(
							patternname.toString());
					String prison = String.format("%02d", prison_id.get(i2));
					for (int i3 = 0; i3 < producer_id.size(); i3++) {
						StringBuilder f_pattern = new StringBuilder(
								pattern.toString());
						String producer = String.format("%02d",
								producer_id.get(i3));

						f_pattern.append("_prison" + prison + "_batcher"
								+ producer + "_seq\\d{1,2}.fb.gz");
						System.out.println("Pattern: " + f_pattern.toString());
						patterns.add(f_pattern.toString());
					}
				}
			}
			patternname.setLength(0);

		}
		System.out.println(patterns.size());
	}

	
	/**
	 * 
	 * @param folders the folder name
	 * @param startdate the start date
	 * @param enddate the end date
	 */
	public static void getfoldernames(ArrayList<String> folders,
			Date startdate, Date enddate) {

		List<Date> dates = new ArrayList<Date>();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(startdate);

		while ((calendar.getTime().before(enddate))
				|| calendar.getTime().equals(enddate)) {
			Date resultdate = calendar.getTime();
			dates.add(resultdate);
			@SuppressWarnings("deprecation")
			String foldername = "/authoritative/" + resultdate.getYear() + "/"
					+ String.format("%02d", (resultdate.getMonth() + 1)) + "/"
					+ String.format("%02d", resultdate.getDate());
			System.out.println(foldername);
			calendar.add(Calendar.DATE, 1);
			folders.add(foldername);
		}

		/*
		 * for(int y=Integer.parseInt(from_year);
		 * y<=Integer.parseInt(from_year)+year_range ; y++) {
		 * 
		 * int year = y; Calendar c= Calendar.getInstance(); c.set(c.YEAR,
		 * year); if(year==Integer.parseInt(from_year) && year_range==0) {
		 * 
		 * for(int i =Integer.parseInt(from_month);
		 * i<=Integer.parseInt(from_month)+month_range; i++) {
		 * 
		 * int m=i; c.set(c.MONTH, m-1); int total_days=
		 * c.getActualMaximum(c.DAY_OF_MONTH);
		 * System.out.println("total_days :"+total_days+ " For month: "+ m);
		 * String month = String.format("%02d", m); if(m==
		 * Integer.parseInt(from_month) && month_range>0) { for ( int
		 * j=Integer.parseInt(from_day); j<=total_days;j++) {
		 * 
		 * String day = String.format("%02d", j); StringBuilder folderpath = new
		 * StringBuilder();
		 * folderpath.append("/authoritative/"+year+"/"+month+"/"+day);
		 * System.out.println("File path 5555 here: "+folderpath.toString());
		 * folders.add(folderpath.toString());
		 * 
		 * } } else if (m== Integer.parseInt(from_month) && month_range==0) {
		 * for(int j =0; j<=days_range; j++) { String day =
		 * String.format("%02d",Integer.parseInt(from_day)+j); StringBuilder
		 * folderpath = new StringBuilder();
		 * folderpath.append("/authoritative/"+year+"/"+month+"/"+day);
		 * System.out.println("File path 5555 here: "+folderpath.toString());
		 * folders.add(folderpath.toString());
		 * 
		 * } } if (m == Integer.parseInt(from_month)+month_range &&
		 * month_range>0) { for ( int j1 = 1;
		 * j1<=Integer.parseInt(from_day)+days_range; j1++) { String day =
		 * String.format("%02d", j1); StringBuilder folderpath = new
		 * StringBuilder();
		 * folderpath.append("/authoritative/"+year+"/"+month+"/"+day);
		 * System.out.println("File path 5555 here: "+folderpath.toString());
		 * folders.add(folderpath.toString()); } } if( m
		 * >Integer.parseInt(from_month) && m<
		 * Integer.parseInt(from_month)+month_range) { for ( int j1 = 1;
		 * j1<=total_days; j1++) { String day = String.format("%02d", j1);
		 * StringBuilder folderpath = new StringBuilder();
		 * folderpath.append("/authoritative/"+year+"/"+month+"/"+day);
		 * System.out.println("File path 5555 here: "+folderpath.toString());
		 * folders.add(folderpath.toString()); } }
		 * 
		 * }
		 * 
		 * } else if (year == Integer.parseInt(from_year) && year_range>0) {
		 * for( int u= Integer.parseInt(from_month) ; u<=12; u++) { int m =u;
		 * c.set(c.MONTH, m-1); int total_days=
		 * c.getActualMaximum(c.DAY_OF_MONTH); String month =
		 * String.format("%02d", u); if( m == Integer.parseInt(from_month)) {
		 * for ( int j=Integer.parseInt(from_day); j<=total_days;j++) {
		 * 
		 * String day = String.format("%02d", j); StringBuilder folderpath = new
		 * StringBuilder();
		 * folderpath.append("/authoritative/"+year+"/"+month+"/"+day);
		 * System.out.println("File path 5555 here: "+folderpath.toString());
		 * folders.add(folderpath.toString());
		 * 
		 * } } else{ for ( int j1 = 1; j1<=total_days; j1++) { String day =
		 * String.format("%02d", j1); StringBuilder folderpath = new
		 * StringBuilder();
		 * folderpath.append("/authoritative/"+year+"/"+month+"/"+day);
		 * System.out.println("File path 5555 here: "+folderpath.toString());
		 * folders.add(folderpath.toString()); } } } } else if( year ==
		 * Integer.parseInt(from_year)+year_range && year_range>0) { for( int
		 * u=1; u<= Integer.parseInt(from_month)+month_range ; u++) { int m= u;
		 * c.set(c.MONTH, m-1); int total_days=
		 * c.getActualMaximum(c.DAY_OF_MONTH); String month =
		 * String.format("%02d", u); if(m==
		 * Integer.parseInt(from_month)+month_range && month_range>=0) { for (
		 * int j1 = 1; j1<=Integer.parseInt(from_day)+days_range; j1++) { String
		 * day = String.format("%02d", j1); StringBuilder folderpath = new
		 * StringBuilder();
		 * folderpath.append("/authoritative/"+year+"/"+month+"/"+day);
		 * System.out.println("File path 5555 here: "+folderpath.toString());
		 * folders.add(folderpath.toString()); } } else { for ( int j1 = 1;
		 * j1<=total_days; j1++) { String day = String.format("%02d", j1);
		 * StringBuilder folderpath = new StringBuilder();
		 * folderpath.append("/authoritative/"+year+"/"+month+"/"+day);
		 * System.out.println("File path 5555 here: "+folderpath.toString());
		 * folders.add(folderpath.toString()); } } } } else{ for( int u=1 ;
		 * u<=12; u++) { int m=u; c.set(c.MONTH, m-1); int total_days=
		 * c.getActualMaximum(c.DAY_OF_MONTH); String month =
		 * String.format("%02d", u); for( int d=1; d<=total_days ; d++) { String
		 * day = String.format("%02d", d); StringBuilder folderpath = new
		 * StringBuilder();
		 * folderpath.append("/authoritative/"+year+"/"+month+"/"+day);
		 * System.out.println("File path 5555 here: "+folderpath.toString());
		 * folders.add(folderpath.toString()); } } } }
		 */
	}

}
