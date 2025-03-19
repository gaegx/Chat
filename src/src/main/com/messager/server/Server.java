package src.main.com.messager.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Server {
    private static final Map<String, User> online = new HashMap<>(); // Используем Map<String, User>

    // Класс для проверки порта
    public static class PortCheck {
        public static boolean checkPort(int port) {
            try (ServerSocket check = new ServerSocket(port)) {
                return true;
            } catch (IOException e) {
                return false;
            }
        }
    }

    // Класс для хранения информации о пользователе
    public static class User {
        private String username;
        private Socket socket;
        private PrintWriter writer;

        public User(String username, Socket socket, PrintWriter writer) {
            this.username = username;
            this.socket = socket;
            this.writer = writer;
        }

        public String getUsername() {
            return username;
        }

        public Socket getSocket() {
            return socket;
        }

        public PrintWriter getWriter() {
            return writer;
        }
    }

    // Обработчик клиента
    public static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        // Рассылка сообщения всем пользователям
        private void broadcast(String message) {
            synchronized (online) {
                for (User user : online.values()) {
                    user.getWriter().println(message);
                }
            }
        }

        // Удаление пользователя из списка
        private void remove(String username) {
            synchronized (online) {
                online.remove(username);
            }
            broadcast(username + " покинул чат.");
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {

                // Регистрация пользователя
                writer.println("Введите ваше имя:");
                String username = reader.readLine();
                User user = new User(username, clientSocket, writer);

                // Добавление пользователя в список
                synchronized (online) {
                    online.put(username, user);
                }

                broadcast(username + " присоединился к чату.");

                // Обработка сообщений
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    System.out.println("Получено от " + username + ": " + inputLine);
                    broadcast(username + ": " + inputLine);
                }
            } catch (IOException e) {
                System.err.println("Ошибка при работе с клиентом: " + e.getMessage());
            } finally {
                // Удаление пользователя при отключении
                try {
                    String username = ((User) online.get(clientSocket)).getUsername();
                    remove(username);
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("Ошибка при закрытии сокета: " + e.getMessage());
                }
            }
        }
    }

    // Запуск сервера
    public static void start() {
        boolean correctPort = false;
        System.out.print("Введите порт для сервера: ");
        Scanner scanner = new Scanner(System.in);
        int port = 0;

        // Проверка корректности порта
        while (!correctPort) {
            port = scanner.nextInt();
            if (PortCheck.checkPort(port)) {
                correctPort = true;
            } else {
                System.out.println("Этот порт занят попробуйте снова:");
            }
        }

        // Запуск сервера
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected with IP: " + clientSocket.getInetAddress().getHostAddress());


                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        start();
    }
}