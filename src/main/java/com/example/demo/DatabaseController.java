package com.example.demo;
import java.sql.*;
import java.util.ArrayList;

public class DatabaseController {

    private static final String URL = "jdbc:postgresql://localhost:5432/lab_pg";
    private static final String USER = "postgres";
    private static final String PASSWORD = "1";
    // login
    public static boolean addCredentials(String login, String password) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String checkQuery = "SELECT COUNT(*) FROM users WHERE login = ?";
            try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
                checkStatement.setString(1, login);
                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        if (count > 0) {
                            return false;
                        }
                    }
                }
            }

            String insertQuery = "INSERT INTO users (login, password) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, login);
                preparedStatement.setString(2, password);
                preparedStatement.executeUpdate();
                System.out.println("Данные успешно добавлены в базу данных");
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean checkCredentials(String login, String password) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE login = ? AND password = ?")) {

            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    // Catalog
    public static ArrayList<Comics> displayImage() {
        String query = """
                          SELECT titles.title_name, photos.photo_url
                          FROM titles INNER JOIN photos ON titles.title_id = photos.title_id
                          ORDER BY titles.title_name ASC
                        """;
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            ArrayList<Comics> comics = new ArrayList<>();
            while (resultSet.next()) {
                comics.add(new Comics(resultSet.getString("title_name"),resultSet.getString("photo_url")));
            }
            return comics;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static ArrayList<String> displayTypes(){
        String query = "SELECT type_name FROM types order by type_id";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            ArrayList<String> manga_type = new ArrayList<>();
            while (resultSet.next()) {
                manga_type.add(resultSet.getString("type_name"));

            }
            return manga_type;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static ArrayList<String> displayGenres() {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            String query = """
                            SELECT genre_name FROM genres
                            ORDER BY genre_name ASC""";
            ResultSet resultSet = statement.executeQuery(query);

            ArrayList<String> genres = new ArrayList<>();
            while (resultSet.next()) {
                genres.add(resultSet.getString("genre_name"));

            }
            return genres;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static ArrayList<Comics> ChooseYears(String From, String To) {
        if (From.isEmpty() && To.isEmpty()) {
            return displayImage();
        } else {
            if (From.isEmpty()) {
                From = "1970-01-01";
            } else {
                From += "-01-01";
            }
            if (To.isEmpty()) {
                To = "2030-12-31";
            } else {
                To += "-12-31";
            }
        }
        String query = """
                        SELECT titles.title_name, photos.photo_url
                        FROM titles INNER JOIN photos ON titles.title_id = photos.title_id
                        WHERE CAST(titles.release_date AS DATE) BETWEEN CAST(? AS DATE) AND CAST(? AS DATE)""";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
             pstmt.setString(1, From);
             pstmt.setString(2, To);
             ResultSet resultSet = pstmt.executeQuery();
             ArrayList<Comics> comicsChosenYear = new ArrayList<>();
            while (resultSet.next()) {
                comicsChosenYear.add(new Comics( resultSet.getString("title_name"),resultSet.getString("photo_url")));
            }
            return comicsChosenYear;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    public static ArrayList<Comics> ChooseTypes(ArrayList<String> chosenTypes) {
        if(chosenTypes.isEmpty()){
            return displayImage();
        }
        String query = """      
                        SELECT titles.title_name, photos.photo_url
                        FROM titles
                        INNER JOIN photos ON titles.title_id = photos.title_id
                        INNER JOIN types ON titles.type_id = types.type_id
                        WHERE types.type_name IN (%s)
                        GROUP BY titles.title_name, photos.photo_url
                        """;
        StringBuilder placeType = new StringBuilder();
        for (int i = 0; i < chosenTypes.size(); i++) {
            placeType.append("?");
            if (i < chosenTypes.size() - 1) {
                placeType.append(", ");
            }
        }

        // Заменяем плейсхолдер в запросе списком жанров
        query = String.format(query, placeType);

        ArrayList<Comics> comicsType = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Устанавливаем параметры для выбранных жанров
            for (int i = 0; i < chosenTypes.size(); i++) {
                pstmt.setString(i + 1, chosenTypes.get(i));
            }


            // Выполняем запрос и обрабатываем результаты
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String titleName = rs.getString("title_name");
                String photoUrl = rs.getString("photo_url");
                comicsType.add(new Comics(titleName, photoUrl));
            }
            return comicsType;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static ArrayList<Comics> ChooseGenres(ArrayList<String> chosenGenres) {
        if(chosenGenres.isEmpty()){
            return displayImage();
        }
        String query = """
                        SELECT titles.title_name, photos.photo_url
                        FROM titles
                        INNER JOIN photos ON titles.title_id = photos.title_id
                        INNER JOIN titlegenres ON titles.title_id = titlegenres.title_id
                        INNER JOIN genres ON titlegenres.genre_id = genres.genre_id
                        WHERE genres.genre_name IN (%s)
                        GROUP BY titles.title_name, photos.photo_url
                        HAVING COUNT(DISTINCT genres.genre_name) = ?
                        """;

        // Формирование списка мест для параметров запроса
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < chosenGenres.size(); i++) {
            placeholders.append("?");
            if (i < chosenGenres.size() - 1) {
                placeholders.append(", ");
            }
        }

        // Заменяем плейсхолдер в запросе списком жанров
        query = String.format(query, placeholders);

        ArrayList<Comics> comicsChosenGenre = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Устанавливаем параметры для выбранных жанров
            for (int i = 0; i < chosenGenres.size(); i++) {
                pstmt.setString(i + 1, chosenGenres.get(i));
            }

            // Устанавливаем условие для того, чтобы получить только те данные, которые соответствуют всем выбранным жанрам
            pstmt.setInt(chosenGenres.size() + 1, chosenGenres.size());

            // Выполняем запрос и обрабатываем результаты
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String titleName = rs.getString("title_name");
                String photoUrl = rs.getString("photo_url");
                comicsChosenGenre.add(new Comics(titleName, photoUrl));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return comicsChosenGenre;
    }
    static ArrayList<Comics> displayResearch(String Name){
        if(Name == null){
            return displayImage();
        }
        ArrayList<Comics> comicsChosenResearch = new ArrayList<>();
        String query = """
                SELECT titles.title_name , photos.photo_url
                FROM titles INNER JOIN photos ON titles.title_id = photos.title_id
                WHERE LOWER(title_name) LIKE LOWER(?)""";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, "%" + Name + "%");
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                String titleName = resultSet.getString("title_name");
                String photoUrl = resultSet.getString("photo_url");
                comicsChosenResearch.add(new Comics(titleName, photoUrl));            }
            return comicsChosenResearch;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    //Comics
    public static ArrayList<String> DataChapters(String Name) {
        String qeury = """
                SELECT chapters.tom_number, chapters.chapter_number
                FROM titles
                    INNER JOIN chapters ON titles.title_id = chapters.title_id
                WHERE title_name = ?
                ORDER BY chapters.chapter_number DESC
                """;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(qeury)) {
            pstmt.setString(1, Name);
            ResultSet resultSet = pstmt.executeQuery();
            ArrayList<String> chapters_data = new ArrayList<>();
            while (resultSet.next()) {
                chapters_data.add(resultSet.getString("tom_number")+" "+resultSet.getString("chapter_number"));
            }
            return chapters_data;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static ArrayList<String> ComicsDataBase(String Name){
        String query = """
                SELECT types.type_name , author, artist,EXTRACT(YEAR FROM release_date) AS year, description
                FROM Titles
                    INNER JOIN types ON Titles.type_id = types.type_id
                WHERE title_name = ?
                """;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, Name);
            ResultSet rs = pstmt.executeQuery();
            ArrayList<String> data = new ArrayList<>();
            while (rs.next()) {
                data.add(rs.getString("type_name"));
                data.add(rs.getString("author"));
                data.add(rs.getString("artist"));
                data.add(rs.getString("year"));
                data.add(rs.getString("description"));
            }
            return data;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static ArrayList<String> ComicsImagedata(String Name, int Tom, int Chapter){
        String query = """
                SELECT chapterphotos.photo_url
                FROM titles
                    INNER JOIN chapters ON titles.title_id = chapters.title_id
                    INNER JOIN chapterphotos ON chapters.chapter_id = chapterphotos.chapter_id
                WHERE title_name = ? AND tom_number = ? AND chapter_number = ?
                ORDER BY
                    -- Извлечение числовых значений из photo_url
                    CAST(REGEXP_REPLACE(photo_url, '[^0-9]', '', 'g') AS INTEGER);
                """;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, Name);
                pstmt.setInt(2, Tom);
                pstmt.setInt(3, Chapter);
                ResultSet rs = pstmt.executeQuery();
                ArrayList<String> photo_url = new ArrayList<>();
                while (rs.next()) {
                    photo_url.add(rs.getString("photo_url"));

                }
            return photo_url;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    static ArrayList<String> GenreComics(String Name){
        String query = """
                SELECT genres.genre_name
                FROM titles INNER JOIN titlegenres ON titles.title_id = titlegenres.title_id
                            INNER JOIN genres ON titlegenres.genre_id = genres.genre_id
                WHERE title_name = ?
                """;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, Name);
            ResultSet rs = pstmt.executeQuery();
            ArrayList<String> Genres = new ArrayList<>();
            while (rs.next()) {
                Genres.add(rs.getString("genre_name"));
            }
            return Genres;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}