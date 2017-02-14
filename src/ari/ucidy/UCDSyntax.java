package ari.ucidy;

/*
 * This file is part of Ucidy.
 *
 * Ucidy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Ucidy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Ucidy.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2017 - Gregory Mantelet (ARI/ZAH)
 */

/**
 * All possible syntax rules as defined by the IVOA Recommendation 2007-04-02 for the UCD1+ controlled vocabulary v1.23:
 * http://www.ivoa.net/documents/REC/UCD/UCDlist-20070402.html
 *
 * @author Gr&eacute;gory Mantelet (ARI)
 * @version 1.0 (02/2017)
 */
public enum UCDSyntax{

	/** The word can only be used as “primary” or first word. */
	PRIMARY('P'),

	/** The word cannot be used as the first word to describe a single quantity. */
	SECONDARY('S'),

	/** The word can be used indifferently as first or secondary word. */
	BOTH('Q'),

	/** A photometric quantity ; can be followed by a word describing a part of the electromagnetic spectrum.
	 * <p><i><b>Implementation note:</b>
	 * 	It will be considered as {@link #BOTH} in term of order (i.e. its place in the UCD is not important).
	 * </i></p> */
	PHOT_QUANTITY('E'),

	/** A colour index ; can be followed by two successive word describing a part of the electromagnetic spectrum.
	 * <p><i><b>Implementation note:</b>
	 * 	It will be considered as {@link #BOTH} in term of order (i.e. its place in the UCD is not important).
	 * </i></p> */
	COLOUR('C'),

	/** A vector. Such a word can be followed by another describing the axis or reference frame in which the measurement is done.
	 * <p><i><b>Implementation note:</b>
	 * 	It will be considered as {@link #BOTH} in term of order (i.e. its place in the UCD is not important).
	 * </i></p> */
	VECTOR('V');

	/** Character associated with this UCD syntax.
	 * <p><i>Note: the case of the character (i.e. lower or upper case) should not be important.</i></p> */
	protected final char syntaxCode;

	/** Human readable list of all possible syntaxes (in their character form) listed in this Enum class. */
	public final static String allowedSyntaxCodes;
	static{
		StringBuffer buf = new StringBuffer("");
		for(UCDSyntax s : values()){
			if (buf.length() > 0)
				buf.append(", ");
			buf.append(s.syntaxCode);
		}
		allowedSyntaxCodes = buf.toString();
	}

	/**
	 * Create a {@link UCDSyntax}.
	 *
	 * <p><b>IMPORTANT:</b>
	 * 	The given character <b>MUST NOT</b> be already used by another {@link UCDSyntax} item.
	 * </p>
	 *
	 * @param syntaxCode	The character form of this {@link UCDSyntax}.
	 */
	private UCDSyntax(final char syntaxCode) throws IllegalArgumentException{
		this.syntaxCode = Character.toUpperCase(syntaxCode);
	}

	/**
	 * Get the Enum item representing the given syntax code character.
	 *
	 * @param syntaxCode	The syntax code character for which the corresponding Enum item is asked.
	 *
	 * @return	The corresponding Enum item,
	 *        	or <code>null</code> if none matches.
	 */
	public static UCDSyntax get(final char syntaxCode){
		for(UCDSyntax s : values()){
			if (s.syntaxCode == Character.toUpperCase(syntaxCode))
				return s;
		}
		return null;
	}
}
