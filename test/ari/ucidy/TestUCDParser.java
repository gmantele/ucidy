package ari.ucidy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;

import ari.ucidy.UCD;
import ari.ucidy.UCDParser;
import ari.ucidy.UCDSyntax;
import ari.ucidy.UCDWord;
import ari.ucidy.UCDWordList;

public class TestUCDParser {

	@Before
	public void setUp() throws Exception{}

	@Test
	public void testParse(){

		/* CASE: Check the closest match attribute */

		/*   SUB-CASE: Correct word */

		UCD parsed = UCDParser.defaultParser.parse("meta.id");
		assertEquals(1, parsed.size());
		assertEquals(new UCDWord("meta.id"), parsed.getWord(0));
		assertTrue(parsed.isFullyValid());
		assertNull(parsed.getWord(0).closest);

		/*   SUB-CASE: Word with a typo */

		parsed = UCDParser.defaultParser.parse("meta.i");
		assertEquals(1, parsed.size());
		assertEquals(new UCDWord("meta.i"), parsed.getWord(0));
		assertTrue(parsed.isAllValid());
		assertFalse(parsed.isAllRecognised());
		assertNotNull(parsed.getWord(0).closest);
		assertEquals(1, parsed.getWord(0).closest.length);
		assertEquals(new UCDWord("meta.id"), parsed.getWord(0).closest[0]);

		/*   SUB-CASE: Word with the wrong or a missing namespace */

		UCDWordList knownWords = new UCDWordList();
		knownWords.add(new UCDWord(UCDSyntax.BOTH, "custom:my.ucd_word", null, false));
		knownWords.add(new UCDWord(UCDSyntax.PRIMARY, "meta.id", null, true));
		UCDParser customParser = new UCDParser(knownWords);

		// missing namespace
		parsed = customParser.parse("my.ucd_word");
		assertEquals(1, parsed.size());
		assertEquals(new UCDWord("my.ucd_word"), parsed.getWord(0));
		assertTrue(parsed.isAllValid());
		assertFalse(parsed.isAllRecognised());
		assertNotNull(parsed.getWord(0).closest);
		assertEquals(1, parsed.getWord(0).closest.length);
		assertEquals(new UCDWord("custom:my.ucd_word"), parsed.getWord(0).closest[0]);

		// wrong namespace
		parsed = customParser.parse("wrong:my.ucd_word");
		assertEquals(1, parsed.size());
		assertEquals(new UCDWord("wrong:my.ucd_word"), parsed.getWord(0));
		assertTrue(parsed.isAllValid());
		assertFalse(parsed.isAllRecognised());
		assertNotNull(parsed.getWord(0).closest);
		assertEquals(1, parsed.getWord(0).closest.length);
		assertEquals(new UCDWord("custom:my.ucd_word"), parsed.getWord(0).closest[0]);
	}

	@Test
	public void testParseWordListReaderBoolean(){
		Reader reader = null;
		try{
			reader = new InputStreamReader(UCDWordList.class.getResourceAsStream("/ucd1p-words.txt"));
			UCDWordList words = UCDParser.parseWordList(reader, true);
			assertEquals(524, words.size());
			for(UCDWord w : words){
				if (!w.recommended)
					System.out.println("NOT RECOMMENDED: " + w);
				assertTrue(w.recommended);
			}
		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("Unexpected exception! (see the error's stack trace in the error output for more details)");
		}finally{
			if (reader != null){
				try{
					reader.close();
				}catch(Exception ex){}
			}
		}
	}

	@Test
	public void testParsePSVLine(){

		/* CASE: No PSV line */

		try{
			UCDParser.parsePSVLine(null, true);
		}catch(Exception ex){
			assertTrue(ex instanceof NullPointerException);
			assertEquals("No PSV file line to parse!", ex.getMessage());
		}
		try{
			UCDParser.parsePSVLine("", true);
		}catch(Exception ex){
			assertTrue(ex instanceof NullPointerException);
			assertEquals("No PSV file line to parse!", ex.getMessage());
		}
		try{
			UCDParser.parsePSVLine(" 	", true);
		}catch(Exception ex){
			assertTrue(ex instanceof NullPointerException);
			assertEquals("No PSV file line to parse!", ex.getMessage());
		}

		/* CASE: No column separator (i.e. |) */

		try{
			UCDParser.parsePSVLine("meta.foo", true);
		}catch(Exception ex){
			assertTrue(ex instanceof ParseException);
			assertEquals("No valid separator found between the syntax code and the UCD word!", ex.getMessage());
			assertEquals(0, ((ParseException)ex).getErrorOffset());
		}

		/* CASE: Not enough column */

		try{
			UCDParser.parsePSVLine("S|meta.foo", true);
		}catch(Exception ex){
			assertTrue(ex instanceof ParseException);
			assertEquals("No valid separator found between the UCD word and its description!", ex.getMessage());
			assertEquals(2, ((ParseException)ex).getErrorOffset());
		}

		/* CASE: Wrong syntax code */

		try{
			UCDParser.parsePSVLine("|meta.foo|", true);
		}catch(Exception ex){
			assertTrue(ex instanceof ParseException);
			assertEquals("Unknown syntax code: \"\"! It should be EXACTLY one character among: " + UCDSyntax.allowedSyntaxCodes + ".", ex.getMessage());
			assertEquals(0, ((ParseException)ex).getErrorOffset());
		}

		try{
			UCDParser.parsePSVLine("W|meta.foo|", true);
		}catch(Exception ex){
			assertTrue(ex instanceof ParseException);
			assertEquals("Unknown syntax code: \"W\"! It should be a character among: " + UCDSyntax.allowedSyntaxCodes + ".", ex.getMessage());
			assertEquals(0, ((ParseException)ex).getErrorOffset());
		}

		try{
			UCDParser.parsePSVLine("Wrong|meta.foo|", true);
		}catch(Exception ex){
			assertTrue(ex instanceof ParseException);
			assertEquals("Unknown syntax code: \"Wrong\"! It should be EXACTLY one character among: " + UCDSyntax.allowedSyntaxCodes + ".", ex.getMessage());
			assertEquals(0, ((ParseException)ex).getErrorOffset());
		}

		/* CASE: Missing UCD */

		try{
			UCDParser.parsePSVLine("P||", true);
		}catch(Exception ex){
			assertTrue(ex instanceof NullPointerException);
			assertEquals("Missing UCD word!", ex.getMessage());
		}

		try{
			UCDParser.parsePSVLine("P| |", true);
		}catch(Exception ex){
			assertTrue(ex instanceof NullPointerException);
			assertEquals("Missing UCD word!", ex.getMessage());
		}

		try{
			UCDParser.parsePSVLine("P|	|", true);
		}catch(Exception ex){
			assertTrue(ex instanceof NullPointerException);
			assertEquals("Missing UCD word!", ex.getMessage());
		}

		/* CASE: Everything ok */

		try{
			UCDWord ucd = UCDParser.parsePSVLine("P | meta.main | Main value of something ", true);
			assertEquals(UCDSyntax.PRIMARY, ucd.syntaxCode);
			assertEquals("meta.main", ucd.word);
			assertEquals("Main value of something", ucd.description);
			assertTrue(ucd.recognised);
			assertTrue(ucd.valid);
			assertTrue(ucd.recommended);

			ucd = UCDParser.parsePSVLine("  P | meta.main|	Main value of something ", false);
			assertEquals(UCDSyntax.PRIMARY, ucd.syntaxCode);
			assertEquals("meta.main", ucd.word);
			assertEquals("Main value of something", ucd.description);
			assertTrue(ucd.recognised);
			assertTrue(ucd.valid);
			assertFalse(ucd.recommended);

			ucd = UCDParser.parsePSVLine("V | meta.main |  ", true);
			assertEquals(UCDSyntax.VECTOR, ucd.syntaxCode);
			assertEquals("meta.main", ucd.word);
			assertNull(ucd.description);
			assertTrue(ucd.recognised);
			assertTrue(ucd.valid);
			assertTrue(ucd.recommended);

			ucd = UCDParser.parsePSVLine("Q | meta.main | Main value of | something ", true);
			assertEquals(UCDSyntax.BOTH, ucd.syntaxCode);
			assertEquals("meta.main", ucd.word);
			assertEquals("Main value of | something", ucd.description);
			assertTrue(ucd.recognised);
			assertTrue(ucd.valid);
			assertTrue(ucd.recommended);
		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("Unexpected exception! (see the error's stack trace in the error output for more details)");
		}

		/* CASE: Valid, recognised but with a custom namespace */

		try{
			UCDWord ucd = UCDParser.parsePSVLine("P | custom:meta.main | Main value of something ", true);
			assertEquals(UCDSyntax.PRIMARY, ucd.syntaxCode);
			assertEquals("custom:meta.main", ucd.rawWord);
			assertEquals("meta.main", ucd.word);
			assertEquals("custom", ucd.namespace);
			assertEquals("Main value of something", ucd.description);
			assertTrue(ucd.recognised);
			assertTrue(ucd.valid);
			assertFalse(ucd.recommended);
		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("Unexpected exception! (see the error's stack trace in the error output for more details)");
		}

		/* CASE: Not valid UCD */

		try{
			UCDWord ucd = UCDParser.parsePSVLine("E | _foo | My own non valid UCD. ", true);
			assertEquals(UCDSyntax.PHOT_QUANTITY, ucd.syntaxCode);
			assertEquals("_foo", ucd.word);
			assertEquals("My own non valid UCD.", ucd.description);
			assertFalse(ucd.valid);
			assertFalse(ucd.recognised);
			assertFalse(ucd.recommended);
		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("Unexpected exception! (see the error's stack trace in the error output for more details)");
		}
	}

}
