package data;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import data.*;


public class Inventory {
	private ArrayList<Equipment> inventory;
	
	public Inventory() {
		inventory = new ArrayList<Equipment>(4);
		
	}
	
	public boolean addEquipment(Equipment e) {
		if(inventory.size()>4) {
			inventory.add(e);
			return true;	
		}
		return false;
	}
	
	public boolean removeItem(int index) {
        if (index >= 0 && index < inventory.size()) {
            inventory.remove(index);
            return true;
        }
        return false;
    }
	
	public Equipment getEquipmentAt(int index) {
        if (index >= 0 && index < inventory.size()) {
            return inventory.get(index);
        }
        return null;
    }
	/*public static void main(String[] args) {
		JFrame frame = new JFrame("inventory");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        Inventory inventory = new Inventory();
        JPanel inventoryPanel = new JPanel();
        inventoryPanel.setLayout(new GridLayout(1,5));
        inventoryPanel.add()args;
        
        frame.add(inventoryPanel, BorderLayout.CENTER);
        frame.setVisible(true); 
	}*/
	
}

