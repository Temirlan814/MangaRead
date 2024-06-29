package com.example.demo;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import java.util.List;
import java.util.ResourceBundle;

public class ComicsController implements Initializable {

    @FXML
    private VBox ComicsChapterblock;
    @FXML
    private GridPane GenreComics;
    @FXML
    private Label ComicsType;
    @FXML
    private Label ComicsAuthor;
    @FXML
    private Label ReleaseData;
    @FXML
    private Label ComicsArtist;
    @FXML
    private Line Anime;
    @FXML
    private VBox ComicsChapters;
    @FXML
    private VBox ComicsInformation;
    @FXML
    private Label Description;
    @FXML
    private ScrollPane ReadComics;
    @FXML
    private BorderPane ComicsDescription;
    @FXML
    private ImageView ComicsImange;
    @FXML
    private ListView<ImageView> Comics;
    @FXML
    private Label ComicsName;
    private static String Name;
    private static String ImagePath;
    @FXML
    public VBox ComicsInfo;
    @FXML
    private List<ImageView> imageList;
    private int currentIndex = 0;
    private int totalPages;
    @FXML
    private VBox pageButtonsContainer; // VBox внутри ScrollPane для размещения кнопок страниц
    @FXML
    private Button toggleButton;
    @FXML
    private StackPane PageChapter;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ComicsRead();
        ComicsData(DatabaseController.ComicsDataBase(Name));
        ImageChapters(DatabaseController.DataChapters(Name));
        Genres(DatabaseController.GenreComics(Name));
    }
    public static void Comics(Comics comics) {
        Name = comics.getName();
        ImagePath = comics.getPhoto_url();
    }
    @FXML
    private void returnCatalog() throws IOException {
        HelloApplication.changeScene();
    }
    @FXML
    private void CloseComics() {
        ComicsDescription.setVisible(true);
        ReadComics.setVisible(false);
        PageChapter.setVisible(false);
    }
    private void ImageChapters(ArrayList<String> Chapters){
        for (String chapter : Chapters) {
            String[] Chapter = chapter.split(" ");

            String labelText = "Том: " + Chapter[0] + ", Глава: " + Chapter[1];
            Label volumeChapterLabel = new Label(labelText);

            Button actionButton = new Button();
            actionButton.setGraphic(volumeChapterLabel);
            actionButton.setOnAction(event -> {
                Comics_Chapter_Read(Chapter[0], Chapter[1]);
                System.out.println("Кнопка с текстом \"" + "\" и информацией о томе " + Chapter[0] + " и главе " + Chapter[1] + " нажата!" + ComicsChapters.getMaxWidth());
            });
            ComicsChapters.setFillWidth(true);
            actionButton.setAlignment(Pos.CENTER_LEFT);
            actionButton.setMaxWidth(Double.MAX_VALUE);
            ComicsChapters.getChildren().add(actionButton);
            actionButton.setStyle(
                    "-fx-background-color: white;" +  // Цвет фона кнопки по умолчанию
                            "-fx-padding: 10;" +  // Отступы внутри кнопки
                            "-fx-background-radius: 5;" +  // Радиус закругления фона кнопки
                            "-fx-border-radius: 5;" +  // Радиус закругления границы кнопки
                            "-fx-text-fill: black;" +  // Цвет текста кнопки
                            "-fx-font-size: 14;"  // Размер шрифта текста
            );

            actionButton.setOnMouseEntered(e -> actionButton.setStyle(
                    "-fx-background-color: #e0e0e0;" +  // Серый цвет фона при наведении курсора
                            "-fx-padding: 10;" +
                            "-fx-background-radius: 5;" +
                            "-fx-border-radius: 5;" +
                            "-fx-text-fill: black;" +
                            "-fx-font-size: 14;"));
            actionButton.setOnMouseExited(e -> actionButton.setStyle(
                    "-fx-background-color: white;" +  // Цвет фона по умолчанию
                            "-fx-padding: 10;" +
                            "-fx-background-radius: 5;" +
                            "-fx-border-radius: 5;" +
                            "-fx-text-fill: black;" +
                            "-fx-font-size: 14;"
            ));
        }
    }
    @FXML
    private void ChangePageChapter() {
        PageChapter.setVisible(!PageChapter.isVisible());
    }
    private void initializePageButtons() {
        // Очистите контейнер перед добавлением новых кнопок
        pageButtonsContainer.getChildren().clear();
        toggleButton.setText("Cтраница 1 ↑");
        pageButtonsContainer.setFillWidth(true);
        // Создайте кнопки для каждой страницы
        for (int i = 0; i < totalPages; i++) {
            Button pageButton = new Button("Страница " + (i + 1));
            int pageIndex = i;
            pageButton.setMaxWidth(Double.MAX_VALUE);
            pageButton.setStyle(
                    "-fx-background-color: white;" +  // Цвет фона кнопки по умолчанию
                            "-fx-padding: 10;" +  // Отступы внутри кнопки
                            "-fx-background-radius: 5;" +  // Радиус закругления фона кнопки
                            "-fx-border-radius: 5;" +  // Радиус закругления границы кнопки
                            "-fx-text-fill: black;" +// Цвет текста кнопки
                            "-fx-font-size: 10px"
            );

            // Добавляем эффект изменения фона кнопки при наведении курсора
            pageButton.setOnMouseEntered(e -> pageButton.setStyle(
                    "-fx-background-color: #e0e0e0;" +  // Серый цвет фона при наведении курсора
                            "-fx-padding: 10;" +
                            "-fx-background-radius: 5;" +
                            "-fx-border-radius: 5;" +
                            "-fx-text-fill: black;" +// Цвет текста кнопки
                            "-fx-font-size: 10px"
            ));
            pageButton.setOnMouseExited(e -> pageButton.setStyle(
                    "-fx-background-color: white;" +  // Цвет фона по умолчанию
                            "-fx-padding: 10;" +
                            "-fx-background-radius: 5;" +
                            "-fx-border-radius: 5;" +
                            "-fx-text-fill: black;"+// Цвет текста кнопки
                            "-fx-font-size: 10px"
            ));
            // Установите обработчик события для перехода к соответствующей странице при нажатии на кнопку
            pageButton.setOnAction(event -> {
                // Обновите текущий индекс страницы и отображайте содержимое
                currentIndex = pageIndex + 1;
                showPreviousImage();
                toggleButton.setText("Страница " + (pageIndex + 1) + "↑");
            });
            // Добавьте кнопку в контейнер
            pageButtonsContainer.getChildren().add(pageButton);
        }
    }
    @FXML
    private void Comics_Chapter_Read(String Tom, String Chapter) {
        totalPages = 0;
        // Скрываем описание и показываем область чтения
        ComicsDescription.setVisible(false);
        ReadComics.setVisible(true);
        // Получаем список изображений для главы и тома
        ArrayList<String> ImageComics = DatabaseController.ComicsImagedata(Name, Integer.parseInt(Tom), Integer.parseInt(Chapter));
        totalPages = ImageComics.size();
        // Очищаем список изображений, если он существует
        if (imageList == null) {
            imageList = new ArrayList<>();
        } else {
            imageList.clear();
        }
        // Устанавливаем текущий индекс в 0 для начала
        currentIndex = 0;

        // Добавляем изображения в список
        Task<List<ImageView>> loadImagesTask = new Task<>() {
            @Override
            protected List<ImageView> call() {
                List<ImageView> images = new ArrayList<>();
                for (String imagePath : ImageComics) {
                    Image image = new Image("file:" + imagePath, true); // Используйте фоновую загрузку
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(680);
                    imageView.setPreserveRatio(true);
                    images.add(imageView);
                }
                return images;
            }

            @Override
            protected void succeeded() {
                imageList = getValue();
                Comics.getItems().setAll(); // Очищаем все элементы из ListView
                Comics.getItems().add(imageList.get(currentIndex)); // Добавляем текущий элемент

                scrollToTop(Comics);
            }
        };

        new Thread(loadImagesTask).start();

        // Настраиваем ListView для отображения изображений
        Comics.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(ImageView item, boolean empty) {
                super.updateItem(item, empty);
                setStyle("-fx-background-color:#272727;");
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setStyle("-fx-background-color:#272727;");

                    // Устанавливаем контейнер как графический элемент ячейки
                    setGraphic(item);
                }
            }
        });
        // Устанавливаем первое изображение в ListView
        Comics.getStyleClass().add("my-list-view");
        initializePageButtons();
        // Добавляем вызов прокрутки к началу после установки первого изображения
        scrollToTop(Comics);
    }

    private void scrollToTop(ListView<ImageView> listView) {
        Platform.runLater(() -> {
            listView.scrollTo(0); // Прокручиваем ListView к началу

        });
    }
    @FXML
    private void showPreviousImage() {
        scrollToTop(Comics);
        if (currentIndex > 0) {
            toggleButton.setText("Страница " + (currentIndex) + "↑");
            currentIndex--;
            Comics.getItems().setAll(imageList.get(currentIndex));

        }
    }

    // Метод для отображения следующего изображения
    @FXML
    private void showNextImage() {
        scrollToTop(Comics);
        if (currentIndex < imageList.size() - 1) {
            currentIndex++;
            toggleButton.setText("Страница " + (currentIndex + 1) + "↑");
            Comics.getItems().setAll(imageList.get(currentIndex));
        }
        else{
            CloseComics();
        }
    }
    @FXML
    private void ComicsRead() {
        Image image = new Image("file:" + ImagePath);
        ComicsImange.setImage(image);
        ComicsImange.setFitWidth(160);
        ComicsImange.setFitHeight(230);
        Rectangle clipRect = new Rectangle(ComicsImange.getFitWidth(), ComicsImange.getFitHeight());
        clipRect.setArcWidth(15); // Радиус закругления по ширине
        clipRect.setArcHeight(15); // Радиус закругления по высоте
        ComicsImange.setClip(clipRect);
        ComicsName.setText(Name);
    }
    @FXML
    private void AnimationRight(){
        if(Anime.getTranslateX() == 0)
            Animation(107, ComicsInformation, ComicsChapterblock);
    }
    @FXML
    private void AnimationLeft(){
        if(Anime.getTranslateX() == 107)
            Animation(-107, ComicsChapterblock, ComicsInformation);
    }
    private void Animation(int x, VBox odds, VBox news){
        TranslateTransition front = new TranslateTransition(Duration.seconds(0.5), Anime);
        front.setByX(x); // Поднимаем errorLabel вверх на 50px, чтобы он стал видимым
        front.play();
        odds.setVisible(false);
        news.setVisible(true);

    }
    @FXML
    private void Genres(ArrayList<String> Genres){
        int row = 0;
        int col = 0;
        for(String genre : Genres){
            Button button = new Button();
            button.setText(genre);
            button.setStyle("-fx-background-color:Orange;-fx-border-radius: 10; -fx-background-radius: 10");

            button.setOnAction(event ->{
                try {
                    CatalogController.setGenreComics(genre);
                    HelloApplication.changeScene();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            GenreComics.setPadding(new Insets(10, 10, 10, 10));
            GenreComics.setHgap(10); // Промежуток между столбцами (горизонтальный промежуток)
            GenreComics.setVgap(10);
            GenreComics.add(button,col,row);
            col++;

        }
    }
    private void ComicsData(ArrayList<String> strings){
        ComicsType.setText(strings.get(0));
        ComicsAuthor.setText(strings.get(1));
        ComicsArtist.setText(strings.get(2));
        ReleaseData.setText(strings.get(3));
        Description.setText(strings.get(4));
    }
}
