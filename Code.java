import java.util.ArrayList;
public abstract class Agent{
    protected int x;
    protected int y;
    protected String type;
    private static ArrayList<Agent> l = new ArrayList<Agent>();
    public Agent(int x,int y,String type){
        this.x=x;
        this.x=y;
        this.type=type;
        l.add(this);
    }
    public Agent(String type){
        this((int)(Math.random()*10),(int)(Math.random()*10),type);
    }
    public void seDeplacer(int xnew,int ynew){
        for(Agent a:l){
            if(a.x==xnew && a.y==ynew)return;
        }
        x=xnew;
        y=ynew;
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public double distance(int x,int y){
        return Math.sqrt((this.x-x)*(this.x-x)+((this.y-y)*(this.y-y)));
    }

    public String toString(){
        return type+" en position "+x+" "+y;
    }
    public String getType(){ return type; }
}
public class AniException extends Exception{
    
}
public abstract class Animaux extends Agent{
    protected int hp;
    protected String [] mangeable;

    public Animaux(int vie,String[] mangeable,String type){
        super(type);
        this.hp=vie;
        this.mangeable=mangeable;
    }
    public Animaux(String[] mangeable,String type){
        this(100,mangeable,type);
    }
    public abstract boolean manger(Ressource res);
    public String toString(){
        return super.toString()+" vie restante: "+hp;
    }
    public int getHP(){
        return hp;
    }
    public void getMangeable(){
        System.out.println("L'animal "+type+" peut manger : ");
        for(int i=0;i<mangeable.length;i++){
            System.out.print(mangeable[i]+" ");
        }
        System.out.println();
    }
}
public class AnimauxFerme extends Animaux{

    public AnimauxFerme(int hp,String[] mangeable,String type){
        super(hp,mangeable,type);
    }
    public AnimauxFerme clone(){
       return new AnimauxFerme(hp, mangeable, type);
    }
    public boolean manger(Ressource res){
        for(int i=0;i<mangeable.length;i++){
            if(mangeable[i].equals(res.type)){
                int q = res.getQuantite();
                res.setQuantite(0);
                if(hp<100){ hp+=q; }
                if(hp>100){ hp=100; }
                System.out.println("L'animal "+this.type+" mange "+res.toString());
                return true;
            }
        }
        System.out.println("L'animal "+this.type+" n'a pas trouve de nourriture...");
        return false;
    }
    public AnimauxFerme reproduire(){
        AnimauxFerme a = clone();
        a.hp/=2;
        return a;
    }
    public void reduceHp(int enleve){
        this.hp-=enleve;
    }
    public String toString(){
        return "Animal de la ferme: "+super.toString();
    }
}
import java.util.ArrayList;
public class AnimauxPred extends Animaux implements Ennemi{

    public AnimauxPred(int hp,String[] mangeable){
        super(hp,mangeable,"AnimauxPred");
    }

    public static void ajouterPred(ArrayList<Agent> agents, int nbLigne, int nbColonne){
        // Definir sa position
        int lignePred = (int) (Math.random() * (nbLigne) + (nbLigne / 2)); // Entre [taille/2,taille[
        int colonnePred = (int) (Math.random() * (nbColonne)); // Entre [0,taille[
        // Initialiser et placer l'animal prÃ©dateur dans la liste d'agents
        AnimauxPred pred = new AnimauxPred(100, null);
        pred.seDeplacer(lignePred, colonnePred);
        agents.add(pred);
    }

    public AnimauxPred clone(){
        return new AnimauxPred(hp, mangeable);
    }

    public AnimauxPred reproduire(){
        AnimauxPred a = new AnimauxPred(hp/2,mangeable);
        hp/=2;
        return a;
    }

    public boolean manger(Ressource res){
        for(int i=0;i<mangeable.length;i++){
            if(mangeable[i]==res.type){
                int q=res.getQuantite();
                res.setQuantite(0);
                if(hp<100)hp+=q;
                if(hp>100)hp=100;
                System.out.println("L'animal "+this.type+" mange"+mangeable[i]);
                return true;
            }
        }
        return false;
    }
    public void tuer(ArrayList<AnimauxFerme> l){
        for(int i=0; i<l.size();i++)
        {
            AnimauxFerme tmp=l.get(i);
            if (tmp.x== this.x && tmp.y== this.y){
                this.hp+=tmp.hp;
                if(this.hp>100)this.hp=100;
                tmp.hp=0;
                tmp=null;
            }
        }
    }
    public void tuer(ArrayList<Agent> l, Agent a){
        this.hp+=((AnimauxFerme)a).hp;
        l.remove(a);
    }
    public String toString(){
        return "Animal predateur: "+super.toString();
    }
}
import java.util.ArrayList;

public class Chasseur extends Homme implements Ennemi
{
    private boolean mortel; //True pour armes mortelle et False pour armes qui blesse
    private int munitions;
    private int nbMort;
    private int nbBlesse;

    public Chasseur(boolean mortel,int x, int y)
    {
        super(x,y,"Chasseur");
        this.mortel=mortel;
        munitions=(int)(Math.random()*3)+3;
    }

    public static void ajouterChasseur(ArrayList<Agent> agents, int nbLigne, int nbColonne){
        // Definir sa position
        int ligneChasseur = (int) (Math.random() * (nbLigne) + (nbLigne / 2)); // Entre [taille/2,taille[
        int colonneChasseur = (int) (Math.random() * (nbColonne)); // Entre [0,taille[
        // Initialiser et placer le Chasseur dans la liste d'agents
        Chasseur chass = new Chasseur(true, ligneChasseur, colonneChasseur);
        agents.add(chass);
    }

    public void tuer(ArrayList<AnimauxFerme> tab)
    {
        //Si on suppose que le terrain est de taille 20*20 et que la ferme est dans la premiere partie
        //du terrain (de la ligne/colonne 1 a ligne/colonne 10)
        //Et que le terrain ou les animaux se balade (il peux y avoir chasseur + predateur) se situe dans la deuxieme partie
        //(de la ligne/colonne 11 a ligne/colonne 20)
        
        if(munitions>0)
        {
            //Coordonees de la cible

            for(int i=0;i<tab.size();i++)
            {
                if(tab.get(i).getX()==this.x && tab.get(i).getY()==this.y)
                {
                    if(mortel) //Si armes mortelle
                    {
                        tab.remove(i);
                        munitions--;
                        nbMort++;
                        return;
                    }
                    (tab.get(i)).reduceHp(1);
                }
            }
            //Si aucun animal ne se situe Ã  ces coordonnees, alors la balle est perdue et aucun degat n'a ete commis
            munitions--;
        }
        else
        {
            if(mortel)
                System.out.println("Chasseur Ã  tuer "+nbMort+" animaux");
            else
                System.out.println("Chasseur Ã  blesser "+nbBlesse+" animaux");
        }
    }
    public void tuer(ArrayList<Agent> tab, Agent a)
    {
        //Si on suppose que le terrain est de taille 20*20 et que la ferme est dans la premiere partie
        //du terrain (de la ligne/colonne 1 a ligne/colonne 10)
        //Et que le terrain ou les animaux se balade (il peux y avoir chasseur + predateur) se situe dans la deuxieme partie
        //(de la ligne/colonne 11 a ligne/colonne 20)
        
        if(munitions>0)
        {
            //Coordonees de la cible

            for(int i=0;i<tab.size();i++)
            {
                if(tab.get(i).getX()==this.x && tab.get(i).getY()==this.y)
                {
                    if(mortel) //Si armes mortelle
                    {
                        tab.remove(i);
                        munitions--;
                        nbMort++;
                        return;
                    }
                    ((AnimauxFerme)(tab.get(i))).reduceHp(1);
                }
            }
            //Si aucun animal ne se situe Ã  ces coordonnees, alors la balle est perdue et aucun degat n'a ete commis
            munitions--;
        }
        else
        {
            if(mortel)
                System.out.println("Chasseur Ã  tuer "+nbMort+" animaux");
            else
                System.out.println("Chasseur Ã  blesser "+nbBlesse+" animaux");
        }
    }   
}
import java.util.ArrayList;

public interface Ennemi
{
    public void tuer(ArrayList<AnimauxFerme> l);
}
import java.util.ArrayList;

public class Fermier extends Homme {
  private ArrayList<Ressource> reserve;
  private ArrayList<Ressource> tab;
  protected int taille;
  private static double protMoyenneRelevee=0.9;

  public Fermier(int x, int y, String nom, ArrayList<Ressource> poche) {
    super(x, y, nom);
    tab = poche;
    reserve = new ArrayList<Ressource>();
    Ressource reserveHerbe = new Ressource("Herbe", (int) (Math.random() * (1000)));
    reserveHerbe.setPosition(0, 0);
    reserve.add(reserveHerbe);

    Ressource reserveEau = new Ressource("Eau", (int) (Math.random() * (1000)));
    reserveEau.setPosition(0, 0);
    reserve.add(reserveEau);
    Ressource reserveGraine = new Ressource("Graine", (int) (Math.random() * (1000)));
    reserveGraine.setPosition(0, 0);
    reserve.add(reserveGraine);
    Ressource reserveFoin = new Ressource("Foin", (int) (Math.random() * (1000)));
    reserveFoin.setPosition(0, 0);
    reserve.add(reserveFoin);

    taille = tab.size();
  }

  public void reaprovisionner(Ressource res, String type) {
    for (int i = 0; i < tab.size(); i++) {
      if (type.equals(tab.get(i).type)) {
        res.setQuantite(res.getQuantite() + tab.get(i).getQuantite());
        System.out.println("LE FERMIER RAJOUTE " + res.type + " x" + tab.get(i).getQuantite() + " SUR SA CASE");
        tab.get(i).setQuantite(0);
      }
    }
  }

  public void recupReserve() {
    for (int i = 0; i < reserve.size(); i++) {
      for (int j = 0; j < tab.size(); j++) {
        if (reserve.get(i).type.equals(tab.get(j).type)) {
          int qtt = (int) (Math.random() * 10 + 2);
          if (reserve.get(i).getQuantite() - qtt > 0) {
            System.out.println("LE FERMIER RENTRE CHEZ LUI POUR RECUPERER " + qtt + " " + reserve.get(i).type);
            tab.get(j).setQuantite(tab.get(j).getQuantite() + qtt);
            reserve.get(i).setQuantite(reserve.get(i).getQuantite() - qtt);
          } else {
            System.out.println("LE FERMIER N'A PLUS DE " + reserve.get(i).type + " DANS SA RESERVE !");
          }
        }
      }
    }
  }

  public void updateRessource() {
    for (int i = 0; i < reserve.size(); i++) {
      Ressource r = reserve.get(i);
      r.setQuantite(r.getQuantite() + (int) (Math.random() * (100 - 5 + 1)) + 5);
    }
  }

  public void getRessourceDisponible() {
    System.out.println("LA RESERVE CONTIENT :");
    for (int i = 0; i < reserve.size(); i++) {
      Ressource r = reserve.get(i);
      System.out.println(r.toString());
    }
  }

  public void getRessourceDisponiblePoche() {
    System.out.println("LA POCHE CONTIENT:");
    for (int i = 0; i < tab.size(); i++) {
      Ressource r = tab.get(i);
      System.out.println(r.toString());
    }
  }

  public void setPochePosition() {
    for(int i=0;i<tab.size();i++){
      tab.get(i).setPosition(this.getX(), this.getY());
    }
  }

  // Chaque mois fermier recois stock de ressource
  public Ressource getPoche(int i) {
    return tab.get(i);
  }

    public static void analyseProtMoyenne(String saison,int jours)
    {
        //Jours long avec + de soleil => +de production de lait mais diminution du taux de proteines par effet de dillution
        //Prot depend aussi de l'energie de l'animal lors de l'extraction

        //Plus de lait produit en ete et moins en hiver
        
        if(saison=="hiver")
        {   
            protMoyenneRelevee-=0.0087; //Diminue progressivement au fil de la saison
            return;
        }
        if(saison=="primtemps")
        {
            protMoyenneRelevee-=0.011;
            return;
        }
        if(saison=="ete")
        {
            protMoyenneRelevee+=0.011;
            return;
        }
        if(saison=="automne")
        {
            protMoyenneRelevee+=0.0087; //Diminue progressivement au fil de la saison
            return;
        }
    }

    public static double getProtMoyenneRelevee()
    {
        return protMoyenneRelevee;
    }

}
public abstract class Homme extends Agent
{
    public Homme(int x, int y,String nom)
    {
        super(x,y,nom);
    }
}
import java.util.ArrayList;

public class Poule extends AnimauxFerme{
    private int nboeufs=0;

    public Poule(int v,String[] mangeable){
        super(v,mangeable,"Poule");
    }

    public static void ajouterPoule(ArrayList<Agent> agents, int nbLigne, int nbColonne){
        // Definir sa position
        int lignePoule = (int) (Math.random() * (nbLigne / 2)); // Entre [0,taille/2[
        int colonnePoule = (int) (Math.random() * (nbColonne)); // Entre [0,taille[
        // Initialiser la liste des ressources mangeables par la poule
        String[] mangeable = new String[2]; // Graine, Eau
        mangeable[0] = "Graine";
        mangeable[1] = "Eau";
        // Initialiser et placer la Poule dans la liste d'agents
        Animaux poule = new Poule(100, mangeable);
        poule.seDeplacer(lignePoule, colonnePoule);
        agents.add(poule);
    }

    public void setOeufs(){
        if(hp>20){
            nboeufs+=10;
            hp-=5;
        }
    }
     public void extraire(int q){
        nboeufs-=q;
        if(nboeufs<0)nboeufs=0;
    }
    public int recupOeuf(){
        if(nboeufs>0){
            int qtt = (int)(Math.random()*nboeufs);
            this.extraire(qtt);
            return qtt;
        }
        return 0;
    }
    public AnimauxFerme reproduire(){
        AnimauxFerme a = new AnimauxFerme(hp/2,mangeable,this.type);
        hp/=2;
        return a;
    }
    public String toString(){
        return super.toString();
    }
}
// 
// Decompiled by Procyon v0.5.36
// 

public class Ressource
{
    private static int nbRessourcesCreees;
    public final int ident;
    public final String type;
    private int quantite;
    private int x;
    private int y;
    
    static {
        Ressource.nbRessourcesCreees = 0;
    }
    
    public Ressource(final String type, final int quantite) {
        this.type = type;
        this.quantite = quantite;
        this.ident = Ressource.nbRessourcesCreees++;
        this.x = -1;
        this.y = -1;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public int getQuantite() {
        return this.quantite;
    }
    
    public void setQuantite(final int quantite) {
        this.quantite = quantite;
    }
    
    public void setPosition(final int lig, final int col) {
        this.x = lig;
        this.y = col;
    }
    
    public void initialisePosition() {
        this.x = -1;
        this.y = -1;
    }
    
    @Override
    public String toString() {
        String sortie = String.valueOf(this.type) + "[id:" + this.ident + " quantite: " + this.quantite + "] ";
        if (this.x == -1 || this.y == -1) {
            sortie = String.valueOf(sortie) + " n'est pas sur le terrain.";
        }
        else {
            sortie = String.valueOf(sortie) + " en position (" + this.x + ", " + this.y + ")";
        }
        return sortie;
    }
}
import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Simulation {
  private Terrain terrain;
  private ArrayList<Agent> agents; // Liste des agents
  private Ressource[] ressources; // Liste des ressources possibles
  private int nbRes;
  int taille;

  public Simulation(int taille, int m, int n)
  {
    /*
    Etapes de la simulation :

    - Initialiser un terrain
    - Remplir le terrain
    - Generer une liste d'agents et de ressources
      -> Agents : Fermier, Predateurs, Poule, Vache
      -> Ressources : Eau, Foin, Graine, Herbe
    
      
    /* INITIALISER LE TERRAIN DE TAILLE TAILLE x TAILLE (Attention, si TAILLE > nbColonne (ou nbLigne), alors TAILLE = nbColonne (ou nbLigne)) */

    if ((taille > Terrain.NBCOLONNESMAX) || (taille > Terrain.NBLIGNESMAX)) {
      this.taille = Terrain.NBLIGNESMAX;
      terrain = new Terrain(Terrain.NBLIGNESMAX, Terrain.NBCOLONNESMAX);
    } else {
      this.taille = taille;
      terrain = new Terrain(taille, taille);
    }

    /* CREATION DE LA LISTE DE RESSOURCE */    /// A MODIFIER
    ressources = new Ressource[m];
    nbRes = 0;

    /*
      Proportion de ressources sur le terrain :
      - 35% de rien
      - 25% d'herbe
      - 20% d'eau
      - 10% de graine
      - 10% de foin
    */

    /* INITIALISER LE TERRAIN AVEC DES CASES "VIDES" */
    for (int ligne = 0; ligne < taille; ligne++) { // On parcourt chaque ligne
      for (int colonne = 0; colonne < taille; colonne++) { // On parcourt chaque colonne
        Ressource rien = new Ressource(" ", 0);
        rien.setPosition(ligne, colonne);
        terrain.setCase(ligne, colonne, rien);
      }
    }

    /* ON PARCOURT LA LISTE DES RESSOURCES */
    while (nbRes < m) {
      // 25% DE CHANCE D'AVOIR DE L'HERBE
      if (Math.random() < 0.25) {
        // Initialisation de la quantite et de la Ressource
        int qtt = (int) (Math.random() * (100) + 1);
        Ressource herbe = new Ressource("Herbe", qtt);
        // Coordonees
        int ligne = (int) (Math.random() * (taille - taille / 2) + taille / 2);
        int colonne = (int) (Math.random() * (taille));
        // Tant qu'il y a qlq chose sur la case, on regenÃ¨re des coordonnees
        while (terrain.getCase(ligne, colonne).type != " ") {
          ligne = (int) (Math.random() * (taille - taille / 2) + taille / 2);
          colonne = (int) (Math.random() * (taille));
        }
        // On definit la position de la Ressource
        herbe.setPosition(ligne, colonne);
        // On ajoute la Ressource Ã  la liste de Ressource et on la place sur le Terrain
        ressources[nbRes++] = herbe;
        terrain.setCase(ligne, colonne, herbe);
      } 
      // 20% DE CHANCE D'AVOIR DE L'EAU
      else if (Math.random() < 0.45) {
        // Initialisation de la quantite et de la Ressource
        int qtt = (int) (Math.random() * (100) + 1);
        Ressource eau = new Ressource("Eau", qtt);
        // Coordonees
        int ligne = (int) (Math.random() * (taille - taille / 2) + taille / 2);
        int colonne = (int) (Math.random() * (taille));
        // Tant qu'il y a qlq chose sur la case, on regenÃ¨re des coordonnees
        while (terrain.getCase(ligne, colonne).type != " ") {
          ligne = (int) (Math.random() * (taille - taille / 2) + taille / 2);
          colonne = (int) (Math.random() * (taille));
        }
        // On definit la position de la Ressource
        eau.setPosition(ligne, colonne);
        // On ajoute la Ressource Ã  la liste de Ressource et on la place sur le Terrain
        ressources[nbRes++] = eau;
        terrain.setCase(ligne, colonne, eau);
      } 
      // 10% DE CHANCE D'AVOIR DES GRAINES
      else if (Math.random() < 0.55) {
        // Initialisation de la quantite et de la Ressource
        int qtt = (int) (Math.random() * (100) + 1);
        Ressource graine = new Ressource("Graine", qtt);
        // Coordonees
        int ligne = (int) (Math.random() * (taille - taille / 2) + taille / 2);
        int colonne = (int) (Math.random() * (taille));
        // Tant qu'il y a qlq chose sur la case, on regenÃ¨re des coordonnees
        while (terrain.getCase(ligne, colonne).type != " ") {
          ligne = (int) (Math.random() * (taille - taille / 2) + taille / 2);
          colonne = (int) (Math.random() * (taille));
        }
        // On definit la position de la Ressource
        graine.setPosition(ligne, colonne);
        // On ajoute la Ressource Ã  la liste de Ressource et on la place sur le Terrain
        ressources[nbRes++] = graine;
        terrain.setCase(ligne, colonne, graine);
      } 
      // 10% DE CHANCE D'AVOIR DU FOIN
      else if (Math.random() < 0.65) {
        // Initialisation de la quantite et de la Ressource
        int qtt = (int) (Math.random() * (100) + 1);
        Ressource foin = new Ressource("Foin", qtt);
        // Coordonees
        int ligne = (int) (Math.random() * (taille - taille / 2) + taille / 2);
        int colonne = (int) (Math.random() * (taille));
        // Tant qu'il y a qlq chose sur la case, on regenÃ¨re des coordonnees
        while (terrain.getCase(ligne, colonne).type != " ") {
          ligne = (int) (Math.random() * (taille - taille / 2) + taille / 2);
          colonne = (int) (Math.random() * (taille));
        }
        // On definit la position de la Ressource
        foin.setPosition(ligne, colonne);
        // On ajoute la Ressource Ã  la liste de Ressource et on la place sur le Terrain
        ressources[nbRes++] = foin;
        terrain.setCase(ligne, colonne, foin);
      }
    }


    /* CREATION DE LA FORET (LA PREMIER MOITIE DU TERRAIN) AVEC DE L'HERBE */
    for (int i = 0; i < taille / 2; i++) { // nbLigne
      for (int j = 0; j < taille; j++) { // nbColonne
        int qtt = (int) (Math.random() * (100) + 1);
        Ressource herbe = new Ressource("Herbe", qtt);
        terrain.setCase(i, j, herbe);
      }
    }


    /* GENERATION DE LA LISTE D'AGENTS */
    agents = new ArrayList<Agent>();

    // Remplir la liste des agents
    /*
    Probabilite :
    Au minimum, on aura :
      1 fermier : 100%
      1 predateur : 100%
      1 vache : 100%
    Sinon :
      Predateurs : 25%
        - Chasseur : 50%
        - AnimauxPredateurs : 50%
      AnimauxFerme : 75%
        - Vache : 50%
        - Poule : 50%
     */


    /* Placer 1 fermier Ã  une position alÃ©atoire : */
    // CoordonÃ©es du fermier

    int ligneFermier = (int) (Math.random() * (taille));
    int colonneFermier = (int) (Math.random() * (taille));

    // Initialiser les poches du fermier
    agents.add(new Fermier(ligneFermier, colonneFermier, "Fermier", initPoche(ligneFermier, colonneFermier)));

    /* Placer 1 chasseur */
    Chasseur.ajouterChasseur(agents,taille,taille);

    /* Placer 1 vache */
    Vache.ajouterVache(agents,taille,taille);

    // Initialiser la liste des agents
    while (agents.size() < n) {
      // Si c'est un prÃ©dateur
      if (Math.random() < 0.25) {
        if (Math.random() < 0.5) { // C'est un chasseur
          // Initialiser un chasseur et l'ajouter Ã  la liste d'agents
          Chasseur.ajouterChasseur(agents, taille, taille);
        } else {
          // Initialiser un animal prÃ©dateur et l'ajouter Ã  la liste d'agents
          AnimauxPred.ajouterPred(agents, taille, taille);
        }
      } 
      // Si c'est un animal de la ferme
      else {
        if (Math.random() < 0.5) { // C'est une vache
          // Initialiser une vache et l'ajouter Ã  la liste d'agents
          Vache.ajouterVache(agents, taille, taille);
        } 
        else { // C'est une poule
          // Initialiser une poule et l'ajouter Ã  la liste d'agents
          Poule.ajouterPoule(agents, taille, taille);
        }
      }
    }
  }

  public void simuler() throws IOException{
    /* INITIALISATION DES COMPTEURS ET DE LA SAISON*/

    int aniTue = 0;
    int aniRepro = 0;
    int nbLait = 0;
    int nbOeuf = 0;
    int nbMortEpuise = 0;
    String[] saison={"hiver","primtemps","ete","automne"};
    //Supposons que chaque saison dur environ 90 jours
    
    /* Initialisation de la liste des protÃ©ines */
    double[] tabMoyProt=new double[4];
    double tmpProt=0.0;

    FileWriter fw=new FileWriter(new File("test.txt"));
    //Ecriture dans un fichier qui nous permet de generer un graph sur gnuplot
    //Titre: Affichage du graphique representant l'evolution du taux de proteine dans le lait en fonction des saison
    //plot "test.txt" using 1:2 with lines title "MoyenneProt"

    double cpt=0.0;
    int z;

    for(z=0;z<saison.length;z++)
    {
        for (int it = 0; it < 90; it++) { // On fait 90 iterations de la simulation
            
            fw.write((cpt++)+" "+Fermier.getProtMoyenneRelevee()+"\n");
            fw.flush();
            tmpProt+=Fermier.getProtMoyenneRelevee();
            Fermier.analyseProtMoyenne(saison[z],it);

            if (it % 10 == 0) {
                System.out.println("REAPPROVISIONNEMENT DE LA RESERVE");
                ((Fermier) agents.get(0)).updateRessource();
            }

            System.out.println("------------------------------------------");
            System.out.println("Simulation - JOURS " + it+" DE LA SAISON "+saison[z]);
            System.out.println("------------------------------------------");

            for (int i = 0; i < agents.size(); i++) {
                /* Action de chaque agent : */

                // Pour les animaux de la ferme
                if (agents.get(i) instanceof AnimauxFerme) {
                  // On rÃ©cupÃ¨re ses coordonnÃ©es
                  int x = agents.get(i).getX();
                  int y = agents.get(i).getY();

                  // Si c'est une Poule, on met Ã  jour son nombre d'oeufs (elle pond des oeufs)
                  if (agents.get(i) instanceof Poule) {
                      ((Poule) agents.get(i)).setOeufs();
                  }

                  // S'il y a une ressource (herbe, foin, eau) sur sa case
                  if (!terrain.getCase(x, y).type.equals(" ")) { 
                    // L'animal mange la ressource sur sa case (seulement les ressources mangeables par l'animal)
                    if(((AnimauxFerme) agents.get(i)).manger(terrain.getCase(x, y))){ // S'il a bien mangÃ© la ressource
                      // On vide la case de la Ressource
                      Ressource rien = new Ressource(" ", 0);
                      rien.setPosition(x, y);
                      terrain.setCase(x, y, rien);
                    }

                    // On dÃ©place l'agent Ã  une postion alÃ©atoire
                    agents.get(i).seDeplacer((int) (Math.random() * (taille)), (int) (Math.random() * (taille)));

                    // ProbabilitÃ© de se reproduire
                    if (Math.random() < 0.1) {
                      agents.add(((AnimauxFerme) agents.get(i)).reproduire());
                      aniRepro++;
                      System.out.println("L'animal " + agents.get(i).toString() + " se reproduit");
                    } else {
                      System.out.println("Pas de bebe pour " + agents.get(i).toString());
                    }

                    // L'animal se deplace Ã  une position alÃ©atoire
                    x = (int) (Math.random() * taille);
                    y = (int) (Math.random() * taille);
                    agents.get(i).seDeplacer(x, y);

                    // Si l'animal n'a plus assez de hp
                    if (((AnimauxFerme) agents.get(i)).getHP() <= 0) {
                      agents.remove(i);
                      nbMortEpuise++;
                      i--;
                    }
                  } 
                  // S'il n'y a pas de ressource sur sa case
                  else {
                      x = (int) (Math.random() * (taille));
                      y = (int) (Math.random() * (taille));
                      agents.get(i).seDeplacer(x, y);
                  }
                }

                if (agents.get(i) instanceof Fermier)
                {
                  // On affiche les informations du Fermier 
                  affichagePocheReserve(i);

                  /* On rÃ©cupÃ¨re les coordonnÃ©es du Fermier et on met Ã  jour la case du fermier :
                  - S'il est sur une case contenant une ressource prÃ©sente dans sa poche, alors il va en rajouter
                  - Sinon, il la pose par terre
                  */
                  int x = agents.get(i).getX();
                  int y = agents.get(i).getY();
                  updateCaseFermier(agents, i, x, y);

                  // ALORS TRAIRE LES VACHES QUI SONT A UNE DISTANCE DE 4 CASES DE LUI
                  for (int v = 0; v < agents.size(); v++)
                  {
                    if (((agents.get(v) instanceof Vache) && ((int) (agents.get(i).distance(agents.get(v).getX(), agents.get(v).getY())) < 4)))
                    {
                      nbLait += ((Vache)agents.get(v)).extraire();
                      ((Vache)agents.get(v)).setLait();
                    } 
                    else if (((agents.get(v).type.equals("Poule")) && ((int) (agents.get(i).distance(agents.get(v).getX(), agents.get(v).getY())) < 4))) 
                    {
                      if (((AnimauxFerme) agents.get(v)).getHP() > 20) 
                      {
                        if (agents.get(v) instanceof Poule) 
                        {
                          nbOeuf += ((Poule) agents.get(v)).recupOeuf();
                        }
                      }
                    }
                  }
                }

                // Pour les chasseurs :
                if (agents.get(i) instanceof Chasseur) 
                {
                  int resindice = 0;
                  ArrayList<Agent> tmp = agents;
                  while (resindice < agents.size())
                  {
                    if ((((tmp.get(resindice) instanceof AnimauxFerme)
                      && ((int) (agents.get(i).distance(tmp.get(resindice).getX(), tmp.get(resindice).getY())) < 4)))
                        && (resindice > i)) 
                    {
                      System.out.println("Le chasseur tue " + tmp.get(resindice).toString());
                      aniTue++;
                      ((Chasseur) agents.get(i)).tuer(agents, tmp.get(resindice));
                      i = 0;
                      resindice = 0;
                      break;
                    }
                    else if ((((agents.get(resindice) instanceof AnimauxFerme)&& ((int) (agents.get(i).distance(agents.get(resindice).getX(), agents.get(resindice).getY())) < 4)))
                        && (resindice < i))
                      {
                    System.out.println("Le chasseur tue " + agents.get(resindice).toString());
                    aniTue++;
                    ((Chasseur) agents.get(i)).tuer(agents, agents.get(resindice));
                    int x = (int) (Math.random() * taille);
                    int y = (int) (Math.random() * taille);
                    if (i < agents.size()) {
                        agents.get(i).seDeplacer(x, y);
                    }
                    resindice = 0;
                    i = 0;
                    break;
                    } else {
                    int x = (int) (Math.random() * taille);
                    int y = (int) (Math.random() * taille);
                    agents.get(i).seDeplacer(x, y);
                    resindice++;
                    }
                  }
                  agents = tmp;
                }

                // Pour les predateurs :
                if (agents.get(i) instanceof AnimauxPred) {
                int resindice = 0;
                ArrayList<Agent> tmp = agents;
                while (resindice < agents.size()) {
                    // TUER LES ANIMAUX DE LA FERME QUI SONT AU MAXIMUM A 4 CASES DE L'ANIMAL PREDATEUR
                    if ((((agents.get(resindice) instanceof AnimauxFerme)
                        && ((int) (agents.get(i).distance(agents.get(resindice).getX(), agents.get(resindice).getY())) < 4)))
                        && (resindice > i)) {
                    System.out.println("L'animal predateur mange " + agents.get(resindice).toString());
                    aniTue++;
                    ((AnimauxPred) agents.get(i)).tuer(tmp, agents.get(resindice));
                    int x = (int) (Math.random() * taille);
                    int y = (int) (Math.random() * taille);
                    agents.get(i).seDeplacer(x, y);
                    resindice = 0;
                    } else {
                    int x = (int) (Math.random() * taille);
                    int y = (int) (Math.random() * taille);
                    agents.get(i).seDeplacer(x, y);
                    resindice++;
                    }
                }
                agents = tmp;
                }

            }

            /* PAS NECESSAIRE SI LE TERRAIN EST MODIFIE PAR L'AGENT */
            // Mise Ã  jour des cases du terrain et on affiche les informations sur ce qui
            // s'est produit durant l'etape
            for (int i1 = 0; i1 < taille; i1++) {
                for (int i2 = 0; i2 < taille; i2++) {
                // Pour chaque case : Mise Ã  jour
                if (terrain.getCase(i1, i2).type.equals(" ")) { // S'il y a une ressource sur la case
                    if (Math.random() < 0.1) {
                    Ressource pousse = new Ressource("Herbe", (int) (Math.random() * 10));
                    terrain.setCase(i1, i2, pousse);
                    System.out.println("De l'herbe vient de pousser sur la case " + i1 + "," + i2 + " !");
                    }
                } else if (terrain.getCase(i1, i2).type.equals("Herbe")) {
                    if (Math.random() < 0.1) {

                    terrain.getCase(i1, i2).setQuantite(terrain.getCase(i1, i2).getQuantite() + (int) (Math.random() * 10));
                    System.out.println("De l'herbe commence Ã  s'agrandir en " + i1 + "," + i2 + " !");
                    }
                }
                }
            }
            System.out.println("---------REAPPROVISIONNEMENT--------------");
            ((Fermier) agents.get(0)).recupReserve();
            /*
            * try {
            * Thread.sleep(2000);
            * } catch (InterruptedException e) {
            * Thread.currentThread().interrupt();
            * }
            */
            System.out.println("-------ETAT ACTUEL DE LA FERME ET DE LA FORET-------");
            terrain.affiche(2);
            /*
            try {
              Thread.sleep(2000);
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
            }
            */
        }
        tabMoyProt[z]=tmpProt/90;
        tmpProt=0.0;

    }
    fw.close();
    System.out.println("------------------------------------------");
    System.out.println("BILAN au bout de " + 360 + " jours");
    System.out.println("------------------------------------------");
    System.out.println("Nombre d'animal tue : " + aniTue);
    System.out.println("Nombre d'animal mort d'epuisement : " + nbMortEpuise);
    System.out.println("Nombre de naissance : " + aniRepro);
    System.out.println("Nombre de litre de lait recupere : " + nbLait + "L");
    System.out.println("Nombre d'oeuf recupere : " + nbOeuf);
    System.out.println("Taux de Proteine Moyen contenu dans le lait en saison "+saison[0]+" : " + String.format("%.4f",tabMoyProt[0])+" g/L");
    System.out.println("Taux de Proteine Moyen contenu dans le lait en saison "+saison[1]+" : " + String.format("%.4f",tabMoyProt[1])+" g/L");
    System.out.println("Taux de Proteine Moyen contenu dans le lait en saison "+saison[2]+" : " + String.format("%.4f",tabMoyProt[2])+" g/L");
    System.out.println("Taux de Proteine Moyen contenu dans le lait en saison "+saison[3]+" : " + String.format("%.4f",tabMoyProt[3])+" g/L");
  }

  private void affichagePocheReserve(int indiceAgent){
    System.out.println("------------------RESERVE-----------------");
    ((Fermier) agents.get(indiceAgent)).getRessourceDisponible();
    System.out.println("------------------------------------------");
    System.out.println("-------------------POCHE------------------");
    ((Fermier) agents.get(indiceAgent)).getRessourceDisponiblePoche();
    System.out.println("------------------------------------------");
    System.out.println("------------RAPPORT JOURNALIER------------");
  }

  public ArrayList<Ressource> initPoche(int ligneFermier, int colonneFermier){
    ArrayList<Ressource> poche = new ArrayList<Ressource>(4); // Le fermier a 4 places (pour chaque ressource)
    int qttHerbe = (int) (Math.random() * (10));
    int qttFoin = (int) (Math.random() * (10));
    int qttEau = (int) (Math.random() * (10));
    int qttGraine = (int) (Math.random() * (10));
    Ressource herbe = new Ressource("Herbe", qttHerbe);
    herbe.setPosition(ligneFermier, colonneFermier);
    Ressource foin = new Ressource("Foin", qttFoin);
    foin.setPosition(ligneFermier, colonneFermier);
    Ressource eau = new Ressource("Eau", qttEau);
    eau.setPosition(ligneFermier, colonneFermier);
    Ressource graine = new Ressource("Graine", qttGraine);
    graine.setPosition(ligneFermier, colonneFermier);
    poche.add(herbe);
    poche.add(foin);
    poche.add(eau);
    poche.add(graine);
    return poche;
  }

  private void updateCaseFermier(ArrayList<Agent> agents, int i, int x, int y){
    // S'il est sur une case contenant une ressource qu'il a dans sa poche
    if (!terrain.getCase(x, y).type.equals(" ")) {
      ((Fermier) agents.get(i)).reaprovisionner(terrain.getCase(x, y), terrain.getCase(x, y).type);
      x = (int) (Math.random() * (taille));
      y = (int) (Math.random() * (taille));
      agents.get(i).seDeplacer(x, y);
    } 
    // Sinon il la depose par terre
    else {
      int indice = (int) (Math.random() * (((Fermier) agents.get(i)).taille));
      Ressource tmp = new Ressource(((Fermier) agents.get(0)).getPoche(indice).type,((Fermier) agents.get(0)).getPoche(indice).getQuantite());
      System.out.println("Le fermier depose " + ((Fermier) agents.get(0)).getPoche(indice).getQuantite() + " "
          + ((Fermier) agents.get(0)).getPoche(indice).type + " par terre");
      ((Fermier) agents.get(0)).getPoche(indice).setQuantite(0);
      terrain.setCase(x, y, tmp);
      x = (int) (Math.random() * (taille - taille / 2) + taille / 2);
      y = (int) (Math.random() * taille);
      agents.get(i).seDeplacer(x, y);
      ((Fermier) agents.get(i)).setPochePosition();
    }
  }
}
import java.util.ArrayList;

// 
// Decompiled by Procyon v0.5.36
// 

public final class Terrain
{
    public static final int NBLIGNESMAX = 20;
    public static final int NBCOLONNESMAX = 20;
    public final int nbLignes;
    public final int nbColonnes;
    private Ressource[][] terrain;
    
    public Terrain() {
        this(20, 20);
    }
    
    public Terrain(final int nblig, final int nbcol) {
        if (nblig > 20) {
            this.nbLignes = 20;
        }
        else if (nblig <= 0) {
            this.nbLignes = 1;
        }
        else {
            this.nbLignes = nblig;
        }
        if (nbcol > 20) {
            this.nbColonnes = 20;
        }
        else if (nbcol <= 0) {
            this.nbColonnes = 1;
        }
        else {
            this.nbColonnes = nbcol;
        }
        this.terrain = new Ressource[this.nbLignes][this.nbColonnes];
    }
    
    public Ressource getCase(final int lig, final int col) {
        if (this.sontValides(lig, col)) {
            return this.terrain[lig][col];
        }
        return null;
    }
    
    public Ressource videCase(final int lig, final int col) {
        if (this.sontValides(lig, col) && this.terrain[lig][col] != null) {
            final Ressource elt = this.terrain[lig][col];
            elt.initialisePosition();
            this.terrain[lig][col] = null;
            return elt;
        }
        return null;
    }
    
    public boolean setCase(final int lig, final int col, final Ressource ress) {
        if (this.sontValides(lig, col)) {
            if (this.terrain[lig][col] != null) {
                this.terrain[lig][col].initialisePosition();
            }
            (this.terrain[lig][col] = ress).setPosition(lig, col);
            return true;
        }
        return false;
    }
    
    public boolean caseEstVide(final int lig, final int col) {
        return !this.sontValides(lig, col) || this.terrain[lig][col] == null;
    }
    
    public ArrayList<Ressource> lesRessources() {
        final ArrayList<Ressource> list = new ArrayList<Ressource>();
        for (int lig = 0; lig < this.nbLignes; ++lig) {
            for (int col = 0; col < this.nbColonnes; ++col) {
                if (this.terrain[lig][col] != null) {
                    list.add(this.terrain[lig][col]);
                }
            }
        }
        return list;
    }
    
    public boolean sontValides(final int lig, final int col) {
        return lig >= 0 && lig < this.nbLignes && col >= 0 && col < this.nbColonnes;
    }
    
    public void affiche(final int nbCaracteres) {
        String sortie = "";
        String cadre = ":";
        String ligne = "";
        final int nbCar = Math.max(nbCaracteres, 1);
        for (int i = 0; i < nbCar; ++i) {
            ligne = String.valueOf(ligne) + "-";
        }
        for (int j = 0; j < this.nbColonnes; ++j) {
            cadre = String.valueOf(cadre) + ligne + ":";
        }
        cadre = (sortie = String.valueOf(cadre) + "\n");
        for (int lig = 0; lig < this.nbLignes; ++lig) {
            for (int col = 0; col < this.nbColonnes; ++col) {
                if (this.terrain[lig][col] == null) {
                    sortie = String.valueOf(sortie) + "|" + String.format("%-" + nbCar + "s", " ");
                }
                else {
                    sortie = String.valueOf(sortie) + "|" + this.premiersCar(this.terrain[lig][col].type, nbCar);
                }
            }
            sortie = String.valueOf(sortie) + "|\n" + cadre;
        }
        System.out.println(sortie);
    }
    
    @Override
    public String toString() {
        int compte = 0;
        for (int i = 0; i < this.nbLignes; ++i) {
            for (int j = 0; j < this.nbColonnes; ++j) {
                if (this.terrain[i][j] != null) {
                    ++compte;
                }
            }
        }
        String sortie = "Terrain de " + this.nbLignes + "x" + this.nbColonnes + " cases: ";
        if (compte == 0) {
            sortie = String.valueOf(sortie) + "toutes les cases sont libres.";
        }
        else if (compte == 1) {
            sortie = String.valueOf(sortie) + "il y a une case occup\u00e9e.";
        }
        else {
            sortie = String.valueOf(sortie) + "il y a " + compte + " cases occup\u00e9es.";
        }
        return sortie;
    }
    
    private String premiersCar(final String s, final int nbCar) {
        final String sExtended = String.format("%-" + nbCar + "s", s);
        return sExtended.substring(0, nbCar);
    }
}
import java.io.IOException;


public class TestSimulation {
  public static void main(String[] args)
  throws IOException 
  {
    /*
     * Creation de l'environnement :
     * - Terrain de 10x10
     * - Initialiser et placer aleatoirement 12 ressources sur le terrain
     * - Generer 3 agents
     */
    Simulation s1 = new Simulation(10, 15, 12);

    // On simule 10 iterations et on affiche les actions effectuees durant chaque
    // iteration
    s1.simuler();
    // On afficher le terrain
    // s1.afficher();
  }
}
/**
 * @author Christophe Marsala (LU2IN002 2022oct)
 * 
 * Gestion d'un terrain
 *
 */

import java.util.ArrayList;

public class TestTerrain {

        /**
         * @param args
         */
        public static void main(String[] args) {
                // Exemple de creation de terrain
                Terrain t = new Terrain(5,6);
                
                // Terrain initial : il est vide
                t.affiche(2);
                // Informations sur le terrain
                System.out.println("Informations sur le terrain:\n"+t);         
                
                // On cree une ressource
                Ressource e1 = new Ressource("Miel",5);
                // et on la place sur le terrain
                if (t.setCase(2,3,e1))
                        System.out.println("Ajout de " +e1+" valide !");
                else
                        System.out.println("Ajout incorrect: problÃ¨me de coordonnees !");
                
                // On cree une autre ressource
                Ressource e2 = new Ressource("Pollen",4);
                // On rajoute la ressource sur le terrain
                if (t.setCase(0,2,e2))
                        System.out.println("Ajout de " +e2+" valide !");
                else
                        System.out.println("Ajout incorrect: problÃ¨me de coordonnees !");
                
                // On rajoute une autre ressource et on la met sur le terrain
                if (t.setCase(4,1,new Ressource("Pollen",3)))
                        System.out.println("Ajout valide !");
                else
                        System.out.println("Ajout incorrect: problÃ¨me de coordonnees !");

                // Affichage du terrain avec les ressources ajoutees
                t.affiche(6);
                // Informations sur le terrain
                System.out.println("Informations sur le terrain:\n"+t);
                
                // Contenu d'une case:
                System.out.println("Dans la case (1,4): "+t.getCase(1,4));
                // Contenu d'une case:
                System.out.println("Dans la case (0,2): "+t.getCase(0,2));
                
                // Vidage d'une case:
                System.out.println("Vidage d'une case:");
                Ressource etaitDansLaCase = t.videCase(0,2);
                if (etaitDansLaCase == null)
                        System.out.println("La case etait dejÃ  vide.");
                else 
                        System.out.println("La case contenait : "+etaitDansLaCase);
                
                // Affichage du terrain avec les ressources ajoutees
                // on n'utilise que 3 caractÃ¨res d'affichage
                t.affiche(3);
                
                                System.out.println("Liste de toutes les ressources presentes actuellement:");
                ArrayList<Ressource> liste = t.lesRessources();
                for (Ressource r : liste) {
                        System.out.println(r);
                }
                
        }

}
import java.util.ArrayList;

public class Vache extends AnimauxFerme{
    private int qlait;
  
    public Vache(int v,String[] r,int q){
        super(v,r,"Vache");
        qlait=q;
    }
    public static void ajouterVache(ArrayList<Agent> agents, int nbLigne, int nbColonne){
        // Definir sa position
        int ligneVache2 = (int) (Math.random() * (nbLigne - nbLigne / 2) + nbLigne / 2); // Entre [0,taille/2[
        int colonneVache2 = (int) (Math.random() * (nbColonne)); // Entre [0,taille[
        // Initialiser la liste des ressources mangeables par la vache
        String[] mangeable2 = new String[3]; // Herbe, Eau, Foin
        mangeable2[0] = "Herbe";
        mangeable2[1] = "Eau";
        mangeable2[2] = "Foin";
        // Initialiser et placer la Vache dans la liste d'agents
        Vache vache2 = new Vache(100, mangeable2, 24);
        vache2.seDeplacer(ligneVache2, colonneVache2);
        agents.add(vache2);
    }

    public void setLait(){
        if(hp>50){
            qlait+=24;
            hp-=20;
        }
    }
    public int extraire(){
      int qtt = qlait;
      qlait=0;
      System.out.println("Le fermier trait la vache !");
      return qtt;
    }
    public String toString(){
        return super.toString();
    }
  }

