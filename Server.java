 /*
 * @tile             Distributed Password Breaker(Server side)
 * @author           Mahedi Hasan
 * @description      developed for project submission in ICT-6544: Distributed Systems course in BUET
 * @date             05/02/2016
 */
 
import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;


public class Server {

    static final int portNumber = 8003;
    static int range = -1;
    static String hashPassword;

    public static void main(String args[]) throws IOException {
 
        System.out.println("\n**** Distributed Password Breaker ****");
        System.out.println("**************** Server **************");
        hashPassword = generateARandomPasswordWithHash();
        System.out.println("Hash password: " + hashPassword);

        ServerSocket ss = new ServerSocket(portNumber);
        System.out.println("\nWaiting for client response....");
        int id = 0;
        
        while (true) {
            Socket clientSocket = ss.accept();
            ClientServiceThread clientService = new ClientServiceThread(clientSocket, ++id);
            clientService.start();
            
            System.out.println("\nClient_" + id + " connection established\n");
        }
    }

    

    /**
     * @return random password with md5 hash combining it with date 
     */
    public static String generateARandomPasswordWithHash() {

        StringBuilder tmp = new StringBuilder();
        char[] symbols, buffer = new char[5];
        String date = getSystemDate();
        Random random = new Random();
        StringBuffer sb = new StringBuffer();

        for (char ch = '0'; ch <= '9'; ++ch) {
            tmp.append(ch);
        }
        for (char ch = 'A'; ch <= 'Z'; ++ch) {
            tmp.append(ch);
        }
        symbols = tmp.toString().toCharArray();

        for (int index = 0; index < buffer.length; ++index) {
            buffer[index] = symbols[random.nextInt(symbols.length)];
        }
        
        try {
            String actualPassword = new String(buffer);
            System.out.println("\nRandomly generated password: " + actualPassword);
            
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(( actualPassword + date).getBytes());
            byte[] byteData = md.digest();

            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
        } catch (NoSuchAlgorithmException ex) {

        }

        return new String(sb);
    }

   
    
    
    /**
     * this method is for getting system date. 
    */
    public static String getSystemDate() {

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = (dateFormat.format(cal.getTime())).toString();
        return date;
    }
 
    
    
    
    
    
    /**
     * this Thread class is used for handling multiple clients 
     */
    public static class ClientServiceThread extends Thread{
        
        int noOfDataSent = 1, clientId = -1;
        String sendMsg = "", receiveMsg = "";
        Socket clientSocket;
        
        ClientServiceThread(Socket socket, int id){
            clientSocket = socket;
            clientId = id;
        }
        
     /**
     * this method print client range and data packet in command prompt.
     * @param noOfDataSent 
     * @param clientId
     */
        void printIntoSystem(int noOfDataSent, int clientId) {
            
            long lower_limit = 1000000 * range; //1,000,000
            long upper_limit = lower_limit + (1000000 - 1);

            String sLower_limit = Long.toString(lower_limit, 36);
            String sUpper_limit = Long.toString(upper_limit, 36);

            if ((sLower_limit.length()) < 5) {

                StringBuilder sb = new StringBuilder(sLower_limit);
                for (int j = 0; j < 5 - (sLower_limit.length()); j++) {
                    sb.insert(j, "0");
                }

                sLower_limit = sb.toString().toUpperCase();
            }

            if ((sUpper_limit.length()) < 5) {

                StringBuilder sb = new StringBuilder(sUpper_limit);
                for (int j = 0; j < 5 - (sUpper_limit.length()); j++) {
                    sb.insert(j, "0");
                }

                sUpper_limit = sb.toString().toUpperCase();
            }      
            
            System.out.println("\nData packet_" + noOfDataSent +" sent to client_" + clientId);
            System.out.println("Given Range: " + 
                    sLower_limit.toUpperCase() + " to " + sUpper_limit.toUpperCase());
        }
        
        public void run() {
            try {
                BufferedReader brIN = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintStream psOUT = new PrintStream(clientSocket.getOutputStream());   //for writing

                while (noOfDataSent <= 4) {
                    sendMsg = (++range) + "\n" + hashPassword;
                    psOUT.println(sendMsg);
                    printIntoSystem(noOfDataSent, clientId);
                
                    receiveMsg = brIN.readLine();
                    if (receiveMsg.equals("success")) {
                        System.out.println("Client_" + clientId + " successfully crack the password");
                        break;
                    }
                    noOfDataSent++;
                }

                clientSocket.close();
                brIN.close();
                psOUT.close();
                System.out.println("Connection lost from client_" + clientId);

            } catch (IOException ex) {

            }

        }
    }
}
