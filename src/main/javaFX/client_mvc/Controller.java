package client_mvc;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
// Ces deux classes doivent appartenir au projet du client, nous les avons pris depuis le server au lieu de dupliquer:
import server.models.Course;
import server.models.RegistrationForm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Classe qui s'occupe de la logique de l'application. Elle gère les interactions entre la vue et les modèles, ainsi que
 * les interactions entre le client et le serveur.
 */
public final class Controller {

    /**
     * Constante qui représente l'adresse IP du serveur auquel se connecter.
     */
    public final static String SERVER_IP = "127.0.0.1";

    /**
     * Constante qui représente le port du serveur auquel se connecter.
     */
    public final static int SERVER_PORT = 1337;

    /**
     * Constante reliée à la gestion d'événements, qui contient une des commandes dont le programme s'attend à recevoir.
     */
    public final static String LOAD_COMMAND = "CHARGER";

    /**
     * Constante reliée à la gestion d'événements, qui contient une des commandes dont le programme s'attend à recevoir.
     */
    public final static String REGISTER_COMMAND = "INSCRIRE";

    private final View view;

    /**
     * Constructeur de la classe <code>Controller</code> qui s'occupe de la gestion des événements entre la vue et les
     * modèles (également le serveur) de l'application.
     *
     * @param view Une instance de la classe <code>View</code> du modèle MVC de l'application.
     * @param course Une instance de la classe <code>Course</code>.
     * @param regForm Une instance de la classe <code>RegistrationForm</code>.
     */
    public Controller(View view, Course course, RegistrationForm regForm) {
        this.view = view;

        // Evenement du bouton "charger"
        view.getLoadButton().setOnAction((action) -> {
            String session = this.getSelectedSession();
            List<Course> availableCourses = this.getAllCourse(session);
            // Afficher les cours seulement si le client a reussis a obtenir les cours
            if (availableCourses == null) {
                action.consume();
            } else {
                displayAvailableCourses(availableCourses);
            }
        });

        // Evenement du bouton "envoyer"
        view.getSubmitButton().setOnAction((action) -> {
            String errorString = "";

            String courseCode = this.getSelectedCourseCode();
            String session = this.getSelectedSession();
            String firstName = this.getFirstName();
            String lastName = this.getLastName();
            String registrationNumber = this.getRegistrationNumber();
            String email = this.getEmail();

            // Verifier si un cours a ete selectionne -----------------------------------------------//
            if (courseCode == null) {
                errorString += "\n-> Vous devez sélectionner un cours!";
                view.getTableView().setStyle("-fx-border-color: red;" +
                                             "-fx-border-radius: 2px");     // Bordure rouge = invalide
            } else {
                view.getTableView().setStyle(null);                         // Retirer le rouge si corrige
            }

            // Verifier si le prenom est valide (regex source: https://regexr.com/3f8cm) ------------//
            if (firstName.isBlank() || !firstName.matches("\\b([A-ZÀ-ÿ][-,a-z. ']+[ ]*)+")){
                errorString += "\n-> Le champ 'Prénom' est invalide!";
                view.getFirstNameField().setStyle("-fx-border-color: red;" +
                                                  "-fx-border-radius: 2px");// Bordure rouge = invalide
            } else {
                view.getFirstNameField().setStyle(null);                    // Retirer le rouge si corrige
            }

            // Verifier si le nom est valide (regex source: https://regexr.com/3f8cm) ---------------//
            if (lastName.isBlank() || !lastName.matches("\\b([A-ZÀ-ÿ][-,a-z. ']+[ ]*)+")) {
                errorString += "\n-> Le champ 'Nom' est invalide!";
                view.getLastNameField().setStyle("-fx-border-color: red;" +
                                                 "-fx-border-radius: 2px"); // Bordure rouge = invalide
            } else {
                view.getLastNameField().setStyle(null);                     // Retirer le rouge si corrige
            }

            // Verifier si l'adresse courriel est valide (regex source: https://regexr.com/3e48o) ---//
            if (email.isBlank() || !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                errorString += "\n-> Le champ 'Email' est invalide!";
                view.getEmailField().setStyle("-fx-border-color: red;" +
                                              "-fx-border-radius: 2px");    // Bordure rouge = invalide
            } else {
                view.getEmailField().setStyle(null);                        // Retirer le rouge si corrige
            }

            // Verifier si le matricule est valide (regex source: Piazza @117) ----------------------//
            if (registrationNumber.isBlank() || !registrationNumber.matches("^[0-9]{8}$")) {
                errorString += "\n-> Le champ 'Matricule' est invalide!";
                view.getRegistrationNumberField().setStyle("-fx-border-color: red;" +
                                                "-fx-border-radius: 2px");  // Bordure rouge = invalide
            } else {
                view.getRegistrationNumberField().setStyle(null);           // Retirer le rouge si corrige
            }

            // S'il y a une erreur, alors la personne ne peut pas s'inscrire au cours --------------//
            if (!errorString.equals("")) {
                view.showError("Le formulaire est invalide." + errorString);
                action.consume(); //Arreter l'evenement
            } else {
                // "registrationSuccess" est true si inscription reussi, sinon, il y a eu une exception
                course.setCode(courseCode);
                course.setName(getSelectedCourseName());
                course.setSession(session);
                regForm.setPrenom(firstName);
                regForm.setNom(lastName);
                regForm.setEmail(email);
                regForm.setMatricule(registrationNumber);
                regForm.setCourse(course);
                String registrationSuccess = this.registration(regForm);

                // S'il y a eu une exception durant l'inscription → Arreter l'evenement
                if (registrationSuccess == null) {
                    action.consume();
                } else { // Inscription avec succes
                    view.getFirstNameField().clear();
                    view.getLastNameField().clear();
                    view.getEmailField().clear();
                    view.getRegistrationNumberField().clear();

                    // On peut aussi reutiliser le message obtenu par le serveur, mais le format ci-dessous est meilleur
                    view.showMessage("Félicitation! " + lastName + " " + firstName + " est inscrit(e)" +
                                     "\navec succès pour le cours " + courseCode + "!");
                }
            }
        }); // fin de l'evenement du boutton "envoyer"
    }


    //================================================================================================================//
    //----------------------- Methodes reliant la vue aux modeles (Course et RegistrationForm) -----------------------//


    // Retourne "null" s'il y a une exception. Sinon, retourne une liste des cours disponibles pour la session
    private List<Course> getAllCourse(String session) {
        try(Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())){

            objectOutputStream.writeObject(LOAD_COMMAND + " " + session);

            return (List<Course>) objectInputStream.readObject();

        } catch (ConnectException e) {
            this.view.showError("ERREUR: Le serveur est actuellement indisponible. " +
                    "\nVeuillez réessayer plus tard.");
        } catch (UnknownHostException e) {
            this.view.showError("ERREUR: Nom d'hôte inconnu. " +
                    "\nVeuillez vérifier que vous avez la bonne adresse IP.");
        } catch (IOException e) {
            this.view.showError("ERREUR: Une erreur est survenue lors de la " +
                    "\ncommunication avec le serveur."
                    + "\nVeuillez réessayer plus tard.");
        } catch (ClassNotFoundException e) {
            this.view.showError("ERREUR: Une erreur est survenue lors de la " +
                    "\nréception des données: " +
                    "\nClasse introuvable.");
        }

        return null;
    }

    private void displayAvailableCourses(List<Course> listOfCourses) {

        ObservableList<ObservableList<String>> rows = FXCollections.observableArrayList();
        for (Course course : listOfCourses) {
            String code = course.getCode();
            // SOURCE pour "replace": https://www.w3schools.com/java/ref_string_replace.asp
            String name = course.getName().replace('_', ' ');      // remplacer les '_' par des ' '
            rows.add(FXCollections.observableArrayList(code, name));
        }
        this.view.getTableView().setItems(rows);
    }

    private String registration(RegistrationForm registrationForm) {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())) {

            objectOutputStream.writeObject(REGISTER_COMMAND);
            objectOutputStream.writeObject(registrationForm);

            return (String) objectInputStream.readObject(); // Aucune exception

        } catch (ConnectException e) {
            this.view.showError("ERREUR: Le serveur est actuellement indisponible. " +
                    "\nVeuillez réessayer plus tard.");
        } catch (UnknownHostException e) {
            this.view.showError("ERREUR: Nom d'hôte inconnu. " +
                    "\nVeuillez vérifier que vous avez la bonne adresse IP.");
        } catch (IOException e) {
            this.view.showError("ERREUR: Une erreur est survenue lors de la " +
                    "\ncommunication avec le serveur."
                    + "\nVeuillez réessayer plus tard.");
        } catch (ClassNotFoundException e) {
            this.view.showError("ERREUR: Une erreur est survenue lors de la " +
                    "\nréception des données: " +
                    "\nClasse introuvable.");
        }
        return null; // Il y a eu une exception durant l'inscription
    }

    private String getSelectedCourseCode() { return this.view.getSelectedRow()[0]; }
    private String getSelectedCourseName() { return this.view.getSelectedRow()[1]; }
    private String getSelectedSession() { return this.view.getSelectedSemester(); }
    private String getFirstName() { return this.view.getFirstNameField().getText(); }
    private String getLastName() { return this.view.getLastNameField().getText(); }
    private String getEmail() { return this.view.getEmailField().getText(); }
    private String getRegistrationNumber() { return this.view.getRegistrationNumberField().getText(); }

}
