package data.dialogue;

public class DialogueManager {

    private final String[] dialogues;
    private int index = 0;

    public DialogueManager() {
        this.dialogues = new String[] {
            "Bienvenue, Ghaya ! Une nouvelle journée resplandissante s'annonce.",
            "Tu n'es pas au courant de la dernière nouvelle?",
            "Un marchand a établi son camp dans une nouvelle maison de bois claire non loin d'ici.",
            "Seul problème, il a fait tomber sa bourse de pièces en cherchant un lieu pour loger.",
            "La nouvelle de ce butin perdu à rametter des monstres du Royaume des Tenèbres.",
            "Ton rôle aujourd'hui est donc de récuperer ces pièces pour les rendre au marchand.",
            " et qui sait il t'offrira peut-être quelque chose en récompense..."
        };
    }

    public boolean hasNext() {
        return index < dialogues.length - 1;
    }

    public String getCurrentDialogue() {
        return dialogues[index];
    }

    public void nextDialogue() {
        if (hasNext()) {
            index++;
        }
    }

    public boolean isFinished() {
        return index >= dialogues.length - 1;
    }
}