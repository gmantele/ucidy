package ari.ucd;

import java.util.Objects;

/**
 * Definition of a UCD word, according to the IVOA (v1.1 - 12 August 2005).
 *
 * <p><i>See http://www.ivoa.net/documents/REC/UCD/UCD-20050812.html for more details.</i></p>
 *
 * @author Gr&eacute;gory Mantelet (ARI)
 * @version 1.0 (02/2017)
 */
public class UCDWord {

	/** Character used to separate the namespace prefix from a UCD word. */
	protected final static String NAMESPACE_SEP = ":";

	/** Default namespace: <code>ivoa</code>.
	 * When no namespace is specified, this is the one implied.
	 * However, it MUST be used only for words recommended by the IVOA. */
	protected final static String IVOA_NAMESPACE = "ivoa";

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
	 * 	Textually, this regular expression says that a UCD is a composition of a possible namespace and of at least one atom.
	 * 	All atoms MUST be separated by a period (.). The namespace syntax must be the same as an atom.
	 * </p> */
	public final static String REGEXP_UCD_WORD = "(" + REGEXP_UCD_ATOM + ":)?" + REGEXP_UCD_ATOM + "(\\." + REGEXP_UCD_ATOM + ")*";

	/** Rule about the syntax of the usage of this UCD word.
	 * <i>(see {@link UCDSyntax} for more details)</i>
	 * <p><i>May be <code>null</code>. If so, this UCD word can NOT be recommended.</i></p> */
	public final UCDSyntax syntaxCode;

	/** The UCD word as provided (may have a namespace prefix).
	 * <p><i>Can NOT be <code>null</code>.</i></p>
	 * @see #namespace
	 * @see #word */
	public final String rawWord;

	/** The namespace of this UCD, as extracted from {@link #rawWord}.
	 * <p><i>This attribute is <code>null</code> if {@link #rawWord} is not valid or does not have any namespace prefix.</i></p> */
	public final String namespace;

	/** The UCD word (without namespace prefix), as extracted from {@link #rawWord}.
	 * <p>
	 * 	This attribute is the resulting part of {@link #rawWord} when its namespace prefix has been extracted ({@link #namespace}).
	 * 	If {@link #rawWord} is not valid or if no namespace is specified, this attribute will have exactly the same value as {@link #rawWord}.
	 * </p>
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
	 * <p><b>Important 1:</b> A <i>recommended</i> UCD MUST be <i>{@link #recognised}</i>
	 *                        AND MUST have either no namespace or have the namespace {@value #IVOA_NAMESPACE}.</p>
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
	 * 	                                       AND the parameter <code>{@link #recommended}</code> MUST be set to <code>true</code>
	 * 	                                       AND the namespace MUST be <code>null</code> or {@value #IVOA_NAMESPACE}.</li>
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
		this.rawWord = word;
		this.description = description;

		// split the namespace and the word if necessary:
		int indSep = this.rawWord.indexOf(NAMESPACE_SEP);
		if (indSep > 0){
			this.namespace = this.rawWord.substring(0, indSep);
			this.word = this.rawWord.substring(indSep + 1);
		}else{
			this.namespace = null;
			this.word = this.rawWord;
		}

		// set the flags:
		this.valid = this.rawWord.matches(REGEXP_UCD_WORD);
		this.recognised = (this.valid && syntaxCode != null);
		this.recommended = (recommended && this.recognised && (this.namespace == null || this.namespace.equalsIgnoreCase(IVOA_NAMESPACE)));
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
		this.rawWord = word;
		this.description = null;

		// split the namespace and the word if necessary:
		int indSep = this.rawWord.indexOf(NAMESPACE_SEP);
		if (indSep > 0){
			this.namespace = this.rawWord.substring(0, indSep);
			this.word = this.rawWord.substring(indSep + 1);
		}else{
			this.namespace = null;
			this.word = this.rawWord;
		}

		// set the flags:
		this.valid = this.rawWord.matches(REGEXP_UCD_WORD);
		this.recognised = false;
		this.recommended = false;

		// set the given closest matches:
		this.closest = (closestMatches != null && closestMatches.length == 0) ? null : closestMatches;
	}

	/* ****************** */
	/* EQUALITY FUNCTIONS */
	/* ****************** */

	@Override
	public boolean equals(final Object obj){
		if (obj != null && obj instanceof UCDWord){
			UCDWord anotherWord = (UCDWord)obj;
			if (word.equalsIgnoreCase(anotherWord.word))
				return ((namespace == null || namespace.equalsIgnoreCase(IVOA_NAMESPACE)) && (anotherWord.namespace == null || anotherWord.namespace.equalsIgnoreCase(IVOA_NAMESPACE))) || (namespace != null && anotherWord.namespace != null && namespace.equalsIgnoreCase(anotherWord.namespace));
		}
		return false;
	}

	@Override
	public int hashCode(){
		return (namespace == null) ? Objects.hash(IVOA_NAMESPACE, word.toLowerCase()) : Objects.hash(namespace.toLowerCase(), word.toLowerCase());
	}

	/* ******************** */
	/* STRING SERIALIZATION */
	/* ******************** */

	@Override
	public String toString(){
		return rawWord;
	}

}
