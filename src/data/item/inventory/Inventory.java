package data.item.inventory;

import java.util.ArrayList;

import data.item.Equipment;

public class Inventory {
    private ArrayList<Equipment> equipment;

    public Inventory() {
        equipment = new ArrayList<>(); 
    }

    public Equipment getEquipmentAt(int index) {
        if (index >= 0 && index < equipment.size()) {
            return equipment.get(index); 
        }
        return null; 
    }

    public boolean isFull() {
        return equipment.size() >= 4; 
    }

    public int size() {
        return equipment.size();
    }

    public ArrayList<Equipment> getEquipments() {
        return equipment; 
    }

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
    
    public void addEquipment(Equipment eq) {
        if (eq != null && equipment.size() < 4) { 
            equipment.add(eq);
        }
    }

	
}
