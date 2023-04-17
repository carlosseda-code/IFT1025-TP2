package client_mvc;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * La classe <code>View</code>, une sous-classe de <code>HBox</code>, représente l'interface graphique du client. Elle
 * contient les éléments nécessaires à l'affichage des cours disponibles et à un formulaire d'inscription.
 */
public final class View extends HBox {

    // Au lieu de TableView<Course> pour separer la vue du reste du mvc:
    private final TableView<ObservableList<String>> table = new TableView<>();
    private final ChoiceBox<String> choiceBox = new ChoiceBox<>();
    private final Button loadButton = new Button("charger");

    //---------------------------- Gauche | Droite ----------------------------//

    private final TextField firstNameField = new TextField();
    private final TextField lastNameField = new TextField();
    private final TextField emailField = new TextField();
    private final TextField registrationNumberField = new TextField();
    private final Button submitButton = new Button("envoyer");


    /**
     * Constructeur de la classe <code>View</code> qui positionne et configure tous les éléments à afficher dans la
     * fenêtre du client.
     */
    public View() {
        // L'information responsable pour la creation (majoritaire) du constructeur se trouve sur:
        // https://jenkov.com/tutorials/javafx/index.html

        //--------------------- Cote gauche de l'interface graphique ---------------------//
        // Variables locales:
        VBox leftSide = new VBox();
        Text leftTitle = new Text("Liste des cours");
        HBox leftBottomBox = new HBox();

        // Taille et style du titre gauche
        leftTitle.setFont(Font.font("Arial", 20));

        // Arrangement du tableau des cours disponibles
        TableColumn<ObservableList<String>, String> codeColumn = new TableColumn<>("Code");
        TableColumn<ObservableList<String>, String> nameColumn = new TableColumn<>("Cours");
        codeColumn.setCellValueFactory(cellData -> Bindings.valueAt(cellData.getValue(), 0));
        nameColumn.setCellValueFactory(cellData -> Bindings.valueAt(cellData.getValue(), 1));
        codeColumn.setPrefWidth(105);
        nameColumn.setPrefWidth(160);
        table.setPlaceholder(new Label("No content in table"));
        table.getColumns().add(codeColumn);
        table.getColumns().add(nameColumn);

        // Sessions dont l'utilisateur peut selectionner
        choiceBox.setValue("Hiver");
        choiceBox.getItems().add("Hiver");
        choiceBox.getItems().add("Ete");
        choiceBox.getItems().add("Automne");
        choiceBox.setPrefWidth(110);

        // Alignements et espacements des elements
        leftSide.setAlignment(Pos.TOP_CENTER);               // alignement au centre
        leftSide.setPadding(new Insets(15));              // espace autour du "leftSide"
        leftSide.setSpacing(10);                             // espace entre les elements
        leftBottomBox.setAlignment(Pos.TOP_CENTER);          // alignement au centre
        leftBottomBox.setSpacing(45);                        // espace entre les elements

        // Tout ajouter dans le "View"
        leftBottomBox.getChildren().addAll(choiceBox, loadButton);
        leftSide.getChildren().addAll(leftTitle, table, leftBottomBox);
        this.getChildren().add(leftSide);

        // Separateur entre le cote gauche et le cote droit
        Separator separator = new Separator(Orientation.VERTICAL);
        this.getChildren().add(separator);

        //-------------------- Cote droit de l'interface graphique ---------------------//
        // Variables locales:
        VBox rightSide = new VBox();
        Text rightTitle = new Text("Formulaire d'inscription");
        GridPane gridPane = new GridPane();
        Text firstName = new Text("Prénom");
        Text lastName = new Text("Nom");
        Text email = new Text("Email");
        Text registrationNumber = new Text("Matricule");

        // Taille et style du titre droit
        rightTitle.setFont(Font.font("Arial", 20));

        // Taille bouton envoyer
        submitButton.setPrefWidth(100);

        // Espacement entre les champs textuels
        firstNameField.setPadding(new Insets(5, 10, 5, 10));
        lastNameField.setPadding(new Insets(5, 10, 5, 10));
        emailField.setPadding(new Insets(5, 10, 5, 10));
        registrationNumberField.setPadding(new Insets(5, 10, 5, 10));
        gridPane.setHgap(30);
        gridPane.setVgap(10);

        // Ajouter formulaire
        gridPane.add(firstName, 0, 0);
        gridPane.add(firstNameField, 1, 0);
        gridPane.add(lastName, 0, 1);
        gridPane.add(lastNameField, 1, 1);
        gridPane.add(email, 0, 2);
        gridPane.add(emailField, 1, 2);
        gridPane.add(registrationNumber, 0, 3);
        gridPane.add(registrationNumberField, 1, 3);
        gridPane.add(submitButton, 1, 4);
        GridPane.setHalignment(submitButton, HPos.CENTER);

        // Alignements et espacements des elements
        rightSide.setAlignment(Pos.TOP_CENTER);               // alignement au centre
        rightSide.setPadding(new Insets(15));              // espace autour du "rightSide"
        rightSide.setSpacing(10);                             // espace entre les elements
        gridPane.setPadding(new Insets(15));               // espace autour du formulaire

        // Ajouter le reste dans le "View"
        rightSide.getChildren().addAll(rightTitle, gridPane);
        this.getChildren().add(rightSide);

        // Couleur de fond
        this.setStyle("-fx-background-color: #dddddd;");
    }

    //----------------------------------------------------------------------------------------------------------------//

    /**
     * Méthode qui crée une fenêtre d'erreur contenant un message d'erreur et l'affiche au client.
     *
     * @param errorMessages Le message d'erreur à afficher dans la fenêtre.
     */
    public void showError(String errorMessages) {
        // SOURCE: http://www.java2s.com/example/java/javafx/show-javafx-error-popup.html
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error");
        alert.setContentText(errorMessages);

        alert.showAndWait();
    }

    /**
     * Méthode qui crée une fenêtre contenant un message à transmettre au client.
     *
     * @param message Le message à afficher dans la fenêtre.
     */
    public void showMessage(String message){
        // SOURCE: https://stackoverflow.com/questions/11662857/javafx-2-1-messagebox
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Message");
        alert.setHeaderText("Message");
        alert.setContentText(message);

        alert.showAndWait();
    }

    /**
     * Méthode qui permet d'acquérir le <code>TableView</code> de l'interface graphique qui agit comme le contenant des
     * divers cours disponibles pour une session.
     *
     * @return Le <code>TableView</code> de l'interface graphique.
     */
    public TableView<ObservableList<String>> getTableView() { return this.table; }

    /**
     * Méthode qui permet d'acquérir la rangée sélectionnée par le client qui représente le cours auquel ce client
     * souhaite s'inscrire.
     *
     * @return Un tableau de <code>String</code> qui contient le code et le nom du cours sélectionné, dans cet ordre, ou
     * null si aucune sélection n'est effectuée.
     */
    public String[] getSelectedRow() {

        ObservableList<String> selectedItem = this.table.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            return new String[]{(String) this.table.getColumns().get(0).getCellData(selectedItem),      // course code
                                (String) this.table.getColumns().get(1).getCellData(selectedItem)};     // course name
        }
        return new String[]{null, null};    // Pour eviter le NullPointerException lorsqu'on fait ".getSelectedRow()[1]"
    }

    /**
     * Méthode qui permet d'acquérir la session sélectionnée par le client, parmi les choix de sessions.
     *
     * @return Un <code>String</code> contenant une des trois valeurs possibles: "Hiver", "Ete" ou "Automne".
     */
    public String getSelectedSemester() { return this.choiceBox.getValue(); }

    /**
     * Méthode qui permet d'acquérir le bouton "charger" de la vue.
     *
     * @return Le <code>Button</code> "charger" de la vue.
     */
    public Button getLoadButton() { return this.loadButton; }

    /**
     * Méthode qui permet d'acquérir le champ de texte pour le prénom du client.
     *
     * @return Le <code>TextField</code> associé au prénom du client.
     */
    public TextField getFirstNameField() { return this.firstNameField; }

    /**
     * Méthode qui permet d'acquérir le champ de texte pour le nom du client.
     *
     * @return Le <code>TextField</code> associé au nom du client.
     */
    public TextField getLastNameField() { return this.lastNameField; }

    /**
     * Méthode qui permet d'acquérir le champ de texte pour l'adresse courriel du client.
     *
     * @return Le <code>TextField</code> associé à l'adresse courriel du client.
     */
    public TextField getEmailField() { return this.emailField; }

    /**
     * Méthode qui permet d'acquérir le champ de texte pour la matricule du client.
     *
     * @return Le <code>TextField</code> associé à la matricule du client.
     */
    public TextField getRegistrationNumberField() { return this.registrationNumberField; }

    /**
     * Méthode qui permet d'acquérir le bouton "envoyer" de la vue.
     *
     * @return Le <code>Button</code> "envoyer" de la vue.
     */
    public Button getSubmitButton() { return this.submitButton; }

}
