package iut_lry.coursedorientation;

/**
 * Created by Baptiste on 02/12/2016 oui.
 */

public class Checkpoint {

    private int id;
    public int baliseActuel;
    public int baliseSuivante;
    public String indication;
    public String poste;

    public Checkpoint() {

    }

    public Checkpoint(int baliseActuel, int baliseSuivante, String indication, String poste){
        this.baliseActuel = baliseActuel;
        this.baliseSuivante = baliseSuivante;
        this.indication = indication;
        this.poste = poste;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBaliseActuel() {
        return baliseActuel;
    }

    public void setBaliseActuel(int baliseActuel) {
        this.baliseActuel = baliseActuel;
    }

    public int getBaliseSuivante() {
        return baliseSuivante;
    }

    public void setBaliseSuivante(int baliseSuivante) {
        this.baliseSuivante = baliseSuivante;
    }

    public String getIndication() {
        return indication;
    }

    public void setIndication(String indication) {
        this.indication = indication;
    }

    public String getPoste() {
        return poste;
    }

    public void setPoste(String poste) {
        this.poste = poste;
    }

    public String toString(){
        return "ID : "+id+"\nBalise pointée : "+baliseActuel+"\nBalise suivante : "+baliseSuivante;
    }
}
