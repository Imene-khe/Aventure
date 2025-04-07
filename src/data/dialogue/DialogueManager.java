package data.dialogue;

import java.util.HashMap;

public class DialogueManager {

	private  HashMap<String, String[]> dialoguesByEvent = new HashMap<>();
	private  HashMap<String, Integer> dialogueIndexByEvent = new HashMap<>();
    
	public DialogueManager() {
        dialoguesByEvent.put("intro", new String[] {"Bienvenue, Ghaya ! Ca me fait plaisir de te revoir",
            "Tu n'es pas au courant de la dernière nouvelle?",
            "Un marchand a établi son camp dans une maison de bois claire non loin d'ici.",
            "Seul problème, il a fait tomber sa bourse de pièces...",
            "En apprenant l'existence de ce petit trésor, une horde de monstre est apparue...",
            "Ton rôle aujourd'hui est donc de récuperer ces pièces pour les rendre au marchand.",
            "Mais fais bien attention à la horde."
        });

        
        dialoguesByEvent.put("enter_shop", new String[] {
        	    "Bienvenue dans ma boutique, jeune aventurier !",
        	    "Que puis-je faire pour toi ?!"
        });
        
        dialoguesByEvent.put("enter_shop_give_gold", new String[] {
        	    "Ohhh... c’est donc toi qui m’a rapporté cette bourse !",
        	    "Tu n’imagines pas à quel point tu me sauves.",
        	    "Tiens, prends ceci en remerciement !"
        });
        
        dialoguesByEvent.put("exit_shop_1", new String[] {
                "Ahhhh te revoilà... Nous te cherchions !",
                "Le chef de la horde est devenu fou de rage quand il a appris que tu avais recupérer les pièces !",
                "Sa bande nous a attaqué, ils ont mis le feu à toutes nos maisons. ",
                "Aide nous à éteindre nos maisons par pitié !"
        });
        
        

        dialoguesByEvent.put("enter_shop_chat", new String[] {
        	    "Eh bien oui, je viens tout juste de m’installer.",
        	    "Mais avec tous ces monstres dehors, ce n’est pas de tout repos !",
        	    "D'ailleurs, n'aurais-tu pas vu une bourse remplie d'or ?",
        	    "Je crois que je l'ai faite tomber de ma charette pendant le voyage..."
        });
        
        dialoguesByEvent.put("flames_extinguished", new String[] {
        		"Oh non je viens de voir le chef de la horde emporter ton épouse ?!",
        		"Il faut absoluement que tu ailles la sauver ! Je crois que la horde campe dans la forêt interdite...",
        		"Pour que tu puisses arriver avant eux, cherche dans les coffres du village.", 
        		"Une pierre rouge magique te permettra de le téléporter là-bas..."
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