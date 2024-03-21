import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Syntax {
    
    // Keywords
    public static int PROGRAM = -1;
    public static int BEGIN = -2;
    public static int END = -3;
    public static int READ = -4;
    public static int WRITE = -5;
    public static int IF = -6;
    public static int IFNOT = -7;
    public static int WHILE = -8;
    public static int REPEAT = -9;
    public static int UNTIL = -10;
    public static int INT_KW = -11;
    public static int REAL_KW = -12;
    public static int STRING_KW = -13;
    public static int BOOLEAN_KW = -14;
    public static int VAR = -15;
    public static int THEN = -16;
    public static int DO = -17;
    // Operators
    public static int MUL_OP = -21;     // Arithmetic
    public static int DIV_OP = -22;
    public static int MOD_OP = -23;
    public static int ADD_OP = -24;
    public static int SUB_OP = -25;
    public static int ASSIGN_OP = -26;
    public static int LT_OP = -31;      // Relational
    public static int LE_OP = -32;
    public static int GT_OP = -33;
    public static int GE_OP = -34;
    public static int EQ_OP = -35;
    public static int NE_OP = -36;
    public static int AND_OP = -41;     // Logical
    public static int OR_OP = -42;
    public static int NOT_OP = -43;
    // Identifiers
    public static int INT_ID = -51;
    public static int REAL_ID = -52;
    public static int STRING_ID = -53;
    public static int BOOLEAN_ID = -54;
    public static int GENERAL_ID = -55;
    // Constants
    public static int INT_CONST = -61;
    public static int REAL_CONST = -62;
    public static int STRING_CONST = -63;
    public static int TRUE_CONST = -64;
    public static int FALSE_CONST = -65;
    // Chars
    public static int OPEN_PAR = -73;
    public static int CLOSE_PAR = -74;
    public static int SEMICOLON = -75;
    public static int COMMA = -76;

    public static Set<Integer>    keywords = new HashSet<Integer>(
        Arrays.asList(
            Syntax.PROGRAM,
            Syntax.BEGIN,
            Syntax.END,
            Syntax.READ,
            Syntax.WRITE,
            Syntax.IF,
            Syntax.IFNOT,
            Syntax.WHILE,
            Syntax.REPEAT,
            Syntax.UNTIL,
            Syntax.INT_KW,
            Syntax.REAL_KW,
            Syntax.STRING_KW,
            Syntax.BOOLEAN_KW,
            Syntax.VAR,
            Syntax.THEN,
            Syntax.DO
        )
    );

    public static Set<Integer>    arithmeticOperators = new HashSet<Integer>(
        Arrays.asList(
            Syntax.MUL_OP,
            Syntax.DIV_OP,
            Syntax.MOD_OP,
            Syntax.ADD_OP,
            Syntax.SUB_OP
        )
    );

    public static Set<Integer>    relationalOperators = new HashSet<Integer>(
        Arrays.asList(
            Syntax.LT_OP,
            Syntax.LE_OP,
            Syntax.GT_OP,
            Syntax.GE_OP,
            Syntax.EQ_OP,
            Syntax.NE_OP
        )
    );

    public static Set<Integer>    logicalOperators = new HashSet<Integer>(
        Arrays.asList(
            Syntax.AND_OP,
            Syntax.OR_OP,
            Syntax.NOT_OP
        )
    );

    public static Set<Integer>    logicalAndRelationalOperators = new HashSet<Integer>(
        Arrays.asList(
            Syntax.LT_OP,
            Syntax.LE_OP,
            Syntax.GT_OP,
            Syntax.GE_OP,
            Syntax.EQ_OP,
            Syntax.NE_OP,
            Syntax.AND_OP,
            Syntax.OR_OP,
            Syntax.NOT_OP
        )
    );

    public static Set<Integer>    identifiers = new HashSet<Integer>(
        Arrays.asList(
            Syntax.INT_ID,
            Syntax.REAL_ID,
            Syntax.STRING_ID,
            Syntax.BOOLEAN_ID,
            Syntax.GENERAL_ID
        )
    );

    public static Set<Integer>    constants = new HashSet<Integer>(
        Arrays.asList(
            Syntax.INT_CONST,
            Syntax.REAL_CONST,
            Syntax.STRING_CONST,
            Syntax.TRUE_CONST,
            Syntax.FALSE_CONST
        )
    );

    public static Set<Integer>    chars = new HashSet<Integer>(
        Arrays.asList(
            Syntax.OPEN_PAR,
            Syntax.CLOSE_PAR,
            Syntax.SEMICOLON,
            Syntax.COMMA
        )
    );

}
