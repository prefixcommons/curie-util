package org.prefixcommons;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;


public class CurieUtilTest {

  CurieUtil util;
  Map<String, String> map = new HashMap<>();

  @Before
  public void setup() {
    map.put("", "http://x.org/");
    map.put("A", "http://x.org/a_");
    map.put("B", "http://x.org/B_");
    map.put("CC", "http://x.org/C_C");
    map.put("C", "http://x.org/C_");
    util = new CurieUtil(map);
  }

  @Test
  public void curiePrefixes() {
    assertThat(util.getPrefixes(), hasItems("A", "B"));
  }

  @Test
  public void expansion() {
    assertThat(util.getExpansion("A"), is("http://x.org/a_"));
  }

  @Test
  public void absentIri_whenMappingIsNotPresent() {
    assertThat(util.getIri("NONE:foo").isPresent(), is(false));
  }

  @Test
  public void fullIri_whenInputHasNoPrefix() {
    assertThat(util.getIri(":foo").get(), is("http://x.org/foo"));
  }

  @Test
  public void curie_whenShortMappingIsPresent() {
    assertThat(util.getCurie("http://x.org/foo"), is(Optional.of(":foo")));
  }

  @Test
  public void curie_whenLongMappingIsPresent() {
    assertThat(util.getCurie("http://x.org/a_foo"), is(Optional.of("A:foo")));
  }

  @Test
  public void noCurie_whenMappingIsNotPresent() {
    assertThat(util.getCurie("http://none.org/none"), is(Optional.<String>empty()));
  }

  @Test
  public void curie_canBeDefinedInAnyOrder() {
    assertThat(util.getCurie("http://x.org/C_Chello"), is(Optional.of("CC:hello")));
  }

  @Test
  public void getMap() {
    assertThat(util.getCurieMap(), is(map));
  }

  @Test
  public void fromJsonLdFile() throws IOException {
    CurieUtil curieUtil = CurieUtil.fromJsonLdFile("src/test/resources/obo_context.jsonld");
    assertThat(curieUtil.getCurie("http://purl.obolibrary.org/obo/FIX_foo"),
        is(Optional.of("FIX:foo")));
    assertThat(curieUtil.getCurie("http://purl.obolibrary.org/obo/XAO_foo"),
        is(Optional.of("XAO:foo")));
    assertThat(curieUtil.getCurie("http://purl.obolibrary.org/none_foo").isPresent(), is(false));
    assertThat(curieUtil.getIri("NONE:foo").isPresent(), is(false));
    assertThat(curieUtil.getIri("FIX:foo"),
        is(Optional.of("http://purl.obolibrary.org/obo/FIX_foo")));
    assertThat(curieUtil.getIri("XAO:foo"),
        is(Optional.of("http://purl.obolibrary.org/obo/XAO_foo")));
  }

  @Test
  public void getIriPrefix() {
    assertThat(util.getCuriePrefix("http://x.org/a_12345"), is(Optional.of("A")));
    assertThat(util.getCuriePrefix("http://x.org/C_C12345"), is(Optional.of("CC")));
  }
}
