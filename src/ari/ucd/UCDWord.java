package ari.ucd;

/**
 * Definition of a UCD word, according to the IVOA (v1.1 - 12 August 2005).
 *
 * <p><i>See http://www.ivoa.net/documents/REC/UCD/UCD-20050812.html for more details.</i></p>
 *
 * <p>
 * 	{@link UCDWord}s are comparable each other.
 * 	The comparison is done case insensitively on the {@link #word} value.
 * </p>
 *
 * @author Gr&eacute;gory Mantelet (ARI)
 * @version 1.0 (02/2017)
 */
public class UCDWord implements Comparable<UCDWord> {

	/**
	 * Regular Expression for a valid UCD's atom, according to the BNF provided in the IVOA Recommendation 2005-08-12 for UCD v1.1:
	 * http://www.ivoa.net/documents/REC/UCD/UCD-20050812.html
	 *
	 * <p>
	 * 	Textually, this regular expression says that a UCD atom can contain ONLY (lower or upper case) letters, digits, hyphens and
	 *  underscores, AND must NOT start with an hyphen or an underscore.
	 * </p>
	 */
	public final static String REGEXP_UCD_ATOM = "[a-zA-Z0-9][a-zA-Z0-9\\-_]*";

	/** Regular Expression for a valid UCD, according to the BNF provided in the IVOA Recommendation 2005-08-12 for UCD v1.1:
	 * http://www.ivoa.net/documents/REC/UCD/UCD-20050812.html
	 *
	 * <p>
	 * 	Textually, this regular expression says that a UCD is a composition of at least one atom.
	 * 	All atoms MUST be separated by a period (.).
	 * </p> */
	public final static String REGEXP_UCD_WORD = REGEXP_UCD_ATOM + "(\\." + REGEXP_UCD_ATOM + ")*";

	/** Rule about the syntax of the usage of this UCD word.
	 * <i>(see {@link UCDSyntax} for more details)</i>
	 * <p><i>May be <code>null</code>. If so, this UCD word can NOT be recommended.</i></p> */
	public final UCDSyntax syntaxCode;

	/** The UCD word.
	 * <p><i>Can NOT be <code>null</code>.</i></p> */
	public final String word;

	/** Human description of this UCD word.
	 * <p><i>May be <code>null</code>.</i></p> */
	public final String description;

	/** A UCD is <i>valid</i> if its syntax is correct.
	 *
	 * <p>In other words:</p>
	 * <ul>
	 * 	<li>if all atoms are composed of letters, digits, hyphens and/or underscores, BUT does not start with an hyphen or an underscore</li>
	 * 	<li>atoms MUST be separated by '.'</li></li>
	 * </ul>
	 * <p>
	 * 	All these rules are expressed by a regular expression: {@link #REGEXP_UCD_WORD}.
	 * 	So, this attribute will be <code>true</code> if it matches this regular expression.
	 * </p>
	 *
	 * <p><i><b>Note:</b> This test is performed case INsensitively.</i></p>
	 */
	public final boolean valid;

	/**
	 * A UCD word is <i>recognised</i> if among a list of well-known UCD words (not necessarily the ones provided by the IVOA).
	 *
	 * <p><b>Important:</b> A <i>recognised</i> UCD word MUST be <i>{@link #valid}</i> AND its {@link #syntaxCode} MUST be set.</p>
	 */
	public final boolean recognised;

	/**
	 * A UCD word is <i>recommended</i> if allowed by the IVOA, according to IVOA Recommendation 2007-04-02 for the UCD1+ controlled vocabulary v1.23:
	 * http://www.ivoa.net/documents/REC/UCD/UCDlist-20070402.html
	 *
	 * <p><b>Important 1:</b> A <i>recommended</i> UCD MUST be <i>{@link #recognised}</i>.</p>
	 *
	 * <p><i><b>Important 2:</b> This test must have been performed case INsensitively.</i></p>
	 */
	public final boolean recommended;

	/**
	 * This attribute is set by {@link UCDParser#parse(String)} when a UCD word can not be recognised.
	 * Then the parse function tries to find the closest UCD words of its list of recognised words.
	 * If it finds any, it set this attribute with them using {@link #UCDWord(String, UCDWord[])}.
	 *
	 * <p><i><b>Important note:</b>
	 * 	If this {@link UCDWord} is recognised, this attribute is <code>null</code>.
	 * 	If set, this array WILL always contain at least one item.
	 * 	Besides, if it is set the closest words specified here SHOULD always be recognised ;
	 * 	it is the responsibility of the provider of this array to ensure the "recognised" status
	 * 	of all given words.
	 * </i></p> */
	public final UCDWord[] closest;

	/**
	 * Create a <i>{@link #recognised}</i> (under conditions, see below) UCD word.
	 *
	 * <p><b>Conditions:</b></p>
	 * <ul>
	 * 	<li><i>To be {@link #valid}:</i> the syntax of this UCD word MUST be correct. See {@link #valid} for more details.</li>
	 * 	<li><i>To be {@link #recognised}:</i>  it MUST be {@link #valid}
	 * 	                                       AND the syntax code MUST be correct. See {@link UCDSyntax} for more details.</li>
	 * 	<li><i>To be {@link #recommended}:</i> it MUST be {@link #recognised}
	 * 	                                       AND the parameter <code>{@link #recommended}</code> MUST be set to <code>true</code>.</li>
	 * </ul>
	 *
	 * @param syntax		Rule about the syntax when using this UCD word.
	 *              		<i>(see {@link UCDSyntax} for more details)</i>
	 * @param word			The UCD word, itself.
	 * @param description	Human description of this UCD word.
	 * @param recommended	<code>true</code> to consider this UCD word as <i>{@link #recommended}</i> by the IVOA,
	 *                   	<code>false</code> otherwise.
	 *
	 * @throws NullPointerException	If the given word is <code>null</code>.
	 */
	public UCDWord(final UCDSyntax syntax, final String word, final String description, final boolean recommended) throws NullPointerException{
		if (word == null)
			throw new NullPointerException("Missing UCD word!");

		// set the UCD word definition:
		this.syntaxCode = syntax;
		this.word = word;
		this.description = description;

		// set the flags:
		this.valid = this.word.matches(REGEXP_UCD_WORD);
		this.recognised = (this.valid && syntaxCode != null);
		this.recommended = (recommended && this.recognised);
		this.closest = null;
	}

	/**
	 * Create a NON <i>{@link #recommended}</i> and NON <i>{@link #recognised}</i> UCD word.
	 *
	 * <p>However, it may be flagged as <i>{@link #valid}</i> if its structure is correct.</p>
	 *
	 * @param word	A UCD word.
	 *
	 * @throws NullPointerException	If the given word is <code>null</code> or an empty string.
	 */
	public UCDWord(final String word) throws NullPointerException{
		this(word, null);
	}

	/**
	 * Create a NON <i>{@link #recommended}</i> and NON <i>{@link #recognised}</i> UCD word.
	 *
	 * <p>However, it may be flagged as <i>{@link #valid}</i> if its structure is correct.</p>
	 *
	 * <p><b>IMPORTANT:</b>
	 * 	The {@link UCDWord} provided in the given array - closestMatches - SHOULD contain
	 * 	ONLY recognised words. This fact won't be tested by this constructor.
	 * </p>
	 *
	 * @param word				A UCD word.
	 * @param closestMatches	Closest recognised matches found in the list of recognised words by the {@link UCDParser}.
	 *
	 * @throws NullPointerException	If the given word is <code>null</code> or an empty string.
	 */
	protected UCDWord(final String word, final UCDWord[] closestMatches) throws NullPointerException{
		if (word == null)
			throw new NullPointerException("Missing UCD word!");

		// set the UCD word definition:
		this.syntaxCode = null;
		this.word = word;
		this.description = null;

		// set the flags:
		this.valid = this.word.matches(REGEXP_UCD_WORD);
		this.recognised = false;
		this.recommended = false;

		// set the given closest matches:
		this.closest = (closestMatches != null && closestMatches.length == 0) ? null : closestMatches;
	}

	/* ******************** */
	/* COMPARISON FUNCTIONS */
	/* ******************** */

	@Override
	public int compareTo(final UCDWord anotherWord){
		if (anotherWord == null)
			return 1;
		else
			return word.compareToIgnoreCase(anotherWord.word);
	}

	@Override
	public boolean equals(final Object obj){
		return (obj != null && obj instanceof UCDWord && word.equalsIgnoreCase(((UCDWord)obj).word));
	}

	@Override
	public int hashCode(){
		return word.toLowerCase().hashCode();
	}

	/* ******************** */
	/* STRING SERIALIZATION */
	/* ******************** */

	@Override
	public String toString(){
		return word;
	}

}
