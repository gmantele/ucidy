package ari.ucidy;

import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.jupiter.api.Assertions.*;

class TestDeprecatedUCDWordList {

	private final DeprecatedUCDWordList words;

	public TestDeprecatedUCDWordList() throws NullPointerException, IOException {
		final InputStream inputWordsFile = UCDWordList.class.getResourceAsStream(UCDParser.FILE_UCD_WORDS);
		assertNotNull(inputWordsFile);

		final UCDWordList knownWords = UCDParser.parseWordList(new InputStreamReader(inputWordsFile), true);
		words = new DeprecatedUCDWordList(knownWords);
	}

	@BeforeEach
	void setUp() throws Exception {
		words.add(new UCDWord("pos.eop.nutation", new UCD(new UCDWord[] { new UCDWord(UCDSyntax.PRIMARY, "pos.nutation", null, true) }), null, null));
		words.add(new UCDWord("phot.color.B-R", new UCD(new UCDWord[] { new UCDWord(UCDSyntax.COLOUR, "phot.color", null, true), new UCDWord(UCDSyntax.PHOT_QUANTITY, "em.opt.B", null, true), new UCDWord(UCDSyntax.PHOT_QUANTITY, "em.opt.R", null, true) }), null, null));
	}

	@org.junit.jupiter.api.Test
	void testAddUCDWord() {

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
