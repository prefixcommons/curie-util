package org.prefixcommons;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.prefixcommons.trie.Trie;

import com.github.jsonldjava.core.Context;
import com.github.jsonldjava.utils.JsonUtils;
import com.google.common.collect.ImmutableBiMap;


public class CurieUtil {
  final private Trie trie;
  private final ImmutableBiMap<String, String> curieMap;

  public CurieUtil(Map<String, String> mapping) {
    Collection<String> iris = mapping.values();
    this.curieMap = ImmutableBiMap.copyOf(mapping);
    this.trie = new Trie();
    for (String key : iris) {
      this.trie.insert(key);
    }
  }

  static public CurieUtil fromJsonLdFile(String filePath) throws IOException {
    InputStream inputStream = new FileInputStream(filePath);
    Object jsonObject = JsonUtils.fromInputStream(inputStream);
    Context context = parseContext(jsonObject);
    return new CurieUtil(context.getPrefixes(false));
  }

  /**
   * Load a map of prefixes from the "@context" of a JSON-LD string.
   *
   * @param jsonString the JSON-LD string
   * @return a map from prefix name strings to prefix IRI strings
   * @throws IOException on any problem
   */
  private static Context parseContext(Object jsonObject) throws IOException {
    try {
      if (!(jsonObject instanceof Map)) {
        return null;
      }
      Map<String, Object> jsonMap = (Map<String, Object>) jsonObject;
      if (!jsonMap.containsKey("@context")) {
        return null;
      }
      Object jsonContext = jsonMap.get("@context");
      return new Context().parse(jsonContext);
    } catch (Exception e) {
      throw new IOException(e);
    }
  }

  /***
   * @return all of the CURIE prefixes
   */
  public Collection<String> getPrefixes() {
    return curieMap.keySet();
  }

  /***
   * Expand a CURIE prefix to the corresponding IRI prefix.
   * 
   * @param curiePrefix The curiePrefix to expand.
   * @return mapped IRI prefix
   */
  public String getExpansion(String curiePrefix) {
    return curieMap.get(curiePrefix);
  }

  /***
   * Returns the CURIE of an IRI, if mapped.
   * 
   * @param iri The iri to compact.
   * @return An {@link Optional} CURIE
   */
  public Optional<String> getCurie(String iri) {
    String prefix = trie.getMatchingPrefix(iri);
    if (prefix.equals("")) {
      return Optional.empty();
    } else {
      String curiePrefix = curieMap.inverse().get(prefix);
      return Optional.of(curiePrefix + ":" + iri.substring(prefix.length(), iri.length()));
    }
  }

  /***
   * Expands a CURIE to a full IRI, if mapped.
   * 
   * @param curie The curie to expand.
   * @return an {@link Optional} IRI
   */
  public Optional<String> getIri(String curie) {
    String[] parts = checkNotNull(curie).split(":");
    if (parts.length > 1) {
      String prefix = parts[0];
      if (curieMap.containsKey(prefix)) {
        return Optional.of(curieMap.get(prefix) + curie.substring(curie.indexOf(':') + 1));
      }
    }
    return Optional.empty();
  }

  /***
   * Returns the curie map.
   * 
   * @return the curie map
   */
  public Map<String, String> getCurieMap() {
    return curieMap;
  }

}
