package org.prefixcommons.trie;

import java.util.HashMap;

/**
 * Represents a Node in a Trie.
 *
 */
class TrieNode {
  private char value;
  private HashMap<Character, TrieNode> children;
  private boolean isLeaf;

  public TrieNode(char ch) {
    value = ch;
    children = new HashMap<>();
    isLeaf = false;
  }

  public HashMap<Character, TrieNode> getChildren() {
    return children;
  }

  public char getValue() {
    return value;
  }

  public void setIsLeaf(boolean val) {
    isLeaf = val;
  }

  public boolean isLeaf() {
    return isLeaf;
  }
}
