package ari.ucd;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Object listing and ordering alphabetically a set of UCD1+ words.
 *
 * <h3>Searching UCD1+ words</h3>
 *
 * <p>This object behaves like a dictionary. It is possible to search UCD1+ words in different ways:</p>
 * <ul>
 * 	<li><i>For a specific UCD1+ word:</i> {@link #get(String)}</li>
 * 	<li><i>For all UCD1+ words starting with a given string:</i> {@link #startingWith(String)}</li>
 * 	<li><i>For UCD1+ whose the description (and also the word's atoms) matches some keywords:</i> {@link #search(String)}</li>
 * </ul>
 *
 * <h3>Import from a file</h3>
 *
 * <p>
 * 	A list of UCD1+ word definitions can be imported from a PSV (Pipe-Separated-Value) file using
 * 	{@link #addAll(File)}, {@link #addAll(File, boolean)}, {@link #addAll(InputStream, boolean)} or
 * 	{@link #addAll(Reader, boolean)}. The first function will import all identified UCD1+ words as
 * 	<b>NOT <i>{@link UCDWord#recommended recommended}</i></b>. If you want to change this behaviour,
 * 	use {@link #addAll(File, boolean)} instead.
 * </p>
 *
 * @author Gr&eacute;gory Mantelet (ARI)
 * @version 1.0 (06/2016)
 */
public class UCDWordList implements Iterable<UCDWord> {

	/* ####################################################################################################################
	 * # MAIN FUNCTION ####################################################################################################
	 * #################################################################################################################### */
	public static void main(final String[] args) throws Throwable{

		UCDWordList words = new UCDWordList();
		int nbWords = words.addAll(UCDWordList.class.getResourceAsStream("/ucd1p-words.txt"), true);
		System.out.println(nbWords + " UCD1+ words successfully read!");

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

	}
	/* #################################################################################################################### */

	/** List of all known UCD1+ words. */
	protected TreeSet<UCDWord> words = new TreeSet<UCDWord>();

	/**
	 * Create an empty list of UCD1+ words.
	 */
	public UCDWordList(){}

	/* ****************** */
	/* ADDITION FUNCTIONS */
	/* ****************** */

	/**
	 * Add the given UCD1+ word.
	 *
	 * @param newWord	The UCD1+ word to add.
	 *
	 * @return	<code>true</code> if the given UCD1+ word has been successfully added,
	 *        	<code>false</code> if the given word is <code>null</code> or already exists in the list.
	 */
	public boolean add(final UCDWord newWord){
		return (newWord == null) ? false : words.add(newWord);
	}

	/**
	 * Add all the UCD1+ words listed in the given collection.
	 *
	 * <p><i>Note:
	 * 	<code>null</code> items are ignored.
	 * </i></p>
	 *
	 * @param newWords	A collection of new UCD1+ words to add.
	 *
	 * @return	The number of successfully added UCD1+ words,
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
	 * Add all the UCD1+ words listed in the given array.
	 *
	 * <p><i>Note:
	 * 	<code>null</code> items are ignored.
	 * </i></p>
	 *
	 * @param newWords	An array of new UCD1+ words to add.
	 *
	 * @return	The number of successfully added UCD1+ words,
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
	 * Add all UCD1+ words declared using the PSV (Pipe-Separated-Value) format inside the specified file.
	 *
	 * <p><b>
	 * 	The real parsing is performed by {@link UCDParser#parseWordList(Reader, boolean, UCDWordList)}.
	 * 	Please take a look to its Javadoc for more details about the expected syntax and the possible
	 * 	errors that can occur.
	 * </b></p>
	 *
	 * @param psvFile	PSV file to parse.
	 *
	 * @return	The number of successfully added UCD1+ words extracted from the file.
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
	 * Add all UCD1+ words declared using the PSV (Pipe-Separated-Value) format inside the specified file.
	 *
	 * <p><b>
	 * 	The real parsing is performed by {@link UCDParser#parseWordList(Reader, boolean, UCDWordList)}.
	 * 	Please take a look to its Javadoc for more details about the expected syntax and the possible
	 * 	errors that can occur.
	 * </b></p>
	 *
	 * @param psvFile		PSV file to parse.
	 * @param recommended	<code>true</code> to flag all imported UCD1+ words as <i>{@link UCDWord#recommended recommended}</i>,
	 *                   	<code>false</code> otherwise.
	 *
	 * @return	The number of successfully added UCD1+ words extracted from the file.
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
	 * Add all UCD1+ words declared using the PSV (Pipe-Separated-Value) format inside the specified input stream.
	 *
	 * <p><b>
	 * 	The real parsing is performed by {@link UCDParser#parseWordList(Reader, boolean, UCDWordList)}.
	 * 	Please take a look to its Javadoc for more details about the expected syntax and the possible
	 * 	errors that can occur.
	 * </b></p>
	 *
	 * @param stream		Input stream to parse.
	 * @param recommended	<code>true</code> to flag all imported UCD1+ words as <i>{@link UCDWord#recommended recommended}</i>,
	 *                   	<code>false</code> otherwise.
	 *
	 * @return	The number of successfully added UCD1+ words extracted from the input stream.
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
	 * Add all UCD1+ words declared using the PSV (Pipe-Separated-Value) format inside the specified reader.
	 *
	 * <p><b>
	 * 	The real parsing is performed by {@link UCDParser#parseWordList(Reader, boolean, UCDWordList)}.
	 * 	Please take a look to its Javadoc for more details about the expected syntax and the possible
	 * 	errors that can occur.
	 * </b></p>
	 *
	 * @param reader		Reader whose the content must be parsed.
	 * @param recommended	<code>true</code> to flag all imported UCD1+ words as <i>{@link UCDWord#recommended recommended}</i>,
	 *                   	<code>false</code> otherwise.
	 *
	 * @return	The number of successfully added UCD1+ words extracted from the input.
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
	 * Let iterate (by alphabetic order) over the whole list of UCD1+ words.
	 *
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<UCDWord> iterator(){
		return words.iterator();
	}

	/**
	 * Total number of UCD1+ words stored in this list.
	 *
	 * @return	Number of stored UCD1+ words.
	 */
	public int size(){
		return words.size();
	}

	/* **************** */
	/* SEARCH FUNCTIONS */
	/* **************** */

	/**
	 * Tell whether the given UCD1+ word is part of this list.
	 *
	 * <p><i><b>Important note:</b>
	 * 	Comparisons are case <b>in</b>sensitive.
	 * </i></p>
	 *
	 * @param ucdWord	The UCD1+ word to test.
	 *
	 * @return	<code>true</code> if the given UCD1+ word exists in this list,
	 *        	<code>false</code> otherwise (and particularly if the given word is <code>null</code>).
	 */
	public boolean contains(final String ucdWord){
		return (ucdWord == null || ucdWord.trim().length() == 0) ? false : words.contains(new UCDWord(ucdWord));
	}

	/**
	 * Search for an exact match with the given supposed UCD1+ word.
	 *
	 * <p><i><b>Important note:</b>
	 * 	Comparisons are case <b>in</b>sensitive.
	 * </i></p>
	 *
	 * @param ucdWord	The UCD1+ word to search.
	 *
	 * @return	The corresponding {@link UCDWord} instance matching the given UCD1+ word,
	 *        	or <code>null</code> if the given word is <code>null</code>, an empty string or can not be found in this list.
	 */
	public UCDWord get(String ucdWord){
		if (ucdWord == null || ucdWord.trim().length() == 0)
			return null;

		SortedSet<UCDWord> result = words.subSet(new UCDWord(ucdWord), new UCDWord(ucdWord + Character.MIN_VALUE));
		if (result.size() == 0)
			return null;
		else
			return result.first();
	}

	/**
	 * Search for all UCD1+ words starting with the given string.
	 *
	 * <p><i><b>Important note:</b>
	 * 	Comparisons are case <b>in</b>sensitive.
	 * </i></p>
	 *
	 * @param startStr	The starting string.
	 *
	 * @return	A sorted set containing all UCD1+ words starting with the given string,
	 *        	or <code>null</code> if the given string is <code>null</code>, empty or can not be found in this list.
	 */
	public SortedSet<UCDWord> startingWith(String startStr){
		if (startStr == null || startStr.trim().length() == 0)
			return new TreeSet<UCDWord>();
		else
			return words.subSet(new UCDWord(startStr), new UCDWord(startStr + Character.MAX_VALUE));
	}

	/**
	 * Search for all UCD1+ words whose the description matches the given keywords.
	 *
	 * <p><i><b>Important note:</b>
	 * 	Comparisons are case <b>in</b>sensitive.
	 * </i></p>
	 *
	 * @param keywords	String concatenating (with space characters) some keywords.
	 *
	 * @return	A set of UCD1+ words whose the description matches the given keywords.
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
	 * Remove the UCD1+ word matching the given one.
	 *
	 * <p><i><b>Important note:</b>
	 * 	Comparisons are case <b>in</b>sensitive.
	 * </i></p>
	 *
	 * @param ucdWord	The UCD1+ word to remove from this list.
	 *
	 * @return	The removed UCD1+ word,
	 *        	or <code>null</code> if no UCD1+ word matches.
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
	 * Remove ALL UCD1+ words listed in this object.
	 */
	public void clear(){
		words.clear();
	}

}
