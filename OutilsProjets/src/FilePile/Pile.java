package FilePile;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayDeque;

/**
 *
 * @author guillaume.laurent
 */
public class Pile<Element> {

    private ArrayDeque<Element> pile;

    public Pile() {
        pile = new ArrayDeque<>();
    }

    public void push(Element e) {
        this.pile.addLast(e);
    }

    public Element pop() {
        return this.pile.removeLast();
    }

    public Element peek() {
        return this.pile.getLast();
    }

    public boolean isEmpty() {
        return this.pile.isEmpty();
    }

    public int size() {
        return this.pile.size();
    }

    public String toString() {
        return "Pile = " + pile ;
    }
}
