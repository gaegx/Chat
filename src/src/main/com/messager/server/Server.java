package src.main.com.messager.server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {

    public class PortCheck {
        public static boolean checkPort(int port) {
            try (ServerSocket chek=new ServerSocket(port)) {
                return true;
            }catch (IOException e){
                return false;
            }
        }
    }

    public static void Start(){
        boolean coorectport=false;
        System.out.print("Enter the server port:");
        Scanner scanner = new Scanner(System.in);
        int port=0;
        while(!coorectport){
            port = scanner.nextInt();
            if(PortCheck.checkPort(port)){
                coorectport=true;
            }
            else{
                System.out.println("Invalid server port, please try again:");
            }
        }
        try(ServerSocket serverSocket =new ServerSocket(port)) {
            System.out.println("Server started in port "+port);
            while(true){
                Socket clientsocet = serverSocket.accept();
                System.out.println("Client connected in ip "+clientsocet.getInetAddress().getHostAddress());

            }


        }catch (IOException e){
            e.printStackTrace();
        }



    }

    public static void main(String[] args) {
        Start();

    }


}
