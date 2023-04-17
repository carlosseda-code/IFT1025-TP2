package server;

import javafx.util.Pair;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import server.models.Course;
import server.models.RegistrationForm;

/**
 * Classe qui représente un serveur qui porte sur l'inscription d'un client à un cours et qui traite les commandes d'un
 * client lorsqu'il est connecté.
 */
public class Server {

    /**
     * Constante reliée à la gestion d'événements, qui contient une des commandes dont le programme s'attend à recevoir.
     */
    public final static String REGISTER_COMMAND = "INSCRIRE";

    /**
     * Constante reliée à la gestion d'événements, qui contient une des commandes dont le programme s'attend à recevoir.
     */
    public final static String LOAD_COMMAND = "CHARGER";
    private final ServerSocket server;
    private Socket client;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private final ArrayList<EventHandler> handlers;

    /**
     * Constructeur de la classe <code>Server</code> qui permet d'avoir une communication potentielle avec un client et
     * qui sauvegarde les différents gestionnaires d'événements qui pourront être appelés.
     *
     * @param port Le port sur lequel une intercommunication potentielle peut se former entre le serveur et le client.
     * @throws IOException si une erreur survient à l'ouverture du socket.
     */
    public Server(int port) throws IOException {
        this.server = new ServerSocket(port, 1);
        this.handlers = new ArrayList<EventHandler>();
        this.addEventHandler(this::handleEvents);
    }

    /**
     * Méthode qui ajoute un gestionnaire d'événement (ce qui gère les commandes que le serveur reçoit du client) à la
     * liste de gestionnaires d'événements.
     *
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
     * gestions d'événements pour traiter la commande.
     *
     * @throws IOException si une erreur survient lors de la lecture du fichier.
     * @throws ClassNotFoundException si la classe qu'on désérialise n'existe pas dans le programme.
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
     * Méthode qui décompose une ligne de commande en sa composante commande et sa composante arguments.
     *
     * @param line La ligne de commande à décomposer.
     * @return Une paire dont le premier élément est la commande et le deuxième, les arguments de la commande.
     */
    public Pair<String, String> processCommandLine(String line) {
        String[] parts = line.split(" ");
        String cmd = parts[0];
        String args = String.join(" ", Arrays.asList(parts).subList(1, parts.length));
        return new Pair<>(cmd, args);
    }

    /**
     * Méthode qui met fin au serveur.
     *
     * @throws IOException si une erreur survient lors de la fermeture de l'ObjectOutputStream, de l'ObjectInputStream
     * ou du Socket.
     */
    public void disconnect() throws IOException {
        objectOutputStream.close();
        objectInputStream.close();
        client.close();
    }

    /**
     * Méthode qui applique le gestionnaire d'événement associé à une certaine commande.
     *
     * @param cmd La commande à traiter.
     * @param arg L'argument associé à la commande.
     */
    public void handleEvents(String cmd, String arg) {
        if (cmd.equals(REGISTER_COMMAND)) {
            handleRegistration();
        } else if (cmd.equals(LOAD_COMMAND)) {
            handleLoadCourses(arg);
        }
    }


    /**
     * Méthode qui, depuis un fichier texte contenant des informations sur les cours, retourne au client une liste des
     * cours offerts pour une session spécifique.
     *
     * @param arg La session pour laquelle on veut récupérer la liste des cours.
     */
    public void handleLoadCourses(String arg) {
        /*
         * Utilisation d'un "try-with-resources", donc un try qui comprend une ressource entre parentheses qui assure
         * la fermeture de la ressource peu importe si le try se termine normalement ou non (agit un peu comme le
         * "finally", mais plus court, plus propre et automatique)
         * SOURCE: https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html
         * -> Pas besoin d'inclure: "reader.close();", car fermeture automatique à la sortie du try.
         */
        try (BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.dir")
                + "/cours.txt"))){

            // Verifier si le fichier est vide (ne contient rien du tout)
            if (reader.readLine() == null) {
                System.out.print("\nERREUR: Le fichier cours.txt est vide.");
                throw new EOFException("Le fichier cours.txt est vide");
            }

            // Supposer que le fichier est vide de caracteres:
            boolean courseFileIsEmpty = true;

            // Le fichier cours.txt doit se retrouver dans le même dossier que le jar
            List<Course> courses = new ArrayList<>();

            // Pour tous les cours disponibles:
            String line;
            while ((line = reader.readLine()) != null) {

                // Sauter les lignes vides
                if (line.trim().isBlank()){
                    continue;
                } else { // Ligne non-vide, donc fichier non-vide
                    courseFileIsEmpty = false;
                }

                String[] parts = line.split("\t");

                // Verifier le format de 'cours.txt'
                for (String part : parts) {
                    if (parts.length != 3) {
                        System.out.println("\nERREUR: Le fichier cours.txt ne respecte pas le bon format à la ligne:");
                        System.out.println("'" + line + "'");
                        System.out.println("-> Chaque ligne doit contenir trois sections séparés par des tabulations");
                        throw new IllegalArgumentException("Ligne mal formatée dans 'cours.txt': " + line);
                    }
                    if (part.contains(" ")) {
                        System.out.println("\nERREUR: Le fichier cours.txt ne respecte pas le bon format à la ligne:");
                        System.out.println("'" + line + "'");
                        System.out.println("-> Le contenu des trois sections ne devrait pas contenir d'espaces." +
                                           " Remplacez-les par des '_'.");
                        throw new IllegalArgumentException("Ligne mal formatée dans 'cours.txt': " + line);
                    }
                }

                // Filtre: conserver seulement les cours de la session specifiee dans le 'arg'
                if (parts[2].equals(arg)) {
                    Course course = new Course(parts[1], parts[0], parts[2]);
                    courses.add(course);
                }
            }

            if (courseFileIsEmpty) {
                System.out.print("\nERREUR: Le fichier cours.txt est vide.");
                throw new EOFException("Le fichier cours.txt est vide");
            } else {
                objectOutputStream.writeObject(courses);
                objectOutputStream.flush();
                objectOutputStream.close();
            }

        } catch (FileNotFoundException e) {
            System.out.println("\nERREUR: Le fichier cours.txt qui contient la liste des cours est manquant.\n");
            System.out.println("Veuillez vous assurer que le fichier est bien placé dans " +
                                "\nle même répertoire que le server.jar avant d'exécuter le serveur.\n");
        } catch (IOException e) { // Traite aussi EOFException
            System.out.println("\nERREUR: Une erreur est survenue lors de l'entrée ou la sortie de données.\n");
        } catch (IllegalArgumentException e) {
            System.out.println("Veuillez corriger le format du fichier.\n");
        }
    }


    /**
     * Méthode qui, depuis un formulaire d'inscription envoyé par le client, prend en note l'inscription du client dans
     * un fichier texte, puis renvoie un message de confirmation au client.
     */
    public void handleRegistration() {
        /*
        * Utilisation d'un "try-with-resources" expliqué dans la méthode précédante (handleLoadCourses).
        * -> Pas besoin d'inclure: "fileWriter.close();", car fermeture automatique à la sortie du try.
        */
        try(BufferedWriter fileWriter = new BufferedWriter(new FileWriter(System.getProperty("user.dir")
                + "/inscription.txt", true))) {

            RegistrationForm registrationForm = (RegistrationForm) objectInputStream.readObject();

            // Le fichier inscription.txt doit se retrouver dans le même dossier que le jar
            // Transferer les informations d'inscriptions dans le fichier inscription.txt:
            fileWriter.write(registrationForm.getCourse().getSession() + "\t" +
                         registrationForm.getCourse().getCode() + "\t" +
                         registrationForm.getMatricule() + "\t" +
                         registrationForm.getPrenom() + "\t" +
                         registrationForm.getNom() + "\t" +
                         registrationForm.getEmail() + "\n");
            
            objectOutputStream.writeObject("Félicitations! Inscription réussie de " + registrationForm.getPrenom() +
                                            " au cours " + registrationForm.getCourse().getCode() + ".");
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (IOException e) {
            System.out.println("\nERREUR: Une erreur est survenue lors de l'entrée ou la sortie de données.\n");
        } catch (ClassNotFoundException e) {
            System.out.println("\nERREUR: Une erreur est survenue lors de la réception des données: " +
                                "classe introuvable.\n");
        }
    }
}
