import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Sintaxis {
    
    // Keywords
    public static int PROGRAMA = -1;
    public static int INICIO = -2;
    public static int FIN = -3;
    public static int LEER = -4;
    public static int ESCRIBIR = -5;
    public static int IF = -6;
    public static int SINO = -7;
    public static int MIENTRAS = -8;
    public static int REPETIR = -9;
    public static int HASTA = -10;
    public static int INT_PR = -11;
    public static int REAL_PR = -12;
    public static int STRING_PR = -13;
    public static int BOOLEANO_PR = -14;
    public static int VAR = -15;
    public static int ENTONCES = -16;
    public static int HACER = -17;
    // Operators
    public static int MUL_OP = -21;     // Arithmetic
    public static int DIV_OP = -22;
    public static int MOD_OP = -23;
    public static int SUM_OP = -24;
    public static int RES_OP = -25;
    public static int ASIGN_OP = -26;
    public static int MENOR_OP = -31;      // Relational
    public static int MENIG_OP = -32;
    public static int MAYOR_OP = -33;
    public static int MAYIG_OP = -34;
    public static int IGUAL_OP = -35;
    public static int DIF_OP = -36;
    public static int AND_OP = -41;     // Logical
    public static int OR_OP = -42;
    public static int NOT_OP = -43;
    // Identifiers
    public static int INT_ID = -51;
    public static int REAL_ID = -52;
    public static int STRING_ID = -53;
    public static int BOOLEANO_ID = -54;
    public static int GENERAL_ID = -55;
    // Constants
    public static int INT_CONST = -61;
    public static int REAL_CONST = -62;
    public static int STRING_CONST = -63;
    public static int TRUE_CONST = -64;
    public static int FALSE_CONST = -65;
    // Chars
    public static int PAR_ABRIR = -73;
    public static int PAR_CERRAR = -74;
    public static int PUNTOCOMA = -75;
    public static int COMA = -76;

    public static Set<Integer>    keywords = new HashSet<Integer>(
        Arrays.asList(
            Sintaxis.PROGRAMA,
            Sintaxis.INICIO,
            Sintaxis.FIN,
            Sintaxis.LEER,
            Sintaxis.ESCRIBIR,
            Sintaxis.IF,
            Sintaxis.SINO,
            Sintaxis.MIENTRAS,
            Sintaxis.REPETIR,
            Sintaxis.HASTA,
            Sintaxis.INT_PR,
            Sintaxis.REAL_PR,
            Sintaxis.STRING_PR,
            Sintaxis.BOOLEANO_PR,
            Sintaxis.VAR,
            Sintaxis.ENTONCES,
            Sintaxis.HACER
        )
    );

    public static Set<Integer>    arithmeticOperators = new HashSet<Integer>(
        Arrays.asList(
            Sintaxis.MUL_OP,
            Sintaxis.DIV_OP,
            Sintaxis.MOD_OP,
            Sintaxis.SUM_OP,
            Sintaxis.RES_OP
        )
    );

    public static Set<Integer>    relationalOperators = new HashSet<Integer>(
        Arrays.asList(
            Sintaxis.MENOR_OP,
            Sintaxis.MENIG_OP,
            Sintaxis.MAYOR_OP,
            Sintaxis.MAYIG_OP,
            Sintaxis.IGUAL_OP,
            Sintaxis.DIF_OP
        )
    );

    public static Set<Integer>    logicalOperators = new HashSet<Integer>(
        Arrays.asList(
            Sintaxis.AND_OP,
            Sintaxis.OR_OP,
            Sintaxis.NOT_OP
        )
    );

    public static Set<Integer>    logicalAndRelationalOperators = new HashSet<Integer>(
        Arrays.asList(
            Sintaxis.MENOR_OP,
            Sintaxis.MENIG_OP,
            Sintaxis.MAYOR_OP,
            Sintaxis.MAYIG_OP,
            Sintaxis.IGUAL_OP,
            Sintaxis.DIF_OP,
            Sintaxis.AND_OP,
            Sintaxis.OR_OP,
            Sintaxis.NOT_OP
        )
    );

    public static Set<Integer>    identifiers = new HashSet<Integer>(
        Arrays.asList(
            Sintaxis.INT_ID,
            Sintaxis.REAL_ID,
            Sintaxis.STRING_ID,
            Sintaxis.BOOLEANO_ID,
            Sintaxis.GENERAL_ID
        )
    );

    public static Set<Integer>    constants = new HashSet<Integer>(
        Arrays.asList(
            Sintaxis.INT_CONST,
            Sintaxis.REAL_CONST,
            Sintaxis.STRING_CONST,
            Sintaxis.TRUE_CONST,
            Sintaxis.FALSE_CONST
        )
    );

    public static Set<Integer>    chars = new HashSet<Integer>(
        Arrays.asList(
            Sintaxis.PAR_ABRIR,
            Sintaxis.PAR_CERRAR,
            Sintaxis.PUNTOCOMA,
            Sintaxis.COMA
        )
    );

}
