package ari.ucd;

/*
 * This file is part of UV (UcdValidator).
 *
 * UV is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UV is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with UV.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2016 - Astronomisches Rechen Institut (ARI)
 */

/**
 * Definition of a UCD1+ word, according to the IVOA.
 *
 * <p><i>See http://www.ivoa.net/documents/REC/UCD/UCDlist-20070402.html for more details.</i></p>
 *
 * <p>
 * 	{@link UCDWord}s are comparable each other.
 * 	The comparison is done case insensitively on the {@link #word} value.
 * </p>
 *
 * @author Gr&eacute;gory Mantelet (ARI)
 * @version 1.0 (06/2016)
 */
public class UCDWord implements Comparable<UCDWord> {

	/** Regular Expression for a valid UCD, according to a personal interpretation of the IVOA document about UCD1+:
	 * http://www.ivoa.net/documents/REC/UCD/UCDlist-20070402.html
	 *
	 * <p>
	 * 	Textually, this regular expression says that a UCD MUST start with one of the following atoms:
	 * 	arith, em, instr, meta, obs, phot, phys, pos, spect, src, stat or time. Then it MAY be followed by other atoms
	 * 	that MUST be separated by '.'. None of the atoms can contain a space character (e.g. ' ', a tabulation, a carriage return)
	 * 	or a semi-colon (i.e. ';').
	 * </p>
	 */
	public final static String REGEXP_UCD_WORD = "(arith|em|instr|meta|obs|phot|phys|pos|spect|src|stat|time)(\\.[^\\s;]+)?";

	/** Rule about the syntax of the usage of this UCD1+ word.
	 * <i>(see {@link UCDSyntax} for more details)</i>
	 * <p><i>May be <code>null</code>. If so, this UCD1+ word can NOT be recommended.</i></p> */
	public final UCDSyntax syntaxCode;

	/** The UCD1+ word.
	 * <p><i>Can NOT be <code>null</code>.</i></p> */
	public final String word;

	/** Human description of this UCD1+ word.
	 * <p><i>May be <code>null</code>.</i></p> */
	public final String description;

	/** A UCD1+ is <i>valid</i> if its syntax is correct.
	 *
	 * <p>In other words:</p>
	 * <ul>
	 * 	<li>if it starts with one of the following atoms:
	 * 		arith, em, instr, meta, obs, phot, phys, pos, spect, src, stat or time.</li>
	 * 	<li>then it MAY be followed by other atoms that MUST be separated by '.'.</li>
	 * 	<li>none of the atoms can contain a space character (e.g. ' ', a tabulation, a carriage return)
	 * 		or a semi-colon (i.e. ';').</li>
	 * </ul>
	 * <p>
	 * 	All these rules are expressed by a regular expression: {@link #REGEXP_UCD_WORD}.
	 * 	So, this attribute will be <code>true</code> if it matches this regular expression.
	 * </p>
	 */
	public final boolean valid;

	/**
	 * A UCD word is <i>recognised</i> if among a list of well-known UCD words (not necessarily the ones provided by the IVOA).
	 *
	 * <p><b>Important:</b> A <i>recognised</i> UCD1+ word MUST be <i>{@link #valid}</i> AND its {@link #syntaxCode} MUST be set.</p>
	 */
	public final boolean recognised;

	/**
	 * A UCD word is <i>recommended</i> if allowed by the IVOA, according to the document:
	 * http://www.ivoa.net/documents/REC/UCD/UCDlist-20070402.html
	 *
	 * <p><b>Important:</b> A <i>recommended</i> UCD MUST be <i>{@link #recognised}</i>.</p>
	 */
	public final boolean recommended;

	/**
	 * Create a <i>{@link #recognised}</i> (under conditions, see below) UCD1+ word.
	 *
	 * <p><b>Conditions:</b></p>
	 * <ul>
	 * 	<li><i>To be {@link #valid}:</i> the syntax of this UCD1+ word MUST be correct. See {@link #valid} for more details.</li>
	 * 	<li><i>To be {@link #recognised}:</i>  it MUST be {@link #valid}
	 * 	                                       AND the syntax code MUST be correct. See {@link UCDSyntax} for more details.</li>
	 * 	<li><i>To be {@link #recommended}:</i> it MUST be {@link #recognised}
	 * 	                                       AND the parameter <code>{@link #recommended}</code> MUST be set to <code>true</code>.</li>
	 * </ul>
	 *
	 * @param syntax		Rule about the syntax when using this UCD1+ word.
	 *              		<i>(see {@link UCDSyntax} for more details)</i>
	 * @param word			The UCD1+ word, itself.
	 * @param description	Human description of this UCD1+ word.
	 * @param recommended	<code>true</code> to consider this UCD1+ word as <i>{@link #recommended}</i> by the IVOA,
	 *                   	<code>false</code> otherwise.
	 *
	 * @throws NullPointerException	If the given word is <code>null</code>.
	 */
	public UCDWord(final UCDSyntax syntax, final String word, final String description, final boolean recommended) throws NullPointerException{
		if (word == null)
			throw new NullPointerException("Missing UCD word!");

		this.syntaxCode = syntax;
		this.word = word;
		this.description = description;
		this.valid = this.word.matches(REGEXP_UCD_WORD);
		this.recognised = (this.valid && syntaxCode != null);
		this.recommended = (recommended && this.recognised);
	}

	/**
	 * Create a NON <i>{@link #recommended}</i> and NON <i>{@link #recognised}</i> UCD1+ word.
	 *
	 * <p>However, it may be flagged as <i>{@link #valid}</i> if its structure is correct.</p>
	 *
	 * @param word	A UCD1+ word.
	 *
	 * @throws NullPointerException	If the given word is <code>null</code> or an empty string.
	 */
	public UCDWord(final String word) throws NullPointerException{
		if (word == null)
			throw new NullPointerException("Missing UCD word!");

		this.syntaxCode = null;
		this.word = word;
		this.description = null;
		this.valid = this.word.matches(REGEXP_UCD_WORD);
		this.recognised = false;
		this.recommended = false;
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
