package data.item;

public class Chest {

    private Inventory inventory; // Inventaire du coffre
    private boolean opened; // Indique si le coffre a été ouvert

    /**
     * Constructeur du coffre initialisé avec quelques objets aléatoires.
     */
    public Chest() {
        this.inventory = new Inventory(); // Création d'un inventaire pour stocker les objets
        this.opened = false;
        // Exemple : Ajout d'objets par défaut
        inventory.addEquipment(new Equipment("Potion"));
        inventory.addEquipment(new Equipment("Épée"));
    }

    /**
     * Ouvre le coffre et retourne son contenu sous forme d'inventaire.
     * @return L'inventaire du coffre si ce n'est pas déjà fait, sinon un inventaire vide.
     */
    public Inventory open() {
        if (!opened) {
            opened = true;
            Inventory loot = inventory; // Récupération du contenu du coffre
            inventory = new Inventory(); // Vider le coffre en recréant un inventaire vide
            return loot;
        }
        return new Inventory(); // Retourne un inventaire vide si le coffre a déjà été ouvert
    }

    /**
     * Vérifie si le coffre a été ouvert.
     * @return true si le coffre a été ouvert, sinon false.
     */
    public boolean isOpened() {
        return opened;
    }

    /**
     * Ajoute un équipement au coffre.
     * @param item L'équipement à ajouter au coffre.
     */
    public void addItem(Equipment item) {
        inventory.addEquipment(item);
    }

    /**
     * Retourne l'inventaire du coffre.
     * @return L'inventaire du coffre.
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Vide le coffre.
     */
    public void clearItems() {
        inventory = new Inventory(); // Réinitialise l'inventaire du coffre
    }

    // === Main interne pour tester la classe ===
    public static void main(String[] args) {
        System.out.println("=== Test de la classe Chest ===");

        // Création d'un coffre
        Chest chest = new Chest();
        System.out.println("Coffre créé avec les objets : " + chest.getInventory().size());

        // Ajout d'objets au coffre
        chest.addItem(new Equipment("Bouclier"));
        chest.addItem(new Equipment("Arc"));
        System.out.println("Objets après ajout : " + chest.getInventory().size());

        // Vérification de l'état du coffre
        System.out.println("Le coffre est-il ouvert ? " + chest.isOpened());

        // Ouverture du coffre
        Inventory loot = chest.open();
        System.out.println("Contenu du coffre lors de l'ouverture : " + loot.size());

        // Vérification après ouverture
        System.out.println("Le coffre est-il ouvert ? " + chest.isOpened());
        System.out.println("Contenu du coffre après ouverture : " + chest.getInventory().size());

        // Tentative d'ouverture d'un coffre déjà ouvert
        Inventory emptyLoot = chest.open();
        System.out.println("Tentative de réouverture : " + emptyLoot.size());

        // Nettoyage du coffre
        chest.clearItems();
        System.out.println("Contenu du coffre après nettoyage : " + chest.getInventory().size());
    }
}
