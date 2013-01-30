package org.refined.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
/**
 * @author LordBen
 */
public class Looting {

	public final static Hashtable<Integer, Integer> Pricetable = new Hashtable<Integer, Integer>();//Id, price
	
	public static int getPrice(final int id, final int stackSize) {

    	Integer priceInTable = Pricetable.get(id);
    	if (priceInTable == null) {//If it's not in our table
	        try {
	            String price;
	            final URL url = new URL("http://open.tip.it/json/ge_single_item?item=" + id);
	            final URLConnection con = url.openConnection();
	            final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
	            String line;
	            while ((line = in.readLine()) != null) {
	                if (line.contains("mark_price")) {
	                    price = line.substring(line.indexOf("mark_price") + 13, line.indexOf(",\"daily_gp") - 1);
	                    price = price.replace(",", "");
	                    Pricetable.put(id, price != null ? Integer.parseInt(price) : -1);//If it's untrableable add value as -1
	                    return Integer.parseInt(price) * stackSize;
	                }
	            }
	        } catch (final Exception ignored) {
	            return -1;
	        }
	        return -1;
    	} else {
    		return Pricetable.get(id) * stackSize;//Gets price from our Hashtable * by amount
    	}
    }
}

