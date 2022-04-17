package fr.usmb.m1isc.compilation.tp;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Tree {
    private Tree droite;
    private Tree gauche;
    private String value;
    private Type type;

    public enum Type {
            PLUS, MOINS, MOINS_UNAIRE, MUL, DIV, MOD, NOT, OR, AND, SEMI, POINT, LET, IDENT, INPUT, OUTPUT, IF, THEN, ELSE, WHILE, DO, EGAL, GT, GTE, NIL, ERROR
    };

    public Tree(String value) {
        this.value = value;
        this.type = Type.NIL;
    }

    public Tree(String value, Tree gauche, Tree droite) {
        this.gauche = gauche;
        this.droite = droite;
        this.value = value;
        this.type = Type.NIL;
    }
    public Tree(Type type, String value, Tree gauche, Tree droite) {
        this.gauche = gauche;
        this.droite = droite;
        this.value = value;
        this.type = type;
    }

    public Tree getGauche() {
        return gauche;
    }

    public Tree getDroite() {
        return droite;
    }

    public String getValue() {
        return value;
    }

    public void setGauche(Tree gauche) {
        this.gauche = gauche;
    }

    public void setDroite(Tree droite) {
        this.droite = droite;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String[] getVariable() {
        Set<String> var = new HashSet<String>();
        if(type == Type.LET){
            var.add(gauche.getValue() );
        }
        if (gauche != null) {
            Collections.addAll(var, gauche.getVariable());
        }
        if (droite != null) {
            Collections.addAll(var, droite.getVariable());
        }
        String[] res = new String[var.size()];
        var.toArray(res);
        return res;
    }

    public void getValeur(){

        if (gauche != null) {
            gauche.getValeur();
        }
        if (droite != null && type != Type.WHILE) {
            droite.getValeur();
        }

        switch (type){
            case LET:
                if (droite.getType() == Type.INPUT) {
                    System.out.println("\t" + "in eax");
                }else {
                    if (droite.getType() == Type.NIL) {
                        System.out.println("\t" + "mov eax, " + droite.getValue());
                    }else {
                        System.out.println("\t" + "mov eax, ebx");
                    }
                }
                System.out.println("\t" + "mov " + gauche.getValue() + ", eax");
                break;
            case MUL:
                prefix();
                System.out.println("\t" + "mul eax, ebx");
                break;
            case DIV:
                prefix();
                System.out.println("\t" + "div eax, ebx");
                break;
            case MOD:
                prefix();
                System.out.println("\t" + "mov ecx, eax");
                System.out.println("\t" + "div ecx, ebx");
                System.out.println("\t" + "mul ecx, edx");
                System.out.println("\t" + "sub eax, ecx");
                break;
            case PLUS:
                prefix();
                System.out.println("\t" + "plus eax, ebx");
                break;
            case MOINS:
                prefix();
                System.out.println("\t" + "sub eax, ebx");
                break;
            case WHILE:
                System.out.println("\t" + "\tdebut_while:");
                prefix();
                if (gauche.getType() == Type.GT) {
                    System.out.println("\t" + "sub eax, ebx");
                    System.out.println("\t" + "jle faux_gt");
                    System.out.println("\t" + "mov eax, 1");
                    System.out.println("\t" + "jmp sortie_gt");
                    System.out.println("\t" + "\tfaux_gt_1:");
                    System.out.println("\t" + "mov eax, 0");
                    System.out.println("\t" + "\tsortie_gt:");
                    System.out.println("\t" + "jz sortie_while_1");
                    droite.getValeur();
                    System.out.println("\t" + "jmp debut_while");
                    System.out.println("\t" + "\tsortie_while_1:");
                    break;
                }else if (gauche.getType() == Type.GTE){
                    System.out.println("\t" + "sub eax, ebx");
                    System.out.println("\t" + "jle faux_gt");
                    System.out.println("\t" + "mov eax, 1");
                    System.out.println("\t" + "jmp sortie_gt");
                    System.out.println("\t" + "\tfaux_gt_1:");
                    System.out.println("\t" + "mov eax, 0");
                    System.out.println("\t" + "\tsortie_gt:");
                    System.out.println("\t" + "jz sortie_while_1");
                    droite.getValeur();
                    System.out.println("\t" + "jmp debut_while");
                    System.out.println("\t" + "\tsortie_while_1:");
                    break;
                }
                break;
            case OUTPUT:
                System.out.println("\t" + "mov eax, " + gauche.getValue());
                System.out.println("\t" + "out eax");
                break;
        }
    }

    public void prefix(){
        if (gauche.getType() == Type.NIL) {
            System.out.println("\t" + "mov eax, " + gauche.getValue());
        }
        System.out.println("\t" + "push eax");
        if (droite.getType() == Type.NIL) {
            System.out.println("\t" + "mov eax, " + droite.getValue());
        }
        System.out.println("\t" + "pop ebx");
    }


    public void codeAsm(){
        String[] vars = this.getVariable();
        System.out.println("DATA SEGMENT");
        for(String var : vars){
            System.out.println("\t" + var + " DD");
        }
        System.out.println("DATA ENDS");
        System.out.println("CODE SEGMENT");
        this.getValeur();
        System.out.println("CODE ENDS");
    }


    public String toString() {
        String g = "";
        String d = "";
        if (gauche != null ) {
            g += gauche;
        }
        if (droite != null) {
            d += droite;
        }
        if (gauche == null && droite == null) {
            return value;
        }
        return "(" + value + " " + g + " " + d + ")";
    }
}
