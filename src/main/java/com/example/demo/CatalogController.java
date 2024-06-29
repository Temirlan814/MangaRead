package com.example.demo;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import javafx.scene.layout.Region;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class CatalogController implements Initializable {
    private static String GenreComics;
    public Button Genres;
    @FXML
    public VBox Settings;
    public Button Genres_back;
    public Button GenresButton;
    public TextField DataSearhFrom;
    public TextField DataSearhTO;
    public Button Resets;
    public StackPane Catalog;
    public Label errorLabel;
    @FXML
    private GridPane gridPane;
    @FXML
    private GridPane MangaType;
    @FXML
    private VBox vBox_genres;
    @FXML
    private TextField TextSearch;
    @FXML
    private ListView<HBox> listView;
    private void ChosenComicsGenre(){
        for (HBox hbox : listView.getItems()) {
            CheckBox checkBox = (CheckBox) hbox.getChildren().get(0);
            Label label = (Label) hbox.getChildren().get(2);
            if (label.getText().equals(GenreComics)) {
                checkBox.setSelected(true);
                ImageShow(DatabaseController.ChooseGenres(new ArrayList<>(Collections.singleton(GenreComics))));
            }
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ImageShowCatalog();
        Genres_list();
        MangaType_list();
        TextSearch.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                // Вызов метода для выполнения поиска при нажатии Enter
                ImageShow(DatabaseController.displayResearch(TextSearch.getText().strip()));
            }
        });
        ChosenComicsGenre();
    }
    public static void setGenreComics(String genre) {
        GenreComics = genre;
    }
    @FXML
    private void errorYears(){

        // Создаем TranslateTransition для анимации появления ошибки
        TranslateTransition showErrorTransition = new TranslateTransition(Duration.seconds(2), errorLabel);
        showErrorTransition.setByY(50); // Поднимаем errorLabel вверх на 50px, чтобы он стал видимым

        // Пауза, чтобы ошибка оставалась видимой в течение определенного времени
        PauseTransition pause = new PauseTransition(Duration.seconds(2)); // 2 секунды видимости

        // Создаем TranslateTransition для анимации исчезновения ошибки
        TranslateTransition hideErrorTransition = new TranslateTransition(Duration.seconds(2), errorLabel);
        hideErrorTransition.setByY(-50); // Опускаем errorLabel обратно вниз

        // Создаем последовательный переход (SequentialTransition), объединяя все три анимации
        SequentialTransition animation = new SequentialTransition(showErrorTransition, pause, hideErrorTransition);
        animation.play();
    }
    @FXML
    private void AllSetting(){
        LinkedHashSet<Comics> setByGenre = new LinkedHashSet<>(ChooseGenre());
        LinkedHashSet<Comics> setByType = new LinkedHashSet<>(MangaType_Choose());
        LinkedHashSet<Comics> setByYear = new LinkedHashSet<>(Years_Choose());
        LinkedHashSet<Comics> setResearch = new LinkedHashSet<>(DatabaseController.displayResearch(TextSearch.getText().strip()));
        // Находим пересечение множеств
        setByGenre.retainAll(setByType);
        setByGenre.retainAll(setByYear);
        setByGenre.retainAll(setResearch);
        // Преобразуем результат обратно в ArrayList
        ArrayList<Comics> filteredComics = new ArrayList<>(setByGenre);
        System.out.println(filteredComics.size());
        ImageShow(filteredComics);
    }
    @FXML
    private void ResetAll(){
        for (HBox hbox : listView.getItems()) {
            CheckBox checkBox = (CheckBox) hbox.getChildren().getFirst();
            checkBox.setSelected(false);
        }
        ArrayList<String> mangadata = DatabaseController.displayTypes();
        for (int row = 0; row < MangaType.getRowCount(); row++) {
            for (int col = 0; col < MangaType.getColumnCount(); col++) {
                if(row * MangaType.getColumnCount() + col < mangadata.size()) {
                    HBox hbox = (HBox) MangaType.getChildren().get(row * MangaType.getColumnCount() + col);
                    CheckBox checkBox = (CheckBox) hbox.getChildren().getFirst();
                    checkBox.setSelected(false);
                }
            }
        }
        DataSearhFrom.clear();
        DataSearhTO.clear();
        ImageShowCatalog();
    }
    private ArrayList<Comics> Years_Choose(){
        String From = DataSearhFrom.getText();
        String To = DataSearhTO.getText();
        if (!From.matches("[0-9]*") || !To.matches("[0-9]*")){
            errorYears();
            return DatabaseController.displayImage();
        }
        return DatabaseController.ChooseYears(From,To);
    }
    @FXML
    private void MangaType_list(){
        ArrayList<String> mangadata = DatabaseController.displayTypes();
        int row = 0;
        int col = 0;
        for (String item : mangadata){
            CheckBox checkBox = new CheckBox();
            Label label = new Label(item);
            Region region = new Region();
            region.setPrefWidth(10);
            HBox hbox = new HBox(checkBox,region, label);
            MangaType.add(hbox,col,row);
            MangaType.setPadding(new Insets(10, 10, 10, 10));
            MangaType.setHgap(25); // Промежуток между столбцами (горизонтальный промежуток)
            MangaType.setVgap(25);
            col++;
            if (col >= 2) {
                col = 0;
                row++;
            }
        }

    }
    private ArrayList<Comics> MangaType_Choose(){
        ArrayList<String> selectedItems = new ArrayList<>();
        for (int row = 0; row < MangaType.getRowCount(); row++) {
            for (int col = 0; col < MangaType.getColumnCount(); col++) {
                ArrayList<String> mangadata = DatabaseController.displayTypes();
                if(row * MangaType.getColumnCount() + col < mangadata.size()) {
                    HBox hbox = (HBox) MangaType.getChildren().get(row * MangaType.getColumnCount() + col);
                    CheckBox checkBox = (CheckBox) hbox.getChildren().getFirst();
                    Label label = (Label)  hbox.getChildren().getLast();
                    if (checkBox.isSelected()) {
                        selectedItems.add(label.getText());
                    }
                }
            }
        }
        System.out.println(selectedItems.size());
        return DatabaseController.ChooseTypes(selectedItems);
    }
    @FXML
    private void Genres_list(){
        ArrayList<String> data = DatabaseController.displayGenres();
        ObservableList<HBox> items = FXCollections.observableArrayList();
        for (String item : data) {
            CheckBox checkBox = new CheckBox();
            Region region = new Region();
            region.setPrefWidth(10);
            Label label = new Label(item);
            HBox hbox = new HBox(checkBox,region, label);
            checkBox.isSelected();
            items.add(hbox);
        }
        listView.setItems(items);
    }
    @FXML
    private ArrayList<Comics> ChooseGenre(){
        ArrayList<String> selectedItems = new ArrayList<>();
        for (HBox hbox : listView.getItems()) {
            CheckBox checkBox = (CheckBox) hbox.getChildren().get(0);
            Label label = (Label) hbox.getChildren().get(2);
            if (checkBox.isSelected()) {
                selectedItems.add(label.getText());
            }
        }
        return DatabaseController.ChooseGenres(selectedItems);
    }
    @FXML
    private void ShowGenres(){
        ShowG(vBox_genres,Genres,Genres_back);
    }
    @FXML
    private void ShowGenresBack(){
        ShowG(Settings,Genres_back,Genres);
    }
    private void ShowG( VBox news,Button origins_b, Button news_b){
        FadeTransition fadeOutTransition = new FadeTransition(Duration.seconds(1), news);
        fadeOutTransition.setFromValue(1.0); // Начинаем с непрозрачности 1.0
        fadeOutTransition.setToValue(0.0); // Завершаем непрозрачностью 0.0
        fadeOutTransition.setOnFinished(event -> news.setVisible(false));

        // Добавляем обработчик события к кнопке закрытия, чтобы запустить анимацию исчезновения
        news_b.setOnAction(event -> fadeOutTransition.play());


        // Создаем анимацию FadeTransition для плавного появления дополнительного окна
        FadeTransition fadeInTransition = new FadeTransition(Duration.seconds(1), news);
        fadeInTransition.setFromValue(0.0); // Начинаем с прозрачности 0.0
        fadeInTransition.setToValue(1.0); // Окончательная непрозрачность 1.0

        // Обработчик события для кнопки
        origins_b.setOnAction(event -> {
            // Устанавливаем видимость дополнительного окна в true
            news.setVisible(true);
            // Запускаем анимацию плавного появления
            fadeInTransition.play();
        });
    }
    @FXML
    private void ImageShowCatalog(){
        ImageShow(DatabaseController.displayImage());
    }
    @FXML
    private void ImageShow(ArrayList<Comics> comicses) {
        gridPane.getChildren().clear();
        int row = 0;
        int col = 0;
        for (Comics comics: comicses) {
            String path = comics.getPhoto_url();
            String imageName = comics.getName();
            Image image = new Image("file:" + path);
            if (image.isError()) {
                System.out.println("Failed to load image from path: " + path);
                continue;
            }
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(160);
            imageView.setFitHeight(210);
            imageView.setPreserveRatio(false);
            Label nameLabel = new Label(imageName);

            // Устанавливаем стиль для метки (цвет текста и фона)
            nameLabel.setStyle("-fx-background-color: rgba(0, 0, 0, 0); -fx-text-fill: white;-fx-font-weight: bold; -fx-font-size: 13px;");

            // Устанавливаем стиль для метки, чтобы она была расположена внизу StackPane
            StackPane.setAlignment(nameLabel, Pos.BOTTOM_CENTER);

            // Создаем StackPane, чтобы разместить метку внутри изображения
            StackPane stackPane = new StackPane();

            gridPane.setPadding(new Insets(10, 10, 10, 10));
            gridPane.setHgap(25); // Промежуток между столбцами (горизонтальный промежуток)
            gridPane.setVgap(25);
            Rectangle clipRect = new Rectangle(imageView.getFitWidth(), imageView.getFitHeight());
            clipRect.setArcWidth(15); // Радиус закругления по ширине
            clipRect.setArcHeight(15);
            Rectangle gradientRectangle = new Rectangle(imageView.getFitWidth(), imageView.getFitHeight()/3);
            // Установка градиента от прозрачного к черному цвету
            LinearGradient gradient = new LinearGradient(0, 0, 0, 1,
                    true, null,
                    new Stop(0, Color.TRANSPARENT),
                    new Stop(1, Color.BLACK));
            gradientRectangle.setFill(gradient);

            // Позиционируем прямоугольник в нижней части изображения
            // Устанавливаем перевод (translateY) так, чтобы прямоугольник был в нижней части
            gradientRectangle.setTranslateY(imageView.getFitHeight() / 2 - gradientRectangle.getHeight() / 2);
            stackPane.getChildren().addAll(imageView, gradientRectangle,nameLabel);
            // Радиус закругления по высоте
            imageView.setClip(clipRect);
            Button button = new Button();
            button.setGraphic(stackPane);
            button.setMinWidth(160);   // Минимальная ширина кнопки равна ширине изображения
            button.setMaxWidth(160);   // Максимальная ширина кнопки равна ширине изображения
            button.setMinHeight(210);  // Минимальная высота кнопки равна высоте изображения
            button.setMaxHeight(210);
            gridPane.setStyle("-fx-background-radius: 30; -fx-border-radius: 30;");
            button.setOnAction(event -> {
                System.out.println("Button with path " + path + " clicked!");
                try {
                    ComicsController.Comics(comics);
                    HelloApplication.ComicsRead();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                // Здесь вы можете добавить логику, которая будет выполняться при нажатии на кнопку
            });

            // Добавляем кнопку в сетку GridPane
            gridPane.add(button, col, row);
            // Увеличиваем индекс столбца
            col++;
            // Промежуток между строками (вертикальный промежуток)

            // Если индекс столбца достигает 5, переходим на следующий ряд
            if (col >= 4) {
                col = 0;
                row++;
            }
        }
    }
}
