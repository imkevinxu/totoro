package com.totoro.incardisplay;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.net.telnet.EchoOptionHandler;
import org.apache.commons.net.telnet.InvalidTelnetOptionException;
import org.apache.commons.net.telnet.SuppressGAOptionHandler;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TelnetNotificationHandler;
import org.apache.commons.net.telnet.TerminalTypeOptionHandler;

import android.os.AsyncTask;
import android.util.Log;

/***
 * Commands:
 * <Req><Subscribe url="VehicleSpeed" ival="1000" notification="onChange"/></Req>
 * <Req><Unsubscribe url="VehicleSpeed"/></Req>
 * <Req><Dir urlPattern="*"/></Req>
 */ 

public class TelnetClientOutput extends AsyncTask implements Runnable, TelnetNotificationHandler 
{
    static TelnetClient tc = null;
	public static BlockingQueue<Double> queue = new ArrayBlockingQueue<Double>(100);

    // EC2 IP address instance is 184.169.154.101 port 28501
    public static final String IP_ADDR = "184.169.154.101";
    public static final int PORT = 28501;
    
   /* public static void main(String[] args) throws Exception {
    	System.out.println("LALALALAAL");
    	new TelnetClientOutput();
    }*/
    
    public TelnetClientOutput(){}
    

    public void executeNet() throws Exception
    {
        //FileOutputStream fout = null;

        String remoteip = IP_ADDR;

        int remoteport = PORT;

       /* try
        {
            fout = new FileOutputStream ("spy.log", true);
        }
        catch (IOException e)
        {
            System.err.println(
                "Exception while opening the spy file: "
                + e.getMessage());
        }*/

        tc = new TelnetClient();

        TerminalTypeOptionHandler ttopt = new TerminalTypeOptionHandler("VT100", false, false, true, false);
        EchoOptionHandler echoopt = new EchoOptionHandler(true, false, true, false);
        SuppressGAOptionHandler gaopt = new SuppressGAOptionHandler(true, true, true, true);

        try
        {
            tc.addOptionHandler(ttopt);
            tc.addOptionHandler(echoopt);
            tc.addOptionHandler(gaopt);
        }
        catch (InvalidTelnetOptionException e)
        {
            System.err.println("Error registering option handlers: " + e.getMessage());
        }
        
        boolean first_iteration = true;

        //while (true)
        //{
            boolean end_loop = false;
            try
            {
            	System.out.println("TRY");
                tc.connect(remoteip, remoteport);
                System.out.println("PASS");

                Thread reader = new Thread (new TelnetClientOutput());
                tc.registerNotifHandler(new TelnetClientOutput());
                /*System.out.println("TelnetClientOutput");
                System.out.println("Type AYT to send an AYT telnet command");
                System.out.println("Type OPT to print a report of status of options (0-24)");
                System.out.println("Type REGISTER to register a new SimpleOptionHandler");
                System.out.println("Type UNREGISTER to unregister an OptionHandler");
                System.out.println("Type SPY to register the spy (connect to port 3333 to spy)");
                System.out.println("Type UNSPY to stop spying the connection"); */

                reader.start();
                OutputStream outstr = tc.getOutputStream();

                byte[] buff = new byte[1024];
                int ret_read = 0;
                
                // hackish method to initially subscribe to car speed
                if (first_iteration) {
                    String comm = "<Req><Subscribe url='VehicleSpeed' ival='1000' notification='onChange'/></Req>";
                    buff = comm.getBytes();
                	
                	outstr.write(buff, 0 , 78);
                    outstr.flush();
                	
                	first_iteration = false;
                }
               /* try
                {
                    tc.disconnect();
                }
                catch (IOException e)
                {
                          System.err.println("Exception while connecting:" + e.getMessage());
                }*/
            }
            catch (IOException e)
            {
                    System.err.println("Exception while connecting:" + e.getMessage());
                    System.exit(1);
            }
        //}
    }
    
    
    /***
     * Callback method called when TelnetClient receives an option
     * negotiation command.
     ***/
    @Override
    public void receivedNegotiation(int negotiation_code, int option_code)
    {
        String command = null;
        if(negotiation_code == TelnetNotificationHandler.RECEIVED_DO)
        {
            command = "DO";
        }
        else if(negotiation_code == TelnetNotificationHandler.RECEIVED_DONT)
        {
            command = "DONT";
        }
        else if(negotiation_code == TelnetNotificationHandler.RECEIVED_WILL)
        {
            command = "WILL";
        }
        else if(negotiation_code == TelnetNotificationHandler.RECEIVED_WONT)
        {
            command = "WONT";
        }
        System.out.println("Received " + command + " for option code " + option_code);
   }

    
    /***
     * Reader thread currently echoes output from simulator
     ***/
    @Override
    public void run()
    {
    	Log.i("RUN", "before instr");
        InputStream instr = tc.getInputStream();
    	Log.i("RUN", "after instr");

        try
        {

            byte[] buff = new byte[1024];
            int ret_read = 0;

            do
            {
                ret_read = instr.read(buff);
                if(ret_read > 0)
                {
                	String output = new String(buff, 0, ret_read);
                	// Parse out speed 
                	System.out.println("Output: " + output);
                	int val_ind = output.indexOf("val=");
                	if (val_ind != -1) {
                		int num_ind = val_ind + 5;
                		if (num_ind < output.length()) {
	                		String new_str = output.substring(num_ind);
	                		int index_apos = new_str.indexOf('"');
	                		if (index_apos >= 0) { 
		                		String val = new_str.substring(0, index_apos);
		                		
		                		try {
		                			double doub_val = Double.parseDouble(val);
			                		Double d = Double.valueOf(doub_val);
			                		System.out.println(d);
			                		
									queue.put(d);
									System.out.println("val: " + d + " " + queue.size());
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									// e.printStackTrace();
								} catch (IllegalArgumentException e) {
									// e.printStackTrace();
								}
	                		}
                		}
                	}
                	
                	Log.i("RUN", "after parse, ret_read= " + ret_read);                    
                }
            }
            while (ret_read >= 0);
        }
        catch (IOException e)
        {
            System.err.println("Exception while reading socket:" + e.getMessage());
        }

        try
        {
            tc.disconnect();
        }
        catch (IOException e)
        {
            System.err.println("Exception while closing telnet:" + e.getMessage());
        }
    }

	@Override
	protected Object doInBackground(Object... arg0) {
		// TODO Auto-generated method stub
		try {
			executeNet();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
