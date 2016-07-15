package ari.ucd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

public class TestUCDSyntax {

	@Before
	public void setUp() throws Exception{}

	@Test
	public void testGet(){

		/* Known syntax code */
		for(UCDSyntax s : UCDSyntax.values()){
			assertEquals(s, UCDSyntax.get(s.syntaxCode));
			assertEquals(s, UCDSyntax.get(Character.toLowerCase(s.syntaxCode)));
			assertEquals(s, UCDSyntax.get(Character.toUpperCase(s.syntaxCode)));
		}

		/* Unknown syntax code */
		assertNull(UCDSyntax.get('w'));
		assertNull(UCDSyntax.get(' '));
		assertNull(UCDSyntax.get('	'));
		assertNull(UCDSyntax.get('\n'));
		assertNull(UCDSyntax.get('\0'));
		assertNull(UCDSyntax.get('9'));

	}

}
