package client_mvc;

import javafx.application.Application;

/**
 * Classe qui lance le démarrage de l'interface graphique du client. Elle est nécessaire à la création d'un jar
 * exécutable.
 */
public class ClientLauncher { // La solution a une erreur lors de l'execution du jar - SOURCE: Piazza @120_f2
    /**
     * Méthode qui lance l'application JavaFX à partir de la classe <code>Main</code>, qui est la classe principale de
     * l'application.
     *
     * @param args Les arguments de la ligne de commandes.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
