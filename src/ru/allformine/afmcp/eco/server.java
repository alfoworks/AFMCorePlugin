package ru.allformine.afmcp.eco;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import ru.allformine.afmcp.vars;

public class server
{
  public static String post(String data)
    throws Exception
  {
    URL obj = new URL(vars.ecoServerUrl + "?check=z5FXxPQqFJMKk5eGyTR2zhms6iAGwp&" + data);
    HttpsURLConnection con = (HttpsURLConnection)obj.openConnection();
    con.setRequestMethod("POST");
    con.setRequestProperty("User-Agent", "Mozilla/5.0");
    con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
    String urlParameters = "";
    con.setDoOutput(true);
    DataOutputStream wr = new DataOutputStream(con.getOutputStream());
    wr.writeBytes(urlParameters);
    wr.flush();
    wr.close();
    int responseCode = con.getResponseCode();
    
    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
    
    StringBuffer response = new StringBuffer();
    String inputLine; while ((inputLine = in.readLine()) != null) {
      response.append(inputLine);
    }
    in.close();
    
    return response.toString();
  }
  
  public static String get(String nick) throws Exception {
    return post("act=get&nick=" + nick);
  }
  
  public static String rem(String nick, String moneyToRemove) throws Exception {
    return post("act=rem&nick=" + nick + "&dif=" + moneyToRemove);
  }
  
  public static String set(String nick, String moneyToSet) throws Exception {
    return post("act=set&nick=" + nick + "&dif=" + moneyToSet);
  }
  
  public static String add(String nick, String moneyToAdd) throws Exception {
    return post("act=add&nick=" + nick + "&dif=" + moneyToAdd);
  }
}