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
 * Copyright 2018 - Gregory Mantelet (CDS)
 */

import java.io.IOException;
import java.io.Reader;

/**
 * Object listing and ordering alphabetically a set of deprecated UCD words.
 *
 * <p>
 * This class is an extension of {@link UCDWordList}. The only difference
 * lies in the fact that to be added in this list a word MUST be deprecated,
 * valid and recognised.
 * </p>
 *
 * <p>
 * <i>Note:
 * Words of this list may not have a UCD syntax code or a description.
 * </i>
 * </p>
 *
 * @author Gr&eacute;gory Mantelet (CDS)
 * @version 1.1 (06/2018)
 * @since 1.1
 */
public class DeprecatedUCDWordList extends UCDWordList {

	/** List of non deprecated words which complements this UCD words listed
	 * in this class.
	 * <i>NEVER NULL</i> */
	protected final UCDWordList nonDeprecatedWords;

	/**
	 * Create a list of all deprecated list compared to the given list of
	 * "active" UCD words.
	 *
	 * @param nonDeprecatedWords	List of all "active" UCD words.
	 *
	 * @throws NullPointerException	If no list of "active" UCD words is
	 *                             	provided.
	 */
	public DeprecatedUCDWordList(final UCDWordList nonDeprecatedWords) throws NullPointerException{
		if (nonDeprecatedWords == null)
			throw new NullPointerException("Missing list of all non deprecated UCD words!");
		this.nonDeprecatedWords = nonDeprecatedWords;
	}

	/**
	 * Add the given deprecated UCD word.
	 *
	 * @param newWord
	 *            The deprecated UCD word to add.
	 *
	 * @return <code>true</code> if the given deprecated UCD word has been
	 *         successfully added,
	 *         <code>false</code> if the given word
	 *                               is <code>null</code>,
	 *                               is not valid,
	 *                               is recognised,
	 *                               is not deprecated,
	 *                               already exists in the list
	 *                               or words of the suggested UCD extract
	 *                               are not all recognised.
	 */
	@Override
	public boolean add(final UCDWord newWord){
		if (newWord == null || !newWord.isDeprecated() || !newWord.valid || newWord.recognised || !newWord.suggestedReplacement.isAllRecognised())
			return false;
		else
			return words.add(newWord);
	}

	/**
	 * Add all deprecated UCD words declared inside the specified reader.
	 *
	 * <p><b>
	 * 	The real parsing is performed by
	 * 	{@link UCDParser#parseDeprecatedWordList(Reader, UCDWordList, DeprecatedUCDWordList)}.
	 * 	Please take a look to its Javadoc for more details about the expected
	 * 	syntax and the possible errors that can occur.
	 * </b></p>
	 *
	 * @param reader		Reader whose the content must be parsed.
	 * @param recommended	<i>IGNORED in this implementation</i>
	 *
	 * @return	The number of successfully added deprecated UCD words extracted
	 *        	from the given input.
	 *
	 * @throws NullPointerException	If the given reader is <code>null</code>.
	 * @throws IOException			If an error occurred while reading the
	 *                    			specified input.
	 *
	 * @see UCDParser#parseDeprecatedWordList(Reader, UCDWordList, DeprecatedUCDWordList)
	 */
	@Override
	public int addAll(final Reader reader, final boolean recommended) throws NullPointerException, IOException{
		return UCDParser.parseDeprecatedWordList(reader, nonDeprecatedWords, this);
	}

}
