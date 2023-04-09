package server;

/**
 * Interface fonctionnelle associé à la gestion d'événements, contient une unique méthode <code>handle</code> qui
 * prends en paramètre deux <code>String</code> représentant la commande 'cmd' et l'argument 'arg' associé à cette
 * commande.
 */
@FunctionalInterface
public interface EventHandler {
    void handle(String cmd, String arg);
}
