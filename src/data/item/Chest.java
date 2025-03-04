package data.item;

import java.util.ArrayList;

/**
 * Classe représentant un coffre qui contient des objets.
 */
public class Chest {

    private ArrayList<String> items; // Liste des objets contenus dans le coffre
    private boolean opened; // Indique si le coffre a été ouvert

    /**
     * Constructeur du coffre initialisé avec quelques objets aléatoires.
     */
    public Chest() {
        this.items = new ArrayList<>();
        this.opened = false;
        // Exemple : Ajout d'objets par défaut (pour l'exemple, vous pouvez changer cela)
        items.add("Potion");
        items.add("Épée");
    }

    /**
     * Ouvre le coffre et retourne son contenu si ce n'est pas déjà fait.
     * @return Liste des objets trouvés ou un message indiquant qu'il est vide.
     */
    public ArrayList<String> open() {
        if (!opened) {
            opened = true;
            ArrayList<String> loot = new ArrayList<>(items); // Copie des objets dans le coffre
            items.clear(); // Vider le coffre après l'ouverture
            return loot;
        }
        ArrayList<String> emptyLoot = new ArrayList<>();
        emptyLoot.add("Coffre vide");
        return emptyLoot;
    }


    /**
     * Vérifie si le coffre a été ouvert.
     * @return true si le coffre a été ouvert, sinon false.
     */
    public boolean isOpened() {
        return opened;
    }

    /**
     * Ajoute un objet au coffre.
     * @param item L'objet à ajouter au coffre.
     */
    public void addItem(String item) {
        items.add(item);
    }

    /**
     * Retourne la liste des objets du coffre.
     * @return Liste des objets présents dans le coffre.
     */
    public ArrayList<String> getItems() {
        return new ArrayList<>(items);
    }

    /**
     * Vide le coffre (par exemple, après qu'un joueur l'ait ouvert et pris son contenu).
     */
    public void clearItems() {
        items.clear();
    }
}
