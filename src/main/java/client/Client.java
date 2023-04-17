package client;

// Ces deux classes doivent appartenir au projet du client, nous les avons pris depuis le server au lieu de dupliquer
import server.models.Course;
import server.models.RegistrationForm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Client {
    public final static String SERVER_IP = "127.0.0.1";
    public final static int SERVER_PORT = 1337;
    public final static String LOAD_COMMAND = "CHARGER";
    public final static String REGISTER_COMMAND = "INSCRIRE";


    public static void main(String[] args) {

        /*
         * Utilisation d'un "try-with-resources", donc un try qui comprend une ressource entre parentheses qui assure
         * la fermeture de la ressource peu importe si le try se termine normalement ou non (agit un peu comme le
         * "finally", mais plus court, plus propre et automatique)
         * SOURCE: https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html
         */

        try (Scanner scanner = new Scanner(System.in)) {

            System.out.println("*** Bienvenue au portail d'inscription de cours de l'UDEM ***");

            /*
             * Utilisation de deux "Labeled Loop" afin de pouvoir sauter a des points precis pour pouvoir repeter le
             * code depuis ces points
             * SOURCE: https://www.javatpoint.com/labeled-loop-in-java
             */

            // Boucle qui assure que l'utilisateur entre un choix valide pour la session:
            sessionLoop:
            while (true) {
                System.out.println("Veuillez choisir la session pour laquelle vous voulez consulter la liste des " +
                                    "cours:");
                System.out.println("1. Automne\n2. Hiver\n3. Ete");
                System.out.print("> Choix: ");

                int choice;
                choice = scanner.nextInt();
                scanner.nextLine();

                String session;
                switch (choice) {
                    case 1:
                        session = "Automne";
                        break;
                    case 2:
                        session = "Hiver";
                        break;
                    case 3:
                        session = "Ete";
                        break;
                    default:
                        System.out.println("\n-> Choix invalide. Veuillez entrez un choix valide:\n");
                        continue sessionLoop;
                }

                // Creer une connection avec le serveur (on utilise try-catch pour assurer la fermeture des ressources)
                // pour obtenir la liste des cours disponibles:
                List<Course> courses;
                ArrayList<String> courseCodeList = new ArrayList<>();
                try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                     ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                     ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())) {
                    // Envoyer une requete "charger" au serveur pour recevoir une liste des cours disponibles
                    objectOutputStream.writeObject(LOAD_COMMAND + " " + session);
                    courses = (List<Course>) objectInputStream.readObject();

                    // Afficher les cours offerts (et sauvegarder le code des cours)
                    System.out.println("Les cours offerts pendant la session " + session + " sont:");
                    int i = 1;
                    for (Course course : courses) {
                        System.out.println(i + ". " + course.getCode() + "\t" + course.getName());
                        courseCodeList.add(course.getCode());
                        i++;
                    }
                } catch (ConnectException e) {
                    throw e;                                // L'exception est traite dans le catch externe
                } catch (UnknownHostException e) {
                    throw e;                                // L'exception est traite dans le catch externe
                } catch (IOException e) {
                    throw e;                                // L'exception est traite dans le catch externe
                } catch (ClassNotFoundException e) {
                    throw e;                                // L'exception est traite dans le catch externe
                }

                //---------------------------------------------------------------------------------------------//

                // Boucle qui assure que l'utilisateur entre un choix valide
                secondChoiceLoop:
                while (true) {

                    System.out.println("> Choix: ");
                    System.out.println("1. Consulter les cours offerts pour une autre session");
                    System.out.println("2. Inscription à un cours");
                    System.out.print("> Choix: ");

                    choice = scanner.nextInt();
                    scanner.nextLine();

                    if (choice != 1 && choice != 2) {
                        System.out.println("\n-> Choix invalide. Veuillez entrez un choix valide:\n");
                        continue secondChoiceLoop;

                    } else if (choice == 1) {
                        // retour au choix de la session (retour au labeled loop: sessionLoop)
                        continue sessionLoop;

                    } else { // si (choice == 2): inscription
                        System.out.print("Veuillez saisir votre prénom: ");
                        String firstName = scanner.nextLine();
                        System.out.print("Veuillez saisir votre nom: ");
                        String lastName = scanner.nextLine();
                        System.out.print("Veuillez saisir votre email: ");
                        String email = scanner.nextLine();
                        System.out.print("Veuillez saisir votre matricule: ");
                        String matricule = scanner.nextLine();
                        System.out.print("Veuillez saisir le code du cours: ");
                        String courseCode = scanner.nextLine();

                        if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || matricule.isBlank()
                                || courseCode.isBlank()) {
                            throw new IllegalArgumentException("Input is empty");

                        } else if (!courseCodeList.contains(courseCode)) {
                            // Si le cours n'appartient pas a la liste de cours:
                            throw new NoSuchElementException("Class not given during this semester");

                        } else {
                            // Creer une connection avec le serveur, on utilise try-catch pour assurer la fermeture des
                            // ressources
                            try (Socket socket2 = new Socket(SERVER_IP, SERVER_PORT);
                                 ObjectOutputStream objectOutputStream2 = new ObjectOutputStream(
                                         socket2.getOutputStream());
                                 ObjectInputStream objectInputStream2 = new ObjectInputStream(
                                         socket2.getInputStream())) {

                                RegistrationForm registrationForm = new RegistrationForm(firstName, lastName,
                                        email, matricule, new Course("", courseCode, session));

                                objectOutputStream2.writeObject(REGISTER_COMMAND);
                                objectOutputStream2.writeObject(registrationForm);

                                String message = (String) objectInputStream2.readObject();
                                System.out.println(message);
                            } catch (ConnectException e) {
                                throw e;                                // L'exception est traite dans le catch externe
                            } catch (UnknownHostException e) {
                                throw e;                                // L'exception est traite dans le catch externe
                            } catch (IOException e) {
                                throw e;                                // L'exception est traite dans le catch externe
                            } catch (ClassNotFoundException e) {
                                throw e;                                // L'exception est traite dans le catch externe
                            }
                        }
                    }
                    break; // sortir du "secondChoiceLoop"
                }
                break; // sortir du "sessionLoop"
            }
        } catch (IllegalArgumentException e) {
            System.out.println("\n*** ERREUR: Inscription échoué ***");
            System.out.println("*** CAUSE: champ(s) manquant(s) ***");
        } catch (NoSuchElementException e) {
            System.out.println("\n*** ERREUR: Inscription échoué ***");
            System.out.println("*** CAUSE: cours demandé est indisponnible durant cette session");
        } catch (ConnectException e) {
            System.out.println("\nERREUR: Le serveur est actuellement indisponible. " +
                    "\nVeuillez réessayer plus tard.");
        } catch (UnknownHostException e) {
            System.out.println("\nERREUR: Nom d'hôte inconnu. " +
                    "\nVeuillez vérifier que vous avez la bonne adresse IP.");
        } catch (IOException e) {
            System.out.println("\nERREUR: Une erreur est survenue lors de la communication avec le serveur. " +
                    "\nVeuillez réessayer plus tard.");
        } catch (ClassNotFoundException e) {
            System.out.println("\nERREUR: Une erreur est survenue lors de la réception des données: " +
                    "classe introuvable.");
        }
    }
}
