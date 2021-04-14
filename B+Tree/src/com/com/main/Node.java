package com.main;

import java.util.*;

class Node<T extends Comparable<T>> {
    private List<Element<T>> elements;
    public Node<T> parent;
    public Node<T> nextNode = null;
    public Node<T> previousNode = null;

    public Node(Node<T> parent) {
        this.elements = new ArrayList<>();
        this.parent = parent;
    }

    public boolean isRoot(){
        return parent == null;
    }

    public boolean isLeaf() {
        return elements.stream().allMatch(node -> node.getLeftNeighbour() == null && node.getRightNeighbour() == null);
    }

    public void insertElement(Element<T> element) {
        if(elements.isEmpty()) {
            elements.add(element);
            return;
        }
        Integer index = null;
        try {
            Element<T> greaterElement = elements.stream().filter(e -> e.getValue().compareTo(element.getValue()) >= 0).findFirst().orElseThrow();
            index = elements.indexOf(greaterElement);
            elements.add(index, element);
        } catch (NoSuchElementException e) {
            index = elements.size();
            elements.add(index, element);
        }
        if (index < elements.size() - 1) {
            elements.get(index + 1).setLeftNeighbour(element.getRightNeighbour());
        }
    }

    Node<T> getLinkFor(T value)
    {
        for(int i = 0; i < elements.size(); i++)
        {
            if(elements.get(i).getValue().compareTo(value) > 0)
                return elements.get(i).getLeftNeighbour();
            if(i == elements.size() - 1 && elements.get(i).getValue().compareTo(value) < 0)
                return elements.get(i).getRightNeighbour();
        }
        return null;
    }

    Element<T> getElementForValue(T value)
    {
        for(int i = 0; i < getElements().size(); i++)
        {
            if(getElements().get(i).getValue() == value)
                return getElements().get(i);
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node<?> node = (Node<?>) o;
        return elements.equals(node.elements) && Objects.equals(parent, node.parent) && Objects.equals(nextNode, node.nextNode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elements, parent, nextNode);
    }

    public List<Element<T>> getElements() {
        return elements;
    }

    public void setElements(List<Element<T>> elements) {
        this.elements = elements;
    }

    public void setParent(Node<T> parent) {
        this.parent = parent;
    }

    public void print()
    {
        System.out.print(printContent());
    }

    public String printContent()
    {
        String result = "";
        result += "[";
        for(Element<T> el : elements)
        {
            result += (el.getValue() + " ");
        }
        result += "]";
        return result;
    }

}