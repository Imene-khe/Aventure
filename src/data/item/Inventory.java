package data.item;

import java.util.ArrayList;

public class Inventory {
    private ArrayList<Equipment> equipment;

    // Constructeur pour initialiser l'inventaire (liste vide)
    public Inventory() {
        equipment = new ArrayList<>(); // Initialisation de la liste vide
    }

    // Ajouter un équipement dans l'inventaire
    public void addEquipment(Equipment item) {
        equipment.add(item); // Ajoute l'équipement à la fin de la liste
    }

    // Récupérer un équipement à un indice donné
    public Equipment getEquipmentAt(int index) {
        if (index >= 0 && index < equipment.size()) {
            return equipment.get(index); // Retourne l'équipement à l'indice spécifié
        }
        return null; // Retourne null si l'indice est invalide
    }

    // Vérifier si l'inventaire est plein (optionnel, dépend de vos besoins)
    public boolean isFull() {
        return equipment.size() >= 4; // Par exemple, considérer l'inventaire comme "plein" lorsqu'il contient 4 objets
    }

    // Retourner la taille actuelle de l'inventaire
    public int size() {
        return equipment.size();
    }
}
