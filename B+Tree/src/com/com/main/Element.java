package com.main;

public class Element<T extends Comparable<T>> {
    private T value;
    private Node<T> leftNeighbour;
    private Node<T> rightNeighbour;

    public Element(T value) {
        this.value = value;
    }

    public Node<T> getLeftNeighbour() {
        return leftNeighbour;
    }

    public Node<T> getRightNeighbour() {
        return rightNeighbour;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public void setLeftNeighbour(Node<T> leftNeighbour) {
        this.leftNeighbour = leftNeighbour;
    }

    public void setRightNeighbour(Node<T> rightNeighbour) {
        this.rightNeighbour = rightNeighbour;
    }

    public T getValue() {
        return value;
    }
}
