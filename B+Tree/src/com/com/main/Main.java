package com.main;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        BTree<Integer> tree = new BTree<>(3);
        tree.insert(5);
        tree.insert(15);
        tree.insert(35);
        tree.insert(45);
        tree.insert(20);
        tree.insert(25);
        tree.insert(30);
        //tree.insert(55);
        //tree.insert(40);
        //tree.delete(5);
        tree.print();
        List<Integer> entries = tree.getDataEntries();
        System.out.println(entries);
        System.out.println(tree.contains(3));
    }
}
