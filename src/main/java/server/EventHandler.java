package server;

/**
 * Interface fonctionnelle associée à la gestion d'événements. Elle contient une unique méthode <code>handle</code> qui
 * s'occupe de la gestion d'un événement.
 */
@FunctionalInterface
public interface EventHandler {
    /**
     * Méthode unique de l'interface fonctionnelle <code>EventHandler</code> qui est appelé lorsqu'un événement, composé
     * d'une commande et de son argument, doit être géré.
     *
     * @param cmd La commande associée à l'événement.
     * @param arg L'argument associé à la commande.
     */
    void handle(String cmd, String arg);
}
