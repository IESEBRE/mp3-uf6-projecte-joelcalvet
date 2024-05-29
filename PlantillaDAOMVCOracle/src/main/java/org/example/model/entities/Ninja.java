package org.example.model.entities;

import java.util.Collection;
import java.util.TreeSet;

public class Ninja {

    private Long id;
    private String nom;
    private double anys;
    private boolean viu;

    private Collection<Poder> poder;


    public Ninja(){}


    /**
     * Constructor per a la creació d'un ninja
     * @param id Identificador del ninja
     * @param nom Nom del ninja
     * @param anys Edat del ninja
     * @param viu Si el ninja està viu o no
     * @param poders Conjunt de poders del ninja
     */
    public Ninja(long id, String nom, double anys, boolean viu, TreeSet<Poder> poders) {
        this.id = id;
        this.nom = nom;
        this.anys = anys;
        this.viu = viu;
        this.poder = poders;
    }

    /**
     * Constructor per a la creació d'un ninja
     * @param text Nom del ninja
     * @param any Edat del ninja
     * @param selected Si el ninja està viu o no
     * @param existingPowers Conjunt de poders del ninja
     */
    public Ninja(String text, double any, boolean selected, TreeSet<Poder> existingPowers) {
        this.nom = text;
        this.anys = any;
        this.viu = selected;
        this.poder = existingPowers;
    }


    public Collection<Poder> getPoder() {
        return poder;
    }

    public void setPoder(Collection<Poder> poder) {
        this.poder = poder;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public double getAnys() {
        return anys;
    }

    public void setAnys(double anys) {
        this.anys = anys;
    }

    public boolean isViu() {
        return viu;
    }

    public void setViu(boolean viu) {
        this.viu = viu;
    }

    public long getId() {
        return id;
    }

    public void setId(long aLong) {}

    /**
     * Afegeix un poder al ninja
     */
    public static class Poder implements Comparable<Poder>{

        private int id;
        private TipusChakra tipusChakra;
        private int quantitatChakra;

        /**
         * Constructor per a la creació d'un poder
         * @param chakra Tipus de chakra
         * @param quantitatChakra Quantitat de chakra
         */
        public Poder(TipusChakra chakra, int quantitatChakra) {
            this.tipusChakra = chakra;
            this.quantitatChakra = quantitatChakra;
        }

        public TipusChakra getTipusChakra() {
            return tipusChakra;
        }

        public void setTipusChakra(TipusChakra tipusChakra) {
            this.tipusChakra = tipusChakra;
        }

        public int getQuantitatChakra() {
            return quantitatChakra;
        }

        public void setQuantitatChakra(int quantitatChakra) {
            this.quantitatChakra = quantitatChakra;
        }

        public long getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }


        @Override
        public int compareTo(Poder o) {
            return this.tipusChakra.compareTo(o.getTipusChakra());
        }

        /**
         * Enumeració dels tipus de chakra
         */
        public enum TipusChakra {
            Foc("Katon"), Vent("Kaze"), Llamp("Raiyon"), Terra("Doton"),
            Aigua("Suiton"), Gel("Kori"), Llum("Hikari"), Ombra("Kage"),
            Fusta("Mokuton"), Ferro("Tetsu"), Vapor("Futto"), Pols("Chiri");

            private String nom;

            TipusChakra(String nom) {
                this.nom = nom;
            }

            public String getNom() {
                return nom;
            }

            @Override
            public String toString() {
                return this.name()+" - " +nom;
            }

        }
    }


}

