package server;

/**
 * Classe responsable au lancement du serveur.
 */
public class ServerLauncher {
    /**
     * Constante qui représente le port sur lequel le serveur va attendre les connexions entrantes.
     */
    public final static int PORT = 1337;

    /**
     * Classe qui lance le serveur sur le port spécifié.
     *
     * @param args Les arguments de la ligne de commande.
     */
    public static void main(String[] args) {
        Server server;
        try {
            server = new Server(PORT);
            System.out.println("Server is running...");
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}