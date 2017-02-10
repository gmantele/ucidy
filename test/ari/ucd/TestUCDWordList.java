package ari.ucd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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
		// Let's add some recommended words:
		words.add(new UCDWord(UCDSyntax.PRIMARY, "meta.main", null, true));
		words.add(new UCDWord(UCDSyntax.BOTH, "pos.eq.ra", null, true));
		words.add(new UCDWord(UCDSyntax.BOTH, "pos.eq.dec", null, true));
		words.add(new UCDWord(UCDSyntax.SECONDARY, "em.radio", null, true));
		words.add(new UCDWord(UCDSyntax.SECONDARY, "em", null, true));
		words.add(new UCDWord(UCDSyntax.SECONDARY, "em.mm", null, true));
		words.add(new UCDWord(UCDSyntax.SECONDARY, "em.opt", null, true));
		words.add(new UCDWord(UCDSyntax.SECONDARY, "em.opt.U", null, true));
		words.add(new UCDWord(UCDSyntax.SECONDARY, "em.opt.V", null, true));
		words.add(new UCDWord(UCDSyntax.SECONDARY, "em.opt.B", null, true));
		words.add(new UCDWord(UCDSyntax.SECONDARY, "em.opt.R", null, true));
		words.add(new UCDWord(UCDSyntax.SECONDARY, "em.opt.I", null, true));

		// ...and a custom UCD word:
		words.add(new UCDWord(UCDSyntax.BOTH, "custom:my.ucd_word", "Blabla", false));
	}

	@Test
	public void testAddUCDWord(){

		/* CASE: Possible additions (and there respective deletion) */

		/*   SUB-CASE: Valid, recognised but not recommended word */

		// with custom namespace
		assertTrue(words.add(new UCDWord(UCDSyntax.BOTH, "custom:foo.bar", "A correct custom UCD word.", false)));
		assertNotNull(words.remove("foo.bar"));

		// with no namespace
		/* NOTE: this test should theoretically fail because "no namespace" is similar to have the "ivoa" namespace.
		 *       Since a custom word can not be recommended, it should not be allowed to have this reserved namespace.
		 *       However, this is permitted by this library, though it is discouraged (a warning should be displayed in the terminal). */
		assertTrue(words.add(new UCDWord(UCDSyntax.BOTH, "foo.bar", "A correct custom UCD word ; BUT WITH NO NAMESPACE...it is permitted but strongly discouraged.", false)));
		assertNotNull(words.remove("foo.bar"));

		/*   SUB-CASE: Recommended with an explicit "ivoa" namespace */
		assertTrue(words.add(new UCDWord(UCDSyntax.PRIMARY, "ivoa:meta.code", "Code or flag", true)));
		assertNotNull(words.remove("meta.code"));

		/*   SUB-CASE: Recommended with no namespace */
		assertTrue(words.add(new UCDWord(UCDSyntax.PRIMARY, "meta.code", "Code or flag", true)));
		assertNotNull(words.remove("meta.code"));

		/* CASE: Impossible additions */

		/*   SUB-CASE: NULL word: */

		assertFalse(words.add(null));

		/*   SUB-CASE: Valid but non-recognised word */

		assertFalse(words.add(new UCDWord("foo.bar")));

		/*   SUB-CASE: Non-valid word even though it is said as recognised */

		assertFalse(words.add(new UCDWord(UCDSyntax.BOTH, "foo@bar", "Really wrong UCD word.", false)));

		/*   SUB-CASE: Non-recommended word having "ivoa" as namespace ; this namespace is reserved to ONLY recommended words */

		assertFalse(words.add(new UCDWord(UCDSyntax.BOTH, "ivoa:foo.bar", "UCD word using a reserved namespace.", false)));
		//assertFalse(words.add(new UCDWord(UCDSyntax.BOTH, "foo.bar", "UCD word using a reserved namespace.", false)));
		/* NOTE: test skipped because for the moment, it is permitted, but strongly discouraged,
		 *       to have a custom (i.e. non-recommended) UCD word in the list with no namespace,
		 *       although usually "no namespace" = 'ivoa' namespace. */

		/*   SUB-CASE: An already existing UCD word: */

		assertFalse(words.add(new UCDWord(UCDSyntax.BOTH, "pos.eq.ra", null, true)));
		assertFalse(words.add(new UCDWord(UCDSyntax.BOTH, "custom:my.ucd_word", "Blabla", false)));

		/* Considering that having no namespace is the same as having the "ivoa" namespace,
		 * adding a UCD word with an explicit "ivoa" namespace must fail as well
		 * if the word already exists with no namespace: */
		assertFalse(words.add(new UCDWord(UCDSyntax.BOTH, "ivoa:pos.eq.ra", null, true)));

		/*   SUB-CASE: An already existing UCD word with a different namespace: */

		assertFalse(words.add(new UCDWord(UCDSyntax.BOTH, "custom:pos.eq.ra", "Bloblo", false)));
		assertFalse(words.add(new UCDWord(UCDSyntax.BOTH, "my.ucd_word", "Blabla", false)));
	}

	@Test
	public void testOrdering(){
		assertEquals(13, words.size());

		Iterator<UCDWord> it = words.iterator();
		assertEquals("em", it.next().word);
		assertEquals("em.mm", it.next().word);
		assertEquals("em.opt", it.next().word);
		assertEquals("em.opt.B", it.next().word);
		assertEquals("em.opt.I", it.next().word);
		assertEquals("em.opt.R", it.next().word);
		assertEquals("em.opt.U", it.next().word);
		assertEquals("em.opt.V", it.next().word);
		assertEquals("em.radio", it.next().word);
		assertEquals("meta.main", it.next().word);
		assertEquals("my.ucd_word", it.next().word);
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
		assertFalse(words.contains("custom:"));
		assertFalse(words.contains("ivoa: "));

		/* CASE: Not in the list */

		assertFalse(words.contains("foo"));
		assertFalse(words.contains("em.radi"));

		/* CASE: First atom not in the list alone */

		assertFalse(words.contains("meta"));
		assertFalse(words.contains("pos.eq"));
		assertFalse(words.contains("custom:my"));

		/* CASE: In the list */

		assertTrue(words.contains("meta.main"));
		assertTrue(words.contains("my.ucd_word"));

		/* CASE: Case INsensitive */

		assertTrue(words.contains("META.Main"));
		assertTrue(words.contains("MY.UCD_Word"));

		/* CASE: UCD word with an explicit "ivoa" namespace */

		assertTrue(words.contains("ivoa:pos.eq.ra"));
		assertTrue(words.contains("custom:my.ucd_word"));

		/* CASE: Same word but with a different namespace */

		assertTrue(words.contains("CUSTOM:pos.eq.ra"));
		assertTrue(words.contains("IVOA:my.ucd_word"));
	}

	@Test
	public void testGetString(){

		/* CASE: Null or empty search string */

		assertNull(words.get(null));
		assertNull(words.get(""));
		assertNull(words.get(" "));
		assertNull(words.get("custom:"));
		assertNull(words.get("ivoa: "));

		/* CASE: Not in the list */

		assertNull(words.get("foo"));

		/* CASE: First atom not in the list alone */

		assertNull(words.get("pos"));
		assertNull(words.get("pos.eq"));

		/* CASE: In the list */

		assertEquals(new UCDWord("pos.eq.ra"), words.get("pos.eq.ra"));

		/* CASE: Case INsensitive */

		assertEquals(new UCDWord("em.radio"), words.get("EM.RaDiO"));

		/* CASE: the beginning of the UCD word is correct, but it is only the beginning ;
		 *       this word does not really exist in the list and none should be found */

		assertNull(words.get("em.radi"));

		/* CASE: UCD word with an explicit "ivoa" namespace */

		assertEquals(new UCDWord("pos.eq.ra"), words.get("ivoa:pos.eq.ra"));

		/* CASE: Same word but with a different namespace */

		assertEquals(new UCDWord("pos.eq.ra"), words.get("CUSTOM:pos.eq.ra"));
	}

	@Test
	public void testGetStringBoolean(){

		/* Note: Tests with the second parameter set to FALSE are already performed by #testGetString().
		 *       ALL the below tests aim to test the namespace check. */

		/* CASE: UCD word with the correct namespace */

		assertEquals(new UCDWord("custom:my.ucd_word"), words.get("Custom:My.UCD_Word", true));

		/* CASE: UCD word with no namespace */

		assertEquals(new UCDWord("pos.eq.ra"), words.get("pos.eq.ra", true));

		/* CASE: UCD word with an explicit "ivoa" namespace */

		assertEquals(new UCDWord("pos.eq.ra"), words.get("ivoa:pos.eq.ra", true));

		/* CASE: Same word but with a different namespace */

		assertNull(words.get("CUSTOM:pos.eq.ra", true));

		/* CASE: UCD word with no namespace while this word is known with a namespace */
		assertNull(words.get("my.ucd_word", true));
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

		/*   SUB-CASE: A different namespace ; the namespace must be ignored for that kind of research: */

		matches = words.startingWith("custom:pos.eq.ra");
		assertEquals(1, matches.size());
		assertEquals(new UCDWord("pos.eq.ra"), matches.first());

		/*   SUB-CASE: incomplete UCD word */

		matches = words.startingWith("em.radi");
		assertEquals(1, matches.size());
		assertEquals(new UCDWord("em.radio"), matches.first());

		/* CASE: In the list - Multiple matches */

		matches = words.startingWith("em");
		assertEquals(9, matches.size());
		Iterator<UCDWord> it = matches.iterator();
		assertEquals("em", it.next().word);
		assertEquals("em.mm", it.next().word);
		assertEquals("em.opt", it.next().word);
		assertEquals("em.opt.B", it.next().word);
		assertEquals("em.opt.I", it.next().word);
		assertEquals("em.opt.R", it.next().word);
		assertEquals("em.opt.U", it.next().word);
		assertEquals("em.opt.V", it.next().word);
		assertEquals("em.radio", it.next().word);
		assertFalse(it.hasNext());

		matches = words.startingWith("pos.eq");
		assertEquals(2, matches.size());
		it = matches.iterator();
		assertEquals("pos.eq.dec", it.next().word);
		assertEquals("pos.eq.ra", it.next().word);
		assertFalse(it.hasNext());

		matches = words.startingWith("custom:pos.eq");
		assertEquals(2, matches.size());
		it = matches.iterator();
		assertEquals("pos.eq.dec", it.next().word);
		assertEquals("pos.eq.ra", it.next().word);
		assertFalse(it.hasNext());
	}

	@Test
	public void testGetClosest(){

		/* CASE: Null or empty parameter */

		assertEquals(0, words.getClosest(null).length);
		assertEquals(0, words.getClosest("").length);
		assertEquals(0, words.getClosest(" ").length);

		/* CASE: A correct word */

		UCDWord[] matches = words.getClosest("pos.eq.ra");
		assertEquals(1, matches.length);
		assertEquals(new UCDWord("pos.eq.ra"), matches[0]);

		// ensure case INsensitivity:
		matches = words.getClosest("POS.eq.Ra");
		assertEquals(1, matches.length);
		assertEquals(new UCDWord("pos.eq.ra"), matches[0]);

		/* CASE: Completely wrong small word */

		matches = words.getClosest("ft");
		assertEquals(0, matches.length);

		/* CASE: Half correct small word */

		matches = words.getClosest("et");
		assertEquals(1, matches.length);
		assertEquals(new UCDWord("em"), matches[0]);

		/* CASE: Less than half correct small word */

		matches = words.getClosest("fts");
		assertEquals(0, matches.length);

		/* CASE: Leading and trailing white-space characters are ignored */

		/* Note:
		 * The following test can work because of the threshold used by getClosest()
		 * to select words only if they have a distance smaller than half the size of
		 * the given word. */

		matches = words.getClosest(" 	   em	  	");
		assertEquals(1, matches.length);
		assertEquals(new UCDWord("em"), matches[0]);

		/* CASE: Only one letter missing */

		matches = words.getClosest("po.eq.ra");
		assertEquals(1, matches.length);
		assertEquals(new UCDWord("pos.eq.ra"), matches[0]);

		/* CASE: Several candidates in case of ties */

		matches = words.getClosest("em.ot.x");
		assertEquals(5, matches.length);
		assertEquals(new UCDWord("em.opt.B"), matches[0]);
		assertEquals(new UCDWord("em.opt.I"), matches[1]);
		assertEquals(new UCDWord("em.opt.R"), matches[2]);
		assertEquals(new UCDWord("em.opt.U"), matches[3]);
		assertEquals(new UCDWord("em.opt.V"), matches[4]);

		/* CASE: Fun case */

		matches = words.getClosest("elec.optical.u");
		assertEquals(1, matches.length);
		assertEquals(new UCDWord("em.opt.U"), matches[0]);
	}

	@Test
	public void testSearch(){
		// TODO Test UCDWordList.search(String) when implemented.
		//fail("Not yet implemented");
	}

}
