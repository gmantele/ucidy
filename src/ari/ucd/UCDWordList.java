package ari.ucd;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Object listing and ordering alphabetically a set of UCD words.
 *
 * <h3>Searching UCD words</h3>
 *
 * <p>This object behaves like a dictionary. It is possible to search UCD words in different ways:</p>
 * <ul>
 * 	<li><i>For a specific UCD word:</i> {@link #get(String)} <i>(namespaces ignored)</i>, {@link #get(String, boolean)}</li>
 * 	<li><i>For all UCD words starting with a given string:</i> {@link #startingWith(String)}</li>
 * 	<li><i>For a UCD word with some typographical errors:</i> {@link #getClosest(String)}</li>
 * 	<li><i>For UCD whose the description (and also the word's atoms) matches some keywords:</i> {@link #search(String)}</li>
 * </ul>
 *
 * <h3>Import from a file</h3>
 *
 * <p>
 * 	A list of UCD word definitions can be imported from a PSV (Pipe-Separated-Value) file using
 * 	{@link #addAll(File)}, {@link #addAll(File, boolean)}, {@link #addAll(InputStream, boolean)} or
 * 	{@link #addAll(Reader, boolean)}. The first function will import all identified UCD words as
 * 	<b>NOT <i>{@link UCDWord#recommended recommended}</i></b>. If you want to change this behaviour,
 * 	use {@link #addAll(File, boolean)} instead.
 * </p>
 *
 * @author Gr&eacute;gory Mantelet (ARI)
 * @version 1.0 (02/2017)
 */
public class UCDWordList implements Iterable<UCDWord> {

	/* ####################################################################################################################
	 * # MAIN FUNCTION ####################################################################################################
	 * #################################################################################################################### */
	public static void main(final String[] args) throws Throwable{

		UCDWordList words = new UCDWordList();
		int nbWords = words.addAll(UCDWordList.class.getResourceAsStream("/ucd1p-words.txt"), true);
		System.out.println(nbWords + " UCD words successfully read!");

		for(UCDWord w : words){
			if (!w.recommended)
				System.out.println("- \"" + w + "\" NOT RECOMMENDED");
		}

		String search = "META";

		System.out.print("\nEXACT MATCH for \"" + search + "\": ");
		System.out.println(words.get(search));

		System.out.println("\nSTARTING WITH \"" + search + "\":");
		for(UCDWord w : words.startingWith(search))
			System.out.println("  - " + w);

		search = "pos.eq.de";
		System.out.println("\nCLOSEST TO \"" + search + "\":");
		for(UCDWord w : words.getClosest(search))
			System.out.println("  - " + w);

		search = "elec.optical.u";
		System.out.println("\nCLOSEST TO \"" + search + "\":");
		for(UCDWord w : words.getClosest(search))
			System.out.println("  - " + w);

		search = "em.ot.x";
		System.out.println("\nCLOSEST TO \"" + search + "\":");
		for(UCDWord w : words.getClosest(search))
			System.out.println("  - " + w);

	}
	/* #################################################################################################################### */

	/**
	 * Class used to sort the UCD words in this {@link UCDWordList}.
	 *
	 * <p>
	 * 	Here, words are sorted case INsensitively without namespace.
	 * 	Thus, two identical words with a different namespace can NOT be in the same {@link UCDWordList}.
	 * 	The idea is to avoid any ambiguity for the users reading UCDs.
	 * </p>
	 *
	 * @author Gr&eacute;gory Mantelet (ARI)
	 * @version 1.0 (02/2017)
	 */
	protected class UCDWordComparator implements Comparator<UCDWord> {

		@Override
		public int compare(final UCDWord o1, final UCDWord o2){
			if (o1 == null || o2 == null)
				return -1;
			else
				return o1.word.compareToIgnoreCase(o2.word);
		}

	}

	/** List of all known UCD words. */
	protected TreeSet<UCDWord> words = new TreeSet<UCDWord>(new UCDWordComparator());

	/**
	 * Create an empty list of UCD words.
	 */
	public UCDWordList(){}

	/* ****************** */
	/* ADDITION FUNCTIONS */
	/* ****************** */

	/**
	 * Add the given UCD word.
	 *
	 * @param newWord	The UCD word to add.
	 *
	 * @return	<code>true</code> if the given UCD word has been successfully added,
	 *        	<code>false</code> if the given word is <code>null</code> or already exists in the list.
	 */
	public boolean add(final UCDWord newWord){
		return (newWord == null) ? false : words.add(newWord);
	}

	/**
	 * Add all the UCD words listed in the given collection.
	 *
	 * <p><i>Note:
	 * 	<code>null</code> items are ignored.
	 * </i></p>
	 *
	 * @param newWords	A collection of new UCD words to add.
	 *
	 * @return	The number of successfully added UCD words,
	 *        	or <code>0</code> if the given collection is <code>null</code>.
	 */
	public int addAll(final Collection<UCDWord> newWords){
		if (newWords == null)
			return 0;

		int nbAdded = 0;
		for(UCDWord w : newWords){
			if (w != null && this.words.add(w))
				nbAdded++;
		}
		return nbAdded;
	}

	/**
	 * Add all the UCD words listed in the given array.
	 *
	 * <p><i>Note:
	 * 	<code>null</code> items are ignored.
	 * </i></p>
	 *
	 * @param newWords	An array of new UCD words to add.
	 *
	 * @return	The number of successfully added UCD words,
	 *        	or <code>0</code> if the given array is <code>null</code>.
	 */
	public int addAll(final UCDWord[] newWords){
		if (newWords == null)
			return 0;

		int nbAdded = 0;
		for(UCDWord w : newWords){
			if (w != null && this.words.add(w))
				nbAdded++;
		}
		return nbAdded;
	}

	/**
	 * Add all UCD words declared using the PSV (Pipe-Separated-Value) format inside the specified file.
	 *
	 * <p><b>
	 * 	The real parsing is performed by {@link UCDParser#parseWordList(Reader, boolean, UCDWordList)}.
	 * 	Please take a look to its Javadoc for more details about the expected syntax and the possible
	 * 	errors that can occur.
	 * </b></p>
	 *
	 * @param psvFile	PSV file to parse.
	 *
	 * @return	The number of successfully added UCD words extracted from the file.
	 *
	 * @throws NullPointerException	If the given file is <code>null</code>.
	 * @throws IOException			If an error occurred while reading the specified file.
	 *
	 * @see #addAll(File, boolean)
	 */
	public int addAll(final File psvFile) throws NullPointerException, IOException{
		return addAll(psvFile, false);
	}

	/**
	 * Add all UCD words declared using the PSV (Pipe-Separated-Value) format inside the specified file.
	 *
	 * <p><b>
	 * 	The real parsing is performed by {@link UCDParser#parseWordList(Reader, boolean, UCDWordList)}.
	 * 	Please take a look to its Javadoc for more details about the expected syntax and the possible
	 * 	errors that can occur.
	 * </b></p>
	 *
	 * @param psvFile		PSV file to parse.
	 * @param recommended	<code>true</code> to flag all imported UCD words as <i>{@link UCDWord#recommended recommended}</i>,
	 *                   	<code>false</code> otherwise.
	 *
	 * @return	The number of successfully added UCD words extracted from the file.
	 *
	 * @throws NullPointerException	If the given file is <code>null</code>.
	 * @throws IOException			If an error occurred while reading the specified file.
	 *
	 * @see #addAll(Reader, boolean)
	 */
	public int addAll(final File psvFile, final boolean recommended) throws NullPointerException, IOException{
		if (psvFile == null)
			throw new NullPointerException("Missing file to parse!");
		return addAll(new FileReader(psvFile), recommended);
	}

	/**
	 * Add all UCD words declared using the PSV (Pipe-Separated-Value) format inside the specified input stream.
	 *
	 * <p><b>
	 * 	The real parsing is performed by {@link UCDParser#parseWordList(Reader, boolean, UCDWordList)}.
	 * 	Please take a look to its Javadoc for more details about the expected syntax and the possible
	 * 	errors that can occur.
	 * </b></p>
	 *
	 * @param stream		Input stream to parse.
	 * @param recommended	<code>true</code> to flag all imported UCD words as <i>{@link UCDWord#recommended recommended}</i>,
	 *                   	<code>false</code> otherwise.
	 *
	 * @return	The number of successfully added UCD words extracted from the input stream.
	 *
	 * @throws NullPointerException	If the given stream is <code>null</code>.
	 * @throws IOException			If an error occurred while reading the specified stream.
	 *
	 * @see #addAll(Reader, boolean)
	 */
	public int addAll(final InputStream stream, final boolean recommended) throws NullPointerException, IOException{
		if (stream == null)
			throw new NullPointerException("Missing input stream to parse!");
		return addAll(new InputStreamReader(stream), recommended);
	}

	/**
	 * Add all UCD words declared using the PSV (Pipe-Separated-Value) format inside the specified reader.
	 *
	 * <p><b>
	 * 	The real parsing is performed by {@link UCDParser#parseWordList(Reader, boolean, UCDWordList)}.
	 * 	Please take a look to its Javadoc for more details about the expected syntax and the possible
	 * 	errors that can occur.
	 * </b></p>
	 *
	 * @param reader		Reader whose the content must be parsed.
	 * @param recommended	<code>true</code> to flag all imported UCD words as <i>{@link UCDWord#recommended recommended}</i>,
	 *                   	<code>false</code> otherwise.
	 *
	 * @return	The number of successfully added UCD words extracted from the input.
	 *
	 * @throws NullPointerException	If the given reader is <code>null</code>.
	 * @throws IOException			If an error occurred while reading the specified input.
	 *
	 * @see UCDParser#parseWordList(Reader, boolean, UCDWordList)
	 */
	public int addAll(final Reader reader, final boolean recommended) throws NullPointerException, IOException{
		return UCDParser.parseWordList(reader, recommended, this);
	}

	/* ***************** */
	/* NAVIGATION & SIZE */
	/* ***************** */

	/**
	 * Let iterate (by alphabetic order) over the whole list of UCD words.
	 *
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<UCDWord> iterator(){
		return words.iterator();
	}

	/**
	 * Total number of UCD words stored in this list.
	 *
	 * @return	Number of stored UCD words.
	 */
	public int size(){
		return words.size();
	}

	/* **************** */
	/* SEARCH FUNCTIONS */
	/* **************** */

	/**
	 * Tell whether the given UCD word is part of this list.
	 *
	 * <p><i><b>Important note:</b>
	 * 	Comparisons are <b>case INsensitive</b> and the <b>namespaces are ignored</b>.
	 * </i></p>
	 *
	 * @param ucdWord	The UCD word to test.
	 *
	 * @return	<code>true</code> if the given UCD word exists in this list,
	 *        	<code>false</code> otherwise (and particularly if the given word is <code>null</code>).
	 */
	public boolean contains(final String ucdWord){
		return (ucdWord == null || ucdWord.trim().length() == 0) ? false : words.contains(new UCDWord(ucdWord));
	}

	/**
	 * Search for an exact match with the given UCD word but <i>while <b>ignoring</b> their namespace</i>.
	 *
	 * <p><i><b>Important note:</b>
	 * 	Comparisons are <b>case INsensitive</b> and the <b>namespaces are ignored</b>.
	 * </i></p>
	 *
	 * <p><b>Warning:</b>
	 * 	If this function returns a {@link UCDWord}, it may have a different namespace than the one of the given UCD word.
	 * 	To search on the word AND on the namespace you should use {@link #get(String, boolean)} or apply a final test
	 * 	on the namespace of the returned word.
	 * </p>
	 *
	 * @param ucdWord	The UCD word to search.
	 *
	 * @return	The corresponding {@link UCDWord} instance matching the given UCD word,
	 *        	or <code>null</code> if the given word is <code>null</code>, an empty string or can not be found in this list.
	 *
	 * @see #get(String, boolean)
	 */
	public UCDWord get(String ucdWord){
		return get(ucdWord, false);
	}

	/**
	 * Search for an exact match with the given supposed UCD word.
	 *
	 * <p><i><b>Important note:</b>
	 * 	Comparisons are <b>case INsensitive</b>.
	 * </i></p>
	 *
	 * @param ucdWord			The UCD word to search.
	 * @param checkNamespace	<code>true</code> to ensure the namespace of the match is the same as the one of the given word,
	 *                      	<code>false</code> to search only on the word and not check the namespace.
	 *
	 * @return	The corresponding {@link UCDWord} instance matching the given UCD word,
	 *        	or <code>null</code> if the given word is <code>null</code>, an empty string or can not be found in this list.
	 */
	public UCDWord get(String ucdWord, final boolean checkNamespace){
		if (ucdWord == null || ucdWord.trim().length() == 0)
			return null;

		UCDWord word = new UCDWord(ucdWord);
		SortedSet<UCDWord> result = words.subSet(word, new UCDWord(ucdWord + Character.MIN_VALUE));
		if (result.size() == 0)
			return null;
		else if (!checkNamespace || result.first().equals(word))
			return result.first();
		else
			return null;
	}

	/**
	 * Search for all UCD words starting with the given string.
	 *
	 * <p><i><b>Important note:</b>
	 * 	Comparisons are <b>case INsensitive</b> and the <b>namespaces are ignored</b>.
	 * </i></p>
	 *
	 * @param startStr	The starting string.
	 *
	 * @return	A sorted set containing all UCD words starting with the given string,
	 *        	or <code>null</code> if the given string is <code>null</code>, empty or can not be found in this list.
	 */
	public SortedSet<UCDWord> startingWith(String startStr){
		if (startStr == null || startStr.trim().length() == 0)
			return new TreeSet<UCDWord>();
		else
			return words.subSet(new UCDWord(startStr), new UCDWord(startStr + Character.MAX_VALUE));
	}

	/**
	 * Search for a UCD word the closest as possible from the given one.
	 *
	 * <p>
	 * 	This function aims to help fixing typo in a given UCD word.
	 * 	It uses the Levenshtein algorithm to determine the UCD word of this list
	 * 	which requires the fewest number of editions (i.e. add, replace and remove a character)
	 * 	to match the given word.
	 * </p>
	 *
	 * <p>
	 * 	This function is designed to select UCD words with a number of required editions
	 * 	less or equal to half the length of the given word. Consequently, smaller is the given word,
	 * 	smaller are the probabilities to find a closest match in this list. In other words, this
	 * 	function should not be used for small words (e.g. less than 3-4 characters).
	 * </p>
	 *
	 * <p><i>Note 1:</i>
	 * 	It is entirely possible that several words have the same distance from the given word.
	 * 	In such case, this function will return all candidates in an array.
	 * </p>
	 *
	 * <p><i><b>Note 2:</b>
	 * 	Search is performed <b>case INsensitive</b> and the <b>namespaces are ignored</b>.
	 * </i></p>
	 *
	 * @param wrongWord	The word to fix.
	 *
	 * @return	List of all the closest {@link UCDWord}s from the given UCD word.
	 */
	public UCDWord[] getClosest(String wrongWord){
		if (wrongWord == null || wrongWord.trim().length() == 0)
			return new UCDWord[0];

		wrongWord = wrongWord.trim().toLowerCase();

		ArrayList<UCDWord> match = new ArrayList<UCDWord>(10);
		int threshold = Math.round(wrongWord.length() / 2f), dist,
				bestDistance = Integer.MAX_VALUE;

		for(UCDWord w : words){
			dist = levenshtein(wrongWord, w.word.toLowerCase());
			if (dist == 0)
				return new UCDWord[]{w};
			else if (dist == bestDistance)
				match.add(w);
			else if (dist < bestDistance && dist <= threshold){
				match.clear();
				match.add(w);
				bestDistance = dist;
			}
		}

		return (match.size() == 0) ? new UCDWord[0] : match.toArray(new UCDWord[match.size()]);
	}

	/**
	 * Search for all UCD words whose the description matches the given keywords.
	 *
	 * <p><i><b>Important note:</b>
	 * 	Comparisons are case <b>in</b>sensitive.
	 * </i></p>
	 *
	 * @param keywords	String concatenating (with space characters) some keywords.
	 *
	 * @return	A set of UCD words whose the description matches the given keywords.
	 *        	This set is ordered by descending score.
	 */
	public Set<UCDWord> search(final String keywords){

		// TODO Search UCDs by keyword(s) (in the UCD word itself, but in particular in the description).

		return new TreeSet<UCDWord>();
	}

	/* **************** */
	/* REMOVE FUNCTIONS */
	/* **************** */

	/**
	 * Remove the UCD word matching the given one.
	 *
	 * <p><i><b>Important note:</b>
	 * 	Comparisons are <b>case INsensitive</b> and the <b>namespaces are ignored</b>.
	 * </i></p>
	 *
	 * @param ucdWord	The UCD word to remove from this list.
	 *
	 * @return	The removed UCD word,
	 *        	or <code>null</code> if no UCD word matches.
	 */
	public UCDWord remove(final String ucdWord){
		if (ucdWord == null || ucdWord.trim().length() == 0)
			return null;

		UCDWord match = get(ucdWord);
		if (match != null)
			return words.remove(match) ? match : null;
		else
			return null;
	}

	/**
	 * Remove ALL UCD words listed in this object.
	 */
	public void clear(){
		words.clear();
	}

	/* ***************** */
	/* DISTANCE FUNCTION */
	/* ***************** */

	/**
	 * Compute the Levenshtein distance between the two given strings.
	 *
	 * <p>Short definition of the Levenshtein algorithm by Wikipedia:</p>
	 * <blockquote>
	 * 	In information theory and computer science, the Levenshtein distance is a string metric for measuring the difference between two sequences.
	 * 	Informally, the Levenshtein distance between two words is the minimum number of single-character edits (i.e. insertions, deletions or substitutions)
	 * 	required to change one word into the other.
	 * </blockquote>
	 *
	 * <p><i>
	 * 	This function has been strongly inspired of the Java code source provided on
	 * 	        http://rosettacode.org/wiki/Levenshtein_distance#Java
	 * </i></p>
	 *
	 * @param left	A first string. <b>Must NOT be NULL</b>
	 * @param right	A second string. <b>Must NOT be NULL</b>
	 *
	 * @return	The distance between the two given strings.
	 *        	<i>0 is a perfect equality ; max(left.length, right.length) is a perfect difference.</i>
	 *
	 * @throws NullPointerException	If one of the given string is <code>null</code>.
	 */
	protected static int levenshtein(final String left, final String right) throws NullPointerException{
		int alen = left.length(), blen = right.length();
		int i, j, nw, cj;
		int[] costs = new int[blen + 1];
		for(j = 0; j < costs.length; j++)
			costs[j] = j;
		for(i = 1; i <= alen; i++){
			costs[0] = i;
			nw = i - 1;
			for(j = 1; j <= blen; j++){
				cj = lev_min(1 + costs[j], 1 + costs[j - 1], left.charAt(i - 1) == right.charAt(j - 1) ? nw : nw + 1);
				nw = costs[j];
				costs[j] = cj;
			}
		}
		return costs[blen];
	}

	/**
	 * Compute the minimum value among the three given one.
	 *
	 * <p><i>This function is used only by {@link #levenshtein(String, String)} ; hence its name.</i></p>
	 *
	 * @param a	A value.
	 * @param b	Another value.
	 * @param c	A last value.
	 *
	 * @return	The minimum of a, b and c.
	 */
	protected static int lev_min(final int a, final int b, final int c){
		return (a < b) ? ((a < c) ? a : c) : ((b < c) ? b : c);
	}

}
