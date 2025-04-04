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

        
        dialoguesByEvent.put("enter_shop", new String[] {
        	    "Bienvenue dans ma boutique, jeune aventurier !",
        	    "Que puis-je faire pour toi ?!"
        });
        
        dialoguesByEvent.put("exit_shop_1", new String[] {
                "Ahhhh te revoilà... Nous te cherchions !",
                "Une horde de monstres de passage a entendu parler de toi et du fait que tu es trouvé les pièces.",
                "En apprenant ce que tu avais fait, ils nous ont attaqués et se sont venger sur nos maisons !",
                "Vite trouve un seau et va éteindre les maisons en feu ! Dépêche toi !"
        });
        
        dialoguesByEvent.put("enter_shop_give_gold", new String[] {
        	    "Ohhh... c’est donc toi qui m’a rapporté cette bourse !",
        	    "Tu n’imagines pas à quel point tu me sauves.",
        	    "Tiens, prends ceci en remerciement !"
        });

        dialoguesByEvent.put("enter_shop_chat", new String[] {
        	    "Eh bien oui, je viens tout juste de m’installer.",
        	    "Mais avec tous ces monstres dehors, ce n’est pas de tout repos !",
        	    "D'ailleurs, n'aurais-tu pas vu une bourse remplie d'or ?",
        	    "Je crois qu'elle est tombé de ma charette pendant le voyage..."
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