package ari.ucd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Representation of a UCD.
 *
 * <p>This is basically a list of {@link UCDWord UCD word}s.</p>
 *
 * <p>A lot of functions can provide information about different degrees of the validity of this UCD:</p>
 * <ul>
 * 	<li>{@link #isAllValid()}</li>
 * 	<li>{@link #isAllRecognised()}</li>
 * 	<li>{@link #isAllRecommended()}</li>
 * 	<li>{@link #isFullyValid()}</li>
 * </ul>
 *
 * <p>Three other functions may gives more information:</p>
 * <ul>
 * 	<li>{@link #getSuggestion()} which proposes a fully valid correction of this UCD (if not already valid ; otherwise the function will return this UCD)</li>
 * 	<li>{@link #getErrors()} which lists errors preventing this UCD to be fully valid</li>
 * 	<li>{@link #getAdvice()} which gives some advice about the construction of this UCD (it does not take care about the validity of this UCD)</li>
 * </ul>
 *
 * @author Gr&eacute;gory Mantelet (ARI)
 * @version 1.0 (02/2017)
 */
public class UCD implements Iterable<UCDWord> {

	/** UCD words composing this UCD.
	 * <p><i>This array may contain <code>null</code> items.</i></p> */
	protected final UCDWord[] words;

	/** String serialization of this UCD.
	 * <p><i>This field is set ONLY when {@link #toString()} is called for the first time.</i></p> */
	protected String strRepresentation = null;

	/** Flag indicating that all words of this UCD are {@link UCDWord#valid valid}.
	 * <p><i>Note that a <code>null</code> word will be considered as not {@link UCDWord#valid valid}.</i></p>
	 *
	 * <p><i>This flag is set ONLY when {@link #checkAllWords()} is called
	 * or when {@link #isAllValid()}, {@link #isAllRecognised()} or {@link #isAllRecommended()} is called for the first time.</i></p> */
	protected Boolean allValid = null;

	/** Flag indicating that all words of this UCD are {@link UCDWord#recognised recognised}.
	 * <p><i>Note that a <code>null</code> word will be considered as not {@link UCDWord#recognised recognised}.</i></p>
	 *
	 * <p><i>This flag is set ONLY when {@link #checkAllWords()} is called
	 * or when {@link #isAllValid()}, {@link #isAllRecognised()} or {@link #isAllRecommended()} is called for the first time.</i></p> */
	protected Boolean allRecognised = null;

	/** Flag indicating that all words of this UCD are {@link UCDWord#recommended recommended}.
	 * <p><i>Note that a <code>null</code> word will be considered as not {@link UCDWord#recommended recommended}.</i></p>
	 *
	 * <p><i>This flag is set ONLY when {@link #checkAllWords()} is called
	 * or when {@link #isAllValid()}, {@link #isAllRecognised()} or {@link #isAllRecommended()} is called for the first time.</i></p> */
	protected Boolean allRecommended = null;

	/** Flag indicating that the whole UCD is {@link #isFullyValid() valid}.
	 *
	 * <p><i>This flag is set ONLY when {@link #checkFullValidity()} is called
	 * or when {@link #isFullyValid()} is called for the first time.</i></p> */
	protected Boolean fullyValid = null;

	/** Flag indicating that {@link #suggestion} has been initialized when {@link #getSuggestion()} is called. */
	protected boolean alreadyHasSuggestion = false;

	/** Suggestion of a {@link #isFullyValid() fully valid} UCD. This is a suggestion of correction of this UCD
	 * when it is not already {@link #isFullyValid() fully valid}. Of course, if it already, {@link #suggestion}
	 * will be set to <code>this</code>.
	 *
	 * <p><i>This field is set ONLY when {@link #createSuggestion()} is called
	 * or when {@link #getSuggestion()} is called for the first time.</i></p> */
	protected UCD suggestion = null;

	/** List of all errors preventing this UCD to be {@link #isFullyValid() fully valid}.
	 *
	 * <p><i>This list is set ONLY when {@link #listErrors()} is called
	 * or when {@link #getErrors()} is called for the first time.</i></p> */
	protected String[] errors = null;

	/** List of advice concerning the construction of this UCD.
	 *
	 * <p><i>This list is set ONLY when {@link #listAdvice()} is called
	 * or when {@link #getAdvice()} is called for the first time.</i></p> */
	protected String[] advice = null;

	/**
	 * Create a UCD with the given words.
	 *
	 * @param words	List of words composing this UCD.
	 *
	 * @throws NullPointerException	If the given collection is <code>null</code>.
	 */
	public UCD(final Collection<UCDWord> words) throws NullPointerException{
		this(words.toArray(new UCDWord[words.size()]));
	}

	/**
	 * Create a UCD with the given words.
	 *
	 * @param words	List of words composing this UCD.
	 *
	 * @throws NullPointerException	If the given array is <code>null</code>.
	 */
	public UCD(final UCDWord[] words) throws NullPointerException{
		if (words == null || words.length == 0)
			throw new NullPointerException("Impossible to create a null UCD!");

		// Store all the words:
		this.words = words;
	}

	/* **************** */
	/* WORDS VALIDATION */
	/* **************** */

	/**
	 * Check whether all words are valid, recognised and recommended.
	 *
	 * <p>
	 * 	This function updates the flags {@link #allValid}, {@link #allRecognised}
	 * 	and {@link #allRecommended} of this {@link UCD}.
	 * </p>
	 *
	 * <p><i>Note:
	 * 	If a <code>null</code> word is detected, all flags are automatically set to <code>false</code>.
	 * </i></p>
	 */
	protected void checkAllWords(){
		boolean tempValid = true, tempRecognised = true, tempRecommended = true;
		for(UCDWord w : words){
			if (w != null){
				tempValid = tempValid && w.valid;
				tempRecognised = tempRecognised && w.recognised;
				tempRecommended = tempRecommended && w.recommended;
			}else{
				this.allValid = false;
				this.allRecognised = false;
				this.allRecommended = false;
				return;
			}
		}
		this.allValid = tempValid;
		this.allRecognised = tempRecognised;
		this.allRecommended = tempRecommended;
	}

	/**
	 * Tell whether all words composing this UCD are {@link UCDWord#valid valid}.
	 *
	 * @return	<code>true</code> if all words are {@link UCDWord#valid valid},
	 *        	<code>false</code> if at least one word is not {@link UCDWord#valid valid}.
	 */
	public final boolean isAllValid(){
		if (allValid == null)
			checkAllWords();
		return allValid;
	}

	/**
	 * Tell whether all words composing this UCD are {@link UCDWord#recognised recognised}.
	 *
	 * @return	<code>true</code> if all words are {@link UCDWord#recognised recognised},
	 *        	<code>false</code> if at least one word is not {@link UCDWord#recognised recognised}.
	 */
	public final boolean isAllRecognised(){
		if (allRecognised == null)
			checkAllWords();
		return allRecognised;
	}

	/**
	 * Tell whether all words composing this UCD are {@link UCDWord#recommended recommended}.
	 *
	 * @return	<code>true</code> if all words are {@link UCDWord#recommended recommended},
	 *        	<code>false</code> if at least one word is not {@link UCDWord#recommended recommended}.
	 */
	public final boolean isAllRecommended(){
		if (allRecommended == null)
			checkAllWords();
		return allRecommended;
	}

	/* ************* */
	/* FULL VALIDITY */
	/* ************* */

	/**
	 * Check whether this {@link UCD} is {@link #isFullyValid() fully valid}.
	 *
	 * <p>
	 * 	This function updates the flag {@link #fullyValid} of this {@link UCD}.
	 * </p>
	 */
	protected void checkFullValidity(){
		// Check all words are at least recognised:
		if (!isAllRecognised()){
			fullyValid = false;
			return;
		}

		// Check the first word is NOT a 'SECONDARY' one:
		if (words[0].syntaxCode == UCDSyntax.SECONDARY){
			fullyValid = false;
			return;
		}

		// All the other words must be NOT 'PRIMARY':
		for(int i = 1; i < words.length; i++){
			if (words[i].syntaxCode == UCDSyntax.PRIMARY){
				fullyValid = false;
				return;
			}
		}

		fullyValid = true;
	}

	/**
	 * Tell whether all words composing this UCD are {@link UCDWord#recognised recognised} AND
	 * that their order inside the UCD is correct according to their syntax code.
	 *
	 * <p><i>Only {@link UCDSyntax#PRIMARY PRIMARY ('P')} and {@link UCDSyntax#SECONDARY SECONDARY ('S')} are considered here.</i></p>
	 *
	 * @return	<code>true</code> if the whole UCD is syntactically correct,
	 *        	<code>false</code> otherwise.
	 */
	public final boolean isFullyValid(){
		if (fullyValid == null)
			checkFullValidity();
		return fullyValid;
	}

	/* ****** */
	/* ERRORS */
	/* ****** */

	/**
	 * Create a list of all errors.
	 *
	 * <p>Errors are grouped by category:</p>
	 * <ul>
	 * 	<li><i><code>null</code> or empty words</i></li>
	 * 	<li><i>Not {@link UCDWord#valid valid} words</i></li>
	 * 	<li><i>Words with no syntax code (that's to say: not {@link UCDWord#recognised recognised} words)</i></li>
	 * 	<li><i>{@link UCDSyntax#PRIMARY PRIMARY} words NOT at the first position or more than one is detected</i></li>
	 * 	<li><i>{@link UCDSyntax#SECONDARY SECONDARY} word at the first position</i></li>
	 * </ul>
	 */
	protected void listErrors(){
		boolean first = true;
		short nbEmptyWords = 0, nbNotValid = 0, nbNotRecognised = 0,
				nbLatePrimary = 0;
		String firstPrimary = null;
		StringBuffer notValid = new StringBuffer();
		StringBuffer notRecognised = new StringBuffer();
		StringBuffer latePrimary = new StringBuffer();
		StringBuffer lstClosest = new StringBuffer();

		ArrayList<String> lstErrors = new ArrayList<String>();

		// For each UCD word, there is an error if...
		for(UCDWord w : words){

			// ...the word is NULL or an empty string:
			if (w == null || w.word.trim().length() == 0)
				nbEmptyWords++;
			else{
				// ...the word is not syntactically valid:
				if (!w.valid){
					nbNotValid++;
					append(notValid, w.rawWord);
				}

				// ...its syntax code is missing (meaning it is not recognised):
				if (w.syntaxCode == null){
					// ...immediately add an error for this word:
					nbNotRecognised++;
					append(notRecognised, w.rawWord);
					// ...and list all closest matches (if any):
					if (w.closest != null){
						lstClosest.delete(0, lstClosest.length());
						for(UCDWord closeWord : w.closest)
							append(lstClosest, closeWord.rawWord);
						notRecognised.append(" (closest: " + lstClosest.toString() + ")");
					}
				}else{

					// ...a SECONDARY word starts the UCD:
					if (first && w.syntaxCode == UCDSyntax.SECONDARY)
						lstErrors.add("UCD starting with a SECONDARY UCD word: \"" + w + "\"! Such words can NOT be in first position.");

					// ...more than one PRIMARY word are detected:
					// ...PRIMARY words are not in first position:
					else if (w.syntaxCode == UCDSyntax.PRIMARY){
						if (first)
							firstPrimary = w.rawWord;
						else{
							nbLatePrimary++;
							append(latePrimary, w.rawWord);
						}
					}
				}
			}
			first = false;
		}

		// Add the errors about the PRIMARY words:
		if (nbLatePrimary > 0){
			lstErrors.add(0, nbLatePrimary + " PRIMARY UCD word" + (nbLatePrimary > 1 ? "s" : "") + " not in first position: " + latePrimary.toString() + "! Such words MUST be in first position.");
			if (firstPrimary != null || nbLatePrimary > 1)
				lstErrors.add(0, "Too many (" + (nbLatePrimary + (firstPrimary != null ? 1 : 0)) + ") PRIMARY UCD words: " + (firstPrimary != null ? "\"" + firstPrimary + "\", " : "") + latePrimary + "! Only one is allowed in a UCD.");
		}

		// Add the errors about the NOT RECOGNISED words:
		if (nbNotRecognised > 0)
			lstErrors.add(0, nbNotRecognised + " not recognised UCD word" + (nbNotRecognised > 1 ? "s" : "") + ": " + notRecognised.toString() + "!");

		// Add the errors about the NOT VALID words:
		if (nbNotValid > 0)
			lstErrors.add(0, "Wrong syntax for " + nbNotValid + " UCD word" + (nbNotValid > 1 ? "s" : "") + ": " + notValid.toString() + "!");

		// Add the errors about the NULL words:
		if (nbEmptyWords > 0)
			lstErrors.add(0, nbEmptyWords + " empty UCD word" + (nbEmptyWords > 1 ? "s" : "") + "!");

		// Set the errors:
		errors = lstErrors.toArray(new String[lstErrors.size()]);
	}

	/**
	 * Append the given UCD word at the end of the given {@link StringBuffer}.
	 * The word is added between double quotes and may be prefixed by a comma
	 * if not the first word of the StringBuffer.
	 *
	 * <p><i>Note:
	 * 	If either the buffer or the word is <code>null</code> (or also empty string ONLY in the case of the word)
	 * 	this function does nothing.
	 * </i></p>
	 *
	 * @param buf	The {@link StringBuffer} to update.
	 * @param word	The word to append.
	 */
	protected final void append(final StringBuffer buf, final String word){
		if (buf == null || word == null || word.trim().length() == 0)
			return;

		if (buf.length() > 0)
			buf.append(", ");
		buf.append('"').append(word).append('"');
	}

	/**
	 * Get all detected errors preventing this UCD to be fully valid.
	 *
	 * @return	All detected errors.
	 */
	public final Iterator<String> getErrors(){
		if (errors == null)
			listErrors();
		return new ErrorsIterator();
	}

	/**
	 * Iterator over the list of all errors.
	 *
	 * @author Gr&eacute;gory Mantelet (ARI)
	 * @version 1.0 (06/2016)
	 */
	protected class ErrorsIterator implements Iterator<String> {

		private int index = -1;

		@Override
		public boolean hasNext(){
			return (index + 1) < errors.length;
		}

		@Override
		public String next(){
			if (!hasNext())
				throw new NoSuchElementException("No more errors!");
			return errors[++index];
		}

		@Override
		public void remove(){
			throw new UnsupportedOperationException("Impossible to drop errors so easily :-P");
		}

	}

	/* ****** */
	/* ADVICE */
	/* ****** */

	/** Regular Expression for any UCD word describing a part of the electromagnetic spectrum. */
	protected static String REGEXP_EM = "em\\..+";

	/** Regular Expression for any UCD word describing an axis or a reference frame. */
	protected static String REGEXP_AXIS_FRAME = "(pos\\.az(\\.(alt|azi|zd))?|pos\\.bodyrc(\\.(alt|lat|long))?|pos\\.cartesian(\\.(x|y|z))?|pos\\.cmb|pos\\.earth(\\.(altitude|lat|lon))?|pos\\.ecliptic(\\.(alt|lon))?|pos\\.eq(\\.(dec|ha|ra|spd))?|pos\\.galactic(\\.(lat|lon))?|pos\\.lg|pos\\.lsr|pos\\.lunar|pos\\.supergalactic(\\.(lat|lon))?)";

	/**
	 * Create a list of some advice.
	 *
	 * <p>Advice is given in the following cases:</p>
	 * <ul>
	 * 	<li>one or more UCD words are duplicated</li>
	 * 	<li>a recommended word has an explicit <code>ivoa</code> namespace prefix</li>
	 * 	<li>a recognised custom word with a namespace is used (it is preferred to use recommended UCD words representing the same quantity instead)</li>
	 * 	<li>a photometric quantity is not followed directly by a part of the EM spectrum <i>(see {@link #REGEXP_EM})</i></li>
	 * 	<li>a color is not followed directly by 2 successive parts of the EM spectrum <i>(see {@link #REGEXP_EM})</i></li>
	 * 	<li>a vector is not followed directly by an axis or a reference frame</li>
	 * </ul>
	 */
	protected void listAdvice(){
		ArrayList<String> lstAdvice = new ArrayList<String>();

		// Detect duplicated UCD words:
		LinkedHashSet<UCDWord> duplicated = new LinkedHashSet<UCDWord>();
		for(int i = 0; i < words.length; i++){
			if (!duplicated.contains(words[i])){
				for(int j = i + 1; j < words.length; j++){
					if (words[i] != null && words[i].equals(words[j]))
						duplicated.add(words[i]);
				}
			}
		}
		if (duplicated.size() > 0)
			lstAdvice.add("For more readability, you should remove duplicated UCD words: " + concat(duplicated) + ".");

		UCDWord curr;
		Set<UCDWord> candidatesEM = searchByPattern(REGEXP_EM);
		Set<UCDWord> candidatesAxisFrame = searchByPattern(REGEXP_AXIS_FRAME);
		String candidatesEM_str = concat(candidatesEM);
		String candidatesAxisFrame_str = concat(candidatesAxisFrame);

		// Clear duplicated ; this set will be now used to avoid duplicated advice:
		duplicated.clear();

		for(int i = 0; i < words.length; i++){
			curr = words[i];

			// Ignore NULL words or words without syntax code:
			if (curr == null || curr.syntaxCode == null)
				continue;

			// If a piece of advice for this UCD word has already been given, skip it and go to the next word:
			if (!duplicated.add(curr))
				continue;

			// The explicit use of the "ivoa" namespace prefix is discouraged for recommended UCD words:
			if (curr.namespace != null && curr.recommended && curr.namespace.equalsIgnoreCase(UCDWord.IVOA_NAMESPACE))
				lstAdvice.add("\"" + curr + "\" is a UCD word recommended by the IVOA. The use of the explicit namespace \"ivoa\" should be avoided for more readability. So you should rather write: \"" + curr.word + "\".");

			// The use of recognised but not recommended words should be avoided:
			if (curr.recognised && !curr.recommended)
				lstAdvice.add("\"" + curr + "\" is a recognised but not recommended word. In order to ensure better detection by VO applications, you should use a UCD word recommended by the IVOA if any can already represent the same quantity.");

			switch(curr.syntaxCode){
				// Detect photometry quantity without direct specification of the spectrum part:
				case PHOT_QUANTITY:
					if (!matches(i + 1, REGEXP_EM)){
						if (candidatesEM.size() == 0)
							lstAdvice.add("No part of the electromagnetic spectrum is specified for the photometric quantity \"" + curr + "\". For more precision, one part of the EM spectrum can be added just after \"" + curr + "\".");
						else
							lstAdvice.add("No part of the electromagnetic spectrum is EXPLICITLY specified for the photometric quantity \"" + curr + "\". Some candidates have been detected in this UCD: " + candidatesEM_str + ". For more clarity, one candidate or a new part of the EM spectrum should be moved just after \"" + curr + "\".");
					}else if (matches(i + 2, REGEXP_EM))
						lstAdvice.add("At least two parts of the electromagnetic spectrum have been specified successively after the photometric quantity \"" + curr + "\". Only one is expected, but maybe more parts of the electromagnetic spectrum are covered here.");
					break;

				// Detect colors without direct specification of the spectrum parts:
				case COLOUR:
					if (!matches(i + 1, REGEXP_EM)){
						if (candidatesEM.size() == 0)
							lstAdvice.add("No range of the electromagnetic spectrum is specified for the colour \"" + curr + "\". For more precision, two successive parts of the EM spectrum can be added after \"" + curr + "\".");
						else
							lstAdvice.add("No range of the electromagnetic spectrum is EXPLICITLY specified for the colour \"" + curr + "\". Some candidates have been detected in this UCD: " + candidatesEM_str + ". For more clarity, two candidates or new parts of the EM spectrum should be moved successively just after \"" + curr + "\".");
					}else if (!matches(i + 2, REGEXP_EM)){
						Set<UCDWord> candidates = new LinkedHashSet<UCDWord>(candidatesEM);
						candidates.remove(words[i + 1]);
						if (candidates.size() == 0)
							lstAdvice.add("Missing second bound of the electromagnetic spectrum range for the colour \"" + curr + "\". For more precision, a part of the EM spectrum can be added after \"" + curr + "\".\"" + words[i + 1] + "\".");
						else
							lstAdvice.add("Missing second bound of the electromagnetic spectrum range for the colour \"" + curr + "\". Some candidates have been detected in this UCD: " + concat(candidates) + ". For more clarity, one candidate or a new part of the EM spectrum should be moved just after \"" + curr + "\".\"" + words[i + 1] + "\".");
					}else if (matches(i + 3, REGEXP_EM))
						lstAdvice.add("At least three parts of the electromagnetic spectrum have been specified successively after the colour \"" + curr + "\". Only two are expected. For more clarity, you should probably consider to remove the excedent.");
					break;

				case VECTOR:
					if (!matches(i + 1, REGEXP_AXIS_FRAME)){
						if (candidatesAxisFrame.size() == 0)
							lstAdvice.add("No axis or reference frame is specified for the vector \"" + curr + "\". For more precision, one axis or reference frame can be added just after \"" + curr + "\".");
						else
							lstAdvice.add("No axis or reference frame is EXPLICITLY specified for the vector \"" + curr + "\". Some candidates have been detected in this UCD: " + candidatesAxisFrame_str + ". For more clarity, one candidate or a axis or reference frame should be moved just after \"" + curr + "\".");
					}else if (matches(i + 2, REGEXP_AXIS_FRAME))
						lstAdvice.add("At least two axis or reference frames have been specified successively after the vector \"" + curr + "\". Only one is expected. For more clarity, you should probably consider to remove the excedent.");
					break;

				// Nothing to do for the other words:
				default:
			}

		}

		advice = lstAdvice.toArray(new String[lstAdvice.size()]);
	}

	/**
	 * Tell whether the specified UCD word exists and matches the given regular expression.
	 *
	 * <p><b>Warning:</b> The UCD word's namespace is ignored ; the regular expression must apply only on the word.</p>
	 *
	 * @param indexWord	The index of the UCD word to test.
	 * @param pattern	A regular expression.
	 *
	 * @return	<code>true</code> if the specified word exists and matches the given regular expression,
	 *        	<code>false</code> otherwise.
	 */
	protected boolean matches(final int indexWord, final String pattern){
		return (pattern != null && indexWord >= 0 && indexWord < words.length && words[indexWord] != null && words[indexWord].word.trim().toLowerCase().matches(pattern));
	}

	/**
	 * Concatenate each part of the given collection using {@link #append(StringBuffer, String)}.
	 *
	 * @param set	The collection of {@link UCDWord} to concatenate inside a {@link String}.
	 *
	 * @return	The string serialization of the given collection.
	 */
	protected String concat(final Collection<UCDWord> set){
		StringBuffer buf = new StringBuffer();
		for(UCDWord d : set)
			append(buf, d.toString());
		return buf.toString();
	}

	/**
	 * Search all UCD words composing this UCD which match the given regular expression.
	 *
	 * <p><b>Warning:</b> The UCD word's namespace is ignored ; the regular expression must apply only on the word.</p>
	 *
	 * @param pattern	A regular expression.
	 *
	 * @return	A set containing (by order of apparition) all UCD words matching the given regular expression.
	 */
	protected LinkedHashSet<UCDWord> searchByPattern(final String pattern){
		LinkedHashSet<UCDWord> match = new LinkedHashSet<UCDWord>();

		if (pattern == null)
			return match;

		for(UCDWord w : words){
			if (w != null && w.word.trim().toLowerCase().matches(pattern))
				match.add(w);
		}

		return match;
	}

	/**
	 * Get advice about UCD words which is possible to add in order to be more precise.
	 *
	 * <p><i>Advice is generally about UCD words of type {@link UCDSyntax#PHOT_QUANTITY photometric quantity},
	 * {@link UCDSyntax#COLOUR colour} and {@link UCDSyntax#VECTOR vector}.</i></p>
	 *
	 * <p><i>This UCD does not need to be valid so that this function provides advice.</i></p>
	 *
	 * @return	The proposed advice.
	 */
	public final Iterator<String> getAdvice(){
		if (advice == null)
			listAdvice();
		return new AdviceIterator();
	}

	/**
	 * Iterator over the list of advice.
	 *
	 * @author Gr&eacute;gory Mantelet (ARI)
	 * @version 1.0 (06/2016)
	 */
	protected class AdviceIterator implements Iterator<String> {

		private int index = -1;

		@Override
		public boolean hasNext(){
			return (index + 1) < advice.length;
		}

		@Override
		public String next(){
			if (!hasNext())
				throw new NoSuchElementException("Sorry, no more advice!");
			return advice[++index];
		}

		@Override
		public void remove(){
			throw new UnsupportedOperationException("Impossible to get rid of my advice :-P");
		}

	}

	/* ********** */
	/* SUGGESTION */
	/* ********** */

	/**
	 * Try to create a {@link #isFullyValid() fully valid} UCD from this one if it is not already.
	 *
	 * <p>The following operations may be performed to fix the UCD:</p>
	 * <ul>
	 * 	<li><i>In order to make all words {@link UCDWord#recognised recognised}:</i>
	 * 		remove all <code>null</code> or empty strings words, and then, remove all leading and trailing spaces
	 * 		and replace all internal space characters by a dot (.).</li>
	 * 	<li><i>In order to fix the words order to match the individual syntax codes:</i>
	 * 		move the first found PRIMARY word in first position and remove all other PRIMARY words.</li>
	 * </ul>
	 *
	 * <p>This function will return immediately <code>null</code> in the following cases:</p>
	 * <ul>
	 * 	<li>when a {@link UCDWord} has no {@link UCDWord#syntaxCode syntaxCode}</li>
	 * 	<li>if after the fixing operations on a UCD word, it is still not {@link UCDWord#recognised recognised}</li>
	 * 	<li>if after removing all <code>null</code> or empty string words, the list of remaining words is empty</li>
	 * 	<li>
	 */
	protected void createSuggestion(){
		if (isFullyValid()){
			suggestion = this;
			return;
		}

		ArrayList<UCDWord> newWords = new ArrayList<UCDWord>(words.length);
		UCD tempSuggestion = null;

		// 1st: IF NOT RECOGNISED:
		if (!allRecognised){
			/* Try to fix every words: */
			for(UCDWord w : words){

				/* Remove all NULL words: */
				if (w != null){

					/* If already recognised, nothing to do for this word: */
					if (w.recognised)
						newWords.add(w);

					/* If valid but not recognised: */
					else if (w.valid){
						// If any closest match has been found by the parse, keep the first one:
						if (w.closest != null)
							newWords.add(w.closest[0]);

						/* Otherwise, there is no way to magically guess a matching UCD word
						 * => return NULL immediately */
						else{
							suggestion = null;
							return;
						}

					}/* If not valid (so it have a UCDSyntax): */
					else{
						/* Remove all empty string words: */
						if (w.word.trim().length() > 0){

							/* Otherwise, try removing leading and trailing spaces, and replacing all internal space characters by a dot (.)
							 * ; if still not valid, nothing can be done, so return NULL: */
							UCDWord newWord = new UCDWord(w.syntaxCode, w.word.trim().replaceAll("\\s+", "."), w.description, w.recommended);
							if (newWord.recognised)
								newWords.add(newWord);
							else{
								suggestion = null;
								return;
							}
						}
					}
				}
			}

			/* If no more word, nothing can be done => NULL */
			if (newWords.size() == 0){
				suggestion = null;
				return;
			}

			/* If all words are now recognised, rebuild a new UCD
			 * and then see if it is fully valid. If not, see the second step of this function. */
			tempSuggestion = new UCD(newWords);
			if (tempSuggestion.isFullyValid()){
				suggestion = tempSuggestion;
				return;
			}else
				tempSuggestion = null;
		}else
			Collections.addAll(newWords, words);

		// 2nd: RE-ORDER THE UCD WORDS:

		/* Either the first word is SECONDARY, or a PRIMARY word is not in first position.
		 * In both case, it can be solve by moving the first PRIMARY word found to the first position.
		 * All next PRIMARY words will be removed. */

		// Search for PRIMARY words:
		UCDWord w, firstPrimary = null;
		Iterator<UCDWord> it = newWords.iterator();
		while(it.hasNext()){
			w = it.next();
			// Keep the first PRIMARY word and remove the others:
			if (w.syntaxCode == UCDSyntax.PRIMARY){
				if (firstPrimary == null)
					firstPrimary = w;
				it.remove();
			}
		}

		// Move the first found PRIMARY word to the first position:
		if (firstPrimary != null)
			newWords.add(0, firstPrimary);

		/* If no PRIMARY word has been found, it is then possible that the first word is a SECONDARY.
		 * So, the first non SECONDARY word, if any, should be moved in first position. */
		else if (newWords.size() > 0 && newWords.get(0).syntaxCode == UCDSyntax.SECONDARY){
			it = newWords.iterator();
			while(it.hasNext()){
				w = it.next();
				// Move the first word which is not SECONDARY in first position:
				if (w.syntaxCode != UCDSyntax.SECONDARY){
					it.remove();
					newWords.add(0, w);
					break;
				}
			}
		}

		/* If no more word, nothing can be done => NULL */
		if (newWords.size() == 0){
			suggestion = null;
			return;
		}

		// If the whole UCD is finally fully valid, return it ; otherwise NULL:
		tempSuggestion = new UCD(newWords);
		suggestion = tempSuggestion.isFullyValid() ? tempSuggestion : null;
	}

	/**
	 * Get a suggestion of a {@link #isFullyValid() fully valid} UCD
	 * created from this one if it is not already {@link #isFullyValid() fully valid}.
	 *
	 * @return	This UCD if it is already {@link #isFullyValid() fully valid},
	 *        	or a new UCD proposing a {@link #isFullyValid() fully valid} alternative to this UCD,
	 *        	or <code>null</code> if no suggestion of correction can be found.
	 */
	public final UCD getSuggestion(){
		if (!alreadyHasSuggestion){
			createSuggestion();
			alreadyHasSuggestion = true;
		}
		return suggestion;
	}

	/* ***************** */
	/* NAVIGATION & SIZE */
	/* ***************** */

	/**
	 * Get the total number of UCD words composing this UCD.
	 *
	 * @return	Number of words inside this UCD.
	 */
	public final int size(){
		return words.length;
	}

	/**
	 * Get the specified UCD words of this UCD.
	 *
	 * @param indWord	Index of the UCD word to get.
	 *
	 * @return	The corresponding UCD word.
	 *
	 * @throws ArrayIndexOutOfBoundsException	If the given index if negative, or equal or greater than the size of this UCD.
	 */
	public final UCDWord getWord(final int indWord) throws ArrayIndexOutOfBoundsException{
		return words[indWord];
	}

	/**
	 * Let iterate over the list of all UCD words composing this UCD.
	 *
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public final Iterator<UCDWord> iterator(){
		return new WordsIterator();
	}

	/**
	 * Iterator over the list of UCD words of this UCD.
	 *
	 * @author Gr&eacute;gory Mantelet (ARI)
	 * @version 1.0 (06/2016)
	 */
	protected class WordsIterator implements Iterator<UCDWord> {

		private int index = -1;

		@Override
		public boolean hasNext(){
			return (index + 1) < words.length;
		}

		@Override
		public UCDWord next(){
			if (!hasNext())
				throw new NoSuchElementException("No more UCD words!");
			return words[++index];
		}

		@Override
		public void remove(){
			throw new UnsupportedOperationException("Impossible to modify a UCD!");
		}

	}

	/* ******************** */
	/* STRING SERIALIZATION */
	/* ******************** */

	@Override
	public final String toString(){
		if (strRepresentation == null){
			StringBuffer buf = new StringBuffer();
			for(UCDWord w : words){
				/* append a semicolon to the string representation
				 * EVEN IF the word is NULL ; because this error should be reflected
				 * here in order to be able to raise accurate errors: */
				if (buf.length() > 0)
					buf.append(';');

				if (w != null)
					buf.append(w.rawWord);
			}
			this.strRepresentation = buf.toString();
		}

		return strRepresentation;
	}

	/* ****************** */
	/* EQUALITY FUNCTIONS */
	/* ****************** */

	@Override
	public boolean equals(final Object obj){
		return (obj != null && obj instanceof UCD && Arrays.equals(words, ((UCD)obj).words));
	}

	@Override
	public int hashCode(){
		return Arrays.hashCode(words);
	}

}
