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

public class TestUCDParser {

	@Before
	public void setUp() throws Exception{
	}

	@Test
	public void testParse(){

		/* CASE: Check a deprecated word */

		UCD parsed = UCDParser.defaultParser.parse("pos.eop.nutation");
		assertEquals(1, parsed.size());
		assertFalse(parsed.isFullyValid());
		assertFalse(parsed.isAllRecognised());
		assertTrue(parsed.isAllValid());
		assertTrue(parsed.getWord(0).isDeprecated());
		assertEquals("pos.nutation", parsed.getSuggestion().toString());

		/* CASE: Check the closest match attribute */

		/*   SUB-CASE: Correct word */

		parsed = UCDParser.defaultParser.parse("meta.id");
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
			assertEquals(523, words.size());
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
				}catch(Exception ex){
				}
			}
		}
	}

	@Test
	public void testParseDeprecatedWordListReaderUCDWordList(){
		Reader reader = null;
		try{
			// Create the list of all official IVOA UCD1+ words:
			UCDWordList lstWords = new UCDWordList();
			lstWords.addAll(UCDWordList.class.getResourceAsStream("/ucd1p-words.txt"), true);

			// Read and add all deprecated words to this list:
			reader = new InputStreamReader(UCDWordList.class.getResourceAsStream("/ucd1p-deprecated.txt"));
			DeprecatedUCDWordList lstDeprecatedWords = UCDParser.parseDeprecatedWordList(reader, lstWords);
			assertEquals(211, lstDeprecatedWords.size());
			for(UCDWord w : lstDeprecatedWords){
				assertTrue(w.isDeprecated());
				assertTrue(w.valid);
				assertFalse(w.recognised);
				assertFalse(w.recommended);
				assertTrue(w.suggestedReplacement.isAllRecognised());
			}
		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("Unexpected exception! (see the error's stack trace in the error output for more details)");
		}finally{
			if (reader != null){
				try{
					reader.close();
				}catch(Exception ex){
				}
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

	@Test
	public void testParseDeprecatedFileLine(){

		/* CASE: Missing line to parse */

		try{
			UCDParser.parseDeprecatedFileLine(null);
			fail("It should be impossible to parse NULL!");
		}catch(Exception ex){
			assertEquals(NullPointerException.class, ex.getClass());
			assertEquals("No line to parse!", ex.getMessage());
		}
		try{
			UCDParser.parseDeprecatedFileLine("");
			fail("It should be impossible to parse an empty string!");
		}catch(Exception ex){
			assertEquals(NullPointerException.class, ex.getClass());
			assertEquals("No line to parse!", ex.getMessage());
		}
		try{
			UCDParser.parseDeprecatedFileLine("  	  ");
			fail("It should be impossible to parse an empty string (even with only space characters)!");
		}catch(Exception ex){
			assertEquals(NullPointerException.class, ex.getClass());
			assertEquals("No line to parse!", ex.getMessage());
		}

		/* CASE: A comment line should throw an error */

		try{
			UCDParser.parseDeprecatedFileLine("# Comment blabla");
			fail("It should be impossible to parse a comment!");
		}catch(Exception ex){
			assertEquals(NullPointerException.class, ex.getClass());
			assertEquals("No UCD word or UCD can be fetched from a comment line!", ex.getMessage());
		}
		try{
			UCDParser.parseDeprecatedFileLine(" 	# Comment blabla with leading spaces");
			fail("It should be impossible to parse a comment (even with leading space characters)!");
		}catch(Exception ex){
			assertEquals(NullPointerException.class, ex.getClass());
			assertEquals("No UCD word or UCD can be fetched from a comment line!", ex.getMessage());
		}

		/* CASE: Only one space separated value */

		try{
			UCDParser.parseDeprecatedFileLine("word");
			fail("It should be impossible to parse a line with only column!");
		}catch(Exception ex){
			assertEquals(ParseException.class, ex.getClass());
			assertEquals("Incorrect syntax for a deprecated entry line! It must be 2 values separated by a space, but no space character has been found.", ex.getMessage());
		}

		/* CASE: A normal line (i.e. exactly 2 space separated values) */

		try{
			String[] parseResult = UCDParser.parseDeprecatedFileLine("word suggestion");
			assertNotNull(parseResult);
			assertEquals(2, parseResult.length);
			assertEquals("word", parseResult[0]);
			assertEquals("suggestion", parseResult[1]);

			// VARIANT: with leading and trailing space characters and multiple space characters as separator

			parseResult = UCDParser.parseDeprecatedFileLine("  	 word 	  suggestion	 ");
			assertNotNull(parseResult);
			assertEquals(2, parseResult.length);
			assertEquals("word", parseResult[0]);
			assertEquals("suggestion", parseResult[1]);
		}catch(Exception ex){
			ex.printStackTrace(System.err);
			fail("Unexpected exception! (see the error's stack trace in the error output for more details)");
		}

		/* CASE: Several space separated values */

		try{
			UCDParser.parseDeprecatedFileLine("too many word suggestions");
			fail("It should be impossible to parse when more than 2 space separators are written.");
		}catch(Exception ex){
			assertEquals(ParseException.class, ex.getClass());
			assertEquals("Incorrect syntax for a deprecated entry line! It must be 2 values separated by a space, but more space separated values have been found.", ex.getMessage());
		}
		// VARIANT: with leading and trailing space characters and multiple space characters as separator
		try{
			UCDParser.parseDeprecatedFileLine("  	 too    many		word 	 suggestions   ");
			fail("It should be impossible to parse when more than 2 space separators are written.");
		}catch(Exception ex){
			assertEquals(ParseException.class, ex.getClass());
			assertEquals("Incorrect syntax for a deprecated entry line! It must be 2 values separated by a space, but more space separated values have been found.", ex.getMessage());
		}
	}

}
