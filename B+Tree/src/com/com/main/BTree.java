package com.main;
import java.util.*;
import java.util.stream.Collectors;

public class BTree<T extends Comparable<T>> {
    public int order;
    public Node<T> root;

    BTree(int order) {
        this.order = order;
        this.root = null;
    }

   private Node<T> nodeForInsertion(Node<T> root, T value) {
        if(root != null) {
            if (root.isLeaf())
                return root;

            Node<T> node = root.getLinkFor(value);
            if (node != null) {
                return nodeForInsertion(node, value);
            }
        }
        return null;
    }

    private void normalize(Node<T> node)
    {
        if(node.getElements().size() >= order && order > 0)
        {
            int medianElementIndex = node.getElements().size() / 2;
            Element<T> medianElement = new Element<T>(node.getElements().get(medianElementIndex).getValue());

            Node<T> newNode = new Node<>(node.parent);
            ArrayList<Element<T>> oldNodeElements = new ArrayList<>();
            for(int i = 0; i < node.getElements().size(); i++)
            {
                if(i >= medianElementIndex)
                    newNode.insertElement(node.getElements().get(i));
                else
                    oldNodeElements.add(node.getElements().get(i));
            }
            node.setElements(oldNodeElements);
            if(node.isLeaf() && newNode.isLeaf())
            {
                newNode.nextNode = node.nextNode;
                newNode.previousNode = node;
                node.nextNode = newNode;
            }

            newNode.getElements().stream().reduce(new ArrayList<Node<T>>(), (list, element) -> {
                if(element.getLeftNeighbour() != null)
                    list.add(element.getLeftNeighbour());
                if(element.getRightNeighbour() != null)
                    list.add(element.getRightNeighbour());
                return list;
            }, (list1, list2) -> {
                list1.addAll(list2);
                return list1;
            }).forEach(nodeEl -> nodeEl.setParent(newNode));

            node.getElements().stream().reduce(new ArrayList<Node<T>>(), (list, element) -> {
                if(element.getLeftNeighbour() != null)
                    list.add(element.getLeftNeighbour());
                if(element.getRightNeighbour() != null)
                    list.add(element.getRightNeighbour());
                return list;
            }, (list1, list2) -> {
                list1.addAll(list2);
                return list1;
            }).forEach(nodeEl -> nodeEl.setParent(node));

            Node<T> parentNode = node.parent;
            medianElement.setLeftNeighbour(node);
            medianElement.setRightNeighbour(newNode);

            if(parentNode != null) {
                parentNode.insertElement(medianElement);
                normalize(parentNode);
            }
            else {
               root = new Node<T>(null);
               node.setParent(root);
               newNode.setParent(root);
               root.insertElement(medianElement);
            }
            if (!newNode.isLeaf()) {
                newNode.getElements().remove(0);
            }
        }
    }

    public void insert(T value)
    {
        if(root == null)
        {
            root = new Node<T>(null);
            Element<T> rootElement = new Element<>(value);
            root.insertElement(rootElement);
            return;
        }
       Node<T> nodeForInsertion = nodeForInsertion(root, value);
        if(nodeForInsertion == null)
            return;
        Element<T> elementForInsertion = new Element<>(value);
        nodeForInsertion.insertElement(elementForInsertion);
        normalize(nodeForInsertion);
    }

    private List<Node<T>> breadthFirstTraversal()
    {
        List<Node<T>> result = new ArrayList<>();
        Queue<Node<T>> nodes = new LinkedList<>();
        nodes.add(root);
        while(!nodes.isEmpty())
        {
            Node<T> node = nodes.poll();
            result.add(node);
            for(Element<T> el : node.getElements())
            {
                if(el.getLeftNeighbour() != null)
                    nodes.add(el.getLeftNeighbour());
                if(el.getRightNeighbour() != null)
                    nodes.add(el.getRightNeighbour());
            }
        }
        return result;
    }

    public void print()
    {
        Queue<Node<T>> nodes = new LinkedList<>(breadthFirstTraversal());
        while(!nodes.isEmpty()) {
            Node<T> node = nodes.poll();
            node.print();
            if (!node.isLeaf()) {
                for (Element<T> el : node.getElements()) {
                    System.out.print(" { Elelement " + el.getValue() + ": ");
                    System.out.print("Left neighbour: ");
                    el.getLeftNeighbour().print();
                    System.out.print(", Right neighbour: ");
                    el.getRightNeighbour().print();
                    System.out.print(" } ");
                }
            }

            System.out.print(", Parent: " + (node.parent != null ? node.parent.printContent() : "None") + "\n");
        }
    }

    public List<T> getDataEntries()
    {
        Node<T> leftmostLeafNode = root;
        if(leftmostLeafNode == null)
            return new ArrayList<T>();
        while(!leftmostLeafNode.isLeaf())
        {
            leftmostLeafNode = leftmostLeafNode.getElements().get(0).getLeftNeighbour();
        }
        List<T> entries = new ArrayList<T>();
        do{
            List<T> nextEntries = leftmostLeafNode.getElements().stream()
                    .map(Element::getValue)
                    .collect(Collectors.toList());
            entries.addAll(nextEntries);
            leftmostLeafNode = leftmostLeafNode.nextNode;
        }
        while(leftmostLeafNode != null);
        return entries;
    }

    private Node<T> getLeftmostNode(Node<T> node)
    {
        while(!node.isLeaf())
        {
            node = node.getElements().get(0).getLeftNeighbour();
        }
        return node;
    }

    private Node<T> findLeafContainingValue(Node<T> node, T value)
    {
        if(node == null)
            return null;

        else if(node.getElements().stream()
                .map(Element::getValue)
                .collect(Collectors.toList())
                .contains(value) && node.isLeaf())
            return node;
        else {
            Element<T> greaterElement = node.getElements().stream()
                    .filter(e -> e.getValue().compareTo(value) > 0)
                    .findFirst()
                    .orElse(null);
            if(greaterElement == null)
                return findLeafContainingValue(node.getElements().get(node.getElements().size() - 1).getRightNeighbour(), value);
            else
                return findLeafContainingValue(greaterElement.getLeftNeighbour(), value);
        }

    }

    private Node<T> binarySearch(Node<T> node, T value)
    {
        if(node == null)
            return null;

        else if(node.getElements().stream()
                .map(Element::getValue)
                .collect(Collectors.toList())
                .contains(value))
            return node;
        else {
            Element<T> greaterElement = node.getElements().stream()
                    .filter(e -> e.getValue().compareTo(value) > 0)
                    .findFirst()
                    .orElse(null);
            if(greaterElement == null)
                return binarySearch(node.getElements().get(node.getElements().size() - 1).getRightNeighbour(), value);
            else
                return binarySearch(greaterElement.getLeftNeighbour(), value);
        }

    }
    public boolean contains(T value)
    {
        return  binarySearch(root,  value) != null;
    }

    private void checkIfUnderflowOccurs(Node<T> node) throws Exception {
        if(node.parent == null)
            return;

        if(node.getElements().size() > (order/2) - 1) {
            checkIfUnderflowOccurs(node.parent);
            return;
        }
        Element<T> currentNodeKey = node.getElements().get(0);

        Element<T> rightSiblingParent = node.parent.getElements().stream()
                .filter(el -> el.getValue().compareTo(currentNodeKey.getValue()) > 0)
                .findFirst()
                .orElse(null);
        Element<T> leftSiblingParent = node.parent.getElements().stream()
                .filter(el -> el.getValue().compareTo(currentNodeKey.getValue()) <= 0)
                .reduce((left, right) -> right) //equals findLast()
                .orElse(null);

        Node<T> rightNeighbourToBorrow = rightSiblingParent != null ? rightSiblingParent.getLeftNeighbour() : null;
        Node<T> leftNeighbourToBorrow = leftSiblingParent != null ? leftSiblingParent.getRightNeighbour() : null;
        boolean isBorrowFromRightPossible = true;
        boolean isBorrowFromLeftPossible = true;

        if(rightNeighbourToBorrow != null && rightNeighbourToBorrow.getElements().size() <= Math.ceil((order/2.0)) - 1)
        {
            isBorrowFromRightPossible = false;
        }
        if(leftNeighbourToBorrow != null && leftNeighbourToBorrow.getElements().size() <= Math.ceil((order/2.0)) - 1)
        {
            isBorrowFromLeftPossible = false;
        }

        if(isBorrowFromRightPossible && rightNeighbourToBorrow != null)
        {
                Element<T> elementToBorrow = new Element<T>(rightSiblingParent.getValue());
                elementToBorrow.setLeftNeighbour(node.getElements().get(node.getElements().size() - 1).getRightNeighbour());
                elementToBorrow.setRightNeighbour(rightNeighbourToBorrow.getElements().get(0).getLeftNeighbour()) ;
                node.getElements().add(elementToBorrow);
                T newParentValue = rightNeighbourToBorrow.isLeaf() ? rightNeighbourToBorrow.getElements().get(1).getValue() : rightNeighbourToBorrow.getElements().get(0).getValue();
                rightSiblingParent.setValue(newParentValue);
                rightNeighbourToBorrow.getElements().remove(0);
        }
        else if(isBorrowFromLeftPossible && leftNeighbourToBorrow != null)
        {
            if(node.isLeaf())
            {
                Element<T> elementToBorrow = new Element<T>(leftNeighbourToBorrow.getElements().get(leftNeighbourToBorrow.getElements().size() - 1).getValue());
                leftSiblingParent.setValue(elementToBorrow.getValue());
                node.getElements().add(0, elementToBorrow);
            }
            else
            {
                T newParentValue = leftNeighbourToBorrow.getElements().get(leftNeighbourToBorrow.getElements().size() - 1).getValue() ;
                Element<T> elementToBorrow = new Element<T>(leftSiblingParent.getValue());
                elementToBorrow.setLeftNeighbour(leftNeighbourToBorrow.getElements().get(leftNeighbourToBorrow.getElements().size() - 1).getRightNeighbour());
                elementToBorrow.setRightNeighbour(node.getElements().get(0).getLeftNeighbour());
                node.getElements().add(0, elementToBorrow);
                leftSiblingParent.setValue(newParentValue);
            }
            leftNeighbourToBorrow.getElements().remove(leftNeighbourToBorrow.getElements().size() - 1);
        }
        else //merge
        {
            if(node.parent == root && node.parent.getElements().size() == 1)
            {
//                Node<T> leftChild = root.getElements().get(0).getLeftNeighbour();
//                Node<T> rightChild = root.getElements().get(0).getRightNeighbour();
//
//                root.getElements().get(0).setLeftNeighbour(leftChild.getElements().get(leftChild.getElements().size() - 1).getRightNeighbour());
//                root.getElements().addAll(0, leftChild.getElements());
//
//                root.getElements().get(0).setRightNeighbour(rightChild.getElements().get(0).getLeftNeighbour());
//                root.getElements().addAll(rightChild.getElements());
            }

            if(rightNeighbourToBorrow != null) //righthand-side merge
            {
                rightNeighbourToBorrow.getElements().addAll(0, node.getElements());
                if (leftSiblingParent != null)
                {
                    leftSiblingParent.setRightNeighbour(rightNeighbourToBorrow);
                }


            }
            else if(leftNeighbourToBorrow != null)
            {
                leftNeighbourToBorrow.getElements().addAll( node.getElements());
                if (rightSiblingParent != null)
                {
                    rightSiblingParent.setLeftNeighbour(leftNeighbourToBorrow);
                }
            }

            if(node)
        }
        checkIfUnderflowOccurs(node.parent);
    }

    public void delete(T value) {
        Node<T> leafContainingValue = findLeafContainingValue(root, value);
        if (leafContainingValue == null) {
            return;
        }
        Element<T> elementForDeletion = leafContainingValue.getElementForValue(value);
        leafContainingValue.getElements().remove(elementForDeletion);

        Node<T> indexNodeContainingValue = binarySearch(root, value);
        if (indexNodeContainingValue != leafContainingValue) {
            Element<T> keyForDeletion = indexNodeContainingValue.getElementForValue(value);

            if(indexNodeContainingValue.getElements().size() == 0)
            {
                keyForDeletion.setValue(keyForDeletion.getRightNeighbour().getElements().get(0).getValue());
            }
            else
            {
                indexNodeContainingValue.getElements().remove(keyForDeletion);
            }
            if(!keyForDeletion.getRightNeighbour().isLeaf())
            {
                keyForDeletion.getRightNeighbour().getElements().remove(0);
            }
        }
        try
        {
            checkIfUnderflowOccurs(leafContainingValue);
        }
        catch(Exception e)
        {
            System.out.print(e.getMessage());
        }
    }

}