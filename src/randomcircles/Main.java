package randomcircles;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;

import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;

public class Main extends Application {

    private Image pattern = null;
    private Rectangle imagePattern = null;

    private final ColorPicker colorPicker = new ColorPicker(Color.RED);
    private final TextField radiusTextField = new TextField();

    private final CheckBox checkBox = new CheckBox();

    private final Slider slider = new Slider(1, 5, 1);

    private final Pane pane = new Pane();

    @Override
    public void start(Stage primaryStage) {

        HBox hBox = new HBox();

        VBox vnejsiVBox = new VBox();
        vnejsiVBox.setAlignment(Pos.CENTER);

        VBox vnitrniVBox = new VBox(10);
        vnitrniVBox.setPadding(new Insets(10));
        VBox.setMargin(vnitrniVBox, new Insets(10));
        VBox.setVgrow(vnitrniVBox, Priority.ALWAYS);
        vnitrniVBox.setBorder(new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, new CornerRadii(10), null)));

        HBox firstRow = new HBox();
        firstRow.setSpacing(10);
        Label colorLabel = new Label("Barva okraje:");
        HBox.setHgrow(colorPicker, Priority.ALWAYS);
        firstRow.getChildren().addAll(colorLabel, colorPicker);

        HBox secondRow = new HBox();
        secondRow.setSpacing(10);
        Label raduiusLabel = new Label("Polomer:");

        HBox.setHgrow(radiusTextField, Priority.ALWAYS);
        secondRow.getChildren().addAll(raduiusLabel, radiusTextField);

        HBox thirdRow = new HBox();
        thirdRow.setSpacing(10);
        Label checkBoxLabel = new Label("Kreslit okraj?");

        checkBox.setSelected(true);
        thirdRow.getChildren().addAll(checkBoxLabel, checkBox);

        HBox fourthRow = new HBox();
        fourthRow.setSpacing(10);
        Label countLabel = new Label("Pocet:");

        slider.setBlockIncrement(1);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
        HBox.setHgrow(slider, Priority.ALWAYS);
        slider.setShowTickLabels(true);
        slider.setSnapToTicks(true);
        fourthRow.getChildren().addAll(countLabel, slider);

        HBox imageHBox = new HBox();
        imagePattern = new Rectangle(0, 0, 200, 200);
        imagePattern.setOnMouseClicked(event -> {
            if (event.getButton() != MouseButton.PRIMARY) {
                return;
            }
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("BMP", "*.bmp"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPG", "*.jpg"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("GIF", "*.gif"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Všechny soubory", "*.*"));

            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                try {
                    pattern = new Image(new FileInputStream(file));
                    imagePattern.setFill(new ImagePattern(pattern));

                } catch (FileNotFoundException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("Vybrany obrazek se nepovedlo nacist");
                    alert.showAndWait();
                }
            }
        });
        imagePattern.setFill(Color.GRAY);
        imagePattern.setStroke(Color.BLUE);
        imageHBox.setAlignment(Pos.CENTER);
        imageHBox.getChildren().add(imagePattern);

        HBox lastRow = new HBox();
        lastRow.setAlignment(Pos.CENTER);
        Button generateButton = new Button("Generuj");
        generateButton.setOnAction(actionEvent -> {
            generateCircles();
        });
        generateButton.setMaxWidth(Double.MAX_VALUE);
        lastRow.getChildren().add(generateButton);
        HBox.setHgrow(generateButton, Priority.ALWAYS);

        vnitrniVBox.getChildren().addAll(firstRow, secondRow, thirdRow, fourthRow, imageHBox, lastRow);
        vnitrniVBox.setAlignment(Pos.CENTER);
        vnejsiVBox.getChildren().add(vnitrniVBox);
        hBox.getChildren().add(vnejsiVBox);

        pane.heightProperty().addListener(observable -> {
            clearPane();
        });
        pane.widthProperty().addListener(observable -> {
            clearPane();
        });
        pane.setBackground(new Background(new BackgroundFill(Color.web("C0FFC0"), null, null)));
        hBox.getChildren().add(pane);
        HBox.setHgrow(pane, Priority.ALWAYS);

        Scene scene = new Scene(hBox, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private void generateCircles() {
        Double radius;
        String radiusString = radiusTextField.getText();
        try {
            radius = Double.valueOf(radiusString);
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Chyba vstupnich hodnot");
            alert.setContentText("Zvolena hodnota neni v rozsahu <10, 200>");
            alert.showAndWait();
            return;
        }
        if (radius < 10 || radius > 200) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Chyba vstupnich hodnot");
            alert.setContentText("Zvolena hodnota neni v rozsahu <10, 200>");
            alert.showAndWait();
            return;
        }
        Color color = colorPicker.getValue();
        boolean addBorder = checkBox.isSelected();
        int count = (int) slider.getValue();

        double paneWidth = pane.getWidth();
        double paneHeight = pane.getHeight();
        System.out.println("Pane Width: " + paneWidth);
        System.out.println("Pane Height: " + paneHeight);
        if (2 * radius > paneHeight || 2 * radius > paneWidth) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Okno je prilis male pro zadany polomer. Kruznice nelze generovat.");
            alert.showAndWait();
            return;
        }
        for (int i = 0; i < count; i++) {
            Circle circle = new Circle();
            circle.setRadius(radius);
            if (pattern == null) {
                circle.setFill(Color.TRANSPARENT);
            } else {
                circle.setFill(new ImagePattern(pattern));
            }
            if (addBorder) {
                circle.strokeWidthProperty().setValue(3);
                circle.setStroke(color);
            }
            Random r = new Random();
            double randomXValue = radius + (paneWidth - 2 * radius) * r.nextDouble();
            double randomYValue = radius + (paneHeight - 2 * radius) * r.nextDouble();

            circle.setTranslateX(randomXValue);
            circle.setTranslateY(randomYValue);
            circle.setOnMouseClicked(event -> {
                if (event.getButton() != MouseButton.SECONDARY) {
                    return;
                }
                pane.getChildren().remove(circle);
            });
            pane.getChildren().add(circle);
        }
    }

    private void clearPane() {
        pane.getChildren().clear();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

}
