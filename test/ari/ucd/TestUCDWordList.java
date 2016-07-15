package ari.ucd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.SortedSet;

import org.junit.Before;
import org.junit.Test;

public class TestUCDWordList {

	private final UCDWordList words = new UCDWordList();

	@Before
	public void setUp() throws Exception{
		words.add(new UCDWord(UCDSyntax.PRIMARY, "meta.main", null, true));
		words.add(new UCDWord(UCDSyntax.BOTH, "pos.eq.ra", null, true));
		words.add(new UCDWord(UCDSyntax.BOTH, "pos.eq.dec", null, true));
		words.add(new UCDWord(UCDSyntax.SECONDARY, "em.radio", null, true));
		words.add(new UCDWord(UCDSyntax.SECONDARY, "em", null, true));
		words.add(new UCDWord(UCDSyntax.SECONDARY, "em.mm", null, true));
	}

	@Test
	public void testOrdering(){
		assertEquals(6, words.size());

		Iterator<UCDWord> it = words.iterator();
		assertEquals("em", it.next().word);
		assertEquals("em.mm", it.next().word);
		assertEquals("em.radio", it.next().word);
		assertEquals("meta.main", it.next().word);
		assertEquals("pos.eq.dec", it.next().word);
		assertEquals("pos.eq.ra", it.next().word);
		assertFalse(it.hasNext());
	}

	@Test
	public void testContains(){

		/* CASE: Null or empty search string */

		assertFalse(words.contains(null));
		assertFalse(words.contains(""));
		assertFalse(words.contains(" "));

		/* CASE: Not in the list */

		assertFalse(words.contains("foo"));

		/* CASE: First atom not in the list alone */

		assertFalse(words.contains("meta"));
		assertFalse(words.contains("pos.eq"));

		/* CASE: In the list */

		assertTrue(words.contains("meta.main"));

		/* CASE: Case INsensitive */

		assertEquals(new UCDWord("meta.main"), words.get("META.Main"));
	}

	@Test
	public void testGet(){

		/* CASE: Null or empty search string */

		assertNull(words.get(null));
		assertNull(words.get(""));
		assertNull(words.get(" "));

		/* CASE: Not in the list */

		assertNull(words.get("foo"));

		/* CASE: First atom not in the list alone */

		assertNull(words.get("pos"));
		assertNull(words.get("pos.eq"));

		/* CASE: In the list */

		assertEquals(new UCDWord("pos.eq.ra"), words.get("pos.eq.ra"));

		/* CASE: Case INsensitive */

		assertEquals(new UCDWord("em.radio"), words.get("EM.RaDiO"));
	}

	@Test
	public void testStartingWith(){

		/* CASE: Null or empty search string */

		assertEquals(0, words.startingWith(null).size());
		assertEquals(0, words.startingWith("").size());
		assertEquals(0, words.startingWith(" ").size());

		/* CASE: First atom never used in the list */

		assertEquals(0, words.startingWith("foo").size());

		/* CASE: In the list - ONLY 1 match */

		SortedSet<UCDWord> matches = words.startingWith("pos.eq.ra");
		assertEquals(1, matches.size());
		assertEquals(new UCDWord("pos.eq.ra"), matches.first());

		/* CASE: In the list - Multiple matches */

		matches = words.startingWith("em");
		assertEquals(3, matches.size());
		Iterator<UCDWord> it = matches.iterator();
		assertEquals("em", it.next().word);
		assertEquals("em.mm", it.next().word);
		assertEquals("em.radio", it.next().word);
		assertFalse(it.hasNext());

		matches = words.startingWith("pos.eq");
		assertEquals(2, matches.size());
		it = matches.iterator();
		assertEquals("pos.eq.dec", it.next().word);
		assertEquals("pos.eq.ra", it.next().word);
		assertFalse(it.hasNext());
	}

	@Test
	public void testSearch(){
		// TODO Test UCDWordList.search(String) when implemented.
		//fail("Not yet implemented");
	}

}
