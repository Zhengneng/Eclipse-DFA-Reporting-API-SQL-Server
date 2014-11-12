/*
 * Copyright (c) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.api.services.samples.dfareporting.cmdline;

//import com.sun.corba.se.pept.transport.Connection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;


public class Sqlconnection {
  public static void connect(String args)
  {
      try
      {
          Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

//          String userName = "sa";
//          String password = "password";
          String url = "jdbc:sqlserver://chisqlintel:1433;databaseName=wag;integratedSecurity=true";
          Connection con =   DriverManager.getConnection(url);
          if(con != null)
              System.out.println("Connection is Successfully!");
          Statement s1 =  con.createStatement();
          String str = "insert into dbo.API_Walgreens_v2 values "+ args;
//          String str = "SELECT TOP 1 * FROM dbo.Appended_rows_import";
          s1.executeUpdate(str);
          

      } catch (Exception e)
      {
          e.printStackTrace();
      }
}
}
