package client;

import server.models.Course;
import server.models.RegistrationForm;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class Client {
    public static final String SERVER_IP = "localhost";
    public static final int SERVER_PORT = 1337;

    public static void main(String[] args) {
        try {
            //Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            //ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            //ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            try (Scanner scanner = new Scanner(System.in)) {
                System.out.println("*** Bienvenue au portail d'inscription de cours de l'UDEM ***");
                while (true) {
                    System.out.println("1. Consulter les cours offerts pour une session");
                    System.out.println("2. Inscription à un cours");
                    System.out.println("3. Quitter");
                    System.out.print("> Choix: ");
                    int choice = scanner.nextInt();
                    scanner.nextLine();

                    if (choice == 1) {
                        System.out.print("Entrez la session pour laquelle vous voulez consulter la liste des cours (Automne, Hiver, Ete): ");
                        String session = scanner.nextLine();

                        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                             ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())) {

                            objectOutputStream.writeObject(server.Server.LOAD_COMMAND + " " + session);
                            List<Course> courses = (List<Course>) objectInputStream.readObject();

                            System.out.println("Les cours offerts pendant la session " + session + " sont:");
                            for (Course course : courses) {
                                System.out.println(course.getCode() + " " + course.getName());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (choice == 2) {
                        System.out.print("Veuillez saisir votre prénom: ");
                        String prenom = scanner.nextLine();
                        System.out.print("Veuillez saisir votre nom: ");
                        String nom = scanner.nextLine();
                        System.out.print("Veuillez saisir votre email: ");
                        String email = scanner.nextLine();
                        System.out.print("Veuillez saisir votre matricule: ");
                        String matricule = scanner.nextLine();
                        System.out.print("Veuillez saisir le code du cours: ");
                        String courseCode = scanner.nextLine();

                        RegistrationForm registrationForm = new RegistrationForm(prenom, nom, email, matricule, new Course("", courseCode, ""));
                        //objectOutputStream.writeObject(server.Server.REGISTER_COMMAND);
                        //objectOutputStream.writeObject(registrationForm);

                        //String message = (String) objectInputStream.readObject();
                        //System.out.println(message);
                        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                             ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())) {

                            objectOutputStream.writeObject(server.Server.REGISTER_COMMAND);
                            objectOutputStream.writeObject(registrationForm);

                            String message = (String) objectInputStream.readObject();
                            System.out.println(message);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (choice == 3) {
                        break;
                    } else {
                        System.out.println("Choix invalide.");
                    }
                }
            }

            //objectOutputStream.close();
            //objectInputStream.close();
            //socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


