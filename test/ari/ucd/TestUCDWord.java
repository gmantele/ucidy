package ari.ucd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class TestUCDWord {

	@Test
	public void testUCDWordUCDSyntaxStringStringBoolean(){

		/* CASE: Null UCD word */

		try{
			new UCDWord(null, null, null, false);
			fail("A NULL UCD is not possible!");
		}catch(Exception ex){
			assertTrue(ex instanceof NullPointerException);
			assertEquals("Missing UCD word!", ex.getMessage());
		}

		/* CASE: Empty UCD word */

		try{
			UCDWord word = new UCDWord(null, "", null, false);
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals("", word.word);
			assertFalse(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);

			word = new UCDWord(null, " ", null, false);
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals(" ", word.word);
			assertFalse(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);

			word = new UCDWord(UCDSyntax.PRIMARY, "	", null, true);
			assertEquals(UCDSyntax.PRIMARY, word.syntaxCode);
			assertNull(word.description);
			assertEquals("	", word.word);
			assertFalse(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);

		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("Unexpected exception! (see the error's stack trace in the error output for more details)");
		}

		/* CASE: Everything good */

		try{
			UCDWord word = new UCDWord(UCDSyntax.PRIMARY, "meta.foo", "My foo primary atom", true);
			assertEquals(UCDSyntax.PRIMARY, word.syntaxCode);
			assertEquals("My foo primary atom", word.description);
			assertEquals("meta.foo", word.word);
			assertTrue(word.valid);
			assertTrue(word.recognised);
			assertTrue(word.recommended);
			assertNull(word.closest);

			word = new UCDWord(UCDSyntax.BOTH, "meta.foo", null, true);
			assertEquals(UCDSyntax.BOTH, word.syntaxCode);
			assertNull(word.description);
			assertEquals("meta.foo", word.word);
			assertTrue(word.valid);
			assertTrue(word.recognised);
			assertTrue(word.recommended);
			assertNull(word.closest);
		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("Unexpected exception! (see the error's stack trace in the error output for more details)");
		}

		/* CASE: Not recommended, but valid and recognised */

		try{
			UCDWord word = new UCDWord(UCDSyntax.SECONDARY, "meta.foo", "My foo primary atom", false);
			assertEquals(UCDSyntax.SECONDARY, word.syntaxCode);
			assertEquals("My foo primary atom", word.description);
			assertEquals("meta.foo", word.word);
			assertTrue(word.valid);
			assertTrue(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);
		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("Unexpected exception! (see the error's stack trace in the error output for more details)");
		}

		/* CASE: No syntax code => Valid but not recognised and so not recommended */

		try{
			UCDWord word = new UCDWord(null, "meta.foo", null, true);
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals("meta.foo", word.word);
			assertTrue(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);
		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("Unexpected exception! (see the error's stack trace in the error output for more details)");
		}

		/* CASE: Not valid UCD => Not valid, so not recognised and not recommended */

		try{
			UCDWord word = new UCDWord(UCDSyntax.COLOUR, "@foo", "My own not valid UCD.", true);
			assertEquals(UCDSyntax.COLOUR, word.syntaxCode);
			assertEquals("My own not valid UCD.", word.description);
			assertEquals("@foo", word.word);
			assertFalse(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);
		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("Unexpected exception! (see the error's stack trace in the error output for more details)");
		}
	}

	@Test
	public void testUCDWordString(){

		/* CASE: NULL UCD word */

		try{
			new UCDWord(null);
			fail("A NULL UCD is not possible!");
		}catch(Exception ex){
			assertTrue(ex instanceof NullPointerException);
			assertEquals("Missing UCD word!", ex.getMessage());
		}

		/* CASE: Empty UCD word */

		try{
			UCDWord word = new UCDWord("");
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals("", word.word);
			assertFalse(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);

			word = new UCDWord(" ");
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals(" ", word.word);
			assertFalse(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);
		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("Unexpected exception! (see the error's stack trace in the error output for more details)");
		}

		/* CASE: Valid UCD words */

		try{
			UCDWord word = new UCDWord("arith");
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals("arith", word.word);
			assertTrue(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);

			word = new UCDWord("em.radio");
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals("em.radio", word.word);
			assertTrue(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);

			word = new UCDWord("em.IR.NIR");
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals("em.IR.NIR", word.word);
			assertTrue(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);

			word = new UCDWord("meta.myOwnAtom");
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals("meta.myOwnAtom", word.word);
			assertTrue(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);

			word = new UCDWord("myOwnSingleAtom");
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals("myOwnSingleAtom", word.word);
			assertTrue(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);

			word = new UCDWord("mixed_12-3_blabla");
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals("mixed_12-3_blabla", word.word);
			assertTrue(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);
		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("Unexpected exception! (see the error's stack trace in the error output for more details)");
		}

		/* CASE: Not VALID UCD */

		try{
			// not starting with a letter or digit
			UCDWord word = new UCDWord("@foo");
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals("@foo", word.word);
			assertFalse(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);

			// starting with a space
			word = new UCDWord(" arith");
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals(" arith", word.word);
			assertFalse(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);

			// starting with an underscore
			word = new UCDWord("_atom");
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals("_atom", word.word);
			assertFalse(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);

			// starting with an hyphen
			word = new UCDWord("-atom");
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals("-atom", word.word);
			assertFalse(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);

			// containing a forbidden character
			word = new UCDWord("em@radio");
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals("em@radio", word.word);
			assertFalse(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);

			// containing a different forbidden character
			word = new UCDWord("em.IR;NIR");
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals("em.IR;NIR", word.word);
			assertFalse(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);

			// containing a space
			word = new UCDWord("em.my atom");
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals("em.my atom", word.word);
			assertFalse(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);
		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("Unexpected exception! (see the error's stack trace in the error output for more details)");
		}
	}

	@Test
	public void testUCDWordStringUCDWord(){

		/* Note:
		 *   This test is just to ensure that the second parameter is correctly taken into account.
		 *   If it is not about this second parameter, the rest of this constructor should be tested
		 *   in the above test function: #testUCDWordString(). */

		/* CASE: UCD with NULL as list of closest matches */

		try{
			UCDWord word = new UCDWord("meta.i", null);
			assertEquals("meta.i", word.word);
			assertNull(word.closest);
		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("Unexpected exception! (see the error's stack trace in the error output for more details)");
		}

		/* CASE: UCD with an empty list of closest matches */

		try{
			UCDWord word = new UCDWord("meta.i", new UCDWord[0]);
			assertEquals("meta.i", word.word);
			assertNull(word.closest);
		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("Unexpected exception! (see the error's stack trace in the error output for more details)");
		}

		/* CASE: UCD with a closest match */

		try{
			UCDWord word = new UCDWord("meta.i", new UCDWord[]{new UCDWord(UCDSyntax.PRIMARY, "meta.id", null, true)});
			assertEquals("meta.i", word.word);
			assertNotNull(word.closest);
			assertEquals(1, word.closest.length);
			assertEquals(new UCDWord("meta.id"), word.closest[0]);
		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("Unexpected exception! (see the error's stack trace in the error output for more details)");
		}
	}

	@Test
	public void testEqualsObject(){
		try{
			UCDWord word1 = new UCDWord("meta.id");
			UCDWord word2 = new UCDWord("meta.id");
			assertTrue(word1.equals(word2));
			assertTrue(word2.equals(word1));
			assertEquals(word1.hashCode(), word2.hashCode());

			// Supposed to be case INsensitive:
			word2 = new UCDWord("META.Id");
			assertTrue(word1.equals(word2));
			assertTrue(word2.equals(word1));
			assertEquals(word1.hashCode(), word2.hashCode());
		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("Unexpected exception! (see the error's stack trace in the error output for more details)");
		}
	}

}
