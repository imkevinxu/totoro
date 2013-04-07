import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.net.telnet.EchoOptionHandler;
import org.apache.commons.net.telnet.InvalidTelnetOptionException;
import org.apache.commons.net.telnet.SimpleOptionHandler;
import org.apache.commons.net.telnet.SuppressGAOptionHandler;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TelnetNotificationHandler;
import org.apache.commons.net.telnet.TerminalTypeOptionHandler;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/***
 * Commands:
 * <Req><Subscribe url="VehicleSpeed" ival="1000" notification="onChange"/></Req>
 * <Req><Unsubscribe url="VehicleSpeed"/></Req>
 * <Req><Dir urlPattern="*"/></Req>
 */

public class TelnetClientExample implements Runnable, TelnetNotificationHandler
{
	SAXReader reader = new SAXReader();
    static TelnetClient tc = null;
    
    // EC2 IP address instance is 184.169.154.101 port 28501
    public static final String IP_ADDR = "184.169.154.101";
    public static final int PORT = 28501;
    
    public static void main(String[] args) throws Exception
    {
        FileOutputStream fout = null;
        
        String remoteip = IP_ADDR;

        int remoteport = PORT;

        try
        {
            fout = new FileOutputStream ("spy.log", true);
        }
        catch (IOException e)
        {
            System.err.println(
                "Exception while opening the spy file: "
                + e.getMessage());
        }
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

        while (true)
        {
            boolean end_loop = false;
            try
            {
                tc.connect(remoteip, remoteport);

                Thread reader = new Thread (new TelnetClientExample());
                tc.registerNotifHandler(new TelnetClientExample());
                /*System.out.println("TelnetClientExample");
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

                do
                {
                    try
                    {
                        ret_read = System.in.read(buff);
                        if(ret_read > 0)
                        {
                            if((new String(buff, 0, ret_read)).startsWith("AYT"))
                            {
                                try
                                {
                                    System.out.println("Sending AYT");

                                    System.out.println("AYT response:" + tc.sendAYT(5000));
                                }
                                catch (IOException e)
                                {
                                    System.err.println("Exception waiting AYT response: " + e.getMessage());
                                }
                            }
                            else if((new String(buff, 0, ret_read)).startsWith("OPT"))
                            {
                                 System.out.println("Status of options:");
                                 for(int ii=0; ii<25; ii++) {
                                     System.out.println("Local Option " + ii + ":" + tc.getLocalOptionState(ii) + " Remote Option " + ii + ":" + tc.getRemoteOptionState(ii));
                                 }
                            }
                            else if((new String(buff, 0, ret_read)).startsWith("REGISTER"))
                            {
                                StringTokenizer st = new StringTokenizer(new String(buff));
                                try
                                {
                                    st.nextToken();
                                    int opcode = Integer.parseInt(st.nextToken());
                                    boolean initlocal = Boolean.parseBoolean(st.nextToken());
                                    boolean initremote = Boolean.parseBoolean(st.nextToken());
                                    boolean acceptlocal = Boolean.parseBoolean(st.nextToken());
                                    boolean acceptremote = Boolean.parseBoolean(st.nextToken());
                                    SimpleOptionHandler opthand = new SimpleOptionHandler(opcode, initlocal, initremote,
                                                                    acceptlocal, acceptremote);
                                    tc.addOptionHandler(opthand);
                                }
                                catch (Exception e)
                                {
                                    if(e instanceof InvalidTelnetOptionException)
                                    {
                                        System.err.println("Error registering option: " + e.getMessage());
                                    }
                                    else
                                    {
                                        System.err.println("Invalid REGISTER command.");
                                        System.err.println("Use REGISTER optcode initlocal initremote acceptlocal acceptremote");
                                        System.err.println("(optcode is an integer.)");
                                        System.err.println("(initlocal, initremote, acceptlocal, acceptremote are boolean)");
                                    }
                                }
                            }
                            else if((new String(buff, 0, ret_read)).startsWith("UNREGISTER"))
                            {
                                StringTokenizer st = new StringTokenizer(new String(buff));
                                try
                                {
                                    st.nextToken();
                                    int opcode = (new Integer(st.nextToken())).intValue();
                                    tc.deleteOptionHandler(opcode);
                                }
                                catch (Exception e)
                                {
                                    if(e instanceof InvalidTelnetOptionException)
                                    {
                                        System.err.println("Error unregistering option: " + e.getMessage());
                                    }
                                    else
                                    {
                                        System.err.println("Invalid UNREGISTER command.");
                                        System.err.println("Use UNREGISTER optcode");
                                        System.err.println("(optcode is an integer)");
                                    }
                                }
                            }
                            else if((new String(buff, 0, ret_read)).startsWith("SPY"))
                            {
                                tc.registerSpyStream(fout);
                            }
                            else if((new String(buff, 0, ret_read)).startsWith("UNSPY"))
                            {
                                tc.stopSpyStream();
                            }
                            else
                            {
                                try
                                {
                                        outstr.write(buff, 0 , ret_read);
                                        outstr.flush();
                                }
                                catch (IOException e)
                                {
                                        end_loop = true;
                                }
                            }
                        }
                    }
                    catch (IOException e)
                    {
                        System.err.println("Exception while reading keyboard:" + e.getMessage());
                        end_loop = true;
                    }
                }
                while((ret_read > 0) && (end_loop == false));

                try
                {
                    tc.disconnect();
                }
                catch (IOException e)
                {
                          System.err.println("Exception while connecting:" + e.getMessage());
                }
            }
            catch (IOException e)
            {
                    System.err.println("Exception while connecting:" + e.getMessage());
                    System.exit(1);
            }
        }
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
        InputStream instr = tc.getInputStream();
        int count = 0;
        String output = "";
        try
        {
            byte[] buff = new byte[1024];
            int ret_read = 0;

            do
            {
                
                ret_read = instr.read(buff);
                if (ret_read > 0) {
                	output += new String(buff, 0, ret_read);
                	int nl_ind = output.indexOf('\n');
                	if (nl_ind != -1) {
                		String line = output.substring(0, nl_ind);
						//System.out.println(count + " " + line);
						
						int val_ind = line.indexOf("val=");
						int name_ind = line.indexOf("name=");
	                	if (val_ind != -1 && name_ind != -1) {
	                		int num_ind = val_ind + 5;
	                		int name_val_ind = name_ind + 6;
	                		
	                		String new_str = line.substring(num_ind);
	                		int index_apos = new_str.indexOf('"');
	                		String val = new_str.substring(0, index_apos);
	                		double doub_val = Double.parseDouble(val);
	                		
	                		String name_new_str = line.substring(name_val_ind);
	                		int name_index_apos = name_new_str.indexOf('"');
	                		String name_val = name_new_str.substring(0, name_index_apos);
	                		
	                		System.out.println(doub_val);
	                		System.out.println(name_val);
	                	}
						
                		count++;
                		output = output.substring(nl_ind+1);
                	}
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
}