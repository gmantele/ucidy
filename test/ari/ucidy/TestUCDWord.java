package ari.ucidy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
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
			assertEquals("", word.rawWord);
			assertNull(word.namespace);
			assertEquals(word.rawWord, word.word);
			assertFalse(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);
			assertFalse(word.isDeprecated());

			word = new UCDWord(null, " ", null, false);
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals(" ", word.rawWord);
			assertNull(word.namespace);
			assertEquals(word.rawWord, word.word);
			assertFalse(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);
			assertFalse(word.isDeprecated());

			word = new UCDWord(UCDSyntax.PRIMARY, "	", null, true);
			assertEquals(UCDSyntax.PRIMARY, word.syntaxCode);
			assertNull(word.description);
			assertEquals("	", word.rawWord);
			assertNull(word.namespace);
			assertEquals(word.rawWord, word.word);
			assertFalse(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);
			assertFalse(word.isDeprecated());

		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("Unexpected exception! (see the error's stack trace in the error output for more details)");
		}

		/* CASE: Everything good */

		try{
			UCDWord word = new UCDWord(UCDSyntax.PRIMARY, "meta.foo", "My foo primary atom", true);
			assertEquals(UCDSyntax.PRIMARY, word.syntaxCode);
			assertEquals("My foo primary atom", word.description);
			assertEquals("meta.foo", word.rawWord);
			assertNull(word.namespace);
			assertEquals(word.rawWord, word.word);
			assertTrue(word.valid);
			assertTrue(word.recognised);
			assertTrue(word.recommended);
			assertNull(word.closest);
			assertFalse(word.isDeprecated());

			word = new UCDWord(UCDSyntax.BOTH, "meta.foo", null, true);
			assertEquals(UCDSyntax.BOTH, word.syntaxCode);
			assertNull(word.description);
			assertEquals("meta.foo", word.rawWord);
			assertNull(word.namespace);
			assertEquals(word.rawWord, word.word);
			assertTrue(word.valid);
			assertTrue(word.recognised);
			assertTrue(word.recommended);
			assertNull(word.closest);
			assertFalse(word.isDeprecated());
		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("Unexpected exception! (see the error's stack trace in the error output for more details)");
		}

		/* CASE: Not recommended, but valid and recognised */

		try{
			UCDWord word = new UCDWord(UCDSyntax.SECONDARY, "meta.foo", "My foo primary atom", false);
			assertEquals(UCDSyntax.SECONDARY, word.syntaxCode);
			assertEquals("My foo primary atom", word.description);
			assertEquals("meta.foo", word.rawWord);
			assertNull(word.namespace);
			assertEquals(word.rawWord, word.word);
			assertTrue(word.valid);
			assertTrue(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);
			assertFalse(word.isDeprecated());
		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("Unexpected exception! (see the error's stack trace in the error output for more details)");
		}

		/* CASE: No syntax code => Valid but not recognised and so not recommended */

		try{
			UCDWord word = new UCDWord(null, "meta.foo", null, true);
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals("meta.foo", word.rawWord);
			assertNull(word.namespace);
			assertEquals(word.rawWord, word.word);
			assertTrue(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);
			assertFalse(word.isDeprecated());
		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("Unexpected exception! (see the error's stack trace in the error output for more details)");
		}

		/* CASE: Not valid UCD => Not valid, so not recognised and not recommended */

		try{
			UCDWord word = new UCDWord(UCDSyntax.COLOUR, "@foo", "My own not valid UCD.", true);
			assertEquals(UCDSyntax.COLOUR, word.syntaxCode);
			assertEquals("My own not valid UCD.", word.description);
			assertEquals("@foo", word.rawWord);
			assertNull(word.namespace);
			assertEquals(word.rawWord, word.word);
			assertFalse(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);
			assertFalse(word.isDeprecated());
		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("Unexpected exception! (see the error's stack trace in the error output for more details)");
		}

		/* CASE: With namespace */

		try{
			UCDWord word = new UCDWord(null, "custom:foo", null, false);
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals("custom:foo", word.rawWord);
			assertEquals("custom", word.namespace);
			assertEquals("foo", word.word);
			assertTrue(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);
			assertFalse(word.isDeprecated());
		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("Unexpected exception! (see the error's stack trace in the error output for more details)");
		}

		/* CASE: Recognised with a namespace. */

		try{
			UCDWord word = new UCDWord(UCDSyntax.PRIMARY, "custom:foo", "My own valid UCD with namespace.", false);
			assertEquals(UCDSyntax.PRIMARY, word.syntaxCode);
			assertEquals("My own valid UCD with namespace.", word.description);
			assertEquals("custom:foo", word.rawWord);
			assertEquals("custom", word.namespace);
			assertEquals("foo", word.word);
			assertTrue(word.valid);
			assertTrue(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);
			assertFalse(word.isDeprecated());
		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("Unexpected exception! (see the error's stack trace in the error output for more details)");
		}

		/* CASE: Recognised with a namespace with a request for recommended but does not have the correct namespace (i.e. NULL or "ivoa"). */

		try{
			UCDWord word = new UCDWord(UCDSyntax.PRIMARY, "custom:foo", "My own valid UCD with namespace.", true);
			assertEquals(UCDSyntax.PRIMARY, word.syntaxCode);
			assertEquals("My own valid UCD with namespace.", word.description);
			assertEquals("custom:foo", word.rawWord);
			assertEquals("custom", word.namespace);
			assertEquals("foo", word.word);
			assertTrue(word.valid);
			assertTrue(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);
			assertFalse(word.isDeprecated());
		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("Unexpected exception! (see the error's stack trace in the error output for more details)");
		}

		/* CASE: Recommended with the namespace "ivoa". */

		try{
			UCDWord word = new UCDWord(UCDSyntax.PRIMARY, "ivoa:foo", "My own valid UCD with namespace.", true);
			assertEquals(UCDSyntax.PRIMARY, word.syntaxCode);
			assertEquals("My own valid UCD with namespace.", word.description);
			assertEquals("ivoa:foo", word.rawWord);
			assertEquals("ivoa", word.namespace);
			assertEquals("foo", word.word);
			assertTrue(word.valid);
			assertTrue(word.recognised);
			assertTrue(word.recommended);
			assertNull(word.closest);
			assertFalse(word.isDeprecated());
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
			assertEquals("", word.rawWord);
			assertNull(word.namespace);
			assertEquals(word.rawWord, word.word);
			assertFalse(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);
			assertFalse(word.isDeprecated());

			word = new UCDWord(" ");
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals(" ", word.rawWord);
			assertNull(word.namespace);
			assertEquals(word.rawWord, word.word);
			assertFalse(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);
			assertFalse(word.isDeprecated());
		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("Unexpected exception! (see the error's stack trace in the error output for more details)");
		}

		/* CASE: Valid UCD words */

		try{
			UCDWord word = new UCDWord("arith");
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals("arith", word.rawWord);
			assertNull(word.namespace);
			assertEquals(word.rawWord, word.word);
			assertTrue(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);
			assertFalse(word.isDeprecated());

			word = new UCDWord("em.radio");
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals("em.radio", word.rawWord);
			assertNull(word.namespace);
			assertEquals(word.rawWord, word.word);
			assertTrue(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);
			assertFalse(word.isDeprecated());

			word = new UCDWord("em.IR.NIR");
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals("em.IR.NIR", word.rawWord);
			assertNull(word.namespace);
			assertEquals(word.rawWord, word.word);
			assertTrue(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);
			assertFalse(word.isDeprecated());

			word = new UCDWord("meta.myOwnAtom");
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals("meta.myOwnAtom", word.rawWord);
			assertNull(word.namespace);
			assertEquals(word.rawWord, word.word);
			assertTrue(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);
			assertFalse(word.isDeprecated());

			word = new UCDWord("myOwnSingleAtom");
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals("myOwnSingleAtom", word.rawWord);
			assertNull(word.namespace);
			assertEquals(word.rawWord, word.word);
			assertTrue(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);
			assertFalse(word.isDeprecated());

			word = new UCDWord("mixed_12-3_blabla");
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals("mixed_12-3_blabla", word.rawWord);
			assertNull(word.namespace);
			assertEquals(word.rawWord, word.word);
			assertTrue(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);
			assertFalse(word.isDeprecated());

			/* With namespace */
			word = new UCDWord("custom:foo");
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals("custom:foo", word.rawWord);
			assertEquals("custom", word.namespace);
			assertEquals("foo", word.word);
			assertTrue(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);
			assertFalse(word.isDeprecated());
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
			assertEquals("@foo", word.rawWord);
			assertNull(word.namespace);
			assertEquals(word.rawWord, word.word);
			assertFalse(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);
			assertFalse(word.isDeprecated());

			// starting with a space
			word = new UCDWord(" arith");
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals(" arith", word.rawWord);
			assertNull(word.namespace);
			assertEquals(word.rawWord, word.word);
			assertFalse(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);
			assertFalse(word.isDeprecated());

			// starting with an underscore
			word = new UCDWord("_atom");
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals("_atom", word.rawWord);
			assertNull(word.namespace);
			assertEquals(word.rawWord, word.word);
			assertFalse(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);
			assertFalse(word.isDeprecated());

			// starting with an hyphen
			word = new UCDWord("-atom");
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals("-atom", word.rawWord);
			assertNull(word.namespace);
			assertEquals(word.rawWord, word.word);
			assertFalse(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);
			assertFalse(word.isDeprecated());

			// containing a forbidden character
			word = new UCDWord("em@radio");
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals("em@radio", word.rawWord);
			assertNull(word.namespace);
			assertEquals(word.rawWord, word.word);
			assertFalse(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);
			assertFalse(word.isDeprecated());

			// containing a different forbidden character
			word = new UCDWord("em.IR;NIR");
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals("em.IR;NIR", word.rawWord);
			assertNull(word.namespace);
			assertEquals(word.rawWord, word.word);
			assertFalse(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);
			assertFalse(word.isDeprecated());

			// containing a space
			word = new UCDWord("em.my atom");
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals("em.my atom", word.rawWord);
			assertNull(word.namespace);
			assertEquals(word.rawWord, word.word);
			assertFalse(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
			assertNull(word.closest);
			assertFalse(word.isDeprecated());
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
			assertFalse(word.isDeprecated());
		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("Unexpected exception! (see the error's stack trace in the error output for more details)");
		}

		/* CASE: UCD with an empty list of closest matches */

		try{
			UCDWord word = new UCDWord("meta.i", new UCDWord[0]);
			assertEquals("meta.i", word.word);
			assertNull(word.closest);
			assertFalse(word.isDeprecated());
		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("Unexpected exception! (see the error's stack trace in the error output for more details)");
		}

		/* CASE: UCD with a closest match */

		try{
			UCDWord word = new UCDWord("meta.i", new UCDWord[]{ new UCDWord(UCDSyntax.PRIMARY, "meta.id", null, true) });
			assertEquals("meta.i", word.word);
			assertNotNull(word.closest);
			assertEquals(1, word.closest.length);
			assertEquals(new UCDWord("meta.id"), word.closest[0]);
			assertFalse(word.isDeprecated());
		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("Unexpected exception! (see the error's stack trace in the error output for more details)");
		}
	}

	@Test
	public void testUCDWordStringUCD(){

		/* CASE: UCD word is NULL */

		try{
			new UCDWord(null, null, null, null);
			fail("It should be impossible to create a deprecated UCD word with NULL!");
		}catch(Exception ex){
			assertEquals(NullPointerException.class, ex.getClass());
			assertEquals("Missing UCD word!", ex.getMessage());
		}

		/* CASE: UCD replacement is NULL */

		try{
			new UCDWord("phot.color.B-R", null, null, null);
			fail("It should be impossible to create a deprecated UCD word without a suggested UCD replacement!");
		}catch(Exception ex){
			assertEquals(NullPointerException.class, ex.getClass());
			assertEquals("Missing UCD replacement for the given deprecated UCD word (\"phot.color.B-R\")!", ex.getMessage());
		}

		/* CASE: Unrecognised UCD replacement */

		try{
			new UCDWord("phot.color.B-R", UCDParser.parseUCD("phot.color;em.mine"), null, null);
			fail("It should be impossible to create a deprecated UCD word with a non recognised UCD replacement!");
		}catch(Exception ex){
			assertEquals(IllegalArgumentException.class, ex.getClass());
			assertEquals("Incorrect UCD replacement for the given deprecated UCD word (\"phot.color.B-R\"): \"phot.color;em.mine\"! It must be fully valid and recognised.", ex.getMessage());
		}

		/* CASE: Minimum valid constructor */

		try{
			UCDWord word = new UCDWord("phot.color.B-R", UCDParser.parseUCD("phot.color;em.opt.B;em.opt.R"), null, null);
			assertEquals("phot.color.B-R", word.word);
			assertTrue(word.isDeprecated());
			assertNotNull(word.suggestedReplacement);
			assertEquals("phot.color;em.opt.B;em.opt.R", word.suggestedReplacement.toString());
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertNull(word.closest);
			assertTrue(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("Unexpected exception! (see the error's stack trace in the error output for more details)");
		}
		/* VARIANT: incorrect UCD word syntax */
		try{
			UCDWord word = new UCDWord("phot color B-R", UCDParser.parseUCD("phot.color;em.opt.B;em.opt.R"), null, null);
			assertEquals("phot color B-R", word.word);
			assertTrue(word.isDeprecated());
			assertNotNull(word.suggestedReplacement);
			assertEquals("phot.color;em.opt.B;em.opt.R", word.suggestedReplacement.toString());
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertNull(word.closest);
			assertFalse(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("Unexpected exception! (see the error's stack trace in the error output for more details)");
		}

		/* CASE: Maximum valid constructor */

		try{
			UCDWord word = new UCDWord("phot.color.B-R", UCDParser.parseUCD("phot.color;em.opt.B;em.opt.R"), UCDSyntax.PRIMARY, "Difference between color B and R.");
			assertEquals("phot.color.B-R", word.word);
			assertTrue(word.isDeprecated());
			assertNotNull(word.suggestedReplacement);
			assertEquals("phot.color;em.opt.B;em.opt.R", word.suggestedReplacement.toString());
			assertEquals(UCDSyntax.PRIMARY, word.syntaxCode);
			assertEquals("Difference between color B and R.", word.description);
			assertNull(word.closest);
			assertTrue(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("Unexpected exception! (see the error's stack trace in the error output for more details)");
		}
		/* VARIANT: incorrect UCD word syntax */
		try{
			UCDWord word = new UCDWord("phot color B-R", UCDParser.parseUCD("phot.color;em.opt.B;em.opt.R"), UCDSyntax.PRIMARY, "Difference between color B and R.");
			assertEquals("phot color B-R", word.word);
			assertTrue(word.isDeprecated());
			assertNotNull(word.suggestedReplacement);
			assertEquals("phot.color;em.opt.B;em.opt.R", word.suggestedReplacement.toString());
			assertEquals(UCDSyntax.PRIMARY, word.syntaxCode);
			assertEquals("Difference between color B and R.", word.description);
			assertNull(word.closest);
			assertFalse(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("Unexpected exception! (see the error's stack trace in the error output for more details)");
		}
	}

	@Test
	public void testIsDeprecated(){

		/* CASE: Still valid UCD word */

		try{
			UCDWord word = new UCDWord(UCDSyntax.COLOUR, "phot.color", "Color index or magnitude difference", true);
			assertFalse(word.isDeprecated());
		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("Unexpected exception! (see the error's stack trace in the error output for more details)");
		}

		/* CASE: Deprecated UCD Word */

		try{
			UCDWord word = new UCDWord("phot.color.B-R", UCDParser.parseUCD("phot.color;em.opt.B;em.opt.R"), null, null);
			assertTrue(word.isDeprecated());
			assertEquals("phot.color;em.opt.B;em.opt.R", word.suggestedReplacement.toString());
		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("Unexpected exception! (see the error's stack trace in the error output for more details)");
		}
	}

	@Test
	public void testEqualsObject(){

		/* CASE: Equality with no namespace */

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

		/* CASE: Equality with namespace */

		try{
			UCDWord word1 = new UCDWord("custom:meta.id");
			UCDWord word2 = new UCDWord("custom:meta.id");
			assertTrue(word1.equals(word2));
			assertTrue(word2.equals(word1));
			assertEquals(word1.hashCode(), word2.hashCode());

			// Supposed to be case INsensitive:
			word2 = new UCDWord("CUstom:META.Id");
			assertTrue(word1.equals(word2));
			assertTrue(word2.equals(word1));
			assertEquals(word1.hashCode(), word2.hashCode());
		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("Unexpected exception! (see the error's stack trace in the error output for more details)");
		}

		try{
			UCDWord word1 = new UCDWord("meta.id");
			UCDWord word2 = new UCDWord("ivoa:meta.id");
			assertTrue(word1.equals(word2));
			assertTrue(word2.equals(word1));
			assertEquals(word1.hashCode(), word2.hashCode());

			// Supposed to be case INsensitive:
			word1 = new UCDWord("ivoa:meta.Id");
			word2 = new UCDWord("IVOA:META.Id");
			assertTrue(word1.equals(word2));
			assertTrue(word2.equals(word1));
			assertEquals(word1.hashCode(), word2.hashCode());
		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("Unexpected exception! (see the error's stack trace in the error output for more details)");
		}

		/* CASE: Inequality on namespace */

		try{
			UCDWord word1 = new UCDWord("custom:meta.id");
			UCDWord word2 = new UCDWord("ivoa:meta.id");
			assertFalse(word1.equals(word2));
			assertFalse(word2.equals(word1));
			assertNotEquals(word1.hashCode(), word2.hashCode());

			// Supposed to be case INsensitive:
			word2 = new UCDWord("META.Id");
			assertFalse(word1.equals(word2));
			assertFalse(word2.equals(word1));
			assertNotEquals(word1.hashCode(), word2.hashCode());
		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("Unexpected exception! (see the error's stack trace in the error output for more details)");
		}
	}

}
