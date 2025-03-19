package src.main.com.messager.client;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;



public class Client {
    public static void  Connect(){
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter IP Address: ");
        String ip = scanner.nextLine();
        System.out.print("Enter Port: ");
        int port = scanner.nextInt();
        try (Socket socket = new Socket(ip, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            System.out.println("Connected to Server " + ip + ":" + port);
            new Thread(() -> {
                try {
                    String serverResponse;
                    while((serverResponse =in.readLine() ) != null){
                        System.out.println(serverResponse);
                    }
                }catch (IOException e){
                    System.err.println("Ошибка при чтении данных: " + e.getMessage());
                }

            }).start();

            String userInput;
            while(true){
                userInput = scanner.nextLine();
                out.println(userInput);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }




    }
    public static void main(String[] args) {
        Connect();

    }
}
