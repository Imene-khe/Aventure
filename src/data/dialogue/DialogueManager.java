package data.dialogue;

import java.util.HashMap;

public class DialogueManager {

	private  HashMap<String, String[]> dialoguesByEvent = new HashMap<>();
	private  HashMap<String, Integer> dialogueIndexByEvent = new HashMap<>();
    
	public DialogueManager() {
        dialoguesByEvent.put("intro", new String[] {"Bienvenue, Ghaya ! Une nouvelle journée resplandissante s'annonce.",
            "Tu n'es pas au courant de la dernière nouvelle?",
            "Un marchand a établi son camp dans une nouvelle maison de bois claire non loin d'ici.",
            "Seul problème, il a fait tomber sa bourse de pièces en cherchant un lieu pour loger.",
            "La nouvelle de ce butin perdu à rametter des monstres du Royaume des Tenèbres.",
            "Ton rôle aujourd'hui est donc de récuperer ces pièces pour les rendre au marchand.",
            " et qui sait il t'offrira peut-être quelque chose en récompense..."
        });
     // Dialogue déclenché à la première sortie de la boutique
        dialoguesByEvent.put("exit_shop_1", new String[] {
            "Le village est en flammes !",
            "Dépêche-toi, il faut sauver les survivants !"
        });
    }

	public boolean hasDialogue(String event) {
        return dialoguesByEvent.containsKey(event);
    }

    public String getCurrent(String event) {
        if (!hasDialogue(event)) return null;

        int index = dialogueIndexByEvent.getOrDefault(event, 0);
        String[] dialogues = dialoguesByEvent.get(event);

        if (index >= dialogues.length) return null;
        return dialogues[index];
    }

    public boolean hasNext(String event) {
        if (!hasDialogue(event)) return false;

        int index = dialogueIndexByEvent.getOrDefault(event, 0);
        return index < dialoguesByEvent.get(event).length - 1;
    }

    public void next(String event) {
        if (hasDialogue(event)) {
            int index = dialogueIndexByEvent.getOrDefault(event, 0);
            if (index < dialoguesByEvent.get(event).length - 1) {
                dialogueIndexByEvent.put(event, index + 1);
            }
        }
    }

    public void reset(String event) {
        dialogueIndexByEvent.put(event, 0);
    }

    public boolean isFinished(String event) {
        return !hasNext(event);
    }
}