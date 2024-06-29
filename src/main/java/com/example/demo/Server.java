package com.example.demo;

import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) throws IOException {
        // Создайте серверный сокет на порту 12345
        ServerSocket serverSocket = new ServerSocket(12345);

        while (true) {
            // Ожидайте клиента
            Socket clientSocket = serverSocket.accept();
            // Обработайте клиента в отдельном потоке
            new ClientHandler(clientSocket).start();
        }
    }
}

class ClientHandler extends Thread {
    private Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);

            // Чтение запроса от клиента
            String request = reader.readLine();

            // Обработка запроса клиента (например, аутентификация пользователя)
            String response = handleRequest(request);

            // Отправка ответа клиенту
            writer.println(response);

            // Закройте соединение
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String handleRequest(String request) {
        // Разберите запрос клиента
        String[] parts = request.split(":");
        String action = parts[0];
        String credentials = parts[1];

        if (action.equals("login")) {
            // Разделите учетные данные на логин и пароль
            String[] creds = credentials.split("=");
            String username = creds[0];
            String password = creds[1];

            // Проверьте учетные данные с помощью DatabaseController (используйте свой метод проверки)
            if (DatabaseController.checkCredentials(username, password)) {
                // Если аутентификация успешна, верните "success"
                return "success";
            } else {
                // Если аутентификация не удалась, верните "failure"
                return "failure";
            }
        }
        // Другие действия можно обработать аналогично

        return "unknown_action";
    }
}
