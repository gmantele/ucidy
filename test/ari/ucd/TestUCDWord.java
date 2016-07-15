package ari.ucd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

			word = new UCDWord(null, " ", null, false);
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals(" ", word.word);
			assertFalse(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);

			word = new UCDWord(UCDSyntax.PRIMARY, "	", null, true);
			assertEquals(UCDSyntax.PRIMARY, word.syntaxCode);
			assertNull(word.description);
			assertEquals("	", word.word);
			assertFalse(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);

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

			word = new UCDWord(UCDSyntax.BOTH, "meta.foo", null, true);
			assertEquals(UCDSyntax.BOTH, word.syntaxCode);
			assertNull(word.description);
			assertEquals("meta.foo", word.word);
			assertTrue(word.valid);
			assertTrue(word.recognised);
			assertTrue(word.recommended);
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
		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("Unexpected exception! (see the error's stack trace in the error output for more details)");
		}

		/* CASE: Not valid UCD => Not valid, so not recognised and not recommended */

		try{
			UCDWord word = new UCDWord(UCDSyntax.COLOUR, "foo", "My own not valid UCD.", true);
			assertEquals(UCDSyntax.COLOUR, word.syntaxCode);
			assertEquals("My own not valid UCD.", word.description);
			assertEquals("foo", word.word);
			assertFalse(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
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

			word = new UCDWord(" ");
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals(" ", word.word);
			assertFalse(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
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

			word = new UCDWord("em.radio");
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals("em.radio", word.word);
			assertTrue(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);

			word = new UCDWord("em.IR.NIR");
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals("em.IR.NIR", word.word);
			assertTrue(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);

			word = new UCDWord("meta.myOwnAtom");
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals("meta.myOwnAtom", word.word);
			assertTrue(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("Unexpected exception! (see the error's stack trace in the error output for more details)");
		}

		/* CASE: Not VALID UCD */

		try{
			UCDWord word = new UCDWord("foo");
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals("foo", word.word);
			assertFalse(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);

			word = new UCDWord(" arith");
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals(" arith", word.word);
			assertFalse(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);

			word = new UCDWord("emradio");
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals("emradio", word.word);
			assertFalse(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);

			word = new UCDWord("em_radio");
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals("em_radio", word.word);
			assertFalse(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);

			word = new UCDWord("em.my atom");
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals("em.my atom", word.word);
			assertFalse(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);

			word = new UCDWord("em.IR;NIR");
			assertNull(word.syntaxCode);
			assertNull(word.description);
			assertEquals("em.IR;NIR", word.word);
			assertFalse(word.valid);
			assertFalse(word.recognised);
			assertFalse(word.recommended);
		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("Unexpected exception! (see the error's stack trace in the error output for more details)");
		}
	}

}
