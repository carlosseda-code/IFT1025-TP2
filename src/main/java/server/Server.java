package server;

import javafx.util.Pair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Classe qui représente un serveur qui porte sur l'inscription d'un client à un cours et qui traite les commandes d'un
 * client lorsqu'il est connecté.
 */
public class Server {

    /**
     * Constante, relié à la gestion d'événements, qui contient une des commandes dont le programme s'attend à recevoir
     */
    public final static String REGISTER_COMMAND = "INSCRIRE";

    /**
     * Constante, relié à la gestion d'événements, qui contient une des commandes dont le programme s'attend à recevoir
     */
    public final static String LOAD_COMMAND = "CHARGER";
    private final ServerSocket server;
    private Socket client;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private final ArrayList<EventHandler> handlers;

    /**
     * Constructeur de la classe <code>Server</code> qui permet d'avoir une communication potentielle avec un client et
     * qui sauvegarde les différents gestionnaires d'événements qui pouront être appelés
     * @param port Le port sur lequel une intercommunication pontentille peut se former entre le serveur et le client.
     * @throws IOException si une erreur survient à l'ouverture du socket
     */
    public Server(int port) throws IOException {
        this.server = new ServerSocket(port, 1);
        this.handlers = new ArrayList<EventHandler>();
        this.addEventHandler(this::handleEvents);
    }

    /**
     * Méthode qui ajoute un gestionnaire d'événement (ce qui gère les commandes que le serveur reçoit du client) à la
     * liste de gestionnaires d'événements.
     * @param h Le gestionnaire d'événement à ajouter à la liste.
     */
    public void addEventHandler(EventHandler h) {
        this.handlers.add(h);
    }

    private void alertHandlers(String cmd, String arg) {
        for (EventHandler h : this.handlers) {
            h.handle(cmd, arg);
        }
    }

    /**
     * Méthode qui lance le serveur puis écoute les commandes entrantes du client lorsqu'il est connecté.
     */
    public void run() {
        while (true) {
            try {
                client = server.accept();
                System.out.println("Connecté au client: " + client);
                objectInputStream = new ObjectInputStream(client.getInputStream());
                objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                listen();
                disconnect();
                System.out.println("Client déconnecté!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Méthode qui, lorsqu'une commande est générée par le client, décompose la commande puis appelle l'avertisseur des
     * gestions d'événements pour traiter la commande
     * @throws IOException si une erreur survient lors de la lecture du fichier
     * @throws ClassNotFoundException si la classe qu'on désérialise n'existe pas dans le programme
     */
    public void listen() throws IOException, ClassNotFoundException {
        String line;
        if ((line = this.objectInputStream.readObject().toString()) != null) {
            Pair<String, String> parts = processCommandLine(line);
            String cmd = parts.getKey();
            String arg = parts.getValue();
            this.alertHandlers(cmd, arg);
        }
    }

    /**
     * Méthode qui décompose une ligne de commande en sa composante commande et sa composante arguments
     * @param line La ligne de commande à décomposer
     * @return retourne une paire dont le premier élément est la commande et le deuxième, les arguments de la commande
     */
    public Pair<String, String> processCommandLine(String line) {
        String[] parts = line.split(" ");
        String cmd = parts[0];
        String args = String.join(" ", Arrays.asList(parts).subList(1, parts.length));
        return new Pair<>(cmd, args);
    }

    /**
     * Méthode qui met fin au serveur.
     * @throws IOException si une erreur survient lors de la fermeture du ObjectOutputStream, du ObjectInputStream ou du
     * Socket
     */
    public void disconnect() throws IOException {
        objectOutputStream.close();
        objectInputStream.close();
        client.close();
    }

    /**
     * Méthode qui applique le gestionnaire d'événement associé à une certaine commande
     * @param cmd La commande à traiter
     * @param arg L'argument associé à la commande
     */
    public void handleEvents(String cmd, String arg) {
        if (cmd.equals(REGISTER_COMMAND)) {
            handleRegistration();
        } else if (cmd.equals(LOAD_COMMAND)) {
            handleLoadCourses(arg);
        }
    }

    /**
     Lire un fichier texte contenant des informations sur les cours et les transofmer en liste d'objets 'Course'.
     La méthode filtre les cours par la session spécifiée en argument.
     Ensuite, elle renvoie la liste des cours pour une session au client en utilisant l'objet 'objectOutputStream'.
     @param arg la session pour laquelle on veut récupérer la liste des cours
     @throws Exception si une erreur se produit lors de la lecture du fichier ou de l'écriture de l'objet dans le flux
     */
    public void handleLoadCourses(String arg) {
        // TODO: implémenter cette méthode
    }

    /**
     Récupérer l'objet 'RegistrationForm' envoyé par le client en utilisant 'objectInputStream', l'enregistrer dans un fichier texte
     et renvoyer un message de confirmation au client.
     @throws Exception si une erreur se produit lors de la lecture de l'objet, l'écriture dans un fichier ou dans le flux de sortie.
     */
    public void handleRegistration() {
        // TODO: implémenter cette méthode
    }
}
