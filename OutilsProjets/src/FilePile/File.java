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
public class File<Element> {

    private ArrayDeque<Element> file;

    public File() {
        file = new ArrayDeque<>();
    }

    public void push(Element e) {
        this.file.addLast(e);
    }

    public Element pop() {
        return this.file.removeFirst();
    }

    public Element peek() {
        return this.file.getFirst();
    }

    public boolean isEmpty() {
        return this.file.isEmpty();
    }

    public int size() {
        return this.file.size();
    }

    public String toString() {
        return "File = " + file;
    }
}
