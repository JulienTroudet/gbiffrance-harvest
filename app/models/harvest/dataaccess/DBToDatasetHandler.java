package models.harvest.dataaccess;


public class DBToDatasetHandler 
{
  public DBToDatasetHandler() {}
  
  /*public void listDatasets(ArrayList<Dataset> datasetsList) throws SQLException
  {
	  
	  String query = "select dataset_id, name, url from dataset where type='"+type+"'";
	  Statement stmt = conn.createStatement();
	  ResultSet rs = stmt.executeQuery(query);
	  while (rs.next())
	  {
		  ArrayList<String> values = new ArrayList<String>();
		  values.add(rs.getString("name"));
		  values.add(rs.getString("url"));
		  list.put(rs.getInt("dataset_id"), values);
	  }
	  LOG.info("providers list imported");
  }*/
}
