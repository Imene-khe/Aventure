package data.item;

import java.util.ArrayList;

public class Inventory {
    private ArrayList<Equipment> equipment;

    // Constructeur pour initialiser l'inventaire (liste vide)
    public Inventory() {
        equipment = new ArrayList<>(); // Initialisation de la liste vide
    }

    // Ajouter un équipement dans l'inventaire
    /*public void addEquipment(Equipment item) {
        equipment.add(item); // Ajoute l'équipement à la fin de la liste
        
    }*/

    // Récupérer un équipement à un indice donné
    public Equipment getEquipmentAt(int index) {
        if (index >= 0 && index < equipment.size()) {
            return equipment.get(index); // Retourne l'équipement à l'indice spécifié
        }
        return null; // Retourne null si l'indice est invalide
    }

    // Vérifier si l'inventaire est plein (optionnel, dépend de tes besoins)
    public boolean isFull() {
        return equipment.size() >= 4; // Par exemple, considérer l'inventaire comme "plein" lorsqu'il contient 4 objets
    }

    // Retourner la taille actuelle de l'inventaire
    public int size() {
        return equipment.size();
    }

    // Méthode pour récupérer la liste d'équipements
    public ArrayList<Equipment> getEquipments() {
        return equipment; // Retourne la liste complète des équipements
    }

    // Méthode pour afficher le contenu de l'inventaire
    @Override
    public String toString() {
        if (equipment.isEmpty()) {
            return "Inventaire vide.";
        }
        StringBuilder sb = new StringBuilder("Contenu de l'inventaire :\n");
        for (int i = 0; i < equipment.size(); i++) {
            sb.append("- ").append(equipment.get(i).toString()).append("\n");
        }
        return sb.toString();
    }

    // === Main interne pour tester l'affichage ===
    public static void main(String[] args) {
        Inventory inventory = new Inventory();

        System.out.println("=== Test de l'affichage de l'inventaire ===");
        System.out.println(inventory); // Devrait afficher "Inventaire vide."

        // Ajout d'objets
        inventory.getEquipments().add(new Equipment("Épée"));
        inventory.getEquipments().add(new Equipment("Bouclier"));
        inventory.getEquipments().add(new Equipment("Potion"));

        // Affichage après ajout
        System.out.println(inventory);
    }

    public void addEquipment(Equipment item) {
        if (item != null) {
            equipment.add(item);
        }
    }

    public void removeEquipment(Equipment item) {
        equipment.remove(item);
    }

	
}
