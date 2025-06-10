package ari.ucidy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TestUCDSyntax {

	@Test
	void testGet(){

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
