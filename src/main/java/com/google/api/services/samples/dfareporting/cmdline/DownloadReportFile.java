/*
 * Copyright (c) 2012 Google Inc.
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

import com.google.api.client.http.HttpResponse;
import com.google.api.client.util.Charsets;
import com.google.api.services.dfareporting.Dfareporting;
import com.google.api.services.dfareporting.model.File;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * This example downloads the contents of a report file.
 * 
 * @author jdilallo@google.com (Joseph DiLallo)
 */
public class DownloadReportFile {

  /**
   * Fetches the contents of a report file.
   * 
   * @param reporting Dfareporting service object on which to run the requests.
   * @param reportFile The completed report file to download.
   * @throws Exception
   */
  @SuppressWarnings("null")
  public static String run(Dfareporting reporting, File reportFile) throws Exception {
    System.out.println("=================================================================");
    System.out.printf("Retrieving and printing a report file for report with ID %s%n",
        reportFile.getReportId());
    System.out.printf("The ID number of this report file is %s%n", reportFile.getId());
    System.out.println("=================================================================");

    HttpResponse fileContents =
        reporting.files().get(reportFile.getReportId(), reportFile.getId()).executeMedia();

    try {

/*      URL website =
          new URL(
              "https://ddm.google.com/analytics/dfa/?defaultDs=1098785%3A5371&hl=en&zx=9lxafia35zjl#query-tool/templateList/1069117%3A5589/%3F_u.date00%3D20141022%26_u.date01%3D20141029%26_.templateId%3D15923025%26_.templateType%3DSTANDARD%26_.tab%3Dtb%26_.list%3DTEMPLATES%26templateListButtonPanel.show%3DMINE%26templateListSidebarPanel.listType%3DFILES%26templateListSidebarPanel.listFilter%3DMINE%26templateDetailsSidebarPanel.tab%3DFILES/");
      ReadableByteChannel rbc = Channels.newChannel(website.openStream());
      FileOutputStream fos = new FileOutputStream("information.html");
      fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);*/
      
      BufferedReader reader =
          new BufferedReader(new InputStreamReader(fileContents.getContent(), Charsets.UTF_8));
      String line;
      String str = "";
      int i = 0;
      while ((line = reader.readLine()) != null) {
        String[] parts = line.split(",");
        if(parts[0].equals("Walgreens"))
          while(i<100){
        str += "('"+parts[0]+"',"+parts[1]+",'"+parts[2]+"','"+parts[3]+"',"+parts[4]+",'"+parts[5]+"',"+parts[6]+",'"+parts[7]+"','"+parts[8]+"','"+parts[9]+"','"+parts[10]+"','"+parts[11]+"','"+parts[12]+"','"+parts[13]+"','"+parts[14]+"','"+parts[15]+"','"+parts[16]+"','"+parts[17]+"',"+parts[18]+","+parts[19]+","+parts[20]+","+parts[21]+"),";
        i++;
        if(parts[0].equals("Advertiser"))
        System.out.println(line);}
      }        
      System.out.println(str);
      str = str.substring(0, str.length()-1);
      System.out.println(str);
      return str;

    } finally {
      fileContents.disconnect();

    }
  }
}
