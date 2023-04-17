package client_mvc;

// Ces deux classes doivent appartenir au projet du client, nous les avons pris depuis le server au lieu de dupliquer
import server.models.Course;
import server.models.RegistrationForm;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;


/**
 * Classe principale du MVC du client qui démarre l'interface graphique pour un client qui veut s'inscrire à un cours à
 * l'Université de Montréal.
 */
public class Main extends Application { // Structure du mvc du client est base sur le "MVC_Meilleur" du professeur


    /**
     * Méthode qui initialise l'interface graphique pour un client qui souhaite s'inscrire à un cours à l'Université de
     * Montréal, en reliant les différentes composantes du MVC et en affichant la fenêtre JavaFX au client.
     *
     * @param primaryStage La fenêtre à afficher au client.
     */
    @Override
    public void start(Stage primaryStage) {

        View view = new View();
        Course course = new Course("", "", "");
        RegistrationForm registrationForm = new RegistrationForm("", "", "", "", course);
        Controller controller = new Controller(view, course, registrationForm);

        Scene scene = new Scene(view, 600, 400);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Inscription UdeM");
        primaryStage.show();
    }

    /**
     * Méthode représentant le point d'entrée au lancement de l'interface graphique définie dans la méthode
     * <code>start()</code> de la classe <code>Main</code>.
     *
     * @param args Les arguments de la ligne de commandes.
     */
    public static void main(String[] args) {
        launch(args);
    }

}
