package src.main.com.messager.client;
import java.io.Serializable;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static volatile boolean isConnected = true;


    class DataPacket implements Serializable {
        private String fileName;
        private long fileSize;
        private byte[] data;

        public DataPacket(String fileName, long fileSize, byte[] data) {
            this.fileName = fileName;
            this.fileSize = fileSize;
            this.data = data;
        }
        public String getFileName() {
            return fileName;
        }
        public long getFileSize() {
            return fileSize;
        }
        public byte[] getData() {
            return data;
        }
    }

    public static void Connect() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter IP Address: ");
        String ip = scanner.nextLine();
        System.out.print("Enter Port: ");
        int port = scanner.nextInt();
        scanner.nextLine(); // Очистка буфера после nextInt()

        try (Socket socket = new Socket(ip, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("Connected to Server " + ip + ":" + port);

            // Поток для чтения сообщений от сервера
            new Thread(() -> {
                try {
                    String serverResponse;
                    while (isConnected && (serverResponse = in.readLine()) != null) {
                        System.out.println(serverResponse);
                    }
                } catch (IOException e) {
                    System.err.println("Ошибка при чтении данных: " + e.getMessage());
                } finally {
                    isConnected = false; // Соединение разорвано
                    System.out.println("Соединение с сервером разорвано.");
                }
            }).start();




            // Отправка сообщений серверу
            String userInput;
            while (isConnected && (userInput = scanner.nextLine()) != null) {
                out.println(userInput);
            }

        } catch (IOException e) {
            System.err.println("Ошибка при подключении к серверу: " + e.getMessage());
        } finally {
            isConnected = false; // Соединение разорвано
            System.out.println("Клиент завершил работу.");
        }
    }

    public static void main(String[] args) {
        Connect();
    }
}
