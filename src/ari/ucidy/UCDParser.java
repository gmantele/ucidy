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
 * Copyright 2017-2021 - Gregory Mantelet (CDS)
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.Iterator;

/**
 * Object that lets parse a UCD ({@link #parseUCD(String)}) or a list of UCD
 * word ({@link #parseWordList(Reader, boolean)}).
 *
 * <p>
 * 	Though the static function {@link #parseUCD(String)} is using a
 * 	{@link UCDParser} already initialized with the list of all official IVOA UCD
 * 	words (see {@link #defaultParser}), it is possible to create an instance of
 * 	{@link UCDParser} with a custom list of UCD words.
 * </p>
 *
 * <p>
 * 	The main function of this parser prompts for a UCD, parses this UCD and
 * 	finally returns some information about it.
 * </p>
 *
 * @author Gr&eacute;gory Mantelet (CDS)
 * @version 1.2 (07/2021)
 */
public class UCDParser {

	/* ######################################################################
	 * # MAIN FUNCTION ######################################################
	 * ###################################################################### */
	public static void main(final String[] args) throws Throwable{

		int nbRead;
		byte[] buffer = new byte[128];
		String read;
		UCD ucd;

		// Prompt:
		System.out.print("UCD to parse? ");

		// Get the input UCD:
		nbRead = System.in.read(buffer);
		read = new String(buffer, 0, nbRead);

		// Parse the UCD:
		ucd = UCDParser.parseUCD(read.trim());

		System.out.println("----------------------------------------------");

		// Words validity:
		System.out.println("All words valid?        " + ucd.isAllValid());
		System.out.println("All words recognised?   " + ucd.isAllRecognised());
		System.out.println("All words recommended?  " + ucd.isAllRecommended());
		// Full validity:
		System.out.println("\nUCD fully valid?        " + ucd.isFullyValid());
		if (!ucd.isFullyValid()){
			System.out.println("\nREASON:");
			Iterator<String> errors = ucd.getErrors();
			while(errors.hasNext())
				System.out.println("    - " + errors.next());
			System.out.println("\nCORRECTION SUGGESTION: " + (ucd.getSuggestion() == null ? "none" : ucd.getSuggestion()));
		}

		// Advice:
		Iterator<String> advice = ucd.getAdvice();
		if (advice.hasNext()){
			System.out.println("\nADVICE for improvement of your UCD:");
			do{
				System.out.println("    - " + advice.next());
			}while(advice.hasNext());
		}

		System.out.println("----------------------------------------------\n");

	}
	/* ###################################################################### */

	/** Default path to the PSV file listing all official IVOA UCDs.
	 * <p>This path must be relative to the class path.</p>
	 * <p><i>
	 * 	See {@link #parseWordList(Reader, boolean)} for more details about the
	 * 	expected file format.
	 * </i></p>
	 * @since 1.2 */
	public static final String FILE_UCD_WORDS = "/ucd1p-words.txt";

	/** Default path to the file listing all deprecated UCD words and their
	 * replacement.
	 * <p>This path must be relative to the class path.</p>
	 * <p><i>
	 * 	See {@link #parseDeprecatedWordList(Reader, UCDWordList)} for more
	 * 	details about the expected file format.
	 * </i></p>
	 * @since 1.2 */
	public static final String FILE_UCD_DEPRECATED = "/ucd1p-deprecated.txt";

	/** Default UCD parser which is initialized with a list of only the official
	 * IVOA UCD words and the deprecated ones (for better error messages and
	 * suggestions).
	 *
	 * <p><i>This parser is generally used through {@link #parseUCD(String)} but could be used directly.</i></p> */
	public final static UCDParser defaultParser = new UCDParser();
	static{
		// Import all the official IVOA's UCD words:
		try{
			defaultParser.knownWords.addAll(UCDWordList.class.getResourceAsStream(FILE_UCD_WORDS), true);
		}catch(NullPointerException npe){
			System.err.println("Impossible to import the official IVOA UCDs inside the default UCD parser! Cause: the UCD words list can not be found.");
		}catch(IOException ioe){
			System.err.println("Impossible to import the official IVOA UCDs inside the default UCD parser! Cause: " + ioe.getMessage());
		}
		// And all the deprecated UCD words:
		try{
			defaultParser.deprecatedWords.addAll(UCDWordList.class.getResourceAsStream(FILE_UCD_DEPRECATED), true);
		}catch(NullPointerException npe){
			System.err.println("Impossible to import the deprecated UCDs inside the default UCD parser! Cause: the UCD words list can not be found.");
		}catch(IOException ioe){
			System.err.println("Impossible to import the deprecated UCDs inside the default UCD parser! Cause: " + ioe.getMessage());
		}
	}

	/** List of all <i>known</i> words.
	 *
	 * <p><b>Important:
	 * 	"Known" means here that all words of this list will be used as reference
	 * 	when building a {@link UCD} object when the corresponding UCD word match
	 * 	the listed {@link UCDWord}. So it means there is no guarantee that ALL
	 * 	{@link UCDWord} objects stored in this list are {@link UCDWord#valid valid},
	 * 	{@link UCDWord#recognised recognised} and/or {@link UCDWord#recommended recommended}.
	 * 	This special status of a {@link UCDWord} depends of its initialization.
	 * </p>
	 *
	 * <p><i>This field is NEVER <code>null</code>.</i></p> */
	public final UCDWordList knownWords;

	/** List of all <i>deprecated</i> words.
	 *
	 * <p><b>Important:
	 * 	When a UCD word can not be resolved, this {@link UCDParser} will try
	 * 	to find it among the deprecated words. If a match is found, the
	 * 	suggested UCD replacement will be proposed to the user.
	 * </p>
	 *
	 * <p><i>This field is NEVER <code>null</code>.</i></p>
	 *
	 * @since 1.1 */
	public final DeprecatedUCDWordList deprecatedWords;

	/**
	 * Create a UCD parser with an empty list of known words.
	 *
	 * <p>
	 * 	You can however fill this list directly through the field
	 * 	{@link #knownWords}.
	 * </p>
	 *
	 * <p><i>Note:
	 * 	The list of deprecated words is also empty by default. As for the known
	 * 	words, this list can be updated directly through the field
	 * 	{@link #deprecatedWords}.
	 * </i></p>
	 */
	public UCDParser(){
		knownWords = new UCDWordList();
		deprecatedWords = new DeprecatedUCDWordList(knownWords);
	}

	/**
	 * Build a UCD parser with the given list of known words.
	 *
	 * <p><i>Note:
	 * 	The given list can obviously be modified (i.e. addition and deletion are
	 * 	allowed) through the field {@link #knownWords} or directly when
	 * 	manipulating the given list (it is stored in this {@link UCDParser}
	 * 	object by reference).
	 * </i></p>
	 *
	 * @param words	List of all known words.
	 */
	public UCDParser(final UCDWordList words){
		this(words, null);
	}

	/**
	 * Build a UCD parser with the given list of known words and a given list of
	 * deprecated UCD words.
	 *
	 * <p><i>Note:
	 * 	The given lists can obviously be modified (i.e. addition and deletion
	 * 	are allowed) through the fields {@link #knownWords} and {@link #deprecatedWords}
	 * 	or directly when manipulating the given lists (they are stored in this
	 * 	{@link UCDParser} object by reference).
	 * </i></p>
	 *
	 * @param words				List of all known words.
	 * @param deprecatedWords	List of all deprecated words.
	 *
	 * @since 1.1
	 */
	public UCDParser(final UCDWordList words, final DeprecatedUCDWordList deprecatedWords){
		knownWords = (words == null) ? new UCDWordList() : words;
		this.deprecatedWords = (deprecatedWords == null) ? new DeprecatedUCDWordList(knownWords) : deprecatedWords;
	}

	/* *********** */
	/* UCD PARSING */
	/* *********** */

	/**
	 * Parse the given string representing a UCD into an object representation:
	 * {@link UCD}.
	 *
	 * <p>
	 * 	Each word of this UCD is searched in the list of <i>known</i> UCD words.
	 * 	If a match is found, the full definition of this UCD will be set in
	 * 	{@link UCD}.
	 * 	If none can be found, the word is searched in the list of deprecated
	 * 	words. If still not found, a not {@link UCDWord#recognised recognised}
	 * 	{@link UCDWord} will be created instead, with a list of the closest
	 * 	recognised UCD words (if any).
	 * </p>
	 *
	 * @param ucdStr	The string serializing a UCD.
	 *
	 * @return	The object representation of this UCD,
	 *        	or <code>null</code>
	 *        	  if the given string is <code>null</code> or empty.
	 */
	public UCD parse(final String ucdStr){
		if (ucdStr == null || ucdStr.trim().length() == 0)
			return null;

		String[] wordsStr = ucdStr.split(";");
		UCDWord[] words = new UCDWord[wordsStr.length];
		for(int i = 0; i < wordsStr.length; i++){
			if (wordsStr[i] == null || wordsStr[i].length() == 0)
				words[i] = null;
			else{
				// search among the list of known words:
				words[i] = knownWords.get(wordsStr[i].trim(), true);
				// if not found, search among the list of deprecated words:
				if (words[i] == null)
					words[i] = deprecatedWords.get(wordsStr[i].trim(), true);
				// if still not found, search for the closest known words:
				if (words[i] == null)
					words[i] = new UCDWord(wordsStr[i], knownWords.getClosest(wordsStr[i]));
				// else, if needed, set the word with the same case as provided:
				else if (!wordsStr[i].equals(words[i].rawWord))
					words[i] = new UCDWord(words[i].syntaxCode, wordsStr[i], words[i].description, words[i].recommended);
			}
		}

		return new UCD(words);
	}

	/**
	 * Parse the given UCD and try to resolve each word as a known UCD word
	 * among the IVOA official list.
	 *
	 * <p>
	 * 	Non resolved words, will still be in the returned {@link UCD} exactly as
	 * 	provided, but won't be flagged as {@link UCDWord#recognised recognised}.
	 * 	The consequence is a final non {@link UCD#isFullyValid() fully valid}
	 * 	UCD.
	 * </p>
	 *
	 * @param ucdStr	The string serializing a UCD.
	 *
	 * @return	The parsed UCD,
	 *        	or <code>null</code> if the given string is <code>null</code>
	 *        	  or empty.
	 *
	 * @see #parse(String)
	 */
	public static UCD parseUCD(final String ucdStr){
		return defaultParser.parse(ucdStr);
	}

	/* ********************* */
	/* UCD WORD LIST PARSING */
	/* ********************* */

	/** Maximum number of consecutive errors while parsing a PSV file, before
	 * stopping the parsing. */
	protected final static int NB_MAX_ERRORS = 10;

	/**
	 * Create a {@link UCDWordList} with all UCD words declared using the PSV
	 * (Pipe-Separated-Value) format inside the specified input.
	 *
	 * <p>
	 * 	The expected PSV file MUST contain at least 3 columns, each separated by
	 * 	a pipe character (|):
	 * </p>
	 * <ol>
	 * 	<li><i>Syntax code:</i> a single character among P, S, Q, E, C and V.
	 *  	<i>See {@link UCDSyntax} for more details.</i></li>
	 * 	<li><i>Word:</i> the UCD word itself.</li>
	 * 	<li><i>Description:</i> optional description of the UCD word.</li>
	 * </ol>
	 *
	 * <p><i>Note:
	 * 	If more columns are provided, they will be considered as part of the
	 * 	description.
	 * </i></p>
	 *
	 * <p>
	 * 	If the syntax of a PSV line is incorrect, an error message will be
	 * 	displayed in the standard error output. If more than
	 * 	{@value #NB_MAX_ERRORS} consecutive errors are raised, the parsing of
	 * 	the file stops immediately with a new error message.
	 * </p>
	 *
	 * <p>A PSV line is considered as incorrect in the following cases:</p>
	 * <ul>
	 * 	<li>there are less than 3 columns (which includes the case where no pipe
	 * 	    separator can be detected)</li>
	 * 	<li>there is no syntax code character (first column)</li>
	 * 	<li>there is no UCD word (second column)</li>
	 * </ul>
	 *
	 * <p>Few additional notes about the parsing:</p>
	 * <ul>
	 * 	<li>All leading and trailing spaces of each column value are removed.</li>
	 * 	<li>If no description is provided, {@link UCDWord#description} will be
	 * 	    set to <code>null</code>.</li>
	 * 	<li>An unknown syntax code character is permitted, but will flag the
	 * 	    resulting {@link UCDWord} as NOT
	 * 	    <i>{@link UCDWord#recommended recommended}</i>.</li>
	 * 	<li>An incorrect UCD word structure is permitted, but will flag the
	 * 	    resulting {@link UCDWordList} as NOT
	 * 	    <i>{@link UCDWord#valid valid}</i> and so automatically as NOT
	 * 	    <i>{@link UCDWord#recommended recommended}</i>.</li>
	 * </ul>
	 *
	 * @param reader		Reader whose the content must be parsed.
	 * @param recommended	<code>true</code> to flag all imported UCD words as
	 *                   	<i>{@link UCDWord#recommended recommended}</i>,
	 *                   	<code>false</code> otherwise.
	 *
	 * @return	The list of all successfully parsed UCD words.
	 *
	 * @throws NullPointerException	If the given reader is <code>null</code>.
	 * @throws IOException			If an error occurred while reading the
	 *                    			specified input.
	 */
	public static UCDWordList parseWordList(final Reader reader, final boolean recommended) throws NullPointerException, IOException{
		UCDWordList words = new UCDWordList();
		parseWordList(reader, recommended, words);
		return words;
	}

	/**
	 * Add inside the given {@link UCDWordList} all UCD words declared using the
	 * PSV (Pipe-Separated-Value) format inside the specified input.
	 *
	 * <p>
	 * 	The expected PSV file MUST contain at least 3 columns, each separated by
	 * 	a pipe character (|):
	 * </p>
	 * <ol>
	 * 	<li><i>Syntax code:</i> a single character among P, S, Q, E, C and V.
	 *  	<i>See {@link UCDSyntax} for more details.</i></li>
	 * 	<li><i>Word:</i> the UCD word itself.</li>
	 * 	<li><i>Description:</i> optional description of the UCD word.</li>
	 * </ol>
	 *
	 * <p><i>Note:
	 * 	If more columns are provided, they will be considered as part of the
	 * 	description.
	 * </i></p>
	 *
	 * <p>
	 * 	If the syntax of a PSV line is incorrect, an error message will be
	 * 	displayed in the standard error output. If more than
	 * 	{@value #NB_MAX_ERRORS} consecutive errors are raised, the parsing of
	 * 	the file stops immediately with a new error message.
	 * </p>
	 *
	 * <p>A PSV line is considered as incorrect in the following cases:</p>
	 * <ul>
	 * 	<li>there are less than 3 columns (which includes the case where no pipe
	 * 	    separator can be detected)</li>
	 * 	<li>there is no syntax code character (first column)</li>
	 * 	<li>there is no UCD word (second column)</li>
	 * </ul>
	 *
	 * <p>Few additional notes about the parsing:</p>
	 * <ul>
	 * 	<li>All leading and trailing spaces of each column value are removed.</li>
	 * 	<li>If no description is provided, {@link UCDWord#description} will be
	 * 	    set to <code>null</code>.</li>
	 * 	<li>An unknown syntax code character is permitted, but will flag the
	 * 	    resulting {@link UCDWord} as NOT
	 * 		<i>{@link UCDWord#recommended recommended}</i>.</li>
	 * 	<li>An incorrect UCD word structure is permitted, but will flag the
	 * 	    resulting {@link UCDWordList} as NOT
	 * 		<i>{@link UCDWord#valid valid}</i> and so automatically as NOT
	 * 	    <i>{@link UCDWord#recommended recommended}</i>.</li>
	 * </ul>
	 *
	 * @param reader		Reader whose the content must be parsed.
	 * @param recommended	<code>true</code> to flag all imported UCD words as
	 *                   	<i>{@link UCDWord#recommended recommended}</i>,
	 *                   	<code>false</code> otherwise.
	 * @param words			The {@link UCDWordList} to complete with the UCD
	 *             			words extracted from the given input.
	 *
	 * @return	The number of successfully added UCD words extracted from the
	 *        	input.
	 *
	 * @throws NullPointerException	If the given reader is <code>null</code>.
	 * @throws IOException			If an error occurred while reading the
	 *                    			specified input.
	 */
	public static int parseWordList(final Reader reader, final boolean recommended, final UCDWordList words) throws NullPointerException, IOException{
		if (reader == null)
			throw new NullPointerException("Missing reader to parse!");

		int nbAdded = 0;
		BufferedReader input = null;
		try{
			// Open the file:
			input = new BufferedReader(reader);

			String line;
			int numLine = 1;
			short nbConsecutiveErrors = 0;

			// For each line:
			while((line = input.readLine()) != null){
				try{
					/* Parse the line as UCD definition
					 * and add the result to the list: */
					if (words.add(parsePSVLine(line, recommended)))
						nbAdded++;
					else
						System.out.println("[l." + numLine + "] WARNING: Duplicated, not recognised or not syntactically correct UCD ignored.");

					// Reset the counter of consecutive errors:
					nbConsecutiveErrors = 0;

				}catch(ParseException pe){
					// Print the error:
					System.err.println("[l." + numLine + "] ERROR: Skipped UCD definition! Cause: " + pe.getMessage());
					// Increment the number of consecutive errors:
					nbConsecutiveErrors++;
					// Stop the parsing if too many errors in a row:
					if (nbConsecutiveErrors > NB_MAX_ERRORS)
						throw new IOException("Parsing stopped before the end! Cause: More than " + NB_MAX_ERRORS + " consecutive incorrect UCD definitions detected.");
				}catch(NullPointerException npe){
					// Empty or NULL line => Ignored!
				}

				// Count lines:
				numLine++;
			}

			return nbAdded;

		}finally{
			// Close the stream, if opened:
			if (input != null){
				try{
					input.close();
				}catch(IOException ioe){
				}
			}
		}
	}

	/**
	 * Create a {@link DeprecatedUCDWordList} with all deprecated UCD words
	 * declared inside the specified input.
	 *
	 * <p>
	 * 	The expected file MUST contain exactly 2 columns, separated by at least
	 * 	one space character:
	 * </p>
	 * <ol>
	 * 	<li><i>Word:</i> the deprecated UCD word.</li>
	 * 	<li><i>Suggested replacement:</i> the UCD suggested to replace the
	 * 	    deprecated UCD word.</li>
	 * </ol>
	 *
	 * <p>
	 * 	If the syntax of a line is incorrect, an error message will be displayed
	 * 	in the standard error output. If more than {@value #NB_MAX_ERRORS}
	 * 	consecutive errors are raised, the parsing of the file stops immediately
	 * 	with a new error message.
	 * </p>
	 *
	 * <p>A line is considered as incorrect in the following cases:</p>
	 * <ul>
	 * 	<li>there are less or more than 2 columns</li>
	 * 	<li>there is no UCD word (first column)</li>
	 * 	<li>there is no suggested UCD replacement (second column)</li>
	 * 	<li>the UCD word has an incorrect syntax</li>
	 * 	<li>the UCD word is already listed as non-deprecated in the given words
	 * 	    list</li>
	 * 	<li>the suggested UCD replacement is is not fully valid (i.e. correct
	 *	    syntax + all words recognised)</li>
	 * </ul>
	 *
	 * <p>
	 * 	If a deprecated UCD word is declared more than once, only the first
	 * 	occurrence will stay in the list. An error will be displayed only if
	 * 	the suggested UCD replacement is different. Otherwise just a warning
	 * 	is displayed.
	 * </p>
	 *
	 * <p>Few additional notes about the parsing:</p>
	 * <ul>
	 * 	<li>All leading and trailing spaces of each column value are
	 * 	    removed.</li>
	 * </ul>
	 *
	 * @param reader	Reader whose the content must be parsed.
	 * @param lstWords	The list of all known and still correct UCD words.
	 *
	 * @return	The list of all successfully parsed deprecated UCD words.
	 *
	 * @throws NullPointerException	If the given reader is <code>null</code>.
	 * @throws IOException			If an error occurred while reading the
	 *                    			specified input.
	 *
	 * @see #parseDeprecatedWordList(Reader, UCDWordList, DeprecatedUCDWordList)
	 *
	 * @since 1.1
	 */
	public static DeprecatedUCDWordList parseDeprecatedWordList(final Reader reader, final UCDWordList lstWords) throws NullPointerException, IOException{
		DeprecatedUCDWordList lstDeprecatedWords = new DeprecatedUCDWordList(lstWords);
		parseDeprecatedWordList(reader, lstWords, lstDeprecatedWords);
		return lstDeprecatedWords;
	}

	/**
	 * Add inside the given {@link UCDWordList} all deprecated UCD words
	 * declared inside the specified input.
	 *
	 * <p>
	 * 	The expected file MUST contain exactly 2 columns, separated by at least
	 * 	one space character:
	 * </p>
	 * <ol>
	 * 	<li><i>Word:</i> the deprecated UCD word.</li>
	 * 	<li><i>Suggested replacement:</i> the UCD suggested to replace the
	 * 	    deprecated UCD word.</li>
	 * </ol>
	 *
	 * <p>
	 * 	If the syntax of a line is incorrect, an error message will be displayed
	 * 	in the standard error output. If more than {@value #NB_MAX_ERRORS}
	 * 	consecutive errors are raised, the parsing of the file stops immediately
	 * 	with a new error message.
	 * </p>
	 *
	 * <p>A line is considered as incorrect in the following cases:</p>
	 * <ul>
	 * 	<li>there are less or more than 2 columns</li>
	 * 	<li>there is no UCD word (first column)</li>
	 * 	<li>there is no suggested UCD replacement (second column)</li>
	 * 	<li>the UCD word has an incorrect syntax</li>
	 * 	<li>the UCD word is already listed as non-deprecated in the given words
	 * 	    list</li>
	 * 	<li>the suggested UCD replacement is is not fully valid (i.e. correct
	 *	    syntax + all words recognised)</li>
	 * </ul>
	 *
	 * <p>
	 * 	If a deprecated UCD word is declared more than once, only the first
	 * 	occurrence will stay in the list. An error will be displayed only if
	 * 	the suggested UCD replacement is different. Otherwise just a warning
	 * 	is displayed.
	 * </p>
	 *
	 * <p>Few additional notes about the parsing:</p>
	 * <ul>
	 * 	<li>All leading and trailing spaces of each column value are
	 * 	    removed.</li>
	 * </ul>
	 *
	 * @param reader				Reader whose the content must be parsed.
	 * @param lstWords				The list of all known and still correct UCD
	 *             					words.
	 * @param lstDeprecatedWords	The {@link DeprecatedUCDWordList} to
	 *                          	complete with the deprecated UCD words
	 *                          	extracted from the given input.
	 *
	 * @return	The number of successfully added deprecated UCD words extracted
	 *        	from the input.
	 *
	 * @throws NullPointerException	If the given reader is <code>null</code>.
	 * @throws IOException			If an error occurred while reading the
	 *                    			specified input.
	 *
	 * @see #parseDeprecatedFileLine(String)
	 *
	 * @since 1.1
	 */
	public static int parseDeprecatedWordList(final Reader reader, final UCDWordList lstWords, final DeprecatedUCDWordList lstDeprecatedWords) throws NullPointerException, IOException{
		if (reader == null)
			throw new NullPointerException("Missing reader to parse!");

		int nbAdded = 0;
		BufferedReader input = null;
		UCDParser ucdParser = new UCDParser(lstWords);
		try{
			// Open the file:
			input = new BufferedReader(reader);

			String line;
			String[] deprecatedEntry;
			int numLine = 1;
			short nbConsecutiveErrors = 0;

			// For each line:
			while((line = input.readLine()) != null){
				try{

					// Parse the line:
					deprecatedEntry = parseDeprecatedFileLine(line);

					/* Check that the UCD word declared as deprecated is not
					 * already listed as still valid: */
					UCDWord duplicatedWord = lstWords.get(deprecatedEntry[0]);
					if (duplicatedWord != null)
						throw new ParseException("The UCD word \"" + deprecatedEntry[0] + "\" can not be declared as deprecated! It is already listed among the recognised UCD words.", 0);

					/* Check that the deprecated UCD word is not already listed
					 * as duplicated: */
					duplicatedWord = lstDeprecatedWords.get(deprecatedEntry[0]);
					if (duplicatedWord != null && duplicatedWord.isDeprecated()){
						/* If an entry is found, it means the declaration has
						 * been duplicated. In such case, throw an error if the
						 * suggested UCD replacement is different, otherwise
						 * just display a warning about the duplicated line. */
						if (duplicatedWord.suggestedReplacement.toString().equalsIgnoreCase(deprecatedEntry[1])){
							System.err.println("[l." + numLine + "] WARNING: Duplicated declaration of the deprecated UCD word \"" + deprecatedEntry[0] + "\"! (note: duplicated declarations are ignored)");
							continue;
						}else
							throw new ParseException("The UCD word \"" + deprecatedEntry[0] + "\" is already declared as deprecated, but with a different suggestion of UCD replacement: \"" + deprecatedEntry[1] + "\" (new suggestion) instead of \"" + duplicatedWord.suggestedReplacement + "\" (know suggestion).", 0);
					}

					// Check that the suggested UCD replacement is fully valid:
					UCD suggestedUCD = ucdParser.parse(deprecatedEntry[1]);
					if (suggestedUCD == null)
						throw new ParseException("Can not parse the suggested UCD replacement as a UCD: \"" + deprecatedEntry[1] + "\"!", 0);
					else if (!suggestedUCD.isAllRecognised())
						throw new ParseException("Incorrect suggested UCD extract: \"" + deprecatedEntry[1] + "\"! One of its words is not recognised.", 0);

					/* Build the deprecated UCD word and check it is
					 * syntactically valid: */
					UCDWord deprecatedWord = new UCDWord(deprecatedEntry[0], suggestedUCD, null, null);
					if (!deprecatedWord.valid)
						throw new ParseException("Incorrect syntax for the deprecated UCD word: \"" + deprecatedEntry[0] + "\"!", 0);

					// Finally, add the result to the list:
					if (lstDeprecatedWords.add(deprecatedWord))
						nbAdded++;
					else
						System.out.println("[l." + numLine + "] WARNING: Duplicated, not recognised or not syntactically correct UCD ignored.");

					// Reset the counter of consecutive errors:
					nbConsecutiveErrors = 0;
				}catch(ParseException pe){
					// Print the error:
					System.err.println("[l." + numLine + "] ERROR: Skipped deprecated UCD declaration! Cause: " + pe.getMessage());
					// Increment the number of consecutive errors:
					nbConsecutiveErrors++;
					// Stop the parsing if too many errors in a row:
					if (nbConsecutiveErrors > NB_MAX_ERRORS)
						throw new IOException("Parsing stopped before the end! Cause: More than " + NB_MAX_ERRORS + " consecutive incorrect deprecated UCD declarations detected.");
				}catch(NullPointerException npe){
					// Empty, NULL or comment line => Ignored!
				}

				// Count lines:
				numLine++;
			}

			return nbAdded;

		}finally{
			// Close the stream, if opened:
			if (input != null){
				try{
					input.close();
				}catch(IOException ioe){
				}
			}
		}
	}

	/**
	 * Parse a line of a PSV (Pipe Separated Value) file as the definition of a
	 * UCD word.
	 *
	 * @param psvLine		A non empty line of a PSV file listing the allowed
	 *               		UCD words.
	 * @param recommended	<code>true</code> if the described UCD word is
	 *                   	<i>{@link UCDWord#recommended}</i> by the IVOA
	 *                   	standard,
	 *                   	<code>false</code> otherwise.
	 *
	 * @return	The corresponding UCD word.
	 *
	 * @throws NullPointerException	If the given PSV line is <code>null</code>
	 *                             	or an empty string.
	 * @throws ParseException		If the syntax of the given PSV line is
	 *                       		incorrect (expected syntax: ()),
	 *                       		or if the syntax code is too long or unknown.
	 *
	 * @see UCDSyntax#get(char)
	 * @see UCDSyntax#allowedSyntaxCodes
	 */
	public static UCDWord parsePSVLine(final String psvLine, final boolean recommended) throws NullPointerException, ParseException{

		// No parameter or empty string => ERROR
		if (psvLine == null || psvLine.trim().length() == 0)
			throw new NullPointerException("No PSV file line to parse!");

		UCDSyntax syntax = null;
		String word = null;
		String description = null;

		// Get the syntax code:
		int start = 0, indPipe = psvLine.indexOf('|');
		if (indPipe < 0)
			throw new ParseException("No valid separator found between the syntax code and the UCD word!", start);
		String tmp = psvLine.substring(0, indPipe).trim();
		if (indPipe == 0 || tmp.length() > 1)
			throw new ParseException("Unknown syntax code: \"" + tmp + "\"! It should be EXACTLY one character among: " + UCDSyntax.allowedSyntaxCodes + ".", 0);
		syntax = UCDSyntax.get(tmp.charAt(0));
		if (syntax == null)
			throw new ParseException("Unknown syntax code: \"" + tmp.charAt(0) + "\"! It should be a character among: " + UCDSyntax.allowedSyntaxCodes + ".", 0);

		// Get the word:
		start = indPipe + 1;
		indPipe = psvLine.indexOf('|', start);
		if (indPipe < 0)
			throw new ParseException("No valid separator found between the UCD word and its description!", start);
		word = psvLine.substring(start, indPipe).trim();

		// Get the description:
		description = psvLine.substring(indPipe + 1).trim();
		if (description.length() == 0)
			description = null;

		// Create the UCDWord:
		return new UCDWord(syntax, word, description, recommended);
	}

	/**
	 * Parse a line of a file listing the deprecated UCD words.
	 *
	 * <p>
	 * 	The expected syntax for a such line is 2 values separated by a space:
	 * 	the deprecated UCD word followed by a suggested UCD in replacement.
	 * </p>
	 *
	 * <p><b>Note:</b>
	 * 	Concatenated space characters will be replaced by a single space on each
	 * 	line. Besides, leading and trailing space characters will be ignored.
	 * </p>
	 *
	 * <p><b>WARNING:</b>
	 * 	Comment lines (i.e. starting with the character #) are not supported.
	 * 	This function will throw a {@link NullPointerException} in such case, as
	 * 	if an empty line was provided.
	 * </p>
	 *
	 * @param line	A non empty and not commented line of a file listing the
	 *            	deprecated UCD words.
	 *
	 * @return	The corresponding deprecated UCD word and its suggested UCD
	 *        	replacement.
	 *
	 * @throws NullPointerException	If the given line is <code>null</code>, an
	 *                             	empty string or a comment.
	 * @throws ParseException		If the given line has no space character or
	 *                       		has too many.
	 *
	 * @since 1.1
	 */
	public static String[] parseDeprecatedFileLine(String line) throws NullPointerException, ParseException{
		// No parameter or empty string => ERROR
		if (line == null || line.trim().length() == 0)
			throw new NullPointerException("No line to parse!");

		/* Normalize the line.
		 *   - no leading and trailing space characters
		 *   - concatenated space characters are replaced by a single space */
		line = line.trim().replaceAll("[ \t]+", " ");

		// If a comment line, throw an exception:
		if (line.charAt(0) == '#')
			throw new NullPointerException("No UCD word or UCD can be fetched from a comment line!");

		// Split the line (ideally in exactly 2 parts):
		String[] parseResult = line.split(" ");
		if (parseResult.length < 2)
			throw new ParseException("Incorrect syntax for a deprecated entry line! It must be 2 values separated by a space, but no space character has been found.", 0);
		else if (parseResult.length > 2)
			throw new ParseException("Incorrect syntax for a deprecated entry line! It must be 2 values separated by a space, but more space separated values have been found.", 0);

		return parseResult;

	}

}
