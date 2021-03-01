import java.io.*;
import java.util.HashMap;
import java.net.*;
import java.security.KeyStore;
import javax.net.*;
import javax.net.ssl.*;
import javax.security.cert.X509Certificate;

public class server implements Runnable {
    private ServerSocket serverSocket = null;
    private static int numConnectedClients = 0;
    private HashMap<String, Doctor> doctorMap;
    private HashMap<String, Nurse> nurseMap;
    private HashMap<String, Gov> govMap;
    private HashMap<String, Patient> patientMap;
    private HashMap<String, Record> db;
    
    public server(ServerSocket ss) throws IOException {
        serverSocket = ss;
        newListener();

        /*
        Indiv[] doctors = new Indiv[100];
        doctors[0] = new Doctor("Elna", "Data");
        Indiv[] nurses = new Indiv[100];
        nurses[0] = new Nurse("Gabriel", "Data");
        Indiv[] govs = new Indiv[100];
        govs[0] = new Gov("Patrik");
        Record r1 = new Record( (Doctor)doctors[0], (Nurse)nurses[0], "Data", "This patient has a severe headache! Prescribing Paracetamol in large doses.");
        Indiv[] patients = new Indiv[100];
        patients[0] = new Patient("Oscar", r1);
        */
        
        this.doctorMap = new HashMap();
        doctorMap.put("Elna", new Doctor("Elna", "Data"));
        this.nurseMap = new HashMap();
        nurseMap.put("Gabriel", new Nurse("Gabriel", "Data"));
        this.govMap = new HashMap();
        govMap.put("Patrik", new Gov("Patrik"));

        Record r1 = new Record( doctorMap.get("Elna"), nurseMap.get("Gabriel"), "Data", "This patient has a severe headache! Prescribing Paracetamol in large doses.");

        this.patientMap = new HashMap();
        patientMap.put("Oscar", new Patient("Oscar", r1));
        
        this.db = new HashMap();
	    db.put("Oscar", r1);
    }

    public void run() {
        try {
            SSLSocket socket=(SSLSocket)serverSocket.accept();
            newListener();
            SSLSession session = socket.getSession();
            X509Certificate cert = (X509Certificate)session.getPeerCertificateChain()[0];
            String subject = cert.getSubjectDN().getName();
            //Check the authenticety of the request.
    	    numConnectedClients++;
            System.out.println("client connected");
            System.out.println("client name (cert subject DN field): " + subject);
            System.out.println(numConnectedClients + " concurrent connection(s)\n");

            PrintWriter out = null;
            BufferedReader in = null;
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String clientMsg = null;
            while ((clientMsg = in.readLine()) != null) {
            //If the user is authentic, then the clientmessage will be a correct 
            String res = "";
            String[] input = clientMsg.split(" ");
            String command = input[0];

            switch(command){
                case "read": {
                    if(input.length == 2){
                        res = db.get(input[1]).toString();
                    }
                }
                case "write": {
                    if(input.length == 3){
                        res = db.get(input[1]).write(input[1], input[2]);
                    }
                }
                case "create": {
                    if(input.length == 4){
                        Patient patient = patientMap.get(input[1]);
                        Doctor doctor = doctorMap.get(input[2]);
                        Nurse nurse = nurseMap.get(input[3]);
                        String department = input[4];
                        String data = input[5];
                        if(patient != null && doctor != null && nurse != null){
                            Record record = new Record(doctor,nurse,department,data);
                            db.put(patient.toString(), record);
                        }
                    }
                    res = "tried to create some data";
                }
                case "delete": {
                    res = "tried to delete some data";
                }
                default: {
                    res = "Command not found";
                }
            }
		
		    System.out.println("received '" + clientMsg + "' from " + subject);
            System.out.print("sending information to client...");
			out.println(res);
			out.flush();
            System.out.println("done\n");
		    }
			in.close();
			out.close();
			socket.close();
    	    numConnectedClients--;
            System.out.println("client disconnected");
            System.out.println(numConnectedClients + " concurrent connection(s)\n");
		} catch (IOException e) {
            System.out.println("Client died: " + e.getMessage());
            e.printStackTrace();
            return;
        }
    }

    private void newListener() { (new Thread(this)).start(); } // calls run()

    public static void main(String args[]) {
        System.out.println("\nServer Started\n");
        int port = -1;
        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);
        }
        String type = "TLS";
        try {
            ServerSocketFactory ssf = getServerSocketFactory(type);
            ServerSocket ss = ssf.createServerSocket(port);
            ((SSLServerSocket)ss).setNeedClientAuth(true); // enables client authentication
            new server(ss);
        } catch (IOException e) {
            System.out.println("Unable to start Server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static ServerSocketFactory getServerSocketFactory(String type) {
        if (type.equals("TLS")) {
            SSLServerSocketFactory ssf = null;
            try { // set up key manager to perform server authentication
                SSLContext ctx = SSLContext.getInstance("TLS");
                KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
                TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
                KeyStore ks = KeyStore.getInstance("JKS");
				KeyStore ts = KeyStore.getInstance("JKS");
                char[] password = "password".toCharArray();

                ks.load(new FileInputStream("serverkeystore"), password);  // keystore password (storepass)
                ts.load(new FileInputStream("servertruststore"), password); // truststore password (storepass) //This is where the log in information about the users is stored.
                kmf.init(ks, password); // certificate password (keypass)
                tmf.init(ts);  // possible to use keystore as truststore here
                ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
                ssf = ctx.getServerSocketFactory();
                return ssf;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return ServerSocketFactory.getDefault();
        }
        return null;
    }
}
