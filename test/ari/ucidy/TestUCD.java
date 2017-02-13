package ari.ucidy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import ari.ucidy.UCD;
import ari.ucidy.UCDSyntax;
import ari.ucidy.UCDWord;

public class TestUCD {

	@Before
	public void setUp() throws Exception{}

	@Test
	public void testListAdvice(){

		/* CASE: Duplicated words */

		UCD ucd = new UCD(new UCDWord[]{new UCDWord("meta.id")});
		ucd.listAdvice();
		assertEquals(0, ucd.advice.length);

		ucd = new UCD(new UCDWord[]{new UCDWord("meta.id"),new UCDWord("pos.eq.ra"),new UCDWord("meta.id")});
		ucd.listAdvice();
		assertEquals(1, ucd.advice.length);
		assertEquals("For more readability, you should remove duplicated UCD words: \"meta.id\".", ucd.advice[0]);

		/* CASE: Photometry quantity */

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.PHOT_QUANTITY, "phot.flux", null, true),new UCDWord("em.radio")});
		ucd.listAdvice();
		assertEquals(0, ucd.advice.length);

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.PHOT_QUANTITY, "phot.flux", null, true)});
		ucd.listAdvice();
		assertEquals(1, ucd.advice.length);
		assertEquals("No part of the electromagnetic spectrum is specified for the photometric quantity \"phot.flux\". For more precision, one part of the EM spectrum can be added just after \"phot.flux\".", ucd.advice[0]);

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.PHOT_QUANTITY, "phot.flux", null, true),new UCDWord("meta.main"),new UCDWord("em.radio")});
		ucd.listAdvice();
		assertEquals(1, ucd.advice.length);
		assertEquals("No part of the electromagnetic spectrum is EXPLICITLY specified for the photometric quantity \"phot.flux\". Some candidates have been detected in this UCD: \"em.radio\". For more clarity, one candidate or a new part of the EM spectrum should be moved just after \"phot.flux\".", ucd.advice[0]);

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.PHOT_QUANTITY, "phot.flux", null, true),new UCDWord("em.radio.20-100MHz"),new UCDWord("em.radio.100-200MHz")});
		ucd.listAdvice();
		assertEquals(1, ucd.advice.length);
		assertEquals("At least two parts of the electromagnetic spectrum have been specified successively after the photometric quantity \"phot.flux\". Only one is expected, but maybe more parts of the electromagnetic spectrum are covered here.", ucd.advice[0]);

		/* CASE: Avoid duplicated advice */

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.PHOT_QUANTITY, "spect.dopplerveloc", null, true),new UCDWord(UCDSyntax.PHOT_QUANTITY, "spect.dopplerveloc", null, true)});
		ucd.listAdvice();
		assertEquals(2, ucd.advice.length);
		assertEquals("For more readability, you should remove duplicated UCD words: \"spect.dopplerveloc\".", ucd.advice[0]);
		assertEquals("No part of the electromagnetic spectrum is specified for the photometric quantity \"spect.dopplerveloc\". For more precision, one part of the EM spectrum can be added just after \"spect.dopplerveloc\".", ucd.advice[1]);

		/* CASE: Colors */

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.COLOUR, "phot.color", null, true),new UCDWord("em.radio"),new UCDWord("em.mm")});
		ucd.listAdvice();
		assertEquals(0, ucd.advice.length);

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.COLOUR, "phot.color", null, true),new UCDWord("em.radio")});
		ucd.listAdvice();
		assertEquals(1, ucd.advice.length);
		assertEquals("Missing second bound of the electromagnetic spectrum range for the colour \"phot.color\". For more precision, a part of the EM spectrum can be added after \"phot.color\".\"em.radio\".", ucd.advice[0]);

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.COLOUR, "phot.color", null, true),null,new UCDWord("em.radio")});
		ucd.listAdvice();
		assertEquals(1, ucd.advice.length);
		assertEquals("No range of the electromagnetic spectrum is EXPLICITLY specified for the colour \"phot.color\". Some candidates have been detected in this UCD: \"em.radio\". For more clarity, two candidates or new parts of the EM spectrum should be moved successively just after \"phot.color\".", ucd.advice[0]);

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.COLOUR, "phot.color", null, true)});
		ucd.listAdvice();
		assertEquals(1, ucd.advice.length);
		assertEquals("No range of the electromagnetic spectrum is specified for the colour \"phot.color\". For more precision, two successive parts of the EM spectrum can be added after \"phot.color\".", ucd.advice[0]);

		ucd = new UCD(new UCDWord[]{new UCDWord("em.opt"),new UCDWord(UCDSyntax.COLOUR, "phot.color", null, true),new UCDWord("em.radio")});
		ucd.listAdvice();
		assertEquals(1, ucd.advice.length);
		assertEquals("Missing second bound of the electromagnetic spectrum range for the colour \"phot.color\". Some candidates have been detected in this UCD: \"em.opt\". For more clarity, one candidate or a new part of the EM spectrum should be moved just after \"phot.color\".\"em.radio\".", ucd.advice[0]);

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.COLOUR, "phot.color", null, true),new UCDWord("em.radio"),new UCDWord("em.mm"),new UCDWord("em.opt")});
		ucd.listAdvice();
		assertEquals(1, ucd.advice.length);
		assertEquals("At least three parts of the electromagnetic spectrum have been specified successively after the colour \"phot.color\". Only two are expected. For more clarity, you should probably consider to remove the excedent.", ucd.advice[0]);

		/* CASE: Vectors */

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.VECTOR, "phys.magField", null, true),new UCDWord("pos.earth")});
		ucd.listAdvice();
		assertEquals(0, ucd.advice.length);

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.VECTOR, "pos.pm", null, true),new UCDWord("pos.eq")});
		ucd.listAdvice();
		assertEquals(0, ucd.advice.length);
		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.VECTOR, "pos.pm", null, true),new UCDWord("pos.eq.ra")});
		ucd.listAdvice();
		assertEquals(0, ucd.advice.length);
		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.VECTOR, "pos.pm", null, true),new UCDWord("pos.eq.ha")});
		ucd.listAdvice();
		assertEquals(0, ucd.advice.length);

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.VECTOR, "phys.magField", null, true),new UCDWord("barycenter")});
		ucd.listAdvice();
		assertEquals(1, ucd.advice.length);
		assertEquals("No axis or reference frame is specified for the vector \"phys.magField\". For more precision, one axis or reference frame can be added just after \"phys.magField\".", ucd.advice[0]);

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.VECTOR, "phys.magField", null, true)});
		ucd.listAdvice();
		assertEquals(1, ucd.advice.length);
		assertEquals("No axis or reference frame is specified for the vector \"phys.magField\". For more precision, one axis or reference frame can be added just after \"phys.magField\".", ucd.advice[0]);

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.VECTOR, "phys.magField", null, true),new UCDWord("meta.main"),new UCDWord("pos.earth")});
		ucd.listAdvice();
		assertEquals(1, ucd.advice.length);
		assertEquals("No axis or reference frame is EXPLICITLY specified for the vector \"phys.magField\". Some candidates have been detected in this UCD: \"pos.earth\". For more clarity, one candidate or a axis or reference frame should be moved just after \"phys.magField\".", ucd.advice[0]);

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.VECTOR, "phys.magField", null, true),new UCDWord("pos.earth"),new UCDWord("pos.galactic")});
		ucd.listAdvice();
		assertEquals(1, ucd.advice.length);
		assertEquals("At least two axis or reference frames have been specified successively after the vector \"phys.magField\". Only one is expected. For more clarity, you should probably consider to remove the excedent.", ucd.advice[0]);

		/* CASE: Recommended word with explicit "ivoa" namespace prefix */

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.PRIMARY, "ivoa:meta.id", null, true)});
		ucd.listAdvice();
		assertEquals(1, ucd.advice.length);
		assertEquals("\"ivoa:meta.id\" is a UCD word recommended by the IVOA. The use of the explicit namespace \"ivoa\" should be avoided for more readability. So you should rather write: \"meta.id\".", ucd.advice[0]);

		/* CASE: Recognised but not recommended word */

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.BOTH, "custom:foo", null, false)});
		ucd.listAdvice();
		assertEquals(1, ucd.advice.length);
		assertEquals("\"custom:foo\" is a recognised but not recommended word. In order to ensure better detection by VO applications, you should use a UCD word recommended by the IVOA if any can already represent the same quantity.", ucd.advice[0]);

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.BOTH, "foo", null, false)});
		ucd.listAdvice();
		assertEquals(1, ucd.advice.length);
		assertEquals("\"foo\" is a recognised but not recommended word. In order to ensure better detection by VO applications, you should use a UCD word recommended by the IVOA if any can already represent the same quantity.", ucd.advice[0]);
	}

	@Test
	public void testListErrors(){

		/* CASE: Empty words */

		UCD ucd = new UCD(new UCDWord[]{null});
		ucd.listErrors();
		assertEquals(1, ucd.errors.length);
		assertEquals("1 empty UCD word!", ucd.errors[0]);

		ucd = new UCD(new UCDWord[]{null,new UCDWord(UCDSyntax.SECONDARY, "meta.id", null, true),new UCDWord(UCDSyntax.BOTH, " ", null, false)});
		ucd.listErrors();
		assertEquals(1, ucd.errors.length);
		assertEquals("2 empty UCD words!", ucd.errors[0]);

		/* CASE: Wrong syntax */

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.PRIMARY, "_foo", null, false)});
		ucd.listErrors();

		assertEquals(1, ucd.errors.length);
		assertEquals("Wrong syntax for 1 UCD word: \"_foo\"!", ucd.errors[0]);

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.PRIMARY, "meta id", null, false),new UCDWord(UCDSyntax.BOTH, "pos.eq.ra", null, true),new UCDWord(UCDSyntax.SECONDARY, "_foo", null, false)});
		ucd.listErrors();
		assertEquals(1, ucd.errors.length);
		assertEquals("Wrong syntax for 2 UCD words: \"meta id\", \"_foo\"!", ucd.errors[0]);

		/* CASE: Not recognised */

		ucd = new UCD(new UCDWord[]{new UCDWord("meta.id")});
		ucd.listErrors();
		assertEquals(1, ucd.errors.length);
		assertEquals("1 not recognised UCD word: \"meta.id\"!", ucd.errors[0]);

		ucd = new UCD(new UCDWord[]{new UCDWord(null, "meta.id", null, true)});
		ucd.listErrors();
		assertEquals(1, ucd.errors.length);
		assertEquals("1 not recognised UCD word: \"meta.id\"!", ucd.errors[0]);

		ucd = new UCD(new UCDWord[]{new UCDWord("meta.id"),new UCDWord(UCDSyntax.BOTH, "pos.eq.ra", null, true),new UCDWord(null, "meta.main", null, true)});
		ucd.listErrors();
		assertEquals(1, ucd.errors.length);
		assertEquals("2 not recognised UCD words: \"meta.id\", \"meta.main\"!", ucd.errors[0]);

		/* CASE: Not recognised with closest match(es) */

		ucd = new UCD(new UCDWord[]{new UCDWord("meta.i", new UCDWord[]{new UCDWord(UCDSyntax.PRIMARY, "meta.id", null, true)})});
		ucd.listErrors();
		assertEquals(1, ucd.errors.length);
		assertEquals("1 not recognised UCD word: \"meta.i\" (closest: \"meta.id\")!", ucd.errors[0]);

		ucd = new UCD(new UCDWord[]{new UCDWord("foo.bar", new UCDWord[]{new UCDWord(UCDSyntax.PRIMARY, "custom:foo.bar", null, true)})});
		ucd.listErrors();
		assertEquals(1, ucd.errors.length);
		assertEquals("1 not recognised UCD word: \"foo.bar\" (closest: \"custom:foo.bar\")!", ucd.errors[0]);

		/* CASE: Not valid AND not recognised */

		ucd = new UCD(new UCDWord[]{new UCDWord("my@ucd")});
		ucd.listErrors();
		assertEquals(2, ucd.errors.length);
		assertEquals("Wrong syntax for 1 UCD word: \"my@ucd\"!", ucd.errors[0]);
		assertEquals("1 not recognised UCD word: \"my@ucd\"!", ucd.errors[1]);

		ucd = new UCD(new UCDWord[]{new UCDWord(null, "meta id", null, true)});
		ucd.listErrors();
		assertEquals(2, ucd.errors.length);
		assertEquals("Wrong syntax for 1 UCD word: \"meta id\"!", ucd.errors[0]);
		assertEquals("1 not recognised UCD word: \"meta id\"!", ucd.errors[1]);

		/* CASE: PRIMARY at wrong position */

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.PHOT_QUANTITY, "phot.flux", null, true),new UCDWord(UCDSyntax.PRIMARY, "arith.factor", null, false)});
		ucd.listErrors();
		assertEquals(1, ucd.errors.length);
		assertEquals("1 PRIMARY UCD word not in first position: \"arith.factor\"! Such words MUST be in first position.", ucd.errors[0]);

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.PHOT_QUANTITY, "phot.flux", null, true),new UCDWord(UCDSyntax.PRIMARY, "arith.factor", null, false),new UCDWord(UCDSyntax.PRIMARY, "meta.id", null, false)});
		ucd.listErrors();
		assertEquals(2, ucd.errors.length);
		assertEquals("Too many (2) PRIMARY UCD words: \"arith.factor\", \"meta.id\"! Only one is allowed in a UCD.", ucd.errors[0]);
		assertEquals("2 PRIMARY UCD words not in first position: \"arith.factor\", \"meta.id\"! Such words MUST be in first position.", ucd.errors[1]);

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.PRIMARY, "arith.factor", null, false),new UCDWord(UCDSyntax.PHOT_QUANTITY, "phot.flux", null, true),new UCDWord(UCDSyntax.PRIMARY, "meta.id", null, false)});
		ucd.listErrors();
		assertEquals(2, ucd.errors.length);
		assertEquals("Too many (2) PRIMARY UCD words: \"arith.factor\", \"meta.id\"! Only one is allowed in a UCD.", ucd.errors[0]);
		assertEquals("1 PRIMARY UCD word not in first position: \"meta.id\"! Such words MUST be in first position.", ucd.errors[1]);

		/* CASE: SECONDARY at first position */

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.SECONDARY, "em.radio", null, true)});
		ucd.listErrors();
		assertEquals(1, ucd.errors.length);
		assertEquals("UCD starting with a SECONDARY UCD word: \"em.radio\"! Such words can NOT be in first position.", ucd.errors[0]);

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.SECONDARY, "em.radio", null, true),new UCDWord(UCDSyntax.SECONDARY, "instr.filter", null, true)});
		ucd.listErrors();
		assertEquals(1, ucd.errors.length);
		assertEquals("UCD starting with a SECONDARY UCD word: \"em.radio\"! Such words can NOT be in first position.", ucd.errors[0]);
	}

	@Test
	public void testGetSuggestion(){

		/* CASE: Empty word */

		UCD ucd = new UCD(new UCDWord[]{null});
		assertFalse(ucd.isFullyValid());
		assertNull(ucd.getSuggestion());

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.PRIMARY, "", null, false)});
		assertFalse(ucd.isFullyValid());
		assertNull(ucd.getSuggestion());

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.BOTH, " ", null, false)});
		assertFalse(ucd.isFullyValid());
		assertNull(ucd.getSuggestion());

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.BOTH, "pos.eq.ra", null, false),null});
		assertFalse(ucd.isFullyValid());
		UCD suggestion = ucd.getSuggestion();
		assertNotNull(suggestion);
		assertTrue(suggestion.isFullyValid());
		assertEquals("pos.eq.ra", suggestion.toString());

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.PRIMARY, " ", null, false),new UCDWord(UCDSyntax.BOTH, "pos.eq.ra", null, false)});
		assertFalse(ucd.isFullyValid());
		suggestion = ucd.getSuggestion();
		assertNotNull(suggestion);
		assertTrue(suggestion.isFullyValid());
		assertEquals("pos.eq.ra", suggestion.toString());

		/* CASE: No syntax code */

		ucd = new UCD(new UCDWord[]{new UCDWord(null, "pos.eq.ra", null, false)});
		assertFalse(ucd.isFullyValid());
		assertNull(ucd.getSuggestion());

		/* CASE: No syntax code but with closest match(es) */

		ucd = new UCD(new UCDWord[]{new UCDWord("meta.i", new UCDWord[]{new UCDWord(UCDSyntax.PRIMARY, "meta.id", null, true)})});
		assertFalse(ucd.isFullyValid());
		assertEquals(new UCD(new UCDWord[]{new UCDWord("meta.id")}), ucd.getSuggestion());

		ucd = new UCD(new UCDWord[]{new UCDWord("foo.bar", new UCDWord[]{new UCDWord(UCDSyntax.PRIMARY, "custom:foo.bar", null, true)})});
		assertFalse(ucd.isFullyValid());
		assertEquals(new UCD(new UCDWord[]{new UCDWord("custom:foo.bar")}), ucd.getSuggestion());

		/* CASE: Word with spaces */

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.BOTH, " pos.eq.ra", null, false)});
		assertFalse(ucd.isFullyValid());
		suggestion = ucd.getSuggestion();
		assertNotNull(suggestion);
		assertTrue(suggestion.isFullyValid());
		assertEquals("pos.eq.ra", suggestion.toString());

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.BOTH, " phot flux ", null, false)});
		assertFalse(ucd.isFullyValid());
		suggestion = ucd.getSuggestion();
		assertNotNull(suggestion);
		assertTrue(suggestion.isFullyValid());
		assertEquals("phot.flux", suggestion.toString());

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.BOTH, " phot flux	  ", null, false),new UCDWord(UCDSyntax.BOTH, " meta.main", null, false)});
		assertFalse(ucd.isFullyValid());
		suggestion = ucd.getSuggestion();
		assertNotNull(suggestion);
		assertTrue(suggestion.isFullyValid());
		assertEquals("phot.flux;meta.main", suggestion.toString());

		/* CASE: Wrong order */

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.SECONDARY, "meta.main", null, true),new UCDWord(UCDSyntax.BOTH, "pos.eq.ra", null, false)});
		assertFalse(ucd.isFullyValid());
		suggestion = ucd.getSuggestion();
		assertNotNull(suggestion);
		assertTrue(suggestion.isFullyValid());
		assertEquals("pos.eq.ra;meta.main", suggestion.toString());

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.PHOT_QUANTITY, "phot.flux", null, true),new UCDWord(UCDSyntax.PRIMARY, "arith.factor", null, true)});
		assertFalse(ucd.isFullyValid());
		suggestion = ucd.getSuggestion();
		assertNotNull(suggestion);
		assertTrue(suggestion.isFullyValid());
		assertEquals("arith.factor;phot.flux", suggestion.toString());

		/* CASE: Only PRIMARY */

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.PRIMARY, "meta.id", null, false),new UCDWord(UCDSyntax.PRIMARY, "arith.factor", null, false)});
		assertFalse(ucd.isFullyValid());
		suggestion = ucd.getSuggestion();
		assertNotNull(suggestion);
		assertTrue(suggestion.isFullyValid());
		assertEquals("meta.id", suggestion.toString());

		/* CASE: Only SECONDARY */

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.SECONDARY, "meta.main", null, false)});
		assertFalse(ucd.isFullyValid());
		assertNull(ucd.getSuggestion());

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.SECONDARY, "meta.main", null, false),new UCDWord(UCDSyntax.SECONDARY, "em.radio", null, false)});
		assertFalse(ucd.isFullyValid());
		assertNull(ucd.getSuggestion());
	}

	@Test
	public void testIsAllValid(){

		/* CASE: Only one word */
		/* 	SUBCASE: Null or empty */

		UCD ucd = new UCD(new UCDWord[]{null});
		assertFalse(ucd.isAllValid());

		ucd = new UCD(new UCDWord[]{new UCDWord("")});
		assertFalse(ucd.isAllValid());

		ucd = new UCD(new UCDWord[]{new UCDWord(" ")});
		assertFalse(ucd.isAllValid());

		/* 	SUBCASE: Wrong words */

		ucd = new UCD(new UCDWord[]{new UCDWord("_foo")});
		assertFalse(ucd.isAllValid());

		ucd = new UCD(new UCDWord[]{new UCDWord(" meta.id")});
		assertFalse(ucd.isAllValid());

		/* 	SUBCASE: Valid word */

		ucd = new UCD(new UCDWord[]{new UCDWord("meta.id")});
		assertTrue(ucd.isAllValid());

		/*  SUBCASE: Ensure the parsing is case INsensitive */

		ucd = new UCD(new UCDWord[]{new UCDWord("META.id")});
		assertTrue(ucd.isAllValid());

		ucd = new UCD(new UCDWord[]{new UCDWord("meta.ID")});
		assertTrue(ucd.isAllValid());

		ucd = new UCD(new UCDWord[]{new UCDWord("META.ID")});
		assertTrue(ucd.isAllValid());

		ucd = new UCD(new UCDWord[]{new UCDWord("MetA.Id")});
		assertTrue(ucd.isAllValid());

		/* CASE: More words */
		/* 	SUBCASE: All valid words */

		ucd = new UCD(new UCDWord[]{new UCDWord("pos.eq.ra"),new UCDWord("meta.main")});
		assertTrue(ucd.isAllValid());

		ucd = new UCD(new UCDWord[]{new UCDWord("phot.flux"),new UCDWord("em.opt.U"),new UCDWord("meta.main")});
		assertTrue(ucd.isAllValid());

		ucd = new UCD(new UCDWord[]{new UCDWord("ivoa:phot.flux"),new UCDWord("em.opt.U"),new UCDWord("custom:my.ucd_word")});
		assertTrue(ucd.isAllValid());

		/*  SUBCASE: Ensure the parsing is case INsensitive */

		ucd = new UCD(new UCDWord[]{new UCDWord("POS.eq.ra"),new UCDWord("meta.MAIN")});
		assertTrue(ucd.isAllValid());

		ucd = new UCD(new UCDWord[]{new UCDWord("Phot.Flux"),new UCDWord("em.OPT.u"),new UCDWord("Meta.MAIN")});
		assertTrue(ucd.isAllValid());

		ucd = new UCD(new UCDWord[]{new UCDWord("IVOA:phot.flux"),new UCDWord("em.opt.U"),new UCDWord("Custom:My.UCD_Word")});
		assertTrue(ucd.isAllValid());

		/* 	SUBCASE: At least one missing word */

		ucd = new UCD(new UCDWord[]{new UCDWord("phot.flux"),null});
		assertFalse(ucd.isAllValid());

		ucd = new UCD(new UCDWord[]{new UCDWord("phot.flux"),new UCDWord(" "),new UCDWord("em.opt.U")});
		assertFalse(ucd.isAllValid());

		ucd = new UCD(new UCDWord[]{null,new UCDWord(" "),new UCDWord("em.opt.U")});
		assertFalse(ucd.isAllValid());

		ucd = new UCD(new UCDWord[]{null,new UCDWord(" "),new UCDWord(" "),null,null});
		assertFalse(ucd.isAllValid());

		/* 	SUBCASE: At least one not valid word */

		ucd = new UCD(new UCDWord[]{new UCDWord("my!position"),new UCDWord("meta.main")});
		assertFalse(ucd.isAllValid());

		ucd = new UCD(new UCDWord[]{new UCDWord(" pos.eq.ra"),new UCDWord("meta.main")});
		assertFalse(ucd.isAllValid());

		ucd = new UCD(new UCDWord[]{new UCDWord("meta.info.main"),new UCDWord("photo!metry"),new UCDWord("#optic")});
		assertFalse(ucd.isAllValid());
	}

	@Test
	public void testIsAllRecognised(){

		/* CASE: Only one word */
		/* 	SUBCASE: Null or empty */

		UCD ucd = new UCD(new UCDWord[]{null});
		assertFalse(ucd.isAllRecognised());

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.PRIMARY, "", null, false)});
		assertFalse(ucd.isAllRecognised());

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.PRIMARY, " ", null, false)});
		assertFalse(ucd.isAllRecognised());

		/* 	SUBCASE: Wrong words */

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.PRIMARY, "_foo", null, false)});
		assertFalse(ucd.isAllRecognised());

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.SECONDARY, " meta.main", null, false)});
		assertFalse(ucd.isAllRecognised());

		/* 	SUBCASE: Valid word */

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.SECONDARY, "meta.main", null, false)});
		assertTrue(ucd.isAllRecognised());

		/* 	SUBCASE: No syntax code*/

		ucd = new UCD(new UCDWord[]{new UCDWord(null, "meta.main", null, false)});
		assertFalse(ucd.isAllRecognised());

		/* CASE: More words */
		/* 	SUBCASE: All recognised words */

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.SECONDARY, "meta.main", null, false),new UCDWord(UCDSyntax.BOTH, "pos.eq.ra", null, false)});
		assertTrue(ucd.isAllRecognised());

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.PHOT_QUANTITY, "phot.flux", null, false),new UCDWord(UCDSyntax.SECONDARY, "em.opt.U", null, false),new UCDWord(UCDSyntax.SECONDARY, "meta.main", null, false)});
		assertTrue(ucd.isAllRecognised());

		/* 	SUBCASE: At least one missing word */

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.BOTH, "pos.eq.ra", null, false),null});
		assertFalse(ucd.isAllRecognised());

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.PHOT_QUANTITY, "phot.flux", null, false),null,new UCDWord(UCDSyntax.SECONDARY, "meta.main", null, false)});
		assertFalse(ucd.isAllRecognised());

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.SECONDARY, "", null, false),new UCDWord(UCDSyntax.BOTH, "pos.eq.ra", null, false)});
		assertFalse(ucd.isAllRecognised());

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.SECONDARY, " ", null, false),new UCDWord(UCDSyntax.BOTH, "pos.eq.ra", null, false)});
		assertFalse(ucd.isAllRecognised());

		/* 	SUBCASE: At least one not valid word => NOT RECOGNISED */

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.PRIMARY, "_foo", null, false),new UCDWord(UCDSyntax.BOTH, "pos.eq.ra", null, false)});
		assertFalse(ucd.isAllRecognised());

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.BOTH, " pos.eq.ra", null, false),new UCDWord(UCDSyntax.SECONDARY, "meta.main", null, false)});
		assertFalse(ucd.isAllRecognised());

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.PRIMARY, "meta.main@info", null, false),new UCDWord(UCDSyntax.PHOT_QUANTITY, "photometry", null, false),new UCDWord(UCDSyntax.SECONDARY, "optic", null, false)});
		assertFalse(ucd.isAllRecognised());
	}

	@Test
	public void testIsAllRecommended(){

		/* CASE: Only one word */
		/* 	SUBCASE: Null or empty */

		UCD ucd = new UCD(new UCDWord[]{null});
		assertFalse(ucd.isAllRecommended());

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.PRIMARY, "", null, true)});
		assertFalse(ucd.isAllRecommended());

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.PRIMARY, " ", null, true)});
		assertFalse(ucd.isAllRecommended());

		/* 	SUBCASE: Wrong words */

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.PRIMARY, "_foo", null, true)});
		assertFalse(ucd.isAllRecommended());

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.PRIMARY, " meta.id", null, true)});
		assertFalse(ucd.isAllRecommended());

		/* 	SUBCASE: Valid word */

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.PRIMARY, "meta.id", null, true)});
		assertTrue(ucd.isAllRecommended());

		/* 	SUBCASE: No syntax code*/

		ucd = new UCD(new UCDWord[]{new UCDWord(null, "meta.main", null, true)});
		assertFalse(ucd.isAllRecommended());

		/* CASE: More words */
		/* 	SUBCASE: All recognised + recommended words */

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.SECONDARY, "meta.main", null, true),new UCDWord(UCDSyntax.BOTH, "pos.eq.ra", null, true)});
		assertTrue(ucd.isAllRecognised());
		assertTrue(ucd.isAllRecommended());

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.PHOT_QUANTITY, "phot.flux", null, true),new UCDWord(UCDSyntax.SECONDARY, "em.opt.U", null, true),new UCDWord(UCDSyntax.SECONDARY, "meta.main", null, true)});
		assertTrue(ucd.isAllRecognised());
		assertTrue(ucd.isAllRecommended());

		/* 	SUBCASE: All recognised but not flagged manually as recommended */

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.BOTH, "meta.main", null, false),new UCDWord(UCDSyntax.BOTH, "pos.eq.ra", null, true)});
		assertTrue(ucd.isAllRecognised());
		assertFalse(ucd.isAllRecommended());

		/* 	SUBCASE: All valid but not recognised words */

		ucd = new UCD(new UCDWord[]{new UCDWord(null, "meta.main", null, true),new UCDWord(UCDSyntax.BOTH, "pos.eq.ra", null, true)});
		assertFalse(ucd.isAllRecognised());
		assertFalse(ucd.isAllRecommended());

		/* 	SUBCASE: At least one missing word */

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.BOTH, "pos.eq.ra", null, true),null});
		assertFalse(ucd.isAllRecommended());

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.PHOT_QUANTITY, "phot.flux", null, true),null,new UCDWord(UCDSyntax.SECONDARY, "meta.main", null, true)});
		assertFalse(ucd.isAllRecommended());

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.SECONDARY, "", null, true),new UCDWord(UCDSyntax.BOTH, "pos.eq.ra", null, true)});
		assertFalse(ucd.isAllRecommended());

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.SECONDARY, " ", null, true),new UCDWord(UCDSyntax.BOTH, "pos.eq.ra", null, true)});
		assertFalse(ucd.isAllRecommended());

		/* 	SUBCASE: At least one not valid word => NOT RECOGNISED */

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.PRIMARY, "_foo", null, true),new UCDWord(UCDSyntax.BOTH, "pos.eq.ra", null, true)});
		assertFalse(ucd.isAllRecommended());

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.BOTH, " pos.eq.ra", null, true),new UCDWord(UCDSyntax.SECONDARY, "meta.main", null, true)});
		assertFalse(ucd.isAllRecommended());

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.PRIMARY, "meta.main@info", null, true),new UCDWord(UCDSyntax.PHOT_QUANTITY, "photometry", null, true),new UCDWord(UCDSyntax.SECONDARY, "optic", null, true)});
		assertFalse(ucd.isAllRecommended());
	}

	@Test
	public void testIsFullyValid(){

		/* CASE: Not fully valid if NOT ALL RECOGNIZED */

		UCD ucd = new UCD(new UCDWord[]{new UCDWord(null, "meta.id", null, false)});
		assertTrue(ucd.isAllValid());
		assertFalse(ucd.isAllRecognised());
		assertFalse(ucd.isFullyValid());

		/* CASE: Starting with a SECONDARY */

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.SECONDARY, "meta.main", null, true),new UCDWord(UCDSyntax.BOTH, "pos.eq.ra", null, true)});
		assertTrue(ucd.isAllValid());
		assertTrue(ucd.isAllRecognised());
		assertFalse(ucd.isFullyValid());

		/* CASE: A PRIMARY not in first position */

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.PHOT_QUANTITY, "phot.flux", null, true),new UCDWord(UCDSyntax.PRIMARY, "arith.factor", null, true)});
		assertTrue(ucd.isAllValid());
		assertTrue(ucd.isAllRecognised());
		assertFalse(ucd.isFullyValid());

		/* CASE: Fully valid */
		/* 	SUBCASE: fully valid but not all recommended */

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.BOTH, "custom:my.ucd_word", null, false),new UCDWord(UCDSyntax.SECONDARY, "meta.main", null, true)});
		assertTrue(ucd.isAllValid());
		assertTrue(ucd.isAllRecognised());
		assertFalse(ucd.isAllRecommended());
		assertTrue(ucd.isFullyValid());

		/* 	SUBCASE: fully valid and recommended */

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.BOTH, "pos.eq.ra", null, true),new UCDWord(UCDSyntax.SECONDARY, "meta.main", null, true)});
		assertTrue(ucd.isAllValid());
		assertTrue(ucd.isAllRecognised());
		assertTrue(ucd.isAllRecommended());
		assertTrue(ucd.isFullyValid());

		ucd = new UCD(new UCDWord[]{new UCDWord(UCDSyntax.BOTH, "ivoa:pos.eq.ra", null, true),new UCDWord(UCDSyntax.SECONDARY, "meta.main", null, true)});
		assertTrue(ucd.isAllValid());
		assertTrue(ucd.isAllRecognised());
		assertTrue(ucd.isAllRecommended());
		assertTrue(ucd.isFullyValid());
	}

	@Test
	public void testEquals(){
		// Equality:
		UCD ucd1 = new UCD(new UCDWord[]{new UCDWord("meta.id")});
		UCD ucd2 = new UCD(new UCDWord[]{new UCDWord("meta.id")});
		assertEquals(ucd1, ucd2);
		assertEquals(ucd2, ucd1);
		assertEquals(ucd1.hashCode(), ucd2.hashCode());

		// Case INsensitive:
		ucd2 = new UCD(new UCDWord[]{new UCDWord("META.Id")});
		assertEquals(ucd1, ucd2);
		assertEquals(ucd2, ucd1);
		assertEquals(ucd1.hashCode(), ucd2.hashCode());

		// With the optional "ivoa" namespace:
		ucd2 = new UCD(new UCDWord[]{new UCDWord("ivoa:meta.id")});
		assertEquals(ucd1, ucd2);
		assertEquals(ucd2, ucd1);
		assertEquals(ucd1.hashCode(), ucd2.hashCode());

		// Not equality:
		ucd2 = new UCD(new UCDWord[]{new UCDWord("pos.eq.ra")});
		assertNotEquals(ucd1, ucd2);
		assertNotEquals(ucd2, ucd1);
		assertNotEquals(ucd1.hashCode(), ucd2.hashCode());

		// Test with NULL:
		assertFalse(ucd1.equals(null));
	}

}
