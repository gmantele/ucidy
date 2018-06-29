package ari.ucidy;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Before;
import org.junit.Test;

public class TestDeprecatedUCDWordList {

	private final DeprecatedUCDWordList words;

	public TestDeprecatedUCDWordList() throws NullPointerException, IOException {
		UCDWordList knownWords = UCDParser.parseWordList(new InputStreamReader(UCDWordList.class.getResourceAsStream("/ucd1p-words.txt")), true);
		words = new DeprecatedUCDWordList(knownWords);
	}

	@Before
	public void setUp() throws Exception {
		words.add(new UCDWord("pos.eop.nutation", new UCD(new UCDWord[] { new UCDWord(UCDSyntax.PRIMARY, "pos.nutation", null, true) }), null, null));
		words.add(new UCDWord("phot.color.B-R", new UCD(new UCDWord[] { new UCDWord(UCDSyntax.COLOUR, "phot.color", null, true), new UCDWord(UCDSyntax.PHOT_QUANTITY, "em.opt.B", null, true), new UCDWord(UCDSyntax.PHOT_QUANTITY, "em.opt.R", null, true) }), null, null));
	}

	@Test
	public void testAddUCDWord() {

		/* CASE: Failed attempt to add NULL */

		assertFalse(words.add(null));

		/* CASE: Failed attempt to add a non-deprecated word */

		assertFalse(words.add(new UCDWord("pos.eq.ra")));

		/* CASE: Failed attempt to add a deprecated word already listed */

		assertFalse(words.add(new UCDWord("pos.eop.nutation", new UCD(new UCDWord[] { new UCDWord(UCDSyntax.PRIMARY, "pos.nutation", null, true) }), null, null)));

		/* CASE: Successful addition of a deprecated word */

		assertTrue(words.add(new UCDWord("pos.ang", new UCD(new UCDWord[] { new UCDWord(UCDSyntax.PRIMARY, "pos", null, true) }), null, null)));

	}

}
