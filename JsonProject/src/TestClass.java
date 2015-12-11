import java.sql.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class TestClass
{
    public static void main(String[] args) 
    {
    	try{
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String connectionUrl = "jdbc:sqlserver://localhost:1433;" +
                    "databaseName=Kalsi;user=sa;password=system123#;";
            //code added by me
                Connection con = DriverManager.getConnection(connectionUrl);
            System.out.println("connected");
            
            Statement stmt = null;
            ResultSet rs = null;
            // SQL query command
            String SQL = "SELECT * FROM ng_test order by srno asc";
            stmt = con.createStatement();
            rs = stmt.executeQuery(SQL);
            String lastTab="";
            JSONObject obj = new JSONObject();
            while (rs.next()) {
            	System.out.println(obj);
            	if(obj.containsKey(rs.getString(3))){
            		 JSONObject obj1 = (JSONObject)obj.get(rs.getString(3));
            		 obj1=obj1==null?new JSONObject():obj1;
            		 if(obj1.containsKey(rs.getString(2))){
            			 JSONObject obj2 = (JSONObject)obj1.get(rs.getString(2));
            			 obj2=obj2==null?new JSONObject():obj2;
            			 obj2.put("label", rs.getString(1));
            			 obj2.put("condition", rs.getString(4));
            			 JSONArray ja1=(JSONArray)obj2.get("GTE");
            			 ja1.add(rs.getString(5));
            			 obj2.put("GTE", ja1);
            			 JSONArray ja2=(JSONArray)obj2.get("LTE");
            			 ja2.add(rs.getString(6));
            			 obj2.put("LTE", ja2);
            			 JSONArray ja3=(JSONArray)obj2.get("EQUAL");
            			 ja3.add(rs.getString(7));
            			 obj2.put("EQUAL", ja3);
            			 JSONArray ja4=(JSONArray)obj2.get("SCORE");
            			 ja4.add(rs.getString(8));
            			 obj2.put("SCORE", ja4);
            			 JSONArray ja5=(JSONArray)obj2.get("MAXSCORE");
            			 ja5.add(rs.getString(9));
            			 obj2.put("MAXSCORE", ja5);
            			 JSONArray ja6=(JSONArray)obj2.get("DDV");
            			 ja6.add(rs.getString(10));
            			 obj2.put("DDV", ja6);
            			 obj1.put(rs.getString(2), obj2);
            		 }
            		 obj.put(rs.getString(3), obj1);
            	}
            	else{
            		 JSONObject obj1=new JSONObject();
            		 JSONObject obj2=new JSONObject();
        			 obj2.put("label", rs.getString(1));
        			 obj2.put("condition", rs.getString(4));
        			 JSONArray ja1=new JSONArray();
        			 ja1.add(rs.getString(5));
        			 obj2.put("GTE", ja1);
        			 JSONArray ja2=new JSONArray();
        			 ja2.add(rs.getString(6));
        			 obj2.put("LTE", ja2);
        			 JSONArray ja3=new JSONArray();
        			 ja3.add(rs.getString(7));
        			 obj2.put("EQUAL", ja3);
        			 JSONArray ja4=new JSONArray();
        			 ja4.add(rs.getString(8));
        			 obj2.put("SCORE", ja4);
        			 JSONArray ja5=new JSONArray();
        			 ja5.add(rs.getString(9));
        			 obj2.put("MAXSCORE", ja5);
        			 JSONArray ja6=new JSONArray();
        			 ja6.add(rs.getString(10));
        			 obj2.put("DDV", ja6);
        			 obj1.put(rs.getString(2), obj2);
        			 obj.put(rs.getString(3), obj1);
            	}
            }
            System.out.println(obj); 
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}